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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTGeomGuideList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGeomGuideList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgeomguidelist364ftype");
    
    List<CTGeomGuide> getGdList();
    
    @Deprecated
    CTGeomGuide[] getGdArray();
    
    CTGeomGuide getGdArray(final int p0);
    
    int sizeOfGdArray();
    
    void setGdArray(final CTGeomGuide[] p0);
    
    void setGdArray(final int p0, final CTGeomGuide p1);
    
    CTGeomGuide insertNewGd(final int p0);
    
    CTGeomGuide addNewGd();
    
    void removeGd(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGeomGuideList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGeomGuideList newInstance() {
            return (CTGeomGuideList)getTypeLoader().newInstance(CTGeomGuideList.type, (XmlOptions)null);
        }
        
        public static CTGeomGuideList newInstance(final XmlOptions xmlOptions) {
            return (CTGeomGuideList)getTypeLoader().newInstance(CTGeomGuideList.type, xmlOptions);
        }
        
        public static CTGeomGuideList parse(final String s) throws XmlException {
            return (CTGeomGuideList)getTypeLoader().parse(s, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        public static CTGeomGuideList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGeomGuideList)getTypeLoader().parse(s, CTGeomGuideList.type, xmlOptions);
        }
        
        public static CTGeomGuideList parse(final File file) throws XmlException, IOException {
            return (CTGeomGuideList)getTypeLoader().parse(file, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        public static CTGeomGuideList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGeomGuideList)getTypeLoader().parse(file, CTGeomGuideList.type, xmlOptions);
        }
        
        public static CTGeomGuideList parse(final URL url) throws XmlException, IOException {
            return (CTGeomGuideList)getTypeLoader().parse(url, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        public static CTGeomGuideList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGeomGuideList)getTypeLoader().parse(url, CTGeomGuideList.type, xmlOptions);
        }
        
        public static CTGeomGuideList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGeomGuideList)getTypeLoader().parse(inputStream, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        public static CTGeomGuideList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGeomGuideList)getTypeLoader().parse(inputStream, CTGeomGuideList.type, xmlOptions);
        }
        
        public static CTGeomGuideList parse(final Reader reader) throws XmlException, IOException {
            return (CTGeomGuideList)getTypeLoader().parse(reader, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        public static CTGeomGuideList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGeomGuideList)getTypeLoader().parse(reader, CTGeomGuideList.type, xmlOptions);
        }
        
        public static CTGeomGuideList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGeomGuideList)getTypeLoader().parse(xmlStreamReader, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        public static CTGeomGuideList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGeomGuideList)getTypeLoader().parse(xmlStreamReader, CTGeomGuideList.type, xmlOptions);
        }
        
        public static CTGeomGuideList parse(final Node node) throws XmlException {
            return (CTGeomGuideList)getTypeLoader().parse(node, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        public static CTGeomGuideList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGeomGuideList)getTypeLoader().parse(node, CTGeomGuideList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGeomGuideList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGeomGuideList)getTypeLoader().parse(xmlInputStream, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGeomGuideList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGeomGuideList)getTypeLoader().parse(xmlInputStream, CTGeomGuideList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGeomGuideList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGeomGuideList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
