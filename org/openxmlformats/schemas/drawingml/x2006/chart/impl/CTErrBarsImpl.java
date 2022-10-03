package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrValType;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrBarType;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrDir;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrBars;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTErrBarsImpl extends XmlComplexContentImpl implements CTErrBars
{
    private static final long serialVersionUID = 1L;
    private static final QName ERRDIR$0;
    private static final QName ERRBARTYPE$2;
    private static final QName ERRVALTYPE$4;
    private static final QName NOENDCAP$6;
    private static final QName PLUS$8;
    private static final QName MINUS$10;
    private static final QName VAL$12;
    private static final QName SPPR$14;
    private static final QName EXTLST$16;
    
    public CTErrBarsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTErrDir getErrDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTErrDir ctErrDir = (CTErrDir)this.get_store().find_element_user(CTErrBarsImpl.ERRDIR$0, 0);
            if (ctErrDir == null) {
                return null;
            }
            return ctErrDir;
        }
    }
    
    public boolean isSetErrDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTErrBarsImpl.ERRDIR$0) != 0;
        }
    }
    
    public void setErrDir(final CTErrDir ctErrDir) {
        this.generatedSetterHelperImpl((XmlObject)ctErrDir, CTErrBarsImpl.ERRDIR$0, 0, (short)1);
    }
    
    public CTErrDir addNewErrDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrDir)this.get_store().add_element_user(CTErrBarsImpl.ERRDIR$0);
        }
    }
    
    public void unsetErrDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTErrBarsImpl.ERRDIR$0, 0);
        }
    }
    
    public CTErrBarType getErrBarType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTErrBarType ctErrBarType = (CTErrBarType)this.get_store().find_element_user(CTErrBarsImpl.ERRBARTYPE$2, 0);
            if (ctErrBarType == null) {
                return null;
            }
            return ctErrBarType;
        }
    }
    
    public void setErrBarType(final CTErrBarType ctErrBarType) {
        this.generatedSetterHelperImpl((XmlObject)ctErrBarType, CTErrBarsImpl.ERRBARTYPE$2, 0, (short)1);
    }
    
    public CTErrBarType addNewErrBarType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrBarType)this.get_store().add_element_user(CTErrBarsImpl.ERRBARTYPE$2);
        }
    }
    
    public CTErrValType getErrValType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTErrValType ctErrValType = (CTErrValType)this.get_store().find_element_user(CTErrBarsImpl.ERRVALTYPE$4, 0);
            if (ctErrValType == null) {
                return null;
            }
            return ctErrValType;
        }
    }
    
    public void setErrValType(final CTErrValType ctErrValType) {
        this.generatedSetterHelperImpl((XmlObject)ctErrValType, CTErrBarsImpl.ERRVALTYPE$4, 0, (short)1);
    }
    
    public CTErrValType addNewErrValType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrValType)this.get_store().add_element_user(CTErrBarsImpl.ERRVALTYPE$4);
        }
    }
    
    public CTBoolean getNoEndCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTErrBarsImpl.NOENDCAP$6, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetNoEndCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTErrBarsImpl.NOENDCAP$6) != 0;
        }
    }
    
    public void setNoEndCap(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTErrBarsImpl.NOENDCAP$6, 0, (short)1);
    }
    
    public CTBoolean addNewNoEndCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTErrBarsImpl.NOENDCAP$6);
        }
    }
    
    public void unsetNoEndCap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTErrBarsImpl.NOENDCAP$6, 0);
        }
    }
    
    public CTNumDataSource getPlus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumDataSource ctNumDataSource = (CTNumDataSource)this.get_store().find_element_user(CTErrBarsImpl.PLUS$8, 0);
            if (ctNumDataSource == null) {
                return null;
            }
            return ctNumDataSource;
        }
    }
    
    public boolean isSetPlus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTErrBarsImpl.PLUS$8) != 0;
        }
    }
    
    public void setPlus(final CTNumDataSource ctNumDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctNumDataSource, CTErrBarsImpl.PLUS$8, 0, (short)1);
    }
    
    public CTNumDataSource addNewPlus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumDataSource)this.get_store().add_element_user(CTErrBarsImpl.PLUS$8);
        }
    }
    
    public void unsetPlus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTErrBarsImpl.PLUS$8, 0);
        }
    }
    
    public CTNumDataSource getMinus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumDataSource ctNumDataSource = (CTNumDataSource)this.get_store().find_element_user(CTErrBarsImpl.MINUS$10, 0);
            if (ctNumDataSource == null) {
                return null;
            }
            return ctNumDataSource;
        }
    }
    
    public boolean isSetMinus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTErrBarsImpl.MINUS$10) != 0;
        }
    }
    
    public void setMinus(final CTNumDataSource ctNumDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctNumDataSource, CTErrBarsImpl.MINUS$10, 0, (short)1);
    }
    
    public CTNumDataSource addNewMinus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumDataSource)this.get_store().add_element_user(CTErrBarsImpl.MINUS$10);
        }
    }
    
    public void unsetMinus() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTErrBarsImpl.MINUS$10, 0);
        }
    }
    
    public CTDouble getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDouble ctDouble = (CTDouble)this.get_store().find_element_user(CTErrBarsImpl.VAL$12, 0);
            if (ctDouble == null) {
                return null;
            }
            return ctDouble;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTErrBarsImpl.VAL$12) != 0;
        }
    }
    
    public void setVal(final CTDouble ctDouble) {
        this.generatedSetterHelperImpl((XmlObject)ctDouble, CTErrBarsImpl.VAL$12, 0, (short)1);
    }
    
    public CTDouble addNewVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDouble)this.get_store().add_element_user(CTErrBarsImpl.VAL$12);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTErrBarsImpl.VAL$12, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTErrBarsImpl.SPPR$14, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTErrBarsImpl.SPPR$14) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTErrBarsImpl.SPPR$14, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTErrBarsImpl.SPPR$14);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTErrBarsImpl.SPPR$14, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTErrBarsImpl.EXTLST$16, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTErrBarsImpl.EXTLST$16) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTErrBarsImpl.EXTLST$16, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTErrBarsImpl.EXTLST$16);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTErrBarsImpl.EXTLST$16, 0);
        }
    }
    
    static {
        ERRDIR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "errDir");
        ERRBARTYPE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "errBarType");
        ERRVALTYPE$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "errValType");
        NOENDCAP$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "noEndCap");
        PLUS$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "plus");
        MINUS$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "minus");
        VAL$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "val");
        SPPR$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        EXTLST$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
