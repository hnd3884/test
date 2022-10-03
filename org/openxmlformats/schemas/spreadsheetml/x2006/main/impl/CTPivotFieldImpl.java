package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFieldSortType;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STAxis;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoSortScope;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTItems;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotField;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPivotFieldImpl extends XmlComplexContentImpl implements CTPivotField
{
    private static final long serialVersionUID = 1L;
    private static final QName ITEMS$0;
    private static final QName AUTOSORTSCOPE$2;
    private static final QName EXTLST$4;
    private static final QName NAME$6;
    private static final QName AXIS$8;
    private static final QName DATAFIELD$10;
    private static final QName SUBTOTALCAPTION$12;
    private static final QName SHOWDROPDOWNS$14;
    private static final QName HIDDENLEVEL$16;
    private static final QName UNIQUEMEMBERPROPERTY$18;
    private static final QName COMPACT$20;
    private static final QName ALLDRILLED$22;
    private static final QName NUMFMTID$24;
    private static final QName OUTLINE$26;
    private static final QName SUBTOTALTOP$28;
    private static final QName DRAGTOROW$30;
    private static final QName DRAGTOCOL$32;
    private static final QName MULTIPLEITEMSELECTIONALLOWED$34;
    private static final QName DRAGTOPAGE$36;
    private static final QName DRAGTODATA$38;
    private static final QName DRAGOFF$40;
    private static final QName SHOWALL$42;
    private static final QName INSERTBLANKROW$44;
    private static final QName SERVERFIELD$46;
    private static final QName INSERTPAGEBREAK$48;
    private static final QName AUTOSHOW$50;
    private static final QName TOPAUTOSHOW$52;
    private static final QName HIDENEWITEMS$54;
    private static final QName MEASUREFILTER$56;
    private static final QName INCLUDENEWITEMSINFILTER$58;
    private static final QName ITEMPAGECOUNT$60;
    private static final QName SORTTYPE$62;
    private static final QName DATASOURCESORT$64;
    private static final QName NONAUTOSORTDEFAULT$66;
    private static final QName RANKBY$68;
    private static final QName DEFAULTSUBTOTAL$70;
    private static final QName SUMSUBTOTAL$72;
    private static final QName COUNTASUBTOTAL$74;
    private static final QName AVGSUBTOTAL$76;
    private static final QName MAXSUBTOTAL$78;
    private static final QName MINSUBTOTAL$80;
    private static final QName PRODUCTSUBTOTAL$82;
    private static final QName COUNTSUBTOTAL$84;
    private static final QName STDDEVSUBTOTAL$86;
    private static final QName STDDEVPSUBTOTAL$88;
    private static final QName VARSUBTOTAL$90;
    private static final QName VARPSUBTOTAL$92;
    private static final QName SHOWPROPCELL$94;
    private static final QName SHOWPROPTIP$96;
    private static final QName SHOWPROPASCAPTION$98;
    private static final QName DEFAULTATTRIBUTEDRILLSTATE$100;
    
    public CTPivotFieldImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTItems getItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTItems ctItems = (CTItems)this.get_store().find_element_user(CTPivotFieldImpl.ITEMS$0, 0);
            if (ctItems == null) {
                return null;
            }
            return ctItems;
        }
    }
    
    public boolean isSetItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotFieldImpl.ITEMS$0) != 0;
        }
    }
    
    public void setItems(final CTItems ctItems) {
        this.generatedSetterHelperImpl((XmlObject)ctItems, CTPivotFieldImpl.ITEMS$0, 0, (short)1);
    }
    
    public CTItems addNewItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTItems)this.get_store().add_element_user(CTPivotFieldImpl.ITEMS$0);
        }
    }
    
    public void unsetItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotFieldImpl.ITEMS$0, 0);
        }
    }
    
    public CTAutoSortScope getAutoSortScope() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAutoSortScope ctAutoSortScope = (CTAutoSortScope)this.get_store().find_element_user(CTPivotFieldImpl.AUTOSORTSCOPE$2, 0);
            if (ctAutoSortScope == null) {
                return null;
            }
            return ctAutoSortScope;
        }
    }
    
    public boolean isSetAutoSortScope() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotFieldImpl.AUTOSORTSCOPE$2) != 0;
        }
    }
    
    public void setAutoSortScope(final CTAutoSortScope ctAutoSortScope) {
        this.generatedSetterHelperImpl((XmlObject)ctAutoSortScope, CTPivotFieldImpl.AUTOSORTSCOPE$2, 0, (short)1);
    }
    
    public CTAutoSortScope addNewAutoSortScope() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAutoSortScope)this.get_store().add_element_user(CTPivotFieldImpl.AUTOSORTSCOPE$2);
        }
    }
    
    public void unsetAutoSortScope() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotFieldImpl.AUTOSORTSCOPE$2, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTPivotFieldImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPivotFieldImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPivotFieldImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTPivotFieldImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPivotFieldImpl.EXTLST$4, 0);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.NAME$6);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotFieldImpl.NAME$6);
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.NAME$6) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.NAME$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.NAME$6);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotFieldImpl.NAME$6);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotFieldImpl.NAME$6);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.NAME$6);
        }
    }
    
    public STAxis.Enum getAxis() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.AXIS$8);
            if (simpleValue == null) {
                return null;
            }
            return (STAxis.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STAxis xgetAxis() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STAxis)this.get_store().find_attribute_user(CTPivotFieldImpl.AXIS$8);
        }
    }
    
    public boolean isSetAxis() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.AXIS$8) != null;
        }
    }
    
    public void setAxis(final STAxis.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.AXIS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.AXIS$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAxis(final STAxis stAxis) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STAxis stAxis2 = (STAxis)this.get_store().find_attribute_user(CTPivotFieldImpl.AXIS$8);
            if (stAxis2 == null) {
                stAxis2 = (STAxis)this.get_store().add_attribute_user(CTPivotFieldImpl.AXIS$8);
            }
            stAxis2.set((XmlObject)stAxis);
        }
    }
    
    public void unsetAxis() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.AXIS$8);
        }
    }
    
    public boolean getDataField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DATAFIELD$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.DATAFIELD$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDataField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DATAFIELD$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.DATAFIELD$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDataField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DATAFIELD$10) != null;
        }
    }
    
    public void setDataField(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DATAFIELD$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DATAFIELD$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDataField(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DATAFIELD$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DATAFIELD$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDataField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DATAFIELD$10);
        }
    }
    
    public String getSubtotalCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALCAPTION$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetSubtotalCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALCAPTION$12);
        }
    }
    
    public boolean isSetSubtotalCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALCAPTION$12) != null;
        }
    }
    
    public void setSubtotalCaption(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALCAPTION$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SUBTOTALCAPTION$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSubtotalCaption(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALCAPTION$12);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotFieldImpl.SUBTOTALCAPTION$12);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetSubtotalCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SUBTOTALCAPTION$12);
        }
    }
    
    public boolean getShowDropDowns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWDROPDOWNS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SHOWDROPDOWNS$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowDropDowns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWDROPDOWNS$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.SHOWDROPDOWNS$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowDropDowns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWDROPDOWNS$14) != null;
        }
    }
    
    public void setShowDropDowns(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWDROPDOWNS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWDROPDOWNS$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowDropDowns(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWDROPDOWNS$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWDROPDOWNS$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowDropDowns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SHOWDROPDOWNS$14);
        }
    }
    
    public boolean getHiddenLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.HIDDENLEVEL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.HIDDENLEVEL$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHiddenLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.HIDDENLEVEL$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.HIDDENLEVEL$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHiddenLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.HIDDENLEVEL$16) != null;
        }
    }
    
    public void setHiddenLevel(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.HIDDENLEVEL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.HIDDENLEVEL$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHiddenLevel(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.HIDDENLEVEL$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.HIDDENLEVEL$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHiddenLevel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.HIDDENLEVEL$16);
        }
    }
    
    public String getUniqueMemberProperty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.UNIQUEMEMBERPROPERTY$18);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetUniqueMemberProperty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTPivotFieldImpl.UNIQUEMEMBERPROPERTY$18);
        }
    }
    
    public boolean isSetUniqueMemberProperty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.UNIQUEMEMBERPROPERTY$18) != null;
        }
    }
    
    public void setUniqueMemberProperty(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.UNIQUEMEMBERPROPERTY$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.UNIQUEMEMBERPROPERTY$18);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetUniqueMemberProperty(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTPivotFieldImpl.UNIQUEMEMBERPROPERTY$18);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTPivotFieldImpl.UNIQUEMEMBERPROPERTY$18);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetUniqueMemberProperty() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.UNIQUEMEMBERPROPERTY$18);
        }
    }
    
    public boolean getCompact() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.COMPACT$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.COMPACT$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCompact() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.COMPACT$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.COMPACT$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCompact() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.COMPACT$20) != null;
        }
    }
    
    public void setCompact(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.COMPACT$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.COMPACT$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCompact(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.COMPACT$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.COMPACT$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCompact() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.COMPACT$20);
        }
    }
    
    public boolean getAllDrilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.ALLDRILLED$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.ALLDRILLED$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAllDrilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.ALLDRILLED$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.ALLDRILLED$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAllDrilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.ALLDRILLED$22) != null;
        }
    }
    
    public void setAllDrilled(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.ALLDRILLED$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.ALLDRILLED$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAllDrilled(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.ALLDRILLED$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.ALLDRILLED$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAllDrilled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.ALLDRILLED$22);
        }
    }
    
    public long getNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.NUMFMTID$24);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STNumFmtId xgetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STNumFmtId)this.get_store().find_attribute_user(CTPivotFieldImpl.NUMFMTID$24);
        }
    }
    
    public boolean isSetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.NUMFMTID$24) != null;
        }
    }
    
    public void setNumFmtId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.NUMFMTID$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.NUMFMTID$24);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetNumFmtId(final STNumFmtId stNumFmtId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STNumFmtId stNumFmtId2 = (STNumFmtId)this.get_store().find_attribute_user(CTPivotFieldImpl.NUMFMTID$24);
            if (stNumFmtId2 == null) {
                stNumFmtId2 = (STNumFmtId)this.get_store().add_attribute_user(CTPivotFieldImpl.NUMFMTID$24);
            }
            stNumFmtId2.set((XmlObject)stNumFmtId);
        }
    }
    
    public void unsetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.NUMFMTID$24);
        }
    }
    
    public boolean getOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.OUTLINE$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.OUTLINE$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.OUTLINE$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.OUTLINE$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.OUTLINE$26) != null;
        }
    }
    
    public void setOutline(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.OUTLINE$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.OUTLINE$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetOutline(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.OUTLINE$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.OUTLINE$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.OUTLINE$26);
        }
    }
    
    public boolean getSubtotalTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALTOP$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SUBTOTALTOP$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSubtotalTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALTOP$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.SUBTOTALTOP$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSubtotalTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALTOP$28) != null;
        }
    }
    
    public void setSubtotalTop(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALTOP$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SUBTOTALTOP$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSubtotalTop(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SUBTOTALTOP$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.SUBTOTALTOP$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSubtotalTop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SUBTOTALTOP$28);
        }
    }
    
    public boolean getDragToRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOROW$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.DRAGTOROW$30);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDragToRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOROW$30);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.DRAGTOROW$30);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDragToRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOROW$30) != null;
        }
    }
    
    public void setDragToRow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOROW$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGTOROW$30);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDragToRow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOROW$30);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGTOROW$30);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDragToRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DRAGTOROW$30);
        }
    }
    
    public boolean getDragToCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOCOL$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.DRAGTOCOL$32);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDragToCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOCOL$32);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.DRAGTOCOL$32);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDragToCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOCOL$32) != null;
        }
    }
    
    public void setDragToCol(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOCOL$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGTOCOL$32);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDragToCol(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOCOL$32);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGTOCOL$32);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDragToCol() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DRAGTOCOL$32);
        }
    }
    
    public boolean getMultipleItemSelectionAllowed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMultipleItemSelectionAllowed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMultipleItemSelectionAllowed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34) != null;
        }
    }
    
    public void setMultipleItemSelectionAllowed(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMultipleItemSelectionAllowed(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMultipleItemSelectionAllowed() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.MULTIPLEITEMSELECTIONALLOWED$34);
        }
    }
    
    public boolean getDragToPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOPAGE$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.DRAGTOPAGE$36);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDragToPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOPAGE$36);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.DRAGTOPAGE$36);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDragToPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOPAGE$36) != null;
        }
    }
    
    public void setDragToPage(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOPAGE$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGTOPAGE$36);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDragToPage(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTOPAGE$36);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGTOPAGE$36);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDragToPage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DRAGTOPAGE$36);
        }
    }
    
    public boolean getDragToData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTODATA$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.DRAGTODATA$38);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDragToData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTODATA$38);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.DRAGTODATA$38);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDragToData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTODATA$38) != null;
        }
    }
    
    public void setDragToData(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTODATA$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGTODATA$38);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDragToData(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGTODATA$38);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGTODATA$38);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDragToData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DRAGTODATA$38);
        }
    }
    
    public boolean getDragOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGOFF$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.DRAGOFF$40);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDragOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGOFF$40);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.DRAGOFF$40);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDragOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGOFF$40) != null;
        }
    }
    
    public void setDragOff(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGOFF$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGOFF$40);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDragOff(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DRAGOFF$40);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DRAGOFF$40);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDragOff() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DRAGOFF$40);
        }
    }
    
    public boolean getShowAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWALL$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SHOWALL$42);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWALL$42);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.SHOWALL$42);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWALL$42) != null;
        }
    }
    
    public void setShowAll(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWALL$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWALL$42);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowAll(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWALL$42);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWALL$42);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SHOWALL$42);
        }
    }
    
    public boolean getInsertBlankRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTBLANKROW$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.INSERTBLANKROW$44);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetInsertBlankRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTBLANKROW$44);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.INSERTBLANKROW$44);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetInsertBlankRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTBLANKROW$44) != null;
        }
    }
    
    public void setInsertBlankRow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTBLANKROW$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.INSERTBLANKROW$44);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetInsertBlankRow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTBLANKROW$44);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.INSERTBLANKROW$44);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetInsertBlankRow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.INSERTBLANKROW$44);
        }
    }
    
    public boolean getServerField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SERVERFIELD$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SERVERFIELD$46);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetServerField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SERVERFIELD$46);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.SERVERFIELD$46);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetServerField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SERVERFIELD$46) != null;
        }
    }
    
    public void setServerField(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SERVERFIELD$46);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SERVERFIELD$46);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetServerField(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SERVERFIELD$46);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.SERVERFIELD$46);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetServerField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SERVERFIELD$46);
        }
    }
    
    public boolean getInsertPageBreak() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTPAGEBREAK$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.INSERTPAGEBREAK$48);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetInsertPageBreak() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTPAGEBREAK$48);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.INSERTPAGEBREAK$48);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetInsertPageBreak() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTPAGEBREAK$48) != null;
        }
    }
    
    public void setInsertPageBreak(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTPAGEBREAK$48);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.INSERTPAGEBREAK$48);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetInsertPageBreak(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.INSERTPAGEBREAK$48);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.INSERTPAGEBREAK$48);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetInsertPageBreak() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.INSERTPAGEBREAK$48);
        }
    }
    
    public boolean getAutoShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.AUTOSHOW$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.AUTOSHOW$50);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAutoShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.AUTOSHOW$50);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.AUTOSHOW$50);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAutoShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.AUTOSHOW$50) != null;
        }
    }
    
    public void setAutoShow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.AUTOSHOW$50);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.AUTOSHOW$50);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAutoShow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.AUTOSHOW$50);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.AUTOSHOW$50);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAutoShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.AUTOSHOW$50);
        }
    }
    
    public boolean getTopAutoShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.TOPAUTOSHOW$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.TOPAUTOSHOW$52);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTopAutoShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.TOPAUTOSHOW$52);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.TOPAUTOSHOW$52);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTopAutoShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.TOPAUTOSHOW$52) != null;
        }
    }
    
    public void setTopAutoShow(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.TOPAUTOSHOW$52);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.TOPAUTOSHOW$52);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTopAutoShow(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.TOPAUTOSHOW$52);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.TOPAUTOSHOW$52);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTopAutoShow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.TOPAUTOSHOW$52);
        }
    }
    
    public boolean getHideNewItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.HIDENEWITEMS$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.HIDENEWITEMS$54);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHideNewItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.HIDENEWITEMS$54);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.HIDENEWITEMS$54);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHideNewItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.HIDENEWITEMS$54) != null;
        }
    }
    
    public void setHideNewItems(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.HIDENEWITEMS$54);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.HIDENEWITEMS$54);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHideNewItems(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.HIDENEWITEMS$54);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.HIDENEWITEMS$54);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHideNewItems() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.HIDENEWITEMS$54);
        }
    }
    
    public boolean getMeasureFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.MEASUREFILTER$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.MEASUREFILTER$56);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMeasureFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.MEASUREFILTER$56);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.MEASUREFILTER$56);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMeasureFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.MEASUREFILTER$56) != null;
        }
    }
    
    public void setMeasureFilter(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.MEASUREFILTER$56);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.MEASUREFILTER$56);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMeasureFilter(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.MEASUREFILTER$56);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.MEASUREFILTER$56);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMeasureFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.MEASUREFILTER$56);
        }
    }
    
    public boolean getIncludeNewItemsInFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetIncludeNewItemsInFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetIncludeNewItemsInFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58) != null;
        }
    }
    
    public void setIncludeNewItemsInFilter(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetIncludeNewItemsInFilter(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetIncludeNewItemsInFilter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.INCLUDENEWITEMSINFILTER$58);
        }
    }
    
    public long getItemPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.ITEMPAGECOUNT$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.ITEMPAGECOUNT$60);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetItemPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotFieldImpl.ITEMPAGECOUNT$60);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTPivotFieldImpl.ITEMPAGECOUNT$60);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetItemPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.ITEMPAGECOUNT$60) != null;
        }
    }
    
    public void setItemPageCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.ITEMPAGECOUNT$60);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.ITEMPAGECOUNT$60);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetItemPageCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotFieldImpl.ITEMPAGECOUNT$60);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotFieldImpl.ITEMPAGECOUNT$60);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetItemPageCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.ITEMPAGECOUNT$60);
        }
    }
    
    public STFieldSortType.Enum getSortType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SORTTYPE$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SORTTYPE$62);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STFieldSortType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STFieldSortType xgetSortType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFieldSortType stFieldSortType = (STFieldSortType)this.get_store().find_attribute_user(CTPivotFieldImpl.SORTTYPE$62);
            if (stFieldSortType == null) {
                stFieldSortType = (STFieldSortType)this.get_default_attribute_value(CTPivotFieldImpl.SORTTYPE$62);
            }
            return stFieldSortType;
        }
    }
    
    public boolean isSetSortType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SORTTYPE$62) != null;
        }
    }
    
    public void setSortType(final STFieldSortType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SORTTYPE$62);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SORTTYPE$62);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSortType(final STFieldSortType stFieldSortType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STFieldSortType stFieldSortType2 = (STFieldSortType)this.get_store().find_attribute_user(CTPivotFieldImpl.SORTTYPE$62);
            if (stFieldSortType2 == null) {
                stFieldSortType2 = (STFieldSortType)this.get_store().add_attribute_user(CTPivotFieldImpl.SORTTYPE$62);
            }
            stFieldSortType2.set((XmlObject)stFieldSortType);
        }
    }
    
    public void unsetSortType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SORTTYPE$62);
        }
    }
    
    public boolean getDataSourceSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DATASOURCESORT$64);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDataSourceSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DATASOURCESORT$64);
        }
    }
    
    public boolean isSetDataSourceSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DATASOURCESORT$64) != null;
        }
    }
    
    public void setDataSourceSort(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DATASOURCESORT$64);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DATASOURCESORT$64);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDataSourceSort(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DATASOURCESORT$64);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DATASOURCESORT$64);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDataSourceSort() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DATASOURCESORT$64);
        }
    }
    
    public boolean getNonAutoSortDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetNonAutoSortDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetNonAutoSortDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66) != null;
        }
    }
    
    public void setNonAutoSortDefault(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetNonAutoSortDefault(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetNonAutoSortDefault() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.NONAUTOSORTDEFAULT$66);
        }
    }
    
    public long getRankBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.RANKBY$68);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetRankBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotFieldImpl.RANKBY$68);
        }
    }
    
    public boolean isSetRankBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.RANKBY$68) != null;
        }
    }
    
    public void setRankBy(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.RANKBY$68);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.RANKBY$68);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetRankBy(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTPivotFieldImpl.RANKBY$68);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTPivotFieldImpl.RANKBY$68);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetRankBy() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.RANKBY$68);
        }
    }
    
    public boolean getDefaultSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDefaultSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDefaultSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTSUBTOTAL$70) != null;
        }
    }
    
    public void setDefaultSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDefaultSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDefaultSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DEFAULTSUBTOTAL$70);
        }
    }
    
    public boolean getSumSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SUMSUBTOTAL$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SUMSUBTOTAL$72);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSumSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SUMSUBTOTAL$72);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.SUMSUBTOTAL$72);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetSumSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SUMSUBTOTAL$72) != null;
        }
    }
    
    public void setSumSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SUMSUBTOTAL$72);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SUMSUBTOTAL$72);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSumSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SUMSUBTOTAL$72);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.SUMSUBTOTAL$72);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSumSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SUMSUBTOTAL$72);
        }
    }
    
    public boolean getCountASubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTASUBTOTAL$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.COUNTASUBTOTAL$74);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCountASubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTASUBTOTAL$74);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.COUNTASUBTOTAL$74);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCountASubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTASUBTOTAL$74) != null;
        }
    }
    
    public void setCountASubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTASUBTOTAL$74);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.COUNTASUBTOTAL$74);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCountASubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTASUBTOTAL$74);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.COUNTASUBTOTAL$74);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCountASubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.COUNTASUBTOTAL$74);
        }
    }
    
    public boolean getAvgSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.AVGSUBTOTAL$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.AVGSUBTOTAL$76);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAvgSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.AVGSUBTOTAL$76);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.AVGSUBTOTAL$76);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAvgSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.AVGSUBTOTAL$76) != null;
        }
    }
    
    public void setAvgSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.AVGSUBTOTAL$76);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.AVGSUBTOTAL$76);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAvgSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.AVGSUBTOTAL$76);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.AVGSUBTOTAL$76);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAvgSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.AVGSUBTOTAL$76);
        }
    }
    
    public boolean getMaxSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.MAXSUBTOTAL$78);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.MAXSUBTOTAL$78);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMaxSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.MAXSUBTOTAL$78);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.MAXSUBTOTAL$78);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMaxSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.MAXSUBTOTAL$78) != null;
        }
    }
    
    public void setMaxSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.MAXSUBTOTAL$78);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.MAXSUBTOTAL$78);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMaxSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.MAXSUBTOTAL$78);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.MAXSUBTOTAL$78);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMaxSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.MAXSUBTOTAL$78);
        }
    }
    
    public boolean getMinSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.MINSUBTOTAL$80);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.MINSUBTOTAL$80);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetMinSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.MINSUBTOTAL$80);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.MINSUBTOTAL$80);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetMinSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.MINSUBTOTAL$80) != null;
        }
    }
    
    public void setMinSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.MINSUBTOTAL$80);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.MINSUBTOTAL$80);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetMinSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.MINSUBTOTAL$80);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.MINSUBTOTAL$80);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetMinSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.MINSUBTOTAL$80);
        }
    }
    
    public boolean getProductSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetProductSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetProductSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.PRODUCTSUBTOTAL$82) != null;
        }
    }
    
    public void setProductSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetProductSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetProductSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.PRODUCTSUBTOTAL$82);
        }
    }
    
    public boolean getCountSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTSUBTOTAL$84);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.COUNTSUBTOTAL$84);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCountSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTSUBTOTAL$84);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.COUNTSUBTOTAL$84);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCountSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTSUBTOTAL$84) != null;
        }
    }
    
    public void setCountSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTSUBTOTAL$84);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.COUNTSUBTOTAL$84);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCountSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.COUNTSUBTOTAL$84);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.COUNTSUBTOTAL$84);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCountSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.COUNTSUBTOTAL$84);
        }
    }
    
    public boolean getStdDevSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetStdDevSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetStdDevSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVSUBTOTAL$86) != null;
        }
    }
    
    public void setStdDevSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetStdDevSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetStdDevSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.STDDEVSUBTOTAL$86);
        }
    }
    
    public boolean getStdDevPSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetStdDevPSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetStdDevPSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVPSUBTOTAL$88) != null;
        }
    }
    
    public void setStdDevPSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetStdDevPSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetStdDevPSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.STDDEVPSUBTOTAL$88);
        }
    }
    
    public boolean getVarSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.VARSUBTOTAL$90);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.VARSUBTOTAL$90);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetVarSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.VARSUBTOTAL$90);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.VARSUBTOTAL$90);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetVarSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.VARSUBTOTAL$90) != null;
        }
    }
    
    public void setVarSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.VARSUBTOTAL$90);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.VARSUBTOTAL$90);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetVarSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.VARSUBTOTAL$90);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.VARSUBTOTAL$90);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetVarSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.VARSUBTOTAL$90);
        }
    }
    
    public boolean getVarPSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.VARPSUBTOTAL$92);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.VARPSUBTOTAL$92);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetVarPSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.VARPSUBTOTAL$92);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.VARPSUBTOTAL$92);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetVarPSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.VARPSUBTOTAL$92) != null;
        }
    }
    
    public void setVarPSubtotal(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.VARPSUBTOTAL$92);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.VARPSUBTOTAL$92);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetVarPSubtotal(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.VARPSUBTOTAL$92);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.VARPSUBTOTAL$92);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetVarPSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.VARPSUBTOTAL$92);
        }
    }
    
    public boolean getShowPropCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPCELL$94);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SHOWPROPCELL$94);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowPropCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPCELL$94);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.SHOWPROPCELL$94);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowPropCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPCELL$94) != null;
        }
    }
    
    public void setShowPropCell(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPCELL$94);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWPROPCELL$94);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowPropCell(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPCELL$94);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWPROPCELL$94);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowPropCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SHOWPROPCELL$94);
        }
    }
    
    public boolean getShowPropTip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPTIP$96);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SHOWPROPTIP$96);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowPropTip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPTIP$96);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.SHOWPROPTIP$96);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowPropTip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPTIP$96) != null;
        }
    }
    
    public void setShowPropTip(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPTIP$96);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWPROPTIP$96);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowPropTip(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPTIP$96);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWPROPTIP$96);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowPropTip() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SHOWPROPTIP$96);
        }
    }
    
    public boolean getShowPropAsCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowPropAsCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowPropAsCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPASCAPTION$98) != null;
        }
    }
    
    public void setShowPropAsCaption(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowPropAsCaption(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowPropAsCaption() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.SHOWPROPASCAPTION$98);
        }
    }
    
    public boolean getDefaultAttributeDrillState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDefaultAttributeDrillState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDefaultAttributeDrillState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100) != null;
        }
    }
    
    public void setDefaultAttributeDrillState(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDefaultAttributeDrillState(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDefaultAttributeDrillState() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTPivotFieldImpl.DEFAULTATTRIBUTEDRILLSTATE$100);
        }
    }
    
    static {
        ITEMS$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "items");
        AUTOSORTSCOPE$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "autoSortScope");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        NAME$6 = new QName("", "name");
        AXIS$8 = new QName("", "axis");
        DATAFIELD$10 = new QName("", "dataField");
        SUBTOTALCAPTION$12 = new QName("", "subtotalCaption");
        SHOWDROPDOWNS$14 = new QName("", "showDropDowns");
        HIDDENLEVEL$16 = new QName("", "hiddenLevel");
        UNIQUEMEMBERPROPERTY$18 = new QName("", "uniqueMemberProperty");
        COMPACT$20 = new QName("", "compact");
        ALLDRILLED$22 = new QName("", "allDrilled");
        NUMFMTID$24 = new QName("", "numFmtId");
        OUTLINE$26 = new QName("", "outline");
        SUBTOTALTOP$28 = new QName("", "subtotalTop");
        DRAGTOROW$30 = new QName("", "dragToRow");
        DRAGTOCOL$32 = new QName("", "dragToCol");
        MULTIPLEITEMSELECTIONALLOWED$34 = new QName("", "multipleItemSelectionAllowed");
        DRAGTOPAGE$36 = new QName("", "dragToPage");
        DRAGTODATA$38 = new QName("", "dragToData");
        DRAGOFF$40 = new QName("", "dragOff");
        SHOWALL$42 = new QName("", "showAll");
        INSERTBLANKROW$44 = new QName("", "insertBlankRow");
        SERVERFIELD$46 = new QName("", "serverField");
        INSERTPAGEBREAK$48 = new QName("", "insertPageBreak");
        AUTOSHOW$50 = new QName("", "autoShow");
        TOPAUTOSHOW$52 = new QName("", "topAutoShow");
        HIDENEWITEMS$54 = new QName("", "hideNewItems");
        MEASUREFILTER$56 = new QName("", "measureFilter");
        INCLUDENEWITEMSINFILTER$58 = new QName("", "includeNewItemsInFilter");
        ITEMPAGECOUNT$60 = new QName("", "itemPageCount");
        SORTTYPE$62 = new QName("", "sortType");
        DATASOURCESORT$64 = new QName("", "dataSourceSort");
        NONAUTOSORTDEFAULT$66 = new QName("", "nonAutoSortDefault");
        RANKBY$68 = new QName("", "rankBy");
        DEFAULTSUBTOTAL$70 = new QName("", "defaultSubtotal");
        SUMSUBTOTAL$72 = new QName("", "sumSubtotal");
        COUNTASUBTOTAL$74 = new QName("", "countASubtotal");
        AVGSUBTOTAL$76 = new QName("", "avgSubtotal");
        MAXSUBTOTAL$78 = new QName("", "maxSubtotal");
        MINSUBTOTAL$80 = new QName("", "minSubtotal");
        PRODUCTSUBTOTAL$82 = new QName("", "productSubtotal");
        COUNTSUBTOTAL$84 = new QName("", "countSubtotal");
        STDDEVSUBTOTAL$86 = new QName("", "stdDevSubtotal");
        STDDEVPSUBTOTAL$88 = new QName("", "stdDevPSubtotal");
        VARSUBTOTAL$90 = new QName("", "varSubtotal");
        VARPSUBTOTAL$92 = new QName("", "varPSubtotal");
        SHOWPROPCELL$94 = new QName("", "showPropCell");
        SHOWPROPTIP$96 = new QName("", "showPropTip");
        SHOWPROPASCAPTION$98 = new QName("", "showPropAsCaption");
        DEFAULTATTRIBUTEDRILLSTATE$100 = new QName("", "defaultAttributeDrillState");
    }
}
