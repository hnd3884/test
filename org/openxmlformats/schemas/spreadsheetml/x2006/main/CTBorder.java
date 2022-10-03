package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBorder extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBorder.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctborderf935type");
    
    CTBorderPr getLeft();
    
    boolean isSetLeft();
    
    void setLeft(final CTBorderPr p0);
    
    CTBorderPr addNewLeft();
    
    void unsetLeft();
    
    CTBorderPr getRight();
    
    boolean isSetRight();
    
    void setRight(final CTBorderPr p0);
    
    CTBorderPr addNewRight();
    
    void unsetRight();
    
    CTBorderPr getTop();
    
    boolean isSetTop();
    
    void setTop(final CTBorderPr p0);
    
    CTBorderPr addNewTop();
    
    void unsetTop();
    
    CTBorderPr getBottom();
    
    boolean isSetBottom();
    
    void setBottom(final CTBorderPr p0);
    
    CTBorderPr addNewBottom();
    
    void unsetBottom();
    
    CTBorderPr getDiagonal();
    
    boolean isSetDiagonal();
    
    void setDiagonal(final CTBorderPr p0);
    
    CTBorderPr addNewDiagonal();
    
    void unsetDiagonal();
    
    CTBorderPr getVertical();
    
    boolean isSetVertical();
    
    void setVertical(final CTBorderPr p0);
    
    CTBorderPr addNewVertical();
    
    void unsetVertical();
    
    CTBorderPr getHorizontal();
    
    boolean isSetHorizontal();
    
    void setHorizontal(final CTBorderPr p0);
    
    CTBorderPr addNewHorizontal();
    
    void unsetHorizontal();
    
    boolean getDiagonalUp();
    
    XmlBoolean xgetDiagonalUp();
    
    boolean isSetDiagonalUp();
    
    void setDiagonalUp(final boolean p0);
    
    void xsetDiagonalUp(final XmlBoolean p0);
    
    void unsetDiagonalUp();
    
    boolean getDiagonalDown();
    
    XmlBoolean xgetDiagonalDown();
    
    boolean isSetDiagonalDown();
    
    void setDiagonalDown(final boolean p0);
    
    void xsetDiagonalDown(final XmlBoolean p0);
    
    void unsetDiagonalDown();
    
    boolean getOutline();
    
    XmlBoolean xgetOutline();
    
    boolean isSetOutline();
    
    void setOutline(final boolean p0);
    
    void xsetOutline(final XmlBoolean p0);
    
    void unsetOutline();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBorder.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBorder newInstance() {
            return (CTBorder)getTypeLoader().newInstance(CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder newInstance(final XmlOptions xmlOptions) {
            return (CTBorder)getTypeLoader().newInstance(CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final String s) throws XmlException {
            return (CTBorder)getTypeLoader().parse(s, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorder)getTypeLoader().parse(s, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final File file) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(file, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(file, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final URL url) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(url, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(url, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(inputStream, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(inputStream, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final Reader reader) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(reader, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBorder)getTypeLoader().parse(reader, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBorder)getTypeLoader().parse(xmlStreamReader, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorder)getTypeLoader().parse(xmlStreamReader, CTBorder.type, xmlOptions);
        }
        
        public static CTBorder parse(final Node node) throws XmlException {
            return (CTBorder)getTypeLoader().parse(node, CTBorder.type, (XmlOptions)null);
        }
        
        public static CTBorder parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBorder)getTypeLoader().parse(node, CTBorder.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBorder parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBorder)getTypeLoader().parse(xmlInputStream, CTBorder.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBorder parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBorder)getTypeLoader().parse(xmlInputStream, CTBorder.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBorder.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBorder.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
