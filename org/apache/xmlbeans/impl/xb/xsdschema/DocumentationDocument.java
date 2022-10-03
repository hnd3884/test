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
import org.apache.xmlbeans.XmlLanguage;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface DocumentationDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(DocumentationDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("documentation6cdbdoctype");
    
    Documentation getDocumentation();
    
    void setDocumentation(final Documentation p0);
    
    Documentation addNewDocumentation();
    
    public interface Documentation extends XmlObject
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Documentation.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("documentationa475elemtype");
        
        String getSource();
        
        XmlAnyURI xgetSource();
        
        boolean isSetSource();
        
        void setSource(final String p0);
        
        void xsetSource(final XmlAnyURI p0);
        
        void unsetSource();
        
        String getLang();
        
        XmlLanguage xgetLang();
        
        boolean isSetLang();
        
        void setLang(final String p0);
        
        void xsetLang(final XmlLanguage p0);
        
        void unsetLang();
        
        public static final class Factory
        {
            public static Documentation newInstance() {
                return (Documentation)XmlBeans.getContextTypeLoader().newInstance(Documentation.type, null);
            }
            
            public static Documentation newInstance(final XmlOptions options) {
                return (Documentation)XmlBeans.getContextTypeLoader().newInstance(Documentation.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static DocumentationDocument newInstance() {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().newInstance(DocumentationDocument.type, null);
        }
        
        public static DocumentationDocument newInstance(final XmlOptions options) {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().newInstance(DocumentationDocument.type, options);
        }
        
        public static DocumentationDocument parse(final String xmlAsString) throws XmlException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, DocumentationDocument.type, null);
        }
        
        public static DocumentationDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, DocumentationDocument.type, options);
        }
        
        public static DocumentationDocument parse(final File file) throws XmlException, IOException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(file, DocumentationDocument.type, null);
        }
        
        public static DocumentationDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(file, DocumentationDocument.type, options);
        }
        
        public static DocumentationDocument parse(final URL u) throws XmlException, IOException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(u, DocumentationDocument.type, null);
        }
        
        public static DocumentationDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(u, DocumentationDocument.type, options);
        }
        
        public static DocumentationDocument parse(final InputStream is) throws XmlException, IOException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(is, DocumentationDocument.type, null);
        }
        
        public static DocumentationDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(is, DocumentationDocument.type, options);
        }
        
        public static DocumentationDocument parse(final Reader r) throws XmlException, IOException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(r, DocumentationDocument.type, null);
        }
        
        public static DocumentationDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(r, DocumentationDocument.type, options);
        }
        
        public static DocumentationDocument parse(final XMLStreamReader sr) throws XmlException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(sr, DocumentationDocument.type, null);
        }
        
        public static DocumentationDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(sr, DocumentationDocument.type, options);
        }
        
        public static DocumentationDocument parse(final Node node) throws XmlException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(node, DocumentationDocument.type, null);
        }
        
        public static DocumentationDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(node, DocumentationDocument.type, options);
        }
        
        @Deprecated
        public static DocumentationDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(xis, DocumentationDocument.type, null);
        }
        
        @Deprecated
        public static DocumentationDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (DocumentationDocument)XmlBeans.getContextTypeLoader().parse(xis, DocumentationDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DocumentationDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, DocumentationDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
