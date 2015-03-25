// Copyright 2011 Google Inc. All Rights Reserved.

package com.elicec.home;

import java.io.FileNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.elicec.activity.MainActivity;
import com.elicec.common.FileOperate;
import com.elicec.common.MyApp;
import com.elicec.hide.HideInfoBmp;
import com.elicec.hide.HideInfoTxt;



import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
	public static final String EXTRAS_FILE_TYPE="file_tpye";
	public static final String EXTRAS_FILE_PATH = "file_url";
	public static final String EXTRAS_ADDRESS = "go_host";
	public static final String EXTRAS_PORT = "go_port";
	
	public String code;
	

	public FileTransferService(String name) {
		super(name);
	}

	public FileTransferService() {
		super("FileTransferService");
	}
	
	
	public static boolean copyFile(InputStream inputStream, OutputStream out) {
		byte buf[] = new byte[1024];
		
		int len;
		try {
			//out.write("imagebmp".getBytes());
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);

			}
			out.close();
			inputStream.close();
		} catch (IOException e) {
			Log.d(MainActivity.TAG, e.toString());
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		Context context = getApplicationContext();
		code=new MyApp(context).getTxtBianma();
		if (intent.getAction().equals(ACTION_SEND_FILE)) {
			String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
			String host = intent.getExtras().getString(EXTRAS_ADDRESS);
			host="192.168.43.1";//测试
			//boolean isb=intent.getExtras().getBoolean(EXTRAS_FILE_TYPE);
			Socket socket = new Socket();
			
			
			
			int port = intent.getExtras().getInt(EXTRAS_PORT);
			Log.i(MainActivity.TAG,"产生数据文件路径："+fileUri+"ip地址："+host+"端口："+port+intent.getType());

			try {
				Log.d(MainActivity.TAG, "Opening client socket - ");
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

				Log.d(MainActivity.TAG, "Client socket - " + socket.isConnected());
				OutputStream stream = socket.getOutputStream();
				ContentResolver cr = context.getContentResolver();
				InputStream is = null;
				InputStream isWrap=null;	
					
				try {
					
					is = cr.openInputStream(Uri.parse(fileUri));
					if(is.available()<250){//小文件采用文本技术200字节
						String modeFilepath=Environment.getExternalStorageDirectory() + "/"
		    					+ "elicec/mode" + "/modetxt0.txt"; 
						isWrap=HideInfoTxt.hideInfo2Text(is, "gb2312", modeFilepath);
					}else{//大文件采用图像
						String modeFilepath=Environment.getExternalStorageDirectory() + "/"
		    					+ "elicec/mode" + "/modebmp0.bmp"; 
						String tmpModefilepath=Environment.getExternalStorageDirectory() + "/"
		    					+ "elicec/modetemp" + "/modebmp0temp.bmp"; 
						File f=new File(tmpModefilepath);
						
						File dirs = new File(f.getParent());
						if(f.exists())f.delete();
		    			if (!dirs.exists())
		    				dirs.mkdirs();
		    			f.createNewFile();
						
						FileOperate.copyFile(modeFilepath, tmpModefilepath);
						RandomAccessFile randFile=new RandomAccessFile(new File(tmpModefilepath),"rw" );
						HideInfoBmp mhide=new HideInfoBmp(is, randFile);
						mhide.HideInfoLength();
						mhide.HideInfoContent();
						isWrap=new FileInputStream(new File(tmpModefilepath));
					}
				
					
					
				} catch (FileNotFoundException e) {
					Log.d(MainActivity.TAG, e.toString());
				}
				copyFile(isWrap, stream);
				Log.i(MainActivity.TAG, "数据写入网络流");
			} catch (IOException e) {
				Log.e(MainActivity.TAG, e.getMessage());
			} finally {
				if (socket != null) {
					if (socket.isConnected()) {
						try {
							socket.close();
						} catch (IOException e) {
							// Give up
							e.printStackTrace();
						}
					}
				}
			}

		}
	}
}
