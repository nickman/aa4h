/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h.interceptors;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.hibernate.EmptyInterceptor;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;




/**
 * <p>Title: HibernateSQLLoggingInterceptor</p>
 * <p>Description: A Hibernate Interceptor that traces SQL statements issued to an MBean.</p>
 * <p>Deployment Notes:<ul>
 * <li>This class should be registered in a <code>SessionFactoryInterceptor</code> element in the hibernate session factory descriptor.</li>
 * <li>Configuration System Properties:<ul>
 * <li><code>hibernate.sql.logging.domain</code>: The domain name of the JMX Agent where the target MBean should be registered. Defaults to <code>jboss</code></li>
 * <li><code>hibernate.sql.logging.mbean</code>: The JMX MBean ObjectName name of the target MBean. Defaults to <code>jboss.hibernate:service=SQLLogger</code></li>
 * </ul></ul>
 * <p>Company: Helios Development Group, 2008</p>
 * @author Whitehead
 * @version $Revision: 1.2 $
 */

public class HibernateSQLLoggingInterceptor extends EmptyInterceptor implements NotificationListener {

	private static final long serialVersionUID = 7974188588689279334L;
	protected ObjectName objectName = null;
	protected MBeanServer server = null;
	protected HibernateSQLLoggingServiceMBean logger = null;
	protected String agentDomain = null;
	protected String loggerMBeanName = null;
	protected boolean enabled = false;
	protected AtomicLong sequence = new AtomicLong();
	
	public static final String AGENT_DOMAIN = "hibernate.sql.logging.domain";
	public static final String MBEAN_NAME = "hibernate.sql.logging.mbean";
	
	/**
	 * Instantiates a new HibernateSQLLoggingInterceptor.
	 * Acquires the target MBeanServer and SQLLoggerMBean from system properties.
	 */
	public HibernateSQLLoggingInterceptor() throws Exception {
		agentDomain = System.getProperty(AGENT_DOMAIN, "jboss");
		loggerMBeanName = System.getProperty(MBEAN_NAME, "jboss.hibernate:service=SQLLogger");
		server = getMBeanServer(agentDomain);
		if(server==null) throw new Exception("MBeanServer for Domain[" + agentDomain + "] Not Found");
		
		objectName = new ObjectName(loggerMBeanName);
		logger = (HibernateSQLLoggingServiceMBean)MBeanServerInvocationHandler.newProxyInstance(server, objectName, HibernateSQLLoggingServiceMBean.class, true);
		server.addNotificationListener(objectName, this, null, "");
		enabled = logger.isOn();
	}
	
	
	/**
	 * Instantiates a new HibernateSQLLoggingInterceptor with a specified MBeanServer and ObjectName.
	 * @param loggerMBeanServer The target SQLLoggerMBean.
	 * @param loggerObjectName The target MBeanServer where the SQLLoggerMBean is registered.
	 * @throws Exception
	 */
	public HibernateSQLLoggingInterceptor(MBeanServer loggerMBeanServer, ObjectName loggerObjectName) throws Exception {
		server = loggerMBeanServer;
		objectName = loggerObjectName;
		logger = (HibernateSQLLoggingServiceMBean)MBeanServerInvocationHandler.newProxyInstance(server, objectName, HibernateSQLLoggingServiceMBean.class, true);
		server.addNotificationListener(objectName, this, null, "");
		enabled = logger.isOn();
	}
	
	
	/**
	 * Acquires the local MBeanServer with the passed default domain.
	 * @param agentDomain
	 * @return An MBeanServer or null if one is not located for the passed domain.
	 */
	@SuppressWarnings("unchecked")
	public MBeanServer getMBeanServer(String agentDomain) {
		List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
		if(servers==null) return null;
		for(MBeanServer ms : servers) {
			if(ms.getDefaultDomain().equals(agentDomain)) return ms;
		}
		return null;
	}


	/**
	 * Called when sql string is being prepared.
	 * If the MBean is connected, will trace the sql statement to the MBean.
	 * If it is not, it will attempt to connect.
	 * @param sql SQL to be prepared
	 * @return The unmodified SQL
	 * @see org.hibernate.EmptyInterceptor#onPrepareStatement(java.lang.String)
	 */
	public String onPrepareStatement(String sql)  {
		if(enabled) {
			long seq = sequence.incrementAndGet();
			logger.submit(seq, sql);
		}
		return sql;
		
	}


	/**
	 * Listens on the logger MBean enabled status change notification and sets the local enabled flag accordingly.
	 * @param notification
	 * @param handback
	 * @see javax.management.NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
	 */
	public void handleNotification(Notification notification, Object handback) {
		if(HibernateSQLLoggingServiceMBean.SET_DISABLED_EVENT.equals(notification.getMessage())) {
			enabled = false;
		} else if(HibernateSQLLoggingServiceMBean.SET_ENABLED_EVENT.equals(notification.getMessage())) {
			enabled = true;
		}
	}
}
