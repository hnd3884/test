package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTblGridCol extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTblGridCol.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttblgridcolbfectype");
    
    BigInteger getW();
    
    STTwipsMeasure xgetW();
    
    boolean isSetW();
    
    void setW(final BigInteger p0);
    
    void xsetW(final STTwipsMeasure p0);
    
    void unsetW();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTblGridCol.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTblGridCol newInstance() {
            return (CTTblGridCol)getTypeLoader().newInstance(CTTblGridCol.type, (XmlOptions)null);
        }
        
        public static CTTblGridCol newInstance(final XmlOptions xmlOptions) {
            return (CTTblGridCol)getTypeLoader().newInstance(CTTblGridCol.type, xmlOptions);
        }
        
        public static CTTblGridCol parse(final String s) throws XmlException {
            return (CTTblGridCol)getTypeLoader().parse(s, CTTblGridCol.type, (XmlOptions)null);
        }
        
        public static CTTblGridCol parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGridCol)getTypeLoader().parse(s, CTTblGridCol.type, xmlOptions);
        }
        
        public static CTTblGridCol parse(final File file) throws XmlException, IOException {
            return (CTTblGridCol)getTypeLoader().parse(file, CTTblGridCol.type, (XmlOptions)null);
        }
        
        public static CTTblGridCol parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGridCol)getTypeLoader().parse(file, CTTblGridCol.type, xmlOptions);
        }
        
        public static CTTblGridCol parse(final URL url) throws XmlException, IOException {
            return (CTTblGridCol)getTypeLoader().parse(url, CTTblGridCol.type, (XmlOptions)null);
        }
        
        public static CTTblGridCol parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGridCol)getTypeLoader().parse(url, CTTblGridCol.type, xmlOptions);
        }
        
        public static CTTblGridCol parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTblGridCol)getTypeLoader().parse(inputStream, CTTblGridCol.type, (XmlOptions)null);
        }
        
        public static CTTblGridCol parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGridCol)getTypeLoader().parse(inputStream, CTTblGridCol.type, xmlOptions);
        }
        
        public static CTTblGridCol parse(final Reader reader) throws XmlException, IOException {
            return (CTTblGridCol)getTypeLoader().parse(reader, CTTblGridCol.type, (XmlOptions)null);
        }
        
        public static CTTblGridCol parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTblGridCol)getTypeLoader().parse(reader, CTTblGridCol.type, xmlOptions);
        }
        
        public static CTTblGridCol parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTblGridCol)getTypeLoader().parse(xmlStreamReader, CTTblGridCol.type, (XmlOptions)null);
        }
        
        public static CTTblGridCol parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGridCol)getTypeLoader().parse(xmlStreamReader, CTTblGridCol.type, xmlOptions);
        }
        
        public static CTTblGridCol parse(final Node node) throws XmlException {
            return (CTTblGridCol)getTypeLoader().parse(node, CTTblGridCol.type, (XmlOptions)null);
        }
        
        public static CTTblGridCol parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTblGridCol)getTypeLoader().parse(node, CTTblGridCol.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTblGridCol parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTblGridCol)getTypeLoader().parse(xmlInputStream, CTTblGridCol.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTblGridCol parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTblGridCol)getTypeLoader().parse(xmlInputStream, CTTblGridCol.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblGridCol.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTblGridCol.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
