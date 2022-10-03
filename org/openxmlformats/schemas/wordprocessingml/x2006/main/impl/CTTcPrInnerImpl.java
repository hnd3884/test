package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCellMergeTrackChange;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrackChange;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPrInner;

public class CTTcPrInnerImpl extends CTTcPrBaseImpl implements CTTcPrInner
{
    private static final long serialVersionUID = 1L;
    private static final QName CELLINS$0;
    private static final QName CELLDEL$2;
    private static final QName CELLMERGE$4;
    
    public CTTcPrInnerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTTrackChange getCellIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTTcPrInnerImpl.CELLINS$0, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    @Override
    public boolean isSetCellIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrInnerImpl.CELLINS$0) != 0;
        }
    }
    
    @Override
    public void setCellIns(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTTcPrInnerImpl.CELLINS$0, 0, (short)1);
    }
    
    @Override
    public CTTrackChange addNewCellIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTTcPrInnerImpl.CELLINS$0);
        }
    }
    
    @Override
    public void unsetCellIns() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrInnerImpl.CELLINS$0, 0);
        }
    }
    
    @Override
    public CTTrackChange getCellDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrackChange ctTrackChange = (CTTrackChange)this.get_store().find_element_user(CTTcPrInnerImpl.CELLDEL$2, 0);
            if (ctTrackChange == null) {
                return null;
            }
            return ctTrackChange;
        }
    }
    
    @Override
    public boolean isSetCellDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrInnerImpl.CELLDEL$2) != 0;
        }
    }
    
    @Override
    public void setCellDel(final CTTrackChange ctTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctTrackChange, CTTcPrInnerImpl.CELLDEL$2, 0, (short)1);
    }
    
    @Override
    public CTTrackChange addNewCellDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrackChange)this.get_store().add_element_user(CTTcPrInnerImpl.CELLDEL$2);
        }
    }
    
    @Override
    public void unsetCellDel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrInnerImpl.CELLDEL$2, 0);
        }
    }
    
    @Override
    public CTCellMergeTrackChange getCellMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCellMergeTrackChange ctCellMergeTrackChange = (CTCellMergeTrackChange)this.get_store().find_element_user(CTTcPrInnerImpl.CELLMERGE$4, 0);
            if (ctCellMergeTrackChange == null) {
                return null;
            }
            return ctCellMergeTrackChange;
        }
    }
    
    @Override
    public boolean isSetCellMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTcPrInnerImpl.CELLMERGE$4) != 0;
        }
    }
    
    @Override
    public void setCellMerge(final CTCellMergeTrackChange ctCellMergeTrackChange) {
        this.generatedSetterHelperImpl((XmlObject)ctCellMergeTrackChange, CTTcPrInnerImpl.CELLMERGE$4, 0, (short)1);
    }
    
    @Override
    public CTCellMergeTrackChange addNewCellMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCellMergeTrackChange)this.get_store().add_element_user(CTTcPrInnerImpl.CELLMERGE$4);
        }
    }
    
    @Override
    public void unsetCellMerge() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTcPrInnerImpl.CELLMERGE$4, 0);
        }
    }
    
    static {
        CELLINS$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cellIns");
        CELLDEL$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cellDel");
        CELLMERGE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cellMerge");
    }
}
