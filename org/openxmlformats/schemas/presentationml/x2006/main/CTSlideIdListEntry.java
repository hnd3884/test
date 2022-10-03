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

public interface CTSlideIdListEntry extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSlideIdListEntry.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctslideidlistentry427dtype");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getId();
    
    STSlideId xgetId();
    
    void setId(final long p0);
    
    void xsetId(final STSlideId p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSlideIdListEntry.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSlideIdListEntry newInstance() {
            return (CTSlideIdListEntry)getTypeLoader().newInstance(CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideIdListEntry newInstance(final XmlOptions xmlOptions) {
            return (CTSlideIdListEntry)getTypeLoader().newInstance(CTSlideIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideIdListEntry parse(final String s) throws XmlException {
            return (CTSlideIdListEntry)getTypeLoader().parse(s, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideIdListEntry parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideIdListEntry)getTypeLoader().parse(s, CTSlideIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideIdListEntry parse(final File file) throws XmlException, IOException {
            return (CTSlideIdListEntry)getTypeLoader().parse(file, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideIdListEntry parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideIdListEntry)getTypeLoader().parse(file, CTSlideIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideIdListEntry parse(final URL url) throws XmlException, IOException {
            return (CTSlideIdListEntry)getTypeLoader().parse(url, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideIdListEntry parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideIdListEntry)getTypeLoader().parse(url, CTSlideIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideIdListEntry parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSlideIdListEntry)getTypeLoader().parse(inputStream, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideIdListEntry parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideIdListEntry)getTypeLoader().parse(inputStream, CTSlideIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideIdListEntry parse(final Reader reader) throws XmlException, IOException {
            return (CTSlideIdListEntry)getTypeLoader().parse(reader, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideIdListEntry parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSlideIdListEntry)getTypeLoader().parse(reader, CTSlideIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideIdListEntry parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSlideIdListEntry)getTypeLoader().parse(xmlStreamReader, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideIdListEntry parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideIdListEntry)getTypeLoader().parse(xmlStreamReader, CTSlideIdListEntry.type, xmlOptions);
        }
        
        public static CTSlideIdListEntry parse(final Node node) throws XmlException {
            return (CTSlideIdListEntry)getTypeLoader().parse(node, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTSlideIdListEntry parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSlideIdListEntry)getTypeLoader().parse(node, CTSlideIdListEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSlideIdListEntry parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSlideIdListEntry)getTypeLoader().parse(xmlInputStream, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSlideIdListEntry parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSlideIdListEntry)getTypeLoader().parse(xmlInputStream, CTSlideIdListEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideIdListEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSlideIdListEntry.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
