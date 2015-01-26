package com.lins.smarthome;

public class Socket {

	private boolean check = true;
	private boolean status;		//插座的状态
	private String socketReceive = null;
	private LinkWifi wifi;

	/**
	 * 构造方法，传入context对象
	 * @param context
	 */
	public Socket() {
		super();
		wifi = LinkWifi.getInstance();
		wifi.setReceiveMsg(null);
	}
	
	/**
	 * 检查插座状态，true为开启，false为关闭
	 * @return status 插座的状态
	 */
	public boolean checkStatus() {
		wifi.sendMsg("SCHK");
		while (check) {
			socketReceive = wifi.getReceiveMsg();
			if ("SOON".equals(socketReceive)) {
				status = true;
				check = false;
			}
			if ("SOFF".equals(socketReceive)) {
				status = false;
				check = false;
			}
		}
		return status;
	}
	
	/**
	 * 设置插座开启
	 */
	public void setSocketOn() {
		wifi.sendMsg("S00T");
	}
	
	/**
	 * 设置插座关闭
	 */
	public void setSocketOff() {
		wifi.sendMsg("S00F");
	}
	
}