package com.lins.smarthome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements RecognitionListener{
	static {
		System.loadLibrary("pocketsphinx_jni");
	}

	private RecognizerTask rec;
	private Thread rec_thread;
	private boolean listening;
	private ProgressDialog rec_dialog;

	private Intent intent;
	private TextView Light,Socket,Environment,Security,voiceInfo;
	private ImageView Voice;

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

		this.rec = new RecognizerTask();
		this.rec_thread = new Thread(this.rec);
		this.listening = false;
		Voice = (ImageView) findViewById(R.id.voice);
		Voice.setOnTouchListener(new Touch());
		voiceInfo = (TextView) findViewById(R.id.voice_info);
		voiceInfo.setText("按住图标说话");
		this.rec.setRecognitionListener(this);
		this.rec_thread.start();
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
			case R.id.voice:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					MainActivity.this.listening = true;
					MainActivity.this.rec.start();
					break;
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (MainActivity.this.listening) {
						Log.d(getClass().getName(), "Showing Dialog");
						MainActivity.this.rec_dialog = ProgressDialog.show(MainActivity.this, "", "Recognizing speech...", true);
						MainActivity.this.rec_dialog.setCancelable(false);
						MainActivity.this.listening = false;
					}
					MainActivity.this.rec.stop();
					break;
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

	@Override
	public void onPartialResults(Bundle b) {
//		final MainActivity that = this;
//		final String hyp = b.getString("hyp");
//		that.voiceInfo.post(new Runnable() {
//			public void run() {
//				that.voiceInfo.setText(hyp);
//			}
//		});
	}

	@Override
	public void onResults(Bundle b) {
		final String hyp = b.getString("hyp");
		final MainActivity that = this;
		this.voiceInfo.post(new Runnable() {
			public void run() {
				that.voiceInfo.setText(hyp);
				Log.d(getClass().getName(), "Hiding Dialog");
				that.rec_dialog.dismiss();
			}
		});
	}

	@Override
	public void onError(int err) {
		final MainActivity that = this;
		that.voiceInfo.post(new Runnable() {
			public void run() {
				that.rec_dialog.dismiss();
			}
		});
	}

}