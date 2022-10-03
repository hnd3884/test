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

public interface CTTableStyleTextStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableStyleTextStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablestyletextstylec932type");
    
    CTFontCollection getFont();
    
    boolean isSetFont();
    
    void setFont(final CTFontCollection p0);
    
    CTFontCollection addNewFont();
    
    void unsetFont();
    
    CTFontReference getFontRef();
    
    boolean isSetFontRef();
    
    void setFontRef(final CTFontReference p0);
    
    CTFontReference addNewFontRef();
    
    void unsetFontRef();
    
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
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    STOnOffStyleType.Enum getB();
    
    STOnOffStyleType xgetB();
    
    boolean isSetB();
    
    void setB(final STOnOffStyleType.Enum p0);
    
    void xsetB(final STOnOffStyleType p0);
    
    void unsetB();
    
    STOnOffStyleType.Enum getI();
    
    STOnOffStyleType xgetI();
    
    boolean isSetI();
    
    void setI(final STOnOffStyleType.Enum p0);
    
    void xsetI(final STOnOffStyleType p0);
    
    void unsetI();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableStyleTextStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableStyleTextStyle newInstance() {
            return (CTTableStyleTextStyle)getTypeLoader().newInstance(CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleTextStyle newInstance(final XmlOptions xmlOptions) {
            return (CTTableStyleTextStyle)getTypeLoader().newInstance(CTTableStyleTextStyle.type, xmlOptions);
        }
        
        public static CTTableStyleTextStyle parse(final String s) throws XmlException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(s, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleTextStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(s, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        public static CTTableStyleTextStyle parse(final File file) throws XmlException, IOException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(file, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleTextStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(file, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        public static CTTableStyleTextStyle parse(final URL url) throws XmlException, IOException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(url, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleTextStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(url, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        public static CTTableStyleTextStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(inputStream, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleTextStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(inputStream, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        public static CTTableStyleTextStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(reader, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleTextStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(reader, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        public static CTTableStyleTextStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(xmlStreamReader, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleTextStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(xmlStreamReader, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        public static CTTableStyleTextStyle parse(final Node node) throws XmlException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(node, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        public static CTTableStyleTextStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(node, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableStyleTextStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(xmlInputStream, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableStyleTextStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableStyleTextStyle)getTypeLoader().parse(xmlInputStream, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleTextStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableStyleTextStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
