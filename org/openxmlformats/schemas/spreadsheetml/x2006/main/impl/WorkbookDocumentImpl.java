package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorkbook;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorkbookDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class WorkbookDocumentImpl extends XmlComplexContentImpl implements WorkbookDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName WORKBOOK$0;
    
    public WorkbookDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTWorkbook getWorkbook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTWorkbook ctWorkbook = (CTWorkbook)this.get_store().find_element_user(WorkbookDocumentImpl.WORKBOOK$0, 0);
            if (ctWorkbook == null) {
                return null;
            }
            return ctWorkbook;
        }
    }
    
    public void setWorkbook(final CTWorkbook ctWorkbook) {
        this.generatedSetterHelperImpl((XmlObject)ctWorkbook, WorkbookDocumentImpl.WORKBOOK$0, 0, (short)1);
    }
    
    public CTWorkbook addNewWorkbook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTWorkbook)this.get_store().add_element_user(WorkbookDocumentImpl.WORKBOOK$0);
        }
    }
    
    static {
        WORKBOOK$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "workbook");
    }
}
