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
			copyResToSdcard();//复制资源文件到sd卡
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
						if(mApp.isRegdit(name)){//数据库中找到用户名，说明已经注册，接着进行验证密码
							String pw,pwDatabase;//pw输入密码，pwDatabase数据库中的密码
							pw=password.getText().toString();
							pwDatabase=mApp.getShareString(name);
							if(pw.equals(pwDatabase)){
								mApp.putShareString(MyApp.SHARED_CURRENT_USER, name);
								Intent intent=new Intent(LoginActivity.this, MainActivity.class);
								startActivity(intent);
							}else{
								Toast.makeText(LoginActivity.this, "账户密码不匹配", Toast.LENGTH_SHORT).show();
							}
							
							
							
						}else{//没有在数据库中找到用户名，说明没有注册
							Toast.makeText(LoginActivity.this, "用户名未注册", Toast.LENGTH_SHORT).show();
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
								Toast.makeText(LoginActivity.this, "该用户已经注册", Toast.LENGTH_SHORT).show();
							
						}else{
							Toast.makeText(LoginActivity.this, "注册失败,请检查输入参数", Toast.LENGTH_SHORT).show();
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
	    * 将需要用到的资源文件拷贝到sd卡
	    * @param name
	 * @throws Exception 
	    */
	   
	   public void copyResToSdcard() throws Exception{
		   InputStream myInput = getResources().openRawResource(R.raw.modetxt0);
			File file = new File(MyApp.FILE_PATH+"mode/modetxt0.txt");
		
			File dir = new File(file.getParent());
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					throw new Exception("创建文件夹失败");
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
			//复制图像模版文件
			
			InputStream myInputbmp = getResources().openRawResource(R.raw.modebmp0);
			File filebmp = new File(MyApp.FILE_PATH+"mode/modebmp0.bmp");
		
			File dirbmp = new File(file.getParent());
			if (!dirbmp.exists()) {
				if (!dirbmp.mkdir()) {
					throw new Exception("创建文件夹失败");
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
