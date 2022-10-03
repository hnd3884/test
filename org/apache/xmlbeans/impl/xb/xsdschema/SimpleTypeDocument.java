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

public interface SimpleTypeDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SimpleTypeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("simpletypedef7doctype");
    
    TopLevelSimpleType getSimpleType();
    
    void setSimpleType(final TopLevelSimpleType p0);
    
    TopLevelSimpleType addNewSimpleType();
    
    public static final class Factory
    {
        public static SimpleTypeDocument newInstance() {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().newInstance(SimpleTypeDocument.type, null);
        }
        
        public static SimpleTypeDocument newInstance(final XmlOptions options) {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().newInstance(SimpleTypeDocument.type, options);
        }
        
        public static SimpleTypeDocument parse(final String xmlAsString) throws XmlException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleTypeDocument.type, null);
        }
        
        public static SimpleTypeDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleTypeDocument.type, options);
        }
        
        public static SimpleTypeDocument parse(final File file) throws XmlException, IOException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(file, SimpleTypeDocument.type, null);
        }
        
        public static SimpleTypeDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(file, SimpleTypeDocument.type, options);
        }
        
        public static SimpleTypeDocument parse(final URL u) throws XmlException, IOException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(u, SimpleTypeDocument.type, null);
        }
        
        public static SimpleTypeDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(u, SimpleTypeDocument.type, options);
        }
        
        public static SimpleTypeDocument parse(final InputStream is) throws XmlException, IOException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(is, SimpleTypeDocument.type, null);
        }
        
        public static SimpleTypeDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(is, SimpleTypeDocument.type, options);
        }
        
        public static SimpleTypeDocument parse(final Reader r) throws XmlException, IOException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(r, SimpleTypeDocument.type, null);
        }
        
        public static SimpleTypeDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(r, SimpleTypeDocument.type, options);
        }
        
        public static SimpleTypeDocument parse(final XMLStreamReader sr) throws XmlException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(sr, SimpleTypeDocument.type, null);
        }
        
        public static SimpleTypeDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(sr, SimpleTypeDocument.type, options);
        }
        
        public static SimpleTypeDocument parse(final Node node) throws XmlException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(node, SimpleTypeDocument.type, null);
        }
        
        public static SimpleTypeDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(node, SimpleTypeDocument.type, options);
        }
        
        @Deprecated
        public static SimpleTypeDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(xis, SimpleTypeDocument.type, null);
        }
        
        @Deprecated
        public static SimpleTypeDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SimpleTypeDocument)XmlBeans.getContextTypeLoader().parse(xis, SimpleTypeDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleTypeDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleTypeDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
