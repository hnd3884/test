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

public interface AnyAttributeDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AnyAttributeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("anyattribute23b3doctype");
    
    Wildcard getAnyAttribute();
    
    void setAnyAttribute(final Wildcard p0);
    
    Wildcard addNewAnyAttribute();
    
    public static final class Factory
    {
        public static AnyAttributeDocument newInstance() {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().newInstance(AnyAttributeDocument.type, null);
        }
        
        public static AnyAttributeDocument newInstance(final XmlOptions options) {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().newInstance(AnyAttributeDocument.type, options);
        }
        
        public static AnyAttributeDocument parse(final String xmlAsString) throws XmlException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AnyAttributeDocument.type, null);
        }
        
        public static AnyAttributeDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AnyAttributeDocument.type, options);
        }
        
        public static AnyAttributeDocument parse(final File file) throws XmlException, IOException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(file, AnyAttributeDocument.type, null);
        }
        
        public static AnyAttributeDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(file, AnyAttributeDocument.type, options);
        }
        
        public static AnyAttributeDocument parse(final URL u) throws XmlException, IOException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(u, AnyAttributeDocument.type, null);
        }
        
        public static AnyAttributeDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(u, AnyAttributeDocument.type, options);
        }
        
        public static AnyAttributeDocument parse(final InputStream is) throws XmlException, IOException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(is, AnyAttributeDocument.type, null);
        }
        
        public static AnyAttributeDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(is, AnyAttributeDocument.type, options);
        }
        
        public static AnyAttributeDocument parse(final Reader r) throws XmlException, IOException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(r, AnyAttributeDocument.type, null);
        }
        
        public static AnyAttributeDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(r, AnyAttributeDocument.type, options);
        }
        
        public static AnyAttributeDocument parse(final XMLStreamReader sr) throws XmlException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(sr, AnyAttributeDocument.type, null);
        }
        
        public static AnyAttributeDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(sr, AnyAttributeDocument.type, options);
        }
        
        public static AnyAttributeDocument parse(final Node node) throws XmlException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(node, AnyAttributeDocument.type, null);
        }
        
        public static AnyAttributeDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(node, AnyAttributeDocument.type, options);
        }
        
        @Deprecated
        public static AnyAttributeDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(xis, AnyAttributeDocument.type, null);
        }
        
        @Deprecated
        public static AnyAttributeDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AnyAttributeDocument)XmlBeans.getContextTypeLoader().parse(xis, AnyAttributeDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AnyAttributeDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AnyAttributeDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
