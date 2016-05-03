package com.xxx;

import com.xxx.nio.ReadFileThread;
import com.xxx.nio.ScanFileHandler;

public class ReadFileMain {

	public static void main(String[] args){
		
		String sourceDir = args[0]; //日志文件源路径
		String targetDir = args[1]; //日志文件目标路径
		String fileName  = args[2]; //日志文件名
		String pattern   = args[3]; //  可读取日志文件的正则 [\\w]+.log.[\\d]+
		int threadNum    = Integer.parseInt(args[4]);

		Thread[] threads = new Thread[threadNum];
		for(int i=0;i<threadNum;i++){
			threads[i] = new Thread(new ReadFileThread());
		}

		for(int j=0;j<threadNum;j++){
			threads[j].start();
		}
		
		ScanFileHandler scanFileHandler =  new ScanFileHandler(sourceDir,targetDir,pattern,fileName);
		Thread scanThread = new Thread(scanFileHandler);
		scanThread.start();
	}

}
