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

public interface EnumerationDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(EnumerationDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("enumeration052edoctype");
    
    NoFixedFacet getEnumeration();
    
    void setEnumeration(final NoFixedFacet p0);
    
    NoFixedFacet addNewEnumeration();
    
    public static final class Factory
    {
        public static EnumerationDocument newInstance() {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().newInstance(EnumerationDocument.type, null);
        }
        
        public static EnumerationDocument newInstance(final XmlOptions options) {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().newInstance(EnumerationDocument.type, options);
        }
        
        public static EnumerationDocument parse(final String xmlAsString) throws XmlException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, EnumerationDocument.type, null);
        }
        
        public static EnumerationDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, EnumerationDocument.type, options);
        }
        
        public static EnumerationDocument parse(final File file) throws XmlException, IOException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(file, EnumerationDocument.type, null);
        }
        
        public static EnumerationDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(file, EnumerationDocument.type, options);
        }
        
        public static EnumerationDocument parse(final URL u) throws XmlException, IOException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(u, EnumerationDocument.type, null);
        }
        
        public static EnumerationDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(u, EnumerationDocument.type, options);
        }
        
        public static EnumerationDocument parse(final InputStream is) throws XmlException, IOException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(is, EnumerationDocument.type, null);
        }
        
        public static EnumerationDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(is, EnumerationDocument.type, options);
        }
        
        public static EnumerationDocument parse(final Reader r) throws XmlException, IOException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(r, EnumerationDocument.type, null);
        }
        
        public static EnumerationDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(r, EnumerationDocument.type, options);
        }
        
        public static EnumerationDocument parse(final XMLStreamReader sr) throws XmlException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(sr, EnumerationDocument.type, null);
        }
        
        public static EnumerationDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(sr, EnumerationDocument.type, options);
        }
        
        public static EnumerationDocument parse(final Node node) throws XmlException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(node, EnumerationDocument.type, null);
        }
        
        public static EnumerationDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(node, EnumerationDocument.type, options);
        }
        
        @Deprecated
        public static EnumerationDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(xis, EnumerationDocument.type, null);
        }
        
        @Deprecated
        public static EnumerationDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (EnumerationDocument)XmlBeans.getContextTypeLoader().parse(xis, EnumerationDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, EnumerationDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, EnumerationDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
