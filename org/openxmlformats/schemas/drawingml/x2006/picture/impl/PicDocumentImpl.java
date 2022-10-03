package org.openxmlformats.schemas.drawingml.x2006.picture.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.picture.PicDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class PicDocumentImpl extends XmlComplexContentImpl implements PicDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName PIC$0;
    
    public PicDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPicture getPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPicture ctPicture = (CTPicture)this.get_store().find_element_user(PicDocumentImpl.PIC$0, 0);
            if (ctPicture == null) {
                return null;
            }
            return ctPicture;
        }
    }
    
    public void setPic(final CTPicture ctPicture) {
        this.generatedSetterHelperImpl((XmlObject)ctPicture, PicDocumentImpl.PIC$0, 0, (short)1);
    }
    
    public CTPicture addNewPic() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPicture)this.get_store().add_element_user(PicDocumentImpl.PIC$0);
        }
    }
    
    static {
        PIC$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/picture", "pic");
    }
}
