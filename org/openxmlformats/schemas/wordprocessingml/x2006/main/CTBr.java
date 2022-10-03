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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbr7dd8type");
    
    STBrType.Enum getType();
    
    STBrType xgetType();
    
    boolean isSetType();
    
    void setType(final STBrType.Enum p0);
    
    void xsetType(final STBrType p0);
    
    void unsetType();
    
    STBrClear.Enum getClear();
    
    STBrClear xgetClear();
    
    boolean isSetClear();
    
    void setClear(final STBrClear.Enum p0);
    
    void xsetClear(final STBrClear p0);
    
    void unsetClear();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBr newInstance() {
            return (CTBr)getTypeLoader().newInstance(CTBr.type, (XmlOptions)null);
        }
        
        public static CTBr newInstance(final XmlOptions xmlOptions) {
            return (CTBr)getTypeLoader().newInstance(CTBr.type, xmlOptions);
        }
        
        public static CTBr parse(final String s) throws XmlException {
            return (CTBr)getTypeLoader().parse(s, CTBr.type, (XmlOptions)null);
        }
        
        public static CTBr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBr)getTypeLoader().parse(s, CTBr.type, xmlOptions);
        }
        
        public static CTBr parse(final File file) throws XmlException, IOException {
            return (CTBr)getTypeLoader().parse(file, CTBr.type, (XmlOptions)null);
        }
        
        public static CTBr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBr)getTypeLoader().parse(file, CTBr.type, xmlOptions);
        }
        
        public static CTBr parse(final URL url) throws XmlException, IOException {
            return (CTBr)getTypeLoader().parse(url, CTBr.type, (XmlOptions)null);
        }
        
        public static CTBr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBr)getTypeLoader().parse(url, CTBr.type, xmlOptions);
        }
        
        public static CTBr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBr)getTypeLoader().parse(inputStream, CTBr.type, (XmlOptions)null);
        }
        
        public static CTBr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBr)getTypeLoader().parse(inputStream, CTBr.type, xmlOptions);
        }
        
        public static CTBr parse(final Reader reader) throws XmlException, IOException {
            return (CTBr)getTypeLoader().parse(reader, CTBr.type, (XmlOptions)null);
        }
        
        public static CTBr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBr)getTypeLoader().parse(reader, CTBr.type, xmlOptions);
        }
        
        public static CTBr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBr)getTypeLoader().parse(xmlStreamReader, CTBr.type, (XmlOptions)null);
        }
        
        public static CTBr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBr)getTypeLoader().parse(xmlStreamReader, CTBr.type, xmlOptions);
        }
        
        public static CTBr parse(final Node node) throws XmlException {
            return (CTBr)getTypeLoader().parse(node, CTBr.type, (XmlOptions)null);
        }
        
        public static CTBr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBr)getTypeLoader().parse(node, CTBr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBr)getTypeLoader().parse(xmlInputStream, CTBr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBr)getTypeLoader().parse(xmlInputStream, CTBr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
