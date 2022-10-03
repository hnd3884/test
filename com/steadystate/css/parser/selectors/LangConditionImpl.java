package com.steadystate.css.parser.selectors;

import com.steadystate.css.format.CSSFormat;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.css.sac.LangCondition;
import com.steadystate.css.parser.LocatableImpl;

public class LangConditionImpl extends LocatableImpl implements LangCondition, CSSFormatable, Serializable
{
    private static final long serialVersionUID = 1701599531953055387L;
    private String lang_;
    
    public void setLang(final String lang) {
        this.lang_ = lang;
    }
    
    public LangConditionImpl(final String lang) {
        this.setLang(lang);
    }
    
    public short getConditionType() {
        return 6;
    }
    
    public String getLang() {
        return this.lang_;
    }
    
    public String getCssText(final CSSFormat format) {
        final StringBuilder result = new StringBuilder();
        result.append(":lang(");
        final String lang = this.getLang();
        if (null != lang) {
            result.append(lang);
        }
        result.append(")");
        return result.toString();
    }
    
    public String toString() {
        return this.getCssText(null);
    }
}
