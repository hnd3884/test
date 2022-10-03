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

public interface CTWorkbookProtection extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTWorkbookProtection.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctworkbookprotection56bctype");
    
    byte[] getWorkbookPassword();
    
    STUnsignedShortHex xgetWorkbookPassword();
    
    boolean isSetWorkbookPassword();
    
    void setWorkbookPassword(final byte[] p0);
    
    void xsetWorkbookPassword(final STUnsignedShortHex p0);
    
    void unsetWorkbookPassword();
    
    byte[] getRevisionsPassword();
    
    STUnsignedShortHex xgetRevisionsPassword();
    
    boolean isSetRevisionsPassword();
    
    void setRevisionsPassword(final byte[] p0);
    
    void xsetRevisionsPassword(final STUnsignedShortHex p0);
    
    void unsetRevisionsPassword();
    
    boolean getLockStructure();
    
    XmlBoolean xgetLockStructure();
    
    boolean isSetLockStructure();
    
    void setLockStructure(final boolean p0);
    
    void xsetLockStructure(final XmlBoolean p0);
    
    void unsetLockStructure();
    
    boolean getLockWindows();
    
    XmlBoolean xgetLockWindows();
    
    boolean isSetLockWindows();
    
    void setLockWindows(final boolean p0);
    
    void xsetLockWindows(final XmlBoolean p0);
    
    void unsetLockWindows();
    
    boolean getLockRevision();
    
    XmlBoolean xgetLockRevision();
    
    boolean isSetLockRevision();
    
    void setLockRevision(final boolean p0);
    
    void xsetLockRevision(final XmlBoolean p0);
    
    void unsetLockRevision();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTWorkbookProtection.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTWorkbookProtection newInstance() {
            return (CTWorkbookProtection)getTypeLoader().newInstance(CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        public static CTWorkbookProtection newInstance(final XmlOptions xmlOptions) {
            return (CTWorkbookProtection)getTypeLoader().newInstance(CTWorkbookProtection.type, xmlOptions);
        }
        
        public static CTWorkbookProtection parse(final String s) throws XmlException {
            return (CTWorkbookProtection)getTypeLoader().parse(s, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        public static CTWorkbookProtection parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbookProtection)getTypeLoader().parse(s, CTWorkbookProtection.type, xmlOptions);
        }
        
        public static CTWorkbookProtection parse(final File file) throws XmlException, IOException {
            return (CTWorkbookProtection)getTypeLoader().parse(file, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        public static CTWorkbookProtection parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbookProtection)getTypeLoader().parse(file, CTWorkbookProtection.type, xmlOptions);
        }
        
        public static CTWorkbookProtection parse(final URL url) throws XmlException, IOException {
            return (CTWorkbookProtection)getTypeLoader().parse(url, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        public static CTWorkbookProtection parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbookProtection)getTypeLoader().parse(url, CTWorkbookProtection.type, xmlOptions);
        }
        
        public static CTWorkbookProtection parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTWorkbookProtection)getTypeLoader().parse(inputStream, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        public static CTWorkbookProtection parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbookProtection)getTypeLoader().parse(inputStream, CTWorkbookProtection.type, xmlOptions);
        }
        
        public static CTWorkbookProtection parse(final Reader reader) throws XmlException, IOException {
            return (CTWorkbookProtection)getTypeLoader().parse(reader, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        public static CTWorkbookProtection parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTWorkbookProtection)getTypeLoader().parse(reader, CTWorkbookProtection.type, xmlOptions);
        }
        
        public static CTWorkbookProtection parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTWorkbookProtection)getTypeLoader().parse(xmlStreamReader, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        public static CTWorkbookProtection parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbookProtection)getTypeLoader().parse(xmlStreamReader, CTWorkbookProtection.type, xmlOptions);
        }
        
        public static CTWorkbookProtection parse(final Node node) throws XmlException {
            return (CTWorkbookProtection)getTypeLoader().parse(node, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        public static CTWorkbookProtection parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTWorkbookProtection)getTypeLoader().parse(node, CTWorkbookProtection.type, xmlOptions);
        }
        
        @Deprecated
        public static CTWorkbookProtection parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTWorkbookProtection)getTypeLoader().parse(xmlInputStream, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTWorkbookProtection parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTWorkbookProtection)getTypeLoader().parse(xmlInputStream, CTWorkbookProtection.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorkbookProtection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTWorkbookProtection.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
