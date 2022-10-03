package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTTblBorders extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblBorders.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblborders459ftype");
    
    CTBorder getTop();
    
    boolean isSetTop();
    
    void setTop(final CTBorder p0);
    
    CTBorder addNewTop();
    
    void unsetTop();
    
    CTBorder getLeft();
    
    boolean isSetLeft();
    
    void setLeft(final CTBorder p0);
    
    CTBorder addNewLeft();
    
    void unsetLeft();
    
    CTBorder getBottom();
    
    boolean isSetBottom();
    
    void setBottom(final CTBorder p0);
    
    CTBorder addNewBottom();
    
    void unsetBottom();
    
    CTBorder getRight();
    
    boolean isSetRight();
    
    void setRight(final CTBorder p0);
    
    CTBorder addNewRight();
    
    void unsetRight();
    
    CTBorder getInsideH();
    
    boolean isSetInsideH();
    
    void setInsideH(final CTBorder p0);
    
    CTBorder addNewInsideH();
    
    void unsetInsideH();
    
    CTBorder getInsideV();
    
    boolean isSetInsideV();
    
    void setInsideV(final CTBorder p0);
    
    CTBorder addNewInsideV();
    
    void unsetInsideV();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblBorders.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblBorders newInstance() {
            return (CTTblBorders)getTypeLoader().newInstance(CTTblBorders.type, (XmlOptions)null);
        }
        
        public static CTTblBorders newInstance(final XmlOptions xmlOptions) {
            return (CTTblBorders)getTypeLoader().newInstance(CTTblBorders.type, xmlOptions);
        }
        
        public static CTTblBorders parse(final String s) throws XmlException {
            return (CTTblBorders)getTypeLoader().parse(s, CTTblBorders.type, (XmlOptions)null);
        }
        
        public static CTTblBorders parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblBorders)getTypeLoader().parse(s, CTTblBorders.type, xmlOptions);
        }
        
        public static CTTblBorders parse(final File file) throws XmlException, IOException {
            return (CTTblBorders)getTypeLoader().parse(file, CTTblBorders.type, (XmlOptions)null);
        }
        
        public static CTTblBorders parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblBorders)getTypeLoader().parse(file, CTTblBorders.type, xmlOptions);
        }
        
        public static CTTblBorders parse(final URL url) throws XmlException, IOException {
            return (CTTblBorders)getTypeLoader().parse(url, CTTblBorders.type, (XmlOptions)null);
        }
        
        public static CTTblBorders parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblBorders)getTypeLoader().parse(url, CTTblBorders.type, xmlOptions);
        }
        
        public static CTTblBorders parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblBorders)getTypeLoader().parse(inputStream, CTTblBorders.type, (XmlOptions)null);
        }
        
        public static CTTblBorders parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblBorders)getTypeLoader().parse(inputStream, CTTblBorders.type, xmlOptions);
        }
        
        public static CTTblBorders parse(final Reader reader) throws XmlException, IOException {
            return (CTTblBorders)getTypeLoader().parse(reader, CTTblBorders.type, (XmlOptions)null);
        }
        
        public static CTTblBorders parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblBorders)getTypeLoader().parse(reader, CTTblBorders.type, xmlOptions);
        }
        
        public static CTTblBorders parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblBorders)getTypeLoader().parse(xmlStreamReader, CTTblBorders.type, (XmlOptions)null);
        }
        
        public static CTTblBorders parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblBorders)getTypeLoader().parse(xmlStreamReader, CTTblBorders.type, xmlOptions);
        }
        
        public static CTTblBorders parse(final Node node) throws XmlException {
            return (CTTblBorders)getTypeLoader().parse(node, CTTblBorders.type, (XmlOptions)null);
        }
        
        public static CTTblBorders parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblBorders)getTypeLoader().parse(node, CTTblBorders.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblBorders parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblBorders)getTypeLoader().parse(xmlInputStream, CTTblBorders.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblBorders parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblBorders)getTypeLoader().parse(xmlInputStream, CTTblBorders.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblBorders.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblBorders.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
