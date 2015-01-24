package com.lins.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SocketActivity extends Activity {

	private ImageView socket;
	private boolean key;					//开关控制值
	private LinkWifi wifi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socket);
		this.socket = (ImageView) super.findViewById(R.id.socket);
		socket.setImageResource(R.drawable.socket_on);
		socket.setOnClickListener(new SocketOnff());
//		Intent intent = SocketActivity.this.getIntent();
//		wifi = (LinkWifi) intent.getSerializableExtra("wifi");
	}
	/**
	 * 插座开关
	 */
	private class SocketOnff implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (key) {
				SocketOn();
				socket.setImageResource(R.drawable.socket_on);
				wifi.sendMsg("LinLiangsheng -> On");
				key = false;
			} else {
				SocketOff();
				socket.setImageResource(R.drawable.socket_off);
				wifi.sendMsg("LinLiangsheng -> Off");
				key = true;
			}
		}
		
	}
	/**
	 * 插座开启时的处理
	 */
	private void SocketOn() {
		
	}
	/**
	 * 插座关闭时的处理
	 */
	private void SocketOff() {
		
	}
}