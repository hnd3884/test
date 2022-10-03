package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTNonVisualConnectorProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNonVisualConnectorProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnonvisualconnectorproperties6f8etype");
    
    CTConnectorLocking getCxnSpLocks();
    
    boolean isSetCxnSpLocks();
    
    void setCxnSpLocks(final CTConnectorLocking p0);
    
    CTConnectorLocking addNewCxnSpLocks();
    
    void unsetCxnSpLocks();
    
    CTConnection getStCxn();
    
    boolean isSetStCxn();
    
    void setStCxn(final CTConnection p0);
    
    CTConnection addNewStCxn();
    
    void unsetStCxn();
    
    CTConnection getEndCxn();
    
    boolean isSetEndCxn();
    
    void setEndCxn(final CTConnection p0);
    
    CTConnection addNewEndCxn();
    
    void unsetEndCxn();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNonVisualConnectorProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNonVisualConnectorProperties newInstance() {
            return (CTNonVisualConnectorProperties)getTypeLoader().newInstance(CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualConnectorProperties newInstance(final XmlOptions xmlOptions) {
            return (CTNonVisualConnectorProperties)getTypeLoader().newInstance(CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        public static CTNonVisualConnectorProperties parse(final String s) throws XmlException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(s, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualConnectorProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(s, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        public static CTNonVisualConnectorProperties parse(final File file) throws XmlException, IOException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(file, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualConnectorProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(file, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        public static CTNonVisualConnectorProperties parse(final URL url) throws XmlException, IOException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(url, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualConnectorProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(url, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        public static CTNonVisualConnectorProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(inputStream, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualConnectorProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(inputStream, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        public static CTNonVisualConnectorProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(reader, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualConnectorProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(reader, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        public static CTNonVisualConnectorProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(xmlStreamReader, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualConnectorProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(xmlStreamReader, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        public static CTNonVisualConnectorProperties parse(final Node node) throws XmlException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(node, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        public static CTNonVisualConnectorProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(node, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNonVisualConnectorProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(xmlInputStream, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNonVisualConnectorProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNonVisualConnectorProperties)getTypeLoader().parse(xmlInputStream, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualConnectorProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNonVisualConnectorProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
