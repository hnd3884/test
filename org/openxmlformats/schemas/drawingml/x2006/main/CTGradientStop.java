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

public interface CTGradientStop extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTGradientStop.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctgradientstopc7edtype");
    
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
    
    int getPos();
    
    STPositiveFixedPercentage xgetPos();
    
    void setPos(final int p0);
    
    void xsetPos(final STPositiveFixedPercentage p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTGradientStop.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTGradientStop newInstance() {
            return (CTGradientStop)getTypeLoader().newInstance(CTGradientStop.type, (XmlOptions)null);
        }
        
        public static CTGradientStop newInstance(final XmlOptions xmlOptions) {
            return (CTGradientStop)getTypeLoader().newInstance(CTGradientStop.type, xmlOptions);
        }
        
        public static CTGradientStop parse(final String s) throws XmlException {
            return (CTGradientStop)getTypeLoader().parse(s, CTGradientStop.type, (XmlOptions)null);
        }
        
        public static CTGradientStop parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientStop)getTypeLoader().parse(s, CTGradientStop.type, xmlOptions);
        }
        
        public static CTGradientStop parse(final File file) throws XmlException, IOException {
            return (CTGradientStop)getTypeLoader().parse(file, CTGradientStop.type, (XmlOptions)null);
        }
        
        public static CTGradientStop parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientStop)getTypeLoader().parse(file, CTGradientStop.type, xmlOptions);
        }
        
        public static CTGradientStop parse(final URL url) throws XmlException, IOException {
            return (CTGradientStop)getTypeLoader().parse(url, CTGradientStop.type, (XmlOptions)null);
        }
        
        public static CTGradientStop parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientStop)getTypeLoader().parse(url, CTGradientStop.type, xmlOptions);
        }
        
        public static CTGradientStop parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTGradientStop)getTypeLoader().parse(inputStream, CTGradientStop.type, (XmlOptions)null);
        }
        
        public static CTGradientStop parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientStop)getTypeLoader().parse(inputStream, CTGradientStop.type, xmlOptions);
        }
        
        public static CTGradientStop parse(final Reader reader) throws XmlException, IOException {
            return (CTGradientStop)getTypeLoader().parse(reader, CTGradientStop.type, (XmlOptions)null);
        }
        
        public static CTGradientStop parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTGradientStop)getTypeLoader().parse(reader, CTGradientStop.type, xmlOptions);
        }
        
        public static CTGradientStop parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTGradientStop)getTypeLoader().parse(xmlStreamReader, CTGradientStop.type, (XmlOptions)null);
        }
        
        public static CTGradientStop parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientStop)getTypeLoader().parse(xmlStreamReader, CTGradientStop.type, xmlOptions);
        }
        
        public static CTGradientStop parse(final Node node) throws XmlException {
            return (CTGradientStop)getTypeLoader().parse(node, CTGradientStop.type, (XmlOptions)null);
        }
        
        public static CTGradientStop parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTGradientStop)getTypeLoader().parse(node, CTGradientStop.type, xmlOptions);
        }
        
        @Deprecated
        public static CTGradientStop parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTGradientStop)getTypeLoader().parse(xmlInputStream, CTGradientStop.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTGradientStop parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTGradientStop)getTypeLoader().parse(xmlInputStream, CTGradientStop.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGradientStop.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTGradientStop.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
