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

public interface CTTransform2D extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTransform2D.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttransform2d5deftype");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTransform2D.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTransform2D newInstance() {
            return (CTTransform2D)getTypeLoader().newInstance(CTTransform2D.type, (XmlOptions)null);
        }
        
        public static CTTransform2D newInstance(final XmlOptions xmlOptions) {
            return (CTTransform2D)getTypeLoader().newInstance(CTTransform2D.type, xmlOptions);
        }
        
        public static CTTransform2D parse(final String s) throws XmlException {
            return (CTTransform2D)getTypeLoader().parse(s, CTTransform2D.type, (XmlOptions)null);
        }
        
        public static CTTransform2D parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTransform2D)getTypeLoader().parse(s, CTTransform2D.type, xmlOptions);
        }
        
        public static CTTransform2D parse(final File file) throws XmlException, IOException {
            return (CTTransform2D)getTypeLoader().parse(file, CTTransform2D.type, (XmlOptions)null);
        }
        
        public static CTTransform2D parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTransform2D)getTypeLoader().parse(file, CTTransform2D.type, xmlOptions);
        }
        
        public static CTTransform2D parse(final URL url) throws XmlException, IOException {
            return (CTTransform2D)getTypeLoader().parse(url, CTTransform2D.type, (XmlOptions)null);
        }
        
        public static CTTransform2D parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTransform2D)getTypeLoader().parse(url, CTTransform2D.type, xmlOptions);
        }
        
        public static CTTransform2D parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTransform2D)getTypeLoader().parse(inputStream, CTTransform2D.type, (XmlOptions)null);
        }
        
        public static CTTransform2D parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTransform2D)getTypeLoader().parse(inputStream, CTTransform2D.type, xmlOptions);
        }
        
        public static CTTransform2D parse(final Reader reader) throws XmlException, IOException {
            return (CTTransform2D)getTypeLoader().parse(reader, CTTransform2D.type, (XmlOptions)null);
        }
        
        public static CTTransform2D parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTransform2D)getTypeLoader().parse(reader, CTTransform2D.type, xmlOptions);
        }
        
        public static CTTransform2D parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTransform2D)getTypeLoader().parse(xmlStreamReader, CTTransform2D.type, (XmlOptions)null);
        }
        
        public static CTTransform2D parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTransform2D)getTypeLoader().parse(xmlStreamReader, CTTransform2D.type, xmlOptions);
        }
        
        public static CTTransform2D parse(final Node node) throws XmlException {
            return (CTTransform2D)getTypeLoader().parse(node, CTTransform2D.type, (XmlOptions)null);
        }
        
        public static CTTransform2D parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTransform2D)getTypeLoader().parse(node, CTTransform2D.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTransform2D parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTransform2D)getTypeLoader().parse(xmlInputStream, CTTransform2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTransform2D parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTransform2D)getTypeLoader().parse(xmlInputStream, CTTransform2D.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTransform2D.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTransform2D.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
