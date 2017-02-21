package com.wificontrol;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class eConfig {
	
	public static Socket socket = null;
	public static PrintWriter out = null;
	public static BufferedReader br = null;
	
	/**服务器端的IP */
	public static final String SERVER_IP = "183.14.168.80";
	/**服务器端接听的TCP端口 */
	public static final int SERVER_TCP_PORT = 8000;


	/**屏幕宽度 */
	public static int screenWidth = 480;
	/**屏幕高度 */
	public static int screenHeight = 800;
	/**屏幕像素密度 */
	public static float density;
	
}
