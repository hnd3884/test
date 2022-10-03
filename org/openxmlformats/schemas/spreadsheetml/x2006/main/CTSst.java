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

public interface CTSst extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSst.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsst44f3type");
    
    List<CTRst> getSiList();
    
    @Deprecated
    CTRst[] getSiArray();
    
    CTRst getSiArray(final int p0);
    
    int sizeOfSiArray();
    
    void setSiArray(final CTRst[] p0);
    
    void setSiArray(final int p0, final CTRst p1);
    
    CTRst insertNewSi(final int p0);
    
    CTRst addNewSi();
    
    void removeSi(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    long getUniqueCount();
    
    XmlUnsignedInt xgetUniqueCount();
    
    boolean isSetUniqueCount();
    
    void setUniqueCount(final long p0);
    
    void xsetUniqueCount(final XmlUnsignedInt p0);
    
    void unsetUniqueCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSst.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSst newInstance() {
            return (CTSst)getTypeLoader().newInstance(CTSst.type, (XmlOptions)null);
        }
        
        public static CTSst newInstance(final XmlOptions xmlOptions) {
            return (CTSst)getTypeLoader().newInstance(CTSst.type, xmlOptions);
        }
        
        public static CTSst parse(final String s) throws XmlException {
            return (CTSst)getTypeLoader().parse(s, CTSst.type, (XmlOptions)null);
        }
        
        public static CTSst parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSst)getTypeLoader().parse(s, CTSst.type, xmlOptions);
        }
        
        public static CTSst parse(final File file) throws XmlException, IOException {
            return (CTSst)getTypeLoader().parse(file, CTSst.type, (XmlOptions)null);
        }
        
        public static CTSst parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSst)getTypeLoader().parse(file, CTSst.type, xmlOptions);
        }
        
        public static CTSst parse(final URL url) throws XmlException, IOException {
            return (CTSst)getTypeLoader().parse(url, CTSst.type, (XmlOptions)null);
        }
        
        public static CTSst parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSst)getTypeLoader().parse(url, CTSst.type, xmlOptions);
        }
        
        public static CTSst parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSst)getTypeLoader().parse(inputStream, CTSst.type, (XmlOptions)null);
        }
        
        public static CTSst parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSst)getTypeLoader().parse(inputStream, CTSst.type, xmlOptions);
        }
        
        public static CTSst parse(final Reader reader) throws XmlException, IOException {
            return (CTSst)getTypeLoader().parse(reader, CTSst.type, (XmlOptions)null);
        }
        
        public static CTSst parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSst)getTypeLoader().parse(reader, CTSst.type, xmlOptions);
        }
        
        public static CTSst parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSst)getTypeLoader().parse(xmlStreamReader, CTSst.type, (XmlOptions)null);
        }
        
        public static CTSst parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSst)getTypeLoader().parse(xmlStreamReader, CTSst.type, xmlOptions);
        }
        
        public static CTSst parse(final Node node) throws XmlException {
            return (CTSst)getTypeLoader().parse(node, CTSst.type, (XmlOptions)null);
        }
        
        public static CTSst parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSst)getTypeLoader().parse(node, CTSst.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSst parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSst)getTypeLoader().parse(xmlInputStream, CTSst.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSst parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSst)getTypeLoader().parse(xmlInputStream, CTSst.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSst.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSst.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
