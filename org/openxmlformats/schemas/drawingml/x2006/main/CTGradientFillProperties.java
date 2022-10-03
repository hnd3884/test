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

public interface CTGradientFillProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGradientFillProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgradientfillproperties81c1type");
    
    CTGradientStopList getGsLst();
    
    boolean isSetGsLst();
    
    void setGsLst(final CTGradientStopList p0);
    
    CTGradientStopList addNewGsLst();
    
    void unsetGsLst();
    
    CTLinearShadeProperties getLin();
    
    boolean isSetLin();
    
    void setLin(final CTLinearShadeProperties p0);
    
    CTLinearShadeProperties addNewLin();
    
    void unsetLin();
    
    CTPathShadeProperties getPath();
    
    boolean isSetPath();
    
    void setPath(final CTPathShadeProperties p0);
    
    CTPathShadeProperties addNewPath();
    
    void unsetPath();
    
    CTRelativeRect getTileRect();
    
    boolean isSetTileRect();
    
    void setTileRect(final CTRelativeRect p0);
    
    CTRelativeRect addNewTileRect();
    
    void unsetTileRect();
    
    STTileFlipMode.Enum getFlip();
    
    STTileFlipMode xgetFlip();
    
    boolean isSetFlip();
    
    void setFlip(final STTileFlipMode.Enum p0);
    
    void xsetFlip(final STTileFlipMode p0);
    
    void unsetFlip();
    
    boolean getRotWithShape();
    
    XmlBoolean xgetRotWithShape();
    
    boolean isSetRotWithShape();
    
    void setRotWithShape(final boolean p0);
    
    void xsetRotWithShape(final XmlBoolean p0);
    
    void unsetRotWithShape();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGradientFillProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGradientFillProperties newInstance() {
            return (CTGradientFillProperties)getTypeLoader().newInstance(CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGradientFillProperties newInstance(final XmlOptions xmlOptions) {
            return (CTGradientFillProperties)getTypeLoader().newInstance(CTGradientFillProperties.type, xmlOptions);
        }
        
        public static CTGradientFillProperties parse(final String s) throws XmlException {
            return (CTGradientFillProperties)getTypeLoader().parse(s, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGradientFillProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientFillProperties)getTypeLoader().parse(s, CTGradientFillProperties.type, xmlOptions);
        }
        
        public static CTGradientFillProperties parse(final File file) throws XmlException, IOException {
            return (CTGradientFillProperties)getTypeLoader().parse(file, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGradientFillProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientFillProperties)getTypeLoader().parse(file, CTGradientFillProperties.type, xmlOptions);
        }
        
        public static CTGradientFillProperties parse(final URL url) throws XmlException, IOException {
            return (CTGradientFillProperties)getTypeLoader().parse(url, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGradientFillProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientFillProperties)getTypeLoader().parse(url, CTGradientFillProperties.type, xmlOptions);
        }
        
        public static CTGradientFillProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGradientFillProperties)getTypeLoader().parse(inputStream, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGradientFillProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientFillProperties)getTypeLoader().parse(inputStream, CTGradientFillProperties.type, xmlOptions);
        }
        
        public static CTGradientFillProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTGradientFillProperties)getTypeLoader().parse(reader, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGradientFillProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientFillProperties)getTypeLoader().parse(reader, CTGradientFillProperties.type, xmlOptions);
        }
        
        public static CTGradientFillProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGradientFillProperties)getTypeLoader().parse(xmlStreamReader, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGradientFillProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientFillProperties)getTypeLoader().parse(xmlStreamReader, CTGradientFillProperties.type, xmlOptions);
        }
        
        public static CTGradientFillProperties parse(final Node node) throws XmlException {
            return (CTGradientFillProperties)getTypeLoader().parse(node, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        public static CTGradientFillProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientFillProperties)getTypeLoader().parse(node, CTGradientFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGradientFillProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGradientFillProperties)getTypeLoader().parse(xmlInputStream, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGradientFillProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGradientFillProperties)getTypeLoader().parse(xmlInputStream, CTGradientFillProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGradientFillProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGradientFillProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
