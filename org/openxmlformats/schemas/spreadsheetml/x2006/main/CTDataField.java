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
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDataField extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDataField.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdatafield6f0ftype");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getName();
    
    STXstring xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    void unsetName();
    
    long getFld();
    
    XmlUnsignedInt xgetFld();
    
    void setFld(final long p0);
    
    void xsetFld(final XmlUnsignedInt p0);
    
    STDataConsolidateFunction.Enum getSubtotal();
    
    STDataConsolidateFunction xgetSubtotal();
    
    boolean isSetSubtotal();
    
    void setSubtotal(final STDataConsolidateFunction.Enum p0);
    
    void xsetSubtotal(final STDataConsolidateFunction p0);
    
    void unsetSubtotal();
    
    STShowDataAs.Enum getShowDataAs();
    
    STShowDataAs xgetShowDataAs();
    
    boolean isSetShowDataAs();
    
    void setShowDataAs(final STShowDataAs.Enum p0);
    
    void xsetShowDataAs(final STShowDataAs p0);
    
    void unsetShowDataAs();
    
    int getBaseField();
    
    XmlInt xgetBaseField();
    
    boolean isSetBaseField();
    
    void setBaseField(final int p0);
    
    void xsetBaseField(final XmlInt p0);
    
    void unsetBaseField();
    
    long getBaseItem();
    
    XmlUnsignedInt xgetBaseItem();
    
    boolean isSetBaseItem();
    
    void setBaseItem(final long p0);
    
    void xsetBaseItem(final XmlUnsignedInt p0);
    
    void unsetBaseItem();
    
    long getNumFmtId();
    
    STNumFmtId xgetNumFmtId();
    
    boolean isSetNumFmtId();
    
    void setNumFmtId(final long p0);
    
    void xsetNumFmtId(final STNumFmtId p0);
    
    void unsetNumFmtId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDataField.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDataField newInstance() {
            return (CTDataField)getTypeLoader().newInstance(CTDataField.type, (XmlOptions)null);
        }
        
        public static CTDataField newInstance(final XmlOptions xmlOptions) {
            return (CTDataField)getTypeLoader().newInstance(CTDataField.type, xmlOptions);
        }
        
        public static CTDataField parse(final String s) throws XmlException {
            return (CTDataField)getTypeLoader().parse(s, CTDataField.type, (XmlOptions)null);
        }
        
        public static CTDataField parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataField)getTypeLoader().parse(s, CTDataField.type, xmlOptions);
        }
        
        public static CTDataField parse(final File file) throws XmlException, IOException {
            return (CTDataField)getTypeLoader().parse(file, CTDataField.type, (XmlOptions)null);
        }
        
        public static CTDataField parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataField)getTypeLoader().parse(file, CTDataField.type, xmlOptions);
        }
        
        public static CTDataField parse(final URL url) throws XmlException, IOException {
            return (CTDataField)getTypeLoader().parse(url, CTDataField.type, (XmlOptions)null);
        }
        
        public static CTDataField parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataField)getTypeLoader().parse(url, CTDataField.type, xmlOptions);
        }
        
        public static CTDataField parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDataField)getTypeLoader().parse(inputStream, CTDataField.type, (XmlOptions)null);
        }
        
        public static CTDataField parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataField)getTypeLoader().parse(inputStream, CTDataField.type, xmlOptions);
        }
        
        public static CTDataField parse(final Reader reader) throws XmlException, IOException {
            return (CTDataField)getTypeLoader().parse(reader, CTDataField.type, (XmlOptions)null);
        }
        
        public static CTDataField parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDataField)getTypeLoader().parse(reader, CTDataField.type, xmlOptions);
        }
        
        public static CTDataField parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDataField)getTypeLoader().parse(xmlStreamReader, CTDataField.type, (XmlOptions)null);
        }
        
        public static CTDataField parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataField)getTypeLoader().parse(xmlStreamReader, CTDataField.type, xmlOptions);
        }
        
        public static CTDataField parse(final Node node) throws XmlException {
            return (CTDataField)getTypeLoader().parse(node, CTDataField.type, (XmlOptions)null);
        }
        
        public static CTDataField parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDataField)getTypeLoader().parse(node, CTDataField.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDataField parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDataField)getTypeLoader().parse(xmlInputStream, CTDataField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDataField parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDataField)getTypeLoader().parse(xmlInputStream, CTDataField.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDataField.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
