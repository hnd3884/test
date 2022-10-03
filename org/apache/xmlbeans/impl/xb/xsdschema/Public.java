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
import org.apache.xmlbeans.XmlToken;

public interface Public extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Public.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("publicf3catype");
    
    public static final class Factory
    {
        public static Public newValue(final Object obj) {
            return (Public)Public.type.newValue(obj);
        }
        
        public static Public newInstance() {
            return (Public)XmlBeans.getContextTypeLoader().newInstance(Public.type, null);
        }
        
        public static Public newInstance(final XmlOptions options) {
            return (Public)XmlBeans.getContextTypeLoader().newInstance(Public.type, options);
        }
        
        public static Public parse(final String xmlAsString) throws XmlException {
            return (Public)XmlBeans.getContextTypeLoader().parse(xmlAsString, Public.type, null);
        }
        
        public static Public parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Public)XmlBeans.getContextTypeLoader().parse(xmlAsString, Public.type, options);
        }
        
        public static Public parse(final File file) throws XmlException, IOException {
            return (Public)XmlBeans.getContextTypeLoader().parse(file, Public.type, null);
        }
        
        public static Public parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Public)XmlBeans.getContextTypeLoader().parse(file, Public.type, options);
        }
        
        public static Public parse(final URL u) throws XmlException, IOException {
            return (Public)XmlBeans.getContextTypeLoader().parse(u, Public.type, null);
        }
        
        public static Public parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Public)XmlBeans.getContextTypeLoader().parse(u, Public.type, options);
        }
        
        public static Public parse(final InputStream is) throws XmlException, IOException {
            return (Public)XmlBeans.getContextTypeLoader().parse(is, Public.type, null);
        }
        
        public static Public parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Public)XmlBeans.getContextTypeLoader().parse(is, Public.type, options);
        }
        
        public static Public parse(final Reader r) throws XmlException, IOException {
            return (Public)XmlBeans.getContextTypeLoader().parse(r, Public.type, null);
        }
        
        public static Public parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Public)XmlBeans.getContextTypeLoader().parse(r, Public.type, options);
        }
        
        public static Public parse(final XMLStreamReader sr) throws XmlException {
            return (Public)XmlBeans.getContextTypeLoader().parse(sr, Public.type, null);
        }
        
        public static Public parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Public)XmlBeans.getContextTypeLoader().parse(sr, Public.type, options);
        }
        
        public static Public parse(final Node node) throws XmlException {
            return (Public)XmlBeans.getContextTypeLoader().parse(node, Public.type, null);
        }
        
        public static Public parse(final Node node, final XmlOptions options) throws XmlException {
            return (Public)XmlBeans.getContextTypeLoader().parse(node, Public.type, options);
        }
        
        @Deprecated
        public static Public parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Public)XmlBeans.getContextTypeLoader().parse(xis, Public.type, null);
        }
        
        @Deprecated
        public static Public parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Public)XmlBeans.getContextTypeLoader().parse(xis, Public.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Public.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Public.type, options);
        }
        
        private Factory() {
        }
    }
}
