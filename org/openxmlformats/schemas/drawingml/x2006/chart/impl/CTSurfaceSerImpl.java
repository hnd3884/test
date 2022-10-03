package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceSer;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSurfaceSerImpl extends XmlComplexContentImpl implements CTSurfaceSer
{
    private static final long serialVersionUID = 1L;
    private static final QName IDX$0;
    private static final QName ORDER$2;
    private static final QName TX$4;
    private static final QName SPPR$6;
    private static final QName CAT$8;
    private static final QName VAL$10;
    private static final QName EXTLST$12;
    
    public CTSurfaceSerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTSurfaceSerImpl.IDX$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setIdx(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTSurfaceSerImpl.IDX$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTSurfaceSerImpl.IDX$0);
        }
    }
    
    public CTUnsignedInt getOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTSurfaceSerImpl.ORDER$2, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setOrder(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTSurfaceSerImpl.ORDER$2, 0, (short)1);
    }
    
    public CTUnsignedInt addNewOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTSurfaceSerImpl.ORDER$2);
        }
    }
    
    public CTSerTx getTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSerTx ctSerTx = (CTSerTx)this.get_store().find_element_user(CTSurfaceSerImpl.TX$4, 0);
            if (ctSerTx == null) {
                return null;
            }
            return ctSerTx;
        }
    }
    
    public boolean isSetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceSerImpl.TX$4) != 0;
        }
    }
    
    public void setTx(final CTSerTx ctSerTx) {
        this.generatedSetterHelperImpl((XmlObject)ctSerTx, CTSurfaceSerImpl.TX$4, 0, (short)1);
    }
    
    public CTSerTx addNewTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSerTx)this.get_store().add_element_user(CTSurfaceSerImpl.TX$4);
        }
    }
    
    public void unsetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceSerImpl.TX$4, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTSurfaceSerImpl.SPPR$6, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceSerImpl.SPPR$6) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTSurfaceSerImpl.SPPR$6, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTSurfaceSerImpl.SPPR$6);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceSerImpl.SPPR$6, 0);
        }
    }
    
    public CTAxDataSource getCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxDataSource ctAxDataSource = (CTAxDataSource)this.get_store().find_element_user(CTSurfaceSerImpl.CAT$8, 0);
            if (ctAxDataSource == null) {
                return null;
            }
            return ctAxDataSource;
        }
    }
    
    public boolean isSetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceSerImpl.CAT$8) != 0;
        }
    }
    
    public void setCat(final CTAxDataSource ctAxDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctAxDataSource, CTSurfaceSerImpl.CAT$8, 0, (short)1);
    }
    
    public CTAxDataSource addNewCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxDataSource)this.get_store().add_element_user(CTSurfaceSerImpl.CAT$8);
        }
    }
    
    public void unsetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceSerImpl.CAT$8, 0);
        }
    }
    
    public CTNumDataSource getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumDataSource ctNumDataSource = (CTNumDataSource)this.get_store().find_element_user(CTSurfaceSerImpl.VAL$10, 0);
            if (ctNumDataSource == null) {
                return null;
            }
            return ctNumDataSource;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceSerImpl.VAL$10) != 0;
        }
    }
    
    public void setVal(final CTNumDataSource ctNumDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctNumDataSource, CTSurfaceSerImpl.VAL$10, 0, (short)1);
    }
    
    public CTNumDataSource addNewVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumDataSource)this.get_store().add_element_user(CTSurfaceSerImpl.VAL$10);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceSerImpl.VAL$10, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSurfaceSerImpl.EXTLST$12, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurfaceSerImpl.EXTLST$12) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSurfaceSerImpl.EXTLST$12, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSurfaceSerImpl.EXTLST$12);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurfaceSerImpl.EXTLST$12, 0);
        }
    }
    
    static {
        IDX$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "idx");
        ORDER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "order");
        TX$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tx");
        SPPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        CAT$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "cat");
        VAL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "val");
        EXTLST$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
