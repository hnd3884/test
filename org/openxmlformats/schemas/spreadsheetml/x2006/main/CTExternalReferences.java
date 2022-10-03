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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTExternalReferences extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExternalReferences.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctexternalreferencesd77ctype");
    
    List<CTExternalReference> getExternalReferenceList();
    
    @Deprecated
    CTExternalReference[] getExternalReferenceArray();
    
    CTExternalReference getExternalReferenceArray(final int p0);
    
    int sizeOfExternalReferenceArray();
    
    void setExternalReferenceArray(final CTExternalReference[] p0);
    
    void setExternalReferenceArray(final int p0, final CTExternalReference p1);
    
    CTExternalReference insertNewExternalReference(final int p0);
    
    CTExternalReference addNewExternalReference();
    
    void removeExternalReference(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExternalReferences.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExternalReferences newInstance() {
            return (CTExternalReferences)getTypeLoader().newInstance(CTExternalReferences.type, (XmlOptions)null);
        }
        
        public static CTExternalReferences newInstance(final XmlOptions xmlOptions) {
            return (CTExternalReferences)getTypeLoader().newInstance(CTExternalReferences.type, xmlOptions);
        }
        
        public static CTExternalReferences parse(final String s) throws XmlException {
            return (CTExternalReferences)getTypeLoader().parse(s, CTExternalReferences.type, (XmlOptions)null);
        }
        
        public static CTExternalReferences parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalReferences)getTypeLoader().parse(s, CTExternalReferences.type, xmlOptions);
        }
        
        public static CTExternalReferences parse(final File file) throws XmlException, IOException {
            return (CTExternalReferences)getTypeLoader().parse(file, CTExternalReferences.type, (XmlOptions)null);
        }
        
        public static CTExternalReferences parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalReferences)getTypeLoader().parse(file, CTExternalReferences.type, xmlOptions);
        }
        
        public static CTExternalReferences parse(final URL url) throws XmlException, IOException {
            return (CTExternalReferences)getTypeLoader().parse(url, CTExternalReferences.type, (XmlOptions)null);
        }
        
        public static CTExternalReferences parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalReferences)getTypeLoader().parse(url, CTExternalReferences.type, xmlOptions);
        }
        
        public static CTExternalReferences parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExternalReferences)getTypeLoader().parse(inputStream, CTExternalReferences.type, (XmlOptions)null);
        }
        
        public static CTExternalReferences parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalReferences)getTypeLoader().parse(inputStream, CTExternalReferences.type, xmlOptions);
        }
        
        public static CTExternalReferences parse(final Reader reader) throws XmlException, IOException {
            return (CTExternalReferences)getTypeLoader().parse(reader, CTExternalReferences.type, (XmlOptions)null);
        }
        
        public static CTExternalReferences parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExternalReferences)getTypeLoader().parse(reader, CTExternalReferences.type, xmlOptions);
        }
        
        public static CTExternalReferences parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExternalReferences)getTypeLoader().parse(xmlStreamReader, CTExternalReferences.type, (XmlOptions)null);
        }
        
        public static CTExternalReferences parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalReferences)getTypeLoader().parse(xmlStreamReader, CTExternalReferences.type, xmlOptions);
        }
        
        public static CTExternalReferences parse(final Node node) throws XmlException {
            return (CTExternalReferences)getTypeLoader().parse(node, CTExternalReferences.type, (XmlOptions)null);
        }
        
        public static CTExternalReferences parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExternalReferences)getTypeLoader().parse(node, CTExternalReferences.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExternalReferences parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExternalReferences)getTypeLoader().parse(xmlInputStream, CTExternalReferences.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExternalReferences parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExternalReferences)getTypeLoader().parse(xmlInputStream, CTExternalReferences.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalReferences.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExternalReferences.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
