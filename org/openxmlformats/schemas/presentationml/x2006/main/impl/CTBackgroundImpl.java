package org.openxmlformats.schemas.presentationml.x2006.main.impl;

import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.main.STBlackWhiteMode;
import org.openxmlformats.schemas.drawingml.x2006.main.CTStyleMatrixReference;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackgroundProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.presentationml.x2006.main.CTBackground;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBackgroundImpl extends XmlComplexContentImpl implements CTBackground
{
    private static final long serialVersionUID = 1L;
    private static final QName BGPR$0;
    private static final QName BGREF$2;
    private static final QName BWMODE$4;
    
    public CTBackgroundImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBackgroundProperties getBgPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBackgroundProperties ctBackgroundProperties = (CTBackgroundProperties)this.get_store().find_element_user(CTBackgroundImpl.BGPR$0, 0);
            if (ctBackgroundProperties == null) {
                return null;
            }
            return ctBackgroundProperties;
        }
    }
    
    public boolean isSetBgPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundImpl.BGPR$0) != 0;
        }
    }
    
    public void setBgPr(final CTBackgroundProperties ctBackgroundProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctBackgroundProperties, CTBackgroundImpl.BGPR$0, 0, (short)1);
    }
    
    public CTBackgroundProperties addNewBgPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBackgroundProperties)this.get_store().add_element_user(CTBackgroundImpl.BGPR$0);
        }
    }
    
    public void unsetBgPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundImpl.BGPR$0, 0);
        }
    }
    
    public CTStyleMatrixReference getBgRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyleMatrixReference ctStyleMatrixReference = (CTStyleMatrixReference)this.get_store().find_element_user(CTBackgroundImpl.BGREF$2, 0);
            if (ctStyleMatrixReference == null) {
                return null;
            }
            return ctStyleMatrixReference;
        }
    }
    
    public boolean isSetBgRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBackgroundImpl.BGREF$2) != 0;
        }
    }
    
    public void setBgRef(final CTStyleMatrixReference ctStyleMatrixReference) {
        this.generatedSetterHelperImpl((XmlObject)ctStyleMatrixReference, CTBackgroundImpl.BGREF$2, 0, (short)1);
    }
    
    public CTStyleMatrixReference addNewBgRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyleMatrixReference)this.get_store().add_element_user(CTBackgroundImpl.BGREF$2);
        }
    }
    
    public void unsetBgRef() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBackgroundImpl.BGREF$2, 0);
        }
    }
    
    public STBlackWhiteMode.Enum getBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.BWMODE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTBackgroundImpl.BWMODE$4);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STBlackWhiteMode.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STBlackWhiteMode xgetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBlackWhiteMode stBlackWhiteMode = (STBlackWhiteMode)this.get_store().find_attribute_user(CTBackgroundImpl.BWMODE$4);
            if (stBlackWhiteMode == null) {
                stBlackWhiteMode = (STBlackWhiteMode)this.get_default_attribute_value(CTBackgroundImpl.BWMODE$4);
            }
            return stBlackWhiteMode;
        }
    }
    
    public boolean isSetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTBackgroundImpl.BWMODE$4) != null;
        }
    }
    
    public void setBwMode(final STBlackWhiteMode.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTBackgroundImpl.BWMODE$4);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTBackgroundImpl.BWMODE$4);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetBwMode(final STBlackWhiteMode stBlackWhiteMode) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STBlackWhiteMode stBlackWhiteMode2 = (STBlackWhiteMode)this.get_store().find_attribute_user(CTBackgroundImpl.BWMODE$4);
            if (stBlackWhiteMode2 == null) {
                stBlackWhiteMode2 = (STBlackWhiteMode)this.get_store().add_attribute_user(CTBackgroundImpl.BWMODE$4);
            }
            stBlackWhiteMode2.set((XmlObject)stBlackWhiteMode);
        }
    }
    
    public void unsetBwMode() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTBackgroundImpl.BWMODE$4);
        }
    }
    
    static {
        BGPR$0 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "bgPr");
        BGREF$2 = new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "bgRef");
        BWMODE$4 = new QName("", "bwMode");
    }
}
