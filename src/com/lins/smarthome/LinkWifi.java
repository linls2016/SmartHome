package com.lins.smarthome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LinkWifi {

	static LinkWifi wifi = new LinkWifi();
	
	public static final String SERVERIP = "192.168.11.254";
	public static final int SERVERPORT = 8888;
	private Socket socket;
	private boolean isConnect = false;
	private boolean isReceive = false;
	private OutputStream outStream;
	private InputStream inStream;
	private String sendMsg = null;
	private String receiveMsg = null;
	private byte[] sendBuffer = null;

	public static LinkWifi getInstance() {
		return wifi;
	}
	
	public void getConnect() {
		if (!isConnect) {
			new Thread(connectThread).start();
		}
	}

	public void sendMsg(String msg) {
		this.sendMsg = msg;
		new Thread(sendThread).start();
	}
	
	public String getReceiveMsg() {
		return receiveMsg;
	}

	public void setReceiveMsg(String receiveMsg) {
		this.receiveMsg = receiveMsg;
	}



	Runnable connectThread = new Runnable() {
		@Override
		public void run() {
			try {
				socket = new Socket(SERVERIP,SERVERPORT);
				isConnect = true;
				new Thread(receiveThread).start();
				isReceive = true;
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
			try {
				sendBuffer = sendMsg.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
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
	
	Runnable receiveThread = new Runnable() {
		@Override
		public void run() {
			try {
				inStream = socket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (isReceive) {
				/*
				byte[] receiveBuffer = new byte[2];
				try {
					inStream.read(receiveBuffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					receiveMsg = new String(receiveBuffer,"UTF-8").trim();
					System.out.println(receiveMsg);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				*/
				try {
					BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					receiveMsg = buffer.readLine();
					System.out.println(receiveMsg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
}