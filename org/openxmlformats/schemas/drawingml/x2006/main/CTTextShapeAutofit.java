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

public interface CTTextShapeAutofit extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextShapeAutofit.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextshapeautofita009type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextShapeAutofit.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextShapeAutofit newInstance() {
            return (CTTextShapeAutofit)getTypeLoader().newInstance(CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextShapeAutofit newInstance(final XmlOptions xmlOptions) {
            return (CTTextShapeAutofit)getTypeLoader().newInstance(CTTextShapeAutofit.type, xmlOptions);
        }
        
        public static CTTextShapeAutofit parse(final String s) throws XmlException {
            return (CTTextShapeAutofit)getTypeLoader().parse(s, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextShapeAutofit parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextShapeAutofit)getTypeLoader().parse(s, CTTextShapeAutofit.type, xmlOptions);
        }
        
        public static CTTextShapeAutofit parse(final File file) throws XmlException, IOException {
            return (CTTextShapeAutofit)getTypeLoader().parse(file, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextShapeAutofit parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextShapeAutofit)getTypeLoader().parse(file, CTTextShapeAutofit.type, xmlOptions);
        }
        
        public static CTTextShapeAutofit parse(final URL url) throws XmlException, IOException {
            return (CTTextShapeAutofit)getTypeLoader().parse(url, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextShapeAutofit parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextShapeAutofit)getTypeLoader().parse(url, CTTextShapeAutofit.type, xmlOptions);
        }
        
        public static CTTextShapeAutofit parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextShapeAutofit)getTypeLoader().parse(inputStream, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextShapeAutofit parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextShapeAutofit)getTypeLoader().parse(inputStream, CTTextShapeAutofit.type, xmlOptions);
        }
        
        public static CTTextShapeAutofit parse(final Reader reader) throws XmlException, IOException {
            return (CTTextShapeAutofit)getTypeLoader().parse(reader, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextShapeAutofit parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextShapeAutofit)getTypeLoader().parse(reader, CTTextShapeAutofit.type, xmlOptions);
        }
        
        public static CTTextShapeAutofit parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextShapeAutofit)getTypeLoader().parse(xmlStreamReader, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextShapeAutofit parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextShapeAutofit)getTypeLoader().parse(xmlStreamReader, CTTextShapeAutofit.type, xmlOptions);
        }
        
        public static CTTextShapeAutofit parse(final Node node) throws XmlException {
            return (CTTextShapeAutofit)getTypeLoader().parse(node, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        public static CTTextShapeAutofit parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextShapeAutofit)getTypeLoader().parse(node, CTTextShapeAutofit.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextShapeAutofit parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextShapeAutofit)getTypeLoader().parse(xmlInputStream, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextShapeAutofit parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextShapeAutofit)getTypeLoader().parse(xmlInputStream, CTTextShapeAutofit.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextShapeAutofit.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextShapeAutofit.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
