package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTCell3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillProperties;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellBorderStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleCellStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableStyleCellStyleImpl extends XmlComplexContentImpl implements CTTableStyleCellStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName TCBDR$0;
    private static final QName FILL$2;
    private static final QName FILLREF$4;
    private static final QName CELL3D$6;
    
    public CTTableStyleCellStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTableCellBorderStyle getTcBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableCellBorderStyle ctTableCellBorderStyle = (CTTableCellBorderStyle)this.get_store().find_element_user(CTTableStyleCellStyleImpl.TCBDR$0, 0);
            if (ctTableCellBorderStyle == null) {
                return null;
            }
            return ctTableCellBorderStyle;
        }
    }
    
    public boolean isSetTcBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleCellStyleImpl.TCBDR$0) != 0;
        }
    }
    
    public void setTcBdr(final CTTableCellBorderStyle ctTableCellBorderStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctTableCellBorderStyle, CTTableStyleCellStyleImpl.TCBDR$0, 0, (short)1);
    }
    
    public CTTableCellBorderStyle addNewTcBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableCellBorderStyle)this.get_store().add_element_user(CTTableStyleCellStyleImpl.TCBDR$0);
        }
    }
    
    public void unsetTcBdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleCellStyleImpl.TCBDR$0, 0);
        }
    }
    
    public CTFillProperties getFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFillProperties ctFillProperties = (CTFillProperties)this.get_store().find_element_user(CTTableStyleCellStyleImpl.FILL$2, 0);
            if (ctFillProperties == null) {
                return null;
            }
            return ctFillProperties;
        }
    }
    
    public boolean isSetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleCellStyleImpl.FILL$2) != 0;
        }
    }
    
    public void setFill(final CTFillProperties ctFillProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctFillProperties, CTTableStyleCellStyleImpl.FILL$2, 0, (short)1);
    }
    
    public CTFillProperties addNewFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillProperties)this.get_store().add_element_user(CTTableStyleCellStyleImpl.FILL$2);
        }
    }
    
    public void unsetFill() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleCellStyleImpl.FILL$2, 0);
        }
    }
    
    public CTStyleMatrixReference getFillRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyleMatrixReference ctStyleMatrixReference = (CTStyleMatrixReference)this.get_store().find_element_user(CTTableStyleCellStyleImpl.FILLREF$4, 0);
            if (ctStyleMatrixReference == null) {
                return null;
            }
            return ctStyleMatrixReference;
        }
    }
    
    public boolean isSetFillRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleCellStyleImpl.FILLREF$4) != 0;
        }
    }
    
    public void setFillRef(final CTStyleMatrixReference ctStyleMatrixReference) {
        this.generatedSetterHelperImpl((XmlObject)ctStyleMatrixReference, CTTableStyleCellStyleImpl.FILLREF$4, 0, (short)1);
    }
    
    public CTStyleMatrixReference addNewFillRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyleMatrixReference)this.get_store().add_element_user(CTTableStyleCellStyleImpl.FILLREF$4);
        }
    }
    
    public void unsetFillRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleCellStyleImpl.FILLREF$4, 0);
        }
    }
    
    public CTCell3D getCell3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCell3D ctCell3D = (CTCell3D)this.get_store().find_element_user(CTTableStyleCellStyleImpl.CELL3D$6, 0);
            if (ctCell3D == null) {
                return null;
            }
            return ctCell3D;
        }
    }
    
    public boolean isSetCell3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableStyleCellStyleImpl.CELL3D$6) != 0;
        }
    }
    
    public void setCell3D(final CTCell3D ctCell3D) {
        this.generatedSetterHelperImpl((XmlObject)ctCell3D, CTTableStyleCellStyleImpl.CELL3D$6, 0, (short)1);
    }
    
    public CTCell3D addNewCell3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCell3D)this.get_store().add_element_user(CTTableStyleCellStyleImpl.CELL3D$6);
        }
    }
    
    public void unsetCell3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableStyleCellStyleImpl.CELL3D$6, 0);
        }
    }
    
    static {
        TCBDR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tcBdr");
        FILL$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fill");
        FILLREF$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillRef");
        CELL3D$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "cell3D");
    }
}
