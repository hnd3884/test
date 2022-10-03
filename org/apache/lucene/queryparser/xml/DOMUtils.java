package org.apache.lucene.queryparser.xml;

import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.io.Reader;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class DOMUtils
{
    public static Element getChildByTagOrFail(final Element e, final String name) throws ParserException {
        final Element kid = getChildByTagName(e, name);
        if (null == kid) {
            throw new ParserException(e.getTagName() + " missing \"" + name + "\" child element");
        }
        return kid;
    }
    
    public static Element getFirstChildOrFail(final Element e) throws ParserException {
        final Element kid = getFirstChildElement(e);
        if (null == kid) {
            throw new ParserException(e.getTagName() + " does not contain a child element");
        }
        return kid;
    }
    
    public static String getAttributeOrFail(final Element e, final String name) throws ParserException {
        final String v = e.getAttribute(name);
        if (null == v) {
            throw new ParserException(e.getTagName() + " missing \"" + name + "\" attribute");
        }
        return v;
    }
    
    public static String getAttributeWithInheritanceOrFail(final Element e, final String name) throws ParserException {
        final String v = getAttributeWithInheritance(e, name);
        if (null == v) {
            throw new ParserException(e.getTagName() + " missing \"" + name + "\" attribute");
        }
        return v;
    }
    
    public static String getNonBlankTextOrFail(final Element e) throws ParserException {
        String v = getText(e);
        if (null != v) {
            v = v.trim();
        }
        if (null == v || 0 == v.length()) {
            throw new ParserException(e.getTagName() + " has no text");
        }
        return v;
    }
    
    public static Element getChildByTagName(final Element e, final String name) {
        for (Node kid = e.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == 1 && name.equals(kid.getNodeName())) {
                return (Element)kid;
            }
        }
        return null;
    }
    
    public static String getAttributeWithInheritance(final Element element, final String attributeName) {
        final String result = element.getAttribute(attributeName);
        if (result != null && !"".equals(result)) {
            return result;
        }
        final Node n = element.getParentNode();
        if (n == element || n == null) {
            return null;
        }
        if (n instanceof Element) {
            final Element parent = (Element)n;
            return getAttributeWithInheritance(parent, attributeName);
        }
        return null;
    }
    
    public static String getChildTextByTagName(final Element e, final String tagName) {
        final Element child = getChildByTagName(e, tagName);
        return (child != null) ? getText(child) : null;
    }
    
    public static Element insertChild(final Element parent, final String tagName, final String text) {
        final Element child = parent.getOwnerDocument().createElement(tagName);
        parent.appendChild(child);
        if (text != null) {
            child.appendChild(child.getOwnerDocument().createTextNode(text));
        }
        return child;
    }
    
    public static String getAttribute(final Element element, final String attributeName, final String deflt) {
        final String result = element.getAttribute(attributeName);
        return (result == null || "".equals(result)) ? deflt : result;
    }
    
    public static float getAttribute(final Element element, final String attributeName, final float deflt) {
        final String result = element.getAttribute(attributeName);
        return (result == null || "".equals(result)) ? deflt : Float.parseFloat(result);
    }
    
    public static int getAttribute(final Element element, final String attributeName, final int deflt) {
        final String result = element.getAttribute(attributeName);
        return (result == null || "".equals(result)) ? deflt : Integer.parseInt(result);
    }
    
    public static boolean getAttribute(final Element element, final String attributeName, final boolean deflt) {
        final String result = element.getAttribute(attributeName);
        return (result == null || "".equals(result)) ? deflt : Boolean.valueOf(result);
    }
    
    public static String getText(final Node e) {
        final StringBuilder sb = new StringBuilder();
        getTextBuffer(e, sb);
        return sb.toString();
    }
    
    public static Element getFirstChildElement(final Element element) {
        for (Node kid = element.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() == 1) {
                return (Element)kid;
            }
        }
        return null;
    }
    
    private static void getTextBuffer(final Node e, final StringBuilder sb) {
        for (Node kid = e.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            switch (kid.getNodeType()) {
                case 3: {
                    sb.append(kid.getNodeValue());
                    break;
                }
                case 1: {
                    getTextBuffer(kid, sb);
                    break;
                }
                case 5: {
                    getTextBuffer(kid, sb);
                    break;
                }
            }
        }
    }
    
    public static Document loadXML(final Reader is) {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (final Exception se) {
            throw new RuntimeException("Parser configuration error", se);
        }
        Document doc = null;
        try {
            doc = db.parse(new InputSource(is));
        }
        catch (final Exception se2) {
            throw new RuntimeException("Error parsing file:" + se2, se2);
        }
        return doc;
    }
}
