package com.lins.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class MainActivity extends Activity {

	private Intent intent;
	private GridView gv;
	private int[] imgRes = new int[] { R.drawable.main_light,
			R.drawable.main_socket, R.drawable.main_environment,
			R.drawable.main_security };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.gv = (GridView) super.findViewById(R.id.gv);
		this.gv.setAdapter(new MainGridAdapter(this, this.imgRes));
		this.gv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					switch (position) {
					case 0:
							intent = new Intent(MainActivity.this, LightActivity.class);
							startActivity(intent);
							finish();
						break;
					case 1:
							intent = new Intent(MainActivity.this, SocketActivity.class);
							startActivity(intent);
							finish();
						break;
					case 2:
							intent = new Intent(MainActivity.this, EnvironmentActivity.class);
							startActivity(intent);
							finish();
						break;
					case 3:
							intent = new Intent(MainActivity.this, SecurityActivity.class);
							startActivity(intent);
							finish();
						break;
					}
			}
		});
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