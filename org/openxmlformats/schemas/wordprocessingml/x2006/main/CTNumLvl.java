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

public interface CTNumLvl extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNumLvl.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnumlvl416ctype");
    
    CTDecimalNumber getStartOverride();
    
    boolean isSetStartOverride();
    
    void setStartOverride(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewStartOverride();
    
    void unsetStartOverride();
    
    CTLvl getLvl();
    
    boolean isSetLvl();
    
    void setLvl(final CTLvl p0);
    
    CTLvl addNewLvl();
    
    void unsetLvl();
    
    BigInteger getIlvl();
    
    STDecimalNumber xgetIlvl();
    
    void setIlvl(final BigInteger p0);
    
    void xsetIlvl(final STDecimalNumber p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNumLvl.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNumLvl newInstance() {
            return (CTNumLvl)getTypeLoader().newInstance(CTNumLvl.type, (XmlOptions)null);
        }
        
        public static CTNumLvl newInstance(final XmlOptions xmlOptions) {
            return (CTNumLvl)getTypeLoader().newInstance(CTNumLvl.type, xmlOptions);
        }
        
        public static CTNumLvl parse(final String s) throws XmlException {
            return (CTNumLvl)getTypeLoader().parse(s, CTNumLvl.type, (XmlOptions)null);
        }
        
        public static CTNumLvl parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumLvl)getTypeLoader().parse(s, CTNumLvl.type, xmlOptions);
        }
        
        public static CTNumLvl parse(final File file) throws XmlException, IOException {
            return (CTNumLvl)getTypeLoader().parse(file, CTNumLvl.type, (XmlOptions)null);
        }
        
        public static CTNumLvl parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumLvl)getTypeLoader().parse(file, CTNumLvl.type, xmlOptions);
        }
        
        public static CTNumLvl parse(final URL url) throws XmlException, IOException {
            return (CTNumLvl)getTypeLoader().parse(url, CTNumLvl.type, (XmlOptions)null);
        }
        
        public static CTNumLvl parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumLvl)getTypeLoader().parse(url, CTNumLvl.type, xmlOptions);
        }
        
        public static CTNumLvl parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNumLvl)getTypeLoader().parse(inputStream, CTNumLvl.type, (XmlOptions)null);
        }
        
        public static CTNumLvl parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumLvl)getTypeLoader().parse(inputStream, CTNumLvl.type, xmlOptions);
        }
        
        public static CTNumLvl parse(final Reader reader) throws XmlException, IOException {
            return (CTNumLvl)getTypeLoader().parse(reader, CTNumLvl.type, (XmlOptions)null);
        }
        
        public static CTNumLvl parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNumLvl)getTypeLoader().parse(reader, CTNumLvl.type, xmlOptions);
        }
        
        public static CTNumLvl parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNumLvl)getTypeLoader().parse(xmlStreamReader, CTNumLvl.type, (XmlOptions)null);
        }
        
        public static CTNumLvl parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumLvl)getTypeLoader().parse(xmlStreamReader, CTNumLvl.type, xmlOptions);
        }
        
        public static CTNumLvl parse(final Node node) throws XmlException {
            return (CTNumLvl)getTypeLoader().parse(node, CTNumLvl.type, (XmlOptions)null);
        }
        
        public static CTNumLvl parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNumLvl)getTypeLoader().parse(node, CTNumLvl.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNumLvl parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNumLvl)getTypeLoader().parse(xmlInputStream, CTNumLvl.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNumLvl parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNumLvl)getTypeLoader().parse(xmlInputStream, CTNumLvl.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumLvl.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNumLvl.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
