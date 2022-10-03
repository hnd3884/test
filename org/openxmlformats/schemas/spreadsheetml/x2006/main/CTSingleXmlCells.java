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

public interface CTSingleXmlCells extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSingleXmlCells.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsinglexmlcells5a6btype");
    
    List<CTSingleXmlCell> getSingleXmlCellList();
    
    @Deprecated
    CTSingleXmlCell[] getSingleXmlCellArray();
    
    CTSingleXmlCell getSingleXmlCellArray(final int p0);
    
    int sizeOfSingleXmlCellArray();
    
    void setSingleXmlCellArray(final CTSingleXmlCell[] p0);
    
    void setSingleXmlCellArray(final int p0, final CTSingleXmlCell p1);
    
    CTSingleXmlCell insertNewSingleXmlCell(final int p0);
    
    CTSingleXmlCell addNewSingleXmlCell();
    
    void removeSingleXmlCell(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSingleXmlCells.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSingleXmlCells newInstance() {
            return (CTSingleXmlCells)getTypeLoader().newInstance(CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCells newInstance(final XmlOptions xmlOptions) {
            return (CTSingleXmlCells)getTypeLoader().newInstance(CTSingleXmlCells.type, xmlOptions);
        }
        
        public static CTSingleXmlCells parse(final String s) throws XmlException {
            return (CTSingleXmlCells)getTypeLoader().parse(s, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCells parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSingleXmlCells)getTypeLoader().parse(s, CTSingleXmlCells.type, xmlOptions);
        }
        
        public static CTSingleXmlCells parse(final File file) throws XmlException, IOException {
            return (CTSingleXmlCells)getTypeLoader().parse(file, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCells parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSingleXmlCells)getTypeLoader().parse(file, CTSingleXmlCells.type, xmlOptions);
        }
        
        public static CTSingleXmlCells parse(final URL url) throws XmlException, IOException {
            return (CTSingleXmlCells)getTypeLoader().parse(url, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCells parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSingleXmlCells)getTypeLoader().parse(url, CTSingleXmlCells.type, xmlOptions);
        }
        
        public static CTSingleXmlCells parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSingleXmlCells)getTypeLoader().parse(inputStream, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCells parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSingleXmlCells)getTypeLoader().parse(inputStream, CTSingleXmlCells.type, xmlOptions);
        }
        
        public static CTSingleXmlCells parse(final Reader reader) throws XmlException, IOException {
            return (CTSingleXmlCells)getTypeLoader().parse(reader, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCells parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSingleXmlCells)getTypeLoader().parse(reader, CTSingleXmlCells.type, xmlOptions);
        }
        
        public static CTSingleXmlCells parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSingleXmlCells)getTypeLoader().parse(xmlStreamReader, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCells parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSingleXmlCells)getTypeLoader().parse(xmlStreamReader, CTSingleXmlCells.type, xmlOptions);
        }
        
        public static CTSingleXmlCells parse(final Node node) throws XmlException {
            return (CTSingleXmlCells)getTypeLoader().parse(node, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        public static CTSingleXmlCells parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSingleXmlCells)getTypeLoader().parse(node, CTSingleXmlCells.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSingleXmlCells parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSingleXmlCells)getTypeLoader().parse(xmlInputStream, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSingleXmlCells parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSingleXmlCells)getTypeLoader().parse(xmlInputStream, CTSingleXmlCells.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSingleXmlCells.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSingleXmlCells.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
