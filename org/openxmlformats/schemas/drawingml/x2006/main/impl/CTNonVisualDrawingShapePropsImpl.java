package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeLocking;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingShapeProps;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNonVisualDrawingShapePropsImpl extends XmlComplexContentImpl implements CTNonVisualDrawingShapeProps
{
    private static final long serialVersionUID = 1L;
    private static final QName SPLOCKS$0;
    private static final QName EXTLST$2;
    private static final QName TXBOX$4;
    
    public CTNonVisualDrawingShapePropsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTShapeLocking getSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeLocking ctShapeLocking = (CTShapeLocking)this.get_store().find_element_user(CTNonVisualDrawingShapePropsImpl.SPLOCKS$0, 0);
            if (ctShapeLocking == null) {
                return null;
            }
            return ctShapeLocking;
        }
    }
    
    public boolean isSetSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualDrawingShapePropsImpl.SPLOCKS$0) != 0;
        }
    }
    
    public void setSpLocks(final CTShapeLocking ctShapeLocking) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeLocking, CTNonVisualDrawingShapePropsImpl.SPLOCKS$0, 0, (short)1);
    }
    
    public CTShapeLocking addNewSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeLocking)this.get_store().add_element_user(CTNonVisualDrawingShapePropsImpl.SPLOCKS$0);
        }
    }
    
    public void unsetSpLocks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualDrawingShapePropsImpl.SPLOCKS$0, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTNonVisualDrawingShapePropsImpl.EXTLST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNonVisualDrawingShapePropsImpl.EXTLST$2) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTNonVisualDrawingShapePropsImpl.EXTLST$2, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTNonVisualDrawingShapePropsImpl.EXTLST$2);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNonVisualDrawingShapePropsImpl.EXTLST$2, 0);
        }
    }
    
    public boolean getTxBox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTxBox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTxBox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTNonVisualDrawingShapePropsImpl.TXBOX$4) != null;
        }
    }
    
    public void setTxBox(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTxBox(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTxBox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTNonVisualDrawingShapePropsImpl.TXBOX$4);
        }
    }
    
    static {
        SPLOCKS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "spLocks");
        EXTLST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        TXBOX$4 = new QName("", "txBox");
    }
}
