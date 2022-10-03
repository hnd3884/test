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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFontScheme extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFontScheme.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfontscheme232ftype");
    
    CTFontCollection getMajorFont();
    
    void setMajorFont(final CTFontCollection p0);
    
    CTFontCollection addNewMajorFont();
    
    CTFontCollection getMinorFont();
    
    void setMinorFont(final CTFontCollection p0);
    
    CTFontCollection addNewMinorFont();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getName();
    
    XmlString xgetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFontScheme.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFontScheme newInstance() {
            return (CTFontScheme)getTypeLoader().newInstance(CTFontScheme.type, (XmlOptions)null);
        }
        
        public static CTFontScheme newInstance(final XmlOptions xmlOptions) {
            return (CTFontScheme)getTypeLoader().newInstance(CTFontScheme.type, xmlOptions);
        }
        
        public static CTFontScheme parse(final String s) throws XmlException {
            return (CTFontScheme)getTypeLoader().parse(s, CTFontScheme.type, (XmlOptions)null);
        }
        
        public static CTFontScheme parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontScheme)getTypeLoader().parse(s, CTFontScheme.type, xmlOptions);
        }
        
        public static CTFontScheme parse(final File file) throws XmlException, IOException {
            return (CTFontScheme)getTypeLoader().parse(file, CTFontScheme.type, (XmlOptions)null);
        }
        
        public static CTFontScheme parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontScheme)getTypeLoader().parse(file, CTFontScheme.type, xmlOptions);
        }
        
        public static CTFontScheme parse(final URL url) throws XmlException, IOException {
            return (CTFontScheme)getTypeLoader().parse(url, CTFontScheme.type, (XmlOptions)null);
        }
        
        public static CTFontScheme parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontScheme)getTypeLoader().parse(url, CTFontScheme.type, xmlOptions);
        }
        
        public static CTFontScheme parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFontScheme)getTypeLoader().parse(inputStream, CTFontScheme.type, (XmlOptions)null);
        }
        
        public static CTFontScheme parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontScheme)getTypeLoader().parse(inputStream, CTFontScheme.type, xmlOptions);
        }
        
        public static CTFontScheme parse(final Reader reader) throws XmlException, IOException {
            return (CTFontScheme)getTypeLoader().parse(reader, CTFontScheme.type, (XmlOptions)null);
        }
        
        public static CTFontScheme parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontScheme)getTypeLoader().parse(reader, CTFontScheme.type, xmlOptions);
        }
        
        public static CTFontScheme parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFontScheme)getTypeLoader().parse(xmlStreamReader, CTFontScheme.type, (XmlOptions)null);
        }
        
        public static CTFontScheme parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontScheme)getTypeLoader().parse(xmlStreamReader, CTFontScheme.type, xmlOptions);
        }
        
        public static CTFontScheme parse(final Node node) throws XmlException {
            return (CTFontScheme)getTypeLoader().parse(node, CTFontScheme.type, (XmlOptions)null);
        }
        
        public static CTFontScheme parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontScheme)getTypeLoader().parse(node, CTFontScheme.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFontScheme parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFontScheme)getTypeLoader().parse(xmlInputStream, CTFontScheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFontScheme parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFontScheme)getTypeLoader().parse(xmlInputStream, CTFontScheme.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontScheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontScheme.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
