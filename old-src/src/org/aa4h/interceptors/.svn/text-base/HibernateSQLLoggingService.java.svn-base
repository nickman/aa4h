/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */

package org.aa4h.interceptors;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

/**
 * <p>Title: HibernateSQLLoggingService</p>
 * <p>Description: An mbean implementation that receives SQL logging calls from a hibernate interceptor.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author Whitehead
 * @version $Revision: 1.2 $
 */

public class HibernateSQLLoggingService extends NotificationBroadcasterSupport implements HibernateSQLLoggingServiceMBean  {


	protected boolean on = false;
	protected boolean captureStack = false;
	protected int maxStatements = 100;
	protected int maxQueueSize = 1000;
	protected long waitTime = 10000;
	public static final String ThreadName = "HibernateSQLLoggingService Queue Processor";
	protected BlockingQueue<SQLEntry> processQueue = null;
	protected LinkedList<SQLEntry> entries = null;
	protected ProcessorThread queueProcessor = null;
	protected long statementCount = 0;

	/**
	 * Instantiates a new HibernateSQLLoggingService.
	 */
	public HibernateSQLLoggingService() {

	}

	/**
	 * Starts the MBean Service.
	 * @throws Exception
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#start() 
	 */
	public void start() throws Exception {
		processQueue = new LinkedBlockingQueue<SQLEntry>(maxQueueSize);
		entries = new LinkedList<SQLEntry>();
		if(on) startProcessorThread();
	}

	/**
	 * Returns true if the processor thread is alive.
	 * @return true if the processing thread is alive.
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#isStarted()
	 */
	public boolean isStarted() {
		return (queueProcessor!=null && queueProcessor.isAlive());
	}

	/**
	 * Starts the processing thread.
	 */
	protected void startProcessorThread() {
		queueProcessor = new ProcessorThread(processQueue, waitTime, entries, maxStatements);
		queueProcessor.setDaemon(true);
		queueProcessor.setName(ThreadName);
		queueProcessor.start();
		Notification notification = new Notification(SET_ENABLED_EVENT, this, System.currentTimeMillis(), SET_ENABLED_EVENT);
		sendNotification(notification);

	}

	/**
	 * Stops the processing thread.
	 */
	protected void stopProcessorThread() {
		Notification notification = new Notification(SET_DISABLED_EVENT, this, System.currentTimeMillis(), SET_DISABLED_EVENT);
		sendNotification(notification);
		if(queueProcessor!=null) queueProcessor.setQueueProcessorRun(false);
	}

	/**
	 * Stops the MBean Service.
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#stop()
	 */
	public void stop() {
		stopProcessorThread();
	}

	/**
	 * Returns the logging state of the service. 
	 * @return true id the logging is turned on.
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#isOn()
	 */
	public boolean isOn() {
		return on;
	}

	/**
	 * Sets the logging state of the service.
	 * @param on
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#setOn(boolean)
	 */
	public void setOn(boolean on) {
		this.on = on;
	}


	/**
	 * Submits a sql string to be logged.
	 * @param id The submitting thread.
	 * @param sql The sql to be logged.
	 * @return true if the service logging is on, false if it is not. 
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#submit(long, java.lang.String)
	 */
	public boolean submit(long id, String sql) {
		if(on) {
			statementCount++;
			StackTraceElement[] stack = null;
			if(captureStack) stack = Thread.currentThread().getStackTrace();
			return processQueue.offer(
					new SQLEntry(Thread.currentThread().getId(), Thread.currentThread().getName(), sql,  stack)
			);
		} else {
			return false;
		}
	}

	/**
	 * Prints an HTML report of the captured SQL.
	 * @return An HTML string report.
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#displaySQL() 
	 */
	public String displaySQL() {
		StringBuilder buff = new StringBuilder("<TABLE BORDER=\"1\">");
		buff.append("<TR><TH>Thread Id</TH><TH>Thread Name</TH><TH>Stack</TH><TH>SQL</TH></TR>");
		synchronized(entries) {
			for(SQLEntry sqlEntry: entries) {
				buff.append("<TR><TD>").append(sqlEntry.getId()).append("</TD>");
				buff.append("<TD>").append(sqlEntry.getThreadName()).append("</TD>");
				buff.append("<TD>").append(sqlEntry.getBackChain()).append("</TD>");
				buff.append("<TD>").append(sqlEntry.getSql()).append("</TD></TR>");
			}
		}
		buff.append("</TABLE>");
		return buff.toString();
	}

	/**
	 * Clears the logged SQL entries.
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#clearEntries()
	 */
	public void clearEntries() {
		synchronized(entries) {
			entries.clear();
		}
	}


	/**
	 * The maximum queue size.
	 * @return the maximum queue size.
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#getMaxQueueSize()
	 */
	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	/**
	 * @param maxQueueSize
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#setMaxQueueSize(int)
	 */
	public void setMaxQueueSize(int maxQueueSize) {
		if(maxQueueSize > 0) this.maxQueueSize = maxQueueSize;
	}

	/**
	 * @return
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#getMaxStatements()
	 */

	public int getMaxStatements() {
		return maxStatements;
	}

	/**
	 * @param maxStatements
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#setMaxStatements(int)
	 */
	public void setMaxStatements(int maxStatements) {
		if(maxStatements > 0) this.maxStatements = maxStatements;
	}


	/**
	 * @return
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#getWaitTime()
	 */

	public long getWaitTime() {
		return waitTime;
	}

	/**
	 * @param waitTime
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#setWaitTime(long)
	 */
	public void setWaitTime(long waitTime) {
		if(waitTime>0) this.waitTime = waitTime;
	}

	/**
	 * @return the statementCount
	 */

	public long getStatementCount() {
		return statementCount;
	}

	/**
	 * @return
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#getCurrentStatementSize()
	 */
	public int getCurrentStatementSize() {
		return entries.size();
	}

	/**
	 * @return
	 * @see org.aa4h.interceptors.HibernateSQLLoggingServiceMBean#getCurrentQueueSize()
	 */
	public int getCurrentQueueSize() {
		return processQueue.size();
	}

	/**
	 * @return the captureStack
	 */

	public boolean isCaptureStack() {
		return captureStack;
	}

	/**
	 * @param captureStack the captureStack to set
	 */
	public void setCaptureStack(boolean captureStack) {
		this.captureStack = captureStack;
	}
}



