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

public interface CTHyperlink extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTHyperlink.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cthyperlink0c85type");
    
    String getRef();
    
    STRef xgetRef();
    
    void setRef(final String p0);
    
    void xsetRef(final STRef p0);
    
    String getId();
    
    STRelationshipId xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    void unsetId();
    
    String getLocation();
    
    STXstring xgetLocation();
    
    boolean isSetLocation();
    
    void setLocation(final String p0);
    
    void xsetLocation(final STXstring p0);
    
    void unsetLocation();
    
    String getTooltip();
    
    STXstring xgetTooltip();
    
    boolean isSetTooltip();
    
    void setTooltip(final String p0);
    
    void xsetTooltip(final STXstring p0);
    
    void unsetTooltip();
    
    String getDisplay();
    
    STXstring xgetDisplay();
    
    boolean isSetDisplay();
    
    void setDisplay(final String p0);
    
    void xsetDisplay(final STXstring p0);
    
    void unsetDisplay();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTHyperlink.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTHyperlink newInstance() {
            return (CTHyperlink)getTypeLoader().newInstance(CTHyperlink.type, (XmlOptions)null);
        }
        
        public static CTHyperlink newInstance(final XmlOptions xmlOptions) {
            return (CTHyperlink)getTypeLoader().newInstance(CTHyperlink.type, xmlOptions);
        }
        
        public static CTHyperlink parse(final String s) throws XmlException {
            return (CTHyperlink)getTypeLoader().parse(s, CTHyperlink.type, (XmlOptions)null);
        }
        
        public static CTHyperlink parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTHyperlink)getTypeLoader().parse(s, CTHyperlink.type, xmlOptions);
        }
        
        public static CTHyperlink parse(final File file) throws XmlException, IOException {
            return (CTHyperlink)getTypeLoader().parse(file, CTHyperlink.type, (XmlOptions)null);
        }
        
        public static CTHyperlink parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHyperlink)getTypeLoader().parse(file, CTHyperlink.type, xmlOptions);
        }
        
        public static CTHyperlink parse(final URL url) throws XmlException, IOException {
            return (CTHyperlink)getTypeLoader().parse(url, CTHyperlink.type, (XmlOptions)null);
        }
        
        public static CTHyperlink parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHyperlink)getTypeLoader().parse(url, CTHyperlink.type, xmlOptions);
        }
        
        public static CTHyperlink parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTHyperlink)getTypeLoader().parse(inputStream, CTHyperlink.type, (XmlOptions)null);
        }
        
        public static CTHyperlink parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHyperlink)getTypeLoader().parse(inputStream, CTHyperlink.type, xmlOptions);
        }
        
        public static CTHyperlink parse(final Reader reader) throws XmlException, IOException {
            return (CTHyperlink)getTypeLoader().parse(reader, CTHyperlink.type, (XmlOptions)null);
        }
        
        public static CTHyperlink parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTHyperlink)getTypeLoader().parse(reader, CTHyperlink.type, xmlOptions);
        }
        
        public static CTHyperlink parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTHyperlink)getTypeLoader().parse(xmlStreamReader, CTHyperlink.type, (XmlOptions)null);
        }
        
        public static CTHyperlink parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTHyperlink)getTypeLoader().parse(xmlStreamReader, CTHyperlink.type, xmlOptions);
        }
        
        public static CTHyperlink parse(final Node node) throws XmlException {
            return (CTHyperlink)getTypeLoader().parse(node, CTHyperlink.type, (XmlOptions)null);
        }
        
        public static CTHyperlink parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTHyperlink)getTypeLoader().parse(node, CTHyperlink.type, xmlOptions);
        }
        
        @Deprecated
        public static CTHyperlink parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTHyperlink)getTypeLoader().parse(xmlInputStream, CTHyperlink.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTHyperlink parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTHyperlink)getTypeLoader().parse(xmlInputStream, CTHyperlink.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHyperlink.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTHyperlink.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
