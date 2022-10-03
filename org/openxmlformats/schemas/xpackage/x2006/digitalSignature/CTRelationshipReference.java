package org.openxmlformats.schemas.xpackage.x2006.digitalSignature;

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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;

public interface CTRelationshipReference extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRelationshipReference.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("ctrelationshipreferencee68ftype");
    
    String getSourceId();
    
    XmlString xgetSourceId();
    
    void setSourceId(final String p0);
    
    void xsetSourceId(final XmlString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRelationshipReference.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRelationshipReference newInstance() {
            return (CTRelationshipReference)getTypeLoader().newInstance(CTRelationshipReference.type, (XmlOptions)null);
        }
        
        public static CTRelationshipReference newInstance(final XmlOptions xmlOptions) {
            return (CTRelationshipReference)getTypeLoader().newInstance(CTRelationshipReference.type, xmlOptions);
        }
        
        public static CTRelationshipReference parse(final String s) throws XmlException {
            return (CTRelationshipReference)getTypeLoader().parse(s, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        public static CTRelationshipReference parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelationshipReference)getTypeLoader().parse(s, CTRelationshipReference.type, xmlOptions);
        }
        
        public static CTRelationshipReference parse(final File file) throws XmlException, IOException {
            return (CTRelationshipReference)getTypeLoader().parse(file, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        public static CTRelationshipReference parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelationshipReference)getTypeLoader().parse(file, CTRelationshipReference.type, xmlOptions);
        }
        
        public static CTRelationshipReference parse(final URL url) throws XmlException, IOException {
            return (CTRelationshipReference)getTypeLoader().parse(url, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        public static CTRelationshipReference parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelationshipReference)getTypeLoader().parse(url, CTRelationshipReference.type, xmlOptions);
        }
        
        public static CTRelationshipReference parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRelationshipReference)getTypeLoader().parse(inputStream, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        public static CTRelationshipReference parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelationshipReference)getTypeLoader().parse(inputStream, CTRelationshipReference.type, xmlOptions);
        }
        
        public static CTRelationshipReference parse(final Reader reader) throws XmlException, IOException {
            return (CTRelationshipReference)getTypeLoader().parse(reader, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        public static CTRelationshipReference parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRelationshipReference)getTypeLoader().parse(reader, CTRelationshipReference.type, xmlOptions);
        }
        
        public static CTRelationshipReference parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRelationshipReference)getTypeLoader().parse(xmlStreamReader, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        public static CTRelationshipReference parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelationshipReference)getTypeLoader().parse(xmlStreamReader, CTRelationshipReference.type, xmlOptions);
        }
        
        public static CTRelationshipReference parse(final Node node) throws XmlException {
            return (CTRelationshipReference)getTypeLoader().parse(node, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        public static CTRelationshipReference parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRelationshipReference)getTypeLoader().parse(node, CTRelationshipReference.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRelationshipReference parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRelationshipReference)getTypeLoader().parse(xmlInputStream, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRelationshipReference parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRelationshipReference)getTypeLoader().parse(xmlInputStream, CTRelationshipReference.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRelationshipReference.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRelationshipReference.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
