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

public interface CTNumData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumdata4f16type");
    
    String getFormatCode();
    
    STXstring xgetFormatCode();
    
    boolean isSetFormatCode();
    
    void setFormatCode(final String p0);
    
    void xsetFormatCode(final STXstring p0);
    
    void unsetFormatCode();
    
    CTUnsignedInt getPtCount();
    
    boolean isSetPtCount();
    
    void setPtCount(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewPtCount();
    
    void unsetPtCount();
    
    List<CTNumVal> getPtList();
    
    @Deprecated
    CTNumVal[] getPtArray();
    
    CTNumVal getPtArray(final int p0);
    
    int sizeOfPtArray();
    
    void setPtArray(final CTNumVal[] p0);
    
    void setPtArray(final int p0, final CTNumVal p1);
    
    CTNumVal insertNewPt(final int p0);
    
    CTNumVal addNewPt();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumData newInstance() {
            return (CTNumData)getTypeLoader().newInstance(CTNumData.type, (XmlOptions)null);
        }
        
        public static CTNumData newInstance(final XmlOptions xmlOptions) {
            return (CTNumData)getTypeLoader().newInstance(CTNumData.type, xmlOptions);
        }
        
        public static CTNumData parse(final String s) throws XmlException {
            return (CTNumData)getTypeLoader().parse(s, CTNumData.type, (XmlOptions)null);
        }
        
        public static CTNumData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumData)getTypeLoader().parse(s, CTNumData.type, xmlOptions);
        }
        
        public static CTNumData parse(final File file) throws XmlException, IOException {
            return (CTNumData)getTypeLoader().parse(file, CTNumData.type, (XmlOptions)null);
        }
        
        public static CTNumData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumData)getTypeLoader().parse(file, CTNumData.type, xmlOptions);
        }
        
        public static CTNumData parse(final URL url) throws XmlException, IOException {
            return (CTNumData)getTypeLoader().parse(url, CTNumData.type, (XmlOptions)null);
        }
        
        public static CTNumData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumData)getTypeLoader().parse(url, CTNumData.type, xmlOptions);
        }
        
        public static CTNumData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumData)getTypeLoader().parse(inputStream, CTNumData.type, (XmlOptions)null);
        }
        
        public static CTNumData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumData)getTypeLoader().parse(inputStream, CTNumData.type, xmlOptions);
        }
        
        public static CTNumData parse(final Reader reader) throws XmlException, IOException {
            return (CTNumData)getTypeLoader().parse(reader, CTNumData.type, (XmlOptions)null);
        }
        
        public static CTNumData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumData)getTypeLoader().parse(reader, CTNumData.type, xmlOptions);
        }
        
        public static CTNumData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumData)getTypeLoader().parse(xmlStreamReader, CTNumData.type, (XmlOptions)null);
        }
        
        public static CTNumData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumData)getTypeLoader().parse(xmlStreamReader, CTNumData.type, xmlOptions);
        }
        
        public static CTNumData parse(final Node node) throws XmlException {
            return (CTNumData)getTypeLoader().parse(node, CTNumData.type, (XmlOptions)null);
        }
        
        public static CTNumData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumData)getTypeLoader().parse(node, CTNumData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumData)getTypeLoader().parse(xmlInputStream, CTNumData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumData)getTypeLoader().parse(xmlInputStream, CTNumData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
