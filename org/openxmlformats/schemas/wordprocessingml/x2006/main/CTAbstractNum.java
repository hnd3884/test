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

public interface CTAbstractNum extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAbstractNum.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctabstractnum588etype");
    
    CTLongHexNumber getNsid();
    
    boolean isSetNsid();
    
    void setNsid(final CTLongHexNumber p0);
    
    CTLongHexNumber addNewNsid();
    
    void unsetNsid();
    
    CTMultiLevelType getMultiLevelType();
    
    boolean isSetMultiLevelType();
    
    void setMultiLevelType(final CTMultiLevelType p0);
    
    CTMultiLevelType addNewMultiLevelType();
    
    void unsetMultiLevelType();
    
    CTLongHexNumber getTmpl();
    
    boolean isSetTmpl();
    
    void setTmpl(final CTLongHexNumber p0);
    
    CTLongHexNumber addNewTmpl();
    
    void unsetTmpl();
    
    CTString getName();
    
    boolean isSetName();
    
    void setName(final CTString p0);
    
    CTString addNewName();
    
    void unsetName();
    
    CTString getStyleLink();
    
    boolean isSetStyleLink();
    
    void setStyleLink(final CTString p0);
    
    CTString addNewStyleLink();
    
    void unsetStyleLink();
    
    CTString getNumStyleLink();
    
    boolean isSetNumStyleLink();
    
    void setNumStyleLink(final CTString p0);
    
    CTString addNewNumStyleLink();
    
    void unsetNumStyleLink();
    
    List<CTLvl> getLvlList();
    
    @Deprecated
    CTLvl[] getLvlArray();
    
    CTLvl getLvlArray(final int p0);
    
    int sizeOfLvlArray();
    
    void setLvlArray(final CTLvl[] p0);
    
    void setLvlArray(final int p0, final CTLvl p1);
    
    CTLvl insertNewLvl(final int p0);
    
    CTLvl addNewLvl();
    
    void removeLvl(final int p0);
    
    BigInteger getAbstractNumId();
    
    STDecimalNumber xgetAbstractNumId();
    
    void setAbstractNumId(final BigInteger p0);
    
    void xsetAbstractNumId(final STDecimalNumber p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAbstractNum.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAbstractNum newInstance() {
            return (CTAbstractNum)getTypeLoader().newInstance(CTAbstractNum.type, (XmlOptions)null);
        }
        
        public static CTAbstractNum newInstance(final XmlOptions xmlOptions) {
            return (CTAbstractNum)getTypeLoader().newInstance(CTAbstractNum.type, xmlOptions);
        }
        
        public static CTAbstractNum parse(final String s) throws XmlException {
            return (CTAbstractNum)getTypeLoader().parse(s, CTAbstractNum.type, (XmlOptions)null);
        }
        
        public static CTAbstractNum parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAbstractNum)getTypeLoader().parse(s, CTAbstractNum.type, xmlOptions);
        }
        
        public static CTAbstractNum parse(final File file) throws XmlException, IOException {
            return (CTAbstractNum)getTypeLoader().parse(file, CTAbstractNum.type, (XmlOptions)null);
        }
        
        public static CTAbstractNum parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAbstractNum)getTypeLoader().parse(file, CTAbstractNum.type, xmlOptions);
        }
        
        public static CTAbstractNum parse(final URL url) throws XmlException, IOException {
            return (CTAbstractNum)getTypeLoader().parse(url, CTAbstractNum.type, (XmlOptions)null);
        }
        
        public static CTAbstractNum parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAbstractNum)getTypeLoader().parse(url, CTAbstractNum.type, xmlOptions);
        }
        
        public static CTAbstractNum parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAbstractNum)getTypeLoader().parse(inputStream, CTAbstractNum.type, (XmlOptions)null);
        }
        
        public static CTAbstractNum parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAbstractNum)getTypeLoader().parse(inputStream, CTAbstractNum.type, xmlOptions);
        }
        
        public static CTAbstractNum parse(final Reader reader) throws XmlException, IOException {
            return (CTAbstractNum)getTypeLoader().parse(reader, CTAbstractNum.type, (XmlOptions)null);
        }
        
        public static CTAbstractNum parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAbstractNum)getTypeLoader().parse(reader, CTAbstractNum.type, xmlOptions);
        }
        
        public static CTAbstractNum parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAbstractNum)getTypeLoader().parse(xmlStreamReader, CTAbstractNum.type, (XmlOptions)null);
        }
        
        public static CTAbstractNum parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAbstractNum)getTypeLoader().parse(xmlStreamReader, CTAbstractNum.type, xmlOptions);
        }
        
        public static CTAbstractNum parse(final Node node) throws XmlException {
            return (CTAbstractNum)getTypeLoader().parse(node, CTAbstractNum.type, (XmlOptions)null);
        }
        
        public static CTAbstractNum parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAbstractNum)getTypeLoader().parse(node, CTAbstractNum.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAbstractNum parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAbstractNum)getTypeLoader().parse(xmlInputStream, CTAbstractNum.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAbstractNum parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAbstractNum)getTypeLoader().parse(xmlInputStream, CTAbstractNum.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAbstractNum.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAbstractNum.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
