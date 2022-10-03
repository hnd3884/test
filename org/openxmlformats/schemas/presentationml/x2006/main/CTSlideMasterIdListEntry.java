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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSlideMasterIdListEntry extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlideMasterIdListEntry.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslidemasteridlistentryae7ftype");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getId();
    
    STSlideMasterId xgetId();
    
    boolean isSetId();
    
    void setId(final long p0);
    
    void xsetId(final STSlideMasterId p0);
    
    void unsetId();
    
    String getId2();
    
    STRelationshipId xgetId2();
    
    void setId2(final String p0);
    
    void xsetId2(final STRelationshipId p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlideMasterIdListEntry.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlideMasterIdListEntry newInstance() {
            return (CTSlideMasterIdListEntry)getTypeLoader().newInstance(CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdListEntry newInstance(final XmlOptions xmlOptions) {
            return (CTSlideMasterIdListEntry)getTypeLoader().newInstance(CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideMasterIdListEntry parse(final String s) throws XmlException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(s, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdListEntry parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(s, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideMasterIdListEntry parse(final File file) throws XmlException, IOException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(file, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdListEntry parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(file, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideMasterIdListEntry parse(final URL url) throws XmlException, IOException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(url, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdListEntry parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(url, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideMasterIdListEntry parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(inputStream, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdListEntry parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(inputStream, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideMasterIdListEntry parse(final Reader reader) throws XmlException, IOException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(reader, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdListEntry parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(reader, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideMasterIdListEntry parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(xmlStreamReader, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdListEntry parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(xmlStreamReader, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideMasterIdListEntry parse(final Node node) throws XmlException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(node, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideMasterIdListEntry parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(node, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlideMasterIdListEntry parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(xmlInputStream, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlideMasterIdListEntry parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlideMasterIdListEntry)getTypeLoader().parse(xmlInputStream, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideMasterIdListEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideMasterIdListEntry.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
