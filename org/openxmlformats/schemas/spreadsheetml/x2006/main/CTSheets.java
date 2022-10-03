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

public interface CTSheets extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheets.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheets49fdtype");
    
    List<CTSheet> getSheetList();
    
    @Deprecated
    CTSheet[] getSheetArray();
    
    CTSheet getSheetArray(final int p0);
    
    int sizeOfSheetArray();
    
    void setSheetArray(final CTSheet[] p0);
    
    void setSheetArray(final int p0, final CTSheet p1);
    
    CTSheet insertNewSheet(final int p0);
    
    CTSheet addNewSheet();
    
    void removeSheet(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheets.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheets newInstance() {
            return (CTSheets)getTypeLoader().newInstance(CTSheets.type, (XmlOptions)null);
        }
        
        public static CTSheets newInstance(final XmlOptions xmlOptions) {
            return (CTSheets)getTypeLoader().newInstance(CTSheets.type, xmlOptions);
        }
        
        public static CTSheets parse(final String s) throws XmlException {
            return (CTSheets)getTypeLoader().parse(s, CTSheets.type, (XmlOptions)null);
        }
        
        public static CTSheets parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheets)getTypeLoader().parse(s, CTSheets.type, xmlOptions);
        }
        
        public static CTSheets parse(final File file) throws XmlException, IOException {
            return (CTSheets)getTypeLoader().parse(file, CTSheets.type, (XmlOptions)null);
        }
        
        public static CTSheets parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheets)getTypeLoader().parse(file, CTSheets.type, xmlOptions);
        }
        
        public static CTSheets parse(final URL url) throws XmlException, IOException {
            return (CTSheets)getTypeLoader().parse(url, CTSheets.type, (XmlOptions)null);
        }
        
        public static CTSheets parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheets)getTypeLoader().parse(url, CTSheets.type, xmlOptions);
        }
        
        public static CTSheets parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheets)getTypeLoader().parse(inputStream, CTSheets.type, (XmlOptions)null);
        }
        
        public static CTSheets parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheets)getTypeLoader().parse(inputStream, CTSheets.type, xmlOptions);
        }
        
        public static CTSheets parse(final Reader reader) throws XmlException, IOException {
            return (CTSheets)getTypeLoader().parse(reader, CTSheets.type, (XmlOptions)null);
        }
        
        public static CTSheets parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheets)getTypeLoader().parse(reader, CTSheets.type, xmlOptions);
        }
        
        public static CTSheets parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheets)getTypeLoader().parse(xmlStreamReader, CTSheets.type, (XmlOptions)null);
        }
        
        public static CTSheets parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheets)getTypeLoader().parse(xmlStreamReader, CTSheets.type, xmlOptions);
        }
        
        public static CTSheets parse(final Node node) throws XmlException {
            return (CTSheets)getTypeLoader().parse(node, CTSheets.type, (XmlOptions)null);
        }
        
        public static CTSheets parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheets)getTypeLoader().parse(node, CTSheets.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheets parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheets)getTypeLoader().parse(xmlInputStream, CTSheets.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheets parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheets)getTypeLoader().parse(xmlInputStream, CTSheets.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheets.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheets.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
