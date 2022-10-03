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

public interface CTDefinedNames extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDefinedNames.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdefinednamesce48type");
    
    List<CTDefinedName> getDefinedNameList();
    
    @Deprecated
    CTDefinedName[] getDefinedNameArray();
    
    CTDefinedName getDefinedNameArray(final int p0);
    
    int sizeOfDefinedNameArray();
    
    void setDefinedNameArray(final CTDefinedName[] p0);
    
    void setDefinedNameArray(final int p0, final CTDefinedName p1);
    
    CTDefinedName insertNewDefinedName(final int p0);
    
    CTDefinedName addNewDefinedName();
    
    void removeDefinedName(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDefinedNames.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDefinedNames newInstance() {
            return (CTDefinedNames)getTypeLoader().newInstance(CTDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTDefinedNames newInstance(final XmlOptions xmlOptions) {
            return (CTDefinedNames)getTypeLoader().newInstance(CTDefinedNames.type, xmlOptions);
        }
        
        public static CTDefinedNames parse(final String s) throws XmlException {
            return (CTDefinedNames)getTypeLoader().parse(s, CTDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTDefinedNames parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDefinedNames)getTypeLoader().parse(s, CTDefinedNames.type, xmlOptions);
        }
        
        public static CTDefinedNames parse(final File file) throws XmlException, IOException {
            return (CTDefinedNames)getTypeLoader().parse(file, CTDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTDefinedNames parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDefinedNames)getTypeLoader().parse(file, CTDefinedNames.type, xmlOptions);
        }
        
        public static CTDefinedNames parse(final URL url) throws XmlException, IOException {
            return (CTDefinedNames)getTypeLoader().parse(url, CTDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTDefinedNames parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDefinedNames)getTypeLoader().parse(url, CTDefinedNames.type, xmlOptions);
        }
        
        public static CTDefinedNames parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDefinedNames)getTypeLoader().parse(inputStream, CTDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTDefinedNames parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDefinedNames)getTypeLoader().parse(inputStream, CTDefinedNames.type, xmlOptions);
        }
        
        public static CTDefinedNames parse(final Reader reader) throws XmlException, IOException {
            return (CTDefinedNames)getTypeLoader().parse(reader, CTDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTDefinedNames parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDefinedNames)getTypeLoader().parse(reader, CTDefinedNames.type, xmlOptions);
        }
        
        public static CTDefinedNames parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDefinedNames)getTypeLoader().parse(xmlStreamReader, CTDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTDefinedNames parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDefinedNames)getTypeLoader().parse(xmlStreamReader, CTDefinedNames.type, xmlOptions);
        }
        
        public static CTDefinedNames parse(final Node node) throws XmlException {
            return (CTDefinedNames)getTypeLoader().parse(node, CTDefinedNames.type, (XmlOptions)null);
        }
        
        public static CTDefinedNames parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDefinedNames)getTypeLoader().parse(node, CTDefinedNames.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDefinedNames parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDefinedNames)getTypeLoader().parse(xmlInputStream, CTDefinedNames.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDefinedNames parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDefinedNames)getTypeLoader().parse(xmlInputStream, CTDefinedNames.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDefinedNames.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDefinedNames.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
