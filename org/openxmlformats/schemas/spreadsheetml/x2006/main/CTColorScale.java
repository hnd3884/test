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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTColorScale extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTColorScale.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcolorscale1a70type");
    
    List<CTCfvo> getCfvoList();
    
    @Deprecated
    CTCfvo[] getCfvoArray();
    
    CTCfvo getCfvoArray(final int p0);
    
    int sizeOfCfvoArray();
    
    void setCfvoArray(final CTCfvo[] p0);
    
    void setCfvoArray(final int p0, final CTCfvo p1);
    
    CTCfvo insertNewCfvo(final int p0);
    
    CTCfvo addNewCfvo();
    
    void removeCfvo(final int p0);
    
    List<CTColor> getColorList();
    
    @Deprecated
    CTColor[] getColorArray();
    
    CTColor getColorArray(final int p0);
    
    int sizeOfColorArray();
    
    void setColorArray(final CTColor[] p0);
    
    void setColorArray(final int p0, final CTColor p1);
    
    CTColor insertNewColor(final int p0);
    
    CTColor addNewColor();
    
    void removeColor(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTColorScale.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTColorScale newInstance() {
            return (CTColorScale)getTypeLoader().newInstance(CTColorScale.type, (XmlOptions)null);
        }
        
        public static CTColorScale newInstance(final XmlOptions xmlOptions) {
            return (CTColorScale)getTypeLoader().newInstance(CTColorScale.type, xmlOptions);
        }
        
        public static CTColorScale parse(final String s) throws XmlException {
            return (CTColorScale)getTypeLoader().parse(s, CTColorScale.type, (XmlOptions)null);
        }
        
        public static CTColorScale parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorScale)getTypeLoader().parse(s, CTColorScale.type, xmlOptions);
        }
        
        public static CTColorScale parse(final File file) throws XmlException, IOException {
            return (CTColorScale)getTypeLoader().parse(file, CTColorScale.type, (XmlOptions)null);
        }
        
        public static CTColorScale parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorScale)getTypeLoader().parse(file, CTColorScale.type, xmlOptions);
        }
        
        public static CTColorScale parse(final URL url) throws XmlException, IOException {
            return (CTColorScale)getTypeLoader().parse(url, CTColorScale.type, (XmlOptions)null);
        }
        
        public static CTColorScale parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorScale)getTypeLoader().parse(url, CTColorScale.type, xmlOptions);
        }
        
        public static CTColorScale parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTColorScale)getTypeLoader().parse(inputStream, CTColorScale.type, (XmlOptions)null);
        }
        
        public static CTColorScale parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorScale)getTypeLoader().parse(inputStream, CTColorScale.type, xmlOptions);
        }
        
        public static CTColorScale parse(final Reader reader) throws XmlException, IOException {
            return (CTColorScale)getTypeLoader().parse(reader, CTColorScale.type, (XmlOptions)null);
        }
        
        public static CTColorScale parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTColorScale)getTypeLoader().parse(reader, CTColorScale.type, xmlOptions);
        }
        
        public static CTColorScale parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTColorScale)getTypeLoader().parse(xmlStreamReader, CTColorScale.type, (XmlOptions)null);
        }
        
        public static CTColorScale parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorScale)getTypeLoader().parse(xmlStreamReader, CTColorScale.type, xmlOptions);
        }
        
        public static CTColorScale parse(final Node node) throws XmlException {
            return (CTColorScale)getTypeLoader().parse(node, CTColorScale.type, (XmlOptions)null);
        }
        
        public static CTColorScale parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTColorScale)getTypeLoader().parse(node, CTColorScale.type, xmlOptions);
        }
        
        @Deprecated
        public static CTColorScale parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTColorScale)getTypeLoader().parse(xmlInputStream, CTColorScale.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTColorScale parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTColorScale)getTypeLoader().parse(xmlInputStream, CTColorScale.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColorScale.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTColorScale.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
