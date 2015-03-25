package com.elicec.home;

import com.example.login.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_about extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.about, container, false);
    	((TextView)(getActivity().findViewById(R.id.ivTitleName))).setText("¹ØÓÚ");
    	return view;
	}

	
}
