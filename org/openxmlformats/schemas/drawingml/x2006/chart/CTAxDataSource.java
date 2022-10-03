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

public interface CTAxDataSource extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAxDataSource.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctaxdatasource1440type");
    
    CTMultiLvlStrRef getMultiLvlStrRef();
    
    boolean isSetMultiLvlStrRef();
    
    void setMultiLvlStrRef(final CTMultiLvlStrRef p0);
    
    CTMultiLvlStrRef addNewMultiLvlStrRef();
    
    void unsetMultiLvlStrRef();
    
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
    
    CTStrRef getStrRef();
    
    boolean isSetStrRef();
    
    void setStrRef(final CTStrRef p0);
    
    CTStrRef addNewStrRef();
    
    void unsetStrRef();
    
    CTStrData getStrLit();
    
    boolean isSetStrLit();
    
    void setStrLit(final CTStrData p0);
    
    CTStrData addNewStrLit();
    
    void unsetStrLit();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAxDataSource.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAxDataSource newInstance() {
            return (CTAxDataSource)getTypeLoader().newInstance(CTAxDataSource.type, (XmlOptions)null);
        }
        
        public static CTAxDataSource newInstance(final XmlOptions xmlOptions) {
            return (CTAxDataSource)getTypeLoader().newInstance(CTAxDataSource.type, xmlOptions);
        }
        
        public static CTAxDataSource parse(final String s) throws XmlException {
            return (CTAxDataSource)getTypeLoader().parse(s, CTAxDataSource.type, (XmlOptions)null);
        }
        
        public static CTAxDataSource parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxDataSource)getTypeLoader().parse(s, CTAxDataSource.type, xmlOptions);
        }
        
        public static CTAxDataSource parse(final File file) throws XmlException, IOException {
            return (CTAxDataSource)getTypeLoader().parse(file, CTAxDataSource.type, (XmlOptions)null);
        }
        
        public static CTAxDataSource parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxDataSource)getTypeLoader().parse(file, CTAxDataSource.type, xmlOptions);
        }
        
        public static CTAxDataSource parse(final URL url) throws XmlException, IOException {
            return (CTAxDataSource)getTypeLoader().parse(url, CTAxDataSource.type, (XmlOptions)null);
        }
        
        public static CTAxDataSource parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxDataSource)getTypeLoader().parse(url, CTAxDataSource.type, xmlOptions);
        }
        
        public static CTAxDataSource parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAxDataSource)getTypeLoader().parse(inputStream, CTAxDataSource.type, (XmlOptions)null);
        }
        
        public static CTAxDataSource parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxDataSource)getTypeLoader().parse(inputStream, CTAxDataSource.type, xmlOptions);
        }
        
        public static CTAxDataSource parse(final Reader reader) throws XmlException, IOException {
            return (CTAxDataSource)getTypeLoader().parse(reader, CTAxDataSource.type, (XmlOptions)null);
        }
        
        public static CTAxDataSource parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAxDataSource)getTypeLoader().parse(reader, CTAxDataSource.type, xmlOptions);
        }
        
        public static CTAxDataSource parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAxDataSource)getTypeLoader().parse(xmlStreamReader, CTAxDataSource.type, (XmlOptions)null);
        }
        
        public static CTAxDataSource parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxDataSource)getTypeLoader().parse(xmlStreamReader, CTAxDataSource.type, xmlOptions);
        }
        
        public static CTAxDataSource parse(final Node node) throws XmlException {
            return (CTAxDataSource)getTypeLoader().parse(node, CTAxDataSource.type, (XmlOptions)null);
        }
        
        public static CTAxDataSource parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAxDataSource)getTypeLoader().parse(node, CTAxDataSource.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAxDataSource parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAxDataSource)getTypeLoader().parse(xmlInputStream, CTAxDataSource.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAxDataSource parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAxDataSource)getTypeLoader().parse(xmlInputStream, CTAxDataSource.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAxDataSource.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAxDataSource.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
