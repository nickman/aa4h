/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h;

/**
 * <p>Title: XMLQueryParserException</p>
 * <p>Description: Custom exception to indicate a soft parse exception in the XML Query document.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$ 
 */
public class XMLQueryParserException extends Exception {
	private static final long serialVersionUID = 4711100070961719649L;
	protected String xmlError = null;
	/**
	 * 
	 */
	public XMLQueryParserException() {
	}

	/**
	 * @param message
	 */
	public XMLQueryParserException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public XMLQueryParserException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public XMLQueryParserException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * @param message
	 * @param xmlError
	 * @param cause
	 */
	public XMLQueryParserException(String message, String xmlError, Throwable cause) {
		super(message, cause);
		this.xmlError = xmlError; 
	}
	

	/**
	 * @return the xmlError
	 */
	public String getXmlError() {
		return xmlError;
	}

	/**
	 * @param xmlError the xmlError to set
	 */
	public void setXmlError(String xmlError) {
		this.xmlError = xmlError;
	}

}
