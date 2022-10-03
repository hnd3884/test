package org.apache.xmlbeans.impl.xb.xmlschema.impl;

import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SpaceAttributeImpl extends XmlComplexContentImpl implements SpaceAttribute
{
    private static final long serialVersionUID = 1L;
    private static final QName SPACE$0;
    
    public SpaceAttributeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Space.Enum getSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(SpaceAttributeImpl.SPACE$0);
            if (target == null) {
                target = (SimpleValue)this.get_default_attribute_value(SpaceAttributeImpl.SPACE$0);
            }
            if (target == null) {
                return null;
            }
            return (Space.Enum)target.getEnumValue();
        }
    }
    
    @Override
    public Space xgetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Space target = null;
            target = (Space)this.get_store().find_attribute_user(SpaceAttributeImpl.SPACE$0);
            if (target == null) {
                target = (Space)this.get_default_attribute_value(SpaceAttributeImpl.SPACE$0);
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(SpaceAttributeImpl.SPACE$0) != null;
        }
    }
    
    @Override
    public void setSpace(final Space.Enum space) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(SpaceAttributeImpl.SPACE$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(SpaceAttributeImpl.SPACE$0);
            }
            target.setEnumValue(space);
        }
    }
    
    @Override
    public void xsetSpace(final Space space) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Space target = null;
            target = (Space)this.get_store().find_attribute_user(SpaceAttributeImpl.SPACE$0);
            if (target == null) {
                target = (Space)this.get_store().add_attribute_user(SpaceAttributeImpl.SPACE$0);
            }
            target.set(space);
        }
    }
    
    @Override
    public void unsetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(SpaceAttributeImpl.SPACE$0);
        }
    }
    
    static {
        SPACE$0 = new QName("http://www.w3.org/XML/1998/namespace", "space");
    }
    
    public static class SpaceImpl extends JavaStringEnumerationHolderEx implements Space
    {
        private static final long serialVersionUID = 1L;
        
        public SpaceImpl(final SchemaType sType) {
            super(sType, false);
        }
        
        protected SpaceImpl(final SchemaType sType, final boolean b) {
            super(sType, b);
        }
    }
}
