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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTMap extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTMap.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctmap023btype");
    
    CTDataBinding getDataBinding();
    
    boolean isSetDataBinding();
    
    void setDataBinding(final CTDataBinding p0);
    
    CTDataBinding addNewDataBinding();
    
    void unsetDataBinding();
    
    long getID();
    
    XmlUnsignedInt xgetID();
    
    void setID(final long p0);
    
    void xsetID(final XmlUnsignedInt p0);
    
    String getName();
    
    XmlString xgetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    String getRootElement();
    
    XmlString xgetRootElement();
    
    void setRootElement(final String p0);
    
    void xsetRootElement(final XmlString p0);
    
    String getSchemaID();
    
    XmlString xgetSchemaID();
    
    void setSchemaID(final String p0);
    
    void xsetSchemaID(final XmlString p0);
    
    boolean getShowImportExportValidationErrors();
    
    XmlBoolean xgetShowImportExportValidationErrors();
    
    void setShowImportExportValidationErrors(final boolean p0);
    
    void xsetShowImportExportValidationErrors(final XmlBoolean p0);
    
    boolean getAutoFit();
    
    XmlBoolean xgetAutoFit();
    
    void setAutoFit(final boolean p0);
    
    void xsetAutoFit(final XmlBoolean p0);
    
    boolean getAppend();
    
    XmlBoolean xgetAppend();
    
    void setAppend(final boolean p0);
    
    void xsetAppend(final XmlBoolean p0);
    
    boolean getPreserveSortAFLayout();
    
    XmlBoolean xgetPreserveSortAFLayout();
    
    void setPreserveSortAFLayout(final boolean p0);
    
    void xsetPreserveSortAFLayout(final XmlBoolean p0);
    
    boolean getPreserveFormat();
    
    XmlBoolean xgetPreserveFormat();
    
    void setPreserveFormat(final boolean p0);
    
    void xsetPreserveFormat(final XmlBoolean p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTMap.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTMap newInstance() {
            return (CTMap)getTypeLoader().newInstance(CTMap.type, (XmlOptions)null);
        }
        
        public static CTMap newInstance(final XmlOptions xmlOptions) {
            return (CTMap)getTypeLoader().newInstance(CTMap.type, xmlOptions);
        }
        
        public static CTMap parse(final String s) throws XmlException {
            return (CTMap)getTypeLoader().parse(s, CTMap.type, (XmlOptions)null);
        }
        
        public static CTMap parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTMap)getTypeLoader().parse(s, CTMap.type, xmlOptions);
        }
        
        public static CTMap parse(final File file) throws XmlException, IOException {
            return (CTMap)getTypeLoader().parse(file, CTMap.type, (XmlOptions)null);
        }
        
        public static CTMap parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMap)getTypeLoader().parse(file, CTMap.type, xmlOptions);
        }
        
        public static CTMap parse(final URL url) throws XmlException, IOException {
            return (CTMap)getTypeLoader().parse(url, CTMap.type, (XmlOptions)null);
        }
        
        public static CTMap parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMap)getTypeLoader().parse(url, CTMap.type, xmlOptions);
        }
        
        public static CTMap parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTMap)getTypeLoader().parse(inputStream, CTMap.type, (XmlOptions)null);
        }
        
        public static CTMap parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMap)getTypeLoader().parse(inputStream, CTMap.type, xmlOptions);
        }
        
        public static CTMap parse(final Reader reader) throws XmlException, IOException {
            return (CTMap)getTypeLoader().parse(reader, CTMap.type, (XmlOptions)null);
        }
        
        public static CTMap parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTMap)getTypeLoader().parse(reader, CTMap.type, xmlOptions);
        }
        
        public static CTMap parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTMap)getTypeLoader().parse(xmlStreamReader, CTMap.type, (XmlOptions)null);
        }
        
        public static CTMap parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTMap)getTypeLoader().parse(xmlStreamReader, CTMap.type, xmlOptions);
        }
        
        public static CTMap parse(final Node node) throws XmlException {
            return (CTMap)getTypeLoader().parse(node, CTMap.type, (XmlOptions)null);
        }
        
        public static CTMap parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTMap)getTypeLoader().parse(node, CTMap.type, xmlOptions);
        }
        
        @Deprecated
        public static CTMap parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTMap)getTypeLoader().parse(xmlInputStream, CTMap.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTMap parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTMap)getTypeLoader().parse(xmlInputStream, CTMap.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMap.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTMap.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
