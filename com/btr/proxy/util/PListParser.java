package com.btr.proxy.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.NodeList;
import java.text.ParseException;
import org.w3c.dom.Node;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import java.io.IOException;
import java.io.Closeable;
import java.util.Map;
import java.text.DateFormat;

public final class PListParser
{
    private static final PListParser PLIST;
    private static final String BASE64_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static final char[] BASE64_CHARS;
    private final DateFormat m_dateFormat;
    private final Map<Class<?>, ElementType> m_simpleTypes;
    
    static void silentlyClose(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (final IOException ex) {}
    }
    
    private static Dict parse(final InputSource input) throws XmlParseException {
        try {
            final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document doc = documentBuilder.parse(input);
            final Element element = doc.getDocumentElement();
            return PListParser.PLIST.parse(element);
        }
        catch (final ParserConfigurationException e) {
            throw new XmlParseException("Error reading input", e);
        }
        catch (final SAXException e2) {
            throw new XmlParseException("Error reading input", e2);
        }
        catch (final IOException e3) {
            throw new XmlParseException("Error reading input", e3);
        }
    }
    
    public static Dict load(final File file) throws XmlParseException, IOException {
        final FileInputStream byteStream = new FileInputStream(file);
        try {
            final InputSource input = new InputSource(byteStream);
            return parse(input);
        }
        finally {
            silentlyClose(byteStream);
        }
    }
    
    PListParser() {
        (this.m_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")).setTimeZone(TimeZone.getTimeZone("Z"));
        (this.m_simpleTypes = new HashMap<Class<?>, ElementType>()).put(Integer.class, ElementType.INTEGER);
        this.m_simpleTypes.put(Byte.class, ElementType.INTEGER);
        this.m_simpleTypes.put(Short.class, ElementType.INTEGER);
        this.m_simpleTypes.put(Short.class, ElementType.INTEGER);
        this.m_simpleTypes.put(Long.class, ElementType.INTEGER);
        this.m_simpleTypes.put(String.class, ElementType.STRING);
        this.m_simpleTypes.put(Float.class, ElementType.REAL);
        this.m_simpleTypes.put(Double.class, ElementType.REAL);
        this.m_simpleTypes.put(byte[].class, ElementType.DATA);
        this.m_simpleTypes.put(Boolean.class, ElementType.TRUE);
        this.m_simpleTypes.put(Date.class, ElementType.DATE);
    }
    
    Dict parse(final Element element) throws XmlParseException {
        if (!"plist".equalsIgnoreCase(element.getNodeName())) {
            throw new XmlParseException("Expected plist top element, was: " + element.getNodeName());
        }
        Node n;
        for (n = element.getFirstChild(); n != null && !n.getNodeName().equals("dict"); n = n.getNextSibling()) {}
        final Dict result = (Dict)this.parseElement(n);
        return result;
    }
    
    private Object parseElement(final Node element) throws XmlParseException {
        try {
            return this.parseElementRaw(element);
        }
        catch (final Exception e) {
            throw new XmlParseException("Failed to parse: " + element.getNodeName(), e);
        }
    }
    
    private Object parseElementRaw(final Node element) throws ParseException {
        final ElementType type = ElementType.valueOf(element.getNodeName().toUpperCase());
        switch (type) {
            case INTEGER: {
                return this.parseInt(this.getValue(element));
            }
            case REAL: {
                return Double.valueOf(this.getValue(element));
            }
            case STRING: {
                return this.getValue(element);
            }
            case DATE: {
                return this.m_dateFormat.parse(this.getValue(element));
            }
            case DATA: {
                return base64decode(this.getValue(element));
            }
            case ARRAY: {
                return this.parseArray(element.getChildNodes());
            }
            case TRUE: {
                return Boolean.TRUE;
            }
            case FALSE: {
                return Boolean.FALSE;
            }
            case DICT: {
                return this.parseDict(element.getChildNodes());
            }
            default: {
                throw new RuntimeException("Unexpected type: " + element.getNodeName());
            }
        }
    }
    
    private String getValue(final Node n) {
        final StringBuilder sb = new StringBuilder();
        for (Node c = n.getFirstChild(); c != null; c = c.getNextSibling()) {
            if (c.getNodeType() == 3) {
                sb.append(c.getNodeValue());
            }
        }
        return sb.toString();
    }
    
    private Number parseInt(final String value) {
        final Long l = Long.valueOf(value);
        if (l.intValue() == l) {
            return l.intValue();
        }
        return l;
    }
    
    private Dict parseDict(final NodeList elements) throws ParseException {
        final Dict dict = new Dict();
        for (int i = 0; i < elements.getLength(); ++i) {
            final Node key = elements.item(i);
            if (key.getNodeType() == 1) {
                if (!"key".equals(key.getNodeName())) {
                    throw new ParseException("Expected key but was " + key.getNodeName(), -1);
                }
                ++i;
                Node value;
                for (value = elements.item(i); value.getNodeType() != 1; value = elements.item(i)) {
                    ++i;
                }
                final Object o = this.parseElementRaw(value);
                final String dictName = this.getValue(key);
                dict.children.put(dictName, o);
            }
        }
        return dict;
    }
    
    private List<Object> parseArray(final NodeList elements) throws ParseException {
        final ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < elements.getLength(); ++i) {
            final Node o = elements.item(i);
            if (o.getNodeType() == 1) {
                list.add(this.parseElementRaw(o));
            }
        }
        return list;
    }
    
    static String base64encode(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder((bytes.length + 2) / 3 * 4);
        for (int i = 0; i < bytes.length; i += 3) {
            final byte b0 = bytes[i];
            final byte b2 = (byte)((i < bytes.length - 1) ? bytes[i + 1] : 0);
            final byte b3 = (byte)((i < bytes.length - 2) ? bytes[i + 2] : 0);
            builder.append(PListParser.BASE64_CHARS[(b0 & 0xFF) >> 2]);
            builder.append(PListParser.BASE64_CHARS[(b0 & 0x3) << 4 | (b2 & 0xF0) >> 4]);
            builder.append((i < bytes.length - 1) ? Character.valueOf(PListParser.BASE64_CHARS[(b2 & 0xF) << 2 | (b3 & 0xC0) >> 6]) : "=");
            builder.append((i < bytes.length - 2) ? Character.valueOf(PListParser.BASE64_CHARS[b3 & 0x3F]) : "=");
        }
        return builder.toString();
    }
    
    static byte[] base64decode(String base64) {
        base64 = base64.trim();
        final int endTrim = base64.endsWith("==") ? 2 : (base64.endsWith("=") ? 1 : 0);
        final int length = base64.length() / 4 * 3 - endTrim;
        base64 = base64.replace('=', 'A');
        final byte[] result = new byte[length];
        final int stringLength = base64.length();
        int index = 0;
        for (int i = 0; i < stringLength; i += 4) {
            final int i2 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(base64.charAt(i));
            final int i3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(base64.charAt(i + 1));
            final int i4 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(base64.charAt(i + 2));
            final int i5 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(base64.charAt(i + 3));
            final byte b0 = (byte)(i2 << 2 | i3 >> 4);
            final byte b2 = (byte)(i3 << 4 | i4 >> 2);
            final byte b3 = (byte)(i4 << 6 | i5);
            result[index++] = b0;
            if (index < length) {
                result[index++] = b2;
                if (index < length) {
                    result[index++] = b3;
                }
            }
        }
        return result;
    }
    
    static {
        PLIST = new PListParser();
        BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    }
    
    public static class XmlParseException extends Exception
    {
        private static final long serialVersionUID = 1L;
        
        public XmlParseException() {
        }
        
        public XmlParseException(final String msg) {
            super(msg);
        }
        
        public XmlParseException(final String msg, final Exception e) {
            super(msg, e);
        }
    }
    
    public static class Dict implements Iterable<Map.Entry<String, Object>>
    {
        private Map<String, Object> children;
        
        public Dict() {
            this.children = new HashMap<String, Object>();
        }
        
        public Object get(final String key) {
            return this.children.get(key);
        }
        
        public Iterator<Map.Entry<String, Object>> iterator() {
            return this.children.entrySet().iterator();
        }
        
        public int size() {
            return this.children.size();
        }
        
        public void dump() {
            System.out.println("PList");
            dumpInternal(this, 1);
        }
        
        private static void dumpInternal(final Dict plist, final int indent) {
            for (final Map.Entry<String, Object> child : plist) {
                if (child.getValue() instanceof Dict) {
                    for (int j = 0; j < indent; ++j) {
                        System.out.print("  ");
                    }
                    System.out.println(child.getKey());
                    dumpInternal(child.getValue(), indent + 1);
                }
                else {
                    for (int j = 0; j < indent; ++j) {
                        System.out.print("  ");
                    }
                    System.out.println(child.getKey() + " = " + child.getValue());
                }
            }
        }
        
        public Object getAtPath(final String path) {
            Dict currentNode = this;
            final String[] pathSegments = path.trim().split("/");
            for (int i = 0; i < pathSegments.length; ++i) {
                final String segment = pathSegments[i].trim();
                if (segment.length() != 0) {
                    final Object o = currentNode.get(segment);
                    if (i >= pathSegments.length - 1) {
                        return o;
                    }
                    if (o == null) {
                        break;
                    }
                    if (!(o instanceof Dict)) {
                        break;
                    }
                    currentNode = (Dict)o;
                }
            }
            return null;
        }
    }
    
    private enum ElementType
    {
        INTEGER, 
        STRING, 
        REAL, 
        DATA, 
        DATE, 
        DICT, 
        ARRAY, 
        TRUE, 
        FALSE;
    }
}
