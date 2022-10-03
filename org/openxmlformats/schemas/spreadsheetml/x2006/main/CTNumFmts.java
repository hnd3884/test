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

public interface CTNumFmts extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumFmts.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumfmtsb58btype");
    
    List<CTNumFmt> getNumFmtList();
    
    @Deprecated
    CTNumFmt[] getNumFmtArray();
    
    CTNumFmt getNumFmtArray(final int p0);
    
    int sizeOfNumFmtArray();
    
    void setNumFmtArray(final CTNumFmt[] p0);
    
    void setNumFmtArray(final int p0, final CTNumFmt p1);
    
    CTNumFmt insertNewNumFmt(final int p0);
    
    CTNumFmt addNewNumFmt();
    
    void removeNumFmt(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumFmts.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumFmts newInstance() {
            return (CTNumFmts)getTypeLoader().newInstance(CTNumFmts.type, (XmlOptions)null);
        }
        
        public static CTNumFmts newInstance(final XmlOptions xmlOptions) {
            return (CTNumFmts)getTypeLoader().newInstance(CTNumFmts.type, xmlOptions);
        }
        
        public static CTNumFmts parse(final String s) throws XmlException {
            return (CTNumFmts)getTypeLoader().parse(s, CTNumFmts.type, (XmlOptions)null);
        }
        
        public static CTNumFmts parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumFmts)getTypeLoader().parse(s, CTNumFmts.type, xmlOptions);
        }
        
        public static CTNumFmts parse(final File file) throws XmlException, IOException {
            return (CTNumFmts)getTypeLoader().parse(file, CTNumFmts.type, (XmlOptions)null);
        }
        
        public static CTNumFmts parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumFmts)getTypeLoader().parse(file, CTNumFmts.type, xmlOptions);
        }
        
        public static CTNumFmts parse(final URL url) throws XmlException, IOException {
            return (CTNumFmts)getTypeLoader().parse(url, CTNumFmts.type, (XmlOptions)null);
        }
        
        public static CTNumFmts parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumFmts)getTypeLoader().parse(url, CTNumFmts.type, xmlOptions);
        }
        
        public static CTNumFmts parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumFmts)getTypeLoader().parse(inputStream, CTNumFmts.type, (XmlOptions)null);
        }
        
        public static CTNumFmts parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumFmts)getTypeLoader().parse(inputStream, CTNumFmts.type, xmlOptions);
        }
        
        public static CTNumFmts parse(final Reader reader) throws XmlException, IOException {
            return (CTNumFmts)getTypeLoader().parse(reader, CTNumFmts.type, (XmlOptions)null);
        }
        
        public static CTNumFmts parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumFmts)getTypeLoader().parse(reader, CTNumFmts.type, xmlOptions);
        }
        
        public static CTNumFmts parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumFmts)getTypeLoader().parse(xmlStreamReader, CTNumFmts.type, (XmlOptions)null);
        }
        
        public static CTNumFmts parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumFmts)getTypeLoader().parse(xmlStreamReader, CTNumFmts.type, xmlOptions);
        }
        
        public static CTNumFmts parse(final Node node) throws XmlException {
            return (CTNumFmts)getTypeLoader().parse(node, CTNumFmts.type, (XmlOptions)null);
        }
        
        public static CTNumFmts parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumFmts)getTypeLoader().parse(node, CTNumFmts.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumFmts parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumFmts)getTypeLoader().parse(xmlInputStream, CTNumFmts.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumFmts parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumFmts)getTypeLoader().parse(xmlInputStream, CTNumFmts.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumFmts.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumFmts.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
