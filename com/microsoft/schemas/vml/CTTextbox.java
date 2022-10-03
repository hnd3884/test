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
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.office.office.STTrueFalse;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTxbxContent;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTextbox extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextbox.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextboxf712type");
    
    CTTxbxContent getTxbxContent();
    
    boolean isSetTxbxContent();
    
    void setTxbxContent(final CTTxbxContent p0);
    
    CTTxbxContent addNewTxbxContent();
    
    void unsetTxbxContent();
    
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
    
    String getInset();
    
    XmlString xgetInset();
    
    boolean isSetInset();
    
    void setInset(final String p0);
    
    void xsetInset(final XmlString p0);
    
    void unsetInset();
    
    STTrueFalse.Enum getSingleclick();
    
    STTrueFalse xgetSingleclick();
    
    boolean isSetSingleclick();
    
    void setSingleclick(final STTrueFalse.Enum p0);
    
    void xsetSingleclick(final STTrueFalse p0);
    
    void unsetSingleclick();
    
    STInsetMode.Enum getInsetmode();
    
    STInsetMode xgetInsetmode();
    
    boolean isSetInsetmode();
    
    void setInsetmode(final STInsetMode.Enum p0);
    
    void xsetInsetmode(final STInsetMode p0);
    
    void unsetInsetmode();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextbox.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextbox newInstance() {
            return (CTTextbox)getTypeLoader().newInstance(CTTextbox.type, (XmlOptions)null);
        }
        
        public static CTTextbox newInstance(final XmlOptions xmlOptions) {
            return (CTTextbox)getTypeLoader().newInstance(CTTextbox.type, xmlOptions);
        }
        
        public static CTTextbox parse(final String s) throws XmlException {
            return (CTTextbox)getTypeLoader().parse(s, CTTextbox.type, (XmlOptions)null);
        }
        
        public static CTTextbox parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextbox)getTypeLoader().parse(s, CTTextbox.type, xmlOptions);
        }
        
        public static CTTextbox parse(final File file) throws XmlException, IOException {
            return (CTTextbox)getTypeLoader().parse(file, CTTextbox.type, (XmlOptions)null);
        }
        
        public static CTTextbox parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextbox)getTypeLoader().parse(file, CTTextbox.type, xmlOptions);
        }
        
        public static CTTextbox parse(final URL url) throws XmlException, IOException {
            return (CTTextbox)getTypeLoader().parse(url, CTTextbox.type, (XmlOptions)null);
        }
        
        public static CTTextbox parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextbox)getTypeLoader().parse(url, CTTextbox.type, xmlOptions);
        }
        
        public static CTTextbox parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextbox)getTypeLoader().parse(inputStream, CTTextbox.type, (XmlOptions)null);
        }
        
        public static CTTextbox parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextbox)getTypeLoader().parse(inputStream, CTTextbox.type, xmlOptions);
        }
        
        public static CTTextbox parse(final Reader reader) throws XmlException, IOException {
            return (CTTextbox)getTypeLoader().parse(reader, CTTextbox.type, (XmlOptions)null);
        }
        
        public static CTTextbox parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextbox)getTypeLoader().parse(reader, CTTextbox.type, xmlOptions);
        }
        
        public static CTTextbox parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextbox)getTypeLoader().parse(xmlStreamReader, CTTextbox.type, (XmlOptions)null);
        }
        
        public static CTTextbox parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextbox)getTypeLoader().parse(xmlStreamReader, CTTextbox.type, xmlOptions);
        }
        
        public static CTTextbox parse(final Node node) throws XmlException {
            return (CTTextbox)getTypeLoader().parse(node, CTTextbox.type, (XmlOptions)null);
        }
        
        public static CTTextbox parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextbox)getTypeLoader().parse(node, CTTextbox.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextbox parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextbox)getTypeLoader().parse(xmlInputStream, CTTextbox.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextbox parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextbox)getTypeLoader().parse(xmlInputStream, CTTextbox.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextbox.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextbox.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
