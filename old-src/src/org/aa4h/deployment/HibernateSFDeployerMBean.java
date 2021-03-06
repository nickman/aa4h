/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h.deployment;

import org.jboss.system.ServiceMBean;
import org.hibernate.SessionFactory;

import javax.management.MBeanRegistration;
import javax.management.ObjectName;
import java.net.URL;
import java.util.Date;


/**
 * <p>Title: HibernateSFDeployerMBean</p>
 * <p>Description: A custom extension of the JBoss Hibernate Deployer MBean Interface.
 * This is a straight code copy from <code>org.jboss.hibernate.jmx.Hibernate</code> and then extended since
 * all the fields and methods were declared private.</p>
 * <p>Company: Helios Development Group</p>
 * @author Whitehead
 * @author <a href="mailto:alex@jboss.org">Alexey Loubyansky</a>
 * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole</a>
 * @author <a href="mailto:dimitris@jboss.org">Dimitris Andreadis</a> 
 * @version $Revision$
 */


public interface HibernateSFDeployerMBean extends ServiceMBean, MBeanRegistration {
	/**
	    * The url to the har deployment, if MBean is operating in har deployment mode.
		 *
		 * @return The url of the har containing this MBean, or null if not part of
		 * a har deployment.
		 */
		public URL getHarUrl();

		/**
		 * Enables scanning of the entire deployment classpath for any potential mapping
		 * sources (jars or directories).
		 * <p/>
		 * Only used in the case of har deployments.
		 *
		 * @return
		 */
		public boolean isScanForMappingsEnabled();

		/**
		 * Enables scanning of the entire deployment classpath for any potential mapping
		 * sources (jars or directories).
		 * <p/>
		 * Only used in the case of har deployments.
		 *
		 * @param scanForMappings
		 */
		public void setScanForMappingsEnabled(boolean scanForMappings);

		/**
		 * The JNDI namespace where the managed {@link org.hibernate.SessionFactory} is to be bound.
		 *
		 * @return The current setting value.
		 */
		public String getSessionFactoryName();

		/**
		 * The JNDI namespace where the managed {@link org.hibernate.SessionFactory} is to be bound.
		 *
		 * @param sessionFactoryName The new JNDI namespace to use.
		 */
		public void setSessionFactoryName(String sessionFactoryName);

		/**
		 * The JMX name of a {@link org.jboss.cache.TreeCache} MBean to be used as the second level cache.
		 * <p/>
		 * Note : only used when {@link #getCacheProviderClass} == {@link org.jboss.hibernate.cache.DeployedTreeCacheProvider}
		 *
		 * @return The current setting
		 */
		public ObjectName getDeployedTreeCacheObjectName();

		/**
		 * The JMX name of a {@link org.jboss.cache.TreeCache} MBean to be used as the second level cache.
		 *
		 * @param deployedTreeCacheObjectName The new mbean object name.
		 */
		public void setDeployedTreeCacheObjectName(ObjectName deployedTreeCacheObjectName);

		/**
		 * Retreive the service name of the managed stats mbean.
		 * <p/>
		 * When statistics are enabled on the managed session factory, the mbean
		 * automatically manages a stats mbean for stats exposure via jmx.  This
		 * returns the name under which that stats mbean is available from the jmx
		 * server.
		 *
		 * @return The service name of the stats mbean, or null if stats not enabled.
		 */
		public ObjectName getStatisticsServiceName();

		/**
		 * The name of the dialect class to use for communicating with the database.
		 *
		 * @return The current setting value.
		 *
		 * @see org.hibernate.cfg.Environment#DIALECT
		 */
		public String getDialect();

		/**
		 * The name of the dialect class to use for communicating with the database.
		 *
		 * @param dialect The new dialect class name to use.
		 */
		public void setDialect(String dialect);

		/**
		 * The form, if any, of schema generation which should be used.
		 *
		 * @return The current setting value.
		 *
		 * @see org.hibernate.cfg.Environment#HBM2DDL_AUTO
		 */
		public String getHbm2ddlAuto();

		/**
		 * The form, if any, of schema generation which should be used.
		 *
		 * @param hbm2ddlAuto The new hbm2ddl setting; valid values are: update, create, create-drop
		 */
		public void setHbm2ddlAuto(String hbm2ddlAuto);

		/**
		 * The JNDI namespace of the {@link javax.sql.DataSource} which should be used by the managed {@link
		 * org.hibernate.SessionFactory}.
		 *
		 * @return The current setting value.
		 *
		 * @see org.hibernate.cfg.Environment#DATASOURCE
		 */
		public String getDatasourceName();

		/**
		 * The JNDI namespace of the {@link javax.sql.DataSource} which should be used by the managed {@link
		 * org.hibernate.SessionFactory}.
		 *
		 * @param datasourceName The new DataSource JNDI name to use.
		 */
		public void setDatasourceName(String datasourceName);

		/**
		 * The username used to access the specified datasource.
		 *
		 * @return The current setting value.
		 *
		 * @see org.hibernate.cfg.Environment#USER
		 */
		public String getUsername();

		/**
		 * The username used to access the specified datasource.
		 *
		 * @param username The new username value.
		 */
		public void setUsername(String username);

		/**
		 * The password used to access the specified datasource.
		 *
		 * @param password The new password value.
		 */
		public void setPassword(String password);

		/**
		 * Should sql comments be used?
		 *
		 * @return
		 *
		 * @see org.hibernate.cfg.Environment#USE_SQL_COMMENTS
		 */
		public Boolean getSqlCommentsEnabled();

		/**
		 * Should sql comments be used?
		 *
		 * @param commentsEnabled
		 */
		public void setSqlCommentsEnabled(Boolean commentsEnabled);

		/**
		 * The default database schema to use within the database being mapped.
		 * <p/>
		 * Used for databases which support the concept of schemas instead of catalogs.
		 *
		 * @return The current setting value.
		 *
		 * @see #getDefaultCatalog
		 * @see org.hibernate.cfg.Environment#DEFAULT_SCHEMA
		 */
		public String getDefaultSchema();

		/**
		 * The default database schema to use within the database being mapped.
		 *
		 * @param defaultSchema The new default schema name to use.
		 */
		public void setDefaultSchema(String defaultSchema);

		/**
		 * The default database catalog to use within the database being mapped.
		 * <p/>
		 * Used for databases which support the concept of catalogs instead of schemas.
		 *
		 * @return The current setting value.
		 *
		 * @see #getDefaultSchema
		 * @see org.hibernate.cfg.Environment#DEFAULT_CATALOG
		 */
		public String getDefaultCatalog();

		/**
		 * The default database catalog to use within the database being mapped.
		 *
		 * @param defaultCatalog The new default catalog name.
		 */
		public void setDefaultCatalog(String defaultCatalog);

		/**
		 * The maximum outer join fetch depth.
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#MAX_FETCH_DEPTH
		 */
		public Integer getMaxFetchDepth();

		/**
		 * The maximum outer join fetch depth.
		 *
		 * @param maxFetchDepth The new max fetch depth value
		 */
		public void setMaxFetchDepth(Integer maxFetchDepth);

		/**
		 * The JDBC batch update batch size.
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#STATEMENT_BATCH_SIZE
		 */
		public Integer getJdbcBatchSize();

		/**
		 * The JDBC batch update batch size.
		 *
		 * @param jdbcBatchSize The new value for the number of statements to batch together.
		 */
		public void setJdbcBatchSize(Integer jdbcBatchSize);

		/**
		 * The JDBC fetch size.
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#STATEMENT_FETCH_SIZE
		 */
		public Integer getJdbcFetchSize();

		/**
		 * The JDBC fetch size.
		 *
		 * @param jdbcFetchSize The new value for the number of rows to fetch from server at a time.
		 */
		public void setJdbcFetchSize(Integer jdbcFetchSize);

		/**
		 * Are scrollable result sets enabled?
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#USE_SCROLLABLE_RESULTSET
		 */
		public Boolean getJdbcScrollableResultSetEnabled();

		/**
		 * Are scrollable result sets enabled?
		 *
		 * @param jdbcScrollableResultSetEnabled The new value.
		 */
		public void setJdbcScrollableResultSetEnabled(Boolean jdbcScrollableResultSetEnabled);

		/**
		 * Is the use of JDBC3 <tt>getGeneratedKeys()</tt> enabled?
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#USE_GET_GENERATED_KEYS
		 */
		public Boolean getGetGeneratedKeysEnabled();

		/**
		 * Is the use of JDBC3 <tt>getGeneratedKeys()</tt> enabled?
		 *
		 * @param getGeneratedKeysEnabled The new value.
		 */
		public void setGetGeneratedKeysEnabled(Boolean getGeneratedKeysEnabled);

		/**
		 * Should Hibernate allow JDBC batch-updating of versioned entities?
		 * <p/>
		 * Many drivers have bugs regarding the row counts returned in response to JDBC Batch API operations; in these cases,
		 * this should definitely be set to false.
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#BATCH_VERSIONED_DATA
		 */
		public Boolean getBatchVersionedDataEnabled();

		/**
		 * Should Hibernate allow JDBC batch-updating of versioned entities?
		 *
		 * @param batchVersionedDataEnabled
		 */
		public void setBatchVersionedDataEnabled(Boolean batchVersionedDataEnabled);

		/**
		 * Should Hibernate use I/O streaming for handling binary/LOB data?
		 *
		 * @return
		 *
		 * @see org.hibernate.cfg.Environment#USE_STREAMS_FOR_BINARY
		 */
		public Boolean getStreamsForBinaryEnabled();

		/**
		 * Should Hibernate use I/O streaming for handling binary/LOB data?
		 *
		 * @param streamsForBinaryEnabled
		 */
		public void setStreamsForBinaryEnabled(Boolean streamsForBinaryEnabled);

		/**
		 * Query substitutions to use.
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#QUERY_SUBSTITUTIONS
		 */
		public String getQuerySubstitutions();

		/**
		 * Query substitutions to use.
		 *
		 * @param querySubstitutions The new query substitutions to use
		 */
		public void setQuerySubstitutions(String querySubstitutions);

		/**
		 * The name of the {@link org.hibernate.cache.CacheProvider} implementation class to use for second level caching.
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#CACHE_PROVIDER
		 */
		public String getCacheProviderClass();

		/**
		 * The name of the {@link org.hibernate.cache.CacheProvider} implementation class to use for second level caching.
		 *
		 * @param cacheProviderClass The new provider impl class name.
		 */
		public void setCacheProviderClass(String cacheProviderClass);

		/**
		 * The prefix to use for this session factory within the second level cache.
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#CACHE_NAMESPACE
		 */
		public String getCacheRegionPrefix();

		/**
		 * The prefix to use for this session factory within the second level cache.
		 *
		 * @param cacheRegionPrefix The new prefix value.
		 */
		public void setCacheRegionPrefix(String cacheRegionPrefix);

		/**
		 * Should minimal puts be enabled against the given cache provider?
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#USE_MINIMAL_PUTS
		 */
		public Boolean getMinimalPutsEnabled();

		/**
		 * Should minimal puts be enabled against the given cache provider?
		 *
		 * @param minimalPutsEnabled
		 */
		public void setMinimalPutsEnabled(Boolean minimalPutsEnabled);

		/**
		 * Should Hibernate use structured cache entries when putting stuff into the second level cache?
		 * <p/>
		 * Mainly useful if users wish to directly browse the second level caches as it is easier to see what the cache entries
		 * actually represent.
		 *
		 * @return
		 *
		 * @see org.hibernate.cfg.Environment#USE_STRUCTURED_CACHE
		 */
		public Boolean getUseStructuredCacheEntriesEnabled();

		/**
		 * Should Hibernate use structured cache entries when putting stuff into the second level cache?
		 *
		 * @param structuredEntriesEnabled
		 */
		public void setUseStructuredCacheEntriesEnabled(Boolean structuredEntriesEnabled);

		public Boolean getSecondLevelCacheEnabled();

		public void setSecondLevelCacheEnabled(Boolean secondLevelCacheEnabled);

		/**
		 * Is use of the query cache enabled?
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#USE_QUERY_CACHE
		 */
		public Boolean getQueryCacheEnabled();

		/**
		 * Is use of the query cache enabled?
		 *
		 * @param queryCacheEnabled The new value of whether or not to enable.
		 */
		public void setQueryCacheEnabled(Boolean queryCacheEnabled);

		/**
		 * Should all SQL be shown (dumped to console and logged)?
		 *
		 * @return The current setting value
		 *
		 * @see org.hibernate.cfg.Environment#SHOW_SQL
		 */
		public Boolean getShowSqlEnabled();

		/**
		 * Should all SQL be shown (dumped to console and logged)?
		 *
		 * @param showSqlEnabled
		 */
		public void setShowSqlEnabled(Boolean showSqlEnabled);

		/**
		 * Should Hibernate use cglib-based reflection optimizations?
		 * <p/>
		 * Note : this may or may not improve performance based on the JVM you are using.
		 *
		 * @return
		 *
		 * @see org.hibernate.cfg.Environment#USE_REFLECTION_OPTIMIZER
		 */
		public Boolean getReflectionOptimizationEnabled();

		/**
		 * Should Hibernate use cglib-based reflection optimizations?
		 *
		 * @param reflectionOptimizationEnabled
		 */
		public void setReflectionOptimizationEnabled(Boolean reflectionOptimizationEnabled);

		/**
		 * Should generation and collection of Hibernate3 statistics be enabled?
		 *
		 * @return
		 *
		 * @see org.hibernate.cfg.Environment#GENERATE_STATISTICS
		 */
		public Boolean getStatGenerationEnabled();

		/**
		 * Should generation and collection of Hibernate3 statistics be enabled?
		 *
		 * @param statGenerationEnabled
		 */
		public void setStatGenerationEnabled(Boolean statGenerationEnabled);

		/**
		 * The name of an {@link org.hibernate.Interceptor} impl class to be attached to the managed {@link
		 * org.hibernate.SessionFactory}.
		 *
		 * @return
		 */
		public String getSessionFactoryInterceptor();

		/**
		 * The name of an {@link org.hibernate.Interceptor} impl class to be attached to the managed {@link
		 * org.hibernate.SessionFactory}.
		 *
		 * @param sessionFactoryInterceptor
		 */
		public void setSessionFactoryInterceptor(String sessionFactoryInterceptor);

		/**
		 * The {@link org.jboss.hibernate.ListenerInjector} implementor class to use.
		 *
		 * @return
		 */
		public String getListenerInjector();

		/**
		 * The {@link org.jboss.hibernate.ListenerInjector} implementor class to use.
		 *
		 * @param listenerInjector
		 */
		public void setListenerInjector(String listenerInjector);

		/**
		 * Is this MBean dirty?  Meaning, have any changes been made to it that have not yet been propogated to the managed
		 * {@link org.hibernate.SessionFactory}?
		 * <p/>
		 * Note : the only way to propogate these changes to the SF is by calling the {@link #rebuildSessionFactory()} managed
		 * operation.
		 *
		 * @return
		 */
		public boolean isDirty();

		/**
		 * Does this MBean instance have a currently running managed {@link org.hibernate.SessionFactory}?
		 *
		 * @return
		 */
		public boolean isSessionFactoryRunning();

		/**
		 * The version Hibernate for the managed {@link org.hibernate.SessionFactory}.
		 *
		 * @return
		 */
		public String getVersion();

		/**
		 * Exposes the internally managed session factory via a read-only JMX managed attribute.
		 *
		 * @return The managed session factory.
		 */
		public SessionFactory getInstance();

	   /**
	    * The date and time since which the currently managed {@link org.hibernate.SessionFactory} has been running.
	    *
	    * @return The date and time the current {@link org.hibernate.SessionFactory} was started.
	    */
	   public Date getRunningSince();

		/**
		 * Export the <tt>CREATE</tt> DDL to the database
		 * @throws Exception
		 */
		public void createSchema() throws Exception;
		/**
		 * Export the <tt>DROP</tt> DDL to the database
		 * @throws Exception
		 */
		public void dropSchema() throws Exception;	   
	   
		/**
		 * A JMX managed operation to rebuild the managed {@link org.hibernate.SessionFactory} such that any setting changes
		 * made can take effect.
		 *
		 * @throws Exception
		 */
		public void rebuildSessionFactory() throws Exception;
		
		/**
		 * @return the sqlLoggerObjectName
		 */
		public ObjectName getSqlLoggerObjectName();
		/**
		 * @param sqlLoggerObjectName the sqlLoggerObjectName to set
		 */
		public void setSqlLoggerObjectName(ObjectName sqlLoggerObjectName);
		
		
		public void setSQLLoggerOn(boolean on);
		public boolean isSQLLoggerOn();
		
		public void setSQLLoggerMaxQueueSize(int queueSize);
		public int getSQLLoggerMaxQueueSize();
		
		public void setSQLLoggerMaxStatements(int statements);
		public int getSQLLoggerMaxStatements();
		
		public void setSQLLoggerWaitTime(long waitTime);
		public long getSQLLoggerWaitTime();
		

}
