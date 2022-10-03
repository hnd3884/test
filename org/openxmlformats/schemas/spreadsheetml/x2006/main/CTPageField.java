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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPageField extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPageField.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpagefield338atype");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    int getFld();
    
    XmlInt xgetFld();
    
    void setFld(final int p0);
    
    void xsetFld(final XmlInt p0);
    
    long getItem();
    
    XmlUnsignedInt xgetItem();
    
    boolean isSetItem();
    
    void setItem(final long p0);
    
    void xsetItem(final XmlUnsignedInt p0);
    
    void unsetItem();
    
    int getHier();
    
    XmlInt xgetHier();
    
    boolean isSetHier();
    
    void setHier(final int p0);
    
    void xsetHier(final XmlInt p0);
    
    void unsetHier();
    
    String getName();
    
    STXstring xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    void unsetName();
    
    String getCap();
    
    STXstring xgetCap();
    
    boolean isSetCap();
    
    void setCap(final String p0);
    
    void xsetCap(final STXstring p0);
    
    void unsetCap();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPageField.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPageField newInstance() {
            return (CTPageField)getTypeLoader().newInstance(CTPageField.type, (XmlOptions)null);
        }
        
        public static CTPageField newInstance(final XmlOptions xmlOptions) {
            return (CTPageField)getTypeLoader().newInstance(CTPageField.type, xmlOptions);
        }
        
        public static CTPageField parse(final String s) throws XmlException {
            return (CTPageField)getTypeLoader().parse(s, CTPageField.type, (XmlOptions)null);
        }
        
        public static CTPageField parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageField)getTypeLoader().parse(s, CTPageField.type, xmlOptions);
        }
        
        public static CTPageField parse(final File file) throws XmlException, IOException {
            return (CTPageField)getTypeLoader().parse(file, CTPageField.type, (XmlOptions)null);
        }
        
        public static CTPageField parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageField)getTypeLoader().parse(file, CTPageField.type, xmlOptions);
        }
        
        public static CTPageField parse(final URL url) throws XmlException, IOException {
            return (CTPageField)getTypeLoader().parse(url, CTPageField.type, (XmlOptions)null);
        }
        
        public static CTPageField parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageField)getTypeLoader().parse(url, CTPageField.type, xmlOptions);
        }
        
        public static CTPageField parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPageField)getTypeLoader().parse(inputStream, CTPageField.type, (XmlOptions)null);
        }
        
        public static CTPageField parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageField)getTypeLoader().parse(inputStream, CTPageField.type, xmlOptions);
        }
        
        public static CTPageField parse(final Reader reader) throws XmlException, IOException {
            return (CTPageField)getTypeLoader().parse(reader, CTPageField.type, (XmlOptions)null);
        }
        
        public static CTPageField parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageField)getTypeLoader().parse(reader, CTPageField.type, xmlOptions);
        }
        
        public static CTPageField parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPageField)getTypeLoader().parse(xmlStreamReader, CTPageField.type, (XmlOptions)null);
        }
        
        public static CTPageField parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageField)getTypeLoader().parse(xmlStreamReader, CTPageField.type, xmlOptions);
        }
        
        public static CTPageField parse(final Node node) throws XmlException {
            return (CTPageField)getTypeLoader().parse(node, CTPageField.type, (XmlOptions)null);
        }
        
        public static CTPageField parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageField)getTypeLoader().parse(node, CTPageField.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPageField parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPageField)getTypeLoader().parse(xmlInputStream, CTPageField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPageField parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPageField)getTypeLoader().parse(xmlInputStream, CTPageField.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageField.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
