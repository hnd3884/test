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

public interface CTExternalSheetName extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalSheetName.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternalsheetnamefcdetype");
    
    String getVal();
    
    STXstring xgetVal();
    
    boolean isSetVal();
    
    void setVal(final String p0);
    
    void xsetVal(final STXstring p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalSheetName.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalSheetName newInstance() {
            return (CTExternalSheetName)getTypeLoader().newInstance(CTExternalSheetName.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetName newInstance(final XmlOptions xmlOptions) {
            return (CTExternalSheetName)getTypeLoader().newInstance(CTExternalSheetName.type, xmlOptions);
        }
        
        public static CTExternalSheetName parse(final String s) throws XmlException {
            return (CTExternalSheetName)getTypeLoader().parse(s, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetName parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalSheetName)getTypeLoader().parse(s, CTExternalSheetName.type, xmlOptions);
        }
        
        public static CTExternalSheetName parse(final File file) throws XmlException, IOException {
            return (CTExternalSheetName)getTypeLoader().parse(file, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetName parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalSheetName)getTypeLoader().parse(file, CTExternalSheetName.type, xmlOptions);
        }
        
        public static CTExternalSheetName parse(final URL url) throws XmlException, IOException {
            return (CTExternalSheetName)getTypeLoader().parse(url, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetName parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalSheetName)getTypeLoader().parse(url, CTExternalSheetName.type, xmlOptions);
        }
        
        public static CTExternalSheetName parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalSheetName)getTypeLoader().parse(inputStream, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetName parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalSheetName)getTypeLoader().parse(inputStream, CTExternalSheetName.type, xmlOptions);
        }
        
        public static CTExternalSheetName parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalSheetName)getTypeLoader().parse(reader, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetName parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalSheetName)getTypeLoader().parse(reader, CTExternalSheetName.type, xmlOptions);
        }
        
        public static CTExternalSheetName parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalSheetName)getTypeLoader().parse(xmlStreamReader, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetName parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalSheetName)getTypeLoader().parse(xmlStreamReader, CTExternalSheetName.type, xmlOptions);
        }
        
        public static CTExternalSheetName parse(final Node node) throws XmlException {
            return (CTExternalSheetName)getTypeLoader().parse(node, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        public static CTExternalSheetName parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalSheetName)getTypeLoader().parse(node, CTExternalSheetName.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalSheetName parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalSheetName)getTypeLoader().parse(xmlInputStream, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalSheetName parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalSheetName)getTypeLoader().parse(xmlInputStream, CTExternalSheetName.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalSheetName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalSheetName.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
