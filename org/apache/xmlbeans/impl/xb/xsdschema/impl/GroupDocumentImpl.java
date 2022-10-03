package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class GroupDocumentImpl extends XmlComplexContentImpl implements GroupDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName GROUP$0;
    
    public GroupDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public NamedGroup getGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamedGroup target = null;
            target = (NamedGroup)this.get_store().find_element_user(GroupDocumentImpl.GROUP$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setGroup(final NamedGroup group) {
        this.generatedSetterHelperImpl(group, GroupDocumentImpl.GROUP$0, 0, (short)1);
    }
    
    @Override
    public NamedGroup addNewGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NamedGroup target = null;
            target = (NamedGroup)this.get_store().add_element_user(GroupDocumentImpl.GROUP$0);
            return target;
        }
    }
    
    static {
        GROUP$0 = new QName("http://www.w3.org/2001/XMLSchema", "group");
    }
}
