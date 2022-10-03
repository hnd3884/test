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

public interface CTSignatureTime extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSignatureTime.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctsignaturetime461dtype");
    
    String getFormat();
    
    STFormat xgetFormat();
    
    void setFormat(final String p0);
    
    void xsetFormat(final STFormat p0);
    
    String getValue();
    
    STValue xgetValue();
    
    void setValue(final String p0);
    
    void xsetValue(final STValue p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSignatureTime.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSignatureTime newInstance() {
            return (CTSignatureTime)getTypeLoader().newInstance(CTSignatureTime.type, (XmlOptions)null);
        }
        
        public static CTSignatureTime newInstance(final XmlOptions xmlOptions) {
            return (CTSignatureTime)getTypeLoader().newInstance(CTSignatureTime.type, xmlOptions);
        }
        
        public static CTSignatureTime parse(final String s) throws XmlException {
            return (CTSignatureTime)getTypeLoader().parse(s, CTSignatureTime.type, (XmlOptions)null);
        }
        
        public static CTSignatureTime parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignatureTime)getTypeLoader().parse(s, CTSignatureTime.type, xmlOptions);
        }
        
        public static CTSignatureTime parse(final File file) throws XmlException, IOException {
            return (CTSignatureTime)getTypeLoader().parse(file, CTSignatureTime.type, (XmlOptions)null);
        }
        
        public static CTSignatureTime parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignatureTime)getTypeLoader().parse(file, CTSignatureTime.type, xmlOptions);
        }
        
        public static CTSignatureTime parse(final URL url) throws XmlException, IOException {
            return (CTSignatureTime)getTypeLoader().parse(url, CTSignatureTime.type, (XmlOptions)null);
        }
        
        public static CTSignatureTime parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignatureTime)getTypeLoader().parse(url, CTSignatureTime.type, xmlOptions);
        }
        
        public static CTSignatureTime parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSignatureTime)getTypeLoader().parse(inputStream, CTSignatureTime.type, (XmlOptions)null);
        }
        
        public static CTSignatureTime parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignatureTime)getTypeLoader().parse(inputStream, CTSignatureTime.type, xmlOptions);
        }
        
        public static CTSignatureTime parse(final Reader reader) throws XmlException, IOException {
            return (CTSignatureTime)getTypeLoader().parse(reader, CTSignatureTime.type, (XmlOptions)null);
        }
        
        public static CTSignatureTime parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSignatureTime)getTypeLoader().parse(reader, CTSignatureTime.type, xmlOptions);
        }
        
        public static CTSignatureTime parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSignatureTime)getTypeLoader().parse(xmlStreamReader, CTSignatureTime.type, (XmlOptions)null);
        }
        
        public static CTSignatureTime parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignatureTime)getTypeLoader().parse(xmlStreamReader, CTSignatureTime.type, xmlOptions);
        }
        
        public static CTSignatureTime parse(final Node node) throws XmlException {
            return (CTSignatureTime)getTypeLoader().parse(node, CTSignatureTime.type, (XmlOptions)null);
        }
        
        public static CTSignatureTime parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSignatureTime)getTypeLoader().parse(node, CTSignatureTime.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSignatureTime parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSignatureTime)getTypeLoader().parse(xmlInputStream, CTSignatureTime.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSignatureTime parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSignatureTime)getTypeLoader().parse(xmlInputStream, CTSignatureTime.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSignatureTime.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSignatureTime.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
