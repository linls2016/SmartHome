package com.lins.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class SocketActivity extends Activity {

	private ImageView backup;
	
	private ImageView socket;
	private boolean key;					//开关控制值
	private Socket sk;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socket);
		initSocket();
		back();
	}
	
	/**
	 * 初始化插座
	 */
	private void initSocket() {
		socket = (ImageView) findViewById(R.id.socket);
		sk = new Socket();
		if (sk.checkStatus()) {
			socket.setImageResource(R.drawable.on);
			key = true;
		} else {
			socket.setImageResource(R.drawable.off);
			key = false;
		}
		socket.setOnTouchListener(new SocketOnOff());
	}

	/**
	 * 插座开关
	 */
	private class SocketOnOff implements OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.performClick();
			if (!key) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					socket.setImageResource(R.drawable.on_pressed);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					sk.setSocketOn();
					socket.setImageResource(R.drawable.on);
					new Player(SocketActivity.this, R.raw.socket_open).play();
					key = true;
				}
			} else {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					socket.setImageResource(R.drawable.off_pressed);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					sk.setSocketOff();
					socket.setImageResource(R.drawable.off);
					new Player(SocketActivity.this, R.raw.socket_close).play();
					key = false;
				}
			}
			return true;
		}
	}
	
	/**
	 * 返回的UI逻辑
	 */
	private void back() {
		backup = (ImageView) findViewById(R.id.socket_backup);
		backup.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					backup.setImageResource(R.drawable.menu_up_after);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					backup.setImageResource(R.drawable.menu_up);
					intentBack();
				}
				return true;
			}
		});
	}
	
	/**
	 * 返回键，按下返回主菜单
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		intentBack();
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 返回的处理函数
	 */
	private void intentBack() {
		Intent intent = new Intent(SocketActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
	}
	
}