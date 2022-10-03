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

public interface CTTblCellMar extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblCellMar.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblcellmar66eatype");
    
    CTTblWidth getTop();
    
    boolean isSetTop();
    
    void setTop(final CTTblWidth p0);
    
    CTTblWidth addNewTop();
    
    void unsetTop();
    
    CTTblWidth getLeft();
    
    boolean isSetLeft();
    
    void setLeft(final CTTblWidth p0);
    
    CTTblWidth addNewLeft();
    
    void unsetLeft();
    
    CTTblWidth getBottom();
    
    boolean isSetBottom();
    
    void setBottom(final CTTblWidth p0);
    
    CTTblWidth addNewBottom();
    
    void unsetBottom();
    
    CTTblWidth getRight();
    
    boolean isSetRight();
    
    void setRight(final CTTblWidth p0);
    
    CTTblWidth addNewRight();
    
    void unsetRight();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblCellMar.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblCellMar newInstance() {
            return (CTTblCellMar)getTypeLoader().newInstance(CTTblCellMar.type, (XmlOptions)null);
        }
        
        public static CTTblCellMar newInstance(final XmlOptions xmlOptions) {
            return (CTTblCellMar)getTypeLoader().newInstance(CTTblCellMar.type, xmlOptions);
        }
        
        public static CTTblCellMar parse(final String s) throws XmlException {
            return (CTTblCellMar)getTypeLoader().parse(s, CTTblCellMar.type, (XmlOptions)null);
        }
        
        public static CTTblCellMar parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblCellMar)getTypeLoader().parse(s, CTTblCellMar.type, xmlOptions);
        }
        
        public static CTTblCellMar parse(final File file) throws XmlException, IOException {
            return (CTTblCellMar)getTypeLoader().parse(file, CTTblCellMar.type, (XmlOptions)null);
        }
        
        public static CTTblCellMar parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblCellMar)getTypeLoader().parse(file, CTTblCellMar.type, xmlOptions);
        }
        
        public static CTTblCellMar parse(final URL url) throws XmlException, IOException {
            return (CTTblCellMar)getTypeLoader().parse(url, CTTblCellMar.type, (XmlOptions)null);
        }
        
        public static CTTblCellMar parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblCellMar)getTypeLoader().parse(url, CTTblCellMar.type, xmlOptions);
        }
        
        public static CTTblCellMar parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblCellMar)getTypeLoader().parse(inputStream, CTTblCellMar.type, (XmlOptions)null);
        }
        
        public static CTTblCellMar parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblCellMar)getTypeLoader().parse(inputStream, CTTblCellMar.type, xmlOptions);
        }
        
        public static CTTblCellMar parse(final Reader reader) throws XmlException, IOException {
            return (CTTblCellMar)getTypeLoader().parse(reader, CTTblCellMar.type, (XmlOptions)null);
        }
        
        public static CTTblCellMar parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblCellMar)getTypeLoader().parse(reader, CTTblCellMar.type, xmlOptions);
        }
        
        public static CTTblCellMar parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblCellMar)getTypeLoader().parse(xmlStreamReader, CTTblCellMar.type, (XmlOptions)null);
        }
        
        public static CTTblCellMar parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblCellMar)getTypeLoader().parse(xmlStreamReader, CTTblCellMar.type, xmlOptions);
        }
        
        public static CTTblCellMar parse(final Node node) throws XmlException {
            return (CTTblCellMar)getTypeLoader().parse(node, CTTblCellMar.type, (XmlOptions)null);
        }
        
        public static CTTblCellMar parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblCellMar)getTypeLoader().parse(node, CTTblCellMar.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblCellMar parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblCellMar)getTypeLoader().parse(xmlInputStream, CTTblCellMar.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblCellMar parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblCellMar)getTypeLoader().parse(xmlInputStream, CTTblCellMar.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblCellMar.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblCellMar.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
