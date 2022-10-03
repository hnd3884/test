package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTTextBulletSizePercent extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextBulletSizePercent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextbulletsizepercent9b26type");
    
    int getVal();
    
    STTextBulletSizePercent xgetVal();
    
    boolean isSetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STTextBulletSizePercent p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextBulletSizePercent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextBulletSizePercent newInstance() {
            return (CTTextBulletSizePercent)getTypeLoader().newInstance(CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizePercent newInstance(final XmlOptions xmlOptions) {
            return (CTTextBulletSizePercent)getTypeLoader().newInstance(CTTextBulletSizePercent.type, xmlOptions);
        }
        
        public static CTTextBulletSizePercent parse(final String s) throws XmlException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(s, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizePercent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(s, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        public static CTTextBulletSizePercent parse(final File file) throws XmlException, IOException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(file, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizePercent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(file, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        public static CTTextBulletSizePercent parse(final URL url) throws XmlException, IOException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(url, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizePercent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(url, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        public static CTTextBulletSizePercent parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(inputStream, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizePercent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(inputStream, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        public static CTTextBulletSizePercent parse(final Reader reader) throws XmlException, IOException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(reader, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizePercent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(reader, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        public static CTTextBulletSizePercent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(xmlStreamReader, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizePercent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(xmlStreamReader, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        public static CTTextBulletSizePercent parse(final Node node) throws XmlException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(node, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizePercent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(node, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextBulletSizePercent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(xmlInputStream, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextBulletSizePercent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextBulletSizePercent)getTypeLoader().parse(xmlInputStream, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBulletSizePercent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
