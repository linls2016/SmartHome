package com.lins.smarthome;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SocketActivity extends Activity {

	private ImageView socket;
	private boolean key;					//开关控制值
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socket);
		this.socket = (ImageView) super.findViewById(R.id.socket);
		socket.setImageResource(R.drawable.socket_on);
		socket.setOnClickListener(new SocketOnff());
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
				key = false;
			} else {
				SocketOff();
				socket.setImageResource(R.drawable.socket_off);
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