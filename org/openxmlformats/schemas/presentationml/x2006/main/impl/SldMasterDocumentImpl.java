package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMaster;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.SldMasterDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SldMasterDocumentImpl extends XmlComplexContentImpl implements SldMasterDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SLDMASTER$0;
    
    public SldMasterDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTSlideMaster getSldMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSlideMaster ctSlideMaster = (CTSlideMaster)this.get_store().find_element_user(SldMasterDocumentImpl.SLDMASTER$0, 0);
            if (ctSlideMaster == null) {
                return null;
            }
            return ctSlideMaster;
        }
    }
    
    public void setSldMaster(final CTSlideMaster ctSlideMaster) {
        this.generatedSetterHelperImpl((XmlObject)ctSlideMaster, SldMasterDocumentImpl.SLDMASTER$0, 0, (short)1);
    }
    
    public CTSlideMaster addNewSldMaster() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSlideMaster)this.get_store().add_element_user(SldMasterDocumentImpl.SLDMASTER$0);
        }
    }
    
    static {
        SLDMASTER$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "sldMaster");
    }
}
