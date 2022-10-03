package org.apache.taglibs.standard.tag.common.fmt;

import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import java.util.TimeZone;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class TimeZoneSupport extends BodyTagSupport
{
    protected Object value;
    private TimeZone timeZone;
    
    public TimeZoneSupport() {
        this.init();
    }
    
    private void init() {
        this.value = null;
    }
    
    public TimeZone getTimeZone() {
        return this.timeZone;
    }
    
    public int doStartTag() throws JspException {
        if (this.value == null) {
            this.timeZone = TimeZone.getTimeZone("GMT");
        }
        else if (this.value instanceof String) {
            if (((String)this.value).trim().equals("")) {
                this.timeZone = TimeZone.getTimeZone("GMT");
            }
            else {
                this.timeZone = TimeZone.getTimeZone((String)this.value);
            }
        }
        else {
            this.timeZone = (TimeZone)this.value;
        }
        return 2;
    }
    
    public int doEndTag() throws JspException {
        try {
            this.pageContext.getOut().print(this.bodyContent.getString());
        }
        catch (final IOException ioe) {
            throw new JspTagException(ioe.toString(), (Throwable)ioe);
        }
        return 6;
    }
    
    public void release() {
        this.init();
    }
    
    static TimeZone getTimeZone(final PageContext pc, final Tag fromTag) {
        TimeZone tz = null;
        final Tag t = findAncestorWithClass(fromTag, (Class)TimeZoneSupport.class);
        if (t != null) {
            final TimeZoneSupport parent = (TimeZoneSupport)t;
            tz = parent.getTimeZone();
        }
        else {
            final Object obj = Config.find(pc, "javax.servlet.jsp.jstl.fmt.timeZone");
            if (obj != null) {
                if (obj instanceof TimeZone) {
                    tz = (TimeZone)obj;
                }
                else {
                    tz = TimeZone.getTimeZone((String)obj);
                }
            }
        }
        return tz;
    }
}
