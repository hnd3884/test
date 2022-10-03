package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayoutMode;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayoutTarget;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTManualLayout;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTManualLayoutImpl extends XmlComplexContentImpl implements CTManualLayout
{
    private static final long serialVersionUID = 1L;
    private static final QName LAYOUTTARGET$0;
    private static final QName XMODE$2;
    private static final QName YMODE$4;
    private static final QName WMODE$6;
    private static final QName HMODE$8;
    private static final QName X$10;
    private static final QName Y$12;
    private static final QName W$14;
    private static final QName H$16;
    private static final QName EXTLST$18;
    
    public CTManualLayoutImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTLayoutTarget getLayoutTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLayoutTarget ctLayoutTarget = (CTLayoutTarget)this.get_store().find_element_user(CTManualLayoutImpl.LAYOUTTARGET$0, 0);
            if (ctLayoutTarget == null) {
                return null;
            }
            return ctLayoutTarget;
        }
    }
    
    public boolean isSetLayoutTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.LAYOUTTARGET$0) != 0;
        }
    }
    
    public void setLayoutTarget(final CTLayoutTarget ctLayoutTarget) {
        this.generatedSetterHelperImpl((XmlObject)ctLayoutTarget, CTManualLayoutImpl.LAYOUTTARGET$0, 0, (short)1);
    }
    
    public CTLayoutTarget addNewLayoutTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLayoutTarget)this.get_store().add_element_user(CTManualLayoutImpl.LAYOUTTARGET$0);
        }
    }
    
    public void unsetLayoutTarget() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.LAYOUTTARGET$0, 0);
        }
    }
    
    public CTLayoutMode getXMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLayoutMode ctLayoutMode = (CTLayoutMode)this.get_store().find_element_user(CTManualLayoutImpl.XMODE$2, 0);
            if (ctLayoutMode == null) {
                return null;
            }
            return ctLayoutMode;
        }
    }
    
    public boolean isSetXMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.XMODE$2) != 0;
        }
    }
    
    public void setXMode(final CTLayoutMode ctLayoutMode) {
        this.generatedSetterHelperImpl((XmlObject)ctLayoutMode, CTManualLayoutImpl.XMODE$2, 0, (short)1);
    }
    
    public CTLayoutMode addNewXMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLayoutMode)this.get_store().add_element_user(CTManualLayoutImpl.XMODE$2);
        }
    }
    
    public void unsetXMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.XMODE$2, 0);
        }
    }
    
    public CTLayoutMode getYMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLayoutMode ctLayoutMode = (CTLayoutMode)this.get_store().find_element_user(CTManualLayoutImpl.YMODE$4, 0);
            if (ctLayoutMode == null) {
                return null;
            }
            return ctLayoutMode;
        }
    }
    
    public boolean isSetYMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.YMODE$4) != 0;
        }
    }
    
    public void setYMode(final CTLayoutMode ctLayoutMode) {
        this.generatedSetterHelperImpl((XmlObject)ctLayoutMode, CTManualLayoutImpl.YMODE$4, 0, (short)1);
    }
    
    public CTLayoutMode addNewYMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLayoutMode)this.get_store().add_element_user(CTManualLayoutImpl.YMODE$4);
        }
    }
    
    public void unsetYMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.YMODE$4, 0);
        }
    }
    
    public CTLayoutMode getWMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLayoutMode ctLayoutMode = (CTLayoutMode)this.get_store().find_element_user(CTManualLayoutImpl.WMODE$6, 0);
            if (ctLayoutMode == null) {
                return null;
            }
            return ctLayoutMode;
        }
    }
    
    public boolean isSetWMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.WMODE$6) != 0;
        }
    }
    
    public void setWMode(final CTLayoutMode ctLayoutMode) {
        this.generatedSetterHelperImpl((XmlObject)ctLayoutMode, CTManualLayoutImpl.WMODE$6, 0, (short)1);
    }
    
    public CTLayoutMode addNewWMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLayoutMode)this.get_store().add_element_user(CTManualLayoutImpl.WMODE$6);
        }
    }
    
    public void unsetWMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.WMODE$6, 0);
        }
    }
    
    public CTLayoutMode getHMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLayoutMode ctLayoutMode = (CTLayoutMode)this.get_store().find_element_user(CTManualLayoutImpl.HMODE$8, 0);
            if (ctLayoutMode == null) {
                return null;
            }
            return ctLayoutMode;
        }
    }
    
    public boolean isSetHMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.HMODE$8) != 0;
        }
    }
    
    public void setHMode(final CTLayoutMode ctLayoutMode) {
        this.generatedSetterHelperImpl((XmlObject)ctLayoutMode, CTManualLayoutImpl.HMODE$8, 0, (short)1);
    }
    
    public CTLayoutMode addNewHMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLayoutMode)this.get_store().add_element_user(CTManualLayoutImpl.HMODE$8);
        }
    }
    
    public void unsetHMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.HMODE$8, 0);
        }
    }
    
    public CTDouble getX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDouble ctDouble = (CTDouble)this.get_store().find_element_user(CTManualLayoutImpl.X$10, 0);
            if (ctDouble == null) {
                return null;
            }
            return ctDouble;
        }
    }
    
    public boolean isSetX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.X$10) != 0;
        }
    }
    
    public void setX(final CTDouble ctDouble) {
        this.generatedSetterHelperImpl((XmlObject)ctDouble, CTManualLayoutImpl.X$10, 0, (short)1);
    }
    
    public CTDouble addNewX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDouble)this.get_store().add_element_user(CTManualLayoutImpl.X$10);
        }
    }
    
    public void unsetX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.X$10, 0);
        }
    }
    
    public CTDouble getY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDouble ctDouble = (CTDouble)this.get_store().find_element_user(CTManualLayoutImpl.Y$12, 0);
            if (ctDouble == null) {
                return null;
            }
            return ctDouble;
        }
    }
    
    public boolean isSetY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.Y$12) != 0;
        }
    }
    
    public void setY(final CTDouble ctDouble) {
        this.generatedSetterHelperImpl((XmlObject)ctDouble, CTManualLayoutImpl.Y$12, 0, (short)1);
    }
    
    public CTDouble addNewY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDouble)this.get_store().add_element_user(CTManualLayoutImpl.Y$12);
        }
    }
    
    public void unsetY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.Y$12, 0);
        }
    }
    
    public CTDouble getW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDouble ctDouble = (CTDouble)this.get_store().find_element_user(CTManualLayoutImpl.W$14, 0);
            if (ctDouble == null) {
                return null;
            }
            return ctDouble;
        }
    }
    
    public boolean isSetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.W$14) != 0;
        }
    }
    
    public void setW(final CTDouble ctDouble) {
        this.generatedSetterHelperImpl((XmlObject)ctDouble, CTManualLayoutImpl.W$14, 0, (short)1);
    }
    
    public CTDouble addNewW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDouble)this.get_store().add_element_user(CTManualLayoutImpl.W$14);
        }
    }
    
    public void unsetW() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.W$14, 0);
        }
    }
    
    public CTDouble getH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDouble ctDouble = (CTDouble)this.get_store().find_element_user(CTManualLayoutImpl.H$16, 0);
            if (ctDouble == null) {
                return null;
            }
            return ctDouble;
        }
    }
    
    public boolean isSetH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.H$16) != 0;
        }
    }
    
    public void setH(final CTDouble ctDouble) {
        this.generatedSetterHelperImpl((XmlObject)ctDouble, CTManualLayoutImpl.H$16, 0, (short)1);
    }
    
    public CTDouble addNewH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDouble)this.get_store().add_element_user(CTManualLayoutImpl.H$16);
        }
    }
    
    public void unsetH() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.H$16, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTManualLayoutImpl.EXTLST$18, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTManualLayoutImpl.EXTLST$18) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTManualLayoutImpl.EXTLST$18, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTManualLayoutImpl.EXTLST$18);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTManualLayoutImpl.EXTLST$18, 0);
        }
    }
    
    static {
        LAYOUTTARGET$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "layoutTarget");
        XMODE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "xMode");
        YMODE$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "yMode");
        WMODE$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "wMode");
        HMODE$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "hMode");
        X$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "x");
        Y$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "y");
        W$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "w");
        H$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "h");
        EXTLST$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
