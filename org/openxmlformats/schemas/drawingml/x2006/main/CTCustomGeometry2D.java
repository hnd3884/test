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

public interface CTCustomGeometry2D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCustomGeometry2D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcustomgeometry2dca70type");
    
    CTGeomGuideList getAvLst();
    
    boolean isSetAvLst();
    
    void setAvLst(final CTGeomGuideList p0);
    
    CTGeomGuideList addNewAvLst();
    
    void unsetAvLst();
    
    CTGeomGuideList getGdLst();
    
    boolean isSetGdLst();
    
    void setGdLst(final CTGeomGuideList p0);
    
    CTGeomGuideList addNewGdLst();
    
    void unsetGdLst();
    
    CTAdjustHandleList getAhLst();
    
    boolean isSetAhLst();
    
    void setAhLst(final CTAdjustHandleList p0);
    
    CTAdjustHandleList addNewAhLst();
    
    void unsetAhLst();
    
    CTConnectionSiteList getCxnLst();
    
    boolean isSetCxnLst();
    
    void setCxnLst(final CTConnectionSiteList p0);
    
    CTConnectionSiteList addNewCxnLst();
    
    void unsetCxnLst();
    
    CTGeomRect getRect();
    
    boolean isSetRect();
    
    void setRect(final CTGeomRect p0);
    
    CTGeomRect addNewRect();
    
    void unsetRect();
    
    CTPath2DList getPathLst();
    
    void setPathLst(final CTPath2DList p0);
    
    CTPath2DList addNewPathLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCustomGeometry2D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCustomGeometry2D newInstance() {
            return (CTCustomGeometry2D)getTypeLoader().newInstance(CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTCustomGeometry2D newInstance(final XmlOptions xmlOptions) {
            return (CTCustomGeometry2D)getTypeLoader().newInstance(CTCustomGeometry2D.type, xmlOptions);
        }
        
        public static CTCustomGeometry2D parse(final String s) throws XmlException {
            return (CTCustomGeometry2D)getTypeLoader().parse(s, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTCustomGeometry2D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCustomGeometry2D)getTypeLoader().parse(s, CTCustomGeometry2D.type, xmlOptions);
        }
        
        public static CTCustomGeometry2D parse(final File file) throws XmlException, IOException {
            return (CTCustomGeometry2D)getTypeLoader().parse(file, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTCustomGeometry2D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCustomGeometry2D)getTypeLoader().parse(file, CTCustomGeometry2D.type, xmlOptions);
        }
        
        public static CTCustomGeometry2D parse(final URL url) throws XmlException, IOException {
            return (CTCustomGeometry2D)getTypeLoader().parse(url, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTCustomGeometry2D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCustomGeometry2D)getTypeLoader().parse(url, CTCustomGeometry2D.type, xmlOptions);
        }
        
        public static CTCustomGeometry2D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCustomGeometry2D)getTypeLoader().parse(inputStream, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTCustomGeometry2D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCustomGeometry2D)getTypeLoader().parse(inputStream, CTCustomGeometry2D.type, xmlOptions);
        }
        
        public static CTCustomGeometry2D parse(final Reader reader) throws XmlException, IOException {
            return (CTCustomGeometry2D)getTypeLoader().parse(reader, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTCustomGeometry2D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCustomGeometry2D)getTypeLoader().parse(reader, CTCustomGeometry2D.type, xmlOptions);
        }
        
        public static CTCustomGeometry2D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCustomGeometry2D)getTypeLoader().parse(xmlStreamReader, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTCustomGeometry2D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCustomGeometry2D)getTypeLoader().parse(xmlStreamReader, CTCustomGeometry2D.type, xmlOptions);
        }
        
        public static CTCustomGeometry2D parse(final Node node) throws XmlException {
            return (CTCustomGeometry2D)getTypeLoader().parse(node, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        public static CTCustomGeometry2D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCustomGeometry2D)getTypeLoader().parse(node, CTCustomGeometry2D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCustomGeometry2D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCustomGeometry2D)getTypeLoader().parse(xmlInputStream, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCustomGeometry2D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCustomGeometry2D)getTypeLoader().parse(xmlInputStream, CTCustomGeometry2D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCustomGeometry2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCustomGeometry2D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
