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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCommentAuthor extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCommentAuthor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcommentauthora405type");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getId();
    
    XmlUnsignedInt xgetId();
    
    void setId(final long p0);
    
    void xsetId(final XmlUnsignedInt p0);
    
    String getName();
    
    STName xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STName p0);
    
    String getInitials();
    
    STName xgetInitials();
    
    void setInitials(final String p0);
    
    void xsetInitials(final STName p0);
    
    long getLastIdx();
    
    XmlUnsignedInt xgetLastIdx();
    
    void setLastIdx(final long p0);
    
    void xsetLastIdx(final XmlUnsignedInt p0);
    
    long getClrIdx();
    
    XmlUnsignedInt xgetClrIdx();
    
    void setClrIdx(final long p0);
    
    void xsetClrIdx(final XmlUnsignedInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCommentAuthor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCommentAuthor newInstance() {
            return (CTCommentAuthor)getTypeLoader().newInstance(CTCommentAuthor.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthor newInstance(final XmlOptions xmlOptions) {
            return (CTCommentAuthor)getTypeLoader().newInstance(CTCommentAuthor.type, xmlOptions);
        }
        
        public static CTCommentAuthor parse(final String s) throws XmlException {
            return (CTCommentAuthor)getTypeLoader().parse(s, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentAuthor)getTypeLoader().parse(s, CTCommentAuthor.type, xmlOptions);
        }
        
        public static CTCommentAuthor parse(final File file) throws XmlException, IOException {
            return (CTCommentAuthor)getTypeLoader().parse(file, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentAuthor)getTypeLoader().parse(file, CTCommentAuthor.type, xmlOptions);
        }
        
        public static CTCommentAuthor parse(final URL url) throws XmlException, IOException {
            return (CTCommentAuthor)getTypeLoader().parse(url, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentAuthor)getTypeLoader().parse(url, CTCommentAuthor.type, xmlOptions);
        }
        
        public static CTCommentAuthor parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCommentAuthor)getTypeLoader().parse(inputStream, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentAuthor)getTypeLoader().parse(inputStream, CTCommentAuthor.type, xmlOptions);
        }
        
        public static CTCommentAuthor parse(final Reader reader) throws XmlException, IOException {
            return (CTCommentAuthor)getTypeLoader().parse(reader, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentAuthor)getTypeLoader().parse(reader, CTCommentAuthor.type, xmlOptions);
        }
        
        public static CTCommentAuthor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCommentAuthor)getTypeLoader().parse(xmlStreamReader, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentAuthor)getTypeLoader().parse(xmlStreamReader, CTCommentAuthor.type, xmlOptions);
        }
        
        public static CTCommentAuthor parse(final Node node) throws XmlException {
            return (CTCommentAuthor)getTypeLoader().parse(node, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentAuthor)getTypeLoader().parse(node, CTCommentAuthor.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCommentAuthor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCommentAuthor)getTypeLoader().parse(xmlInputStream, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCommentAuthor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCommentAuthor)getTypeLoader().parse(xmlInputStream, CTCommentAuthor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCommentAuthor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCommentAuthor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
