package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import com.acproma.kflightchecklists.AppController;
import com.acproma.kflightchecklists.R;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;

/**
 * Created by Congee on 10/17/14.
 */
public class AdColonyNetwork extends AdNetwork {
	// AdColony Ad Availability Listener
	private AdColonyAdAvailabilityListener adColonyAdAvailabilityListener = new AdColonyAdAvailabilityListener() {
		@Override
		public void onAdColonyAdAvailabilityChange(boolean available, String zoneId) {
			Log.d("KFCLADS", String.format("AdColony available[%s] zoneId[%s]", available ? "Available" : "Not Available", zoneId));
			if(available)
				fireAdLoaded();
		}
	};

	// AdColony Ad Listener
	private AdColonyAdListener adColonyAdListener = new AdColonyAdListener() {
		@Override
		public void onAdColonyAdStarted(AdColonyAd adColonyAd) {
			Log.d("KFCLADS", "AdColony onAdColonyAdStarted");
		}

		@Override
		public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {
			Log.d("KFCLADS", "AdColony onAdColonyAdAttemptFinished");
			fireAdCompleted();
			fireAdLoaded();
		}
	};

	private AdColonyVideoAd ad = null;

	public AdColonyNetwork(Activity activity){
		super(activity);
		String versionString = "";

		try {
			PackageManager pm = AppController.getInstance().getPackageManager();
			String packageName = AppController.getInstance().getPackageName();

			versionString = pm.getPackageInfo(packageName, 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			versionString = "1.0.0";
		}

		String appKey = activity.getString(R.string.ad_colony_app_key);
		String zoneId = activity.getString(R.string.ad_colony_zone_id);

		String clientOptions = String.format("version:%s,store:google", versionString );
		AdColony.configure(activity, clientOptions, appKey, zoneId);
		AdColony.addAdAvailabilityListener(adColonyAdAvailabilityListener);
		ad = new AdColonyVideoAd(zoneId);
		ad.withListener(adColonyAdListener);
	}

	@Override
	public void fetch() {
	}

	@Override
	public void show() {
		ad.show();
	}

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		AdColony.resume(activity);
	}

	@Override
	public void onPause(Activity activity) {
		super.onPause(activity);
		AdColony.pause();
	}
}