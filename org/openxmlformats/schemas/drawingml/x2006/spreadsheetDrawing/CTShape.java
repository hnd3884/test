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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTShape extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShape.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshapee40btype");
    
    CTShapeNonVisual getNvSpPr();
    
    void setNvSpPr(final CTShapeNonVisual p0);
    
    CTShapeNonVisual addNewNvSpPr();
    
    CTShapeProperties getSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    CTShapeStyle getStyle();
    
    boolean isSetStyle();
    
    void setStyle(final CTShapeStyle p0);
    
    CTShapeStyle addNewStyle();
    
    void unsetStyle();
    
    CTTextBody getTxBody();
    
    boolean isSetTxBody();
    
    void setTxBody(final CTTextBody p0);
    
    CTTextBody addNewTxBody();
    
    void unsetTxBody();
    
    String getMacro();
    
    XmlString xgetMacro();
    
    boolean isSetMacro();
    
    void setMacro(final String p0);
    
    void xsetMacro(final XmlString p0);
    
    void unsetMacro();
    
    String getTextlink();
    
    XmlString xgetTextlink();
    
    boolean isSetTextlink();
    
    void setTextlink(final String p0);
    
    void xsetTextlink(final XmlString p0);
    
    void unsetTextlink();
    
    boolean getFLocksText();
    
    XmlBoolean xgetFLocksText();
    
    boolean isSetFLocksText();
    
    void setFLocksText(final boolean p0);
    
    void xsetFLocksText(final XmlBoolean p0);
    
    void unsetFLocksText();
    
    boolean getFPublished();
    
    XmlBoolean xgetFPublished();
    
    boolean isSetFPublished();
    
    void setFPublished(final boolean p0);
    
    void xsetFPublished(final XmlBoolean p0);
    
    void unsetFPublished();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShape.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShape newInstance() {
            return (CTShape)getTypeLoader().newInstance(CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape newInstance(final XmlOptions xmlOptions) {
            return (CTShape)getTypeLoader().newInstance(CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final String s) throws XmlException {
            return (CTShape)getTypeLoader().parse(s, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape)getTypeLoader().parse(s, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final File file) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(file, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(file, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final URL url) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(url, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(url, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(inputStream, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(inputStream, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final Reader reader) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(reader, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShape)getTypeLoader().parse(reader, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShape)getTypeLoader().parse(xmlStreamReader, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape)getTypeLoader().parse(xmlStreamReader, CTShape.type, xmlOptions);
        }
        
        public static CTShape parse(final Node node) throws XmlException {
            return (CTShape)getTypeLoader().parse(node, CTShape.type, (XmlOptions)null);
        }
        
        public static CTShape parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShape)getTypeLoader().parse(node, CTShape.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShape parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShape)getTypeLoader().parse(xmlInputStream, CTShape.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShape parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShape)getTypeLoader().parse(xmlInputStream, CTShape.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShape.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShape.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
