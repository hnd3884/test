package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextLineBreakImpl extends XmlComplexContentImpl implements CTTextLineBreak
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    
    public CTTextLineBreakImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextCharacterProperties getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextCharacterProperties ctTextCharacterProperties = (CTTextCharacterProperties)this.get_store().find_element_user(CTTextLineBreakImpl.RPR$0, 0);
            if (ctTextCharacterProperties == null) {
                return null;
            }
            return ctTextCharacterProperties;
        }
    }
    
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextLineBreakImpl.RPR$0) != 0;
        }
    }
    
    public void setRPr(final CTTextCharacterProperties ctTextCharacterProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextCharacterProperties, CTTextLineBreakImpl.RPR$0, 0, (short)1);
    }
    
    public CTTextCharacterProperties addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextCharacterProperties)this.get_store().add_element_user(CTTextLineBreakImpl.RPR$0);
        }
    }
    
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextLineBreakImpl.RPR$0, 0);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "rPr");
    }
}
