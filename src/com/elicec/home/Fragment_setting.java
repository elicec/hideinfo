package com.elicec.home;




import com.elicec.common.MyApp;
import com.example.login.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class Fragment_setting extends Fragment implements OnCheckedChangeListener{
	RadioGroup rg;
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_setting, container, false);
    	((TextView)(getActivity().findViewById(R.id.ivTitleName))).setText("…Ë÷√");
    	rg=(RadioGroup) view.findViewById(R.id.bianma);
    	rg.setOnCheckedChangeListener(this);
    	return view;
    }
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId){
		case R.id.gb2312:
			new MyApp(getActivity()).putShareString(MyApp.SHARED_TXTBIANMA, "gb2312");
			break;
		case R.id.gbk:
			new MyApp(getActivity()).putShareString(MyApp.SHARED_TXTBIANMA, "gbk");
			break;
		case R.id.utf8:
			new MyApp(getActivity()).putShareString(MyApp.SHARED_TXTBIANMA, "utf-8");
			break;
			
				
		}
	}
}
