package com.microsoft.schemas.vml;

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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.XmlDecimal;
import java.math.BigDecimal;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFill extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFill.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfillb241type");
    
    com.microsoft.schemas.office.office.CTFill getFill();
    
    boolean isSetFill();
    
    void setFill(final com.microsoft.schemas.office.office.CTFill p0);
    
    com.microsoft.schemas.office.office.CTFill addNewFill();
    
    void unsetFill();
    
    String getId();
    
    XmlString xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlString p0);
    
    void unsetId();
    
    STFillType.Enum getType();
    
    STFillType xgetType();
    
    boolean isSetType();
    
    void setType(final STFillType.Enum p0);
    
    void xsetType(final STFillType p0);
    
    void unsetType();
    
    STTrueFalse.Enum getOn();
    
    STTrueFalse xgetOn();
    
    boolean isSetOn();
    
    void setOn(final STTrueFalse.Enum p0);
    
    void xsetOn(final STTrueFalse p0);
    
    void unsetOn();
    
    String getColor();
    
    STColorType xgetColor();
    
    boolean isSetColor();
    
    void setColor(final String p0);
    
    void xsetColor(final STColorType p0);
    
    void unsetColor();
    
    String getOpacity();
    
    XmlString xgetOpacity();
    
    boolean isSetOpacity();
    
    void setOpacity(final String p0);
    
    void xsetOpacity(final XmlString p0);
    
    void unsetOpacity();
    
    String getColor2();
    
    STColorType xgetColor2();
    
    boolean isSetColor2();
    
    void setColor2(final String p0);
    
    void xsetColor2(final STColorType p0);
    
    void unsetColor2();
    
    String getSrc();
    
    XmlString xgetSrc();
    
    boolean isSetSrc();
    
    void setSrc(final String p0);
    
    void xsetSrc(final XmlString p0);
    
    void unsetSrc();
    
    String getHref();
    
    XmlString xgetHref();
    
    boolean isSetHref();
    
    void setHref(final String p0);
    
    void xsetHref(final XmlString p0);
    
    void unsetHref();
    
    String getAlthref();
    
    XmlString xgetAlthref();
    
    boolean isSetAlthref();
    
    void setAlthref(final String p0);
    
    void xsetAlthref(final XmlString p0);
    
    void unsetAlthref();
    
    String getSize();
    
    XmlString xgetSize();
    
    boolean isSetSize();
    
    void setSize(final String p0);
    
    void xsetSize(final XmlString p0);
    
    void unsetSize();
    
    String getOrigin();
    
    XmlString xgetOrigin();
    
    boolean isSetOrigin();
    
    void setOrigin(final String p0);
    
    void xsetOrigin(final XmlString p0);
    
    void unsetOrigin();
    
    String getPosition();
    
    XmlString xgetPosition();
    
    boolean isSetPosition();
    
    void setPosition(final String p0);
    
    void xsetPosition(final XmlString p0);
    
    void unsetPosition();
    
    STImageAspect.Enum getAspect();
    
    STImageAspect xgetAspect();
    
    boolean isSetAspect();
    
    void setAspect(final STImageAspect.Enum p0);
    
    void xsetAspect(final STImageAspect p0);
    
    void unsetAspect();
    
    String getColors();
    
    XmlString xgetColors();
    
    boolean isSetColors();
    
    void setColors(final String p0);
    
    void xsetColors(final XmlString p0);
    
    void unsetColors();
    
    BigDecimal getAngle();
    
    XmlDecimal xgetAngle();
    
    boolean isSetAngle();
    
    void setAngle(final BigDecimal p0);
    
    void xsetAngle(final XmlDecimal p0);
    
    void unsetAngle();
    
    STTrueFalse.Enum getAlignshape();
    
    STTrueFalse xgetAlignshape();
    
    boolean isSetAlignshape();
    
    void setAlignshape(final STTrueFalse.Enum p0);
    
    void xsetAlignshape(final STTrueFalse p0);
    
    void unsetAlignshape();
    
    String getFocus();
    
    XmlString xgetFocus();
    
    boolean isSetFocus();
    
    void setFocus(final String p0);
    
    void xsetFocus(final XmlString p0);
    
    void unsetFocus();
    
    String getFocussize();
    
    XmlString xgetFocussize();
    
    boolean isSetFocussize();
    
    void setFocussize(final String p0);
    
    void xsetFocussize(final XmlString p0);
    
    void unsetFocussize();
    
    String getFocusposition();
    
    XmlString xgetFocusposition();
    
    boolean isSetFocusposition();
    
    void setFocusposition(final String p0);
    
    void xsetFocusposition(final XmlString p0);
    
    void unsetFocusposition();
    
    STFillMethod.Enum getMethod();
    
    STFillMethod xgetMethod();
    
    boolean isSetMethod();
    
    void setMethod(final STFillMethod.Enum p0);
    
    void xsetMethod(final STFillMethod p0);
    
    void unsetMethod();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getDetectmouseclick();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetDetectmouseclick();
    
    boolean isSetDetectmouseclick();
    
    void setDetectmouseclick(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetDetectmouseclick(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetDetectmouseclick();
    
    String getTitle();
    
    XmlString xgetTitle();
    
    boolean isSetTitle();
    
    void setTitle(final String p0);
    
    void xsetTitle(final XmlString p0);
    
    void unsetTitle();
    
    String getOpacity2();
    
    XmlString xgetOpacity2();
    
    boolean isSetOpacity2();
    
    void setOpacity2(final String p0);
    
    void xsetOpacity2(final XmlString p0);
    
    void unsetOpacity2();
    
    STTrueFalse.Enum getRecolor();
    
    STTrueFalse xgetRecolor();
    
    boolean isSetRecolor();
    
    void setRecolor(final STTrueFalse.Enum p0);
    
    void xsetRecolor(final STTrueFalse p0);
    
    void unsetRecolor();
    
    STTrueFalse.Enum getRotate();
    
    STTrueFalse xgetRotate();
    
    boolean isSetRotate();
    
    void setRotate(final STTrueFalse.Enum p0);
    
    void xsetRotate(final STTrueFalse p0);
    
    void unsetRotate();
    
    String getId2();
    
    STRelationshipId xgetId2();
    
    boolean isSetId2();
    
    void setId2(final String p0);
    
    void xsetId2(final STRelationshipId p0);
    
    void unsetId2();
    
    String getRelid();
    
    com.microsoft.schemas.office.office.STRelationshipId xgetRelid();
    
    boolean isSetRelid();
    
    void setRelid(final String p0);
    
    void xsetRelid(final com.microsoft.schemas.office.office.STRelationshipId p0);
    
    void unsetRelid();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFill.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFill newInstance() {
            return (CTFill)getTypeLoader().newInstance(CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill newInstance(final XmlOptions xmlOptions) {
            return (CTFill)getTypeLoader().newInstance(CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final String s) throws XmlException {
            return (CTFill)getTypeLoader().parse(s, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFill)getTypeLoader().parse(s, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final File file) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(file, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(file, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final URL url) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(url, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(url, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(inputStream, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(inputStream, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final Reader reader) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(reader, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFill)getTypeLoader().parse(reader, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFill)getTypeLoader().parse(xmlStreamReader, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFill)getTypeLoader().parse(xmlStreamReader, CTFill.type, xmlOptions);
        }
        
        public static CTFill parse(final Node node) throws XmlException {
            return (CTFill)getTypeLoader().parse(node, CTFill.type, (XmlOptions)null);
        }
        
        public static CTFill parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFill)getTypeLoader().parse(node, CTFill.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFill parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFill)getTypeLoader().parse(xmlInputStream, CTFill.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFill parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFill)getTypeLoader().parse(xmlInputStream, CTFill.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFill.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFill.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
