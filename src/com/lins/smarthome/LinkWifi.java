package com.lins.smarthome;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;

public class LinkWifi implements Serializable{
	
	private static final long serialVersionUID = -7620435178023928252L;
	
	private Context context;
	private boolean connect = false;
	private boolean receive = false;
	public static final String SERVERIP = "192.168.11.254";
	public static final int SERVERPORT = 8888;
	private Socket socket;
	private OutputStream outStream;
	private String sendMsg;

	public LinkWifi(Context context) {
		super();
		setContext(context);
	}
	
	public void getConnect() {
		new Thread(connectThread).start();
	}

	public void sendMsg(String msg) {
		this.sendMsg = msg;
		new Thread(sendThread).start();
	}

	public Context getContext() {
		return this.context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public boolean isReceive() {
		return this.receive;
	}

	public boolean isConnect() {
		return this.connect;
	}
	
	Runnable connectThread = new Runnable() {
		@Override
		public void run() {
			try {
				socket = new Socket(SERVERIP,SERVERPORT);
				connect = true;
				receive = true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	Runnable sendThread = new Runnable() {
		@Override
		public void run() {
			byte[] sendBuffer = null;
			try {
				sendBuffer = sendMsg.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			try {
				outStream = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outStream.write(sendBuffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	
}