package com.xxx.nio;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.xxx.util.MessageQueue;
import com.xxx.util.MessageQueueFactory;


public class ScanFileHandler implements Runnable{
	
	private static final Logger log = Logger.getLogger(ScanFileHandler.class);
	
	private String sourceDir;
	private String targetDir;
	private String pattern;
	private String fileName;
	
	public ScanFileHandler(String _sourcePath,String _targetPath,String _pattern,String _fileName){
		this.sourceDir = _sourcePath;
		this.targetDir = _targetPath;
		this.pattern = _pattern;
		this.fileName = _fileName;
	}

	@Override
	public void run() {
		Path sourcePath = Paths.get(sourceDir);
		
		final Pattern patternJ = Pattern.compile(pattern);
		
		MessageQueue mq = MessageQueueFactory.getInstance().getMessageQueue(MessageQueueFactory.queueType.FILE);
		
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			public boolean accept(Path file) throws IOException {
					if (patternJ.matcher(file.getFileName().toString()).matches()) {
						return true;
					}
				return false;
			}
		};
		
		while(true){
			try (DirectoryStream<Path> ds = Files.newDirectoryStream(sourcePath, filter)) {
				for (Path fp : ds) {
					if(mq.isFull()){
						try {
							TimeUnit.SECONDS.sleep(3);
						} catch (InterruptedException e) {
						}
					}
					Path targetPath = Paths.get(targetDir + File.separator + fileName + "." + System.nanoTime());
					Files.move(fp, targetPath, StandardCopyOption.REPLACE_EXISTING);
					mq.push(targetPath);
				}
			} catch (Exception e) {
				log.error("文件移动异常" + e);
			}
		}
	}

}
