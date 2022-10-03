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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTIconSet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTIconSet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cticonset2648type");
    
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
    
    STIconSetType.Enum getIconSet();
    
    STIconSetType xgetIconSet();
    
    boolean isSetIconSet();
    
    void setIconSet(final STIconSetType.Enum p0);
    
    void xsetIconSet(final STIconSetType p0);
    
    void unsetIconSet();
    
    boolean getShowValue();
    
    XmlBoolean xgetShowValue();
    
    boolean isSetShowValue();
    
    void setShowValue(final boolean p0);
    
    void xsetShowValue(final XmlBoolean p0);
    
    void unsetShowValue();
    
    boolean getPercent();
    
    XmlBoolean xgetPercent();
    
    boolean isSetPercent();
    
    void setPercent(final boolean p0);
    
    void xsetPercent(final XmlBoolean p0);
    
    void unsetPercent();
    
    boolean getReverse();
    
    XmlBoolean xgetReverse();
    
    boolean isSetReverse();
    
    void setReverse(final boolean p0);
    
    void xsetReverse(final XmlBoolean p0);
    
    void unsetReverse();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTIconSet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTIconSet newInstance() {
            return (CTIconSet)getTypeLoader().newInstance(CTIconSet.type, (XmlOptions)null);
        }
        
        public static CTIconSet newInstance(final XmlOptions xmlOptions) {
            return (CTIconSet)getTypeLoader().newInstance(CTIconSet.type, xmlOptions);
        }
        
        public static CTIconSet parse(final String s) throws XmlException {
            return (CTIconSet)getTypeLoader().parse(s, CTIconSet.type, (XmlOptions)null);
        }
        
        public static CTIconSet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTIconSet)getTypeLoader().parse(s, CTIconSet.type, xmlOptions);
        }
        
        public static CTIconSet parse(final File file) throws XmlException, IOException {
            return (CTIconSet)getTypeLoader().parse(file, CTIconSet.type, (XmlOptions)null);
        }
        
        public static CTIconSet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIconSet)getTypeLoader().parse(file, CTIconSet.type, xmlOptions);
        }
        
        public static CTIconSet parse(final URL url) throws XmlException, IOException {
            return (CTIconSet)getTypeLoader().parse(url, CTIconSet.type, (XmlOptions)null);
        }
        
        public static CTIconSet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIconSet)getTypeLoader().parse(url, CTIconSet.type, xmlOptions);
        }
        
        public static CTIconSet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTIconSet)getTypeLoader().parse(inputStream, CTIconSet.type, (XmlOptions)null);
        }
        
        public static CTIconSet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIconSet)getTypeLoader().parse(inputStream, CTIconSet.type, xmlOptions);
        }
        
        public static CTIconSet parse(final Reader reader) throws XmlException, IOException {
            return (CTIconSet)getTypeLoader().parse(reader, CTIconSet.type, (XmlOptions)null);
        }
        
        public static CTIconSet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIconSet)getTypeLoader().parse(reader, CTIconSet.type, xmlOptions);
        }
        
        public static CTIconSet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTIconSet)getTypeLoader().parse(xmlStreamReader, CTIconSet.type, (XmlOptions)null);
        }
        
        public static CTIconSet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTIconSet)getTypeLoader().parse(xmlStreamReader, CTIconSet.type, xmlOptions);
        }
        
        public static CTIconSet parse(final Node node) throws XmlException {
            return (CTIconSet)getTypeLoader().parse(node, CTIconSet.type, (XmlOptions)null);
        }
        
        public static CTIconSet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTIconSet)getTypeLoader().parse(node, CTIconSet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTIconSet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTIconSet)getTypeLoader().parse(xmlInputStream, CTIconSet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTIconSet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTIconSet)getTypeLoader().parse(xmlInputStream, CTIconSet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIconSet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIconSet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
