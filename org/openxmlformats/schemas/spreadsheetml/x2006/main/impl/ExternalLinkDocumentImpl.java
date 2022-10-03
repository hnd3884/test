package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalLink;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.ExternalLinkDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class ExternalLinkDocumentImpl extends XmlComplexContentImpl implements ExternalLinkDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTERNALLINK$0;
    
    public ExternalLinkDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExternalLink getExternalLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalLink ctExternalLink = (CTExternalLink)this.get_store().find_element_user(ExternalLinkDocumentImpl.EXTERNALLINK$0, 0);
            if (ctExternalLink == null) {
                return null;
            }
            return ctExternalLink;
        }
    }
    
    public void setExternalLink(final CTExternalLink ctExternalLink) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalLink, ExternalLinkDocumentImpl.EXTERNALLINK$0, 0, (short)1);
    }
    
    public CTExternalLink addNewExternalLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalLink)this.get_store().add_element_user(ExternalLinkDocumentImpl.EXTERNALLINK$0);
        }
    }
    
    static {
        EXTERNALLINK$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "externalLink");
    }
}
