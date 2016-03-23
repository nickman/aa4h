import javax.naming.*;
import javax.management.*;
import org.aa4h.samples.ejb.*;

String QueryDir = "c:/projects3.3/Ajax4Hibernate/test/xmlqueries/";

//String fileName = "EmpCountByJobDept.xml";
String fileName = "SimpleEmp.xml";

File f = new File(QueryDir + fileName);

String xmlQuery = f.getText();

println "\n==================Query====================\n${xmlQuery}\n=============================================\n";

Properties p = new Properties();
p.put(Context.PROVIDER_URL, "localhost:1099");
p.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
p.put("jnp.disableDiscovery", "true");
Context ctx = new InitialContext(p);
MBeanServerConnection rmi = (MBeanServerConnection)ctx.lookup("/jmx/rmi/RMIAdaptor");
hostName = (String)rmi.getAttribute(new ObjectName("jboss.system:type=ServerInfo"), "HostName");
println "Connected to ${hostName}"
hs = ctx.lookup(HibernateServiceRemote.JNDI);
println "Acquired Remote:${hs}";



long start = System.currentTimeMillis();
String result = hs.xmlQuery(xmlQuery);
long elapsed = System.currentTimeMillis()-start;

println "Result:\n${result}\nElapsed:${elapsed}";
