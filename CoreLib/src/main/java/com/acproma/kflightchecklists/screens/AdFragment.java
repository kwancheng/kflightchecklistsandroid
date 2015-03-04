package com.acproma.kflightchecklists.screens;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acproma.kflightchecklists.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdFragment extends Fragment {
	private AdView adView = null;
	private AdListener adListener = new AdListener() {
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
			adView.setVisibility(View.VISIBLE);
		}
	};

    public AdFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View v = inflater.inflate(R.layout.fragment_ad, container, false);
	    adView = (AdView)v.findViewById(R.id.ad_view);
	    adView.setAdListener(adListener);
	    adView.setVisibility(View.GONE);
        return v;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adView.loadAd(new AdRequest.Builder().build());
	}
}
