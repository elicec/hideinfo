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
 * �ı���Ϣ�����㷨������
 * @author penghong
 * @version 20150315
 *
 */
public class HideInfoTxt {
	
	/**
	 * ����һ���ļ���bit������String��ʾ��bit��
	 * @param srcFileStream �ļ���
	 * @param code �ļ��ַ����� һ��Ϊgb2312 utf-8���� gbk����
	 * @return
	 * �����ļ�bit��
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
 * ����ϵ��Ϣ��Ŀ���ļ�
 * @param srcFileStream Դ�ļ���
 * @param code �ļ�����
 * @param modeFile Ŀ���ļ�
 * @return ����һ��ȫ�µ��ļ���
 * @throws FileNotFoundException
 */
	public static InputStream hideInfo2Text(InputStream srcFileStream,String code,String modeFile) throws FileNotFoundException{
		StringBuffer sbSrc=new StringBuffer();	
		StringBuffer sbDes=new StringBuffer();
		
		sbSrc=getBitStringBuffer(srcFileStream, code);
		
		//���ģ���ļ���
//		testVal=sbSrc.length();
//		Log.v(MainActivity.TAG,testVal+"ԭʼ��Ϣbit����");
		
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
		for (int j = 0; j < sbSrc.length(); j = j + 2) {//ѭ����Ҫ���ص��ļ���ÿ��ȡ��λ��j+2������Ϊû����һ����㣬����������λ
			for (int i = position; i < sbDes.length(); i++) {//����Ŀ���ļ������������в�������
				if (sbDes.charAt(i) == '��' || sbDes.charAt(i) == '��') {//Ŀǰֻȡ�������Ķ��ź;�ţ���Ҫ���ֱ�Ӽ� ������
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
		return new FileInputStream(f);//���ز�����֮����ļ��������ڴ���
		
		
		
		
	}
	/**
	 * ��������Ϣ���ı��ļ��л�ȡ���ص���Ϣ������Ϊ���жϱ�����ǰ�����޿ո�
	 * �е�ȡ��һ����1����û�о�ȡ����0��
	 * �������γ���һ��String��01��ɣ���Ȼ��������01����ÿ�˸�����һ��byte�������±�����ַ����롣
	 * ���磺 ���ҡ��ֵ�bit��Ϊ[11001110][11010010]
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
			if(sbSrc.charAt(i)=='��'||sbSrc.charAt(i)=='��'){
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
				//if (sbDes.length()==152)break;//������
			}
		}
		
		
		return bitStringTObyte(sbDes);

		
	}
	/**
	 *   �õ������ļ��� byte[]��
	 * 
	 * @param srcFileStream     �����ļ���
	 * @param code   �ĵ�����
	 * @return       �����ļ���byte[]
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
	 * ��λ��ת��Ϊbyte[]
	 * @param sb   �����λ��
	 * @return  ����һ��byte[]
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
	 *    ��byteת��Ϊbit��
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
