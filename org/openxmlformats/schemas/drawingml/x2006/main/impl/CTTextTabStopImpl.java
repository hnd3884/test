package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextTabAlignType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextTabStop;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextTabStopImpl extends XmlComplexContentImpl implements CTTextTabStop
{
    private static final long serialVersionUID = 1L;
    private static final QName POS$0;
    private static final QName ALGN$2;
    
    public CTTextTabStopImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextTabStopImpl.POS$0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STCoordinate32 xgetPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCoordinate32)this.get_store().find_attribute_user(CTTextTabStopImpl.POS$0);
        }
    }
    
    public boolean isSetPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextTabStopImpl.POS$0) != null;
        }
    }
    
    public void setPos(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextTabStopImpl.POS$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextTabStopImpl.POS$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetPos(final STCoordinate32 stCoordinate32) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCoordinate32 stCoordinate33 = (STCoordinate32)this.get_store().find_attribute_user(CTTextTabStopImpl.POS$0);
            if (stCoordinate33 == null) {
                stCoordinate33 = (STCoordinate32)this.get_store().add_attribute_user(CTTextTabStopImpl.POS$0);
            }
            stCoordinate33.set((XmlObject)stCoordinate32);
        }
    }
    
    public void unsetPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextTabStopImpl.POS$0);
        }
    }
    
    public STTextTabAlignType.Enum getAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextTabStopImpl.ALGN$2);
            if (simpleValue == null) {
                return null;
            }
            return (STTextTabAlignType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTextTabAlignType xgetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTextTabAlignType)this.get_store().find_attribute_user(CTTextTabStopImpl.ALGN$2);
        }
    }
    
    public boolean isSetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextTabStopImpl.ALGN$2) != null;
        }
    }
    
    public void setAlgn(final STTextTabAlignType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextTabStopImpl.ALGN$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextTabStopImpl.ALGN$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAlgn(final STTextTabAlignType stTextTabAlignType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextTabAlignType stTextTabAlignType2 = (STTextTabAlignType)this.get_store().find_attribute_user(CTTextTabStopImpl.ALGN$2);
            if (stTextTabAlignType2 == null) {
                stTextTabAlignType2 = (STTextTabAlignType)this.get_store().add_attribute_user(CTTextTabStopImpl.ALGN$2);
            }
            stTextTabAlignType2.set((XmlObject)stTextTabAlignType);
        }
    }
    
    public void unsetAlgn() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextTabStopImpl.ALGN$2);
        }
    }
    
    static {
        POS$0 = new QName("", "pos");
        ALGN$2 = new QName("", "algn");
    }
}
