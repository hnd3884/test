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

public interface CTExternalLink extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalLink.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternallink966etype");
    
    CTExternalBook getExternalBook();
    
    boolean isSetExternalBook();
    
    void setExternalBook(final CTExternalBook p0);
    
    CTExternalBook addNewExternalBook();
    
    void unsetExternalBook();
    
    CTDdeLink getDdeLink();
    
    boolean isSetDdeLink();
    
    void setDdeLink(final CTDdeLink p0);
    
    CTDdeLink addNewDdeLink();
    
    void unsetDdeLink();
    
    CTOleLink getOleLink();
    
    boolean isSetOleLink();
    
    void setOleLink(final CTOleLink p0);
    
    CTOleLink addNewOleLink();
    
    void unsetOleLink();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalLink.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalLink newInstance() {
            return (CTExternalLink)getTypeLoader().newInstance(CTExternalLink.type, (XmlOptions)null);
        }
        
        public static CTExternalLink newInstance(final XmlOptions xmlOptions) {
            return (CTExternalLink)getTypeLoader().newInstance(CTExternalLink.type, xmlOptions);
        }
        
        public static CTExternalLink parse(final String s) throws XmlException {
            return (CTExternalLink)getTypeLoader().parse(s, CTExternalLink.type, (XmlOptions)null);
        }
        
        public static CTExternalLink parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalLink)getTypeLoader().parse(s, CTExternalLink.type, xmlOptions);
        }
        
        public static CTExternalLink parse(final File file) throws XmlException, IOException {
            return (CTExternalLink)getTypeLoader().parse(file, CTExternalLink.type, (XmlOptions)null);
        }
        
        public static CTExternalLink parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalLink)getTypeLoader().parse(file, CTExternalLink.type, xmlOptions);
        }
        
        public static CTExternalLink parse(final URL url) throws XmlException, IOException {
            return (CTExternalLink)getTypeLoader().parse(url, CTExternalLink.type, (XmlOptions)null);
        }
        
        public static CTExternalLink parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalLink)getTypeLoader().parse(url, CTExternalLink.type, xmlOptions);
        }
        
        public static CTExternalLink parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalLink)getTypeLoader().parse(inputStream, CTExternalLink.type, (XmlOptions)null);
        }
        
        public static CTExternalLink parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalLink)getTypeLoader().parse(inputStream, CTExternalLink.type, xmlOptions);
        }
        
        public static CTExternalLink parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalLink)getTypeLoader().parse(reader, CTExternalLink.type, (XmlOptions)null);
        }
        
        public static CTExternalLink parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalLink)getTypeLoader().parse(reader, CTExternalLink.type, xmlOptions);
        }
        
        public static CTExternalLink parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalLink)getTypeLoader().parse(xmlStreamReader, CTExternalLink.type, (XmlOptions)null);
        }
        
        public static CTExternalLink parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalLink)getTypeLoader().parse(xmlStreamReader, CTExternalLink.type, xmlOptions);
        }
        
        public static CTExternalLink parse(final Node node) throws XmlException {
            return (CTExternalLink)getTypeLoader().parse(node, CTExternalLink.type, (XmlOptions)null);
        }
        
        public static CTExternalLink parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalLink)getTypeLoader().parse(node, CTExternalLink.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalLink parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalLink)getTypeLoader().parse(xmlInputStream, CTExternalLink.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalLink parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalLink)getTypeLoader().parse(xmlInputStream, CTExternalLink.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalLink.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalLink.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
