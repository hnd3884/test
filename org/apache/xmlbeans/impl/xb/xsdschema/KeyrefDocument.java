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
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface KeyrefDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(KeyrefDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("keyref45afdoctype");
    
    Keyref getKeyref();
    
    void setKeyref(final Keyref p0);
    
    Keyref addNewKeyref();
    
    public interface Keyref extends Keybase
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Keyref.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("keyref7a1felemtype");
        
        QName getRefer();
        
        XmlQName xgetRefer();
        
        void setRefer(final QName p0);
        
        void xsetRefer(final XmlQName p0);
        
        public static final class Factory
        {
            public static Keyref newInstance() {
                return (Keyref)XmlBeans.getContextTypeLoader().newInstance(Keyref.type, null);
            }
            
            public static Keyref newInstance(final XmlOptions options) {
                return (Keyref)XmlBeans.getContextTypeLoader().newInstance(Keyref.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static KeyrefDocument newInstance() {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().newInstance(KeyrefDocument.type, null);
        }
        
        public static KeyrefDocument newInstance(final XmlOptions options) {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().newInstance(KeyrefDocument.type, options);
        }
        
        public static KeyrefDocument parse(final String xmlAsString) throws XmlException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, KeyrefDocument.type, null);
        }
        
        public static KeyrefDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, KeyrefDocument.type, options);
        }
        
        public static KeyrefDocument parse(final File file) throws XmlException, IOException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(file, KeyrefDocument.type, null);
        }
        
        public static KeyrefDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(file, KeyrefDocument.type, options);
        }
        
        public static KeyrefDocument parse(final URL u) throws XmlException, IOException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(u, KeyrefDocument.type, null);
        }
        
        public static KeyrefDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(u, KeyrefDocument.type, options);
        }
        
        public static KeyrefDocument parse(final InputStream is) throws XmlException, IOException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(is, KeyrefDocument.type, null);
        }
        
        public static KeyrefDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(is, KeyrefDocument.type, options);
        }
        
        public static KeyrefDocument parse(final Reader r) throws XmlException, IOException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(r, KeyrefDocument.type, null);
        }
        
        public static KeyrefDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(r, KeyrefDocument.type, options);
        }
        
        public static KeyrefDocument parse(final XMLStreamReader sr) throws XmlException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(sr, KeyrefDocument.type, null);
        }
        
        public static KeyrefDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(sr, KeyrefDocument.type, options);
        }
        
        public static KeyrefDocument parse(final Node node) throws XmlException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(node, KeyrefDocument.type, null);
        }
        
        public static KeyrefDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(node, KeyrefDocument.type, options);
        }
        
        @Deprecated
        public static KeyrefDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(xis, KeyrefDocument.type, null);
        }
        
        @Deprecated
        public static KeyrefDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (KeyrefDocument)XmlBeans.getContextTypeLoader().parse(xis, KeyrefDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, KeyrefDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, KeyrefDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
