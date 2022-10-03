package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNumbering extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumbering.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumberingfdf9type");
    
    List<CTNumPicBullet> getNumPicBulletList();
    
    @Deprecated
    CTNumPicBullet[] getNumPicBulletArray();
    
    CTNumPicBullet getNumPicBulletArray(final int p0);
    
    int sizeOfNumPicBulletArray();
    
    void setNumPicBulletArray(final CTNumPicBullet[] p0);
    
    void setNumPicBulletArray(final int p0, final CTNumPicBullet p1);
    
    CTNumPicBullet insertNewNumPicBullet(final int p0);
    
    CTNumPicBullet addNewNumPicBullet();
    
    void removeNumPicBullet(final int p0);
    
    List<CTAbstractNum> getAbstractNumList();
    
    @Deprecated
    CTAbstractNum[] getAbstractNumArray();
    
    CTAbstractNum getAbstractNumArray(final int p0);
    
    int sizeOfAbstractNumArray();
    
    void setAbstractNumArray(final CTAbstractNum[] p0);
    
    void setAbstractNumArray(final int p0, final CTAbstractNum p1);
    
    CTAbstractNum insertNewAbstractNum(final int p0);
    
    CTAbstractNum addNewAbstractNum();
    
    void removeAbstractNum(final int p0);
    
    List<CTNum> getNumList();
    
    @Deprecated
    CTNum[] getNumArray();
    
    CTNum getNumArray(final int p0);
    
    int sizeOfNumArray();
    
    void setNumArray(final CTNum[] p0);
    
    void setNumArray(final int p0, final CTNum p1);
    
    CTNum insertNewNum(final int p0);
    
    CTNum addNewNum();
    
    void removeNum(final int p0);
    
    CTDecimalNumber getNumIdMacAtCleanup();
    
    boolean isSetNumIdMacAtCleanup();
    
    void setNumIdMacAtCleanup(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewNumIdMacAtCleanup();
    
    void unsetNumIdMacAtCleanup();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumbering.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumbering newInstance() {
            return (CTNumbering)getTypeLoader().newInstance(CTNumbering.type, (XmlOptions)null);
        }
        
        public static CTNumbering newInstance(final XmlOptions xmlOptions) {
            return (CTNumbering)getTypeLoader().newInstance(CTNumbering.type, xmlOptions);
        }
        
        public static CTNumbering parse(final String s) throws XmlException {
            return (CTNumbering)getTypeLoader().parse(s, CTNumbering.type, (XmlOptions)null);
        }
        
        public static CTNumbering parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumbering)getTypeLoader().parse(s, CTNumbering.type, xmlOptions);
        }
        
        public static CTNumbering parse(final File file) throws XmlException, IOException {
            return (CTNumbering)getTypeLoader().parse(file, CTNumbering.type, (XmlOptions)null);
        }
        
        public static CTNumbering parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumbering)getTypeLoader().parse(file, CTNumbering.type, xmlOptions);
        }
        
        public static CTNumbering parse(final URL url) throws XmlException, IOException {
            return (CTNumbering)getTypeLoader().parse(url, CTNumbering.type, (XmlOptions)null);
        }
        
        public static CTNumbering parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumbering)getTypeLoader().parse(url, CTNumbering.type, xmlOptions);
        }
        
        public static CTNumbering parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumbering)getTypeLoader().parse(inputStream, CTNumbering.type, (XmlOptions)null);
        }
        
        public static CTNumbering parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumbering)getTypeLoader().parse(inputStream, CTNumbering.type, xmlOptions);
        }
        
        public static CTNumbering parse(final Reader reader) throws XmlException, IOException {
            return (CTNumbering)getTypeLoader().parse(reader, CTNumbering.type, (XmlOptions)null);
        }
        
        public static CTNumbering parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumbering)getTypeLoader().parse(reader, CTNumbering.type, xmlOptions);
        }
        
        public static CTNumbering parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumbering)getTypeLoader().parse(xmlStreamReader, CTNumbering.type, (XmlOptions)null);
        }
        
        public static CTNumbering parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumbering)getTypeLoader().parse(xmlStreamReader, CTNumbering.type, xmlOptions);
        }
        
        public static CTNumbering parse(final Node node) throws XmlException {
            return (CTNumbering)getTypeLoader().parse(node, CTNumbering.type, (XmlOptions)null);
        }
        
        public static CTNumbering parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumbering)getTypeLoader().parse(node, CTNumbering.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumbering parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumbering)getTypeLoader().parse(xmlInputStream, CTNumbering.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumbering parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumbering)getTypeLoader().parse(xmlInputStream, CTNumbering.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumbering.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumbering.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
