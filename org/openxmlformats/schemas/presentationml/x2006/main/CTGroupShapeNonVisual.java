package org.openxmlformats.schemas.presentationml.x2006.main;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualGroupDrawingShapeProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTGroupShapeNonVisual extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGroupShapeNonVisual.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgroupshapenonvisual3e44type");
    
    CTNonVisualDrawingProps getCNvPr();
    
    void setCNvPr(final CTNonVisualDrawingProps p0);
    
    CTNonVisualDrawingProps addNewCNvPr();
    
    CTNonVisualGroupDrawingShapeProps getCNvGrpSpPr();
    
    void setCNvGrpSpPr(final CTNonVisualGroupDrawingShapeProps p0);
    
    CTNonVisualGroupDrawingShapeProps addNewCNvGrpSpPr();
    
    CTApplicationNonVisualDrawingProps getNvPr();
    
    void setNvPr(final CTApplicationNonVisualDrawingProps p0);
    
    CTApplicationNonVisualDrawingProps addNewNvPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGroupShapeNonVisual.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGroupShapeNonVisual newInstance() {
            return (CTGroupShapeNonVisual)getTypeLoader().newInstance(CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeNonVisual newInstance(final XmlOptions xmlOptions) {
            return (CTGroupShapeNonVisual)getTypeLoader().newInstance(CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        public static CTGroupShapeNonVisual parse(final String s) throws XmlException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(s, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeNonVisual parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(s, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        public static CTGroupShapeNonVisual parse(final File file) throws XmlException, IOException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(file, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeNonVisual parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(file, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        public static CTGroupShapeNonVisual parse(final URL url) throws XmlException, IOException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(url, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeNonVisual parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(url, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        public static CTGroupShapeNonVisual parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(inputStream, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeNonVisual parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(inputStream, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        public static CTGroupShapeNonVisual parse(final Reader reader) throws XmlException, IOException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(reader, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeNonVisual parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(reader, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        public static CTGroupShapeNonVisual parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(xmlStreamReader, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeNonVisual parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(xmlStreamReader, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        public static CTGroupShapeNonVisual parse(final Node node) throws XmlException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(node, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTGroupShapeNonVisual parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(node, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGroupShapeNonVisual parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(xmlInputStream, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGroupShapeNonVisual parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGroupShapeNonVisual)getTypeLoader().parse(xmlInputStream, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupShapeNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGroupShapeNonVisual.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
