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

public interface CTPolarAdjustHandle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPolarAdjustHandle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpolaradjusthandled0a6type");
    
    CTAdjPoint2D getPos();
    
    void setPos(final CTAdjPoint2D p0);
    
    CTAdjPoint2D addNewPos();
    
    String getGdRefR();
    
    STGeomGuideName xgetGdRefR();
    
    boolean isSetGdRefR();
    
    void setGdRefR(final String p0);
    
    void xsetGdRefR(final STGeomGuideName p0);
    
    void unsetGdRefR();
    
    Object getMinR();
    
    STAdjCoordinate xgetMinR();
    
    boolean isSetMinR();
    
    void setMinR(final Object p0);
    
    void xsetMinR(final STAdjCoordinate p0);
    
    void unsetMinR();
    
    Object getMaxR();
    
    STAdjCoordinate xgetMaxR();
    
    boolean isSetMaxR();
    
    void setMaxR(final Object p0);
    
    void xsetMaxR(final STAdjCoordinate p0);
    
    void unsetMaxR();
    
    String getGdRefAng();
    
    STGeomGuideName xgetGdRefAng();
    
    boolean isSetGdRefAng();
    
    void setGdRefAng(final String p0);
    
    void xsetGdRefAng(final STGeomGuideName p0);
    
    void unsetGdRefAng();
    
    Object getMinAng();
    
    STAdjAngle xgetMinAng();
    
    boolean isSetMinAng();
    
    void setMinAng(final Object p0);
    
    void xsetMinAng(final STAdjAngle p0);
    
    void unsetMinAng();
    
    Object getMaxAng();
    
    STAdjAngle xgetMaxAng();
    
    boolean isSetMaxAng();
    
    void setMaxAng(final Object p0);
    
    void xsetMaxAng(final STAdjAngle p0);
    
    void unsetMaxAng();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPolarAdjustHandle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPolarAdjustHandle newInstance() {
            return (CTPolarAdjustHandle)getTypeLoader().newInstance(CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTPolarAdjustHandle newInstance(final XmlOptions xmlOptions) {
            return (CTPolarAdjustHandle)getTypeLoader().newInstance(CTPolarAdjustHandle.type, xmlOptions);
        }
        
        public static CTPolarAdjustHandle parse(final String s) throws XmlException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(s, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTPolarAdjustHandle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(s, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        public static CTPolarAdjustHandle parse(final File file) throws XmlException, IOException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(file, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTPolarAdjustHandle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(file, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        public static CTPolarAdjustHandle parse(final URL url) throws XmlException, IOException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(url, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTPolarAdjustHandle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(url, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        public static CTPolarAdjustHandle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(inputStream, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTPolarAdjustHandle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(inputStream, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        public static CTPolarAdjustHandle parse(final Reader reader) throws XmlException, IOException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(reader, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTPolarAdjustHandle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(reader, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        public static CTPolarAdjustHandle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(xmlStreamReader, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTPolarAdjustHandle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(xmlStreamReader, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        public static CTPolarAdjustHandle parse(final Node node) throws XmlException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(node, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        public static CTPolarAdjustHandle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(node, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPolarAdjustHandle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(xmlInputStream, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPolarAdjustHandle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPolarAdjustHandle)getTypeLoader().parse(xmlInputStream, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPolarAdjustHandle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPolarAdjustHandle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
