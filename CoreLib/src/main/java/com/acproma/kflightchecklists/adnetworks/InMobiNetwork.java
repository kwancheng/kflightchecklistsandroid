package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.util.Log;

import com.acproma.kflightchecklists.R;
import com.inmobi.commons.InMobi;
import com.inmobi.monetization.IMErrorCode;
import com.inmobi.monetization.IMInterstitial;
import com.inmobi.monetization.IMInterstitialListener;

import java.util.Map;

/**
 * Created by kwan.cheng on 10/23/2014.
 */
public class InMobiNetwork extends AdNetwork {
	private IMInterstitial interstitial = null;
	private IMInterstitialListener imInterstitialListener = new IMInterstitialListener() {
		@Override
		public void onInterstitialFailed(IMInterstitial imInterstitial, IMErrorCode imErrorCode) {
			Log.d("KFCLADS", String.format("InMobi onInterstitialFailed imErrorCode[%s]", imErrorCode));
		}

		@Override
		public void onInterstitialLoaded(IMInterstitial imInterstitial) {
			Log.d("KFCLADS", String.format("InMobi onIntersitialLoaded"));
			fireAdLoaded();
		}

		@Override
		public void onShowInterstitialScreen(IMInterstitial imInterstitial) {
			Log.d("KFCLADS", String.format("InMobi onShowInterstitialScreen"));
		}

		@Override
		public void onDismissInterstitialScreen(IMInterstitial imInterstitial) {
			Log.d("KFCLADS", String.format("InMobi onDismissInterstitialScreen"));
			fireAdCompleted();
		}

		@Override
		public void onInterstitialInteraction(IMInterstitial imInterstitial, Map<String, String> stringStringMap) {
			Log.d("KFCLADS", String.format("InMobi onInterstitialInteraction stringStringMap[%s]", stringStringMap.toString()));
		}

		@Override
		public void onLeaveApplication(IMInterstitial imInterstitial) {
			Log.d("KFCLADS", String.format("InMobi onLeaveApplication"));
		}
	};

	public InMobiNetwork(Activity activity) {
		super(activity);
		InMobi.initialize(activity, activity.getString(R.string.inmobi_app_key));
		interstitial = new IMInterstitial(activity, activity.getString(R.string.inmobi_app_key));
		interstitial.setIMInterstitialListener(imInterstitialListener);
	}

	@Override
	public void fetch() {
		interstitial.loadInterstitial();
	}

	@Override
	public void show() {
		interstitial.show();
	}
}
