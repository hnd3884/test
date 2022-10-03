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

public interface UniqueDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(UniqueDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("unique3752doctype");
    
    Keybase getUnique();
    
    void setUnique(final Keybase p0);
    
    Keybase addNewUnique();
    
    public static final class Factory
    {
        public static UniqueDocument newInstance() {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().newInstance(UniqueDocument.type, null);
        }
        
        public static UniqueDocument newInstance(final XmlOptions options) {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().newInstance(UniqueDocument.type, options);
        }
        
        public static UniqueDocument parse(final String xmlAsString) throws XmlException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, UniqueDocument.type, null);
        }
        
        public static UniqueDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, UniqueDocument.type, options);
        }
        
        public static UniqueDocument parse(final File file) throws XmlException, IOException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(file, UniqueDocument.type, null);
        }
        
        public static UniqueDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(file, UniqueDocument.type, options);
        }
        
        public static UniqueDocument parse(final URL u) throws XmlException, IOException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(u, UniqueDocument.type, null);
        }
        
        public static UniqueDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(u, UniqueDocument.type, options);
        }
        
        public static UniqueDocument parse(final InputStream is) throws XmlException, IOException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(is, UniqueDocument.type, null);
        }
        
        public static UniqueDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(is, UniqueDocument.type, options);
        }
        
        public static UniqueDocument parse(final Reader r) throws XmlException, IOException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(r, UniqueDocument.type, null);
        }
        
        public static UniqueDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(r, UniqueDocument.type, options);
        }
        
        public static UniqueDocument parse(final XMLStreamReader sr) throws XmlException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(sr, UniqueDocument.type, null);
        }
        
        public static UniqueDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(sr, UniqueDocument.type, options);
        }
        
        public static UniqueDocument parse(final Node node) throws XmlException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(node, UniqueDocument.type, null);
        }
        
        public static UniqueDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(node, UniqueDocument.type, options);
        }
        
        @Deprecated
        public static UniqueDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(xis, UniqueDocument.type, null);
        }
        
        @Deprecated
        public static UniqueDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (UniqueDocument)XmlBeans.getContextTypeLoader().parse(xis, UniqueDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, UniqueDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, UniqueDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
