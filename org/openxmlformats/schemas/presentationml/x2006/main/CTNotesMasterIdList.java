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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNotesMasterIdList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNotesMasterIdList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnotesmasteridlist2853type");
    
    CTNotesMasterIdListEntry getNotesMasterId();
    
    boolean isSetNotesMasterId();
    
    void setNotesMasterId(final CTNotesMasterIdListEntry p0);
    
    CTNotesMasterIdListEntry addNewNotesMasterId();
    
    void unsetNotesMasterId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNotesMasterIdList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNotesMasterIdList newInstance() {
            return (CTNotesMasterIdList)getTypeLoader().newInstance(CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdList newInstance(final XmlOptions xmlOptions) {
            return (CTNotesMasterIdList)getTypeLoader().newInstance(CTNotesMasterIdList.type, xmlOptions);
        }
        
        public static CTNotesMasterIdList parse(final String s) throws XmlException {
            return (CTNotesMasterIdList)getTypeLoader().parse(s, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMasterIdList)getTypeLoader().parse(s, CTNotesMasterIdList.type, xmlOptions);
        }
        
        public static CTNotesMasterIdList parse(final File file) throws XmlException, IOException {
            return (CTNotesMasterIdList)getTypeLoader().parse(file, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMasterIdList)getTypeLoader().parse(file, CTNotesMasterIdList.type, xmlOptions);
        }
        
        public static CTNotesMasterIdList parse(final URL url) throws XmlException, IOException {
            return (CTNotesMasterIdList)getTypeLoader().parse(url, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMasterIdList)getTypeLoader().parse(url, CTNotesMasterIdList.type, xmlOptions);
        }
        
        public static CTNotesMasterIdList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNotesMasterIdList)getTypeLoader().parse(inputStream, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMasterIdList)getTypeLoader().parse(inputStream, CTNotesMasterIdList.type, xmlOptions);
        }
        
        public static CTNotesMasterIdList parse(final Reader reader) throws XmlException, IOException {
            return (CTNotesMasterIdList)getTypeLoader().parse(reader, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNotesMasterIdList)getTypeLoader().parse(reader, CTNotesMasterIdList.type, xmlOptions);
        }
        
        public static CTNotesMasterIdList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNotesMasterIdList)getTypeLoader().parse(xmlStreamReader, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMasterIdList)getTypeLoader().parse(xmlStreamReader, CTNotesMasterIdList.type, xmlOptions);
        }
        
        public static CTNotesMasterIdList parse(final Node node) throws XmlException {
            return (CTNotesMasterIdList)getTypeLoader().parse(node, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        public static CTNotesMasterIdList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNotesMasterIdList)getTypeLoader().parse(node, CTNotesMasterIdList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNotesMasterIdList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNotesMasterIdList)getTypeLoader().parse(xmlInputStream, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNotesMasterIdList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNotesMasterIdList)getTypeLoader().parse(xmlInputStream, CTNotesMasterIdList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNotesMasterIdList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNotesMasterIdList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
