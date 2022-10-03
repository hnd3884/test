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

public interface CTSheetData extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheetData.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheetdata8408type");
    
    List<CTRow> getRowList();
    
    @Deprecated
    CTRow[] getRowArray();
    
    CTRow getRowArray(final int p0);
    
    int sizeOfRowArray();
    
    void setRowArray(final CTRow[] p0);
    
    void setRowArray(final int p0, final CTRow p1);
    
    CTRow insertNewRow(final int p0);
    
    CTRow addNewRow();
    
    void removeRow(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheetData.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheetData newInstance() {
            return (CTSheetData)getTypeLoader().newInstance(CTSheetData.type, (XmlOptions)null);
        }
        
        public static CTSheetData newInstance(final XmlOptions xmlOptions) {
            return (CTSheetData)getTypeLoader().newInstance(CTSheetData.type, xmlOptions);
        }
        
        public static CTSheetData parse(final String s) throws XmlException {
            return (CTSheetData)getTypeLoader().parse(s, CTSheetData.type, (XmlOptions)null);
        }
        
        public static CTSheetData parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetData)getTypeLoader().parse(s, CTSheetData.type, xmlOptions);
        }
        
        public static CTSheetData parse(final File file) throws XmlException, IOException {
            return (CTSheetData)getTypeLoader().parse(file, CTSheetData.type, (XmlOptions)null);
        }
        
        public static CTSheetData parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetData)getTypeLoader().parse(file, CTSheetData.type, xmlOptions);
        }
        
        public static CTSheetData parse(final URL url) throws XmlException, IOException {
            return (CTSheetData)getTypeLoader().parse(url, CTSheetData.type, (XmlOptions)null);
        }
        
        public static CTSheetData parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetData)getTypeLoader().parse(url, CTSheetData.type, xmlOptions);
        }
        
        public static CTSheetData parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheetData)getTypeLoader().parse(inputStream, CTSheetData.type, (XmlOptions)null);
        }
        
        public static CTSheetData parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetData)getTypeLoader().parse(inputStream, CTSheetData.type, xmlOptions);
        }
        
        public static CTSheetData parse(final Reader reader) throws XmlException, IOException {
            return (CTSheetData)getTypeLoader().parse(reader, CTSheetData.type, (XmlOptions)null);
        }
        
        public static CTSheetData parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetData)getTypeLoader().parse(reader, CTSheetData.type, xmlOptions);
        }
        
        public static CTSheetData parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheetData)getTypeLoader().parse(xmlStreamReader, CTSheetData.type, (XmlOptions)null);
        }
        
        public static CTSheetData parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetData)getTypeLoader().parse(xmlStreamReader, CTSheetData.type, xmlOptions);
        }
        
        public static CTSheetData parse(final Node node) throws XmlException {
            return (CTSheetData)getTypeLoader().parse(node, CTSheetData.type, (XmlOptions)null);
        }
        
        public static CTSheetData parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetData)getTypeLoader().parse(node, CTSheetData.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheetData parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheetData)getTypeLoader().parse(xmlInputStream, CTSheetData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheetData parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheetData)getTypeLoader().parse(xmlInputStream, CTSheetData.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetData.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetData.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
