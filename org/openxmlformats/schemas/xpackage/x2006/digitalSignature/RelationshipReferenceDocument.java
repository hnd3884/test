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
import org.apache.xmlbeans.XmlObject;

public interface RelationshipReferenceDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(RelationshipReferenceDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("relationshipreference8903doctype");
    
    CTRelationshipReference getRelationshipReference();
    
    void setRelationshipReference(final CTRelationshipReference p0);
    
    CTRelationshipReference addNewRelationshipReference();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(RelationshipReferenceDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static RelationshipReferenceDocument newInstance() {
            return (RelationshipReferenceDocument)getTypeLoader().newInstance(RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        public static RelationshipReferenceDocument newInstance(final XmlOptions xmlOptions) {
            return (RelationshipReferenceDocument)getTypeLoader().newInstance(RelationshipReferenceDocument.type, xmlOptions);
        }
        
        public static RelationshipReferenceDocument parse(final String s) throws XmlException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(s, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        public static RelationshipReferenceDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(s, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        public static RelationshipReferenceDocument parse(final File file) throws XmlException, IOException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(file, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        public static RelationshipReferenceDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(file, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        public static RelationshipReferenceDocument parse(final URL url) throws XmlException, IOException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(url, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        public static RelationshipReferenceDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(url, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        public static RelationshipReferenceDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(inputStream, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        public static RelationshipReferenceDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(inputStream, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        public static RelationshipReferenceDocument parse(final Reader reader) throws XmlException, IOException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(reader, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        public static RelationshipReferenceDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(reader, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        public static RelationshipReferenceDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(xmlStreamReader, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        public static RelationshipReferenceDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(xmlStreamReader, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        public static RelationshipReferenceDocument parse(final Node node) throws XmlException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(node, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        public static RelationshipReferenceDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(node, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static RelationshipReferenceDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(xmlInputStream, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static RelationshipReferenceDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (RelationshipReferenceDocument)getTypeLoader().parse(xmlInputStream, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, RelationshipReferenceDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, RelationshipReferenceDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
