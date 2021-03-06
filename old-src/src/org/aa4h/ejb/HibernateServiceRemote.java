/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h.ejb;

import java.util.Set;

import javax.ejb.Remote;

import org.aa4h.XMLQueryParserException;


/**
 * <p>Title: HibernateServiceRemote</p>
 * <p>Description: HibernateService EJB Remote Interface</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$ 
 */
@Remote
public interface HibernateServiceRemote {
	public static final String JNDI = "ejb/org/aa4h/remote/HibernateService";	
	public String xmlQuery(Set<String> xmls);	
	public String xmlQuery(String xml) throws XMLQueryParserException;
	
	/**
	 * Executes a set of XML Queries and returns the XML Query results converted into JSON text.
	 * @param xmls A set of XML Query Strings
	 * @return A JSON Object text representation of the query result.
	 * @throws Exception
	 */
	public String xmlQueryJSON(Set<String> xmls) throws Exception;
	
	/**
	 * Test method for testing threading in invoker.
	 * @param sleepTime
	 */
	public void sleep(long sleepTime);

	/**
	 * Prints the thread stack for this invocation.
	 * @return
	 */
	public String printThreadStack();
	
}
