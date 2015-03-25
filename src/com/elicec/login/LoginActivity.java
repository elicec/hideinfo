package com.elicec.login;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.FileOutputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.elicec.activity.MainActivity;
import com.elicec.common.MyApp;
import com.example.login.R;

public class LoginActivity extends Activity {
	//private ImageView loginImage;
	private TextView topText;
	private boolean isBtnRegedit=false;
	private TextPaint tp;
	private Button loginbtn;
	private Button regeditbtn;
	private EditText username;
	private EditText password;
	private EditText validEdit;
	//private Thread mThread;
	private Drawable mIconPerson;
	private Drawable mIconLock;
	private ImageView validPic;
	public static final String TAG="elicec";
	public MyApp mApp;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.main);
		mIconPerson = getResources().getDrawable(R.drawable.txt_person_icon);
		mIconPerson.setBounds(5, 1, 60, 50);
		mIconLock = getResources().getDrawable(R.drawable.txt_lock_icon);
		mIconLock.setBounds(5, 1, 60, 50);

		username = (EditText) findViewById(R.id.username);
		username.setCompoundDrawables(mIconPerson, null, null, null);
		password = (EditText) findViewById(R.id.password);
		password.setCompoundDrawables(mIconLock, null, null, null);
		validEdit=(EditText) findViewById(R.id.querenmima);
		validEdit.setCompoundDrawables(mIconLock, null, null, null);
		
		mApp=new MyApp(this);
		
		try {
			copyResToSdcard();//������Դ�ļ���sd��
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		init();

	}

	public void init() {
		
		
		topText = (TextView) findViewById(R.id.topname);
		topText.setTextColor(Color.MAGENTA);
		topText.setTextSize(24.0f);
		topText.setTypeface(Typeface.MONOSPACE, Typeface.BOLD_ITALIC);

		tp = topText.getPaint();

		tp.setFakeBoldText(true);
		

		loginbtn = (Button) findViewById(R.id.loginbtn);
		loginbtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.getBackground().setAlpha(20);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.getBackground().setAlpha(255);
					String name =username.getText().toString();
						if(mApp.isRegdit(name)){//���ݿ����ҵ��û�����˵���Ѿ�ע�ᣬ���Ž�����֤����
							String pw,pwDatabase;//pw�������룬pwDatabase���ݿ��е�����
							pw=password.getText().toString();
							pwDatabase=mApp.getShareString(name);
							if(pw.equals(pwDatabase)){
								mApp.putShareString(MyApp.SHARED_CURRENT_USER, name);
								Intent intent=new Intent(LoginActivity.this, MainActivity.class);
								startActivity(intent);
							}else{
								Toast.makeText(LoginActivity.this, "�˻����벻ƥ��", Toast.LENGTH_SHORT).show();
							}
							
							
							
						}else{//û�������ݿ����ҵ��û�����˵��û��ע��
							Toast.makeText(LoginActivity.this, "�û���δע��", Toast.LENGTH_SHORT).show();
						}
							
						
						
							
					
				}
				return true;
			}

		});
		
		regeditbtn = (Button) findViewById(R.id.zhuce);
		regeditbtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(!isBtnRegedit){
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						v.getBackground().setAlpha(20);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						v.getBackground().setAlpha(255);
						validEdit.setVisibility(View.VISIBLE);
						isBtnRegedit=true;
					}
					
				}else{
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						v.getBackground().setAlpha(20);
						
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						v.getBackground().setAlpha(255);
						String name=username.getText().toString();
						String pw1=password.getText().toString();
						String pw2=validEdit.getText().toString();
						if(name.length()!=0&&pw1.length()!=0&&pw2.length()!=0&&pw1.equals(pw2)){
							if(!mApp.isRegdit(name)){
								mApp.putShareString(name, pw1);
								mApp.putShareString(MyApp.SHARED_CURRENT_USER, name);
								Intent intent=new Intent(LoginActivity.this, MainActivity.class);
								startActivity(intent);
								
							}else
								Toast.makeText(LoginActivity.this, "���û��Ѿ�ע��", Toast.LENGTH_SHORT).show();
							
						}else{
							Toast.makeText(LoginActivity.this, "ע��ʧ��,�����������", Toast.LENGTH_SHORT).show();
							return true;
						}
					}
				}
					
				
				return true;
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	  /**
	    * ����Ҫ�õ�����Դ�ļ�������sd��
	    * @param name
	 * @throws Exception 
	    */
	   
	   public void copyResToSdcard() throws Exception{
		   InputStream myInput = getResources().openRawResource(R.raw.modetxt0);
			File file = new File(MyApp.FILE_PATH+"mode/modetxt0.txt");
		
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					throw new Exception("�����ļ���ʧ��");
				}
			}
			if (!file.exists()) {			
				try {
					OutputStream myOutput = new FileOutputStream(file);
					
					byte[] buffer = new byte[1024];
			    	int length;
			    	while ((length = myInput.read(buffer))>0){
			    		myOutput.write(buffer, 0, length);
			    	}
			    	myOutput.close();
			    	myInput.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
			//����ͼ��ģ���ļ�
			
			InputStream myInputbmp = getResources().openRawResource(R.raw.modebmp0);
			File filebmp = new File(MyApp.FILE_PATH+"mode/modebmp0.bmp");
		
			File dirbmp = new File(file.getParent());
			if (!dirbmp.exists()) {
				if (!dirbmp.mkdir()) {
					throw new Exception("�����ļ���ʧ��");
				}
			}
			if (!filebmp.exists()) {			
				try {
					OutputStream myOutput = new FileOutputStream(filebmp);
					
					byte[] buffer = new byte[1024];
			    	int length;
			    	while ((length = myInputbmp.read(buffer))>0){
			    		myOutput.write(buffer, 0, length);
			    	}
			    	myOutput.close();
			    	myInputbmp.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
			
		     
		} 
	
	
	
}
