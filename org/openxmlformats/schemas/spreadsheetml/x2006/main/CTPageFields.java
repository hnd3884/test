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

public interface CTPageFields extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPageFields.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpagefields1db1type");
    
    List<CTPageField> getPageFieldList();
    
    @Deprecated
    CTPageField[] getPageFieldArray();
    
    CTPageField getPageFieldArray(final int p0);
    
    int sizeOfPageFieldArray();
    
    void setPageFieldArray(final CTPageField[] p0);
    
    void setPageFieldArray(final int p0, final CTPageField p1);
    
    CTPageField insertNewPageField(final int p0);
    
    CTPageField addNewPageField();
    
    void removePageField(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPageFields.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPageFields newInstance() {
            return (CTPageFields)getTypeLoader().newInstance(CTPageFields.type, (XmlOptions)null);
        }
        
        public static CTPageFields newInstance(final XmlOptions xmlOptions) {
            return (CTPageFields)getTypeLoader().newInstance(CTPageFields.type, xmlOptions);
        }
        
        public static CTPageFields parse(final String s) throws XmlException {
            return (CTPageFields)getTypeLoader().parse(s, CTPageFields.type, (XmlOptions)null);
        }
        
        public static CTPageFields parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageFields)getTypeLoader().parse(s, CTPageFields.type, xmlOptions);
        }
        
        public static CTPageFields parse(final File file) throws XmlException, IOException {
            return (CTPageFields)getTypeLoader().parse(file, CTPageFields.type, (XmlOptions)null);
        }
        
        public static CTPageFields parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageFields)getTypeLoader().parse(file, CTPageFields.type, xmlOptions);
        }
        
        public static CTPageFields parse(final URL url) throws XmlException, IOException {
            return (CTPageFields)getTypeLoader().parse(url, CTPageFields.type, (XmlOptions)null);
        }
        
        public static CTPageFields parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageFields)getTypeLoader().parse(url, CTPageFields.type, xmlOptions);
        }
        
        public static CTPageFields parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPageFields)getTypeLoader().parse(inputStream, CTPageFields.type, (XmlOptions)null);
        }
        
        public static CTPageFields parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageFields)getTypeLoader().parse(inputStream, CTPageFields.type, xmlOptions);
        }
        
        public static CTPageFields parse(final Reader reader) throws XmlException, IOException {
            return (CTPageFields)getTypeLoader().parse(reader, CTPageFields.type, (XmlOptions)null);
        }
        
        public static CTPageFields parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageFields)getTypeLoader().parse(reader, CTPageFields.type, xmlOptions);
        }
        
        public static CTPageFields parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPageFields)getTypeLoader().parse(xmlStreamReader, CTPageFields.type, (XmlOptions)null);
        }
        
        public static CTPageFields parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageFields)getTypeLoader().parse(xmlStreamReader, CTPageFields.type, xmlOptions);
        }
        
        public static CTPageFields parse(final Node node) throws XmlException {
            return (CTPageFields)getTypeLoader().parse(node, CTPageFields.type, (XmlOptions)null);
        }
        
        public static CTPageFields parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageFields)getTypeLoader().parse(node, CTPageFields.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPageFields parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPageFields)getTypeLoader().parse(xmlInputStream, CTPageFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPageFields parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPageFields)getTypeLoader().parse(xmlInputStream, CTPageFields.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageFields.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
