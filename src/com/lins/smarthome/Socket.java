package com.lins.smarthome;

import android.content.Context;

public class Socket {

	private Context context;		//承接上下文
	private boolean status;		//插座的状态

	/**
	 * 构造方法，传入context对象
	 * @param context
	 */
	public Socket(Context context) {
		super();
		this.context = context;
	}
	
	/**
	 * 检查插座状态，true为开启，false为关闭
	 * @return status 插座的状态
	 */
	public boolean checkStatus() {
		return this.status;
	}
	
	/**
	 * 设置插座开启
	 * @return status is true
	 */
	public boolean setSocketOn() {
		this.status = true;
		return this.status;
	}
	
	/**
	 * 设置插座关闭
	 * @return status is false
	 */
	public boolean setSocketOff() {
		this.status = false;
		return this.status;
	}
	
}