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

public interface SimpleRestrictionType extends RestrictionType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SimpleRestrictionType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("simplerestrictiontypeeab1type");
    
    public static final class Factory
    {
        public static SimpleRestrictionType newInstance() {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().newInstance(SimpleRestrictionType.type, null);
        }
        
        public static SimpleRestrictionType newInstance(final XmlOptions options) {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().newInstance(SimpleRestrictionType.type, options);
        }
        
        public static SimpleRestrictionType parse(final String xmlAsString) throws XmlException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleRestrictionType.type, null);
        }
        
        public static SimpleRestrictionType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleRestrictionType.type, options);
        }
        
        public static SimpleRestrictionType parse(final File file) throws XmlException, IOException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(file, SimpleRestrictionType.type, null);
        }
        
        public static SimpleRestrictionType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(file, SimpleRestrictionType.type, options);
        }
        
        public static SimpleRestrictionType parse(final URL u) throws XmlException, IOException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(u, SimpleRestrictionType.type, null);
        }
        
        public static SimpleRestrictionType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(u, SimpleRestrictionType.type, options);
        }
        
        public static SimpleRestrictionType parse(final InputStream is) throws XmlException, IOException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(is, SimpleRestrictionType.type, null);
        }
        
        public static SimpleRestrictionType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(is, SimpleRestrictionType.type, options);
        }
        
        public static SimpleRestrictionType parse(final Reader r) throws XmlException, IOException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(r, SimpleRestrictionType.type, null);
        }
        
        public static SimpleRestrictionType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(r, SimpleRestrictionType.type, options);
        }
        
        public static SimpleRestrictionType parse(final XMLStreamReader sr) throws XmlException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(sr, SimpleRestrictionType.type, null);
        }
        
        public static SimpleRestrictionType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(sr, SimpleRestrictionType.type, options);
        }
        
        public static SimpleRestrictionType parse(final Node node) throws XmlException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(node, SimpleRestrictionType.type, null);
        }
        
        public static SimpleRestrictionType parse(final Node node, final XmlOptions options) throws XmlException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(node, SimpleRestrictionType.type, options);
        }
        
        @Deprecated
        public static SimpleRestrictionType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(xis, SimpleRestrictionType.type, null);
        }
        
        @Deprecated
        public static SimpleRestrictionType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SimpleRestrictionType)XmlBeans.getContextTypeLoader().parse(xis, SimpleRestrictionType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleRestrictionType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleRestrictionType.type, options);
        }
        
        private Factory() {
        }
    }
}
