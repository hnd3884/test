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

public interface CTExternalSheetNames extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalSheetNames.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternalsheetnames7eddtype");
    
    List<CTExternalSheetName> getSheetNameList();
    
    @Deprecated
    CTExternalSheetName[] getSheetNameArray();
    
    CTExternalSheetName getSheetNameArray(final int p0);
    
    int sizeOfSheetNameArray();
    
    void setSheetNameArray(final CTExternalSheetName[] p0);
    
    void setSheetNameArray(final int p0, final CTExternalSheetName p1);
    
    CTExternalSheetName insertNewSheetName(final int p0);
    
    CTExternalSheetName addNewSheetName();
    
    void removeSheetName(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalSheetNames.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalSheetNames newInstance() {
            return (CTExternalSheetNames)getTypeLoader().newInstance(CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetNames newInstance(final XmlOptions xmlOptions) {
            return (CTExternalSheetNames)getTypeLoader().newInstance(CTExternalSheetNames.type, xmlOptions);
        }
        
        public static CTExternalSheetNames parse(final String s) throws XmlException {
            return (CTExternalSheetNames)getTypeLoader().parse(s, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetNames parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalSheetNames)getTypeLoader().parse(s, CTExternalSheetNames.type, xmlOptions);
        }
        
        public static CTExternalSheetNames parse(final File file) throws XmlException, IOException {
            return (CTExternalSheetNames)getTypeLoader().parse(file, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetNames parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalSheetNames)getTypeLoader().parse(file, CTExternalSheetNames.type, xmlOptions);
        }
        
        public static CTExternalSheetNames parse(final URL url) throws XmlException, IOException {
            return (CTExternalSheetNames)getTypeLoader().parse(url, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetNames parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalSheetNames)getTypeLoader().parse(url, CTExternalSheetNames.type, xmlOptions);
        }
        
        public static CTExternalSheetNames parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalSheetNames)getTypeLoader().parse(inputStream, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetNames parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalSheetNames)getTypeLoader().parse(inputStream, CTExternalSheetNames.type, xmlOptions);
        }
        
        public static CTExternalSheetNames parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalSheetNames)getTypeLoader().parse(reader, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetNames parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalSheetNames)getTypeLoader().parse(reader, CTExternalSheetNames.type, xmlOptions);
        }
        
        public static CTExternalSheetNames parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalSheetNames)getTypeLoader().parse(xmlStreamReader, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetNames parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalSheetNames)getTypeLoader().parse(xmlStreamReader, CTExternalSheetNames.type, xmlOptions);
        }
        
        public static CTExternalSheetNames parse(final Node node) throws XmlException {
            return (CTExternalSheetNames)getTypeLoader().parse(node, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetNames parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalSheetNames)getTypeLoader().parse(node, CTExternalSheetNames.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalSheetNames parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalSheetNames)getTypeLoader().parse(xmlInputStream, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalSheetNames parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalSheetNames)getTypeLoader().parse(xmlInputStream, CTExternalSheetNames.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalSheetNames.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalSheetNames.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
