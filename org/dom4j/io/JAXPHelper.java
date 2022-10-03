package org.dom4j.io;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;

class JAXPHelper
{
    protected JAXPHelper() {
    }
    
    public static XMLReader createXMLReader(final boolean validating, final boolean namespaceAware) throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceAware);
        final SAXParser parser = factory.newSAXParser();
        return parser.getXMLReader();
    }
    
    public static Document createDocument(final boolean validating, final boolean namespaceAware) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(validating);
        factory.setNamespaceAware(namespaceAware);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
}
