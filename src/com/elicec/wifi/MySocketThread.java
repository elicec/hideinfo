package com.elicec.wifi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.elicec.activity.MainActivity;
import com.elicec.activity.ServerActivity;
import com.elicec.common.FileOperate;
import com.elicec.common.MyApp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;



public class MySocketThread implements Runnable {

	
	private static MySocketThread instance; // 唯一实例
	private static final String BROADCASTIP = "255.255.255.255"; // 广播地址
	private static Context mContext;
	private boolean isThreadRunning;
	private Thread receiveUDPThread; // 接收UDP数据线程
	private WifiUtils mWifiUtils;
	private ServerSocket serverSocket;
	
	 
	 
	 /** 开始监听线程 **/
	    public void startUDPSocketThread() {
	        if (receiveUDPThread == null) {
	            receiveUDPThread = new Thread(this);
	            receiveUDPThread.start();
	        }
	        isThreadRunning = true;
	    }

	    /** 暂停监听线程 **/
	    public void stopUDPSocketThread() {
	        if (receiveUDPThread != null)
	            receiveUDPThread.interrupt();
	        isThreadRunning = false;
	    }
	 
	 
	 
	 public static MySocketThread getInstance( Context context) {
	        if (instance == null) {
	            mContext = context;
	            instance = new MySocketThread();
	        }
	        return instance;
	    }
	 
	 
	 /** 用户上线通知 发送一个255.255.255.255的广播，广播自己的ip地址
	 * @throws IOException **/
	    public void notifyOnline() throws IOException {
	        // 获取本机用户数据
	       
	         mWifiUtils=WifiUtils.getInstance(mContext);
	         String localIPaddress = "guangboi"+mWifiUtils.getLocalIPAddress();
	         
	         byte[] localIpByte=localIPaddress.getBytes("utf-8");
	         Socket socket = new Socket();
	         socket.bind(null);
	         socket.connect(new InetSocketAddress("255.255.255.255",8988), 5000);
	         OutputStream stream = socket.getOutputStream();
	        // ByteArrayInputStream in=new ByteArrayInputStream(localIpByte);
	         stream.write(localIpByte);
	         stream.close();
	         
	    }
	 
	 
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(isThreadRunning){
			
			try {
				Log.v(MainActivity.TAG, "服务器监听启动……");
				serverSocket = new ServerSocket(8988);
				Socket client = serverSocket.accept();
				Log.v(MainActivity.TAG, "服务器监听启动成功");

				InputStream inputstream = client.getInputStream();
				Log.v(MainActivity.TAG, "活动网络数据流");
				
				 /*先根据发送过来的命令,判断是否需要开启下面的服务*/  
	            byte[] cmdBytes=new byte[4];  
	            String cmdString=null;  
	            int readCount=0;
	            readCount=inputstream.read(cmdBytes);  
	            if(readCount==-1)  
                return;
	       //     cmdString=new String(cmdBytes,0,readCount,"utf-8");  
	            cmdString=FileOperate.bytesToHexString(cmdBytes).toUpperCase();
	            String username=new MyApp(mContext).getShareString(MyApp.SHARED_CURRENT_USER);
	            //424d为bmp文件
	            if (cmdString.contains("424D")){
	            	
	            	final File f = new File(Environment.getExternalStorageDirectory() + "/"
	    					+ "elicec/"+username+"/receivedFile" + "/recevieBMP-" + FileOperate.getDateForfilename()
	    					+ ".bmp");

	    			File dirs = new File(f.getParent());
	    			if (!dirs.exists())
	    				dirs.mkdirs();
	    			f.createNewFile();
	    			OutputStream out=new FileOutputStream(f);
	    			out.write(cmdBytes);
	    			copyFile(inputstream, out);
	    			serverSocket.close();
	    			((ServerActivity)mContext).getData(1,f.getAbsolutePath());//接口回调
	            	
	            }else{
	            	final File f = new File(Environment.getExternalStorageDirectory() + "/"
	    					+ "elicec/"+username+"/receivedFile" +  "/recevieTXT-" + FileOperate.getDateForfilename()
	    					+ ".txt");

	    			File dirs = new File(f.getParent());
	    			if (!dirs.exists())
	    				dirs.mkdirs();
	    			f.createNewFile();
	    			OutputStream out=new FileOutputStream(f);
	    			out.write(cmdBytes);
	    			copyFile(inputstream, out);
	    			serverSocket.close();
	    			((ServerActivity)mContext).getData(0,f.getAbsolutePath());//接口回调文本文件
	            }
			} catch (IOException e) {
				//serverSocket.close();
				System.out.println(e.toString());
				isThreadRunning=false;
				
			}
		}
		
	}

	public static boolean copyFile(InputStream inputStream, OutputStream out) {
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);

			}
			out.close();
			inputStream.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	
	

}
