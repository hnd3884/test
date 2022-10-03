package org.w3.x2000.x09.xmldsig;

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
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface SignatureDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignatureDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signature5269doctype");
    
    SignatureType getSignature();
    
    void setSignature(final SignatureType p0);
    
    SignatureType addNewSignature();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignatureDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignatureDocument newInstance() {
            return (SignatureDocument)getTypeLoader().newInstance(SignatureDocument.type, (XmlOptions)null);
        }
        
        public static SignatureDocument newInstance(final XmlOptions xmlOptions) {
            return (SignatureDocument)getTypeLoader().newInstance(SignatureDocument.type, xmlOptions);
        }
        
        public static SignatureDocument parse(final String s) throws XmlException {
            return (SignatureDocument)getTypeLoader().parse(s, SignatureDocument.type, (XmlOptions)null);
        }
        
        public static SignatureDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureDocument)getTypeLoader().parse(s, SignatureDocument.type, xmlOptions);
        }
        
        public static SignatureDocument parse(final File file) throws XmlException, IOException {
            return (SignatureDocument)getTypeLoader().parse(file, SignatureDocument.type, (XmlOptions)null);
        }
        
        public static SignatureDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureDocument)getTypeLoader().parse(file, SignatureDocument.type, xmlOptions);
        }
        
        public static SignatureDocument parse(final URL url) throws XmlException, IOException {
            return (SignatureDocument)getTypeLoader().parse(url, SignatureDocument.type, (XmlOptions)null);
        }
        
        public static SignatureDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureDocument)getTypeLoader().parse(url, SignatureDocument.type, xmlOptions);
        }
        
        public static SignatureDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignatureDocument)getTypeLoader().parse(inputStream, SignatureDocument.type, (XmlOptions)null);
        }
        
        public static SignatureDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureDocument)getTypeLoader().parse(inputStream, SignatureDocument.type, xmlOptions);
        }
        
        public static SignatureDocument parse(final Reader reader) throws XmlException, IOException {
            return (SignatureDocument)getTypeLoader().parse(reader, SignatureDocument.type, (XmlOptions)null);
        }
        
        public static SignatureDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureDocument)getTypeLoader().parse(reader, SignatureDocument.type, xmlOptions);
        }
        
        public static SignatureDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignatureDocument)getTypeLoader().parse(xmlStreamReader, SignatureDocument.type, (XmlOptions)null);
        }
        
        public static SignatureDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureDocument)getTypeLoader().parse(xmlStreamReader, SignatureDocument.type, xmlOptions);
        }
        
        public static SignatureDocument parse(final Node node) throws XmlException {
            return (SignatureDocument)getTypeLoader().parse(node, SignatureDocument.type, (XmlOptions)null);
        }
        
        public static SignatureDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureDocument)getTypeLoader().parse(node, SignatureDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static SignatureDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignatureDocument)getTypeLoader().parse(xmlInputStream, SignatureDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignatureDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignatureDocument)getTypeLoader().parse(xmlInputStream, SignatureDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignatureDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignatureDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
