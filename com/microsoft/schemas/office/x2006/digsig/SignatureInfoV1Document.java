package com.microsoft.schemas.office.x2006.digsig;

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

public interface SignatureInfoV1Document extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignatureInfoV1Document.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signatureinfov14a6bdoctype");
    
    CTSignatureInfoV1 getSignatureInfoV1();
    
    void setSignatureInfoV1(final CTSignatureInfoV1 p0);
    
    CTSignatureInfoV1 addNewSignatureInfoV1();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignatureInfoV1Document.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignatureInfoV1Document newInstance() {
            return (SignatureInfoV1Document)getTypeLoader().newInstance(SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        public static SignatureInfoV1Document newInstance(final XmlOptions xmlOptions) {
            return (SignatureInfoV1Document)getTypeLoader().newInstance(SignatureInfoV1Document.type, xmlOptions);
        }
        
        public static SignatureInfoV1Document parse(final String s) throws XmlException {
            return (SignatureInfoV1Document)getTypeLoader().parse(s, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        public static SignatureInfoV1Document parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureInfoV1Document)getTypeLoader().parse(s, SignatureInfoV1Document.type, xmlOptions);
        }
        
        public static SignatureInfoV1Document parse(final File file) throws XmlException, IOException {
            return (SignatureInfoV1Document)getTypeLoader().parse(file, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        public static SignatureInfoV1Document parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureInfoV1Document)getTypeLoader().parse(file, SignatureInfoV1Document.type, xmlOptions);
        }
        
        public static SignatureInfoV1Document parse(final URL url) throws XmlException, IOException {
            return (SignatureInfoV1Document)getTypeLoader().parse(url, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        public static SignatureInfoV1Document parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureInfoV1Document)getTypeLoader().parse(url, SignatureInfoV1Document.type, xmlOptions);
        }
        
        public static SignatureInfoV1Document parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignatureInfoV1Document)getTypeLoader().parse(inputStream, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        public static SignatureInfoV1Document parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureInfoV1Document)getTypeLoader().parse(inputStream, SignatureInfoV1Document.type, xmlOptions);
        }
        
        public static SignatureInfoV1Document parse(final Reader reader) throws XmlException, IOException {
            return (SignatureInfoV1Document)getTypeLoader().parse(reader, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        public static SignatureInfoV1Document parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureInfoV1Document)getTypeLoader().parse(reader, SignatureInfoV1Document.type, xmlOptions);
        }
        
        public static SignatureInfoV1Document parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignatureInfoV1Document)getTypeLoader().parse(xmlStreamReader, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        public static SignatureInfoV1Document parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureInfoV1Document)getTypeLoader().parse(xmlStreamReader, SignatureInfoV1Document.type, xmlOptions);
        }
        
        public static SignatureInfoV1Document parse(final Node node) throws XmlException {
            return (SignatureInfoV1Document)getTypeLoader().parse(node, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        public static SignatureInfoV1Document parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureInfoV1Document)getTypeLoader().parse(node, SignatureInfoV1Document.type, xmlOptions);
        }
        
        @Deprecated
        public static SignatureInfoV1Document parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignatureInfoV1Document)getTypeLoader().parse(xmlInputStream, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignatureInfoV1Document parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignatureInfoV1Document)getTypeLoader().parse(xmlInputStream, SignatureInfoV1Document.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignatureInfoV1Document.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignatureInfoV1Document.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
