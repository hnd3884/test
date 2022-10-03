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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPrintOptions extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPrintOptions.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctprintoptions943atype");
    
    boolean getHorizontalCentered();
    
    XmlBoolean xgetHorizontalCentered();
    
    boolean isSetHorizontalCentered();
    
    void setHorizontalCentered(final boolean p0);
    
    void xsetHorizontalCentered(final XmlBoolean p0);
    
    void unsetHorizontalCentered();
    
    boolean getVerticalCentered();
    
    XmlBoolean xgetVerticalCentered();
    
    boolean isSetVerticalCentered();
    
    void setVerticalCentered(final boolean p0);
    
    void xsetVerticalCentered(final XmlBoolean p0);
    
    void unsetVerticalCentered();
    
    boolean getHeadings();
    
    XmlBoolean xgetHeadings();
    
    boolean isSetHeadings();
    
    void setHeadings(final boolean p0);
    
    void xsetHeadings(final XmlBoolean p0);
    
    void unsetHeadings();
    
    boolean getGridLines();
    
    XmlBoolean xgetGridLines();
    
    boolean isSetGridLines();
    
    void setGridLines(final boolean p0);
    
    void xsetGridLines(final XmlBoolean p0);
    
    void unsetGridLines();
    
    boolean getGridLinesSet();
    
    XmlBoolean xgetGridLinesSet();
    
    boolean isSetGridLinesSet();
    
    void setGridLinesSet(final boolean p0);
    
    void xsetGridLinesSet(final XmlBoolean p0);
    
    void unsetGridLinesSet();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPrintOptions.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPrintOptions newInstance() {
            return (CTPrintOptions)getTypeLoader().newInstance(CTPrintOptions.type, (XmlOptions)null);
        }
        
        public static CTPrintOptions newInstance(final XmlOptions xmlOptions) {
            return (CTPrintOptions)getTypeLoader().newInstance(CTPrintOptions.type, xmlOptions);
        }
        
        public static CTPrintOptions parse(final String s) throws XmlException {
            return (CTPrintOptions)getTypeLoader().parse(s, CTPrintOptions.type, (XmlOptions)null);
        }
        
        public static CTPrintOptions parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPrintOptions)getTypeLoader().parse(s, CTPrintOptions.type, xmlOptions);
        }
        
        public static CTPrintOptions parse(final File file) throws XmlException, IOException {
            return (CTPrintOptions)getTypeLoader().parse(file, CTPrintOptions.type, (XmlOptions)null);
        }
        
        public static CTPrintOptions parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPrintOptions)getTypeLoader().parse(file, CTPrintOptions.type, xmlOptions);
        }
        
        public static CTPrintOptions parse(final URL url) throws XmlException, IOException {
            return (CTPrintOptions)getTypeLoader().parse(url, CTPrintOptions.type, (XmlOptions)null);
        }
        
        public static CTPrintOptions parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPrintOptions)getTypeLoader().parse(url, CTPrintOptions.type, xmlOptions);
        }
        
        public static CTPrintOptions parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPrintOptions)getTypeLoader().parse(inputStream, CTPrintOptions.type, (XmlOptions)null);
        }
        
        public static CTPrintOptions parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPrintOptions)getTypeLoader().parse(inputStream, CTPrintOptions.type, xmlOptions);
        }
        
        public static CTPrintOptions parse(final Reader reader) throws XmlException, IOException {
            return (CTPrintOptions)getTypeLoader().parse(reader, CTPrintOptions.type, (XmlOptions)null);
        }
        
        public static CTPrintOptions parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPrintOptions)getTypeLoader().parse(reader, CTPrintOptions.type, xmlOptions);
        }
        
        public static CTPrintOptions parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPrintOptions)getTypeLoader().parse(xmlStreamReader, CTPrintOptions.type, (XmlOptions)null);
        }
        
        public static CTPrintOptions parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPrintOptions)getTypeLoader().parse(xmlStreamReader, CTPrintOptions.type, xmlOptions);
        }
        
        public static CTPrintOptions parse(final Node node) throws XmlException {
            return (CTPrintOptions)getTypeLoader().parse(node, CTPrintOptions.type, (XmlOptions)null);
        }
        
        public static CTPrintOptions parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPrintOptions)getTypeLoader().parse(node, CTPrintOptions.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPrintOptions parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPrintOptions)getTypeLoader().parse(xmlInputStream, CTPrintOptions.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPrintOptions parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPrintOptions)getTypeLoader().parse(xmlInputStream, CTPrintOptions.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPrintOptions.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPrintOptions.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
