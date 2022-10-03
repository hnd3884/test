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

public interface CTSlideMasterIdList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlideMasterIdList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslidemasteridlist0b63type");
    
    List<CTSlideMasterIdListEntry> getSldMasterIdList();
    
    @Deprecated
    CTSlideMasterIdListEntry[] getSldMasterIdArray();
    
    CTSlideMasterIdListEntry getSldMasterIdArray(final int p0);
    
    int sizeOfSldMasterIdArray();
    
    void setSldMasterIdArray(final CTSlideMasterIdListEntry[] p0);
    
    void setSldMasterIdArray(final int p0, final CTSlideMasterIdListEntry p1);
    
    CTSlideMasterIdListEntry insertNewSldMasterId(final int p0);
    
    CTSlideMasterIdListEntry addNewSldMasterId();
    
    void removeSldMasterId(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlideMasterIdList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlideMasterIdList newInstance() {
            return (CTSlideMasterIdList)getTypeLoader().newInstance(CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdList newInstance(final XmlOptions xmlOptions) {
            return (CTSlideMasterIdList)getTypeLoader().newInstance(CTSlideMasterIdList.type, xmlOptions);
        }
        
        public static CTSlideMasterIdList parse(final String s) throws XmlException {
            return (CTSlideMasterIdList)getTypeLoader().parse(s, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterIdList)getTypeLoader().parse(s, CTSlideMasterIdList.type, xmlOptions);
        }
        
        public static CTSlideMasterIdList parse(final File file) throws XmlException, IOException {
            return (CTSlideMasterIdList)getTypeLoader().parse(file, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterIdList)getTypeLoader().parse(file, CTSlideMasterIdList.type, xmlOptions);
        }
        
        public static CTSlideMasterIdList parse(final URL url) throws XmlException, IOException {
            return (CTSlideMasterIdList)getTypeLoader().parse(url, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterIdList)getTypeLoader().parse(url, CTSlideMasterIdList.type, xmlOptions);
        }
        
        public static CTSlideMasterIdList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlideMasterIdList)getTypeLoader().parse(inputStream, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterIdList)getTypeLoader().parse(inputStream, CTSlideMasterIdList.type, xmlOptions);
        }
        
        public static CTSlideMasterIdList parse(final Reader reader) throws XmlException, IOException {
            return (CTSlideMasterIdList)getTypeLoader().parse(reader, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterIdList)getTypeLoader().parse(reader, CTSlideMasterIdList.type, xmlOptions);
        }
        
        public static CTSlideMasterIdList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlideMasterIdList)getTypeLoader().parse(xmlStreamReader, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterIdList)getTypeLoader().parse(xmlStreamReader, CTSlideMasterIdList.type, xmlOptions);
        }
        
        public static CTSlideMasterIdList parse(final Node node) throws XmlException {
            return (CTSlideMasterIdList)getTypeLoader().parse(node, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterIdList)getTypeLoader().parse(node, CTSlideMasterIdList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlideMasterIdList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlideMasterIdList)getTypeLoader().parse(xmlInputStream, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlideMasterIdList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlideMasterIdList)getTypeLoader().parse(xmlInputStream, CTSlideMasterIdList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideMasterIdList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideMasterIdList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
