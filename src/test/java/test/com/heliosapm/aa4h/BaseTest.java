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

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.LoggerFactory;

import com.heliosapm.utils.jmx.JMXHelper;


/**
 * <p>Title: BaseTest</p>
 * <p>Description: The base aa4h unit test</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>test.com.heliosapm.aa4h.BaseTest</code></p>
 */

@Ignore
public class BaseTest {
	/** The currently executing test name */
	@Rule public final TestName name = new TestName();
	/** A random value generator */
	protected static final Random RANDOM = new Random(System.currentTimeMillis());
	
	/** Retain system out */
	protected static final PrintStream OUT = System.out;
	/** Retain system err */
	protected static final PrintStream ERR = System.err;
	
	
	/**
	 * Returns a random positive long
	 * @return a random positive long
	 */
	public static long nextPosLong() {
		return Math.abs(RANDOM.nextLong());
	}
	
	/**
	 * Returns a random positive double
	 * @return a random positive double
	 */
	public static double nextPosDouble() {
		return Math.abs(RANDOM.nextDouble());
	}
	
	/**
	 * Returns a random boolean
	 * @return a random boolean
	 */
	public static boolean nextBoolean() {
		return RANDOM.nextBoolean();
	}
	
	/**
	 * Returns a random positive int
	 * @return a random positive int
	 */
	public static int nextPosInt() {
		return Math.abs(RANDOM.nextInt());
	}
	/**
	 * Returns a random positive int within the bound
	 * @param bound the bound on the random number to be returned. Must be positive. 
	 * @return a random positive int
	 */
	public static int nextPosInt(int bound) {
		return Math.abs(RANDOM.nextInt(bound));
	}
	
	
	
	/**
	 * Prints the test name about to be executed
	 */
	@Before
	public void printTestName() {
		log("\n\t==================================\n\tRunning Test [" + name.getMethodName() + "]\n\t==================================\n");
	}
	
	/** Rule to system.err fails */
	@Rule
	public TestWatcher watchman= new TestWatcher() {
		@Override
		protected void failed(final Throwable e, final Description d) {
			loge("\n\t==================================\n\tTest Failed [" + name.getMethodName() + "]\n\t==================================\n");
			loge("\n\t" + e.toString() + "\n\t==================================");
		}

		@Override
		protected void succeeded(final Description description) {

		}
	};
	
	
	@After
	public void printTestEnd() {
		
	}
	
	
	/**
	 * Stalls the calling thread 
	 * @param time the time to stall in ms.
	 */
	public static void sleep(final long time) {
		try {
			Thread.currentThread().join(time);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	

	
	
	/**
	 * Adds a notification listener to an MBean and waits for a number of notifications
	 * @param objectName The ObjectName of the MBean to listen on notifications from
	 * @param numberOfNotifications The number of notifications to wait for
	 * @param filter An optional filter
	 * @param waitTime The number of milliseconds to wait for
	 * @param run An optional task to run once the listener has been registered
	 * @return A set of the received notifications
	 * @throws TimeoutException thrown when the wait period expires without all the expected notifications being received
	 */
	public static Set<Notification> waitForNotifications(final ObjectName objectName, final int numberOfNotifications, final NotificationFilter filter, final long waitTime, final Callable<?> run) throws TimeoutException {
		final Set<Notification> notifs = new LinkedHashSet<Notification>();
		final AtomicInteger count = new AtomicInteger(0);
		final CountDownLatch latch = new CountDownLatch(numberOfNotifications);
		final NotificationListener listener = new NotificationListener() {
			@Override
			public void handleNotification(final Notification notification, final Object handback) {
				final int x = count.incrementAndGet();
				if(x<=numberOfNotifications) {
					notifs.add(notification);
					latch.countDown();
				}
			}
		};
		try {
			JMXHelper.getHeliosMBeanServer().addNotificationListener(objectName, listener, filter, null);
			if(run!=null) {
				run.call();
			}
			final boolean complete = latch.await(waitTime, TimeUnit.MILLISECONDS);
			if(!complete) {
				throw new TimeoutException("Request timed out. Notifications received: [" + notifs.size() + "]");
			}
			return notifs;
		} catch (Exception ex) {			
			if(ex instanceof TimeoutException) {
				ex.printStackTrace(System.err);
				throw (TimeoutException)ex;
			}
			throw new RuntimeException(ex);
		} finally {
			try { JMXHelper.getHeliosMBeanServer().removeNotificationListener(objectName, listener); } catch (Exception x) {/* No Op */}
		}		
	}
	
	/**
	 * Adds a notification listener to an MBean and waits for a notification
	 * @param objectName The ObjectName of the MBean to listen on notifications from
	 * @param filter An optional filter
	 * @param waitTime The number of milliseconds to wait for
	 * @param run An optional task to run once the listener has been registered
	 * @return the received notification
	 * @throws TimeoutException thrown when the wait period expires without the expected notification being received
	 */
	public static Notification waitForNotification(final ObjectName objectName, final NotificationFilter filter, final long waitTime, final Callable<?> run) throws TimeoutException {
		final Set<Notification> notif = waitForNotifications(objectName, 1, filter, waitTime, run);
		if(notif.isEmpty()) throw new RuntimeException("No notification received");
		return notif.iterator().next();
	}
	
	
	/**
	 * Compares two string maps for equality where both being null means null
	 * @param a One string map
	 * @param b Another string map
	 * @return true if equal, false otherwise
	 */
	public static boolean equal(final Map<String, String> a, final Map<String, String> b) {
		if(a==null && b==null) return true;
		if(a==null || b==null) return false;
		if(a.size() != b.size()) return false;
		if(a.isEmpty()==b.isEmpty()) return true;
		for(Map.Entry<String, String> entry: a.entrySet()) {
			String akey = entry.getKey();
			String avalue = entry.getValue();
			String bvalue = b.get(akey);
			if(bvalue==null) return false;
			if(!equal(avalue, bvalue)) return false;			
		}
		return true;
		
	}
	
	/**
	 * Compares two strings for equality where both being null means null
	 * @param a One string
	 * @param b Another string
	 * @return true if equal, false otherwise
	 */
	public static boolean equal(final String a, final String b) {
		if(a==null && b==null) return true;
		if(a==null || b==null) return false;
		return a.equals(b);
	}
	
	/**
	 * Creates a map of random tags
	 * @param tagCount The number of tags
	 * @return the tag map
	 */
	public static Map<String, String> randomTags(final int tagCount) {
		final Map<String, String> tags = new LinkedHashMap<String, String>(tagCount);
		for(int i = 0; i < tagCount; i++) {
			String[] frags = getRandomFragments();
			tags.put(frags[0], frags[1]);
		}
		return tags;
	}
	
	
	
	
	
	/**
	 * Nothing yet
	 * @throws java.lang.Exception thrown on any error
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}


	/**
	 * Nothing yet...
	 * @throws java.lang.Exception thrown on any error
	 */
	@Before
	public void setUp() throws Exception {
	}
	

	

	/**
	 * Out printer
	 * @param fmt the message format
	 * @param args the message values
	 */
	public static void log(Object fmt, Object...args) {
		OUT.println(String.format(fmt.toString(), args));
	}
	
//	/**
//	 * Returns the environment classloader for the passed TSDB config
//	 * @param configName The config name
//	 * @return The classloader that would be created for the passed config
//	 */
//	public static ClassLoader tsdbClassLoader(String configName) {
//		try {
//			Config config = getConfig(configName);
//			return TSDBPluginServiceLoader.getSupportClassLoader(config);
//		} catch (Exception ex) {
//			throw new RuntimeException("Failed to load config [" + configName + "]", ex);
//			
//		}
//	}
	
	
	
	/**
	 * Err printer
	 * @param fmt the message format
	 * @param args the message values
	 */
	public static void loge(String fmt, Object...args) {
		ERR.print(String.format(fmt, args));
		if(args!=null && args.length>0 && args[0] instanceof Throwable) {
			ERR.println("  Stack trace follows:");
			((Throwable)args[0]).printStackTrace(ERR);
		} else {
			ERR.println("");
		}
	}
	
	/** A set of files to be deleted after each test */
	protected static final Set<File> TO_BE_DELETED = new CopyOnWriteArraySet<File>();
	
	
	
	
	/**
	 * Generates an array of random strings created from splitting a randomly generated UUID.
	 * @return an array of random strings
	 */
	public static String[] getRandomFragments() {
		return UUID.randomUUID().toString().split("-");
	}
	
	/**
	 * Generates a random string made up from a UUID.
	 * @return a random string
	 */
	public static String getRandomFragment() {
		return UUID.randomUUID().toString();
	}
	
	

	
	
}

