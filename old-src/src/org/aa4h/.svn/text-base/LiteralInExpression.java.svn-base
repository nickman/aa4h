/**
 * AA4H: Ajax Adapter For Hibernate
 * Helios Development Group
 */
package org.aa4h;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;
import org.hibernate.util.StringHelper;

/**
 * <p>Title: LiteralInExpression</p>
 * <p>Description: Cirtierion implementation for binding literals into a criteria query.
 * This serves a number of purposes, but was implemented due to issues surrounding Oracle 10g with 
 * bind variables, table partitions and the cost based optimizer.</p>
 * <p>Currently only supports <code>String[]</code> types, but scheduled to be extended.</p>
 * <p>Based on an implementation by Steve Ebsersole.</p>
 * <p>Company: Helios Development Group, 2008</p>
 * @author WhiteheN
 * @author Steve Ebersole
 * @version $Revision$
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





























