package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;
import android.util.Log;

import com.acproma.kflightchecklists.R;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyConnectFlag;
import com.tapjoy.TapjoyConnectNotifier;

import java.util.Hashtable;

/**
 * Created by Congee on 10/17/14.
 */
public class TapJoyNetwork extends AdNetwork {
	// TapJoy Connect Listener
	private TapjoyConnectNotifier tapjoyConnectNotifier = new TapjoyConnectNotifier() {
		@Override
		public void connectSuccess() {
			Log.d("KFCLADS", "TapJoy connectSuccess");
			fireAdLoaded();
		}

		@Override
		public void connectFail() {
			Log.d("KFCLADS", "TapJoy connectFail");
		}
	};

	public TapJoyNetwork(Activity activity){
		super(activity);
		Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
		connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");
		connectFlags.put(TapjoyConnectFlag.USER_ID, "KFCL");
		String appKey = activity.getString(R.string.tap_joy_app_key);
		String secretKey = activity.getString(R.string.tap_joy_secret_key);

		TapjoyConnect.requestTapjoyConnect(
				activity, appKey, secretKey, connectFlags,
				tapjoyConnectNotifier);
	}

	@Override
	public void fetch() {

	}

	@Override
	public void show() {

	}
}
