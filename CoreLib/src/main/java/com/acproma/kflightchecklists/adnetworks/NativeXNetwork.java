package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.util.Log;

import com.acproma.kflightchecklists.R;
import com.nativex.monetization.MonetizationManager;
import com.nativex.monetization.enums.AdEvent;
import com.nativex.monetization.enums.NativeXAdPlacement;
import com.nativex.monetization.listeners.OnAdEventV2;
import com.nativex.monetization.listeners.SessionListener;
import com.nativex.monetization.mraid.AdInfo;

/**
 * Created by Congee on 10/17/14.
 */
public class NativeXNetwork extends AdNetwork{
	// NativeX Fetch Ad Listener
	private OnAdEventV2 nativeXFetchAdEvent = new OnAdEventV2() {
		@Override
		public void onEvent(AdEvent adEvent, AdInfo adInfo, String message) {
			Log.d("KFCLADS", String.format("NativeX nativeXFetchAdEvent.onEvent adEvent[%s] adInfo[%s] message[%s]", adEvent, adInfo, message));
			fireAdLoaded();
		}
	};

	// NativeX Show Ad Listener
	private OnAdEventV2 nativeXShowAdEvent = new OnAdEventV2() {
		@Override
		public void onEvent(AdEvent adEvent, AdInfo adInfo, String message) {
			Log.d("KFCLADS", String.format("NativeX nativeXShowAdEvent.onEvent adEvent[%s] adInfo[%s] message[%s]", adEvent, adInfo, message));
			switch(adEvent){
				case DISMISSED:
					fireAdCompleted();
					break;
			}
		}
	};

	private boolean sessionReady = false;

	public NativeXNetwork(final Activity activity) {
		super(activity);
		String appKey = activity.getString(R.string.native_x_app_key);
		MonetizationManager.createSession(activity, appKey, new SessionListener() {
			@Override
			public void createSessionCompleted(boolean success, boolean isOfferWallEnabled, String sessionId) {
				Log.d("KFCLADS", String.format("NativeX createSessionCompleted success[%s] isOfferWallEnabled[%s] sessionId[%s]",
						(success) ? "true" : "false", (isOfferWallEnabled) ? "enabled" : "disabled", sessionId
				));
				sessionReady = true;
				MonetizationManager.fetchAd(activity, NativeXAdPlacement.Game_Launch, nativeXFetchAdEvent);
			}
		});
	}

	@Override
	public void fetch() {
		if(sessionReady)
			MonetizationManager.fetchAd(activity, NativeXAdPlacement.Game_Launch, nativeXFetchAdEvent);
	}

	@Override
	public void show() {
		MonetizationManager.showAd(activity, NativeXAdPlacement.Game_Launch, nativeXShowAdEvent);
	}

	@Override
	public void onDestroy(Activity activity) {
		super.onDestroy(activity);
		MonetizationManager.release();
	}
}
