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

public interface CTEffectStyleList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEffectStyleList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cteffectstylelistc50ftype");
    
    List<CTEffectStyleItem> getEffectStyleList();
    
    @Deprecated
    CTEffectStyleItem[] getEffectStyleArray();
    
    CTEffectStyleItem getEffectStyleArray(final int p0);
    
    int sizeOfEffectStyleArray();
    
    void setEffectStyleArray(final CTEffectStyleItem[] p0);
    
    void setEffectStyleArray(final int p0, final CTEffectStyleItem p1);
    
    CTEffectStyleItem insertNewEffectStyle(final int p0);
    
    CTEffectStyleItem addNewEffectStyle();
    
    void removeEffectStyle(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEffectStyleList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEffectStyleList newInstance() {
            return (CTEffectStyleList)getTypeLoader().newInstance(CTEffectStyleList.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleList newInstance(final XmlOptions xmlOptions) {
            return (CTEffectStyleList)getTypeLoader().newInstance(CTEffectStyleList.type, xmlOptions);
        }
        
        public static CTEffectStyleList parse(final String s) throws XmlException {
            return (CTEffectStyleList)getTypeLoader().parse(s, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectStyleList)getTypeLoader().parse(s, CTEffectStyleList.type, xmlOptions);
        }
        
        public static CTEffectStyleList parse(final File file) throws XmlException, IOException {
            return (CTEffectStyleList)getTypeLoader().parse(file, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectStyleList)getTypeLoader().parse(file, CTEffectStyleList.type, xmlOptions);
        }
        
        public static CTEffectStyleList parse(final URL url) throws XmlException, IOException {
            return (CTEffectStyleList)getTypeLoader().parse(url, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectStyleList)getTypeLoader().parse(url, CTEffectStyleList.type, xmlOptions);
        }
        
        public static CTEffectStyleList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEffectStyleList)getTypeLoader().parse(inputStream, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectStyleList)getTypeLoader().parse(inputStream, CTEffectStyleList.type, xmlOptions);
        }
        
        public static CTEffectStyleList parse(final Reader reader) throws XmlException, IOException {
            return (CTEffectStyleList)getTypeLoader().parse(reader, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectStyleList)getTypeLoader().parse(reader, CTEffectStyleList.type, xmlOptions);
        }
        
        public static CTEffectStyleList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEffectStyleList)getTypeLoader().parse(xmlStreamReader, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectStyleList)getTypeLoader().parse(xmlStreamReader, CTEffectStyleList.type, xmlOptions);
        }
        
        public static CTEffectStyleList parse(final Node node) throws XmlException {
            return (CTEffectStyleList)getTypeLoader().parse(node, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        public static CTEffectStyleList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectStyleList)getTypeLoader().parse(node, CTEffectStyleList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEffectStyleList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEffectStyleList)getTypeLoader().parse(xmlInputStream, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEffectStyleList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEffectStyleList)getTypeLoader().parse(xmlInputStream, CTEffectStyleList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEffectStyleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEffectStyleList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
