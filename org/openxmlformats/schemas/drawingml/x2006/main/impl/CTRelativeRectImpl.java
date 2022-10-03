package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRelativeRectImpl extends XmlComplexContentImpl implements CTRelativeRect
{
    private static final long serialVersionUID = 1L;
    private static final QName L$0;
    private static final QName T$2;
    private static final QName R$4;
    private static final QName B$6;
    
    public CTRelativeRectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelativeRectImpl.L$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRelativeRectImpl.L$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage = (STPercentage)this.get_store().find_attribute_user(CTRelativeRectImpl.L$0);
            if (stPercentage == null) {
                stPercentage = (STPercentage)this.get_default_attribute_value(CTRelativeRectImpl.L$0);
            }
            return stPercentage;
        }
    }
    
    public boolean isSetL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRelativeRectImpl.L$0) != null;
        }
    }
    
    public void setL(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelativeRectImpl.L$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRelativeRectImpl.L$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetL(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTRelativeRectImpl.L$0);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTRelativeRectImpl.L$0);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetL() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRelativeRectImpl.L$0);
        }
    }
    
    public int getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelativeRectImpl.T$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRelativeRectImpl.T$2);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage = (STPercentage)this.get_store().find_attribute_user(CTRelativeRectImpl.T$2);
            if (stPercentage == null) {
                stPercentage = (STPercentage)this.get_default_attribute_value(CTRelativeRectImpl.T$2);
            }
            return stPercentage;
        }
    }
    
    public boolean isSetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRelativeRectImpl.T$2) != null;
        }
    }
    
    public void setT(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelativeRectImpl.T$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRelativeRectImpl.T$2);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetT(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTRelativeRectImpl.T$2);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTRelativeRectImpl.T$2);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRelativeRectImpl.T$2);
        }
    }
    
    public int getR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelativeRectImpl.R$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRelativeRectImpl.R$4);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage = (STPercentage)this.get_store().find_attribute_user(CTRelativeRectImpl.R$4);
            if (stPercentage == null) {
                stPercentage = (STPercentage)this.get_default_attribute_value(CTRelativeRectImpl.R$4);
            }
            return stPercentage;
        }
    }
    
    public boolean isSetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRelativeRectImpl.R$4) != null;
        }
    }
    
    public void setR(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelativeRectImpl.R$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRelativeRectImpl.R$4);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetR(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTRelativeRectImpl.R$4);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTRelativeRectImpl.R$4);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRelativeRectImpl.R$4);
        }
    }
    
    public int getB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelativeRectImpl.B$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTRelativeRectImpl.B$6);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPercentage xgetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage = (STPercentage)this.get_store().find_attribute_user(CTRelativeRectImpl.B$6);
            if (stPercentage == null) {
                stPercentage = (STPercentage)this.get_default_attribute_value(CTRelativeRectImpl.B$6);
            }
            return stPercentage;
        }
    }
    
    public boolean isSetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTRelativeRectImpl.B$6) != null;
        }
    }
    
    public void setB(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTRelativeRectImpl.B$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTRelativeRectImpl.B$6);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetB(final STPercentage stPercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPercentage stPercentage2 = (STPercentage)this.get_store().find_attribute_user(CTRelativeRectImpl.B$6);
            if (stPercentage2 == null) {
                stPercentage2 = (STPercentage)this.get_store().add_attribute_user(CTRelativeRectImpl.B$6);
            }
            stPercentage2.set((XmlObject)stPercentage);
        }
    }
    
    public void unsetB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTRelativeRectImpl.B$6);
        }
    }
    
    static {
        L$0 = new QName("", "l");
        T$2 = new QName("", "t");
        R$4 = new QName("", "r");
        B$6 = new QName("", "b");
    }
}
