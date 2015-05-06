package com.lins.smarthome;

import java.util.ArrayList;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import android.app.Activity;
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

public class MainActivity extends Activity {

	private Intent intent;
	
	private TextView Light,Socket,Environment,Security,voiceInfo;
	private ImageView Voice;
	
    private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;
	
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
		
		Voice = (ImageView) findViewById(R.id.voice);
		Voice.setOnClickListener(new Voice());
		voiceInfo = (TextView) findViewById(R.id.voice_info);
        mRecognitionListener = new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> rs = results != null ? results
                        .getStringArrayList(RESULTS_RECOGNITION) : null;
                if (rs != null && rs.size() > 0) {
                	voiceInfo.setText(rs.get(0).substring(0, rs.get(0).length()-1));
                }
            }
        };
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
	
	private class Voice implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			voiceInfo.setText(null);
            mCurrentTheme = Config.DIALOG_THEME;
            if (mDialog != null) {
                mDialog.dismiss();
            }
            Bundle params = new Bundle();
            params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
            params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
            params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
            mDialog = new BaiduASRDigitalDialog(MainActivity.this, params);
            mDialog.setDialogRecognitionListener(mRecognitionListener);
            mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
            mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                    Config.getCurrentLanguage());
            Log.e("DEBUG", "Config.PLAY_START_SOUND = "+Config.PLAY_START_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
            mDialog.show();
		}
	}
	
    @Override
    protected void onDestroy() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }
	
}