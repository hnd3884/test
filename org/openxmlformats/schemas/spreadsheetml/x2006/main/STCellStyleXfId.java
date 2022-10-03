package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface STCellStyleXfId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCellStyleXfId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcellstylexfid70c7type");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCellStyleXfId newValue(final Object o) {
            return (STCellStyleXfId)STCellStyleXfId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCellStyleXfId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCellStyleXfId newInstance() {
            return (STCellStyleXfId)getTypeLoader().newInstance(STCellStyleXfId.type, (XmlOptions)null);
        }
        
        public static STCellStyleXfId newInstance(final XmlOptions xmlOptions) {
            return (STCellStyleXfId)getTypeLoader().newInstance(STCellStyleXfId.type, xmlOptions);
        }
        
        public static STCellStyleXfId parse(final String s) throws XmlException {
            return (STCellStyleXfId)getTypeLoader().parse(s, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        public static STCellStyleXfId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCellStyleXfId)getTypeLoader().parse(s, STCellStyleXfId.type, xmlOptions);
        }
        
        public static STCellStyleXfId parse(final File file) throws XmlException, IOException {
            return (STCellStyleXfId)getTypeLoader().parse(file, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        public static STCellStyleXfId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellStyleXfId)getTypeLoader().parse(file, STCellStyleXfId.type, xmlOptions);
        }
        
        public static STCellStyleXfId parse(final URL url) throws XmlException, IOException {
            return (STCellStyleXfId)getTypeLoader().parse(url, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        public static STCellStyleXfId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellStyleXfId)getTypeLoader().parse(url, STCellStyleXfId.type, xmlOptions);
        }
        
        public static STCellStyleXfId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCellStyleXfId)getTypeLoader().parse(inputStream, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        public static STCellStyleXfId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellStyleXfId)getTypeLoader().parse(inputStream, STCellStyleXfId.type, xmlOptions);
        }
        
        public static STCellStyleXfId parse(final Reader reader) throws XmlException, IOException {
            return (STCellStyleXfId)getTypeLoader().parse(reader, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        public static STCellStyleXfId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellStyleXfId)getTypeLoader().parse(reader, STCellStyleXfId.type, xmlOptions);
        }
        
        public static STCellStyleXfId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCellStyleXfId)getTypeLoader().parse(xmlStreamReader, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        public static STCellStyleXfId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCellStyleXfId)getTypeLoader().parse(xmlStreamReader, STCellStyleXfId.type, xmlOptions);
        }
        
        public static STCellStyleXfId parse(final Node node) throws XmlException {
            return (STCellStyleXfId)getTypeLoader().parse(node, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        public static STCellStyleXfId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCellStyleXfId)getTypeLoader().parse(node, STCellStyleXfId.type, xmlOptions);
        }
        
        @Deprecated
        public static STCellStyleXfId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCellStyleXfId)getTypeLoader().parse(xmlInputStream, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCellStyleXfId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCellStyleXfId)getTypeLoader().parse(xmlInputStream, STCellStyleXfId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellStyleXfId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellStyleXfId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
