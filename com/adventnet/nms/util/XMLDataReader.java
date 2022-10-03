package com.adventnet.nms.util;

import java.io.BufferedInputStream;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URL;
import com.adventnet.management.log.SystemUtil;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.io.File;
import java.io.Serializable;

public class XMLDataReader implements Serializable
{
    private XMLNode rootNode;
    private String className;
    private String encoding;
    transient boolean parseTextNode;
    transient boolean throwException;
    
    public XMLDataReader(final String s) {
        this();
        this.load(s);
    }
    
    public XMLDataReader(final String s, final String className) {
        this();
        this.className = className;
        this.load(s);
    }
    
    public XMLDataReader(final String s, final boolean b) {
        this();
        this.load(s, b);
    }
    
    public XMLDataReader(final String s, final boolean b, final boolean b2) {
        this(s, b, b2, false);
    }
    
    public XMLDataReader(final String s, final boolean b, final boolean throwException, final boolean b2) {
        this();
        this.throwException = throwException;
        if (b2) {
            final File encoding = new File(s);
            try {
                this.encoding = this.setEncoding(encoding);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        this.load(s, b);
    }
    
    public XMLDataReader(final InputStream inputStream) {
        this();
        this.load(inputStream);
    }
    
    public XMLDataReader(final InputStream inputStream, final String className) {
        this();
        this.className = className;
        this.load(inputStream);
    }
    
    public XMLDataReader() {
        this.encoding = null;
        this.parseTextNode = false;
        this.throwException = false;
    }
    
    private void parse(final InputSource inputSource) {
        try {
            this.rootNode = this.getXMLNode(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource).getDocumentElement(), null);
        }
        catch (final SAXParseException ex) {
            final StringBuffer sb = new StringBuffer();
            sb.append("Error occured while parsing the uri ");
            sb.append(ex.getSystemId());
            sb.append(", at line " + ex.getLineNumber());
            sb.append("\n   " + ex.getMessage());
            this.throwParseException(sb.toString());
        }
        catch (final SAXException ex2) {
            Exception exception = ex2.getException();
            if (exception == null) {
                exception = ex2;
            }
            this.throwParseException(exception.getMessage());
        }
        catch (final Throwable t) {
            this.throwParseException(t.getMessage());
        }
    }
    
    private void throwParseException(final String s) {
        System.err.println(s);
        if (this.throwException) {
            SystemUtil.cout.println(s);
            throw new RuntimeException(s);
        }
    }
    
    private void load(final String s, final boolean b) {
        if (!b && !new File(s).exists()) {
            System.err.println("The XML File " + s + "  does not exist");
            return;
        }
        final String systemId = b ? s : ("file:" + new File(s).getAbsolutePath().replace(File.separatorChar, '/'));
        InputStream inputStream = null;
        String replace = null;
        try {
            if (b) {
                inputStream = new URL(s).openStream();
            }
            else {
                replace = new File(s).getAbsolutePath().replace(File.separatorChar, '/');
                inputStream = this.openFile(new File(replace));
            }
        }
        catch (final Exception ex) {
            System.err.println("Exception while forming the url: " + replace + " and getting the stream. " + ex.getMessage());
            ex.printStackTrace();
        }
        final InputSource inputSource = new InputSource(inputStream);
        inputSource.setSystemId(systemId);
        this.parse(inputSource);
    }
    
    private InputStream openFile(final File file) throws IOException {
        InputStream resourceAsStream;
        if (System.getProperty("JavaWebStart") != null) {
            System.out.println("Java Web Start mode in Scheduler: " + file);
            resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(file.getName());
        }
        else {
            resourceAsStream = new FileInputStream(file);
        }
        return resourceAsStream;
    }
    
    public void load(final String s) {
        this.load(s, true);
    }
    
    public void load(final InputStream inputStream) {
        this.parse(new InputSource(inputStream));
    }
    
    public XMLNode getRootNode() {
        return this.rootNode;
    }
    
    public XMLNode getNodeById(final String s) {
        return this.searchNodeForId(s, this.rootNode);
    }
    
    public Vector getRootChildNodes() {
        return this.getRootChildNodes(true);
    }
    
    public Vector getRootChildNodes(final boolean b) {
        final Vector vector = new Vector();
        final Enumeration children = this.rootNode.children();
        while (children.hasMoreElements()) {
            final XMLNode xmlNode = children.nextElement();
            if (!b && xmlNode.getNodeType() == 4) {
                continue;
            }
            vector.addElement(xmlNode);
        }
        return vector;
    }
    
    public XMLNode searchNodeForId(final String s, final XMLNode xmlNode) {
        if (xmlNode.getNodeType() == 4 || xmlNode.getNodeType() == 3) {
            return null;
        }
        if (xmlNode.getAttribute("ID") != null && ((String)xmlNode.getAttribute("ID")).equalsIgnoreCase(s)) {
            return xmlNode;
        }
        if (xmlNode.children() != null) {
            final Enumeration children = xmlNode.children();
            while (children.hasMoreElements()) {
                final XMLNode searchNodeForId = this.searchNodeForId(s, children.nextElement());
                if (searchNodeForId != null) {
                    return searchNodeForId;
                }
            }
        }
        return null;
    }
    
    private XMLNode getXMLNode(final Node node, final XMLNode parentNode) {
        XMLNode xmlNode = null;
        if (this.className != null) {
            try {
                final Class<?> forName = Class.forName(this.className);
                if (forName != null) {
                    xmlNode = (XMLNode)forName.newInstance();
                }
            }
            catch (final Exception ex) {
                xmlNode = new XMLNode();
            }
        }
        else {
            xmlNode = new XMLNode();
        }
        xmlNode.setParentNode(parentNode);
        if (node.getNodeType() == 8) {
            xmlNode.setNodeType(4);
        }
        else if (node.getNodeType() == 1) {
            xmlNode.setNodeType(1);
        }
        else if (node.getNodeType() == 9) {
            xmlNode.setNodeType(2);
        }
        else if (node.getNodeType() == 5) {
            xmlNode.setNodeType(3);
        }
        else if (node.getNodeType() == 3 && this.parseTextNode) {
            xmlNode.setNodeType(7);
            final String nodeValue = node.getNodeValue();
            if (nodeValue != null && !nodeValue.trim().equals("\n") && !nodeValue.trim().equals("")) {
                xmlNode.setNodeValue(nodeValue);
                return xmlNode;
            }
            return null;
        }
        xmlNode.setNodeValue(node.getNodeValue());
        if (xmlNode.getNodeType() == 4) {
            return xmlNode;
        }
        xmlNode.setNodeName(node.getNodeName());
        if (xmlNode.getNodeType() == 3) {
            return xmlNode;
        }
        xmlNode.setAttributeList(this.getAttributeList(node));
        final Vector childNodesV = this.getChildNodesV(node, xmlNode);
        for (int i = 0; i < childNodesV.size(); ++i) {
            ((XMLNode)childNodesV.elementAt(i)).setParentNode(null);
            xmlNode.addChildNode((XMLNode)childNodesV.elementAt(i));
        }
        return xmlNode;
    }
    
    private Hashtable getAttributeList(final Node node) {
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return null;
        }
        final int length = attributes.getLength();
        Hashtable<String, String> hashtable = null;
        if (length > 0) {
            hashtable = new Hashtable<String, String>(length);
        }
        for (int i = 0; i < length; ++i) {
            final Attr attr = (Attr)attributes.item(i);
            hashtable.put(attr.getName(), attr.getValue());
        }
        return hashtable;
    }
    
    private Vector getChildNodesV(final Node node, final XMLNode xmlNode) {
        final Vector vector = new Vector();
        final NodeList childNodes = node.getChildNodes();
        for (int length = childNodes.getLength(), i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() != 3 || (this.parseTextNode && item.getNodeType() == 3)) {
                final XMLNode xmlNode2 = this.getXMLNode(item, xmlNode);
                if (xmlNode2 != null) {
                    vector.addElement(xmlNode2);
                }
            }
        }
        return vector;
    }
    
    public void setParseTextNode(final boolean parseTextNode) {
        this.parseTextNode = parseTextNode;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    private String setEncoding(final File file) throws Exception {
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            final StringBuffer sb = new StringBuffer();
            char c;
            while ((c = (char)bufferedInputStream.read()) != '\n') {
                sb.append(c);
            }
            bufferedInputStream.close();
            final String string = sb.toString();
            final int index = string.indexOf(34, string.lastIndexOf("encoding"));
            return string.substring(index + 1, string.indexOf(34, index + 1));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
