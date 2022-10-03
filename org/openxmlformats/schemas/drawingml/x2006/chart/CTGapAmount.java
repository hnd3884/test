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

public interface CTGapAmount extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGapAmount.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgapamountdd98type");
    
    int getVal();
    
    STGapAmount xgetVal();
    
    boolean isSetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STGapAmount p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGapAmount.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGapAmount newInstance() {
            return (CTGapAmount)getTypeLoader().newInstance(CTGapAmount.type, (XmlOptions)null);
        }
        
        public static CTGapAmount newInstance(final XmlOptions xmlOptions) {
            return (CTGapAmount)getTypeLoader().newInstance(CTGapAmount.type, xmlOptions);
        }
        
        public static CTGapAmount parse(final String s) throws XmlException {
            return (CTGapAmount)getTypeLoader().parse(s, CTGapAmount.type, (XmlOptions)null);
        }
        
        public static CTGapAmount parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGapAmount)getTypeLoader().parse(s, CTGapAmount.type, xmlOptions);
        }
        
        public static CTGapAmount parse(final File file) throws XmlException, IOException {
            return (CTGapAmount)getTypeLoader().parse(file, CTGapAmount.type, (XmlOptions)null);
        }
        
        public static CTGapAmount parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGapAmount)getTypeLoader().parse(file, CTGapAmount.type, xmlOptions);
        }
        
        public static CTGapAmount parse(final URL url) throws XmlException, IOException {
            return (CTGapAmount)getTypeLoader().parse(url, CTGapAmount.type, (XmlOptions)null);
        }
        
        public static CTGapAmount parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGapAmount)getTypeLoader().parse(url, CTGapAmount.type, xmlOptions);
        }
        
        public static CTGapAmount parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGapAmount)getTypeLoader().parse(inputStream, CTGapAmount.type, (XmlOptions)null);
        }
        
        public static CTGapAmount parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGapAmount)getTypeLoader().parse(inputStream, CTGapAmount.type, xmlOptions);
        }
        
        public static CTGapAmount parse(final Reader reader) throws XmlException, IOException {
            return (CTGapAmount)getTypeLoader().parse(reader, CTGapAmount.type, (XmlOptions)null);
        }
        
        public static CTGapAmount parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGapAmount)getTypeLoader().parse(reader, CTGapAmount.type, xmlOptions);
        }
        
        public static CTGapAmount parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGapAmount)getTypeLoader().parse(xmlStreamReader, CTGapAmount.type, (XmlOptions)null);
        }
        
        public static CTGapAmount parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGapAmount)getTypeLoader().parse(xmlStreamReader, CTGapAmount.type, xmlOptions);
        }
        
        public static CTGapAmount parse(final Node node) throws XmlException {
            return (CTGapAmount)getTypeLoader().parse(node, CTGapAmount.type, (XmlOptions)null);
        }
        
        public static CTGapAmount parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGapAmount)getTypeLoader().parse(node, CTGapAmount.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGapAmount parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGapAmount)getTypeLoader().parse(xmlInputStream, CTGapAmount.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGapAmount parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGapAmount)getTypeLoader().parse(xmlInputStream, CTGapAmount.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGapAmount.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGapAmount.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
