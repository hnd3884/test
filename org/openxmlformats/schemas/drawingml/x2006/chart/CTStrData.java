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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTStrData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStrData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstrdatad58btype");
    
    CTUnsignedInt getPtCount();
    
    boolean isSetPtCount();
    
    void setPtCount(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewPtCount();
    
    void unsetPtCount();
    
    List<CTStrVal> getPtList();
    
    @Deprecated
    CTStrVal[] getPtArray();
    
    CTStrVal getPtArray(final int p0);
    
    int sizeOfPtArray();
    
    void setPtArray(final CTStrVal[] p0);
    
    void setPtArray(final int p0, final CTStrVal p1);
    
    CTStrVal insertNewPt(final int p0);
    
    CTStrVal addNewPt();
    
    void removePt(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStrData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStrData newInstance() {
            return (CTStrData)getTypeLoader().newInstance(CTStrData.type, (XmlOptions)null);
        }
        
        public static CTStrData newInstance(final XmlOptions xmlOptions) {
            return (CTStrData)getTypeLoader().newInstance(CTStrData.type, xmlOptions);
        }
        
        public static CTStrData parse(final String s) throws XmlException {
            return (CTStrData)getTypeLoader().parse(s, CTStrData.type, (XmlOptions)null);
        }
        
        public static CTStrData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrData)getTypeLoader().parse(s, CTStrData.type, xmlOptions);
        }
        
        public static CTStrData parse(final File file) throws XmlException, IOException {
            return (CTStrData)getTypeLoader().parse(file, CTStrData.type, (XmlOptions)null);
        }
        
        public static CTStrData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrData)getTypeLoader().parse(file, CTStrData.type, xmlOptions);
        }
        
        public static CTStrData parse(final URL url) throws XmlException, IOException {
            return (CTStrData)getTypeLoader().parse(url, CTStrData.type, (XmlOptions)null);
        }
        
        public static CTStrData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrData)getTypeLoader().parse(url, CTStrData.type, xmlOptions);
        }
        
        public static CTStrData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStrData)getTypeLoader().parse(inputStream, CTStrData.type, (XmlOptions)null);
        }
        
        public static CTStrData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrData)getTypeLoader().parse(inputStream, CTStrData.type, xmlOptions);
        }
        
        public static CTStrData parse(final Reader reader) throws XmlException, IOException {
            return (CTStrData)getTypeLoader().parse(reader, CTStrData.type, (XmlOptions)null);
        }
        
        public static CTStrData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStrData)getTypeLoader().parse(reader, CTStrData.type, xmlOptions);
        }
        
        public static CTStrData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStrData)getTypeLoader().parse(xmlStreamReader, CTStrData.type, (XmlOptions)null);
        }
        
        public static CTStrData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrData)getTypeLoader().parse(xmlStreamReader, CTStrData.type, xmlOptions);
        }
        
        public static CTStrData parse(final Node node) throws XmlException {
            return (CTStrData)getTypeLoader().parse(node, CTStrData.type, (XmlOptions)null);
        }
        
        public static CTStrData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStrData)getTypeLoader().parse(node, CTStrData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStrData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStrData)getTypeLoader().parse(xmlInputStream, CTStrData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStrData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStrData)getTypeLoader().parse(xmlInputStream, CTStrData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStrData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStrData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
