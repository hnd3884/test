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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTEmbeddedFontList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEmbeddedFontList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctembeddedfontlist240etype");
    
    List<CTEmbeddedFontListEntry> getEmbeddedFontList();
    
    @Deprecated
    CTEmbeddedFontListEntry[] getEmbeddedFontArray();
    
    CTEmbeddedFontListEntry getEmbeddedFontArray(final int p0);
    
    int sizeOfEmbeddedFontArray();
    
    void setEmbeddedFontArray(final CTEmbeddedFontListEntry[] p0);
    
    void setEmbeddedFontArray(final int p0, final CTEmbeddedFontListEntry p1);
    
    CTEmbeddedFontListEntry insertNewEmbeddedFont(final int p0);
    
    CTEmbeddedFontListEntry addNewEmbeddedFont();
    
    void removeEmbeddedFont(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEmbeddedFontList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEmbeddedFontList newInstance() {
            return (CTEmbeddedFontList)getTypeLoader().newInstance(CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontList newInstance(final XmlOptions xmlOptions) {
            return (CTEmbeddedFontList)getTypeLoader().newInstance(CTEmbeddedFontList.type, xmlOptions);
        }
        
        public static CTEmbeddedFontList parse(final String s) throws XmlException {
            return (CTEmbeddedFontList)getTypeLoader().parse(s, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontList)getTypeLoader().parse(s, CTEmbeddedFontList.type, xmlOptions);
        }
        
        public static CTEmbeddedFontList parse(final File file) throws XmlException, IOException {
            return (CTEmbeddedFontList)getTypeLoader().parse(file, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontList)getTypeLoader().parse(file, CTEmbeddedFontList.type, xmlOptions);
        }
        
        public static CTEmbeddedFontList parse(final URL url) throws XmlException, IOException {
            return (CTEmbeddedFontList)getTypeLoader().parse(url, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontList)getTypeLoader().parse(url, CTEmbeddedFontList.type, xmlOptions);
        }
        
        public static CTEmbeddedFontList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEmbeddedFontList)getTypeLoader().parse(inputStream, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontList)getTypeLoader().parse(inputStream, CTEmbeddedFontList.type, xmlOptions);
        }
        
        public static CTEmbeddedFontList parse(final Reader reader) throws XmlException, IOException {
            return (CTEmbeddedFontList)getTypeLoader().parse(reader, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEmbeddedFontList)getTypeLoader().parse(reader, CTEmbeddedFontList.type, xmlOptions);
        }
        
        public static CTEmbeddedFontList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEmbeddedFontList)getTypeLoader().parse(xmlStreamReader, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontList)getTypeLoader().parse(xmlStreamReader, CTEmbeddedFontList.type, xmlOptions);
        }
        
        public static CTEmbeddedFontList parse(final Node node) throws XmlException {
            return (CTEmbeddedFontList)getTypeLoader().parse(node, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        public static CTEmbeddedFontList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEmbeddedFontList)getTypeLoader().parse(node, CTEmbeddedFontList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEmbeddedFontList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEmbeddedFontList)getTypeLoader().parse(xmlInputStream, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEmbeddedFontList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEmbeddedFontList)getTypeLoader().parse(xmlInputStream, CTEmbeddedFontList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmbeddedFontList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEmbeddedFontList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
