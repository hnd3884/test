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

public interface CTPositivePercentage extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPositivePercentage.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpositivepercentage2f8etype");
    
    int getVal();
    
    STPositivePercentage xgetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STPositivePercentage p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPositivePercentage.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPositivePercentage newInstance() {
            return (CTPositivePercentage)getTypeLoader().newInstance(CTPositivePercentage.type, (XmlOptions)null);
        }
        
        public static CTPositivePercentage newInstance(final XmlOptions xmlOptions) {
            return (CTPositivePercentage)getTypeLoader().newInstance(CTPositivePercentage.type, xmlOptions);
        }
        
        public static CTPositivePercentage parse(final String s) throws XmlException {
            return (CTPositivePercentage)getTypeLoader().parse(s, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        public static CTPositivePercentage parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPositivePercentage)getTypeLoader().parse(s, CTPositivePercentage.type, xmlOptions);
        }
        
        public static CTPositivePercentage parse(final File file) throws XmlException, IOException {
            return (CTPositivePercentage)getTypeLoader().parse(file, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        public static CTPositivePercentage parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPositivePercentage)getTypeLoader().parse(file, CTPositivePercentage.type, xmlOptions);
        }
        
        public static CTPositivePercentage parse(final URL url) throws XmlException, IOException {
            return (CTPositivePercentage)getTypeLoader().parse(url, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        public static CTPositivePercentage parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPositivePercentage)getTypeLoader().parse(url, CTPositivePercentage.type, xmlOptions);
        }
        
        public static CTPositivePercentage parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPositivePercentage)getTypeLoader().parse(inputStream, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        public static CTPositivePercentage parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPositivePercentage)getTypeLoader().parse(inputStream, CTPositivePercentage.type, xmlOptions);
        }
        
        public static CTPositivePercentage parse(final Reader reader) throws XmlException, IOException {
            return (CTPositivePercentage)getTypeLoader().parse(reader, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        public static CTPositivePercentage parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPositivePercentage)getTypeLoader().parse(reader, CTPositivePercentage.type, xmlOptions);
        }
        
        public static CTPositivePercentage parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPositivePercentage)getTypeLoader().parse(xmlStreamReader, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        public static CTPositivePercentage parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPositivePercentage)getTypeLoader().parse(xmlStreamReader, CTPositivePercentage.type, xmlOptions);
        }
        
        public static CTPositivePercentage parse(final Node node) throws XmlException {
            return (CTPositivePercentage)getTypeLoader().parse(node, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        public static CTPositivePercentage parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPositivePercentage)getTypeLoader().parse(node, CTPositivePercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPositivePercentage parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPositivePercentage)getTypeLoader().parse(xmlInputStream, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPositivePercentage parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPositivePercentage)getTypeLoader().parse(xmlInputStream, CTPositivePercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPositivePercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPositivePercentage.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
