package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLvl extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLvl.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlvlf630type");
    
    CTDecimalNumber getStart();
    
    boolean isSetStart();
    
    void setStart(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewStart();
    
    void unsetStart();
    
    CTNumFmt getNumFmt();
    
    boolean isSetNumFmt();
    
    void setNumFmt(final CTNumFmt p0);
    
    CTNumFmt addNewNumFmt();
    
    void unsetNumFmt();
    
    CTDecimalNumber getLvlRestart();
    
    boolean isSetLvlRestart();
    
    void setLvlRestart(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewLvlRestart();
    
    void unsetLvlRestart();
    
    CTString getPStyle();
    
    boolean isSetPStyle();
    
    void setPStyle(final CTString p0);
    
    CTString addNewPStyle();
    
    void unsetPStyle();
    
    CTOnOff getIsLgl();
    
    boolean isSetIsLgl();
    
    void setIsLgl(final CTOnOff p0);
    
    CTOnOff addNewIsLgl();
    
    void unsetIsLgl();
    
    CTLevelSuffix getSuff();
    
    boolean isSetSuff();
    
    void setSuff(final CTLevelSuffix p0);
    
    CTLevelSuffix addNewSuff();
    
    void unsetSuff();
    
    CTLevelText getLvlText();
    
    boolean isSetLvlText();
    
    void setLvlText(final CTLevelText p0);
    
    CTLevelText addNewLvlText();
    
    void unsetLvlText();
    
    CTDecimalNumber getLvlPicBulletId();
    
    boolean isSetLvlPicBulletId();
    
    void setLvlPicBulletId(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewLvlPicBulletId();
    
    void unsetLvlPicBulletId();
    
    CTLvlLegacy getLegacy();
    
    boolean isSetLegacy();
    
    void setLegacy(final CTLvlLegacy p0);
    
    CTLvlLegacy addNewLegacy();
    
    void unsetLegacy();
    
    CTJc getLvlJc();
    
    boolean isSetLvlJc();
    
    void setLvlJc(final CTJc p0);
    
    CTJc addNewLvlJc();
    
    void unsetLvlJc();
    
    CTPPr getPPr();
    
    boolean isSetPPr();
    
    void setPPr(final CTPPr p0);
    
    CTPPr addNewPPr();
    
    void unsetPPr();
    
    CTRPr getRPr();
    
    boolean isSetRPr();
    
    void setRPr(final CTRPr p0);
    
    CTRPr addNewRPr();
    
    void unsetRPr();
    
    BigInteger getIlvl();
    
    STDecimalNumber xgetIlvl();
    
    void setIlvl(final BigInteger p0);
    
    void xsetIlvl(final STDecimalNumber p0);
    
    byte[] getTplc();
    
    STLongHexNumber xgetTplc();
    
    boolean isSetTplc();
    
    void setTplc(final byte[] p0);
    
    void xsetTplc(final STLongHexNumber p0);
    
    void unsetTplc();
    
    STOnOff.Enum getTentative();
    
    STOnOff xgetTentative();
    
    boolean isSetTentative();
    
    void setTentative(final STOnOff.Enum p0);
    
    void xsetTentative(final STOnOff p0);
    
    void unsetTentative();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLvl.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLvl newInstance() {
            return (CTLvl)getTypeLoader().newInstance(CTLvl.type, (XmlOptions)null);
        }
        
        public static CTLvl newInstance(final XmlOptions xmlOptions) {
            return (CTLvl)getTypeLoader().newInstance(CTLvl.type, xmlOptions);
        }
        
        public static CTLvl parse(final String s) throws XmlException {
            return (CTLvl)getTypeLoader().parse(s, CTLvl.type, (XmlOptions)null);
        }
        
        public static CTLvl parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLvl)getTypeLoader().parse(s, CTLvl.type, xmlOptions);
        }
        
        public static CTLvl parse(final File file) throws XmlException, IOException {
            return (CTLvl)getTypeLoader().parse(file, CTLvl.type, (XmlOptions)null);
        }
        
        public static CTLvl parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLvl)getTypeLoader().parse(file, CTLvl.type, xmlOptions);
        }
        
        public static CTLvl parse(final URL url) throws XmlException, IOException {
            return (CTLvl)getTypeLoader().parse(url, CTLvl.type, (XmlOptions)null);
        }
        
        public static CTLvl parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLvl)getTypeLoader().parse(url, CTLvl.type, xmlOptions);
        }
        
        public static CTLvl parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLvl)getTypeLoader().parse(inputStream, CTLvl.type, (XmlOptions)null);
        }
        
        public static CTLvl parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLvl)getTypeLoader().parse(inputStream, CTLvl.type, xmlOptions);
        }
        
        public static CTLvl parse(final Reader reader) throws XmlException, IOException {
            return (CTLvl)getTypeLoader().parse(reader, CTLvl.type, (XmlOptions)null);
        }
        
        public static CTLvl parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLvl)getTypeLoader().parse(reader, CTLvl.type, xmlOptions);
        }
        
        public static CTLvl parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLvl)getTypeLoader().parse(xmlStreamReader, CTLvl.type, (XmlOptions)null);
        }
        
        public static CTLvl parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLvl)getTypeLoader().parse(xmlStreamReader, CTLvl.type, xmlOptions);
        }
        
        public static CTLvl parse(final Node node) throws XmlException {
            return (CTLvl)getTypeLoader().parse(node, CTLvl.type, (XmlOptions)null);
        }
        
        public static CTLvl parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLvl)getTypeLoader().parse(node, CTLvl.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLvl parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLvl)getTypeLoader().parse(xmlInputStream, CTLvl.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLvl parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLvl)getTypeLoader().parse(xmlInputStream, CTLvl.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLvl.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLvl.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
