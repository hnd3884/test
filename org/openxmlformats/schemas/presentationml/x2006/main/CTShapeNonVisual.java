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
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingShapeProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTShapeNonVisual extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShapeNonVisual.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshapenonvisualb619type");
    
    CTNonVisualDrawingProps getCNvPr();
    
    void setCNvPr(final CTNonVisualDrawingProps p0);
    
    CTNonVisualDrawingProps addNewCNvPr();
    
    CTNonVisualDrawingShapeProps getCNvSpPr();
    
    void setCNvSpPr(final CTNonVisualDrawingShapeProps p0);
    
    CTNonVisualDrawingShapeProps addNewCNvSpPr();
    
    CTApplicationNonVisualDrawingProps getNvPr();
    
    void setNvPr(final CTApplicationNonVisualDrawingProps p0);
    
    CTApplicationNonVisualDrawingProps addNewNvPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShapeNonVisual.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShapeNonVisual newInstance() {
            return (CTShapeNonVisual)getTypeLoader().newInstance(CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTShapeNonVisual newInstance(final XmlOptions xmlOptions) {
            return (CTShapeNonVisual)getTypeLoader().newInstance(CTShapeNonVisual.type, xmlOptions);
        }
        
        public static CTShapeNonVisual parse(final String s) throws XmlException {
            return (CTShapeNonVisual)getTypeLoader().parse(s, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTShapeNonVisual parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeNonVisual)getTypeLoader().parse(s, CTShapeNonVisual.type, xmlOptions);
        }
        
        public static CTShapeNonVisual parse(final File file) throws XmlException, IOException {
            return (CTShapeNonVisual)getTypeLoader().parse(file, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTShapeNonVisual parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeNonVisual)getTypeLoader().parse(file, CTShapeNonVisual.type, xmlOptions);
        }
        
        public static CTShapeNonVisual parse(final URL url) throws XmlException, IOException {
            return (CTShapeNonVisual)getTypeLoader().parse(url, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTShapeNonVisual parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeNonVisual)getTypeLoader().parse(url, CTShapeNonVisual.type, xmlOptions);
        }
        
        public static CTShapeNonVisual parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShapeNonVisual)getTypeLoader().parse(inputStream, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTShapeNonVisual parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeNonVisual)getTypeLoader().parse(inputStream, CTShapeNonVisual.type, xmlOptions);
        }
        
        public static CTShapeNonVisual parse(final Reader reader) throws XmlException, IOException {
            return (CTShapeNonVisual)getTypeLoader().parse(reader, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTShapeNonVisual parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeNonVisual)getTypeLoader().parse(reader, CTShapeNonVisual.type, xmlOptions);
        }
        
        public static CTShapeNonVisual parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShapeNonVisual)getTypeLoader().parse(xmlStreamReader, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTShapeNonVisual parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeNonVisual)getTypeLoader().parse(xmlStreamReader, CTShapeNonVisual.type, xmlOptions);
        }
        
        public static CTShapeNonVisual parse(final Node node) throws XmlException {
            return (CTShapeNonVisual)getTypeLoader().parse(node, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        public static CTShapeNonVisual parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeNonVisual)getTypeLoader().parse(node, CTShapeNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShapeNonVisual parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShapeNonVisual)getTypeLoader().parse(xmlInputStream, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShapeNonVisual parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShapeNonVisual)getTypeLoader().parse(xmlInputStream, CTShapeNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapeNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapeNonVisual.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
