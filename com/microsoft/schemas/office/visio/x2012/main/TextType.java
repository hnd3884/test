package com.microsoft.schemas.office.visio.x2012.main;

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

public interface TextType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TextType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("texttyped2ectype");
    
    List<CpType> getCpList();
    
    @Deprecated
    CpType[] getCpArray();
    
    CpType getCpArray(final int p0);
    
    int sizeOfCpArray();
    
    void setCpArray(final CpType[] p0);
    
    void setCpArray(final int p0, final CpType p1);
    
    CpType insertNewCp(final int p0);
    
    CpType addNewCp();
    
    void removeCp(final int p0);
    
    List<PpType> getPpList();
    
    @Deprecated
    PpType[] getPpArray();
    
    PpType getPpArray(final int p0);
    
    int sizeOfPpArray();
    
    void setPpArray(final PpType[] p0);
    
    void setPpArray(final int p0, final PpType p1);
    
    PpType insertNewPp(final int p0);
    
    PpType addNewPp();
    
    void removePp(final int p0);
    
    List<TpType> getTpList();
    
    @Deprecated
    TpType[] getTpArray();
    
    TpType getTpArray(final int p0);
    
    int sizeOfTpArray();
    
    void setTpArray(final TpType[] p0);
    
    void setTpArray(final int p0, final TpType p1);
    
    TpType insertNewTp(final int p0);
    
    TpType addNewTp();
    
    void removeTp(final int p0);
    
    List<FldType> getFldList();
    
    @Deprecated
    FldType[] getFldArray();
    
    FldType getFldArray(final int p0);
    
    int sizeOfFldArray();
    
    void setFldArray(final FldType[] p0);
    
    void setFldArray(final int p0, final FldType p1);
    
    FldType insertNewFld(final int p0);
    
    FldType addNewFld();
    
    void removeFld(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(TextType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static TextType newInstance() {
            return (TextType)getTypeLoader().newInstance(TextType.type, (XmlOptions)null);
        }
        
        public static TextType newInstance(final XmlOptions xmlOptions) {
            return (TextType)getTypeLoader().newInstance(TextType.type, xmlOptions);
        }
        
        public static TextType parse(final String s) throws XmlException {
            return (TextType)getTypeLoader().parse(s, TextType.type, (XmlOptions)null);
        }
        
        public static TextType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (TextType)getTypeLoader().parse(s, TextType.type, xmlOptions);
        }
        
        public static TextType parse(final File file) throws XmlException, IOException {
            return (TextType)getTypeLoader().parse(file, TextType.type, (XmlOptions)null);
        }
        
        public static TextType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TextType)getTypeLoader().parse(file, TextType.type, xmlOptions);
        }
        
        public static TextType parse(final URL url) throws XmlException, IOException {
            return (TextType)getTypeLoader().parse(url, TextType.type, (XmlOptions)null);
        }
        
        public static TextType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TextType)getTypeLoader().parse(url, TextType.type, xmlOptions);
        }
        
        public static TextType parse(final InputStream inputStream) throws XmlException, IOException {
            return (TextType)getTypeLoader().parse(inputStream, TextType.type, (XmlOptions)null);
        }
        
        public static TextType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TextType)getTypeLoader().parse(inputStream, TextType.type, xmlOptions);
        }
        
        public static TextType parse(final Reader reader) throws XmlException, IOException {
            return (TextType)getTypeLoader().parse(reader, TextType.type, (XmlOptions)null);
        }
        
        public static TextType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (TextType)getTypeLoader().parse(reader, TextType.type, xmlOptions);
        }
        
        public static TextType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (TextType)getTypeLoader().parse(xmlStreamReader, TextType.type, (XmlOptions)null);
        }
        
        public static TextType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (TextType)getTypeLoader().parse(xmlStreamReader, TextType.type, xmlOptions);
        }
        
        public static TextType parse(final Node node) throws XmlException {
            return (TextType)getTypeLoader().parse(node, TextType.type, (XmlOptions)null);
        }
        
        public static TextType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (TextType)getTypeLoader().parse(node, TextType.type, xmlOptions);
        }
        
        @Deprecated
        public static TextType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (TextType)getTypeLoader().parse(xmlInputStream, TextType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static TextType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (TextType)getTypeLoader().parse(xmlInputStream, TextType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TextType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, TextType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
