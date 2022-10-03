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

public interface CTTable extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTable.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttable5f3ftype");
    
    CTTableProperties getTblPr();
    
    boolean isSetTblPr();
    
    void setTblPr(final CTTableProperties p0);
    
    CTTableProperties addNewTblPr();
    
    void unsetTblPr();
    
    CTTableGrid getTblGrid();
    
    void setTblGrid(final CTTableGrid p0);
    
    CTTableGrid addNewTblGrid();
    
    List<CTTableRow> getTrList();
    
    @Deprecated
    CTTableRow[] getTrArray();
    
    CTTableRow getTrArray(final int p0);
    
    int sizeOfTrArray();
    
    void setTrArray(final CTTableRow[] p0);
    
    void setTrArray(final int p0, final CTTableRow p1);
    
    CTTableRow insertNewTr(final int p0);
    
    CTTableRow addNewTr();
    
    void removeTr(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTable.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTable newInstance() {
            return (CTTable)getTypeLoader().newInstance(CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable newInstance(final XmlOptions xmlOptions) {
            return (CTTable)getTypeLoader().newInstance(CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final String s) throws XmlException {
            return (CTTable)getTypeLoader().parse(s, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTable)getTypeLoader().parse(s, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final File file) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(file, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(file, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final URL url) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(url, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(url, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(inputStream, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(inputStream, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final Reader reader) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(reader, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(reader, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTable)getTypeLoader().parse(xmlStreamReader, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTable)getTypeLoader().parse(xmlStreamReader, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final Node node) throws XmlException {
            return (CTTable)getTypeLoader().parse(node, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTable)getTypeLoader().parse(node, CTTable.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTable parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTable)getTypeLoader().parse(xmlInputStream, CTTable.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTable parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTable)getTypeLoader().parse(xmlInputStream, CTTable.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTable.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTable.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
