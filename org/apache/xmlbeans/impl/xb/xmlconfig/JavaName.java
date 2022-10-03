package org.apache.xmlbeans.impl.xb.xmlconfig;

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

public interface JavaName extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(JavaName.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLCONFIG").resolveHandle("javanamee640type");
    
    public static final class Factory
    {
        public static JavaName newValue(final Object obj) {
            return (JavaName)JavaName.type.newValue(obj);
        }
        
        public static JavaName newInstance() {
            return (JavaName)XmlBeans.getContextTypeLoader().newInstance(JavaName.type, null);
        }
        
        public static JavaName newInstance(final XmlOptions options) {
            return (JavaName)XmlBeans.getContextTypeLoader().newInstance(JavaName.type, options);
        }
        
        public static JavaName parse(final String xmlAsString) throws XmlException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(xmlAsString, JavaName.type, null);
        }
        
        public static JavaName parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(xmlAsString, JavaName.type, options);
        }
        
        public static JavaName parse(final File file) throws XmlException, IOException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(file, JavaName.type, null);
        }
        
        public static JavaName parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(file, JavaName.type, options);
        }
        
        public static JavaName parse(final URL u) throws XmlException, IOException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(u, JavaName.type, null);
        }
        
        public static JavaName parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(u, JavaName.type, options);
        }
        
        public static JavaName parse(final InputStream is) throws XmlException, IOException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(is, JavaName.type, null);
        }
        
        public static JavaName parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(is, JavaName.type, options);
        }
        
        public static JavaName parse(final Reader r) throws XmlException, IOException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(r, JavaName.type, null);
        }
        
        public static JavaName parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(r, JavaName.type, options);
        }
        
        public static JavaName parse(final XMLStreamReader sr) throws XmlException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(sr, JavaName.type, null);
        }
        
        public static JavaName parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(sr, JavaName.type, options);
        }
        
        public static JavaName parse(final Node node) throws XmlException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(node, JavaName.type, null);
        }
        
        public static JavaName parse(final Node node, final XmlOptions options) throws XmlException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(node, JavaName.type, options);
        }
        
        @Deprecated
        public static JavaName parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(xis, JavaName.type, null);
        }
        
        @Deprecated
        public static JavaName parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (JavaName)XmlBeans.getContextTypeLoader().parse(xis, JavaName.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, JavaName.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, JavaName.type, options);
        }
        
        private Factory() {
        }
    }
}
