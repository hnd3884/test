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

public interface CTTableRow extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableRow.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablerow4ac7type");
    
    List<CTTableCell> getTcList();
    
    @Deprecated
    CTTableCell[] getTcArray();
    
    CTTableCell getTcArray(final int p0);
    
    int sizeOfTcArray();
    
    void setTcArray(final CTTableCell[] p0);
    
    void setTcArray(final int p0, final CTTableCell p1);
    
    CTTableCell insertNewTc(final int p0);
    
    CTTableCell addNewTc();
    
    void removeTc(final int p0);
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getH();
    
    STCoordinate xgetH();
    
    void setH(final long p0);
    
    void xsetH(final STCoordinate p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableRow.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableRow newInstance() {
            return (CTTableRow)getTypeLoader().newInstance(CTTableRow.type, (XmlOptions)null);
        }
        
        public static CTTableRow newInstance(final XmlOptions xmlOptions) {
            return (CTTableRow)getTypeLoader().newInstance(CTTableRow.type, xmlOptions);
        }
        
        public static CTTableRow parse(final String s) throws XmlException {
            return (CTTableRow)getTypeLoader().parse(s, CTTableRow.type, (XmlOptions)null);
        }
        
        public static CTTableRow parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableRow)getTypeLoader().parse(s, CTTableRow.type, xmlOptions);
        }
        
        public static CTTableRow parse(final File file) throws XmlException, IOException {
            return (CTTableRow)getTypeLoader().parse(file, CTTableRow.type, (XmlOptions)null);
        }
        
        public static CTTableRow parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableRow)getTypeLoader().parse(file, CTTableRow.type, xmlOptions);
        }
        
        public static CTTableRow parse(final URL url) throws XmlException, IOException {
            return (CTTableRow)getTypeLoader().parse(url, CTTableRow.type, (XmlOptions)null);
        }
        
        public static CTTableRow parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableRow)getTypeLoader().parse(url, CTTableRow.type, xmlOptions);
        }
        
        public static CTTableRow parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableRow)getTypeLoader().parse(inputStream, CTTableRow.type, (XmlOptions)null);
        }
        
        public static CTTableRow parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableRow)getTypeLoader().parse(inputStream, CTTableRow.type, xmlOptions);
        }
        
        public static CTTableRow parse(final Reader reader) throws XmlException, IOException {
            return (CTTableRow)getTypeLoader().parse(reader, CTTableRow.type, (XmlOptions)null);
        }
        
        public static CTTableRow parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableRow)getTypeLoader().parse(reader, CTTableRow.type, xmlOptions);
        }
        
        public static CTTableRow parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableRow)getTypeLoader().parse(xmlStreamReader, CTTableRow.type, (XmlOptions)null);
        }
        
        public static CTTableRow parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableRow)getTypeLoader().parse(xmlStreamReader, CTTableRow.type, xmlOptions);
        }
        
        public static CTTableRow parse(final Node node) throws XmlException {
            return (CTTableRow)getTypeLoader().parse(node, CTTableRow.type, (XmlOptions)null);
        }
        
        public static CTTableRow parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableRow)getTypeLoader().parse(node, CTTableRow.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableRow parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableRow)getTypeLoader().parse(xmlInputStream, CTTableRow.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableRow parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableRow)getTypeLoader().parse(xmlInputStream, CTTableRow.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableRow.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableRow.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
