/**
 * 
 */
package com.snail.core.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务线程池
 * @version 1.0.0
 */
public class ThreadPool {
	private static final Logger logger = LoggerFactory.getLogger(ThreadPool.class);
	
	private final static String poolName = "threadPool-snail-services-dcwj";
	private ThreadPoolExecutor executor = null;
	private static  ThreadPool threadFixedPool = null;
	
    public static synchronized ThreadPool getFixedInstance() {	 
    	if(threadFixedPool == null){
    		threadFixedPool = new ThreadPool();
    	}
    	return threadFixedPool;
 	}
    
    private ThreadPool(){
    	executor = new ThreadPoolExecutor(50, 50,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(150),
                threadFactory);//Executors.newFixedThreadPool(50, threadFactory);
    	executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy()); //抛弃最久的拒绝策略
    }
    
	public void execute(Runnable r) {
		executor.execute(r);
		logger.info("[ThreadPool.execute][maximumPoolSize=50][TaskCount()={}][ActiveCount={}]", 
				executor.getTaskCount(), executor.getActiveCount());
	}
	
	private ThreadFactory threadFactory = new ThreadFactory() {
		AtomicInteger next = new AtomicInteger(0);
		public Thread newThread(Runnable r) {
			if(next.get() >= Integer.MAX_VALUE) {
				next.set(0);
			}
			final Thread thread = new Thread(r);
			thread.setName(poolName + next.getAndIncrement());
			return thread;
		}
	};
    
}
