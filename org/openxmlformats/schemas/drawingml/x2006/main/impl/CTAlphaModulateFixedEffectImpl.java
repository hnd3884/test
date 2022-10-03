package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateFixedEffect;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAlphaModulateFixedEffectImpl extends XmlComplexContentImpl implements CTAlphaModulateFixedEffect
{
    private static final long serialVersionUID = 1L;
    private static final QName AMT$0;
    
    public CTAlphaModulateFixedEffectImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getAmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAlphaModulateFixedEffectImpl.AMT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTAlphaModulateFixedEffectImpl.AMT$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositivePercentage xgetAmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositivePercentage stPositivePercentage = (STPositivePercentage)this.get_store().find_attribute_user(CTAlphaModulateFixedEffectImpl.AMT$0);
            if (stPositivePercentage == null) {
                stPositivePercentage = (STPositivePercentage)this.get_default_attribute_value(CTAlphaModulateFixedEffectImpl.AMT$0);
            }
            return stPositivePercentage;
        }
    }
    
    public boolean isSetAmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTAlphaModulateFixedEffectImpl.AMT$0) != null;
        }
    }
    
    public void setAmt(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTAlphaModulateFixedEffectImpl.AMT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTAlphaModulateFixedEffectImpl.AMT$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetAmt(final STPositivePercentage stPositivePercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositivePercentage stPositivePercentage2 = (STPositivePercentage)this.get_store().find_attribute_user(CTAlphaModulateFixedEffectImpl.AMT$0);
            if (stPositivePercentage2 == null) {
                stPositivePercentage2 = (STPositivePercentage)this.get_store().add_attribute_user(CTAlphaModulateFixedEffectImpl.AMT$0);
            }
            stPositivePercentage2.set((XmlObject)stPositivePercentage);
        }
    }
    
    public void unsetAmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTAlphaModulateFixedEffectImpl.AMT$0);
        }
    }
    
    static {
        AMT$0 = new QName("", "amt");
    }
}
