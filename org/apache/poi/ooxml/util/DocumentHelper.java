package org.apache.poi.ooxml.util;

import javax.xml.stream.events.Namespace;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.w3c.dom.Document;
import java.io.InputStream;
import org.apache.poi.util.XMLHelper;
import javax.xml.parsers.DocumentBuilder;

public final class DocumentHelper
{
    private static final DocumentBuilder documentBuilderSingleton;
    
    private DocumentHelper() {
    }
    
    public static DocumentBuilder newDocumentBuilder() {
        return XMLHelper.newDocumentBuilder();
    }
    
    public static Document readDocument(final InputStream inp) throws IOException, SAXException {
        return newDocumentBuilder().parse(inp);
    }
    
    public static Document readDocument(final InputSource inp) throws IOException, SAXException {
        return newDocumentBuilder().parse(inp);
    }
    
    public static Document createDocument() {
        return DocumentHelper.documentBuilderSingleton.newDocument();
    }
    
    public static void addNamespaceDeclaration(final Element element, final String namespacePrefix, final String namespaceURI) {
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + namespacePrefix, namespaceURI);
    }
    
    public static void addNamespaceDeclaration(final Element element, final Namespace namespace) {
        addNamespaceDeclaration(element, namespace.getPrefix(), namespace.getNamespaceURI());
    }
    
    static {
        documentBuilderSingleton = newDocumentBuilder();
    }
}
