/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h.interceptors;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: ProcessorThread</p>
 * <p>Description: A background worker thread to execute some formatting of submitted SQL entries.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$ 
 */
public class ProcessorThread extends Thread {
	protected boolean queueProcessorRun = true;
	protected BlockingQueue<SQLEntry> processQueue = null;
	protected long waitTime = 10000;
	protected LinkedList<SQLEntry> entries = null;
	protected int maxSize = 0;
	
	/**
	 * Constructs a new processor thread.
	 * @param processQueue The blocking queue feeding the worker thread.
	 * @param waitTime The wait time on the queue if it is full.
	 * @param entries The list containing the processed SQLEntries.
	 * @param maxSize The maximum size of the entries list.
	 */
	public ProcessorThread(BlockingQueue<SQLEntry> processQueue, long waitTime, LinkedList<SQLEntry> entries, int maxSize) {
		this.processQueue = processQueue;
		this.waitTime = waitTime;
		this.entries = entries;
		this.maxSize = maxSize;
	}
	
	/**
	 * Starts the worker thread.
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		while(queueProcessorRun) {
			try {
				SQLEntry entry = processQueue.poll(waitTime, TimeUnit.MILLISECONDS);
				if(entry!=null) {
					entry.generateBackChain();
					synchronized(entries) {
						entries.addFirst(entry);
						while(entries.size() > maxSize) {
							entries.removeLast();
						}
					}
				}
			} catch (InterruptedException e) {
			}
		}

	}
	/**
	 * Indicates if the thread should continue running.
	 * @return the queueProcessorRun
	 */
	public boolean isQueueProcessorRun() {
		return queueProcessorRun;
	}
	
	/**
	 * If the current value is true, stops the thread from running.
	 * @param queueProcessorRun the queueProcessorRun to set
	 */
	public void setQueueProcessorRun(boolean queueProcessorRun) {
		this.queueProcessorRun = queueProcessorRun;
		if(!queueProcessorRun) {
			interrupt();
		}
		processQueue = null;
		entries = null;
	}
	/**
	 * The wait time for the blocking queue. 
	 * @return the waitTime
	 */

	public long getWaitTime() {
		return waitTime;
	}
	
	/**
	 * Sets the wait time for the blocking queue.
	 * @param waitTime the waitTime to set
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}
	
	/**
	 * The maximum size of the processes SQLEntries list.
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}
	/**
	 * Sets the maximum size of the processes SQLEntries list.
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

}
