package com.lins.smarthome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	
	private LinkWifi wifi;

	private RecognizerTask rec;
	private Thread rec_thread;
	private boolean listening;
	private ProgressDialog rec_dialog;

	private Intent intent;
	private TextView Light,Socket,Environment,Security,voiceInfo;
	private ImageView Voice;
	
	private Handler mainHandler =  new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 8:
				String picNumString = msg.obj.toString();
				int picNum = Integer.parseInt(picNumString);
					switch (picNum) {
					case 1:
						Voice.setImageResource(R.drawable.voice_1);
						break;
					case 2:
						Voice.setImageResource(R.drawable.voice_2);
						break;
					case 3:
						Voice.setImageResource(R.drawable.voice_3);
						break;
					case 4:
						Voice.setImageResource(R.drawable.voice_4);
						break;
					case 5:
						Voice.setImageResource(R.drawable.voice_5);
						break;
					case 6:
						Voice.setImageResource(R.drawable.voice_6);
						break;
					case 7:
						Voice.setImageResource(R.drawable.voice_7);
						break;
					case 8:
						Voice.setImageResource(R.drawable.voice_8);
						break;
					case 9:
						Voice.setImageResource(R.drawable.voice_9);
						break;
					case 10:
						Voice.setImageResource(R.drawable.voice_10);
						break;
					case 11:
						Voice.setImageResource(R.drawable.voice_11);
						break;
					case 12:
						Voice.setImageResource(R.drawable.voice_12);
						break;
					case 13:
						Voice.setImageResource(R.drawable.voice_13);
						break;
					case 14:
						Voice.setImageResource(R.drawable.voice_14);
						break;
					default:
						break;
					}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		wifi = LinkWifi.getInstance();
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

		this.rec = new RecognizerTask(mainHandler);
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

	/**
	 * 语音识别结果集处理
	 */
	@Override
	public void onResults(Bundle b) {
		final String hyp = b.getString("hyp");
		final MainActivity that = this;
		this.voiceInfo.post(new Runnable() {
			public void run() {
				that.Voice.setImageResource(R.drawable.voice);
				that.voiceInfo.setText(hyp);
				Log.d(getClass().getName(), "Hiding Dialog");
				that.rec_dialog.dismiss();
				String result = (String) voiceInfo.getText();
				if ("开灯".equals(result)) {
					wifi.sendMsg("LIINZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("L00TZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("BACKZ");
				}
				if ("关灯".equals(result)) {
					wifi.sendMsg("LIINZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("L00FZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("BACKZ");
				}
				if ("开插座".equals(result)) {
					wifi.sendMsg("SOINZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("S00TZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("BACKZ");
				}
				if ("关插座".equals(result)) {
					wifi.sendMsg("SOINZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("S00FZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("BACKZ");
				}
				if ("温湿度".equals(result)) {
					wifi.sendMsg("ENINZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					String str  = wifi.getReceiveMsg();
					String[] T_R = str.split(",");
					if (T_R.length == 2) {
						int tem = T_R[1].charAt(0);
						int hum = T_R[0].charAt(0);
						that.voiceInfo.setText("温度:"+tem+"，湿度:"+hum);
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("BACKZ");
				}
				if ("关警报".equals(result)) {
					wifi.sendMsg("SEINZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("SEOFZ");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wifi.sendMsg("BACKZ");
				}
			}
		});
	}

	/**
	 * 语音识别错误处理
	 * 处理方式：关闭语音识别对话框
	 */
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