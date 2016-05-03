/**  
 * @since JDK 1.7
 */
package com.xxx.util;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import org.apache.log4j.Logger;

/**
 * @author Bradish7Y
 * @version
 */
public class NIORandomAccessFile2 implements Closeable {

	private static final Logger L = Logger.getLogger(NIORandomAccessFile.class);
	private RandomAccessFile randomAccessFile = null;
	private FileChannel fChannel = null;
	private ByteBuffer NIOByteBuffer = null;
	private static final int CAPACITY = 64*1024;// 64K
	private int lineSize = 1024;
	private Path path = null;
	private String mode = null;

	private long offset = 0;
	private boolean eol = false;
	private ByteBuffer buffer = ByteBuffer.allocate(lineSize);
	private String line = null;
	private boolean endFlag = false;
	private long endPoint = 0;

	public NIORandomAccessFile2(Path path, String mode) throws IOException {
		this.path = path;
		this.mode = mode;
		randomAccessFile = new RandomAccessFile(this.path.toString(), this.mode);
		fChannel = randomAccessFile.getChannel();
		NIOByteBuffer = ByteBuffer.allocate(CAPACITY);
		endPoint = fChannel.size();
	}

	/**
	 * 
	 * readLine:NIO 按行读取文件. <br/>
	 * 
	 * @return
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		if(endFlag){
			return null;
		}
		if (eol == false) {
			NIOByteBuffer.clear();
			if (!read0()) {
				return null;
			}
			NIOByteBuffer.flip();
		}
		
		byte ch = '\b';
		eol = false;
		long length = NIOByteBuffer.limit();
		for (long i = NIOByteBuffer.position(); i < length; i++) {
			ch = NIOByteBuffer.get();
			switch (ch) {
			// Unix LF
			case '\n':
				// new line
				eol = true;
				buffer.flip();
				line = new String(buffer.array(), 0, buffer.limit(),"GBK");
				offset += buffer.limit() + 1;
				buffer.clear();
				if(offset==endPoint){
					endFlag = true;
				}
				return line;
				
				// Windows CR/LF
			case '\r':
				eol = true;
				buffer.flip();
				line = new String(buffer.array(), 0, buffer.limit(),"GBK");
				buffer.clear();
				try {
					ch = NIOByteBuffer.get();
					if (ch != '\n') {
						NIOByteBuffer.position(NIOByteBuffer.position() - 1);
						//offset += line.length() + 1;
						offset += buffer.limit() + 1;
					} else {
						offset += buffer.limit() + 2;
					}
				} catch (BufferUnderflowException e) {
					// no match '\r\n', only '\r'
					offset += buffer.limit() + 1;
				}
				return line;
			default:
				try {
					buffer.put(ch);
				} catch (BufferOverflowException e) {
					// 每行大小超过BUFFERCAPACITY=1024, the size of the line greater
					// than 1024
					L.error(e.getMessage());
					throw e;
				}
				break;
			}
		}

		// 文件末尾没有换行符的特殊处理
		if (fChannel.size() == fChannel.position()) {
			buffer.flip();
			line = new String(buffer.array(), 0, buffer.limit(),"GBK");
			//offset += line.length();
			offset += buffer.limit();
			buffer.clear();
			return line;
		}
		if (eol == false) {
			return readLine();
		}

		return null;
	}

	public long lastestPosWith(long partitionPos) throws IOException{
		seek(partitionPos);
		NIOByteBuffer.clear();
		read0();
		NIOByteBuffer.flip();
		long length = NIOByteBuffer.limit();
		byte ch = '\b';
		for(long i = NIOByteBuffer.position(); i < length; i++){
			ch = NIOByteBuffer.get();
			switch (ch) {
			// Unix LF
			case '\n':
				partitionPos+=1;
				return partitionPos;
			default:
				partitionPos+=1;
				break;
			}
		}
		return partitionPos;
	}
	private boolean read0() throws IOException {
		return fChannel.read(NIOByteBuffer) != -1;
		
	}

	/**
	 * 
	 * TODO 实现Closeable接口，方便try resources with（可选）.
	 * 
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		this.fChannel.close();
		this.randomAccessFile.close();
	}

	/**
	 * 
	 * seek:定位. <br/>
	 * 
	 * @param pos
	 * @throws IOException
	 */
	public void seek(long pos) throws IOException {
		fChannel.position(pos);
		offset = pos;
	}

	public void setEndPoint(long _endPoint){
		if(_endPoint!=0){
			endPoint = _endPoint;
		}
	}
	/**
	 * 
	 * position:返回当前文件偏移量. <br/>
	 * 
	 * @return
	 */
	public long position() {
		return offset;
	}

	/**
	 * 
	 * size:返回文件大小. <br/>
	 * 
	 * @return
	 * @throws IOException
	 */
	public long size() throws IOException {
		return fChannel.size();
	}

	public void setLineSize(int size) {
		this.lineSize = size;
	}
	
	public static void main(String[] args) {
		System.out.println(File.separator);
	}

}
