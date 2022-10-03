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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTblGridBase extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblGridBase.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblgridbasea11dtype");
    
    List<CTTblGridCol> getGridColList();
    
    @Deprecated
    CTTblGridCol[] getGridColArray();
    
    CTTblGridCol getGridColArray(final int p0);
    
    int sizeOfGridColArray();
    
    void setGridColArray(final CTTblGridCol[] p0);
    
    void setGridColArray(final int p0, final CTTblGridCol p1);
    
    CTTblGridCol insertNewGridCol(final int p0);
    
    CTTblGridCol addNewGridCol();
    
    void removeGridCol(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblGridBase.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblGridBase newInstance() {
            return (CTTblGridBase)getTypeLoader().newInstance(CTTblGridBase.type, (XmlOptions)null);
        }
        
        public static CTTblGridBase newInstance(final XmlOptions xmlOptions) {
            return (CTTblGridBase)getTypeLoader().newInstance(CTTblGridBase.type, xmlOptions);
        }
        
        public static CTTblGridBase parse(final String s) throws XmlException {
            return (CTTblGridBase)getTypeLoader().parse(s, CTTblGridBase.type, (XmlOptions)null);
        }
        
        public static CTTblGridBase parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGridBase)getTypeLoader().parse(s, CTTblGridBase.type, xmlOptions);
        }
        
        public static CTTblGridBase parse(final File file) throws XmlException, IOException {
            return (CTTblGridBase)getTypeLoader().parse(file, CTTblGridBase.type, (XmlOptions)null);
        }
        
        public static CTTblGridBase parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGridBase)getTypeLoader().parse(file, CTTblGridBase.type, xmlOptions);
        }
        
        public static CTTblGridBase parse(final URL url) throws XmlException, IOException {
            return (CTTblGridBase)getTypeLoader().parse(url, CTTblGridBase.type, (XmlOptions)null);
        }
        
        public static CTTblGridBase parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGridBase)getTypeLoader().parse(url, CTTblGridBase.type, xmlOptions);
        }
        
        public static CTTblGridBase parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblGridBase)getTypeLoader().parse(inputStream, CTTblGridBase.type, (XmlOptions)null);
        }
        
        public static CTTblGridBase parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGridBase)getTypeLoader().parse(inputStream, CTTblGridBase.type, xmlOptions);
        }
        
        public static CTTblGridBase parse(final Reader reader) throws XmlException, IOException {
            return (CTTblGridBase)getTypeLoader().parse(reader, CTTblGridBase.type, (XmlOptions)null);
        }
        
        public static CTTblGridBase parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGridBase)getTypeLoader().parse(reader, CTTblGridBase.type, xmlOptions);
        }
        
        public static CTTblGridBase parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblGridBase)getTypeLoader().parse(xmlStreamReader, CTTblGridBase.type, (XmlOptions)null);
        }
        
        public static CTTblGridBase parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGridBase)getTypeLoader().parse(xmlStreamReader, CTTblGridBase.type, xmlOptions);
        }
        
        public static CTTblGridBase parse(final Node node) throws XmlException {
            return (CTTblGridBase)getTypeLoader().parse(node, CTTblGridBase.type, (XmlOptions)null);
        }
        
        public static CTTblGridBase parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGridBase)getTypeLoader().parse(node, CTTblGridBase.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblGridBase parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblGridBase)getTypeLoader().parse(xmlInputStream, CTTblGridBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblGridBase parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblGridBase)getTypeLoader().parse(xmlInputStream, CTTblGridBase.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblGridBase.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblGridBase.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
