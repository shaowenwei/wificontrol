package com.wificontrol;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class eConfig {
	
	public static Socket socket = null;
	public static PrintWriter out = null;
	public static BufferedReader br = null;
	
	/**�������˵�IP */
	public static final String SERVER_IP = "183.14.168.80";
	/**�������˽�����TCP�˿� */
	public static final int SERVER_TCP_PORT = 8000;


	/**��Ļ��� */
	public static int screenWidth = 480;
	/**��Ļ�߶� */
	public static int screenHeight = 800;
	/**��Ļ�����ܶ� */
	public static float density;
	
}
