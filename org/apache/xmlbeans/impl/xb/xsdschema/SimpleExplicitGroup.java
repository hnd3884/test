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

public interface SimpleExplicitGroup extends ExplicitGroup
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SimpleExplicitGroup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("simpleexplicitgroup428ctype");
    
    public static final class Factory
    {
        public static SimpleExplicitGroup newInstance() {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().newInstance(SimpleExplicitGroup.type, null);
        }
        
        public static SimpleExplicitGroup newInstance(final XmlOptions options) {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().newInstance(SimpleExplicitGroup.type, options);
        }
        
        public static SimpleExplicitGroup parse(final String xmlAsString) throws XmlException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleExplicitGroup.type, null);
        }
        
        public static SimpleExplicitGroup parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleExplicitGroup.type, options);
        }
        
        public static SimpleExplicitGroup parse(final File file) throws XmlException, IOException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(file, SimpleExplicitGroup.type, null);
        }
        
        public static SimpleExplicitGroup parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(file, SimpleExplicitGroup.type, options);
        }
        
        public static SimpleExplicitGroup parse(final URL u) throws XmlException, IOException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(u, SimpleExplicitGroup.type, null);
        }
        
        public static SimpleExplicitGroup parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(u, SimpleExplicitGroup.type, options);
        }
        
        public static SimpleExplicitGroup parse(final InputStream is) throws XmlException, IOException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(is, SimpleExplicitGroup.type, null);
        }
        
        public static SimpleExplicitGroup parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(is, SimpleExplicitGroup.type, options);
        }
        
        public static SimpleExplicitGroup parse(final Reader r) throws XmlException, IOException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(r, SimpleExplicitGroup.type, null);
        }
        
        public static SimpleExplicitGroup parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(r, SimpleExplicitGroup.type, options);
        }
        
        public static SimpleExplicitGroup parse(final XMLStreamReader sr) throws XmlException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(sr, SimpleExplicitGroup.type, null);
        }
        
        public static SimpleExplicitGroup parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(sr, SimpleExplicitGroup.type, options);
        }
        
        public static SimpleExplicitGroup parse(final Node node) throws XmlException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(node, SimpleExplicitGroup.type, null);
        }
        
        public static SimpleExplicitGroup parse(final Node node, final XmlOptions options) throws XmlException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(node, SimpleExplicitGroup.type, options);
        }
        
        @Deprecated
        public static SimpleExplicitGroup parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(xis, SimpleExplicitGroup.type, null);
        }
        
        @Deprecated
        public static SimpleExplicitGroup parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SimpleExplicitGroup)XmlBeans.getContextTypeLoader().parse(xis, SimpleExplicitGroup.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleExplicitGroup.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleExplicitGroup.type, options);
        }
        
        private Factory() {
        }
    }
}
