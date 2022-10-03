package org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing;

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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTAnchor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAnchor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctanchorff8atype");
    
    CTPoint2D getSimplePos();
    
    void setSimplePos(final CTPoint2D p0);
    
    CTPoint2D addNewSimplePos();
    
    CTPosH getPositionH();
    
    void setPositionH(final CTPosH p0);
    
    CTPosH addNewPositionH();
    
    CTPosV getPositionV();
    
    void setPositionV(final CTPosV p0);
    
    CTPosV addNewPositionV();
    
    CTPositiveSize2D getExtent();
    
    void setExtent(final CTPositiveSize2D p0);
    
    CTPositiveSize2D addNewExtent();
    
    CTEffectExtent getEffectExtent();
    
    boolean isSetEffectExtent();
    
    void setEffectExtent(final CTEffectExtent p0);
    
    CTEffectExtent addNewEffectExtent();
    
    void unsetEffectExtent();
    
    CTWrapNone getWrapNone();
    
    boolean isSetWrapNone();
    
    void setWrapNone(final CTWrapNone p0);
    
    CTWrapNone addNewWrapNone();
    
    void unsetWrapNone();
    
    CTWrapSquare getWrapSquare();
    
    boolean isSetWrapSquare();
    
    void setWrapSquare(final CTWrapSquare p0);
    
    CTWrapSquare addNewWrapSquare();
    
    void unsetWrapSquare();
    
    CTWrapTight getWrapTight();
    
    boolean isSetWrapTight();
    
    void setWrapTight(final CTWrapTight p0);
    
    CTWrapTight addNewWrapTight();
    
    void unsetWrapTight();
    
    CTWrapThrough getWrapThrough();
    
    boolean isSetWrapThrough();
    
    void setWrapThrough(final CTWrapThrough p0);
    
    CTWrapThrough addNewWrapThrough();
    
    void unsetWrapThrough();
    
    CTWrapTopBottom getWrapTopAndBottom();
    
    boolean isSetWrapTopAndBottom();
    
    void setWrapTopAndBottom(final CTWrapTopBottom p0);
    
    CTWrapTopBottom addNewWrapTopAndBottom();
    
    void unsetWrapTopAndBottom();
    
    CTNonVisualDrawingProps getDocPr();
    
    void setDocPr(final CTNonVisualDrawingProps p0);
    
    CTNonVisualDrawingProps addNewDocPr();
    
    CTNonVisualGraphicFrameProperties getCNvGraphicFramePr();
    
    boolean isSetCNvGraphicFramePr();
    
    void setCNvGraphicFramePr(final CTNonVisualGraphicFrameProperties p0);
    
    CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr();
    
    void unsetCNvGraphicFramePr();
    
    CTGraphicalObject getGraphic();
    
    void setGraphic(final CTGraphicalObject p0);
    
    CTGraphicalObject addNewGraphic();
    
    long getDistT();
    
    STWrapDistance xgetDistT();
    
    boolean isSetDistT();
    
    void setDistT(final long p0);
    
    void xsetDistT(final STWrapDistance p0);
    
    void unsetDistT();
    
    long getDistB();
    
    STWrapDistance xgetDistB();
    
    boolean isSetDistB();
    
    void setDistB(final long p0);
    
    void xsetDistB(final STWrapDistance p0);
    
    void unsetDistB();
    
    long getDistL();
    
    STWrapDistance xgetDistL();
    
    boolean isSetDistL();
    
    void setDistL(final long p0);
    
    void xsetDistL(final STWrapDistance p0);
    
    void unsetDistL();
    
    long getDistR();
    
    STWrapDistance xgetDistR();
    
    boolean isSetDistR();
    
    void setDistR(final long p0);
    
    void xsetDistR(final STWrapDistance p0);
    
    void unsetDistR();
    
    boolean getSimplePos2();
    
    XmlBoolean xgetSimplePos2();
    
    boolean isSetSimplePos2();
    
    void setSimplePos2(final boolean p0);
    
    void xsetSimplePos2(final XmlBoolean p0);
    
    void unsetSimplePos2();
    
    long getRelativeHeight();
    
    XmlUnsignedInt xgetRelativeHeight();
    
    void setRelativeHeight(final long p0);
    
    void xsetRelativeHeight(final XmlUnsignedInt p0);
    
    boolean getBehindDoc();
    
    XmlBoolean xgetBehindDoc();
    
    void setBehindDoc(final boolean p0);
    
    void xsetBehindDoc(final XmlBoolean p0);
    
    boolean getLocked();
    
    XmlBoolean xgetLocked();
    
    void setLocked(final boolean p0);
    
    void xsetLocked(final XmlBoolean p0);
    
    boolean getLayoutInCell();
    
    XmlBoolean xgetLayoutInCell();
    
    void setLayoutInCell(final boolean p0);
    
    void xsetLayoutInCell(final XmlBoolean p0);
    
    boolean getHidden();
    
    XmlBoolean xgetHidden();
    
    boolean isSetHidden();
    
    void setHidden(final boolean p0);
    
    void xsetHidden(final XmlBoolean p0);
    
    void unsetHidden();
    
    boolean getAllowOverlap();
    
    XmlBoolean xgetAllowOverlap();
    
    void setAllowOverlap(final boolean p0);
    
    void xsetAllowOverlap(final XmlBoolean p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAnchor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAnchor newInstance() {
            return (CTAnchor)getTypeLoader().newInstance(CTAnchor.type, (XmlOptions)null);
        }
        
        public static CTAnchor newInstance(final XmlOptions xmlOptions) {
            return (CTAnchor)getTypeLoader().newInstance(CTAnchor.type, xmlOptions);
        }
        
        public static CTAnchor parse(final String s) throws XmlException {
            return (CTAnchor)getTypeLoader().parse(s, CTAnchor.type, (XmlOptions)null);
        }
        
        public static CTAnchor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAnchor)getTypeLoader().parse(s, CTAnchor.type, xmlOptions);
        }
        
        public static CTAnchor parse(final File file) throws XmlException, IOException {
            return (CTAnchor)getTypeLoader().parse(file, CTAnchor.type, (XmlOptions)null);
        }
        
        public static CTAnchor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAnchor)getTypeLoader().parse(file, CTAnchor.type, xmlOptions);
        }
        
        public static CTAnchor parse(final URL url) throws XmlException, IOException {
            return (CTAnchor)getTypeLoader().parse(url, CTAnchor.type, (XmlOptions)null);
        }
        
        public static CTAnchor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAnchor)getTypeLoader().parse(url, CTAnchor.type, xmlOptions);
        }
        
        public static CTAnchor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAnchor)getTypeLoader().parse(inputStream, CTAnchor.type, (XmlOptions)null);
        }
        
        public static CTAnchor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAnchor)getTypeLoader().parse(inputStream, CTAnchor.type, xmlOptions);
        }
        
        public static CTAnchor parse(final Reader reader) throws XmlException, IOException {
            return (CTAnchor)getTypeLoader().parse(reader, CTAnchor.type, (XmlOptions)null);
        }
        
        public static CTAnchor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAnchor)getTypeLoader().parse(reader, CTAnchor.type, xmlOptions);
        }
        
        public static CTAnchor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAnchor)getTypeLoader().parse(xmlStreamReader, CTAnchor.type, (XmlOptions)null);
        }
        
        public static CTAnchor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAnchor)getTypeLoader().parse(xmlStreamReader, CTAnchor.type, xmlOptions);
        }
        
        public static CTAnchor parse(final Node node) throws XmlException {
            return (CTAnchor)getTypeLoader().parse(node, CTAnchor.type, (XmlOptions)null);
        }
        
        public static CTAnchor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAnchor)getTypeLoader().parse(node, CTAnchor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAnchor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAnchor)getTypeLoader().parse(xmlInputStream, CTAnchor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAnchor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAnchor)getTypeLoader().parse(xmlInputStream, CTAnchor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAnchor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAnchor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
