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

public interface CTTableCol extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableCol.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablecol19edtype");
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getW();
    
    STCoordinate xgetW();
    
    void setW(final long p0);
    
    void xsetW(final STCoordinate p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableCol.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableCol newInstance() {
            return (CTTableCol)getTypeLoader().newInstance(CTTableCol.type, (XmlOptions)null);
        }
        
        public static CTTableCol newInstance(final XmlOptions xmlOptions) {
            return (CTTableCol)getTypeLoader().newInstance(CTTableCol.type, xmlOptions);
        }
        
        public static CTTableCol parse(final String s) throws XmlException {
            return (CTTableCol)getTypeLoader().parse(s, CTTableCol.type, (XmlOptions)null);
        }
        
        public static CTTableCol parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCol)getTypeLoader().parse(s, CTTableCol.type, xmlOptions);
        }
        
        public static CTTableCol parse(final File file) throws XmlException, IOException {
            return (CTTableCol)getTypeLoader().parse(file, CTTableCol.type, (XmlOptions)null);
        }
        
        public static CTTableCol parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCol)getTypeLoader().parse(file, CTTableCol.type, xmlOptions);
        }
        
        public static CTTableCol parse(final URL url) throws XmlException, IOException {
            return (CTTableCol)getTypeLoader().parse(url, CTTableCol.type, (XmlOptions)null);
        }
        
        public static CTTableCol parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCol)getTypeLoader().parse(url, CTTableCol.type, xmlOptions);
        }
        
        public static CTTableCol parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableCol)getTypeLoader().parse(inputStream, CTTableCol.type, (XmlOptions)null);
        }
        
        public static CTTableCol parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCol)getTypeLoader().parse(inputStream, CTTableCol.type, xmlOptions);
        }
        
        public static CTTableCol parse(final Reader reader) throws XmlException, IOException {
            return (CTTableCol)getTypeLoader().parse(reader, CTTableCol.type, (XmlOptions)null);
        }
        
        public static CTTableCol parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableCol)getTypeLoader().parse(reader, CTTableCol.type, xmlOptions);
        }
        
        public static CTTableCol parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableCol)getTypeLoader().parse(xmlStreamReader, CTTableCol.type, (XmlOptions)null);
        }
        
        public static CTTableCol parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCol)getTypeLoader().parse(xmlStreamReader, CTTableCol.type, xmlOptions);
        }
        
        public static CTTableCol parse(final Node node) throws XmlException {
            return (CTTableCol)getTypeLoader().parse(node, CTTableCol.type, (XmlOptions)null);
        }
        
        public static CTTableCol parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableCol)getTypeLoader().parse(node, CTTableCol.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableCol parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableCol)getTypeLoader().parse(xmlInputStream, CTTableCol.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableCol parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableCol)getTypeLoader().parse(xmlInputStream, CTTableCol.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableCol.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableCol.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
