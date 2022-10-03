package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleLink;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDdeLink;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalBook;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExternalLink;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTExternalLinkImpl extends XmlComplexContentImpl implements CTExternalLink
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTERNALBOOK$0;
    private static final QName DDELINK$2;
    private static final QName OLELINK$4;
    private static final QName EXTLST$6;
    
    public CTExternalLinkImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTExternalBook getExternalBook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalBook ctExternalBook = (CTExternalBook)this.get_store().find_element_user(CTExternalLinkImpl.EXTERNALBOOK$0, 0);
            if (ctExternalBook == null) {
                return null;
            }
            return ctExternalBook;
        }
    }
    
    public boolean isSetExternalBook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalLinkImpl.EXTERNALBOOK$0) != 0;
        }
    }
    
    public void setExternalBook(final CTExternalBook ctExternalBook) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalBook, CTExternalLinkImpl.EXTERNALBOOK$0, 0, (short)1);
    }
    
    public CTExternalBook addNewExternalBook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalBook)this.get_store().add_element_user(CTExternalLinkImpl.EXTERNALBOOK$0);
        }
    }
    
    public void unsetExternalBook() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalLinkImpl.EXTERNALBOOK$0, 0);
        }
    }
    
    public CTDdeLink getDdeLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDdeLink ctDdeLink = (CTDdeLink)this.get_store().find_element_user(CTExternalLinkImpl.DDELINK$2, 0);
            if (ctDdeLink == null) {
                return null;
            }
            return ctDdeLink;
        }
    }
    
    public boolean isSetDdeLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalLinkImpl.DDELINK$2) != 0;
        }
    }
    
    public void setDdeLink(final CTDdeLink ctDdeLink) {
        this.generatedSetterHelperImpl((XmlObject)ctDdeLink, CTExternalLinkImpl.DDELINK$2, 0, (short)1);
    }
    
    public CTDdeLink addNewDdeLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDdeLink)this.get_store().add_element_user(CTExternalLinkImpl.DDELINK$2);
        }
    }
    
    public void unsetDdeLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalLinkImpl.DDELINK$2, 0);
        }
    }
    
    public CTOleLink getOleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOleLink ctOleLink = (CTOleLink)this.get_store().find_element_user(CTExternalLinkImpl.OLELINK$4, 0);
            if (ctOleLink == null) {
                return null;
            }
            return ctOleLink;
        }
    }
    
    public boolean isSetOleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalLinkImpl.OLELINK$4) != 0;
        }
    }
    
    public void setOleLink(final CTOleLink ctOleLink) {
        this.generatedSetterHelperImpl((XmlObject)ctOleLink, CTExternalLinkImpl.OLELINK$4, 0, (short)1);
    }
    
    public CTOleLink addNewOleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOleLink)this.get_store().add_element_user(CTExternalLinkImpl.OLELINK$4);
        }
    }
    
    public void unsetOleLink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalLinkImpl.OLELINK$4, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTExternalLinkImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTExternalLinkImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTExternalLinkImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTExternalLinkImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTExternalLinkImpl.EXTLST$6, 0);
        }
    }
    
    static {
        EXTERNALBOOK$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "externalBook");
        DDELINK$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "ddeLink");
        OLELINK$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "oleLink");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
    }
}
