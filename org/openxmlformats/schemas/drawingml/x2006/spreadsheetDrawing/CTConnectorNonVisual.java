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
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualConnectorProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTConnectorNonVisual extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTConnectorNonVisual.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctconnectornonvisual1a74type");
    
    CTNonVisualDrawingProps getCNvPr();
    
    void setCNvPr(final CTNonVisualDrawingProps p0);
    
    CTNonVisualDrawingProps addNewCNvPr();
    
    CTNonVisualConnectorProperties getCNvCxnSpPr();
    
    void setCNvCxnSpPr(final CTNonVisualConnectorProperties p0);
    
    CTNonVisualConnectorProperties addNewCNvCxnSpPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTConnectorNonVisual.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTConnectorNonVisual newInstance() {
            return (CTConnectorNonVisual)getTypeLoader().newInstance(CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        public static CTConnectorNonVisual newInstance(final XmlOptions xmlOptions) {
            return (CTConnectorNonVisual)getTypeLoader().newInstance(CTConnectorNonVisual.type, xmlOptions);
        }
        
        public static CTConnectorNonVisual parse(final String s) throws XmlException {
            return (CTConnectorNonVisual)getTypeLoader().parse(s, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        public static CTConnectorNonVisual parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectorNonVisual)getTypeLoader().parse(s, CTConnectorNonVisual.type, xmlOptions);
        }
        
        public static CTConnectorNonVisual parse(final File file) throws XmlException, IOException {
            return (CTConnectorNonVisual)getTypeLoader().parse(file, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        public static CTConnectorNonVisual parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectorNonVisual)getTypeLoader().parse(file, CTConnectorNonVisual.type, xmlOptions);
        }
        
        public static CTConnectorNonVisual parse(final URL url) throws XmlException, IOException {
            return (CTConnectorNonVisual)getTypeLoader().parse(url, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        public static CTConnectorNonVisual parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectorNonVisual)getTypeLoader().parse(url, CTConnectorNonVisual.type, xmlOptions);
        }
        
        public static CTConnectorNonVisual parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTConnectorNonVisual)getTypeLoader().parse(inputStream, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        public static CTConnectorNonVisual parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectorNonVisual)getTypeLoader().parse(inputStream, CTConnectorNonVisual.type, xmlOptions);
        }
        
        public static CTConnectorNonVisual parse(final Reader reader) throws XmlException, IOException {
            return (CTConnectorNonVisual)getTypeLoader().parse(reader, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        public static CTConnectorNonVisual parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectorNonVisual)getTypeLoader().parse(reader, CTConnectorNonVisual.type, xmlOptions);
        }
        
        public static CTConnectorNonVisual parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTConnectorNonVisual)getTypeLoader().parse(xmlStreamReader, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        public static CTConnectorNonVisual parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectorNonVisual)getTypeLoader().parse(xmlStreamReader, CTConnectorNonVisual.type, xmlOptions);
        }
        
        public static CTConnectorNonVisual parse(final Node node) throws XmlException {
            return (CTConnectorNonVisual)getTypeLoader().parse(node, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        public static CTConnectorNonVisual parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectorNonVisual)getTypeLoader().parse(node, CTConnectorNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static CTConnectorNonVisual parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTConnectorNonVisual)getTypeLoader().parse(xmlInputStream, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTConnectorNonVisual parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTConnectorNonVisual)getTypeLoader().parse(xmlInputStream, CTConnectorNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnectorNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnectorNonVisual.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
