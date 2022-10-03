package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorScheme;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTColorSchemeImpl extends XmlComplexContentImpl implements CTColorScheme
{
    private static final long serialVersionUID = 1L;
    private static final QName DK1$0;
    private static final QName LT1$2;
    private static final QName DK2$4;
    private static final QName LT2$6;
    private static final QName ACCENT1$8;
    private static final QName ACCENT2$10;
    private static final QName ACCENT3$12;
    private static final QName ACCENT4$14;
    private static final QName ACCENT5$16;
    private static final QName ACCENT6$18;
    private static final QName HLINK$20;
    private static final QName FOLHLINK$22;
    private static final QName EXTLST$24;
    private static final QName NAME$26;
    
    public CTColorSchemeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTColor getDk1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.DK1$0, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setDk1(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.DK1$0, 0, (short)1);
    }
    
    public CTColor addNewDk1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.DK1$0);
        }
    }
    
    public CTColor getLt1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.LT1$2, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setLt1(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.LT1$2, 0, (short)1);
    }
    
    public CTColor addNewLt1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.LT1$2);
        }
    }
    
    public CTColor getDk2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.DK2$4, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setDk2(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.DK2$4, 0, (short)1);
    }
    
    public CTColor addNewDk2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.DK2$4);
        }
    }
    
    public CTColor getLt2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.LT2$6, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setLt2(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.LT2$6, 0, (short)1);
    }
    
    public CTColor addNewLt2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.LT2$6);
        }
    }
    
    public CTColor getAccent1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.ACCENT1$8, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setAccent1(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.ACCENT1$8, 0, (short)1);
    }
    
    public CTColor addNewAccent1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.ACCENT1$8);
        }
    }
    
    public CTColor getAccent2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.ACCENT2$10, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setAccent2(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.ACCENT2$10, 0, (short)1);
    }
    
    public CTColor addNewAccent2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.ACCENT2$10);
        }
    }
    
    public CTColor getAccent3() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.ACCENT3$12, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setAccent3(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.ACCENT3$12, 0, (short)1);
    }
    
    public CTColor addNewAccent3() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.ACCENT3$12);
        }
    }
    
    public CTColor getAccent4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.ACCENT4$14, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setAccent4(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.ACCENT4$14, 0, (short)1);
    }
    
    public CTColor addNewAccent4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.ACCENT4$14);
        }
    }
    
    public CTColor getAccent5() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.ACCENT5$16, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setAccent5(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.ACCENT5$16, 0, (short)1);
    }
    
    public CTColor addNewAccent5() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.ACCENT5$16);
        }
    }
    
    public CTColor getAccent6() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.ACCENT6$18, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setAccent6(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.ACCENT6$18, 0, (short)1);
    }
    
    public CTColor addNewAccent6() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.ACCENT6$18);
        }
    }
    
    public CTColor getHlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.HLINK$20, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setHlink(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.HLINK$20, 0, (short)1);
    }
    
    public CTColor addNewHlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.HLINK$20);
        }
    }
    
    public CTColor getFolHlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTColorSchemeImpl.FOLHLINK$22, 0);
            if (ctColor == null) {
                return null;
            }
            return ctColor;
        }
    }
    
    public void setFolHlink(final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTColorSchemeImpl.FOLHLINK$22, 0, (short)1);
    }
    
    public CTColor addNewFolHlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTColorSchemeImpl.FOLHLINK$22);
        }
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTColorSchemeImpl.EXTLST$24, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColorSchemeImpl.EXTLST$24) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTColorSchemeImpl.EXTLST$24, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTColorSchemeImpl.EXTLST$24);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColorSchemeImpl.EXTLST$24, 0);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorSchemeImpl.NAME$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTColorSchemeImpl.NAME$26);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorSchemeImpl.NAME$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorSchemeImpl.NAME$26);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTColorSchemeImpl.NAME$26);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTColorSchemeImpl.NAME$26);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    static {
        DK1$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "dk1");
        LT1$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lt1");
        DK2$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "dk2");
        LT2$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lt2");
        ACCENT1$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent1");
        ACCENT2$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent2");
        ACCENT3$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent3");
        ACCENT4$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent4");
        ACCENT5$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent5");
        ACCENT6$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "accent6");
        HLINK$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "hlink");
        FOLHLINK$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "folHlink");
        EXTLST$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        NAME$26 = new QName("", "name");
    }
}
