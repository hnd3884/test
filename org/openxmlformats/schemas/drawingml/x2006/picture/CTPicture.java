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
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPicture extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPicture.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpicture1d48type");
    
    CTPictureNonVisual getNvPicPr();
    
    void setNvPicPr(final CTPictureNonVisual p0);
    
    CTPictureNonVisual addNewNvPicPr();
    
    CTBlipFillProperties getBlipFill();
    
    void setBlipFill(final CTBlipFillProperties p0);
    
    CTBlipFillProperties addNewBlipFill();
    
    CTShapeProperties getSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPicture.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPicture newInstance() {
            return (CTPicture)getTypeLoader().newInstance(CTPicture.type, (XmlOptions)null);
        }
        
        public static CTPicture newInstance(final XmlOptions xmlOptions) {
            return (CTPicture)getTypeLoader().newInstance(CTPicture.type, xmlOptions);
        }
        
        public static CTPicture parse(final String s) throws XmlException {
            return (CTPicture)getTypeLoader().parse(s, CTPicture.type, (XmlOptions)null);
        }
        
        public static CTPicture parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPicture)getTypeLoader().parse(s, CTPicture.type, xmlOptions);
        }
        
        public static CTPicture parse(final File file) throws XmlException, IOException {
            return (CTPicture)getTypeLoader().parse(file, CTPicture.type, (XmlOptions)null);
        }
        
        public static CTPicture parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPicture)getTypeLoader().parse(file, CTPicture.type, xmlOptions);
        }
        
        public static CTPicture parse(final URL url) throws XmlException, IOException {
            return (CTPicture)getTypeLoader().parse(url, CTPicture.type, (XmlOptions)null);
        }
        
        public static CTPicture parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPicture)getTypeLoader().parse(url, CTPicture.type, xmlOptions);
        }
        
        public static CTPicture parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPicture)getTypeLoader().parse(inputStream, CTPicture.type, (XmlOptions)null);
        }
        
        public static CTPicture parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPicture)getTypeLoader().parse(inputStream, CTPicture.type, xmlOptions);
        }
        
        public static CTPicture parse(final Reader reader) throws XmlException, IOException {
            return (CTPicture)getTypeLoader().parse(reader, CTPicture.type, (XmlOptions)null);
        }
        
        public static CTPicture parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPicture)getTypeLoader().parse(reader, CTPicture.type, xmlOptions);
        }
        
        public static CTPicture parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPicture)getTypeLoader().parse(xmlStreamReader, CTPicture.type, (XmlOptions)null);
        }
        
        public static CTPicture parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPicture)getTypeLoader().parse(xmlStreamReader, CTPicture.type, xmlOptions);
        }
        
        public static CTPicture parse(final Node node) throws XmlException {
            return (CTPicture)getTypeLoader().parse(node, CTPicture.type, (XmlOptions)null);
        }
        
        public static CTPicture parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPicture)getTypeLoader().parse(node, CTPicture.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPicture parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPicture)getTypeLoader().parse(xmlInputStream, CTPicture.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPicture parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPicture)getTypeLoader().parse(xmlInputStream, CTPicture.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPicture.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPicture.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
