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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCommentList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCommentList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcommentlist7a3ctype");
    
    List<CTComment> getCommentList();
    
    @Deprecated
    CTComment[] getCommentArray();
    
    CTComment getCommentArray(final int p0);
    
    int sizeOfCommentArray();
    
    void setCommentArray(final CTComment[] p0);
    
    void setCommentArray(final int p0, final CTComment p1);
    
    CTComment insertNewComment(final int p0);
    
    CTComment addNewComment();
    
    void removeComment(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCommentList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCommentList newInstance() {
            return (CTCommentList)getTypeLoader().newInstance(CTCommentList.type, (XmlOptions)null);
        }
        
        public static CTCommentList newInstance(final XmlOptions xmlOptions) {
            return (CTCommentList)getTypeLoader().newInstance(CTCommentList.type, xmlOptions);
        }
        
        public static CTCommentList parse(final String s) throws XmlException {
            return (CTCommentList)getTypeLoader().parse(s, CTCommentList.type, (XmlOptions)null);
        }
        
        public static CTCommentList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentList)getTypeLoader().parse(s, CTCommentList.type, xmlOptions);
        }
        
        public static CTCommentList parse(final File file) throws XmlException, IOException {
            return (CTCommentList)getTypeLoader().parse(file, CTCommentList.type, (XmlOptions)null);
        }
        
        public static CTCommentList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentList)getTypeLoader().parse(file, CTCommentList.type, xmlOptions);
        }
        
        public static CTCommentList parse(final URL url) throws XmlException, IOException {
            return (CTCommentList)getTypeLoader().parse(url, CTCommentList.type, (XmlOptions)null);
        }
        
        public static CTCommentList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentList)getTypeLoader().parse(url, CTCommentList.type, xmlOptions);
        }
        
        public static CTCommentList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCommentList)getTypeLoader().parse(inputStream, CTCommentList.type, (XmlOptions)null);
        }
        
        public static CTCommentList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentList)getTypeLoader().parse(inputStream, CTCommentList.type, xmlOptions);
        }
        
        public static CTCommentList parse(final Reader reader) throws XmlException, IOException {
            return (CTCommentList)getTypeLoader().parse(reader, CTCommentList.type, (XmlOptions)null);
        }
        
        public static CTCommentList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentList)getTypeLoader().parse(reader, CTCommentList.type, xmlOptions);
        }
        
        public static CTCommentList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCommentList)getTypeLoader().parse(xmlStreamReader, CTCommentList.type, (XmlOptions)null);
        }
        
        public static CTCommentList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentList)getTypeLoader().parse(xmlStreamReader, CTCommentList.type, xmlOptions);
        }
        
        public static CTCommentList parse(final Node node) throws XmlException {
            return (CTCommentList)getTypeLoader().parse(node, CTCommentList.type, (XmlOptions)null);
        }
        
        public static CTCommentList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentList)getTypeLoader().parse(node, CTCommentList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCommentList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCommentList)getTypeLoader().parse(xmlInputStream, CTCommentList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCommentList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCommentList)getTypeLoader().parse(xmlInputStream, CTCommentList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCommentList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCommentList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
