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

public interface CTShadow extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTShadow.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshadowdfdetype");
    
    String getId();
    
    XmlString xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlString p0);
    
    void unsetId();
    
    STTrueFalse.Enum getOn();
    
    STTrueFalse xgetOn();
    
    boolean isSetOn();
    
    void setOn(final STTrueFalse.Enum p0);
    
    void xsetOn(final STTrueFalse p0);
    
    void unsetOn();
    
    STShadowType.Enum getType();
    
    STShadowType xgetType();
    
    boolean isSetType();
    
    void setType(final STShadowType.Enum p0);
    
    void xsetType(final STShadowType p0);
    
    void unsetType();
    
    STTrueFalse.Enum getObscured();
    
    STTrueFalse xgetObscured();
    
    boolean isSetObscured();
    
    void setObscured(final STTrueFalse.Enum p0);
    
    void xsetObscured(final STTrueFalse p0);
    
    void unsetObscured();
    
    String getColor();
    
    STColorType xgetColor();
    
    boolean isSetColor();
    
    void setColor(final String p0);
    
    void xsetColor(final STColorType p0);
    
    void unsetColor();
    
    String getOpacity();
    
    XmlString xgetOpacity();
    
    boolean isSetOpacity();
    
    void setOpacity(final String p0);
    
    void xsetOpacity(final XmlString p0);
    
    void unsetOpacity();
    
    String getOffset();
    
    XmlString xgetOffset();
    
    boolean isSetOffset();
    
    void setOffset(final String p0);
    
    void xsetOffset(final XmlString p0);
    
    void unsetOffset();
    
    String getColor2();
    
    STColorType xgetColor2();
    
    boolean isSetColor2();
    
    void setColor2(final String p0);
    
    void xsetColor2(final STColorType p0);
    
    void unsetColor2();
    
    String getOffset2();
    
    XmlString xgetOffset2();
    
    boolean isSetOffset2();
    
    void setOffset2(final String p0);
    
    void xsetOffset2(final XmlString p0);
    
    void unsetOffset2();
    
    String getOrigin();
    
    XmlString xgetOrigin();
    
    boolean isSetOrigin();
    
    void setOrigin(final String p0);
    
    void xsetOrigin(final XmlString p0);
    
    void unsetOrigin();
    
    String getMatrix();
    
    XmlString xgetMatrix();
    
    boolean isSetMatrix();
    
    void setMatrix(final String p0);
    
    void xsetMatrix(final XmlString p0);
    
    void unsetMatrix();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTShadow.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTShadow newInstance() {
            return (CTShadow)getTypeLoader().newInstance(CTShadow.type, (XmlOptions)null);
        }
        
        public static CTShadow newInstance(final XmlOptions xmlOptions) {
            return (CTShadow)getTypeLoader().newInstance(CTShadow.type, xmlOptions);
        }
        
        public static CTShadow parse(final String s) throws XmlException {
            return (CTShadow)getTypeLoader().parse(s, CTShadow.type, (XmlOptions)null);
        }
        
        public static CTShadow parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTShadow)getTypeLoader().parse(s, CTShadow.type, xmlOptions);
        }
        
        public static CTShadow parse(final File file) throws XmlException, IOException {
            return (CTShadow)getTypeLoader().parse(file, CTShadow.type, (XmlOptions)null);
        }
        
        public static CTShadow parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShadow)getTypeLoader().parse(file, CTShadow.type, xmlOptions);
        }
        
        public static CTShadow parse(final URL url) throws XmlException, IOException {
            return (CTShadow)getTypeLoader().parse(url, CTShadow.type, (XmlOptions)null);
        }
        
        public static CTShadow parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShadow)getTypeLoader().parse(url, CTShadow.type, xmlOptions);
        }
        
        public static CTShadow parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTShadow)getTypeLoader().parse(inputStream, CTShadow.type, (XmlOptions)null);
        }
        
        public static CTShadow parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShadow)getTypeLoader().parse(inputStream, CTShadow.type, xmlOptions);
        }
        
        public static CTShadow parse(final Reader reader) throws XmlException, IOException {
            return (CTShadow)getTypeLoader().parse(reader, CTShadow.type, (XmlOptions)null);
        }
        
        public static CTShadow parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTShadow)getTypeLoader().parse(reader, CTShadow.type, xmlOptions);
        }
        
        public static CTShadow parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTShadow)getTypeLoader().parse(xmlStreamReader, CTShadow.type, (XmlOptions)null);
        }
        
        public static CTShadow parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTShadow)getTypeLoader().parse(xmlStreamReader, CTShadow.type, xmlOptions);
        }
        
        public static CTShadow parse(final Node node) throws XmlException {
            return (CTShadow)getTypeLoader().parse(node, CTShadow.type, (XmlOptions)null);
        }
        
        public static CTShadow parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTShadow)getTypeLoader().parse(node, CTShadow.type, xmlOptions);
        }
        
        @Deprecated
        public static CTShadow parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTShadow)getTypeLoader().parse(xmlInputStream, CTShadow.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTShadow parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTShadow)getTypeLoader().parse(xmlInputStream, CTShadow.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShadow.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTShadow.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
