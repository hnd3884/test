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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDataFields extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDataFields.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdatafields52cctype");
    
    List<CTDataField> getDataFieldList();
    
    @Deprecated
    CTDataField[] getDataFieldArray();
    
    CTDataField getDataFieldArray(final int p0);
    
    int sizeOfDataFieldArray();
    
    void setDataFieldArray(final CTDataField[] p0);
    
    void setDataFieldArray(final int p0, final CTDataField p1);
    
    CTDataField insertNewDataField(final int p0);
    
    CTDataField addNewDataField();
    
    void removeDataField(final int p0);
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDataFields.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDataFields newInstance() {
            return (CTDataFields)getTypeLoader().newInstance(CTDataFields.type, (XmlOptions)null);
        }
        
        public static CTDataFields newInstance(final XmlOptions xmlOptions) {
            return (CTDataFields)getTypeLoader().newInstance(CTDataFields.type, xmlOptions);
        }
        
        public static CTDataFields parse(final String s) throws XmlException {
            return (CTDataFields)getTypeLoader().parse(s, CTDataFields.type, (XmlOptions)null);
        }
        
        public static CTDataFields parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataFields)getTypeLoader().parse(s, CTDataFields.type, xmlOptions);
        }
        
        public static CTDataFields parse(final File file) throws XmlException, IOException {
            return (CTDataFields)getTypeLoader().parse(file, CTDataFields.type, (XmlOptions)null);
        }
        
        public static CTDataFields parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataFields)getTypeLoader().parse(file, CTDataFields.type, xmlOptions);
        }
        
        public static CTDataFields parse(final URL url) throws XmlException, IOException {
            return (CTDataFields)getTypeLoader().parse(url, CTDataFields.type, (XmlOptions)null);
        }
        
        public static CTDataFields parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataFields)getTypeLoader().parse(url, CTDataFields.type, xmlOptions);
        }
        
        public static CTDataFields parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDataFields)getTypeLoader().parse(inputStream, CTDataFields.type, (XmlOptions)null);
        }
        
        public static CTDataFields parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataFields)getTypeLoader().parse(inputStream, CTDataFields.type, xmlOptions);
        }
        
        public static CTDataFields parse(final Reader reader) throws XmlException, IOException {
            return (CTDataFields)getTypeLoader().parse(reader, CTDataFields.type, (XmlOptions)null);
        }
        
        public static CTDataFields parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataFields)getTypeLoader().parse(reader, CTDataFields.type, xmlOptions);
        }
        
        public static CTDataFields parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDataFields)getTypeLoader().parse(xmlStreamReader, CTDataFields.type, (XmlOptions)null);
        }
        
        public static CTDataFields parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataFields)getTypeLoader().parse(xmlStreamReader, CTDataFields.type, xmlOptions);
        }
        
        public static CTDataFields parse(final Node node) throws XmlException {
            return (CTDataFields)getTypeLoader().parse(node, CTDataFields.type, (XmlOptions)null);
        }
        
        public static CTDataFields parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataFields)getTypeLoader().parse(node, CTDataFields.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDataFields parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDataFields)getTypeLoader().parse(xmlInputStream, CTDataFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDataFields parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDataFields)getTypeLoader().parse(xmlInputStream, CTDataFields.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataFields.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
