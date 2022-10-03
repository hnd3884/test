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

public interface CTRowFields extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRowFields.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrowfields0312type");
    
    List<CTField> getFieldList();
    
    @Deprecated
    CTField[] getFieldArray();
    
    CTField getFieldArray(final int p0);
    
    int sizeOfFieldArray();
    
    void setFieldArray(final CTField[] p0);
    
    void setFieldArray(final int p0, final CTField p1);
    
    CTField insertNewField(final int p0);
    
    CTField addNewField();
    
    void removeField(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRowFields.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRowFields newInstance() {
            return (CTRowFields)getTypeLoader().newInstance(CTRowFields.type, (XmlOptions)null);
        }
        
        public static CTRowFields newInstance(final XmlOptions xmlOptions) {
            return (CTRowFields)getTypeLoader().newInstance(CTRowFields.type, xmlOptions);
        }
        
        public static CTRowFields parse(final String s) throws XmlException {
            return (CTRowFields)getTypeLoader().parse(s, CTRowFields.type, (XmlOptions)null);
        }
        
        public static CTRowFields parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRowFields)getTypeLoader().parse(s, CTRowFields.type, xmlOptions);
        }
        
        public static CTRowFields parse(final File file) throws XmlException, IOException {
            return (CTRowFields)getTypeLoader().parse(file, CTRowFields.type, (XmlOptions)null);
        }
        
        public static CTRowFields parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRowFields)getTypeLoader().parse(file, CTRowFields.type, xmlOptions);
        }
        
        public static CTRowFields parse(final URL url) throws XmlException, IOException {
            return (CTRowFields)getTypeLoader().parse(url, CTRowFields.type, (XmlOptions)null);
        }
        
        public static CTRowFields parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRowFields)getTypeLoader().parse(url, CTRowFields.type, xmlOptions);
        }
        
        public static CTRowFields parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRowFields)getTypeLoader().parse(inputStream, CTRowFields.type, (XmlOptions)null);
        }
        
        public static CTRowFields parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRowFields)getTypeLoader().parse(inputStream, CTRowFields.type, xmlOptions);
        }
        
        public static CTRowFields parse(final Reader reader) throws XmlException, IOException {
            return (CTRowFields)getTypeLoader().parse(reader, CTRowFields.type, (XmlOptions)null);
        }
        
        public static CTRowFields parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRowFields)getTypeLoader().parse(reader, CTRowFields.type, xmlOptions);
        }
        
        public static CTRowFields parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRowFields)getTypeLoader().parse(xmlStreamReader, CTRowFields.type, (XmlOptions)null);
        }
        
        public static CTRowFields parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRowFields)getTypeLoader().parse(xmlStreamReader, CTRowFields.type, xmlOptions);
        }
        
        public static CTRowFields parse(final Node node) throws XmlException {
            return (CTRowFields)getTypeLoader().parse(node, CTRowFields.type, (XmlOptions)null);
        }
        
        public static CTRowFields parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRowFields)getTypeLoader().parse(node, CTRowFields.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRowFields parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRowFields)getTypeLoader().parse(xmlInputStream, CTRowFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRowFields parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRowFields)getTypeLoader().parse(xmlInputStream, CTRowFields.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRowFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRowFields.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
