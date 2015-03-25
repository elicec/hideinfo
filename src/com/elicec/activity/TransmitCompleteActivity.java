package com.elicec.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elicec.common.FileOperate;
import com.elicec.common.MyApp;
import com.elicec.hide.HideInfoBmp;
import com.elicec.hide.HideInfoTxt;
import com.elicec.lock.LoginActivity;
import com.example.login.R;

public class TransmitCompleteActivity extends Activity implements OnClickListener,OnGestureListener,
OnTouchListener,OnDoubleTapListener{
	private ImageView btnFileType;
	private TextView textFileName;
	private TextView textFileSize;
	private Intent intent;
	private int fileType=MyApp.FILETYPE_OTHER;
	private String filePath;
	private RelativeLayout rl;
	private ImageView shouPic;//显示图片信息
	private TextView txtContent;//显示文本信息
	private boolean isClick=false;
	private LinearLayout ll;
	private String code;
	private String FileTypeBy01;
	private GestureDetector mGestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		code=new MyApp(this).getTxtBianma();
		code ="utf-8";
		setContentView(R.layout.transmit_complete);
		
		 mGestureDetector = new GestureDetector(this);
		 mGestureDetector.setOnDoubleTapListener(this);
		 
		btnFileType=(ImageView) findViewById(R.id.img_filetype);
		textFileName=(TextView) findViewById(R.id.text_filename);
		textFileSize=(TextView) findViewById(R.id.transmitsize);
		shouPic=(ImageView) findViewById(R.id.show_pic);
		txtContent=(TextView) findViewById(R.id.txt);
		ll=(LinearLayout) findViewById(R.id.mutitouch);
		
		ll.setOnTouchListener(this);
		ll.setLongClickable(true);
		
		
		
		rl=(RelativeLayout) findViewById(R.id.file_layout);//文件显示的条，点击进行操作
		rl.setOnClickListener(this);
		
		intent=getIntent();
		fileType=intent.getIntExtra(MyApp.ACTIVIT_PASS_FILE_TYPE, 2);
		
		
		filePath=intent.getStringExtra(MyApp.ACTIVIT_PASS_FILE_PATH);
		try {
			FileTypeBy01=FileOperate.getTypeByStream(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		textFileSize.setText("接收"+FileOperate.getFileOrFilesSize(filePath));
		//
//		if(fileType==MyApp.FILETYPE_TEXT){
//			btnFileType.setBackgroundResource(R.drawable.txt2);
//			textFileName.setText("文本文件");
//		}else if(fileType==MyApp.FILETYPE_IMAGE){
//			btnFileType.setImageBitmap(getSmallBitMap(filePath,50));
//			
//			textFileName.setText("图像文件");
//		}else{
//			btnFileType.setBackgroundResource(R.drawable.ic_launcher);
//			textFileName.setText(new File(filePath).getName());
//		}
		
		if (fileType == MyApp.FILETYPE_IMAGE) {
			btnFileType.setImageBitmap(getSmallBitMap(filePath, 50));

			textFileName.setText("图像文件");
		}else if(FileTypeBy01.equals("jpg")){
			btnFileType.setImageBitmap(getSmallBitMap(filePath,50));
			textFileName.setText("图像文件");
		}else{
			btnFileType.setBackgroundResource(R.drawable.txt2);
			textFileName.setText("文本文件");
		}
		
		
		if(fileType==MyApp.FILETYPE_TEXT){
			
				txtContent.setVisibility(View.VISIBLE);
				loadTxt(filePath,code);
				textFileName.setText("提取隐藏信息");
				isClick=true;
			
			
		}else if(fileType==MyApp.FILETYPE_IMAGE){//收到的是图像文件，不确定隐藏文件的类型
			
			
				shouPic.setVisibility(View.VISIBLE);
				shouPic.setImageBitmap(getSmallBitMap(filePath, 300));
				textFileName.setText("提取隐藏信息");
				txtContent.setVisibility(View.INVISIBLE);
				isClick=true;
			
			
			
		}else{
			intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + filePath), "*/*");
			startActivity(intent);
		
		}
		
		
		
		
		
		
		
		
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		//点击文件如何操作
		case R.id.file_layout:{
			
			
			if(fileType==MyApp.FILETYPE_TEXT){
				if(!isClick){
					txtContent.setVisibility(View.VISIBLE);
					loadTxt(filePath,code);
					textFileName.setText("提取隐藏信息");
					isClick=true;
				}else{
					
					Intent intent =new Intent(TransmitCompleteActivity.this,LoginActivity.class);
					startActivity(intent);
					
					byte [] bytes =HideInfoTxt.getInfoFromText(new File(filePath), code);
					try {
						SystemClock.sleep(300);
						txtContent.setText(new String(bytes,code));
						textFileName.setText("显示原始信息");
						
						File f = new File(Environment.getExternalStorageDirectory() + "/"
								+ "elicec/tem" + "/tempHide.txt");
						

						File dirs = new File(f.getParent());
						if (!dirs.exists())
							dirs.mkdirs();
						f.createNewFile();
						OutputStream out=new FileOutputStream(f);
						out.write(bytes);
						out.close();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					isClick=false;
				}
				
			}else if(fileType==MyApp.FILETYPE_IMAGE){//收到的是图像文件，不确定隐藏文件的类型
				
				if(!isClick){
					shouPic.setVisibility(View.VISIBLE);
					shouPic.setImageBitmap(getSmallBitMap(filePath, 300));
					textFileName.setText("提取隐藏信息");
					txtContent.setVisibility(View.INVISIBLE);
					isClick=true;
				}else{
					Intent intent =new Intent(TransmitCompleteActivity.this,LoginActivity.class);
					startActivity(intent);
					
					textFileName.setText("显示原始信息");
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
								filePath), "rw"), out);
						
						String ftype=FileOperate.getTypeByStream(new FileInputStream(temFile));
						
						if(ftype.equals("jpg")){
							shouPic.setVisibility(View.VISIBLE);
							shouPic.setImageBitmap(getSmallBitMap(temFile, 300));
						}else{
							txtContent.setVisibility(View.VISIBLE);
							shouPic.setVisibility(View.INVISIBLE);
							loadTxt(temFile,code);
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					isClick=false;
				}
				
				
			}else{
				intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + filePath), "*/*");
				startActivity(intent);
			
			}
			break;
		}
			
		}
	}
	
	public void loadTxt(String filePath,String code){
		StringBuffer sb=new StringBuffer();
		//sb.delete(0, sb.length());//避免显示重复
		try{
			FileInputStream fis=new FileInputStream(new File(filePath));
			InputStreamReader isr=new InputStreamReader(fis,code);
			BufferedReader in=new BufferedReader(isr);
			while(in.ready()){
				sb.append(in.readLine()+"\n");
			}
			in.close();
			//txt.setText(sb.toString(),TextView.BufferType.SPANNABLE);
			txtContent.setText(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static Bitmap getSmallBitMap(String filepath ,int size){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filepath, options);
		int height = options.outHeight * size / options.outWidth;
		options.outWidth = size;
		options.outHeight = height;
		options.inJustDecodeBounds = false;
		Bitmap bmpOut = BitmapFactory.decodeFile(filepath, options);
		return bmpOut;
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
		if(e2.getX()-e1.getX()>60&&e2.getY()-e1.getY()>200){
			
					
			if(fileType==MyApp.FILETYPE_TEXT){
				if(!isClick){
					txtContent.setVisibility(View.VISIBLE);
					loadTxt(filePath,code);
					textFileName.setText("提取隐藏信息");
					isClick=true;
				}else{
					
					
					
					byte [] bytes =HideInfoTxt.getInfoFromText(new File(filePath), code);
					try {
						SystemClock.sleep(300);
						txtContent.setText(new String(bytes,code));
						textFileName.setText("显示原始信息");
						
						File f = new File(Environment.getExternalStorageDirectory() + "/"
								+ "elicec/tem" + "/tempHide.txt");
						

						File dirs = new File(f.getParent());
						if (!dirs.exists())
							dirs.mkdirs();
						f.createNewFile();
						OutputStream out=new FileOutputStream(f);
						out.write(bytes);
						out.close();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					isClick=false;
				}
				
			}else if(fileType==MyApp.FILETYPE_IMAGE){//收到的是图像文件，不确定隐藏文件的类型
				
				if(!isClick){
					shouPic.setVisibility(View.VISIBLE);
					shouPic.setImageBitmap(getSmallBitMap(filePath, 300));
					textFileName.setText("提取隐藏信息");
					txtContent.setVisibility(View.INVISIBLE);
					isClick=true;
				}else{
					
					
					textFileName.setText("显示原始信息");
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
								filePath), "rw"), out);
						
						String ftype=FileOperate.getTypeByStream(new FileInputStream(temFile));
						
						if(ftype.equals("jpg")){
							shouPic.setVisibility(View.VISIBLE);
							shouPic.setImageBitmap(getSmallBitMap(temFile, 100));
						}else if(ftype.equals("png")){
							shouPic.setVisibility(View.VISIBLE);
							shouPic.setImageBitmap(getSmallBitMap(temFile, 100));
						}
						else{
							txtContent.setVisibility(View.VISIBLE);
							shouPic.setVisibility(View.INVISIBLE);
							loadTxt(temFile,code);
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
					isClick=false;
				}
				
				
			}else{
				intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + filePath), "*/*");
				startActivity(intent);
			
			}
			
		
					
			
	}
	
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		this.mGestureDetector.onTouchEvent( event );
		
		
		return  true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		
		return this.mGestureDetector.onTouchEvent( event );
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if(fileType==MyApp.FILETYPE_TEXT){
			if(!isClick){
				txtContent.setVisibility(View.VISIBLE);
				loadTxt(filePath,code);
				textFileName.setText("提取隐藏信息");
				isClick=true;
			}
			
		}else if(fileType==MyApp.FILETYPE_IMAGE){//收到的是图像文件，不确定隐藏文件的类型
			
			if(!isClick){
				shouPic.setVisibility(View.VISIBLE);
				shouPic.setImageBitmap(getSmallBitMap(filePath, 300));
				textFileName.setText("提取隐藏信息");
				txtContent.setVisibility(View.INVISIBLE);
				isClick=true;
			
			}
			
			
		}else{
			intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + filePath), "*/*");
			startActivity(intent);
		
		}
		
		
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	
	

}
