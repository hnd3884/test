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

public interface CTPositiveFixedPercentage extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPositiveFixedPercentage.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpositivefixedpercentage8966type");
    
    int getVal();
    
    STPositiveFixedPercentage xgetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STPositiveFixedPercentage p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPositiveFixedPercentage.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPositiveFixedPercentage newInstance() {
            return (CTPositiveFixedPercentage)getTypeLoader().newInstance(CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTPositiveFixedPercentage newInstance(final XmlOptions xmlOptions) {
            return (CTPositiveFixedPercentage)getTypeLoader().newInstance(CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static CTPositiveFixedPercentage parse(final String s) throws XmlException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(s, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTPositiveFixedPercentage parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(s, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static CTPositiveFixedPercentage parse(final File file) throws XmlException, IOException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(file, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTPositiveFixedPercentage parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(file, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static CTPositiveFixedPercentage parse(final URL url) throws XmlException, IOException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(url, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTPositiveFixedPercentage parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(url, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static CTPositiveFixedPercentage parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(inputStream, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTPositiveFixedPercentage parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(inputStream, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static CTPositiveFixedPercentage parse(final Reader reader) throws XmlException, IOException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(reader, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTPositiveFixedPercentage parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(reader, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static CTPositiveFixedPercentage parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(xmlStreamReader, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTPositiveFixedPercentage parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(xmlStreamReader, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        public static CTPositiveFixedPercentage parse(final Node node) throws XmlException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(node, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        public static CTPositiveFixedPercentage parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(node, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPositiveFixedPercentage parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(xmlInputStream, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPositiveFixedPercentage parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPositiveFixedPercentage)getTypeLoader().parse(xmlInputStream, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPositiveFixedPercentage.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPositiveFixedPercentage.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
