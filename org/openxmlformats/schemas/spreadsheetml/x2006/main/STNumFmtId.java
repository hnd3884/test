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

public interface STNumFmtId extends XmlUnsignedInt
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STNumFmtId.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stnumfmtid76fbtype");
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STNumFmtId newValue(final Object o) {
            return (STNumFmtId)STNumFmtId.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STNumFmtId.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STNumFmtId newInstance() {
            return (STNumFmtId)getTypeLoader().newInstance(STNumFmtId.type, (XmlOptions)null);
        }
        
        public static STNumFmtId newInstance(final XmlOptions xmlOptions) {
            return (STNumFmtId)getTypeLoader().newInstance(STNumFmtId.type, xmlOptions);
        }
        
        public static STNumFmtId parse(final String s) throws XmlException {
            return (STNumFmtId)getTypeLoader().parse(s, STNumFmtId.type, (XmlOptions)null);
        }
        
        public static STNumFmtId parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STNumFmtId)getTypeLoader().parse(s, STNumFmtId.type, xmlOptions);
        }
        
        public static STNumFmtId parse(final File file) throws XmlException, IOException {
            return (STNumFmtId)getTypeLoader().parse(file, STNumFmtId.type, (XmlOptions)null);
        }
        
        public static STNumFmtId parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STNumFmtId)getTypeLoader().parse(file, STNumFmtId.type, xmlOptions);
        }
        
        public static STNumFmtId parse(final URL url) throws XmlException, IOException {
            return (STNumFmtId)getTypeLoader().parse(url, STNumFmtId.type, (XmlOptions)null);
        }
        
        public static STNumFmtId parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STNumFmtId)getTypeLoader().parse(url, STNumFmtId.type, xmlOptions);
        }
        
        public static STNumFmtId parse(final InputStream inputStream) throws XmlException, IOException {
            return (STNumFmtId)getTypeLoader().parse(inputStream, STNumFmtId.type, (XmlOptions)null);
        }
        
        public static STNumFmtId parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STNumFmtId)getTypeLoader().parse(inputStream, STNumFmtId.type, xmlOptions);
        }
        
        public static STNumFmtId parse(final Reader reader) throws XmlException, IOException {
            return (STNumFmtId)getTypeLoader().parse(reader, STNumFmtId.type, (XmlOptions)null);
        }
        
        public static STNumFmtId parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STNumFmtId)getTypeLoader().parse(reader, STNumFmtId.type, xmlOptions);
        }
        
        public static STNumFmtId parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STNumFmtId)getTypeLoader().parse(xmlStreamReader, STNumFmtId.type, (XmlOptions)null);
        }
        
        public static STNumFmtId parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STNumFmtId)getTypeLoader().parse(xmlStreamReader, STNumFmtId.type, xmlOptions);
        }
        
        public static STNumFmtId parse(final Node node) throws XmlException {
            return (STNumFmtId)getTypeLoader().parse(node, STNumFmtId.type, (XmlOptions)null);
        }
        
        public static STNumFmtId parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STNumFmtId)getTypeLoader().parse(node, STNumFmtId.type, xmlOptions);
        }
        
        @Deprecated
        public static STNumFmtId parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STNumFmtId)getTypeLoader().parse(xmlInputStream, STNumFmtId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STNumFmtId parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STNumFmtId)getTypeLoader().parse(xmlInputStream, STNumFmtId.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STNumFmtId.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STNumFmtId.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
