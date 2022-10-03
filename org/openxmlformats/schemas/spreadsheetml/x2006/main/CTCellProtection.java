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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCellProtection extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCellProtection.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcellprotectionf524type");
    
    boolean getLocked();
    
    XmlBoolean xgetLocked();
    
    boolean isSetLocked();
    
    void setLocked(final boolean p0);
    
    void xsetLocked(final XmlBoolean p0);
    
    void unsetLocked();
    
    boolean getHidden();
    
    XmlBoolean xgetHidden();
    
    boolean isSetHidden();
    
    void setHidden(final boolean p0);
    
    void xsetHidden(final XmlBoolean p0);
    
    void unsetHidden();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCellProtection.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCellProtection newInstance() {
            return (CTCellProtection)getTypeLoader().newInstance(CTCellProtection.type, (XmlOptions)null);
        }
        
        public static CTCellProtection newInstance(final XmlOptions xmlOptions) {
            return (CTCellProtection)getTypeLoader().newInstance(CTCellProtection.type, xmlOptions);
        }
        
        public static CTCellProtection parse(final String s) throws XmlException {
            return (CTCellProtection)getTypeLoader().parse(s, CTCellProtection.type, (XmlOptions)null);
        }
        
        public static CTCellProtection parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellProtection)getTypeLoader().parse(s, CTCellProtection.type, xmlOptions);
        }
        
        public static CTCellProtection parse(final File file) throws XmlException, IOException {
            return (CTCellProtection)getTypeLoader().parse(file, CTCellProtection.type, (XmlOptions)null);
        }
        
        public static CTCellProtection parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellProtection)getTypeLoader().parse(file, CTCellProtection.type, xmlOptions);
        }
        
        public static CTCellProtection parse(final URL url) throws XmlException, IOException {
            return (CTCellProtection)getTypeLoader().parse(url, CTCellProtection.type, (XmlOptions)null);
        }
        
        public static CTCellProtection parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellProtection)getTypeLoader().parse(url, CTCellProtection.type, xmlOptions);
        }
        
        public static CTCellProtection parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCellProtection)getTypeLoader().parse(inputStream, CTCellProtection.type, (XmlOptions)null);
        }
        
        public static CTCellProtection parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellProtection)getTypeLoader().parse(inputStream, CTCellProtection.type, xmlOptions);
        }
        
        public static CTCellProtection parse(final Reader reader) throws XmlException, IOException {
            return (CTCellProtection)getTypeLoader().parse(reader, CTCellProtection.type, (XmlOptions)null);
        }
        
        public static CTCellProtection parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCellProtection)getTypeLoader().parse(reader, CTCellProtection.type, xmlOptions);
        }
        
        public static CTCellProtection parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCellProtection)getTypeLoader().parse(xmlStreamReader, CTCellProtection.type, (XmlOptions)null);
        }
        
        public static CTCellProtection parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellProtection)getTypeLoader().parse(xmlStreamReader, CTCellProtection.type, xmlOptions);
        }
        
        public static CTCellProtection parse(final Node node) throws XmlException {
            return (CTCellProtection)getTypeLoader().parse(node, CTCellProtection.type, (XmlOptions)null);
        }
        
        public static CTCellProtection parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCellProtection)getTypeLoader().parse(node, CTCellProtection.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCellProtection parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCellProtection)getTypeLoader().parse(xmlInputStream, CTCellProtection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCellProtection parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCellProtection)getTypeLoader().parse(xmlInputStream, CTCellProtection.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellProtection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCellProtection.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
