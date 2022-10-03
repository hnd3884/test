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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPictureBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPictureBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpicturebase5f83type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPictureBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPictureBase newInstance() {
            return (CTPictureBase)getTypeLoader().newInstance(CTPictureBase.type, (XmlOptions)null);
        }
        
        public static CTPictureBase newInstance(final XmlOptions xmlOptions) {
            return (CTPictureBase)getTypeLoader().newInstance(CTPictureBase.type, xmlOptions);
        }
        
        public static CTPictureBase parse(final String s) throws XmlException {
            return (CTPictureBase)getTypeLoader().parse(s, CTPictureBase.type, (XmlOptions)null);
        }
        
        public static CTPictureBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureBase)getTypeLoader().parse(s, CTPictureBase.type, xmlOptions);
        }
        
        public static CTPictureBase parse(final File file) throws XmlException, IOException {
            return (CTPictureBase)getTypeLoader().parse(file, CTPictureBase.type, (XmlOptions)null);
        }
        
        public static CTPictureBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureBase)getTypeLoader().parse(file, CTPictureBase.type, xmlOptions);
        }
        
        public static CTPictureBase parse(final URL url) throws XmlException, IOException {
            return (CTPictureBase)getTypeLoader().parse(url, CTPictureBase.type, (XmlOptions)null);
        }
        
        public static CTPictureBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureBase)getTypeLoader().parse(url, CTPictureBase.type, xmlOptions);
        }
        
        public static CTPictureBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPictureBase)getTypeLoader().parse(inputStream, CTPictureBase.type, (XmlOptions)null);
        }
        
        public static CTPictureBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureBase)getTypeLoader().parse(inputStream, CTPictureBase.type, xmlOptions);
        }
        
        public static CTPictureBase parse(final Reader reader) throws XmlException, IOException {
            return (CTPictureBase)getTypeLoader().parse(reader, CTPictureBase.type, (XmlOptions)null);
        }
        
        public static CTPictureBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureBase)getTypeLoader().parse(reader, CTPictureBase.type, xmlOptions);
        }
        
        public static CTPictureBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPictureBase)getTypeLoader().parse(xmlStreamReader, CTPictureBase.type, (XmlOptions)null);
        }
        
        public static CTPictureBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureBase)getTypeLoader().parse(xmlStreamReader, CTPictureBase.type, xmlOptions);
        }
        
        public static CTPictureBase parse(final Node node) throws XmlException {
            return (CTPictureBase)getTypeLoader().parse(node, CTPictureBase.type, (XmlOptions)null);
        }
        
        public static CTPictureBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureBase)getTypeLoader().parse(node, CTPictureBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPictureBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPictureBase)getTypeLoader().parse(xmlInputStream, CTPictureBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPictureBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPictureBase)getTypeLoader().parse(xmlInputStream, CTPictureBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPictureBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPictureBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
