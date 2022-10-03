package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSourceType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConsolidation;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheetSource;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheSource;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCacheSourceImpl extends XmlComplexContentImpl implements CTCacheSource
{
    private static final long serialVersionUID = 1L;
    private static final QName WORKSHEETSOURCE$0;
    private static final QName CONSOLIDATION$2;
    private static final QName EXTLST$4;
    private static final QName TYPE$6;
    private static final QName CONNECTIONID$8;
    
    public CTCacheSourceImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTWorksheetSource getWorksheetSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWorksheetSource ctWorksheetSource = (CTWorksheetSource)this.get_store().find_element_user(CTCacheSourceImpl.WORKSHEETSOURCE$0, 0);
            if (ctWorksheetSource == null) {
                return null;
            }
            return ctWorksheetSource;
        }
    }
    
    public boolean isSetWorksheetSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCacheSourceImpl.WORKSHEETSOURCE$0) != 0;
        }
    }
    
    public void setWorksheetSource(final CTWorksheetSource ctWorksheetSource) {
        this.generatedSetterHelperImpl((XmlObject)ctWorksheetSource, CTCacheSourceImpl.WORKSHEETSOURCE$0, 0, (short)1);
    }
    
    public CTWorksheetSource addNewWorksheetSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWorksheetSource)this.get_store().add_element_user(CTCacheSourceImpl.WORKSHEETSOURCE$0);
        }
    }
    
    public void unsetWorksheetSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCacheSourceImpl.WORKSHEETSOURCE$0, 0);
        }
    }
    
    public CTConsolidation getConsolidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTConsolidation ctConsolidation = (CTConsolidation)this.get_store().find_element_user(CTCacheSourceImpl.CONSOLIDATION$2, 0);
            if (ctConsolidation == null) {
                return null;
            }
            return ctConsolidation;
        }
    }
    
    public boolean isSetConsolidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCacheSourceImpl.CONSOLIDATION$2) != 0;
        }
    }
    
    public void setConsolidation(final CTConsolidation ctConsolidation) {
        this.generatedSetterHelperImpl((XmlObject)ctConsolidation, CTCacheSourceImpl.CONSOLIDATION$2, 0, (short)1);
    }
    
    public CTConsolidation addNewConsolidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTConsolidation)this.get_store().add_element_user(CTCacheSourceImpl.CONSOLIDATION$2);
        }
    }
    
    public void unsetConsolidation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCacheSourceImpl.CONSOLIDATION$2, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCacheSourceImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCacheSourceImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCacheSourceImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCacheSourceImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCacheSourceImpl.EXTLST$4, 0);
        }
    }
    
    public STSourceType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheSourceImpl.TYPE$6);
            if (simpleValue == null) {
                return null;
            }
            return (STSourceType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STSourceType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSourceType)this.get_store().find_attribute_user(CTCacheSourceImpl.TYPE$6);
        }
    }
    
    public void setType(final STSourceType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheSourceImpl.TYPE$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheSourceImpl.TYPE$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STSourceType stSourceType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSourceType stSourceType2 = (STSourceType)this.get_store().find_attribute_user(CTCacheSourceImpl.TYPE$6);
            if (stSourceType2 == null) {
                stSourceType2 = (STSourceType)this.get_store().add_attribute_user(CTCacheSourceImpl.TYPE$6);
            }
            stSourceType2.set((XmlObject)stSourceType);
        }
    }
    
    public long getConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheSourceImpl.CONNECTIONID$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCacheSourceImpl.CONNECTIONID$8);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCacheSourceImpl.CONNECTIONID$8);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTCacheSourceImpl.CONNECTIONID$8);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCacheSourceImpl.CONNECTIONID$8) != null;
        }
    }
    
    public void setConnectionId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCacheSourceImpl.CONNECTIONID$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCacheSourceImpl.CONNECTIONID$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetConnectionId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCacheSourceImpl.CONNECTIONID$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCacheSourceImpl.CONNECTIONID$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetConnectionId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCacheSourceImpl.CONNECTIONID$8);
        }
    }
    
    static {
        WORKSHEETSOURCE$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "worksheetSource");
        CONSOLIDATION$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "consolidation");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        TYPE$6 = new QName("", "type");
        CONNECTIONID$8 = new QName("", "connectionId");
    }
}
