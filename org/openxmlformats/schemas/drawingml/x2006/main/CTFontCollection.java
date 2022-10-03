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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTFontCollection extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFontCollection.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfontcollectiondd68type");
    
    CTTextFont getLatin();
    
    void setLatin(final CTTextFont p0);
    
    CTTextFont addNewLatin();
    
    CTTextFont getEa();
    
    void setEa(final CTTextFont p0);
    
    CTTextFont addNewEa();
    
    CTTextFont getCs();
    
    void setCs(final CTTextFont p0);
    
    CTTextFont addNewCs();
    
    List<CTSupplementalFont> getFontList();
    
    @Deprecated
    CTSupplementalFont[] getFontArray();
    
    CTSupplementalFont getFontArray(final int p0);
    
    int sizeOfFontArray();
    
    void setFontArray(final CTSupplementalFont[] p0);
    
    void setFontArray(final int p0, final CTSupplementalFont p1);
    
    CTSupplementalFont insertNewFont(final int p0);
    
    CTSupplementalFont addNewFont();
    
    void removeFont(final int p0);
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFontCollection.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFontCollection newInstance() {
            return (CTFontCollection)getTypeLoader().newInstance(CTFontCollection.type, (XmlOptions)null);
        }
        
        public static CTFontCollection newInstance(final XmlOptions xmlOptions) {
            return (CTFontCollection)getTypeLoader().newInstance(CTFontCollection.type, xmlOptions);
        }
        
        public static CTFontCollection parse(final String s) throws XmlException {
            return (CTFontCollection)getTypeLoader().parse(s, CTFontCollection.type, (XmlOptions)null);
        }
        
        public static CTFontCollection parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontCollection)getTypeLoader().parse(s, CTFontCollection.type, xmlOptions);
        }
        
        public static CTFontCollection parse(final File file) throws XmlException, IOException {
            return (CTFontCollection)getTypeLoader().parse(file, CTFontCollection.type, (XmlOptions)null);
        }
        
        public static CTFontCollection parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontCollection)getTypeLoader().parse(file, CTFontCollection.type, xmlOptions);
        }
        
        public static CTFontCollection parse(final URL url) throws XmlException, IOException {
            return (CTFontCollection)getTypeLoader().parse(url, CTFontCollection.type, (XmlOptions)null);
        }
        
        public static CTFontCollection parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontCollection)getTypeLoader().parse(url, CTFontCollection.type, xmlOptions);
        }
        
        public static CTFontCollection parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFontCollection)getTypeLoader().parse(inputStream, CTFontCollection.type, (XmlOptions)null);
        }
        
        public static CTFontCollection parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontCollection)getTypeLoader().parse(inputStream, CTFontCollection.type, xmlOptions);
        }
        
        public static CTFontCollection parse(final Reader reader) throws XmlException, IOException {
            return (CTFontCollection)getTypeLoader().parse(reader, CTFontCollection.type, (XmlOptions)null);
        }
        
        public static CTFontCollection parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontCollection)getTypeLoader().parse(reader, CTFontCollection.type, xmlOptions);
        }
        
        public static CTFontCollection parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFontCollection)getTypeLoader().parse(xmlStreamReader, CTFontCollection.type, (XmlOptions)null);
        }
        
        public static CTFontCollection parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontCollection)getTypeLoader().parse(xmlStreamReader, CTFontCollection.type, xmlOptions);
        }
        
        public static CTFontCollection parse(final Node node) throws XmlException {
            return (CTFontCollection)getTypeLoader().parse(node, CTFontCollection.type, (XmlOptions)null);
        }
        
        public static CTFontCollection parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontCollection)getTypeLoader().parse(node, CTFontCollection.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFontCollection parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFontCollection)getTypeLoader().parse(xmlInputStream, CTFontCollection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFontCollection parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFontCollection)getTypeLoader().parse(xmlInputStream, CTFontCollection.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontCollection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontCollection.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
