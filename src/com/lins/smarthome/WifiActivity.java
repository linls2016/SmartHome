package com.lins.smarthome;
/**
 * @author LinLiangsheng
 * WifiActivity.java用于输入Wifi的IP和Port
 * 具有判断wifi是否开启，是否正确连接
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WifiActivity extends Activity {
	
	private EditText IP;
	private EditText Port;
	private Button linkDeviceButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi);
		initView();
	}
	
	/**
	 * 初始化UI控件
	 * 两个编辑框(IP和Port)，一个按钮并绑定监听
	 */
	private void initView() {
		IP = (EditText) findViewById(R.id.et_ip);
		Port = (EditText) findViewById(R.id.et_port);
		linkDeviceButton = (Button) findViewById(R.id.btn_linkdevice);
		linkDeviceButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					linkDeviceButton.setBackgroundResource(R.drawable.link_press);
					linkDeviceButton.setTextColor(Color.BLACK);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					linkDeviceButton.setBackgroundResource(R.drawable.link);
					Link();
				}
				return true;
			}
		});
	}
	
	/**
	 * 连接设备并给出提示信息
	 */
	private void Link() {
		if (isWifiAction()) {
			String serverIP = IP.getText().toString()+"";
			String serverPort = Port.getText().toString()+"";
			if (serverIP.equals("") || serverPort.equals("")) {
				if (serverIP.equals("")) {
					Toast.makeText(WifiActivity.this, "IP不能为空", Toast.LENGTH_SHORT).show();
				}
				if (serverPort.equals("")) {
					Toast.makeText(WifiActivity.this, "Port不能为空", Toast.LENGTH_SHORT).show();
				}
			} else {
				LinkWifi wifi = LinkWifi.getInstance();
				wifi.setServerIP(serverIP);
				wifi.setServerPort(Integer.parseInt(serverPort));
				wifi.getConnect();
				Intent intent = new Intent(WifiActivity.this, MainActivity.class);		//创建界面跳转意图，跳转到WifiActivity
				startActivity(intent);																					//开始界面跳转
				finish();																										//销毁当前Activity
			}
		} else {
			Toast.makeText(WifiActivity.this, "Wifi未开启，请先开启Wifi", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 判断Wifi是否开启
	 * @return true表示开启，false表示未开启
	 */
	private boolean isWifiAction() {
		ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);		//获取系统服务
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();														//获取状态
		if(wifi == State.CONNECTED||wifi==State.CONNECTING) {																								//判断Wifi是否已连接
			return true;
		} else {
			return false;
		}
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