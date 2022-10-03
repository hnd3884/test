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

public interface CTLineStyleList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLineStyleList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlinestylelist510ctype");
    
    List<CTLineProperties> getLnList();
    
    @Deprecated
    CTLineProperties[] getLnArray();
    
    CTLineProperties getLnArray(final int p0);
    
    int sizeOfLnArray();
    
    void setLnArray(final CTLineProperties[] p0);
    
    void setLnArray(final int p0, final CTLineProperties p1);
    
    CTLineProperties insertNewLn(final int p0);
    
    CTLineProperties addNewLn();
    
    void removeLn(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLineStyleList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLineStyleList newInstance() {
            return (CTLineStyleList)getTypeLoader().newInstance(CTLineStyleList.type, (XmlOptions)null);
        }
        
        public static CTLineStyleList newInstance(final XmlOptions xmlOptions) {
            return (CTLineStyleList)getTypeLoader().newInstance(CTLineStyleList.type, xmlOptions);
        }
        
        public static CTLineStyleList parse(final String s) throws XmlException {
            return (CTLineStyleList)getTypeLoader().parse(s, CTLineStyleList.type, (XmlOptions)null);
        }
        
        public static CTLineStyleList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineStyleList)getTypeLoader().parse(s, CTLineStyleList.type, xmlOptions);
        }
        
        public static CTLineStyleList parse(final File file) throws XmlException, IOException {
            return (CTLineStyleList)getTypeLoader().parse(file, CTLineStyleList.type, (XmlOptions)null);
        }
        
        public static CTLineStyleList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineStyleList)getTypeLoader().parse(file, CTLineStyleList.type, xmlOptions);
        }
        
        public static CTLineStyleList parse(final URL url) throws XmlException, IOException {
            return (CTLineStyleList)getTypeLoader().parse(url, CTLineStyleList.type, (XmlOptions)null);
        }
        
        public static CTLineStyleList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineStyleList)getTypeLoader().parse(url, CTLineStyleList.type, xmlOptions);
        }
        
        public static CTLineStyleList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLineStyleList)getTypeLoader().parse(inputStream, CTLineStyleList.type, (XmlOptions)null);
        }
        
        public static CTLineStyleList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineStyleList)getTypeLoader().parse(inputStream, CTLineStyleList.type, xmlOptions);
        }
        
        public static CTLineStyleList parse(final Reader reader) throws XmlException, IOException {
            return (CTLineStyleList)getTypeLoader().parse(reader, CTLineStyleList.type, (XmlOptions)null);
        }
        
        public static CTLineStyleList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineStyleList)getTypeLoader().parse(reader, CTLineStyleList.type, xmlOptions);
        }
        
        public static CTLineStyleList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLineStyleList)getTypeLoader().parse(xmlStreamReader, CTLineStyleList.type, (XmlOptions)null);
        }
        
        public static CTLineStyleList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineStyleList)getTypeLoader().parse(xmlStreamReader, CTLineStyleList.type, xmlOptions);
        }
        
        public static CTLineStyleList parse(final Node node) throws XmlException {
            return (CTLineStyleList)getTypeLoader().parse(node, CTLineStyleList.type, (XmlOptions)null);
        }
        
        public static CTLineStyleList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineStyleList)getTypeLoader().parse(node, CTLineStyleList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLineStyleList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLineStyleList)getTypeLoader().parse(xmlInputStream, CTLineStyleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLineStyleList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLineStyleList)getTypeLoader().parse(xmlInputStream, CTLineStyleList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineStyleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineStyleList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
