package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOrientation;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLogBase;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTScalingImpl extends XmlComplexContentImpl implements CTScaling
{
    private static final long serialVersionUID = 1L;
    private static final QName LOGBASE$0;
    private static final QName ORIENTATION$2;
    private static final QName MAX$4;
    private static final QName MIN$6;
    private static final QName EXTLST$8;
    
    public CTScalingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTLogBase getLogBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLogBase ctLogBase = (CTLogBase)this.get_store().find_element_user(CTScalingImpl.LOGBASE$0, 0);
            if (ctLogBase == null) {
                return null;
            }
            return ctLogBase;
        }
    }
    
    public boolean isSetLogBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScalingImpl.LOGBASE$0) != 0;
        }
    }
    
    public void setLogBase(final CTLogBase ctLogBase) {
        this.generatedSetterHelperImpl((XmlObject)ctLogBase, CTScalingImpl.LOGBASE$0, 0, (short)1);
    }
    
    public CTLogBase addNewLogBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLogBase)this.get_store().add_element_user(CTScalingImpl.LOGBASE$0);
        }
    }
    
    public void unsetLogBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScalingImpl.LOGBASE$0, 0);
        }
    }
    
    public CTOrientation getOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOrientation ctOrientation = (CTOrientation)this.get_store().find_element_user(CTScalingImpl.ORIENTATION$2, 0);
            if (ctOrientation == null) {
                return null;
            }
            return ctOrientation;
        }
    }
    
    public boolean isSetOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScalingImpl.ORIENTATION$2) != 0;
        }
    }
    
    public void setOrientation(final CTOrientation ctOrientation) {
        this.generatedSetterHelperImpl((XmlObject)ctOrientation, CTScalingImpl.ORIENTATION$2, 0, (short)1);
    }
    
    public CTOrientation addNewOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOrientation)this.get_store().add_element_user(CTScalingImpl.ORIENTATION$2);
        }
    }
    
    public void unsetOrientation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScalingImpl.ORIENTATION$2, 0);
        }
    }
    
    public CTDouble getMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDouble ctDouble = (CTDouble)this.get_store().find_element_user(CTScalingImpl.MAX$4, 0);
            if (ctDouble == null) {
                return null;
            }
            return ctDouble;
        }
    }
    
    public boolean isSetMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScalingImpl.MAX$4) != 0;
        }
    }
    
    public void setMax(final CTDouble ctDouble) {
        this.generatedSetterHelperImpl((XmlObject)ctDouble, CTScalingImpl.MAX$4, 0, (short)1);
    }
    
    public CTDouble addNewMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDouble)this.get_store().add_element_user(CTScalingImpl.MAX$4);
        }
    }
    
    public void unsetMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScalingImpl.MAX$4, 0);
        }
    }
    
    public CTDouble getMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDouble ctDouble = (CTDouble)this.get_store().find_element_user(CTScalingImpl.MIN$6, 0);
            if (ctDouble == null) {
                return null;
            }
            return ctDouble;
        }
    }
    
    public boolean isSetMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScalingImpl.MIN$6) != 0;
        }
    }
    
    public void setMin(final CTDouble ctDouble) {
        this.generatedSetterHelperImpl((XmlObject)ctDouble, CTScalingImpl.MIN$6, 0, (short)1);
    }
    
    public CTDouble addNewMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDouble)this.get_store().add_element_user(CTScalingImpl.MIN$6);
        }
    }
    
    public void unsetMin() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScalingImpl.MIN$6, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTScalingImpl.EXTLST$8, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScalingImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTScalingImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTScalingImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScalingImpl.EXTLST$8, 0);
        }
    }
    
    static {
        LOGBASE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "logBase");
        ORIENTATION$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "orientation");
        MAX$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "max");
        MIN$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "min");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
