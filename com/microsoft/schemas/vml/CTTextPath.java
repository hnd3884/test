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

public interface CTTextPath extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextPath.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextpath14f0type");
    
    String getId();
    
    XmlString xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlString p0);
    
    void unsetId();
    
    String getStyle();
    
    XmlString xgetStyle();
    
    boolean isSetStyle();
    
    void setStyle(final String p0);
    
    void xsetStyle(final XmlString p0);
    
    void unsetStyle();
    
    STTrueFalse.Enum getOn();
    
    STTrueFalse xgetOn();
    
    boolean isSetOn();
    
    void setOn(final STTrueFalse.Enum p0);
    
    void xsetOn(final STTrueFalse p0);
    
    void unsetOn();
    
    STTrueFalse.Enum getFitshape();
    
    STTrueFalse xgetFitshape();
    
    boolean isSetFitshape();
    
    void setFitshape(final STTrueFalse.Enum p0);
    
    void xsetFitshape(final STTrueFalse p0);
    
    void unsetFitshape();
    
    STTrueFalse.Enum getFitpath();
    
    STTrueFalse xgetFitpath();
    
    boolean isSetFitpath();
    
    void setFitpath(final STTrueFalse.Enum p0);
    
    void xsetFitpath(final STTrueFalse p0);
    
    void unsetFitpath();
    
    STTrueFalse.Enum getTrim();
    
    STTrueFalse xgetTrim();
    
    boolean isSetTrim();
    
    void setTrim(final STTrueFalse.Enum p0);
    
    void xsetTrim(final STTrueFalse p0);
    
    void unsetTrim();
    
    STTrueFalse.Enum getXscale();
    
    STTrueFalse xgetXscale();
    
    boolean isSetXscale();
    
    void setXscale(final STTrueFalse.Enum p0);
    
    void xsetXscale(final STTrueFalse p0);
    
    void unsetXscale();
    
    String getString();
    
    XmlString xgetString();
    
    boolean isSetString();
    
    void setString(final String p0);
    
    void xsetString(final XmlString p0);
    
    void unsetString();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextPath.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextPath newInstance() {
            return (CTTextPath)getTypeLoader().newInstance(CTTextPath.type, (XmlOptions)null);
        }
        
        public static CTTextPath newInstance(final XmlOptions xmlOptions) {
            return (CTTextPath)getTypeLoader().newInstance(CTTextPath.type, xmlOptions);
        }
        
        public static CTTextPath parse(final String s) throws XmlException {
            return (CTTextPath)getTypeLoader().parse(s, CTTextPath.type, (XmlOptions)null);
        }
        
        public static CTTextPath parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextPath)getTypeLoader().parse(s, CTTextPath.type, xmlOptions);
        }
        
        public static CTTextPath parse(final File file) throws XmlException, IOException {
            return (CTTextPath)getTypeLoader().parse(file, CTTextPath.type, (XmlOptions)null);
        }
        
        public static CTTextPath parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextPath)getTypeLoader().parse(file, CTTextPath.type, xmlOptions);
        }
        
        public static CTTextPath parse(final URL url) throws XmlException, IOException {
            return (CTTextPath)getTypeLoader().parse(url, CTTextPath.type, (XmlOptions)null);
        }
        
        public static CTTextPath parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextPath)getTypeLoader().parse(url, CTTextPath.type, xmlOptions);
        }
        
        public static CTTextPath parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextPath)getTypeLoader().parse(inputStream, CTTextPath.type, (XmlOptions)null);
        }
        
        public static CTTextPath parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextPath)getTypeLoader().parse(inputStream, CTTextPath.type, xmlOptions);
        }
        
        public static CTTextPath parse(final Reader reader) throws XmlException, IOException {
            return (CTTextPath)getTypeLoader().parse(reader, CTTextPath.type, (XmlOptions)null);
        }
        
        public static CTTextPath parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextPath)getTypeLoader().parse(reader, CTTextPath.type, xmlOptions);
        }
        
        public static CTTextPath parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextPath)getTypeLoader().parse(xmlStreamReader, CTTextPath.type, (XmlOptions)null);
        }
        
        public static CTTextPath parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextPath)getTypeLoader().parse(xmlStreamReader, CTTextPath.type, xmlOptions);
        }
        
        public static CTTextPath parse(final Node node) throws XmlException {
            return (CTTextPath)getTypeLoader().parse(node, CTTextPath.type, (XmlOptions)null);
        }
        
        public static CTTextPath parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextPath)getTypeLoader().parse(node, CTTextPath.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextPath parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextPath)getTypeLoader().parse(xmlInputStream, CTTextPath.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextPath parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextPath)getTypeLoader().parse(xmlInputStream, CTTextPath.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextPath.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextPath.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
