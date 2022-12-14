package org.apache.taglibs.standard.tag.common.fmt;

import java.lang.reflect.Method;
import java.util.Locale;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.servlet.jsp.tagext.Tag;
import java.text.NumberFormat;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.Util;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class FormatNumberSupport extends BodyTagSupport
{
    private static final Class[] GET_INSTANCE_PARAM_TYPES;
    private static final String NUMBER = "number";
    private static final String CURRENCY = "currency";
    private static final String PERCENT = "percent";
    protected Object value;
    protected boolean valueSpecified;
    protected String type;
    protected String pattern;
    protected String currencyCode;
    protected String currencySymbol;
    protected boolean isGroupingUsed;
    protected boolean groupingUsedSpecified;
    protected int maxIntegerDigits;
    protected boolean maxIntegerDigitsSpecified;
    protected int minIntegerDigits;
    protected boolean minIntegerDigitsSpecified;
    protected int maxFractionDigits;
    protected boolean maxFractionDigitsSpecified;
    protected int minFractionDigits;
    protected boolean minFractionDigitsSpecified;
    private String var;
    private int scope;
    private static Class currencyClass;
    
    public FormatNumberSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.type = s;
        this.value = s;
        this.valueSpecified = false;
        final String s2 = null;
        this.currencySymbol = s2;
        this.currencyCode = s2;
        this.var = s2;
        this.pattern = s2;
        this.groupingUsedSpecified = false;
        final boolean b = false;
        this.minIntegerDigitsSpecified = b;
        this.maxIntegerDigitsSpecified = b;
        final boolean b2 = false;
        this.minFractionDigitsSpecified = b2;
        this.maxFractionDigitsSpecified = b2;
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
        Object input = null;
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
        if (input instanceof String) {
            try {
                if (((String)input).indexOf(46) != -1) {
                    input = Double.valueOf((String)input);
                }
                else {
                    input = Long.valueOf((String)input);
                }
            }
            catch (final NumberFormatException nfe) {
                throw new JspException(Resources.getMessage("FORMAT_NUMBER_PARSE_ERROR", input), (Throwable)nfe);
            }
        }
        final Locale loc = SetLocaleSupport.getFormattingLocale(this.pageContext, (Tag)this, true, NumberFormat.getAvailableLocales());
        if (loc != null) {
            NumberFormat formatter = null;
            if (this.pattern != null && !this.pattern.equals("")) {
                final DecimalFormatSymbols symbols = new DecimalFormatSymbols(loc);
                formatter = new DecimalFormat(this.pattern, symbols);
            }
            else {
                formatter = this.createFormatter(loc);
            }
            Label_0281: {
                if (this.pattern == null || this.pattern.equals("")) {
                    if (!"currency".equalsIgnoreCase(this.type)) {
                        break Label_0281;
                    }
                }
                try {
                    this.setCurrency(formatter);
                }
                catch (final Exception e) {
                    throw new JspException(Resources.getMessage("FORMAT_NUMBER_CURRENCY_ERROR"), (Throwable)e);
                }
            }
            this.configureFormatter(formatter);
            formatted = formatter.format(input);
        }
        else {
            formatted = input.toString();
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
    
    private NumberFormat createFormatter(final Locale loc) throws JspException {
        NumberFormat formatter = null;
        if (this.type == null || "number".equalsIgnoreCase(this.type)) {
            formatter = NumberFormat.getNumberInstance(loc);
        }
        else if ("currency".equalsIgnoreCase(this.type)) {
            formatter = NumberFormat.getCurrencyInstance(loc);
        }
        else {
            if (!"percent".equalsIgnoreCase(this.type)) {
                throw new JspException(Resources.getMessage("FORMAT_NUMBER_INVALID_TYPE", this.type));
            }
            formatter = NumberFormat.getPercentInstance(loc);
        }
        return formatter;
    }
    
    private void configureFormatter(final NumberFormat formatter) {
        if (this.groupingUsedSpecified) {
            formatter.setGroupingUsed(this.isGroupingUsed);
        }
        if (this.maxIntegerDigitsSpecified) {
            formatter.setMaximumIntegerDigits(this.maxIntegerDigits);
        }
        if (this.minIntegerDigitsSpecified) {
            formatter.setMinimumIntegerDigits(this.minIntegerDigits);
        }
        if (this.maxFractionDigitsSpecified) {
            formatter.setMaximumFractionDigits(this.maxFractionDigits);
        }
        if (this.minFractionDigitsSpecified) {
            formatter.setMinimumFractionDigits(this.minFractionDigits);
        }
    }
    
    private void setCurrency(final NumberFormat formatter) throws Exception {
        String code = null;
        String symbol = null;
        if (this.currencyCode == null && this.currencySymbol == null) {
            return;
        }
        if (this.currencyCode != null && this.currencySymbol != null) {
            if (FormatNumberSupport.currencyClass != null) {
                code = this.currencyCode;
            }
            else {
                symbol = this.currencySymbol;
            }
        }
        else if (this.currencyCode == null) {
            symbol = this.currencySymbol;
        }
        else if (FormatNumberSupport.currencyClass != null) {
            code = this.currencyCode;
        }
        else {
            symbol = this.currencyCode;
        }
        if (code != null) {
            final Object[] methodArgs = { null };
            Method m = FormatNumberSupport.currencyClass.getMethod("getInstance", (Class[])FormatNumberSupport.GET_INSTANCE_PARAM_TYPES);
            methodArgs[0] = code;
            final Object currency = m.invoke(null, methodArgs);
            final Class[] paramTypes = { FormatNumberSupport.currencyClass };
            final Class numberFormatClass = Class.forName("java.text.NumberFormat");
            m = numberFormatClass.getMethod("setCurrency", (Class[])paramTypes);
            methodArgs[0] = currency;
            m.invoke(formatter, methodArgs);
        }
        else {
            final DecimalFormat df = (DecimalFormat)formatter;
            final DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            dfs.setCurrencySymbol(symbol);
            df.setDecimalFormatSymbols(dfs);
        }
    }
    
    static {
        GET_INSTANCE_PARAM_TYPES = new Class[] { String.class };
        try {
            FormatNumberSupport.currencyClass = Class.forName("java.util.Currency");
        }
        catch (final Exception ex) {}
    }
}
