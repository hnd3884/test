package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTTblGrid extends CTTblGridBase
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblGrid.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblgrid2eeetype");
    
    CTTblGridChange getTblGridChange();
    
    boolean isSetTblGridChange();
    
    void setTblGridChange(final CTTblGridChange p0);
    
    CTTblGridChange addNewTblGridChange();
    
    void unsetTblGridChange();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblGrid.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblGrid newInstance() {
            return (CTTblGrid)getTypeLoader().newInstance(CTTblGrid.type, (XmlOptions)null);
        }
        
        public static CTTblGrid newInstance(final XmlOptions xmlOptions) {
            return (CTTblGrid)getTypeLoader().newInstance(CTTblGrid.type, xmlOptions);
        }
        
        public static CTTblGrid parse(final String s) throws XmlException {
            return (CTTblGrid)getTypeLoader().parse(s, CTTblGrid.type, (XmlOptions)null);
        }
        
        public static CTTblGrid parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGrid)getTypeLoader().parse(s, CTTblGrid.type, xmlOptions);
        }
        
        public static CTTblGrid parse(final File file) throws XmlException, IOException {
            return (CTTblGrid)getTypeLoader().parse(file, CTTblGrid.type, (XmlOptions)null);
        }
        
        public static CTTblGrid parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGrid)getTypeLoader().parse(file, CTTblGrid.type, xmlOptions);
        }
        
        public static CTTblGrid parse(final URL url) throws XmlException, IOException {
            return (CTTblGrid)getTypeLoader().parse(url, CTTblGrid.type, (XmlOptions)null);
        }
        
        public static CTTblGrid parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGrid)getTypeLoader().parse(url, CTTblGrid.type, xmlOptions);
        }
        
        public static CTTblGrid parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblGrid)getTypeLoader().parse(inputStream, CTTblGrid.type, (XmlOptions)null);
        }
        
        public static CTTblGrid parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGrid)getTypeLoader().parse(inputStream, CTTblGrid.type, xmlOptions);
        }
        
        public static CTTblGrid parse(final Reader reader) throws XmlException, IOException {
            return (CTTblGrid)getTypeLoader().parse(reader, CTTblGrid.type, (XmlOptions)null);
        }
        
        public static CTTblGrid parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGrid)getTypeLoader().parse(reader, CTTblGrid.type, xmlOptions);
        }
        
        public static CTTblGrid parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblGrid)getTypeLoader().parse(xmlStreamReader, CTTblGrid.type, (XmlOptions)null);
        }
        
        public static CTTblGrid parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGrid)getTypeLoader().parse(xmlStreamReader, CTTblGrid.type, xmlOptions);
        }
        
        public static CTTblGrid parse(final Node node) throws XmlException {
            return (CTTblGrid)getTypeLoader().parse(node, CTTblGrid.type, (XmlOptions)null);
        }
        
        public static CTTblGrid parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGrid)getTypeLoader().parse(node, CTTblGrid.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblGrid parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblGrid)getTypeLoader().parse(xmlInputStream, CTTblGrid.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblGrid parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblGrid)getTypeLoader().parse(xmlInputStream, CTTblGrid.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblGrid.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblGrid.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
