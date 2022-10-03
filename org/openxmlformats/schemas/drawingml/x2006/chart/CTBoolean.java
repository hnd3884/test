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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBoolean extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBoolean.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbooleancc3etype");
    
    boolean getVal();
    
    XmlBoolean xgetVal();
    
    boolean isSetVal();
    
    void setVal(final boolean p0);
    
    void xsetVal(final XmlBoolean p0);
    
    void unsetVal();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBoolean.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBoolean newInstance() {
            return (CTBoolean)getTypeLoader().newInstance(CTBoolean.type, (XmlOptions)null);
        }
        
        public static CTBoolean newInstance(final XmlOptions xmlOptions) {
            return (CTBoolean)getTypeLoader().newInstance(CTBoolean.type, xmlOptions);
        }
        
        public static CTBoolean parse(final String s) throws XmlException {
            return (CTBoolean)getTypeLoader().parse(s, CTBoolean.type, (XmlOptions)null);
        }
        
        public static CTBoolean parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBoolean)getTypeLoader().parse(s, CTBoolean.type, xmlOptions);
        }
        
        public static CTBoolean parse(final File file) throws XmlException, IOException {
            return (CTBoolean)getTypeLoader().parse(file, CTBoolean.type, (XmlOptions)null);
        }
        
        public static CTBoolean parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBoolean)getTypeLoader().parse(file, CTBoolean.type, xmlOptions);
        }
        
        public static CTBoolean parse(final URL url) throws XmlException, IOException {
            return (CTBoolean)getTypeLoader().parse(url, CTBoolean.type, (XmlOptions)null);
        }
        
        public static CTBoolean parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBoolean)getTypeLoader().parse(url, CTBoolean.type, xmlOptions);
        }
        
        public static CTBoolean parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBoolean)getTypeLoader().parse(inputStream, CTBoolean.type, (XmlOptions)null);
        }
        
        public static CTBoolean parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBoolean)getTypeLoader().parse(inputStream, CTBoolean.type, xmlOptions);
        }
        
        public static CTBoolean parse(final Reader reader) throws XmlException, IOException {
            return (CTBoolean)getTypeLoader().parse(reader, CTBoolean.type, (XmlOptions)null);
        }
        
        public static CTBoolean parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBoolean)getTypeLoader().parse(reader, CTBoolean.type, xmlOptions);
        }
        
        public static CTBoolean parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBoolean)getTypeLoader().parse(xmlStreamReader, CTBoolean.type, (XmlOptions)null);
        }
        
        public static CTBoolean parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBoolean)getTypeLoader().parse(xmlStreamReader, CTBoolean.type, xmlOptions);
        }
        
        public static CTBoolean parse(final Node node) throws XmlException {
            return (CTBoolean)getTypeLoader().parse(node, CTBoolean.type, (XmlOptions)null);
        }
        
        public static CTBoolean parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBoolean)getTypeLoader().parse(node, CTBoolean.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBoolean parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBoolean)getTypeLoader().parse(xmlInputStream, CTBoolean.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBoolean parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBoolean)getTypeLoader().parse(xmlInputStream, CTBoolean.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBoolean.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBoolean.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
