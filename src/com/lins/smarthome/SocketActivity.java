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
	
	private int countForEnd = 8;						//仅进行8次的查询判断
	private boolean onTouchClick = true;	//按下仅发送一次
	
	private LinkWifi wifi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socket);
		wifi = LinkWifi.getInstance();
		wifi.sendMsg("SOINZ");
		try {
			Thread.sleep(500);
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
		while (countForEnd>0) {
			socketReceive = wifi.getReceiveMsg();
			if ("SOON".equals(socketReceive)) {
				status = true;
				break;
			}
			if ("SOFF".equals(socketReceive)) {
				status = false;
				break;
			}
			try {
				Thread.sleep(500);
				countForEnd--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		countForEnd = 8;
		if (status) {
			socket.setImageResource(R.drawable.on);
		} else {
			socket.setImageResource(R.drawable.off);
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
			if (onTouchClick) {
				wifi.sendMsg("SCHKZ");
				try {
					Thread.sleep(500);
					countForEnd--;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (countForEnd>0) {
				onTouchClick = false;
				socketReceive = wifi.getReceiveMsg();
				if ("SOON".equals(socketReceive)) {
					status = true;
					break;
				}
				if ("SOFF".equals(socketReceive)) {
					status = false;
					break;
				}
				try {
					Thread.sleep(500);
					countForEnd--;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			countForEnd = 8;
			if (!status) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					socket.setImageResource(R.drawable.on_pressed);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					onTouchClick = true;
					wifi.sendMsg("S00TZ");
					socket.setImageResource(R.drawable.on);
					new Player(SocketActivity.this, R.raw.socket_open).play();
				}
			} else {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
//					socket.setImageResource(R.drawable.off_pressed);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					onTouchClick = true;
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