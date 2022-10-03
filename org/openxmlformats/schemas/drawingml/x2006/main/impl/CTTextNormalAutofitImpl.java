package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPercent;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontScalePercent;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextNormalAutofit;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextNormalAutofitImpl extends XmlComplexContentImpl implements CTTextNormalAutofit
{
    private static final long serialVersionUID = 1L;
    private static final QName FONTSCALE$0;
    private static final QName LNSPCREDUCTION$2;
    
    public CTTextNormalAutofitImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public int getFontScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextNormalAutofitImpl.FONTSCALE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextNormalAutofitImpl.FONTSCALE$0);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextFontScalePercent xgetFontScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextFontScalePercent stTextFontScalePercent = (STTextFontScalePercent)this.get_store().find_attribute_user(CTTextNormalAutofitImpl.FONTSCALE$0);
            if (stTextFontScalePercent == null) {
                stTextFontScalePercent = (STTextFontScalePercent)this.get_default_attribute_value(CTTextNormalAutofitImpl.FONTSCALE$0);
            }
            return stTextFontScalePercent;
        }
    }
    
    public boolean isSetFontScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextNormalAutofitImpl.FONTSCALE$0) != null;
        }
    }
    
    public void setFontScale(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextNormalAutofitImpl.FONTSCALE$0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextNormalAutofitImpl.FONTSCALE$0);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetFontScale(final STTextFontScalePercent stTextFontScalePercent) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextFontScalePercent stTextFontScalePercent2 = (STTextFontScalePercent)this.get_store().find_attribute_user(CTTextNormalAutofitImpl.FONTSCALE$0);
            if (stTextFontScalePercent2 == null) {
                stTextFontScalePercent2 = (STTextFontScalePercent)this.get_store().add_attribute_user(CTTextNormalAutofitImpl.FONTSCALE$0);
            }
            stTextFontScalePercent2.set((XmlObject)stTextFontScalePercent);
        }
    }
    
    public void unsetFontScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextNormalAutofitImpl.FONTSCALE$0);
        }
    }
    
    public int getLnSpcReduction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
            }
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public STTextSpacingPercent xgetLnSpcReduction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextSpacingPercent stTextSpacingPercent = (STTextSpacingPercent)this.get_store().find_attribute_user(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
            if (stTextSpacingPercent == null) {
                stTextSpacingPercent = (STTextSpacingPercent)this.get_default_attribute_value(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
            }
            return stTextSpacingPercent;
        }
    }
    
    public boolean isSetLnSpcReduction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTextNormalAutofitImpl.LNSPCREDUCTION$2) != null;
        }
    }
    
    public void setLnSpcReduction(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetLnSpcReduction(final STTextSpacingPercent stTextSpacingPercent) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTextSpacingPercent stTextSpacingPercent2 = (STTextSpacingPercent)this.get_store().find_attribute_user(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
            if (stTextSpacingPercent2 == null) {
                stTextSpacingPercent2 = (STTextSpacingPercent)this.get_store().add_attribute_user(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
            }
            stTextSpacingPercent2.set((XmlObject)stTextSpacingPercent);
        }
    }
    
    public void unsetLnSpcReduction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTextNormalAutofitImpl.LNSPCREDUCTION$2);
        }
    }
    
    static {
        FONTSCALE$0 = new QName("", "fontScale");
        LNSPCREDUCTION$2 = new QName("", "lnSpcReduction");
    }
}
