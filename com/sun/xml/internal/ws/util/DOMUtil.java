package com.sun.xml.internal.ws.util;

import java.util.ArrayList;
import java.util.List;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.NamedNodeMap;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.stream.XMLStreamWriter;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.FactoryConfigurationError;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;

public class DOMUtil
{
    private static DocumentBuilder db;
    
    public static Document createDom() {
        synchronized (DOMUtil.class) {
            if (DOMUtil.db == null) {
                try {
                    final DocumentBuilderFactory dbf = XmlUtil.newDocumentBuilderFactory();
                    DOMUtil.db = dbf.newDocumentBuilder();
                }
                catch (final ParserConfigurationException e) {
                    throw new FactoryConfigurationError(e);
                }
            }
            return DOMUtil.db.newDocument();
        }
    }
    
    public static void serializeNode(final Element node, final XMLStreamWriter writer) throws XMLStreamException {
        writeTagWithAttributes(node, writer);
        if (node.hasChildNodes()) {
            final NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); ++i) {
                final Node child = children.item(i);
                switch (child.getNodeType()) {
                    case 7: {
                        writer.writeProcessingInstruction(child.getNodeValue());
                    }
                    case 4: {
                        writer.writeCData(child.getNodeValue());
                        break;
                    }
                    case 8: {
                        writer.writeComment(child.getNodeValue());
                        break;
                    }
                    case 3: {
                        writer.writeCharacters(child.getNodeValue());
                        break;
                    }
                    case 1: {
                        serializeNode((Element)child, writer);
                        break;
                    }
                }
            }
        }
        writer.writeEndElement();
    }
    
    public static void writeTagWithAttributes(final Element node, final XMLStreamWriter writer) throws XMLStreamException {
        final String nodePrefix = fixNull(node.getPrefix());
        final String nodeNS = fixNull(node.getNamespaceURI());
        final String nodeLocalName = (node.getLocalName() == null) ? node.getNodeName() : node.getLocalName();
        boolean prefixDecl = isPrefixDeclared(writer, nodeNS, nodePrefix);
        writer.writeStartElement(nodePrefix, nodeLocalName, nodeNS);
        if (node.hasAttributes()) {
            final NamedNodeMap attrs = node.getAttributes();
            for (int numOfAttributes = attrs.getLength(), i = 0; i < numOfAttributes; ++i) {
                final Node attr = attrs.item(i);
                final String nsUri = fixNull(attr.getNamespaceURI());
                if (nsUri.equals("http://www.w3.org/2000/xmlns/")) {
                    final String local = attr.getLocalName().equals("xmlns") ? "" : attr.getLocalName();
                    if (local.equals(nodePrefix) && attr.getNodeValue().equals(nodeNS)) {
                        prefixDecl = true;
                    }
                    if (local.equals("")) {
                        writer.writeDefaultNamespace(attr.getNodeValue());
                    }
                    else {
                        writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
                        writer.writeNamespace(attr.getLocalName(), attr.getNodeValue());
                    }
                }
            }
        }
        if (!prefixDecl) {
            writer.writeNamespace(nodePrefix, nodeNS);
        }
        if (node.hasAttributes()) {
            final NamedNodeMap attrs = node.getAttributes();
            for (int numOfAttributes = attrs.getLength(), i = 0; i < numOfAttributes; ++i) {
                final Node attr = attrs.item(i);
                final String attrPrefix = fixNull(attr.getPrefix());
                final String attrNS = fixNull(attr.getNamespaceURI());
                if (!attrNS.equals("http://www.w3.org/2000/xmlns/")) {
                    String localName = attr.getLocalName();
                    if (localName == null) {
                        localName = attr.getNodeName();
                    }
                    final boolean attrPrefixDecl = isPrefixDeclared(writer, attrNS, attrPrefix);
                    if (!attrPrefix.equals("") && !attrPrefixDecl) {
                        writer.setPrefix(attr.getLocalName(), attr.getNodeValue());
                        writer.writeNamespace(attrPrefix, attrNS);
                    }
                    writer.writeAttribute(attrPrefix, attrNS, localName, attr.getNodeValue());
                }
            }
        }
    }
    
    private static boolean isPrefixDeclared(final XMLStreamWriter writer, final String nsUri, final String prefix) {
        boolean prefixDecl = false;
        final NamespaceContext nscontext = writer.getNamespaceContext();
        final Iterator prefixItr = nscontext.getPrefixes(nsUri);
        while (prefixItr.hasNext()) {
            if (prefix.equals(prefixItr.next())) {
                prefixDecl = true;
                break;
            }
        }
        return prefixDecl;
    }
    
    public static Element getFirstChild(final Element e, final String nsUri, final String local) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                final Element c = (Element)n;
                if (c.getLocalName().equals(local) && c.getNamespaceURI().equals(nsUri)) {
                    return c;
                }
            }
        }
        return null;
    }
    
    @NotNull
    private static String fixNull(@Nullable final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }
    
    @Nullable
    public static Element getFirstElementChild(final Node parent) {
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                return (Element)n;
            }
        }
        return null;
    }
    
    @NotNull
    public static List<Element> getChildElements(final Node parent) {
        final List<Element> elements = new ArrayList<Element>();
        for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == 1) {
                elements.add((Element)n);
            }
        }
        return elements;
    }
}
