package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.TblStyleLstDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class TblStyleLstDocumentImpl extends XmlComplexContentImpl implements TblStyleLstDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName TBLSTYLELST$0;
    
    public TblStyleLstDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTableStyleList getTblStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableStyleList list = (CTTableStyleList)this.get_store().find_element_user(TblStyleLstDocumentImpl.TBLSTYLELST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setTblStyleLst(final CTTableStyleList list) {
        this.generatedSetterHelperImpl((XmlObject)list, TblStyleLstDocumentImpl.TBLSTYLELST$0, 0, (short)1);
    }
    
    public CTTableStyleList addNewTblStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableStyleList)this.get_store().add_element_user(TblStyleLstDocumentImpl.TBLSTYLELST$0);
        }
    }
    
    static {
        TBLSTYLELST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "tblStyleLst");
    }
}
