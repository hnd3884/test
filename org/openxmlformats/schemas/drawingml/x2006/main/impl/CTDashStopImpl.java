package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTDashStop;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDashStopImpl extends XmlComplexContentImpl implements CTDashStop
{
    private static final long serialVersionUID = 1L;
    private static final QName D$0;
    private static final QName SP$2;
    
    public CTDashStopImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getD() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDashStopImpl.D$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositivePercentage xgetD() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositivePercentage)this.get_store().find_attribute_user(CTDashStopImpl.D$0);
        }
    }
    
    public void setD(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDashStopImpl.D$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDashStopImpl.D$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetD(final STPositivePercentage stPositivePercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositivePercentage stPositivePercentage2 = (STPositivePercentage)this.get_store().find_attribute_user(CTDashStopImpl.D$0);
            if (stPositivePercentage2 == null) {
                stPositivePercentage2 = (STPositivePercentage)this.get_store().add_attribute_user(CTDashStopImpl.D$0);
            }
            stPositivePercentage2.set((XmlObject)stPositivePercentage);
        }
    }
    
    public int getSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDashStopImpl.SP$2);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositivePercentage xgetSp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositivePercentage)this.get_store().find_attribute_user(CTDashStopImpl.SP$2);
        }
    }
    
    public void setSp(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTDashStopImpl.SP$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTDashStopImpl.SP$2);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSp(final STPositivePercentage stPositivePercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositivePercentage stPositivePercentage2 = (STPositivePercentage)this.get_store().find_attribute_user(CTDashStopImpl.SP$2);
            if (stPositivePercentage2 == null) {
                stPositivePercentage2 = (STPositivePercentage)this.get_store().add_attribute_user(CTDashStopImpl.SP$2);
            }
            stPositivePercentage2.set((XmlObject)stPositivePercentage);
        }
    }
    
    static {
        D$0 = new QName("", "d");
        SP$2 = new QName("", "sp");
    }
}
