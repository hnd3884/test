package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface CTSmartTagPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSmartTagPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsmarttagprf715type");
    
    List<CTAttr> getAttrList();
    
    @Deprecated
    CTAttr[] getAttrArray();
    
    CTAttr getAttrArray(final int p0);
    
    int sizeOfAttrArray();
    
    void setAttrArray(final CTAttr[] p0);
    
    void setAttrArray(final int p0, final CTAttr p1);
    
    CTAttr insertNewAttr(final int p0);
    
    CTAttr addNewAttr();
    
    void removeAttr(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSmartTagPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSmartTagPr newInstance() {
            return (CTSmartTagPr)getTypeLoader().newInstance(CTSmartTagPr.type, (XmlOptions)null);
        }
        
        public static CTSmartTagPr newInstance(final XmlOptions xmlOptions) {
            return (CTSmartTagPr)getTypeLoader().newInstance(CTSmartTagPr.type, xmlOptions);
        }
        
        public static CTSmartTagPr parse(final String s) throws XmlException {
            return (CTSmartTagPr)getTypeLoader().parse(s, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        public static CTSmartTagPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSmartTagPr)getTypeLoader().parse(s, CTSmartTagPr.type, xmlOptions);
        }
        
        public static CTSmartTagPr parse(final File file) throws XmlException, IOException {
            return (CTSmartTagPr)getTypeLoader().parse(file, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        public static CTSmartTagPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSmartTagPr)getTypeLoader().parse(file, CTSmartTagPr.type, xmlOptions);
        }
        
        public static CTSmartTagPr parse(final URL url) throws XmlException, IOException {
            return (CTSmartTagPr)getTypeLoader().parse(url, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        public static CTSmartTagPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSmartTagPr)getTypeLoader().parse(url, CTSmartTagPr.type, xmlOptions);
        }
        
        public static CTSmartTagPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSmartTagPr)getTypeLoader().parse(inputStream, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        public static CTSmartTagPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSmartTagPr)getTypeLoader().parse(inputStream, CTSmartTagPr.type, xmlOptions);
        }
        
        public static CTSmartTagPr parse(final Reader reader) throws XmlException, IOException {
            return (CTSmartTagPr)getTypeLoader().parse(reader, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        public static CTSmartTagPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSmartTagPr)getTypeLoader().parse(reader, CTSmartTagPr.type, xmlOptions);
        }
        
        public static CTSmartTagPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSmartTagPr)getTypeLoader().parse(xmlStreamReader, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        public static CTSmartTagPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSmartTagPr)getTypeLoader().parse(xmlStreamReader, CTSmartTagPr.type, xmlOptions);
        }
        
        public static CTSmartTagPr parse(final Node node) throws XmlException {
            return (CTSmartTagPr)getTypeLoader().parse(node, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        public static CTSmartTagPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSmartTagPr)getTypeLoader().parse(node, CTSmartTagPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSmartTagPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSmartTagPr)getTypeLoader().parse(xmlInputStream, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSmartTagPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSmartTagPr)getTypeLoader().parse(xmlInputStream, CTSmartTagPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSmartTagPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSmartTagPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
