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

public interface CTTextBulletColorFollowText extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextBulletColorFollowText.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextbulletcolorfollowtext2ca3type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextBulletColorFollowText.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextBulletColorFollowText newInstance() {
            return (CTTextBulletColorFollowText)getTypeLoader().newInstance(CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletColorFollowText newInstance(final XmlOptions xmlOptions) {
            return (CTTextBulletColorFollowText)getTypeLoader().newInstance(CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletColorFollowText parse(final String s) throws XmlException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(s, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletColorFollowText parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(s, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletColorFollowText parse(final File file) throws XmlException, IOException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(file, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletColorFollowText parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(file, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletColorFollowText parse(final URL url) throws XmlException, IOException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(url, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletColorFollowText parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(url, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletColorFollowText parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(inputStream, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletColorFollowText parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(inputStream, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletColorFollowText parse(final Reader reader) throws XmlException, IOException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(reader, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletColorFollowText parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(reader, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletColorFollowText parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(xmlStreamReader, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletColorFollowText parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(xmlStreamReader, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        public static CTTextBulletColorFollowText parse(final Node node) throws XmlException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(node, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        public static CTTextBulletColorFollowText parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(node, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextBulletColorFollowText parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(xmlInputStream, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextBulletColorFollowText parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextBulletColorFollowText)getTypeLoader().parse(xmlInputStream, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBulletColorFollowText.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextBulletColorFollowText.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
