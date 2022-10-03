package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTFontReference;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTShapeStyleImpl extends XmlComplexContentImpl implements CTShapeStyle
{
    private static final long serialVersionUID = 1L;
    private static final QName LNREF$0;
    private static final QName FILLREF$2;
    private static final QName EFFECTREF$4;
    private static final QName FONTREF$6;
    
    public CTShapeStyleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTStyleMatrixReference getLnRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyleMatrixReference ctStyleMatrixReference = (CTStyleMatrixReference)this.get_store().find_element_user(CTShapeStyleImpl.LNREF$0, 0);
            if (ctStyleMatrixReference == null) {
                return null;
            }
            return ctStyleMatrixReference;
        }
    }
    
    public void setLnRef(final CTStyleMatrixReference ctStyleMatrixReference) {
        this.generatedSetterHelperImpl((XmlObject)ctStyleMatrixReference, CTShapeStyleImpl.LNREF$0, 0, (short)1);
    }
    
    public CTStyleMatrixReference addNewLnRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyleMatrixReference)this.get_store().add_element_user(CTShapeStyleImpl.LNREF$0);
        }
    }
    
    public CTStyleMatrixReference getFillRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyleMatrixReference ctStyleMatrixReference = (CTStyleMatrixReference)this.get_store().find_element_user(CTShapeStyleImpl.FILLREF$2, 0);
            if (ctStyleMatrixReference == null) {
                return null;
            }
            return ctStyleMatrixReference;
        }
    }
    
    public void setFillRef(final CTStyleMatrixReference ctStyleMatrixReference) {
        this.generatedSetterHelperImpl((XmlObject)ctStyleMatrixReference, CTShapeStyleImpl.FILLREF$2, 0, (short)1);
    }
    
    public CTStyleMatrixReference addNewFillRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyleMatrixReference)this.get_store().add_element_user(CTShapeStyleImpl.FILLREF$2);
        }
    }
    
    public CTStyleMatrixReference getEffectRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyleMatrixReference ctStyleMatrixReference = (CTStyleMatrixReference)this.get_store().find_element_user(CTShapeStyleImpl.EFFECTREF$4, 0);
            if (ctStyleMatrixReference == null) {
                return null;
            }
            return ctStyleMatrixReference;
        }
    }
    
    public void setEffectRef(final CTStyleMatrixReference ctStyleMatrixReference) {
        this.generatedSetterHelperImpl((XmlObject)ctStyleMatrixReference, CTShapeStyleImpl.EFFECTREF$4, 0, (short)1);
    }
    
    public CTStyleMatrixReference addNewEffectRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyleMatrixReference)this.get_store().add_element_user(CTShapeStyleImpl.EFFECTREF$4);
        }
    }
    
    public CTFontReference getFontRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontReference ctFontReference = (CTFontReference)this.get_store().find_element_user(CTShapeStyleImpl.FONTREF$6, 0);
            if (ctFontReference == null) {
                return null;
            }
            return ctFontReference;
        }
    }
    
    public void setFontRef(final CTFontReference ctFontReference) {
        this.generatedSetterHelperImpl((XmlObject)ctFontReference, CTShapeStyleImpl.FONTREF$6, 0, (short)1);
    }
    
    public CTFontReference addNewFontRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontReference)this.get_store().add_element_user(CTShapeStyleImpl.FONTREF$6);
        }
    }
    
    static {
        LNREF$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnRef");
        FILLREF$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillRef");
        EFFECTREF$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectRef");
        FONTREF$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fontRef");
    }
}
