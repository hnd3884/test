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
import org.apache.xmlbeans.XmlUnsignedShort;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface MasterType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(MasterType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("mastertype2d97type");
    
    PageSheetType getPageSheet();
    
    boolean isSetPageSheet();
    
    void setPageSheet(final PageSheetType p0);
    
    PageSheetType addNewPageSheet();
    
    void unsetPageSheet();
    
    RelType getRel();
    
    void setRel(final RelType p0);
    
    RelType addNewRel();
    
    IconType getIcon();
    
    boolean isSetIcon();
    
    void setIcon(final IconType p0);
    
    IconType addNewIcon();
    
    void unsetIcon();
    
    long getID();
    
    XmlUnsignedInt xgetID();
    
    void setID(final long p0);
    
    void xsetID(final XmlUnsignedInt p0);
    
    String getBaseID();
    
    XmlString xgetBaseID();
    
    boolean isSetBaseID();
    
    void setBaseID(final String p0);
    
    void xsetBaseID(final XmlString p0);
    
    void unsetBaseID();
    
    String getUniqueID();
    
    XmlString xgetUniqueID();
    
    boolean isSetUniqueID();
    
    void setUniqueID(final String p0);
    
    void xsetUniqueID(final XmlString p0);
    
    void unsetUniqueID();
    
    boolean getMatchByName();
    
    XmlBoolean xgetMatchByName();
    
    boolean isSetMatchByName();
    
    void setMatchByName(final boolean p0);
    
    void xsetMatchByName(final XmlBoolean p0);
    
    void unsetMatchByName();
    
    String getName();
    
    XmlString xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    void unsetName();
    
    String getNameU();
    
    XmlString xgetNameU();
    
    boolean isSetNameU();
    
    void setNameU(final String p0);
    
    void xsetNameU(final XmlString p0);
    
    void unsetNameU();
    
    boolean getIsCustomName();
    
    XmlBoolean xgetIsCustomName();
    
    boolean isSetIsCustomName();
    
    void setIsCustomName(final boolean p0);
    
    void xsetIsCustomName(final XmlBoolean p0);
    
    void unsetIsCustomName();
    
    boolean getIsCustomNameU();
    
    XmlBoolean xgetIsCustomNameU();
    
    boolean isSetIsCustomNameU();
    
    void setIsCustomNameU(final boolean p0);
    
    void xsetIsCustomNameU(final XmlBoolean p0);
    
    void unsetIsCustomNameU();
    
    int getIconSize();
    
    XmlUnsignedShort xgetIconSize();
    
    boolean isSetIconSize();
    
    void setIconSize(final int p0);
    
    void xsetIconSize(final XmlUnsignedShort p0);
    
    void unsetIconSize();
    
    int getPatternFlags();
    
    XmlUnsignedShort xgetPatternFlags();
    
    boolean isSetPatternFlags();
    
    void setPatternFlags(final int p0);
    
    void xsetPatternFlags(final XmlUnsignedShort p0);
    
    void unsetPatternFlags();
    
    String getPrompt();
    
    XmlString xgetPrompt();
    
    boolean isSetPrompt();
    
    void setPrompt(final String p0);
    
    void xsetPrompt(final XmlString p0);
    
    void unsetPrompt();
    
    boolean getHidden();
    
    XmlBoolean xgetHidden();
    
    boolean isSetHidden();
    
    void setHidden(final boolean p0);
    
    void xsetHidden(final XmlBoolean p0);
    
    void unsetHidden();
    
    boolean getIconUpdate();
    
    XmlBoolean xgetIconUpdate();
    
    boolean isSetIconUpdate();
    
    void setIconUpdate(final boolean p0);
    
    void xsetIconUpdate(final XmlBoolean p0);
    
    void unsetIconUpdate();
    
    int getAlignName();
    
    XmlUnsignedShort xgetAlignName();
    
    boolean isSetAlignName();
    
    void setAlignName(final int p0);
    
    void xsetAlignName(final XmlUnsignedShort p0);
    
    void unsetAlignName();
    
    int getMasterType();
    
    XmlUnsignedShort xgetMasterType();
    
    boolean isSetMasterType();
    
    void setMasterType(final int p0);
    
    void xsetMasterType(final XmlUnsignedShort p0);
    
    void unsetMasterType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(MasterType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static MasterType newInstance() {
            return (MasterType)getTypeLoader().newInstance(MasterType.type, (XmlOptions)null);
        }
        
        public static MasterType newInstance(final XmlOptions xmlOptions) {
            return (MasterType)getTypeLoader().newInstance(MasterType.type, xmlOptions);
        }
        
        public static MasterType parse(final String s) throws XmlException {
            return (MasterType)getTypeLoader().parse(s, MasterType.type, (XmlOptions)null);
        }
        
        public static MasterType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (MasterType)getTypeLoader().parse(s, MasterType.type, xmlOptions);
        }
        
        public static MasterType parse(final File file) throws XmlException, IOException {
            return (MasterType)getTypeLoader().parse(file, MasterType.type, (XmlOptions)null);
        }
        
        public static MasterType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MasterType)getTypeLoader().parse(file, MasterType.type, xmlOptions);
        }
        
        public static MasterType parse(final URL url) throws XmlException, IOException {
            return (MasterType)getTypeLoader().parse(url, MasterType.type, (XmlOptions)null);
        }
        
        public static MasterType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MasterType)getTypeLoader().parse(url, MasterType.type, xmlOptions);
        }
        
        public static MasterType parse(final InputStream inputStream) throws XmlException, IOException {
            return (MasterType)getTypeLoader().parse(inputStream, MasterType.type, (XmlOptions)null);
        }
        
        public static MasterType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MasterType)getTypeLoader().parse(inputStream, MasterType.type, xmlOptions);
        }
        
        public static MasterType parse(final Reader reader) throws XmlException, IOException {
            return (MasterType)getTypeLoader().parse(reader, MasterType.type, (XmlOptions)null);
        }
        
        public static MasterType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (MasterType)getTypeLoader().parse(reader, MasterType.type, xmlOptions);
        }
        
        public static MasterType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (MasterType)getTypeLoader().parse(xmlStreamReader, MasterType.type, (XmlOptions)null);
        }
        
        public static MasterType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (MasterType)getTypeLoader().parse(xmlStreamReader, MasterType.type, xmlOptions);
        }
        
        public static MasterType parse(final Node node) throws XmlException {
            return (MasterType)getTypeLoader().parse(node, MasterType.type, (XmlOptions)null);
        }
        
        public static MasterType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (MasterType)getTypeLoader().parse(node, MasterType.type, xmlOptions);
        }
        
        @Deprecated
        public static MasterType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (MasterType)getTypeLoader().parse(xmlInputStream, MasterType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static MasterType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (MasterType)getTypeLoader().parse(xmlInputStream, MasterType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, MasterType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, MasterType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
