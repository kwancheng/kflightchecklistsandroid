package com.acproma.kflightchecklists.adnetworks;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Congee on 10/17/14.
 */
abstract public class AdNetwork {
	private ArrayList<AdNetworkCallback> adNetworkCallbacks = new ArrayList<AdNetworkCallback>();

	public void addAdNetworkCallback(AdNetworkCallback adNetworkCallback) {
		if(!adNetworkCallbacks.contains(adNetworkCallback))
			adNetworkCallbacks.add(adNetworkCallback);
	}

	public void removeAdNetworkCallback(AdNetworkCallback adNetworkCallback) {
		adNetworkCallbacks.remove(adNetworkCallback);
	}

	synchronized protected void fireAdLoaded(){
		for(AdNetworkCallback anc : adNetworkCallbacks)
			anc.adLoaded(this);
	}

	synchronized protected void fireAdCompleted(){
		for(AdNetworkCallback anc : adNetworkCallbacks)
			anc.adCompleted(this);
	}

	protected Activity activity = null;

	public AdNetwork(Activity activity){
		this.activity = activity;
	}

	abstract public void fetch();
	abstract public void show();

	public void onResume(Activity activity){
		this.activity = activity;
	}

	public void onPause(Activity activity){
		if(this.activity == activity)
			activity = null;
	}

	public void onDestroy(Activity activity){

	}
}