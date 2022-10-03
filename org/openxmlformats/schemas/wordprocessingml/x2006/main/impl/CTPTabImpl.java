package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabLeader;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabRelativeTo;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPTabAlignment;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPTab;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPTabImpl extends XmlComplexContentImpl implements CTPTab
{
    private static final long serialVersionUID = 1L;
    private static final QName ALIGNMENT$0;
    private static final QName RELATIVETO$2;
    private static final QName LEADER$4;
    
    public CTPTabImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public STPTabAlignment.Enum getAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPTabImpl.ALIGNMENT$0);
            if (simpleValue == null) {
                return null;
            }
            return (STPTabAlignment.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPTabAlignment xgetAlignment() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPTabAlignment)this.get_store().find_attribute_user(CTPTabImpl.ALIGNMENT$0);
        }
    }
    
    public void setAlignment(final STPTabAlignment.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPTabImpl.ALIGNMENT$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPTabImpl.ALIGNMENT$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAlignment(final STPTabAlignment stpTabAlignment) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPTabAlignment stpTabAlignment2 = (STPTabAlignment)this.get_store().find_attribute_user(CTPTabImpl.ALIGNMENT$0);
            if (stpTabAlignment2 == null) {
                stpTabAlignment2 = (STPTabAlignment)this.get_store().add_attribute_user(CTPTabImpl.ALIGNMENT$0);
            }
            stpTabAlignment2.set((XmlObject)stpTabAlignment);
        }
    }
    
    public STPTabRelativeTo.Enum getRelativeTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPTabImpl.RELATIVETO$2);
            if (simpleValue == null) {
                return null;
            }
            return (STPTabRelativeTo.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPTabRelativeTo xgetRelativeTo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPTabRelativeTo)this.get_store().find_attribute_user(CTPTabImpl.RELATIVETO$2);
        }
    }
    
    public void setRelativeTo(final STPTabRelativeTo.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPTabImpl.RELATIVETO$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPTabImpl.RELATIVETO$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetRelativeTo(final STPTabRelativeTo stpTabRelativeTo) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPTabRelativeTo stpTabRelativeTo2 = (STPTabRelativeTo)this.get_store().find_attribute_user(CTPTabImpl.RELATIVETO$2);
            if (stpTabRelativeTo2 == null) {
                stpTabRelativeTo2 = (STPTabRelativeTo)this.get_store().add_attribute_user(CTPTabImpl.RELATIVETO$2);
            }
            stpTabRelativeTo2.set((XmlObject)stpTabRelativeTo);
        }
    }
    
    public STPTabLeader.Enum getLeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPTabImpl.LEADER$4);
            if (simpleValue == null) {
                return null;
            }
            return (STPTabLeader.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STPTabLeader xgetLeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STPTabLeader)this.get_store().find_attribute_user(CTPTabImpl.LEADER$4);
        }
    }
    
    public void setLeader(final STPTabLeader.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTPTabImpl.LEADER$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTPTabImpl.LEADER$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetLeader(final STPTabLeader stpTabLeader) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STPTabLeader stpTabLeader2 = (STPTabLeader)this.get_store().find_attribute_user(CTPTabImpl.LEADER$4);
            if (stpTabLeader2 == null) {
                stpTabLeader2 = (STPTabLeader)this.get_store().add_attribute_user(CTPTabImpl.LEADER$4);
            }
            stpTabLeader2.set((XmlObject)stpTabLeader);
        }
    }
    
    static {
        ALIGNMENT$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alignment");
        RELATIVETO$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "relativeTo");
        LEADER$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "leader");
    }
}
