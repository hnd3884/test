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

public interface LocalElement extends Element
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(LocalElement.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("localelement2ce2type");
    
    public static final class Factory
    {
        public static LocalElement newInstance() {
            return (LocalElement)XmlBeans.getContextTypeLoader().newInstance(LocalElement.type, null);
        }
        
        public static LocalElement newInstance(final XmlOptions options) {
            return (LocalElement)XmlBeans.getContextTypeLoader().newInstance(LocalElement.type, options);
        }
        
        public static LocalElement parse(final String xmlAsString) throws XmlException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(xmlAsString, LocalElement.type, null);
        }
        
        public static LocalElement parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(xmlAsString, LocalElement.type, options);
        }
        
        public static LocalElement parse(final File file) throws XmlException, IOException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(file, LocalElement.type, null);
        }
        
        public static LocalElement parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(file, LocalElement.type, options);
        }
        
        public static LocalElement parse(final URL u) throws XmlException, IOException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(u, LocalElement.type, null);
        }
        
        public static LocalElement parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(u, LocalElement.type, options);
        }
        
        public static LocalElement parse(final InputStream is) throws XmlException, IOException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(is, LocalElement.type, null);
        }
        
        public static LocalElement parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(is, LocalElement.type, options);
        }
        
        public static LocalElement parse(final Reader r) throws XmlException, IOException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(r, LocalElement.type, null);
        }
        
        public static LocalElement parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(r, LocalElement.type, options);
        }
        
        public static LocalElement parse(final XMLStreamReader sr) throws XmlException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(sr, LocalElement.type, null);
        }
        
        public static LocalElement parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(sr, LocalElement.type, options);
        }
        
        public static LocalElement parse(final Node node) throws XmlException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(node, LocalElement.type, null);
        }
        
        public static LocalElement parse(final Node node, final XmlOptions options) throws XmlException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(node, LocalElement.type, options);
        }
        
        @Deprecated
        public static LocalElement parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(xis, LocalElement.type, null);
        }
        
        @Deprecated
        public static LocalElement parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (LocalElement)XmlBeans.getContextTypeLoader().parse(xis, LocalElement.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LocalElement.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LocalElement.type, options);
        }
        
        private Factory() {
        }
    }
}
