package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBreak extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBreak.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbreak815etype");
    
    long getId();
    
    XmlUnsignedInt xgetId();
    
    boolean isSetId();
    
    void setId(final long p0);
    
    void xsetId(final XmlUnsignedInt p0);
    
    void unsetId();
    
    long getMin();
    
    XmlUnsignedInt xgetMin();
    
    boolean isSetMin();
    
    void setMin(final long p0);
    
    void xsetMin(final XmlUnsignedInt p0);
    
    void unsetMin();
    
    long getMax();
    
    XmlUnsignedInt xgetMax();
    
    boolean isSetMax();
    
    void setMax(final long p0);
    
    void xsetMax(final XmlUnsignedInt p0);
    
    void unsetMax();
    
    boolean getMan();
    
    XmlBoolean xgetMan();
    
    boolean isSetMan();
    
    void setMan(final boolean p0);
    
    void xsetMan(final XmlBoolean p0);
    
    void unsetMan();
    
    boolean getPt();
    
    XmlBoolean xgetPt();
    
    boolean isSetPt();
    
    void setPt(final boolean p0);
    
    void xsetPt(final XmlBoolean p0);
    
    void unsetPt();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBreak.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBreak newInstance() {
            return (CTBreak)getTypeLoader().newInstance(CTBreak.type, (XmlOptions)null);
        }
        
        public static CTBreak newInstance(final XmlOptions xmlOptions) {
            return (CTBreak)getTypeLoader().newInstance(CTBreak.type, xmlOptions);
        }
        
        public static CTBreak parse(final String s) throws XmlException {
            return (CTBreak)getTypeLoader().parse(s, CTBreak.type, (XmlOptions)null);
        }
        
        public static CTBreak parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBreak)getTypeLoader().parse(s, CTBreak.type, xmlOptions);
        }
        
        public static CTBreak parse(final File file) throws XmlException, IOException {
            return (CTBreak)getTypeLoader().parse(file, CTBreak.type, (XmlOptions)null);
        }
        
        public static CTBreak parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBreak)getTypeLoader().parse(file, CTBreak.type, xmlOptions);
        }
        
        public static CTBreak parse(final URL url) throws XmlException, IOException {
            return (CTBreak)getTypeLoader().parse(url, CTBreak.type, (XmlOptions)null);
        }
        
        public static CTBreak parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBreak)getTypeLoader().parse(url, CTBreak.type, xmlOptions);
        }
        
        public static CTBreak parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBreak)getTypeLoader().parse(inputStream, CTBreak.type, (XmlOptions)null);
        }
        
        public static CTBreak parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBreak)getTypeLoader().parse(inputStream, CTBreak.type, xmlOptions);
        }
        
        public static CTBreak parse(final Reader reader) throws XmlException, IOException {
            return (CTBreak)getTypeLoader().parse(reader, CTBreak.type, (XmlOptions)null);
        }
        
        public static CTBreak parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBreak)getTypeLoader().parse(reader, CTBreak.type, xmlOptions);
        }
        
        public static CTBreak parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBreak)getTypeLoader().parse(xmlStreamReader, CTBreak.type, (XmlOptions)null);
        }
        
        public static CTBreak parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBreak)getTypeLoader().parse(xmlStreamReader, CTBreak.type, xmlOptions);
        }
        
        public static CTBreak parse(final Node node) throws XmlException {
            return (CTBreak)getTypeLoader().parse(node, CTBreak.type, (XmlOptions)null);
        }
        
        public static CTBreak parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBreak)getTypeLoader().parse(node, CTBreak.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBreak parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBreak)getTypeLoader().parse(xmlInputStream, CTBreak.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBreak parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBreak)getTypeLoader().parse(xmlInputStream, CTBreak.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBreak.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBreak.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
