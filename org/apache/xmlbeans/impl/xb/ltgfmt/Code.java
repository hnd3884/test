package org.apache.xmlbeans.impl.xb.ltgfmt;

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
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface Code extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Code.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLTOOLS").resolveHandle("codef72ftype");
    
    String getID();
    
    XmlToken xgetID();
    
    boolean isSetID();
    
    void setID(final String p0);
    
    void xsetID(final XmlToken p0);
    
    void unsetID();
    
    public static final class Factory
    {
        public static Code newInstance() {
            return (Code)XmlBeans.getContextTypeLoader().newInstance(Code.type, null);
        }
        
        public static Code newInstance(final XmlOptions options) {
            return (Code)XmlBeans.getContextTypeLoader().newInstance(Code.type, options);
        }
        
        public static Code parse(final String xmlAsString) throws XmlException {
            return (Code)XmlBeans.getContextTypeLoader().parse(xmlAsString, Code.type, null);
        }
        
        public static Code parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Code)XmlBeans.getContextTypeLoader().parse(xmlAsString, Code.type, options);
        }
        
        public static Code parse(final File file) throws XmlException, IOException {
            return (Code)XmlBeans.getContextTypeLoader().parse(file, Code.type, null);
        }
        
        public static Code parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Code)XmlBeans.getContextTypeLoader().parse(file, Code.type, options);
        }
        
        public static Code parse(final URL u) throws XmlException, IOException {
            return (Code)XmlBeans.getContextTypeLoader().parse(u, Code.type, null);
        }
        
        public static Code parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Code)XmlBeans.getContextTypeLoader().parse(u, Code.type, options);
        }
        
        public static Code parse(final InputStream is) throws XmlException, IOException {
            return (Code)XmlBeans.getContextTypeLoader().parse(is, Code.type, null);
        }
        
        public static Code parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Code)XmlBeans.getContextTypeLoader().parse(is, Code.type, options);
        }
        
        public static Code parse(final Reader r) throws XmlException, IOException {
            return (Code)XmlBeans.getContextTypeLoader().parse(r, Code.type, null);
        }
        
        public static Code parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Code)XmlBeans.getContextTypeLoader().parse(r, Code.type, options);
        }
        
        public static Code parse(final XMLStreamReader sr) throws XmlException {
            return (Code)XmlBeans.getContextTypeLoader().parse(sr, Code.type, null);
        }
        
        public static Code parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Code)XmlBeans.getContextTypeLoader().parse(sr, Code.type, options);
        }
        
        public static Code parse(final Node node) throws XmlException {
            return (Code)XmlBeans.getContextTypeLoader().parse(node, Code.type, null);
        }
        
        public static Code parse(final Node node, final XmlOptions options) throws XmlException {
            return (Code)XmlBeans.getContextTypeLoader().parse(node, Code.type, options);
        }
        
        @Deprecated
        public static Code parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Code)XmlBeans.getContextTypeLoader().parse(xis, Code.type, null);
        }
        
        @Deprecated
        public static Code parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Code)XmlBeans.getContextTypeLoader().parse(xis, Code.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Code.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Code.type, options);
        }
        
        private Factory() {
        }
    }
}
