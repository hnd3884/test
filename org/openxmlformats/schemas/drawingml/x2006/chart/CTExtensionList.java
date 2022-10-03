package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface CTExtensionList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTExtensionList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctextensionlist7389type");
    
    List<CTExtension> getExtList();
    
    @Deprecated
    CTExtension[] getExtArray();
    
    CTExtension getExtArray(final int p0);
    
    int sizeOfExtArray();
    
    void setExtArray(final CTExtension[] p0);
    
    void setExtArray(final int p0, final CTExtension p1);
    
    CTExtension insertNewExt(final int p0);
    
    CTExtension addNewExt();
    
    void removeExt(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTExtensionList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTExtensionList newInstance() {
            return (CTExtensionList)getTypeLoader().newInstance(CTExtensionList.type, (XmlOptions)null);
        }
        
        public static CTExtensionList newInstance(final XmlOptions xmlOptions) {
            return (CTExtensionList)getTypeLoader().newInstance(CTExtensionList.type, xmlOptions);
        }
        
        public static CTExtensionList parse(final String s) throws XmlException {
            return (CTExtensionList)getTypeLoader().parse(s, CTExtensionList.type, (XmlOptions)null);
        }
        
        public static CTExtensionList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTExtensionList)getTypeLoader().parse(s, CTExtensionList.type, xmlOptions);
        }
        
        public static CTExtensionList parse(final File file) throws XmlException, IOException {
            return (CTExtensionList)getTypeLoader().parse(file, CTExtensionList.type, (XmlOptions)null);
        }
        
        public static CTExtensionList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExtensionList)getTypeLoader().parse(file, CTExtensionList.type, xmlOptions);
        }
        
        public static CTExtensionList parse(final URL url) throws XmlException, IOException {
            return (CTExtensionList)getTypeLoader().parse(url, CTExtensionList.type, (XmlOptions)null);
        }
        
        public static CTExtensionList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExtensionList)getTypeLoader().parse(url, CTExtensionList.type, xmlOptions);
        }
        
        public static CTExtensionList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTExtensionList)getTypeLoader().parse(inputStream, CTExtensionList.type, (XmlOptions)null);
        }
        
        public static CTExtensionList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExtensionList)getTypeLoader().parse(inputStream, CTExtensionList.type, xmlOptions);
        }
        
        public static CTExtensionList parse(final Reader reader) throws XmlException, IOException {
            return (CTExtensionList)getTypeLoader().parse(reader, CTExtensionList.type, (XmlOptions)null);
        }
        
        public static CTExtensionList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTExtensionList)getTypeLoader().parse(reader, CTExtensionList.type, xmlOptions);
        }
        
        public static CTExtensionList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTExtensionList)getTypeLoader().parse(xmlStreamReader, CTExtensionList.type, (XmlOptions)null);
        }
        
        public static CTExtensionList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTExtensionList)getTypeLoader().parse(xmlStreamReader, CTExtensionList.type, xmlOptions);
        }
        
        public static CTExtensionList parse(final Node node) throws XmlException {
            return (CTExtensionList)getTypeLoader().parse(node, CTExtensionList.type, (XmlOptions)null);
        }
        
        public static CTExtensionList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTExtensionList)getTypeLoader().parse(node, CTExtensionList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTExtensionList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTExtensionList)getTypeLoader().parse(xmlInputStream, CTExtensionList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTExtensionList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTExtensionList)getTypeLoader().parse(xmlInputStream, CTExtensionList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExtensionList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTExtensionList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
