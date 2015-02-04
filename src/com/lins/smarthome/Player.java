package com.lins.smarthome;
/**
 * @author LinLiangsheng
 * Player.java用于播放mp3声音文件
 */
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

	/**
	 * 播放声音，播放结束释放资源
	 */
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