package com.lins.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LinkWifi wifi;
	private Intent intent;
	private GridView gv;
	private int[] imgRes = new int[] { R.drawable.main_light,
			R.drawable.main_socket, R.drawable.main_environment,
			R.drawable.main_security };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		wifi = new LinkWifi(this);
		if (!wifi.isConnect()) {
			wifi.getConnect();
			Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
		}
		this.gv = (GridView) super.findViewById(R.id.gv);
		this.gv.setAdapter(new MainGridAdapter(this, this.imgRes));
		this.gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					switch (position) {
					case 0:
							intent = new Intent(MainActivity.this, LightActivity.class);
							wifi.sendMsg("LinLiangsheng");
							startActivity(intent);
						break;
					case 1:
						intent = new Intent(MainActivity.this, SocketActivity.class);
						MainActivity.this.startActivity(intent);
					break;
					case 2:
						intent = new Intent(MainActivity.this, EnvironmentActivity.class);
						startActivity(intent);
					break;
					case 3:
						intent = new Intent(MainActivity.this, SecurityActivity.class);
						startActivity(intent);
					break;
					}
			}
		});
	}

}