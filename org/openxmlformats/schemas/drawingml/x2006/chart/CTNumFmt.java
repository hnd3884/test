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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNumFmt extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumFmt.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumfmtc0f5type");
    
    String getFormatCode();
    
    STXstring xgetFormatCode();
    
    void setFormatCode(final String p0);
    
    void xsetFormatCode(final STXstring p0);
    
    boolean getSourceLinked();
    
    XmlBoolean xgetSourceLinked();
    
    boolean isSetSourceLinked();
    
    void setSourceLinked(final boolean p0);
    
    void xsetSourceLinked(final XmlBoolean p0);
    
    void unsetSourceLinked();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumFmt.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumFmt newInstance() {
            return (CTNumFmt)getTypeLoader().newInstance(CTNumFmt.type, (XmlOptions)null);
        }
        
        public static CTNumFmt newInstance(final XmlOptions xmlOptions) {
            return (CTNumFmt)getTypeLoader().newInstance(CTNumFmt.type, xmlOptions);
        }
        
        public static CTNumFmt parse(final String s) throws XmlException {
            return (CTNumFmt)getTypeLoader().parse(s, CTNumFmt.type, (XmlOptions)null);
        }
        
        public static CTNumFmt parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumFmt)getTypeLoader().parse(s, CTNumFmt.type, xmlOptions);
        }
        
        public static CTNumFmt parse(final File file) throws XmlException, IOException {
            return (CTNumFmt)getTypeLoader().parse(file, CTNumFmt.type, (XmlOptions)null);
        }
        
        public static CTNumFmt parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumFmt)getTypeLoader().parse(file, CTNumFmt.type, xmlOptions);
        }
        
        public static CTNumFmt parse(final URL url) throws XmlException, IOException {
            return (CTNumFmt)getTypeLoader().parse(url, CTNumFmt.type, (XmlOptions)null);
        }
        
        public static CTNumFmt parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumFmt)getTypeLoader().parse(url, CTNumFmt.type, xmlOptions);
        }
        
        public static CTNumFmt parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumFmt)getTypeLoader().parse(inputStream, CTNumFmt.type, (XmlOptions)null);
        }
        
        public static CTNumFmt parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumFmt)getTypeLoader().parse(inputStream, CTNumFmt.type, xmlOptions);
        }
        
        public static CTNumFmt parse(final Reader reader) throws XmlException, IOException {
            return (CTNumFmt)getTypeLoader().parse(reader, CTNumFmt.type, (XmlOptions)null);
        }
        
        public static CTNumFmt parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumFmt)getTypeLoader().parse(reader, CTNumFmt.type, xmlOptions);
        }
        
        public static CTNumFmt parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumFmt)getTypeLoader().parse(xmlStreamReader, CTNumFmt.type, (XmlOptions)null);
        }
        
        public static CTNumFmt parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumFmt)getTypeLoader().parse(xmlStreamReader, CTNumFmt.type, xmlOptions);
        }
        
        public static CTNumFmt parse(final Node node) throws XmlException {
            return (CTNumFmt)getTypeLoader().parse(node, CTNumFmt.type, (XmlOptions)null);
        }
        
        public static CTNumFmt parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumFmt)getTypeLoader().parse(node, CTNumFmt.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumFmt parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumFmt)getTypeLoader().parse(xmlInputStream, CTNumFmt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumFmt parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumFmt)getTypeLoader().parse(xmlInputStream, CTNumFmt.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumFmt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumFmt.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
