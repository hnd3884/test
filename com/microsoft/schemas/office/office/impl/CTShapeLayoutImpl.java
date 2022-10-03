package com.microsoft.schemas.office.office.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import com.microsoft.schemas.vml.STExt;
import com.microsoft.schemas.office.office.CTRules;
import com.microsoft.schemas.office.office.CTRegroupTable;
import org.apache.xmlbeans.XmlObject;
import com.microsoft.schemas.office.office.CTIdMap;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.office.office.CTShapeLayout;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShapeLayoutImpl extends XmlComplexContentImpl implements CTShapeLayout
{
    private static final long serialVersionUID = 1L;
    private static final QName IDMAP$0;
    private static final QName REGROUPTABLE$2;
    private static final QName RULES$4;
    private static final QName EXT$6;
    
    public CTShapeLayoutImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTIdMap getIdmap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIdMap ctIdMap = (CTIdMap)this.get_store().find_element_user(CTShapeLayoutImpl.IDMAP$0, 0);
            if (ctIdMap == null) {
                return null;
            }
            return ctIdMap;
        }
    }
    
    public boolean isSetIdmap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapeLayoutImpl.IDMAP$0) != 0;
        }
    }
    
    public void setIdmap(final CTIdMap ctIdMap) {
        this.generatedSetterHelperImpl((XmlObject)ctIdMap, CTShapeLayoutImpl.IDMAP$0, 0, (short)1);
    }
    
    public CTIdMap addNewIdmap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIdMap)this.get_store().add_element_user(CTShapeLayoutImpl.IDMAP$0);
        }
    }
    
    public void unsetIdmap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapeLayoutImpl.IDMAP$0, 0);
        }
    }
    
    public CTRegroupTable getRegrouptable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRegroupTable ctRegroupTable = (CTRegroupTable)this.get_store().find_element_user(CTShapeLayoutImpl.REGROUPTABLE$2, 0);
            if (ctRegroupTable == null) {
                return null;
            }
            return ctRegroupTable;
        }
    }
    
    public boolean isSetRegrouptable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapeLayoutImpl.REGROUPTABLE$2) != 0;
        }
    }
    
    public void setRegrouptable(final CTRegroupTable ctRegroupTable) {
        this.generatedSetterHelperImpl((XmlObject)ctRegroupTable, CTShapeLayoutImpl.REGROUPTABLE$2, 0, (short)1);
    }
    
    public CTRegroupTable addNewRegrouptable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRegroupTable)this.get_store().add_element_user(CTShapeLayoutImpl.REGROUPTABLE$2);
        }
    }
    
    public void unsetRegrouptable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapeLayoutImpl.REGROUPTABLE$2, 0);
        }
    }
    
    public CTRules getRules() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRules ctRules = (CTRules)this.get_store().find_element_user(CTShapeLayoutImpl.RULES$4, 0);
            if (ctRules == null) {
                return null;
            }
            return ctRules;
        }
    }
    
    public boolean isSetRules() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTShapeLayoutImpl.RULES$4) != 0;
        }
    }
    
    public void setRules(final CTRules ctRules) {
        this.generatedSetterHelperImpl((XmlObject)ctRules, CTShapeLayoutImpl.RULES$4, 0, (short)1);
    }
    
    public CTRules addNewRules() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRules)this.get_store().add_element_user(CTShapeLayoutImpl.RULES$4);
        }
    }
    
    public void unsetRules() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTShapeLayoutImpl.RULES$4, 0);
        }
    }
    
    public STExt.Enum getExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeLayoutImpl.EXT$6);
            if (simpleValue == null) {
                return null;
            }
            return (STExt.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STExt xgetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STExt)this.get_store().find_attribute_user(CTShapeLayoutImpl.EXT$6);
        }
    }
    
    public boolean isSetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTShapeLayoutImpl.EXT$6) != null;
        }
    }
    
    public void setExt(final STExt.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTShapeLayoutImpl.EXT$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTShapeLayoutImpl.EXT$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetExt(final STExt stExt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STExt stExt2 = (STExt)this.get_store().find_attribute_user(CTShapeLayoutImpl.EXT$6);
            if (stExt2 == null) {
                stExt2 = (STExt)this.get_store().add_attribute_user(CTShapeLayoutImpl.EXT$6);
            }
            stExt2.set((XmlObject)stExt);
        }
    }
    
    public void unsetExt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTShapeLayoutImpl.EXT$6);
        }
    }
    
    static {
        IDMAP$0 = new QName("urn:schemas-microsoft-com:office:office", "idmap");
        REGROUPTABLE$2 = new QName("urn:schemas-microsoft-com:office:office", "regrouptable");
        RULES$4 = new QName("urn:schemas-microsoft-com:office:office", "rules");
        EXT$6 = new QName("urn:schemas-microsoft-com:vml", "ext");
    }
}
