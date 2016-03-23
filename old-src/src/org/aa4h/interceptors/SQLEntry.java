/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h.interceptors;

/**
 * <p>Title: SQLEntry</p>
 * <p>Description: Encapsulates a logged SQL entry and the submitting thread's stack.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$ 
 */
public class SQLEntry {
	protected long id = 0L; // thread id
	protected String threadName = null; // thread name
	protected String sql = null;  // the sql statement
	protected StackTraceElement[] stackTrace = null; // the stack trace
	protected String backChain = null;

	/**
	 * Constructs a new SQLEntry.
	 * @param id The thread Id.
	 * @param threadName The thread name.
	 * @param sql The sql string to be logged.
	 * @param stackTrace The thread's stack trace.
	 */
	public SQLEntry(long id, String threadName, String sql, StackTraceElement[] stackTrace) {
		this.id = id;
		this.threadName = threadName;
		this.sql = sql;
		this.stackTrace = stackTrace;
	}

	/**
	 * Generates an html string representation of the thread stack. 
	 */
	public void generateBackChain() {
		if(stackTrace != null && stackTrace.length > 1) {
			StringBuilder buff = new StringBuilder("<UL>");
			for(int i = 1; i < stackTrace.length; i++) {
				String clazz = stackTrace[i].getClassName();
				if(clazz.contains("org.hibernate") ||
				   clazz.contains("org.jboss") ||
				   clazz.contains("$Proxy") ||
				   clazz.contains("com.arjuna") ||
				   clazz.contains("EDU.oswego") ||
				   clazz.contains("java.lang") ||
				   clazz.contains("sun.reflect")
				) continue;
				buff.append("<li>");
				buff.append(stackTrace[i].getClassName());
				buff.append("/");
				buff.append(stackTrace[i].getMethodName());
				buff.append("/");
				buff.append(stackTrace[i].getLineNumber());
				buff.append("</li>");
			}
			buff.append("</UL>");
			backChain = buff.toString();
		} else {
			backChain = "No Trace";
		}
	}
	/**
	 * The thread's Id.
	 * @return the id
	 */

	public long getId() {
		return id;
	}
	/**
	 * Sets the thread id
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * Returns the logged sql fragment
	 * @return the sql
	 */

	public String getSql() {
		return sql;
	}
	/**
	 * Sets the logged sql fragment
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	/**
	 * Returns the formatted thread call stack.
	 * @return the backChain
	 */
	public String getBackChain() {
		return backChain;
	}

	/**
	 * Gets the thread name.
	 * @return the threadName
	 */

	public String getThreadName() {
		return threadName;
	}
}