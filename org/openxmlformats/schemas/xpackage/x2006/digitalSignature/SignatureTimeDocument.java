package org.openxmlformats.schemas.xpackage.x2006.digitalSignature;

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

public interface SignatureTimeDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SignatureTimeDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("signaturetime9c91doctype");
    
    CTSignatureTime getSignatureTime();
    
    void setSignatureTime(final CTSignatureTime p0);
    
    CTSignatureTime addNewSignatureTime();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SignatureTimeDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SignatureTimeDocument newInstance() {
            return (SignatureTimeDocument)getTypeLoader().newInstance(SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        public static SignatureTimeDocument newInstance(final XmlOptions xmlOptions) {
            return (SignatureTimeDocument)getTypeLoader().newInstance(SignatureTimeDocument.type, xmlOptions);
        }
        
        public static SignatureTimeDocument parse(final String s) throws XmlException {
            return (SignatureTimeDocument)getTypeLoader().parse(s, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        public static SignatureTimeDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureTimeDocument)getTypeLoader().parse(s, SignatureTimeDocument.type, xmlOptions);
        }
        
        public static SignatureTimeDocument parse(final File file) throws XmlException, IOException {
            return (SignatureTimeDocument)getTypeLoader().parse(file, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        public static SignatureTimeDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureTimeDocument)getTypeLoader().parse(file, SignatureTimeDocument.type, xmlOptions);
        }
        
        public static SignatureTimeDocument parse(final URL url) throws XmlException, IOException {
            return (SignatureTimeDocument)getTypeLoader().parse(url, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        public static SignatureTimeDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureTimeDocument)getTypeLoader().parse(url, SignatureTimeDocument.type, xmlOptions);
        }
        
        public static SignatureTimeDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (SignatureTimeDocument)getTypeLoader().parse(inputStream, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        public static SignatureTimeDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureTimeDocument)getTypeLoader().parse(inputStream, SignatureTimeDocument.type, xmlOptions);
        }
        
        public static SignatureTimeDocument parse(final Reader reader) throws XmlException, IOException {
            return (SignatureTimeDocument)getTypeLoader().parse(reader, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        public static SignatureTimeDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SignatureTimeDocument)getTypeLoader().parse(reader, SignatureTimeDocument.type, xmlOptions);
        }
        
        public static SignatureTimeDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SignatureTimeDocument)getTypeLoader().parse(xmlStreamReader, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        public static SignatureTimeDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureTimeDocument)getTypeLoader().parse(xmlStreamReader, SignatureTimeDocument.type, xmlOptions);
        }
        
        public static SignatureTimeDocument parse(final Node node) throws XmlException {
            return (SignatureTimeDocument)getTypeLoader().parse(node, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        public static SignatureTimeDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SignatureTimeDocument)getTypeLoader().parse(node, SignatureTimeDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static SignatureTimeDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SignatureTimeDocument)getTypeLoader().parse(xmlInputStream, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SignatureTimeDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SignatureTimeDocument)getTypeLoader().parse(xmlInputStream, SignatureTimeDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignatureTimeDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SignatureTimeDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
