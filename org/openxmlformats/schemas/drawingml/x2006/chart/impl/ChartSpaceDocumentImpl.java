package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartSpace;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.ChartSpaceDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ChartSpaceDocumentImpl extends XmlComplexContentImpl implements ChartSpaceDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName CHARTSPACE$0;
    
    public ChartSpaceDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTChartSpace getChartSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartSpace ctChartSpace = (CTChartSpace)this.get_store().find_element_user(ChartSpaceDocumentImpl.CHARTSPACE$0, 0);
            if (ctChartSpace == null) {
                return null;
            }
            return ctChartSpace;
        }
    }
    
    public void setChartSpace(final CTChartSpace ctChartSpace) {
        this.generatedSetterHelperImpl((XmlObject)ctChartSpace, ChartSpaceDocumentImpl.CHARTSPACE$0, 0, (short)1);
    }
    
    public CTChartSpace addNewChartSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartSpace)this.get_store().add_element_user(ChartSpaceDocumentImpl.CHARTSPACE$0);
        }
    }
    
    static {
        CHARTSPACE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "chartSpace");
    }
}
