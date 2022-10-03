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

public interface STTextBulletSizePercent extends STPercentage
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextBulletSizePercent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextbulletsizepercentb516type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextBulletSizePercent newValue(final Object o) {
            return (STTextBulletSizePercent)STTextBulletSizePercent.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextBulletSizePercent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextBulletSizePercent newInstance() {
            return (STTextBulletSizePercent)getTypeLoader().newInstance(STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static STTextBulletSizePercent newInstance(final XmlOptions xmlOptions) {
            return (STTextBulletSizePercent)getTypeLoader().newInstance(STTextBulletSizePercent.type, xmlOptions);
        }
        
        public static STTextBulletSizePercent parse(final String s) throws XmlException {
            return (STTextBulletSizePercent)getTypeLoader().parse(s, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static STTextBulletSizePercent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextBulletSizePercent)getTypeLoader().parse(s, STTextBulletSizePercent.type, xmlOptions);
        }
        
        public static STTextBulletSizePercent parse(final File file) throws XmlException, IOException {
            return (STTextBulletSizePercent)getTypeLoader().parse(file, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static STTextBulletSizePercent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextBulletSizePercent)getTypeLoader().parse(file, STTextBulletSizePercent.type, xmlOptions);
        }
        
        public static STTextBulletSizePercent parse(final URL url) throws XmlException, IOException {
            return (STTextBulletSizePercent)getTypeLoader().parse(url, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static STTextBulletSizePercent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextBulletSizePercent)getTypeLoader().parse(url, STTextBulletSizePercent.type, xmlOptions);
        }
        
        public static STTextBulletSizePercent parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextBulletSizePercent)getTypeLoader().parse(inputStream, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static STTextBulletSizePercent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextBulletSizePercent)getTypeLoader().parse(inputStream, STTextBulletSizePercent.type, xmlOptions);
        }
        
        public static STTextBulletSizePercent parse(final Reader reader) throws XmlException, IOException {
            return (STTextBulletSizePercent)getTypeLoader().parse(reader, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static STTextBulletSizePercent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextBulletSizePercent)getTypeLoader().parse(reader, STTextBulletSizePercent.type, xmlOptions);
        }
        
        public static STTextBulletSizePercent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextBulletSizePercent)getTypeLoader().parse(xmlStreamReader, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static STTextBulletSizePercent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextBulletSizePercent)getTypeLoader().parse(xmlStreamReader, STTextBulletSizePercent.type, xmlOptions);
        }
        
        public static STTextBulletSizePercent parse(final Node node) throws XmlException {
            return (STTextBulletSizePercent)getTypeLoader().parse(node, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        public static STTextBulletSizePercent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextBulletSizePercent)getTypeLoader().parse(node, STTextBulletSizePercent.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextBulletSizePercent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextBulletSizePercent)getTypeLoader().parse(xmlInputStream, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextBulletSizePercent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextBulletSizePercent)getTypeLoader().parse(xmlInputStream, STTextBulletSizePercent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextBulletSizePercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextBulletSizePercent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
