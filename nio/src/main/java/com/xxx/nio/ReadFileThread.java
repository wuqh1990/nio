package com.xxx.nio;

import java.nio.file.Path;

import org.apache.log4j.Logger;


import com.xxx.util.MessageQueue;
import com.xxx.util.MessageQueueFactory;
import com.xxx.util.NIORandomAccessFile;

public class ReadFileThread implements Runnable {
	private static final Logger log = Logger.getLogger(ReadFileThread.class);

	
	@Override
	public void run() {
		while(true){
			MessageQueue mq = MessageQueueFactory.getInstance().getMessageQueue(MessageQueueFactory.queueType.FILE);
			try {
				Path path = (Path) mq.pop();
				readFile(path);
			} catch (Exception e) {
				log.error("获取文件/文件处理出错" + e);
			}
		}
	}
	
	public void readFile(Path path){
		try (NIORandomAccessFile nio = new NIORandomAccessFile(path,"rw")){
			while (true) {
				String line = nio.readLine();
				if(null==line){
					break;
				}
				if("".equals(line)){
					continue;
				}
				log.info(line);
			}
		} catch (Exception e) {
			log.error("文件读取异常",e);
		}
	}
}
