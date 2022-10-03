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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTRegularTextRun extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRegularTextRun.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctregulartextrun7e3dtype");
    
    CTTextCharacterProperties getRPr();
    
    boolean isSetRPr();
    
    void setRPr(final CTTextCharacterProperties p0);
    
    CTTextCharacterProperties addNewRPr();
    
    void unsetRPr();
    
    String getT();
    
    XmlString xgetT();
    
    void setT(final String p0);
    
    void xsetT(final XmlString p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRegularTextRun.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRegularTextRun newInstance() {
            return (CTRegularTextRun)getTypeLoader().newInstance(CTRegularTextRun.type, (XmlOptions)null);
        }
        
        public static CTRegularTextRun newInstance(final XmlOptions xmlOptions) {
            return (CTRegularTextRun)getTypeLoader().newInstance(CTRegularTextRun.type, xmlOptions);
        }
        
        public static CTRegularTextRun parse(final String s) throws XmlException {
            return (CTRegularTextRun)getTypeLoader().parse(s, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        public static CTRegularTextRun parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRegularTextRun)getTypeLoader().parse(s, CTRegularTextRun.type, xmlOptions);
        }
        
        public static CTRegularTextRun parse(final File file) throws XmlException, IOException {
            return (CTRegularTextRun)getTypeLoader().parse(file, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        public static CTRegularTextRun parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRegularTextRun)getTypeLoader().parse(file, CTRegularTextRun.type, xmlOptions);
        }
        
        public static CTRegularTextRun parse(final URL url) throws XmlException, IOException {
            return (CTRegularTextRun)getTypeLoader().parse(url, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        public static CTRegularTextRun parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRegularTextRun)getTypeLoader().parse(url, CTRegularTextRun.type, xmlOptions);
        }
        
        public static CTRegularTextRun parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRegularTextRun)getTypeLoader().parse(inputStream, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        public static CTRegularTextRun parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRegularTextRun)getTypeLoader().parse(inputStream, CTRegularTextRun.type, xmlOptions);
        }
        
        public static CTRegularTextRun parse(final Reader reader) throws XmlException, IOException {
            return (CTRegularTextRun)getTypeLoader().parse(reader, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        public static CTRegularTextRun parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRegularTextRun)getTypeLoader().parse(reader, CTRegularTextRun.type, xmlOptions);
        }
        
        public static CTRegularTextRun parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRegularTextRun)getTypeLoader().parse(xmlStreamReader, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        public static CTRegularTextRun parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRegularTextRun)getTypeLoader().parse(xmlStreamReader, CTRegularTextRun.type, xmlOptions);
        }
        
        public static CTRegularTextRun parse(final Node node) throws XmlException {
            return (CTRegularTextRun)getTypeLoader().parse(node, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        public static CTRegularTextRun parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRegularTextRun)getTypeLoader().parse(node, CTRegularTextRun.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRegularTextRun parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRegularTextRun)getTypeLoader().parse(xmlInputStream, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRegularTextRun parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRegularTextRun)getTypeLoader().parse(xmlInputStream, CTRegularTextRun.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRegularTextRun.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRegularTextRun.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
