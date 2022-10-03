package com.me.devicemanagement.onpremise.start.util;

import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import javax.xml.transform.dom.DOMSource;
import java.net.ServerSocket;
import java.util.Properties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.File;
import java.util.logging.Level;
import org.w3c.dom.Element;
import java.util.logging.Logger;

public class PortCheckerUtil
{
    private static final Logger LOGGER;
    public static final String WEB_CONNECTOR_NAME = "WebServer";
    public static final String SSL_CONNECTOR_NAME = "SSL";
    Element rootElement;
    String fileName;
    
    public PortCheckerUtil(final String confFile) {
        this.rootElement = null;
        this.fileName = null;
        this.parseXml(this.fileName = confFile);
    }
    
    public synchronized void parseXml(final String filePath) {
        PortCheckerUtil.LOGGER.log(Level.INFO, "PortCheckerUtil: parseXml() is invoked with file name: " + filePath);
        try {
            this.fileName = filePath;
            final File portFile = new File(filePath);
            final Document document = XMLUtils.getDocumentBuilderInstance().parse(portFile);
            this.rootElement = document.getDocumentElement();
        }
        catch (final Exception e) {
            PortCheckerUtil.LOGGER.log(Level.INFO, " Error reading/parsing file " + filePath + "\t exception: " + e);
            e.printStackTrace();
        }
    }
    
    public int getPort(final String connectorName) {
        if (this.rootElement == null) {
            return -1;
        }
        final NodeList nlist = this.rootElement.getElementsByTagName("Service");
        for (int j = 0; j < nlist.getLength(); ++j) {
            final Node nl = nlist.item(j);
            final NodeList clist = ((Element)nl).getElementsByTagName("Connector");
            for (int k = 0; k < clist.getLength(); ++k) {
                final Node cl = clist.item(k);
                final String nodeName = ((Element)cl).getAttribute("name");
                PortCheckerUtil.LOGGER.log(Level.INFO, " Node name: " + nodeName);
                if (nodeName != null && nodeName.equalsIgnoreCase(connectorName)) {
                    final String portValue = ((Element)cl).getAttribute("port");
                    PortCheckerUtil.LOGGER.log(Level.INFO, " Port string value: " + portValue);
                    if (portValue != null) {
                        return Integer.parseInt(portValue);
                    }
                }
            }
        }
        return -1;
    }
    
    public synchronized boolean changePort(final Properties p) {
        boolean result = false;
        final NodeList nlist = this.rootElement.getElementsByTagName("Service");
        for (int j = 0; j < nlist.getLength(); ++j) {
            final Node nl = nlist.item(j);
            final NodeList clist = ((Element)nl).getElementsByTagName("Connector");
            for (int k = 0; k < clist.getLength(); ++k) {
                final Node cl = clist.item(k);
                final String nodeName = ((Element)cl).getAttribute("name");
                if (p.getProperty(nodeName) != null) {
                    final String portValue = ((Element)cl).getAttribute("port");
                    final String newWebPort = p.getProperty(nodeName);
                    if (newWebPort != null) {
                        PortCheckerUtil.LOGGER.log(Level.INFO, " Replaced old port " + portValue + " with new port = " + newWebPort);
                        ((Element)cl).setAttribute("port", newWebPort);
                        if (nodeName.equalsIgnoreCase("WebServer")) {
                            final String sslWebPort = p.getProperty("SSL");
                            if (sslWebPort != null) {
                                ((Element)cl).setAttribute("redirectPort", sslWebPort);
                            }
                        }
                        result = true;
                    }
                }
            }
        }
        if (result) {
            this.writeToFile();
        }
        return result;
    }
    
    public boolean checkPortAvailability(final int portNum) {
        if (portNum < 0) {
            return false;
        }
        try {
            final ServerSocket sock = new ServerSocket(portNum);
            sock.close();
            return true;
        }
        catch (final Exception ex) {
            PortCheckerUtil.LOGGER.log(Level.INFO, "Port " + portNum + " occupied");
            return false;
        }
    }
    
    private void writeToFile() {
        try {
            final Transformer trans = XMLUtils.getTransformerInstance();
            trans.setOutputProperty("indent", "yes");
            final DOMSource domSource = new DOMSource(this.rootElement);
            final FileWriter writer = new FileWriter(new File(this.fileName));
            final StreamResult streamResult = new StreamResult(writer);
            trans.transform(domSource, streamResult);
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        catch (final Exception ex) {
            PortCheckerUtil.LOGGER.log(Level.SEVERE, "Exception while writing file " + ex);
            ex.printStackTrace();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(PortCheckerUtil.class.getName());
    }
}
