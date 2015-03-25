package com.elicec.home;





import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.elicec.activity.DetailActivity;
import com.elicec.common.FileOperate;
import com.elicec.common.MyApp;
import com.example.login.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Fragment_history extends Fragment implements OnItemClickListener{
	
	private ListView lv;
	File[] dqs=null;
	private String username;
	
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	 
    	View view = inflater.inflate(R.layout.fragment_history, container, false);
    	((TextView)(getActivity().findViewById(R.id.ivTitleName))).setText("历史记录");
    	 //setListAdapter(new WiFiListAdapter(getActivity(), R.layout.devices_row, MyApp.ipList));
    	username=new MyApp(getActivity()).getShareString(MyApp.SHARED_CURRENT_USER);
    	lv=(ListView) view.findViewById(R.id.history_list);
		lv.setOnItemClickListener(this);
		try {
			tc();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	 
    	return view;
    }
    
     
     
     
     @Override
 	public void onItemClick(AdapterView<?> parent, View view, int position,
 			long id) {
 		// 跳转到详细界面
 		
 		String filepath =dqs[position].getAbsolutePath();
 		Intent intent=new  Intent(getActivity(),DetailActivity.class);
 		intent.putExtra(MyApp.ACTIVIT_PASS_FILE_PATH, filepath);
 		startActivity(intent);
 	}
     
     
     private void tc() throws IOException {
 		File f = new File(Environment.getExternalStorageDirectory() + "/"
 				+ "elicec/"+username+"/receivedFile/");

 		if (!f.exists())
 			f.mkdirs();

 		dqs = f.listFiles();
 		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
 		for (int i = 0; i < dqs.length; i++) {
 			Map<String, Object> list = new HashMap<String, Object>();
 			if (FileOperate.isBmp(dqs[i])) {
 				list.put("icon", R.drawable.bmpicon);
 			} else {
 				list.put("icon", R.drawable.txt2);
 			}
 			list.put("name", dqs[i].getName());
 			lists.add(list);
 		}

 		SimpleAdapter adapter = new SimpleAdapter(getActivity(), lists,
 				R.layout.history_row, new String[] { "name", "icon" },
 				new int[] { R.id.history_row_fileanme, R.id.history_row_img });
 		lv.setAdapter(adapter);
 	}
	
}
