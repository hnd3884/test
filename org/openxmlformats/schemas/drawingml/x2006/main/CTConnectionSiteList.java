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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTConnectionSiteList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTConnectionSiteList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctconnectionsitelistab9etype");
    
    List<CTConnectionSite> getCxnList();
    
    @Deprecated
    CTConnectionSite[] getCxnArray();
    
    CTConnectionSite getCxnArray(final int p0);
    
    int sizeOfCxnArray();
    
    void setCxnArray(final CTConnectionSite[] p0);
    
    void setCxnArray(final int p0, final CTConnectionSite p1);
    
    CTConnectionSite insertNewCxn(final int p0);
    
    CTConnectionSite addNewCxn();
    
    void removeCxn(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTConnectionSiteList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTConnectionSiteList newInstance() {
            return (CTConnectionSiteList)getTypeLoader().newInstance(CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        public static CTConnectionSiteList newInstance(final XmlOptions xmlOptions) {
            return (CTConnectionSiteList)getTypeLoader().newInstance(CTConnectionSiteList.type, xmlOptions);
        }
        
        public static CTConnectionSiteList parse(final String s) throws XmlException {
            return (CTConnectionSiteList)getTypeLoader().parse(s, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        public static CTConnectionSiteList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectionSiteList)getTypeLoader().parse(s, CTConnectionSiteList.type, xmlOptions);
        }
        
        public static CTConnectionSiteList parse(final File file) throws XmlException, IOException {
            return (CTConnectionSiteList)getTypeLoader().parse(file, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        public static CTConnectionSiteList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectionSiteList)getTypeLoader().parse(file, CTConnectionSiteList.type, xmlOptions);
        }
        
        public static CTConnectionSiteList parse(final URL url) throws XmlException, IOException {
            return (CTConnectionSiteList)getTypeLoader().parse(url, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        public static CTConnectionSiteList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectionSiteList)getTypeLoader().parse(url, CTConnectionSiteList.type, xmlOptions);
        }
        
        public static CTConnectionSiteList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTConnectionSiteList)getTypeLoader().parse(inputStream, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        public static CTConnectionSiteList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectionSiteList)getTypeLoader().parse(inputStream, CTConnectionSiteList.type, xmlOptions);
        }
        
        public static CTConnectionSiteList parse(final Reader reader) throws XmlException, IOException {
            return (CTConnectionSiteList)getTypeLoader().parse(reader, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        public static CTConnectionSiteList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConnectionSiteList)getTypeLoader().parse(reader, CTConnectionSiteList.type, xmlOptions);
        }
        
        public static CTConnectionSiteList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTConnectionSiteList)getTypeLoader().parse(xmlStreamReader, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        public static CTConnectionSiteList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectionSiteList)getTypeLoader().parse(xmlStreamReader, CTConnectionSiteList.type, xmlOptions);
        }
        
        public static CTConnectionSiteList parse(final Node node) throws XmlException {
            return (CTConnectionSiteList)getTypeLoader().parse(node, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        public static CTConnectionSiteList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTConnectionSiteList)getTypeLoader().parse(node, CTConnectionSiteList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTConnectionSiteList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTConnectionSiteList)getTypeLoader().parse(xmlInputStream, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTConnectionSiteList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTConnectionSiteList)getTypeLoader().parse(xmlInputStream, CTConnectionSiteList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnectionSiteList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConnectionSiteList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
