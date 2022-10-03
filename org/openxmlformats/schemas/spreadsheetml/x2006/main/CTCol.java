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
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCol extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCol.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcola95ftype");
    
    long getMin();
    
    XmlUnsignedInt xgetMin();
    
    void setMin(final long p0);
    
    void xsetMin(final XmlUnsignedInt p0);
    
    long getMax();
    
    XmlUnsignedInt xgetMax();
    
    void setMax(final long p0);
    
    void xsetMax(final XmlUnsignedInt p0);
    
    double getWidth();
    
    XmlDouble xgetWidth();
    
    boolean isSetWidth();
    
    void setWidth(final double p0);
    
    void xsetWidth(final XmlDouble p0);
    
    void unsetWidth();
    
    long getStyle();
    
    XmlUnsignedInt xgetStyle();
    
    boolean isSetStyle();
    
    void setStyle(final long p0);
    
    void xsetStyle(final XmlUnsignedInt p0);
    
    void unsetStyle();
    
    boolean getHidden();
    
    XmlBoolean xgetHidden();
    
    boolean isSetHidden();
    
    void setHidden(final boolean p0);
    
    void xsetHidden(final XmlBoolean p0);
    
    void unsetHidden();
    
    boolean getBestFit();
    
    XmlBoolean xgetBestFit();
    
    boolean isSetBestFit();
    
    void setBestFit(final boolean p0);
    
    void xsetBestFit(final XmlBoolean p0);
    
    void unsetBestFit();
    
    boolean getCustomWidth();
    
    XmlBoolean xgetCustomWidth();
    
    boolean isSetCustomWidth();
    
    void setCustomWidth(final boolean p0);
    
    void xsetCustomWidth(final XmlBoolean p0);
    
    void unsetCustomWidth();
    
    boolean getPhonetic();
    
    XmlBoolean xgetPhonetic();
    
    boolean isSetPhonetic();
    
    void setPhonetic(final boolean p0);
    
    void xsetPhonetic(final XmlBoolean p0);
    
    void unsetPhonetic();
    
    short getOutlineLevel();
    
    XmlUnsignedByte xgetOutlineLevel();
    
    boolean isSetOutlineLevel();
    
    void setOutlineLevel(final short p0);
    
    void xsetOutlineLevel(final XmlUnsignedByte p0);
    
    void unsetOutlineLevel();
    
    boolean getCollapsed();
    
    XmlBoolean xgetCollapsed();
    
    boolean isSetCollapsed();
    
    void setCollapsed(final boolean p0);
    
    void xsetCollapsed(final XmlBoolean p0);
    
    void unsetCollapsed();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCol.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCol newInstance() {
            return (CTCol)getTypeLoader().newInstance(CTCol.type, (XmlOptions)null);
        }
        
        public static CTCol newInstance(final XmlOptions xmlOptions) {
            return (CTCol)getTypeLoader().newInstance(CTCol.type, xmlOptions);
        }
        
        public static CTCol parse(final String s) throws XmlException {
            return (CTCol)getTypeLoader().parse(s, CTCol.type, (XmlOptions)null);
        }
        
        public static CTCol parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCol)getTypeLoader().parse(s, CTCol.type, xmlOptions);
        }
        
        public static CTCol parse(final File file) throws XmlException, IOException {
            return (CTCol)getTypeLoader().parse(file, CTCol.type, (XmlOptions)null);
        }
        
        public static CTCol parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCol)getTypeLoader().parse(file, CTCol.type, xmlOptions);
        }
        
        public static CTCol parse(final URL url) throws XmlException, IOException {
            return (CTCol)getTypeLoader().parse(url, CTCol.type, (XmlOptions)null);
        }
        
        public static CTCol parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCol)getTypeLoader().parse(url, CTCol.type, xmlOptions);
        }
        
        public static CTCol parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCol)getTypeLoader().parse(inputStream, CTCol.type, (XmlOptions)null);
        }
        
        public static CTCol parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCol)getTypeLoader().parse(inputStream, CTCol.type, xmlOptions);
        }
        
        public static CTCol parse(final Reader reader) throws XmlException, IOException {
            return (CTCol)getTypeLoader().parse(reader, CTCol.type, (XmlOptions)null);
        }
        
        public static CTCol parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCol)getTypeLoader().parse(reader, CTCol.type, xmlOptions);
        }
        
        public static CTCol parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCol)getTypeLoader().parse(xmlStreamReader, CTCol.type, (XmlOptions)null);
        }
        
        public static CTCol parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCol)getTypeLoader().parse(xmlStreamReader, CTCol.type, xmlOptions);
        }
        
        public static CTCol parse(final Node node) throws XmlException {
            return (CTCol)getTypeLoader().parse(node, CTCol.type, (XmlOptions)null);
        }
        
        public static CTCol parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCol)getTypeLoader().parse(node, CTCol.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCol parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCol)getTypeLoader().parse(xmlInputStream, CTCol.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCol parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCol)getTypeLoader().parse(xmlInputStream, CTCol.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCol.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCol.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
