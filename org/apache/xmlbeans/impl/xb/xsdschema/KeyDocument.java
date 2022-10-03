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

public interface KeyDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(KeyDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("key5d16doctype");
    
    Keybase getKey();
    
    void setKey(final Keybase p0);
    
    Keybase addNewKey();
    
    public static final class Factory
    {
        public static KeyDocument newInstance() {
            return (KeyDocument)XmlBeans.getContextTypeLoader().newInstance(KeyDocument.type, null);
        }
        
        public static KeyDocument newInstance(final XmlOptions options) {
            return (KeyDocument)XmlBeans.getContextTypeLoader().newInstance(KeyDocument.type, options);
        }
        
        public static KeyDocument parse(final String xmlAsString) throws XmlException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, KeyDocument.type, null);
        }
        
        public static KeyDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, KeyDocument.type, options);
        }
        
        public static KeyDocument parse(final File file) throws XmlException, IOException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(file, KeyDocument.type, null);
        }
        
        public static KeyDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(file, KeyDocument.type, options);
        }
        
        public static KeyDocument parse(final URL u) throws XmlException, IOException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(u, KeyDocument.type, null);
        }
        
        public static KeyDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(u, KeyDocument.type, options);
        }
        
        public static KeyDocument parse(final InputStream is) throws XmlException, IOException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(is, KeyDocument.type, null);
        }
        
        public static KeyDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(is, KeyDocument.type, options);
        }
        
        public static KeyDocument parse(final Reader r) throws XmlException, IOException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(r, KeyDocument.type, null);
        }
        
        public static KeyDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(r, KeyDocument.type, options);
        }
        
        public static KeyDocument parse(final XMLStreamReader sr) throws XmlException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(sr, KeyDocument.type, null);
        }
        
        public static KeyDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(sr, KeyDocument.type, options);
        }
        
        public static KeyDocument parse(final Node node) throws XmlException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(node, KeyDocument.type, null);
        }
        
        public static KeyDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(node, KeyDocument.type, options);
        }
        
        @Deprecated
        public static KeyDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(xis, KeyDocument.type, null);
        }
        
        @Deprecated
        public static KeyDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (KeyDocument)XmlBeans.getContextTypeLoader().parse(xis, KeyDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, KeyDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, KeyDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
