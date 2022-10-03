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

public interface CTWorksheetSource extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTWorksheetSource.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctworksheetsourced4c8type");
    
    String getRef();
    
    STRef xgetRef();
    
    boolean isSetRef();
    
    void setRef(final String p0);
    
    void xsetRef(final STRef p0);
    
    void unsetRef();
    
    String getName();
    
    STXstring xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    void unsetName();
    
    String getSheet();
    
    STXstring xgetSheet();
    
    boolean isSetSheet();
    
    void setSheet(final String p0);
    
    void xsetSheet(final STXstring p0);
    
    void unsetSheet();
    
    String getId();
    
    STRelationshipId xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    void unsetId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTWorksheetSource.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTWorksheetSource newInstance() {
            return (CTWorksheetSource)getTypeLoader().newInstance(CTWorksheetSource.type, (XmlOptions)null);
        }
        
        public static CTWorksheetSource newInstance(final XmlOptions xmlOptions) {
            return (CTWorksheetSource)getTypeLoader().newInstance(CTWorksheetSource.type, xmlOptions);
        }
        
        public static CTWorksheetSource parse(final String s) throws XmlException {
            return (CTWorksheetSource)getTypeLoader().parse(s, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        public static CTWorksheetSource parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorksheetSource)getTypeLoader().parse(s, CTWorksheetSource.type, xmlOptions);
        }
        
        public static CTWorksheetSource parse(final File file) throws XmlException, IOException {
            return (CTWorksheetSource)getTypeLoader().parse(file, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        public static CTWorksheetSource parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorksheetSource)getTypeLoader().parse(file, CTWorksheetSource.type, xmlOptions);
        }
        
        public static CTWorksheetSource parse(final URL url) throws XmlException, IOException {
            return (CTWorksheetSource)getTypeLoader().parse(url, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        public static CTWorksheetSource parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorksheetSource)getTypeLoader().parse(url, CTWorksheetSource.type, xmlOptions);
        }
        
        public static CTWorksheetSource parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTWorksheetSource)getTypeLoader().parse(inputStream, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        public static CTWorksheetSource parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorksheetSource)getTypeLoader().parse(inputStream, CTWorksheetSource.type, xmlOptions);
        }
        
        public static CTWorksheetSource parse(final Reader reader) throws XmlException, IOException {
            return (CTWorksheetSource)getTypeLoader().parse(reader, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        public static CTWorksheetSource parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorksheetSource)getTypeLoader().parse(reader, CTWorksheetSource.type, xmlOptions);
        }
        
        public static CTWorksheetSource parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTWorksheetSource)getTypeLoader().parse(xmlStreamReader, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        public static CTWorksheetSource parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorksheetSource)getTypeLoader().parse(xmlStreamReader, CTWorksheetSource.type, xmlOptions);
        }
        
        public static CTWorksheetSource parse(final Node node) throws XmlException {
            return (CTWorksheetSource)getTypeLoader().parse(node, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        public static CTWorksheetSource parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorksheetSource)getTypeLoader().parse(node, CTWorksheetSource.type, xmlOptions);
        }
        
        @Deprecated
        public static CTWorksheetSource parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTWorksheetSource)getTypeLoader().parse(xmlInputStream, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTWorksheetSource parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTWorksheetSource)getTypeLoader().parse(xmlInputStream, CTWorksheetSource.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorksheetSource.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorksheetSource.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
