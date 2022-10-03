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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTComments extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTComments.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcommentse3bdtype");
    
    CTAuthors getAuthors();
    
    void setAuthors(final CTAuthors p0);
    
    CTAuthors addNewAuthors();
    
    CTCommentList getCommentList();
    
    void setCommentList(final CTCommentList p0);
    
    CTCommentList addNewCommentList();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTComments.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTComments newInstance() {
            return (CTComments)getTypeLoader().newInstance(CTComments.type, (XmlOptions)null);
        }
        
        public static CTComments newInstance(final XmlOptions xmlOptions) {
            return (CTComments)getTypeLoader().newInstance(CTComments.type, xmlOptions);
        }
        
        public static CTComments parse(final String s) throws XmlException {
            return (CTComments)getTypeLoader().parse(s, CTComments.type, (XmlOptions)null);
        }
        
        public static CTComments parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTComments)getTypeLoader().parse(s, CTComments.type, xmlOptions);
        }
        
        public static CTComments parse(final File file) throws XmlException, IOException {
            return (CTComments)getTypeLoader().parse(file, CTComments.type, (XmlOptions)null);
        }
        
        public static CTComments parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTComments)getTypeLoader().parse(file, CTComments.type, xmlOptions);
        }
        
        public static CTComments parse(final URL url) throws XmlException, IOException {
            return (CTComments)getTypeLoader().parse(url, CTComments.type, (XmlOptions)null);
        }
        
        public static CTComments parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTComments)getTypeLoader().parse(url, CTComments.type, xmlOptions);
        }
        
        public static CTComments parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTComments)getTypeLoader().parse(inputStream, CTComments.type, (XmlOptions)null);
        }
        
        public static CTComments parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTComments)getTypeLoader().parse(inputStream, CTComments.type, xmlOptions);
        }
        
        public static CTComments parse(final Reader reader) throws XmlException, IOException {
            return (CTComments)getTypeLoader().parse(reader, CTComments.type, (XmlOptions)null);
        }
        
        public static CTComments parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTComments)getTypeLoader().parse(reader, CTComments.type, xmlOptions);
        }
        
        public static CTComments parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTComments)getTypeLoader().parse(xmlStreamReader, CTComments.type, (XmlOptions)null);
        }
        
        public static CTComments parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTComments)getTypeLoader().parse(xmlStreamReader, CTComments.type, xmlOptions);
        }
        
        public static CTComments parse(final Node node) throws XmlException {
            return (CTComments)getTypeLoader().parse(node, CTComments.type, (XmlOptions)null);
        }
        
        public static CTComments parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTComments)getTypeLoader().parse(node, CTComments.type, xmlOptions);
        }
        
        @Deprecated
        public static CTComments parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTComments)getTypeLoader().parse(xmlInputStream, CTComments.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTComments parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTComments)getTypeLoader().parse(xmlInputStream, CTComments.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTComments.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTComments.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
