package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.ChartsheetDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ChartsheetDocumentImpl extends XmlComplexContentImpl implements ChartsheetDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName CHARTSHEET$0;
    
    public ChartsheetDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTChartsheet getChartsheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartsheet ctChartsheet = (CTChartsheet)this.get_store().find_element_user(ChartsheetDocumentImpl.CHARTSHEET$0, 0);
            if (ctChartsheet == null) {
                return null;
            }
            return ctChartsheet;
        }
    }
    
    public void setChartsheet(final CTChartsheet ctChartsheet) {
        this.generatedSetterHelperImpl((XmlObject)ctChartsheet, ChartsheetDocumentImpl.CHARTSHEET$0, 0, (short)1);
    }
    
    public CTChartsheet addNewChartsheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartsheet)this.get_store().add_element_user(ChartsheetDocumentImpl.CHARTSHEET$0);
        }
    }
    
    static {
        CHARTSHEET$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "chartsheet");
    }
}
