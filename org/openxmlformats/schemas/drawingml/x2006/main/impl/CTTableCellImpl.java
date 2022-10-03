package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCell;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableCellImpl extends XmlComplexContentImpl implements CTTableCell
{
    private static final long serialVersionUID = 1L;
    private static final QName TXBODY$0;
    private static final QName TCPR$2;
    private static final QName EXTLST$4;
    private static final QName ROWSPAN$6;
    private static final QName GRIDSPAN$8;
    private static final QName HMERGE$10;
    private static final QName VMERGE$12;
    
    public CTTableCellImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextBody getTxBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBody ctTextBody = (CTTextBody)this.get_store().find_element_user(CTTableCellImpl.TXBODY$0, 0);
            if (ctTextBody == null) {
                return null;
            }
            return ctTextBody;
        }
    }
    
    public boolean isSetTxBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellImpl.TXBODY$0) != 0;
        }
    }
    
    public void setTxBody(final CTTextBody ctTextBody) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBody, CTTableCellImpl.TXBODY$0, 0, (short)1);
    }
    
    public CTTextBody addNewTxBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBody)this.get_store().add_element_user(CTTableCellImpl.TXBODY$0);
        }
    }
    
    public void unsetTxBody() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellImpl.TXBODY$0, 0);
        }
    }
    
    public CTTableCellProperties getTcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableCellProperties ctTableCellProperties = (CTTableCellProperties)this.get_store().find_element_user(CTTableCellImpl.TCPR$2, 0);
            if (ctTableCellProperties == null) {
                return null;
            }
            return ctTableCellProperties;
        }
    }
    
    public boolean isSetTcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellImpl.TCPR$2) != 0;
        }
    }
    
    public void setTcPr(final CTTableCellProperties ctTableCellProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTableCellProperties, CTTableCellImpl.TCPR$2, 0, (short)1);
    }
    
    public CTTableCellProperties addNewTcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableCellProperties)this.get_store().add_element_user(CTTableCellImpl.TCPR$2);
        }
    }
    
    public void unsetTcPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellImpl.TCPR$2, 0);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTTableCellImpl.EXTLST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableCellImpl.EXTLST$4) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTableCellImpl.EXTLST$4, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTTableCellImpl.EXTLST$4);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableCellImpl.EXTLST$4, 0);
        }
    }
    
    public int getRowSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellImpl.ROWSPAN$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellImpl.ROWSPAN$6);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetRowSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTTableCellImpl.ROWSPAN$6);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTTableCellImpl.ROWSPAN$6);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetRowSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellImpl.ROWSPAN$6) != null;
        }
    }
    
    public void setRowSpan(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellImpl.ROWSPAN$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellImpl.ROWSPAN$6);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetRowSpan(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTTableCellImpl.ROWSPAN$6);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTTableCellImpl.ROWSPAN$6);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetRowSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellImpl.ROWSPAN$6);
        }
    }
    
    public int getGridSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellImpl.GRIDSPAN$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellImpl.GRIDSPAN$8);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetGridSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt = (XmlInt)this.get_store().find_attribute_user(CTTableCellImpl.GRIDSPAN$8);
            if (xmlInt == null) {
                xmlInt = (XmlInt)this.get_default_attribute_value(CTTableCellImpl.GRIDSPAN$8);
            }
            return xmlInt;
        }
    }
    
    public boolean isSetGridSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellImpl.GRIDSPAN$8) != null;
        }
    }
    
    public void setGridSpan(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellImpl.GRIDSPAN$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellImpl.GRIDSPAN$8);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetGridSpan(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTTableCellImpl.GRIDSPAN$8);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTTableCellImpl.GRIDSPAN$8);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetGridSpan() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellImpl.GRIDSPAN$8);
        }
    }
    
    public boolean getHMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellImpl.HMERGE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellImpl.HMERGE$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableCellImpl.HMERGE$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableCellImpl.HMERGE$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetHMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellImpl.HMERGE$10) != null;
        }
    }
    
    public void setHMerge(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellImpl.HMERGE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellImpl.HMERGE$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHMerge(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableCellImpl.HMERGE$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableCellImpl.HMERGE$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellImpl.HMERGE$10);
        }
    }
    
    public boolean getVMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellImpl.VMERGE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableCellImpl.VMERGE$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetVMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTTableCellImpl.VMERGE$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTTableCellImpl.VMERGE$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetVMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableCellImpl.VMERGE$12) != null;
        }
    }
    
    public void setVMerge(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableCellImpl.VMERGE$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableCellImpl.VMERGE$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetVMerge(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTTableCellImpl.VMERGE$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTTableCellImpl.VMERGE$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetVMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableCellImpl.VMERGE$12);
        }
    }
    
    static {
        TXBODY$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "txBody");
        TCPR$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tcPr");
        EXTLST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        ROWSPAN$6 = new QName("", "rowSpan");
        GRIDSPAN$8 = new QName("", "gridSpan");
        HMERGE$10 = new QName("", "hMerge");
        VMERGE$12 = new QName("", "vMerge");
    }
}
