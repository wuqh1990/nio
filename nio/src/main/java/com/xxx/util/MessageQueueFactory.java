package com.xxx.util;

import java.util.concurrent.ConcurrentHashMap;


public class MessageQueueFactory {
	
	public static enum queueType {
		FILE, // 文件队列
		OTHER // 其他
	}

	
	private volatile static MessageQueueFactory instance = null;
	private ConcurrentHashMap<queueType, MessageQueue> m = new ConcurrentHashMap<queueType, MessageQueue>();
	private static Object O_O = new Object();

	public static MessageQueueFactory getInstance() {
		if (instance == null) {
			synchronized (MessageQueueFactory.class) {
				if (instance == null) {
					instance = new MessageQueueFactory();
				}
			}
		}
		return instance;
	}

	
	public MessageQueue getMessageQueue(queueType type) {
		MessageQueue q = m.get(type);
		if (q == null) {
			synchronized (O_O) {
				q = m.get(type);
				if (q == null) {
					q = new MessageQueue();
					m.put(type, q);
				}
			}
		}
		return q;
	}
}
