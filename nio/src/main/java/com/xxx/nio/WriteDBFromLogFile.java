package com.xxx.nio;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.xxx.util.NIORandomAccessFile2;


public class WriteDBFromLogFile implements Job {

	public long[] getPartitions(Path p, int mod) {
		long[] partitions = new long[mod];
		try (NIORandomAccessFile2 nio = new NIORandomAccessFile2(p, "rw")) {
			System.out.println(nio.size());
			partitions[0] = 0;
			long partition = (nio.size() / mod);
			long startPoint;
			for (int i = 1; i < mod; i++) {
				startPoint = nio.lastestPosWith(partition * i);
				partitions[i] = startPoint;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return partitions;

	}

	public void readFile(String filePath,int mod) throws InterruptedException {
		long[] partitions = getPartitions(Paths.get(filePath),mod);
		Path p = Paths.get(filePath);
		ReadFileThread2[] readFileThreads = new ReadFileThread2[partitions.length];
		Thread[] threads = new Thread[partitions.length];
		for(int i=0;i<partitions.length;i++){
			if(i==(partitions.length-1)){
				readFileThreads[i] = new ReadFileThread2(partitions[i],0,p,"rw",i);
			}else{
				readFileThreads[i] = new ReadFileThread2(partitions[i],partitions[i+1],p,"rw",i);
			}
			threads[i]=new Thread(readFileThreads[i]);
		}
		for(int j=0;j<threads.length;j++){
			threads[j].start();
		}
	}

	public static void main(String[] args) throws Exception {
		WriteDBFromLogFile wr = new WriteDBFromLogFile();
		wr.readFile(args[0],Integer.parseInt(args[1]));
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		try {
			readFile("F:\\test.log",128);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
