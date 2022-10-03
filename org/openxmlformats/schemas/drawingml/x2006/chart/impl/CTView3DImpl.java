package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPerspective;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDepthPercent;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRotY;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTHPercent;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRotX;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTView3D;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTView3DImpl extends XmlComplexContentImpl implements CTView3D
{
    private static final long serialVersionUID = 1L;
    private static final QName ROTX$0;
    private static final QName HPERCENT$2;
    private static final QName ROTY$4;
    private static final QName DEPTHPERCENT$6;
    private static final QName RANGAX$8;
    private static final QName PERSPECTIVE$10;
    private static final QName EXTLST$12;
    
    public CTView3DImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRotX getRotX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRotX ctRotX = (CTRotX)this.get_store().find_element_user(CTView3DImpl.ROTX$0, 0);
            if (ctRotX == null) {
                return null;
            }
            return ctRotX;
        }
    }
    
    public boolean isSetRotX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTView3DImpl.ROTX$0) != 0;
        }
    }
    
    public void setRotX(final CTRotX ctRotX) {
        this.generatedSetterHelperImpl((XmlObject)ctRotX, CTView3DImpl.ROTX$0, 0, (short)1);
    }
    
    public CTRotX addNewRotX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRotX)this.get_store().add_element_user(CTView3DImpl.ROTX$0);
        }
    }
    
    public void unsetRotX() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTView3DImpl.ROTX$0, 0);
        }
    }
    
    public CTHPercent getHPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHPercent cthPercent = (CTHPercent)this.get_store().find_element_user(CTView3DImpl.HPERCENT$2, 0);
            if (cthPercent == null) {
                return null;
            }
            return cthPercent;
        }
    }
    
    public boolean isSetHPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTView3DImpl.HPERCENT$2) != 0;
        }
    }
    
    public void setHPercent(final CTHPercent cthPercent) {
        this.generatedSetterHelperImpl((XmlObject)cthPercent, CTView3DImpl.HPERCENT$2, 0, (short)1);
    }
    
    public CTHPercent addNewHPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHPercent)this.get_store().add_element_user(CTView3DImpl.HPERCENT$2);
        }
    }
    
    public void unsetHPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTView3DImpl.HPERCENT$2, 0);
        }
    }
    
    public CTRotY getRotY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRotY ctRotY = (CTRotY)this.get_store().find_element_user(CTView3DImpl.ROTY$4, 0);
            if (ctRotY == null) {
                return null;
            }
            return ctRotY;
        }
    }
    
    public boolean isSetRotY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTView3DImpl.ROTY$4) != 0;
        }
    }
    
    public void setRotY(final CTRotY ctRotY) {
        this.generatedSetterHelperImpl((XmlObject)ctRotY, CTView3DImpl.ROTY$4, 0, (short)1);
    }
    
    public CTRotY addNewRotY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRotY)this.get_store().add_element_user(CTView3DImpl.ROTY$4);
        }
    }
    
    public void unsetRotY() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTView3DImpl.ROTY$4, 0);
        }
    }
    
    public CTDepthPercent getDepthPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDepthPercent ctDepthPercent = (CTDepthPercent)this.get_store().find_element_user(CTView3DImpl.DEPTHPERCENT$6, 0);
            if (ctDepthPercent == null) {
                return null;
            }
            return ctDepthPercent;
        }
    }
    
    public boolean isSetDepthPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTView3DImpl.DEPTHPERCENT$6) != 0;
        }
    }
    
    public void setDepthPercent(final CTDepthPercent ctDepthPercent) {
        this.generatedSetterHelperImpl((XmlObject)ctDepthPercent, CTView3DImpl.DEPTHPERCENT$6, 0, (short)1);
    }
    
    public CTDepthPercent addNewDepthPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDepthPercent)this.get_store().add_element_user(CTView3DImpl.DEPTHPERCENT$6);
        }
    }
    
    public void unsetDepthPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTView3DImpl.DEPTHPERCENT$6, 0);
        }
    }
    
    public CTBoolean getRAngAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTView3DImpl.RANGAX$8, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetRAngAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTView3DImpl.RANGAX$8) != 0;
        }
    }
    
    public void setRAngAx(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTView3DImpl.RANGAX$8, 0, (short)1);
    }
    
    public CTBoolean addNewRAngAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTView3DImpl.RANGAX$8);
        }
    }
    
    public void unsetRAngAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTView3DImpl.RANGAX$8, 0);
        }
    }
    
    public CTPerspective getPerspective() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPerspective ctPerspective = (CTPerspective)this.get_store().find_element_user(CTView3DImpl.PERSPECTIVE$10, 0);
            if (ctPerspective == null) {
                return null;
            }
            return ctPerspective;
        }
    }
    
    public boolean isSetPerspective() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTView3DImpl.PERSPECTIVE$10) != 0;
        }
    }
    
    public void setPerspective(final CTPerspective ctPerspective) {
        this.generatedSetterHelperImpl((XmlObject)ctPerspective, CTView3DImpl.PERSPECTIVE$10, 0, (short)1);
    }
    
    public CTPerspective addNewPerspective() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPerspective)this.get_store().add_element_user(CTView3DImpl.PERSPECTIVE$10);
        }
    }
    
    public void unsetPerspective() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTView3DImpl.PERSPECTIVE$10, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTView3DImpl.EXTLST$12, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTView3DImpl.EXTLST$12) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTView3DImpl.EXTLST$12, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTView3DImpl.EXTLST$12);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTView3DImpl.EXTLST$12, 0);
        }
    }
    
    static {
        ROTX$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "rotX");
        HPERCENT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "hPercent");
        ROTY$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "rotY");
        DEPTHPERCENT$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "depthPercent");
        RANGAX$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "rAngAx");
        PERSPECTIVE$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "perspective");
        EXTLST$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
