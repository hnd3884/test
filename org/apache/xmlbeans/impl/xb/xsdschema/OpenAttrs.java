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

public interface OpenAttrs extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(OpenAttrs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("openattrs2d4dtype");
    
    public static final class Factory
    {
        public static OpenAttrs newInstance() {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().newInstance(OpenAttrs.type, null);
        }
        
        public static OpenAttrs newInstance(final XmlOptions options) {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().newInstance(OpenAttrs.type, options);
        }
        
        public static OpenAttrs parse(final String xmlAsString) throws XmlException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(xmlAsString, OpenAttrs.type, null);
        }
        
        public static OpenAttrs parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(xmlAsString, OpenAttrs.type, options);
        }
        
        public static OpenAttrs parse(final File file) throws XmlException, IOException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(file, OpenAttrs.type, null);
        }
        
        public static OpenAttrs parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(file, OpenAttrs.type, options);
        }
        
        public static OpenAttrs parse(final URL u) throws XmlException, IOException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(u, OpenAttrs.type, null);
        }
        
        public static OpenAttrs parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(u, OpenAttrs.type, options);
        }
        
        public static OpenAttrs parse(final InputStream is) throws XmlException, IOException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(is, OpenAttrs.type, null);
        }
        
        public static OpenAttrs parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(is, OpenAttrs.type, options);
        }
        
        public static OpenAttrs parse(final Reader r) throws XmlException, IOException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(r, OpenAttrs.type, null);
        }
        
        public static OpenAttrs parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(r, OpenAttrs.type, options);
        }
        
        public static OpenAttrs parse(final XMLStreamReader sr) throws XmlException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(sr, OpenAttrs.type, null);
        }
        
        public static OpenAttrs parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(sr, OpenAttrs.type, options);
        }
        
        public static OpenAttrs parse(final Node node) throws XmlException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(node, OpenAttrs.type, null);
        }
        
        public static OpenAttrs parse(final Node node, final XmlOptions options) throws XmlException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(node, OpenAttrs.type, options);
        }
        
        @Deprecated
        public static OpenAttrs parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(xis, OpenAttrs.type, null);
        }
        
        @Deprecated
        public static OpenAttrs parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (OpenAttrs)XmlBeans.getContextTypeLoader().parse(xis, OpenAttrs.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, OpenAttrs.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, OpenAttrs.type, options);
        }
        
        private Factory() {
        }
    }
}
