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

public interface CTSlideIdList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlideIdList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslideidlist70a5type");
    
    List<CTSlideIdListEntry> getSldIdList();
    
    @Deprecated
    CTSlideIdListEntry[] getSldIdArray();
    
    CTSlideIdListEntry getSldIdArray(final int p0);
    
    int sizeOfSldIdArray();
    
    void setSldIdArray(final CTSlideIdListEntry[] p0);
    
    void setSldIdArray(final int p0, final CTSlideIdListEntry p1);
    
    CTSlideIdListEntry insertNewSldId(final int p0);
    
    CTSlideIdListEntry addNewSldId();
    
    void removeSldId(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlideIdList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlideIdList newInstance() {
            return (CTSlideIdList)getTypeLoader().newInstance(CTSlideIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideIdList newInstance(final XmlOptions xmlOptions) {
            return (CTSlideIdList)getTypeLoader().newInstance(CTSlideIdList.type, xmlOptions);
        }
        
        public static CTSlideIdList parse(final String s) throws XmlException {
            return (CTSlideIdList)getTypeLoader().parse(s, CTSlideIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideIdList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideIdList)getTypeLoader().parse(s, CTSlideIdList.type, xmlOptions);
        }
        
        public static CTSlideIdList parse(final File file) throws XmlException, IOException {
            return (CTSlideIdList)getTypeLoader().parse(file, CTSlideIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideIdList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideIdList)getTypeLoader().parse(file, CTSlideIdList.type, xmlOptions);
        }
        
        public static CTSlideIdList parse(final URL url) throws XmlException, IOException {
            return (CTSlideIdList)getTypeLoader().parse(url, CTSlideIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideIdList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideIdList)getTypeLoader().parse(url, CTSlideIdList.type, xmlOptions);
        }
        
        public static CTSlideIdList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlideIdList)getTypeLoader().parse(inputStream, CTSlideIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideIdList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideIdList)getTypeLoader().parse(inputStream, CTSlideIdList.type, xmlOptions);
        }
        
        public static CTSlideIdList parse(final Reader reader) throws XmlException, IOException {
            return (CTSlideIdList)getTypeLoader().parse(reader, CTSlideIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideIdList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideIdList)getTypeLoader().parse(reader, CTSlideIdList.type, xmlOptions);
        }
        
        public static CTSlideIdList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlideIdList)getTypeLoader().parse(xmlStreamReader, CTSlideIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideIdList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideIdList)getTypeLoader().parse(xmlStreamReader, CTSlideIdList.type, xmlOptions);
        }
        
        public static CTSlideIdList parse(final Node node) throws XmlException {
            return (CTSlideIdList)getTypeLoader().parse(node, CTSlideIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideIdList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideIdList)getTypeLoader().parse(node, CTSlideIdList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlideIdList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlideIdList)getTypeLoader().parse(xmlInputStream, CTSlideIdList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlideIdList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlideIdList)getTypeLoader().parse(xmlInputStream, CTSlideIdList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideIdList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideIdList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
