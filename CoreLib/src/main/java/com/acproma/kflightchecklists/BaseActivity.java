package com.acproma.kflightchecklists;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.LinearLayout;

import com.acproma.kflightchecklists.adnetworks.AdMobNetwork;
import com.acproma.kflightchecklists.utils.Graphics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.localytics.android.LocalyticsAmpSession;
import com.supersonicads.sdk.SSAPublisher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import statemap.FSMContext;

/**
 * Created by kwan.cheng on 9/18/2014.
 */
public class BaseActivity<T extends FSMContext> extends FragmentActivity {
//    private LocalyticsAmpSession localyticsSession = null;
	protected T sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        localyticsSession = new LocalyticsAmpSession(getApplicationContext());
//        getApplication().registerActivityLifecycleCallbacks(
//                new LocalyticsActivityLifecycleCallbacks(localyticsSession));
    }

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	protected void reportToLocalytics(String activity, String message) {
	    Log.d("Error Reporter", String.format("%s[%s]", activity, message));

        Map<String, String> m = new HashMap<String, String>();
        m.put("activity", activity);
        m.put("message", message);
//        localyticsSession.tagEvent("Error", m);
    }

	public void smReportUnknownTransition() {
		String msg = String.format("Unknown Transition : [%s, %s]",
				sm.getPreviousState().getName(), sm.getTransition());
		Log.d(getClass().getSimpleName(), msg);

		Map<String,String> m = new HashMap<String, String>();
		m.put("Previous State", sm.getPreviousState().getName());
		m.put("Transition", sm.getTransition());
//		localyticsSession.tagEvent("Unknown Transition",m);
	}

	protected void initializeBannerAd(final LinearLayout mainContentArea) {
		final AdView adMobBanner = new AdView(this);
		String bannerAdUnitId = getString(R.string.ad_mob_banner_ad_unit_id);
		adMobBanner.setAdUnitId(bannerAdUnitId);
		adMobBanner.setAdSize(AdSize.BANNER);
		adMobBanner.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				super.onAdClosed();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
			}

			@Override
			public void onAdLeftApplication() {
				super.onAdLeftApplication();
			}

			@Override
			public void onAdOpened() {
				super.onAdOpened();
			}

			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				if(!adMobBanner.isShown()) {
					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.setMargins(0, Graphics.DpToPixMargin(5), 0, 0);
					mainContentArea.addView(adMobBanner, lp);
				}
			}
		});
		adMobBanner.loadAd(new AdRequest.Builder().build());
	}
}
