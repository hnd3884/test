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

public interface CTVectorLpstr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTVectorLpstr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctvectorlpstr9b1dtype");
    
    CTVector getVector();
    
    void setVector(final CTVector p0);
    
    CTVector addNewVector();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTVectorLpstr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTVectorLpstr newInstance() {
            return (CTVectorLpstr)getTypeLoader().newInstance(CTVectorLpstr.type, (XmlOptions)null);
        }
        
        public static CTVectorLpstr newInstance(final XmlOptions xmlOptions) {
            return (CTVectorLpstr)getTypeLoader().newInstance(CTVectorLpstr.type, xmlOptions);
        }
        
        public static CTVectorLpstr parse(final String s) throws XmlException {
            return (CTVectorLpstr)getTypeLoader().parse(s, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        public static CTVectorLpstr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTVectorLpstr)getTypeLoader().parse(s, CTVectorLpstr.type, xmlOptions);
        }
        
        public static CTVectorLpstr parse(final File file) throws XmlException, IOException {
            return (CTVectorLpstr)getTypeLoader().parse(file, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        public static CTVectorLpstr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVectorLpstr)getTypeLoader().parse(file, CTVectorLpstr.type, xmlOptions);
        }
        
        public static CTVectorLpstr parse(final URL url) throws XmlException, IOException {
            return (CTVectorLpstr)getTypeLoader().parse(url, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        public static CTVectorLpstr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVectorLpstr)getTypeLoader().parse(url, CTVectorLpstr.type, xmlOptions);
        }
        
        public static CTVectorLpstr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTVectorLpstr)getTypeLoader().parse(inputStream, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        public static CTVectorLpstr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVectorLpstr)getTypeLoader().parse(inputStream, CTVectorLpstr.type, xmlOptions);
        }
        
        public static CTVectorLpstr parse(final Reader reader) throws XmlException, IOException {
            return (CTVectorLpstr)getTypeLoader().parse(reader, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        public static CTVectorLpstr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVectorLpstr)getTypeLoader().parse(reader, CTVectorLpstr.type, xmlOptions);
        }
        
        public static CTVectorLpstr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTVectorLpstr)getTypeLoader().parse(xmlStreamReader, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        public static CTVectorLpstr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTVectorLpstr)getTypeLoader().parse(xmlStreamReader, CTVectorLpstr.type, xmlOptions);
        }
        
        public static CTVectorLpstr parse(final Node node) throws XmlException {
            return (CTVectorLpstr)getTypeLoader().parse(node, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        public static CTVectorLpstr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTVectorLpstr)getTypeLoader().parse(node, CTVectorLpstr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTVectorLpstr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTVectorLpstr)getTypeLoader().parse(xmlInputStream, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTVectorLpstr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTVectorLpstr)getTypeLoader().parse(xmlInputStream, CTVectorLpstr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVectorLpstr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVectorLpstr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
