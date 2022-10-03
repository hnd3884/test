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

public interface CTExternalDefinedNames extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalDefinedNames.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternaldefinednamesccf3type");
    
    List<CTExternalDefinedName> getDefinedNameList();
    
    @Deprecated
    CTExternalDefinedName[] getDefinedNameArray();
    
    CTExternalDefinedName getDefinedNameArray(final int p0);
    
    int sizeOfDefinedNameArray();
    
    void setDefinedNameArray(final CTExternalDefinedName[] p0);
    
    void setDefinedNameArray(final int p0, final CTExternalDefinedName p1);
    
    CTExternalDefinedName insertNewDefinedName(final int p0);
    
    CTExternalDefinedName addNewDefinedName();
    
    void removeDefinedName(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalDefinedNames.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalDefinedNames newInstance() {
            return (CTExternalDefinedNames)getTypeLoader().newInstance(CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedNames newInstance(final XmlOptions xmlOptions) {
            return (CTExternalDefinedNames)getTypeLoader().newInstance(CTExternalDefinedNames.type, xmlOptions);
        }
        
        public static CTExternalDefinedNames parse(final String s) throws XmlException {
            return (CTExternalDefinedNames)getTypeLoader().parse(s, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedNames parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalDefinedNames)getTypeLoader().parse(s, CTExternalDefinedNames.type, xmlOptions);
        }
        
        public static CTExternalDefinedNames parse(final File file) throws XmlException, IOException {
            return (CTExternalDefinedNames)getTypeLoader().parse(file, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedNames parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalDefinedNames)getTypeLoader().parse(file, CTExternalDefinedNames.type, xmlOptions);
        }
        
        public static CTExternalDefinedNames parse(final URL url) throws XmlException, IOException {
            return (CTExternalDefinedNames)getTypeLoader().parse(url, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedNames parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalDefinedNames)getTypeLoader().parse(url, CTExternalDefinedNames.type, xmlOptions);
        }
        
        public static CTExternalDefinedNames parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalDefinedNames)getTypeLoader().parse(inputStream, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedNames parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalDefinedNames)getTypeLoader().parse(inputStream, CTExternalDefinedNames.type, xmlOptions);
        }
        
        public static CTExternalDefinedNames parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalDefinedNames)getTypeLoader().parse(reader, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedNames parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalDefinedNames)getTypeLoader().parse(reader, CTExternalDefinedNames.type, xmlOptions);
        }
        
        public static CTExternalDefinedNames parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalDefinedNames)getTypeLoader().parse(xmlStreamReader, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedNames parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalDefinedNames)getTypeLoader().parse(xmlStreamReader, CTExternalDefinedNames.type, xmlOptions);
        }
        
        public static CTExternalDefinedNames parse(final Node node) throws XmlException {
            return (CTExternalDefinedNames)getTypeLoader().parse(node, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedNames parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalDefinedNames)getTypeLoader().parse(node, CTExternalDefinedNames.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalDefinedNames parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalDefinedNames)getTypeLoader().parse(xmlInputStream, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalDefinedNames parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalDefinedNames)getTypeLoader().parse(xmlInputStream, CTExternalDefinedNames.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalDefinedNames.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalDefinedNames.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
