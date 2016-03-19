/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.com.heliosapm.aa4h;

import java.net.URL;

import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.internal.SessionImpl;
import org.hibernate.internal.util.xml.XmlDocument;
import org.hibernate.internal.util.xml.XmlDocumentImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 * <p>Title: HibernateBaseTest</p>
 * <p>Description: Base hibernate test</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>test.com.heliosapm.aa4h.HibernateBaseTest</code></p>
 */

@Ignore
public class HibernateBaseTest extends BaseTest {
	/** The hibernate configuration for this test */
	static Configuration configuration = null;
	/** The hibernate session factory for this test */
	static SessionFactory sessionFactory = null;
	/** The hibernate session for this test */
	static Session session = null;
	/** The hibernate session impl to provide a test connection for this test */
	static SessionImpl sessionImpl = null;
	
	/** The hibernate service registry */
	static StandardServiceRegistry registry = null; 
	/** The data source, referenced so we can create our JDBCHelper */
	static DataSource dataSource = null;
	
	/** The JDBC Helper */
	static JDBCHelper jdbcHelper = null;
	
	
	
	public static void main(String[] args) {
		HibernateBaseTest t = new HibernateBaseTest();
		try {
			t.setupHibernate();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		} finally {
			t.tearDownHibernate();
			System.exit(-1);
		}
	}

	/**
	 * Initializes the hibernate session factory
	 * @throws Exception thrown on any error
	 */
	@SuppressWarnings("deprecation")
	@BeforeClass
	public static void setupHibernate() throws Exception {
		try {
			configuration = new Configuration();
			
			configuration.add(xmlFor("mappings/Bonus.hbm.xml"));
			configuration.add(xmlFor("mappings/Dept.hbm.xml"));
			configuration.add(xmlFor("mappings/Emp.hbm.xml"));
			configuration.add(xmlFor("mappings/Job.hbm.xml"));
			configuration.add(xmlFor("mappings/Salgrade.hbm.xml"));
			configuration.setProperty("hibernate.dialect", Oracle10gDialect.class.getName());
			configuration.setProperty("hibernate.show_sql", "true");
			configuration.setProperty("hibernate.format_sql", "true");
			configuration.setProperty("hibernate.generate_statistics", "true");
			configuration.setProperty("hibernate.connection.user", "scott");
			configuration.setProperty("hibernate.connection.password", "tiger");
			configuration.setProperty("hibernate.cache.region.factory_class", "org.hibernate.testing.cache.CachingRegionFactory");
			configuration.setProperty("hibernate.connection.provider_class", "com.zaxxer.hikari.hibernate.HikariConnectionProvider");
			configuration.setProperty("hibernate.hikari.minimumIdle", "5");
			configuration.setProperty("hibernate.hikari.maximumPoolSize", "10");
			configuration.setProperty("hibernate.hikari.idleTimeout", "15000");
			configuration.setProperty("hibernate.hikari.dataSourceClassName", "oracle.jdbc.pool.OracleDataSource");
			configuration.setProperty("hibernate.hikari.dataSource.url", "jdbc:oracle:thin:@//localhost:1521/xe");
			configuration.setProperty("hibernate.hikari.dataSource.user", "scott");
			configuration.setProperty("hibernate.hikari.dataSource.password", "tiger");
	//		configuration.setProperty("", "true");
	//		configuration.setProperty("", "true");
	//		configuration.setProperty("", "true");
	//		configuration.setProperty("", "true");
			
	//		hibernate.connection.provider_class=com.zaxxer.hikari.hibernate.HikariConnectionProvider
	//				hibernate.hikari.minimumIdle=5
	//				hibernate.hikari.maximumPoolSize=10
	//				hibernate.hikari.idleTimeout=30000
	//				hibernate.hikari.dataSourceClassName=com.mysql.jdbc.jdbc2.optional.MysqlDataSource
	//				hibernate.hikari.dataSource.url=jdbc:mysql://localhost/database
	//				hibernate.hikari.dataSource.user=bart
	//				hibernate.hikari.dataSource.password=51mp50n		
			
	//	    configuration.setProperty("hibernate.connection.driver_class", "oracle.jdbc.OracleDriver");
	//		configuration.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@//localhost:1521/xe");
			configuration.setProperty("hibernate.hbm2ddl.auto", "validate");
			log("Config: " + configuration);
			registry = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.build();
			sessionFactory = configuration.buildSessionFactory(registry);
			log("SessionFactory: " + sessionFactory);
			session = sessionFactory.openSession();		
			log("Acquired Session: " + session);
			sessionImpl = (SessionImpl)sessionFactory.openSession();
			dataSource = sessionImpl.getSessionFactory().getConnectionProvider().unwrap(javax.sql.DataSource.class);
			jdbcHelper = new JDBCHelper(dataSource);
			log("Acquired SessionImpl: " + sessionImpl);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			throw ex;
		}
	}
	
	/**
	 * Tears down hibernate after the test
	 */
	@AfterClass
	public static void tearDownHibernate() {
		log("Starting Hibernate Shutdown....");
		try { session.close(); } catch (Exception x) {/* No Op */}
		try { sessionImpl.close(); } catch (Exception x) {/* No Op */}
		try { sessionFactory.close(); } catch (Exception x) {/* No Op */}
		
		log("Cleaned Up Hibernate");
	}
	
	/**
	 * Parses the content read from the passed resource name into a hibernate xml document
	 * @param resourceName The resource name which will be used to load the resource from the classpath
	 * @return the hibernate xml document
	 * @throws Exception on any error
	 */
	public static XmlDocument xmlFor(final String resourceName) throws Exception {
		SAXReader reader = new SAXReader();
		final URL url = HibernateBaseTest.class.getClassLoader().getResource(resourceName);
		log("URL:" + url);
	    Document document = reader.read(url);
		return new XmlDocumentImpl(document, "", "");
	}
	
	
	
	
}
