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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPresetGeometry2D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPresetGeometry2D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpresetgeometry2db1detype");
    
    CTGeomGuideList getAvLst();
    
    boolean isSetAvLst();
    
    void setAvLst(final CTGeomGuideList p0);
    
    CTGeomGuideList addNewAvLst();
    
    void unsetAvLst();
    
    STShapeType.Enum getPrst();
    
    STShapeType xgetPrst();
    
    void setPrst(final STShapeType.Enum p0);
    
    void xsetPrst(final STShapeType p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPresetGeometry2D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPresetGeometry2D newInstance() {
            return (CTPresetGeometry2D)getTypeLoader().newInstance(CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTPresetGeometry2D newInstance(final XmlOptions xmlOptions) {
            return (CTPresetGeometry2D)getTypeLoader().newInstance(CTPresetGeometry2D.type, xmlOptions);
        }
        
        public static CTPresetGeometry2D parse(final String s) throws XmlException {
            return (CTPresetGeometry2D)getTypeLoader().parse(s, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTPresetGeometry2D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPresetGeometry2D)getTypeLoader().parse(s, CTPresetGeometry2D.type, xmlOptions);
        }
        
        public static CTPresetGeometry2D parse(final File file) throws XmlException, IOException {
            return (CTPresetGeometry2D)getTypeLoader().parse(file, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTPresetGeometry2D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPresetGeometry2D)getTypeLoader().parse(file, CTPresetGeometry2D.type, xmlOptions);
        }
        
        public static CTPresetGeometry2D parse(final URL url) throws XmlException, IOException {
            return (CTPresetGeometry2D)getTypeLoader().parse(url, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTPresetGeometry2D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPresetGeometry2D)getTypeLoader().parse(url, CTPresetGeometry2D.type, xmlOptions);
        }
        
        public static CTPresetGeometry2D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPresetGeometry2D)getTypeLoader().parse(inputStream, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTPresetGeometry2D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPresetGeometry2D)getTypeLoader().parse(inputStream, CTPresetGeometry2D.type, xmlOptions);
        }
        
        public static CTPresetGeometry2D parse(final Reader reader) throws XmlException, IOException {
            return (CTPresetGeometry2D)getTypeLoader().parse(reader, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTPresetGeometry2D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPresetGeometry2D)getTypeLoader().parse(reader, CTPresetGeometry2D.type, xmlOptions);
        }
        
        public static CTPresetGeometry2D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPresetGeometry2D)getTypeLoader().parse(xmlStreamReader, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTPresetGeometry2D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPresetGeometry2D)getTypeLoader().parse(xmlStreamReader, CTPresetGeometry2D.type, xmlOptions);
        }
        
        public static CTPresetGeometry2D parse(final Node node) throws XmlException {
            return (CTPresetGeometry2D)getTypeLoader().parse(node, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTPresetGeometry2D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPresetGeometry2D)getTypeLoader().parse(node, CTPresetGeometry2D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPresetGeometry2D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPresetGeometry2D)getTypeLoader().parse(xmlInputStream, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPresetGeometry2D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPresetGeometry2D)getTypeLoader().parse(xmlInputStream, CTPresetGeometry2D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPresetGeometry2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPresetGeometry2D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
