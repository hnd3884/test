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

public interface CTOfficeArtExtensionList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOfficeArtExtensionList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctofficeartextensionlista211type");
    
    List<CTOfficeArtExtension> getExtList();
    
    @Deprecated
    CTOfficeArtExtension[] getExtArray();
    
    CTOfficeArtExtension getExtArray(final int p0);
    
    int sizeOfExtArray();
    
    void setExtArray(final CTOfficeArtExtension[] p0);
    
    void setExtArray(final int p0, final CTOfficeArtExtension p1);
    
    CTOfficeArtExtension insertNewExt(final int p0);
    
    CTOfficeArtExtension addNewExt();
    
    void removeExt(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOfficeArtExtensionList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOfficeArtExtensionList newInstance() {
            return (CTOfficeArtExtensionList)getTypeLoader().newInstance(CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        public static CTOfficeArtExtensionList newInstance(final XmlOptions xmlOptions) {
            return (CTOfficeArtExtensionList)getTypeLoader().newInstance(CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        public static CTOfficeArtExtensionList parse(final String s) throws XmlException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(s, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        public static CTOfficeArtExtensionList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(s, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        public static CTOfficeArtExtensionList parse(final File file) throws XmlException, IOException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(file, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        public static CTOfficeArtExtensionList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(file, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        public static CTOfficeArtExtensionList parse(final URL url) throws XmlException, IOException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(url, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        public static CTOfficeArtExtensionList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(url, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        public static CTOfficeArtExtensionList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(inputStream, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        public static CTOfficeArtExtensionList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(inputStream, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        public static CTOfficeArtExtensionList parse(final Reader reader) throws XmlException, IOException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(reader, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        public static CTOfficeArtExtensionList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(reader, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        public static CTOfficeArtExtensionList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(xmlStreamReader, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        public static CTOfficeArtExtensionList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(xmlStreamReader, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        public static CTOfficeArtExtensionList parse(final Node node) throws XmlException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(node, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        public static CTOfficeArtExtensionList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(node, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOfficeArtExtensionList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(xmlInputStream, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOfficeArtExtensionList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOfficeArtExtensionList)getTypeLoader().parse(xmlInputStream, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOfficeArtExtensionList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOfficeArtExtensionList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
