package com.lins.smarthome;
/**
 * @author LinLiangsheng
 * StartActivity.java主要目的是显示开机画面(一幅图片welcome.png)
 * 图片界面在start.xml中进行配置
 * 延时时间(DELAYTIME=3000):3秒
 * 3秒后跳转到WifiActivity.class
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class StartActivity extends Activity {
	
	private static final int DELAYTIME = 3000;		//延时时间3秒

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);							//UI显示
		
		/**
		 * new一个Handler匿名对象
		 *通过postDelayed(Runnable r, long delayMillis)方法界面跳转和实现延时
		 */
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
					Intent intent = new Intent(StartActivity.this, WifiActivity.class);		//创建界面跳转意图，跳转到WifiActivity
					startActivity(intent);																					//开始界面跳转
					finish();																										//销毁当前Activity
					overridePendingTransition(R.anim.start_in, R.anim.start_out);		//动画
			}
		}, DELAYTIME);
		
	}
	
}