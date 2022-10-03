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

public interface ExplicitGroup extends Group
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ExplicitGroup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("explicitgroup4efatype");
    
    public static final class Factory
    {
        public static ExplicitGroup newInstance() {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().newInstance(ExplicitGroup.type, null);
        }
        
        public static ExplicitGroup newInstance(final XmlOptions options) {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().newInstance(ExplicitGroup.type, options);
        }
        
        public static ExplicitGroup parse(final String xmlAsString) throws XmlException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, ExplicitGroup.type, null);
        }
        
        public static ExplicitGroup parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, ExplicitGroup.type, options);
        }
        
        public static ExplicitGroup parse(final File file) throws XmlException, IOException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(file, ExplicitGroup.type, null);
        }
        
        public static ExplicitGroup parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(file, ExplicitGroup.type, options);
        }
        
        public static ExplicitGroup parse(final URL u) throws XmlException, IOException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(u, ExplicitGroup.type, null);
        }
        
        public static ExplicitGroup parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(u, ExplicitGroup.type, options);
        }
        
        public static ExplicitGroup parse(final InputStream is) throws XmlException, IOException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(is, ExplicitGroup.type, null);
        }
        
        public static ExplicitGroup parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(is, ExplicitGroup.type, options);
        }
        
        public static ExplicitGroup parse(final Reader r) throws XmlException, IOException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(r, ExplicitGroup.type, null);
        }
        
        public static ExplicitGroup parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(r, ExplicitGroup.type, options);
        }
        
        public static ExplicitGroup parse(final XMLStreamReader sr) throws XmlException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(sr, ExplicitGroup.type, null);
        }
        
        public static ExplicitGroup parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(sr, ExplicitGroup.type, options);
        }
        
        public static ExplicitGroup parse(final Node node) throws XmlException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(node, ExplicitGroup.type, null);
        }
        
        public static ExplicitGroup parse(final Node node, final XmlOptions options) throws XmlException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(node, ExplicitGroup.type, options);
        }
        
        @Deprecated
        public static ExplicitGroup parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(xis, ExplicitGroup.type, null);
        }
        
        @Deprecated
        public static ExplicitGroup parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ExplicitGroup)XmlBeans.getContextTypeLoader().parse(xis, ExplicitGroup.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ExplicitGroup.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ExplicitGroup.type, options);
        }
        
        private Factory() {
        }
    }
}
