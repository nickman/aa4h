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

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.junit.Assert;
import org.junit.Test;

import com.heliosapm.aa4h.parser.XMLQueryParser;
import com.heliosapm.utils.url.URLHelper;

import test.com.heliosapm.aa4h.pojos.Emp;

/**
 * <p>Title: XMLQueryTest</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>test.com.heliosapm.aa4h.XMLQueryTest</code></p>
 */

public class XMLQueryTest extends HibernateBaseTest {
	/** The current XML to test */
	protected String xmlQuery = null;

	
	
	/**
	 * Tests a basic row count
	 * @throws Exception on any error
	 */
	@Test
	public void testEmpCount() throws Exception {
		setXmlQuery();
		final List<?> result = XMLQueryParser.execute(xmlQuery, session);
		Assert.assertNotNull("Result was null", result);
		Assert.assertFalse("Result was empty", result.isEmpty());
		final long hCount = (Long)result.iterator().next();
		final int jdbcCount = jdbcHelper.queryForInt("SELECT COUNT(*) FROM EMP");
		Assert.assertEquals("Count not [" + jdbcCount + "]", jdbcCount, hCount);
	}
	
	/**
	 * Tests a basic row count with a group by job
	 * @throws Exception on any error
	 */
	@Test
	public void testEmpCountByJobDept() throws Exception {
		List results = session.createCriteria(Emp.class)
				.createAlias("job", "j")
				.createAlias("dept", "d")
			    .setProjection( Projections.projectionList()
			        .add( Projections.rowCount(), "empCount" )
			        .add(  Projections.groupProperty("j.jobName"), "jobid" )
			        .add(  Projections.groupProperty("d.dname"), "department" )
			    )
			    .list();
		for(Object o: results) {
			log(o);
		}
		
		setXmlQuery();
		final List<?> result = XMLQueryParser.execute(xmlQuery, session);
		Assert.assertNotNull("Result was null", result);
		Assert.assertFalse("Result was empty", result.isEmpty());
		for(Object o: result) {
			log(o);
		}
		//final Map<Object, Object> map = jdbcHelper.queryForMap("SELECT JOB_NAME, COUNT(*) FROM EMP E, JOB J WHERE E.JOB_ID = J.JOB_ID GROUP BY JOB_NAME");
		//Assert.assertEquals("Count not [" + jdbcCount + "]", jdbcCount, hCount);
	}
	
	@Test
	public void testGetJobDepartmentCount() throws Exception {
		setXmlQuery();
		final List<?> result = XMLQueryParser.execute(xmlQuery, session);
		Assert.assertNotNull("Result was null", result);
		Assert.assertFalse("Result was empty", result.isEmpty());
		for(Object o: result) {
			log(o);
		}
	}
	
	/**
	 * Sets the XML query text according to the method name
	 */
	public void setXmlQuery() {
		String tname = name.getMethodName().replaceFirst("test", "");
		xmlQuery = URLHelper.getTextFromURL(getClass().getClassLoader().getResource("xmlqueries/" + tname + ".xml"));
	}
	
}
