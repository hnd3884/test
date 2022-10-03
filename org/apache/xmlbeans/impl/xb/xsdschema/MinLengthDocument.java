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

public interface MinLengthDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MinLengthDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("minlengthe7fddoctype");
    
    NumFacet getMinLength();
    
    void setMinLength(final NumFacet p0);
    
    NumFacet addNewMinLength();
    
    public static final class Factory
    {
        public static MinLengthDocument newInstance() {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().newInstance(MinLengthDocument.type, null);
        }
        
        public static MinLengthDocument newInstance(final XmlOptions options) {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().newInstance(MinLengthDocument.type, options);
        }
        
        public static MinLengthDocument parse(final String xmlAsString) throws XmlException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MinLengthDocument.type, null);
        }
        
        public static MinLengthDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MinLengthDocument.type, options);
        }
        
        public static MinLengthDocument parse(final File file) throws XmlException, IOException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(file, MinLengthDocument.type, null);
        }
        
        public static MinLengthDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(file, MinLengthDocument.type, options);
        }
        
        public static MinLengthDocument parse(final URL u) throws XmlException, IOException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(u, MinLengthDocument.type, null);
        }
        
        public static MinLengthDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(u, MinLengthDocument.type, options);
        }
        
        public static MinLengthDocument parse(final InputStream is) throws XmlException, IOException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(is, MinLengthDocument.type, null);
        }
        
        public static MinLengthDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(is, MinLengthDocument.type, options);
        }
        
        public static MinLengthDocument parse(final Reader r) throws XmlException, IOException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(r, MinLengthDocument.type, null);
        }
        
        public static MinLengthDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(r, MinLengthDocument.type, options);
        }
        
        public static MinLengthDocument parse(final XMLStreamReader sr) throws XmlException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(sr, MinLengthDocument.type, null);
        }
        
        public static MinLengthDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(sr, MinLengthDocument.type, options);
        }
        
        public static MinLengthDocument parse(final Node node) throws XmlException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(node, MinLengthDocument.type, null);
        }
        
        public static MinLengthDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(node, MinLengthDocument.type, options);
        }
        
        @Deprecated
        public static MinLengthDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(xis, MinLengthDocument.type, null);
        }
        
        @Deprecated
        public static MinLengthDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (MinLengthDocument)XmlBeans.getContextTypeLoader().parse(xis, MinLengthDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MinLengthDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MinLengthDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
