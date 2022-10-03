package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STColorSchemeIndex;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTOfficeArtExtensionList;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTColorMappingImpl extends XmlComplexContentImpl implements CTColorMapping
{
    private static final long serialVersionUID = 1L;
    private static final QName EXTLST$0;
    private static final QName BG1$2;
    private static final QName TX1$4;
    private static final QName BG2$6;
    private static final QName TX2$8;
    private static final QName ACCENT1$10;
    private static final QName ACCENT2$12;
    private static final QName ACCENT3$14;
    private static final QName ACCENT4$16;
    private static final QName ACCENT5$18;
    private static final QName ACCENT6$20;
    private static final QName HLINK$22;
    private static final QName FOLHLINK$24;
    
    public CTColorMappingImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTOfficeArtExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOfficeArtExtensionList list = (CTOfficeArtExtensionList)this.get_store().find_element_user(CTColorMappingImpl.EXTLST$0, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTColorMappingImpl.EXTLST$0) != 0;
        }
    }
    
    public void setExtLst(final CTOfficeArtExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTColorMappingImpl.EXTLST$0, 0, (short)1);
    }
    
    public CTOfficeArtExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOfficeArtExtensionList)this.get_store().add_element_user(CTColorMappingImpl.EXTLST$0);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTColorMappingImpl.EXTLST$0, 0);
        }
    }
    
    public STColorSchemeIndex.Enum getBg1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.BG1$2);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetBg1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.BG1$2);
        }
    }
    
    public void setBg1(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.BG1$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.BG1$2);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBg1(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.BG1$2);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.BG1$2);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getTx1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.TX1$4);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetTx1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.TX1$4);
        }
    }
    
    public void setTx1(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.TX1$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.TX1$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetTx1(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.TX1$4);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.TX1$4);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getBg2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.BG2$6);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetBg2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.BG2$6);
        }
    }
    
    public void setBg2(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.BG2$6);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.BG2$6);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBg2(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.BG2$6);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.BG2$6);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getTx2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.TX2$8);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetTx2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.TX2$8);
        }
    }
    
    public void setTx2(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.TX2$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.TX2$8);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetTx2(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.TX2$8);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.TX2$8);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getAccent1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT1$10);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetAccent1() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT1$10);
        }
    }
    
    public void setAccent1(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT1$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT1$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAccent1(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT1$10);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT1$10);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getAccent2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT2$12);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetAccent2() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT2$12);
        }
    }
    
    public void setAccent2(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT2$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT2$12);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAccent2(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT2$12);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT2$12);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getAccent3() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT3$14);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetAccent3() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT3$14);
        }
    }
    
    public void setAccent3(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT3$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT3$14);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAccent3(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT3$14);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT3$14);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getAccent4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT4$16);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetAccent4() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT4$16);
        }
    }
    
    public void setAccent4(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT4$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT4$16);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAccent4(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT4$16);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT4$16);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getAccent5() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT5$18);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetAccent5() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT5$18);
        }
    }
    
    public void setAccent5(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT5$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT5$18);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAccent5(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT5$18);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT5$18);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getAccent6() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT6$20);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetAccent6() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT6$20);
        }
    }
    
    public void setAccent6(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT6$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT6$20);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetAccent6(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.ACCENT6$20);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.ACCENT6$20);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getHlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.HLINK$22);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetHlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.HLINK$22);
        }
    }
    
    public void setHlink(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.HLINK$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.HLINK$22);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetHlink(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.HLINK$22);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.HLINK$22);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    public STColorSchemeIndex.Enum getFolHlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.FOLHLINK$24);
            if (simpleValue == null) {
                return null;
            }
            return (STColorSchemeIndex.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STColorSchemeIndex xgetFolHlink() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.FOLHLINK$24);
        }
    }
    
    public void setFolHlink(final STColorSchemeIndex.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTColorMappingImpl.FOLHLINK$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTColorMappingImpl.FOLHLINK$24);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetFolHlink(final STColorSchemeIndex stColorSchemeIndex) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STColorSchemeIndex stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().find_attribute_user(CTColorMappingImpl.FOLHLINK$24);
            if (stColorSchemeIndex2 == null) {
                stColorSchemeIndex2 = (STColorSchemeIndex)this.get_store().add_attribute_user(CTColorMappingImpl.FOLHLINK$24);
            }
            stColorSchemeIndex2.set((XmlObject)stColorSchemeIndex);
        }
    }
    
    static {
        EXTLST$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "extLst");
        BG1$2 = new QName("", "bg1");
        TX1$4 = new QName("", "tx1");
        BG2$6 = new QName("", "bg2");
        TX2$8 = new QName("", "tx2");
        ACCENT1$10 = new QName("", "accent1");
        ACCENT2$12 = new QName("", "accent2");
        ACCENT3$14 = new QName("", "accent3");
        ACCENT4$16 = new QName("", "accent4");
        ACCENT5$18 = new QName("", "accent5");
        ACCENT6$20 = new QName("", "accent6");
        HLINK$22 = new QName("", "hlink");
        FOLHLINK$24 = new QName("", "folHlink");
    }
}
