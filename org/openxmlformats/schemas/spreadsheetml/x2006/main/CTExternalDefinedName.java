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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTExternalDefinedName extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalDefinedName.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternaldefinedname9408type");
    
    String getName();
    
    STXstring xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    String getRefersTo();
    
    STXstring xgetRefersTo();
    
    boolean isSetRefersTo();
    
    void setRefersTo(final String p0);
    
    void xsetRefersTo(final STXstring p0);
    
    void unsetRefersTo();
    
    long getSheetId();
    
    XmlUnsignedInt xgetSheetId();
    
    boolean isSetSheetId();
    
    void setSheetId(final long p0);
    
    void xsetSheetId(final XmlUnsignedInt p0);
    
    void unsetSheetId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalDefinedName.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalDefinedName newInstance() {
            return (CTExternalDefinedName)getTypeLoader().newInstance(CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedName newInstance(final XmlOptions xmlOptions) {
            return (CTExternalDefinedName)getTypeLoader().newInstance(CTExternalDefinedName.type, xmlOptions);
        }
        
        public static CTExternalDefinedName parse(final String s) throws XmlException {
            return (CTExternalDefinedName)getTypeLoader().parse(s, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedName parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalDefinedName)getTypeLoader().parse(s, CTExternalDefinedName.type, xmlOptions);
        }
        
        public static CTExternalDefinedName parse(final File file) throws XmlException, IOException {
            return (CTExternalDefinedName)getTypeLoader().parse(file, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedName parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalDefinedName)getTypeLoader().parse(file, CTExternalDefinedName.type, xmlOptions);
        }
        
        public static CTExternalDefinedName parse(final URL url) throws XmlException, IOException {
            return (CTExternalDefinedName)getTypeLoader().parse(url, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedName parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalDefinedName)getTypeLoader().parse(url, CTExternalDefinedName.type, xmlOptions);
        }
        
        public static CTExternalDefinedName parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalDefinedName)getTypeLoader().parse(inputStream, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedName parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalDefinedName)getTypeLoader().parse(inputStream, CTExternalDefinedName.type, xmlOptions);
        }
        
        public static CTExternalDefinedName parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalDefinedName)getTypeLoader().parse(reader, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedName parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalDefinedName)getTypeLoader().parse(reader, CTExternalDefinedName.type, xmlOptions);
        }
        
        public static CTExternalDefinedName parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalDefinedName)getTypeLoader().parse(xmlStreamReader, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedName parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalDefinedName)getTypeLoader().parse(xmlStreamReader, CTExternalDefinedName.type, xmlOptions);
        }
        
        public static CTExternalDefinedName parse(final Node node) throws XmlException {
            return (CTExternalDefinedName)getTypeLoader().parse(node, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        public static CTExternalDefinedName parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalDefinedName)getTypeLoader().parse(node, CTExternalDefinedName.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalDefinedName parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalDefinedName)getTypeLoader().parse(xmlInputStream, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalDefinedName parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalDefinedName)getTypeLoader().parse(xmlInputStream, CTExternalDefinedName.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalDefinedName.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalDefinedName.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
