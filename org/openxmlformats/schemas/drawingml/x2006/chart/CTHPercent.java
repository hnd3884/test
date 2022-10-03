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

public interface CTHPercent extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHPercent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cthpercent59dftype");
    
    int getVal();
    
    STHPercent xgetVal();
    
    boolean isSetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STHPercent p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHPercent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHPercent newInstance() {
            return (CTHPercent)getTypeLoader().newInstance(CTHPercent.type, (XmlOptions)null);
        }
        
        public static CTHPercent newInstance(final XmlOptions xmlOptions) {
            return (CTHPercent)getTypeLoader().newInstance(CTHPercent.type, xmlOptions);
        }
        
        public static CTHPercent parse(final String s) throws XmlException {
            return (CTHPercent)getTypeLoader().parse(s, CTHPercent.type, (XmlOptions)null);
        }
        
        public static CTHPercent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHPercent)getTypeLoader().parse(s, CTHPercent.type, xmlOptions);
        }
        
        public static CTHPercent parse(final File file) throws XmlException, IOException {
            return (CTHPercent)getTypeLoader().parse(file, CTHPercent.type, (XmlOptions)null);
        }
        
        public static CTHPercent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHPercent)getTypeLoader().parse(file, CTHPercent.type, xmlOptions);
        }
        
        public static CTHPercent parse(final URL url) throws XmlException, IOException {
            return (CTHPercent)getTypeLoader().parse(url, CTHPercent.type, (XmlOptions)null);
        }
        
        public static CTHPercent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHPercent)getTypeLoader().parse(url, CTHPercent.type, xmlOptions);
        }
        
        public static CTHPercent parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHPercent)getTypeLoader().parse(inputStream, CTHPercent.type, (XmlOptions)null);
        }
        
        public static CTHPercent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHPercent)getTypeLoader().parse(inputStream, CTHPercent.type, xmlOptions);
        }
        
        public static CTHPercent parse(final Reader reader) throws XmlException, IOException {
            return (CTHPercent)getTypeLoader().parse(reader, CTHPercent.type, (XmlOptions)null);
        }
        
        public static CTHPercent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHPercent)getTypeLoader().parse(reader, CTHPercent.type, xmlOptions);
        }
        
        public static CTHPercent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHPercent)getTypeLoader().parse(xmlStreamReader, CTHPercent.type, (XmlOptions)null);
        }
        
        public static CTHPercent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHPercent)getTypeLoader().parse(xmlStreamReader, CTHPercent.type, xmlOptions);
        }
        
        public static CTHPercent parse(final Node node) throws XmlException {
            return (CTHPercent)getTypeLoader().parse(node, CTHPercent.type, (XmlOptions)null);
        }
        
        public static CTHPercent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHPercent)getTypeLoader().parse(node, CTHPercent.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHPercent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHPercent)getTypeLoader().parse(xmlInputStream, CTHPercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHPercent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHPercent)getTypeLoader().parse(xmlInputStream, CTHPercent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHPercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHPercent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
