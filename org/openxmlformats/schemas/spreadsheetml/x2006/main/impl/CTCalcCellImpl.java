package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcCell;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCalcCellImpl extends XmlComplexContentImpl implements CTCalcCell
{
    private static final long serialVersionUID = 1L;
    private static final QName R$0;
    private static final QName I$2;
    private static final QName S$4;
    private static final QName L$6;
    private static final QName T$8;
    private static final QName A$10;
    
    public CTCalcCellImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.R$0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCellRef xgetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellRef)this.get_store().find_attribute_user(CTCalcCellImpl.R$0);
        }
    }
    
    public void setR(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.R$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcCellImpl.R$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetR(final STCellRef stCellRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellRef stCellRef2 = (STCellRef)this.get_store().find_attribute_user(CTCalcCellImpl.R$0);
            if (stCellRef2 == null) {
                stCellRef2 = (STCellRef)this.get_store().add_attribute_user(CTCalcCellImpl.R$0);
            }
            stCellRef2.set((XmlObject)stCellRef);
        }
    }
    
    public int getI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.I$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcCellImpl.I$2);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTCalcCellImpl.I$2);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTCalcCellImpl.I$2);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcCellImpl.I$2) != null;
        }
    }
    
    public void setI(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.I$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcCellImpl.I$2);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetI(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTCalcCellImpl.I$2);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTCalcCellImpl.I$2);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcCellImpl.I$2);
        }
    }
    
    public boolean getS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.S$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcCellImpl.S$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcCellImpl.S$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcCellImpl.S$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcCellImpl.S$4) != null;
        }
    }
    
    public void setS(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.S$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcCellImpl.S$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetS(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcCellImpl.S$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcCellImpl.S$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcCellImpl.S$4);
        }
    }
    
    public boolean getL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.L$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcCellImpl.L$6);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcCellImpl.L$6);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcCellImpl.L$6);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcCellImpl.L$6) != null;
        }
    }
    
    public void setL(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.L$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcCellImpl.L$6);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetL(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcCellImpl.L$6);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcCellImpl.L$6);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcCellImpl.L$6);
        }
    }
    
    public boolean getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.T$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcCellImpl.T$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcCellImpl.T$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcCellImpl.T$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcCellImpl.T$8) != null;
        }
    }
    
    public void setT(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.T$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcCellImpl.T$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetT(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcCellImpl.T$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcCellImpl.T$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcCellImpl.T$8);
        }
    }
    
    public boolean getA() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.A$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCalcCellImpl.A$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetA() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCalcCellImpl.A$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCalcCellImpl.A$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetA() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCalcCellImpl.A$10) != null;
        }
    }
    
    public void setA(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCalcCellImpl.A$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCalcCellImpl.A$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetA(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCalcCellImpl.A$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCalcCellImpl.A$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetA() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCalcCellImpl.A$10);
        }
    }
    
    static {
        R$0 = new QName("", "r");
        I$2 = new QName("", "i");
        S$4 = new QName("", "s");
        L$6 = new QName("", "l");
        T$8 = new QName("", "t");
        A$10 = new QName("", "a");
    }
}
