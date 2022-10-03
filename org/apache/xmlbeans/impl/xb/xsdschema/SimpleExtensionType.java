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

public interface SimpleExtensionType extends ExtensionType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SimpleExtensionType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("simpleextensiontypee0detype");
    
    public static final class Factory
    {
        public static SimpleExtensionType newInstance() {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().newInstance(SimpleExtensionType.type, null);
        }
        
        public static SimpleExtensionType newInstance(final XmlOptions options) {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().newInstance(SimpleExtensionType.type, options);
        }
        
        public static SimpleExtensionType parse(final String xmlAsString) throws XmlException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleExtensionType.type, null);
        }
        
        public static SimpleExtensionType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleExtensionType.type, options);
        }
        
        public static SimpleExtensionType parse(final File file) throws XmlException, IOException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(file, SimpleExtensionType.type, null);
        }
        
        public static SimpleExtensionType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(file, SimpleExtensionType.type, options);
        }
        
        public static SimpleExtensionType parse(final URL u) throws XmlException, IOException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(u, SimpleExtensionType.type, null);
        }
        
        public static SimpleExtensionType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(u, SimpleExtensionType.type, options);
        }
        
        public static SimpleExtensionType parse(final InputStream is) throws XmlException, IOException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(is, SimpleExtensionType.type, null);
        }
        
        public static SimpleExtensionType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(is, SimpleExtensionType.type, options);
        }
        
        public static SimpleExtensionType parse(final Reader r) throws XmlException, IOException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(r, SimpleExtensionType.type, null);
        }
        
        public static SimpleExtensionType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(r, SimpleExtensionType.type, options);
        }
        
        public static SimpleExtensionType parse(final XMLStreamReader sr) throws XmlException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(sr, SimpleExtensionType.type, null);
        }
        
        public static SimpleExtensionType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(sr, SimpleExtensionType.type, options);
        }
        
        public static SimpleExtensionType parse(final Node node) throws XmlException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(node, SimpleExtensionType.type, null);
        }
        
        public static SimpleExtensionType parse(final Node node, final XmlOptions options) throws XmlException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(node, SimpleExtensionType.type, options);
        }
        
        @Deprecated
        public static SimpleExtensionType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(xis, SimpleExtensionType.type, null);
        }
        
        @Deprecated
        public static SimpleExtensionType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SimpleExtensionType)XmlBeans.getContextTypeLoader().parse(xis, SimpleExtensionType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleExtensionType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleExtensionType.type, options);
        }
        
        private Factory() {
        }
    }
}
