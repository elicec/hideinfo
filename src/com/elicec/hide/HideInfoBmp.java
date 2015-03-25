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
 * 图像隐藏算法核心类。原理：LSB算法，将需要隐藏的文件转换为bit流，每个比特隐藏到像素的最低有效位，这样
 * 图像的改变非常微小，肉眼无法看出。需要隐藏的内容包括两部分，1，文件的大小（因为提取信息时，必须知道文件的大小才能
 * 提取出来信息）2，文件的实际内容（二进制byte层面操作）。提取原理：首先提取出文件的大小，用作后续提取文件实际内容循环的
 * 边界条件。知道了隐藏文件的大小后，就可以根据大小限定范围，提取源文件实际内容
 * @author penghong
 *
 */

public class HideInfoBmp {
	private InputStream mInputStream;//隐藏文件的输入流，在构造方法中初始化
	private RandomAccessFile mModeFileStream;//模版文件的随机文件读写流，因为对源文件既要进行读，也要同时进行写，必须用随机文件读写技术，便于方便控制流的位置。
	private static long filelength;
/**
 * 构造方法，
 * @param is  隐藏文件流
 * @param mModefileStream 模版文件流
 * @throws IOException
 */
	public HideInfoBmp(InputStream is, RandomAccessFile mModefileStream) throws IOException {
		this.mInputStream = is;
		this.mModeFileStream = mModefileStream;
		

	}
/**
 * 将long型转换为 二进制
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
 * 将byte 转为二进制
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
	 * 隐藏文件的大小到图像中去
	 * @throws IOException
	 */

	public void HideInfoLength() throws IOException {
		byte[] picBlock = new byte[12];
		// 读取原始图像的第55至第66字节的内容块
		// mModeFileInputStream.skip(54);
		mModeFileStream.seek(54);

		mModeFileStream.read(picBlock, 0, picBlock.length);
		getInputStreamSize(mInputStream);
		byte[] lenArray = ConvertToBinaryArray(filelength);
		// 置入隐藏文件的长度信息
		int index = 0;
		for (int i = 0; i < 4; i++) {
			// 置a7，a6位
			picBlock[i * 3] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3] & 253)
					: (picBlock[i * 3] | 2));
			picBlock[i * 3] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3] & 254)
					: (picBlock[i * 3] | 1));
			// 置a5位
			picBlock[i * 3 + 1] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3 + 1] & 254)
					: (picBlock[i * 3 + 1] | 1));
			// 置a4,a3,a2位
			picBlock[i * 3 + 2] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3 + 2] & 251)
					: (picBlock[i * 3 + 2] | 4));
			picBlock[i * 3 + 2] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3 + 2] & 253)
					: (picBlock[i * 3 + 2] | 2));
			picBlock[i * 3 + 2] = (byte) ((lenArray[index++] == 0) ? (picBlock[i * 3 + 2] & 254)
					: (picBlock[i * 3 + 2] | 1));
		}
		// 将原始文件流重定位到第55字节处并将已嵌入长度信息的12字节块写回
		mModeFileStream.seek(54);
		mModeFileStream.write(picBlock);

	}

	/**
	 * 将隐藏信息以每3个字节写入原始图像从第67字节开始的每12字节块的LSB中
	 * @throws IOException
	 */
	public void HideInfoContent() throws IOException {
		int readCnt = 0;
		// 计算循环处理的次数
		//long infoLen = mInputStream.available();
		long infoLen = filelength;
		
		int cnt = (int) (infoLen % 3 == 0 ? infoLen / 3 : infoLen / 3 + 1);
		
		//重置流文件
		
		InputStream mInputStreamChongzhi=new FileInputStream(new File(MyApp.FILE_PATH+"temp.temp"));
		// _picStream.Position = 66;
		mModeFileStream.seek(66);
		for (int i = 0; i < cnt; i++) {
			// 每次循环读取BMP图像的下一个12字节的内容
			byte[] picBlock = new byte[12];
			readCnt = mModeFileStream.read(picBlock, 0, picBlock.length);
			// 读取待隐藏信息的下一个3字节内容
			byte[] readBuffer = new byte[3];
			
			
			
			
			//mInputStream.read(readBuffer, 0, readBuffer.length);
			mInputStreamChongzhi.read(readBuffer, 0, readBuffer.length);
			byte[] infoBlock = ConvertToBinaryArray(readBuffer);
			// 置位操作
			int index = 0;
			for (int ii = 0; ii < 4; ii++) {
				// 置a7，a6位
				picBlock[ii * 3] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3] & 253)
						: (picBlock[ii * 3] | 2));
				picBlock[ii * 3] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3] & 254)
						: (picBlock[ii * 3] | 1));
				// 置a5位
				picBlock[ii * 3 + 1] = (byte) ((infoBlock[index++] == 0) ? (picBlock[ii * 3 + 1] & 254)
						: (picBlock[ii * 3 + 1] | 1));
				// 置a4,a3,a2位
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
	 * 得到信息隐藏信息的大小
	 * 
	 * @throws IOException
	 */

	private static int GetInfoLength(RandomAccessFile mRandFileStream)
			throws IOException {
		// 将文件流定位到第55个字节处
		mRandFileStream.seek(54);
		// 读取包含隐藏信息长度的12个字节块
		byte[] lengthBlock = new byte[12];
		mRandFileStream.read(lengthBlock, 0, lengthBlock.length);
		// 提取LSB
		byte[] lengthBitArray = ExtractHidingBits(lengthBlock);
		// 还原出整型长度值
		int len = lengthBitArray[0] * 65536 + lengthBitArray[1] * 256
				+ lengthBitArray[2];
		// 如果长度不正确表明该图片不含有隐藏信息，此检测LSB方法不能适用于所有情况，具体的检测算法未知
		if (len <= 0 || len > (mRandFileStream.length() - 54) / 4 - 3) {
			mRandFileStream.close();
			return -1;
		}
		return (len);
	}

	/**
	 * 利用位操作提取伪装文件流中每12字节的LSB位
	 * @param arr
	 * @return
	 */
	private static byte[] ExtractHidingBits(byte[] arr) {
		// 用于存放从12个字节块中提取出来的3个字节的隐藏信息
		byte[] buf = new byte[3];
		// 24个bit位的处理
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
	 * 提取信息，分两部，先提取大小，然后用大小控制提取内容。
	 * @param mRandFileStream
	 * @param outFile
	 * @return
	 * @throws IOException
	 */
	public static boolean ExecuteDecrypt(RandomAccessFile mRandFileStream,
			OutputStream outFile) throws IOException {
		int infoLen = GetInfoLength(mRandFileStream);//提取隐藏在图像中的文件大小
		if (infoLen == -1) {
			return false;
		}
		// 置文件流位置
		// _camouflageStream.Position = 66;
		mRandFileStream.seek(66);
		// 计算循环次数
		int cycle = (infoLen % 3 == 0) ? (infoLen / 3) : (infoLen / 3 + 1);
		// 按12字节一组每次提取3个字节的隐藏信息并写入文件
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
	 * 根据输入流获得文件的大小。方法：将输入流存入输出流 形成临时文件，在对文件进行求大小
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
