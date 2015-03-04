package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.util.Log;

import com.acproma.kflightchecklists.R;
import com.vungle.publisher.AdConfig;
import com.vungle.publisher.EventListener;
import com.vungle.publisher.Orientation;
import com.vungle.publisher.VunglePub;

/**
 * Created by Congee on 10/17/14.
 */
public class VungleNetwork extends AdNetwork {
	// Vungle Event Listener
	private EventListener vungleEventListener = new EventListener() {
		@Override
		public void onCachedAdAvailable() {
			Log.d("KFCLADS", "Vungle onCachedAdAvailable");
			fireAdLoaded();
		}

		@Override
		public void onAdEnd(boolean wasCallToActionClicked) {
			Log.d("KFCLADS", String.format("Vungle onAdEnd wasCallToActionClicked[%s]", wasCallToActionClicked ? "true" : "false"));
			fireAdCompleted();
			fireAdLoaded();
		}

		@Override
		public void onAdStart() {
			Log.d("KFCLADS", "Vungle onAdStart");
		}

		@Override
		public void onAdUnavailable(String s) {
			Log.d("KFCLADS", String.format("Vungle onAdUnavailable s[%s]",s));
			fireAdCompleted();
			fireAdLoaded();
		}

		@Override
		public void onVideoView(boolean isCompletedView, int watchedMillis, int videoDurationMillis) {
			Log.d("KFCLADS", String.format("Vungle onVideoView isCompletedView[%s] watchedMillis[%d] videoDurationMillis[%d]", isCompletedView?"true":"false", watchedMillis, videoDurationMillis));
		}
	};

	private VunglePub vunglePub = null;

	public VungleNetwork(Activity activity) {
		super(activity);
		vunglePub = VunglePub.getInstance();
		String appKey = activity.getString(R.string.vungle_app_key);
		vunglePub.init(activity, appKey);
		vunglePub.setEventListener(vungleEventListener);
		AdConfig overrideConfig = vunglePub.getGlobalAdConfig();
		overrideConfig.setOrientation(Orientation.autoRotate);
	}

	@Override
	public void fetch() {

	}

	@Override
	public void show() {
		vunglePub.playAd();
	}

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
		vunglePub.onResume();
	}

	@Override
	public void onPause(Activity activity) {
		super.onPause(activity);
		vunglePub.onPause();
	}
}
