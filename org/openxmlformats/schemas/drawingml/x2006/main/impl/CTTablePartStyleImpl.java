package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleCellStyle;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleTextStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTablePartStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTablePartStyleImpl extends XmlComplexContentImpl implements CTTablePartStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName TCTXSTYLE$0;
    private static final QName TCSTYLE$2;
    
    public CTTablePartStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTableStyleTextStyle getTcTxStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyleTextStyle ctTableStyleTextStyle = (CTTableStyleTextStyle)this.get_store().find_element_user(CTTablePartStyleImpl.TCTXSTYLE$0, 0);
            if (ctTableStyleTextStyle == null) {
                return null;
            }
            return ctTableStyleTextStyle;
        }
    }
    
    public boolean isSetTcTxStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePartStyleImpl.TCTXSTYLE$0) != 0;
        }
    }
    
    public void setTcTxStyle(final CTTableStyleTextStyle ctTableStyleTextStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTableStyleTextStyle, CTTablePartStyleImpl.TCTXSTYLE$0, 0, (short)1);
    }
    
    public CTTableStyleTextStyle addNewTcTxStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyleTextStyle)this.get_store().add_element_user(CTTablePartStyleImpl.TCTXSTYLE$0);
        }
    }
    
    public void unsetTcTxStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePartStyleImpl.TCTXSTYLE$0, 0);
        }
    }
    
    public CTTableStyleCellStyle getTcStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyleCellStyle ctTableStyleCellStyle = (CTTableStyleCellStyle)this.get_store().find_element_user(CTTablePartStyleImpl.TCSTYLE$2, 0);
            if (ctTableStyleCellStyle == null) {
                return null;
            }
            return ctTableStyleCellStyle;
        }
    }
    
    public boolean isSetTcStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTablePartStyleImpl.TCSTYLE$2) != 0;
        }
    }
    
    public void setTcStyle(final CTTableStyleCellStyle ctTableStyleCellStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTableStyleCellStyle, CTTablePartStyleImpl.TCSTYLE$2, 0, (short)1);
    }
    
    public CTTableStyleCellStyle addNewTcStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyleCellStyle)this.get_store().add_element_user(CTTablePartStyleImpl.TCSTYLE$2);
        }
    }
    
    public void unsetTcStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTablePartStyleImpl.TCSTYLE$2, 0);
        }
    }
    
    static {
        TCTXSTYLE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tcTxStyle");
        TCSTYLE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tcStyle");
    }
}
