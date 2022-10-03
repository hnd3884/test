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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDxf extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDxf.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdxfa3b1type");
    
    CTFont getFont();
    
    boolean isSetFont();
    
    void setFont(final CTFont p0);
    
    CTFont addNewFont();
    
    void unsetFont();
    
    CTNumFmt getNumFmt();
    
    boolean isSetNumFmt();
    
    void setNumFmt(final CTNumFmt p0);
    
    CTNumFmt addNewNumFmt();
    
    void unsetNumFmt();
    
    CTFill getFill();
    
    boolean isSetFill();
    
    void setFill(final CTFill p0);
    
    CTFill addNewFill();
    
    void unsetFill();
    
    CTCellAlignment getAlignment();
    
    boolean isSetAlignment();
    
    void setAlignment(final CTCellAlignment p0);
    
    CTCellAlignment addNewAlignment();
    
    void unsetAlignment();
    
    CTBorder getBorder();
    
    boolean isSetBorder();
    
    void setBorder(final CTBorder p0);
    
    CTBorder addNewBorder();
    
    void unsetBorder();
    
    CTCellProtection getProtection();
    
    boolean isSetProtection();
    
    void setProtection(final CTCellProtection p0);
    
    CTCellProtection addNewProtection();
    
    void unsetProtection();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDxf.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDxf newInstance() {
            return (CTDxf)getTypeLoader().newInstance(CTDxf.type, (XmlOptions)null);
        }
        
        public static CTDxf newInstance(final XmlOptions xmlOptions) {
            return (CTDxf)getTypeLoader().newInstance(CTDxf.type, xmlOptions);
        }
        
        public static CTDxf parse(final String s) throws XmlException {
            return (CTDxf)getTypeLoader().parse(s, CTDxf.type, (XmlOptions)null);
        }
        
        public static CTDxf parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDxf)getTypeLoader().parse(s, CTDxf.type, xmlOptions);
        }
        
        public static CTDxf parse(final File file) throws XmlException, IOException {
            return (CTDxf)getTypeLoader().parse(file, CTDxf.type, (XmlOptions)null);
        }
        
        public static CTDxf parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDxf)getTypeLoader().parse(file, CTDxf.type, xmlOptions);
        }
        
        public static CTDxf parse(final URL url) throws XmlException, IOException {
            return (CTDxf)getTypeLoader().parse(url, CTDxf.type, (XmlOptions)null);
        }
        
        public static CTDxf parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDxf)getTypeLoader().parse(url, CTDxf.type, xmlOptions);
        }
        
        public static CTDxf parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDxf)getTypeLoader().parse(inputStream, CTDxf.type, (XmlOptions)null);
        }
        
        public static CTDxf parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDxf)getTypeLoader().parse(inputStream, CTDxf.type, xmlOptions);
        }
        
        public static CTDxf parse(final Reader reader) throws XmlException, IOException {
            return (CTDxf)getTypeLoader().parse(reader, CTDxf.type, (XmlOptions)null);
        }
        
        public static CTDxf parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDxf)getTypeLoader().parse(reader, CTDxf.type, xmlOptions);
        }
        
        public static CTDxf parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDxf)getTypeLoader().parse(xmlStreamReader, CTDxf.type, (XmlOptions)null);
        }
        
        public static CTDxf parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDxf)getTypeLoader().parse(xmlStreamReader, CTDxf.type, xmlOptions);
        }
        
        public static CTDxf parse(final Node node) throws XmlException {
            return (CTDxf)getTypeLoader().parse(node, CTDxf.type, (XmlOptions)null);
        }
        
        public static CTDxf parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDxf)getTypeLoader().parse(node, CTDxf.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDxf parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDxf)getTypeLoader().parse(xmlInputStream, CTDxf.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDxf parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDxf)getTypeLoader().parse(xmlInputStream, CTDxf.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDxf.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDxf.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
