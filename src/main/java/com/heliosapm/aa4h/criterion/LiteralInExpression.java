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
package com.heliosapm.aa4h.criterion;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.util.StringHelper;

/**
 * <p>Title: LiteralInExpression</p>
 * <p>Description: Cirtierion implementation for binding literals into a criteria query.
 * This serves a number of purposes, but was implemented due to issues surrounding Oracle 10g with 
 * bind variables, table partitions and the cost based optimizer.</p>
 * <p>Currently only supports <code>String[]</code> types, but scheduled to be extended.</p>
 * <p>Based on an implementation by Steve Ebsersole.</p>
 * <p>Company: Helios Development Group LLC</p>
 * @author Steve Ebersole
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.aa4h.criterion.LiteralInExpression</code></p>
 */

public class LiteralInExpression implements Criterion {
	private static final long serialVersionUID = 1L;
	private final String propertyName;
	private final String valueList;

	/**
	 * Constructs a new LiteralInExpression criterion.
	 * @param propertyName The property name being bound to.
	 * @param values The values to be bound.
	 */
	public LiteralInExpression(String propertyName, String[] values) {
		this.propertyName = propertyName;
		this.valueList = StringHelper.join( ", ", values );
	}

	/**
	 * Constructs a new LiteralInExpression criterion.
	 * @param propertyName The property name being bound to.
	 * @param valueList The values to be bound.
	 */
	public LiteralInExpression(String propertyName, String valueList) {
		this.propertyName = propertyName;
		this.valueList = valueList;
	}

	/**
	 * Return typed values for all parameters in the rendered SQL fragment
	 * @param criteria
	 * @param criteriaQuery
	 * @return
	 * @see org.hibernate.criterion.Criterion#getTypedValues(org.hibernate.Criteria, org.hibernate.criterion.CriteriaQuery)
	 */
	public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) {
		// no parameters to deal with because we build a literal in expression
		return new TypedValue[0];
	}

	/**
	 * Render the SQL fragment
	 * @param criteria
	 * @param criteriaQuery
	 * @return the rendered SQL fragment
	 * @throws HibernateException
	 * @see org.hibernate.criterion.Criterion#toSqlString(org.hibernate.Criteria, org.hibernate.criterion.CriteriaQuery)
	 */
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		StringBuffer sqlFragment = new StringBuffer();
		String[] propertyColumnNames = criteriaQuery.getColumnsUsingProjection( criteria, propertyName );
		renderColumnNames( propertyColumnNames, sqlFragment );

		sqlFragment.append( " in (" ).append( valueList ).append( ')' );

		return sqlFragment.toString();
	}

	/**
	 * Renders the formatted literals for insertion into the rendered SQL fragment.
	 * @param propertyColumnNames
	 * @param sqlFragment
	 */
	private void renderColumnNames(String[] propertyColumnNames, StringBuffer sqlFragment) {
		if ( propertyColumnNames.length == 1 ) {
			sqlFragment.append( propertyColumnNames[0] );
		}
		else {
			sqlFragment.append( '(' ).append( StringHelper.join( ", ", propertyColumnNames ) ).append( ')' );
		}
	}
}
