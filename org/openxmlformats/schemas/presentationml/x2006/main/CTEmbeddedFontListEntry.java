package org.openxmlformats.schemas.presentationml.x2006.main;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextFont;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTEmbeddedFontListEntry extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEmbeddedFontListEntry.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctembeddedfontlistentry48b4type");
    
    CTTextFont getFont();
    
    void setFont(final CTTextFont p0);
    
    CTTextFont addNewFont();
    
    CTEmbeddedFontDataId getRegular();
    
    boolean isSetRegular();
    
    void setRegular(final CTEmbeddedFontDataId p0);
    
    CTEmbeddedFontDataId addNewRegular();
    
    void unsetRegular();
    
    CTEmbeddedFontDataId getBold();
    
    boolean isSetBold();
    
    void setBold(final CTEmbeddedFontDataId p0);
    
    CTEmbeddedFontDataId addNewBold();
    
    void unsetBold();
    
    CTEmbeddedFontDataId getItalic();
    
    boolean isSetItalic();
    
    void setItalic(final CTEmbeddedFontDataId p0);
    
    CTEmbeddedFontDataId addNewItalic();
    
    void unsetItalic();
    
    CTEmbeddedFontDataId getBoldItalic();
    
    boolean isSetBoldItalic();
    
    void setBoldItalic(final CTEmbeddedFontDataId p0);
    
    CTEmbeddedFontDataId addNewBoldItalic();
    
    void unsetBoldItalic();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEmbeddedFontListEntry.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEmbeddedFontListEntry newInstance() {
            return (CTEmbeddedFontListEntry)getTypeLoader().newInstance(CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontListEntry newInstance(final XmlOptions xmlOptions) {
            return (CTEmbeddedFontListEntry)getTypeLoader().newInstance(CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        public static CTEmbeddedFontListEntry parse(final String s) throws XmlException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(s, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontListEntry parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(s, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        public static CTEmbeddedFontListEntry parse(final File file) throws XmlException, IOException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(file, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontListEntry parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(file, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        public static CTEmbeddedFontListEntry parse(final URL url) throws XmlException, IOException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(url, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontListEntry parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(url, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        public static CTEmbeddedFontListEntry parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(inputStream, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontListEntry parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(inputStream, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        public static CTEmbeddedFontListEntry parse(final Reader reader) throws XmlException, IOException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(reader, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontListEntry parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(reader, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        public static CTEmbeddedFontListEntry parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(xmlStreamReader, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontListEntry parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(xmlStreamReader, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        public static CTEmbeddedFontListEntry parse(final Node node) throws XmlException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(node, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontListEntry parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(node, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEmbeddedFontListEntry parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(xmlInputStream, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEmbeddedFontListEntry parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEmbeddedFontListEntry)getTypeLoader().parse(xmlInputStream, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmbeddedFontListEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmbeddedFontListEntry.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
