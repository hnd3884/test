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

public interface CTTileInfoProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTileInfoProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttileinfoproperties2featype");
    
    long getTx();
    
    STCoordinate xgetTx();
    
    boolean isSetTx();
    
    void setTx(final long p0);
    
    void xsetTx(final STCoordinate p0);
    
    void unsetTx();
    
    long getTy();
    
    STCoordinate xgetTy();
    
    boolean isSetTy();
    
    void setTy(final long p0);
    
    void xsetTy(final STCoordinate p0);
    
    void unsetTy();
    
    int getSx();
    
    STPercentage xgetSx();
    
    boolean isSetSx();
    
    void setSx(final int p0);
    
    void xsetSx(final STPercentage p0);
    
    void unsetSx();
    
    int getSy();
    
    STPercentage xgetSy();
    
    boolean isSetSy();
    
    void setSy(final int p0);
    
    void xsetSy(final STPercentage p0);
    
    void unsetSy();
    
    STTileFlipMode.Enum getFlip();
    
    STTileFlipMode xgetFlip();
    
    boolean isSetFlip();
    
    void setFlip(final STTileFlipMode.Enum p0);
    
    void xsetFlip(final STTileFlipMode p0);
    
    void unsetFlip();
    
    STRectAlignment.Enum getAlgn();
    
    STRectAlignment xgetAlgn();
    
    boolean isSetAlgn();
    
    void setAlgn(final STRectAlignment.Enum p0);
    
    void xsetAlgn(final STRectAlignment p0);
    
    void unsetAlgn();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTileInfoProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTileInfoProperties newInstance() {
            return (CTTileInfoProperties)getTypeLoader().newInstance(CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTTileInfoProperties newInstance(final XmlOptions xmlOptions) {
            return (CTTileInfoProperties)getTypeLoader().newInstance(CTTileInfoProperties.type, xmlOptions);
        }
        
        public static CTTileInfoProperties parse(final String s) throws XmlException {
            return (CTTileInfoProperties)getTypeLoader().parse(s, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTTileInfoProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTileInfoProperties)getTypeLoader().parse(s, CTTileInfoProperties.type, xmlOptions);
        }
        
        public static CTTileInfoProperties parse(final File file) throws XmlException, IOException {
            return (CTTileInfoProperties)getTypeLoader().parse(file, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTTileInfoProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTileInfoProperties)getTypeLoader().parse(file, CTTileInfoProperties.type, xmlOptions);
        }
        
        public static CTTileInfoProperties parse(final URL url) throws XmlException, IOException {
            return (CTTileInfoProperties)getTypeLoader().parse(url, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTTileInfoProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTileInfoProperties)getTypeLoader().parse(url, CTTileInfoProperties.type, xmlOptions);
        }
        
        public static CTTileInfoProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTileInfoProperties)getTypeLoader().parse(inputStream, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTTileInfoProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTileInfoProperties)getTypeLoader().parse(inputStream, CTTileInfoProperties.type, xmlOptions);
        }
        
        public static CTTileInfoProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTTileInfoProperties)getTypeLoader().parse(reader, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTTileInfoProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTileInfoProperties)getTypeLoader().parse(reader, CTTileInfoProperties.type, xmlOptions);
        }
        
        public static CTTileInfoProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTileInfoProperties)getTypeLoader().parse(xmlStreamReader, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTTileInfoProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTileInfoProperties)getTypeLoader().parse(xmlStreamReader, CTTileInfoProperties.type, xmlOptions);
        }
        
        public static CTTileInfoProperties parse(final Node node) throws XmlException {
            return (CTTileInfoProperties)getTypeLoader().parse(node, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        public static CTTileInfoProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTileInfoProperties)getTypeLoader().parse(node, CTTileInfoProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTileInfoProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTileInfoProperties)getTypeLoader().parse(xmlInputStream, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTileInfoProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTileInfoProperties)getTypeLoader().parse(xmlInputStream, CTTileInfoProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTileInfoProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTileInfoProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
