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

public interface CTFixedPercentage extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFixedPercentage.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfixedpercentagea2dftype");
    
    int getVal();
    
    STFixedPercentage xgetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STFixedPercentage p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFixedPercentage.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFixedPercentage newInstance() {
            return (CTFixedPercentage)getTypeLoader().newInstance(CTFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTFixedPercentage newInstance(final XmlOptions xmlOptions) {
            return (CTFixedPercentage)getTypeLoader().newInstance(CTFixedPercentage.type, xmlOptions);
        }
        
        public static CTFixedPercentage parse(final String s) throws XmlException {
            return (CTFixedPercentage)getTypeLoader().parse(s, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTFixedPercentage parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFixedPercentage)getTypeLoader().parse(s, CTFixedPercentage.type, xmlOptions);
        }
        
        public static CTFixedPercentage parse(final File file) throws XmlException, IOException {
            return (CTFixedPercentage)getTypeLoader().parse(file, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTFixedPercentage parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFixedPercentage)getTypeLoader().parse(file, CTFixedPercentage.type, xmlOptions);
        }
        
        public static CTFixedPercentage parse(final URL url) throws XmlException, IOException {
            return (CTFixedPercentage)getTypeLoader().parse(url, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTFixedPercentage parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFixedPercentage)getTypeLoader().parse(url, CTFixedPercentage.type, xmlOptions);
        }
        
        public static CTFixedPercentage parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFixedPercentage)getTypeLoader().parse(inputStream, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTFixedPercentage parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFixedPercentage)getTypeLoader().parse(inputStream, CTFixedPercentage.type, xmlOptions);
        }
        
        public static CTFixedPercentage parse(final Reader reader) throws XmlException, IOException {
            return (CTFixedPercentage)getTypeLoader().parse(reader, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTFixedPercentage parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFixedPercentage)getTypeLoader().parse(reader, CTFixedPercentage.type, xmlOptions);
        }
        
        public static CTFixedPercentage parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFixedPercentage)getTypeLoader().parse(xmlStreamReader, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTFixedPercentage parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFixedPercentage)getTypeLoader().parse(xmlStreamReader, CTFixedPercentage.type, xmlOptions);
        }
        
        public static CTFixedPercentage parse(final Node node) throws XmlException {
            return (CTFixedPercentage)getTypeLoader().parse(node, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTFixedPercentage parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFixedPercentage)getTypeLoader().parse(node, CTFixedPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFixedPercentage parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFixedPercentage)getTypeLoader().parse(xmlInputStream, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFixedPercentage parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFixedPercentage)getTypeLoader().parse(xmlInputStream, CTFixedPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFixedPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFixedPercentage.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
