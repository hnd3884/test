package com.adventnet.nms.util;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class XMLDataWriter
{
    private Document xmlDoc;
    private Element rootElement;
    
    public XMLDataWriter(final String s, final XMLNode xmlNode) {
        this(s, xmlNode, null);
    }
    
    public XMLDataWriter(final String s, final XMLNode xmlNode, final String s2) {
        this(s, xmlNode, s2, "ISO-8859-1");
    }
    
    public XMLDataWriter(final String s, final XMLNode xmlNode, final String s2, final String s3) {
        this.rootElement = null;
        try {
            this.writeXML(new OutputStreamWriter(new FileOutputStream(s), s3), xmlNode, s2, s3);
        }
        catch (final Exception ex) {
            System.err.println("Error in writing the file: " + s);
            ex.printStackTrace();
        }
    }
    
    public XMLDataWriter(final Writer writer, final XMLNode xmlNode) {
        this.rootElement = null;
        try {
            this.writeXML(writer, xmlNode, null, "ISO-8859-1");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void writeXML(final Writer writer, final XMLNode xmlNode, final String s, final String s2) throws Exception {
        this.createXMLDocument(xmlNode);
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        if (s != null) {
            transformer.setOutputProperty("doctype-system", s);
        }
        transformer.setOutputProperty("encoding", s2);
        transformer.transform(new DOMSource(this.xmlDoc.getDocumentElement()), new StreamResult(writer));
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        catch (final IOException ex) {
            System.err.println("Exception while writing file " + ex);
            ex.printStackTrace();
        }
    }
    
    private void createXMLDocument(final XMLNode xmlNode) throws Exception {
        this.xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        final Element element = this.xmlDoc.createElement(xmlNode.getNodeName());
        this.setAttributesToNode(xmlNode.getAttributeList(), xmlNode.getAttrKeysVector(), element);
        this.xmlDoc.appendChild(element);
        this.process(xmlNode.getChildNodes(), element);
    }
    
    private void setAttributesToNode(final Hashtable hashtable, final Vector vector, final Element element) {
        if (hashtable != null && !hashtable.isEmpty()) {
            Enumeration enumeration;
            if (vector != null && !vector.isEmpty()) {
                enumeration = vector.elements();
            }
            else {
                enumeration = hashtable.keys();
            }
            while (enumeration.hasMoreElements()) {
                final String s = (String)enumeration.nextElement();
                element.setAttribute(s, (String)hashtable.get(s));
            }
        }
    }
    
    private void process(final Vector vector, final Element element) {
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            final XMLNode xmlNode = (XMLNode)elements.nextElement();
            if (xmlNode.getNodeType() == 1) {
                final Element element2 = this.xmlDoc.createElement(xmlNode.getNodeName());
                element.appendChild(element2);
                this.setAttributesToNode(xmlNode.getAttributeList(), xmlNode.getAttrKeysVector(), element2);
                final Vector childNodes = xmlNode.getChildNodes();
                if (childNodes.isEmpty()) {
                    continue;
                }
                this.process(childNodes, element2);
            }
            else if (xmlNode.getNodeType() == 4) {
                element.appendChild(this.xmlDoc.createComment(xmlNode.getNodeValue()));
            }
            else if (xmlNode.getNodeType() == 6) {
                element.appendChild(this.xmlDoc.createCDATASection(xmlNode.getNodeValue()));
            }
            else if (xmlNode.getNodeType() == 3) {
                element.appendChild(this.xmlDoc.createEntityReference(xmlNode.getNodeValue()));
            }
            else {
                if (xmlNode.getNodeType() == 2) {
                    continue;
                }
                if (xmlNode.getNodeType() != 5) {
                    continue;
                }
                element.appendChild(this.xmlDoc.createProcessingInstruction(xmlNode.getNodeName(), xmlNode.getNodeValue()));
            }
        }
    }
}
