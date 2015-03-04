package com.acproma.kflightchecklists.screens;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.acproma.kflightchecklists.adnetworks.AdColonyNetwork;
import com.acproma.kflightchecklists.adnetworks.AdMobNetwork;
import com.acproma.kflightchecklists.adnetworks.AdNetwork;
import com.acproma.kflightchecklists.adnetworks.AdNetworkCallback;
import com.acproma.kflightchecklists.adnetworks.InMobiNetwork;
import com.acproma.kflightchecklists.adnetworks.LeadBoltNetwork;
import com.acproma.kflightchecklists.adnetworks.NativeXNetwork;
import com.acproma.kflightchecklists.adnetworks.StartAppNetwork;
import com.acproma.kflightchecklists.adnetworks.SuperSonicAdsNetwork;
import com.acproma.kflightchecklists.adnetworks.VungleNetwork;
import com.acproma.kflightchecklists.AppController;
import com.acproma.kflightchecklists.BaseActivity;
import com.acproma.kflightchecklists.R;
import com.acproma.kflightchecklists.data.KFlightChecklistDB;
import com.acproma.kflightchecklists.models.Checklist;
import com.acproma.kflightchecklists.models.Craft;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class ChecklistSelectScreen extends BaseActivity<ChecklistSelectScreenStateMachine> {
	final static public String SELECTED_CRAFT = "SELECTED_CRAFT";

	final static private String ACTIVITY_NAME = ChecklistSelectScreen.class.getSimpleName();
	final static private String SM_KEY = ACTIVITY_NAME + "_sm";
	final static private int CHECKLIST_RUNDOWN_REQUEST_CODE = 9000;

	private Craft selectedCraft;
	private List<Checklist> checklists;
	private ListView checklistList;
	private ProgressDialog progressDialog;

	private AdMobNetwork adMobNetwork = null;
	private SuperSonicAdsNetwork superSonicAdsNetwork = null;
	private AdColonyNetwork adColonyNetwork = null;
	private VungleNetwork vungleNetwork = null;
	private NativeXNetwork nativeXNetwork = null;
	private StartAppNetwork startAppNetwork = null;
	private LeadBoltNetwork leadBoltNetwork = null;
	private InMobiNetwork inMobiNetwork = null;

	private Checklist selectedChecklist;
	private ArrayBlockingQueue<AdNetwork> loadedAdNetworks = new ArrayBlockingQueue<AdNetwork>(10);

	private int requestCode;
	private int resultCode;
	private Intent data;

	// AdNetworkCallback ///////////////////////////////////////////////////////////////////////////
	private AdNetworkCallback adNetworkCallback = new AdNetworkCallback() {
		@Override
		public void adLoaded(AdNetwork adNetwork) {
			loadedAdNetworks.add(adNetwork);
			Log.d("KFCLADS", String.format("[%d. %s] Queued", loadedAdNetworks.size(), adNetwork));
		}

		@Override
		public void adCompleted(AdNetwork adNetwork) {
			adNetwork.fetch();
			sm.AdClosed();
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(SELECTED_CRAFT, selectedCraft);
		outState.putSerializable(SM_KEY, sm);
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		if(savedInstanceState != null) {
			selectedCraft = (Craft)savedInstanceState.getParcelable(SELECTED_CRAFT);
			sm = (ChecklistSelectScreenStateMachine)savedInstanceState.getSerializable(SM_KEY);
			sm.setOwner(this);
		} else {
			sm = new ChecklistSelectScreenStateMachine(this);
		}
		sm.setDebugFlag(AppController.getInstance().debugStateMachine());

		setContentView(R.layout.activity_checklist_select_screen);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		checklistList = (ListView)findViewById(R.id.checklist_list);
		checklistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedChecklist = checklists.get(position);
				sm.ChecklistSelected();
			}
		});

		LinearLayout mainContentArea = (LinearLayout)findViewById(R.id.main_content_area);
		if(!AppController.getInstance().adsDisabled()) {
			if(mainContentArea != null)
				initializeBannerAd(mainContentArea);

			// Ad Mob Network Initialize ///////////////////////////////////////////////////////////////
			adMobNetwork = new AdMobNetwork(this);
			adMobNetwork.addAdNetworkCallback(adNetworkCallback);
			adMobNetwork.fetch();

			// Super Sonic Ads Initialize //////////////////////////////////////////////////////////////
			superSonicAdsNetwork = new SuperSonicAdsNetwork(this);
			superSonicAdsNetwork.addAdNetworkCallback(adNetworkCallback);
			superSonicAdsNetwork.fetch();

			// AdColony Initialization /////////////////////////////////////////////////////////////////
			adColonyNetwork = new AdColonyNetwork(this);
			adColonyNetwork.addAdNetworkCallback(adNetworkCallback);
			adColonyNetwork.fetch();

			// Vungle Initialization
			vungleNetwork = new VungleNetwork(this);
			vungleNetwork.addAdNetworkCallback(adNetworkCallback);
			vungleNetwork.fetch();

			// NativeX Initialization
			nativeXNetwork = new NativeXNetwork(this);
			nativeXNetwork.addAdNetworkCallback(adNetworkCallback);
			nativeXNetwork.fetch();

			// StartApp Initialization
			startAppNetwork = new StartAppNetwork(this);
			startAppNetwork.addAdNetworkCallback(adNetworkCallback);
			startAppNetwork.fetch();

			// LeadBolt Initialization
			leadBoltNetwork = new LeadBoltNetwork(this);
			leadBoltNetwork.addAdNetworkCallback(adNetworkCallback);
			leadBoltNetwork.fetch();

			// InMobi Initialization
			inMobiNetwork = new InMobiNetwork(this);
			inMobiNetwork.addAdNetworkCallback(adNetworkCallback);
			inMobiNetwork.fetch();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Tracker appTracker = AppController.getInstance().appTracker;
		appTracker.setScreenName("Checklist Select Screen");
		appTracker.send(new HitBuilders.ScreenViewBuilder().build());

		if(!AppController.getInstance().adsDisabled()){
			superSonicAdsNetwork.onResume(this);
			adColonyNetwork.onResume(this);
			vungleNetwork.onResume(this);
			startAppNetwork.onResume(this);
			leadBoltNetwork.onResume(this);
		}

		sm.OnResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(!AppController.getInstance().adsDisabled()) {
			superSonicAdsNetwork.onPause(this);
			adColonyNetwork.onPause(this);
			vungleNetwork.onPause(this);
			startAppNetwork.onPause(this);
			leadBoltNetwork.onPause(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if(!AppController.getInstance().adsDisabled()){
			superSonicAdsNetwork.onDestroy(this);
			nativeXNetwork.onDestroy(this);
			leadBoltNetwork.onDestroy(this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.requestCode = requestCode;
		this.resultCode = resultCode;
		this.data = data;
		sm.OnActivityResult();
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

	// State Machine Methods ///////////////////////////////////////////////////////////////////////
	protected void smExtractIntentParameters() {
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(SELECTED_CRAFT)) {
			selectedCraft = extras.getParcelable(SELECTED_CRAFT);
			setTitle(selectedCraft.name);
			sm.IntentParametersExtracted();
		} else {
			sm.MissingIntentParameters();
		}
	}

	protected void smCloseScreen() {
		finish();
	}

	protected void smTransitionDone() {
		sm.Done();
	}

	protected void smLoadChecklists(){
		AsyncTask<Craft, String, Void> at = new AsyncTask<Craft, String, Void>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(ChecklistSelectScreen.this);
				progressDialog.setTitle("Loading Checklists");
				progressDialog.setMessage("Please wait.");
				progressDialog.setCancelable(false);
				progressDialog.setIndeterminate(true);
				progressDialog.show();
			}

			@Override
			protected Void doInBackground(Craft... params) {
				checklists = KFlightChecklistDB.getInstance().CHECKLISTS_TABLE.getChecklists(selectedCraft.id, false);
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);
				progressDialog.dismiss();
				progressDialog = null;
				checklistList.setAdapter(new BaseAdapter() {
					@Override
					public int getCount() {
						return checklists.size();
					}

					@Override
					public Object getItem(int position) {
						return checklists.get(position);
					}

					@Override
					public long getItemId(int position) {
						return checklists.get(position).id;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						LayoutInflater inflater = (LayoutInflater) AppController.getInstance()
								.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

						LinearLayout row = (LinearLayout)inflater.inflate(R.layout.row_craft, null);
						TextView tvCraftName = (TextView)row.findViewById(R.id.textview_craft_name);
						tvCraftName.setText(checklists.get(position).name);

						return row;
					}
				});
				sm.LoadCompleted();
			}
		};
		at.execute(selectedCraft);
	}

	protected void smShowChecklistRundownScreen() {
		Intent intent = new Intent(this, ChecklistRundownScreen.class);
		intent.putExtra(ChecklistRundownScreen.SELECTED_CHECKLIST, selectedChecklist);
		startActivityForResult(intent, CHECKLIST_RUNDOWN_REQUEST_CODE);
	}

	protected void smDisplayInterstitialAd() {
		AdNetwork adNetwork = loadedAdNetworks.poll();

		if(adNetwork == null)
			sm.AdNotLoaded();
		else {
			Log.d("KFCLADS", String.format("Showing Ad Network [%s]", adNetwork.getClass().getName()));
			adNetwork.show();
		}
	}

	protected void smShowChecklistsLoadFailed() {
		// TODO
	}

	protected void smShowMissingIntentParameters() {
		// TODO
	}

}