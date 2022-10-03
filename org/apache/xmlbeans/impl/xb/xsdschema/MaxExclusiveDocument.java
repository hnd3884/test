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

public interface MaxExclusiveDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MaxExclusiveDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("maxexclusive6d69doctype");
    
    Facet getMaxExclusive();
    
    void setMaxExclusive(final Facet p0);
    
    Facet addNewMaxExclusive();
    
    public static final class Factory
    {
        public static MaxExclusiveDocument newInstance() {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().newInstance(MaxExclusiveDocument.type, null);
        }
        
        public static MaxExclusiveDocument newInstance(final XmlOptions options) {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().newInstance(MaxExclusiveDocument.type, options);
        }
        
        public static MaxExclusiveDocument parse(final String xmlAsString) throws XmlException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MaxExclusiveDocument.type, null);
        }
        
        public static MaxExclusiveDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MaxExclusiveDocument.type, options);
        }
        
        public static MaxExclusiveDocument parse(final File file) throws XmlException, IOException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(file, MaxExclusiveDocument.type, null);
        }
        
        public static MaxExclusiveDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(file, MaxExclusiveDocument.type, options);
        }
        
        public static MaxExclusiveDocument parse(final URL u) throws XmlException, IOException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(u, MaxExclusiveDocument.type, null);
        }
        
        public static MaxExclusiveDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(u, MaxExclusiveDocument.type, options);
        }
        
        public static MaxExclusiveDocument parse(final InputStream is) throws XmlException, IOException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(is, MaxExclusiveDocument.type, null);
        }
        
        public static MaxExclusiveDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(is, MaxExclusiveDocument.type, options);
        }
        
        public static MaxExclusiveDocument parse(final Reader r) throws XmlException, IOException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(r, MaxExclusiveDocument.type, null);
        }
        
        public static MaxExclusiveDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(r, MaxExclusiveDocument.type, options);
        }
        
        public static MaxExclusiveDocument parse(final XMLStreamReader sr) throws XmlException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(sr, MaxExclusiveDocument.type, null);
        }
        
        public static MaxExclusiveDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(sr, MaxExclusiveDocument.type, options);
        }
        
        public static MaxExclusiveDocument parse(final Node node) throws XmlException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(node, MaxExclusiveDocument.type, null);
        }
        
        public static MaxExclusiveDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(node, MaxExclusiveDocument.type, options);
        }
        
        @Deprecated
        public static MaxExclusiveDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(xis, MaxExclusiveDocument.type, null);
        }
        
        @Deprecated
        public static MaxExclusiveDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (MaxExclusiveDocument)XmlBeans.getContextTypeLoader().parse(xis, MaxExclusiveDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MaxExclusiveDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MaxExclusiveDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
