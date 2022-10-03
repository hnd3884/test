package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCells;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.SingleXmlCellsDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SingleXmlCellsDocumentImpl extends XmlComplexContentImpl implements SingleXmlCellsDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SINGLEXMLCELLS$0;
    
    public SingleXmlCellsDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSingleXmlCells getSingleXmlCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSingleXmlCells ctSingleXmlCells = (CTSingleXmlCells)this.get_store().find_element_user(SingleXmlCellsDocumentImpl.SINGLEXMLCELLS$0, 0);
            if (ctSingleXmlCells == null) {
                return null;
            }
            return ctSingleXmlCells;
        }
    }
    
    public void setSingleXmlCells(final CTSingleXmlCells ctSingleXmlCells) {
        this.generatedSetterHelperImpl((XmlObject)ctSingleXmlCells, SingleXmlCellsDocumentImpl.SINGLEXMLCELLS$0, 0, (short)1);
    }
    
    public CTSingleXmlCells addNewSingleXmlCells() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSingleXmlCells)this.get_store().add_element_user(SingleXmlCellsDocumentImpl.SINGLEXMLCELLS$0);
        }
    }
    
    static {
        SINGLEXMLCELLS$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "singleXmlCells");
    }
}
