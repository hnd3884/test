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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPlaceholder extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPlaceholder.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctplaceholder9efctype");
    
    CTExtensionListModify getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionListModify p0);
    
    CTExtensionListModify addNewExtLst();
    
    void unsetExtLst();
    
    STPlaceholderType.Enum getType();
    
    STPlaceholderType xgetType();
    
    boolean isSetType();
    
    void setType(final STPlaceholderType.Enum p0);
    
    void xsetType(final STPlaceholderType p0);
    
    void unsetType();
    
    STDirection.Enum getOrient();
    
    STDirection xgetOrient();
    
    boolean isSetOrient();
    
    void setOrient(final STDirection.Enum p0);
    
    void xsetOrient(final STDirection p0);
    
    void unsetOrient();
    
    STPlaceholderSize.Enum getSz();
    
    STPlaceholderSize xgetSz();
    
    boolean isSetSz();
    
    void setSz(final STPlaceholderSize.Enum p0);
    
    void xsetSz(final STPlaceholderSize p0);
    
    void unsetSz();
    
    long getIdx();
    
    XmlUnsignedInt xgetIdx();
    
    boolean isSetIdx();
    
    void setIdx(final long p0);
    
    void xsetIdx(final XmlUnsignedInt p0);
    
    void unsetIdx();
    
    boolean getHasCustomPrompt();
    
    XmlBoolean xgetHasCustomPrompt();
    
    boolean isSetHasCustomPrompt();
    
    void setHasCustomPrompt(final boolean p0);
    
    void xsetHasCustomPrompt(final XmlBoolean p0);
    
    void unsetHasCustomPrompt();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPlaceholder.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPlaceholder newInstance() {
            return (CTPlaceholder)getTypeLoader().newInstance(CTPlaceholder.type, (XmlOptions)null);
        }
        
        public static CTPlaceholder newInstance(final XmlOptions xmlOptions) {
            return (CTPlaceholder)getTypeLoader().newInstance(CTPlaceholder.type, xmlOptions);
        }
        
        public static CTPlaceholder parse(final String s) throws XmlException {
            return (CTPlaceholder)getTypeLoader().parse(s, CTPlaceholder.type, (XmlOptions)null);
        }
        
        public static CTPlaceholder parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPlaceholder)getTypeLoader().parse(s, CTPlaceholder.type, xmlOptions);
        }
        
        public static CTPlaceholder parse(final File file) throws XmlException, IOException {
            return (CTPlaceholder)getTypeLoader().parse(file, CTPlaceholder.type, (XmlOptions)null);
        }
        
        public static CTPlaceholder parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPlaceholder)getTypeLoader().parse(file, CTPlaceholder.type, xmlOptions);
        }
        
        public static CTPlaceholder parse(final URL url) throws XmlException, IOException {
            return (CTPlaceholder)getTypeLoader().parse(url, CTPlaceholder.type, (XmlOptions)null);
        }
        
        public static CTPlaceholder parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPlaceholder)getTypeLoader().parse(url, CTPlaceholder.type, xmlOptions);
        }
        
        public static CTPlaceholder parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPlaceholder)getTypeLoader().parse(inputStream, CTPlaceholder.type, (XmlOptions)null);
        }
        
        public static CTPlaceholder parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPlaceholder)getTypeLoader().parse(inputStream, CTPlaceholder.type, xmlOptions);
        }
        
        public static CTPlaceholder parse(final Reader reader) throws XmlException, IOException {
            return (CTPlaceholder)getTypeLoader().parse(reader, CTPlaceholder.type, (XmlOptions)null);
        }
        
        public static CTPlaceholder parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPlaceholder)getTypeLoader().parse(reader, CTPlaceholder.type, xmlOptions);
        }
        
        public static CTPlaceholder parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPlaceholder)getTypeLoader().parse(xmlStreamReader, CTPlaceholder.type, (XmlOptions)null);
        }
        
        public static CTPlaceholder parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPlaceholder)getTypeLoader().parse(xmlStreamReader, CTPlaceholder.type, xmlOptions);
        }
        
        public static CTPlaceholder parse(final Node node) throws XmlException {
            return (CTPlaceholder)getTypeLoader().parse(node, CTPlaceholder.type, (XmlOptions)null);
        }
        
        public static CTPlaceholder parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPlaceholder)getTypeLoader().parse(node, CTPlaceholder.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPlaceholder parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPlaceholder)getTypeLoader().parse(xmlInputStream, CTPlaceholder.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPlaceholder parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPlaceholder)getTypeLoader().parse(xmlInputStream, CTPlaceholder.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPlaceholder.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPlaceholder.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
