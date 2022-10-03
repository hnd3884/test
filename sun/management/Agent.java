package sun.management;

import java.util.Hashtable;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import sun.management.jdp.JdpException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import sun.management.jdp.JdpController;
import java.lang.management.ManagementFactory;
import sun.management.jmxremote.ConnectorBootstrap;
import sun.misc.VMSupport;
import java.util.Map;
import javax.management.remote.JMXConnectorServer;
import java.util.ResourceBundle;
import java.util.Properties;

public class Agent
{
    private static Properties mgmtProps;
    private static ResourceBundle messageRB;
    private static final String CONFIG_FILE = "com.sun.management.config.file";
    private static final String SNMP_PORT = "com.sun.management.snmp.port";
    private static final String JMXREMOTE = "com.sun.management.jmxremote";
    private static final String JMXREMOTE_PORT = "com.sun.management.jmxremote.port";
    private static final String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
    private static final String ENABLE_THREAD_CONTENTION_MONITORING = "com.sun.management.enableThreadContentionMonitoring";
    private static final String LOCAL_CONNECTOR_ADDRESS_PROP = "com.sun.management.jmxremote.localConnectorAddress";
    private static final String SNMP_ADAPTOR_BOOTSTRAP_CLASS_NAME = "sun.management.snmp.AdaptorBootstrap";
    private static final String JDP_DEFAULT_ADDRESS = "224.0.23.178";
    private static final int JDP_DEFAULT_PORT = 7095;
    private static JMXConnectorServer jmxServer;
    
    private static Properties parseString(final String s) {
        final Properties properties = new Properties();
        if (s != null && !s.trim().equals("")) {
            final String[] split = s.split(",");
            for (int length = split.length, i = 0; i < length; ++i) {
                final String[] split2 = split[i].split("=", 2);
                final String trim = split2[0].trim();
                final String s2 = (split2.length > 1) ? split2[1].trim() : "";
                if (!trim.startsWith("com.sun.management.")) {
                    error("agent.err.invalid.option", trim);
                }
                properties.setProperty(trim, s2);
            }
        }
        return properties;
    }
    
    public static void premain(final String s) throws Exception {
        agentmain(s);
    }
    
    public static void agentmain(String s) throws Exception {
        if (s == null || s.length() == 0) {
            s = "com.sun.management.jmxremote";
        }
        final Properties string = parseString(s);
        final Properties properties = new Properties();
        readConfiguration(string.getProperty("com.sun.management.config.file"), properties);
        properties.putAll(string);
        startAgent(properties);
    }
    
    private static synchronized void startLocalManagementAgent() {
        final Properties agentProperties = VMSupport.getAgentProperties();
        if (((Hashtable<String, String>)agentProperties).get("com.sun.management.jmxremote.localConnectorAddress") == null) {
            final String string = ConnectorBootstrap.startLocalConnectorServer().getAddress().toString();
            ((Hashtable<String, String>)agentProperties).put("com.sun.management.jmxremote.localConnectorAddress", string);
            try {
                ConnectorAddressLink.export(string);
            }
            catch (final Exception ex) {
                warning("agent.err.exportaddress.failed", ex.getMessage());
            }
        }
    }
    
    private static synchronized void startRemoteManagementAgent(final String s) throws Exception {
        if (Agent.jmxServer != null) {
            throw new RuntimeException(getText("agent.err.invalid.state", "Agent already started"));
        }
        try {
            final Properties string = parseString(s);
            final Properties properties = new Properties();
            readConfiguration(System.getProperty("com.sun.management.config.file"), properties);
            final Properties properties2 = System.getProperties();
            synchronized (properties2) {
                properties.putAll(properties2);
            }
            final String property = string.getProperty("com.sun.management.config.file");
            if (property != null) {
                readConfiguration(property, properties);
            }
            properties.putAll(string);
            if (properties.getProperty("com.sun.management.enableThreadContentionMonitoring") != null) {
                ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
            }
            final String property2 = properties.getProperty("com.sun.management.jmxremote.port");
            if (property2 == null) {
                throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", new String[] { "No port specified" });
            }
            Agent.jmxServer = ConnectorBootstrap.startRemoteConnectorServer(property2, properties);
            startDiscoveryService(properties);
        }
        catch (final AgentConfigurationError agentConfigurationError) {
            error(agentConfigurationError);
        }
    }
    
    private static synchronized void stopRemoteManagementAgent() throws Exception {
        JdpController.stopDiscoveryService();
        if (Agent.jmxServer != null) {
            ConnectorBootstrap.unexportRegistry();
            Agent.jmxServer.stop();
            Agent.jmxServer = null;
        }
    }
    
    private static void startAgent(final Properties properties) throws Exception {
        final String property = properties.getProperty("com.sun.management.snmp.port");
        final String property2 = properties.getProperty("com.sun.management.jmxremote");
        final String property3 = properties.getProperty("com.sun.management.jmxremote.port");
        if (properties.getProperty("com.sun.management.enableThreadContentionMonitoring") != null) {
            ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
        }
        try {
            if (property != null) {
                loadSnmpAgent(property, properties);
            }
            if (property2 != null || property3 != null) {
                if (property3 != null) {
                    Agent.jmxServer = ConnectorBootstrap.startRemoteConnectorServer(property3, properties);
                    startDiscoveryService(properties);
                }
                startLocalManagementAgent();
            }
        }
        catch (final AgentConfigurationError agentConfigurationError) {
            error(agentConfigurationError);
        }
        catch (final Exception ex) {
            error(ex);
        }
    }
    
    private static void startDiscoveryService(final Properties properties) throws IOException {
        final String property = properties.getProperty("com.sun.management.jdp.port");
        final String property2 = properties.getProperty("com.sun.management.jdp.address");
        final String property3 = properties.getProperty("com.sun.management.jmxremote.autodiscovery");
        boolean boolean1;
        if (property3 == null) {
            boolean1 = (property != null);
        }
        else {
            try {
                boolean1 = Boolean.parseBoolean(property3);
            }
            catch (final NumberFormatException ex) {
                throw new AgentConfigurationError("Couldn't parse autodiscovery argument");
            }
        }
        if (boolean1) {
            InetAddress inetAddress;
            try {
                inetAddress = ((property2 == null) ? InetAddress.getByName("224.0.23.178") : InetAddress.getByName(property2));
            }
            catch (final UnknownHostException ex2) {
                throw new AgentConfigurationError("Unable to broadcast to requested address", ex2);
            }
            int int1 = 7095;
            if (property != null) {
                try {
                    int1 = Integer.parseInt(property);
                }
                catch (final NumberFormatException ex3) {
                    throw new AgentConfigurationError("Couldn't parse JDP port argument");
                }
            }
            final String property4 = properties.getProperty("com.sun.management.jmxremote.port");
            final String property5 = properties.getProperty("com.sun.management.jmxremote.rmi.port");
            final String host = Agent.jmxServer.getAddress().getHost();
            final String s = (property5 != null) ? String.format("service:jmx:rmi://%s:%s/jndi/rmi://%s:%s/jmxrmi", host, property5, host, property4) : String.format("service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi", host, property4);
            final String property6 = properties.getProperty("com.sun.management.jdp.name");
            try {
                JdpController.startDiscoveryService(inetAddress, int1, property6, s);
            }
            catch (final JdpException ex4) {
                throw new AgentConfigurationError("Couldn't start JDP service", ex4);
            }
        }
    }
    
    public static Properties loadManagementProperties() {
        final Properties properties = new Properties();
        readConfiguration(System.getProperty("com.sun.management.config.file"), properties);
        final Properties properties2 = System.getProperties();
        synchronized (properties2) {
            properties.putAll(properties2);
        }
        return properties;
    }
    
    public static synchronized Properties getManagementProperties() {
        if (Agent.mgmtProps == null) {
            final String property = System.getProperty("com.sun.management.config.file");
            final String property2 = System.getProperty("com.sun.management.snmp.port");
            final String property3 = System.getProperty("com.sun.management.jmxremote");
            final String property4 = System.getProperty("com.sun.management.jmxremote.port");
            if (property == null && property2 == null && property3 == null && property4 == null) {
                return null;
            }
            Agent.mgmtProps = loadManagementProperties();
        }
        return Agent.mgmtProps;
    }
    
    private static void loadSnmpAgent(final String s, final Properties properties) {
        try {
            Class.forName("sun.management.snmp.AdaptorBootstrap", true, null).getMethod("initialize", String.class, Properties.class).invoke(null, s, properties);
        }
        catch (final ClassNotFoundException | NoSuchMethodException | IllegalAccessException ex) {
            throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", (Throwable)ex);
        }
        catch (final InvocationTargetException ex2) {
            final Throwable cause = ex2.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new UnsupportedOperationException("Unsupported management property: com.sun.management.snmp.port", cause);
        }
    }
    
    private static void readConfiguration(String string, final Properties properties) {
        if (string == null) {
            final String property = System.getProperty("java.home");
            if (property == null) {
                throw new Error("Can't find java.home ??");
            }
            final StringBuffer sb = new StringBuffer(property);
            sb.append(File.separator).append("lib");
            sb.append(File.separator).append("management");
            sb.append(File.separator).append("management.properties");
            string = sb.toString();
        }
        final File file = new File(string);
        if (!file.exists()) {
            error("agent.err.configfile.notfound", string);
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            properties.load(new BufferedInputStream(inputStream));
        }
        catch (final FileNotFoundException ex) {
            error("agent.err.configfile.failed", ex.getMessage());
        }
        catch (final IOException ex2) {
            error("agent.err.configfile.failed", ex2.getMessage());
        }
        catch (final SecurityException ex3) {
            error("agent.err.configfile.access.denied", string);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final IOException ex4) {
                    error("agent.err.configfile.closed.failed", string);
                }
            }
        }
    }
    
    public static void startAgent() throws Exception {
        final String property = System.getProperty("com.sun.management.agent.class");
        if (property == null) {
            final Properties managementProperties = getManagementProperties();
            if (managementProperties != null) {
                startAgent(managementProperties);
            }
            return;
        }
        final String[] split = property.split(":");
        if (split.length < 1 || split.length > 2) {
            error("agent.err.invalid.agentclass", "\"" + property + "\"");
        }
        final String s = split[0];
        final String s2 = (split.length == 2) ? split[1] : null;
        if (s == null || s.length() == 0) {
            error("agent.err.invalid.agentclass", "\"" + property + "\"");
        }
        if (s != null) {
            try {
                ClassLoader.getSystemClassLoader().loadClass(s).getMethod("premain", String.class).invoke(null, s2);
            }
            catch (final ClassNotFoundException ex) {
                error("agent.err.agentclass.notfound", "\"" + s + "\"");
            }
            catch (final NoSuchMethodException ex2) {
                error("agent.err.premain.notfound", "\"" + s + "\"");
            }
            catch (final SecurityException ex3) {
                error("agent.err.agentclass.access.denied");
            }
            catch (final Exception ex4) {
                error("agent.err.agentclass.failed", (ex4.getCause() == null) ? ex4.getMessage() : ex4.getCause().getMessage());
            }
        }
    }
    
    public static void error(final String s) {
        final String text = getText(s);
        System.err.print(getText("agent.err.error") + ": " + text);
        throw new RuntimeException(text);
    }
    
    public static void error(final String s, final String s2) {
        final String text = getText(s);
        System.err.print(getText("agent.err.error") + ": " + text);
        System.err.println(": " + s2);
        throw new RuntimeException(text + ": " + s2);
    }
    
    public static void error(final Exception ex) {
        ex.printStackTrace();
        System.err.println(getText("agent.err.exception") + ": " + ex.toString());
        throw new RuntimeException(ex);
    }
    
    public static void error(final AgentConfigurationError agentConfigurationError) {
        final String text = getText(agentConfigurationError.getError());
        final String[] params = agentConfigurationError.getParams();
        System.err.print(getText("agent.err.error") + ": " + text);
        if (params != null && params.length != 0) {
            final StringBuffer sb = new StringBuffer(params[0]);
            for (int i = 1; i < params.length; ++i) {
                sb.append(" " + params[i]);
            }
            System.err.println(": " + (Object)sb);
        }
        agentConfigurationError.printStackTrace();
        throw new RuntimeException(agentConfigurationError);
    }
    
    public static void warning(final String s, final String s2) {
        System.err.print(getText("agent.err.warning") + ": " + getText(s));
        System.err.println(": " + s2);
    }
    
    private static void initResource() {
        try {
            Agent.messageRB = ResourceBundle.getBundle("sun.management.resources.agent");
        }
        catch (final MissingResourceException ex) {
            throw new Error("Fatal: Resource for management agent is missing");
        }
    }
    
    public static String getText(final String s) {
        if (Agent.messageRB == null) {
            initResource();
        }
        try {
            return Agent.messageRB.getString(s);
        }
        catch (final MissingResourceException ex) {
            return "Missing management agent resource bundle: key = \"" + s + "\"";
        }
    }
    
    public static String getText(final String s, final String... array) {
        if (Agent.messageRB == null) {
            initResource();
        }
        String s2 = Agent.messageRB.getString(s);
        if (s2 == null) {
            s2 = "missing resource key: key = \"" + s + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
        }
        return MessageFormat.format(s2, (Object[])array);
    }
    
    static {
        Agent.jmxServer = null;
    }
}
