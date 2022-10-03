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

public interface CTTextSpacingPoint extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextSpacingPoint.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextspacingpoint6cf5type");
    
    int getVal();
    
    STTextSpacingPoint xgetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STTextSpacingPoint p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextSpacingPoint.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextSpacingPoint newInstance() {
            return (CTTextSpacingPoint)getTypeLoader().newInstance(CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPoint newInstance(final XmlOptions xmlOptions) {
            return (CTTextSpacingPoint)getTypeLoader().newInstance(CTTextSpacingPoint.type, xmlOptions);
        }
        
        public static CTTextSpacingPoint parse(final String s) throws XmlException {
            return (CTTextSpacingPoint)getTypeLoader().parse(s, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPoint parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacingPoint)getTypeLoader().parse(s, CTTextSpacingPoint.type, xmlOptions);
        }
        
        public static CTTextSpacingPoint parse(final File file) throws XmlException, IOException {
            return (CTTextSpacingPoint)getTypeLoader().parse(file, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPoint parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacingPoint)getTypeLoader().parse(file, CTTextSpacingPoint.type, xmlOptions);
        }
        
        public static CTTextSpacingPoint parse(final URL url) throws XmlException, IOException {
            return (CTTextSpacingPoint)getTypeLoader().parse(url, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPoint parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacingPoint)getTypeLoader().parse(url, CTTextSpacingPoint.type, xmlOptions);
        }
        
        public static CTTextSpacingPoint parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextSpacingPoint)getTypeLoader().parse(inputStream, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPoint parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacingPoint)getTypeLoader().parse(inputStream, CTTextSpacingPoint.type, xmlOptions);
        }
        
        public static CTTextSpacingPoint parse(final Reader reader) throws XmlException, IOException {
            return (CTTextSpacingPoint)getTypeLoader().parse(reader, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPoint parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacingPoint)getTypeLoader().parse(reader, CTTextSpacingPoint.type, xmlOptions);
        }
        
        public static CTTextSpacingPoint parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextSpacingPoint)getTypeLoader().parse(xmlStreamReader, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPoint parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacingPoint)getTypeLoader().parse(xmlStreamReader, CTTextSpacingPoint.type, xmlOptions);
        }
        
        public static CTTextSpacingPoint parse(final Node node) throws XmlException {
            return (CTTextSpacingPoint)getTypeLoader().parse(node, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPoint parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacingPoint)getTypeLoader().parse(node, CTTextSpacingPoint.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextSpacingPoint parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextSpacingPoint)getTypeLoader().parse(xmlInputStream, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextSpacingPoint parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextSpacingPoint)getTypeLoader().parse(xmlInputStream, CTTextSpacingPoint.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextSpacingPoint.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextSpacingPoint.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
