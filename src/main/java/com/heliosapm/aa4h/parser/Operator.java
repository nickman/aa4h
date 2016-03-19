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
package com.heliosapm.aa4h.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.xml.sax.Attributes;


/**
 * <p>Title: Operator</p>
 * <p>Description: Utility class that provides type sensitive binding operation support between the XML nodes/attributes and the Hibernate Criteria API.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.aa4h.parser.Operator</code></p>
 */

public class Operator {
	protected Calendar calendar = Calendar.getInstance();
	protected static final String DATE_FORMAT = "yyyy-MM-dd";
	private String name = null;
	private String name2 = null;
	private String type = null;
	private String value = null;
	private String value2 = null;
	private String format = null;
	private boolean literal = false;

	/**
	 * Creates a new Operator from the supplied XML node attributes.
	 * @param attrs The XML node attributes.
	 */
	public Operator(Attributes attrs) {
		name = attrs.getValue("name");
		name2 = attrs.getValue("name2");
		type = attrs.getValue("type");
		value = attrs.getValue("value");
		value2 = attrs.getValue("value2");
		format = attrs.getValue("format");
		String tmp = attrs.getValue("literal");
		literal = "TRUE".equalsIgnoreCase(tmp) || "Y".equalsIgnoreCase(tmp);
		if(format==null) {
			format = DATE_FORMAT;
		}
	}

	/**
	 * Creates a new Operator for a specific binding.
	 * @param name The name of the property.
	 * @param type The type of the property.
	 * @param value The value to be bound.
	 * @param format The format of the supplied value.
	 */
	public Operator(String name, String type, String value, String format) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
		this.format = format;
		if(format==null) {
			format = DATE_FORMAT;
		}

	}

	/**
	 * The format of the supplied value.
	 * @return
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * Sets the format of the supplied value.
	 * @param format
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * The name of the property being bound.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the property being bound.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The secondary name of the property being bound.
	 * @return
	 */
	public String getName2() {
		return name2;
	}

	/**
	 * Sets the secondary name of the property being bound.
	 * @param name
	 */
	public void setName2(String name) {
		this.name2 = name;
	}

	/**
	 * The type of the property.
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type of the property.
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * The value of the property.
	 * @return
	 */
	public String getValue() {
		return value;
	}

	/**
	 * The secondary value of the property.
	 * @return
	 */
	public String getValue2() {
		return value2;
	}


	/**
	 * Sets the value of the property.
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Sets the secondary value of the property.
	 * @param value
	 */
	public void setValue2(String value) {
		this.value2 = value;
	}

	/**
	 * Generates an object of the correct native data type from the provided value for the specified type.
	 * @return An object of the defined type.
	 * @throws ParseException
	 */
	public Object getNativeValue() throws ParseException {
		return translateDataType(value, type, format);
	}

	/**
	 * Generates an object of the correct native data type from the provided secondary value for the specified type.
	 * @return An object of the defined type.
	 * @throws ParseException
	 */
	public Object getNativeValue2() throws ParseException {
		return translateDataType(value2, type, format);
	}


	/**
	 * Provides the underlying translation from the string type to the actual type.
	 * Essentially performs the function of a PropertyEditor.
	 * @param value
	 * @param type
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public Object translateDataType(String value, String type, String format) throws ParseException {
		Object ret = null;
		if("String".equalsIgnoreCase(type)) {
			ret = value;
		} else if("DateString".equalsIgnoreCase(type)) {
			if("TODAY".equalsIgnoreCase(value)) {
				calendar.setTime(new Date(System.currentTimeMillis()));
				calendar.set(Calendar.HOUR, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				ret = calendar.getTime();
			} else {
				SimpleDateFormat dateFormat = new SimpleDateFormat(format);
				ret = dateFormat.parse(value);
			}
		} else if("int".equalsIgnoreCase(type)) {
			ret = new Integer(value);
		} else if("long".equalsIgnoreCase(type)) {
			ret = new Long(value);
		} else if("boolean".equalsIgnoreCase(type)) {
			ret = new Boolean(value);
		} else if("int[]".equalsIgnoreCase(type)) {
			String[] s = value.split(format);
			ArrayList values = new ArrayList(s.length);
			for(int i = 0; i < s.length; i++) {
				values.add(new Integer(s[i]));
			}
			ret = values;
		} else if("long[]".equalsIgnoreCase(type)) {
			String[] s = value.split(format);
			ArrayList values = new ArrayList(s.length);

			for(int i = 0; i < s.length; i++) {
				values.add(new Long(s[i]));
			}
			ret = values;
		}  else if("String[]".equalsIgnoreCase(type)) {
			if(!isLiteral()) {
				String[] s = value.split(format);
				ArrayList values = new ArrayList(s.length);
				for(int i = 0; i < s.length; i++) {
					values.add(s[i]);
				}
				ret = values;
			} else {
				StringBuilder buff = new StringBuilder();
				String[] s = value.split(format);
				for(String val: s) {
					buff.append("'").append(val.trim()).append("',");
				}
				buff.deleteCharAt(buff.length()-1);

				return buff.toString();
			}
		} else {
			ret = value;
		}
		return ret;
	}

	/**
	 * Returns the literal status of the Operation.
	 * @return true if the current operation is defining a literal to be bound.
	 */

	public boolean isLiteral() {
		return literal;
	}

	/**
	 * Sets the literal status of the Operation.
	 * @param literal set true if the Operation represents a literal bind.
	 */
	public void setLiteral(boolean literal) {
		this.literal = literal;
	}


}
