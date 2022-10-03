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

public interface CTTextSpacingPercent extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextSpacingPercent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextspacingpercent322atype");
    
    int getVal();
    
    STTextSpacingPercent xgetVal();
    
    void setVal(final int p0);
    
    void xsetVal(final STTextSpacingPercent p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextSpacingPercent.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextSpacingPercent newInstance() {
            return (CTTextSpacingPercent)getTypeLoader().newInstance(CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPercent newInstance(final XmlOptions xmlOptions) {
            return (CTTextSpacingPercent)getTypeLoader().newInstance(CTTextSpacingPercent.type, xmlOptions);
        }
        
        public static CTTextSpacingPercent parse(final String s) throws XmlException {
            return (CTTextSpacingPercent)getTypeLoader().parse(s, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPercent parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacingPercent)getTypeLoader().parse(s, CTTextSpacingPercent.type, xmlOptions);
        }
        
        public static CTTextSpacingPercent parse(final File file) throws XmlException, IOException {
            return (CTTextSpacingPercent)getTypeLoader().parse(file, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPercent parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacingPercent)getTypeLoader().parse(file, CTTextSpacingPercent.type, xmlOptions);
        }
        
        public static CTTextSpacingPercent parse(final URL url) throws XmlException, IOException {
            return (CTTextSpacingPercent)getTypeLoader().parse(url, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPercent parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacingPercent)getTypeLoader().parse(url, CTTextSpacingPercent.type, xmlOptions);
        }
        
        public static CTTextSpacingPercent parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextSpacingPercent)getTypeLoader().parse(inputStream, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPercent parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacingPercent)getTypeLoader().parse(inputStream, CTTextSpacingPercent.type, xmlOptions);
        }
        
        public static CTTextSpacingPercent parse(final Reader reader) throws XmlException, IOException {
            return (CTTextSpacingPercent)getTypeLoader().parse(reader, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPercent parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextSpacingPercent)getTypeLoader().parse(reader, CTTextSpacingPercent.type, xmlOptions);
        }
        
        public static CTTextSpacingPercent parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextSpacingPercent)getTypeLoader().parse(xmlStreamReader, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPercent parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacingPercent)getTypeLoader().parse(xmlStreamReader, CTTextSpacingPercent.type, xmlOptions);
        }
        
        public static CTTextSpacingPercent parse(final Node node) throws XmlException {
            return (CTTextSpacingPercent)getTypeLoader().parse(node, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        public static CTTextSpacingPercent parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextSpacingPercent)getTypeLoader().parse(node, CTTextSpacingPercent.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextSpacingPercent parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextSpacingPercent)getTypeLoader().parse(xmlInputStream, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextSpacingPercent parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextSpacingPercent)getTypeLoader().parse(xmlInputStream, CTTextSpacingPercent.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextSpacingPercent.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextSpacingPercent.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
