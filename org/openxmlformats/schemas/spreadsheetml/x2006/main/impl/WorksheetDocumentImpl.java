package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorksheetDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class WorksheetDocumentImpl extends XmlComplexContentImpl implements WorksheetDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName WORKSHEET$0;
    
    public WorksheetDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTWorksheet getWorksheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWorksheet ctWorksheet = (CTWorksheet)this.get_store().find_element_user(WorksheetDocumentImpl.WORKSHEET$0, 0);
            if (ctWorksheet == null) {
                return null;
            }
            return ctWorksheet;
        }
    }
    
    public void setWorksheet(final CTWorksheet ctWorksheet) {
        this.generatedSetterHelperImpl((XmlObject)ctWorksheet, WorksheetDocumentImpl.WORKSHEET$0, 0, (short)1);
    }
    
    public CTWorksheet addNewWorksheet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWorksheet)this.get_store().add_element_user(WorksheetDocumentImpl.WORKSHEET$0);
        }
    }
    
    static {
        WORKSHEET$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "worksheet");
    }
}
