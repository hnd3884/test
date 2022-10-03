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

public interface CTTablePart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTablePart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablepart1140type");
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTablePart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTablePart newInstance() {
            return (CTTablePart)getTypeLoader().newInstance(CTTablePart.type, (XmlOptions)null);
        }
        
        public static CTTablePart newInstance(final XmlOptions xmlOptions) {
            return (CTTablePart)getTypeLoader().newInstance(CTTablePart.type, xmlOptions);
        }
        
        public static CTTablePart parse(final String s) throws XmlException {
            return (CTTablePart)getTypeLoader().parse(s, CTTablePart.type, (XmlOptions)null);
        }
        
        public static CTTablePart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTablePart)getTypeLoader().parse(s, CTTablePart.type, xmlOptions);
        }
        
        public static CTTablePart parse(final File file) throws XmlException, IOException {
            return (CTTablePart)getTypeLoader().parse(file, CTTablePart.type, (XmlOptions)null);
        }
        
        public static CTTablePart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTablePart)getTypeLoader().parse(file, CTTablePart.type, xmlOptions);
        }
        
        public static CTTablePart parse(final URL url) throws XmlException, IOException {
            return (CTTablePart)getTypeLoader().parse(url, CTTablePart.type, (XmlOptions)null);
        }
        
        public static CTTablePart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTablePart)getTypeLoader().parse(url, CTTablePart.type, xmlOptions);
        }
        
        public static CTTablePart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTablePart)getTypeLoader().parse(inputStream, CTTablePart.type, (XmlOptions)null);
        }
        
        public static CTTablePart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTablePart)getTypeLoader().parse(inputStream, CTTablePart.type, xmlOptions);
        }
        
        public static CTTablePart parse(final Reader reader) throws XmlException, IOException {
            return (CTTablePart)getTypeLoader().parse(reader, CTTablePart.type, (XmlOptions)null);
        }
        
        public static CTTablePart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTablePart)getTypeLoader().parse(reader, CTTablePart.type, xmlOptions);
        }
        
        public static CTTablePart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTablePart)getTypeLoader().parse(xmlStreamReader, CTTablePart.type, (XmlOptions)null);
        }
        
        public static CTTablePart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTablePart)getTypeLoader().parse(xmlStreamReader, CTTablePart.type, xmlOptions);
        }
        
        public static CTTablePart parse(final Node node) throws XmlException {
            return (CTTablePart)getTypeLoader().parse(node, CTTablePart.type, (XmlOptions)null);
        }
        
        public static CTTablePart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTablePart)getTypeLoader().parse(node, CTTablePart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTablePart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTablePart)getTypeLoader().parse(xmlInputStream, CTTablePart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTablePart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTablePart)getTypeLoader().parse(xmlInputStream, CTTablePart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTablePart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTablePart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
