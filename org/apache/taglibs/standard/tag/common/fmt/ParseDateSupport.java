package org.apache.taglibs.standard.tag.common.fmt;

import java.util.Date;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import java.text.ParseException;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.tagext.Tag;
import java.text.DateFormat;
import org.apache.taglibs.standard.tag.common.core.Util;
import java.util.Locale;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class ParseDateSupport extends BodyTagSupport
{
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String DATETIME = "both";
    protected String value;
    protected boolean valueSpecified;
    protected String type;
    protected String pattern;
    protected Object timeZone;
    protected Locale parseLocale;
    protected String dateStyle;
    protected String timeStyle;
    private String var;
    private int scope;
    
    public ParseDateSupport() {
        this.init();
    }
    
    private void init() {
        final String type = null;
        this.timeStyle = type;
        this.dateStyle = type;
        this.type = type;
        final String value = null;
        this.var = value;
        this.pattern = value;
        this.value = value;
        this.valueSpecified = false;
        this.timeZone = null;
        this.scope = 1;
        this.parseLocale = null;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public int doEndTag() throws JspException {
        String input = null;
        if (this.valueSpecified) {
            input = this.value;
        }
        else if (this.bodyContent != null && this.bodyContent.getString() != null) {
            input = this.bodyContent.getString().trim();
        }
        if (input == null || input.equals("")) {
            if (this.var != null) {
                this.pageContext.removeAttribute(this.var, this.scope);
            }
            return 6;
        }
        Locale locale = this.parseLocale;
        if (locale == null) {
            locale = SetLocaleSupport.getFormattingLocale(this.pageContext, (Tag)this, false, DateFormat.getAvailableLocales());
        }
        if (locale == null) {
            throw new JspException(Resources.getMessage("PARSE_DATE_NO_PARSE_LOCALE"));
        }
        DateFormat parser = this.createParser(locale);
        if (this.pattern != null) {
            try {
                ((SimpleDateFormat)parser).applyPattern(this.pattern);
            }
            catch (final ClassCastException cce) {
                parser = new SimpleDateFormat(this.pattern, locale);
            }
        }
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
                    throw new JspException(Resources.getMessage("PARSE_DATE_BAD_TIMEZONE"));
                }
                tz = (TimeZone)this.timeZone;
            }
        }
        else {
            tz = TimeZoneSupport.getTimeZone(this.pageContext, (Tag)this);
        }
        if (tz != null) {
            parser.setTimeZone(tz);
        }
        Date parsed = null;
        try {
            parsed = parser.parse(input);
        }
        catch (final ParseException pe) {
            throw new JspException(Resources.getMessage("PARSE_DATE_PARSE_ERROR", input), (Throwable)pe);
        }
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, (Object)parsed, this.scope);
        }
        else {
            try {
                this.pageContext.getOut().print((Object)parsed);
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
    
    private DateFormat createParser(final Locale loc) throws JspException {
        DateFormat parser = null;
        if (this.type == null || "date".equalsIgnoreCase(this.type)) {
            parser = DateFormat.getDateInstance(Util.getStyle(this.dateStyle, "PARSE_DATE_INVALID_DATE_STYLE"), loc);
        }
        else if ("time".equalsIgnoreCase(this.type)) {
            parser = DateFormat.getTimeInstance(Util.getStyle(this.timeStyle, "PARSE_DATE_INVALID_TIME_STYLE"), loc);
        }
        else {
            if (!"both".equalsIgnoreCase(this.type)) {
                throw new JspException(Resources.getMessage("PARSE_DATE_INVALID_TYPE", this.type));
            }
            parser = DateFormat.getDateTimeInstance(Util.getStyle(this.dateStyle, "PARSE_DATE_INVALID_DATE_STYLE"), Util.getStyle(this.timeStyle, "PARSE_DATE_INVALID_TIME_STYLE"), loc);
        }
        parser.setLenient(false);
        return parser;
    }
}
