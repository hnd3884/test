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

public interface CTExternalReference extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalReference.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternalreference945ftype");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalReference.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalReference newInstance() {
            return (CTExternalReference)getTypeLoader().newInstance(CTExternalReference.type, (XmlOptions)null);
        }
        
        public static CTExternalReference newInstance(final XmlOptions xmlOptions) {
            return (CTExternalReference)getTypeLoader().newInstance(CTExternalReference.type, xmlOptions);
        }
        
        public static CTExternalReference parse(final String s) throws XmlException {
            return (CTExternalReference)getTypeLoader().parse(s, CTExternalReference.type, (XmlOptions)null);
        }
        
        public static CTExternalReference parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalReference)getTypeLoader().parse(s, CTExternalReference.type, xmlOptions);
        }
        
        public static CTExternalReference parse(final File file) throws XmlException, IOException {
            return (CTExternalReference)getTypeLoader().parse(file, CTExternalReference.type, (XmlOptions)null);
        }
        
        public static CTExternalReference parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalReference)getTypeLoader().parse(file, CTExternalReference.type, xmlOptions);
        }
        
        public static CTExternalReference parse(final URL url) throws XmlException, IOException {
            return (CTExternalReference)getTypeLoader().parse(url, CTExternalReference.type, (XmlOptions)null);
        }
        
        public static CTExternalReference parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalReference)getTypeLoader().parse(url, CTExternalReference.type, xmlOptions);
        }
        
        public static CTExternalReference parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalReference)getTypeLoader().parse(inputStream, CTExternalReference.type, (XmlOptions)null);
        }
        
        public static CTExternalReference parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalReference)getTypeLoader().parse(inputStream, CTExternalReference.type, xmlOptions);
        }
        
        public static CTExternalReference parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalReference)getTypeLoader().parse(reader, CTExternalReference.type, (XmlOptions)null);
        }
        
        public static CTExternalReference parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalReference)getTypeLoader().parse(reader, CTExternalReference.type, xmlOptions);
        }
        
        public static CTExternalReference parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalReference)getTypeLoader().parse(xmlStreamReader, CTExternalReference.type, (XmlOptions)null);
        }
        
        public static CTExternalReference parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalReference)getTypeLoader().parse(xmlStreamReader, CTExternalReference.type, xmlOptions);
        }
        
        public static CTExternalReference parse(final Node node) throws XmlException {
            return (CTExternalReference)getTypeLoader().parse(node, CTExternalReference.type, (XmlOptions)null);
        }
        
        public static CTExternalReference parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalReference)getTypeLoader().parse(node, CTExternalReference.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalReference parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalReference)getTypeLoader().parse(xmlInputStream, CTExternalReference.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalReference parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalReference)getTypeLoader().parse(xmlInputStream, CTExternalReference.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalReference.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalReference.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
