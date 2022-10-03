package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTTableGrid extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableGrid.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablegrid69a5type");
    
    List<CTTableCol> getGridColList();
    
    @Deprecated
    CTTableCol[] getGridColArray();
    
    CTTableCol getGridColArray(final int p0);
    
    int sizeOfGridColArray();
    
    void setGridColArray(final CTTableCol[] p0);
    
    void setGridColArray(final int p0, final CTTableCol p1);
    
    CTTableCol insertNewGridCol(final int p0);
    
    CTTableCol addNewGridCol();
    
    void removeGridCol(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableGrid.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableGrid newInstance() {
            return (CTTableGrid)getTypeLoader().newInstance(CTTableGrid.type, (XmlOptions)null);
        }
        
        public static CTTableGrid newInstance(final XmlOptions xmlOptions) {
            return (CTTableGrid)getTypeLoader().newInstance(CTTableGrid.type, xmlOptions);
        }
        
        public static CTTableGrid parse(final String s) throws XmlException {
            return (CTTableGrid)getTypeLoader().parse(s, CTTableGrid.type, (XmlOptions)null);
        }
        
        public static CTTableGrid parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableGrid)getTypeLoader().parse(s, CTTableGrid.type, xmlOptions);
        }
        
        public static CTTableGrid parse(final File file) throws XmlException, IOException {
            return (CTTableGrid)getTypeLoader().parse(file, CTTableGrid.type, (XmlOptions)null);
        }
        
        public static CTTableGrid parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableGrid)getTypeLoader().parse(file, CTTableGrid.type, xmlOptions);
        }
        
        public static CTTableGrid parse(final URL url) throws XmlException, IOException {
            return (CTTableGrid)getTypeLoader().parse(url, CTTableGrid.type, (XmlOptions)null);
        }
        
        public static CTTableGrid parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableGrid)getTypeLoader().parse(url, CTTableGrid.type, xmlOptions);
        }
        
        public static CTTableGrid parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableGrid)getTypeLoader().parse(inputStream, CTTableGrid.type, (XmlOptions)null);
        }
        
        public static CTTableGrid parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableGrid)getTypeLoader().parse(inputStream, CTTableGrid.type, xmlOptions);
        }
        
        public static CTTableGrid parse(final Reader reader) throws XmlException, IOException {
            return (CTTableGrid)getTypeLoader().parse(reader, CTTableGrid.type, (XmlOptions)null);
        }
        
        public static CTTableGrid parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableGrid)getTypeLoader().parse(reader, CTTableGrid.type, xmlOptions);
        }
        
        public static CTTableGrid parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableGrid)getTypeLoader().parse(xmlStreamReader, CTTableGrid.type, (XmlOptions)null);
        }
        
        public static CTTableGrid parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableGrid)getTypeLoader().parse(xmlStreamReader, CTTableGrid.type, xmlOptions);
        }
        
        public static CTTableGrid parse(final Node node) throws XmlException {
            return (CTTableGrid)getTypeLoader().parse(node, CTTableGrid.type, (XmlOptions)null);
        }
        
        public static CTTableGrid parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableGrid)getTypeLoader().parse(node, CTTableGrid.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableGrid parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableGrid)getTypeLoader().parse(xmlInputStream, CTTableGrid.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableGrid parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableGrid)getTypeLoader().parse(xmlInputStream, CTTableGrid.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableGrid.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableGrid.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
