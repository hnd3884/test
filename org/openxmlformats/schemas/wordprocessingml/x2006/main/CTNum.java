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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTNum extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTNum.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctnume94ctype");
    
    CTDecimalNumber getAbstractNumId();
    
    void setAbstractNumId(final CTDecimalNumber p0);
    
    CTDecimalNumber addNewAbstractNumId();
    
    List<CTNumLvl> getLvlOverrideList();
    
    @Deprecated
    CTNumLvl[] getLvlOverrideArray();
    
    CTNumLvl getLvlOverrideArray(final int p0);
    
    int sizeOfLvlOverrideArray();
    
    void setLvlOverrideArray(final CTNumLvl[] p0);
    
    void setLvlOverrideArray(final int p0, final CTNumLvl p1);
    
    CTNumLvl insertNewLvlOverride(final int p0);
    
    CTNumLvl addNewLvlOverride();
    
    void removeLvlOverride(final int p0);
    
    BigInteger getNumId();
    
    STDecimalNumber xgetNumId();
    
    void setNumId(final BigInteger p0);
    
    void xsetNumId(final STDecimalNumber p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTNum.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTNum newInstance() {
            return (CTNum)getTypeLoader().newInstance(CTNum.type, (XmlOptions)null);
        }
        
        public static CTNum newInstance(final XmlOptions xmlOptions) {
            return (CTNum)getTypeLoader().newInstance(CTNum.type, xmlOptions);
        }
        
        public static CTNum parse(final String s) throws XmlException {
            return (CTNum)getTypeLoader().parse(s, CTNum.type, (XmlOptions)null);
        }
        
        public static CTNum parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTNum)getTypeLoader().parse(s, CTNum.type, xmlOptions);
        }
        
        public static CTNum parse(final File file) throws XmlException, IOException {
            return (CTNum)getTypeLoader().parse(file, CTNum.type, (XmlOptions)null);
        }
        
        public static CTNum parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNum)getTypeLoader().parse(file, CTNum.type, xmlOptions);
        }
        
        public static CTNum parse(final URL url) throws XmlException, IOException {
            return (CTNum)getTypeLoader().parse(url, CTNum.type, (XmlOptions)null);
        }
        
        public static CTNum parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNum)getTypeLoader().parse(url, CTNum.type, xmlOptions);
        }
        
        public static CTNum parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTNum)getTypeLoader().parse(inputStream, CTNum.type, (XmlOptions)null);
        }
        
        public static CTNum parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNum)getTypeLoader().parse(inputStream, CTNum.type, xmlOptions);
        }
        
        public static CTNum parse(final Reader reader) throws XmlException, IOException {
            return (CTNum)getTypeLoader().parse(reader, CTNum.type, (XmlOptions)null);
        }
        
        public static CTNum parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTNum)getTypeLoader().parse(reader, CTNum.type, xmlOptions);
        }
        
        public static CTNum parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTNum)getTypeLoader().parse(xmlStreamReader, CTNum.type, (XmlOptions)null);
        }
        
        public static CTNum parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTNum)getTypeLoader().parse(xmlStreamReader, CTNum.type, xmlOptions);
        }
        
        public static CTNum parse(final Node node) throws XmlException {
            return (CTNum)getTypeLoader().parse(node, CTNum.type, (XmlOptions)null);
        }
        
        public static CTNum parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTNum)getTypeLoader().parse(node, CTNum.type, xmlOptions);
        }
        
        @Deprecated
        public static CTNum parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTNum)getTypeLoader().parse(xmlInputStream, CTNum.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTNum parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTNum)getTypeLoader().parse(xmlInputStream, CTNum.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNum.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTNum.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
