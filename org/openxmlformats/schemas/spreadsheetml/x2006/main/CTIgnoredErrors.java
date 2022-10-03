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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTIgnoredErrors extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTIgnoredErrors.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctignorederrorsbebctype");
    
    List<CTIgnoredError> getIgnoredErrorList();
    
    @Deprecated
    CTIgnoredError[] getIgnoredErrorArray();
    
    CTIgnoredError getIgnoredErrorArray(final int p0);
    
    int sizeOfIgnoredErrorArray();
    
    void setIgnoredErrorArray(final CTIgnoredError[] p0);
    
    void setIgnoredErrorArray(final int p0, final CTIgnoredError p1);
    
    CTIgnoredError insertNewIgnoredError(final int p0);
    
    CTIgnoredError addNewIgnoredError();
    
    void removeIgnoredError(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTIgnoredErrors.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTIgnoredErrors newInstance() {
            return (CTIgnoredErrors)getTypeLoader().newInstance(CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        public static CTIgnoredErrors newInstance(final XmlOptions xmlOptions) {
            return (CTIgnoredErrors)getTypeLoader().newInstance(CTIgnoredErrors.type, xmlOptions);
        }
        
        public static CTIgnoredErrors parse(final String s) throws XmlException {
            return (CTIgnoredErrors)getTypeLoader().parse(s, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        public static CTIgnoredErrors parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTIgnoredErrors)getTypeLoader().parse(s, CTIgnoredErrors.type, xmlOptions);
        }
        
        public static CTIgnoredErrors parse(final File file) throws XmlException, IOException {
            return (CTIgnoredErrors)getTypeLoader().parse(file, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        public static CTIgnoredErrors parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIgnoredErrors)getTypeLoader().parse(file, CTIgnoredErrors.type, xmlOptions);
        }
        
        public static CTIgnoredErrors parse(final URL url) throws XmlException, IOException {
            return (CTIgnoredErrors)getTypeLoader().parse(url, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        public static CTIgnoredErrors parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIgnoredErrors)getTypeLoader().parse(url, CTIgnoredErrors.type, xmlOptions);
        }
        
        public static CTIgnoredErrors parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTIgnoredErrors)getTypeLoader().parse(inputStream, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        public static CTIgnoredErrors parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIgnoredErrors)getTypeLoader().parse(inputStream, CTIgnoredErrors.type, xmlOptions);
        }
        
        public static CTIgnoredErrors parse(final Reader reader) throws XmlException, IOException {
            return (CTIgnoredErrors)getTypeLoader().parse(reader, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        public static CTIgnoredErrors parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIgnoredErrors)getTypeLoader().parse(reader, CTIgnoredErrors.type, xmlOptions);
        }
        
        public static CTIgnoredErrors parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTIgnoredErrors)getTypeLoader().parse(xmlStreamReader, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        public static CTIgnoredErrors parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTIgnoredErrors)getTypeLoader().parse(xmlStreamReader, CTIgnoredErrors.type, xmlOptions);
        }
        
        public static CTIgnoredErrors parse(final Node node) throws XmlException {
            return (CTIgnoredErrors)getTypeLoader().parse(node, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        public static CTIgnoredErrors parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTIgnoredErrors)getTypeLoader().parse(node, CTIgnoredErrors.type, xmlOptions);
        }
        
        @Deprecated
        public static CTIgnoredErrors parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTIgnoredErrors)getTypeLoader().parse(xmlInputStream, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTIgnoredErrors parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTIgnoredErrors)getTypeLoader().parse(xmlInputStream, CTIgnoredErrors.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIgnoredErrors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIgnoredErrors.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
