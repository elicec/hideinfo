package com.elicec.hide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.http.util.ByteArrayBuffer;

import com.elicec.activity.MainActivity;

import android.os.Environment;
import android.util.Log;

/**
 * 文本信息隐藏算法核心类
 * @author penghong
 * @version 20150315
 *
 */
public class HideInfoTxt {
	
	/**
	 * 返回一个文件的bit流，用String表示此bit流
	 * @param srcFileStream 文件流
	 * @param code 文件字符编码 一般为gb2312 utf-8或者 gbk编码
	 * @return
	 * 返回文件bit流
	 */
	
//	public static  int  testVal;
	public static StringBuffer getBitStringBuffer(InputStream srcFileStream ,String code){
		StringBuffer sb=new StringBuffer();
		byte [] filebytes=getFileBytes(srcFileStream, code);
		for(int i=0;i<filebytes.length;i++){
			sb.append(byteToBit(filebytes[i]));
		}
		return sb;
		
	}
	
/**
 * 隐藏系信息到目标文件
 * @param srcFileStream 源文件流
 * @param code 文件编码
 * @param modeFile 目标文件
 * @return 返回一个全新的文件流
 * @throws FileNotFoundException
 */
	public static InputStream hideInfo2Text(InputStream srcFileStream,String code,String modeFile) throws FileNotFoundException{
		StringBuffer sbSrc=new StringBuffer();	
		StringBuffer sbDes=new StringBuffer();
		
		sbSrc=getBitStringBuffer(srcFileStream, code);
		
		//获得模版文件流
//		testVal=sbSrc.length();
//		Log.v(MainActivity.TAG,testVal+"原始信息bit长度");
		
		try {
			InputStreamReader isr;
			isr = new InputStreamReader(new FileInputStream(new File(modeFile)),code);
			
			BufferedReader in=new BufferedReader(isr);
			while(in.ready()){
				sbDes.append(in.readLine()+"\n");
			}
			in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		int position =0;
		for (int j = 0; j < sbSrc.length(); j = j + 2) {//循环需要隐藏的文件，每次取俩位（j+2），因为没遇到一个标点，可以隐藏两位
			for (int i = position; i < sbDes.length(); i++) {//遍历目标文件，遇到标点进行操作隐藏
				if (sbDes.charAt(i) == '，' || sbDes.charAt(i) == '。') {//目前只取出现最多的逗号和句号，需要添加直接加 或条件
					if (sbSrc.charAt(j) == '0' && sbSrc.charAt(j + 1) == '0') {
						position = i+1;
						break;
					} else if (sbSrc.charAt(j) == '1'
							&& sbSrc.charAt(j + 1) == '0') {
						sbDes.insert(i, ' ');
						i++;
						position = i+1;
						break;
					} else if (sbSrc.charAt(j) == '0'
							&& sbSrc.charAt(j + 1) == '1') {
						sbDes.insert(i + 1, ' ');
						i++;
						position = i+1;
						break;
					} else if (sbSrc.charAt(j) == '1'
							&& sbSrc.charAt(j + 1) == '1') {
						sbDes.insert(i, ' ');
						i++;
						sbDes.insert(i + 1, ' ');
						i++;
						position = i+1;
						break;
					}
				}
			}
		}
		
		File f = new File(Environment.getExternalStorageDirectory() + "/"
				+ "elicec/tem" + "/tempHide.temp");
		

		File dirs = new File(f.getParent());
		if (!dirs.exists())
			dirs.mkdirs();
		if(f.exists())f.delete();
		try{
			f.createNewFile();
			OutputStream out=new FileOutputStream(f);
			out.write(sbDes.toString().getBytes());
			out.close();
		}catch(Exception e){
			e.printStackTrace();
			
		}
		return new FileInputStream(f);//返回操作玩之后的文件流，便于传输
		
		
		
		
	}
	/**
	 * 从隐藏信息的文本文件中获取隐藏的信息，过程为：判断标点符号前后有无空格，
	 * 有的取出一个‘1’，没有就取出‘0’
	 * 这样就形成了一个String｛01组成｝，然后解析这个01串，每八个生成一个byte。并重新编码成字符代码。
	 * 例如： ‘我’字的bit流为[11001110][11010010]
	 * @param file
	 * @param code
	 * @return
	 */
	public static byte[] getInfoFromText(File file,String code) {
		StringBuffer sbSrc = new StringBuffer();
		StringBuffer sbDes = new StringBuffer();

		try {
			InputStream is=new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is, code);
			BufferedReader in = new BufferedReader(isr);
			while (in.ready()) {
				sbSrc.append(in.readLine() + "\n");
			}
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		for(int i=0;i<sbSrc.length();i++){
			if(sbSrc.charAt(i)=='，'||sbSrc.charAt(i)=='。'){
				if(sbSrc.charAt(i-1)!=' '&&sbSrc.charAt(i+1)!=' '){
					sbDes.append('0');
					sbDes.append('0');
				}else if(sbSrc.charAt(i-1)==' '&&sbSrc.charAt(i+1)!=' '){
					sbDes.append('1');
					sbDes.append('0');
				}else if(sbSrc.charAt(i-1)!=' '&&sbSrc.charAt(i+1)==' '){
					sbDes.append('0');
					sbDes.append('1');
				}else if(sbSrc.charAt(i-1)==' '&&sbSrc.charAt(i+1)==' '){
					sbDes.append('1');
					sbDes.append('1');
				}
				//if (sbDes.length()==152)break;//测试用
			}
		}
		
		
		return bitStringTObyte(sbDes);

		
	}
	/**
	 *   得到输入文件的 byte[]。
	 * 
	 * @param srcFileStream     输入文件流
	 * @param code   文档编码
	 * @return       返回文件的byte[]
	 */
	
	public static byte[] getFileBytes(InputStream srcFileStream,String code){
		StringBuffer sb=new StringBuffer();
		byte[] bytes=new byte[1024*10];
		
		try{
			
			//InputStreamReader isr=new InputStreamReader(srcFileStream,code);
			//BufferedReader in=new BufferedReader(isr);
//			while(in.ready()){
//				sb.append(in.readLine()+"\n");
//			}
			//while(srcFileStream.read())
			srcFileStream.read(bytes);
			
			srcFileStream.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//return sb.toString().getBytes();
		return bytes;
		
	}
	/**
	 * 将位流转换为byte[]
	 * @param sb   输入的位流
	 * @return  返回一个byte[]
	 */
	
	public static byte[] bitStringTObyte(StringBuffer sb) {
		byte[] bytes = new byte[sb.length() / 8];
		int k, n = 0;
		char[] buff = new char[8];
		for (int i = 0, j = i + 8; i < sb.length(); i = i + 8, j = j + 8) {
			sb.getChars(i, j, buff, 0);
			if (buff[0] == '0')
				k = Integer.parseInt(new String(buff), 2);
			else
				k = Integer.parseInt(new String(buff), 2)-256;
			bytes[n++] = (byte) k;
			
		}
		return bytes;
	}
	
	/**
	 *    将byte转换为bit串
	 * @param b
	 * @return
	 */
	
	public static String byteToBit(byte b) {  
        return ""  
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)  
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)  
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)  
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);  
    }  

}
