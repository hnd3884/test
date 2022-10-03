package org.apache.taglibs.standard.tag.common.fmt;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.Config;
import java.util.TimeZone;
import org.apache.taglibs.standard.tag.common.core.Util;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class SetTimeZoneSupport extends TagSupport
{
    protected Object value;
    private int scope;
    private String var;
    
    public SetTimeZoneSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.var = s;
        this.value = s;
        this.scope = 1;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public int doEndTag() throws JspException {
        TimeZone timeZone = null;
        if (this.value == null) {
            timeZone = TimeZone.getTimeZone("GMT");
        }
        else if (this.value instanceof String) {
            if (((String)this.value).trim().equals("")) {
                timeZone = TimeZone.getTimeZone("GMT");
            }
            else {
                timeZone = TimeZone.getTimeZone((String)this.value);
            }
        }
        else {
            timeZone = (TimeZone)this.value;
        }
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, (Object)timeZone, this.scope);
        }
        else {
            Config.set(this.pageContext, "javax.servlet.jsp.jstl.fmt.timeZone", (Object)timeZone, this.scope);
        }
        return 6;
    }
    
    public void release() {
        this.init();
    }
}
