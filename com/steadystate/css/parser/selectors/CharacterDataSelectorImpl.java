package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.CharacterDataSelector;
import com.steadystate.css.parser.LocatableImpl;

public class CharacterDataSelectorImpl extends LocatableImpl implements CharacterDataSelector, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 4635511567927852889L;
    private String data_;
    
    public void setData(final String data) {
        this.data_ = data;
    }
    
    public CharacterDataSelectorImpl(final String data) {
        this.setData(data);
    }
    
    public short getSelectorType() {
        return 6;
    }
    
    public String getData() {
        return this.data_;
    }
    
    public String getCssText(final CSSFormat format) {
        final String data = this.getData();
        if (data == null) {
            return "";
        }
        return data;
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}
