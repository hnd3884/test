package org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGraphicFrameProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTGraphicalObjectFrameNonVisual extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGraphicalObjectFrameNonVisual.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgraphicalobjectframenonvisual833ctype");
    
    CTNonVisualDrawingProps getCNvPr();
    
    void setCNvPr(final CTNonVisualDrawingProps p0);
    
    CTNonVisualDrawingProps addNewCNvPr();
    
    CTNonVisualGraphicFrameProperties getCNvGraphicFramePr();
    
    void setCNvGraphicFramePr(final CTNonVisualGraphicFrameProperties p0);
    
    CTNonVisualGraphicFrameProperties addNewCNvGraphicFramePr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGraphicalObjectFrameNonVisual.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGraphicalObjectFrameNonVisual newInstance() {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().newInstance(CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameNonVisual newInstance(final XmlOptions xmlOptions) {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().newInstance(CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final String s) throws XmlException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(s, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(s, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final File file) throws XmlException, IOException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(file, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(file, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final URL url) throws XmlException, IOException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(url, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(url, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(inputStream, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(inputStream, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final Reader reader) throws XmlException, IOException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(reader, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(reader, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(xmlStreamReader, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(xmlStreamReader, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final Node node) throws XmlException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(node, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGraphicalObjectFrameNonVisual parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(node, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGraphicalObjectFrameNonVisual parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(xmlInputStream, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGraphicalObjectFrameNonVisual parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGraphicalObjectFrameNonVisual)getTypeLoader().parse(xmlInputStream, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObjectFrameNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGraphicalObjectFrameNonVisual.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
