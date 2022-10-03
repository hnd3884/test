package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTShapeStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShapeStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshapestyle81ebtype");
    
    CTStyleMatrixReference getLnRef();
    
    void setLnRef(final CTStyleMatrixReference p0);
    
    CTStyleMatrixReference addNewLnRef();
    
    CTStyleMatrixReference getFillRef();
    
    void setFillRef(final CTStyleMatrixReference p0);
    
    CTStyleMatrixReference addNewFillRef();
    
    CTStyleMatrixReference getEffectRef();
    
    void setEffectRef(final CTStyleMatrixReference p0);
    
    CTStyleMatrixReference addNewEffectRef();
    
    CTFontReference getFontRef();
    
    void setFontRef(final CTFontReference p0);
    
    CTFontReference addNewFontRef();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShapeStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShapeStyle newInstance() {
            return (CTShapeStyle)getTypeLoader().newInstance(CTShapeStyle.type, (XmlOptions)null);
        }
        
        public static CTShapeStyle newInstance(final XmlOptions xmlOptions) {
            return (CTShapeStyle)getTypeLoader().newInstance(CTShapeStyle.type, xmlOptions);
        }
        
        public static CTShapeStyle parse(final String s) throws XmlException {
            return (CTShapeStyle)getTypeLoader().parse(s, CTShapeStyle.type, (XmlOptions)null);
        }
        
        public static CTShapeStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeStyle)getTypeLoader().parse(s, CTShapeStyle.type, xmlOptions);
        }
        
        public static CTShapeStyle parse(final File file) throws XmlException, IOException {
            return (CTShapeStyle)getTypeLoader().parse(file, CTShapeStyle.type, (XmlOptions)null);
        }
        
        public static CTShapeStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeStyle)getTypeLoader().parse(file, CTShapeStyle.type, xmlOptions);
        }
        
        public static CTShapeStyle parse(final URL url) throws XmlException, IOException {
            return (CTShapeStyle)getTypeLoader().parse(url, CTShapeStyle.type, (XmlOptions)null);
        }
        
        public static CTShapeStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeStyle)getTypeLoader().parse(url, CTShapeStyle.type, xmlOptions);
        }
        
        public static CTShapeStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShapeStyle)getTypeLoader().parse(inputStream, CTShapeStyle.type, (XmlOptions)null);
        }
        
        public static CTShapeStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeStyle)getTypeLoader().parse(inputStream, CTShapeStyle.type, xmlOptions);
        }
        
        public static CTShapeStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTShapeStyle)getTypeLoader().parse(reader, CTShapeStyle.type, (XmlOptions)null);
        }
        
        public static CTShapeStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShapeStyle)getTypeLoader().parse(reader, CTShapeStyle.type, xmlOptions);
        }
        
        public static CTShapeStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShapeStyle)getTypeLoader().parse(xmlStreamReader, CTShapeStyle.type, (XmlOptions)null);
        }
        
        public static CTShapeStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeStyle)getTypeLoader().parse(xmlStreamReader, CTShapeStyle.type, xmlOptions);
        }
        
        public static CTShapeStyle parse(final Node node) throws XmlException {
            return (CTShapeStyle)getTypeLoader().parse(node, CTShapeStyle.type, (XmlOptions)null);
        }
        
        public static CTShapeStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShapeStyle)getTypeLoader().parse(node, CTShapeStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShapeStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShapeStyle)getTypeLoader().parse(xmlInputStream, CTShapeStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShapeStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShapeStyle)getTypeLoader().parse(xmlInputStream, CTShapeStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapeStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShapeStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
