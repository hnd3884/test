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

public interface CTMergeCell extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMergeCell.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmergecelle8d9type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMergeCell.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMergeCell newInstance() {
            return (CTMergeCell)getTypeLoader().newInstance(CTMergeCell.type, (XmlOptions)null);
        }
        
        public static CTMergeCell newInstance(final XmlOptions xmlOptions) {
            return (CTMergeCell)getTypeLoader().newInstance(CTMergeCell.type, xmlOptions);
        }
        
        public static CTMergeCell parse(final String s) throws XmlException {
            return (CTMergeCell)getTypeLoader().parse(s, CTMergeCell.type, (XmlOptions)null);
        }
        
        public static CTMergeCell parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMergeCell)getTypeLoader().parse(s, CTMergeCell.type, xmlOptions);
        }
        
        public static CTMergeCell parse(final File file) throws XmlException, IOException {
            return (CTMergeCell)getTypeLoader().parse(file, CTMergeCell.type, (XmlOptions)null);
        }
        
        public static CTMergeCell parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMergeCell)getTypeLoader().parse(file, CTMergeCell.type, xmlOptions);
        }
        
        public static CTMergeCell parse(final URL url) throws XmlException, IOException {
            return (CTMergeCell)getTypeLoader().parse(url, CTMergeCell.type, (XmlOptions)null);
        }
        
        public static CTMergeCell parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMergeCell)getTypeLoader().parse(url, CTMergeCell.type, xmlOptions);
        }
        
        public static CTMergeCell parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMergeCell)getTypeLoader().parse(inputStream, CTMergeCell.type, (XmlOptions)null);
        }
        
        public static CTMergeCell parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMergeCell)getTypeLoader().parse(inputStream, CTMergeCell.type, xmlOptions);
        }
        
        public static CTMergeCell parse(final Reader reader) throws XmlException, IOException {
            return (CTMergeCell)getTypeLoader().parse(reader, CTMergeCell.type, (XmlOptions)null);
        }
        
        public static CTMergeCell parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMergeCell)getTypeLoader().parse(reader, CTMergeCell.type, xmlOptions);
        }
        
        public static CTMergeCell parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMergeCell)getTypeLoader().parse(xmlStreamReader, CTMergeCell.type, (XmlOptions)null);
        }
        
        public static CTMergeCell parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMergeCell)getTypeLoader().parse(xmlStreamReader, CTMergeCell.type, xmlOptions);
        }
        
        public static CTMergeCell parse(final Node node) throws XmlException {
            return (CTMergeCell)getTypeLoader().parse(node, CTMergeCell.type, (XmlOptions)null);
        }
        
        public static CTMergeCell parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMergeCell)getTypeLoader().parse(node, CTMergeCell.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMergeCell parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMergeCell)getTypeLoader().parse(xmlInputStream, CTMergeCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMergeCell parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMergeCell)getTypeLoader().parse(xmlInputStream, CTMergeCell.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMergeCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMergeCell.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
