package com.acproma.kflightchecklists.screens;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acproma.kflightchecklists.R;
import com.acproma.kflightchecklists.models.ActionItem;

/**
 * Created by Congee on 10/8/14.
 */
public class ActionItemMessageRow extends LinearLayout {
	public ActionItemMessageRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void populate(ActionItem actionItem){
		TextView string1 = (TextView)findViewById(R.id.string_1);
		string1.setText(actionItem.string1);
	}
}
