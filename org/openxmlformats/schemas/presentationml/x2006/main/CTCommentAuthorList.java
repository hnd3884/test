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

public interface CTCommentAuthorList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCommentAuthorList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcommentauthorlisteb07type");
    
    List<CTCommentAuthor> getCmAuthorList();
    
    @Deprecated
    CTCommentAuthor[] getCmAuthorArray();
    
    CTCommentAuthor getCmAuthorArray(final int p0);
    
    int sizeOfCmAuthorArray();
    
    void setCmAuthorArray(final CTCommentAuthor[] p0);
    
    void setCmAuthorArray(final int p0, final CTCommentAuthor p1);
    
    CTCommentAuthor insertNewCmAuthor(final int p0);
    
    CTCommentAuthor addNewCmAuthor();
    
    void removeCmAuthor(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCommentAuthorList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCommentAuthorList newInstance() {
            return (CTCommentAuthorList)getTypeLoader().newInstance(CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthorList newInstance(final XmlOptions xmlOptions) {
            return (CTCommentAuthorList)getTypeLoader().newInstance(CTCommentAuthorList.type, xmlOptions);
        }
        
        public static CTCommentAuthorList parse(final String s) throws XmlException {
            return (CTCommentAuthorList)getTypeLoader().parse(s, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthorList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentAuthorList)getTypeLoader().parse(s, CTCommentAuthorList.type, xmlOptions);
        }
        
        public static CTCommentAuthorList parse(final File file) throws XmlException, IOException {
            return (CTCommentAuthorList)getTypeLoader().parse(file, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthorList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentAuthorList)getTypeLoader().parse(file, CTCommentAuthorList.type, xmlOptions);
        }
        
        public static CTCommentAuthorList parse(final URL url) throws XmlException, IOException {
            return (CTCommentAuthorList)getTypeLoader().parse(url, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthorList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentAuthorList)getTypeLoader().parse(url, CTCommentAuthorList.type, xmlOptions);
        }
        
        public static CTCommentAuthorList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCommentAuthorList)getTypeLoader().parse(inputStream, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthorList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentAuthorList)getTypeLoader().parse(inputStream, CTCommentAuthorList.type, xmlOptions);
        }
        
        public static CTCommentAuthorList parse(final Reader reader) throws XmlException, IOException {
            return (CTCommentAuthorList)getTypeLoader().parse(reader, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthorList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCommentAuthorList)getTypeLoader().parse(reader, CTCommentAuthorList.type, xmlOptions);
        }
        
        public static CTCommentAuthorList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCommentAuthorList)getTypeLoader().parse(xmlStreamReader, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthorList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentAuthorList)getTypeLoader().parse(xmlStreamReader, CTCommentAuthorList.type, xmlOptions);
        }
        
        public static CTCommentAuthorList parse(final Node node) throws XmlException {
            return (CTCommentAuthorList)getTypeLoader().parse(node, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        public static CTCommentAuthorList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCommentAuthorList)getTypeLoader().parse(node, CTCommentAuthorList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCommentAuthorList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCommentAuthorList)getTypeLoader().parse(xmlInputStream, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCommentAuthorList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCommentAuthorList)getTypeLoader().parse(xmlInputStream, CTCommentAuthorList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCommentAuthorList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCommentAuthorList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
