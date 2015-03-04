package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.util.Log;

import com.acproma.kflightchecklists.R;
import com.supersonicads.sdk.SSAFactory;
import com.supersonicads.sdk.SSAPublisher;
import com.supersonicads.sdk.listeners.OnInterstitialListener;

/**
 * Created by Congee on 10/17/14.
 */
public class SuperSonicAdsNetwork extends AdNetwork {
	// SuperSonicAds Listener
	private OnInterstitialListener ssaInterstitialListener = new OnInterstitialListener() {
		@Override
		public void onISInitSuccess() {
			// Invoked when Interstitial initialization process completes successfully.
			fireAdLoaded();
			Log.d("KFCLADS", "SSA onISInitSuccess");
		}

		@Override
		public void onISInitFail(String userAgent) {
			// Invoked when Interstitial initialization process is failed.
			// @param description - A String which represents the reason of initialization
			Log.d("KFCLADS", String.format("SSA onISInitFail userAgent[%s]", userAgent));
		}

		@Override
		public void onISLoaded() {
			// Invoked when ad has been successfully loaded and the ad is ready to be shown to the user.
			fireAdLoaded();
			Log.d("KFCLADS", "SSA onISLoaded");
		}

		@Override
		public void onISLoadedFail(String userAgent) {
			// Invoked when the ad is failed to load or when there is no ad available.
			// @param description - A String which represents the reason of ad loading failure.
			Log.d("KFCLADS", String.format("SSA onISLoadedFail userAgent[%s]", userAgent));
		}

		@Override
		public void onISAdClosed() {
			// Invoked when the ad is closed and the user is about to return to the application.
			Log.d("KFCLADS", "SSA onISAdClosed");
			fireAdCompleted();
		}

		@Override
		public void onISGeneric(String userAgent, String userAgent2) {
			Log.d("KFCLADS", String.format("SSA onISGeneric userAgent[%s], userAgent2[%s]", userAgent, userAgent2));
		}
	};

	private SSAPublisher ssaPublisher = null;

	public SuperSonicAdsNetwork(Activity activity) {
		super(activity);

		String appKey = activity.getString(R.string.super_sonic_ads_app_key);
		String userId = activity.getString(R.string.super_sonic_ads_user_id);

		ssaPublisher = SSAFactory.getPublisherInstance(activity);
		ssaPublisher.initInterstitial(appKey, userId, null, ssaInterstitialListener);
	}

	@Override
	public void fetch() {
		ssaPublisher.loadInterstitial();
	}

	@Override
	public void show() {
		ssaPublisher.showInterstitial();
	}

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		ssaPublisher.onResume(activity);
	}

	@Override
	public void onPause(Activity activity) {
		super.onPause(activity);
		ssaPublisher.onPause(activity);
	}

	@Override
	public void onDestroy(Activity activity) {
		super.onDestroy(activity);
		ssaPublisher.release(activity);
	}
}
