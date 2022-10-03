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

public interface CTPageBreak extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPageBreak.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpagebreakeb4ftype");
    
    List<CTBreak> getBrkList();
    
    @Deprecated
    CTBreak[] getBrkArray();
    
    CTBreak getBrkArray(final int p0);
    
    int sizeOfBrkArray();
    
    void setBrkArray(final CTBreak[] p0);
    
    void setBrkArray(final int p0, final CTBreak p1);
    
    CTBreak insertNewBrk(final int p0);
    
    CTBreak addNewBrk();
    
    void removeBrk(final int p0);
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    long getManualBreakCount();
    
    XmlUnsignedInt xgetManualBreakCount();
    
    boolean isSetManualBreakCount();
    
    void setManualBreakCount(final long p0);
    
    void xsetManualBreakCount(final XmlUnsignedInt p0);
    
    void unsetManualBreakCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPageBreak.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPageBreak newInstance() {
            return (CTPageBreak)getTypeLoader().newInstance(CTPageBreak.type, (XmlOptions)null);
        }
        
        public static CTPageBreak newInstance(final XmlOptions xmlOptions) {
            return (CTPageBreak)getTypeLoader().newInstance(CTPageBreak.type, xmlOptions);
        }
        
        public static CTPageBreak parse(final String s) throws XmlException {
            return (CTPageBreak)getTypeLoader().parse(s, CTPageBreak.type, (XmlOptions)null);
        }
        
        public static CTPageBreak parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageBreak)getTypeLoader().parse(s, CTPageBreak.type, xmlOptions);
        }
        
        public static CTPageBreak parse(final File file) throws XmlException, IOException {
            return (CTPageBreak)getTypeLoader().parse(file, CTPageBreak.type, (XmlOptions)null);
        }
        
        public static CTPageBreak parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageBreak)getTypeLoader().parse(file, CTPageBreak.type, xmlOptions);
        }
        
        public static CTPageBreak parse(final URL url) throws XmlException, IOException {
            return (CTPageBreak)getTypeLoader().parse(url, CTPageBreak.type, (XmlOptions)null);
        }
        
        public static CTPageBreak parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageBreak)getTypeLoader().parse(url, CTPageBreak.type, xmlOptions);
        }
        
        public static CTPageBreak parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPageBreak)getTypeLoader().parse(inputStream, CTPageBreak.type, (XmlOptions)null);
        }
        
        public static CTPageBreak parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageBreak)getTypeLoader().parse(inputStream, CTPageBreak.type, xmlOptions);
        }
        
        public static CTPageBreak parse(final Reader reader) throws XmlException, IOException {
            return (CTPageBreak)getTypeLoader().parse(reader, CTPageBreak.type, (XmlOptions)null);
        }
        
        public static CTPageBreak parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageBreak)getTypeLoader().parse(reader, CTPageBreak.type, xmlOptions);
        }
        
        public static CTPageBreak parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPageBreak)getTypeLoader().parse(xmlStreamReader, CTPageBreak.type, (XmlOptions)null);
        }
        
        public static CTPageBreak parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageBreak)getTypeLoader().parse(xmlStreamReader, CTPageBreak.type, xmlOptions);
        }
        
        public static CTPageBreak parse(final Node node) throws XmlException {
            return (CTPageBreak)getTypeLoader().parse(node, CTPageBreak.type, (XmlOptions)null);
        }
        
        public static CTPageBreak parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageBreak)getTypeLoader().parse(node, CTPageBreak.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPageBreak parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPageBreak)getTypeLoader().parse(xmlInputStream, CTPageBreak.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPageBreak parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPageBreak)getTypeLoader().parse(xmlInputStream, CTPageBreak.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageBreak.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageBreak.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
