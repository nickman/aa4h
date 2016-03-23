/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.aa4h.ejb.HibernateService;
import org.apache.log4j.Logger;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: XMLQueryServlet</p>
 * <p>Description: A servlet providing an http invocation interface to the adapter for XML formatted results.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$ 
 */
public class XMLQueryServlet extends HttpServlet {

	private static final long serialVersionUID = -5194106088167794864L;
	protected Logger LOG = null;
	protected String ejbLocalName = null;
	protected static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><XMLQueryResponse/>";
	protected static final String XML_STATS_CLEARED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><StatsCleared/>";
	public static final String EJB_SYSPROP_LOCAL_NAME = "aa4h.ejb.jndiname.local";

	/** A thread local container for a handle to the aa4h ejb for each thread. */
	protected ThreadLocal<HibernateService> hibernateService = new ThreadLocal<HibernateService>();

	/**
	 * Basic constructor.
	 */
	public XMLQueryServlet() {
		LOG = Logger.getLogger(getClass());
		LOG.info("Instantiated " + getClass().getName());
	}

	/**
	 * Initializes the servlet.
	 * @param config
	 * @throws ServletException
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		ejbLocalName = System.getProperty(EJB_SYSPROP_LOCAL_NAME, HibernateService.JNDI);
		LOG.info("\n\t==================================\n\t\n\t" + getClass().getName() + "\n\tAA4H LocalEJB Name:" + ejbLocalName + "\n\t==================================\n");
	}

	/**
	 * Invokes <code>doPost</code>.
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	/**
	 * Acquires the aa4h ejb handle from the thread local, initializing it if necessary.
	 * @return a handle to the aa4h ejb.
	 */
	protected HibernateService getHibernateService() {
		HibernateService hs = hibernateService.get();
		if (hs == null) {
			Context ctx = null;
			try {
				ctx = new InitialContext();
				hs = (HibernateService) ctx.lookup(ejbLocalName);
				hibernateService.set(hs);
			} catch (Exception ex) {
				LOG.error("Failed to acquire HibernateService EJB Handle", ex);
				throw new RuntimeException(
						"Failed to acquire HibernateService EJB Handle", ex);
			} finally {
				try {
					ctx.close();
				} catch (Exception e) {
				}
			}
		}
		return hs;
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
		res.setContentType("text/xml");
		if(queries.size() < 1) {
			buffer.append(XML_HEADER);
		} else {
			buffer.append(hs.xmlQuery(queries));
		}
	}

	/**
	 * Http Invocation point for the adapter.
	 * XML queries are passed in with the parameter name <code>query</code>.
	 * The response is an XML document containing the query results. 
	 * @param req
	 * @param res
	 * @throws ServletException
	 * @throws IOException
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPosts(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("XMLQuery Requested");
		long start = System.currentTimeMillis(), end = 0L;
		try {
			res.setHeader("Content-Type", "no-cache");
			res.setHeader("Pragma", "no-cache");
			res.setHeader("Expires", "Tue, 1 Jan 1980 10:00:00 GMT");
			Set<String> queries = new HashSet<String>();			
			String[] queryParameters = req.getParameterValues("query");
			if(queryParameters==null) queryParameters = new String[]{};
			StringBuilder resultBuffer = new StringBuilder();
			for (String s : queryParameters) {
				queries.add(s);
			}
			processQuery(resultBuffer, queries, res);
			String result = resultBuffer.toString();
			res.setBufferSize(result.getBytes().length);
			LOG.info("Result Size:" + result.getBytes().length);
			OutputStream out = res.getOutputStream();
			out.write(result.getBytes());
			out.flush();
			out.close();
			end = System.currentTimeMillis();
			if (LOG.isDebugEnabled())
				LOG.debug("XMLQuery Invoked In " + (end - start) + " ms.");
		} catch (Exception e) {
			LOG.error("Failed to execute XMLQuery", e);
			throw new ServletException("Failed to execute XMLQuery", e);
		} finally {

		}
	}

}
