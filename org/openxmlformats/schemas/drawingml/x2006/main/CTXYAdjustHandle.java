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

public interface CTXYAdjustHandle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTXYAdjustHandle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctxyadjusthandlefaf3type");
    
    CTAdjPoint2D getPos();
    
    void setPos(final CTAdjPoint2D p0);
    
    CTAdjPoint2D addNewPos();
    
    String getGdRefX();
    
    STGeomGuideName xgetGdRefX();
    
    boolean isSetGdRefX();
    
    void setGdRefX(final String p0);
    
    void xsetGdRefX(final STGeomGuideName p0);
    
    void unsetGdRefX();
    
    Object getMinX();
    
    STAdjCoordinate xgetMinX();
    
    boolean isSetMinX();
    
    void setMinX(final Object p0);
    
    void xsetMinX(final STAdjCoordinate p0);
    
    void unsetMinX();
    
    Object getMaxX();
    
    STAdjCoordinate xgetMaxX();
    
    boolean isSetMaxX();
    
    void setMaxX(final Object p0);
    
    void xsetMaxX(final STAdjCoordinate p0);
    
    void unsetMaxX();
    
    String getGdRefY();
    
    STGeomGuideName xgetGdRefY();
    
    boolean isSetGdRefY();
    
    void setGdRefY(final String p0);
    
    void xsetGdRefY(final STGeomGuideName p0);
    
    void unsetGdRefY();
    
    Object getMinY();
    
    STAdjCoordinate xgetMinY();
    
    boolean isSetMinY();
    
    void setMinY(final Object p0);
    
    void xsetMinY(final STAdjCoordinate p0);
    
    void unsetMinY();
    
    Object getMaxY();
    
    STAdjCoordinate xgetMaxY();
    
    boolean isSetMaxY();
    
    void setMaxY(final Object p0);
    
    void xsetMaxY(final STAdjCoordinate p0);
    
    void unsetMaxY();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTXYAdjustHandle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTXYAdjustHandle newInstance() {
            return (CTXYAdjustHandle)getTypeLoader().newInstance(CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTXYAdjustHandle newInstance(final XmlOptions xmlOptions) {
            return (CTXYAdjustHandle)getTypeLoader().newInstance(CTXYAdjustHandle.type, xmlOptions);
        }
        
        public static CTXYAdjustHandle parse(final String s) throws XmlException {
            return (CTXYAdjustHandle)getTypeLoader().parse(s, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTXYAdjustHandle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTXYAdjustHandle)getTypeLoader().parse(s, CTXYAdjustHandle.type, xmlOptions);
        }
        
        public static CTXYAdjustHandle parse(final File file) throws XmlException, IOException {
            return (CTXYAdjustHandle)getTypeLoader().parse(file, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTXYAdjustHandle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXYAdjustHandle)getTypeLoader().parse(file, CTXYAdjustHandle.type, xmlOptions);
        }
        
        public static CTXYAdjustHandle parse(final URL url) throws XmlException, IOException {
            return (CTXYAdjustHandle)getTypeLoader().parse(url, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTXYAdjustHandle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXYAdjustHandle)getTypeLoader().parse(url, CTXYAdjustHandle.type, xmlOptions);
        }
        
        public static CTXYAdjustHandle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTXYAdjustHandle)getTypeLoader().parse(inputStream, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTXYAdjustHandle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXYAdjustHandle)getTypeLoader().parse(inputStream, CTXYAdjustHandle.type, xmlOptions);
        }
        
        public static CTXYAdjustHandle parse(final Reader reader) throws XmlException, IOException {
            return (CTXYAdjustHandle)getTypeLoader().parse(reader, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTXYAdjustHandle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTXYAdjustHandle)getTypeLoader().parse(reader, CTXYAdjustHandle.type, xmlOptions);
        }
        
        public static CTXYAdjustHandle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTXYAdjustHandle)getTypeLoader().parse(xmlStreamReader, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTXYAdjustHandle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTXYAdjustHandle)getTypeLoader().parse(xmlStreamReader, CTXYAdjustHandle.type, xmlOptions);
        }
        
        public static CTXYAdjustHandle parse(final Node node) throws XmlException {
            return (CTXYAdjustHandle)getTypeLoader().parse(node, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTXYAdjustHandle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTXYAdjustHandle)getTypeLoader().parse(node, CTXYAdjustHandle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTXYAdjustHandle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTXYAdjustHandle)getTypeLoader().parse(xmlInputStream, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTXYAdjustHandle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTXYAdjustHandle)getTypeLoader().parse(xmlInputStream, CTXYAdjustHandle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXYAdjustHandle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTXYAdjustHandle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
