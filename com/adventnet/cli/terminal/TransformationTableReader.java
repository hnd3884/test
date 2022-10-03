package com.adventnet.cli.terminal;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.util.Vector;
import java.util.StringTokenizer;
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

class TransformationTableReader
{
    DocumentBuilder docBuilder;
    Document doc;
    static final String table = "TABLE";
    static final String transform = "TRANSFORM";
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
    
    TransformationTableReader(final String trFileName) throws Exception {
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
            Hashtable hashtable2;
            if (attributeByName != null) {
                hashtable2 = this.getTable(node, attributeByName.getValue());
            }
            else {
                hashtable2 = this.getTable(node, null);
            }
            hashtable.put(this.getAttributeByName(node, "NAME").getValue(), hashtable2);
        }
        return hashtable;
    }
    
    Hashtable getTable(final Node node, final String s) {
        final Vector tokensByName = this.getTokensByName(node, "TRANSFORM");
        final Hashtable hashtable = new Hashtable();
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
                hashtable.put(value.getBytes(), value2.getBytes());
            }
            else {
                final StringTokenizer stringTokenizer = new StringTokenizer(value);
                final byte[] array = new byte[stringTokenizer.countTokens()];
                int n = 0;
                int n2 = 10;
                if (value3.toUpperCase().equals("byteHex".toUpperCase())) {
                    n2 = 16;
                }
                while (stringTokenizer.hasMoreTokens()) {
                    array[n] = (byte)(0xFF & Integer.parseInt(stringTokenizer.nextToken(), n2));
                    ++n;
                }
                final StringTokenizer stringTokenizer2 = new StringTokenizer(value2);
                final byte[] array2 = new byte[stringTokenizer2.countTokens()];
                int n3 = 0;
                while (stringTokenizer2.hasMoreTokens()) {
                    array2[n3] = (byte)(0xFF & Integer.parseInt(stringTokenizer2.nextToken(), n2));
                    ++n3;
                }
                hashtable.put(array, array2);
            }
        }
        return this.sortEntries(this.arrangeEntries(hashtable));
    }
    
    Hashtable arrangeEntries(final Hashtable hashtable) {
        final Hashtable hashtable2 = new Hashtable();
        final Enumeration keys = hashtable.keys();
        while (hashtable.size() > 0) {
            byte[] array;
            try {
                array = (byte[])keys.nextElement();
            }
            catch (final Exception ex) {
                return null;
            }
            final byte[] array2 = hashtable.remove(array);
            final Vector<byte[]> vector = new Vector<byte[]>();
            final Vector vector2 = new Vector();
            vector.addElement(array);
            vector2.addElement(array2);
            final byte b = array[0];
            final Enumeration keys2 = hashtable.keys();
            while (keys2.hasMoreElements()) {
                byte[] array3;
                try {
                    array3 = (byte[])keys2.nextElement();
                }
                catch (final Exception ex2) {
                    break;
                }
                if (b == array3[0]) {
                    final byte[] array4 = hashtable.remove(array3);
                    vector.addElement(array3);
                    vector2.addElement(array4);
                }
            }
            hashtable2.put(new Byte(b), new Vector[] { vector, vector2 });
        }
        return hashtable2;
    }
    
    Hashtable sortEntries(final Hashtable hashtable) {
        final Hashtable hashtable2 = new Hashtable();
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final Byte b = (Byte)keys.nextElement();
            hashtable2.put(b, this.sort((Vector[])hashtable.get(b)));
        }
        return hashtable2;
    }
    
    Vector[] sort(final Vector[] array) {
        final Vector vector = array[0];
        final Vector vector2 = array[1];
        final Vector vector3 = new Vector();
        final Vector vector4 = new Vector();
        while (vector.size() > 0) {
            int n = 0;
            int n2 = 255;
            for (int i = 0; i < vector.size(); ++i) {
                final int length = ((byte[])vector.elementAt(i)).length;
                if (length <= n2) {
                    n2 = length;
                    n = i;
                }
            }
            final byte[] array2 = vector.elementAt(n);
            final byte[] array3 = vector2.elementAt(n);
            vector.removeElementAt(n);
            vector3.addElement(array2);
            vector2.removeElementAt(n);
            vector4.addElement(array3);
        }
        return new Vector[] { vector3, vector4 };
    }
    
    int findNumericValue(final byte[] array, final int n) {
        int n2 = 0;
        for (int i = n - 1; i >= 0; --i) {
            n2 += (array[i] - this.ASCI_BEGIN) * this.powerOfTen(n - 1 - i);
        }
        return n2;
    }
    
    int powerOfTen(final int n) {
        int n2 = 1;
        for (int i = 0; i < n; ++i) {
            n2 *= 10;
        }
        return n2;
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
