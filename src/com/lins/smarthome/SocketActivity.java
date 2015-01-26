package com.lins.smarthome;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SocketActivity extends Activity {

	private ImageView socket;
	private boolean key;					//开关控制值
	private Socket sk;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.socket);
		initSocket();
	}
	
	/**
	 * 初始化插座
	 */
	private void initSocket() {
		socket = (ImageView) findViewById(R.id.socket);
		sk = new Socket();
		if (sk.checkStatus()) {
			socket.setImageResource(R.drawable.socket_on);
			key = true;
		} else {
			socket.setImageResource(R.drawable.socket_off);
			key = false;
		}
		socket.setOnClickListener(new SocketOnOff());
	}

	/**
	 * 插座开关
	 */
	private class SocketOnOff implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (!key) {
				sk.setSocketOn();
				socket.setImageResource(R.drawable.socket_on);
				key = true;
			} else {
				sk.setSocketOff();
				socket.setImageResource(R.drawable.socket_off);
				key = false;
			}
		}
	}
}