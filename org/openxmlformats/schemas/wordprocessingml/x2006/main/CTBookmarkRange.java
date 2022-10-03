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

public interface CTBookmarkRange extends CTMarkupRange
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBookmarkRange.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbookmarkranged88btype");
    
    BigInteger getColFirst();
    
    STDecimalNumber xgetColFirst();
    
    boolean isSetColFirst();
    
    void setColFirst(final BigInteger p0);
    
    void xsetColFirst(final STDecimalNumber p0);
    
    void unsetColFirst();
    
    BigInteger getColLast();
    
    STDecimalNumber xgetColLast();
    
    boolean isSetColLast();
    
    void setColLast(final BigInteger p0);
    
    void xsetColLast(final STDecimalNumber p0);
    
    void unsetColLast();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBookmarkRange.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBookmarkRange newInstance() {
            return (CTBookmarkRange)getTypeLoader().newInstance(CTBookmarkRange.type, (XmlOptions)null);
        }
        
        public static CTBookmarkRange newInstance(final XmlOptions xmlOptions) {
            return (CTBookmarkRange)getTypeLoader().newInstance(CTBookmarkRange.type, xmlOptions);
        }
        
        public static CTBookmarkRange parse(final String s) throws XmlException {
            return (CTBookmarkRange)getTypeLoader().parse(s, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        public static CTBookmarkRange parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookmarkRange)getTypeLoader().parse(s, CTBookmarkRange.type, xmlOptions);
        }
        
        public static CTBookmarkRange parse(final File file) throws XmlException, IOException {
            return (CTBookmarkRange)getTypeLoader().parse(file, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        public static CTBookmarkRange parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookmarkRange)getTypeLoader().parse(file, CTBookmarkRange.type, xmlOptions);
        }
        
        public static CTBookmarkRange parse(final URL url) throws XmlException, IOException {
            return (CTBookmarkRange)getTypeLoader().parse(url, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        public static CTBookmarkRange parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookmarkRange)getTypeLoader().parse(url, CTBookmarkRange.type, xmlOptions);
        }
        
        public static CTBookmarkRange parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBookmarkRange)getTypeLoader().parse(inputStream, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        public static CTBookmarkRange parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookmarkRange)getTypeLoader().parse(inputStream, CTBookmarkRange.type, xmlOptions);
        }
        
        public static CTBookmarkRange parse(final Reader reader) throws XmlException, IOException {
            return (CTBookmarkRange)getTypeLoader().parse(reader, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        public static CTBookmarkRange parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookmarkRange)getTypeLoader().parse(reader, CTBookmarkRange.type, xmlOptions);
        }
        
        public static CTBookmarkRange parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBookmarkRange)getTypeLoader().parse(xmlStreamReader, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        public static CTBookmarkRange parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookmarkRange)getTypeLoader().parse(xmlStreamReader, CTBookmarkRange.type, xmlOptions);
        }
        
        public static CTBookmarkRange parse(final Node node) throws XmlException {
            return (CTBookmarkRange)getTypeLoader().parse(node, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        public static CTBookmarkRange parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookmarkRange)getTypeLoader().parse(node, CTBookmarkRange.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBookmarkRange parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBookmarkRange)getTypeLoader().parse(xmlInputStream, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBookmarkRange parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBookmarkRange)getTypeLoader().parse(xmlInputStream, CTBookmarkRange.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBookmarkRange.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBookmarkRange.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
