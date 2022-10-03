package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTChartLinesImpl extends XmlComplexContentImpl implements CTChartLines
{
    private static final long serialVersionUID = 1L;
    private static final QName SPPR$0;
    
    public CTChartLinesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTChartLinesImpl.SPPR$0, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartLinesImpl.SPPR$0) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTChartLinesImpl.SPPR$0, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTChartLinesImpl.SPPR$0);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartLinesImpl.SPPR$0, 0);
        }
    }
    
    static {
        SPPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
    }
}
