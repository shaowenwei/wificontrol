//��׿��¼����
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
	
	String ip; //wifiģ��TCPģʽ��IP
	int port; //wifiģ��TCPģʽ�Ķ˿ں�

	private EditText ipedittext; //IP�༭��
	private EditText portedittext; //�˿ںű༭��
	private ImageButton wifi_button; //��¼��ť
	private ImageButton quit_button; //�˳���ť

	private Socket socket; //TCP�׽��ֶ���

	private ProgressDialog progressDialog = null; // ������,������ʾwifi����״̬

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//���س���������Ͱ�׿״̬��
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics displaysMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
		eConfig.density = displaysMetrics.density; // ��ȡ��Ļ�����ܶ�
		eConfig.screenWidth = displaysMetrics.widthPixels;// ��ȡ��Ļ���
		eConfig.screenHeight = displaysMetrics.heightPixels;// ��ȡ��Ļ�߶�

		setContentView(R.layout.activity_login);
		
		/**
		 * ��ȡ�û�����Ķ˿ں�IP
		 */
		ipedittext = (EditText)findViewById(R.id.editText_ip);
		portedittext = (EditText)findViewById(R.id.editText_port);
		
		//�ж��Ƿ��ǵ�һ�ε�¼��������򴴽�wifiled.txt�����û���IP�Ͷ˿ںţ�����������ÿ���û����õ�IP�Ͷ˿ں�д�뵽���ļ�
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
		
		 //��¼��������Ӧ����
		wifi_button = (ImageButton) findViewById(R.id.wifi_button);
		wifi_button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				progressDialog = ProgressDialog.show(LoginActivity.this,
						"wifi�ҵ�Զ�̿���", "�������ӵ�Ƭ��WIFI������.......");
				
				ip = ipedittext.getText().toString(); //�ӱ༭���ȡIP
				port = Integer.parseInt(portedittext.getText().toString()); //�ӱ༭���ȡ�˿ں�

				new Thread() {

					public void run() {
						if(eConfig.socket!=null){
							Intent it = new Intent(LoginActivity.this,
									MainActivity.class);
							LoginActivity.this.startActivity(it);
						}
						else{
							try {
                        
								//����һ���׽��ֲ�������һ����ȡ��IP�Ͷ˿ں�����wifi��TCP������
								socket = new Socket(ip, port);
								if (socket.isConnected()) {
									System.out.println("���ӷ������ɹ�!\n");
									InetAddress address = socket.getInetAddress();
									System.out.println("IP��: "
											+ address.getHostAddress() + "  ��������: "
											+ address.getHostName());
								}
								
								//������wifi��˵��IP�Ͷ˿ںſ����򱣴浱ǰ���õ�IP�Ͷ˿ں�ʹ��ÿ�ν����¼���治����������
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
									
									Log.d("zhinengjiaji", "���ӳɹ�");
									/**
									 * ��������,������,�����
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
									Log.d("zhinengjiaji", "����ʧ��,����������ip,����");
									Intent it = new Intent(LoginActivity.this,
											LoginActivity.class);
									LoginActivity.this.startActivity(it);
									finish();
								}

							} catch (Exception e) {
								System.out.println("���ӳ���");
							}
						}

						Message msg_listData = new Message();
						msg_listData.what = MESSAGETYPE_01;
						handler.sendMessage(msg_listData);
					}
				}.start();

			}
		});
		
		//�˳���ť����Ӧ����
		quit_button = (ImageButton) findViewById(R.id.quit_button);
		quit_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					if(eConfig.socket!=null)
					{
						//�˳��ͻ��ˣ��˿�TCP�ͻ�������
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
	 * ������,������ʾwifi������״̬
	 */
	private Handler handler = new Handler() {

		@SuppressLint("HandlerLeak")
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				// ˢ��UI����ʾ���ݣ����رս�����
				progressDialog.dismiss(); // �رս�����
				break;
			}
		}
	};

}
