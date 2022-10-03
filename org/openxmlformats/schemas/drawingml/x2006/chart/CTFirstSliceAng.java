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

public interface CTFirstSliceAng extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFirstSliceAng.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfirstsliceang0ceetype");
    
    int getVal();
    
    STFirstSliceAng xgetVal();
    
    boolean isSetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STFirstSliceAng p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFirstSliceAng.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFirstSliceAng newInstance() {
            return (CTFirstSliceAng)getTypeLoader().newInstance(CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        public static CTFirstSliceAng newInstance(final XmlOptions xmlOptions) {
            return (CTFirstSliceAng)getTypeLoader().newInstance(CTFirstSliceAng.type, xmlOptions);
        }
        
        public static CTFirstSliceAng parse(final String s) throws XmlException {
            return (CTFirstSliceAng)getTypeLoader().parse(s, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        public static CTFirstSliceAng parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFirstSliceAng)getTypeLoader().parse(s, CTFirstSliceAng.type, xmlOptions);
        }
        
        public static CTFirstSliceAng parse(final File file) throws XmlException, IOException {
            return (CTFirstSliceAng)getTypeLoader().parse(file, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        public static CTFirstSliceAng parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFirstSliceAng)getTypeLoader().parse(file, CTFirstSliceAng.type, xmlOptions);
        }
        
        public static CTFirstSliceAng parse(final URL url) throws XmlException, IOException {
            return (CTFirstSliceAng)getTypeLoader().parse(url, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        public static CTFirstSliceAng parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFirstSliceAng)getTypeLoader().parse(url, CTFirstSliceAng.type, xmlOptions);
        }
        
        public static CTFirstSliceAng parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFirstSliceAng)getTypeLoader().parse(inputStream, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        public static CTFirstSliceAng parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFirstSliceAng)getTypeLoader().parse(inputStream, CTFirstSliceAng.type, xmlOptions);
        }
        
        public static CTFirstSliceAng parse(final Reader reader) throws XmlException, IOException {
            return (CTFirstSliceAng)getTypeLoader().parse(reader, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        public static CTFirstSliceAng parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFirstSliceAng)getTypeLoader().parse(reader, CTFirstSliceAng.type, xmlOptions);
        }
        
        public static CTFirstSliceAng parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFirstSliceAng)getTypeLoader().parse(xmlStreamReader, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        public static CTFirstSliceAng parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFirstSliceAng)getTypeLoader().parse(xmlStreamReader, CTFirstSliceAng.type, xmlOptions);
        }
        
        public static CTFirstSliceAng parse(final Node node) throws XmlException {
            return (CTFirstSliceAng)getTypeLoader().parse(node, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        public static CTFirstSliceAng parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFirstSliceAng)getTypeLoader().parse(node, CTFirstSliceAng.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFirstSliceAng parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFirstSliceAng)getTypeLoader().parse(xmlInputStream, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFirstSliceAng parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFirstSliceAng)getTypeLoader().parse(xmlInputStream, CTFirstSliceAng.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFirstSliceAng.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFirstSliceAng.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
