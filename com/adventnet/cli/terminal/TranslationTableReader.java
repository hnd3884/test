package com.adventnet.cli.terminal;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.util.Vector;
import org.w3c.dom.Attr;
import java.util.Enumeration;
import org.w3c.dom.Node;
import java.util.Hashtable;
import java.io.File;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

class TranslationTableReader
{
    DocumentBuilder docBuilder;
    Document doc;
    static final String table = "TABLE";
    static final String translate = "TRANSLATE";
    static final String name = "NAME";
    static final String valueStr = "VALUE";
    static final String codeStr = "CODE";
    static final String typeStr = "TYPE";
    static final String alphaType = "alpha";
    static final String byteHexType = "byteHex";
    static final String byteDecType = "byteDec";
    Element rootNode;
    String trFileName;
    byte ASCI_BEGIN;
    
    TranslationTableReader(final String trFileName) throws Exception {
        this.docBuilder = null;
        this.doc = null;
        this.rootNode = null;
        this.trFileName = null;
        this.ASCI_BEGIN = 48;
        this.docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        this.trFileName = trFileName;
    }
    
    void parseXml(final InputStream inputStream) throws IOException, SAXException, IllegalArgumentException {
        if (this.docBuilder == null) {
            System.out.println("docbuilder is null");
        }
        this.doc = this.docBuilder.parse(inputStream);
    }
    
    void parseXml() throws IOException, SAXException, IllegalArgumentException {
        final File file = new File(this.trFileName);
        if (this.docBuilder == null) {
            System.out.println("docbuilder is null");
        }
        this.doc = this.docBuilder.parse(file);
    }
    
    Hashtable readTables() {
        this.rootNode = this.doc.getDocumentElement();
        final Enumeration elements = this.getTokensByName(this.rootNode, "TABLE").elements();
        final Hashtable hashtable = new Hashtable();
        while (elements.hasMoreElements()) {
            final Node node = (Node)elements.nextElement();
            final Attr attributeByName = this.getAttributeByName(node, "TYPE");
            int[] array;
            if (attributeByName != null) {
                array = this.getTable(node, attributeByName.getValue());
            }
            else {
                array = this.getTable(node, null);
            }
            hashtable.put(this.getAttributeByName(node, "NAME").getValue(), array);
        }
        return hashtable;
    }
    
    int[] getTable(final Node node, final String s) {
        final Vector tokensByName = this.getTokensByName(node, "TRANSLATE");
        final int[] array = new int[256];
        for (int i = 0; i < 256; ++i) {
            array[i] = 256;
        }
        final Enumeration elements = tokensByName.elements();
        while (elements.hasMoreElements()) {
            final Node node2 = (Node)elements.nextElement();
            final String value = this.getAttributeByName(node2, "CODE").getValue();
            final String value2 = this.getAttributeByName(node2, "VALUE").getValue();
            final Attr attributeByName = this.getAttributeByName(node2, "TYPE");
            String value3;
            if (attributeByName != null) {
                value3 = attributeByName.getValue();
            }
            else if (s != null) {
                value3 = s;
            }
            else {
                value3 = "byteDec";
            }
            if (value3.toUpperCase().equals("alpha".toUpperCase())) {
                array[value.getBytes()[0]] = value2.getBytes()[0];
            }
            else {
                int n = 10;
                if (value3.toUpperCase().equals("byteHex".toUpperCase())) {
                    n = 16;
                }
                array[(byte)(0xFF & Integer.parseInt(value, n))] = (byte)(0xFF & Integer.parseInt(value2, n));
            }
        }
        return array;
    }
    
    Vector getTokensByName(final Node node, final String s) {
        final NodeList childNodes = node.getChildNodes();
        final Vector vector = new Vector();
        for (int length = childNodes.getLength(), i = 0; i < length; ++i) {
            final Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNodeName().equals(s)) {
                vector.addElement(item);
            }
        }
        return vector;
    }
    
    Attr getAttributeByName(final Node node, final String s) {
        int n = 0;
        final NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return null;
        }
        Attr attr;
        while ((attr = (Attr)attributes.item(n)) != null) {
            if (attr.getName().equals(s)) {
                return attr;
            }
            ++n;
        }
        return null;
    }
}
