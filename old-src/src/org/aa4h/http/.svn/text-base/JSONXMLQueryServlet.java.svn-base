/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */

package org.aa4h.http;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.aa4h.ejb.HibernateService;


/**
 * <p>Title: JSONXMLQueryServlet</p>
 * <p>Description: A servlet providing an http invocation interface to the adapter for JSON formatted results.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$ 
 */
public class JSONXMLQueryServlet extends XMLQueryServlet {
	private static final long serialVersionUID = -7381948155384971986L;
	protected static final String JSON_HEADER = "{\"JSON Query Result\":\"Empty Result\"}";
	public JSONXMLQueryServlet() {
		super();
	}
	
	/**
	 * Handles the actual invocation of the call to the EJB and any other 
	 * format specific operations required for the returned data type.
	 * Extracted to allow for data format specific overriding.
	 * @param buffer The StringBuilder where the results are written.
	 * @param queries The set of queries to be executed.
	 * @param res The Servlet response handle.
	 */
	protected void processQuery(StringBuilder buffer, Set<String> queries, HttpServletResponse res) {
		HibernateService hs = getHibernateService();
		res.setContentType("application/javascript");
		if(queries.size() < 1) {
			buffer.append(JSON_HEADER);
		} else {
			try {
				buffer.append(hs.xmlQueryJSON(queries));
			} catch (Exception e) {
				LOG.error("Failure to process JSON Request", e);
				throw new RuntimeException("Failure to process JSON Request", e);
			}			
		}
	}	

}
