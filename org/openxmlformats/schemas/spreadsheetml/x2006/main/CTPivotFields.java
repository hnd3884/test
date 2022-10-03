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

public interface CTPivotFields extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPivotFields.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpivotfields12batype");
    
    List<CTPivotField> getPivotFieldList();
    
    @Deprecated
    CTPivotField[] getPivotFieldArray();
    
    CTPivotField getPivotFieldArray(final int p0);
    
    int sizeOfPivotFieldArray();
    
    void setPivotFieldArray(final CTPivotField[] p0);
    
    void setPivotFieldArray(final int p0, final CTPivotField p1);
    
    CTPivotField insertNewPivotField(final int p0);
    
    CTPivotField addNewPivotField();
    
    void removePivotField(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPivotFields.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPivotFields newInstance() {
            return (CTPivotFields)getTypeLoader().newInstance(CTPivotFields.type, (XmlOptions)null);
        }
        
        public static CTPivotFields newInstance(final XmlOptions xmlOptions) {
            return (CTPivotFields)getTypeLoader().newInstance(CTPivotFields.type, xmlOptions);
        }
        
        public static CTPivotFields parse(final String s) throws XmlException {
            return (CTPivotFields)getTypeLoader().parse(s, CTPivotFields.type, (XmlOptions)null);
        }
        
        public static CTPivotFields parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotFields)getTypeLoader().parse(s, CTPivotFields.type, xmlOptions);
        }
        
        public static CTPivotFields parse(final File file) throws XmlException, IOException {
            return (CTPivotFields)getTypeLoader().parse(file, CTPivotFields.type, (XmlOptions)null);
        }
        
        public static CTPivotFields parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotFields)getTypeLoader().parse(file, CTPivotFields.type, xmlOptions);
        }
        
        public static CTPivotFields parse(final URL url) throws XmlException, IOException {
            return (CTPivotFields)getTypeLoader().parse(url, CTPivotFields.type, (XmlOptions)null);
        }
        
        public static CTPivotFields parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotFields)getTypeLoader().parse(url, CTPivotFields.type, xmlOptions);
        }
        
        public static CTPivotFields parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPivotFields)getTypeLoader().parse(inputStream, CTPivotFields.type, (XmlOptions)null);
        }
        
        public static CTPivotFields parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotFields)getTypeLoader().parse(inputStream, CTPivotFields.type, xmlOptions);
        }
        
        public static CTPivotFields parse(final Reader reader) throws XmlException, IOException {
            return (CTPivotFields)getTypeLoader().parse(reader, CTPivotFields.type, (XmlOptions)null);
        }
        
        public static CTPivotFields parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotFields)getTypeLoader().parse(reader, CTPivotFields.type, xmlOptions);
        }
        
        public static CTPivotFields parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPivotFields)getTypeLoader().parse(xmlStreamReader, CTPivotFields.type, (XmlOptions)null);
        }
        
        public static CTPivotFields parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotFields)getTypeLoader().parse(xmlStreamReader, CTPivotFields.type, xmlOptions);
        }
        
        public static CTPivotFields parse(final Node node) throws XmlException {
            return (CTPivotFields)getTypeLoader().parse(node, CTPivotFields.type, (XmlOptions)null);
        }
        
        public static CTPivotFields parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotFields)getTypeLoader().parse(node, CTPivotFields.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPivotFields parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPivotFields)getTypeLoader().parse(xmlInputStream, CTPivotFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPivotFields parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPivotFields)getTypeLoader().parse(xmlInputStream, CTPivotFields.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotFields.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotFields.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
