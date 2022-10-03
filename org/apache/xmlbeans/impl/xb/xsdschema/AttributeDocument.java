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

public interface AttributeDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AttributeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("attributeedb9doctype");
    
    TopLevelAttribute getAttribute();
    
    void setAttribute(final TopLevelAttribute p0);
    
    TopLevelAttribute addNewAttribute();
    
    public static final class Factory
    {
        public static AttributeDocument newInstance() {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().newInstance(AttributeDocument.type, null);
        }
        
        public static AttributeDocument newInstance(final XmlOptions options) {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().newInstance(AttributeDocument.type, options);
        }
        
        public static AttributeDocument parse(final String xmlAsString) throws XmlException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AttributeDocument.type, null);
        }
        
        public static AttributeDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AttributeDocument.type, options);
        }
        
        public static AttributeDocument parse(final File file) throws XmlException, IOException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(file, AttributeDocument.type, null);
        }
        
        public static AttributeDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(file, AttributeDocument.type, options);
        }
        
        public static AttributeDocument parse(final URL u) throws XmlException, IOException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(u, AttributeDocument.type, null);
        }
        
        public static AttributeDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(u, AttributeDocument.type, options);
        }
        
        public static AttributeDocument parse(final InputStream is) throws XmlException, IOException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(is, AttributeDocument.type, null);
        }
        
        public static AttributeDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(is, AttributeDocument.type, options);
        }
        
        public static AttributeDocument parse(final Reader r) throws XmlException, IOException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(r, AttributeDocument.type, null);
        }
        
        public static AttributeDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(r, AttributeDocument.type, options);
        }
        
        public static AttributeDocument parse(final XMLStreamReader sr) throws XmlException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(sr, AttributeDocument.type, null);
        }
        
        public static AttributeDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(sr, AttributeDocument.type, options);
        }
        
        public static AttributeDocument parse(final Node node) throws XmlException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(node, AttributeDocument.type, null);
        }
        
        public static AttributeDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(node, AttributeDocument.type, options);
        }
        
        @Deprecated
        public static AttributeDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(xis, AttributeDocument.type, null);
        }
        
        @Deprecated
        public static AttributeDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AttributeDocument)XmlBeans.getContextTypeLoader().parse(xis, AttributeDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AttributeDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AttributeDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
