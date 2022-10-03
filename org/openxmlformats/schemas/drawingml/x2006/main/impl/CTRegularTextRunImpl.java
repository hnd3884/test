package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRegularTextRunImpl extends XmlComplexContentImpl implements CTRegularTextRun
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    private static final QName T$2;
    
    public CTRegularTextRunImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextCharacterProperties getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextCharacterProperties ctTextCharacterProperties = (CTTextCharacterProperties)this.get_store().find_element_user(CTRegularTextRunImpl.RPR$0, 0);
            if (ctTextCharacterProperties == null) {
                return null;
            }
            return ctTextCharacterProperties;
        }
    }
    
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRegularTextRunImpl.RPR$0) != 0;
        }
    }
    
    public void setRPr(final CTTextCharacterProperties ctTextCharacterProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextCharacterProperties, CTRegularTextRunImpl.RPR$0, 0, (short)1);
    }
    
    public CTTextCharacterProperties addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextCharacterProperties)this.get_store().add_element_user(CTRegularTextRunImpl.RPR$0);
        }
    }
    
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRegularTextRunImpl.RPR$0, 0);
        }
    }
    
    public String getT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTRegularTextRunImpl.T$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetT() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTRegularTextRunImpl.T$2, 0);
        }
    }
    
    public void setT(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTRegularTextRunImpl.T$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTRegularTextRunImpl.T$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetT(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTRegularTextRunImpl.T$2, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTRegularTextRunImpl.T$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "rPr");
        T$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "t");
    }
}
