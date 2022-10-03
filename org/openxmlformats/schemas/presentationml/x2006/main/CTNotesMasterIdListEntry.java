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

public interface CTNotesMasterIdListEntry extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNotesMasterIdListEntry.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnotesmasteridlistentry278ftype");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getId();
    
    STRelationshipId xgetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNotesMasterIdListEntry.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNotesMasterIdListEntry newInstance() {
            return (CTNotesMasterIdListEntry)getTypeLoader().newInstance(CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdListEntry newInstance(final XmlOptions xmlOptions) {
            return (CTNotesMasterIdListEntry)getTypeLoader().newInstance(CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTNotesMasterIdListEntry parse(final String s) throws XmlException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(s, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdListEntry parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(s, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTNotesMasterIdListEntry parse(final File file) throws XmlException, IOException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(file, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdListEntry parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(file, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTNotesMasterIdListEntry parse(final URL url) throws XmlException, IOException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(url, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdListEntry parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(url, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTNotesMasterIdListEntry parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(inputStream, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdListEntry parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(inputStream, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTNotesMasterIdListEntry parse(final Reader reader) throws XmlException, IOException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(reader, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdListEntry parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(reader, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTNotesMasterIdListEntry parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(xmlStreamReader, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdListEntry parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(xmlStreamReader, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        public static CTNotesMasterIdListEntry parse(final Node node) throws XmlException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(node, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdListEntry parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(node, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNotesMasterIdListEntry parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(xmlInputStream, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNotesMasterIdListEntry parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNotesMasterIdListEntry)getTypeLoader().parse(xmlInputStream, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNotesMasterIdListEntry.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNotesMasterIdListEntry.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
