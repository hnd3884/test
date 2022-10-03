package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTControl;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRel;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPicture;

public class CTPictureImpl extends CTPictureBaseImpl implements CTPicture
{
    private static final long serialVersionUID = 1L;
    private static final QName MOVIE$0;
    private static final QName CONTROL$2;
    
    public CTPictureImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTRel getMovie() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRel ctRel = (CTRel)this.get_store().find_element_user(CTPictureImpl.MOVIE$0, 0);
            if (ctRel == null) {
                return null;
            }
            return ctRel;
        }
    }
    
    @Override
    public boolean isSetMovie() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPictureImpl.MOVIE$0) != 0;
        }
    }
    
    @Override
    public void setMovie(final CTRel ctRel) {
        this.generatedSetterHelperImpl((XmlObject)ctRel, CTPictureImpl.MOVIE$0, 0, (short)1);
    }
    
    @Override
    public CTRel addNewMovie() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRel)this.get_store().add_element_user(CTPictureImpl.MOVIE$0);
        }
    }
    
    @Override
    public void unsetMovie() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPictureImpl.MOVIE$0, 0);
        }
    }
    
    @Override
    public CTControl getControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTControl ctControl = (CTControl)this.get_store().find_element_user(CTPictureImpl.CONTROL$2, 0);
            if (ctControl == null) {
                return null;
            }
            return ctControl;
        }
    }
    
    @Override
    public boolean isSetControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPictureImpl.CONTROL$2) != 0;
        }
    }
    
    @Override
    public void setControl(final CTControl ctControl) {
        this.generatedSetterHelperImpl((XmlObject)ctControl, CTPictureImpl.CONTROL$2, 0, (short)1);
    }
    
    @Override
    public CTControl addNewControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTControl)this.get_store().add_element_user(CTPictureImpl.CONTROL$2);
        }
    }
    
    @Override
    public void unsetControl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPictureImpl.CONTROL$2, 0);
        }
    }
    
    static {
        MOVIE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "movie");
        CONTROL$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "control");
    }
}
