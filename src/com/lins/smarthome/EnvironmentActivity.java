package com.lins.smarthome;
/**
 * @author LinLiangsheng
 * 环境监测子界面及温湿度数据接收处理
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class EnvironmentActivity extends Activity {

	private ImageView backup;			//返回按钮
	private LinkWifi wifi;						//wifi对象

	private TextView humidity;			//湿度
	private TextView temperature;		//温度

	private boolean ifStart = true;	//控制是否退出线程对温湿度的更新

	private static final int RESULT = 0;
	private Handler handler =  new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RESULT:
					String result = msg.obj.toString();
					String[] T_R = result.split(",");
					if (T_R.length == 2) {
						int tem = T_R[1].charAt(0);
						int hum = T_R[0].charAt(0);
						temperature.setText(""+tem);
						humidity.setText(""+hum);
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
		setContentView(R.layout.environment);
		back();
		humidity = (TextView) findViewById(R.id.tv_humidity);
		temperature = (TextView) findViewById(R.id.tv_temperature);
		humidity.setText("0");
		temperature.setText("0");
		wifi = LinkWifi.getInstance();
		wifi.sendMsg("ENINZ");
		reFlash();
	}
	
	/**
	 * 用于更新温湿度数据
	 */
	private void reFlash() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String str = "";
				while(ifStart) {
					str = ""+wifi.getReceiveMsg();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Message msg = handler.obtainMessage(RESULT, str);
					handler.sendMessage(msg);
				}
			}
		}).start();;
	}

	/**
	 * 返回的UI逻辑
	 */
	private void back() {
		backup = (ImageView) findViewById(R.id.environment_backup);
		backup.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					backup.setImageResource(R.drawable.menu_up_after);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					backup.setImageResource(R.drawable.menu_up);
					intentBack();
				}
				return true;
			}
		});
	}
	
	/**
	 * 返回键，按下返回主菜单
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		intentBack();
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 返回的处理函数
	 */
	private void intentBack() {
		ifStart = false;
		wifi.sendMsg("BACKZ");
		Intent intent = new Intent(EnvironmentActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
	}
	
}