package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface CTAxisUnit extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAxisUnit.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctaxisunitead7type");
    
    double getVal();
    
    STAxisUnit xgetVal();
    
    void setVal(final double p0);
    
    void xsetVal(final STAxisUnit p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAxisUnit.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAxisUnit newInstance() {
            return (CTAxisUnit)getTypeLoader().newInstance(CTAxisUnit.type, (XmlOptions)null);
        }
        
        public static CTAxisUnit newInstance(final XmlOptions xmlOptions) {
            return (CTAxisUnit)getTypeLoader().newInstance(CTAxisUnit.type, xmlOptions);
        }
        
        public static CTAxisUnit parse(final String s) throws XmlException {
            return (CTAxisUnit)getTypeLoader().parse(s, CTAxisUnit.type, (XmlOptions)null);
        }
        
        public static CTAxisUnit parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxisUnit)getTypeLoader().parse(s, CTAxisUnit.type, xmlOptions);
        }
        
        public static CTAxisUnit parse(final File file) throws XmlException, IOException {
            return (CTAxisUnit)getTypeLoader().parse(file, CTAxisUnit.type, (XmlOptions)null);
        }
        
        public static CTAxisUnit parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxisUnit)getTypeLoader().parse(file, CTAxisUnit.type, xmlOptions);
        }
        
        public static CTAxisUnit parse(final URL url) throws XmlException, IOException {
            return (CTAxisUnit)getTypeLoader().parse(url, CTAxisUnit.type, (XmlOptions)null);
        }
        
        public static CTAxisUnit parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxisUnit)getTypeLoader().parse(url, CTAxisUnit.type, xmlOptions);
        }
        
        public static CTAxisUnit parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAxisUnit)getTypeLoader().parse(inputStream, CTAxisUnit.type, (XmlOptions)null);
        }
        
        public static CTAxisUnit parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxisUnit)getTypeLoader().parse(inputStream, CTAxisUnit.type, xmlOptions);
        }
        
        public static CTAxisUnit parse(final Reader reader) throws XmlException, IOException {
            return (CTAxisUnit)getTypeLoader().parse(reader, CTAxisUnit.type, (XmlOptions)null);
        }
        
        public static CTAxisUnit parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxisUnit)getTypeLoader().parse(reader, CTAxisUnit.type, xmlOptions);
        }
        
        public static CTAxisUnit parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAxisUnit)getTypeLoader().parse(xmlStreamReader, CTAxisUnit.type, (XmlOptions)null);
        }
        
        public static CTAxisUnit parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxisUnit)getTypeLoader().parse(xmlStreamReader, CTAxisUnit.type, xmlOptions);
        }
        
        public static CTAxisUnit parse(final Node node) throws XmlException {
            return (CTAxisUnit)getTypeLoader().parse(node, CTAxisUnit.type, (XmlOptions)null);
        }
        
        public static CTAxisUnit parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxisUnit)getTypeLoader().parse(node, CTAxisUnit.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAxisUnit parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAxisUnit)getTypeLoader().parse(xmlInputStream, CTAxisUnit.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAxisUnit parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAxisUnit)getTypeLoader().parse(xmlInputStream, CTAxisUnit.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAxisUnit.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAxisUnit.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
