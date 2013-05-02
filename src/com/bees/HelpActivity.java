/**
 * =======================================================================
 * Copyright (C) 2012
 *
 * This file contains proprietary information.
 * Copying or reproduction without prior written approval is prohibited.
 * =======================================================================
 */

package com.bees;

import com.bees.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * HelpActivity.java
 * 
 * @author MURVIN BHANTOOA
 * @date14/05/2012
 */
public class HelpActivity extends Activity {

	private String[] titles, descriptions;
	private Bitmap[] images;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		initActionBar();
		initResources();
		initGalleryView();
	}

	private void initActionBar() {
		TextView txtTitle = (TextView) findViewById(R.id.textViewActionBarTitle);
		txtTitle.setText(R.string.menu_help);

		ImageButton btnHome = (ImageButton) findViewById(R.id.imageButtonHome);
		btnHome.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		LinearLayout l_about = (LinearLayout) findViewById(R.id.linearLayoutAbout);
		l_about.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				showDialog(Constants.DIALOG_ABOUT);
			}
		});
	}

	private void initResources() {
		titles = getResources().getStringArray(R.array.help_titles);
		descriptions = getResources().getStringArray(R.array.help_desc);

		images = new Bitmap[7];
		images[0] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help1);
		images[1] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help2);
		images[2] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help3);
		images[3] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help4);
		images[4] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help5);
		images[5] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help6);
		images[6] = BitmapFactory.decodeResource(getResources(),
				R.drawable.help7);
	}

	private void initGalleryView() {
		Gallery g = (Gallery) findViewById(R.id.galleryViewHelp);
		g.setAdapter(new HelpAdapter(this));
	}

	public class HelpAdapter extends BaseAdapter {
		private Context mContext;

		public HelpAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return titles.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			HelpViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.helpitem, null);

				holder = new HelpViewHolder();
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.textViewHelpTitle);
				holder.txtEventDesc = (TextView) convertView
						.findViewById(R.id.textViewHelpDescription);
				holder.imgHelp = (ImageView) convertView
						.findViewById(R.id.imageViewHelpItem);

				convertView.setTag(holder);
			} else {
				holder = (HelpViewHolder) convertView.getTag();
			}

			holder.txtTitle.setText(titles[position]);
			holder.txtEventDesc.setText(descriptions[position]);
			holder.imgHelp.setImageBitmap(images[position]);

			return convertView;
		}
	}

	static class HelpViewHolder {
		TextView txtTitle;
		ImageView imgHelp;
		TextView txtEventDesc;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case Constants.DIALOG_ABOUT: {
			AlertDialog.Builder gps_builder = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.AlertDialogCustom));
			View v = getLayoutInflater().inflate(R.layout.about, null);
			gps_builder
					.setTitle(getString(R.string.menu_about))
					.setIcon(R.drawable.ic_launcher2)
					.setView(v)
					.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			dialog = gps_builder.create();
			break;
		}
		}

		return dialog;
	}

}
