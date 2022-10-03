package org.apache.taglibs.standard.tag.common.fmt;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.tagext.Tag;
import java.text.NumberFormat;
import org.apache.taglibs.standard.tag.common.core.Util;
import java.util.Locale;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class ParseNumberSupport extends BodyTagSupport
{
    private static final String NUMBER = "number";
    private static final String CURRENCY = "currency";
    private static final String PERCENT = "percent";
    protected String value;
    protected boolean valueSpecified;
    protected String type;
    protected String pattern;
    protected Locale parseLocale;
    protected boolean isIntegerOnly;
    protected boolean integerOnlySpecified;
    private String var;
    private int scope;
    
    public ParseNumberSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.var = s;
        this.pattern = s;
        this.type = s;
        this.value = s;
        this.valueSpecified = false;
        this.parseLocale = null;
        this.integerOnlySpecified = false;
        this.scope = 1;
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
        Locale loc = this.parseLocale;
        if (loc == null) {
            loc = SetLocaleSupport.getFormattingLocale(this.pageContext, (Tag)this, false, NumberFormat.getAvailableLocales());
        }
        if (loc == null) {
            throw new JspException(Resources.getMessage("PARSE_NUMBER_NO_PARSE_LOCALE"));
        }
        NumberFormat parser = null;
        if (this.pattern != null && !this.pattern.equals("")) {
            final DecimalFormatSymbols symbols = new DecimalFormatSymbols(loc);
            parser = new DecimalFormat(this.pattern, symbols);
        }
        else {
            parser = this.createParser(loc);
        }
        if (this.integerOnlySpecified) {
            parser.setParseIntegerOnly(this.isIntegerOnly);
        }
        Number parsed = null;
        try {
            parsed = parser.parse(input);
        }
        catch (final ParseException pe) {
            throw new JspException(Resources.getMessage("PARSE_NUMBER_PARSE_ERROR", input), (Throwable)pe);
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
    
    private NumberFormat createParser(final Locale loc) throws JspException {
        NumberFormat parser = null;
        if (this.type == null || "number".equalsIgnoreCase(this.type)) {
            parser = NumberFormat.getNumberInstance(loc);
        }
        else if ("currency".equalsIgnoreCase(this.type)) {
            parser = NumberFormat.getCurrencyInstance(loc);
        }
        else {
            if (!"percent".equalsIgnoreCase(this.type)) {
                throw new JspException(Resources.getMessage("PARSE_NUMBER_INVALID_TYPE", this.type));
            }
            parser = NumberFormat.getPercentInstance(loc);
        }
        return parser;
    }
}
