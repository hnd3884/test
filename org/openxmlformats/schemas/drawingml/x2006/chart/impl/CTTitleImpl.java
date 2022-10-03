package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTx;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTitleImpl extends XmlComplexContentImpl implements CTTitle
{
    private static final long serialVersionUID = 1L;
    private static final QName TX$0;
    private static final QName LAYOUT$2;
    private static final QName OVERLAY$4;
    private static final QName SPPR$6;
    private static final QName TXPR$8;
    private static final QName EXTLST$10;
    
    public CTTitleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTx getTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTx ctTx = (CTTx)this.get_store().find_element_user(CTTitleImpl.TX$0, 0);
            if (ctTx == null) {
                return null;
            }
            return ctTx;
        }
    }
    
    public boolean isSetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTitleImpl.TX$0) != 0;
        }
    }
    
    public void setTx(final CTTx ctTx) {
        this.generatedSetterHelperImpl((XmlObject)ctTx, CTTitleImpl.TX$0, 0, (short)1);
    }
    
    public CTTx addNewTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTx)this.get_store().add_element_user(CTTitleImpl.TX$0);
        }
    }
    
    public void unsetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTitleImpl.TX$0, 0);
        }
    }
    
    public CTLayout getLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLayout ctLayout = (CTLayout)this.get_store().find_element_user(CTTitleImpl.LAYOUT$2, 0);
            if (ctLayout == null) {
                return null;
            }
            return ctLayout;
        }
    }
    
    public boolean isSetLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTitleImpl.LAYOUT$2) != 0;
        }
    }
    
    public void setLayout(final CTLayout ctLayout) {
        this.generatedSetterHelperImpl((XmlObject)ctLayout, CTTitleImpl.LAYOUT$2, 0, (short)1);
    }
    
    public CTLayout addNewLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLayout)this.get_store().add_element_user(CTTitleImpl.LAYOUT$2);
        }
    }
    
    public void unsetLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTitleImpl.LAYOUT$2, 0);
        }
    }
    
    public CTBoolean getOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTTitleImpl.OVERLAY$4, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTitleImpl.OVERLAY$4) != 0;
        }
    }
    
    public void setOverlay(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTTitleImpl.OVERLAY$4, 0, (short)1);
    }
    
    public CTBoolean addNewOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTTitleImpl.OVERLAY$4);
        }
    }
    
    public void unsetOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTitleImpl.OVERLAY$4, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTTitleImpl.SPPR$6, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTitleImpl.SPPR$6) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTTitleImpl.SPPR$6, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTTitleImpl.SPPR$6);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTitleImpl.SPPR$6, 0);
        }
    }
    
    public CTTextBody getTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBody ctTextBody = (CTTextBody)this.get_store().find_element_user(CTTitleImpl.TXPR$8, 0);
            if (ctTextBody == null) {
                return null;
            }
            return ctTextBody;
        }
    }
    
    public boolean isSetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTitleImpl.TXPR$8) != 0;
        }
    }
    
    public void setTxPr(final CTTextBody ctTextBody) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBody, CTTitleImpl.TXPR$8, 0, (short)1);
    }
    
    public CTTextBody addNewTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBody)this.get_store().add_element_user(CTTitleImpl.TXPR$8);
        }
    }
    
    public void unsetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTitleImpl.TXPR$8, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTTitleImpl.EXTLST$10, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTitleImpl.EXTLST$10) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTitleImpl.EXTLST$10, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTTitleImpl.EXTLST$10);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTitleImpl.EXTLST$10, 0);
        }
    }
    
    static {
        TX$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tx");
        LAYOUT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "layout");
        OVERLAY$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "overlay");
        SPPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        TXPR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "txPr");
        EXTLST$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
