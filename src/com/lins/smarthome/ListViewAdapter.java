package com.lins.smarthome;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	private boolean[] hasChecked;

	public final class ListItemView {
		public TextView start_time_value;
		public TextView stop_time_value;
		public ImageView iv_light_switch;
	}
	
	public ListViewAdapter(Context context, List<Map<String, Object>> listItems) {
		super();
		this.context = context;
		this.listItems = listItems;
		listContainer = LayoutInflater.from(context);
		hasChecked = new boolean[getCount()];
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
	 * 记录点选了哪个
	 * @param checkedID
	 */
	private void checkedOnClick(int checkedID) {
		hasChecked[checkedID] = !hasChecked[checkedID];
	}
	
	/**  
     * 判断是否选择  
     * @param checkedID 序号  
     * @return 返回是否选中状态  
     */  
    public boolean hasChecked(int checkedID) {   
        return hasChecked[checkedID];   
    }
	
    /**  
     * 显示详情  
     * @param clickID  
     */  
    private void showDetailInfo(int clickID) {   
        new AlertDialog.Builder(context)   
        .setTitle("物品详情：" + listItems.get(clickID).get("info"))   
        .setMessage(listItems.get(clickID).get("detail").toString())                 
        .setPositiveButton("确定", null)   
        .show();   
    }
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ListItemView listItemView;
		if (convertView == null) {
			listItemView = new ListItemView();
			convertView = listContainer.inflate(R.layout.list_items, null);
			listItemView.start_time_value = (TextView) convertView.findViewById(R.id.start_time_value);
			listItemView.stop_time_value = (TextView) convertView.findViewById(R.id.stop_time_value);
			listItemView.iv_light_switch = (ImageView) convertView.findViewById(R.id.iv_light_switch);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
		listItemView.start_time_value.setText((String) listItems.get(position).get("start_time_value"));
		listItemView.stop_time_value.setText((String) listItems.get(position).get("stop_time_value"));
		listItemView.iv_light_switch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				listItemView.iv_light_switch.setImageResource(R.drawable.settings_on);
//				listItemView.iv_light_switch.setImageResource(R.drawable.settings_on);
//				checkedOnClick(position);
//				System.out.println(hasChecked(position));
				String start = (String) listItemView.start_time_value.getText();
				String stop = (String) listItemView.stop_time_value.getText();
				System.out.println("START:"+start + ",STOP:" + stop);
			}
		});
		return convertView;
	}
	
}