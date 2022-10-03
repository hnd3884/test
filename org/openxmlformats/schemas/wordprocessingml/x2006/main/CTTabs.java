package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTTabs extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTabs.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttabsa2aatype");
    
    List<CTTabStop> getTabList();
    
    @Deprecated
    CTTabStop[] getTabArray();
    
    CTTabStop getTabArray(final int p0);
    
    int sizeOfTabArray();
    
    void setTabArray(final CTTabStop[] p0);
    
    void setTabArray(final int p0, final CTTabStop p1);
    
    CTTabStop insertNewTab(final int p0);
    
    CTTabStop addNewTab();
    
    void removeTab(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTabs.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTabs newInstance() {
            return (CTTabs)getTypeLoader().newInstance(CTTabs.type, (XmlOptions)null);
        }
        
        public static CTTabs newInstance(final XmlOptions xmlOptions) {
            return (CTTabs)getTypeLoader().newInstance(CTTabs.type, xmlOptions);
        }
        
        public static CTTabs parse(final String s) throws XmlException {
            return (CTTabs)getTypeLoader().parse(s, CTTabs.type, (XmlOptions)null);
        }
        
        public static CTTabs parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTabs)getTypeLoader().parse(s, CTTabs.type, xmlOptions);
        }
        
        public static CTTabs parse(final File file) throws XmlException, IOException {
            return (CTTabs)getTypeLoader().parse(file, CTTabs.type, (XmlOptions)null);
        }
        
        public static CTTabs parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTabs)getTypeLoader().parse(file, CTTabs.type, xmlOptions);
        }
        
        public static CTTabs parse(final URL url) throws XmlException, IOException {
            return (CTTabs)getTypeLoader().parse(url, CTTabs.type, (XmlOptions)null);
        }
        
        public static CTTabs parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTabs)getTypeLoader().parse(url, CTTabs.type, xmlOptions);
        }
        
        public static CTTabs parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTabs)getTypeLoader().parse(inputStream, CTTabs.type, (XmlOptions)null);
        }
        
        public static CTTabs parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTabs)getTypeLoader().parse(inputStream, CTTabs.type, xmlOptions);
        }
        
        public static CTTabs parse(final Reader reader) throws XmlException, IOException {
            return (CTTabs)getTypeLoader().parse(reader, CTTabs.type, (XmlOptions)null);
        }
        
        public static CTTabs parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTabs)getTypeLoader().parse(reader, CTTabs.type, xmlOptions);
        }
        
        public static CTTabs parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTabs)getTypeLoader().parse(xmlStreamReader, CTTabs.type, (XmlOptions)null);
        }
        
        public static CTTabs parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTabs)getTypeLoader().parse(xmlStreamReader, CTTabs.type, xmlOptions);
        }
        
        public static CTTabs parse(final Node node) throws XmlException {
            return (CTTabs)getTypeLoader().parse(node, CTTabs.type, (XmlOptions)null);
        }
        
        public static CTTabs parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTabs)getTypeLoader().parse(node, CTTabs.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTabs parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTabs)getTypeLoader().parse(xmlInputStream, CTTabs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTabs parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTabs)getTypeLoader().parse(xmlInputStream, CTTabs.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTabs.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTabs.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
