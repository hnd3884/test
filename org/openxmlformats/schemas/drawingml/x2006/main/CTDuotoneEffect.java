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

public interface CTDuotoneEffect extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDuotoneEffect.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctduotoneeffectae52type");
    
    List<CTScRgbColor> getScrgbClrList();
    
    @Deprecated
    CTScRgbColor[] getScrgbClrArray();
    
    CTScRgbColor getScrgbClrArray(final int p0);
    
    int sizeOfScrgbClrArray();
    
    void setScrgbClrArray(final CTScRgbColor[] p0);
    
    void setScrgbClrArray(final int p0, final CTScRgbColor p1);
    
    CTScRgbColor insertNewScrgbClr(final int p0);
    
    CTScRgbColor addNewScrgbClr();
    
    void removeScrgbClr(final int p0);
    
    List<CTSRgbColor> getSrgbClrList();
    
    @Deprecated
    CTSRgbColor[] getSrgbClrArray();
    
    CTSRgbColor getSrgbClrArray(final int p0);
    
    int sizeOfSrgbClrArray();
    
    void setSrgbClrArray(final CTSRgbColor[] p0);
    
    void setSrgbClrArray(final int p0, final CTSRgbColor p1);
    
    CTSRgbColor insertNewSrgbClr(final int p0);
    
    CTSRgbColor addNewSrgbClr();
    
    void removeSrgbClr(final int p0);
    
    List<CTHslColor> getHslClrList();
    
    @Deprecated
    CTHslColor[] getHslClrArray();
    
    CTHslColor getHslClrArray(final int p0);
    
    int sizeOfHslClrArray();
    
    void setHslClrArray(final CTHslColor[] p0);
    
    void setHslClrArray(final int p0, final CTHslColor p1);
    
    CTHslColor insertNewHslClr(final int p0);
    
    CTHslColor addNewHslClr();
    
    void removeHslClr(final int p0);
    
    List<CTSystemColor> getSysClrList();
    
    @Deprecated
    CTSystemColor[] getSysClrArray();
    
    CTSystemColor getSysClrArray(final int p0);
    
    int sizeOfSysClrArray();
    
    void setSysClrArray(final CTSystemColor[] p0);
    
    void setSysClrArray(final int p0, final CTSystemColor p1);
    
    CTSystemColor insertNewSysClr(final int p0);
    
    CTSystemColor addNewSysClr();
    
    void removeSysClr(final int p0);
    
    List<CTSchemeColor> getSchemeClrList();
    
    @Deprecated
    CTSchemeColor[] getSchemeClrArray();
    
    CTSchemeColor getSchemeClrArray(final int p0);
    
    int sizeOfSchemeClrArray();
    
    void setSchemeClrArray(final CTSchemeColor[] p0);
    
    void setSchemeClrArray(final int p0, final CTSchemeColor p1);
    
    CTSchemeColor insertNewSchemeClr(final int p0);
    
    CTSchemeColor addNewSchemeClr();
    
    void removeSchemeClr(final int p0);
    
    List<CTPresetColor> getPrstClrList();
    
    @Deprecated
    CTPresetColor[] getPrstClrArray();
    
    CTPresetColor getPrstClrArray(final int p0);
    
    int sizeOfPrstClrArray();
    
    void setPrstClrArray(final CTPresetColor[] p0);
    
    void setPrstClrArray(final int p0, final CTPresetColor p1);
    
    CTPresetColor insertNewPrstClr(final int p0);
    
    CTPresetColor addNewPrstClr();
    
    void removePrstClr(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDuotoneEffect.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDuotoneEffect newInstance() {
            return (CTDuotoneEffect)getTypeLoader().newInstance(CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        public static CTDuotoneEffect newInstance(final XmlOptions xmlOptions) {
            return (CTDuotoneEffect)getTypeLoader().newInstance(CTDuotoneEffect.type, xmlOptions);
        }
        
        public static CTDuotoneEffect parse(final String s) throws XmlException {
            return (CTDuotoneEffect)getTypeLoader().parse(s, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        public static CTDuotoneEffect parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDuotoneEffect)getTypeLoader().parse(s, CTDuotoneEffect.type, xmlOptions);
        }
        
        public static CTDuotoneEffect parse(final File file) throws XmlException, IOException {
            return (CTDuotoneEffect)getTypeLoader().parse(file, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        public static CTDuotoneEffect parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDuotoneEffect)getTypeLoader().parse(file, CTDuotoneEffect.type, xmlOptions);
        }
        
        public static CTDuotoneEffect parse(final URL url) throws XmlException, IOException {
            return (CTDuotoneEffect)getTypeLoader().parse(url, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        public static CTDuotoneEffect parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDuotoneEffect)getTypeLoader().parse(url, CTDuotoneEffect.type, xmlOptions);
        }
        
        public static CTDuotoneEffect parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDuotoneEffect)getTypeLoader().parse(inputStream, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        public static CTDuotoneEffect parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDuotoneEffect)getTypeLoader().parse(inputStream, CTDuotoneEffect.type, xmlOptions);
        }
        
        public static CTDuotoneEffect parse(final Reader reader) throws XmlException, IOException {
            return (CTDuotoneEffect)getTypeLoader().parse(reader, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        public static CTDuotoneEffect parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDuotoneEffect)getTypeLoader().parse(reader, CTDuotoneEffect.type, xmlOptions);
        }
        
        public static CTDuotoneEffect parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDuotoneEffect)getTypeLoader().parse(xmlStreamReader, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        public static CTDuotoneEffect parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDuotoneEffect)getTypeLoader().parse(xmlStreamReader, CTDuotoneEffect.type, xmlOptions);
        }
        
        public static CTDuotoneEffect parse(final Node node) throws XmlException {
            return (CTDuotoneEffect)getTypeLoader().parse(node, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        public static CTDuotoneEffect parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDuotoneEffect)getTypeLoader().parse(node, CTDuotoneEffect.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDuotoneEffect parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDuotoneEffect)getTypeLoader().parse(xmlInputStream, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDuotoneEffect parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDuotoneEffect)getTypeLoader().parse(xmlInputStream, CTDuotoneEffect.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDuotoneEffect.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDuotoneEffect.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
