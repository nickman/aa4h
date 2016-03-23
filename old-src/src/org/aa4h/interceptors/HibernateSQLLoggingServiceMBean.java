/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h.interceptors;

/**
 * <p>Title: HibernateSQLLoggingServiceMBean</p>
 * <p>Description: The HibernateSQLLoggingService MBean interface.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$
 */
public interface HibernateSQLLoggingServiceMBean {

	public static final String SET_ENABLED_EVENT = "hibernate.logging.enabled";
	public static final String SET_DISABLED_EVENT = "hibernate.logging.disabled";
	
	/**
	 * Creates the processQueue of the configured size and starts the queue processing thread.
	 * @throws Exception
	 */
	public abstract void start() throws Exception;

	/**
	 * Stops the queue processing thread. 
	 */
	public abstract void stop();

	/**
	 * @return the on
	 */

	public abstract boolean isOn();

	/**
	 * 
	 * @param on the on to set
	 */
	public abstract void setOn(boolean on);

	/**
	 * Submit a new SQL Statement.
	 * @param id
	 * @param sql
	 * @return
	 */
	public abstract boolean submit(long id, String sql);

	/**
	 * Generates a report of the currently traced SQL statements.
	 * @return
	 */
	public abstract String displaySQL();

	/**
	 * @return the maxQueueSize
	 */

	public abstract int getMaxQueueSize();

	/**
	 * @param maxQueueSize the maxQueueSize to set
	 */
	public abstract void setMaxQueueSize(int maxQueueSize);

	/**
	 * @return the maxStatements
	 */

	public abstract int getMaxStatements();

	/**
	 * @param maxStatements the maxStatements to set
	 */
	public abstract void setMaxStatements(int maxStatements);

	/**
	 * @return the waitTime
	 */

	public abstract long getWaitTime();

	/**
	 * @param waitTime the waitTime to set
	 */
	public abstract void setWaitTime(long waitTime);
	
	public abstract long getStatementCount();
	public abstract int getCurrentStatementSize();
	public abstract int getCurrentQueueSize();
	
	/**
	 * @return the captureStack
	 */	
	public boolean isCaptureStack();
	/**
	 * @param captureStack the captureStack to set
	 */
	public void setCaptureStack(boolean captureStack);
	
	/**
	 * Clears the list of processed SQLEntries.
	 */
	public void clearEntries();
	
	/**
	 * Indicates if the processing thread is started.
	 * @return true if it is started.
	 */
	public boolean isStarted();	

}
