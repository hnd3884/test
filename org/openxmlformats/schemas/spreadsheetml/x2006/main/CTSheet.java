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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSheet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheet4dbetype");
    
    String getName();
    
    STXstring xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    long getSheetId();
    
    XmlUnsignedInt xgetSheetId();
    
    void setSheetId(final long p0);
    
    void xsetSheetId(final XmlUnsignedInt p0);
    
    STSheetState.Enum getState();
    
    STSheetState xgetState();
    
    boolean isSetState();
    
    void setState(final STSheetState.Enum p0);
    
    void xsetState(final STSheetState p0);
    
    void unsetState();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheet newInstance() {
            return (CTSheet)getTypeLoader().newInstance(CTSheet.type, (XmlOptions)null);
        }
        
        public static CTSheet newInstance(final XmlOptions xmlOptions) {
            return (CTSheet)getTypeLoader().newInstance(CTSheet.type, xmlOptions);
        }
        
        public static CTSheet parse(final String s) throws XmlException {
            return (CTSheet)getTypeLoader().parse(s, CTSheet.type, (XmlOptions)null);
        }
        
        public static CTSheet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheet)getTypeLoader().parse(s, CTSheet.type, xmlOptions);
        }
        
        public static CTSheet parse(final File file) throws XmlException, IOException {
            return (CTSheet)getTypeLoader().parse(file, CTSheet.type, (XmlOptions)null);
        }
        
        public static CTSheet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheet)getTypeLoader().parse(file, CTSheet.type, xmlOptions);
        }
        
        public static CTSheet parse(final URL url) throws XmlException, IOException {
            return (CTSheet)getTypeLoader().parse(url, CTSheet.type, (XmlOptions)null);
        }
        
        public static CTSheet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheet)getTypeLoader().parse(url, CTSheet.type, xmlOptions);
        }
        
        public static CTSheet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheet)getTypeLoader().parse(inputStream, CTSheet.type, (XmlOptions)null);
        }
        
        public static CTSheet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheet)getTypeLoader().parse(inputStream, CTSheet.type, xmlOptions);
        }
        
        public static CTSheet parse(final Reader reader) throws XmlException, IOException {
            return (CTSheet)getTypeLoader().parse(reader, CTSheet.type, (XmlOptions)null);
        }
        
        public static CTSheet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheet)getTypeLoader().parse(reader, CTSheet.type, xmlOptions);
        }
        
        public static CTSheet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheet)getTypeLoader().parse(xmlStreamReader, CTSheet.type, (XmlOptions)null);
        }
        
        public static CTSheet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheet)getTypeLoader().parse(xmlStreamReader, CTSheet.type, xmlOptions);
        }
        
        public static CTSheet parse(final Node node) throws XmlException {
            return (CTSheet)getTypeLoader().parse(node, CTSheet.type, (XmlOptions)null);
        }
        
        public static CTSheet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheet)getTypeLoader().parse(node, CTSheet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheet)getTypeLoader().parse(xmlInputStream, CTSheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheet)getTypeLoader().parse(xmlInputStream, CTSheet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
