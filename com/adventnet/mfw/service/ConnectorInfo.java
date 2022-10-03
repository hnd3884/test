package com.adventnet.mfw.service;

import org.w3c.dom.Node;
import java.util.List;
import org.w3c.dom.NamedNodeMap;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.util.Properties;

public class ConnectorInfo
{
    private String protocol;
    private int port;
    private String scheme;
    private Properties other_properties;
    private String portPattern;
    
    public ConnectorInfo(final Element element) {
        this.protocol = null;
        this.port = 0;
        this.scheme = null;
        this.other_properties = null;
        this.portPattern = null;
        try {
            this.port = Integer.parseInt(element.getAttribute("port"));
        }
        catch (final NumberFormatException e) {
            this.portPattern = element.getAttribute("port");
        }
        this.scheme = element.getAttribute("scheme");
        this.protocol = element.getAttribute("protocol");
        this.other_properties = this.loadProperties(element);
    }
    
    private Properties loadProperties(final Element e) {
        final NamedNodeMap attributes = e.getAttributes();
        final int size = attributes.getLength();
        final List<String> members = new ArrayList<String>();
        members.add("port");
        members.add("protocol");
        members.add("scheme");
        final Properties prop = new Properties();
        for (int i = 0; i < size; ++i) {
            final Node node = attributes.item(i);
            if (!members.contains(node.getNodeName())) {
                prop.setProperty(node.getNodeName(), node.getNodeValue());
            }
        }
        return prop;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getProtocol() {
        return this.protocol;
    }
    
    public String getScheme() {
        return this.scheme;
    }
    
    public Properties getOtherProperties() {
        return this.other_properties;
    }
    
    public String getPortPattern() {
        return this.portPattern;
    }
}
