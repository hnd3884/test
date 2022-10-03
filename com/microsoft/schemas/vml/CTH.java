package com.microsoft.schemas.vml;

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

public interface CTH extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTH.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cth4cbctype");
    
    String getPosition();
    
    XmlString xgetPosition();
    
    boolean isSetPosition();
    
    void setPosition(final String p0);
    
    void xsetPosition(final XmlString p0);
    
    void unsetPosition();
    
    String getPolar();
    
    XmlString xgetPolar();
    
    boolean isSetPolar();
    
    void setPolar(final String p0);
    
    void xsetPolar(final XmlString p0);
    
    void unsetPolar();
    
    String getMap();
    
    XmlString xgetMap();
    
    boolean isSetMap();
    
    void setMap(final String p0);
    
    void xsetMap(final XmlString p0);
    
    void unsetMap();
    
    STTrueFalse.Enum getInvx();
    
    STTrueFalse xgetInvx();
    
    boolean isSetInvx();
    
    void setInvx(final STTrueFalse.Enum p0);
    
    void xsetInvx(final STTrueFalse p0);
    
    void unsetInvx();
    
    STTrueFalse.Enum getInvy();
    
    STTrueFalse xgetInvy();
    
    boolean isSetInvy();
    
    void setInvy(final STTrueFalse.Enum p0);
    
    void xsetInvy(final STTrueFalse p0);
    
    void unsetInvy();
    
    STTrueFalseBlank.Enum getSwitch();
    
    STTrueFalseBlank xgetSwitch();
    
    boolean isSetSwitch();
    
    void setSwitch(final STTrueFalseBlank.Enum p0);
    
    void xsetSwitch(final STTrueFalseBlank p0);
    
    void unsetSwitch();
    
    String getXrange();
    
    XmlString xgetXrange();
    
    boolean isSetXrange();
    
    void setXrange(final String p0);
    
    void xsetXrange(final XmlString p0);
    
    void unsetXrange();
    
    String getYrange();
    
    XmlString xgetYrange();
    
    boolean isSetYrange();
    
    void setYrange(final String p0);
    
    void xsetYrange(final XmlString p0);
    
    void unsetYrange();
    
    String getRadiusrange();
    
    XmlString xgetRadiusrange();
    
    boolean isSetRadiusrange();
    
    void setRadiusrange(final String p0);
    
    void xsetRadiusrange(final XmlString p0);
    
    void unsetRadiusrange();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTH.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTH newInstance() {
            return (CTH)getTypeLoader().newInstance(CTH.type, (XmlOptions)null);
        }
        
        public static CTH newInstance(final XmlOptions xmlOptions) {
            return (CTH)getTypeLoader().newInstance(CTH.type, xmlOptions);
        }
        
        public static CTH parse(final String s) throws XmlException {
            return (CTH)getTypeLoader().parse(s, CTH.type, (XmlOptions)null);
        }
        
        public static CTH parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTH)getTypeLoader().parse(s, CTH.type, xmlOptions);
        }
        
        public static CTH parse(final File file) throws XmlException, IOException {
            return (CTH)getTypeLoader().parse(file, CTH.type, (XmlOptions)null);
        }
        
        public static CTH parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTH)getTypeLoader().parse(file, CTH.type, xmlOptions);
        }
        
        public static CTH parse(final URL url) throws XmlException, IOException {
            return (CTH)getTypeLoader().parse(url, CTH.type, (XmlOptions)null);
        }
        
        public static CTH parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTH)getTypeLoader().parse(url, CTH.type, xmlOptions);
        }
        
        public static CTH parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTH)getTypeLoader().parse(inputStream, CTH.type, (XmlOptions)null);
        }
        
        public static CTH parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTH)getTypeLoader().parse(inputStream, CTH.type, xmlOptions);
        }
        
        public static CTH parse(final Reader reader) throws XmlException, IOException {
            return (CTH)getTypeLoader().parse(reader, CTH.type, (XmlOptions)null);
        }
        
        public static CTH parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTH)getTypeLoader().parse(reader, CTH.type, xmlOptions);
        }
        
        public static CTH parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTH)getTypeLoader().parse(xmlStreamReader, CTH.type, (XmlOptions)null);
        }
        
        public static CTH parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTH)getTypeLoader().parse(xmlStreamReader, CTH.type, xmlOptions);
        }
        
        public static CTH parse(final Node node) throws XmlException {
            return (CTH)getTypeLoader().parse(node, CTH.type, (XmlOptions)null);
        }
        
        public static CTH parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTH)getTypeLoader().parse(node, CTH.type, xmlOptions);
        }
        
        @Deprecated
        public static CTH parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTH)getTypeLoader().parse(xmlInputStream, CTH.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTH parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTH)getTypeLoader().parse(xmlInputStream, CTH.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTH.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTH.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
