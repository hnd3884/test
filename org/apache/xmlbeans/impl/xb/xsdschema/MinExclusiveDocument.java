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

public interface MinExclusiveDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MinExclusiveDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("minexclusive64d7doctype");
    
    Facet getMinExclusive();
    
    void setMinExclusive(final Facet p0);
    
    Facet addNewMinExclusive();
    
    public static final class Factory
    {
        public static MinExclusiveDocument newInstance() {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().newInstance(MinExclusiveDocument.type, null);
        }
        
        public static MinExclusiveDocument newInstance(final XmlOptions options) {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().newInstance(MinExclusiveDocument.type, options);
        }
        
        public static MinExclusiveDocument parse(final String xmlAsString) throws XmlException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MinExclusiveDocument.type, null);
        }
        
        public static MinExclusiveDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MinExclusiveDocument.type, options);
        }
        
        public static MinExclusiveDocument parse(final File file) throws XmlException, IOException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(file, MinExclusiveDocument.type, null);
        }
        
        public static MinExclusiveDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(file, MinExclusiveDocument.type, options);
        }
        
        public static MinExclusiveDocument parse(final URL u) throws XmlException, IOException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(u, MinExclusiveDocument.type, null);
        }
        
        public static MinExclusiveDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(u, MinExclusiveDocument.type, options);
        }
        
        public static MinExclusiveDocument parse(final InputStream is) throws XmlException, IOException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(is, MinExclusiveDocument.type, null);
        }
        
        public static MinExclusiveDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(is, MinExclusiveDocument.type, options);
        }
        
        public static MinExclusiveDocument parse(final Reader r) throws XmlException, IOException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(r, MinExclusiveDocument.type, null);
        }
        
        public static MinExclusiveDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(r, MinExclusiveDocument.type, options);
        }
        
        public static MinExclusiveDocument parse(final XMLStreamReader sr) throws XmlException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(sr, MinExclusiveDocument.type, null);
        }
        
        public static MinExclusiveDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(sr, MinExclusiveDocument.type, options);
        }
        
        public static MinExclusiveDocument parse(final Node node) throws XmlException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(node, MinExclusiveDocument.type, null);
        }
        
        public static MinExclusiveDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(node, MinExclusiveDocument.type, options);
        }
        
        @Deprecated
        public static MinExclusiveDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(xis, MinExclusiveDocument.type, null);
        }
        
        @Deprecated
        public static MinExclusiveDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (MinExclusiveDocument)XmlBeans.getContextTypeLoader().parse(xis, MinExclusiveDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MinExclusiveDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MinExclusiveDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
