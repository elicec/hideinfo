package com.elicec.common;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;


public class MyApp {
	/**
	 * 全局常量
	 */
	public static List<String> ipList=new ArrayList<String>();
	public static final String TAG="elicec";
	public static final String FILE_TYPE_IMAGE="imagebmp";
	public static final String FILE_TYPE_TXT="texttext";
	public static final String ACTIVIT_PASS_FILE_PATH="filepath";
	public static final String ACTIVIT_PASS_FILE_TYPE="filetype";
	public static final String SHARED_FILENAME="shared_file_name";
	public static final String SHARED_TXTBIANMA="shared_txt_bianma";
	public static final String SHARED_CURRENT_USER="shared_current_user";
	public static final String FILE_PATH =Environment.getExternalStorageDirectory() + "/elicec/";
	public static final int FILETYPE_TEXT=0;
	public static final int FILETYPE_IMAGE=1;
	public static final int FILETYPE_OTHER=2;
	/**
	 * 私有
	 * 
	 */
	private SharedPreferences mshare;
	private SharedPreferences.Editor mEdit;

	public MyApp(Context context) {
		mshare = context.getSharedPreferences(SHARED_FILENAME, 0);
		mEdit = mshare.edit();
	}

	public void putShareString(String name, String value) {
		mEdit.putString(name, value);
		mEdit.commit();
	}

	public boolean isRegdit(String name) {
		if (!mshare.contains(name))
			return false;
		else
			return true;

	}
	
	public String getShareString(String key){
		return mshare.getString(key, "defValue");
	}
	
	public String getTxtBianma(){
		return mshare.getString(SHARED_TXTBIANMA,"utf-8");
	}

}
