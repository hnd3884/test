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
import org.apache.xmlbeans.XmlInt;

public interface STTextBulletStartAtNum extends XmlInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextBulletStartAtNum.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextbulletstartatnum562btype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextBulletStartAtNum newValue(final Object o) {
            return (STTextBulletStartAtNum)STTextBulletStartAtNum.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextBulletStartAtNum.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextBulletStartAtNum newInstance() {
            return (STTextBulletStartAtNum)getTypeLoader().newInstance(STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        public static STTextBulletStartAtNum newInstance(final XmlOptions xmlOptions) {
            return (STTextBulletStartAtNum)getTypeLoader().newInstance(STTextBulletStartAtNum.type, xmlOptions);
        }
        
        public static STTextBulletStartAtNum parse(final String s) throws XmlException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(s, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        public static STTextBulletStartAtNum parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(s, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        public static STTextBulletStartAtNum parse(final File file) throws XmlException, IOException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(file, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        public static STTextBulletStartAtNum parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(file, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        public static STTextBulletStartAtNum parse(final URL url) throws XmlException, IOException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(url, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        public static STTextBulletStartAtNum parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(url, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        public static STTextBulletStartAtNum parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(inputStream, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        public static STTextBulletStartAtNum parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(inputStream, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        public static STTextBulletStartAtNum parse(final Reader reader) throws XmlException, IOException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(reader, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        public static STTextBulletStartAtNum parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(reader, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        public static STTextBulletStartAtNum parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(xmlStreamReader, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        public static STTextBulletStartAtNum parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(xmlStreamReader, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        public static STTextBulletStartAtNum parse(final Node node) throws XmlException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(node, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        public static STTextBulletStartAtNum parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(node, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextBulletStartAtNum parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(xmlInputStream, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextBulletStartAtNum parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextBulletStartAtNum)getTypeLoader().parse(xmlInputStream, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextBulletStartAtNum.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextBulletStartAtNum.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
