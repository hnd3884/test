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
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTInline extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTInline.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctinline5726type");
    
    CTPositiveSize2D getExtent();
    
    void setExtent(final CTPositiveSize2D p0);
    
    CTPositiveSize2D addNewExtent();
    
    CTEffectExtent getEffectExtent();
    
    boolean isSetEffectExtent();
    
    void setEffectExtent(final CTEffectExtent p0);
    
    CTEffectExtent addNewEffectExtent();
    
    void unsetEffectExtent();
    
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
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTInline.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTInline newInstance() {
            return (CTInline)getTypeLoader().newInstance(CTInline.type, (XmlOptions)null);
        }
        
        public static CTInline newInstance(final XmlOptions xmlOptions) {
            return (CTInline)getTypeLoader().newInstance(CTInline.type, xmlOptions);
        }
        
        public static CTInline parse(final String s) throws XmlException {
            return (CTInline)getTypeLoader().parse(s, CTInline.type, (XmlOptions)null);
        }
        
        public static CTInline parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTInline)getTypeLoader().parse(s, CTInline.type, xmlOptions);
        }
        
        public static CTInline parse(final File file) throws XmlException, IOException {
            return (CTInline)getTypeLoader().parse(file, CTInline.type, (XmlOptions)null);
        }
        
        public static CTInline parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTInline)getTypeLoader().parse(file, CTInline.type, xmlOptions);
        }
        
        public static CTInline parse(final URL url) throws XmlException, IOException {
            return (CTInline)getTypeLoader().parse(url, CTInline.type, (XmlOptions)null);
        }
        
        public static CTInline parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTInline)getTypeLoader().parse(url, CTInline.type, xmlOptions);
        }
        
        public static CTInline parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTInline)getTypeLoader().parse(inputStream, CTInline.type, (XmlOptions)null);
        }
        
        public static CTInline parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTInline)getTypeLoader().parse(inputStream, CTInline.type, xmlOptions);
        }
        
        public static CTInline parse(final Reader reader) throws XmlException, IOException {
            return (CTInline)getTypeLoader().parse(reader, CTInline.type, (XmlOptions)null);
        }
        
        public static CTInline parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTInline)getTypeLoader().parse(reader, CTInline.type, xmlOptions);
        }
        
        public static CTInline parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTInline)getTypeLoader().parse(xmlStreamReader, CTInline.type, (XmlOptions)null);
        }
        
        public static CTInline parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTInline)getTypeLoader().parse(xmlStreamReader, CTInline.type, xmlOptions);
        }
        
        public static CTInline parse(final Node node) throws XmlException {
            return (CTInline)getTypeLoader().parse(node, CTInline.type, (XmlOptions)null);
        }
        
        public static CTInline parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTInline)getTypeLoader().parse(node, CTInline.type, xmlOptions);
        }
        
        @Deprecated
        public static CTInline parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTInline)getTypeLoader().parse(xmlInputStream, CTInline.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTInline parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTInline)getTypeLoader().parse(xmlInputStream, CTInline.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTInline.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTInline.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
