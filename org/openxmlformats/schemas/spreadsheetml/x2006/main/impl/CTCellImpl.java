package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellType;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCell;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCellImpl extends XmlComplexContentImpl implements CTCell
{
    private static final long serialVersionUID = 1L;
    private static final QName F$0;
    private static final QName V$2;
    private static final QName IS$4;
    private static final QName EXTLST$6;
    private static final QName R$8;
    private static final QName S$10;
    private static final QName T$12;
    private static final QName CM$14;
    private static final QName VM$16;
    private static final QName PH$18;
    
    public CTCellImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTCellFormula getF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellFormula ctCellFormula = (CTCellFormula)this.get_store().find_element_user(CTCellImpl.F$0, 0);
            if (ctCellFormula == null) {
                return null;
            }
            return ctCellFormula;
        }
    }
    
    public boolean isSetF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCellImpl.F$0) != 0;
        }
    }
    
    public void setF(final CTCellFormula ctCellFormula) {
        this.generatedSetterHelperImpl((XmlObject)ctCellFormula, CTCellImpl.F$0, 0, (short)1);
    }
    
    public CTCellFormula addNewF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellFormula)this.get_store().add_element_user(CTCellImpl.F$0);
        }
    }
    
    public void unsetF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCellImpl.F$0, 0);
        }
    }
    
    public String getV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTCellImpl.V$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_element_user(CTCellImpl.V$2, 0);
        }
    }
    
    public boolean isSetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCellImpl.V$2) != 0;
        }
    }
    
    public void setV(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTCellImpl.V$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTCellImpl.V$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetV(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_element_user(CTCellImpl.V$2, 0);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_element_user(CTCellImpl.V$2);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetV() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCellImpl.V$2, 0);
        }
    }
    
    public CTRst getIs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRst ctRst = (CTRst)this.get_store().find_element_user(CTCellImpl.IS$4, 0);
            if (ctRst == null) {
                return null;
            }
            return ctRst;
        }
    }
    
    public boolean isSetIs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCellImpl.IS$4) != 0;
        }
    }
    
    public void setIs(final CTRst ctRst) {
        this.generatedSetterHelperImpl((XmlObject)ctRst, CTCellImpl.IS$4, 0, (short)1);
    }
    
    public CTRst addNewIs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRst)this.get_store().add_element_user(CTCellImpl.IS$4);
        }
    }
    
    public void unsetIs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCellImpl.IS$4, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCellImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCellImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCellImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCellImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCellImpl.EXTLST$6, 0);
        }
    }
    
    public String getR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.R$8);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCellRef xgetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellRef)this.get_store().find_attribute_user(CTCellImpl.R$8);
        }
    }
    
    public boolean isSetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellImpl.R$8) != null;
        }
    }
    
    public void setR(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.R$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellImpl.R$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetR(final STCellRef stCellRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellRef stCellRef2 = (STCellRef)this.get_store().find_attribute_user(CTCellImpl.R$8);
            if (stCellRef2 == null) {
                stCellRef2 = (STCellRef)this.get_store().add_attribute_user(CTCellImpl.R$8);
            }
            stCellRef2.set((XmlObject)stCellRef);
        }
    }
    
    public void unsetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellImpl.R$8);
        }
    }
    
    public long getS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.S$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellImpl.S$10);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellImpl.S$10);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTCellImpl.S$10);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellImpl.S$10) != null;
        }
    }
    
    public void setS(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.S$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellImpl.S$10);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetS(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellImpl.S$10);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCellImpl.S$10);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellImpl.S$10);
        }
    }
    
    public STCellType.Enum getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.T$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellImpl.T$12);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STCellType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCellType xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellType stCellType = (STCellType)this.get_store().find_attribute_user(CTCellImpl.T$12);
            if (stCellType == null) {
                stCellType = (STCellType)this.get_default_attribute_value(CTCellImpl.T$12);
            }
            return stCellType;
        }
    }
    
    public boolean isSetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellImpl.T$12) != null;
        }
    }
    
    public void setT(final STCellType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.T$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellImpl.T$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetT(final STCellType stCellType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellType stCellType2 = (STCellType)this.get_store().find_attribute_user(CTCellImpl.T$12);
            if (stCellType2 == null) {
                stCellType2 = (STCellType)this.get_store().add_attribute_user(CTCellImpl.T$12);
            }
            stCellType2.set((XmlObject)stCellType);
        }
    }
    
    public void unsetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellImpl.T$12);
        }
    }
    
    public long getCm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.CM$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellImpl.CM$14);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellImpl.CM$14);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTCellImpl.CM$14);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetCm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellImpl.CM$14) != null;
        }
    }
    
    public void setCm(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.CM$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellImpl.CM$14);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCm(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellImpl.CM$14);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCellImpl.CM$14);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellImpl.CM$14);
        }
    }
    
    public long getVm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.VM$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellImpl.VM$16);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetVm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellImpl.VM$16);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTCellImpl.VM$16);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetVm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellImpl.VM$16) != null;
        }
    }
    
    public void setVm(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.VM$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellImpl.VM$16);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetVm(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellImpl.VM$16);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCellImpl.VM$16);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetVm() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellImpl.VM$16);
        }
    }
    
    public boolean getPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.PH$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellImpl.PH$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCellImpl.PH$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCellImpl.PH$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellImpl.PH$18) != null;
        }
    }
    
    public void setPh(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellImpl.PH$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellImpl.PH$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPh(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellImpl.PH$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellImpl.PH$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPh() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellImpl.PH$18);
        }
    }
    
    static {
        F$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "f");
        V$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "v");
        IS$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "is");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        R$8 = new QName("", "r");
        S$10 = new QName("", "s");
        T$12 = new QName("", "t");
        CM$14 = new QName("", "cm");
        VM$16 = new QName("", "vm");
        PH$18 = new QName("", "ph");
    }
}
