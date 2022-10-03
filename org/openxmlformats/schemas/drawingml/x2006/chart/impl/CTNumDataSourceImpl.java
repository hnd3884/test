package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTNumDataSourceImpl extends XmlComplexContentImpl implements CTNumDataSource
{
    private static final long serialVersionUID = 1L;
    private static final QName NUMREF$0;
    private static final QName NUMLIT$2;
    
    public CTNumDataSourceImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTNumRef getNumRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumRef ctNumRef = (CTNumRef)this.get_store().find_element_user(CTNumDataSourceImpl.NUMREF$0, 0);
            if (ctNumRef == null) {
                return null;
            }
            return ctNumRef;
        }
    }
    
    public boolean isSetNumRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumDataSourceImpl.NUMREF$0) != 0;
        }
    }
    
    public void setNumRef(final CTNumRef ctNumRef) {
        this.generatedSetterHelperImpl((XmlObject)ctNumRef, CTNumDataSourceImpl.NUMREF$0, 0, (short)1);
    }
    
    public CTNumRef addNewNumRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumRef)this.get_store().add_element_user(CTNumDataSourceImpl.NUMREF$0);
        }
    }
    
    public void unsetNumRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumDataSourceImpl.NUMREF$0, 0);
        }
    }
    
    public CTNumData getNumLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumData ctNumData = (CTNumData)this.get_store().find_element_user(CTNumDataSourceImpl.NUMLIT$2, 0);
            if (ctNumData == null) {
                return null;
            }
            return ctNumData;
        }
    }
    
    public boolean isSetNumLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTNumDataSourceImpl.NUMLIT$2) != 0;
        }
    }
    
    public void setNumLit(final CTNumData ctNumData) {
        this.generatedSetterHelperImpl((XmlObject)ctNumData, CTNumDataSourceImpl.NUMLIT$2, 0, (short)1);
    }
    
    public CTNumData addNewNumLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumData)this.get_store().add_element_user(CTNumDataSourceImpl.NUMLIT$2);
        }
    }
    
    public void unsetNumLit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTNumDataSourceImpl.NUMLIT$2, 0);
        }
    }
    
    static {
        NUMREF$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "numRef");
        NUMLIT$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "numLit");
    }
}
