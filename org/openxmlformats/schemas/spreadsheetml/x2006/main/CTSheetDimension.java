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

public interface CTSheetDimension extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheetDimension.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheetdimensiond310type");
    
    String getRef();
    
    STRef xgetRef();
    
    void setRef(final String p0);
    
    void xsetRef(final STRef p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheetDimension.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheetDimension newInstance() {
            return (CTSheetDimension)getTypeLoader().newInstance(CTSheetDimension.type, (XmlOptions)null);
        }
        
        public static CTSheetDimension newInstance(final XmlOptions xmlOptions) {
            return (CTSheetDimension)getTypeLoader().newInstance(CTSheetDimension.type, xmlOptions);
        }
        
        public static CTSheetDimension parse(final String s) throws XmlException {
            return (CTSheetDimension)getTypeLoader().parse(s, CTSheetDimension.type, (XmlOptions)null);
        }
        
        public static CTSheetDimension parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetDimension)getTypeLoader().parse(s, CTSheetDimension.type, xmlOptions);
        }
        
        public static CTSheetDimension parse(final File file) throws XmlException, IOException {
            return (CTSheetDimension)getTypeLoader().parse(file, CTSheetDimension.type, (XmlOptions)null);
        }
        
        public static CTSheetDimension parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetDimension)getTypeLoader().parse(file, CTSheetDimension.type, xmlOptions);
        }
        
        public static CTSheetDimension parse(final URL url) throws XmlException, IOException {
            return (CTSheetDimension)getTypeLoader().parse(url, CTSheetDimension.type, (XmlOptions)null);
        }
        
        public static CTSheetDimension parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetDimension)getTypeLoader().parse(url, CTSheetDimension.type, xmlOptions);
        }
        
        public static CTSheetDimension parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheetDimension)getTypeLoader().parse(inputStream, CTSheetDimension.type, (XmlOptions)null);
        }
        
        public static CTSheetDimension parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetDimension)getTypeLoader().parse(inputStream, CTSheetDimension.type, xmlOptions);
        }
        
        public static CTSheetDimension parse(final Reader reader) throws XmlException, IOException {
            return (CTSheetDimension)getTypeLoader().parse(reader, CTSheetDimension.type, (XmlOptions)null);
        }
        
        public static CTSheetDimension parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetDimension)getTypeLoader().parse(reader, CTSheetDimension.type, xmlOptions);
        }
        
        public static CTSheetDimension parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheetDimension)getTypeLoader().parse(xmlStreamReader, CTSheetDimension.type, (XmlOptions)null);
        }
        
        public static CTSheetDimension parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetDimension)getTypeLoader().parse(xmlStreamReader, CTSheetDimension.type, xmlOptions);
        }
        
        public static CTSheetDimension parse(final Node node) throws XmlException {
            return (CTSheetDimension)getTypeLoader().parse(node, CTSheetDimension.type, (XmlOptions)null);
        }
        
        public static CTSheetDimension parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetDimension)getTypeLoader().parse(node, CTSheetDimension.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheetDimension parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheetDimension)getTypeLoader().parse(xmlInputStream, CTSheetDimension.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheetDimension parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheetDimension)getTypeLoader().parse(xmlInputStream, CTSheetDimension.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetDimension.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetDimension.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
