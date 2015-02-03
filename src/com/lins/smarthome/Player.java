package com.lins.smarthome;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Player {
	
	private Context context;
	private int res;
	private MediaPlayer media;
	
	public Player(Context context, int res) {
		super();
		this.context = context;
		this.res = res;
	}

	public void play() {
		media = MediaPlayer.create(context, res);
		media.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				media.release();
			}
		});
		try {
			media.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
		media.start();
	}
	
}