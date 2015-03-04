package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.util.Log;

import com.acproma.kflightchecklists.R;
import com.appfireworks.android.listener.AppModuleListener;
import com.appfireworks.android.track.AppTracker;
import com.mmxnuhsjdp.AdController;
import com.mmxnuhsjdp.AdListener;

/**
 * Created by Congee on 10/17/14.
 */
public class LeadBoltNetwork extends AdNetwork {
	private AppModuleListener appModuleListener = new AppModuleListener() {
		@Override
		public void onModuleLoaded() {
			Log.d("KFCLADS", "LeadBolt onModuleLoaded");
		}

		@Override
		public void onModuleClosed() {
			Log.d("KFCLADS", "LeadBolt onModuleClosed");
		}

		@Override
		public void onModuleFailed() {
			Log.d("KFCLADS", "LeadBolt onModuleFailed");
		}

		@Override
		public void onModuleCached() {
			Log.d("KFCLADS", "LeadBolt onModuleCached");
		}
	};

	private AdListener adListener = new AdListener() {
		@Override
		public void onAdClicked() {
			Log.d("KFCLADS", "LeadBolt onAdClicked");
		}

		@Override
		public void onAdCached() {
			Log.d("KFCLADS", "LeadBolt onAdCached");
			fireAdLoaded();
		}

		@Override
		public void onAdFailed() {
			Log.d("KFCLADS", "LeadBolt onAdFailed");
		}

		@Override
		public void onAdLoaded() {
			Log.d("KFCLADS", "LeadBolt onAdLoaded");
		}

		@Override
		public void onAdClosed() {
			Log.d("KFCLADS", "LeadBolt onAdClosed");
			fireAdCompleted();
		}
	};

	private AdController interstitial = null;

	public LeadBoltNetwork(Activity activity){
		super(activity);
		String appFireworksApiKey = activity.getString(R.string.lead_bolt_app_fireworks_api_key);
		AppTracker.startSession(activity, appFireworksApiKey, appModuleListener);
	}

	@Override
	public void fetch() {
		String interstitialId = activity.getString(R.string.lead_bolt_interstitial_id);
		interstitial = new AdController(activity, interstitialId, adListener);
		interstitial.loadAdToCache();
	}

	@Override
	public void show() {
		interstitial.loadAd();
	}

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		AppTracker.resume(activity.getApplicationContext());
	}

	@Override
	public void onPause(Activity activity) {
		AppTracker.pause(activity.getApplicationContext());
		AppTracker.closeSession(activity.getApplicationContext(),true);
		super.onPause(activity);
	}

	@Override
	public void onDestroy(Activity activity) {
		super.onDestroy(activity);
		interstitial.destroyAd();
	}
}
