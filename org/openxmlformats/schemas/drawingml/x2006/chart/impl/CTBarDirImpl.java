package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarDir;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarDir;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBarDirImpl extends XmlComplexContentImpl implements CTBarDir
{
    private static final long serialVersionUID = 1L;
    private static final QName VAL$0;
    
    public CTBarDirImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STBarDir.Enum getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBarDirImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBarDirImpl.VAL$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STBarDir.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBarDir xgetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBarDir stBarDir = (STBarDir)this.get_store().find_attribute_user(CTBarDirImpl.VAL$0);
            if (stBarDir == null) {
                stBarDir = (STBarDir)this.get_default_attribute_value(CTBarDirImpl.VAL$0);
            }
            return stBarDir;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBarDirImpl.VAL$0) != null;
        }
    }
    
    public void setVal(final STBarDir.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBarDirImpl.VAL$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBarDirImpl.VAL$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetVal(final STBarDir stBarDir) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBarDir stBarDir2 = (STBarDir)this.get_store().find_attribute_user(CTBarDirImpl.VAL$0);
            if (stBarDir2 == null) {
                stBarDir2 = (STBarDir)this.get_store().add_attribute_user(CTBarDirImpl.VAL$0);
            }
            stBarDir2.set((XmlObject)stBarDir);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBarDirImpl.VAL$0);
        }
    }
    
    static {
        VAL$0 = new QName("", "val");
    }
}
