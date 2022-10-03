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

public interface CTTextBulletSizeFollowText extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextBulletSizeFollowText.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextbulletsizefollowtext11e9type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextBulletSizeFollowText.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextBulletSizeFollowText newInstance() {
            return (CTTextBulletSizeFollowText)getTypeLoader().newInstance(CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizeFollowText newInstance(final XmlOptions xmlOptions) {
            return (CTTextBulletSizeFollowText)getTypeLoader().newInstance(CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletSizeFollowText parse(final String s) throws XmlException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(s, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizeFollowText parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(s, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletSizeFollowText parse(final File file) throws XmlException, IOException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(file, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizeFollowText parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(file, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletSizeFollowText parse(final URL url) throws XmlException, IOException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(url, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizeFollowText parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(url, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletSizeFollowText parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(inputStream, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizeFollowText parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(inputStream, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletSizeFollowText parse(final Reader reader) throws XmlException, IOException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(reader, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizeFollowText parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(reader, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletSizeFollowText parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(xmlStreamReader, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizeFollowText parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(xmlStreamReader, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletSizeFollowText parse(final Node node) throws XmlException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(node, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletSizeFollowText parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(node, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextBulletSizeFollowText parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(xmlInputStream, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextBulletSizeFollowText parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextBulletSizeFollowText)getTypeLoader().parse(xmlInputStream, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBulletSizeFollowText.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBulletSizeFollowText.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
