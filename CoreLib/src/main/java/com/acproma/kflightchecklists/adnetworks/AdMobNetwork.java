package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.util.Log;

import com.acproma.kflightchecklists.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

/**
 * Created by Congee on 10/17/14.
 */
public class AdMobNetwork extends AdNetwork {
	// AdListener AdMob
	private AdListener adListener = new AdListener() {
		@Override
		public void onAdLoaded() {
			super.onAdLoaded();
			fireAdLoaded();
			Log.d("KFCLADS", "AdMob onAdLoaded");
		}

		@Override
		public void onAdOpened() {
			super.onAdOpened();
			Log.d("KFCLADS", "AdMob onAdOpened");
		}

		@Override
		public void onAdClosed() {
			super.onAdClosed();
			fireAdCompleted();
			Log.d("KFCLADS", "AdMob onAdClosed");
		}

		@Override
		public void onAdLeftApplication() {
			super.onAdLeftApplication();
			Log.d("KFCLADS", "AdMob onAdLeftApplication");
		}

		@Override
		public void onAdFailedToLoad(int errorCode) {
			super.onAdFailedToLoad(errorCode);
			// TODO need to log this ad load error
			Log.d("KFCLADS", String.format("AdMob onAdFailedToLoad errorCode[%d]", errorCode));
		}
	};

	private InterstitialAd interstitialAd = null;

	public AdMobNetwork(Activity activity) {
		super(activity);

		String adUnitId = activity.getString(R.string.ad_mob_ad_unit_id);

		interstitialAd = new InterstitialAd(activity);
		interstitialAd.setAdUnitId(adUnitId);
		interstitialAd.setAdListener(adListener);
	}

	public void fetch() {
		interstitialAd.loadAd(new AdRequest.Builder().build());
	}

	public void show() {
		interstitialAd.show();
	}
}