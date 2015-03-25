package com.elicec.hide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import com.elicec.common.MyApp;
import com.elicec.home.FileTransferService;

/**
 * ͼ�������㷨�����ࡣԭ��LSB�㷨������Ҫ���ص��ļ�ת��Ϊbit����ÿ���������ص����ص������Чλ������
 * ͼ��ĸı�ǳ�΢С�������޷���������Ҫ���ص����ݰ��������֣�1���ļ��Ĵ�С����Ϊ��ȡ��Ϣʱ������֪���ļ��Ĵ�С����
 * ��ȡ������Ϣ��2���ļ���ʵ�����ݣ�������byte�������������ȡԭ��������ȡ���ļ��Ĵ�С������������ȡ�ļ�ʵ������ѭ����
 * �߽�������֪���������ļ��Ĵ�С�󣬾Ϳ��Ը��ݴ�С�޶���Χ����ȡԴ�ļ�ʵ������
 * @author penghong
 *
 */

public class HideInfoBmp {
	private InputStream mInputStream;//�����ļ������������ڹ��췽���г�ʼ��
	private RandomAccessFile mModeFileStream;//ģ���ļ�������ļ���д������Ϊ��Դ�ļ���Ҫ���ж���ҲҪͬʱ����д������������ļ���д���������ڷ����������λ�á�
	private static long filelength;
/**
 * ���췽����
 * @param is  �����ļ���
 * @param mModefileStream ģ���ļ���
 * @throws IOException
 */
	public HideInfoBmp(InputStream is, RandomAccessFile mModefileStream) throws IOException {
		this.mInputStream = is;
		this.mModeFileStream = mModefileStream;
		

	}
/**
 * ��long��ת��Ϊ ������
 * @param x
 * @return
 */
	private byte[] ConvertToBinaryArray(long x) {
		byte[] binaryArray = new byte[24];
		for (int i = 0; i != 23; i++) {
			binaryArray[23 - i] = (byte) (x & 1);
			x = x >> 1;
		}
		return binaryArray;
	}
/**
 * ��byte תΪ������
 * @param array
 * @return
 */
	private byte[] ConvertToBinaryArray(byte[] array) {
		byte[] binaryArray = new byte[24];
		int a = array[0];
		int b = array[1];
		int c = array[2];
		for (int i = 0; i != 8; i++) {
			binaryArray[7 - i] = (byte) (a & 1);
			a = a >> 1;
		}
		for (int i = 0; i != 8; i++) {
			binaryArray[15 - i] = (byte) (b & 1);
			b = b >> 1;
		}
		for (int i = 0; i != 8; i++) {
			binaryArray[23 - i] = (byte) (c & 1);
			c = c >> 1;
		}
		return binaryArray;
	}
	/**
	 * �����ļ��Ĵ�С��ͼ����ȥ
	 * @throws IOException
	 */

	public void HideInfoLength() throws IOException {
		byte[] picBlock = new byte[12];
		// ��ȡԭʼͼ��ĵ�55����66�ֽڵ����ݿ�
		// mModeFileInputStream.skip(54);
		mModeFileStream.seek(54);

		mModeFileStream.read(picBlock, 0, picBlock.length);
		getInputStreamSize(mInputStream);
		byte[] lenArray = ConvertToBinaryArray(filelength);
		// ���������ļ��ĳ�����Ϣ
		int index = 0;
		for (int i = 0; i < 4; i++) {
			// ��a7��a6λ
			picBlock[i * 3] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3] & 253)
					: (picBlock[i * 3] | 2));
			picBlock[i * 3] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3] & 254)
					: (picBlock[i * 3] | 1));
			// ��a5λ
			picBlock[i * 3 + 1] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3 + 1] & 254)
					: (picBlock[i * 3 + 1] | 1));
			// ��a4,a3,a2λ
			picBlock[i * 3 + 2] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3 + 2] & 251)
					: (picBlock[i * 3 + 2] | 4));
			picBlock[i * 3 + 2] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3 + 2] & 253)
					: (picBlock[i * 3 + 2] | 2));
			picBlock[i * 3 + 2] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3 + 2] & 254)
					: (picBlock[i * 3 + 2] | 1));
		}
		// ��ԭʼ�ļ����ض�λ����55�ֽڴ�������Ƕ�볤����Ϣ��12�ֽڿ�д��
		mModeFileStream.seek(54);
		mModeFileStream.write(picBlock);

	}

	/**
	 * ��������Ϣ��ÿ3���ֽ�д��ԭʼͼ��ӵ�67�ֽڿ�ʼ��ÿ12�ֽڿ��LSB��
	 * @throws IOException
	 */
	public void HideInfoContent() throws IOException {
		int readCnt = 0;
		// ����ѭ������Ĵ���
		//long infoLen = mInputStream.available();
		long infoLen = filelength;
		
		int cnt = (int) (infoLen % 3 == 0 ? infoLen / 3 : infoLen / 3 + 1);
		
		//�������ļ�
		
		InputStream mInputStreamChongzhi=new FileInputStream(new File(MyApp.FILE_PATH+"temp.temp"));
		// _picStream.Position = 66;
		mModeFileStream.seek(66);
		for (int i = 0; i < cnt; i++) {
			// ÿ��ѭ����ȡBMPͼ�����һ��12�ֽڵ�����
			byte[] picBlock = new byte[12];
			readCnt = mModeFileStream.read(picBlock, 0, picBlock.length);
			// ��ȡ��������Ϣ����һ��3�ֽ�����
			byte[] readBuffer = new byte[3];
			
			
			
			
			//mInputStream.read(readBuffer, 0, readBuffer.length);
			mInputStreamChongzhi.read(readBuffer, 0, readBuffer.length);
			byte[] infoBlock = ConvertToBinaryArray(readBuffer);
			// ��λ����
			int index = 0;
			for (int ii = 0; ii < 4; ii++) {
				// ��a7��a6λ
				picBlock[ii * 3] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3] & 253)
						: (picBlock[ii * 3] | 2));
				picBlock[ii * 3] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3] & 254)
						: (picBlock[ii * 3] | 1));
				// ��a5λ
				picBlock[ii * 3 + 1] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3 + 1] & 254)
						: (picBlock[ii * 3 + 1] | 1));
				// ��a4,a3,a2λ
				picBlock[ii * 3 + 2] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3 + 2] & 251)
						: (picBlock[ii * 3 + 2] | 4));
				picBlock[ii * 3 + 2] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3 + 2] & 253)
						: (picBlock[ii * 3 + 2] | 2));
				picBlock[ii * 3 + 2] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3 + 2] & 254)
						: (picBlock[ii * 3 + 2] | 1));
			}
			// mModeFileStream.Position -= readCnt;
			mModeFileStream.seek(mModeFileStream.getFilePointer() - readCnt);
			mModeFileStream.write(picBlock, 0, picBlock.length);

		}
	}

	/**
	 * �õ���Ϣ������Ϣ�Ĵ�С
	 * 
	 * @throws IOException
	 */

	private static int GetInfoLength(RandomAccessFile mRandFileStream)
			throws IOException {
		// ���ļ�����λ����55���ֽڴ�
		mRandFileStream.seek(54);
		// ��ȡ����������Ϣ���ȵ�12���ֽڿ�
		byte[] lengthBlock = new byte[12];
		mRandFileStream.read(lengthBlock, 0, lengthBlock.length);
		// ��ȡLSB
		byte[] lengthBitArray = ExtractHidingBits(lengthBlock);
		// ��ԭ�����ͳ���ֵ
		int len = lengthBitArray[0] * 65536 + lengthBitArray[1] * 256
				+ lengthBitArray[2];
		// ������Ȳ���ȷ������ͼƬ������������Ϣ���˼��LSB���������������������������ļ���㷨δ֪
		if (len <= 0 || len > (mRandFileStream.length() - 54) / 4 - 3) {
			mRandFileStream.close();
			return -1;
		}
		return (len);
	}

	/**
	 * ����λ������ȡαװ�ļ�����ÿ12�ֽڵ�LSBλ
	 * @param arr
	 * @return
	 */
	private static byte[] ExtractHidingBits(byte[] arr) {
		// ���ڴ�Ŵ�12���ֽڿ�����ȡ������3���ֽڵ�������Ϣ
		byte[] buf = new byte[3];
		// 24��bitλ�Ĵ���
		buf[0] = (byte) ((arr[0] & 2) == 0 ? (buf[0] & 127) : (buf[0] | 128)); // a7
		buf[0] = (byte) ((arr[0] & 1) == 0 ? (buf[0] & 191) : (buf[0] | 64)); // a6
		buf[0] = (byte) ((arr[1] & 1) == 0 ? (buf[0] & 223) : (buf[0] | 32)); // a5
		buf[0] = (byte) ((arr[2] & 4) == 0 ? (buf[0] & 239) : (buf[0] | 16)); // a4
		buf[0] = (byte) ((arr[2] & 2) == 0 ? (buf[0] & 247) : (buf[0] | 8)); // a3
		buf[0] = (byte) ((arr[2] & 1) == 0 ? (buf[0] & 251) : (buf[0] | 4)); // a2
		buf[0] = (byte) ((arr[3] & 2) == 0 ? (buf[0] & 253) : (buf[0] | 2)); // a1
		buf[0] = (byte) ((arr[3] & 1) == 0 ? (buf[0] & 254) : (buf[0] | 1)); // a0

		buf[1] = (byte) ((arr[4] & 1) == 0 ? (buf[1] & 127) : (buf[1] | 128)); // b7
		buf[1] = (byte) ((arr[5] & 4) == 0 ? (buf[1] & 191) : (buf[1] | 64)); // b6
		buf[1] = (byte) ((arr[5] & 2) == 0 ? (buf[1] & 223) : (buf[1] | 32)); // b5
		buf[1] = (byte) ((arr[5] & 1) == 0 ? (buf[1] & 239) : (buf[1] | 16)); // b4
		buf[1] = (byte) ((arr[6] & 2) == 0 ? (buf[1] & 247) : (buf[1] | 8)); // b3
		buf[1] = (byte) ((arr[6] & 1) == 0 ? (buf[1] & 251) : (buf[1] | 4)); // b2
		buf[1] = (byte) ((arr[7] & 1) == 0 ? (buf[1] & 253) : (buf[1] | 2)); // b1
		buf[1] = (byte) ((arr[8] & 4) == 0 ? (buf[1] & 254) : (buf[1] | 1)); // b0

		buf[2] = (byte) ((arr[8] & 2) == 0 ? (buf[2] & 127) : (buf[2] | 128)); // c7
		buf[2] = (byte) ((arr[8] & 1) == 0 ? (buf[2] & 191) : (buf[2] | 64)); // c6
		buf[2] = (byte) ((arr[9] & 2) == 0 ? (buf[2] & 223) : (buf[2] | 32)); // c5
		buf[2] = (byte) ((arr[9] & 1) == 0 ? (buf[2] & 239) : (buf[2] | 16)); // c4
		buf[2] = (byte) ((arr[10] & 1) == 0 ? (buf[2] & 247) : (buf[2] | 8)); // c3
		buf[2] = (byte) ((arr[11] & 4) == 0 ? (buf[2] & 251) : (buf[2] | 4)); // c2
		buf[2] = (byte) ((arr[11] & 2) == 0 ? (buf[2] & 253) : (buf[2] | 2)); // c1
		buf[2] = (byte) ((arr[11] & 1) == 0 ? (buf[2] & 254) : (buf[2] | 1)); // c0

		return buf;
	}

	/**
	 * ��ȡ��Ϣ��������������ȡ��С��Ȼ���ô�С������ȡ���ݡ�
	 * @param mRandFileStream
	 * @param outFile
	 * @return
	 * @throws IOException
	 */
	public static boolean ExecuteDecrypt(RandomAccessFile mRandFileStream,
			OutputStream outFile) throws IOException {
		int infoLen = GetInfoLength(mRandFileStream);//��ȡ������ͼ���е��ļ���С
		if (infoLen == -1) {
			return false;
		}
		// ���ļ���λ��
		// _camouflageStream.Position = 66;
		mRandFileStream.seek(66);
		// ����ѭ������
		int cycle = (infoLen % 3 == 0) ? (infoLen / 3) : (infoLen / 3 + 1);
		// ��12�ֽ�һ��ÿ����ȡ3���ֽڵ�������Ϣ��д���ļ�
		for (int i = 0; i < cycle; i++) {
			byte[] contentBlock = new byte[12];
			mRandFileStream.read(contentBlock, 0, contentBlock.length);
			byte[] contentBitArray = ExtractHidingBits(contentBlock);
			outFile.write(contentBitArray);
		}
		outFile.close();
		mRandFileStream.close();

		return true;
	}
	/**
	 * ��������������ļ��Ĵ�С������������������������� �γ���ʱ�ļ����ڶ��ļ��������С
	 * @param is
	 * @throws IOException
	 */
	
	public static void getInputStreamSize(InputStream is) throws IOException{
		long len;
		File temp=new File(MyApp.FILE_PATH+"temp.temp");
		File dirs = new File(temp.getParent());
		if(temp.exists())temp.delete();
		if (!dirs.exists())
			dirs.mkdirs();
		
		temp.createNewFile();
		InputStream istemp=is;
		OutputStream fos=new FileOutputStream(temp);
		FileTransferService.copyFile(istemp, fos);
		filelength= temp.length();
		
	}

}
