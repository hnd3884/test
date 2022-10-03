package org.openxmlformats.schemas.presentationml.x2006.main;

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
import org.openxmlformats.schemas.drawingml.x2006.main.STBlackWhiteMode;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBackground extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBackground.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbackground36f7type");
    
    CTBackgroundProperties getBgPr();
    
    boolean isSetBgPr();
    
    void setBgPr(final CTBackgroundProperties p0);
    
    CTBackgroundProperties addNewBgPr();
    
    void unsetBgPr();
    
    CTStyleMatrixReference getBgRef();
    
    boolean isSetBgRef();
    
    void setBgRef(final CTStyleMatrixReference p0);
    
    CTStyleMatrixReference addNewBgRef();
    
    void unsetBgRef();
    
    STBlackWhiteMode.Enum getBwMode();
    
    STBlackWhiteMode xgetBwMode();
    
    boolean isSetBwMode();
    
    void setBwMode(final STBlackWhiteMode.Enum p0);
    
    void xsetBwMode(final STBlackWhiteMode p0);
    
    void unsetBwMode();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBackground.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBackground newInstance() {
            return (CTBackground)getTypeLoader().newInstance(CTBackground.type, (XmlOptions)null);
        }
        
        public static CTBackground newInstance(final XmlOptions xmlOptions) {
            return (CTBackground)getTypeLoader().newInstance(CTBackground.type, xmlOptions);
        }
        
        public static CTBackground parse(final String s) throws XmlException {
            return (CTBackground)getTypeLoader().parse(s, CTBackground.type, (XmlOptions)null);
        }
        
        public static CTBackground parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackground)getTypeLoader().parse(s, CTBackground.type, xmlOptions);
        }
        
        public static CTBackground parse(final File file) throws XmlException, IOException {
            return (CTBackground)getTypeLoader().parse(file, CTBackground.type, (XmlOptions)null);
        }
        
        public static CTBackground parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackground)getTypeLoader().parse(file, CTBackground.type, xmlOptions);
        }
        
        public static CTBackground parse(final URL url) throws XmlException, IOException {
            return (CTBackground)getTypeLoader().parse(url, CTBackground.type, (XmlOptions)null);
        }
        
        public static CTBackground parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackground)getTypeLoader().parse(url, CTBackground.type, xmlOptions);
        }
        
        public static CTBackground parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBackground)getTypeLoader().parse(inputStream, CTBackground.type, (XmlOptions)null);
        }
        
        public static CTBackground parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackground)getTypeLoader().parse(inputStream, CTBackground.type, xmlOptions);
        }
        
        public static CTBackground parse(final Reader reader) throws XmlException, IOException {
            return (CTBackground)getTypeLoader().parse(reader, CTBackground.type, (XmlOptions)null);
        }
        
        public static CTBackground parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackground)getTypeLoader().parse(reader, CTBackground.type, xmlOptions);
        }
        
        public static CTBackground parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBackground)getTypeLoader().parse(xmlStreamReader, CTBackground.type, (XmlOptions)null);
        }
        
        public static CTBackground parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackground)getTypeLoader().parse(xmlStreamReader, CTBackground.type, xmlOptions);
        }
        
        public static CTBackground parse(final Node node) throws XmlException {
            return (CTBackground)getTypeLoader().parse(node, CTBackground.type, (XmlOptions)null);
        }
        
        public static CTBackground parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackground)getTypeLoader().parse(node, CTBackground.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBackground parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBackground)getTypeLoader().parse(xmlInputStream, CTBackground.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBackground parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBackground)getTypeLoader().parse(xmlInputStream, CTBackground.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBackground.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBackground.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
