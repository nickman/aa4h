/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h;

/**
 * <p>Title: TimeOutException</p>
 * <p>Description: A custom exception used to differentiate between a user requested timeout on a query and all other SQLExceptions.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$
 */
public class TimeOutException extends Exception {

	private static final long serialVersionUID = 8255152799940116700L;

	/**
	 * 
	 */
	public TimeOutException() {
		super();
	}

	/**
	 * @param message
	 */
	public TimeOutException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TimeOutException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public TimeOutException(Throwable cause) {
		super(cause);
	}

}

