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
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPivotCacheDefinition extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPivotCacheDefinition.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpivotcachedefinition575ctype");
    
    CTCacheSource getCacheSource();
    
    void setCacheSource(final CTCacheSource p0);
    
    CTCacheSource addNewCacheSource();
    
    CTCacheFields getCacheFields();
    
    void setCacheFields(final CTCacheFields p0);
    
    CTCacheFields addNewCacheFields();
    
    CTCacheHierarchies getCacheHierarchies();
    
    boolean isSetCacheHierarchies();
    
    void setCacheHierarchies(final CTCacheHierarchies p0);
    
    CTCacheHierarchies addNewCacheHierarchies();
    
    void unsetCacheHierarchies();
    
    CTPCDKPIs getKpis();
    
    boolean isSetKpis();
    
    void setKpis(final CTPCDKPIs p0);
    
    CTPCDKPIs addNewKpis();
    
    void unsetKpis();
    
    CTTupleCache getTupleCache();
    
    boolean isSetTupleCache();
    
    void setTupleCache(final CTTupleCache p0);
    
    CTTupleCache addNewTupleCache();
    
    void unsetTupleCache();
    
    CTCalculatedItems getCalculatedItems();
    
    boolean isSetCalculatedItems();
    
    void setCalculatedItems(final CTCalculatedItems p0);
    
    CTCalculatedItems addNewCalculatedItems();
    
    void unsetCalculatedItems();
    
    CTCalculatedMembers getCalculatedMembers();
    
    boolean isSetCalculatedMembers();
    
    void setCalculatedMembers(final CTCalculatedMembers p0);
    
    CTCalculatedMembers addNewCalculatedMembers();
    
    void unsetCalculatedMembers();
    
    CTDimensions getDimensions();
    
    boolean isSetDimensions();
    
    void setDimensions(final CTDimensions p0);
    
    CTDimensions addNewDimensions();
    
    void unsetDimensions();
    
    CTMeasureGroups getMeasureGroups();
    
    boolean isSetMeasureGroups();
    
    void setMeasureGroups(final CTMeasureGroups p0);
    
    CTMeasureGroups addNewMeasureGroups();
    
    void unsetMeasureGroups();
    
    CTMeasureDimensionMaps getMaps();
    
    boolean isSetMaps();
    
    void setMaps(final CTMeasureDimensionMaps p0);
    
    CTMeasureDimensionMaps addNewMaps();
    
    void unsetMaps();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    String getId();
    
    STRelationshipId xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final STRelationshipId p0);
    
    void unsetId();
    
    boolean getInvalid();
    
    XmlBoolean xgetInvalid();
    
    boolean isSetInvalid();
    
    void setInvalid(final boolean p0);
    
    void xsetInvalid(final XmlBoolean p0);
    
    void unsetInvalid();
    
    boolean getSaveData();
    
    XmlBoolean xgetSaveData();
    
    boolean isSetSaveData();
    
    void setSaveData(final boolean p0);
    
    void xsetSaveData(final XmlBoolean p0);
    
    void unsetSaveData();
    
    boolean getRefreshOnLoad();
    
    XmlBoolean xgetRefreshOnLoad();
    
    boolean isSetRefreshOnLoad();
    
    void setRefreshOnLoad(final boolean p0);
    
    void xsetRefreshOnLoad(final XmlBoolean p0);
    
    void unsetRefreshOnLoad();
    
    boolean getOptimizeMemory();
    
    XmlBoolean xgetOptimizeMemory();
    
    boolean isSetOptimizeMemory();
    
    void setOptimizeMemory(final boolean p0);
    
    void xsetOptimizeMemory(final XmlBoolean p0);
    
    void unsetOptimizeMemory();
    
    boolean getEnableRefresh();
    
    XmlBoolean xgetEnableRefresh();
    
    boolean isSetEnableRefresh();
    
    void setEnableRefresh(final boolean p0);
    
    void xsetEnableRefresh(final XmlBoolean p0);
    
    void unsetEnableRefresh();
    
    String getRefreshedBy();
    
    STXstring xgetRefreshedBy();
    
    boolean isSetRefreshedBy();
    
    void setRefreshedBy(final String p0);
    
    void xsetRefreshedBy(final STXstring p0);
    
    void unsetRefreshedBy();
    
    double getRefreshedDate();
    
    XmlDouble xgetRefreshedDate();
    
    boolean isSetRefreshedDate();
    
    void setRefreshedDate(final double p0);
    
    void xsetRefreshedDate(final XmlDouble p0);
    
    void unsetRefreshedDate();
    
    boolean getBackgroundQuery();
    
    XmlBoolean xgetBackgroundQuery();
    
    boolean isSetBackgroundQuery();
    
    void setBackgroundQuery(final boolean p0);
    
    void xsetBackgroundQuery(final XmlBoolean p0);
    
    void unsetBackgroundQuery();
    
    long getMissingItemsLimit();
    
    XmlUnsignedInt xgetMissingItemsLimit();
    
    boolean isSetMissingItemsLimit();
    
    void setMissingItemsLimit(final long p0);
    
    void xsetMissingItemsLimit(final XmlUnsignedInt p0);
    
    void unsetMissingItemsLimit();
    
    short getCreatedVersion();
    
    XmlUnsignedByte xgetCreatedVersion();
    
    boolean isSetCreatedVersion();
    
    void setCreatedVersion(final short p0);
    
    void xsetCreatedVersion(final XmlUnsignedByte p0);
    
    void unsetCreatedVersion();
    
    short getRefreshedVersion();
    
    XmlUnsignedByte xgetRefreshedVersion();
    
    boolean isSetRefreshedVersion();
    
    void setRefreshedVersion(final short p0);
    
    void xsetRefreshedVersion(final XmlUnsignedByte p0);
    
    void unsetRefreshedVersion();
    
    short getMinRefreshableVersion();
    
    XmlUnsignedByte xgetMinRefreshableVersion();
    
    boolean isSetMinRefreshableVersion();
    
    void setMinRefreshableVersion(final short p0);
    
    void xsetMinRefreshableVersion(final XmlUnsignedByte p0);
    
    void unsetMinRefreshableVersion();
    
    long getRecordCount();
    
    XmlUnsignedInt xgetRecordCount();
    
    boolean isSetRecordCount();
    
    void setRecordCount(final long p0);
    
    void xsetRecordCount(final XmlUnsignedInt p0);
    
    void unsetRecordCount();
    
    boolean getUpgradeOnRefresh();
    
    XmlBoolean xgetUpgradeOnRefresh();
    
    boolean isSetUpgradeOnRefresh();
    
    void setUpgradeOnRefresh(final boolean p0);
    
    void xsetUpgradeOnRefresh(final XmlBoolean p0);
    
    void unsetUpgradeOnRefresh();
    
    boolean getTupleCache2();
    
    XmlBoolean xgetTupleCache2();
    
    boolean isSetTupleCache2();
    
    void setTupleCache2(final boolean p0);
    
    void xsetTupleCache2(final XmlBoolean p0);
    
    void unsetTupleCache2();
    
    boolean getSupportSubquery();
    
    XmlBoolean xgetSupportSubquery();
    
    boolean isSetSupportSubquery();
    
    void setSupportSubquery(final boolean p0);
    
    void xsetSupportSubquery(final XmlBoolean p0);
    
    void unsetSupportSubquery();
    
    boolean getSupportAdvancedDrill();
    
    XmlBoolean xgetSupportAdvancedDrill();
    
    boolean isSetSupportAdvancedDrill();
    
    void setSupportAdvancedDrill(final boolean p0);
    
    void xsetSupportAdvancedDrill(final XmlBoolean p0);
    
    void unsetSupportAdvancedDrill();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPivotCacheDefinition.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPivotCacheDefinition newInstance() {
            return (CTPivotCacheDefinition)getTypeLoader().newInstance(CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheDefinition newInstance(final XmlOptions xmlOptions) {
            return (CTPivotCacheDefinition)getTypeLoader().newInstance(CTPivotCacheDefinition.type, xmlOptions);
        }
        
        public static CTPivotCacheDefinition parse(final String s) throws XmlException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(s, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheDefinition parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(s, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        public static CTPivotCacheDefinition parse(final File file) throws XmlException, IOException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(file, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheDefinition parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(file, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        public static CTPivotCacheDefinition parse(final URL url) throws XmlException, IOException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(url, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheDefinition parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(url, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        public static CTPivotCacheDefinition parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(inputStream, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheDefinition parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(inputStream, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        public static CTPivotCacheDefinition parse(final Reader reader) throws XmlException, IOException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(reader, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheDefinition parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(reader, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        public static CTPivotCacheDefinition parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(xmlStreamReader, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheDefinition parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(xmlStreamReader, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        public static CTPivotCacheDefinition parse(final Node node) throws XmlException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(node, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        public static CTPivotCacheDefinition parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(node, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPivotCacheDefinition parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(xmlInputStream, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPivotCacheDefinition parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPivotCacheDefinition)getTypeLoader().parse(xmlInputStream, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotCacheDefinition.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPivotCacheDefinition.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
