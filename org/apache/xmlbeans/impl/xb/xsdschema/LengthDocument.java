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

public interface LengthDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(LengthDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("length7edddoctype");
    
    NumFacet getLength();
    
    void setLength(final NumFacet p0);
    
    NumFacet addNewLength();
    
    public static final class Factory
    {
        public static LengthDocument newInstance() {
            return (LengthDocument)XmlBeans.getContextTypeLoader().newInstance(LengthDocument.type, null);
        }
        
        public static LengthDocument newInstance(final XmlOptions options) {
            return (LengthDocument)XmlBeans.getContextTypeLoader().newInstance(LengthDocument.type, options);
        }
        
        public static LengthDocument parse(final String xmlAsString) throws XmlException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, LengthDocument.type, null);
        }
        
        public static LengthDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, LengthDocument.type, options);
        }
        
        public static LengthDocument parse(final File file) throws XmlException, IOException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(file, LengthDocument.type, null);
        }
        
        public static LengthDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(file, LengthDocument.type, options);
        }
        
        public static LengthDocument parse(final URL u) throws XmlException, IOException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(u, LengthDocument.type, null);
        }
        
        public static LengthDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(u, LengthDocument.type, options);
        }
        
        public static LengthDocument parse(final InputStream is) throws XmlException, IOException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(is, LengthDocument.type, null);
        }
        
        public static LengthDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(is, LengthDocument.type, options);
        }
        
        public static LengthDocument parse(final Reader r) throws XmlException, IOException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(r, LengthDocument.type, null);
        }
        
        public static LengthDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(r, LengthDocument.type, options);
        }
        
        public static LengthDocument parse(final XMLStreamReader sr) throws XmlException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(sr, LengthDocument.type, null);
        }
        
        public static LengthDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(sr, LengthDocument.type, options);
        }
        
        public static LengthDocument parse(final Node node) throws XmlException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(node, LengthDocument.type, null);
        }
        
        public static LengthDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(node, LengthDocument.type, options);
        }
        
        @Deprecated
        public static LengthDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(xis, LengthDocument.type, null);
        }
        
        @Deprecated
        public static LengthDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (LengthDocument)XmlBeans.getContextTypeLoader().parse(xis, LengthDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LengthDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, LengthDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
