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

import java.io.IOException;
import java.util.Stack;

import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Title: JSONQueryParser</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.aa4h.parser.JSONQueryParser</code></p>
 */

public class JSONQueryParser implements ContentHandler {
	
	private static final String TEST = "{\"Query\":{\"name\":\"GetAllEmps\",\"Class\":{\"prefix\":\"org.aa4h.samples\",\"name\":\"Emp\",\"rowCountOnly\":false}}}";
	private static final String TEST2 = "{\"Query\":{\"Alias\":[{\"name\":\"job\",\"value\":\"j\"},{\"name\":\"dept\",\"value\":\"d\"}],\"name\":\"GetAllEmps\",\"Class\":{\"prefix\":\"org.aa4h.samples\",\"name\":\"Emp\",\"rowCountOnly\":false},\"Projections\":{\"name\":\"DepartmentJobCount\",\"Projection\":[{\"name\":\"j.jobName\",\"alias\":\"jobname\",\"type\":\"groupProperty\"},{\"name\":\"d.dname\",\"alias\":\"department\",\"type\":\"groupProperty\"},{\"name\":\"empno\",\"alias\":\"count\",\"type\":\"count\"}]}}}";
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	protected final JSONParser parser = new JSONParser();
	protected final StringBuilder b = new StringBuilder();
	protected final Stack<String> stack = new Stack<String>();
	
	/**
	 * Creates a new JSONQueryParser
	 */
	public JSONQueryParser() {
		log.info("Created parser");
	}
	
	public static void main(String[] args) {
		JSONQueryParser parser = new JSONQueryParser();
		try {
			parser.parse(TEST2);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
	}
	
	public void parse(final CharSequence query) throws ParseException {
		parser.parse(query.toString(), this);
	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#endArray()
	 */
	@Override
	public boolean endArray() throws ParseException, IOException {
//		log.info("endArray");
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#endJSON()
	 */
	@Override
	public void endJSON() throws ParseException, IOException {
//		log.info("endJSON");
	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#endObject()
	 */
	@Override
	public boolean endObject() throws ParseException, IOException {
//		log.info("endObject");
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#endObjectEntry()
	 */
	@Override
	public boolean endObjectEntry() throws ParseException, IOException {
//		log.info("endObjectEntry");
		b.deleteCharAt(b.length()-1);
		final String key = stack.pop();
		log.info("{}end [{}]", b, key);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#primitive(java.lang.Object)
	 */
	@Override
	public boolean primitive(final Object p) throws ParseException, IOException {
		log.info("{}primitive: [{}]", b, p);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#startArray()
	 */
	@Override
	public boolean startArray() throws ParseException, IOException {
//		log.info("startArray");
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#startJSON()
	 */
	@Override
	public void startJSON() throws ParseException, IOException {
//		log.info("startJSON");

	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#startObject()
	 */
	@Override
	public boolean startObject() throws ParseException, IOException {
//		log.info("startObject");
		return true;
	}

	/**
	 * {@inheritDoc}
	 * @see org.json.simple.parser.ContentHandler#startObjectEntry(java.lang.String)
	 */
	@Override
	public boolean startObjectEntry(final String key) throws ParseException, IOException {		
		log.info("{}startObjectEntry: [{}]", b, key);
		b.append("\t");
		stack.push(key);
		return true;
	}

}
