package com.acproma.kflightchecklists.utils;

import com.acproma.kflightchecklists.AppController;

/**
 * Created by Congee on 10/19/14.
 */
public class Graphics {
	static public int DpToPixMargin(int dp) {
		return Math.round(dp * AppController.getInstance().getResources().getDisplayMetrics().density);
	}
}
