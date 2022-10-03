package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyContent;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRubyPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRuby;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRubyImpl extends XmlComplexContentImpl implements CTRuby
{
    private static final long serialVersionUID = 1L;
    private static final QName RUBYPR$0;
    private static final QName RT$2;
    private static final QName RUBYBASE$4;
    
    public CTRubyImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRubyPr getRubyPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRubyPr ctRubyPr = (CTRubyPr)this.get_store().find_element_user(CTRubyImpl.RUBYPR$0, 0);
            if (ctRubyPr == null) {
                return null;
            }
            return ctRubyPr;
        }
    }
    
    public void setRubyPr(final CTRubyPr ctRubyPr) {
        this.generatedSetterHelperImpl((XmlObject)ctRubyPr, CTRubyImpl.RUBYPR$0, 0, (short)1);
    }
    
    public CTRubyPr addNewRubyPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRubyPr)this.get_store().add_element_user(CTRubyImpl.RUBYPR$0);
        }
    }
    
    public CTRubyContent getRt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRubyContent ctRubyContent = (CTRubyContent)this.get_store().find_element_user(CTRubyImpl.RT$2, 0);
            if (ctRubyContent == null) {
                return null;
            }
            return ctRubyContent;
        }
    }
    
    public void setRt(final CTRubyContent ctRubyContent) {
        this.generatedSetterHelperImpl((XmlObject)ctRubyContent, CTRubyImpl.RT$2, 0, (short)1);
    }
    
    public CTRubyContent addNewRt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRubyContent)this.get_store().add_element_user(CTRubyImpl.RT$2);
        }
    }
    
    public CTRubyContent getRubyBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRubyContent ctRubyContent = (CTRubyContent)this.get_store().find_element_user(CTRubyImpl.RUBYBASE$4, 0);
            if (ctRubyContent == null) {
                return null;
            }
            return ctRubyContent;
        }
    }
    
    public void setRubyBase(final CTRubyContent ctRubyContent) {
        this.generatedSetterHelperImpl((XmlObject)ctRubyContent, CTRubyImpl.RUBYBASE$4, 0, (short)1);
    }
    
    public CTRubyContent addNewRubyBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRubyContent)this.get_store().add_element_user(CTRubyImpl.RUBYBASE$4);
        }
    }
    
    static {
        RUBYPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rubyPr");
        RT$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rt");
        RUBYBASE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rubyBase");
    }
}
