/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.dom4j.QName;
import org.dom4j.tree.DefaultElement;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.UnionExpression;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;





/**
 * <p>Title: XMLQueryBuilder</p>
 * <p>Description: Parses and executes an XML Query. The class is basically a pseudo-stateful SAX parser.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @version $Revision$
 * TODO: Organize methods into sections by SAX Handlers, attributes etc.
 * TODO: Complete support for subqueries and full projection support.
 * TODO: Figure out how to use custom annotations to provide support for auto-generated documentation for the XML Query Syntax.
 * TODO: Bundle post query attributes into one return composite object.
 */
public class XMLQueryParser implements ContentHandler {
	@SuppressWarnings("unchecked")
	protected static Class[] ADD_ARGS_TYPE = new Class[]{Criterion.class};
	protected Session session = null;
	protected long startTime = 0L;
	protected long endTime = 0L;
	protected String queryName = null;
	protected Stack<CriteriaSpecification> criteriaStack = new Stack<CriteriaSpecification>();
	protected Stack<ProjectionList> projectionStack = new Stack<ProjectionList>();
	@SuppressWarnings("unchecked")
	protected Stack opStack = new Stack();
	@SuppressWarnings("unchecked")
	protected Stack notStack = new Stack();
	protected List<DefaultElement> result = null;
	protected String rootElementName = null;
	protected Logger log = null;
	protected boolean inJunction = false;
	protected TimeOutException toe = null;
	protected String sql = "";
	protected boolean inDetached = false;
	protected String className = null;
	protected String[] columnNames = null;
	protected boolean isNamedQuery = false;
	protected String namedQuery = "";
	protected Query query = null;
	protected Boolean isNamedParams = null;
	protected long parseTime = 0L;
	protected String[] aliases = new String[]{};
	protected String projectionsName = null;



	/** Thread Local to contain object/thread exclusive rowCountOnly flag */
	protected ThreadLocal<Boolean> rowCountOnlyQuery = new ThreadLocal<Boolean>();

	/**
	 * Constructs a new XMLQueryParser using the passed Hibernate Session.
	 * @param session A Hibernate Session used to query the datasource.
	 */
	public XMLQueryParser(Session session) {
		this();
		this.session = session;
	}

	/**
	 * Parameterless XMLQueryParser constructor.
	 * @throws RuntimeException
	 */
	public XMLQueryParser() {
		super();
		log = Logger.getLogger(getClass());
		log.info("Insantiated " + getClass().getName());
	}

	/**
	 * Sets the XMLQueryParser's Hibernate Session.
	 * @param session A Hibernate Session used to query the datasource.
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * The parsed out name of the query processed.
	 * Returns null before a query has been parsed.
	 * @return The assigned name of the query that was just parsed.
	 */
	public String getQueryName() {
		return queryName;
	}

	/**
	 * Sets the value of the rowCountOnlyQuery ThreadLocal
	 * @param rc true if query is rowCount only.
	 */
	public void setRowCountOnly(boolean rc) {
		rowCountOnlyQuery.set(Boolean.valueOf(rc));
	}

	/**
	 * Returns the rowCountOnly ThreadLocal value.
	 * @return true if query is rowCount only.
	 */
	public boolean isRowCountOnly() {
		return rowCountOnlyQuery.get().booleanValue();
	}


	/**
	 * Unimplemented SAX ContentHandler Method.
	 * @param arg0
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator arg0) {

	}

	/**
	 * Start Document SAX ContentHandler Method.
	 * Initializes all stateful fields to prepare for a query parse.
	 * @see org.xml.sax.ContentHandler#startDocument()
	 * @throws SAXException
	 */
	public void startDocument() throws SAXException {
		startTime = System.currentTimeMillis();
		opStack.clear();
		notStack.clear();
		criteriaStack.clear();
		projectionStack.clear();
		toe = null;
		queryName = "";
		setRowCountOnly(false);
		namedQuery = "";
		isNamedQuery = false;
		query = null;
		isNamedParams = null;
		aliases = new String[]{};
		projectionsName = null;
		parseTime = System.currentTimeMillis();
	}




	/**
	 * End Document SAX ContentHandler Method.
	 * Fired at the end of the document parsing event.
	 * At this point the query has been fully parsed and the named or criteria query is complete.
	 * This method executes the query and sets the result field, handling any errors.
	 * @see org.xml.sax.ContentHandler#endDocument()
	 * @throws SAXException
	 * TODO Clean up the timeout handling. The current one is Oracle specific. We need the timeout handling to differentiate between standard DB Errors and timeouts as a result of a user requested timeout.
	 */
	@SuppressWarnings("unchecked")
	public void endDocument() throws SAXException {
		parseTime = System.currentTimeMillis() - parseTime;
		try {
			if(isNamedQuery) {
				result = query.list();
			} else {
				Criteria finalCriteria = (Criteria)criteriaStack.pop();
				Projection p = ((org.hibernate.impl.CriteriaImpl)finalCriteria).getProjection();
				if(p!=null) {
					aliases = p.getAliases();
				}
				result = finalCriteria.list();
			}
		} catch (Exception ex) {
			try {
				if(ex.getCause().getMessage().startsWith("ORA-01013")) {
					toe = new TimeOutException("Query " + queryName + " Timed Out");
					QName q = new QName("Error");
					DefaultElement de = new DefaultElement(q);
					de.addAttribute("reason", "Query Time Out");
					result = new ArrayList<DefaultElement>();
					result.add(de);
					endTime = System.currentTimeMillis();

				} else {
					throw new SAXException("Unknown Exception At List Time for Query:" + queryName, ex);
				}
			} catch (Exception e) {
				log.error("Unknown Exception At List Time for Query:" + queryName, ex);
				throw new SAXException("Unknown Exception At List Time for Query:" + queryName, ex);
			}

		}
		endTime = System.currentTimeMillis();
		if(log.isDebugEnabled()) {
			log.info(new StringBuffer("CATSQuery[").append(queryName).append("] Returned ").append(result.size()).append(" elements in ").append((endTime-startTime)).append(" ms.").toString());
			log.info("Elapsed Time For [" + queryName + "]:" + (endTime-startTime) + " ms.");
		}

	}

	/**
	 * Unimplemented SAX ContentHandler Method.
	 * @param arg0
	 * @param arg1
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	public void startPrefixMapping(String arg0, String arg1) throws SAXException {
	}

	/**
	 * Unimplemented SAX ContentHandler Method.
	 * @param arg0
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String arg0) throws SAXException {
	}

	/**
	 * Adds a new projection list
	 * @param attrs The attributes of the processed node.
	 */
	protected void startProjection(Attributes attrs) {
		projectionsName = attrs.getValue("name");
		projectionStack.push(Projections.projectionList());
	}


	/**
	 * Initializes a Criteria Query.
	 * Mandatory Attributes:<ul>
	 * <li><b>name</b>: The unqualified class name driving the criteria query.</li>
	 * </ul>
	 * Optional Attributes:<ul>
	 * <li><b>prefix</b>: The package name of the class driving the criteria query. If null, no package is assumed.</li>
	 * <li><b>maxSize</b>: The maximum number of rows to return from the database.</li>
	 * <li><b>fetchSize</b>: The number of rows to fetch when rows are requested. Usually not useful for AA4H.</li>
	 * <li><b>cacheEnabled</b>: Enables or disables caching for the queried objects.</li>
	 * <li><b>cacheMode</b>: The cache options for the queried objects.</li>
	 * <li><b>flushMode</b>: The session flush options.</li>
	 * <li><b>fetchMode</b>: The collection fetch options for the query.</li>
	 * <li><b>lockMode</b>: The row lock options for the queried rows.</li>
	 * <li><b>timeOut</b>: The query timeout option.</li>
	 * <li><b>rowCountOnly</b>: Returns a count of the query rows only.</li>
	 * </ul>
	 * @param attrs The attributes of the processed node.
	 * @return An appended or new CriteriaSpecification
	 * @throws SAXException
	 */
	protected CriteriaSpecification processCriteria(Attributes attrs) throws SAXException {
		if(inDetached) {
			return criteriaStack.peek();
		}
		String name = attrs.getValue("name");
		String prefix = attrs.getValue("prefix");
		if(prefix!=null) {
			className = prefix + "." + name;
		} else {
			className = name;
		}
		String maxSize = attrs.getValue("maxSize");
		String fetchSize = attrs.getValue("fetchSize");
		String firstResult = attrs.getValue("firstResult");
		String cacheEnabled = attrs.getValue("cacheEnabled");
		String cacheMode = attrs.getValue("cacheMode");
		String flushMode = attrs.getValue("flushMode");
		String fetchMode = attrs.getValue("fetchMode");
		String lockMode = attrs.getValue("lockMode");
		String timeOut = attrs.getValue("timeOut");
		String rowCountOnly = attrs.getValue("rowCountOnly");
		Criteria newCriteria = null;
		try {
			if(criteriaStack.size()==0) {
				newCriteria = session.createCriteria(className);
			} else {
				newCriteria = ((Criteria)criteriaStack.peek()).createCriteria(className);
			}
			criteriaStack.push(newCriteria);
			if("true".equalsIgnoreCase(rowCountOnly)) {
				newCriteria.setProjection(Projections.projectionList()
					.add(Projections.rowCount())

				);
				setRowCountOnly(true);
			}
			if(maxSize != null && isRowCountOnly()==false) {
				newCriteria.setMaxResults(Integer.parseInt(maxSize));
			}
			if(fetchSize != null && isRowCountOnly()==false) {
				newCriteria.setFetchSize(Integer.parseInt(fetchSize));
			}
			if(firstResult != null && isRowCountOnly()==false) {
				newCriteria.setFirstResult(Integer.parseInt(firstResult));
			}
			if(timeOut != null) {
				newCriteria.setTimeout(Integer.parseInt(timeOut));
			}

			if("true".equalsIgnoreCase(cacheEnabled)) {
				newCriteria.setCacheable(true);
			} else if("false".equalsIgnoreCase(cacheEnabled)) {
				newCriteria.setCacheable(false);
			}
			if(fetchMode != null && fetchMode.length() > 0) {
				if("JOIN".equalsIgnoreCase(fetchMode)) {
					newCriteria.setFetchMode(name, FetchMode.JOIN);
				} else if("SELECT".equalsIgnoreCase(fetchMode)) {
					newCriteria.setFetchMode(name, FetchMode.SELECT);
				} else {
					newCriteria.setFetchMode(name, FetchMode.DEFAULT);
				}
			} else {
				newCriteria.setFetchMode(name, FetchMode.DEFAULT);
			}
			if(cacheMode != null && cacheMode.length() > 0) {
				if("GET".equalsIgnoreCase(cacheMode)) {
					newCriteria.setCacheMode(CacheMode.GET);
				} else if("IGNORE".equalsIgnoreCase(cacheMode)) {
					newCriteria.setCacheMode(CacheMode.IGNORE);
				} else if("NORMAL".equalsIgnoreCase(cacheMode)) {
					newCriteria.setCacheMode(CacheMode.NORMAL);
				} else if("PUT".equalsIgnoreCase(cacheMode)) {
					newCriteria.setCacheMode(CacheMode.PUT);
				} else if("REFRESH".equalsIgnoreCase(cacheMode)) {
					newCriteria.setCacheMode(CacheMode.REFRESH);
				} else {
					newCriteria.setCacheMode(CacheMode.NORMAL);
				}
			}
			if(lockMode != null && lockMode.length() > 0) {
				if("NONE".equalsIgnoreCase(lockMode)) {
					newCriteria.setLockMode(LockMode.NONE);
				} else if("READ".equalsIgnoreCase(lockMode)) {
					newCriteria.setLockMode(LockMode.READ);
				} else if("UPGRADE".equalsIgnoreCase(lockMode)) {
					newCriteria.setLockMode(LockMode.UPGRADE);
				} else if("UPGRADE_NOWAIT".equalsIgnoreCase(lockMode)) {
					newCriteria.setLockMode(LockMode.UPGRADE_NOWAIT);
				} else if("WRITE".equalsIgnoreCase(lockMode)) {
					newCriteria.setLockMode(LockMode.WRITE);
				} else {
					throw new SAXException("lockMode[" + lockMode + "] Not Recognized");
				}
			}
			if(flushMode != null && flushMode.length() > 0) {
				if("ALWAYS".equalsIgnoreCase(flushMode)) {
					newCriteria.setFlushMode(FlushMode.ALWAYS);
				} else if("AUTO".equalsIgnoreCase(flushMode)) {
					newCriteria.setFlushMode(FlushMode.AUTO);
				} else if("COMMIT".equalsIgnoreCase(flushMode)) {
					newCriteria.setFlushMode(FlushMode.COMMIT);
				} else if("NEVER".equalsIgnoreCase(flushMode)) {
					// NEVER is deprecated, so we won't throw an exception but we'll ignore it.
				} else {
					throw new SAXException("flushMode[" + flushMode + "] Not Recognized");
				}
			}
			return newCriteria;

		} catch (Exception e) {
			throw new SAXException("Unable to configure class " + className, e);
		}
	}

	/**
	 * Adds a new projection to the current projectionList stack.
	 * Mandatory Attributes:<ul>
	 * <li><b>name</b>: The field to create a projection on.</li>
	 * <li><b>type</b>: The projection type.</li>
	 * <li><b>alias</b>: The name applied to the projection returned field.</li>
	 * </ul>
	 * @param attrs The attributes of the processed node.
	 * @throws SAXException
	 * TODO: Implement checks for mandatory attributes.
	 */
	public void addProjection(Attributes attrs) throws SAXException {
		ProjectionList projectionList = projectionStack.peek();
		if(projectionList==null) {
			throw new SAXException("Attempted to add Projection and no ProjectionList Exists On The Stack. (Projection must be embedded inside a Projections)");
		}
		String type  = attrs.getValue("type");
		String name  = attrs.getValue("name");
		String alias  = attrs.getValue("alias");
		if("avg".equalsIgnoreCase(type)) {
			projectionList.add(Projections.avg(name), alias);
		} else if("count".equalsIgnoreCase(type)) {
			projectionList.add(Projections.count(name), alias);
		} else if("countDistinct".equalsIgnoreCase(type)) {
			projectionList.add(Projections.countDistinct(name), alias);
		} else if("groupProperty".equalsIgnoreCase(type)) {
			projectionList.add(Projections.groupProperty(name), alias);
		} else if("max".equalsIgnoreCase(type)) {
			projectionList.add(Projections.max(name), alias);
		} else if("min".equalsIgnoreCase(type)) {
			projectionList.add(Projections.min(name), alias);
		} else if("sum".equalsIgnoreCase(type)) {
			projectionList.add(Projections.sum(name), alias);
		} else if("rowCount".equalsIgnoreCase(type)) {
			projectionList.add(Projections.rowCount(), alias);
		}

	}

	/**
	 * Processes the bind of an argument against a named query.
	 * Mandatory Attributes:<ul>
	 * <li><b>name</b> or <b>id</b>: The name or bind sequence id of the parameter.</li>
	 * <li><b>type</b>: The data type of the parameter.</li>
	 * <li><b>value</b>: The the value of the parameter to be bound.</li>
	 * </ul>
	 * Optional Attributes:<ul>
	 * <li><b>format</b>: The format of the value being passed. Required if type is a Date/Time or array.</li>
	 * </ul>
	 * @param attrs The attributes of the processed node.
	 * @throws SAXException
	 */
	protected void processQueryBind(Attributes attrs) throws SAXException {
		if(query==null) {
			throw new SAXException("Encountered Query Param Bind With No Initialized Query");
		}
		boolean isNamed = false;

		String id = attrs.getValue("id");
		int pid = -1;
		String name = attrs.getValue("name");
		String value = attrs.getValue("value");
		String type = attrs.getValue("type");
		String format = attrs.getValue("format");
		if(type==null) throw new SAXException("Query Param Bind Has No Type");
		if(id==null && name==null) throw new SAXException("Query Param Bind Has No Name or Id");
		if(id==null) {
			isNamed = true;
		} else {
			isNamed = false;
			try {
				pid = Integer.parseInt(id);
			} catch (Exception e) {
				throw new SAXException("Query Param Bind Has Invalid Id:" + id);
			}
		}
		if(isNamedParams == null) {
			isNamedParams = new Boolean(isNamed);
		} else {
			if(isNamedParams.booleanValue() && (!isNamed)) {
				throw new SAXException("Query Param Binds Combined Names and Ids");
			}
		}
		Operator op = new Operator(isNamed ? name : id, type, value, format);
		try {
			if(isNamed) {
				query.setParameter(name, op.getNativeValue());
			} else {
				query.setParameter(pid, op.getNativeValue());
			}
		} catch (Exception e) {
			throw new SAXException("Failed to Process Query Param Bind[" + (isNamed ? name : id) + "]", e);
		}
	}

	/**
	 * Element Start SAX ContentHandler Method.
	 * @param uri
	 * @param localName
	 * @param qName
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 * @throws SAXException
	 * TODO: Document supported tags in javadoc.
	 */
	@SuppressWarnings({"unchecked"})
	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
		if("Query".equalsIgnoreCase(localName)) {
			queryName = attrs.getValue("name");
			rootElementName = queryName;
		} else if("Class".equalsIgnoreCase(localName)) {
			processCriteria(attrs);
		} else if("Union".equalsIgnoreCase(localName)) {
			inDetached = true;
			DetachedCriteria dc = DetachedCriteria.forEntityName(className);
			criteriaStack.push(dc);
		} else if("NamedQuery".equalsIgnoreCase(localName)) {
			isNamedQuery = true;
			namedQuery = attrs.getValue("name");
			rootElementName = namedQuery;
			try {
				query = session.getNamedQuery(namedQuery);
			} catch (HibernateException he) {
				throw new SAXException("Failed to retrieve named query[" + namedQuery + "]", he);
			}
		} else if("qparam".equalsIgnoreCase(localName)) {
			processQueryBind(attrs);
		} else if("Join".equalsIgnoreCase(localName)) {
			processCriteria(attrs);
		} else if("Projections".equalsIgnoreCase(localName)) {
				startProjection(attrs);
		} else if("Projection".equalsIgnoreCase(localName)) {
				addProjection(attrs);
		} else if("Order".equalsIgnoreCase(localName)) {
			if(isRowCountOnly()==false) {
				try {
					String name = attrs.getValue("name");
					String type = attrs.getValue("type");
					((Criteria)criteriaStack.peek()).addOrder(type.equalsIgnoreCase("asc") ? Order.asc(name) : Order.desc(name));
				} catch (Exception e) {
					throw new SAXException("Unable To Parse GreaterThan:" + attrs.getValue("name"), e);
				}
			}
		}else if("GreaterThan".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.gt(operator.getName(), operator.getNativeValue()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse GreaterThan:" + attrs.getValue("name"), e);
			}
		} else if("GreaterThanOrEqual".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.ge(operator.getName(), operator.getNativeValue()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse GreaterThanOrEqual:" + attrs.getValue("name"), e);
			}
		} else if("LessThan".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.lt(operator.getName(), operator.getNativeValue()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse LessThan:" + attrs.getValue("name"), e);
			}
		}  else if("LessThanOrEqual".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.le(operator.getName(), operator.getNativeValue()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse LessThanOrEqual:" + attrs.getValue("name"), e);
			}
		} else if("Equals".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				if("true".equalsIgnoreCase(attrs.getValue("not"))) {
					addCriterion(Restrictions.not(Restrictions.eq(operator.getName(), operator.getNativeValue())));
				} else {
					addCriterion(Restrictions.eq(operator.getName(), operator.getNativeValue()));
				}

			} catch (Exception e) {
				throw new SAXException("Unable To Parse Equals:" + attrs.getValue("name"), e);
			}
		} else if("Alias".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				((Criteria)criteriaStack.peek()).createAlias(operator.getName(), operator.getValue());
			} catch (Exception e) {
				throw new SAXException("Unable To Create Alias:" + attrs.getValue("name"), e);
			}
		} else if("GreaterThanProperty".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.gtProperty(operator.getName(), operator.getName2()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse GreaterThanProperty:" + attrs.getValue("name"), e);
			}
		} else if("GreaterThanOrEqualProperty".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.geProperty(operator.getName(), operator.getName2()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse GreaterThanOrEqualProperty:" + attrs.getValue("name"), e);
			}
		} else if("LessThanProperty".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.ltProperty(operator.getName(), operator.getName2()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse LessThanProperty:" + attrs.getValue("name"), e);
			}
		}  else if("LessThanOrEqualProperty".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.leProperty(operator.getName(), operator.getName2()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse LessThanOrEqualProperty:" + attrs.getValue("name"), e);
			}
		} else if("EqualsProperty".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				if("true".equalsIgnoreCase(attrs.getValue("not"))) {
					addCriterion(Restrictions.not(Restrictions.eqProperty(operator.getName(), operator.getName2())));
				} else {
					addCriterion(Restrictions.eqProperty(operator.getName(), operator.getName2()));
				}
			} catch (Exception e) {
				throw new SAXException("Unable To Parse EqualsProperty:" + attrs.getValue("name"), e);
			}
		} else if("Like".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.like(operator.getName(), operator.getNativeValue()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse Like:" + attrs.getValue("name"), e);
			}
		} else if("Between".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.between(operator.getName(), operator.getNativeValue(), operator.getNativeValue2()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse Between:" + attrs.getValue("name"), e);
			}
		}  else if("IsEmpty".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.isEmpty(operator.getName()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse IsEmpty:" + attrs.getValue("name"), e);
			}
		} else if("IsNotEmpty".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.isNotEmpty(operator.getName()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse IsNotEmpty:" + attrs.getValue("name"), e);
			}
		} else if("IsNull".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.isNull(operator.getName()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse IsNull:" + attrs.getValue("name"), e);
			}
		} else if("IsNotNull".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				addCriterion(Restrictions.isNotNull(operator.getName()));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse IsNotNull:" + attrs.getValue("name"), e);
			}
		} else if("In".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				if(operator.isLiteral()) {
					addCriterion(new LiteralInExpression(operator.getName(), (String)operator.getNativeValue()));
				} else {
					addCriterion(Restrictions.in(operator.getName(), (Collection)operator.getNativeValue()));
				}
			} catch (Exception e) {
				throw new SAXException("Unable To Parse In:" + attrs.getValue("name"), e);
			}
		}  else if("SizeEquals".equalsIgnoreCase(localName)) {
			try {
				Operator operator = new Operator(attrs);
				int i = ((Integer)operator.getNativeValue()).intValue();
				addCriterion(Restrictions.sizeEq(operator.getName(), i));
			} catch (Exception e) {
				throw new SAXException("Unable To Parse SizeEquals:" + attrs.getValue("name"), e);
			}
		} else if("Not".equalsIgnoreCase(localName)) {
			notStack.push(new Object());
		} else if("Or".equalsIgnoreCase(localName)) {
			opStack.push(Restrictions.disjunction());
		} else if("And".equalsIgnoreCase(localName)) {
			opStack.push(Restrictions.conjunction());
		} else {
			throw new SAXException("Element Name[" + localName + "] Not Recognized.");
		}
	}

	/**
	 * Adds a criterion to the current item on the opStack.
	 * If there are no items on the opStack, the criterion is added to the master criteria.
	 * If the master criteria is added to, the notStack is checked to see if the added criterion should be negated.
	 * @param criterion
	 */
	protected void addCriterion(Criterion criterion) {
		if(opStack.size()>0) {
			try {
				Object peekedObject = opStack.peek();
				peekedObject.getClass().getMethod("add", ADD_ARGS_TYPE).invoke(peekedObject, new Object[]{criterion});
			} catch (Exception e) {
				log.error("XMLQueryBuilder addCriterion Method Failed", e);
				throw new RuntimeException("XMLQueryBuilder addCriterion Method Failed", e);
			}
		} else {
			if(notStack.size()>0) {
				if(inDetached) {
					((DetachedCriteria)criteriaStack.peek()).add(Restrictions.not(criterion));
				} else {
					((Criteria)criteriaStack.peek()).add(Restrictions.not(criterion));
				}
			} else {
				if(inDetached) {
					((DetachedCriteria)criteriaStack.peek()).add(criterion);
				} else {
					((Criteria)criteriaStack.peek()).add(criterion);
				}
			}
		}
	}




	/**
	 * End element SAX ContentHandler Method.
	 * @param uri
	 * @param localName
	 * @param qName
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if("Or".equalsIgnoreCase(localName) || "And".equalsIgnoreCase(localName)) {
			Criterion criterion = (Criterion)opStack.pop();
			addCriterion(criterion);
		} else if("Join".equalsIgnoreCase(localName)) {
			criteriaStack.pop();
		} else if("Union".equalsIgnoreCase(localName)) {
			DetachedCriteria dc = (DetachedCriteria)criteriaStack.pop();
			inDetached = false;
			((Criteria)criteriaStack.peek()).add(UnionExpression.getInstance(className, session, dc));
		} else if("Projections".equalsIgnoreCase(localName)) {
			if(inDetached) {
				((DetachedCriteria)criteriaStack.peek()).setProjection(projectionStack.pop());
			} else {
				((Criteria)criteriaStack.peek()).setProjection(projectionStack.pop());
			}
		} else if("Projection".equalsIgnoreCase(localName)) {

		}  else if("Not".equalsIgnoreCase(localName)) {
			notStack.pop();
		}
	}

	/**
	 * Returns the result of the query.
	 * Value is null before SAX processing is complete.
	 * @return The result of the query.
	 */
	@SuppressWarnings("unchecked")
	public List getResult() {
		return result;
	}

	/**
	 * Unimplemented SAX ContentHandler Method.
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
	}

	/**
	 * Unimplemented SAX ContentHandler Method.
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {

	}

	/**
	 * Unimplemented SAX ContentHandler Method.
	 * @param arg0
	 * @param arg1
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {

	}

	/**
	 * Unimplemented SAX ContentHandler Method.
	 * @param arg0
	 * @throws SAXException
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String arg0) throws SAXException {
	}


	/**
	 * Returns the name of the query.
	 * Value is null before SAX processing is complete.
	 * @return The query name.
	 */
	public String getRootElementName() {
		return rootElementName;
	}

	/**
	 * Returns true if a non-projected query was forced to rowcount.
	 * @return true if query returns a forced rowcount only.
	 */
	public boolean isForceRowCountOnly() {
		return isRowCountOnly();
	}

	public void setForceRowCountOnly(boolean forceRowCountOnly) {
		setRowCountOnly(forceRowCountOnly);
	}

	/**
	 * Returns the column names for projection queries.
	 * Value is null before SAX processing is complete.
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * Returns the SAX Parse time.
	 * Value is invalid before SAX processing is complete.
	 * @return the parseTime
	 */
	public long getParseTime() {
		return parseTime;
	}

	/**
	 * Returns the alias list for the query.
	 * Value is null before SAX processing is complete.
	 * @return the aliases
	 */
	public String[] getAliases() {
		return aliases;
	}

	/**
	 * Returns the pseudo-entity name of the projection list for projection queries.
	 * Value is null before SAX processing is complete.
	 * @return the projectionsName
	 */
	public String getProjectionsName() {
		return projectionsName;
	}

	/**
	 * Returns the class name for the criteria/named query
	 * Value is null before SAX processing is complete.
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

}














