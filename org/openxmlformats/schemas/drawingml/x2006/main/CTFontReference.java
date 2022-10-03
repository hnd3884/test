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

public interface CTFontReference extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTFontReference.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctfontreferencef5adtype");
    
    CTScRgbColor getScrgbClr();
    
    boolean isSetScrgbClr();
    
    void setScrgbClr(final CTScRgbColor p0);
    
    CTScRgbColor addNewScrgbClr();
    
    void unsetScrgbClr();
    
    CTSRgbColor getSrgbClr();
    
    boolean isSetSrgbClr();
    
    void setSrgbClr(final CTSRgbColor p0);
    
    CTSRgbColor addNewSrgbClr();
    
    void unsetSrgbClr();
    
    CTHslColor getHslClr();
    
    boolean isSetHslClr();
    
    void setHslClr(final CTHslColor p0);
    
    CTHslColor addNewHslClr();
    
    void unsetHslClr();
    
    CTSystemColor getSysClr();
    
    boolean isSetSysClr();
    
    void setSysClr(final CTSystemColor p0);
    
    CTSystemColor addNewSysClr();
    
    void unsetSysClr();
    
    CTSchemeColor getSchemeClr();
    
    boolean isSetSchemeClr();
    
    void setSchemeClr(final CTSchemeColor p0);
    
    CTSchemeColor addNewSchemeClr();
    
    void unsetSchemeClr();
    
    CTPresetColor getPrstClr();
    
    boolean isSetPrstClr();
    
    void setPrstClr(final CTPresetColor p0);
    
    CTPresetColor addNewPrstClr();
    
    void unsetPrstClr();
    
    STFontCollectionIndex.Enum getIdx();
    
    STFontCollectionIndex xgetIdx();
    
    void setIdx(final STFontCollectionIndex.Enum p0);
    
    void xsetIdx(final STFontCollectionIndex p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTFontReference.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTFontReference newInstance() {
            return (CTFontReference)getTypeLoader().newInstance(CTFontReference.type, (XmlOptions)null);
        }
        
        public static CTFontReference newInstance(final XmlOptions xmlOptions) {
            return (CTFontReference)getTypeLoader().newInstance(CTFontReference.type, xmlOptions);
        }
        
        public static CTFontReference parse(final String s) throws XmlException {
            return (CTFontReference)getTypeLoader().parse(s, CTFontReference.type, (XmlOptions)null);
        }
        
        public static CTFontReference parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontReference)getTypeLoader().parse(s, CTFontReference.type, xmlOptions);
        }
        
        public static CTFontReference parse(final File file) throws XmlException, IOException {
            return (CTFontReference)getTypeLoader().parse(file, CTFontReference.type, (XmlOptions)null);
        }
        
        public static CTFontReference parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontReference)getTypeLoader().parse(file, CTFontReference.type, xmlOptions);
        }
        
        public static CTFontReference parse(final URL url) throws XmlException, IOException {
            return (CTFontReference)getTypeLoader().parse(url, CTFontReference.type, (XmlOptions)null);
        }
        
        public static CTFontReference parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontReference)getTypeLoader().parse(url, CTFontReference.type, xmlOptions);
        }
        
        public static CTFontReference parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTFontReference)getTypeLoader().parse(inputStream, CTFontReference.type, (XmlOptions)null);
        }
        
        public static CTFontReference parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontReference)getTypeLoader().parse(inputStream, CTFontReference.type, xmlOptions);
        }
        
        public static CTFontReference parse(final Reader reader) throws XmlException, IOException {
            return (CTFontReference)getTypeLoader().parse(reader, CTFontReference.type, (XmlOptions)null);
        }
        
        public static CTFontReference parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTFontReference)getTypeLoader().parse(reader, CTFontReference.type, xmlOptions);
        }
        
        public static CTFontReference parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTFontReference)getTypeLoader().parse(xmlStreamReader, CTFontReference.type, (XmlOptions)null);
        }
        
        public static CTFontReference parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontReference)getTypeLoader().parse(xmlStreamReader, CTFontReference.type, xmlOptions);
        }
        
        public static CTFontReference parse(final Node node) throws XmlException {
            return (CTFontReference)getTypeLoader().parse(node, CTFontReference.type, (XmlOptions)null);
        }
        
        public static CTFontReference parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTFontReference)getTypeLoader().parse(node, CTFontReference.type, xmlOptions);
        }
        
        @Deprecated
        public static CTFontReference parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTFontReference)getTypeLoader().parse(xmlInputStream, CTFontReference.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTFontReference parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTFontReference)getTypeLoader().parse(xmlInputStream, CTFontReference.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontReference.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTFontReference.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
