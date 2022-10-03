package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellFormulaType;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellFormula;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class CTCellFormulaImpl extends JavaStringHolderEx implements CTCellFormula
{
    private static final long serialVersionUID = 1L;
    private static final QName T$0;
    private static final QName ACA$2;
    private static final QName REF$4;
    private static final QName DT2D$6;
    private static final QName DTR$8;
    private static final QName DEL1$10;
    private static final QName DEL2$12;
    private static final QName R1$14;
    private static final QName R2$16;
    private static final QName CA$18;
    private static final QName SI$20;
    private static final QName BX$22;
    
    public CTCellFormulaImpl(final SchemaType schemaType) {
        super(schemaType, true);
    }
    
    protected CTCellFormulaImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
    
    public STCellFormulaType.Enum getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.T$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellFormulaImpl.T$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STCellFormulaType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCellFormulaType xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellFormulaType stCellFormulaType = (STCellFormulaType)this.get_store().find_attribute_user(CTCellFormulaImpl.T$0);
            if (stCellFormulaType == null) {
                stCellFormulaType = (STCellFormulaType)this.get_default_attribute_value(CTCellFormulaImpl.T$0);
            }
            return stCellFormulaType;
        }
    }
    
    public boolean isSetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.T$0) != null;
        }
    }
    
    public void setT(final STCellFormulaType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.T$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.T$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetT(final STCellFormulaType stCellFormulaType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellFormulaType stCellFormulaType2 = (STCellFormulaType)this.get_store().find_attribute_user(CTCellFormulaImpl.T$0);
            if (stCellFormulaType2 == null) {
                stCellFormulaType2 = (STCellFormulaType)this.get_store().add_attribute_user(CTCellFormulaImpl.T$0);
            }
            stCellFormulaType2.set((XmlObject)stCellFormulaType);
        }
    }
    
    public void unsetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.T$0);
        }
    }
    
    public boolean getAca() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.ACA$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellFormulaImpl.ACA$2);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAca() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.ACA$2);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCellFormulaImpl.ACA$2);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAca() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.ACA$2) != null;
        }
    }
    
    public void setAca(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.ACA$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.ACA$2);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAca(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.ACA$2);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellFormulaImpl.ACA$2);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAca() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.ACA$2);
        }
    }
    
    public String getRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.REF$4);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STRef xgetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STRef)this.get_store().find_attribute_user(CTCellFormulaImpl.REF$4);
        }
    }
    
    public boolean isSetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.REF$4) != null;
        }
    }
    
    public void setRef(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.REF$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.REF$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetRef(final STRef stRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STRef stRef2 = (STRef)this.get_store().find_attribute_user(CTCellFormulaImpl.REF$4);
            if (stRef2 == null) {
                stRef2 = (STRef)this.get_store().add_attribute_user(CTCellFormulaImpl.REF$4);
            }
            stRef2.set((XmlObject)stRef);
        }
    }
    
    public void unsetRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.REF$4);
        }
    }
    
    public boolean getDt2D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.DT2D$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellFormulaImpl.DT2D$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDt2D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.DT2D$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCellFormulaImpl.DT2D$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDt2D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.DT2D$6) != null;
        }
    }
    
    public void setDt2D(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.DT2D$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.DT2D$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDt2D(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.DT2D$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellFormulaImpl.DT2D$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDt2D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.DT2D$6);
        }
    }
    
    public boolean getDtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.DTR$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellFormulaImpl.DTR$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.DTR$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCellFormulaImpl.DTR$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.DTR$8) != null;
        }
    }
    
    public void setDtr(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.DTR$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.DTR$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDtr(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.DTR$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellFormulaImpl.DTR$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDtr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.DTR$8);
        }
    }
    
    public boolean getDel1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.DEL1$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellFormulaImpl.DEL1$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDel1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.DEL1$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCellFormulaImpl.DEL1$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDel1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.DEL1$10) != null;
        }
    }
    
    public void setDel1(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.DEL1$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.DEL1$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDel1(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.DEL1$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellFormulaImpl.DEL1$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDel1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.DEL1$10);
        }
    }
    
    public boolean getDel2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.DEL2$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellFormulaImpl.DEL2$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDel2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.DEL2$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCellFormulaImpl.DEL2$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDel2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.DEL2$12) != null;
        }
    }
    
    public void setDel2(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.DEL2$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.DEL2$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDel2(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.DEL2$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellFormulaImpl.DEL2$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDel2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.DEL2$12);
        }
    }
    
    public String getR1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.R1$14);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCellRef xgetR1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellRef)this.get_store().find_attribute_user(CTCellFormulaImpl.R1$14);
        }
    }
    
    public boolean isSetR1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.R1$14) != null;
        }
    }
    
    public void setR1(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.R1$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.R1$14);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetR1(final STCellRef stCellRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellRef stCellRef2 = (STCellRef)this.get_store().find_attribute_user(CTCellFormulaImpl.R1$14);
            if (stCellRef2 == null) {
                stCellRef2 = (STCellRef)this.get_store().add_attribute_user(CTCellFormulaImpl.R1$14);
            }
            stCellRef2.set((XmlObject)stCellRef);
        }
    }
    
    public void unsetR1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.R1$14);
        }
    }
    
    public String getR2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.R2$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCellRef xgetR2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellRef)this.get_store().find_attribute_user(CTCellFormulaImpl.R2$16);
        }
    }
    
    public boolean isSetR2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.R2$16) != null;
        }
    }
    
    public void setR2(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.R2$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.R2$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetR2(final STCellRef stCellRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellRef stCellRef2 = (STCellRef)this.get_store().find_attribute_user(CTCellFormulaImpl.R2$16);
            if (stCellRef2 == null) {
                stCellRef2 = (STCellRef)this.get_store().add_attribute_user(CTCellFormulaImpl.R2$16);
            }
            stCellRef2.set((XmlObject)stCellRef);
        }
    }
    
    public void unsetR2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.R2$16);
        }
    }
    
    public boolean getCa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.CA$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellFormulaImpl.CA$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetCa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.CA$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCellFormulaImpl.CA$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetCa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.CA$18) != null;
        }
    }
    
    public void setCa(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.CA$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.CA$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetCa(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.CA$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellFormulaImpl.CA$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetCa() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.CA$18);
        }
    }
    
    public long getSi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.SI$20);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetSi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellFormulaImpl.SI$20);
        }
    }
    
    public boolean isSetSi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.SI$20) != null;
        }
    }
    
    public void setSi(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.SI$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.SI$20);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetSi(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCellFormulaImpl.SI$20);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCellFormulaImpl.SI$20);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetSi() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.SI$20);
        }
    }
    
    public boolean getBx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.BX$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCellFormulaImpl.BX$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.BX$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCellFormulaImpl.BX$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetBx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCellFormulaImpl.BX$22) != null;
        }
    }
    
    public void setBx(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCellFormulaImpl.BX$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCellFormulaImpl.BX$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBx(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCellFormulaImpl.BX$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCellFormulaImpl.BX$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCellFormulaImpl.BX$22);
        }
    }
    
    static {
        T$0 = new QName("", "t");
        ACA$2 = new QName("", "aca");
        REF$4 = new QName("", "ref");
        DT2D$6 = new QName("", "dt2D");
        DTR$8 = new QName("", "dtr");
        DEL1$10 = new QName("", "del1");
        DEL2$12 = new QName("", "del2");
        R1$14 = new QName("", "r1");
        R2$16 = new QName("", "r2");
        CA$18 = new QName("", "ca");
        SI$20 = new QName("", "si");
        BX$22 = new QName("", "bx");
    }
}
