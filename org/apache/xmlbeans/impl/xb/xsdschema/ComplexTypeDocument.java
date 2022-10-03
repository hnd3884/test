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

public interface ComplexTypeDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ComplexTypeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("complextype83cbdoctype");
    
    TopLevelComplexType getComplexType();
    
    void setComplexType(final TopLevelComplexType p0);
    
    TopLevelComplexType addNewComplexType();
    
    public static final class Factory
    {
        public static ComplexTypeDocument newInstance() {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().newInstance(ComplexTypeDocument.type, null);
        }
        
        public static ComplexTypeDocument newInstance(final XmlOptions options) {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().newInstance(ComplexTypeDocument.type, options);
        }
        
        public static ComplexTypeDocument parse(final String xmlAsString) throws XmlException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ComplexTypeDocument.type, null);
        }
        
        public static ComplexTypeDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ComplexTypeDocument.type, options);
        }
        
        public static ComplexTypeDocument parse(final File file) throws XmlException, IOException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(file, ComplexTypeDocument.type, null);
        }
        
        public static ComplexTypeDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(file, ComplexTypeDocument.type, options);
        }
        
        public static ComplexTypeDocument parse(final URL u) throws XmlException, IOException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(u, ComplexTypeDocument.type, null);
        }
        
        public static ComplexTypeDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(u, ComplexTypeDocument.type, options);
        }
        
        public static ComplexTypeDocument parse(final InputStream is) throws XmlException, IOException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(is, ComplexTypeDocument.type, null);
        }
        
        public static ComplexTypeDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(is, ComplexTypeDocument.type, options);
        }
        
        public static ComplexTypeDocument parse(final Reader r) throws XmlException, IOException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(r, ComplexTypeDocument.type, null);
        }
        
        public static ComplexTypeDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(r, ComplexTypeDocument.type, options);
        }
        
        public static ComplexTypeDocument parse(final XMLStreamReader sr) throws XmlException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(sr, ComplexTypeDocument.type, null);
        }
        
        public static ComplexTypeDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(sr, ComplexTypeDocument.type, options);
        }
        
        public static ComplexTypeDocument parse(final Node node) throws XmlException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(node, ComplexTypeDocument.type, null);
        }
        
        public static ComplexTypeDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(node, ComplexTypeDocument.type, options);
        }
        
        @Deprecated
        public static ComplexTypeDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(xis, ComplexTypeDocument.type, null);
        }
        
        @Deprecated
        public static ComplexTypeDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ComplexTypeDocument)XmlBeans.getContextTypeLoader().parse(xis, ComplexTypeDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ComplexTypeDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ComplexTypeDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
