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
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTExternalBook extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalBook.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternalbookc89dtype");
    
    CTExternalSheetNames getSheetNames();
    
    boolean isSetSheetNames();
    
    void setSheetNames(final CTExternalSheetNames p0);
    
    CTExternalSheetNames addNewSheetNames();
    
    void unsetSheetNames();
    
    CTExternalDefinedNames getDefinedNames();
    
    boolean isSetDefinedNames();
    
    void setDefinedNames(final CTExternalDefinedNames p0);
    
    CTExternalDefinedNames addNewDefinedNames();
    
    void unsetDefinedNames();
    
    CTExternalSheetDataSet getSheetDataSet();
    
    boolean isSetSheetDataSet();
    
    void setSheetDataSet(final CTExternalSheetDataSet p0);
    
    CTExternalSheetDataSet addNewSheetDataSet();
    
    void unsetSheetDataSet();
    
    String getId();
    
    STRelationshipId xgetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalBook.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalBook newInstance() {
            return (CTExternalBook)getTypeLoader().newInstance(CTExternalBook.type, (XmlOptions)null);
        }
        
        public static CTExternalBook newInstance(final XmlOptions xmlOptions) {
            return (CTExternalBook)getTypeLoader().newInstance(CTExternalBook.type, xmlOptions);
        }
        
        public static CTExternalBook parse(final String s) throws XmlException {
            return (CTExternalBook)getTypeLoader().parse(s, CTExternalBook.type, (XmlOptions)null);
        }
        
        public static CTExternalBook parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalBook)getTypeLoader().parse(s, CTExternalBook.type, xmlOptions);
        }
        
        public static CTExternalBook parse(final File file) throws XmlException, IOException {
            return (CTExternalBook)getTypeLoader().parse(file, CTExternalBook.type, (XmlOptions)null);
        }
        
        public static CTExternalBook parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalBook)getTypeLoader().parse(file, CTExternalBook.type, xmlOptions);
        }
        
        public static CTExternalBook parse(final URL url) throws XmlException, IOException {
            return (CTExternalBook)getTypeLoader().parse(url, CTExternalBook.type, (XmlOptions)null);
        }
        
        public static CTExternalBook parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalBook)getTypeLoader().parse(url, CTExternalBook.type, xmlOptions);
        }
        
        public static CTExternalBook parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalBook)getTypeLoader().parse(inputStream, CTExternalBook.type, (XmlOptions)null);
        }
        
        public static CTExternalBook parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalBook)getTypeLoader().parse(inputStream, CTExternalBook.type, xmlOptions);
        }
        
        public static CTExternalBook parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalBook)getTypeLoader().parse(reader, CTExternalBook.type, (XmlOptions)null);
        }
        
        public static CTExternalBook parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalBook)getTypeLoader().parse(reader, CTExternalBook.type, xmlOptions);
        }
        
        public static CTExternalBook parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalBook)getTypeLoader().parse(xmlStreamReader, CTExternalBook.type, (XmlOptions)null);
        }
        
        public static CTExternalBook parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalBook)getTypeLoader().parse(xmlStreamReader, CTExternalBook.type, xmlOptions);
        }
        
        public static CTExternalBook parse(final Node node) throws XmlException {
            return (CTExternalBook)getTypeLoader().parse(node, CTExternalBook.type, (XmlOptions)null);
        }
        
        public static CTExternalBook parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalBook)getTypeLoader().parse(node, CTExternalBook.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalBook parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalBook)getTypeLoader().parse(xmlInputStream, CTExternalBook.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalBook parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalBook)getTypeLoader().parse(xmlInputStream, CTExternalBook.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalBook.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalBook.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
