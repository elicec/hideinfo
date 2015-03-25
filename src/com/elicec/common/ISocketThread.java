package com.elicec.common;

public interface ISocketThread {
	/**
	 * 线程接受玩数据的回调，
	 * @param dataType
	 * 0代表文本文件，1代表图像，2代表其他文件
	 */
	 void getData(int dataType,String pathFile );
	 
	 
}
