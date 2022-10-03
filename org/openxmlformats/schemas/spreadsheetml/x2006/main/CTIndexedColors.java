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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTIndexedColors extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTIndexedColors.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctindexedcolorsa0a0type");
    
    List<CTRgbColor> getRgbColorList();
    
    @Deprecated
    CTRgbColor[] getRgbColorArray();
    
    CTRgbColor getRgbColorArray(final int p0);
    
    int sizeOfRgbColorArray();
    
    void setRgbColorArray(final CTRgbColor[] p0);
    
    void setRgbColorArray(final int p0, final CTRgbColor p1);
    
    CTRgbColor insertNewRgbColor(final int p0);
    
    CTRgbColor addNewRgbColor();
    
    void removeRgbColor(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTIndexedColors.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTIndexedColors newInstance() {
            return (CTIndexedColors)getTypeLoader().newInstance(CTIndexedColors.type, (XmlOptions)null);
        }
        
        public static CTIndexedColors newInstance(final XmlOptions xmlOptions) {
            return (CTIndexedColors)getTypeLoader().newInstance(CTIndexedColors.type, xmlOptions);
        }
        
        public static CTIndexedColors parse(final String s) throws XmlException {
            return (CTIndexedColors)getTypeLoader().parse(s, CTIndexedColors.type, (XmlOptions)null);
        }
        
        public static CTIndexedColors parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTIndexedColors)getTypeLoader().parse(s, CTIndexedColors.type, xmlOptions);
        }
        
        public static CTIndexedColors parse(final File file) throws XmlException, IOException {
            return (CTIndexedColors)getTypeLoader().parse(file, CTIndexedColors.type, (XmlOptions)null);
        }
        
        public static CTIndexedColors parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIndexedColors)getTypeLoader().parse(file, CTIndexedColors.type, xmlOptions);
        }
        
        public static CTIndexedColors parse(final URL url) throws XmlException, IOException {
            return (CTIndexedColors)getTypeLoader().parse(url, CTIndexedColors.type, (XmlOptions)null);
        }
        
        public static CTIndexedColors parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIndexedColors)getTypeLoader().parse(url, CTIndexedColors.type, xmlOptions);
        }
        
        public static CTIndexedColors parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTIndexedColors)getTypeLoader().parse(inputStream, CTIndexedColors.type, (XmlOptions)null);
        }
        
        public static CTIndexedColors parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIndexedColors)getTypeLoader().parse(inputStream, CTIndexedColors.type, xmlOptions);
        }
        
        public static CTIndexedColors parse(final Reader reader) throws XmlException, IOException {
            return (CTIndexedColors)getTypeLoader().parse(reader, CTIndexedColors.type, (XmlOptions)null);
        }
        
        public static CTIndexedColors parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTIndexedColors)getTypeLoader().parse(reader, CTIndexedColors.type, xmlOptions);
        }
        
        public static CTIndexedColors parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTIndexedColors)getTypeLoader().parse(xmlStreamReader, CTIndexedColors.type, (XmlOptions)null);
        }
        
        public static CTIndexedColors parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTIndexedColors)getTypeLoader().parse(xmlStreamReader, CTIndexedColors.type, xmlOptions);
        }
        
        public static CTIndexedColors parse(final Node node) throws XmlException {
            return (CTIndexedColors)getTypeLoader().parse(node, CTIndexedColors.type, (XmlOptions)null);
        }
        
        public static CTIndexedColors parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTIndexedColors)getTypeLoader().parse(node, CTIndexedColors.type, xmlOptions);
        }
        
        @Deprecated
        public static CTIndexedColors parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTIndexedColors)getTypeLoader().parse(xmlInputStream, CTIndexedColors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTIndexedColors parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTIndexedColors)getTypeLoader().parse(xmlInputStream, CTIndexedColors.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIndexedColors.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTIndexedColors.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
