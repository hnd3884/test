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

public interface CTTcBorders extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTcBorders.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttcbordersa5fatype");
    
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
    
    CTBorder getTl2Br();
    
    boolean isSetTl2Br();
    
    void setTl2Br(final CTBorder p0);
    
    CTBorder addNewTl2Br();
    
    void unsetTl2Br();
    
    CTBorder getTr2Bl();
    
    boolean isSetTr2Bl();
    
    void setTr2Bl(final CTBorder p0);
    
    CTBorder addNewTr2Bl();
    
    void unsetTr2Bl();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTcBorders.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTcBorders newInstance() {
            return (CTTcBorders)getTypeLoader().newInstance(CTTcBorders.type, (XmlOptions)null);
        }
        
        public static CTTcBorders newInstance(final XmlOptions xmlOptions) {
            return (CTTcBorders)getTypeLoader().newInstance(CTTcBorders.type, xmlOptions);
        }
        
        public static CTTcBorders parse(final String s) throws XmlException {
            return (CTTcBorders)getTypeLoader().parse(s, CTTcBorders.type, (XmlOptions)null);
        }
        
        public static CTTcBorders parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcBorders)getTypeLoader().parse(s, CTTcBorders.type, xmlOptions);
        }
        
        public static CTTcBorders parse(final File file) throws XmlException, IOException {
            return (CTTcBorders)getTypeLoader().parse(file, CTTcBorders.type, (XmlOptions)null);
        }
        
        public static CTTcBorders parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcBorders)getTypeLoader().parse(file, CTTcBorders.type, xmlOptions);
        }
        
        public static CTTcBorders parse(final URL url) throws XmlException, IOException {
            return (CTTcBorders)getTypeLoader().parse(url, CTTcBorders.type, (XmlOptions)null);
        }
        
        public static CTTcBorders parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcBorders)getTypeLoader().parse(url, CTTcBorders.type, xmlOptions);
        }
        
        public static CTTcBorders parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTcBorders)getTypeLoader().parse(inputStream, CTTcBorders.type, (XmlOptions)null);
        }
        
        public static CTTcBorders parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcBorders)getTypeLoader().parse(inputStream, CTTcBorders.type, xmlOptions);
        }
        
        public static CTTcBorders parse(final Reader reader) throws XmlException, IOException {
            return (CTTcBorders)getTypeLoader().parse(reader, CTTcBorders.type, (XmlOptions)null);
        }
        
        public static CTTcBorders parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcBorders)getTypeLoader().parse(reader, CTTcBorders.type, xmlOptions);
        }
        
        public static CTTcBorders parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTcBorders)getTypeLoader().parse(xmlStreamReader, CTTcBorders.type, (XmlOptions)null);
        }
        
        public static CTTcBorders parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcBorders)getTypeLoader().parse(xmlStreamReader, CTTcBorders.type, xmlOptions);
        }
        
        public static CTTcBorders parse(final Node node) throws XmlException {
            return (CTTcBorders)getTypeLoader().parse(node, CTTcBorders.type, (XmlOptions)null);
        }
        
        public static CTTcBorders parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcBorders)getTypeLoader().parse(node, CTTcBorders.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTcBorders parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTcBorders)getTypeLoader().parse(xmlInputStream, CTTcBorders.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTcBorders parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTcBorders)getTypeLoader().parse(xmlInputStream, CTTcBorders.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTcBorders.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTcBorders.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
