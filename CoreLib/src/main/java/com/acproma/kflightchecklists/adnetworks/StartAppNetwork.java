package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.util.Log;

import com.acproma.kflightchecklists.R;
import com.startapp.android.publish.Ad;
import com.startapp.android.publish.AdDisplayListener;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;

/**
 * Created by Congee on 10/17/14.
 */
public class StartAppNetwork extends AdNetwork {
	// StartApp Load Listener
	private AdEventListener startAppLoadEventListener = new AdEventListener() {
		@Override
		public void onReceiveAd(Ad ad) {
			Log.d("KFCLADS", String.format("StartApp onReceiveAd [%s]", ad));
			fireAdLoaded();
		}

		@Override
		public void onFailedToReceiveAd(Ad ad) {
			Log.d("KFCLADS", String.format("StartApp onFailedToReceiveAd [%s]", ad));
		}
	};

	// StartApp Ad Display
	private AdDisplayListener startAppAdDisplayListener = new AdDisplayListener() {
		@Override
		public void adHidden(Ad ad) {
			Log.d("KFCLADS", String.format("StartApp adHidden [%s]", ad));
			fireAdCompleted();
		}

		@Override
		public void adDisplayed(Ad ad) {
			Log.d("KFCLADS", String.format("StartApp adDisplayed [%s]", ad));
		}

		@Override
		public void adClicked(Ad ad) {
			Log.d("KFCLADS", String.format("StartApp adClicked [%s]", ad));
		}
	};

	private StartAppAd startAppAd = null;

	public StartAppNetwork(Activity activity){
		super(activity);
		String developerId = activity.getString(R.string.start_app_developer_id);
		String appKey = activity.getString(R.string.start_app_app_key);
		StartAppSDK.init(activity, developerId, appKey, true);
		startAppAd = new StartAppAd(activity);
	}

	@Override
	public void fetch() {
		startAppAd.loadAd(startAppLoadEventListener);
	}

	@Override
	public void show() {
		startAppAd.showAd(startAppAdDisplayListener);
	}

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		startAppAd.onResume();
	}

	@Override
	public void onPause(Activity activity) {
		super.onPause(activity);
		startAppAd.onPause();
	}
}
