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

public interface MaxInclusiveDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MaxInclusiveDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("maxinclusive93dbdoctype");
    
    Facet getMaxInclusive();
    
    void setMaxInclusive(final Facet p0);
    
    Facet addNewMaxInclusive();
    
    public static final class Factory
    {
        public static MaxInclusiveDocument newInstance() {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().newInstance(MaxInclusiveDocument.type, null);
        }
        
        public static MaxInclusiveDocument newInstance(final XmlOptions options) {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().newInstance(MaxInclusiveDocument.type, options);
        }
        
        public static MaxInclusiveDocument parse(final String xmlAsString) throws XmlException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MaxInclusiveDocument.type, null);
        }
        
        public static MaxInclusiveDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, MaxInclusiveDocument.type, options);
        }
        
        public static MaxInclusiveDocument parse(final File file) throws XmlException, IOException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(file, MaxInclusiveDocument.type, null);
        }
        
        public static MaxInclusiveDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(file, MaxInclusiveDocument.type, options);
        }
        
        public static MaxInclusiveDocument parse(final URL u) throws XmlException, IOException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(u, MaxInclusiveDocument.type, null);
        }
        
        public static MaxInclusiveDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(u, MaxInclusiveDocument.type, options);
        }
        
        public static MaxInclusiveDocument parse(final InputStream is) throws XmlException, IOException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(is, MaxInclusiveDocument.type, null);
        }
        
        public static MaxInclusiveDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(is, MaxInclusiveDocument.type, options);
        }
        
        public static MaxInclusiveDocument parse(final Reader r) throws XmlException, IOException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(r, MaxInclusiveDocument.type, null);
        }
        
        public static MaxInclusiveDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(r, MaxInclusiveDocument.type, options);
        }
        
        public static MaxInclusiveDocument parse(final XMLStreamReader sr) throws XmlException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(sr, MaxInclusiveDocument.type, null);
        }
        
        public static MaxInclusiveDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(sr, MaxInclusiveDocument.type, options);
        }
        
        public static MaxInclusiveDocument parse(final Node node) throws XmlException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(node, MaxInclusiveDocument.type, null);
        }
        
        public static MaxInclusiveDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(node, MaxInclusiveDocument.type, options);
        }
        
        @Deprecated
        public static MaxInclusiveDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(xis, MaxInclusiveDocument.type, null);
        }
        
        @Deprecated
        public static MaxInclusiveDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (MaxInclusiveDocument)XmlBeans.getContextTypeLoader().parse(xis, MaxInclusiveDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MaxInclusiveDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, MaxInclusiveDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
