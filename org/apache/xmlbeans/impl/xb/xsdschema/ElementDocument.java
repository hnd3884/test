package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ElementDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ElementDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("element7f99doctype");
    
    TopLevelElement getElement();
    
    void setElement(final TopLevelElement p0);
    
    TopLevelElement addNewElement();
    
    public static final class Factory
    {
        public static ElementDocument newInstance() {
            return (ElementDocument)XmlBeans.getContextTypeLoader().newInstance(ElementDocument.type, null);
        }
        
        public static ElementDocument newInstance(final XmlOptions options) {
            return (ElementDocument)XmlBeans.getContextTypeLoader().newInstance(ElementDocument.type, options);
        }
        
        public static ElementDocument parse(final String xmlAsString) throws XmlException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ElementDocument.type, null);
        }
        
        public static ElementDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ElementDocument.type, options);
        }
        
        public static ElementDocument parse(final File file) throws XmlException, IOException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(file, ElementDocument.type, null);
        }
        
        public static ElementDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(file, ElementDocument.type, options);
        }
        
        public static ElementDocument parse(final URL u) throws XmlException, IOException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(u, ElementDocument.type, null);
        }
        
        public static ElementDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(u, ElementDocument.type, options);
        }
        
        public static ElementDocument parse(final InputStream is) throws XmlException, IOException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(is, ElementDocument.type, null);
        }
        
        public static ElementDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(is, ElementDocument.type, options);
        }
        
        public static ElementDocument parse(final Reader r) throws XmlException, IOException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(r, ElementDocument.type, null);
        }
        
        public static ElementDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(r, ElementDocument.type, options);
        }
        
        public static ElementDocument parse(final XMLStreamReader sr) throws XmlException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(sr, ElementDocument.type, null);
        }
        
        public static ElementDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(sr, ElementDocument.type, options);
        }
        
        public static ElementDocument parse(final Node node) throws XmlException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(node, ElementDocument.type, null);
        }
        
        public static ElementDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(node, ElementDocument.type, options);
        }
        
        @Deprecated
        public static ElementDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(xis, ElementDocument.type, null);
        }
        
        @Deprecated
        public static ElementDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ElementDocument)XmlBeans.getContextTypeLoader().parse(xis, ElementDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ElementDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ElementDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
