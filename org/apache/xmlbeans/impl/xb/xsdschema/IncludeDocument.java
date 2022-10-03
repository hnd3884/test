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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface IncludeDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(IncludeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("includeaf6ddoctype");
    
    Include getInclude();
    
    void setInclude(final Include p0);
    
    Include addNewInclude();
    
    public interface Include extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Include.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("include59d9elemtype");
        
        String getSchemaLocation();
        
        XmlAnyURI xgetSchemaLocation();
        
        void setSchemaLocation(final String p0);
        
        void xsetSchemaLocation(final XmlAnyURI p0);
        
        public static final class Factory
        {
            public static Include newInstance() {
                return (Include)XmlBeans.getContextTypeLoader().newInstance(Include.type, null);
            }
            
            public static Include newInstance(final XmlOptions options) {
                return (Include)XmlBeans.getContextTypeLoader().newInstance(Include.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static IncludeDocument newInstance() {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().newInstance(IncludeDocument.type, null);
        }
        
        public static IncludeDocument newInstance(final XmlOptions options) {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().newInstance(IncludeDocument.type, options);
        }
        
        public static IncludeDocument parse(final String xmlAsString) throws XmlException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, IncludeDocument.type, null);
        }
        
        public static IncludeDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, IncludeDocument.type, options);
        }
        
        public static IncludeDocument parse(final File file) throws XmlException, IOException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(file, IncludeDocument.type, null);
        }
        
        public static IncludeDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(file, IncludeDocument.type, options);
        }
        
        public static IncludeDocument parse(final URL u) throws XmlException, IOException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(u, IncludeDocument.type, null);
        }
        
        public static IncludeDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(u, IncludeDocument.type, options);
        }
        
        public static IncludeDocument parse(final InputStream is) throws XmlException, IOException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(is, IncludeDocument.type, null);
        }
        
        public static IncludeDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(is, IncludeDocument.type, options);
        }
        
        public static IncludeDocument parse(final Reader r) throws XmlException, IOException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(r, IncludeDocument.type, null);
        }
        
        public static IncludeDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(r, IncludeDocument.type, options);
        }
        
        public static IncludeDocument parse(final XMLStreamReader sr) throws XmlException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(sr, IncludeDocument.type, null);
        }
        
        public static IncludeDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(sr, IncludeDocument.type, options);
        }
        
        public static IncludeDocument parse(final Node node) throws XmlException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(node, IncludeDocument.type, null);
        }
        
        public static IncludeDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(node, IncludeDocument.type, options);
        }
        
        @Deprecated
        public static IncludeDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(xis, IncludeDocument.type, null);
        }
        
        @Deprecated
        public static IncludeDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (IncludeDocument)XmlBeans.getContextTypeLoader().parse(xis, IncludeDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, IncludeDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, IncludeDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
