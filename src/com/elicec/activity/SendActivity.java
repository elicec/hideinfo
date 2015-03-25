package com.elicec.activity;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.elicec.home.FileTransferService;
import com.elicec.home.WFSearchAnimationFrameLayout;
import com.elicec.wifi.WifiApConst;
import com.elicec.wifi.WifiUtils;
import com.elicec.wifi.WifiapBroadcast;
import com.example.login.R;

public class SendActivity extends Activity implements
		WifiapBroadcast.EventHandler {
	
	public WifiUtils mWifiUtils;
	public ImageButton btnSearch;
	public ImageView otherPhone;
	public WifiapBroadcast mWifiapBroadcast;
	private ArrayList<String> mWifiApList; // 符合条件的热点列表
	public WFSearchAnimationFrameLayout m_FrameLWTSearchAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchactivity);

		mWifiapBroadcast = new WifiapBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(mWifiapBroadcast, filter);

		m_FrameLWTSearchAnimation = (WFSearchAnimationFrameLayout) findViewById(R.id.search_animati);

		otherPhone = (ImageView) findViewById(R.id.otherphone);
		mWifiUtils = WifiUtils.getInstance(this);
		mWifiapBroadcast.addehList(this);
		mWifiApList = new ArrayList<String>();

		btnSearch = (ImageButton) findViewById(R.id.btn_serach);

		if (!mWifiUtils.mWifiManager.isWifiEnabled()) {// 如果wifi关闭着
			mWifiUtils.OpenWifi();
		}
		mWifiUtils.startScan();
		m_FrameLWTSearchAnimation.startAnimation();

		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if (!mWifiUtils.mWifiManager.isWifiEnabled()) {// 如果wifi关闭着
				// mWifiUtils.OpenWifi();
				// }
				// mWifiUtils.startScan();
				// m_FrameLWTSearchAnimation.startAnimation();

			}
		});

		otherPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WifiConfiguration localWifiConfiguration = mWifiUtils
						.createWifiInfo(mWifiApList.get(0),
								WifiApConst.WIFI_AP_PASSWORD, 3, "wt");
				mWifiUtils.addNetwork(localWifiConfiguration);

				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("*/*");
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				try {
					startActivityForResult(
							Intent.createChooser(intent, "选择文件发送"), 5);
				} catch (ActivityNotFoundException ex) {
					Toast.makeText(SendActivity.this, "没有找到文件管理器，请安装",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		otherPhone.setClickable(false);

	}

	@Override
	public void handleConnectChange() {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage(WifiApConst.ApConnectResult);
		handler.sendMessage(msg);
	}

	@Override
	public void scanResultsAvailable() {
		// TODO Auto-generated method stub

		Message msg = handler.obtainMessage(WifiApConst.ApScanResult);
		handler.sendMessage(msg);
	}

	@Override
	public void wifiStatusNotification() {
		// TODO Auto-generated method stub

	}

	/** handler 异步更新UI **/
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 搜索超时
			case WifiApConst.ApSearchTimeOut:

				break;
			// 搜索结果
			case WifiApConst.ApScanResult:

				int size = mWifiUtils.mWifiManager.getScanResults().size();
				if (size > 0) {
					for (int i = 0; i < size; ++i) {
						String apSSID = mWifiUtils.mWifiManager
								.getScanResults().get(i).SSID;
						if (apSSID.startsWith(WifiApConst.WIFI_AP_HEADER)
								&& !mWifiApList.contains(apSSID)) {
							mWifiApList.add(apSSID);
						}
					}
					// WifiConfiguration localWifiConfiguration =
					// mWifiUtils.createWifiInfo(mWifiApList.get(0),
					// WifiApConst.WIFI_AP_PASSWORD, 3, "wt");
					// mWifiUtils.addNetwork(localWifiConfiguration);
					if (mWifiApList.size() > 0){
						otherPhone.setVisibility(View.VISIBLE);
						otherPhone.setClickable(true);
					}
						
				}
				break;
			// 连接成功,开始发送文件
			case WifiApConst.ApConnectResult:
				// Toast.makeText(SendActivity.this, "连接成功",
				// Toast.LENGTH_LONG).show();
				otherPhone.setClickable(true);
				break;

			// 热点创建结果
			case WifiApConst.ApCreateAPResult:

				break;
			case WifiApConst.ApConnectting:

				break;
			case WifiApConst.ApConnected:

				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.v(MainActivity.TAG, "文件返回" + data);
		if(data==null)return;
		Uri uri = data.getData();
		// boolean filetype=false;
		// try{
		// InputStream is = getContentResolver().openInputStream(uri);
		//
		// filetype=FileOperate.isBmp(is);
		// }catch(IOException e){
		// e.printStackTrace();
		// }
		//

		// String uri2filePath=FileOperate.getPath(this, uri);//通过uri获得文件实际路径
		// String filename=new File(uri2filePath).getName();
		// Log.v(MainActivity.TAG, data.getExtras().toString());
		Intent serviceIntent = new Intent(this, FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH,
				uri.toString());

		// serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_TYPE,
		// filetype);
		String ip = mWifiUtils.getServerIPAddress();
		serviceIntent.putExtra(FileTransferService.EXTRAS_ADDRESS, ip);
		serviceIntent.putExtra(FileTransferService.EXTRAS_PORT, 8988);
		startService(serviceIntent);
		Log.v(MainActivity.TAG, "启动发送文件服务");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mWifiapBroadcast); // 撤销广播
		mWifiapBroadcast.removeehList(this);
		m_FrameLWTSearchAnimation.stopAnimation();
		mWifiUtils.closeWifi();

	}

}
