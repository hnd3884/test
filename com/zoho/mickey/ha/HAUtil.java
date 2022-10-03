package com.zoho.mickey.ha;

import com.zoho.conf.Configuration;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import com.adventnet.iam.security.SecurityUtil;
import java.io.File;
import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.logging.Level;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.logging.Logger;

public class HAUtil
{
    private static final Logger LOG;
    private static String server_home;
    private static String haType;
    
    public static String getIPAddr(final String publicIP) {
        String ipaddr = null;
        try {
            final Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            while (n.hasMoreElements()) {
                final NetworkInterface e = n.nextElement();
                final Enumeration<InetAddress> a = e.getInetAddresses();
                while (a.hasMoreElements()) {
                    final InetAddress addr = a.nextElement();
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address && !addr.getHostAddress().equals(publicIP)) {
                        ipaddr = addr.getHostAddress();
                    }
                }
            }
        }
        catch (final Exception e2) {
            HAUtil.LOG.log(Level.SEVERE, e2.getMessage());
            throw new RuntimeException("Error in obtaining IP address", e2);
        }
        return ipaddr;
    }
    
    public static List<String> getList(final String prefix, final String prop) {
        if (prop == null) {
            throw new IllegalArgumentException("Property to be splitted cannot be null");
        }
        final List<String> list = new ArrayList<String>();
        final String[] split;
        final String[] parts = split = prop.split(",");
        for (final String entry : split) {
            if (entry.trim().length() > 0) {
                list.add((prefix + entry).trim());
            }
        }
        return list;
    }
    
    public static String getValue(final Properties props, final String property) {
        final String val = props.getProperty(property);
        if (val == null) {
            throw new IllegalArgumentException("The Mandatory property " + property + " is not configured or null");
        }
        return val;
    }
    
    public static boolean isHAEnabled() {
        final String type = "HA";
        return (HAUtil.haType != null) ? HAUtil.haType.equals(type) : Boolean.FALSE;
    }
    
    public static boolean isDataBaseHAEnabled() {
        final String type = "DataBaseHA";
        return (HAUtil.haType != null) ? HAUtil.haType.equals(type) : Boolean.FALSE;
    }
    
    private static String getType() throws Exception {
        final String entry = "HA";
        final File file = new File(HAUtil.server_home + File.separator + "conf" + File.separator + "Persistence" + File.separator + "module-startstop-processors.xml");
        if (!file.exists()) {
            return null;
        }
        final DocumentBuilder docBuilder = SecurityUtil.createDocumentBuilder(true, false, (Properties)null);
        final Document doc = docBuilder.parse(file);
        final Element root = doc.getDocumentElement();
        final NodeList connList = root.getElementsByTagName("ModuleStartStopProcessor");
        final int length = connList.getLength();
        if (length > 0) {
            for (int i = 0; i < length; ++i) {
                final Element connectorEl = (Element)connList.item(i);
                if (connectorEl.getAttribute("PROCESSOR_NAME").equals(entry)) {
                    final String className = connectorEl.getAttribute("CLASSNAME");
                    final HA instance = (HA)Class.forName(className).newInstance();
                    return instance.getType();
                }
            }
        }
        return null;
    }
    
    static {
        LOG = Logger.getLogger(HAUtil.class.getName());
        HAUtil.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        HAUtil.haType = null;
        try {
            HAUtil.haType = getType();
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception occurred while getting the type of HA, enabled in the system.");
        }
    }
}
