package org.apache.xmlbeans.impl.xb.ltgfmt.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CodeImpl extends XmlComplexContentImpl implements Code
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    
    public CodeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public String getID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(CodeImpl.ID$0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    @Override
    public XmlToken xgetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(CodeImpl.ID$0);
            return target;
        }
    }
    
    @Override
    public boolean isSetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CodeImpl.ID$0) != null;
        }
    }
    
    @Override
    public void setID(final String id) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(CodeImpl.ID$0);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(CodeImpl.ID$0);
            }
            target.setStringValue(id);
        }
    }
    
    @Override
    public void xsetID(final XmlToken id) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlToken target = null;
            target = (XmlToken)this.get_store().find_attribute_user(CodeImpl.ID$0);
            if (target == null) {
                target = (XmlToken)this.get_store().add_attribute_user(CodeImpl.ID$0);
            }
            target.set(id);
        }
    }
    
    @Override
    public void unsetID() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CodeImpl.ID$0);
        }
    }
    
    static {
        ID$0 = new QName("", "ID");
    }
}
