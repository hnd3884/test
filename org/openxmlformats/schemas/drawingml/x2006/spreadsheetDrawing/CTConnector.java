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
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTConnector extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTConnector.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctconnector3d37type");
    
    CTConnectorNonVisual getNvCxnSpPr();
    
    void setNvCxnSpPr(final CTConnectorNonVisual p0);
    
    CTConnectorNonVisual addNewNvCxnSpPr();
    
    CTShapeProperties getSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    CTShapeStyle getStyle();
    
    boolean isSetStyle();
    
    void setStyle(final CTShapeStyle p0);
    
    CTShapeStyle addNewStyle();
    
    void unsetStyle();
    
    String getMacro();
    
    XmlString xgetMacro();
    
    boolean isSetMacro();
    
    void setMacro(final String p0);
    
    void xsetMacro(final XmlString p0);
    
    void unsetMacro();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTConnector.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTConnector newInstance() {
            return (CTConnector)getTypeLoader().newInstance(CTConnector.type, (XmlOptions)null);
        }
        
        public static CTConnector newInstance(final XmlOptions xmlOptions) {
            return (CTConnector)getTypeLoader().newInstance(CTConnector.type, xmlOptions);
        }
        
        public static CTConnector parse(final String s) throws XmlException {
            return (CTConnector)getTypeLoader().parse(s, CTConnector.type, (XmlOptions)null);
        }
        
        public static CTConnector parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnector)getTypeLoader().parse(s, CTConnector.type, xmlOptions);
        }
        
        public static CTConnector parse(final File file) throws XmlException, IOException {
            return (CTConnector)getTypeLoader().parse(file, CTConnector.type, (XmlOptions)null);
        }
        
        public static CTConnector parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnector)getTypeLoader().parse(file, CTConnector.type, xmlOptions);
        }
        
        public static CTConnector parse(final URL url) throws XmlException, IOException {
            return (CTConnector)getTypeLoader().parse(url, CTConnector.type, (XmlOptions)null);
        }
        
        public static CTConnector parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnector)getTypeLoader().parse(url, CTConnector.type, xmlOptions);
        }
        
        public static CTConnector parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTConnector)getTypeLoader().parse(inputStream, CTConnector.type, (XmlOptions)null);
        }
        
        public static CTConnector parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnector)getTypeLoader().parse(inputStream, CTConnector.type, xmlOptions);
        }
        
        public static CTConnector parse(final Reader reader) throws XmlException, IOException {
            return (CTConnector)getTypeLoader().parse(reader, CTConnector.type, (XmlOptions)null);
        }
        
        public static CTConnector parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnector)getTypeLoader().parse(reader, CTConnector.type, xmlOptions);
        }
        
        public static CTConnector parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTConnector)getTypeLoader().parse(xmlStreamReader, CTConnector.type, (XmlOptions)null);
        }
        
        public static CTConnector parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnector)getTypeLoader().parse(xmlStreamReader, CTConnector.type, xmlOptions);
        }
        
        public static CTConnector parse(final Node node) throws XmlException {
            return (CTConnector)getTypeLoader().parse(node, CTConnector.type, (XmlOptions)null);
        }
        
        public static CTConnector parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnector)getTypeLoader().parse(node, CTConnector.type, xmlOptions);
        }
        
        @Deprecated
        public static CTConnector parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTConnector)getTypeLoader().parse(xmlInputStream, CTConnector.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTConnector parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTConnector)getTypeLoader().parse(xmlInputStream, CTConnector.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnector.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnector.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
