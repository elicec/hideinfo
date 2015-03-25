package com.elicec.activity;



import com.elicec.common.ISocketThread;
import com.elicec.common.MyApp;
import com.elicec.home.WFSearchAnimationFrameLayout;
import com.elicec.wifi.MySocketThread;
import com.elicec.wifi.WifiApConst;
import com.elicec.wifi.WifiUtils;
import com.elicec.wifi.WifiapBroadcast;
import com.example.login.R;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ServerActivity extends Activity implements ISocketThread{
	public ImageButton btnSearch;
	public MySocketThread mSocketThread;
	public WifiapBroadcast mWifiapBroadcast;
	public WFSearchAnimationFrameLayout m_FrameLWTSearchAnimation;
	public TextView title;
	public WifiUtils mWifiUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchactivity);
		initBroadcast();
		m_FrameLWTSearchAnimation=(WFSearchAnimationFrameLayout) findViewById(R.id.search_animati);
		title=(TextView)findViewById(R.id.ivTitleName);
		title.setText("等待接收文件");
		m_FrameLWTSearchAnimation.startAnimation();
		mSocketThread=MySocketThread.getInstance(this);
		mSocketThread.startUDPSocketThread();
		Log.v(MainActivity.TAG, "监听启动"+mSocketThread.toString());
		btnSearch=(ImageButton) findViewById(R.id.btn_serach);
		mWifiUtils =WifiUtils.getInstance(this);
		btnSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	 public void initBroadcast() {
         mWifiapBroadcast = new WifiapBroadcast();
         IntentFilter filter = new IntentFilter();
         filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
         filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
         filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
         filter.setPriority(Integer.MAX_VALUE);
         registerReceiver(mWifiapBroadcast, filter);
     }

	@Override
	public void getData(int dataType,String filePath) {
		Intent intent;
		
		intent=new Intent(this,TransmitCompleteActivity.class);
		intent.putExtra(MyApp.ACTIVIT_PASS_FILE_PATH, filePath);
		intent.putExtra(MyApp.ACTIVIT_PASS_FILE_TYPE, dataType);
		startActivity(intent);
		
		
//		switch (dataType){
//		case 0:
//			intent = new Intent();
//			intent.setAction(android.content.Intent.ACTION_VIEW);
//			intent.setDataAndType(Uri.parse("file://" + filePath), "text/plain");
//			startActivity(intent);
//			break;
//		case 1:
//			intent = new Intent();
//			intent.setAction(android.content.Intent.ACTION_VIEW);
//			intent.setDataAndType(Uri.parse("file://" + filePath), "image/*");
//			startActivity(intent);
//			break;
//		case 2:
//			intent = new Intent();
//			intent.setAction(android.content.Intent.ACTION_VIEW);
//			intent.setDataAndType(Uri.parse("file://" + filePath), "*/*");
//			startActivity(intent);
//			break;
//		
//		}
	
		
	}
	 @Override
		public void onDestroy() {
			// TODO Auto-generated method stub
		 super.onDestroy();
	    	 unregisterReceiver(mWifiapBroadcast); // 撤销广播
	    	 mSocketThread.stopUDPSocketThread();
	    	 mWifiUtils.createWiFiAP(mWifiUtils.createWifiInfo(
                     mWifiUtils.getApSSID(), WifiApConst.WIFI_AP_PASSWORD,
                     3, "ap"), false);
	        
			
		}
	

}
