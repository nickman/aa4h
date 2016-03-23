/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h.ejb;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.aa4h.XMLQueryParser;
import org.aa4h.XMLQueryParserException;
import org.apache.log4j.Logger;
import org.dom4j.tree.DefaultElement;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import org.json.XML;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.ParserAdapter;




/**
 * <p>Title: HibernateServiceBean</p>
 * <p>Description: An EJB interface to the XML Query Parser.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$
 */
@Stateless (name="AA4H-HibernateService", description="AA4H Hibernate Query Service")
@Remote(HibernateServiceRemote.class)
@Local(HibernateService.class)
@RemoteBinding(jndiBinding=HibernateServiceRemote.JNDI)
@LocalBinding(jndiBinding=HibernateService.JNDI)
public class HibernateServiceBean implements HibernateService, HibernateServiceRemote {
	/** XML Query Parser */
	protected XMLQueryParser qParser = new XMLQueryParser();
	/** Parser Interface */
	protected ParserAdapter parserAdapter = null;

	/** Hibernate Session Factory */
	protected SessionFactory sessionFactory = null;
	
	/** XML Document Header */
	protected static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";	
	/** Logger */
	protected Logger log = null;
	
	/** Re-entrant interface */
	protected HibernateService hService = null;
	
	/**
	 * Setter for injected self-reference
	 * @param hService
	 */
	public void setSelfReference(HibernateService hService) {
		this.hService = hService;
	}
	
	
	/**
	 * Setter for injected Hibernate session factory.
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	static {
		String xmlParser = System.getProperty("org.xml.sax.parser");
		if(xmlParser == null) {
			System.setProperty("org.xml.sax.parser", "com.sun.org.apache.xerces.internal.parsers.SAXParser");
		}		
	}
	
	/**
	 * Prints the thread stack for this invocation.
	 * @return A slightly formatted thread stack. 
	 */
	public String printThreadStack() {
		StringBuilder buff = new StringBuilder("Thread Stack");
		StackTraceElement[] stes = ManagementFactory.getThreadMXBean().getThreadInfo(Thread.currentThread().getId(), Integer.MAX_VALUE).getStackTrace();
		for(StackTraceElement ste: stes) {
			buff.append("\n\t").append(ste.toString());
		}
		return buff.toString();
	}
	
	/**
	 * Test method for testing threading in invoker.
	 * @param sleepTime The period of time in ms. to sleep.
	 */
	public void sleep(long sleepTime) {
		try {
			Thread.sleep(sleepTime);
			log.info("Slept for " + sleepTime);
		} catch (Exception e) {
			throw new RuntimeException("Failed to sleep", e);
		}
	}
	
	/**
	 * Simple constructor.
	 * Initializes logger and query parsing interface.
	 */
	public HibernateServiceBean() {
		log = Logger.getLogger(this.getClass());	
		try {
			parserAdapter = new ParserAdapter();
			parserAdapter.setContentHandler(qParser);
			log.info("Instantiated HibernateServiceBean");
		} catch (Exception e) {
			log.error("Failed to construct HibernateServiceBean", e);
			throw new RuntimeException("Failed to construct HibernateServiceBean", e);
		}
	}
	
	/**
	 * Executes a set of XML Queries and returns the XML Query results converted into JSON text.
	 * @param xmls A set of XML Query Strings
	 * @return A JSON Object text representation of the query result.
	 * @throws Exception
	 */
	public String xmlQueryJSON(Set<String> xmls) throws Exception {
		String xmlResult = xmlQuery(xmls);
		String result = XML.toJSONObject(xmlResult).toString();
		return result;
	}
	
	
	/**
	 * @param xmls A set of XML Query Strings.
	 * @return An XML Document representing one or more query results.
	 * @see org.aa4h.ejb.HibernateService#xmlQuery(java.util.Set)
	 */
	public String xmlQuery(Set<String> xmls) {
		StringBuilder xmlDoc = new StringBuilder(XML_HEADER);
		StringBuilder xmlResults = new StringBuilder();
		long start = System.currentTimeMillis();
		long elapsed = 0;
		int queries = 0;
		int errors = 0;
		int oks = 0;
		String result = null;
		for(String xml: xmls) {
			queries++;
			try {
				result = hService.xmlQuery(xml);
				xmlResults.append(result);
				if(result != null && result.startsWith("<XMLQueryError>")) {					
					errors++;					
				} else {
					oks++;
				}
			} catch (Exception e) {
				log.error("XMLQuery Failure", e);
				// convert exception to xml and output an error message
				xmlResults.append(generateError(e));
				errors++;
			}
		}
		elapsed = System.currentTimeMillis() - start;

		xmlDoc.append("<XMLQueryResults elapsedTime=\"").append(elapsed).append("\"");
		xmlDoc.append(" queries=\"").append(queries).append("\"");
		xmlDoc.append(" errors=\"").append(errors).append("\"");
		xmlDoc.append(" success=\"").append(oks).append("\"");		
		xmlDoc.append(">");
		xmlDoc.append(xmlResults);
		xmlDoc.append("</XMLQueryResults>");
		
		return xmlDoc.toString();
	}
	
	/**
	 * Generates an XML formatted error for the passed exception.
	 * @param e The exception to report.
	 * @return An XML fragment describing the exception.
	 */
	protected String generateError(Exception e) {
		StringBuilder errorBuff = new StringBuilder("<XMLQueryError><Exception>");
		errorBuff.append(e.toString());
		errorBuff.append("</Exception>");
		if(e.getMessage()!=null) {
			errorBuff.append("<Message>");
			errorBuff.append(e.getMessage());
			errorBuff.append("</Message>");
		}
		if(e.getCause()!=null) {
			errorBuff.append("<Cause>");
			errorBuff.append(e.getCause().toString());
			errorBuff.append("</Cause>");
		}
		errorBuff.append("</XMLQueryError>");
		return errorBuff.toString();						
	}
	
	/**
	 * Parses and executes the passed XML Query string, then returns the XML result.
	 * @param xml An XML Query String.
	 * @return An XML Document representing the query result.
	 * @throws XMLQueryParserException
	 * @see org.aa4h.ejb.HibernateService#xmlQuery(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String xmlQuery(String xml) throws XMLQueryParserException {
		Session session = null;
		int rowCount = 0;
		long start = System.currentTimeMillis();
		long elapsed = 0L;
		StringBuilder xmlHeader = new StringBuilder();
		StringBuilder xmlBody = new StringBuilder();
		String rootElementName = null;
		DefaultElement element = null;	
		try {			
			session = sessionFactory.getCurrentSession().getSession(EntityMode.DOM4J);
			BufferedReader br = new BufferedReader(new StringReader(xml));
			InputSource is = new InputSource(br);
			qParser.setSession(session);
			parserAdapter.parse(is);			
			rootElementName = qParser.getRootElementName();
			if(qParser.getResult()!=null) {
				rowCount = qParser.getResult().size();
			} else {
				rowCount = 0;
			}
			Iterator fragments = qParser.getResult().iterator();
			String docType = null;
			String className = qParser.getClassName();
			String[] aliases = qParser.getAliases();
			String projectionName = qParser.getProjectionsName();
			while(fragments.hasNext()) {
				Object retObject = fragments.next();
				if(retObject instanceof Integer) {
					xmlBody.append("<rowCount>").append(retObject).append("</rowCount>");
					docType = "RowCount";
				} else if(retObject instanceof Object[]) { 
					renderProjection((Object[])retObject, aliases, projectionName, xmlBody);
					docType = "Projection";
				} else {
					element = (DefaultElement)retObject;
					xmlBody.append(element.asXML());
					docType = "Object";
				}								
			}
			elapsed = System.currentTimeMillis() - start;
			xmlHeader.append("<").append(rootElementName).append(" docType=\"").append(docType).append("\"");
			if("Object".equals(docType)) {
				xmlHeader.append(" class=\"").append(className).append("\"");
			}
			xmlHeader.append(" rowCount=\"").append(rowCount).append("\" elapsedTime=\"").append(elapsed).append("\">");
			xmlHeader.append(xmlBody);
			xmlHeader.append("</" + rootElementName + ">");
			return xmlHeader.toString();				
		} catch (Exception e) {
			log.error("Failed to execute query:", e);
			xmlHeader.append(generateError(e));
			return xmlHeader.toString();
			//throw new XMLQueryParserException("Failed to execute query", e.getMessage(), e);
		} finally {
			try {  } catch (Exception e) {}
		}
	}
	
	/**
	 * Renders a projection based query result into an XML fragment.
	 * @param returnObject The object array returned from the query.
	 * @param aliases The aliases of the query result columns.
	 * @param projectionName The assigned name of the projected object.
	 * @param buffer The buffer into which the projection result XML will be written.
	 */
	protected void renderProjection(Object[] returnObject, String[] aliases, String projectionName, StringBuilder buffer) {
		buffer.append("<").append(projectionName).append(">");
		for(int i = 0; i < returnObject.length; i++) {
			buffer.append("<").append(aliases[i]).append(">");
			buffer.append(returnObject[i]);
			buffer.append("</").append(aliases[i]).append(">");
		}
		buffer.append("</").append(projectionName).append(">");
	}
	
}
