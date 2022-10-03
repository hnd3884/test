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

public interface CTOuterShadowEffect extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOuterShadowEffect.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctoutershadoweffect7b5dtype");
    
    CTScRgbColor getScrgbClr();
    
    boolean isSetScrgbClr();
    
    void setScrgbClr(final CTScRgbColor p0);
    
    CTScRgbColor addNewScrgbClr();
    
    void unsetScrgbClr();
    
    CTSRgbColor getSrgbClr();
    
    boolean isSetSrgbClr();
    
    void setSrgbClr(final CTSRgbColor p0);
    
    CTSRgbColor addNewSrgbClr();
    
    void unsetSrgbClr();
    
    CTHslColor getHslClr();
    
    boolean isSetHslClr();
    
    void setHslClr(final CTHslColor p0);
    
    CTHslColor addNewHslClr();
    
    void unsetHslClr();
    
    CTSystemColor getSysClr();
    
    boolean isSetSysClr();
    
    void setSysClr(final CTSystemColor p0);
    
    CTSystemColor addNewSysClr();
    
    void unsetSysClr();
    
    CTSchemeColor getSchemeClr();
    
    boolean isSetSchemeClr();
    
    void setSchemeClr(final CTSchemeColor p0);
    
    CTSchemeColor addNewSchemeClr();
    
    void unsetSchemeClr();
    
    CTPresetColor getPrstClr();
    
    boolean isSetPrstClr();
    
    void setPrstClr(final CTPresetColor p0);
    
    CTPresetColor addNewPrstClr();
    
    void unsetPrstClr();
    
    long getBlurRad();
    
    STPositiveCoordinate xgetBlurRad();
    
    boolean isSetBlurRad();
    
    void setBlurRad(final long p0);
    
    void xsetBlurRad(final STPositiveCoordinate p0);
    
    void unsetBlurRad();
    
    long getDist();
    
    STPositiveCoordinate xgetDist();
    
    boolean isSetDist();
    
    void setDist(final long p0);
    
    void xsetDist(final STPositiveCoordinate p0);
    
    void unsetDist();
    
    int getDir();
    
    STPositiveFixedAngle xgetDir();
    
    boolean isSetDir();
    
    void setDir(final int p0);
    
    void xsetDir(final STPositiveFixedAngle p0);
    
    void unsetDir();
    
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
    
    int getKx();
    
    STFixedAngle xgetKx();
    
    boolean isSetKx();
    
    void setKx(final int p0);
    
    void xsetKx(final STFixedAngle p0);
    
    void unsetKx();
    
    int getKy();
    
    STFixedAngle xgetKy();
    
    boolean isSetKy();
    
    void setKy(final int p0);
    
    void xsetKy(final STFixedAngle p0);
    
    void unsetKy();
    
    STRectAlignment.Enum getAlgn();
    
    STRectAlignment xgetAlgn();
    
    boolean isSetAlgn();
    
    void setAlgn(final STRectAlignment.Enum p0);
    
    void xsetAlgn(final STRectAlignment p0);
    
    void unsetAlgn();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOuterShadowEffect.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOuterShadowEffect newInstance() {
            return (CTOuterShadowEffect)getTypeLoader().newInstance(CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        public static CTOuterShadowEffect newInstance(final XmlOptions xmlOptions) {
            return (CTOuterShadowEffect)getTypeLoader().newInstance(CTOuterShadowEffect.type, xmlOptions);
        }
        
        public static CTOuterShadowEffect parse(final String s) throws XmlException {
            return (CTOuterShadowEffect)getTypeLoader().parse(s, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        public static CTOuterShadowEffect parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOuterShadowEffect)getTypeLoader().parse(s, CTOuterShadowEffect.type, xmlOptions);
        }
        
        public static CTOuterShadowEffect parse(final File file) throws XmlException, IOException {
            return (CTOuterShadowEffect)getTypeLoader().parse(file, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        public static CTOuterShadowEffect parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOuterShadowEffect)getTypeLoader().parse(file, CTOuterShadowEffect.type, xmlOptions);
        }
        
        public static CTOuterShadowEffect parse(final URL url) throws XmlException, IOException {
            return (CTOuterShadowEffect)getTypeLoader().parse(url, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        public static CTOuterShadowEffect parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOuterShadowEffect)getTypeLoader().parse(url, CTOuterShadowEffect.type, xmlOptions);
        }
        
        public static CTOuterShadowEffect parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOuterShadowEffect)getTypeLoader().parse(inputStream, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        public static CTOuterShadowEffect parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOuterShadowEffect)getTypeLoader().parse(inputStream, CTOuterShadowEffect.type, xmlOptions);
        }
        
        public static CTOuterShadowEffect parse(final Reader reader) throws XmlException, IOException {
            return (CTOuterShadowEffect)getTypeLoader().parse(reader, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        public static CTOuterShadowEffect parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOuterShadowEffect)getTypeLoader().parse(reader, CTOuterShadowEffect.type, xmlOptions);
        }
        
        public static CTOuterShadowEffect parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOuterShadowEffect)getTypeLoader().parse(xmlStreamReader, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        public static CTOuterShadowEffect parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOuterShadowEffect)getTypeLoader().parse(xmlStreamReader, CTOuterShadowEffect.type, xmlOptions);
        }
        
        public static CTOuterShadowEffect parse(final Node node) throws XmlException {
            return (CTOuterShadowEffect)getTypeLoader().parse(node, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        public static CTOuterShadowEffect parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOuterShadowEffect)getTypeLoader().parse(node, CTOuterShadowEffect.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOuterShadowEffect parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOuterShadowEffect)getTypeLoader().parse(xmlInputStream, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOuterShadowEffect parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOuterShadowEffect)getTypeLoader().parse(xmlInputStream, CTOuterShadowEffect.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOuterShadowEffect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOuterShadowEffect.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
