//安卓登录界面
package com.wificontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class LoginActivity extends Activity {

	private static final int MESSAGETYPE_01 = 0x0001;
	
	String ip; //wifi模块TCP模式的IP
	int port; //wifi模块TCP模式的端口号

	private EditText ipedittext; //IP编辑框
	private EditText portedittext; //端口号编辑框
	private ImageButton wifi_button; //登录按钮
	private ImageButton quit_button; //退出按钮

	private Socket socket; //TCP套接字对象

	private ProgressDialog progressDialog = null; // 进度条,用来显示wifi连接状态

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//隐藏程序标题栏和安卓状态栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics displaysMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
		eConfig.density = displaysMetrics.density; // 获取屏幕像素密度
		eConfig.screenWidth = displaysMetrics.widthPixels;// 获取屏幕宽度
		eConfig.screenHeight = displaysMetrics.heightPixels;// 获取屏幕高度

		setContentView(R.layout.activity_login);
		
		/**
		 * 获取用户输入的端口和IP
		 */
		ipedittext = (EditText)findViewById(R.id.editText_ip);
		portedittext = (EditText)findViewById(R.id.editText_port);
		
		//判断是否是第一次登录，如果是则创建wifiled.txt保存用户的IP和端口号，如果不是则把每次用户设置的IP和端口号写入到该文件
		 String txtfile = "/mnt/sdcard/wifiled.txt";
		 File filetxt=new File(txtfile);
		 if(filetxt.exists()){
			 try{
				 String res="";
				 File filetxt11=new File(txtfile);
				 FileInputStream fis;
				 fis = new FileInputStream(filetxt11);
				 int length = fis.available();
				 byte [] buffer = new byte[length];
				 fis.read(buffer);    
				 res = EncodingUtils.getString(buffer, "UTF-8");
				 String sub = res.substring(res.indexOf(35), res.lastIndexOf(35) + 1);
				 String[] strs = sub.split("#");
				 ipedittext.setText(strs[1]);
				 portedittext.setText(strs[2]);
				 
				 fis.close();
				 }
			 catch(Exception e){
				 e.printStackTrace();
				 }
			 }
		
		 //登录按键的响应功能
		wifi_button = (ImageButton) findViewById(R.id.wifi_button);
		wifi_button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				progressDialog = ProgressDialog.show(LoginActivity.this,
						"wifi家电远程控制", "尝试连接单片机WIFI服务器.......");
				
				ip = ipedittext.getText().toString(); //从编辑框获取IP
				port = Integer.parseInt(portedittext.getText().toString()); //从编辑框获取端口号

				new Thread() {

					public void run() {
						if(eConfig.socket!=null){
							Intent it = new Intent(LoginActivity.this,
									MainActivity.class);
							LoginActivity.this.startActivity(it);
						}
						else{
							try {
                        
								//创建一个套接字并根据上一步获取的IP和端口号连接wifi的TCP服务器
								socket = new Socket(ip, port);
								if (socket.isConnected()) {
									System.out.println("连接服务器成功!\n");
									InetAddress address = socket.getInetAddress();
									System.out.println("IP是: "
											+ address.getHostAddress() + "  主机名是: "
											+ address.getHostName());
								}
								
								//连接上wifi后说明IP和端口号可用则保存当前设置的IP和端口号使得每次进入登录界面不用重新设置
								if (socket != null) {
									
									try{
										File file=new File("/mnt/sdcard/ehome.txt");
										FileOutputStream fos;
										fos = new FileOutputStream(file);
										fos.write(("#"+ipedittext.getText().toString()+"#"+portedittext.getText().toString()+"#").getBytes());
										fos.close();
										}
									catch(Exception e){
										e.printStackTrace();
										}
									
									Log.d("zhinengjiaji", "连接成功");
									/**
									 * 设置网络,输入流,输出流
									 */
									eConfig.socket = socket;
									eConfig.out = new PrintWriter(
											new BufferedWriter(
													new OutputStreamWriter(socket
															.getOutputStream(),
															"UTF-8")), true);
									eConfig.br = new BufferedReader(
											new InputStreamReader(socket
													.getInputStream(), "UTF-8"));
									//sleep(30);
									Intent it = new Intent(LoginActivity.this,
											MainActivity.class);
									LoginActivity.this.startActivity(it);
									////finish();
								} else {
									Log.d("zhinengjiaji", "连接失败,请重新配置ip,重连");
									Intent it = new Intent(LoginActivity.this,
											LoginActivity.class);
									LoginActivity.this.startActivity(it);
									finish();
								}

							} catch (Exception e) {
								System.out.println("连接出错");
							}
						}

						Message msg_listData = new Message();
						msg_listData.what = MESSAGETYPE_01;
						handler.sendMessage(msg_listData);
					}
				}.start();

			}
		});
		
		//退出按钮的响应操作
		quit_button = (ImageButton) findViewById(R.id.quit_button);
		quit_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					if(eConfig.socket!=null)
					{
						//退出客户端，端口TCP客户端连接
						eConfig.socket.close();
						eConfig.socket = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				finish();
			}
		});
	}

	/**
	 * 进度条,用于显示wifi的连接状态
	 */
	private Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				// 刷新UI，显示数据，并关闭进度条
				progressDialog.dismiss(); // 关闭进度条
				break;
			}
		}
	};

}
