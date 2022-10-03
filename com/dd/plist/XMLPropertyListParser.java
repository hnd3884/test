package com.dd.plist;

import org.xml.sax.InputSource;
import org.w3c.dom.Text;
import java.util.ArrayList;
import org.w3c.dom.NodeList;
import java.util.List;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import java.io.ByteArrayInputStream;
import java.text.ParseException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLPropertyListParser
{
    private static final DocumentBuilderFactory FACTORY;
    
    public static DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        final DocumentBuilder builder = XMLPropertyListParser.FACTORY.newDocumentBuilder();
        builder.setEntityResolver(new PlistDtdResolver());
        return builder;
    }
    
    public static NSObject parse(final File f) throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException, ParseException {
        return parse(getDocBuilder().parse(new FileInputStream(f)));
    }
    
    public static NSObject parse(final byte[] bytes) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        return parse(new ByteArrayInputStream(bytes));
    }
    
    public static NSObject parse(final InputStream is) throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException, ParseException {
        return parse(getDocBuilder().parse(is));
    }
    
    public static NSObject parse(final Document doc) throws PropertyListFormatException, IOException, ParseException {
        final DocumentType docType = doc.getDoctype();
        if (docType == null) {
            if (!doc.getDocumentElement().getNodeName().equals("plist")) {
                throw new UnsupportedOperationException("The given XML document is not a property list.");
            }
        }
        else if (!docType.getName().equals("plist")) {
            throw new UnsupportedOperationException("The given XML document is not a property list.");
        }
        Node rootNode;
        if (doc.getDocumentElement().getNodeName().equals("plist")) {
            final List<Node> rootNodes = filterElementNodes(doc.getDocumentElement().getChildNodes());
            if (rootNodes.isEmpty()) {
                throw new PropertyListFormatException("The given XML property list has no root element!");
            }
            if (rootNodes.size() != 1) {
                throw new PropertyListFormatException("The given XML property list has more than one root element!");
            }
            rootNode = rootNodes.get(0);
        }
        else {
            rootNode = doc.getDocumentElement();
        }
        return parseObject(rootNode);
    }
    
    private static NSObject parseObject(final Node n) throws ParseException, IOException {
        final String type = n.getNodeName();
        if (type.equals("dict")) {
            final NSDictionary dict = new NSDictionary();
            final List<Node> children = filterElementNodes(n.getChildNodes());
            for (int i = 0; i < children.size(); i += 2) {
                final Node key = children.get(i);
                final Node val = children.get(i + 1);
                final String keyString = getNodeTextContents(key);
                dict.put(keyString, parseObject(val));
            }
            return dict;
        }
        if (type.equals("array")) {
            final List<Node> children2 = filterElementNodes(n.getChildNodes());
            final NSArray array = new NSArray(children2.size());
            for (int i = 0; i < children2.size(); ++i) {
                array.setValue(i, parseObject(children2.get(i)));
            }
            return array;
        }
        if (type.equals("true")) {
            return new NSNumber(true);
        }
        if (type.equals("false")) {
            return new NSNumber(false);
        }
        if (type.equals("integer")) {
            return new NSNumber(getNodeTextContents(n));
        }
        if (type.equals("real")) {
            return new NSNumber(getNodeTextContents(n));
        }
        if (type.equals("string")) {
            return new NSString(getNodeTextContents(n));
        }
        if (type.equals("data")) {
            return new NSData(getNodeTextContents(n));
        }
        if (type.equals("date")) {
            return new NSDate(getNodeTextContents(n));
        }
        return null;
    }
    
    private static List<Node> filterElementNodes(final NodeList list) {
        final List<Node> result = new ArrayList<Node>(list.getLength());
        for (int i = 0; i < list.getLength(); ++i) {
            if (list.item(i).getNodeType() == 1) {
                result.add(list.item(i));
            }
        }
        return result;
    }
    
    private static String getNodeTextContents(final Node n) {
        if (n.getNodeType() == 3 || n.getNodeType() == 4) {
            final Text txtNode = (Text)n;
            final String content = txtNode.getWholeText();
            if (content == null) {
                return "";
            }
            return content;
        }
        else {
            if (n.hasChildNodes()) {
                final NodeList children = n.getChildNodes();
                int i = 0;
                while (i < children.getLength()) {
                    final Node child = children.item(i);
                    if (child.getNodeType() == 3 || child.getNodeType() == 4) {
                        final Text txtNode2 = (Text)child;
                        final String content2 = txtNode2.getWholeText();
                        if (content2 == null) {
                            return "";
                        }
                        return content2;
                    }
                    else {
                        ++i;
                    }
                }
                return "";
            }
            return "";
        }
    }
    
    static {
        FACTORY = DocumentBuilderFactory.newInstance();
        try {
            XMLPropertyListParser.FACTORY.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        }
        catch (final ParserConfigurationException ex) {}
        try {
            XMLPropertyListParser.FACTORY.setFeature("http://xml.org/sax/features/external-general-entities", false);
        }
        catch (final ParserConfigurationException ex2) {}
        try {
            XMLPropertyListParser.FACTORY.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        }
        catch (final ParserConfigurationException ex3) {}
        try {
            XMLPropertyListParser.FACTORY.setXIncludeAware(false);
        }
        catch (final UnsupportedOperationException ex4) {}
        XMLPropertyListParser.FACTORY.setExpandEntityReferences(false);
        XMLPropertyListParser.FACTORY.setNamespaceAware(false);
        XMLPropertyListParser.FACTORY.setIgnoringComments(true);
        XMLPropertyListParser.FACTORY.setCoalescing(true);
        XMLPropertyListParser.FACTORY.setValidating(false);
    }
    
    private static class PlistDtdResolver implements EntityResolver
    {
        private static final String PLIST_PUBLIC_ID_1 = "-//Apple Computer//DTD PLIST 1.0//EN";
        private static final String PLIST_PUBLIC_ID_2 = "-//Apple//DTD PLIST 1.0//EN";
        
        PlistDtdResolver() {
        }
        
        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) {
            if ("-//Apple Computer//DTD PLIST 1.0//EN".equals(publicId) || "-//Apple//DTD PLIST 1.0//EN".equals(publicId)) {
                return new InputSource(new ByteArrayInputStream(new byte[0]));
            }
            return null;
        }
    }
}
