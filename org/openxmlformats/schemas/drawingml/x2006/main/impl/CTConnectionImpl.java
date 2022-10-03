package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STDrawingElementId;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnection;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTConnectionImpl extends XmlComplexContentImpl implements CTConnection
{
    private static final long serialVersionUID = 1L;
    private static final QName ID$0;
    private static final QName IDX$2;
    
    public CTConnectionImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectionImpl.ID$0);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDrawingElementId xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDrawingElementId)this.get_store().find_attribute_user(CTConnectionImpl.ID$0);
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectionImpl.ID$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTConnectionImpl.ID$0);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final STDrawingElementId stDrawingElementId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDrawingElementId stDrawingElementId2 = (STDrawingElementId)this.get_store().find_attribute_user(CTConnectionImpl.ID$0);
            if (stDrawingElementId2 == null) {
                stDrawingElementId2 = (STDrawingElementId)this.get_store().add_attribute_user(CTConnectionImpl.ID$0);
            }
            stDrawingElementId2.set((XmlObject)stDrawingElementId);
        }
    }
    
    public long getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectionImpl.IDX$2);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTConnectionImpl.IDX$2);
        }
    }
    
    public void setIdx(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTConnectionImpl.IDX$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTConnectionImpl.IDX$2);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetIdx(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTConnectionImpl.IDX$2);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTConnectionImpl.IDX$2);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    static {
        ID$0 = new QName("", "id");
        IDX$2 = new QName("", "idx");
    }
}
