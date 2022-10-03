package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CommentsDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CommentsDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("comments3da0doctype");
    
    CTComments getComments();
    
    void setComments(final CTComments p0);
    
    CTComments addNewComments();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CommentsDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CommentsDocument newInstance() {
            return (CommentsDocument)getTypeLoader().newInstance(CommentsDocument.type, (XmlOptions)null);
        }
        
        public static CommentsDocument newInstance(final XmlOptions xmlOptions) {
            return (CommentsDocument)getTypeLoader().newInstance(CommentsDocument.type, xmlOptions);
        }
        
        public static CommentsDocument parse(final String s) throws XmlException {
            return (CommentsDocument)getTypeLoader().parse(s, CommentsDocument.type, (XmlOptions)null);
        }
        
        public static CommentsDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CommentsDocument)getTypeLoader().parse(s, CommentsDocument.type, xmlOptions);
        }
        
        public static CommentsDocument parse(final File file) throws XmlException, IOException {
            return (CommentsDocument)getTypeLoader().parse(file, CommentsDocument.type, (XmlOptions)null);
        }
        
        public static CommentsDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CommentsDocument)getTypeLoader().parse(file, CommentsDocument.type, xmlOptions);
        }
        
        public static CommentsDocument parse(final URL url) throws XmlException, IOException {
            return (CommentsDocument)getTypeLoader().parse(url, CommentsDocument.type, (XmlOptions)null);
        }
        
        public static CommentsDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CommentsDocument)getTypeLoader().parse(url, CommentsDocument.type, xmlOptions);
        }
        
        public static CommentsDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (CommentsDocument)getTypeLoader().parse(inputStream, CommentsDocument.type, (XmlOptions)null);
        }
        
        public static CommentsDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CommentsDocument)getTypeLoader().parse(inputStream, CommentsDocument.type, xmlOptions);
        }
        
        public static CommentsDocument parse(final Reader reader) throws XmlException, IOException {
            return (CommentsDocument)getTypeLoader().parse(reader, CommentsDocument.type, (XmlOptions)null);
        }
        
        public static CommentsDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CommentsDocument)getTypeLoader().parse(reader, CommentsDocument.type, xmlOptions);
        }
        
        public static CommentsDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CommentsDocument)getTypeLoader().parse(xmlStreamReader, CommentsDocument.type, (XmlOptions)null);
        }
        
        public static CommentsDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CommentsDocument)getTypeLoader().parse(xmlStreamReader, CommentsDocument.type, xmlOptions);
        }
        
        public static CommentsDocument parse(final Node node) throws XmlException {
            return (CommentsDocument)getTypeLoader().parse(node, CommentsDocument.type, (XmlOptions)null);
        }
        
        public static CommentsDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CommentsDocument)getTypeLoader().parse(node, CommentsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static CommentsDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CommentsDocument)getTypeLoader().parse(xmlInputStream, CommentsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CommentsDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CommentsDocument)getTypeLoader().parse(xmlInputStream, CommentsDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CommentsDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CommentsDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
