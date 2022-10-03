package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBackgroundFillStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectStyleList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTLineStyleList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTFillStyleList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrix;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTStyleMatrixImpl extends XmlComplexContentImpl implements CTStyleMatrix
{
    private static final long serialVersionUID = 1L;
    private static final QName FILLSTYLELST$0;
    private static final QName LNSTYLELST$2;
    private static final QName EFFECTSTYLELST$4;
    private static final QName BGFILLSTYLELST$6;
    private static final QName NAME$8;
    
    public CTStyleMatrixImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTFillStyleList getFillStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFillStyleList list = (CTFillStyleList)this.get_store().find_element_user(CTStyleMatrixImpl.FILLSTYLELST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setFillStyleLst(final CTFillStyleList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTStyleMatrixImpl.FILLSTYLELST$0, 0, (short)1);
    }
    
    public CTFillStyleList addNewFillStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFillStyleList)this.get_store().add_element_user(CTStyleMatrixImpl.FILLSTYLELST$0);
        }
    }
    
    public CTLineStyleList getLnStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineStyleList list = (CTLineStyleList)this.get_store().find_element_user(CTStyleMatrixImpl.LNSTYLELST$2, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setLnStyleLst(final CTLineStyleList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTStyleMatrixImpl.LNSTYLELST$2, 0, (short)1);
    }
    
    public CTLineStyleList addNewLnStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineStyleList)this.get_store().add_element_user(CTStyleMatrixImpl.LNSTYLELST$2);
        }
    }
    
    public CTEffectStyleList getEffectStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEffectStyleList list = (CTEffectStyleList)this.get_store().find_element_user(CTStyleMatrixImpl.EFFECTSTYLELST$4, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setEffectStyleLst(final CTEffectStyleList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTStyleMatrixImpl.EFFECTSTYLELST$4, 0, (short)1);
    }
    
    public CTEffectStyleList addNewEffectStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEffectStyleList)this.get_store().add_element_user(CTStyleMatrixImpl.EFFECTSTYLELST$4);
        }
    }
    
    public CTBackgroundFillStyleList getBgFillStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBackgroundFillStyleList list = (CTBackgroundFillStyleList)this.get_store().find_element_user(CTStyleMatrixImpl.BGFILLSTYLELST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public void setBgFillStyleLst(final CTBackgroundFillStyleList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTStyleMatrixImpl.BGFILLSTYLELST$6, 0, (short)1);
    }
    
    public CTBackgroundFillStyleList addNewBgFillStyleLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBackgroundFillStyleList)this.get_store().add_element_user(CTStyleMatrixImpl.BGFILLSTYLELST$6);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleMatrixImpl.NAME$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTStyleMatrixImpl.NAME$8);
            }
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString = (XmlString)this.get_store().find_attribute_user(CTStyleMatrixImpl.NAME$8);
            if (xmlString == null) {
                xmlString = (XmlString)this.get_default_attribute_value(CTStyleMatrixImpl.NAME$8);
            }
            return xmlString;
        }
    }
    
    public boolean isSetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTStyleMatrixImpl.NAME$8) != null;
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTStyleMatrixImpl.NAME$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTStyleMatrixImpl.NAME$8);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTStyleMatrixImpl.NAME$8);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTStyleMatrixImpl.NAME$8);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTStyleMatrixImpl.NAME$8);
        }
    }
    
    static {
        FILLSTYLELST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fillStyleLst");
        LNSTYLELST$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lnStyleLst");
        EFFECTSTYLELST$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "effectStyleLst");
        BGFILLSTYLELST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "bgFillStyleLst");
        NAME$8 = new QName("", "name");
    }
}
