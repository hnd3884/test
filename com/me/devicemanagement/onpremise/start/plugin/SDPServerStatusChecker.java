package com.me.devicemanagement.onpremise.start.plugin;

import java.util.Hashtable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SDPServerStatusChecker
{
    private static Logger logger;
    private static final String WEB_SERVICE_NAME = "jboss.web:service=WebServer";
    private static final String DC_PLUGIN_FILE;
    
    public static void main(final String[] args) {
        final int serverPort = getWebServerPort();
        SDPServerStatusChecker.logger.log(Level.INFO, " serverPort  " + serverPort);
        final boolean isPortOccupied = isPortOccupied(new Integer(serverPort));
        SDPServerStatusChecker.logger.log(Level.INFO, "========================================================");
        SDPServerStatusChecker.logger.log(Level.INFO, " SDP Server STATUS  " + isPortOccupied);
        SDPServerStatusChecker.logger.log(Level.INFO, "========================================================");
        final Properties pluginProps = new Properties();
        ((Hashtable<String, String>)pluginProps).put("sdpStaus", String.valueOf(isPortOccupied));
        StartupUtil.storeProperties(pluginProps, SDPServerStatusChecker.DC_PLUGIN_FILE);
        if (isPortOccupied) {
            System.exit(1);
        }
        else {
            System.exit(0);
        }
    }
    
    public static boolean isPortOccupied(final int port) {
        try {
            ServerSocket s = new ServerSocket(port);
            s.close();
            s = null;
        }
        catch (final IOException e) {
            return true;
        }
        return false;
    }
    
    public static int getWebServerPort() {
        int sdpPort = 8080;
        Element rootElement = null;
        Document document = null;
        String filePath = ".." + File.separator + ".." + File.separator + "mLiteMigration" + File.separator + "default" + File.separator + "conf" + File.separator + "sample-bindings.xml";
        if (!new File(filePath).exists()) {
            filePath = ".." + File.separator + ".." + File.separator + "server" + File.separator + "default" + File.separator + "conf" + File.separator + "sample-bindings.xml";
        }
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final File portFile = new File(filePath);
            document = factory.newDocumentBuilder().parse(portFile);
            rootElement = document.getDocumentElement();
            if (rootElement == null) {
                return sdpPort;
            }
            final NodeList nlist = rootElement.getElementsByTagName("service-config");
            for (int j = 0; j < nlist.getLength(); ++j) {
                final Node nl = nlist.item(j);
                final String nodeName = ((Element)nl).getAttribute("name");
                if (nodeName.equalsIgnoreCase("jboss.web:service=WebServer")) {
                    final NodeList portList = ((Element)nl).getElementsByTagName("binding");
                    final Element portElement = (Element)portList.item(0);
                    final String portValue = portElement.getAttribute("port");
                    sdpPort = Integer.parseInt(portValue);
                }
            }
        }
        catch (final Exception e) {
            SDPServerStatusChecker.logger.log(Level.WARNING, " Error reading/parsing file " + filePath);
        }
        return sdpPort;
    }
    
    static {
        SDPServerStatusChecker.logger = Logger.getLogger(SDPServerStatusChecker.class.getName());
        DC_PLUGIN_FILE = ".." + File.separator + "bin" + File.separator + "sdp_server_status.conf";
    }
}
