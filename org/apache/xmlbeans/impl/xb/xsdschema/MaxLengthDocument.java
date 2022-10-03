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

public interface MaxLengthDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MaxLengthDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("maxlengthf8abdoctype");
    
    NumFacet getMaxLength();
    
    void setMaxLength(final NumFacet p0);
    
    NumFacet addNewMaxLength();
    
    public static final class Factory
    {
        public static MaxLengthDocument newInstance() {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().newInstance(MaxLengthDocument.type, null);
        }
        
        public static MaxLengthDocument newInstance(final XmlOptions options) {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().newInstance(MaxLengthDocument.type, options);
        }
        
        public static MaxLengthDocument parse(final String xmlAsString) throws XmlException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MaxLengthDocument.type, null);
        }
        
        public static MaxLengthDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MaxLengthDocument.type, options);
        }
        
        public static MaxLengthDocument parse(final File file) throws XmlException, IOException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(file, MaxLengthDocument.type, null);
        }
        
        public static MaxLengthDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(file, MaxLengthDocument.type, options);
        }
        
        public static MaxLengthDocument parse(final URL u) throws XmlException, IOException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(u, MaxLengthDocument.type, null);
        }
        
        public static MaxLengthDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(u, MaxLengthDocument.type, options);
        }
        
        public static MaxLengthDocument parse(final InputStream is) throws XmlException, IOException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(is, MaxLengthDocument.type, null);
        }
        
        public static MaxLengthDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(is, MaxLengthDocument.type, options);
        }
        
        public static MaxLengthDocument parse(final Reader r) throws XmlException, IOException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(r, MaxLengthDocument.type, null);
        }
        
        public static MaxLengthDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(r, MaxLengthDocument.type, options);
        }
        
        public static MaxLengthDocument parse(final XMLStreamReader sr) throws XmlException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(sr, MaxLengthDocument.type, null);
        }
        
        public static MaxLengthDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(sr, MaxLengthDocument.type, options);
        }
        
        public static MaxLengthDocument parse(final Node node) throws XmlException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(node, MaxLengthDocument.type, null);
        }
        
        public static MaxLengthDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(node, MaxLengthDocument.type, options);
        }
        
        @Deprecated
        public static MaxLengthDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(xis, MaxLengthDocument.type, null);
        }
        
        @Deprecated
        public static MaxLengthDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (MaxLengthDocument)XmlBeans.getContextTypeLoader().parse(xis, MaxLengthDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MaxLengthDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MaxLengthDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
