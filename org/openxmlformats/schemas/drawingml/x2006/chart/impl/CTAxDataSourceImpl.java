package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMultiLvlStrRef;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAxDataSourceImpl extends XmlComplexContentImpl implements CTAxDataSource
{
    private static final long serialVersionUID = 1L;
    private static final QName MULTILVLSTRREF$0;
    private static final QName NUMREF$2;
    private static final QName NUMLIT$4;
    private static final QName STRREF$6;
    private static final QName STRLIT$8;
    
    public CTAxDataSourceImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTMultiLvlStrRef getMultiLvlStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMultiLvlStrRef ctMultiLvlStrRef = (CTMultiLvlStrRef)this.get_store().find_element_user(CTAxDataSourceImpl.MULTILVLSTRREF$0, 0);
            if (ctMultiLvlStrRef == null) {
                return null;
            }
            return ctMultiLvlStrRef;
        }
    }
    
    public boolean isSetMultiLvlStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAxDataSourceImpl.MULTILVLSTRREF$0) != 0;
        }
    }
    
    public void setMultiLvlStrRef(final CTMultiLvlStrRef ctMultiLvlStrRef) {
        this.generatedSetterHelperImpl((XmlObject)ctMultiLvlStrRef, CTAxDataSourceImpl.MULTILVLSTRREF$0, 0, (short)1);
    }
    
    public CTMultiLvlStrRef addNewMultiLvlStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMultiLvlStrRef)this.get_store().add_element_user(CTAxDataSourceImpl.MULTILVLSTRREF$0);
        }
    }
    
    public void unsetMultiLvlStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAxDataSourceImpl.MULTILVLSTRREF$0, 0);
        }
    }
    
    public CTNumRef getNumRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumRef ctNumRef = (CTNumRef)this.get_store().find_element_user(CTAxDataSourceImpl.NUMREF$2, 0);
            if (ctNumRef == null) {
                return null;
            }
            return ctNumRef;
        }
    }
    
    public boolean isSetNumRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAxDataSourceImpl.NUMREF$2) != 0;
        }
    }
    
    public void setNumRef(final CTNumRef ctNumRef) {
        this.generatedSetterHelperImpl((XmlObject)ctNumRef, CTAxDataSourceImpl.NUMREF$2, 0, (short)1);
    }
    
    public CTNumRef addNewNumRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumRef)this.get_store().add_element_user(CTAxDataSourceImpl.NUMREF$2);
        }
    }
    
    public void unsetNumRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAxDataSourceImpl.NUMREF$2, 0);
        }
    }
    
    public CTNumData getNumLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumData ctNumData = (CTNumData)this.get_store().find_element_user(CTAxDataSourceImpl.NUMLIT$4, 0);
            if (ctNumData == null) {
                return null;
            }
            return ctNumData;
        }
    }
    
    public boolean isSetNumLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAxDataSourceImpl.NUMLIT$4) != 0;
        }
    }
    
    public void setNumLit(final CTNumData ctNumData) {
        this.generatedSetterHelperImpl((XmlObject)ctNumData, CTAxDataSourceImpl.NUMLIT$4, 0, (short)1);
    }
    
    public CTNumData addNewNumLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumData)this.get_store().add_element_user(CTAxDataSourceImpl.NUMLIT$4);
        }
    }
    
    public void unsetNumLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAxDataSourceImpl.NUMLIT$4, 0);
        }
    }
    
    public CTStrRef getStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrRef ctStrRef = (CTStrRef)this.get_store().find_element_user(CTAxDataSourceImpl.STRREF$6, 0);
            if (ctStrRef == null) {
                return null;
            }
            return ctStrRef;
        }
    }
    
    public boolean isSetStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAxDataSourceImpl.STRREF$6) != 0;
        }
    }
    
    public void setStrRef(final CTStrRef ctStrRef) {
        this.generatedSetterHelperImpl((XmlObject)ctStrRef, CTAxDataSourceImpl.STRREF$6, 0, (short)1);
    }
    
    public CTStrRef addNewStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrRef)this.get_store().add_element_user(CTAxDataSourceImpl.STRREF$6);
        }
    }
    
    public void unsetStrRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAxDataSourceImpl.STRREF$6, 0);
        }
    }
    
    public CTStrData getStrLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStrData ctStrData = (CTStrData)this.get_store().find_element_user(CTAxDataSourceImpl.STRLIT$8, 0);
            if (ctStrData == null) {
                return null;
            }
            return ctStrData;
        }
    }
    
    public boolean isSetStrLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAxDataSourceImpl.STRLIT$8) != 0;
        }
    }
    
    public void setStrLit(final CTStrData ctStrData) {
        this.generatedSetterHelperImpl((XmlObject)ctStrData, CTAxDataSourceImpl.STRLIT$8, 0, (short)1);
    }
    
    public CTStrData addNewStrLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStrData)this.get_store().add_element_user(CTAxDataSourceImpl.STRLIT$8);
        }
    }
    
    public void unsetStrLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAxDataSourceImpl.STRLIT$8, 0);
        }
    }
    
    static {
        MULTILVLSTRREF$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "multiLvlStrRef");
        NUMREF$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "numRef");
        NUMLIT$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "numLit");
        STRREF$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "strRef");
        STRLIT$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "strLit");
    }
}
