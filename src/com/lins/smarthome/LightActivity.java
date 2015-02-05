package com.lins.smarthome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class LightActivity extends Activity {

	private ImageView backup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.light);
		
		initView();
		
	}
	
	private void initView() {
		backup = (ImageView) findViewById(R.id.light_backup);
		/*
		backup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("OnClick >>>>>>>>>>>>>>>>>>>>>>>>>");

			}
		});
		*/
		backup.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					backup.setImageResource(R.drawable.menu_up_after);
					System.out.println("ACTION_DOWN >>>>>>>>>>>>>>>>>>>>>>>>>");
					break;
				case MotionEvent.ACTION_UP:
					backup.setImageResource(R.drawable.menu_up);
					Intent intent = new Intent(LightActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
					System.out.println("ACTION_UP >>>>>>>>>>>>>>>>>>>>>>>>>");
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Intent intent = new Intent(LightActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
		return super.onKeyDown(keyCode, event);
	}
	
}