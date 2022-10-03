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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSingleXmlCell extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSingleXmlCell.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsinglexmlcell7790type");
    
    CTXmlCellPr getXmlCellPr();
    
    void setXmlCellPr(final CTXmlCellPr p0);
    
    CTXmlCellPr addNewXmlCellPr();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getId();
    
    XmlUnsignedInt xgetId();
    
    void setId(final long p0);
    
    void xsetId(final XmlUnsignedInt p0);
    
    String getR();
    
    STCellRef xgetR();
    
    void setR(final String p0);
    
    void xsetR(final STCellRef p0);
    
    long getConnectionId();
    
    XmlUnsignedInt xgetConnectionId();
    
    void setConnectionId(final long p0);
    
    void xsetConnectionId(final XmlUnsignedInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSingleXmlCell.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSingleXmlCell newInstance() {
            return (CTSingleXmlCell)getTypeLoader().newInstance(CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCell newInstance(final XmlOptions xmlOptions) {
            return (CTSingleXmlCell)getTypeLoader().newInstance(CTSingleXmlCell.type, xmlOptions);
        }
        
        public static CTSingleXmlCell parse(final String s) throws XmlException {
            return (CTSingleXmlCell)getTypeLoader().parse(s, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCell parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSingleXmlCell)getTypeLoader().parse(s, CTSingleXmlCell.type, xmlOptions);
        }
        
        public static CTSingleXmlCell parse(final File file) throws XmlException, IOException {
            return (CTSingleXmlCell)getTypeLoader().parse(file, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCell parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSingleXmlCell)getTypeLoader().parse(file, CTSingleXmlCell.type, xmlOptions);
        }
        
        public static CTSingleXmlCell parse(final URL url) throws XmlException, IOException {
            return (CTSingleXmlCell)getTypeLoader().parse(url, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCell parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSingleXmlCell)getTypeLoader().parse(url, CTSingleXmlCell.type, xmlOptions);
        }
        
        public static CTSingleXmlCell parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSingleXmlCell)getTypeLoader().parse(inputStream, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCell parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSingleXmlCell)getTypeLoader().parse(inputStream, CTSingleXmlCell.type, xmlOptions);
        }
        
        public static CTSingleXmlCell parse(final Reader reader) throws XmlException, IOException {
            return (CTSingleXmlCell)getTypeLoader().parse(reader, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCell parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSingleXmlCell)getTypeLoader().parse(reader, CTSingleXmlCell.type, xmlOptions);
        }
        
        public static CTSingleXmlCell parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSingleXmlCell)getTypeLoader().parse(xmlStreamReader, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCell parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSingleXmlCell)getTypeLoader().parse(xmlStreamReader, CTSingleXmlCell.type, xmlOptions);
        }
        
        public static CTSingleXmlCell parse(final Node node) throws XmlException {
            return (CTSingleXmlCell)getTypeLoader().parse(node, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCell parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSingleXmlCell)getTypeLoader().parse(node, CTSingleXmlCell.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSingleXmlCell parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSingleXmlCell)getTypeLoader().parse(xmlInputStream, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSingleXmlCell parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSingleXmlCell)getTypeLoader().parse(xmlInputStream, CTSingleXmlCell.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSingleXmlCell.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSingleXmlCell.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
