package com.lins.smarthome;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MainGridAdapter extends BaseAdapter {

	private Context context;
	private int[] imgIds;
	
	public MainGridAdapter(Context context, int[] imgIds) {
		super();
		this.context = context;
		this.imgIds = imgIds;
	}

	@Override
	public int getCount() {
		return this.imgIds.length;
	}

	@Override
	public Object getItem(int position) {
		return this.imgIds[position];
	}

	@Override
	public long getItemId(int position) {
		return this.imgIds[position];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView img = new ImageView(this.context);
		img.setImageResource(this.imgIds[position]);
		img.setScaleType(ImageView.ScaleType.CENTER);
		return img;
	}

}
