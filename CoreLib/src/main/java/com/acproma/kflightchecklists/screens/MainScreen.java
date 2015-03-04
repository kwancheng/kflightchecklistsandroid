package com.acproma.kflightchecklists.screens;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.acproma.kflightchecklists.AppController;
import com.acproma.kflightchecklists.BaseActivity;
import com.acproma.kflightchecklists.R;
import com.acproma.kflightchecklists.adnetworks.AdMobNetwork;
import com.acproma.kflightchecklists.data.KFlightChecklistDB;
import com.acproma.kflightchecklists.models.ActionItem;
import com.acproma.kflightchecklists.models.Checklist;
import com.acproma.kflightchecklists.models.Craft;
import com.acproma.kflightchecklists.models.ActionItemAction;
import com.acproma.kflightchecklists.models.MessageType;
import com.acproma.kflightchecklists.models.Section;
import com.acproma.kflightchecklists.utils.ActionItemActionTypeAdapter;
import com.acproma.kflightchecklists.utils.Graphics;
import com.acproma.kflightchecklists.utils.MessageTypeTypeAdapter;
import com.google.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainScreen extends BaseActivity<MainScreenStateMachine> {
    final static private String ACTIVITY_NAME = MainScreen.class.getSimpleName();
    final static private String SM_KEY = ACTIVITY_NAME + "_sm";
	final static private int CHECKLIST_SELECT_SCREEN_REQUEST_CODE = 9000;

    private ProgressDialog progressDialog = null;
    private ListView craftList = null;

	// Transient State Machine Variables ///////////////////////////////////////////////////////////
	private Craft selectedCraft;
	private int requestCode;
	private int resultCode;
	private Intent data;

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SM_KEY, sm);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        if(savedInstanceState != null) {
            sm = (MainScreenStateMachine)savedInstanceState.getSerializable(SM_KEY);
            sm.setOwner(this);
        } else {
            sm = new MainScreenStateMachine(this);
        }
        sm.setDebugFlag(AppController.getInstance().debugStateMachine());

        craftList = (ListView)findViewById(R.id.listview_craft);
	    craftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    MainScreen.this.selectedCraft = crafts.get(position);
			    sm.CraftSelected();
		    }
	    });

	    LinearLayout mainContentArea = (LinearLayout)findViewById(R.id.main_content_area);
	    if(mainContentArea != null && !AppController.getInstance().adsDisabled())
		    initializeBannerAd(mainContentArea);
    }

    @Override
    protected void onResume() {
        super.onResume();
	    Tracker appTracker = AppController.getInstance().appTracker;
	    appTracker.setScreenName("Main Screen");
	    appTracker.send(new HitBuilders.AppViewBuilder().build());
        sm.OnResume();
    }

    @Override
    protected void onDestroy() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.requestCode = requestCode;
		this.resultCode = resultCode;
		this.data = data;
		sm.OnActivityResult();
	}

	private ChecklistPackInfo decompressAsset(String zippedAssetPath, String destLocation) throws Exception {
		final int BUFFER_SIZE = 2048;
		final String internalStoragePath = getFilesDir().getAbsolutePath();

		ChecklistPackInfo clpi = new ChecklistPackInfo();

		ZipInputStream zippedAssetInputStream = new ZipInputStream(getAssets().open(zippedAssetPath));

		ZipEntry zipEntry = null;
		byte[] buffer = new byte[BUFFER_SIZE];

		String fPath = null;
		File f = null;
		while((zipEntry = zippedAssetInputStream.getNextEntry()) != null) {
			fPath = String.format("%s%s%s%s%s", internalStoragePath, File.separator, destLocation, File.separator, zipEntry.getName());
			if(fPath.contains("data.json") && !fPath.contains("__MACOSX"))
				clpi.dataJsonPath = fPath;
			else if(zipEntry.isDirectory() && !fPath.contains("__MACOSX") && fPath.contains("images")){
				clpi.imagesDirectory = fPath;
			}
			f = new File(fPath);
			if(zipEntry.isDirectory()) {
				if(!f.isDirectory())
					f.mkdirs();
			} else {
				if(!f.getParentFile().exists())
					f.getParentFile().mkdirs();

				FileOutputStream fos = new FileOutputStream(f);
				int count = -1;
				while((count = zippedAssetInputStream.read(buffer, 0, BUFFER_SIZE)) != -1)
					fos.write(buffer, 0, count);
				zippedAssetInputStream.closeEntry();
				fos.close();
			}
		}

		zippedAssetInputStream.close();
		return clpi;
	}

	private void loadCraft(Craft craft, List<String> partialErrors) {
		boolean containsCraft = KFlightChecklistDB.getInstance().CRAFTS_TABLE.contains(craft);
		if(containsCraft) {
			partialErrors.add(String.format("Skipped Loading Craft[%s] Version[%s] Is POH[%s] found in db.",
				craft.name, craft.version, Boolean.toString(craft.isPoh)
			));
		} else {
			KFlightChecklistDB.getInstance().CRAFTS_TABLE.addCraft(craft, false);
			for(int checklistIndex = 0; checklistIndex < craft.checklists.size(); checklistIndex++) {
				Checklist checklist = craft.checklists.get(checklistIndex);
				checklist.craftId = craft.id;
				checklist.displayOrder = checklistIndex;
				loadChecklist(craft, checklist, partialErrors);
			}
		}
	}

	private void loadChecklist(Craft craft, Checklist checklist, List<String> partialErrors) {
		boolean containsChecklist = KFlightChecklistDB.getInstance().CHECKLISTS_TABLE.contains(checklist);
		if(containsChecklist) {
            partialErrors.add(String.format("Skipped loading checklist[%s]. Duplicate checklist name in craft[%s]",
                checklist.name, craft.name
            ));
		} else {
			try {
				KFlightChecklistDB.getInstance().CHECKLISTS_TABLE.addChecklist(checklist, false);
				for(int sectionIndex = 0; sectionIndex < checklist.sections.size(); sectionIndex++) {
					Section section = checklist.sections.get(sectionIndex);
					section.checklistId = checklist.id;
					section.displayOrder = sectionIndex;
					loadSection(craft, checklist, section, partialErrors);
				}
			} catch (Exception checklistException) {
				partialErrors.add(String.format("Error adding checklist[%s] error[%s]",
						checklist.name, checklistException.getLocalizedMessage()));
				checklistException.printStackTrace();
			}
		}
	}

	private void loadSection(Craft craft, Checklist checklist, Section section, List<String> partialErrors) {
		boolean containsSection = KFlightChecklistDB.SECTIONS_TABLE.contains(section);
		if(containsSection){
            partialErrors.add(String.format(
                    "Skipped loading section[%s]. Duplicate section name in craft[%s] checklist[%s]",
                    section.name, craft.name, checklist.name
            ));
		} else {
            try {
                KFlightChecklistDB.SECTIONS_TABLE.addSection(section,false);

                for(int actionItemIndex = 0; actionItemIndex < section.actionItems.size(); actionItemIndex++) {
                    ActionItem actionItem = section.actionItems.get(actionItemIndex);
                    actionItem.sectionId = section.id;
                    actionItem.displayOrder = actionItemIndex;
	                if(actionItem.image != null)
	                    actionItem.image = String.format("%s%s%s", craft.imagesDirectory, File.separator, actionItem.image);
                    try{
						KFlightChecklistDB.ACTION_ITEMS_TABLE.addActionItem(actionItem);
                    } catch(Exception actionItemAddException) {
                        partialErrors.add(String.format(
                                "Error adding action item number[%d] of craft[%s] checklist[%s] section[%s]",
                                actionItemIndex, craft.name, checklist.name, section.name
                        ));
                        actionItemAddException.printStackTrace();
                    }
                }
            } catch (Exception sectionAddException) {
                partialErrors.add(String.format(
                        "Error adding section[%s] error[%s]",
                        section.name, sectionAddException.getLocalizedMessage()
                ));
                sectionAddException.printStackTrace();
            }
		}
	}

	private void loadAssets() {
        AsyncTask<Void, String, Boolean> initTask = new AsyncTask<Void, String, Boolean>() {
            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(MainScreen.this);
                progressDialog.setTitle("Loading Crafts");
                progressDialog.setMessage("Please wait.");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
	            KFlightChecklistDB.getInstance().getWritableDatabase().close();
	            KFlightChecklistDB.getInstance().clear();

                String dataFolderName = getString(R.string.asset_data_dir_name);
                String[] tList = null;
                try {
                    publishProgress("Searching for Preloaded Crafts");
                    tList = getAssets().list("");
                    boolean foundDataFolder = false;
                    for(String listItem : tList)
                        if(listItem.equalsIgnoreCase(dataFolderName)) {
                            foundDataFolder = true;
                            break;
                        }

                    if(foundDataFolder) {
	                    // Create Gson Parser
                        GsonBuilder gb = new GsonBuilder();
                        gb.registerTypeAdapter(ActionItemAction.class, new ActionItemActionTypeAdapter());
                        gb.registerTypeAdapter(MessageType.class, new MessageTypeTypeAdapter());
                        Gson gson = gb.create();

                        // clear the database
	                    KFlightChecklistDB kFlightChecklistDB = KFlightChecklistDB.getInstance();
	                    kFlightChecklistDB.clear();

                        tList = getAssets().list(dataFolderName);
                        publishProgress(String.format("Loading %d Preloaded Crafts", tList.length));

                        ArrayList<String> partialErrors = new ArrayList<String>();

                        for(int craftIndex = 0; craftIndex < tList.length; craftIndex++) {
                            try{
	                            String zippedAssetPath = String.format("%s%s%s", dataFolderName, File.separator, tList[craftIndex]);
	                            // decompress to internal storage
	                            ChecklistPackInfo clpi = decompressAsset(zippedAssetPath, dataFolderName);

								if(clpi.dataJsonPath == null) {
									partialErrors.add(String.format("Checklist Pack [%s] does not contain a data.json file.", tList[craftIndex]));
								} else if(clpi.imagesDirectory == null){
									partialErrors.add(String.format("Checklist Pack [%s] does not contain a images directory.", tList[craftIndex]));
								} else {
									InputStreamReader isr = new InputStreamReader(new FileInputStream(clpi.dataJsonPath));

									Craft craft = gson.fromJson(isr, Craft.class);
									craft.displayOrder = craftIndex;
									craft.imagesDirectory = clpi.imagesDirectory;
									loadCraft(craft, partialErrors);
								}
                            } catch (Exception craftProcessingError) {
                                partialErrors.add(String.format(
                                        "Error processing preload craft file [%s] error [%s].",
                                        tList[craftIndex], craftProcessingError.getLocalizedMessage()));
	                            craftProcessingError.printStackTrace();
                            }
                        }

                        // report partial errors
                        for(String partialError : partialErrors)
                            reportToLocalytics(ACTIVITY_NAME, partialError);
                        publishProgress("Load Completed");
                        return true;
                    } else {
                        reportToLocalytics(ACTIVITY_NAME, "Data Folder Not Found");
                        return false;
                    }
                } catch (IOException e) {
                    reportToLocalytics(ACTIVITY_NAME, String.format("Error Loading Assets - " + e.getLocalizedMessage()));
	                e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                progressDialog.dismiss();
	            progressDialog = null;
                if(aBoolean){
                    AppController.getInstance().setHasInitialized(true);
                    sm.InitializationComplete();
                } else {
                    sm.InitializationFailed();
                }
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                if(values.length > 0)
                    progressDialog.setMessage(values[0]);
            }
        };
        initTask.execute();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.menu_item_reload_data) {
			sm.ReloadAssets();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		sm.BackPressed();
	}

	// StateMachine Methods ////////////////////////////////////////////////////////////////////////
    protected void smInitializeApp() {
        if(!AppController.getInstance().hasInitialized()) {
            loadAssets();
        } else {
            sm.InitializationComplete();
        }
    }

	protected void smReloadAssets() {
		loadAssets();
	}

    protected void smCloseApp() {
		finish();
    }


    private List<Craft> crafts = null;
    private Exception getCraftsError = null;

    protected void smLoadCrafts() {
        AsyncTask<Void, String, Boolean> craftLoadTask = new AsyncTask<Void, String, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(MainScreen.this);
                progressDialog.setTitle("Loading Crafts");
                progressDialog.setMessage("Please wait.");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
	                KFlightChecklistDB kFlightChecklistDB = KFlightChecklistDB.getInstance();
	                crafts = kFlightChecklistDB.CRAFTS_TABLE.getCrafts();
                    return true;
                } catch(Exception getCraftsError) {
                    MainScreen.this.getCraftsError = getCraftsError;
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean) {
                    craftList.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return crafts.size();
                        }

                        @Override
                        public Object getItem(int position) {
                            return crafts.get(position);
                        }

                        @Override
                        public long getItemId(int position) {
                            return crafts.get(position).id;
                        }

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            LayoutInflater inflater = (LayoutInflater)AppController.getInstance()
                                    .getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            LinearLayout row = (LinearLayout)inflater.inflate(R.layout.row_craft, null);
                            TextView tvCraftName = (TextView)row.findViewById(R.id.textview_craft_name);
                            tvCraftName.setText(crafts.get(position).name);

                            return row;
                        }
                    });
                    sm.LoadCompleted();
                } else {
                    sm.LoadFailed();
                }
                progressDialog.dismiss();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }
        };
        craftLoadTask.execute();
    }

	protected void smShowChecklistSelectScreen(){
		Intent intent = new Intent(this, ChecklistSelectScreen.class);
		intent.putExtra(ChecklistSelectScreen.SELECTED_CRAFT, selectedCraft);
		startActivityForResult(intent, CHECKLIST_SELECT_SCREEN_REQUEST_CODE);
	}

	protected void smShowDisclaimer() {
		if(AppController.getInstance().shownDislcaimer()){
			sm.Acknowledged();
		} else {
			final Dialog d = new Dialog(this);
			d.setContentView(R.layout.dialog_disclaimer);
			d.setTitle("Disclaimer");
			d.setCancelable(false);

			TextView disclaimerMsg = (TextView)d.findViewById(R.id.disclaimer_message);
			disclaimerMsg.setText(getResources().getText(R.string.disclaimer));

			final Button acknowledgementButton = (Button)d.findViewById(R.id.acknowledgement_button);
			acknowledgementButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AppController.getInstance().setShownDisclaimer(true);
					d.dismiss();
					sm.Acknowledged();
				}
			});
			acknowledgementButton.setEnabled(false);

			CheckBox acknowledgementCheckbox = (CheckBox)d.findViewById(R.id.acknowledgement_checkbox);
			acknowledgementCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					acknowledgementButton.setEnabled(isChecked);
				}
			});
			d.show();
		}
	}

	private class ChecklistPackInfo {
		String dataJsonPath;
		String imagesDirectory;
	}

	protected void smShowInitializationFailed() {

	}

	protected void smLoadCraftsFailed() {

	}
}