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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTGroupTransform2D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGroupTransform2D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgrouptransform2d411atype");
    
    CTPoint2D getOff();
    
    boolean isSetOff();
    
    void setOff(final CTPoint2D p0);
    
    CTPoint2D addNewOff();
    
    void unsetOff();
    
    CTPositiveSize2D getExt();
    
    boolean isSetExt();
    
    void setExt(final CTPositiveSize2D p0);
    
    CTPositiveSize2D addNewExt();
    
    void unsetExt();
    
    CTPoint2D getChOff();
    
    boolean isSetChOff();
    
    void setChOff(final CTPoint2D p0);
    
    CTPoint2D addNewChOff();
    
    void unsetChOff();
    
    CTPositiveSize2D getChExt();
    
    boolean isSetChExt();
    
    void setChExt(final CTPositiveSize2D p0);
    
    CTPositiveSize2D addNewChExt();
    
    void unsetChExt();
    
    int getRot();
    
    STAngle xgetRot();
    
    boolean isSetRot();
    
    void setRot(final int p0);
    
    void xsetRot(final STAngle p0);
    
    void unsetRot();
    
    boolean getFlipH();
    
    XmlBoolean xgetFlipH();
    
    boolean isSetFlipH();
    
    void setFlipH(final boolean p0);
    
    void xsetFlipH(final XmlBoolean p0);
    
    void unsetFlipH();
    
    boolean getFlipV();
    
    XmlBoolean xgetFlipV();
    
    boolean isSetFlipV();
    
    void setFlipV(final boolean p0);
    
    void xsetFlipV(final XmlBoolean p0);
    
    void unsetFlipV();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGroupTransform2D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGroupTransform2D newInstance() {
            return (CTGroupTransform2D)getTypeLoader().newInstance(CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        public static CTGroupTransform2D newInstance(final XmlOptions xmlOptions) {
            return (CTGroupTransform2D)getTypeLoader().newInstance(CTGroupTransform2D.type, xmlOptions);
        }
        
        public static CTGroupTransform2D parse(final String s) throws XmlException {
            return (CTGroupTransform2D)getTypeLoader().parse(s, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        public static CTGroupTransform2D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupTransform2D)getTypeLoader().parse(s, CTGroupTransform2D.type, xmlOptions);
        }
        
        public static CTGroupTransform2D parse(final File file) throws XmlException, IOException {
            return (CTGroupTransform2D)getTypeLoader().parse(file, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        public static CTGroupTransform2D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupTransform2D)getTypeLoader().parse(file, CTGroupTransform2D.type, xmlOptions);
        }
        
        public static CTGroupTransform2D parse(final URL url) throws XmlException, IOException {
            return (CTGroupTransform2D)getTypeLoader().parse(url, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        public static CTGroupTransform2D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupTransform2D)getTypeLoader().parse(url, CTGroupTransform2D.type, xmlOptions);
        }
        
        public static CTGroupTransform2D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGroupTransform2D)getTypeLoader().parse(inputStream, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        public static CTGroupTransform2D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupTransform2D)getTypeLoader().parse(inputStream, CTGroupTransform2D.type, xmlOptions);
        }
        
        public static CTGroupTransform2D parse(final Reader reader) throws XmlException, IOException {
            return (CTGroupTransform2D)getTypeLoader().parse(reader, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        public static CTGroupTransform2D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupTransform2D)getTypeLoader().parse(reader, CTGroupTransform2D.type, xmlOptions);
        }
        
        public static CTGroupTransform2D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGroupTransform2D)getTypeLoader().parse(xmlStreamReader, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        public static CTGroupTransform2D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupTransform2D)getTypeLoader().parse(xmlStreamReader, CTGroupTransform2D.type, xmlOptions);
        }
        
        public static CTGroupTransform2D parse(final Node node) throws XmlException {
            return (CTGroupTransform2D)getTypeLoader().parse(node, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        public static CTGroupTransform2D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupTransform2D)getTypeLoader().parse(node, CTGroupTransform2D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGroupTransform2D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGroupTransform2D)getTypeLoader().parse(xmlInputStream, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGroupTransform2D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGroupTransform2D)getTypeLoader().parse(xmlInputStream, CTGroupTransform2D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupTransform2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupTransform2D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
