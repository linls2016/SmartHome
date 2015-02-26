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
	private boolean status;		//插座的状态
	private String socketReceive = null;
	
	private LinkWifi wifi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socket);
		wifi = LinkWifi.getInstance();
		wifi.sendMsg("SOINZ");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		initSocket();
		back();
	}
	
	/**
	 * 初始化插座
	 */
	private void initSocket() {
		socket = (ImageView) findViewById(R.id.socket);
		wifi.sendMsg("SCHKZ");
		while (true) {
			socketReceive = wifi.getReceiveMsg();
			if ("SOON".equals(socketReceive)) {
				status = true;
				break;
			}
			if ("SOFF".equals(socketReceive)) {
				status = false;
				break;
			}
		}
		if (status) {
			socket.setImageResource(R.drawable.off);
		} else {
			socket.setImageResource(R.drawable.on);
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
			wifi.sendMsg("SCHKZ");
			while (true) {
				socketReceive = wifi.getReceiveMsg();
				if ("SOON".equals(socketReceive)) {
					status = true;
					break;
				}
				if ("SOFF".equals(socketReceive)) {
					status = false;
					break;
				}
			}
			if (!status) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					socket.setImageResource(R.drawable.on_pressed);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					wifi.sendMsg("S00TZ");
					socket.setImageResource(R.drawable.on);
					new Player(SocketActivity.this, R.raw.socket_open).play();
				}
			} else {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					socket.setImageResource(R.drawable.off_pressed);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					wifi.sendMsg("S00FZ");
					socket.setImageResource(R.drawable.off);
					new Player(SocketActivity.this, R.raw.socket_close).play();
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
		wifi.sendMsg("BACKZ");
		Intent intent = new Intent(SocketActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
	}
	
}