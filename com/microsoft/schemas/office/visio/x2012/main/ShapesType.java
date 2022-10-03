package com.microsoft.schemas.office.visio.x2012.main;

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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ShapesType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ShapesType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("shapestypef507type");
    
    List<ShapeSheetType> getShapeList();
    
    @Deprecated
    ShapeSheetType[] getShapeArray();
    
    ShapeSheetType getShapeArray(final int p0);
    
    int sizeOfShapeArray();
    
    void setShapeArray(final ShapeSheetType[] p0);
    
    void setShapeArray(final int p0, final ShapeSheetType p1);
    
    ShapeSheetType insertNewShape(final int p0);
    
    ShapeSheetType addNewShape();
    
    void removeShape(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ShapesType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ShapesType newInstance() {
            return (ShapesType)getTypeLoader().newInstance(ShapesType.type, (XmlOptions)null);
        }
        
        public static ShapesType newInstance(final XmlOptions xmlOptions) {
            return (ShapesType)getTypeLoader().newInstance(ShapesType.type, xmlOptions);
        }
        
        public static ShapesType parse(final String s) throws XmlException {
            return (ShapesType)getTypeLoader().parse(s, ShapesType.type, (XmlOptions)null);
        }
        
        public static ShapesType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ShapesType)getTypeLoader().parse(s, ShapesType.type, xmlOptions);
        }
        
        public static ShapesType parse(final File file) throws XmlException, IOException {
            return (ShapesType)getTypeLoader().parse(file, ShapesType.type, (XmlOptions)null);
        }
        
        public static ShapesType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ShapesType)getTypeLoader().parse(file, ShapesType.type, xmlOptions);
        }
        
        public static ShapesType parse(final URL url) throws XmlException, IOException {
            return (ShapesType)getTypeLoader().parse(url, ShapesType.type, (XmlOptions)null);
        }
        
        public static ShapesType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ShapesType)getTypeLoader().parse(url, ShapesType.type, xmlOptions);
        }
        
        public static ShapesType parse(final InputStream inputStream) throws XmlException, IOException {
            return (ShapesType)getTypeLoader().parse(inputStream, ShapesType.type, (XmlOptions)null);
        }
        
        public static ShapesType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ShapesType)getTypeLoader().parse(inputStream, ShapesType.type, xmlOptions);
        }
        
        public static ShapesType parse(final Reader reader) throws XmlException, IOException {
            return (ShapesType)getTypeLoader().parse(reader, ShapesType.type, (XmlOptions)null);
        }
        
        public static ShapesType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ShapesType)getTypeLoader().parse(reader, ShapesType.type, xmlOptions);
        }
        
        public static ShapesType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ShapesType)getTypeLoader().parse(xmlStreamReader, ShapesType.type, (XmlOptions)null);
        }
        
        public static ShapesType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ShapesType)getTypeLoader().parse(xmlStreamReader, ShapesType.type, xmlOptions);
        }
        
        public static ShapesType parse(final Node node) throws XmlException {
            return (ShapesType)getTypeLoader().parse(node, ShapesType.type, (XmlOptions)null);
        }
        
        public static ShapesType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ShapesType)getTypeLoader().parse(node, ShapesType.type, xmlOptions);
        }
        
        @Deprecated
        public static ShapesType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ShapesType)getTypeLoader().parse(xmlInputStream, ShapesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ShapesType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ShapesType)getTypeLoader().parse(xmlInputStream, ShapesType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ShapesType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ShapesType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
