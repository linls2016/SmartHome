package com.lins.smarthome;
/**
 * 用于创建Wifi连接的单例模式
 * 使得各个类中的wifi对象一致
 * @author LinLiangsheng
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

public class LinkWifi {
	static LinkWifi wifi = new LinkWifi();	//创建wifi静态对象

	private String ServerIP;							//IP地址
	private int ServerPort;							//端口号
	private Socket socket;							//socket对象，用于网络数据通讯
	private boolean isConnect = false;	//是否连接
	private boolean isReceive = false;	//是否接收
	private OutputStream outStream;		//输出流
	private String sendMsg = null;				//发送数据
	private String receiveMsg = null;			//接收数据
	private byte[] sendBuffer = null;			//接收缓冲区

	/**
	 * 得到Wifi实例
	 * @return wifi实例对象
	 */
	public static LinkWifi getInstance() {
		return wifi;
	}

	/**
	 * 获取IP地址
	 * @return IP地址
	 */
	public String getServerIP() {
		return ServerIP;
	}

	/**
	 * 设置IP地址
	 * @param serverIP
	 */
	public void setServerIP(String serverIP) {
		ServerIP = serverIP;
	}

	/**
	 * 获取端口号
	 * @return 端口号
	 */
	public int getServerPort() {
		return ServerPort;
	}

	/**
	 * 设置端口号
	 * @param serverPort
	 */
	public void setServerPort(int serverPort) {
		ServerPort = serverPort;
	}

	/**
	 * 获取wifi连接，通过线程方式
	 */
	public void getConnect() {
		/*未连接则进行连接，已连接则不再进行连接*/
		if (!isConnect) {
			new Thread(connectThread).start();
		}
	}

	/**
	 * 发送数据，通过线程方式
	 * @param msg
	 */
	public void sendMsg(String msg) {
		this.sendMsg = msg;
		new Thread(sendThread).start();
	}

	/**
	 * 获取接收到的数据
	 * @return 接收数据
	 */
	public String getReceiveMsg() {
		return receiveMsg;
	}

	/**
	 * 自定义接收数据
	 * @param receiveMsg
	 */
	public void setReceiveMsg(String receiveMsg) {
		this.receiveMsg = receiveMsg;
	}

	/**
	 * wifi连接的线程实现
	 */
	Runnable connectThread = new Runnable() {
		@Override
		public void run() {
			try {
				socket = new Socket(getServerIP(),getServerPort());	//通过IP和端口新建socket对象
				isConnect = true;																//标记已连接
				new Thread(receiveThread).start();								//开启接收数据线程，时刻接收数据
				isReceive = true;																//标记已开启数据接收
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 数据发送的线程实现
	 */
	Runnable sendThread = new Runnable() {
		@Override
		public void run() {
			try {
				sendBuffer = sendMsg.getBytes("UTF-8");		//以UTF-8比特数据的格式发送
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			try {
				outStream = socket.getOutputStream();			//创建输出流对象
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outStream.write(sendBuffer);								//数据输出
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 数据接收的线程实现
	 */
	Runnable receiveThread = new Runnable() {
		@Override
		public void run() {
			/*isReceive在创建连接时被赋予true，此循环将在线程中一直进行下去*/
			while (isReceive) {
				try {
					BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	//数据接收缓存
					receiveMsg = buffer.readLine();			//获取接收数据，数据以'\n'作为接收结束标志
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	};

}