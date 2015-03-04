package com.acproma.kflightchecklists.screens;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.acproma.kflightchecklists.R;
import com.acproma.kflightchecklists.AppController;
import com.acproma.kflightchecklists.BaseActivity;
import com.acproma.kflightchecklists.data.KFlightChecklistDB;
import com.acproma.kflightchecklists.models.ActionItem;
import com.acproma.kflightchecklists.models.ActionItemAction;
import com.acproma.kflightchecklists.models.Checklist;
import com.acproma.kflightchecklists.models.Section;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ChecklistRundownScreen extends BaseActivity<ChecklistRundownScreenStateMachine> {
	final static public String SELECTED_CHECKLIST = "SELECTED_CHECKLIST";
	final static public String SELECTED_SECTION = "SELECTED_SECTION";

	final static private String ACTIVITY_NAME = ChecklistRundownScreen.class.getSimpleName();
	final static private String SM_KEY = ACTIVITY_NAME + "_sm";

	private Spinner sectionDropDown;
	private ListView actionItemList;
	private ToggleButton autoAdvanceSectionButton;
	private ToggleButton flashLightButton;
	private ProgressDialog progressDialog;
	private Camera camera;
	private Dialog tutorialImageDialog;

	private List<Section> sections;
	private Checklist selectedChecklist;
	private Section selectedSection;
	private int timerSeconds;
	private int currentActionItemIndex;
	private String tutorialImagePath;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(SELECTED_CHECKLIST, selectedChecklist);
		outState.putParcelable(SELECTED_SECTION, selectedSection);
		outState.putSerializable(SM_KEY, sm);
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_rundown_screen);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if(savedInstanceState != null){
			selectedChecklist = savedInstanceState.getParcelable(SELECTED_CHECKLIST);
			selectedSection = savedInstanceState.getParcelable(SELECTED_SECTION);
			selectedChecklist = (Checklist)savedInstanceState.getParcelable(SELECTED_CHECKLIST);
			sm.setOwner(this);
		} else {
			sm = new ChecklistRundownScreenStateMachine(this);
		}
		sm.setDebugFlag(AppController.getInstance().debugStateMachine());

		sectionDropDown = (Spinner)findViewById(R.id.section_drop_down);
		actionItemList = (ListView)findViewById(R.id.action_item_list);
		autoAdvanceSectionButton = (ToggleButton)findViewById(R.id.auto_advance_section_toggle);
		flashLightButton = (ToggleButton)findViewById(R.id.flash_light_toggle);

		sectionDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedSection = sections.get(position);
				sm.SectionSelected();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		actionItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(currentActionItemIndex == position){
					sm.ActionCompleted();
				} else {
					currentActionItemIndex = position;
					actionItemList.setItemChecked(position,true);
					sm.ResetPosition();
				}
			}
		});

		if(hasFlash()){
			flashLightButton.setEnabled(true);
			flashLightButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					sm.ToggleLight();
				}
			});
		} else {
			flashLightButton.setEnabled(false);
		}

		LinearLayout mainContentArea = (LinearLayout)findViewById(R.id.main_content_area);
		if(mainContentArea != null && !AppController.getInstance().adsDisabled())
			initializeBannerAd(mainContentArea);
    }

	@Override
	protected void onResume() {
		super.onResume();
		Tracker appTracker = AppController.getInstance().appTracker;
		appTracker.setScreenName("Checklist Rundown Screen");
		appTracker.send(new HitBuilders.ScreenViewBuilder().build());
		sm.OnResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case android.R.id.home :
				sm.BackPressed();
				return true;
			default :
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		sm.BackPressed();
	}

	private boolean hasFlash() {
		boolean retVal = false;

		if(camera == null) {
			camera = Camera.open();
			if(camera == null) // still null, means no camera
				return false;
		}

		Camera.Parameters parameters = camera.getParameters();
		if (parameters.getFlashMode() != null) {
			List<String> supportedFlashModes = parameters.getSupportedFlashModes();
			retVal =
				supportedFlashModes != null &&
				supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH);
		}

		camera.release();

		return retVal;
	}

	protected void smExtractIntentParameters() {
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(SELECTED_CHECKLIST)) {
			selectedChecklist = extras.getParcelable(SELECTED_CHECKLIST);
			setTitle(selectedChecklist.name);
			sm.IntentParametersExtracted();
		} else {
			sm.ExtractIntentParametersFailed();
		}
	}

	protected void smLoadChecklist() {
		AsyncTask<Checklist, String, Void> at = new AsyncTask<Checklist, String, Void>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(ChecklistRundownScreen.this);
				progressDialog.setTitle("Loading Sections");
				progressDialog.setMessage("Please wait.");
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(true);
				progressDialog.show();
			}

			@Override
			protected Void doInBackground(Checklist... params) {
				sections = KFlightChecklistDB.SECTIONS_TABLE.getSections(selectedChecklist.id, false);
				for(Section section : sections)
					section.actionItems = KFlightChecklistDB.ACTION_ITEMS_TABLE.getActionItems(section.id);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				progressDialog.dismiss();

				sectionDropDown.setAdapter(new BaseAdapter() {
					@Override
					public int getCount() {
						return sections.size();
					}

					@Override
					public Object getItem(int position) {
						return sections.get(position);
					}

					@Override
					public long getItemId(int position) {
						return sections.get(position).id;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						LayoutInflater inflater = (LayoutInflater) ChecklistRundownScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

						LinearLayout row = (LinearLayout)inflater.inflate(R.layout.row_craft, null);
						TextView tvCraftName = (TextView)row.findViewById(R.id.textview_craft_name);
						tvCraftName.setText(sections.get(position).name);

						return row;
					}
				});

				sm.LoadCompleted();
			}
		};
		at.execute(selectedChecklist);
	}

	private void routeAction(ActionItemAction actionItemAction) {
		if(actionItemAction != null) {
			switch(actionItemAction.actionName){
				case TIMER :
					timerSeconds = Integer.parseInt(actionItemAction.config);
					sm.ShowTimer();
					break;
				default :
					sm.ActionCompleted();
					break;
			}
		} else {
			sm.ActionCompleted();
		}
	}

	protected void smPerformEntryAction() {
		ActionItem actionItem = selectedSection.actionItems.get(actionItemList.getCheckedItemPosition());
		routeAction(actionItem.entryAction);
	}

	protected void smPerformExitAction() {
		ActionItem actionItem = selectedSection.actionItems.get(actionItemList.getCheckedItemPosition());
		routeAction(actionItem.exitAction);
	}

	protected void smShowActionItem() {
		int firstPosition = actionItemList.getFirstVisiblePosition() - actionItemList.getHeaderViewsCount();
		int actPosition = actionItemList.getCheckedItemPosition() - firstPosition;

		if(actionItemList.getCheckedItemPosition() > 0 && actPosition < actionItemList.getChildCount()) {
			View view = actionItemList.getChildAt(actPosition);

			int listHeight = actionItemList.getHeight();
			int rowHeight = view.getHeight();

			actionItemList.smoothScrollToPositionFromTop(actionItemList.getCheckedItemPosition(), listHeight/2 - rowHeight/2);
		}
	}

	protected void smCloseScreen() {
		finish();
	}

	protected void smTransitionDone() {
		sm.Done();
	}

	private class TimerDialogDisplayData {
		Boolean isEntryMethod = null;
		String timerMsgPrefix = null;
		String buttonText = "Cancel Timer";
	}

	protected void smShowTimer() {
		final Dialog d = new Dialog(this);
		d.requestWindowFeature(Window.FEATURE_NO_TITLE);
		d.setContentView(R.layout.timer_alert_dialog);
		d.setCancelable(false);

		ActionItem ai = selectedSection.actionItems.get(currentActionItemIndex);
		final TimerDialogDisplayData dialogData = new TimerDialogDisplayData();

		if(ai.entryAction != null) {
			dialogData.timerMsgPrefix = ai.string2;
			dialogData.buttonText = "Action Performed";
			dialogData.isEntryMethod = true;
		} else if(ai.exitAction != null){
			dialogData.timerMsgPrefix = "Waiting";
			dialogData.buttonText = "Perform Next Action";
			dialogData.isEntryMethod = false;
		}

		final TextView c = (TextView) d.findViewById(R.id.chronometer);
		final TextView s = (TextView) d.findViewById(R.id.string_2);
		Button cancelTimerButton = (Button)d.findViewById(R.id.cancel_timer_button);
		cancelTimerButton.setText(dialogData.buttonText);

		if(dialogData.timerMsgPrefix == null || dialogData.isEntryMethod==null) {
			s.setVisibility(View.GONE);
		} else {
			if(dialogData.isEntryMethod){
				s.setText(String.format("%s\nin", dialogData.timerMsgPrefix));
			} else {
				s.setText(String.format("Waiting"));
			}
		}

		final CountDownTimer cdt = new CountDownTimer(timerSeconds * 1000, 100) {
			@Override
			public void onTick(long millisUntilFinished) {
				String timeStr = null;
				if(millisUntilFinished <= 60000){
					timeStr = String.format("%02d sec",
							TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
				} else {
					timeStr = String.format("%02d min %02d sec",
							TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
							TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
									TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
				}

				c.setText(timeStr);
			}

			@Override
			public void onFinish() {
				if(dialogData.isEntryMethod != null && dialogData.timerMsgPrefix!=null){
					if(dialogData.isEntryMethod){
						s.setText(dialogData.timerMsgPrefix);
					} else {
						s.setVisibility(View.GONE);
					}
				}
				c.setVisibility(View.GONE);
			}
		};

		d.findViewById(R.id.cancel_timer_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ActionItem ai = (ActionItem)actionItemList.getItemAtPosition(currentActionItemIndex);
				if(ai.entryAction != null) {
					TextView chronometer = (TextView)d.findViewById(R.id.chronometer);
					chronometer.setText(ai.string2);
				}

				d.dismiss();
				cdt.cancel();
				sm.TimerExpired();
			}
		});

		d.show();
		cdt.start();
	}

	protected void smShowActionItemImage() {
		if(tutorialImagePath!=null){
			tutorialImageDialog = new Dialog(this);
			tutorialImageDialog.setOnKeyListener(new Dialog.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(keyCode==KeyEvent.KEYCODE_BACK) {
						sm.BackPressed();
					}
					return true;
				}
			});
			tutorialImageDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					sm.Done();
				}
			});
			tutorialImageDialog.setCancelable(true);
			tutorialImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			tutorialImageDialog.setContentView(R.layout.tutorial_image_viewer);

			ImageView tutorialImage = (ImageView)tutorialImageDialog.findViewById(R.id.tutorial_image_view);
			Bitmap bm = BitmapFactory.decodeFile(tutorialImagePath);
			tutorialImage.setImageBitmap(bm);

			new PhotoViewAttacher(tutorialImage);
			tutorialImageDialog.show();
		} else {
			sm.Done();
		}
	}

	protected void smHideActionItemImage() {
		tutorialImageDialog.dismiss();
		tutorialImageDialog = null;
	}

	protected void smRecordNote() {
		sm.Done();
	}

	protected void smToggleLight() {
		try{
			if(flashLightButton.isChecked()){
				camera = Camera.open();
				Camera.Parameters p = camera.getParameters();
				p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				camera.setParameters(p);
				camera.setPreviewTexture(new SurfaceTexture(0));
				camera.startPreview();
			} else {
				camera.stopPreview();
				camera.release();
				camera=null;
			}
		} catch ( Exception e){
			e.printStackTrace();
		}
		sm.Done();
	}

	protected void smAdvanceActionItem() {
		int count = actionItemList.getCount();
		int pos = actionItemList.getCheckedItemPosition();
		if(pos + 1 < count) {
			// mark current as complete
			ActionItem curActionItem = (ActionItem) actionItemList.getItemAtPosition(pos);
			curActionItem.completed = true;
			actionItemList.setItemChecked(pos+1, true);
			currentActionItemIndex = pos+1;
			sm.ActionItemAdvanced();
		} else {
			if(autoAdvanceSectionButton.isChecked()) {
				sm.AdvanceSection();
			} else {
				sm.EndOfList();
			}
		}
	}

	protected void smLoadCurrentSection() {
		selectedSection = (Section)sectionDropDown.getSelectedItem();

		// clear all checked items
		for(ActionItem ai : selectedSection.actionItems)
			ai.completed = false;

		actionItemList.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return selectedSection.actionItems.size();
			}

			@Override
			public Object getItem(int position) {
				return selectedSection.actionItems.get(position);
			}

			@Override
			public long getItemId(int position) {
				return selectedSection.actionItems.get(position).id;
			}

			@Override
			public int getViewTypeCount() {
				return 3;
			}

			@Override
			public int getItemViewType(int position) {
				int retType = 0;
				switch(selectedSection.actionItems.get(position).messageType){
					case ACTION_ITEM:
						retType = 0;
						break;
					case CAUTION:
						retType = 1;
						break;
					case NOTE:
						retType = 2;
						break;
				}
				return retType;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) ChecklistRundownScreen.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final ActionItem actionItem = selectedSection.actionItems.get(position);

				View row = convertView;

				if(row == null){
					ActionItemMessageRow aiwr = null;
					switch(actionItem.messageType) {
						case ACTION_ITEM:
							ActionItemRow air = (ActionItemRow)inflater.inflate(R.layout.row_action_item,null);
							air.populate(actionItem, new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									ChecklistRundownScreen.this.tutorialImagePath = actionItem.image;
									sm.ShowImage();
								}
							});
							row = air;
							break;
						case CAUTION:
							aiwr = (ActionItemMessageRow)inflater.inflate(R.layout.row_action_item_caution,null);
							aiwr.populate(actionItem);
							row = aiwr;
							break;
						case NOTE:
							aiwr = (ActionItemMessageRow)inflater.inflate(R.layout.row_action_item_note,null);
							aiwr.populate(actionItem);
							row = aiwr;
							break;
					}
				} else if(row instanceof ActionItemRow){
					((ActionItemRow)row).populate(actionItem, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ChecklistRundownScreen.this.tutorialImagePath = actionItem.image;
							sm.ShowImage();
						}
					});
				} else if(row instanceof ActionItemMessageRow) {
					((ActionItemMessageRow)row).populate(actionItem);
				}

				return row;
			}
		});

		actionItemList.setItemChecked(0,true);
		currentActionItemIndex = 0;

		sm.LoadCompleted();
	}

	protected void smAdvanceSection() {
		int curSectionPosition = sectionDropDown.getSelectedItemPosition();
		if(curSectionPosition + 1 < sectionDropDown.getCount()){
			sectionDropDown.setSelection(curSectionPosition+1);
		} else {
			sm.EndOfList();
		}
	}

	protected void smShowExtractIntentParametersFailed() {
	}

	protected void smShowChecklistLoadFailed() {
	}
}