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

public interface CTTextTabStopList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextTabStopList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttexttabstoplistf539type");
    
    List<CTTextTabStop> getTabList();
    
    @Deprecated
    CTTextTabStop[] getTabArray();
    
    CTTextTabStop getTabArray(final int p0);
    
    int sizeOfTabArray();
    
    void setTabArray(final CTTextTabStop[] p0);
    
    void setTabArray(final int p0, final CTTextTabStop p1);
    
    CTTextTabStop insertNewTab(final int p0);
    
    CTTextTabStop addNewTab();
    
    void removeTab(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextTabStopList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextTabStopList newInstance() {
            return (CTTextTabStopList)getTypeLoader().newInstance(CTTextTabStopList.type, (XmlOptions)null);
        }
        
        public static CTTextTabStopList newInstance(final XmlOptions xmlOptions) {
            return (CTTextTabStopList)getTypeLoader().newInstance(CTTextTabStopList.type, xmlOptions);
        }
        
        public static CTTextTabStopList parse(final String s) throws XmlException {
            return (CTTextTabStopList)getTypeLoader().parse(s, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        public static CTTextTabStopList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextTabStopList)getTypeLoader().parse(s, CTTextTabStopList.type, xmlOptions);
        }
        
        public static CTTextTabStopList parse(final File file) throws XmlException, IOException {
            return (CTTextTabStopList)getTypeLoader().parse(file, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        public static CTTextTabStopList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextTabStopList)getTypeLoader().parse(file, CTTextTabStopList.type, xmlOptions);
        }
        
        public static CTTextTabStopList parse(final URL url) throws XmlException, IOException {
            return (CTTextTabStopList)getTypeLoader().parse(url, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        public static CTTextTabStopList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextTabStopList)getTypeLoader().parse(url, CTTextTabStopList.type, xmlOptions);
        }
        
        public static CTTextTabStopList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextTabStopList)getTypeLoader().parse(inputStream, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        public static CTTextTabStopList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextTabStopList)getTypeLoader().parse(inputStream, CTTextTabStopList.type, xmlOptions);
        }
        
        public static CTTextTabStopList parse(final Reader reader) throws XmlException, IOException {
            return (CTTextTabStopList)getTypeLoader().parse(reader, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        public static CTTextTabStopList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextTabStopList)getTypeLoader().parse(reader, CTTextTabStopList.type, xmlOptions);
        }
        
        public static CTTextTabStopList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextTabStopList)getTypeLoader().parse(xmlStreamReader, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        public static CTTextTabStopList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextTabStopList)getTypeLoader().parse(xmlStreamReader, CTTextTabStopList.type, xmlOptions);
        }
        
        public static CTTextTabStopList parse(final Node node) throws XmlException {
            return (CTTextTabStopList)getTypeLoader().parse(node, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        public static CTTextTabStopList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextTabStopList)getTypeLoader().parse(node, CTTextTabStopList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextTabStopList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextTabStopList)getTypeLoader().parse(xmlInputStream, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextTabStopList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextTabStopList)getTypeLoader().parse(xmlInputStream, CTTextTabStopList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextTabStopList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextTabStopList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
