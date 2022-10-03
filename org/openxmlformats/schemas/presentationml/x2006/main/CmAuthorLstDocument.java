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

public interface CmAuthorLstDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CmAuthorLstDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cmauthorlst86abdoctype");
    
    CTCommentAuthorList getCmAuthorLst();
    
    void setCmAuthorLst(final CTCommentAuthorList p0);
    
    CTCommentAuthorList addNewCmAuthorLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CmAuthorLstDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CmAuthorLstDocument newInstance() {
            return (CmAuthorLstDocument)getTypeLoader().newInstance(CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        public static CmAuthorLstDocument newInstance(final XmlOptions xmlOptions) {
            return (CmAuthorLstDocument)getTypeLoader().newInstance(CmAuthorLstDocument.type, xmlOptions);
        }
        
        public static CmAuthorLstDocument parse(final String s) throws XmlException {
            return (CmAuthorLstDocument)getTypeLoader().parse(s, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        public static CmAuthorLstDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CmAuthorLstDocument)getTypeLoader().parse(s, CmAuthorLstDocument.type, xmlOptions);
        }
        
        public static CmAuthorLstDocument parse(final File file) throws XmlException, IOException {
            return (CmAuthorLstDocument)getTypeLoader().parse(file, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        public static CmAuthorLstDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CmAuthorLstDocument)getTypeLoader().parse(file, CmAuthorLstDocument.type, xmlOptions);
        }
        
        public static CmAuthorLstDocument parse(final URL url) throws XmlException, IOException {
            return (CmAuthorLstDocument)getTypeLoader().parse(url, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        public static CmAuthorLstDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CmAuthorLstDocument)getTypeLoader().parse(url, CmAuthorLstDocument.type, xmlOptions);
        }
        
        public static CmAuthorLstDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (CmAuthorLstDocument)getTypeLoader().parse(inputStream, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        public static CmAuthorLstDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CmAuthorLstDocument)getTypeLoader().parse(inputStream, CmAuthorLstDocument.type, xmlOptions);
        }
        
        public static CmAuthorLstDocument parse(final Reader reader) throws XmlException, IOException {
            return (CmAuthorLstDocument)getTypeLoader().parse(reader, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        public static CmAuthorLstDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CmAuthorLstDocument)getTypeLoader().parse(reader, CmAuthorLstDocument.type, xmlOptions);
        }
        
        public static CmAuthorLstDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CmAuthorLstDocument)getTypeLoader().parse(xmlStreamReader, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        public static CmAuthorLstDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CmAuthorLstDocument)getTypeLoader().parse(xmlStreamReader, CmAuthorLstDocument.type, xmlOptions);
        }
        
        public static CmAuthorLstDocument parse(final Node node) throws XmlException {
            return (CmAuthorLstDocument)getTypeLoader().parse(node, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        public static CmAuthorLstDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CmAuthorLstDocument)getTypeLoader().parse(node, CmAuthorLstDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static CmAuthorLstDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CmAuthorLstDocument)getTypeLoader().parse(xmlInputStream, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CmAuthorLstDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CmAuthorLstDocument)getTypeLoader().parse(xmlInputStream, CmAuthorLstDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CmAuthorLstDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CmAuthorLstDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
