package com.acproma.kflightchecklists.screens;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acproma.kflightchecklists.R;
import com.acproma.kflightchecklists.models.ActionItem;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by Congee on 10/8/14.
 */
public class ActionItemRow extends LinearLayout {
	public ActionItemRow(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void populate(ActionItem actionItem, OnClickListener tutorialImageOnClickListener) {
		ImageView tutorialImage = (ImageView)findViewById(R.id.tutorial_image);
		ImageView chkMarkImg = (ImageView)findViewById(R.id.check_mark);
		TextView string1 = (TextView)findViewById(R.id.string_1);
		TextView string2 = (TextView)findViewById(R.id.string_2);

		if(actionItem.image != null && !TextUtils.isEmpty(actionItem.image)) {
			Uri fileUrl = Uri.fromFile(new File(actionItem.image));

			Picasso.with(getContext()).load(new File(actionItem.image)).resize(100,100).centerCrop().into(tutorialImage);

			tutorialImage.setVisibility(View.VISIBLE);
			tutorialImage.setOnClickListener(tutorialImageOnClickListener);
		} else {
			tutorialImage.setVisibility(View.GONE);
		}

		chkMarkImg.setVisibility((actionItem.completed)?View.VISIBLE:View.GONE);

		string1.setText(actionItem.string1);
		string2.setText(actionItem.string2);
	}
}
