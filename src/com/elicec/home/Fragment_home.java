package com.elicec.home;

import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.elicec.activity.SendActivity;
import com.elicec.activity.ServerActivity;
import com.elicec.wifi.MySocketThread;
import com.elicec.wifi.WifiApConst;
import com.elicec.wifi.WifiUtils;
import com.elicec.wifi.WifiapBroadcast;
import com.example.login.R;

public class Fragment_home extends Fragment implements OnClickListener{
	Button btnSend;
	Button btnReceive;
	MySocketThread mSocketThread;
	private WifiapBroadcast mWifiapBroadcast;
	private Context mContext;
	private WifiUtils mWifiUtils;
	
	 @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    		Bundle savedInstanceState) {
	    	View view = inflater.inflate(R.layout.fragment_home, container, false);
	    	mContext=getActivity();
	    	btnReceive=(Button) view.findViewById(R.id.btn_receivefile);
	    	btnReceive.setOnClickListener(this);
	    	btnSend=(Button) view.findViewById(R.id.btn_sendfile);
	    	btnSend.setOnClickListener(this);
	    	mWifiUtils = WifiUtils.getInstance(mContext);
	    	((TextView)(getActivity().findViewById(R.id.ivTitleName))).setText("主页");
	    	
	    	return view;
	    }
	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_receivefile: {
			mWifiUtils.closeWifi();
            mWifiUtils.createWiFiAP(mWifiUtils.createWifiInfo(
                    WifiApConst.WIFI_AP_HEADER + getLocalHostName(),
                    WifiApConst.WIFI_AP_PASSWORD, 3, "ap"), true);
			Intent intent=new Intent(mContext,ServerActivity.class);
			startActivity(intent);
			break;

		}
		case R.id.btn_sendfile: {
			startActivity(new Intent(mContext, SendActivity.class));
			break;
		}

		}

	}
	
	
	 /**
     * 获取Wifi热点名
     * 
     * <p>
     * BuildBRAND 系统定制商 ； BuildMODEL 版本
     * </p>
     * 
     * @return 返回 定制商+版本 (String类型),用于创建热点。
     */
    public String getLocalHostName() {
        String str1 = Build.BRAND;
        String str2 = getRandomNumStr(3);
        return str1 + "_" + str2;
    }
    /**
     * 返回指定长度的一串数字
     * 
     * @param NumLen 数字串位数
     * @return
     */
    public static String getRandomNumStr(int NumLen) {
        Random random = new Random(System.currentTimeMillis());
        StringBuffer str = new StringBuffer();
        int i, num;
        for (i = 0; i < NumLen; i++) {
            num = random.nextInt(10); // 0-10的随机数
            str.append(num);
        }
        return str.toString();
    }

    public String getPhoneModel() {
        String str1 = Build.BRAND;
        String str2 = Build.MODEL;
        if (-1 == str2.toUpperCase().indexOf(str1.toUpperCase()))
            str2 = str1 + "_" + str2;
        return str2;
    }
    
}
