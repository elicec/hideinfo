package com.elicec.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.elicec.common.FileOperate;
import com.elicec.common.MyApp;
import com.elicec.hide.HideInfoBmp;
import com.elicec.hide.HideInfoTxt;
import com.elicec.lock.LoginActivity;
import com.example.login.R;

public class DetailActivity extends Activity implements OnClickListener,OnTouchListener,OnGestureListener{
	private ImageView pic1,pic2;
	private TextView text1,text2;
	private RelativeLayout rl;
	private Intent intent;
	private String filepath;
	private ScrollView sv;
	private LinearLayout ll;
	private GestureDetector mGesture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detal_file);
		ll=(LinearLayout) findViewById(R.id.gesture);
		mGesture=new  GestureDetector(this);
		ll.setOnTouchListener(this);
		ll.setLongClickable(true);
		sv=(ScrollView) findViewById(R.id.scroolltext1);
		pic1=(ImageView) findViewById(R.id.show_pic1);
		pic2=(ImageView) findViewById(R.id.show_pic2);
		text1=(TextView) findViewById(R.id.show_txt1);
		text2=(TextView) findViewById(R.id.show_txt2);
		rl=(RelativeLayout) findViewById(R.id.btn_shou);
		rl.setOnClickListener(this);
		
		intent=getIntent();
		filepath=intent.getStringExtra(MyApp.ACTIVIT_PASS_FILE_PATH);
		try {
			if(FileOperate.isBmp(new File(filepath))){
				Bitmap bm=TransmitCompleteActivity.getSmallBitMap(filepath, 400);
				pic1.setVisibility(View.VISIBLE);
				pic1.setImageBitmap(bm);
			}else{
				text1.setVisibility(View.VISIBLE);
				loadTxt(filepath, "utf-8", text1);
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btn_shou){
			try{
				
				
				
				
				if(FileOperate.isBmp(new File(filepath))){
					String temFile=Environment.getExternalStorageDirectory() + "/"//提取出来的临时文件
							+ "elicec/tem" + "/tempHide";
					File f = new File(temFile);
					
					try {
						File dirs = new File(f.getParent());
						
						if (!dirs.exists())
							dirs.mkdirs();
						if(f.exists())f.delete();
						f.createNewFile();
						OutputStream out = new FileOutputStream(f);

						HideInfoBmp.ExecuteDecrypt(new RandomAccessFile(new File(
								filepath), "rw"), out);
						
						String ftype=FileOperate.getTypeByStream(new FileInputStream(temFile));
						
						if(ftype.equals("jpg")){
							pic2.setVisibility(View.VISIBLE);
							pic2.setImageBitmap(TransmitCompleteActivity.getSmallBitMap(temFile, 300));
						}else{
							text2.setVisibility(View.VISIBLE);
							//text2.setVisibility(View.INVISIBLE);
							//pic2.setVisibility(View.INVISIBLE);
							SystemClock.sleep(300);
							loadTxt(temFile,"utf-8",text2);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					byte [] bytes =HideInfoTxt.getInfoFromText(new File(filepath), "utf-8");
					text2.setVisibility(View.VISIBLE);
					SystemClock.sleep(300);
					text2.setText(new String(bytes,"utf-8"));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
		
		
	}
	
	
	public void loadTxt(String filePath,String code,TextView txtContent){
		StringBuffer sb=new StringBuffer();
		try{
			FileInputStream fis=new FileInputStream(new File(filePath));
			InputStreamReader isr=new InputStreamReader(fis,code);
			BufferedReader in=new BufferedReader(isr);
			while(in.ready()){
				sb.append(in.readLine()+"\n");
			}
			in.close();
			txtContent.setText(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		
		if(e2.getX()-e1.getX()>60&&e2.getY()-e1.getY()>150){
			try{
				if(FileOperate.isBmp(new File(filepath))){
					String temFile=Environment.getExternalStorageDirectory() + "/"//提取出来的临时文件
							+ "elicec/tem" + "/tempHide";
					File f = new File(temFile);
					
					try {
						File dirs = new File(f.getParent());
						
						if (!dirs.exists())
							dirs.mkdirs();
						if(f.exists())f.delete();
						f.createNewFile();
						OutputStream out = new FileOutputStream(f);

						HideInfoBmp.ExecuteDecrypt(new RandomAccessFile(new File(
								filepath), "rw"), out);
						
						String ftype=FileOperate.getTypeByStream(new FileInputStream(temFile));
						
						if(ftype.equals("jpg")){
							pic2.setVisibility(View.VISIBLE);
							pic2.setImageBitmap(TransmitCompleteActivity.getSmallBitMap(temFile, 300));
						}else{
							text2.setVisibility(View.VISIBLE);
							//text2.setVisibility(View.INVISIBLE);
							//pic2.setVisibility(View.INVISIBLE);
							SystemClock.sleep(300);
							loadTxt(temFile,"utf-8",text2);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					byte [] bytes =HideInfoTxt.getInfoFromText(new File(filepath), "utf-8");
					text2.setVisibility(View.VISIBLE);
					SystemClock.sleep(300);
					text2.setText(new String(bytes,"utf-8"));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		mGesture.onTouchEvent(event);
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		mGesture.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
	
	

}
