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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface PicDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PicDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("pic8010doctype");
    
    CTPicture getPic();
    
    void setPic(final CTPicture p0);
    
    CTPicture addNewPic();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PicDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PicDocument newInstance() {
            return (PicDocument)getTypeLoader().newInstance(PicDocument.type, (XmlOptions)null);
        }
        
        public static PicDocument newInstance(final XmlOptions xmlOptions) {
            return (PicDocument)getTypeLoader().newInstance(PicDocument.type, xmlOptions);
        }
        
        public static PicDocument parse(final String s) throws XmlException {
            return (PicDocument)getTypeLoader().parse(s, PicDocument.type, (XmlOptions)null);
        }
        
        public static PicDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PicDocument)getTypeLoader().parse(s, PicDocument.type, xmlOptions);
        }
        
        public static PicDocument parse(final File file) throws XmlException, IOException {
            return (PicDocument)getTypeLoader().parse(file, PicDocument.type, (XmlOptions)null);
        }
        
        public static PicDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PicDocument)getTypeLoader().parse(file, PicDocument.type, xmlOptions);
        }
        
        public static PicDocument parse(final URL url) throws XmlException, IOException {
            return (PicDocument)getTypeLoader().parse(url, PicDocument.type, (XmlOptions)null);
        }
        
        public static PicDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PicDocument)getTypeLoader().parse(url, PicDocument.type, xmlOptions);
        }
        
        public static PicDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (PicDocument)getTypeLoader().parse(inputStream, PicDocument.type, (XmlOptions)null);
        }
        
        public static PicDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PicDocument)getTypeLoader().parse(inputStream, PicDocument.type, xmlOptions);
        }
        
        public static PicDocument parse(final Reader reader) throws XmlException, IOException {
            return (PicDocument)getTypeLoader().parse(reader, PicDocument.type, (XmlOptions)null);
        }
        
        public static PicDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PicDocument)getTypeLoader().parse(reader, PicDocument.type, xmlOptions);
        }
        
        public static PicDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PicDocument)getTypeLoader().parse(xmlStreamReader, PicDocument.type, (XmlOptions)null);
        }
        
        public static PicDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PicDocument)getTypeLoader().parse(xmlStreamReader, PicDocument.type, xmlOptions);
        }
        
        public static PicDocument parse(final Node node) throws XmlException {
            return (PicDocument)getTypeLoader().parse(node, PicDocument.type, (XmlOptions)null);
        }
        
        public static PicDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PicDocument)getTypeLoader().parse(node, PicDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static PicDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PicDocument)getTypeLoader().parse(xmlInputStream, PicDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PicDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PicDocument)getTypeLoader().parse(xmlInputStream, PicDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PicDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PicDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
