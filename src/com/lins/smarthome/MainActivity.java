package com.lins.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Intent intent;
	
	private TextView Light,Socket,Environment,Security;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();
	}
	
	private void initView() {
		Light = (TextView) findViewById(R.id.main_light);
		Socket = (TextView) findViewById(R.id.main_socket);
		Environment = (TextView) findViewById(R.id.main_environment);
		Security = (TextView) findViewById(R.id.main_security);
		Light.setTextColor(Color.GRAY);
		Socket.setTextColor(Color.GRAY);
		Environment.setTextColor(Color.GRAY);
		Security.setTextColor(Color.GRAY);
		Light.setOnTouchListener(new Touch());
		Socket.setOnTouchListener(new Touch());
		Environment.setOnTouchListener(new Touch());
		Security.setOnTouchListener(new Touch());
	}
	
	private class Touch implements View.OnTouchListener {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			v.performClick();
			switch (v.getId()) {
			case R.id.main_light:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Light.setBackgroundResource(R.drawable.main_bg_pressed);
					Light.setTextColor(Color.WHITE);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Light.setBackgroundResource(R.drawable.main_bg);
					Light.setTextColor(Color.GRAY);
					onLight();
				}
				break;
			case R.id.main_socket:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Socket.setBackgroundResource(R.drawable.main_bg_pressed);
					Socket.setTextColor(Color.WHITE);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Socket.setBackgroundResource(R.drawable.main_bg);
					Socket.setTextColor(Color.GRAY);
					onSocket();
				}
				break;
			case R.id.main_environment:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Environment.setBackgroundResource(R.drawable.main_bg_pressed);
					Environment.setTextColor(Color.WHITE);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Environment.setBackgroundResource(R.drawable.main_bg);
					Environment.setTextColor(Color.GRAY);
					onEnvironment();
				}
				break;
			case R.id.main_security:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Security.setBackgroundResource(R.drawable.main_bg_pressed);
					Security.setTextColor(Color.WHITE);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Security.setBackgroundResource(R.drawable.main_bg);
					Security.setTextColor(Color.GRAY);
					onSecurity();
				}
				break;
			default:
				break;
			}
			return true;
		}
	}
	
	private void onLight() {
		intent = new Intent(MainActivity.this, LightActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
	}
	
	private void onSocket() {
		intent = new Intent(MainActivity.this, SocketActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
	}
	
	private void onEnvironment() {
		intent = new Intent(MainActivity.this, EnvironmentActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
	}
	
	private void onSecurity() {
		intent = new Intent(MainActivity.this, SecurityActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
	}
	
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}