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
import com.microsoft.schemas.office.office.CTStrokeChild;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTStroke extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStroke.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstrokee2f6type");
    
    CTStrokeChild getLeft();
    
    boolean isSetLeft();
    
    void setLeft(final CTStrokeChild p0);
    
    CTStrokeChild addNewLeft();
    
    void unsetLeft();
    
    CTStrokeChild getTop();
    
    boolean isSetTop();
    
    void setTop(final CTStrokeChild p0);
    
    CTStrokeChild addNewTop();
    
    void unsetTop();
    
    CTStrokeChild getRight();
    
    boolean isSetRight();
    
    void setRight(final CTStrokeChild p0);
    
    CTStrokeChild addNewRight();
    
    void unsetRight();
    
    CTStrokeChild getBottom();
    
    boolean isSetBottom();
    
    void setBottom(final CTStrokeChild p0);
    
    CTStrokeChild addNewBottom();
    
    void unsetBottom();
    
    CTStrokeChild getColumn();
    
    boolean isSetColumn();
    
    void setColumn(final CTStrokeChild p0);
    
    CTStrokeChild addNewColumn();
    
    void unsetColumn();
    
    String getId();
    
    XmlString xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlString p0);
    
    void unsetId();
    
    STTrueFalse.Enum getOn();
    
    STTrueFalse xgetOn();
    
    boolean isSetOn();
    
    void setOn(final STTrueFalse.Enum p0);
    
    void xsetOn(final STTrueFalse p0);
    
    void unsetOn();
    
    String getWeight();
    
    XmlString xgetWeight();
    
    boolean isSetWeight();
    
    void setWeight(final String p0);
    
    void xsetWeight(final XmlString p0);
    
    void unsetWeight();
    
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
    
    STStrokeLineStyle.Enum getLinestyle();
    
    STStrokeLineStyle xgetLinestyle();
    
    boolean isSetLinestyle();
    
    void setLinestyle(final STStrokeLineStyle.Enum p0);
    
    void xsetLinestyle(final STStrokeLineStyle p0);
    
    void unsetLinestyle();
    
    BigDecimal getMiterlimit();
    
    XmlDecimal xgetMiterlimit();
    
    boolean isSetMiterlimit();
    
    void setMiterlimit(final BigDecimal p0);
    
    void xsetMiterlimit(final XmlDecimal p0);
    
    void unsetMiterlimit();
    
    STStrokeJoinStyle.Enum getJoinstyle();
    
    STStrokeJoinStyle xgetJoinstyle();
    
    boolean isSetJoinstyle();
    
    void setJoinstyle(final STStrokeJoinStyle.Enum p0);
    
    void xsetJoinstyle(final STStrokeJoinStyle p0);
    
    void unsetJoinstyle();
    
    STStrokeEndCap.Enum getEndcap();
    
    STStrokeEndCap xgetEndcap();
    
    boolean isSetEndcap();
    
    void setEndcap(final STStrokeEndCap.Enum p0);
    
    void xsetEndcap(final STStrokeEndCap p0);
    
    void unsetEndcap();
    
    String getDashstyle();
    
    XmlString xgetDashstyle();
    
    boolean isSetDashstyle();
    
    void setDashstyle(final String p0);
    
    void xsetDashstyle(final XmlString p0);
    
    void unsetDashstyle();
    
    STFillType.Enum getFilltype();
    
    STFillType xgetFilltype();
    
    boolean isSetFilltype();
    
    void setFilltype(final STFillType.Enum p0);
    
    void xsetFilltype(final STFillType p0);
    
    void unsetFilltype();
    
    String getSrc();
    
    XmlString xgetSrc();
    
    boolean isSetSrc();
    
    void setSrc(final String p0);
    
    void xsetSrc(final XmlString p0);
    
    void unsetSrc();
    
    STImageAspect.Enum getImageaspect();
    
    STImageAspect xgetImageaspect();
    
    boolean isSetImageaspect();
    
    void setImageaspect(final STImageAspect.Enum p0);
    
    void xsetImageaspect(final STImageAspect p0);
    
    void unsetImageaspect();
    
    String getImagesize();
    
    XmlString xgetImagesize();
    
    boolean isSetImagesize();
    
    void setImagesize(final String p0);
    
    void xsetImagesize(final XmlString p0);
    
    void unsetImagesize();
    
    STTrueFalse.Enum getImagealignshape();
    
    STTrueFalse xgetImagealignshape();
    
    boolean isSetImagealignshape();
    
    void setImagealignshape(final STTrueFalse.Enum p0);
    
    void xsetImagealignshape(final STTrueFalse p0);
    
    void unsetImagealignshape();
    
    String getColor2();
    
    STColorType xgetColor2();
    
    boolean isSetColor2();
    
    void setColor2(final String p0);
    
    void xsetColor2(final STColorType p0);
    
    void unsetColor2();
    
    STStrokeArrowType.Enum getStartarrow();
    
    STStrokeArrowType xgetStartarrow();
    
    boolean isSetStartarrow();
    
    void setStartarrow(final STStrokeArrowType.Enum p0);
    
    void xsetStartarrow(final STStrokeArrowType p0);
    
    void unsetStartarrow();
    
    STStrokeArrowWidth.Enum getStartarrowwidth();
    
    STStrokeArrowWidth xgetStartarrowwidth();
    
    boolean isSetStartarrowwidth();
    
    void setStartarrowwidth(final STStrokeArrowWidth.Enum p0);
    
    void xsetStartarrowwidth(final STStrokeArrowWidth p0);
    
    void unsetStartarrowwidth();
    
    STStrokeArrowLength.Enum getStartarrowlength();
    
    STStrokeArrowLength xgetStartarrowlength();
    
    boolean isSetStartarrowlength();
    
    void setStartarrowlength(final STStrokeArrowLength.Enum p0);
    
    void xsetStartarrowlength(final STStrokeArrowLength p0);
    
    void unsetStartarrowlength();
    
    STStrokeArrowType.Enum getEndarrow();
    
    STStrokeArrowType xgetEndarrow();
    
    boolean isSetEndarrow();
    
    void setEndarrow(final STStrokeArrowType.Enum p0);
    
    void xsetEndarrow(final STStrokeArrowType p0);
    
    void unsetEndarrow();
    
    STStrokeArrowWidth.Enum getEndarrowwidth();
    
    STStrokeArrowWidth xgetEndarrowwidth();
    
    boolean isSetEndarrowwidth();
    
    void setEndarrowwidth(final STStrokeArrowWidth.Enum p0);
    
    void xsetEndarrowwidth(final STStrokeArrowWidth p0);
    
    void unsetEndarrowwidth();
    
    STStrokeArrowLength.Enum getEndarrowlength();
    
    STStrokeArrowLength xgetEndarrowlength();
    
    boolean isSetEndarrowlength();
    
    void setEndarrowlength(final STStrokeArrowLength.Enum p0);
    
    void xsetEndarrowlength(final STStrokeArrowLength p0);
    
    void unsetEndarrowlength();
    
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
    
    String getTitle();
    
    XmlString xgetTitle();
    
    boolean isSetTitle();
    
    void setTitle(final String p0);
    
    void xsetTitle(final XmlString p0);
    
    void unsetTitle();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getForcedash();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetForcedash();
    
    boolean isSetForcedash();
    
    void setForcedash(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetForcedash(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetForcedash();
    
    String getId2();
    
    STRelationshipId xgetId2();
    
    boolean isSetId2();
    
    void setId2(final String p0);
    
    void xsetId2(final STRelationshipId p0);
    
    void unsetId2();
    
    STTrueFalse.Enum getInsetpen();
    
    STTrueFalse xgetInsetpen();
    
    boolean isSetInsetpen();
    
    void setInsetpen(final STTrueFalse.Enum p0);
    
    void xsetInsetpen(final STTrueFalse p0);
    
    void unsetInsetpen();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStroke.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStroke newInstance() {
            return (CTStroke)getTypeLoader().newInstance(CTStroke.type, (XmlOptions)null);
        }
        
        public static CTStroke newInstance(final XmlOptions xmlOptions) {
            return (CTStroke)getTypeLoader().newInstance(CTStroke.type, xmlOptions);
        }
        
        public static CTStroke parse(final String s) throws XmlException {
            return (CTStroke)getTypeLoader().parse(s, CTStroke.type, (XmlOptions)null);
        }
        
        public static CTStroke parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStroke)getTypeLoader().parse(s, CTStroke.type, xmlOptions);
        }
        
        public static CTStroke parse(final File file) throws XmlException, IOException {
            return (CTStroke)getTypeLoader().parse(file, CTStroke.type, (XmlOptions)null);
        }
        
        public static CTStroke parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStroke)getTypeLoader().parse(file, CTStroke.type, xmlOptions);
        }
        
        public static CTStroke parse(final URL url) throws XmlException, IOException {
            return (CTStroke)getTypeLoader().parse(url, CTStroke.type, (XmlOptions)null);
        }
        
        public static CTStroke parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStroke)getTypeLoader().parse(url, CTStroke.type, xmlOptions);
        }
        
        public static CTStroke parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStroke)getTypeLoader().parse(inputStream, CTStroke.type, (XmlOptions)null);
        }
        
        public static CTStroke parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStroke)getTypeLoader().parse(inputStream, CTStroke.type, xmlOptions);
        }
        
        public static CTStroke parse(final Reader reader) throws XmlException, IOException {
            return (CTStroke)getTypeLoader().parse(reader, CTStroke.type, (XmlOptions)null);
        }
        
        public static CTStroke parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStroke)getTypeLoader().parse(reader, CTStroke.type, xmlOptions);
        }
        
        public static CTStroke parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStroke)getTypeLoader().parse(xmlStreamReader, CTStroke.type, (XmlOptions)null);
        }
        
        public static CTStroke parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStroke)getTypeLoader().parse(xmlStreamReader, CTStroke.type, xmlOptions);
        }
        
        public static CTStroke parse(final Node node) throws XmlException {
            return (CTStroke)getTypeLoader().parse(node, CTStroke.type, (XmlOptions)null);
        }
        
        public static CTStroke parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStroke)getTypeLoader().parse(node, CTStroke.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStroke parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStroke)getTypeLoader().parse(xmlInputStream, CTStroke.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStroke parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStroke)getTypeLoader().parse(xmlInputStream, CTStroke.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStroke.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStroke.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
