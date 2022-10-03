package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STPositivePercentage;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineJoinMiterProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLineJoinMiterPropertiesImpl extends XmlComplexContentImpl implements CTLineJoinMiterProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName LIM$0;
    
    public CTLineJoinMiterPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getLim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLineJoinMiterPropertiesImpl.LIM$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STPositivePercentage xgetLim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPositivePercentage)this.get_store().find_attribute_user(CTLineJoinMiterPropertiesImpl.LIM$0);
        }
    }
    
    public boolean isSetLim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTLineJoinMiterPropertiesImpl.LIM$0) != null;
        }
    }
    
    public void setLim(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTLineJoinMiterPropertiesImpl.LIM$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTLineJoinMiterPropertiesImpl.LIM$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetLim(final STPositivePercentage stPositivePercentage) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPositivePercentage stPositivePercentage2 = (STPositivePercentage)this.get_store().find_attribute_user(CTLineJoinMiterPropertiesImpl.LIM$0);
            if (stPositivePercentage2 == null) {
                stPositivePercentage2 = (STPositivePercentage)this.get_store().add_attribute_user(CTLineJoinMiterPropertiesImpl.LIM$0);
            }
            stPositivePercentage2.set((XmlObject)stPositivePercentage);
        }
    }
    
    public void unsetLim() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTLineJoinMiterPropertiesImpl.LIM$0);
        }
    }
    
    static {
        LIM$0 = new QName("", "lim");
    }
}
