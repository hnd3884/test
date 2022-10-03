package com.adventnet.mfw.service;

import java.util.Hashtable;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import com.zoho.mickey.api.TransformerFactoryUtil;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import javax.xml.parsers.DocumentBuilder;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.iam.security.SecurityUtil;
import java.io.FileNotFoundException;
import java.util.Map;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import com.zoho.framework.utils.FileUtils;
import org.w3c.dom.Element;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class ServerxmlUtil
{
    private static final Logger LOGGER;
    
    protected static void initTemplateServerxml(final String confDirPath, final String encoding) throws Exception {
        final String templateFileName = confDirPath + File.separator + "server_template.xml";
        final String serverxml = confDirPath + File.separator + "server.xml";
        if (!isExists(templateFileName)) {
            final File serverxmlFile = new File(serverxml);
            takeBackup(confDirPath, serverxmlFile);
            final Document doc = parseServerxml(serverxml);
            final Element root = doc.getDocumentElement();
            final NodeList connList = root.getElementsByTagName("Connector");
            ServerxmlUtil.LOGGER.log(Level.FINER, " Creating port props from server.xml");
            final Properties portProps = new Properties();
            final Map<String, String> portMap = new HashMap<String, String>();
            final int length = connList.getLength();
            for (int i = 0; i < length; ++i) {
                final Element connectorEl = (Element)connList.item(i);
                final String portStr = connectorEl.getAttribute("port");
                ((Hashtable<String, String>)portProps).put("port_" + (i + 1), portStr);
                portMap.put(portStr, "#port_" + (i + 1));
                connectorEl.setAttribute("port", "#port_" + (i + 1));
            }
            for (int i = 0; i < length; ++i) {
                final Element connectorEl = (Element)connList.item(i);
                final String redirectPortStr = connectorEl.getAttribute("redirectPort");
                if (portMap.containsKey(redirectPortStr)) {
                    connectorEl.setAttribute("redirectPort", portMap.get(redirectPortStr));
                }
            }
            ServerxmlUtil.LOGGER.log(Level.FINER, "Port properties: " + portProps.toString());
            writeTomcatConf(new File(templateFileName), doc, encoding);
            FileUtils.writeToFile(new File(confDirPath + File.separator + "port.properties"), portProps, "Tomcat connector port properties file." + System.getProperty("line.separator") + "Format: [PortName]=[value1],[value2],..[valuen]");
            ServerxmlUtil.LOGGER.log(Level.FINER, "Init template serverxml successfull");
        }
        else {
            ServerxmlUtil.LOGGER.log(Level.FINER, "server_template.xml file already present");
        }
    }
    
    private static void takeBackup(final String confDirPath, final File serverxml) {
        final String backupFileName = confDirPath + File.separator + "server_backup.xml";
        ServerxmlUtil.LOGGER.log(Level.FINER, "Taking backup of server.xml");
        try {
            final File backupFile = new File(backupFileName);
            if (!backupFile.exists()) {
                FileUtils.copyFile(serverxml, new File(backupFileName));
            }
            else {
                ServerxmlUtil.LOGGER.info("Backup File already Present");
            }
        }
        catch (final Exception ex) {
            ServerxmlUtil.LOGGER.log(Level.SEVERE, "Couldn't create backup file for " + serverxml.getName(), ex);
            throw new RuntimeException(ex);
        }
    }
    
    protected static Document parseServerxml(final String serverxmlPath) throws Exception {
        Document doc = null;
        try {
            final File serverxml = new File(serverxmlPath);
            if (!serverxml.exists()) {
                throw new FileNotFoundException("Specified File Not Found :: [" + serverxml.getPath() + "]");
            }
            final DocumentBuilder docBuilder = SecurityUtil.createDocumentBuilder(true, false, (Properties)null);
            try {
                doc = docBuilder.parse(serverxml);
            }
            catch (final Exception e) {
                ConsoleOut.print(serverxml.getName() + " might be corrupted. Kindly move [conf/server_backup.xml as [conf/server.xml] and start the server again.");
                throw e;
            }
        }
        catch (final Exception ex) {
            ServerxmlUtil.LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            throw ex;
        }
        return doc;
    }
    
    protected static void writeTomcatConf(final File serverxml, final Document doc, final String encoding) {
        ServerxmlUtil.LOGGER.log(Level.FINER, "creating " + serverxml.toString());
        try {
            final BufferedWriter writer = new BufferedWriter(new FileWriter(serverxml, false));
            final Transformer transformer = TransformerFactoryUtil.newInstance().newTransformer();
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(writer);
            final Properties prop = new Properties();
            ((Hashtable<String, String>)prop).put("indent", "yes");
            ((Hashtable<String, String>)prop).put("encoding", encoding);
            ((Hashtable<String, String>)prop).put("method", "xml");
            ((Hashtable<String, String>)prop).put("omit-xml-declaration", "yes");
            transformer.setOutputProperties(prop);
            transformer.transform(source, result);
            ServerxmlUtil.LOGGER.log(Level.FINER, "Replacing Cntrl+M characters");
            String content = org.apache.commons.io.FileUtils.readFileToString(serverxml, encoding);
            content = content.replaceAll("[\\p{Cntrl}]+", "\n");
            org.apache.commons.io.FileUtils.writeStringToFile(serverxml, content, encoding);
        }
        catch (final Exception ex) {
            ServerxmlUtil.LOGGER.log(Level.SEVERE, "Modified " + serverxml.getName() + " file write failed.");
            ConsoleOut.println(serverxml.getName() + " is corrupted.  Check the logs for detail and kindly use the backup file [conf/server_backup.xml] as [conf/server.xml] and start the server again.");
            throw new RuntimeException(ex);
        }
    }
    
    private static boolean isExists(final String fileName) {
        return new File(fileName).exists();
    }
    
    protected static void setSystemProperties(final int httpPort, final int httpsPort) {
        ServerxmlUtil.LOGGER.log(Level.FINER, "Setting system properties:: httpport: [" + httpPort + "], httpsport:[" + httpsPort + "]");
        String protocol = System.getProperty("connect.protocol");
        if (protocol == null) {
            protocol = ((httpsPort != -1) ? "https" : "http");
            System.setProperty("connect.protocol", protocol);
        }
        System.setProperty("port.check", String.valueOf(protocol.equals("https") ? httpsPort : httpPort));
        if (httpPort != -1) {
            System.setProperty("http.port", httpPort + "");
        }
        if (httpsPort != -1) {
            System.setProperty("https.port", httpsPort + "");
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ServerxmlUtil.class.getName());
    }
}
