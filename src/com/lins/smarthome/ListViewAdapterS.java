package com.lins.smarthome;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapterS extends BaseAdapter {

	private LinkWifi wifi;
	
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	private boolean[] hasChecked;
	
	public final class ListItemView {
		public TextView start_time_value;
		public TextView stop_time_value;
		public ImageView iv_security_switch;
	}
	
	public ListViewAdapterS(Context context, List<Map<String, Object>> listItems) {
		super();
		this.context = context;
		this.listItems = listItems;
		listContainer = LayoutInflater.from(context);
		hasChecked = new boolean[1000];
		wifi = LinkWifi.getInstance();
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	/**  
     * 判断是否选择  
     * @param checkedID 序号  
     * @return 返回是否选中状态  
     */  
    public boolean hasChecked(int position) {
        return hasChecked[position];   
    }
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ListItemView listItemView;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(R.layout.list_items_s, parent,false);
			listItemView.start_time_value = (TextView) convertView.findViewById(R.id.start_time_value);
			listItemView.stop_time_value = (TextView) convertView.findViewById(R.id.stop_time_value);
			listItemView.iv_security_switch = (ImageView) convertView.findViewById(R.id.iv_security_switch);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.start_time_value.setText((String) listItems.get(position).get("start_time_value"));
		listItemView.stop_time_value.setText((String) listItems.get(position).get("stop_time_value"));
		
		if ("star".equals((String) listItems.get(position).get("start_stop_image"))) {
			listItemView.iv_security_switch.setImageResource(R.drawable.settings_on);
			String DS1302_StartTime = (String) listItems.get(position).get("start_time_value");
			String DS1302_StopTime  = (String) listItems.get(position).get("stop_time_value");
			StringBuffer start = new StringBuffer();
			StringBuffer stop  = new StringBuffer();
			char temp;
			for (int i = 0; i < DS1302_StartTime.length(); i++) {
				if (i != 2) {
					temp = (char) (DS1302_StartTime.charAt(i) - "0".charAt(0));
					start.append(temp);
				}
			}
			for (int i = 0; i < DS1302_StopTime.length(); i++) {
				if (i != 2) {
					temp = (char) (DS1302_StopTime.charAt(i) - "0".charAt(0));
					stop.append(temp);
				}
			}
			String sendTime = "A" + start + stop + "Z";
			wifi.sendMsg(sendTime);
		}
		if ("stop".equals((String) listItems.get(position).get("start_stop_image"))) {
			listItemView.iv_security_switch.setImageResource(R.drawable.settings_off);
		}
		listItemView.iv_security_switch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hasChecked(position) == true) {
					listItemView.iv_security_switch.setImageResource(R.drawable.settings_off);
					hasChecked[position] = false;
					listItems.get(position).put("start_stop_image", "stop");
					Editor editor = context.getSharedPreferences("LinLiangsheng", Context.MODE_PRIVATE).edit();
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < getCount(); i++) {
						sb.append(getItem(i)).append("!");
					}
					if (sb.length() > 1) {
						String content = sb.substring(0, sb.length()-1);
						editor.putString(KEY_SECURITY_TIME_LIST, content);
					} else {
						editor.putString(KEY_SECURITY_TIME_LIST, null);
					}
					editor.commit();
				} else {
					listItemView.iv_security_switch.setImageResource(R.drawable.settings_on);
					hasChecked[position] = true;
					listItems.get(position).put("start_stop_image", "star");
					Editor editor = context.getSharedPreferences("LinLiangsheng", Context.MODE_PRIVATE).edit();
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < getCount(); i++) {
						sb.append(getItem(i)).append("!");
					}
					if (sb.length() > 1) {
						String content = sb.substring(0, sb.length()-1);
						editor.putString(KEY_SECURITY_TIME_LIST, content);
					} else {
						editor.putString(KEY_SECURITY_TIME_LIST, null);
					}
					editor.commit();
				}
				setSecurityTime();
			}
		});
		return convertView;
	}
	
	private static final String KEY_SECURITY_TIME_LIST = "SecurityTime";
	public void setSecurityTime() {
		StringBuffer all = new StringBuffer();
		SharedPreferences sp = context.getSharedPreferences("LinLiangsheng", Context.MODE_PRIVATE);
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
				if ("star".equals(imageTime)) {
					all.append("!").append(startTime).append("|").append(stopTime).append("?");
				}
			}
		}
	}
	
}