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

public interface LocalSimpleType extends SimpleType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(LocalSimpleType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("localsimpletype410etype");
    
    public static final class Factory
    {
        public static LocalSimpleType newInstance() {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().newInstance(LocalSimpleType.type, null);
        }
        
        public static LocalSimpleType newInstance(final XmlOptions options) {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().newInstance(LocalSimpleType.type, options);
        }
        
        public static LocalSimpleType parse(final String xmlAsString) throws XmlException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(xmlAsString, LocalSimpleType.type, null);
        }
        
        public static LocalSimpleType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(xmlAsString, LocalSimpleType.type, options);
        }
        
        public static LocalSimpleType parse(final File file) throws XmlException, IOException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(file, LocalSimpleType.type, null);
        }
        
        public static LocalSimpleType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(file, LocalSimpleType.type, options);
        }
        
        public static LocalSimpleType parse(final URL u) throws XmlException, IOException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(u, LocalSimpleType.type, null);
        }
        
        public static LocalSimpleType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(u, LocalSimpleType.type, options);
        }
        
        public static LocalSimpleType parse(final InputStream is) throws XmlException, IOException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(is, LocalSimpleType.type, null);
        }
        
        public static LocalSimpleType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(is, LocalSimpleType.type, options);
        }
        
        public static LocalSimpleType parse(final Reader r) throws XmlException, IOException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(r, LocalSimpleType.type, null);
        }
        
        public static LocalSimpleType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(r, LocalSimpleType.type, options);
        }
        
        public static LocalSimpleType parse(final XMLStreamReader sr) throws XmlException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(sr, LocalSimpleType.type, null);
        }
        
        public static LocalSimpleType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(sr, LocalSimpleType.type, options);
        }
        
        public static LocalSimpleType parse(final Node node) throws XmlException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(node, LocalSimpleType.type, null);
        }
        
        public static LocalSimpleType parse(final Node node, final XmlOptions options) throws XmlException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(node, LocalSimpleType.type, options);
        }
        
        @Deprecated
        public static LocalSimpleType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(xis, LocalSimpleType.type, null);
        }
        
        @Deprecated
        public static LocalSimpleType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (LocalSimpleType)XmlBeans.getContextTypeLoader().parse(xis, LocalSimpleType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LocalSimpleType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LocalSimpleType.type, options);
        }
        
        private Factory() {
        }
    }
}
