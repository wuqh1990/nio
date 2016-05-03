package com.xxx.nio;

import java.nio.file.Path;


import com.xxx.util.NIORandomAccessFile2;

public class ReadFileThread2 implements Runnable {
	
	private long startPosititon;
	private long endPosition;
	private Path path;
	private String mode;
	private int num;
	

	public ReadFileThread2(long startPosititon, long endPosition, Path path,String mode,int _num) {
		super();
		this.startPosititon = startPosititon;
		this.endPosition = endPosition;
		this.path = path;
		this.mode = mode;
		this.num=_num;
	}

	
	@Override
	public void run() {
		try (NIORandomAccessFile2 nio = new NIORandomAccessFile2(path,mode)){
			nio.seek(startPosititon);
			nio.setEndPoint(endPosition);
			System.out.println("线程"+num+"(startPosititon:"+startPosititon+",endPosition:"+endPosition+")");
			while (true) {
				String line = nio.readLine();
				if(null==line){
					break;
				}
				if("".equals(line)){
					continue;
				}
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
