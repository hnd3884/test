package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface SldMasterDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SldMasterDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sldmaster5156doctype");
    
    CTSlideMaster getSldMaster();
    
    void setSldMaster(final CTSlideMaster p0);
    
    CTSlideMaster addNewSldMaster();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SldMasterDocument.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SldMasterDocument newInstance() {
            return (SldMasterDocument)getTypeLoader().newInstance(SldMasterDocument.type, (XmlOptions)null);
        }
        
        public static SldMasterDocument newInstance(final XmlOptions xmlOptions) {
            return (SldMasterDocument)getTypeLoader().newInstance(SldMasterDocument.type, xmlOptions);
        }
        
        public static SldMasterDocument parse(final String s) throws XmlException {
            return (SldMasterDocument)getTypeLoader().parse(s, SldMasterDocument.type, (XmlOptions)null);
        }
        
        public static SldMasterDocument parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SldMasterDocument)getTypeLoader().parse(s, SldMasterDocument.type, xmlOptions);
        }
        
        public static SldMasterDocument parse(final File file) throws XmlException, IOException {
            return (SldMasterDocument)getTypeLoader().parse(file, SldMasterDocument.type, (XmlOptions)null);
        }
        
        public static SldMasterDocument parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SldMasterDocument)getTypeLoader().parse(file, SldMasterDocument.type, xmlOptions);
        }
        
        public static SldMasterDocument parse(final URL url) throws XmlException, IOException {
            return (SldMasterDocument)getTypeLoader().parse(url, SldMasterDocument.type, (XmlOptions)null);
        }
        
        public static SldMasterDocument parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SldMasterDocument)getTypeLoader().parse(url, SldMasterDocument.type, xmlOptions);
        }
        
        public static SldMasterDocument parse(final InputStream inputStream) throws XmlException, IOException {
            return (SldMasterDocument)getTypeLoader().parse(inputStream, SldMasterDocument.type, (XmlOptions)null);
        }
        
        public static SldMasterDocument parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SldMasterDocument)getTypeLoader().parse(inputStream, SldMasterDocument.type, xmlOptions);
        }
        
        public static SldMasterDocument parse(final Reader reader) throws XmlException, IOException {
            return (SldMasterDocument)getTypeLoader().parse(reader, SldMasterDocument.type, (XmlOptions)null);
        }
        
        public static SldMasterDocument parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SldMasterDocument)getTypeLoader().parse(reader, SldMasterDocument.type, xmlOptions);
        }
        
        public static SldMasterDocument parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SldMasterDocument)getTypeLoader().parse(xmlStreamReader, SldMasterDocument.type, (XmlOptions)null);
        }
        
        public static SldMasterDocument parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SldMasterDocument)getTypeLoader().parse(xmlStreamReader, SldMasterDocument.type, xmlOptions);
        }
        
        public static SldMasterDocument parse(final Node node) throws XmlException {
            return (SldMasterDocument)getTypeLoader().parse(node, SldMasterDocument.type, (XmlOptions)null);
        }
        
        public static SldMasterDocument parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SldMasterDocument)getTypeLoader().parse(node, SldMasterDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static SldMasterDocument parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SldMasterDocument)getTypeLoader().parse(xmlInputStream, SldMasterDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SldMasterDocument parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SldMasterDocument)getTypeLoader().parse(xmlInputStream, SldMasterDocument.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SldMasterDocument.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SldMasterDocument.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
