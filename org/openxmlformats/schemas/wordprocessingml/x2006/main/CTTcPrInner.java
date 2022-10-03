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

public interface CTTcPrInner extends CTTcPrBase
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTcPrInner.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttcprinnerc56dtype");
    
    CTTrackChange getCellIns();
    
    boolean isSetCellIns();
    
    void setCellIns(final CTTrackChange p0);
    
    CTTrackChange addNewCellIns();
    
    void unsetCellIns();
    
    CTTrackChange getCellDel();
    
    boolean isSetCellDel();
    
    void setCellDel(final CTTrackChange p0);
    
    CTTrackChange addNewCellDel();
    
    void unsetCellDel();
    
    CTCellMergeTrackChange getCellMerge();
    
    boolean isSetCellMerge();
    
    void setCellMerge(final CTCellMergeTrackChange p0);
    
    CTCellMergeTrackChange addNewCellMerge();
    
    void unsetCellMerge();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTcPrInner.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTcPrInner newInstance() {
            return (CTTcPrInner)getTypeLoader().newInstance(CTTcPrInner.type, (XmlOptions)null);
        }
        
        public static CTTcPrInner newInstance(final XmlOptions xmlOptions) {
            return (CTTcPrInner)getTypeLoader().newInstance(CTTcPrInner.type, xmlOptions);
        }
        
        public static CTTcPrInner parse(final String s) throws XmlException {
            return (CTTcPrInner)getTypeLoader().parse(s, CTTcPrInner.type, (XmlOptions)null);
        }
        
        public static CTTcPrInner parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPrInner)getTypeLoader().parse(s, CTTcPrInner.type, xmlOptions);
        }
        
        public static CTTcPrInner parse(final File file) throws XmlException, IOException {
            return (CTTcPrInner)getTypeLoader().parse(file, CTTcPrInner.type, (XmlOptions)null);
        }
        
        public static CTTcPrInner parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPrInner)getTypeLoader().parse(file, CTTcPrInner.type, xmlOptions);
        }
        
        public static CTTcPrInner parse(final URL url) throws XmlException, IOException {
            return (CTTcPrInner)getTypeLoader().parse(url, CTTcPrInner.type, (XmlOptions)null);
        }
        
        public static CTTcPrInner parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPrInner)getTypeLoader().parse(url, CTTcPrInner.type, xmlOptions);
        }
        
        public static CTTcPrInner parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTcPrInner)getTypeLoader().parse(inputStream, CTTcPrInner.type, (XmlOptions)null);
        }
        
        public static CTTcPrInner parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPrInner)getTypeLoader().parse(inputStream, CTTcPrInner.type, xmlOptions);
        }
        
        public static CTTcPrInner parse(final Reader reader) throws XmlException, IOException {
            return (CTTcPrInner)getTypeLoader().parse(reader, CTTcPrInner.type, (XmlOptions)null);
        }
        
        public static CTTcPrInner parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTcPrInner)getTypeLoader().parse(reader, CTTcPrInner.type, xmlOptions);
        }
        
        public static CTTcPrInner parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTcPrInner)getTypeLoader().parse(xmlStreamReader, CTTcPrInner.type, (XmlOptions)null);
        }
        
        public static CTTcPrInner parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPrInner)getTypeLoader().parse(xmlStreamReader, CTTcPrInner.type, xmlOptions);
        }
        
        public static CTTcPrInner parse(final Node node) throws XmlException {
            return (CTTcPrInner)getTypeLoader().parse(node, CTTcPrInner.type, (XmlOptions)null);
        }
        
        public static CTTcPrInner parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTcPrInner)getTypeLoader().parse(node, CTTcPrInner.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTcPrInner parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTcPrInner)getTypeLoader().parse(xmlInputStream, CTTcPrInner.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTcPrInner parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTcPrInner)getTypeLoader().parse(xmlInputStream, CTTcPrInner.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTcPrInner.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTcPrInner.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
