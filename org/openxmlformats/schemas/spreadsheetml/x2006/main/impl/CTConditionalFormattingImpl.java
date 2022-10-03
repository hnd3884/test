package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSqref;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormatting;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTConditionalFormattingImpl extends XmlComplexContentImpl implements CTConditionalFormatting
{
    private static final long serialVersionUID = 1L;
    private static final QName CFRULE$0;
    private static final QName EXTLST$2;
    private static final QName PIVOT$4;
    private static final QName SQREF$6;
    
    public CTConditionalFormattingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCfRule> getCfRuleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CfRuleList extends AbstractList<CTCfRule>
            {
                @Override
                public CTCfRule get(final int n) {
                    return CTConditionalFormattingImpl.this.getCfRuleArray(n);
                }
                
                @Override
                public CTCfRule set(final int n, final CTCfRule ctCfRule) {
                    final CTCfRule cfRuleArray = CTConditionalFormattingImpl.this.getCfRuleArray(n);
                    CTConditionalFormattingImpl.this.setCfRuleArray(n, ctCfRule);
                    return cfRuleArray;
                }
                
                @Override
                public void add(final int n, final CTCfRule ctCfRule) {
                    CTConditionalFormattingImpl.this.insertNewCfRule(n).set((XmlObject)ctCfRule);
                }
                
                @Override
                public CTCfRule remove(final int n) {
                    final CTCfRule cfRuleArray = CTConditionalFormattingImpl.this.getCfRuleArray(n);
                    CTConditionalFormattingImpl.this.removeCfRule(n);
                    return cfRuleArray;
                }
                
                @Override
                public int size() {
                    return CTConditionalFormattingImpl.this.sizeOfCfRuleArray();
                }
            }
            return new CfRuleList();
        }
    }
    
    @Deprecated
    public CTCfRule[] getCfRuleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTConditionalFormattingImpl.CFRULE$0, (List)list);
            final CTCfRule[] array = new CTCfRule[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCfRule getCfRuleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCfRule ctCfRule = (CTCfRule)this.get_store().find_element_user(CTConditionalFormattingImpl.CFRULE$0, n);
            if (ctCfRule == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCfRule;
        }
    }
    
    public int sizeOfCfRuleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTConditionalFormattingImpl.CFRULE$0);
        }
    }
    
    public void setCfRuleArray(final CTCfRule[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTConditionalFormattingImpl.CFRULE$0);
    }
    
    public void setCfRuleArray(final int n, final CTCfRule ctCfRule) {
        this.generatedSetterHelperImpl((XmlObject)ctCfRule, CTConditionalFormattingImpl.CFRULE$0, n, (short)2);
    }
    
    public CTCfRule insertNewCfRule(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCfRule)this.get_store().insert_element_user(CTConditionalFormattingImpl.CFRULE$0, n);
        }
    }
    
    public CTCfRule addNewCfRule() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCfRule)this.get_store().add_element_user(CTConditionalFormattingImpl.CFRULE$0);
        }
    }
    
    public void removeCfRule(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTConditionalFormattingImpl.CFRULE$0, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTConditionalFormattingImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTConditionalFormattingImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTConditionalFormattingImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTConditionalFormattingImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTConditionalFormattingImpl.EXTLST$2, 0);
        }
    }
    
    public boolean getPivot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConditionalFormattingImpl.PIVOT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTConditionalFormattingImpl.PIVOT$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPivot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTConditionalFormattingImpl.PIVOT$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTConditionalFormattingImpl.PIVOT$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPivot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTConditionalFormattingImpl.PIVOT$4) != null;
        }
    }
    
    public void setPivot(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConditionalFormattingImpl.PIVOT$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTConditionalFormattingImpl.PIVOT$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPivot(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTConditionalFormattingImpl.PIVOT$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTConditionalFormattingImpl.PIVOT$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPivot() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTConditionalFormattingImpl.PIVOT$4);
        }
    }
    
    public List getSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConditionalFormattingImpl.SQREF$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getListValue();
        }
    }
    
    public STSqref xgetSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STSqref)this.get_store().find_attribute_user(CTConditionalFormattingImpl.SQREF$6);
        }
    }
    
    public boolean isSetSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTConditionalFormattingImpl.SQREF$6) != null;
        }
    }
    
    public void setSqref(final List listValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConditionalFormattingImpl.SQREF$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTConditionalFormattingImpl.SQREF$6);
            }
            simpleValue.setListValue(listValue);
        }
    }
    
    public void xsetSqref(final STSqref stSqref) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSqref stSqref2 = (STSqref)this.get_store().find_attribute_user(CTConditionalFormattingImpl.SQREF$6);
            if (stSqref2 == null) {
                stSqref2 = (STSqref)this.get_store().add_attribute_user(CTConditionalFormattingImpl.SQREF$6);
            }
            stSqref2.set((XmlObject)stSqref);
        }
    }
    
    public void unsetSqref() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTConditionalFormattingImpl.SQREF$6);
        }
    }
    
    static {
        CFRULE$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "cfRule");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        PIVOT$4 = new QName("", "pivot");
        SQREF$6 = new QName("", "sqref");
    }
}
