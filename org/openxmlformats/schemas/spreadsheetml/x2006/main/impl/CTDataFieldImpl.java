package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;
import org.apache.xmlbeans.XmlInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STShowDataAs;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataConsolidateFunction;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataField;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDataFieldImpl extends XmlComplexContentImpl implements CTDataField
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName NAME$2;
    private static final QName FLD$4;
    private static final QName SUBTOTAL$6;
    private static final QName SHOWDATAAS$8;
    private static final QName BASEFIELD$10;
    private static final QName BASEITEM$12;
    private static final QName NUMFMTID$14;
    
    public CTDataFieldImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTDataFieldImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDataFieldImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTDataFieldImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTDataFieldImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDataFieldImpl.EXTLST$0, 0);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.NAME$2);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTDataFieldImpl.NAME$2);
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataFieldImpl.NAME$2) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.NAME$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataFieldImpl.NAME$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTDataFieldImpl.NAME$2);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTDataFieldImpl.NAME$2);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataFieldImpl.NAME$2);
        }
    }
    
    public long getFld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.FLD$4);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetFld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataFieldImpl.FLD$4);
        }
    }
    
    public void setFld(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.FLD$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataFieldImpl.FLD$4);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetFld(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataFieldImpl.FLD$4);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDataFieldImpl.FLD$4);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public STDataConsolidateFunction.Enum getSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.SUBTOTAL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataFieldImpl.SUBTOTAL$6);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STDataConsolidateFunction.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STDataConsolidateFunction xgetSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataConsolidateFunction stDataConsolidateFunction = (STDataConsolidateFunction)this.get_store().find_attribute_user(CTDataFieldImpl.SUBTOTAL$6);
            if (stDataConsolidateFunction == null) {
                stDataConsolidateFunction = (STDataConsolidateFunction)this.get_default_attribute_value(CTDataFieldImpl.SUBTOTAL$6);
            }
            return stDataConsolidateFunction;
        }
    }
    
    public boolean isSetSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataFieldImpl.SUBTOTAL$6) != null;
        }
    }
    
    public void setSubtotal(final STDataConsolidateFunction.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.SUBTOTAL$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataFieldImpl.SUBTOTAL$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSubtotal(final STDataConsolidateFunction stDataConsolidateFunction) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDataConsolidateFunction stDataConsolidateFunction2 = (STDataConsolidateFunction)this.get_store().find_attribute_user(CTDataFieldImpl.SUBTOTAL$6);
            if (stDataConsolidateFunction2 == null) {
                stDataConsolidateFunction2 = (STDataConsolidateFunction)this.get_store().add_attribute_user(CTDataFieldImpl.SUBTOTAL$6);
            }
            stDataConsolidateFunction2.set((XmlObject)stDataConsolidateFunction);
        }
    }
    
    public void unsetSubtotal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataFieldImpl.SUBTOTAL$6);
        }
    }
    
    public STShowDataAs.Enum getShowDataAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.SHOWDATAAS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataFieldImpl.SHOWDATAAS$8);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STShowDataAs.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STShowDataAs xgetShowDataAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShowDataAs stShowDataAs = (STShowDataAs)this.get_store().find_attribute_user(CTDataFieldImpl.SHOWDATAAS$8);
            if (stShowDataAs == null) {
                stShowDataAs = (STShowDataAs)this.get_default_attribute_value(CTDataFieldImpl.SHOWDATAAS$8);
            }
            return stShowDataAs;
        }
    }
    
    public boolean isSetShowDataAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataFieldImpl.SHOWDATAAS$8) != null;
        }
    }
    
    public void setShowDataAs(final STShowDataAs.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.SHOWDATAAS$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataFieldImpl.SHOWDATAAS$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetShowDataAs(final STShowDataAs stShowDataAs) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STShowDataAs stShowDataAs2 = (STShowDataAs)this.get_store().find_attribute_user(CTDataFieldImpl.SHOWDATAAS$8);
            if (stShowDataAs2 == null) {
                stShowDataAs2 = (STShowDataAs)this.get_store().add_attribute_user(CTDataFieldImpl.SHOWDATAAS$8);
            }
            stShowDataAs2.set((XmlObject)stShowDataAs);
        }
    }
    
    public void unsetShowDataAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataFieldImpl.SHOWDATAAS$8);
        }
    }
    
    public int getBaseField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.BASEFIELD$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataFieldImpl.BASEFIELD$10);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetBaseField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTDataFieldImpl.BASEFIELD$10);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTDataFieldImpl.BASEFIELD$10);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetBaseField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataFieldImpl.BASEFIELD$10) != null;
        }
    }
    
    public void setBaseField(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.BASEFIELD$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataFieldImpl.BASEFIELD$10);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetBaseField(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTDataFieldImpl.BASEFIELD$10);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTDataFieldImpl.BASEFIELD$10);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetBaseField() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataFieldImpl.BASEFIELD$10);
        }
    }
    
    public long getBaseItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.BASEITEM$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTDataFieldImpl.BASEITEM$12);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetBaseItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataFieldImpl.BASEITEM$12);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTDataFieldImpl.BASEITEM$12);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetBaseItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataFieldImpl.BASEITEM$12) != null;
        }
    }
    
    public void setBaseItem(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.BASEITEM$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataFieldImpl.BASEITEM$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetBaseItem(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTDataFieldImpl.BASEITEM$12);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTDataFieldImpl.BASEITEM$12);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetBaseItem() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataFieldImpl.BASEITEM$12);
        }
    }
    
    public long getNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.NUMFMTID$14);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STNumFmtId xgetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STNumFmtId)this.get_store().find_attribute_user(CTDataFieldImpl.NUMFMTID$14);
        }
    }
    
    public boolean isSetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTDataFieldImpl.NUMFMTID$14) != null;
        }
    }
    
    public void setNumFmtId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDataFieldImpl.NUMFMTID$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDataFieldImpl.NUMFMTID$14);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetNumFmtId(final STNumFmtId stNumFmtId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STNumFmtId stNumFmtId2 = (STNumFmtId)this.get_store().find_attribute_user(CTDataFieldImpl.NUMFMTID$14);
            if (stNumFmtId2 == null) {
                stNumFmtId2 = (STNumFmtId)this.get_store().add_attribute_user(CTDataFieldImpl.NUMFMTID$14);
            }
            stNumFmtId2.set((XmlObject)stNumFmtId);
        }
    }
    
    public void unsetNumFmtId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTDataFieldImpl.NUMFMTID$14);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        NAME$2 = new QName("", "name");
        FLD$4 = new QName("", "fld");
        SUBTOTAL$6 = new QName("", "subtotal");
        SHOWDATAAS$8 = new QName("", "showDataAs");
        BASEFIELD$10 = new QName("", "baseField");
        BASEITEM$12 = new QName("", "baseItem");
        NUMFMTID$14 = new QName("", "numFmtId");
    }
}
