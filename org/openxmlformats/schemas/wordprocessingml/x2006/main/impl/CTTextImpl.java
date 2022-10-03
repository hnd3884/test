package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class CTTextImpl extends JavaStringHolderEx implements CTText
{
    private static final long serialVersionUID = 1L;
    private static final QName SPACE$0;
    
    public CTTextImpl(final SchemaType schemaType) {
        super(schemaType, true);
    }
    
    protected CTTextImpl(final SchemaType schemaType, final boolean b) {
        super(schemaType, b);
    }
    
    public SpaceAttribute.Space.Enum getSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextImpl.SPACE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextImpl.SPACE$0);
            }
            if (simpleValue == null) {
                return null;
            }
            return (SpaceAttribute.Space.Enum)simpleValue.getEnumValue();
        }
    }
    
    public SpaceAttribute.Space xgetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SpaceAttribute.Space space = (SpaceAttribute.Space)this.get_store().find_attribute_user(CTTextImpl.SPACE$0);
            if (space == null) {
                space = (SpaceAttribute.Space)this.get_default_attribute_value(CTTextImpl.SPACE$0);
            }
            return space;
        }
    }
    
    public boolean isSetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextImpl.SPACE$0) != null;
        }
    }
    
    public void setSpace(final SpaceAttribute.Space.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextImpl.SPACE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextImpl.SPACE$0);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetSpace(final SpaceAttribute.Space space) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SpaceAttribute.Space space2 = (SpaceAttribute.Space)this.get_store().find_attribute_user(CTTextImpl.SPACE$0);
            if (space2 == null) {
                space2 = (SpaceAttribute.Space)this.get_store().add_attribute_user(CTTextImpl.SPACE$0);
            }
            space2.set((XmlObject)space);
        }
    }
    
    public void unsetSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextImpl.SPACE$0);
        }
    }
    
    static {
        SPACE$0 = new QName("http://www.w3.org/XML/1998/namespace", "space");
    }
}
