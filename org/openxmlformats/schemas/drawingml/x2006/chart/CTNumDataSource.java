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

public interface CTNumDataSource extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumDataSource.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumdatasourcef0bbtype");
    
    CTNumRef getNumRef();
    
    boolean isSetNumRef();
    
    void setNumRef(final CTNumRef p0);
    
    CTNumRef addNewNumRef();
    
    void unsetNumRef();
    
    CTNumData getNumLit();
    
    boolean isSetNumLit();
    
    void setNumLit(final CTNumData p0);
    
    CTNumData addNewNumLit();
    
    void unsetNumLit();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumDataSource.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumDataSource newInstance() {
            return (CTNumDataSource)getTypeLoader().newInstance(CTNumDataSource.type, (XmlOptions)null);
        }
        
        public static CTNumDataSource newInstance(final XmlOptions xmlOptions) {
            return (CTNumDataSource)getTypeLoader().newInstance(CTNumDataSource.type, xmlOptions);
        }
        
        public static CTNumDataSource parse(final String s) throws XmlException {
            return (CTNumDataSource)getTypeLoader().parse(s, CTNumDataSource.type, (XmlOptions)null);
        }
        
        public static CTNumDataSource parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumDataSource)getTypeLoader().parse(s, CTNumDataSource.type, xmlOptions);
        }
        
        public static CTNumDataSource parse(final File file) throws XmlException, IOException {
            return (CTNumDataSource)getTypeLoader().parse(file, CTNumDataSource.type, (XmlOptions)null);
        }
        
        public static CTNumDataSource parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumDataSource)getTypeLoader().parse(file, CTNumDataSource.type, xmlOptions);
        }
        
        public static CTNumDataSource parse(final URL url) throws XmlException, IOException {
            return (CTNumDataSource)getTypeLoader().parse(url, CTNumDataSource.type, (XmlOptions)null);
        }
        
        public static CTNumDataSource parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumDataSource)getTypeLoader().parse(url, CTNumDataSource.type, xmlOptions);
        }
        
        public static CTNumDataSource parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumDataSource)getTypeLoader().parse(inputStream, CTNumDataSource.type, (XmlOptions)null);
        }
        
        public static CTNumDataSource parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumDataSource)getTypeLoader().parse(inputStream, CTNumDataSource.type, xmlOptions);
        }
        
        public static CTNumDataSource parse(final Reader reader) throws XmlException, IOException {
            return (CTNumDataSource)getTypeLoader().parse(reader, CTNumDataSource.type, (XmlOptions)null);
        }
        
        public static CTNumDataSource parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumDataSource)getTypeLoader().parse(reader, CTNumDataSource.type, xmlOptions);
        }
        
        public static CTNumDataSource parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumDataSource)getTypeLoader().parse(xmlStreamReader, CTNumDataSource.type, (XmlOptions)null);
        }
        
        public static CTNumDataSource parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumDataSource)getTypeLoader().parse(xmlStreamReader, CTNumDataSource.type, xmlOptions);
        }
        
        public static CTNumDataSource parse(final Node node) throws XmlException {
            return (CTNumDataSource)getTypeLoader().parse(node, CTNumDataSource.type, (XmlOptions)null);
        }
        
        public static CTNumDataSource parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumDataSource)getTypeLoader().parse(node, CTNumDataSource.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumDataSource parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumDataSource)getTypeLoader().parse(xmlInputStream, CTNumDataSource.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumDataSource parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumDataSource)getTypeLoader().parse(xmlInputStream, CTNumDataSource.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumDataSource.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumDataSource.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
