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
import org.apache.xmlbeans.XmlUnsignedInt;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFills extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFills.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfills2c6ftype");
    
    List<CTFill> getFillList();
    
    @Deprecated
    CTFill[] getFillArray();
    
    CTFill getFillArray(final int p0);
    
    int sizeOfFillArray();
    
    void setFillArray(final CTFill[] p0);
    
    void setFillArray(final int p0, final CTFill p1);
    
    CTFill insertNewFill(final int p0);
    
    CTFill addNewFill();
    
    void removeFill(final int p0);
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFills.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFills newInstance() {
            return (CTFills)getTypeLoader().newInstance(CTFills.type, (XmlOptions)null);
        }
        
        public static CTFills newInstance(final XmlOptions xmlOptions) {
            return (CTFills)getTypeLoader().newInstance(CTFills.type, xmlOptions);
        }
        
        public static CTFills parse(final String s) throws XmlException {
            return (CTFills)getTypeLoader().parse(s, CTFills.type, (XmlOptions)null);
        }
        
        public static CTFills parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFills)getTypeLoader().parse(s, CTFills.type, xmlOptions);
        }
        
        public static CTFills parse(final File file) throws XmlException, IOException {
            return (CTFills)getTypeLoader().parse(file, CTFills.type, (XmlOptions)null);
        }
        
        public static CTFills parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFills)getTypeLoader().parse(file, CTFills.type, xmlOptions);
        }
        
        public static CTFills parse(final URL url) throws XmlException, IOException {
            return (CTFills)getTypeLoader().parse(url, CTFills.type, (XmlOptions)null);
        }
        
        public static CTFills parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFills)getTypeLoader().parse(url, CTFills.type, xmlOptions);
        }
        
        public static CTFills parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFills)getTypeLoader().parse(inputStream, CTFills.type, (XmlOptions)null);
        }
        
        public static CTFills parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFills)getTypeLoader().parse(inputStream, CTFills.type, xmlOptions);
        }
        
        public static CTFills parse(final Reader reader) throws XmlException, IOException {
            return (CTFills)getTypeLoader().parse(reader, CTFills.type, (XmlOptions)null);
        }
        
        public static CTFills parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFills)getTypeLoader().parse(reader, CTFills.type, xmlOptions);
        }
        
        public static CTFills parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFills)getTypeLoader().parse(xmlStreamReader, CTFills.type, (XmlOptions)null);
        }
        
        public static CTFills parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFills)getTypeLoader().parse(xmlStreamReader, CTFills.type, xmlOptions);
        }
        
        public static CTFills parse(final Node node) throws XmlException {
            return (CTFills)getTypeLoader().parse(node, CTFills.type, (XmlOptions)null);
        }
        
        public static CTFills parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFills)getTypeLoader().parse(node, CTFills.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFills parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFills)getTypeLoader().parse(xmlInputStream, CTFills.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFills parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFills)getTypeLoader().parse(xmlInputStream, CTFills.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFills.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFills.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
