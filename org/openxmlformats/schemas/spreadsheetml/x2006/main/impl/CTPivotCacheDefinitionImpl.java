package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlDouble;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.officeDocument.x2006.relationships.STRelationshipId;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMeasureDimensionMaps;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMeasureGroups;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDimensions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalculatedMembers;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalculatedItems;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTupleCache;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPCDKPIs;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheHierarchies;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheFields;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheSource;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheDefinition;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPivotCacheDefinitionImpl extends XmlComplexContentImpl implements CTPivotCacheDefinition
{
    private static final long serialVersionUID = 1L;
    private static final QName CACHESOURCE$0;
    private static final QName CACHEFIELDS$2;
    private static final QName CACHEHIERARCHIES$4;
    private static final QName KPIS$6;
    private static final QName TUPLECACHE$8;
    private static final QName CALCULATEDITEMS$10;
    private static final QName CALCULATEDMEMBERS$12;
    private static final QName DIMENSIONS$14;
    private static final QName MEASUREGROUPS$16;
    private static final QName MAPS$18;
    private static final QName EXTLST$20;
    private static final QName ID$22;
    private static final QName INVALID$24;
    private static final QName SAVEDATA$26;
    private static final QName REFRESHONLOAD$28;
    private static final QName OPTIMIZEMEMORY$30;
    private static final QName ENABLEREFRESH$32;
    private static final QName REFRESHEDBY$34;
    private static final QName REFRESHEDDATE$36;
    private static final QName BACKGROUNDQUERY$38;
    private static final QName MISSINGITEMSLIMIT$40;
    private static final QName CREATEDVERSION$42;
    private static final QName REFRESHEDVERSION$44;
    private static final QName MINREFRESHABLEVERSION$46;
    private static final QName RECORDCOUNT$48;
    private static final QName UPGRADEONREFRESH$50;
    private static final QName TUPLECACHE2$52;
    private static final QName SUPPORTSUBQUERY$54;
    private static final QName SUPPORTADVANCEDDRILL$56;
    
    public CTPivotCacheDefinitionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCacheSource getCacheSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCacheSource ctCacheSource = (CTCacheSource)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.CACHESOURCE$0, 0);
            if (ctCacheSource == null) {
                return null;
            }
            return ctCacheSource;
        }
    }
    
    public void setCacheSource(final CTCacheSource ctCacheSource) {
        this.generatedSetterHelperImpl((XmlObject)ctCacheSource, CTPivotCacheDefinitionImpl.CACHESOURCE$0, 0, (short)1);
    }
    
    public CTCacheSource addNewCacheSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCacheSource)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.CACHESOURCE$0);
        }
    }
    
    public CTCacheFields getCacheFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCacheFields ctCacheFields = (CTCacheFields)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.CACHEFIELDS$2, 0);
            if (ctCacheFields == null) {
                return null;
            }
            return ctCacheFields;
        }
    }
    
    public void setCacheFields(final CTCacheFields ctCacheFields) {
        this.generatedSetterHelperImpl((XmlObject)ctCacheFields, CTPivotCacheDefinitionImpl.CACHEFIELDS$2, 0, (short)1);
    }
    
    public CTCacheFields addNewCacheFields() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCacheFields)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.CACHEFIELDS$2);
        }
    }
    
    public CTCacheHierarchies getCacheHierarchies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCacheHierarchies ctCacheHierarchies = (CTCacheHierarchies)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.CACHEHIERARCHIES$4, 0);
            if (ctCacheHierarchies == null) {
                return null;
            }
            return ctCacheHierarchies;
        }
    }
    
    public boolean isSetCacheHierarchies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.CACHEHIERARCHIES$4) != 0;
        }
    }
    
    public void setCacheHierarchies(final CTCacheHierarchies ctCacheHierarchies) {
        this.generatedSetterHelperImpl((XmlObject)ctCacheHierarchies, CTPivotCacheDefinitionImpl.CACHEHIERARCHIES$4, 0, (short)1);
    }
    
    public CTCacheHierarchies addNewCacheHierarchies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCacheHierarchies)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.CACHEHIERARCHIES$4);
        }
    }
    
    public void unsetCacheHierarchies() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.CACHEHIERARCHIES$4, 0);
        }
    }
    
    public CTPCDKPIs getKpis() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPCDKPIs ctpcdkpIs = (CTPCDKPIs)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.KPIS$6, 0);
            if (ctpcdkpIs == null) {
                return null;
            }
            return ctpcdkpIs;
        }
    }
    
    public boolean isSetKpis() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.KPIS$6) != 0;
        }
    }
    
    public void setKpis(final CTPCDKPIs ctpcdkpIs) {
        this.generatedSetterHelperImpl((XmlObject)ctpcdkpIs, CTPivotCacheDefinitionImpl.KPIS$6, 0, (short)1);
    }
    
    public CTPCDKPIs addNewKpis() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPCDKPIs)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.KPIS$6);
        }
    }
    
    public void unsetKpis() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.KPIS$6, 0);
        }
    }
    
    public CTTupleCache getTupleCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTupleCache ctTupleCache = (CTTupleCache)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.TUPLECACHE$8, 0);
            if (ctTupleCache == null) {
                return null;
            }
            return ctTupleCache;
        }
    }
    
    public boolean isSetTupleCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.TUPLECACHE$8) != 0;
        }
    }
    
    public void setTupleCache(final CTTupleCache ctTupleCache) {
        this.generatedSetterHelperImpl((XmlObject)ctTupleCache, CTPivotCacheDefinitionImpl.TUPLECACHE$8, 0, (short)1);
    }
    
    public CTTupleCache addNewTupleCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTupleCache)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.TUPLECACHE$8);
        }
    }
    
    public void unsetTupleCache() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.TUPLECACHE$8, 0);
        }
    }
    
    public CTCalculatedItems getCalculatedItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCalculatedItems ctCalculatedItems = (CTCalculatedItems)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.CALCULATEDITEMS$10, 0);
            if (ctCalculatedItems == null) {
                return null;
            }
            return ctCalculatedItems;
        }
    }
    
    public boolean isSetCalculatedItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.CALCULATEDITEMS$10) != 0;
        }
    }
    
    public void setCalculatedItems(final CTCalculatedItems ctCalculatedItems) {
        this.generatedSetterHelperImpl((XmlObject)ctCalculatedItems, CTPivotCacheDefinitionImpl.CALCULATEDITEMS$10, 0, (short)1);
    }
    
    public CTCalculatedItems addNewCalculatedItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCalculatedItems)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.CALCULATEDITEMS$10);
        }
    }
    
    public void unsetCalculatedItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.CALCULATEDITEMS$10, 0);
        }
    }
    
    public CTCalculatedMembers getCalculatedMembers() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCalculatedMembers ctCalculatedMembers = (CTCalculatedMembers)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.CALCULATEDMEMBERS$12, 0);
            if (ctCalculatedMembers == null) {
                return null;
            }
            return ctCalculatedMembers;
        }
    }
    
    public boolean isSetCalculatedMembers() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.CALCULATEDMEMBERS$12) != 0;
        }
    }
    
    public void setCalculatedMembers(final CTCalculatedMembers ctCalculatedMembers) {
        this.generatedSetterHelperImpl((XmlObject)ctCalculatedMembers, CTPivotCacheDefinitionImpl.CALCULATEDMEMBERS$12, 0, (short)1);
    }
    
    public CTCalculatedMembers addNewCalculatedMembers() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCalculatedMembers)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.CALCULATEDMEMBERS$12);
        }
    }
    
    public void unsetCalculatedMembers() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.CALCULATEDMEMBERS$12, 0);
        }
    }
    
    public CTDimensions getDimensions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDimensions ctDimensions = (CTDimensions)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.DIMENSIONS$14, 0);
            if (ctDimensions == null) {
                return null;
            }
            return ctDimensions;
        }
    }
    
    public boolean isSetDimensions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.DIMENSIONS$14) != 0;
        }
    }
    
    public void setDimensions(final CTDimensions ctDimensions) {
        this.generatedSetterHelperImpl((XmlObject)ctDimensions, CTPivotCacheDefinitionImpl.DIMENSIONS$14, 0, (short)1);
    }
    
    public CTDimensions addNewDimensions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDimensions)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.DIMENSIONS$14);
        }
    }
    
    public void unsetDimensions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.DIMENSIONS$14, 0);
        }
    }
    
    public CTMeasureGroups getMeasureGroups() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMeasureGroups ctMeasureGroups = (CTMeasureGroups)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.MEASUREGROUPS$16, 0);
            if (ctMeasureGroups == null) {
                return null;
            }
            return ctMeasureGroups;
        }
    }
    
    public boolean isSetMeasureGroups() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.MEASUREGROUPS$16) != 0;
        }
    }
    
    public void setMeasureGroups(final CTMeasureGroups ctMeasureGroups) {
        this.generatedSetterHelperImpl((XmlObject)ctMeasureGroups, CTPivotCacheDefinitionImpl.MEASUREGROUPS$16, 0, (short)1);
    }
    
    public CTMeasureGroups addNewMeasureGroups() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMeasureGroups)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.MEASUREGROUPS$16);
        }
    }
    
    public void unsetMeasureGroups() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.MEASUREGROUPS$16, 0);
        }
    }
    
    public CTMeasureDimensionMaps getMaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMeasureDimensionMaps ctMeasureDimensionMaps = (CTMeasureDimensionMaps)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.MAPS$18, 0);
            if (ctMeasureDimensionMaps == null) {
                return null;
            }
            return ctMeasureDimensionMaps;
        }
    }
    
    public boolean isSetMaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.MAPS$18) != 0;
        }
    }
    
    public void setMaps(final CTMeasureDimensionMaps ctMeasureDimensionMaps) {
        this.generatedSetterHelperImpl((XmlObject)ctMeasureDimensionMaps, CTPivotCacheDefinitionImpl.MAPS$18, 0, (short)1);
    }
    
    public CTMeasureDimensionMaps addNewMaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMeasureDimensionMaps)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.MAPS$18);
        }
    }
    
    public void unsetMaps() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.MAPS$18, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTPivotCacheDefinitionImpl.EXTLST$20, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotCacheDefinitionImpl.EXTLST$20) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPivotCacheDefinitionImpl.EXTLST$20, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTPivotCacheDefinitionImpl.EXTLST$20);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotCacheDefinitionImpl.EXTLST$20, 0);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ID$22);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRelationshipId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRelationshipId)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ID$22);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ID$22) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ID$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.ID$22);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final STRelationshipId stRelationshipId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRelationshipId stRelationshipId2 = (STRelationshipId)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ID$22);
            if (stRelationshipId2 == null) {
                stRelationshipId2 = (STRelationshipId)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.ID$22);
            }
            stRelationshipId2.set((XmlObject)stRelationshipId);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.ID$22);
        }
    }
    
    public boolean getInvalid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.INVALID$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.INVALID$24);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetInvalid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.INVALID$24);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.INVALID$24);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetInvalid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.INVALID$24) != null;
        }
    }
    
    public void setInvalid(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.INVALID$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.INVALID$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetInvalid(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.INVALID$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.INVALID$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetInvalid() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.INVALID$24);
        }
    }
    
    public boolean getSaveData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SAVEDATA$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.SAVEDATA$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSaveData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SAVEDATA$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.SAVEDATA$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSaveData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SAVEDATA$26) != null;
        }
    }
    
    public void setSaveData(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SAVEDATA$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.SAVEDATA$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSaveData(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SAVEDATA$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.SAVEDATA$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSaveData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.SAVEDATA$26);
        }
    }
    
    public boolean getRefreshOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRefreshOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetRefreshOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28) != null;
        }
    }
    
    public void setRefreshOnLoad(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRefreshOnLoad(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRefreshOnLoad() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.REFRESHONLOAD$28);
        }
    }
    
    public boolean getOptimizeMemory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetOptimizeMemory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetOptimizeMemory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30) != null;
        }
    }
    
    public void setOptimizeMemory(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetOptimizeMemory(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetOptimizeMemory() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.OPTIMIZEMEMORY$30);
        }
    }
    
    public boolean getEnableRefresh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEnableRefresh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEnableRefresh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32) != null;
        }
    }
    
    public void setEnableRefresh(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEnableRefresh(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEnableRefresh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.ENABLEREFRESH$32);
        }
    }
    
    public String getRefreshedBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDBY$34);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetRefreshedBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDBY$34);
        }
    }
    
    public boolean isSetRefreshedBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDBY$34) != null;
        }
    }
    
    public void setRefreshedBy(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDBY$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDBY$34);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRefreshedBy(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDBY$34);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDBY$34);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetRefreshedBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.REFRESHEDBY$34);
        }
    }
    
    public double getRefreshedDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDDATE$36);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetRefreshedDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDDATE$36);
        }
    }
    
    public boolean isSetRefreshedDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDDATE$36) != null;
        }
    }
    
    public void setRefreshedDate(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDDATE$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDDATE$36);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetRefreshedDate(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDDATE$36);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDDATE$36);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetRefreshedDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.REFRESHEDDATE$36);
        }
    }
    
    public boolean getBackgroundQuery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBackgroundQuery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetBackgroundQuery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38) != null;
        }
    }
    
    public void setBackgroundQuery(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBackgroundQuery(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBackgroundQuery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.BACKGROUNDQUERY$38);
        }
    }
    
    public long getMissingItemsLimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MISSINGITEMSLIMIT$40);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetMissingItemsLimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MISSINGITEMSLIMIT$40);
        }
    }
    
    public boolean isSetMissingItemsLimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MISSINGITEMSLIMIT$40) != null;
        }
    }
    
    public void setMissingItemsLimit(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MISSINGITEMSLIMIT$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.MISSINGITEMSLIMIT$40);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetMissingItemsLimit(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MISSINGITEMSLIMIT$40);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.MISSINGITEMSLIMIT$40);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetMissingItemsLimit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.MISSINGITEMSLIMIT$40);
        }
    }
    
    public short getCreatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetCreatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetCreatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.CREATEDVERSION$42) != null;
        }
    }
    
    public void setCreatedVersion(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetCreatedVersion(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetCreatedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.CREATEDVERSION$42);
        }
    }
    
    public short getRefreshedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetRefreshedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetRefreshedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44) != null;
        }
    }
    
    public void setRefreshedVersion(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetRefreshedVersion(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetRefreshedVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.REFRESHEDVERSION$44);
        }
    }
    
    public short getMinRefreshableVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getShortValue();
        }
    }
    
    public XmlUnsignedByte xgetMinRefreshableVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
            if (xmlUnsignedByte == null) {
                xmlUnsignedByte = (XmlUnsignedByte)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
            }
            return xmlUnsignedByte;
        }
    }
    
    public boolean isSetMinRefreshableVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46) != null;
        }
    }
    
    public void setMinRefreshableVersion(final short shortValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
            }
            simpleValue.setShortValue(shortValue);
        }
    }
    
    public void xsetMinRefreshableVersion(final XmlUnsignedByte xmlUnsignedByte) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedByte xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
            if (xmlUnsignedByte2 == null) {
                xmlUnsignedByte2 = (XmlUnsignedByte)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
            }
            xmlUnsignedByte2.set((XmlObject)xmlUnsignedByte);
        }
    }
    
    public void unsetMinRefreshableVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.MINREFRESHABLEVERSION$46);
        }
    }
    
    public long getRecordCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.RECORDCOUNT$48);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetRecordCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.RECORDCOUNT$48);
        }
    }
    
    public boolean isSetRecordCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.RECORDCOUNT$48) != null;
        }
    }
    
    public void setRecordCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.RECORDCOUNT$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.RECORDCOUNT$48);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetRecordCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.RECORDCOUNT$48);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.RECORDCOUNT$48);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetRecordCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.RECORDCOUNT$48);
        }
    }
    
    public boolean getUpgradeOnRefresh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetUpgradeOnRefresh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetUpgradeOnRefresh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50) != null;
        }
    }
    
    public void setUpgradeOnRefresh(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetUpgradeOnRefresh(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetUpgradeOnRefresh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.UPGRADEONREFRESH$50);
        }
    }
    
    public boolean getTupleCache2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTupleCache2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTupleCache2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.TUPLECACHE2$52) != null;
        }
    }
    
    public void setTupleCache2(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTupleCache2(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTupleCache2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.TUPLECACHE2$52);
        }
    }
    
    public boolean getSupportSubquery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSupportSubquery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSupportSubquery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54) != null;
        }
    }
    
    public void setSupportSubquery(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSupportSubquery(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSupportSubquery() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.SUPPORTSUBQUERY$54);
        }
    }
    
    public boolean getSupportAdvancedDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSupportAdvancedDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSupportAdvancedDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56) != null;
        }
    }
    
    public void setSupportAdvancedDrill(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSupportAdvancedDrill(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSupportAdvancedDrill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotCacheDefinitionImpl.SUPPORTADVANCEDDRILL$56);
        }
    }
    
    static {
        CACHESOURCE$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cacheSource");
        CACHEFIELDS$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cacheFields");
        CACHEHIERARCHIES$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cacheHierarchies");
        KPIS$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "kpis");
        TUPLECACHE$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "tupleCache");
        CALCULATEDITEMS$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "calculatedItems");
        CALCULATEDMEMBERS$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "calculatedMembers");
        DIMENSIONS$14 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dimensions");
        MEASUREGROUPS$16 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "measureGroups");
        MAPS$18 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "maps");
        EXTLST$20 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        ID$22 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/relationships", "id");
        INVALID$24 = new QName("", "invalid");
        SAVEDATA$26 = new QName("", "saveData");
        REFRESHONLOAD$28 = new QName("", "refreshOnLoad");
        OPTIMIZEMEMORY$30 = new QName("", "optimizeMemory");
        ENABLEREFRESH$32 = new QName("", "enableRefresh");
        REFRESHEDBY$34 = new QName("", "refreshedBy");
        REFRESHEDDATE$36 = new QName("", "refreshedDate");
        BACKGROUNDQUERY$38 = new QName("", "backgroundQuery");
        MISSINGITEMSLIMIT$40 = new QName("", "missingItemsLimit");
        CREATEDVERSION$42 = new QName("", "createdVersion");
        REFRESHEDVERSION$44 = new QName("", "refreshedVersion");
        MINREFRESHABLEVERSION$46 = new QName("", "minRefreshableVersion");
        RECORDCOUNT$48 = new QName("", "recordCount");
        UPGRADEONREFRESH$50 = new QName("", "upgradeOnRefresh");
        TUPLECACHE2$52 = new QName("", "tupleCache");
        SUPPORTSUBQUERY$54 = new QName("", "supportSubquery");
        SUPPORTADVANCEDDRILL$56 = new QName("", "supportAdvancedDrill");
    }
}
