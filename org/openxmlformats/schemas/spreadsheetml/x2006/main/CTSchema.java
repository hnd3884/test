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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSchema extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSchema.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctschema0e6atype");
    
    String getID();
    
    XmlString xgetID();
    
    void setID(final String p0);
    
    void xsetID(final XmlString p0);
    
    String getSchemaRef();
    
    XmlString xgetSchemaRef();
    
    boolean isSetSchemaRef();
    
    void setSchemaRef(final String p0);
    
    void xsetSchemaRef(final XmlString p0);
    
    void unsetSchemaRef();
    
    String getNamespace();
    
    XmlString xgetNamespace();
    
    boolean isSetNamespace();
    
    void setNamespace(final String p0);
    
    void xsetNamespace(final XmlString p0);
    
    void unsetNamespace();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSchema.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSchema newInstance() {
            return (CTSchema)getTypeLoader().newInstance(CTSchema.type, (XmlOptions)null);
        }
        
        public static CTSchema newInstance(final XmlOptions xmlOptions) {
            return (CTSchema)getTypeLoader().newInstance(CTSchema.type, xmlOptions);
        }
        
        public static CTSchema parse(final String s) throws XmlException {
            return (CTSchema)getTypeLoader().parse(s, CTSchema.type, (XmlOptions)null);
        }
        
        public static CTSchema parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSchema)getTypeLoader().parse(s, CTSchema.type, xmlOptions);
        }
        
        public static CTSchema parse(final File file) throws XmlException, IOException {
            return (CTSchema)getTypeLoader().parse(file, CTSchema.type, (XmlOptions)null);
        }
        
        public static CTSchema parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSchema)getTypeLoader().parse(file, CTSchema.type, xmlOptions);
        }
        
        public static CTSchema parse(final URL url) throws XmlException, IOException {
            return (CTSchema)getTypeLoader().parse(url, CTSchema.type, (XmlOptions)null);
        }
        
        public static CTSchema parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSchema)getTypeLoader().parse(url, CTSchema.type, xmlOptions);
        }
        
        public static CTSchema parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSchema)getTypeLoader().parse(inputStream, CTSchema.type, (XmlOptions)null);
        }
        
        public static CTSchema parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSchema)getTypeLoader().parse(inputStream, CTSchema.type, xmlOptions);
        }
        
        public static CTSchema parse(final Reader reader) throws XmlException, IOException {
            return (CTSchema)getTypeLoader().parse(reader, CTSchema.type, (XmlOptions)null);
        }
        
        public static CTSchema parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSchema)getTypeLoader().parse(reader, CTSchema.type, xmlOptions);
        }
        
        public static CTSchema parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSchema)getTypeLoader().parse(xmlStreamReader, CTSchema.type, (XmlOptions)null);
        }
        
        public static CTSchema parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSchema)getTypeLoader().parse(xmlStreamReader, CTSchema.type, xmlOptions);
        }
        
        public static CTSchema parse(final Node node) throws XmlException {
            return (CTSchema)getTypeLoader().parse(node, CTSchema.type, (XmlOptions)null);
        }
        
        public static CTSchema parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSchema)getTypeLoader().parse(node, CTSchema.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSchema parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSchema)getTypeLoader().parse(xmlInputStream, CTSchema.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSchema parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSchema)getTypeLoader().parse(xmlInputStream, CTSchema.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSchema.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSchema.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
