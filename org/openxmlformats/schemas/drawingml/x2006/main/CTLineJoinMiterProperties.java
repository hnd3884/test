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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLineJoinMiterProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLineJoinMiterProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlinejoinmiterproperties02abtype");
    
    int getLim();
    
    STPositivePercentage xgetLim();
    
    boolean isSetLim();
    
    void setLim(final int p0);
    
    void xsetLim(final STPositivePercentage p0);
    
    void unsetLim();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLineJoinMiterProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLineJoinMiterProperties newInstance() {
            return (CTLineJoinMiterProperties)getTypeLoader().newInstance(CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        public static CTLineJoinMiterProperties newInstance(final XmlOptions xmlOptions) {
            return (CTLineJoinMiterProperties)getTypeLoader().newInstance(CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        public static CTLineJoinMiterProperties parse(final String s) throws XmlException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(s, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        public static CTLineJoinMiterProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(s, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        public static CTLineJoinMiterProperties parse(final File file) throws XmlException, IOException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(file, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        public static CTLineJoinMiterProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(file, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        public static CTLineJoinMiterProperties parse(final URL url) throws XmlException, IOException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(url, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        public static CTLineJoinMiterProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(url, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        public static CTLineJoinMiterProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(inputStream, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        public static CTLineJoinMiterProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(inputStream, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        public static CTLineJoinMiterProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(reader, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        public static CTLineJoinMiterProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(reader, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        public static CTLineJoinMiterProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(xmlStreamReader, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        public static CTLineJoinMiterProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(xmlStreamReader, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        public static CTLineJoinMiterProperties parse(final Node node) throws XmlException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(node, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        public static CTLineJoinMiterProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(node, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLineJoinMiterProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(xmlInputStream, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLineJoinMiterProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLineJoinMiterProperties)getTypeLoader().parse(xmlInputStream, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineJoinMiterProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLineJoinMiterProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
