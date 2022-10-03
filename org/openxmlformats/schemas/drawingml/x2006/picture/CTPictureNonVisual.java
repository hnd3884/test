package org.openxmlformats.schemas.drawingml.x2006.picture;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualPictureProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPictureNonVisual extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPictureNonVisual.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpicturenonvisual05adtype");
    
    CTNonVisualDrawingProps getCNvPr();
    
    void setCNvPr(final CTNonVisualDrawingProps p0);
    
    CTNonVisualDrawingProps addNewCNvPr();
    
    CTNonVisualPictureProperties getCNvPicPr();
    
    void setCNvPicPr(final CTNonVisualPictureProperties p0);
    
    CTNonVisualPictureProperties addNewCNvPicPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPictureNonVisual.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPictureNonVisual newInstance() {
            return (CTPictureNonVisual)getTypeLoader().newInstance(CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        public static CTPictureNonVisual newInstance(final XmlOptions xmlOptions) {
            return (CTPictureNonVisual)getTypeLoader().newInstance(CTPictureNonVisual.type, xmlOptions);
        }
        
        public static CTPictureNonVisual parse(final String s) throws XmlException {
            return (CTPictureNonVisual)getTypeLoader().parse(s, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        public static CTPictureNonVisual parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureNonVisual)getTypeLoader().parse(s, CTPictureNonVisual.type, xmlOptions);
        }
        
        public static CTPictureNonVisual parse(final File file) throws XmlException, IOException {
            return (CTPictureNonVisual)getTypeLoader().parse(file, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        public static CTPictureNonVisual parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureNonVisual)getTypeLoader().parse(file, CTPictureNonVisual.type, xmlOptions);
        }
        
        public static CTPictureNonVisual parse(final URL url) throws XmlException, IOException {
            return (CTPictureNonVisual)getTypeLoader().parse(url, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        public static CTPictureNonVisual parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureNonVisual)getTypeLoader().parse(url, CTPictureNonVisual.type, xmlOptions);
        }
        
        public static CTPictureNonVisual parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPictureNonVisual)getTypeLoader().parse(inputStream, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        public static CTPictureNonVisual parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureNonVisual)getTypeLoader().parse(inputStream, CTPictureNonVisual.type, xmlOptions);
        }
        
        public static CTPictureNonVisual parse(final Reader reader) throws XmlException, IOException {
            return (CTPictureNonVisual)getTypeLoader().parse(reader, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        public static CTPictureNonVisual parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPictureNonVisual)getTypeLoader().parse(reader, CTPictureNonVisual.type, xmlOptions);
        }
        
        public static CTPictureNonVisual parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPictureNonVisual)getTypeLoader().parse(xmlStreamReader, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        public static CTPictureNonVisual parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureNonVisual)getTypeLoader().parse(xmlStreamReader, CTPictureNonVisual.type, xmlOptions);
        }
        
        public static CTPictureNonVisual parse(final Node node) throws XmlException {
            return (CTPictureNonVisual)getTypeLoader().parse(node, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        public static CTPictureNonVisual parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPictureNonVisual)getTypeLoader().parse(node, CTPictureNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPictureNonVisual parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPictureNonVisual)getTypeLoader().parse(xmlInputStream, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPictureNonVisual parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPictureNonVisual)getTypeLoader().parse(xmlInputStream, CTPictureNonVisual.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPictureNonVisual.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPictureNonVisual.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
