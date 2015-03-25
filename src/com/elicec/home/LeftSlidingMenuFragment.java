package com.elicec.home;




import com.elicec.activity.MainActivity;
import com.elicec.common.MyApp;
import com.example.login.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class LeftSlidingMenuFragment extends Fragment implements OnClickListener{
	private View homeBtnLayout;
	private View historyBtnLayout;
	private View settingBtnLayout;
	private TextView nickName;
	private TextView aboutText;
	private MyApp mApp;
	
     @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    }
     
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	 View view = inflater.inflate(R.layout.main_left_fragment, container,
 				false);
		mApp = new MyApp(getActivity());
		homeBtnLayout = view.findViewById(R.id.home_page);
		homeBtnLayout.setOnClickListener(this);
		historyBtnLayout = view.findViewById(R.id.history_page);
		historyBtnLayout.setOnClickListener(this);
		settingBtnLayout = view.findViewById(R.id.settingBtnLayout);
		settingBtnLayout.setOnClickListener(this);
		nickName = (TextView) view.findViewById(R.id.nickNameTextView);
		nickName.setText(mApp.getShareString(MyApp.SHARED_CURRENT_USER));
		aboutText = (TextView) view.findViewById(R.id.about_page);
		aboutText.setOnClickListener(this);
    	  
    	 
 		System.out.println();
    	return view;
    }

	@Override
	public void onClick(View v) {
		Fragment newContent = null;
		switch (v.getId()) {
		case R.id.home_page:
			newContent =  new Fragment_home();
			
			homeBtnLayout.setSelected(true);
			historyBtnLayout.setSelected(false);
			settingBtnLayout.setSelected(false);
			
			break;
		case R.id.history_page:
			newContent = new Fragment_history();
			homeBtnLayout.setSelected(false);
			historyBtnLayout.setSelected(true);
			settingBtnLayout.setSelected(false);
			break;
		case R.id.settingBtnLayout:
			newContent = new Fragment_setting();
			homeBtnLayout.setSelected(false);
			historyBtnLayout.setSelected(false);
			settingBtnLayout.setSelected(true);
		    break;
		case R.id.about_page:
			newContent = new Fragment_about();
			aboutText.setSelected(true);
			homeBtnLayout.setSelected(false);
			historyBtnLayout.setSelected(false);
			settingBtnLayout.setSelected(false);
			
		default:
			break;
		}
		
		if (newContent != null)
			switchFragment(newContent);
		
	}
	
	
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		
			MainActivity ra = (MainActivity) getActivity();
			ra.switchContent(fragment);
			
		
	}
}
