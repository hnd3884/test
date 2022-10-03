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
import org.apache.xmlbeans.XmlUnsignedInt;

public interface STDrawingElementId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDrawingElementId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdrawingelementid75a4type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDrawingElementId newValue(final Object o) {
            return (STDrawingElementId)STDrawingElementId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDrawingElementId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDrawingElementId newInstance() {
            return (STDrawingElementId)getTypeLoader().newInstance(STDrawingElementId.type, (XmlOptions)null);
        }
        
        public static STDrawingElementId newInstance(final XmlOptions xmlOptions) {
            return (STDrawingElementId)getTypeLoader().newInstance(STDrawingElementId.type, xmlOptions);
        }
        
        public static STDrawingElementId parse(final String s) throws XmlException {
            return (STDrawingElementId)getTypeLoader().parse(s, STDrawingElementId.type, (XmlOptions)null);
        }
        
        public static STDrawingElementId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDrawingElementId)getTypeLoader().parse(s, STDrawingElementId.type, xmlOptions);
        }
        
        public static STDrawingElementId parse(final File file) throws XmlException, IOException {
            return (STDrawingElementId)getTypeLoader().parse(file, STDrawingElementId.type, (XmlOptions)null);
        }
        
        public static STDrawingElementId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDrawingElementId)getTypeLoader().parse(file, STDrawingElementId.type, xmlOptions);
        }
        
        public static STDrawingElementId parse(final URL url) throws XmlException, IOException {
            return (STDrawingElementId)getTypeLoader().parse(url, STDrawingElementId.type, (XmlOptions)null);
        }
        
        public static STDrawingElementId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDrawingElementId)getTypeLoader().parse(url, STDrawingElementId.type, xmlOptions);
        }
        
        public static STDrawingElementId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDrawingElementId)getTypeLoader().parse(inputStream, STDrawingElementId.type, (XmlOptions)null);
        }
        
        public static STDrawingElementId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDrawingElementId)getTypeLoader().parse(inputStream, STDrawingElementId.type, xmlOptions);
        }
        
        public static STDrawingElementId parse(final Reader reader) throws XmlException, IOException {
            return (STDrawingElementId)getTypeLoader().parse(reader, STDrawingElementId.type, (XmlOptions)null);
        }
        
        public static STDrawingElementId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDrawingElementId)getTypeLoader().parse(reader, STDrawingElementId.type, xmlOptions);
        }
        
        public static STDrawingElementId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDrawingElementId)getTypeLoader().parse(xmlStreamReader, STDrawingElementId.type, (XmlOptions)null);
        }
        
        public static STDrawingElementId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDrawingElementId)getTypeLoader().parse(xmlStreamReader, STDrawingElementId.type, xmlOptions);
        }
        
        public static STDrawingElementId parse(final Node node) throws XmlException {
            return (STDrawingElementId)getTypeLoader().parse(node, STDrawingElementId.type, (XmlOptions)null);
        }
        
        public static STDrawingElementId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDrawingElementId)getTypeLoader().parse(node, STDrawingElementId.type, xmlOptions);
        }
        
        @Deprecated
        public static STDrawingElementId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDrawingElementId)getTypeLoader().parse(xmlInputStream, STDrawingElementId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDrawingElementId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDrawingElementId)getTypeLoader().parse(xmlInputStream, STDrawingElementId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDrawingElementId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDrawingElementId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
