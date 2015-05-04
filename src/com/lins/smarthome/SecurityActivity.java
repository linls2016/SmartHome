package com.lins.smarthome;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class SecurityActivity extends Activity {

	private ImageView backup;
	
	private RelativeLayout add_security;
	private ImageView iv_add_security;
	private TextView tv_add_security;
	
	private TextView tv_add_security_start;
	private TextView tv_add_security_stop;
	
	private ListView security_time_lists;
	private List<Map<String, Object>> listItems;
	private ListViewAdapterS listViewAdapter;
	
	private boolean SecurityTimeKey = true;
	private int updateID;
	
	private LinkWifi wifi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security);
		wifi = LinkWifi.getInstance();
		wifi.sendMsg("SEINZ");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		initView();															//初始化UI控件
		initListView();
		back();
		readSecurityTimeList();
		initDSTime();
	}
	
	/**
	 * 初始化DS1302的时间
	 */
	private void initDSTime() {
		Date date = new Date();
		SimpleDateFormat SDF_YY = new SimpleDateFormat("yy",Locale.US);
		SimpleDateFormat SDF_MM = new SimpleDateFormat("MM",Locale.US);
		SimpleDateFormat SDF_DD = new SimpleDateFormat("dd",Locale.US);
		SimpleDateFormat SDF_HH = new SimpleDateFormat("hh",Locale.US);
		SimpleDateFormat SDF_MI = new SimpleDateFormat("mm",Locale.US);
		SimpleDateFormat SDF_SS = new SimpleDateFormat("ss",Locale.US);
		String syy  = SDF_YY.format(date);
		int yy = Integer.parseInt(syy, 16);
		String smm  = SDF_MM.format(date);
		int mm = Integer.parseInt(smm, 16);
		String sdd  = SDF_DD.format(date);
		int dd = Integer.parseInt(sdd, 16);
		String shh  = SDF_HH.format(date);
		int hh = Integer.parseInt(shh, 16);
		String smi  = SDF_MI.format(date);
		int mi = Integer.parseInt(smi, 16);
		String sss  = SDF_SS.format(date);
		int ss = Integer.parseInt(sss, 16);
//		String sendTime = "K" + yy + mm + dd + hh + mi + ss + "Z";
		String sendTime = "K" + ss + "Z";
		System.out.println("发送数值："+sendTime);
		wifi.sendMsg(sendTime);
	}

	/**
	 * 初始化各个UI并绑定监听
	 */
	private void initView() {
		iv_add_security = (ImageView) findViewById(R.id.iv_add_security);			//添加按钮，为一幅图片
		tv_add_security = (TextView) findViewById(R.id.tv_add_security);				//文本：添加开关时段
		tv_add_security.setTextColor(Color.GRAY);													//设置文字为灰色
		add_security = (RelativeLayout) findViewById(R.id.rl_add_security);			//整一个添加按钮跟文本构成一个组件使用，绑定了OnTouch事件
		add_security.setOnTouchListener(new OnTouchListener() {						//添加开关时段控件的OnTouch事件
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {				//按下s的处理
					iv_add_security.setImageResource(R.drawable.add_pressed);
					tv_add_security.setTextColor(Color.BLACK);
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {						//提起的处理
					iv_add_security.setImageResource(R.drawable.add);
					tv_add_security.setTextColor(Color.GRAY);
					SecurityTimeKey = true;
					selectDialog();																					//提起后弹出一个选择时段的自定义对话框
				}
				return true;
			}
		});
	}
	
	private void initListView() {
		security_time_lists = (ListView) findViewById(R.id.security_time_lists);
		listItems = new ArrayList<Map<String,Object>>();
		listViewAdapter = new ListViewAdapterS(SecurityActivity.this,listItems);
		security_time_lists.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new AlertDialog.Builder(SecurityActivity.this).setTitle("操作选项").setItems(new CharSequence[]{"删除"}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
								deleteSecurityTime(position);
							break;
						default:
							break;
						}
					}
				}).setNegativeButton("取消",null).show();
				return true;
			}
		});
		security_time_lists.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String start = (String) listItems.get(position).get("start_time_value");
				String stop = (String) listItems.get(position).get("stop_time_value");
				Message msg_start = new Message();
				msg_start.what = ADD_SECURITY_START;
				msg_start.obj = start;
				handler.sendMessage(msg_start);
				Message msg_stop = new Message();
				msg_stop.what = ADD_SECURITY_STOP;
				msg_stop.obj = stop;
				handler.sendMessage(msg_stop);
				SecurityTimeKey = false;
				updateID = position;
				selectDialog();
			}
		});
	}
	
	private void addSecurityTime(String startTime,String stopTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start_time_value", startTime);
		map.put("stop_time_value", stopTime);
		map.put("start_stop_image", "stop");
		listItems.add(map);
		security_time_lists.setAdapter(listViewAdapter);
		saveSecurityTimeList();
	}
	
	private void deleteSecurityTime(int position) {
		listItems.remove(listViewAdapter.getItem(position));
		listViewAdapter.notifyDataSetChanged();
		saveSecurityTimeList();
		listViewAdapter.setSecurityTime();
	}
	
	private void updateSecurityTime(int position,String startTime,String stopTime) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start_time_value", startTime);
		map.put("stop_time_value", stopTime);
		map.put("start_stop_image", "stop");
		listItems.set(position, map);
		security_time_lists.setAdapter(listViewAdapter);
		saveSecurityTimeList();
		listViewAdapter.setSecurityTime();
	}
	
	private static final String KEY_SECURITY_TIME_LIST = "SecurityTime";
	
	private void readSecurityTimeList() {
		SharedPreferences sp = getSharedPreferences("LinLiangsheng", Context.MODE_PRIVATE);
		String content = sp.getString(KEY_SECURITY_TIME_LIST, null);
		if (content != null) {
			String[] timeStrings = content.split("!");
			for (String string : timeStrings) {
				int startTimeStart = string.indexOf("start_time_value")+17;
				int startTimeStop = string.indexOf("start_time_value")+22;
				String startTime = string.substring(startTimeStart, startTimeStop);
				int stopTimeStart = string.indexOf("stop_time_value")+16;
				int stopTimeStop = string.indexOf("stop_time_value")+21;
				String stopTime = string.substring(stopTimeStart, stopTimeStop);
				int imageStart = string.indexOf("start_stop_image")+17;
				int imageStop = string.indexOf("start_stop_image")+21;
				String imageTime = string.substring(imageStart, imageStop);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("start_time_value", startTime);
				map.put("stop_time_value", stopTime);
				map.put("start_stop_image", imageTime);
				listItems.add(map);
				security_time_lists.setAdapter(listViewAdapter);
				listViewAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private void saveSecurityTimeList() {
		Editor editor = getSharedPreferences("LinLiangsheng", Context.MODE_PRIVATE).edit();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < listViewAdapter.getCount(); i++) {
			sb.append(listViewAdapter.getItem(i)).append("!");
		}
		if (sb.length() > 1) {
			String content = sb.substring(0, sb.length()-1);
			editor.putString(KEY_SECURITY_TIME_LIST, content);
			System.out.println(content);
		} else {
			editor.putString(KEY_SECURITY_TIME_LIST, null);
		}
		editor.commit();
	}
	
	/**
	 * 选择时段对话框，自定义的
	 * 开启/关闭时间可以OnTouch设定
	 * 具备确定和取消两个按钮
	 */
	private void selectDialog() {
		Dialog dialog = new Dialog(SecurityActivity.this);										//实例化对话框
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);			//对话框样式
        dialog.setContentView(R.layout.security_add_choice);								//对话框布局，自定义
		initSelectDialogView(dialog);																		//初始化对话框的UI控件
		dialog.show();																								//显示对话框
	}
	
	/**
	 * 初始化对话框控件
	 * @param dialog
	 */
	private void initSelectDialogView(final Dialog dialog) {
		RelativeLayout rl_add_security_start = (RelativeLayout) dialog.findViewById(R.id.rl_add_security_start);		//开启时间组件
		RelativeLayout rl_add_security_stop = (RelativeLayout) dialog.findViewById(R.id.rl_add_security_stop);		//关闭时间组件
		tv_add_security_start = (TextView) dialog.findViewById(R.id.tv_add_security_start);											//开启时间
		tv_add_security_stop = (TextView) dialog.findViewById(R.id.tv_add_security_stop);											//关闭时间
		TextView tv_add_security_ensure = (TextView) dialog.findViewById(R.id.tv_add_security_ensure);				//确定
		TextView tv_add_security_cancle = (TextView) dialog.findViewById(R.id.tv_add_security_cancle);					//取消
		rl_add_security_start.setOnTouchListener(new OnTouchListener() {																	//开启时间组件绑定OnTouch
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {																		//开启时间的按下事件处理
					
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {																				//开启时间的提起事件处理
					setTime(tv_add_security_start);																													//设置更新开启时间
				}
				return true;
			}
		});
		rl_add_security_stop.setOnTouchListener(new OnTouchListener() {																	//关闭时间组件绑定OnTouch
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {																		//关闭时间的按下事件处理
					
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {																				//关闭时间的提起事件处理
					setTime(tv_add_security_stop);																														//设置更新关闭时间
				}
				return true;
			}
		});
		tv_add_security_ensure.setOnTouchListener(new OnTouchListener() {															//确定时间处理
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {																		//确定被按下
					//TODO
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {																				//确定被提起
					//TODO
					String start = (String) tv_add_security_start.getText();
					String stop = (String) tv_add_security_stop.getText();
					if (SecurityTimeKey) {
						addSecurityTime(start,stop);
					} else {
						updateSecurityTime(updateID,start,stop);
					}
					dialog.dismiss();																																		//隐藏对话框
				}
				return true;
			}
		});
		tv_add_security_cancle.setOnTouchListener(new OnTouchListener() {																//取消事件处理
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				if (event.getAction() == MotionEvent.ACTION_DOWN) {																		//取消被按下
					//TODO
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {																				//取消被提起
					dialog.dismiss();																																		//隐藏对话框
				}
				return true;
			}
		});
	}

	/**
	 * 主线程Handler，用于接收消息来更新UI控件
	 */
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case ADD_SECURITY_START:																	//更新开启时间
				tv_add_security_start.setText(msg.obj.toString());
				break;
			case ADD_SECURITY_STOP:																	//更新关闭时间
				tv_add_security_stop.setText(msg.obj.toString());
				break;
			default:
				break;
			}
			return false;
		}
	});
	
	private static final int ADD_SECURITY_START = 1;									//开启时间消息
	private static final int ADD_SECURITY_STOP = 2;									//关闭时间消息
	
	/**
	 * 弹出设置时间对话框来设置时间，并发送消息更新时间显示的控件
	 * @param tv
	 */
	private void setTime(TextView tv) {
		final Message msg = new Message();													//实例化消息
		switch (tv.getId()) {																					//判断发送哪个消息
		case R.id.tv_add_security_start:
			msg.what = ADD_SECURITY_START;
			break;
		case R.id.tv_add_security_stop:
			msg.what = ADD_SECURITY_STOP;
			break;
		default:
			break;
		}
		/*用TimePickerDialog来设定时间*/
		Calendar c = Calendar.getInstance();
		new TimePickerDialog(SecurityActivity.this, new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					String time = String.format(Locale.getDefault(),"%02d:%02d", hourOfDay,minute);
					msg.obj = time;
					handler.sendMessage(msg);
			}
		}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
	}
	
	/**
	 * 返回的UI逻辑
	 */
	private void back() {
		backup = (ImageView) findViewById(R.id.security_backup);
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
		wifi.sendMsg("BACKZ");
		Intent intent = new Intent(SecurityActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.menu_in, R.anim.menu_out);		//动画
	}
	
}