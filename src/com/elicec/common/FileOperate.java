package com.elicec.common;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import android.R;
import android.util.Log;

public class FileOperate {

	public static String getFileOrFilesSize(String filePath) {
		File file = new File(filePath);
		long blockSize = 0;
		try {

			blockSize = getFileSize(file);

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("��ȡ�ļ���С", "��ȡʧ��!");
		}
		return FormetFileSize(blockSize);
	}

	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			// file.createNewFile();
			Log.e("��ȡ�ļ���С", "�ļ�������!");
		}
		return size;
	}

	private static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("0.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		fileSizeString = df.format((double) fileS / 1024);// kb
		return fileSizeString;
	}
	
	/**
	 * �ֽ�ת��Ϊ16�����ַ���
	 */
	public static String bytesToHexString(byte[] src){      
        StringBuilder stringBuilder = new StringBuilder();      
        if (src == null || src.length <= 0) {      
            return null;      
        }      
        for (int i = 0; i < src.length; i++) {      
            int v = src[i] & 0xFF;      
            String hv = Integer.toHexString(v);      
            if (hv.length() < 2) {      
                stringBuilder.append(0);      
            }      
            stringBuilder.append(hv);      
        }      
        return stringBuilder.toString();      
    } 
	
	 /**
	  * �����ļ�����ȡͼƬ�ļ���ʵ����
	  * @param is �ļ��� FileInputStream
	  * @return
	 * @throws IOException 
	  */
	public static boolean isBmp(InputStream is) throws IOException {
		byte[] b = new byte[4];
		try {
			is.read(b, 0, b.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String type = bytesToHexString(b).toUpperCase();

		if (type.contains("424D")) {
			is.close();
			return true;
			
		}else{
			is.close();
			return false;	
		}
		
	}
/**
 * �ж��Ƿ�Ϊ�ı�txt(��bug ��ʱ����)
 * @param sFileName
 * @return
 * @throws FileNotFoundException 
 */
	public static boolean isTxt(String sFileName) throws FileNotFoundException

    {
         boolean   xx=false;     

         FileInputStream fs = new  FileInputStream(new File(sFileName)); ;
         byte[] buffer=new byte[4];
         try
         {
             fs.read(buffer,0,4);
         } 
         catch   (IOException e) 
         { 
        	 e.printStackTrace();
         }
         String type = bytesToHexString(buffer).toUpperCase();
         return false;


    }
	
	/**
	 * �ж�һ���ļ��Ƿ�Ϊbmpͼ��,ԭ����ȡ�ļ���ǰ�ĸ��ֽڣ�ת��Ϊ16�����ַ�����
	 * ����ַ����а��� 424D����ô���ļ�һ����bmpͼ���ļ�
	 * @param file ��Ҫ�жϵ��ļ�
	 * @return �����bmp����true�����򷵻�false
	 * @throws IOException �ļ����δ�ҵ�
	 */
	public static boolean  isBmp(File file) throws IOException {
		byte[] cmdBytes = new byte[4];
		String cmdString = null;
		int readCount = 0;
		FileInputStream inputstream = new FileInputStream(file);
		readCount = inputstream.read(cmdBytes);
		if (readCount == -1)
			return false;
		cmdString = FileOperate.bytesToHexString(cmdBytes).toUpperCase();
		// 424dΪbmp�ļ�
		if (cmdString.contains("424D"))
			return true;
		else
			return false;

	}
	
	public static String getDateForfilename(){
		Calendar c = Calendar.getInstance();

		long time = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
		Date d1 = new Date(time);
		String t1 = format.format(d1);
		return t1;		
	}
	
	/**  
     * ���Ƶ����ļ�  
     * @param oldPath String ԭ�ļ�·�� �磺c:/fqf.txt  
     * @param newPath String ���ƺ�·�� �磺f:/fqf.txt  
     * @return boolean  
     */   
   public static void copyFile(String oldPath, String newPath) {   
       try {   
           int bytesum = 0;   
           int byteread = 0;   
           File oldfile = new File(oldPath);   
           if (oldfile.exists()) { //�ļ�����ʱ   
               InputStream inStream = new FileInputStream(oldPath); //����ԭ�ļ�   
               FileOutputStream fs = new FileOutputStream(newPath);   
               byte[] buffer = new byte[1444];   
               int length;   
               while ( (byteread = inStream.read(buffer)) != -1) {   
                   bytesum += byteread; //�ֽ��� �ļ���С   
                  // System.out.println(bytesum);   
                   fs.write(buffer, 0, byteread);   
               }   
               fs.close();
               inStream.close();   
           }   
       }   
       catch (Exception e) {   
           System.out.println("���Ƶ����ļ���������");   
           e.printStackTrace();   
  
       }   
   }  
   
   /**
    * �����ļ�����ȡͼƬ�ļ���ʵ����
    * @param is
    * @return
 * @throws IOException 
    */
   public static String getTypeByStream(FileInputStream is) throws IOException{
       byte[] b = new byte[10];   
          try {
     is.read(b, 0, b.length);
    } catch (IOException e) {
     e.printStackTrace();
    }
          String type = bytesToHexString(b).toUpperCase();
          if(type.contains("FFD8FF")){
           return "jpg";
          }else if(type.contains("89504E47")){
           return "png";
          }else if(type.contains("47494638")){
           return "gif";
          }else if(type.contains("49492A00")){
           return "tif";
          }else if(type.contains("424D")){
           return "bmp";
          }
          is.close();
          return type;
      }
   
 
}
