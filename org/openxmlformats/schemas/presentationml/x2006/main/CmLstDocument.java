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

public interface CmLstDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CmLstDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cmlst3880doctype");
    
    CTCommentList getCmLst();
    
    void setCmLst(final CTCommentList p0);
    
    CTCommentList addNewCmLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CmLstDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CmLstDocument newInstance() {
            return (CmLstDocument)getTypeLoader().newInstance(CmLstDocument.type, (XmlOptions)null);
        }
        
        public static CmLstDocument newInstance(final XmlOptions xmlOptions) {
            return (CmLstDocument)getTypeLoader().newInstance(CmLstDocument.type, xmlOptions);
        }
        
        public static CmLstDocument parse(final String s) throws XmlException {
            return (CmLstDocument)getTypeLoader().parse(s, CmLstDocument.type, (XmlOptions)null);
        }
        
        public static CmLstDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CmLstDocument)getTypeLoader().parse(s, CmLstDocument.type, xmlOptions);
        }
        
        public static CmLstDocument parse(final File file) throws XmlException, IOException {
            return (CmLstDocument)getTypeLoader().parse(file, CmLstDocument.type, (XmlOptions)null);
        }
        
        public static CmLstDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CmLstDocument)getTypeLoader().parse(file, CmLstDocument.type, xmlOptions);
        }
        
        public static CmLstDocument parse(final URL url) throws XmlException, IOException {
            return (CmLstDocument)getTypeLoader().parse(url, CmLstDocument.type, (XmlOptions)null);
        }
        
        public static CmLstDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CmLstDocument)getTypeLoader().parse(url, CmLstDocument.type, xmlOptions);
        }
        
        public static CmLstDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (CmLstDocument)getTypeLoader().parse(inputStream, CmLstDocument.type, (XmlOptions)null);
        }
        
        public static CmLstDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CmLstDocument)getTypeLoader().parse(inputStream, CmLstDocument.type, xmlOptions);
        }
        
        public static CmLstDocument parse(final Reader reader) throws XmlException, IOException {
            return (CmLstDocument)getTypeLoader().parse(reader, CmLstDocument.type, (XmlOptions)null);
        }
        
        public static CmLstDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CmLstDocument)getTypeLoader().parse(reader, CmLstDocument.type, xmlOptions);
        }
        
        public static CmLstDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CmLstDocument)getTypeLoader().parse(xmlStreamReader, CmLstDocument.type, (XmlOptions)null);
        }
        
        public static CmLstDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CmLstDocument)getTypeLoader().parse(xmlStreamReader, CmLstDocument.type, xmlOptions);
        }
        
        public static CmLstDocument parse(final Node node) throws XmlException {
            return (CmLstDocument)getTypeLoader().parse(node, CmLstDocument.type, (XmlOptions)null);
        }
        
        public static CmLstDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CmLstDocument)getTypeLoader().parse(node, CmLstDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static CmLstDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CmLstDocument)getTypeLoader().parse(xmlInputStream, CmLstDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CmLstDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CmLstDocument)getTypeLoader().parse(xmlInputStream, CmLstDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CmLstDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CmLstDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
