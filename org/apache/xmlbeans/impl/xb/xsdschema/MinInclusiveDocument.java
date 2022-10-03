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

public interface MinInclusiveDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MinInclusiveDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("mininclusive8b49doctype");
    
    Facet getMinInclusive();
    
    void setMinInclusive(final Facet p0);
    
    Facet addNewMinInclusive();
    
    public static final class Factory
    {
        public static MinInclusiveDocument newInstance() {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().newInstance(MinInclusiveDocument.type, null);
        }
        
        public static MinInclusiveDocument newInstance(final XmlOptions options) {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().newInstance(MinInclusiveDocument.type, options);
        }
        
        public static MinInclusiveDocument parse(final String xmlAsString) throws XmlException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MinInclusiveDocument.type, null);
        }
        
        public static MinInclusiveDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MinInclusiveDocument.type, options);
        }
        
        public static MinInclusiveDocument parse(final File file) throws XmlException, IOException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(file, MinInclusiveDocument.type, null);
        }
        
        public static MinInclusiveDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(file, MinInclusiveDocument.type, options);
        }
        
        public static MinInclusiveDocument parse(final URL u) throws XmlException, IOException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(u, MinInclusiveDocument.type, null);
        }
        
        public static MinInclusiveDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(u, MinInclusiveDocument.type, options);
        }
        
        public static MinInclusiveDocument parse(final InputStream is) throws XmlException, IOException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(is, MinInclusiveDocument.type, null);
        }
        
        public static MinInclusiveDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(is, MinInclusiveDocument.type, options);
        }
        
        public static MinInclusiveDocument parse(final Reader r) throws XmlException, IOException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(r, MinInclusiveDocument.type, null);
        }
        
        public static MinInclusiveDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(r, MinInclusiveDocument.type, options);
        }
        
        public static MinInclusiveDocument parse(final XMLStreamReader sr) throws XmlException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(sr, MinInclusiveDocument.type, null);
        }
        
        public static MinInclusiveDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(sr, MinInclusiveDocument.type, options);
        }
        
        public static MinInclusiveDocument parse(final Node node) throws XmlException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(node, MinInclusiveDocument.type, null);
        }
        
        public static MinInclusiveDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(node, MinInclusiveDocument.type, options);
        }
        
        @Deprecated
        public static MinInclusiveDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(xis, MinInclusiveDocument.type, null);
        }
        
        @Deprecated
        public static MinInclusiveDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (MinInclusiveDocument)XmlBeans.getContextTypeLoader().parse(xis, MinInclusiveDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MinInclusiveDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MinInclusiveDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
