package com.wificontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Socket socket = eConfig.socket;
	BufferedReader br;
	private ImageButton btn_quit; //exit button
	MyThread myThread;
	volatile boolean bThreadRun = false;
	volatile boolean door = false;
	volatile boolean fire = false;
	private EditText editText_t; //temperature
	private EditText editText_h; //humidity
	private EditText editText_l; //light
	private EditText editText_d; //door
	private EditText editText_f; //fire
	private Button btn_lighton,btn_lightoff,btn_alarmon,btn_alarmoff,btn_fanon,btn_fanoff,btn_cookeron,btn_cookeroff;
	private CheckBox box;
	
	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//隐藏程序标题栏和安卓状态栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		DisplayMetrics displaysMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
		eConfig.density = displaysMetrics.density; //screen pixel density
		eConfig.screenWidth = displaysMetrics.widthPixels;//screen width
		eConfig.screenHeight = displaysMetrics.heightPixels;//screen height

		this.setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}

		editText_t = (EditText)findViewById(R.id.editText_t);
		editText_h = (EditText)findViewById(R.id.editText_h);
		editText_l = (EditText)findViewById(R.id.editText_l);
		editText_d = (EditText)findViewById(R.id.editText_d);
		editText_f = (EditText)findViewById(R.id.editText_f);
		box=(CheckBox)findViewById(R.id.box);
		box.setChecked(true);
		if (socket.isConnected()) {
			try {
				socket.getOutputStream().write(0x5);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			            public void onCheckedChanged(CompoundButton buttonView,
			                    boolean isChecked) {
			                // TODO Auto-generated method stub
			                if(isChecked){
			                	if (socket.isConnected()) {
			    					try {
			    						socket.getOutputStream().write(0x5);
			    					} catch (IOException e) {
			    						// TODO Auto-generated catch block
			    						e.printStackTrace();
			    					}
			                	}
			                }else{
			                	if (socket.isConnected()) {
			    					try {
			    						socket.getOutputStream().write(0x6);
			    					} catch (IOException e) {
			    						// TODO Auto-generated catch block
			    						e.printStackTrace();
			    					}
			                	}
			                }
			            }
			        });

		
		//开启灯光按钮的响应操作
		btn_lighton = (Button) findViewById(R.id.btn_lighton);
		btn_lighton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (socket.isConnected()) {
						try {
							box.setChecked(false);
							socket.getOutputStream().write(0x6);
							socket.getOutputStream().write(0x1);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		});
		
		//关闭灯光按钮的响应操作
		btn_lightoff = (Button) findViewById(R.id.btn_lightoff);
		btn_lightoff.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (socket.isConnected()) {
					try {
						box.setChecked(false);
						socket.getOutputStream().write(0x6);
						socket.getOutputStream().write(0x2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
		});
		
		//开启报警按钮的响应操作
		btn_alarmon = (Button) findViewById(R.id.btn_alarmon);
		btn_alarmon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (socket.isConnected()) {
					try {
						socket.getOutputStream().write(0x3);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
		});
		
		//关闭报警按钮的响应操作
		btn_alarmoff = (Button) findViewById(R.id.btn_alarmoff);
		btn_alarmoff.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (socket.isConnected()) {
					try {
						socket.getOutputStream().write(0x4);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
		});
		
		//开启电风扇按钮的响应操作
		btn_fanon = (Button) findViewById(R.id.btn_fanon);
		btn_fanon.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (socket.isConnected()) {
					try {
						socket.getOutputStream().write(0x7);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
		});
		
		//关闭电风扇按钮的响应操作
		btn_fanoff = (Button) findViewById(R.id.btn_fanoff);
		btn_fanoff.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (socket.isConnected()) {
					try {
						socket.getOutputStream().write(0x8);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
		});
		
		//开电饭煲按钮的响应操作
		btn_cookeron = (Button) findViewById(R.id.btn_cookeron);
		btn_cookeron.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (socket.isConnected()) {
					try {
						socket.getOutputStream().write(0x9);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
		});
		
		//关闭电饭煲按钮的响应操作
		btn_cookeroff = (Button) findViewById(R.id.btn_cookeroff);
		btn_cookeroff.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (socket.isConnected()) {
					try {
						socket.getOutputStream().write(0xa);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			}
		});
		
		//退出按钮的响应操作
		btn_quit = (ImageButton) findViewById(R.id.return_button);
		btn_quit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		myThread = new MyThread();
		myThread.start();
		bThreadRun = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		bThreadRun = false;
		onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// myThread.destroy();
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
		switch (msg.what) {
		case 1:
		updateTitle();
		break;
		}
		};
	};
	
	//该函数用于不断在线程中读取是否有wifi模块发来数据，此处不用，后续可以再次拓展
	public void updateTitle() {
		String msg = null;
		if (socket.isConnected()) {
			br = eConfig.br;
			try {
				socket.getOutputStream().write(0x0);
				if(br.ready()){
					msg = br.readLine();
					System.out.println("来自客户端的数据: " + msg);
					if(msg.contains("#")){
						String sub = msg.substring(msg.indexOf(35), msg.lastIndexOf(35) + 1);
						String[] strs = sub.split("#");
						System.out.println("温度: " + strs[2]);
						System.out.println("湿度: " + strs[1]);
						editText_t.setText(strs[2]+" 'C");
						editText_h.setText(strs[1]+" %");
						
						if(strs[3].contentEquals("0"))
							editText_l.setText("关闭");
						else
							editText_l.setText("开启");
						
						if(strs[4].contentEquals("0"))
						{
							door=false;
							editText_d.setText("关闭");
						}
						else
						{
							if(!door)
							{
								door=true;
								editText_d.setText("开启");
								Toast.makeText(getApplicationContext(), "请注意：家门被强行打开！", Toast.LENGTH_LONG).show();
							}
						}
						
						if(strs[5].contentEquals("0"))
						{
							fire=false;
							editText_f.setText("正常");	
						}
						else
						{
							if(!fire)
							{
								fire=true;
								editText_f.setText("报警");
								Toast.makeText(getApplicationContext(), "请注意：家里发生火灾！", Toast.LENGTH_LONG).show();
						
							}
						}
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public class MyThread extends Thread {
		// 声明字符串变量
		public MyThread() {
		}
		@Override
		public void start() {
			super.start();
		}

		// 线程的主要工作方法
		@Override
		public void run() {
			while (bThreadRun) {
				try {
					//每500毫秒更新一次ui
					sleep(1000);
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				} catch (InterruptedException ex) {
				}
			}
			}
		}
}
