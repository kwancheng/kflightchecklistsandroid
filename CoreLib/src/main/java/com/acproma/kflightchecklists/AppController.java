package com.acproma.kflightchecklists;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Congee on 8/3/14.
 */
public class AppController extends Application {
	final static public String META_DATA_IS_PROD = "is_prod";
    final static public String META_DATA_DEBUG_STATE_MACHINE = "debug_state_machine";
    final static private String KEY_HAS_INITIALIZED = "key_has_initialized";
	final static private String KEY_SHOWN_DISCLAIMER = "key_shown_disclaimer";

    private boolean debugStateMachine = false;
	private boolean isProd = true;
	public Tracker appTracker = null;

	static private AppController instance = null;
	static public AppController getInstance() {
		return instance;
	}

	public SharedPreferences getSharedPreferences() {
		return getSharedPreferences(instance.getResources().getString(R.string.preference_file_name), Context.MODE_PRIVATE);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
        // Configure through meta data
        String packageName = getApplicationContext().getPackageName();
        PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo app = null;
        try {
            app = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Bundle bundle = app.metaData;
        debugStateMachine = bundle.getBoolean(META_DATA_DEBUG_STATE_MACHINE, false);
		isProd = bundle.getBoolean(META_DATA_IS_PROD, true);

		String gaTrackerId = getString(R.string.google_analytics_id);

		appTracker = GoogleAnalytics.getInstance(this).newTracker(gaTrackerId);
		appTracker.enableExceptionReporting(true);
		appTracker.enableAutoActivityTracking(true);
		appTracker.enableAdvertisingIdCollection(true);
	}

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public void setHasInitialized(boolean hasInitialized) {
        getSharedPreferences().edit().putBoolean(KEY_HAS_INITIALIZED, hasInitialized).commit();
    }

    public boolean hasInitialized() {
        return getSharedPreferences().getBoolean(KEY_HAS_INITIALIZED, false);
    }

	public void setShownDisclaimer(boolean shownDisclaimer) {
		getSharedPreferences().edit().putBoolean(KEY_SHOWN_DISCLAIMER, shownDisclaimer).commit();
	}

	public boolean shownDislcaimer() {
		return getSharedPreferences().getBoolean(KEY_SHOWN_DISCLAIMER, false);
	}

    public boolean debugStateMachine() {
        return debugStateMachine;
    }
	public boolean isProd(){
		return isProd;
	}

	public boolean adsDisabled() {
		return true;
	}
}
