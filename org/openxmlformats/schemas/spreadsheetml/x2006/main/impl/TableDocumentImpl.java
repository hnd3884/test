package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.TableDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TableDocumentImpl extends XmlComplexContentImpl implements TableDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName TABLE$0;
    
    public TableDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTable getTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTable ctTable = (CTTable)this.get_store().find_element_user(TableDocumentImpl.TABLE$0, 0);
            if (ctTable == null) {
                return null;
            }
            return ctTable;
        }
    }
    
    public void setTable(final CTTable ctTable) {
        this.generatedSetterHelperImpl((XmlObject)ctTable, TableDocumentImpl.TABLE$0, 0, (short)1);
    }
    
    public CTTable addNewTable() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTable)this.get_store().add_element_user(TableDocumentImpl.TABLE$0);
        }
    }
    
    static {
        TABLE$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "table");
    }
}
