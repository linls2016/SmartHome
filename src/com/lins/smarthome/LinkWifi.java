package com.lins.smarthome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LinkWifi {

	static LinkWifi wifi = new LinkWifi();
	
	private String ServerIP;
	private int ServerPort;
	private Socket socket;
	private boolean isConnect = false;
	private boolean isReceive = false;
	private OutputStream outStream;

	private String sendMsg = null;
	private String receiveMsg = null;
	private byte[] sendBuffer = null;

	public static LinkWifi getInstance() {
		return wifi;
	}
	
	public String getServerIP() {
		return ServerIP;
	}

	public void setServerIP(String serverIP) {
		ServerIP = serverIP;
	}

	public int getServerPort() {
		return ServerPort;
	}

	public void setServerPort(int serverPort) {
		ServerPort = serverPort;
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
				socket = new Socket(getServerIP(),getServerPort());
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
			while (isReceive) {
				try {
					BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					receiveMsg = buffer.readLine();
					System.out.println(Integer.toHexString(receiveMsg.charAt(0)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
}