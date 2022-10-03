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
import org.apache.xmlbeans.XmlBoolean;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCacheField extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCacheField.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcachefieldae21type");
    
    CTSharedItems getSharedItems();
    
    boolean isSetSharedItems();
    
    void setSharedItems(final CTSharedItems p0);
    
    CTSharedItems addNewSharedItems();
    
    void unsetSharedItems();
    
    CTFieldGroup getFieldGroup();
    
    boolean isSetFieldGroup();
    
    void setFieldGroup(final CTFieldGroup p0);
    
    CTFieldGroup addNewFieldGroup();
    
    void unsetFieldGroup();
    
    List<CTX> getMpMapList();
    
    @Deprecated
    CTX[] getMpMapArray();
    
    CTX getMpMapArray(final int p0);
    
    int sizeOfMpMapArray();
    
    void setMpMapArray(final CTX[] p0);
    
    void setMpMapArray(final int p0, final CTX p1);
    
    CTX insertNewMpMap(final int p0);
    
    CTX addNewMpMap();
    
    void removeMpMap(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getName();
    
    STXstring xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    String getCaption();
    
    STXstring xgetCaption();
    
    boolean isSetCaption();
    
    void setCaption(final String p0);
    
    void xsetCaption(final STXstring p0);
    
    void unsetCaption();
    
    String getPropertyName();
    
    STXstring xgetPropertyName();
    
    boolean isSetPropertyName();
    
    void setPropertyName(final String p0);
    
    void xsetPropertyName(final STXstring p0);
    
    void unsetPropertyName();
    
    boolean getServerField();
    
    XmlBoolean xgetServerField();
    
    boolean isSetServerField();
    
    void setServerField(final boolean p0);
    
    void xsetServerField(final XmlBoolean p0);
    
    void unsetServerField();
    
    boolean getUniqueList();
    
    XmlBoolean xgetUniqueList();
    
    boolean isSetUniqueList();
    
    void setUniqueList(final boolean p0);
    
    void xsetUniqueList(final XmlBoolean p0);
    
    void unsetUniqueList();
    
    long getNumFmtId();
    
    STNumFmtId xgetNumFmtId();
    
    boolean isSetNumFmtId();
    
    void setNumFmtId(final long p0);
    
    void xsetNumFmtId(final STNumFmtId p0);
    
    void unsetNumFmtId();
    
    String getFormula();
    
    STXstring xgetFormula();
    
    boolean isSetFormula();
    
    void setFormula(final String p0);
    
    void xsetFormula(final STXstring p0);
    
    void unsetFormula();
    
    int getSqlType();
    
    XmlInt xgetSqlType();
    
    boolean isSetSqlType();
    
    void setSqlType(final int p0);
    
    void xsetSqlType(final XmlInt p0);
    
    void unsetSqlType();
    
    int getHierarchy();
    
    XmlInt xgetHierarchy();
    
    boolean isSetHierarchy();
    
    void setHierarchy(final int p0);
    
    void xsetHierarchy(final XmlInt p0);
    
    void unsetHierarchy();
    
    long getLevel();
    
    XmlUnsignedInt xgetLevel();
    
    boolean isSetLevel();
    
    void setLevel(final long p0);
    
    void xsetLevel(final XmlUnsignedInt p0);
    
    void unsetLevel();
    
    boolean getDatabaseField();
    
    XmlBoolean xgetDatabaseField();
    
    boolean isSetDatabaseField();
    
    void setDatabaseField(final boolean p0);
    
    void xsetDatabaseField(final XmlBoolean p0);
    
    void unsetDatabaseField();
    
    long getMappingCount();
    
    XmlUnsignedInt xgetMappingCount();
    
    boolean isSetMappingCount();
    
    void setMappingCount(final long p0);
    
    void xsetMappingCount(final XmlUnsignedInt p0);
    
    void unsetMappingCount();
    
    boolean getMemberPropertyField();
    
    XmlBoolean xgetMemberPropertyField();
    
    boolean isSetMemberPropertyField();
    
    void setMemberPropertyField(final boolean p0);
    
    void xsetMemberPropertyField(final XmlBoolean p0);
    
    void unsetMemberPropertyField();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCacheField.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCacheField newInstance() {
            return (CTCacheField)getTypeLoader().newInstance(CTCacheField.type, (XmlOptions)null);
        }
        
        public static CTCacheField newInstance(final XmlOptions xmlOptions) {
            return (CTCacheField)getTypeLoader().newInstance(CTCacheField.type, xmlOptions);
        }
        
        public static CTCacheField parse(final String s) throws XmlException {
            return (CTCacheField)getTypeLoader().parse(s, CTCacheField.type, (XmlOptions)null);
        }
        
        public static CTCacheField parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheField)getTypeLoader().parse(s, CTCacheField.type, xmlOptions);
        }
        
        public static CTCacheField parse(final File file) throws XmlException, IOException {
            return (CTCacheField)getTypeLoader().parse(file, CTCacheField.type, (XmlOptions)null);
        }
        
        public static CTCacheField parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheField)getTypeLoader().parse(file, CTCacheField.type, xmlOptions);
        }
        
        public static CTCacheField parse(final URL url) throws XmlException, IOException {
            return (CTCacheField)getTypeLoader().parse(url, CTCacheField.type, (XmlOptions)null);
        }
        
        public static CTCacheField parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheField)getTypeLoader().parse(url, CTCacheField.type, xmlOptions);
        }
        
        public static CTCacheField parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCacheField)getTypeLoader().parse(inputStream, CTCacheField.type, (XmlOptions)null);
        }
        
        public static CTCacheField parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheField)getTypeLoader().parse(inputStream, CTCacheField.type, xmlOptions);
        }
        
        public static CTCacheField parse(final Reader reader) throws XmlException, IOException {
            return (CTCacheField)getTypeLoader().parse(reader, CTCacheField.type, (XmlOptions)null);
        }
        
        public static CTCacheField parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCacheField)getTypeLoader().parse(reader, CTCacheField.type, xmlOptions);
        }
        
        public static CTCacheField parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCacheField)getTypeLoader().parse(xmlStreamReader, CTCacheField.type, (XmlOptions)null);
        }
        
        public static CTCacheField parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheField)getTypeLoader().parse(xmlStreamReader, CTCacheField.type, xmlOptions);
        }
        
        public static CTCacheField parse(final Node node) throws XmlException {
            return (CTCacheField)getTypeLoader().parse(node, CTCacheField.type, (XmlOptions)null);
        }
        
        public static CTCacheField parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCacheField)getTypeLoader().parse(node, CTCacheField.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCacheField parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCacheField)getTypeLoader().parse(xmlInputStream, CTCacheField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCacheField parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCacheField)getTypeLoader().parse(xmlInputStream, CTCacheField.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCacheField.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCacheField.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
