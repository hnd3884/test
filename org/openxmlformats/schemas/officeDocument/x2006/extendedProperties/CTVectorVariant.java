package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties;

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
import org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVector;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTVectorVariant extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTVectorVariant.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctvectorvariant9d75type");
    
    CTVector getVector();
    
    void setVector(final CTVector p0);
    
    CTVector addNewVector();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTVectorVariant.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTVectorVariant newInstance() {
            return (CTVectorVariant)getTypeLoader().newInstance(CTVectorVariant.type, (XmlOptions)null);
        }
        
        public static CTVectorVariant newInstance(final XmlOptions xmlOptions) {
            return (CTVectorVariant)getTypeLoader().newInstance(CTVectorVariant.type, xmlOptions);
        }
        
        public static CTVectorVariant parse(final String s) throws XmlException {
            return (CTVectorVariant)getTypeLoader().parse(s, CTVectorVariant.type, (XmlOptions)null);
        }
        
        public static CTVectorVariant parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTVectorVariant)getTypeLoader().parse(s, CTVectorVariant.type, xmlOptions);
        }
        
        public static CTVectorVariant parse(final File file) throws XmlException, IOException {
            return (CTVectorVariant)getTypeLoader().parse(file, CTVectorVariant.type, (XmlOptions)null);
        }
        
        public static CTVectorVariant parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVectorVariant)getTypeLoader().parse(file, CTVectorVariant.type, xmlOptions);
        }
        
        public static CTVectorVariant parse(final URL url) throws XmlException, IOException {
            return (CTVectorVariant)getTypeLoader().parse(url, CTVectorVariant.type, (XmlOptions)null);
        }
        
        public static CTVectorVariant parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVectorVariant)getTypeLoader().parse(url, CTVectorVariant.type, xmlOptions);
        }
        
        public static CTVectorVariant parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTVectorVariant)getTypeLoader().parse(inputStream, CTVectorVariant.type, (XmlOptions)null);
        }
        
        public static CTVectorVariant parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVectorVariant)getTypeLoader().parse(inputStream, CTVectorVariant.type, xmlOptions);
        }
        
        public static CTVectorVariant parse(final Reader reader) throws XmlException, IOException {
            return (CTVectorVariant)getTypeLoader().parse(reader, CTVectorVariant.type, (XmlOptions)null);
        }
        
        public static CTVectorVariant parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVectorVariant)getTypeLoader().parse(reader, CTVectorVariant.type, xmlOptions);
        }
        
        public static CTVectorVariant parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTVectorVariant)getTypeLoader().parse(xmlStreamReader, CTVectorVariant.type, (XmlOptions)null);
        }
        
        public static CTVectorVariant parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTVectorVariant)getTypeLoader().parse(xmlStreamReader, CTVectorVariant.type, xmlOptions);
        }
        
        public static CTVectorVariant parse(final Node node) throws XmlException {
            return (CTVectorVariant)getTypeLoader().parse(node, CTVectorVariant.type, (XmlOptions)null);
        }
        
        public static CTVectorVariant parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTVectorVariant)getTypeLoader().parse(node, CTVectorVariant.type, xmlOptions);
        }
        
        @Deprecated
        public static CTVectorVariant parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTVectorVariant)getTypeLoader().parse(xmlInputStream, CTVectorVariant.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTVectorVariant parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTVectorVariant)getTypeLoader().parse(xmlInputStream, CTVectorVariant.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVectorVariant.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVectorVariant.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
