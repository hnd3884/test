package com.adventnet.mfw.service;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import com.adventnet.mfw.ConsoleOut;
import org.w3c.dom.Element;
import java.text.DateFormat;
import java.text.ParseException;
import org.apache.catalina.util.ServerInfo;
import java.text.SimpleDateFormat;
import java.util.Locale;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.Starter;
import java.net.URLClassLoader;
import java.util.Iterator;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.io.File;
import com.adventnet.mfw.message.MessageListener;
import org.apache.catalina.startup.Catalina;
import java.util.logging.Logger;

public class WebService implements Service
{
    private static final Logger LOGGER;
    private Catalina cl;
    private MessageListener msgLsr;
    private PortInUseHandler portInUseHandler;
    private static final String PORT_BUSY = "PORT_BUSY";
    private String confDirPath;
    private String encoding;
    
    public WebService() {
        this.msgLsr = null;
        this.portInUseHandler = null;
        this.confDirPath = System.getProperty("server.home") + File.separator + "conf";
    }
    
    public void create(final DataObject serviceDO) throws Exception {
        if (serviceDO != null) {
            final Iterator itr = serviceDO.getRows("ServiceProperties");
            Row temp = null;
            while (itr.hasNext()) {
                temp = itr.next();
                if (((String)temp.get("PROPERTY")).equalsIgnoreCase("portInUseHandler")) {
                    this.portInUseHandler = (PortInUseHandler)Class.forName((String)temp.get("VALUE")).newInstance();
                }
            }
            if (this.portInUseHandler != null) {
                WebService.LOGGER.log(Level.INFO, "portInUseHandler : " + this.portInUseHandler.getClass().getName() + " initialized");
            }
            else {
                WebService.LOGGER.log(Level.INFO, "No portInUseHandler has been defined");
            }
        }
        final String serverxmlType = PersistenceInitializer.getConfigurationValue("serverxmlType");
        this.encoding = ((PersistenceInitializer.getConfigurationValue("serverxmlEncoding") != null) ? PersistenceInitializer.getConfigurationValue("serverxmlEncoding") : "ISO-8859-1");
        if (Boolean.getBoolean("check.tomcatport")) {
            if (serverxmlType != null && serverxmlType.equals("templated")) {
                WebService.LOGGER.info("Serverxml type:[ Templated ]");
                ServerxmlUtil.initTemplateServerxml(new File(this.confDirPath).toString(), this.encoding);
                this.generateServerxml();
            }
            else {
                WebService.LOGGER.info("Serverxml type:[ Default ]");
                this.checkTomcatPort(new File(this.confDirPath + File.separator, "server.xml").toString());
            }
        }
    }
    
    public void start() throws Exception {
        final URLClassLoader ucl = (URLClassLoader)Thread.currentThread().getContextClassLoader();
        this.cl = new Catalina();
        WebService.LOGGER.log(Level.INFO, "ClassLoader set in ThreadContext is {0}", ucl);
        this.cl.setParentClassLoader((ClassLoader)ucl);
        this.checkTomcatServerInfo();
        WebService.LOGGER.log(Level.INFO, "Disabling Catalina's shutdown hook");
        this.cl.setUseShutdownHook(false);
        Starter.isCatalinaShutdownHookDisabled = true;
        this.cl.start();
        if (Starter.isSafeStart()) {
            final String safeStartMessageListener = PersistenceInitializer.getConfigurationValue("SafeStartMessageListener");
            if (safeStartMessageListener != null && safeStartMessageListener.length() > 0) {
                Messenger.subscribe("SafeStartTopic", this.msgLsr = (MessageListener)ucl.loadClass(safeStartMessageListener).newInstance(), true, (MessageFilter)null);
            }
        }
    }
    
    private void checkTomcatServerInfo() throws Exception {
        boolean isMasked = false;
        try {
            final DateFormat df = new SimpleDateFormat("MMM dd yyyy kk:mm:ss zzz", Locale.ENGLISH);
            df.parse(ServerInfo.getServerBuilt());
        }
        catch (final ParseException ex) {
            WebService.LOGGER.fine("tomcat build time is masked");
            try {
                Long.parseLong(ServerInfo.getServerNumber().replaceAll("\\.", ""));
            }
            catch (final NumberFormatException ex2) {
                WebService.LOGGER.fine("tomcat version is masked");
                try {
                    final String serverInfo = ServerInfo.getServerInfo().split("/")[1];
                    Long.parseLong(serverInfo.replaceAll("\\.", ""));
                }
                catch (final NumberFormatException ex2) {
                    isMasked = true;
                }
                if (!isMasked) {
                    throw new Exception("Server Platform Information should be masked to avoid exploit based on server platform. Please mask properties of ServerInfo.properties");
                }
            }
        }
        finally {
            if (!isMasked) {
                throw new Exception("Server Platform Information should be masked to avoid exploit based on server platform. Please mask properties of ServerInfo.properties");
            }
        }
    }
    
    public void stop() throws Exception {
        this.cl.stop();
        if (Starter.isSafeStart() && this.msgLsr != null) {
            Messenger.unsubscribe("SafeStartTopic", this.msgLsr);
        }
    }
    
    public void destroy() throws Exception {
    }
    
    private void checkTomcatPort(final String serverxml) throws Exception {
        final Document doc = ServerxmlUtil.parseServerxml(serverxml);
        final Element root = doc.getDocumentElement();
        final NodeList connList = root.getElementsByTagName("Connector");
        final int length = connList.getLength();
        int httpPort = -1;
        int httpsPort = -1;
        final String bindAddress = System.getProperty("bindaddress");
        for (int i = 0; i < length; ++i) {
            final Element connectorEl = (Element)connList.item(i);
            final String scheme = connectorEl.getAttribute("scheme");
            final String portStr = connectorEl.getAttribute("port");
            final int origPort = Integer.parseInt(portStr);
            final boolean isFree = Starter.isPortFree(origPort);
            if (!isFree) {
                if (this.portInUseHandler == null || !this.portInUseHandler.canIgnore(new ConnectorInfo(connectorEl))) {
                    final String msg = "Port " + origPort + " already in use. Change WebServer port in <ProductHome>/conf/server.xml";
                    ConsoleOut.println(msg);
                    throw new Exception(msg);
                }
                WebService.LOGGER.log(Level.SEVERE, "Port: [" + origPort + "] already in use. Change the port in <ProductHome>/conf/server.xml and restart the server to enable the service associated with this port");
            }
            else {
                if (scheme.equals("https") && httpsPort == -1) {
                    httpsPort = origPort;
                }
                else if (httpPort == -1) {
                    httpPort = origPort;
                }
                if (bindAddress != null) {
                    connectorEl.setAttribute("address", System.getProperty("bindaddress"));
                }
            }
        }
        ServerxmlUtil.setSystemProperties(httpPort, httpsPort);
        if (bindAddress != null) {
            ServerxmlUtil.writeTomcatConf(new File(serverxml), doc, this.encoding);
        }
    }
    
    private void generateServerxml() throws Exception {
        try {
            final String portFilePath = this.confDirPath + File.separator + "port.properties";
            final String templateFilePath = this.confDirPath + File.separator + "server_template.xml";
            if (!new File(portFilePath).exists()) {
                throw new Exception("port.properties file not found. set reset=true property in persistence-configuration.xml and restartto regenerate port.properties");
            }
            final Properties portProps = Starter.getProperties(portFilePath);
            final Enumeration e = portProps.propertyNames();
            while (e.hasMoreElements()) {
                final String key = e.nextElement();
                final String freePort = this.chooseFreePort(portProps.getProperty(key));
                if (freePort == null) {
                    ((Hashtable<String, String>)portProps).put(key, "PORT_BUSY");
                }
                else {
                    ((Hashtable<String, String>)portProps).put(key, freePort);
                }
            }
            WebService.LOGGER.log(Level.FINER, "============PROPERTIES========" + portProps.toString());
            final String bindAddress = System.getProperty("bindaddress");
            final Document doc = ServerxmlUtil.parseServerxml(templateFilePath);
            final Element root = doc.getDocumentElement();
            final NodeList connList = root.getElementsByTagName("Connector");
            final int length = connList.getLength();
            int httpPort = -1;
            int httpsPort = -1;
            for (int i = 0; i < length; ++i) {
                final Element connectorEl = (Element)connList.item(i);
                final String scheme = connectorEl.getAttribute("scheme");
                final String portStr = connectorEl.getAttribute("port");
                final String freePort2 = portProps.getProperty(portStr.substring(1));
                if (freePort2 == null || freePort2.equals("PORT_BUSY")) {
                    if (this.portInUseHandler == null || !this.portInUseHandler.canIgnore(new ConnectorInfo(connectorEl))) {
                        throw new Exception("All the ports specified for the pattern [" + portStr + "] are already in use.");
                    }
                    WebService.LOGGER.severe("All the ports specified for the pattern [" + portStr + "] are already in use. Choose different port and restart to enable the service associated with this port");
                }
                else {
                    WebService.LOGGER.log(Level.FINER, "Pattern " + portStr + " Available port " + freePort2);
                    connectorEl.setAttribute("port", freePort2);
                    final String redirectPort = connectorEl.getAttribute("redirectPort");
                    if (redirectPort.trim().length() > 0 && portProps.getProperty(redirectPort.substring(1)) != null) {
                        connectorEl.setAttribute("redirectPort", portProps.getProperty(redirectPort.substring(1)));
                    }
                    if (freePort2 != null && scheme.equals("https") && httpsPort == -1) {
                        httpsPort = Integer.parseInt(freePort2);
                    }
                    else if (freePort2 != null && httpPort == -1) {
                        httpPort = Integer.parseInt(freePort2);
                    }
                    if (bindAddress != null) {
                        connectorEl.setAttribute("address", System.getProperty("bindaddress"));
                    }
                }
            }
            ServerxmlUtil.setSystemProperties(httpPort, httpsPort);
            final File serverxml = new File(this.confDirPath + File.separator + "server.xml");
            if (serverxml.exists() && !serverxml.delete()) {
                WebService.LOGGER.log(Level.SEVERE, "Error in deleting old server.xml" + serverxml.getName());
            }
            serverxml.createNewFile();
            ServerxmlUtil.writeTomcatConf(serverxml, doc, this.encoding);
        }
        catch (final Exception e2) {
            ConsoleOut.println("Error in generating server.xml. Refer log for more details");
            throw e2;
        }
    }
    
    private String chooseFreePort(final String portList) {
        final String[] ports = portList.split(",");
        String freePort = null;
        for (final String port : ports) {
            if (Starter.isPortFree(Integer.parseInt(port))) {
                freePort = port;
                break;
            }
        }
        return freePort;
    }
    
    static {
        LOGGER = Logger.getLogger(WebService.class.getName());
    }
}
