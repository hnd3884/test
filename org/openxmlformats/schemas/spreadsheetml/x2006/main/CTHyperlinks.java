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

public interface CTHyperlinks extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHyperlinks.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cthyperlinks6416type");
    
    List<CTHyperlink> getHyperlinkList();
    
    @Deprecated
    CTHyperlink[] getHyperlinkArray();
    
    CTHyperlink getHyperlinkArray(final int p0);
    
    int sizeOfHyperlinkArray();
    
    void setHyperlinkArray(final CTHyperlink[] p0);
    
    void setHyperlinkArray(final int p0, final CTHyperlink p1);
    
    CTHyperlink insertNewHyperlink(final int p0);
    
    CTHyperlink addNewHyperlink();
    
    void removeHyperlink(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHyperlinks.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHyperlinks newInstance() {
            return (CTHyperlinks)getTypeLoader().newInstance(CTHyperlinks.type, (XmlOptions)null);
        }
        
        public static CTHyperlinks newInstance(final XmlOptions xmlOptions) {
            return (CTHyperlinks)getTypeLoader().newInstance(CTHyperlinks.type, xmlOptions);
        }
        
        public static CTHyperlinks parse(final String s) throws XmlException {
            return (CTHyperlinks)getTypeLoader().parse(s, CTHyperlinks.type, (XmlOptions)null);
        }
        
        public static CTHyperlinks parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHyperlinks)getTypeLoader().parse(s, CTHyperlinks.type, xmlOptions);
        }
        
        public static CTHyperlinks parse(final File file) throws XmlException, IOException {
            return (CTHyperlinks)getTypeLoader().parse(file, CTHyperlinks.type, (XmlOptions)null);
        }
        
        public static CTHyperlinks parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHyperlinks)getTypeLoader().parse(file, CTHyperlinks.type, xmlOptions);
        }
        
        public static CTHyperlinks parse(final URL url) throws XmlException, IOException {
            return (CTHyperlinks)getTypeLoader().parse(url, CTHyperlinks.type, (XmlOptions)null);
        }
        
        public static CTHyperlinks parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHyperlinks)getTypeLoader().parse(url, CTHyperlinks.type, xmlOptions);
        }
        
        public static CTHyperlinks parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHyperlinks)getTypeLoader().parse(inputStream, CTHyperlinks.type, (XmlOptions)null);
        }
        
        public static CTHyperlinks parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHyperlinks)getTypeLoader().parse(inputStream, CTHyperlinks.type, xmlOptions);
        }
        
        public static CTHyperlinks parse(final Reader reader) throws XmlException, IOException {
            return (CTHyperlinks)getTypeLoader().parse(reader, CTHyperlinks.type, (XmlOptions)null);
        }
        
        public static CTHyperlinks parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHyperlinks)getTypeLoader().parse(reader, CTHyperlinks.type, xmlOptions);
        }
        
        public static CTHyperlinks parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHyperlinks)getTypeLoader().parse(xmlStreamReader, CTHyperlinks.type, (XmlOptions)null);
        }
        
        public static CTHyperlinks parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHyperlinks)getTypeLoader().parse(xmlStreamReader, CTHyperlinks.type, xmlOptions);
        }
        
        public static CTHyperlinks parse(final Node node) throws XmlException {
            return (CTHyperlinks)getTypeLoader().parse(node, CTHyperlinks.type, (XmlOptions)null);
        }
        
        public static CTHyperlinks parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHyperlinks)getTypeLoader().parse(node, CTHyperlinks.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHyperlinks parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHyperlinks)getTypeLoader().parse(xmlInputStream, CTHyperlinks.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHyperlinks parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHyperlinks)getTypeLoader().parse(xmlInputStream, CTHyperlinks.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHyperlinks.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHyperlinks.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
