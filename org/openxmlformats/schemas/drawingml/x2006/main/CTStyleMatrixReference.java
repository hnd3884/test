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

public interface CTStyleMatrixReference extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTStyleMatrixReference.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctstylematrixreference6ef4type");
    
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
    
    long getIdx();
    
    STStyleMatrixColumnIndex xgetIdx();
    
    void setIdx(final long p0);
    
    void xsetIdx(final STStyleMatrixColumnIndex p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTStyleMatrixReference.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTStyleMatrixReference newInstance() {
            return (CTStyleMatrixReference)getTypeLoader().newInstance(CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrixReference newInstance(final XmlOptions xmlOptions) {
            return (CTStyleMatrixReference)getTypeLoader().newInstance(CTStyleMatrixReference.type, xmlOptions);
        }
        
        public static CTStyleMatrixReference parse(final String s) throws XmlException {
            return (CTStyleMatrixReference)getTypeLoader().parse(s, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrixReference parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyleMatrixReference)getTypeLoader().parse(s, CTStyleMatrixReference.type, xmlOptions);
        }
        
        public static CTStyleMatrixReference parse(final File file) throws XmlException, IOException {
            return (CTStyleMatrixReference)getTypeLoader().parse(file, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrixReference parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyleMatrixReference)getTypeLoader().parse(file, CTStyleMatrixReference.type, xmlOptions);
        }
        
        public static CTStyleMatrixReference parse(final URL url) throws XmlException, IOException {
            return (CTStyleMatrixReference)getTypeLoader().parse(url, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrixReference parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyleMatrixReference)getTypeLoader().parse(url, CTStyleMatrixReference.type, xmlOptions);
        }
        
        public static CTStyleMatrixReference parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTStyleMatrixReference)getTypeLoader().parse(inputStream, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrixReference parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyleMatrixReference)getTypeLoader().parse(inputStream, CTStyleMatrixReference.type, xmlOptions);
        }
        
        public static CTStyleMatrixReference parse(final Reader reader) throws XmlException, IOException {
            return (CTStyleMatrixReference)getTypeLoader().parse(reader, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrixReference parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTStyleMatrixReference)getTypeLoader().parse(reader, CTStyleMatrixReference.type, xmlOptions);
        }
        
        public static CTStyleMatrixReference parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTStyleMatrixReference)getTypeLoader().parse(xmlStreamReader, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrixReference parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyleMatrixReference)getTypeLoader().parse(xmlStreamReader, CTStyleMatrixReference.type, xmlOptions);
        }
        
        public static CTStyleMatrixReference parse(final Node node) throws XmlException {
            return (CTStyleMatrixReference)getTypeLoader().parse(node, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        public static CTStyleMatrixReference parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTStyleMatrixReference)getTypeLoader().parse(node, CTStyleMatrixReference.type, xmlOptions);
        }
        
        @Deprecated
        public static CTStyleMatrixReference parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTStyleMatrixReference)getTypeLoader().parse(xmlInputStream, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTStyleMatrixReference parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTStyleMatrixReference)getTypeLoader().parse(xmlInputStream, CTStyleMatrixReference.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStyleMatrixReference.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTStyleMatrixReference.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
