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

public interface ComplexRestrictionType extends RestrictionType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ComplexRestrictionType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("complexrestrictiontype1b7dtype");
    
    public static final class Factory
    {
        public static ComplexRestrictionType newInstance() {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().newInstance(ComplexRestrictionType.type, null);
        }
        
        public static ComplexRestrictionType newInstance(final XmlOptions options) {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().newInstance(ComplexRestrictionType.type, options);
        }
        
        public static ComplexRestrictionType parse(final String xmlAsString) throws XmlException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, ComplexRestrictionType.type, null);
        }
        
        public static ComplexRestrictionType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, ComplexRestrictionType.type, options);
        }
        
        public static ComplexRestrictionType parse(final File file) throws XmlException, IOException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(file, ComplexRestrictionType.type, null);
        }
        
        public static ComplexRestrictionType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(file, ComplexRestrictionType.type, options);
        }
        
        public static ComplexRestrictionType parse(final URL u) throws XmlException, IOException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(u, ComplexRestrictionType.type, null);
        }
        
        public static ComplexRestrictionType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(u, ComplexRestrictionType.type, options);
        }
        
        public static ComplexRestrictionType parse(final InputStream is) throws XmlException, IOException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(is, ComplexRestrictionType.type, null);
        }
        
        public static ComplexRestrictionType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(is, ComplexRestrictionType.type, options);
        }
        
        public static ComplexRestrictionType parse(final Reader r) throws XmlException, IOException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(r, ComplexRestrictionType.type, null);
        }
        
        public static ComplexRestrictionType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(r, ComplexRestrictionType.type, options);
        }
        
        public static ComplexRestrictionType parse(final XMLStreamReader sr) throws XmlException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(sr, ComplexRestrictionType.type, null);
        }
        
        public static ComplexRestrictionType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(sr, ComplexRestrictionType.type, options);
        }
        
        public static ComplexRestrictionType parse(final Node node) throws XmlException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(node, ComplexRestrictionType.type, null);
        }
        
        public static ComplexRestrictionType parse(final Node node, final XmlOptions options) throws XmlException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(node, ComplexRestrictionType.type, options);
        }
        
        @Deprecated
        public static ComplexRestrictionType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(xis, ComplexRestrictionType.type, null);
        }
        
        @Deprecated
        public static ComplexRestrictionType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ComplexRestrictionType)XmlBeans.getContextTypeLoader().parse(xis, ComplexRestrictionType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ComplexRestrictionType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ComplexRestrictionType.type, options);
        }
        
        private Factory() {
        }
    }
}
