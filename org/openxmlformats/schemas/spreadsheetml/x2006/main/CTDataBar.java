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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDataBar extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDataBar.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdatabar4128type");
    
    List<CTCfvo> getCfvoList();
    
    @Deprecated
    CTCfvo[] getCfvoArray();
    
    CTCfvo getCfvoArray(final int p0);
    
    int sizeOfCfvoArray();
    
    void setCfvoArray(final CTCfvo[] p0);
    
    void setCfvoArray(final int p0, final CTCfvo p1);
    
    CTCfvo insertNewCfvo(final int p0);
    
    CTCfvo addNewCfvo();
    
    void removeCfvo(final int p0);
    
    CTColor getColor();
    
    void setColor(final CTColor p0);
    
    CTColor addNewColor();
    
    long getMinLength();
    
    XmlUnsignedInt xgetMinLength();
    
    boolean isSetMinLength();
    
    void setMinLength(final long p0);
    
    void xsetMinLength(final XmlUnsignedInt p0);
    
    void unsetMinLength();
    
    long getMaxLength();
    
    XmlUnsignedInt xgetMaxLength();
    
    boolean isSetMaxLength();
    
    void setMaxLength(final long p0);
    
    void xsetMaxLength(final XmlUnsignedInt p0);
    
    void unsetMaxLength();
    
    boolean getShowValue();
    
    XmlBoolean xgetShowValue();
    
    boolean isSetShowValue();
    
    void setShowValue(final boolean p0);
    
    void xsetShowValue(final XmlBoolean p0);
    
    void unsetShowValue();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDataBar.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDataBar newInstance() {
            return (CTDataBar)getTypeLoader().newInstance(CTDataBar.type, (XmlOptions)null);
        }
        
        public static CTDataBar newInstance(final XmlOptions xmlOptions) {
            return (CTDataBar)getTypeLoader().newInstance(CTDataBar.type, xmlOptions);
        }
        
        public static CTDataBar parse(final String s) throws XmlException {
            return (CTDataBar)getTypeLoader().parse(s, CTDataBar.type, (XmlOptions)null);
        }
        
        public static CTDataBar parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataBar)getTypeLoader().parse(s, CTDataBar.type, xmlOptions);
        }
        
        public static CTDataBar parse(final File file) throws XmlException, IOException {
            return (CTDataBar)getTypeLoader().parse(file, CTDataBar.type, (XmlOptions)null);
        }
        
        public static CTDataBar parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataBar)getTypeLoader().parse(file, CTDataBar.type, xmlOptions);
        }
        
        public static CTDataBar parse(final URL url) throws XmlException, IOException {
            return (CTDataBar)getTypeLoader().parse(url, CTDataBar.type, (XmlOptions)null);
        }
        
        public static CTDataBar parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataBar)getTypeLoader().parse(url, CTDataBar.type, xmlOptions);
        }
        
        public static CTDataBar parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDataBar)getTypeLoader().parse(inputStream, CTDataBar.type, (XmlOptions)null);
        }
        
        public static CTDataBar parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataBar)getTypeLoader().parse(inputStream, CTDataBar.type, xmlOptions);
        }
        
        public static CTDataBar parse(final Reader reader) throws XmlException, IOException {
            return (CTDataBar)getTypeLoader().parse(reader, CTDataBar.type, (XmlOptions)null);
        }
        
        public static CTDataBar parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataBar)getTypeLoader().parse(reader, CTDataBar.type, xmlOptions);
        }
        
        public static CTDataBar parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDataBar)getTypeLoader().parse(xmlStreamReader, CTDataBar.type, (XmlOptions)null);
        }
        
        public static CTDataBar parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataBar)getTypeLoader().parse(xmlStreamReader, CTDataBar.type, xmlOptions);
        }
        
        public static CTDataBar parse(final Node node) throws XmlException {
            return (CTDataBar)getTypeLoader().parse(node, CTDataBar.type, (XmlOptions)null);
        }
        
        public static CTDataBar parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataBar)getTypeLoader().parse(node, CTDataBar.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDataBar parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDataBar)getTypeLoader().parse(xmlInputStream, CTDataBar.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDataBar parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDataBar)getTypeLoader().parse(xmlInputStream, CTDataBar.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataBar.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataBar.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
