package org.apache.taglibs.standard.tag.common.fmt;

import java.text.SimpleDateFormat;
import javax.servlet.jsp.JspException;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import java.util.TimeZone;
import javax.servlet.jsp.tagext.Tag;
import java.text.DateFormat;
import org.apache.taglibs.standard.tag.common.core.Util;
import java.util.Date;
import javax.servlet.jsp.tagext.TagSupport;

public abstract class FormatDateSupport extends TagSupport
{
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String DATETIME = "both";
    protected Date value;
    protected String type;
    protected String pattern;
    protected Object timeZone;
    protected String dateStyle;
    protected String timeStyle;
    private String var;
    private int scope;
    
    public FormatDateSupport() {
        this.init();
    }
    
    private void init() {
        final String type = null;
        this.timeStyle = type;
        this.dateStyle = type;
        this.type = type;
        final String s = null;
        this.var = s;
        this.pattern = s;
        this.value = null;
        this.timeZone = null;
        this.scope = 1;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public int doEndTag() throws JspException {
        String formatted = null;
        if (this.value == null) {
            if (this.var != null) {
                this.pageContext.removeAttribute(this.var, this.scope);
            }
            return 6;
        }
        final Locale locale = SetLocaleSupport.getFormattingLocale(this.pageContext, (Tag)this, true, DateFormat.getAvailableLocales());
        if (locale != null) {
            final DateFormat formatter = this.createFormatter(locale, this.pattern);
            TimeZone tz = null;
            if (this.timeZone instanceof String && ((String)this.timeZone).equals("")) {
                this.timeZone = null;
            }
            if (this.timeZone != null) {
                if (this.timeZone instanceof String) {
                    tz = TimeZone.getTimeZone((String)this.timeZone);
                }
                else {
                    if (!(this.timeZone instanceof TimeZone)) {
                        throw new JspTagException(Resources.getMessage("FORMAT_DATE_BAD_TIMEZONE"));
                    }
                    tz = (TimeZone)this.timeZone;
                }
            }
            else {
                tz = TimeZoneSupport.getTimeZone(this.pageContext, (Tag)this);
            }
            if (tz != null) {
                formatter.setTimeZone(tz);
            }
            formatted = formatter.format(this.value);
        }
        else {
            formatted = this.value.toString();
        }
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, (Object)formatted, this.scope);
        }
        else {
            try {
                this.pageContext.getOut().print(formatted);
            }
            catch (final IOException ioe) {
                throw new JspTagException(ioe.toString(), (Throwable)ioe);
            }
        }
        return 6;
    }
    
    public void release() {
        this.init();
    }
    
    private DateFormat createFormatter(final Locale loc, final String pattern) throws JspException {
        if (pattern != null) {
            return new SimpleDateFormat(pattern, loc);
        }
        if (this.type == null || "date".equalsIgnoreCase(this.type)) {
            final int style = Util.getStyle(this.dateStyle, "FORMAT_DATE_INVALID_DATE_STYLE");
            return DateFormat.getDateInstance(style, loc);
        }
        if ("time".equalsIgnoreCase(this.type)) {
            final int style = Util.getStyle(this.timeStyle, "FORMAT_DATE_INVALID_TIME_STYLE");
            return DateFormat.getTimeInstance(style, loc);
        }
        if ("both".equalsIgnoreCase(this.type)) {
            final int style2 = Util.getStyle(this.dateStyle, "FORMAT_DATE_INVALID_DATE_STYLE");
            final int style3 = Util.getStyle(this.timeStyle, "FORMAT_DATE_INVALID_TIME_STYLE");
            return DateFormat.getDateTimeInstance(style2, style3, loc);
        }
        throw new JspException(Resources.getMessage("FORMAT_DATE_INVALID_TYPE", this.type));
    }
}
