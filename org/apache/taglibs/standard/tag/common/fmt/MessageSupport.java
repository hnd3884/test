package org.apache.taglibs.standard.tag.common.fmt;

import java.util.Locale;
import java.util.ResourceBundle;
import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.Util;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class MessageSupport extends BodyTagSupport
{
    public static final String UNDEFINED_KEY = "???";
    protected String keyAttrValue;
    protected boolean keySpecified;
    protected LocalizationContext bundleAttrValue;
    protected boolean bundleSpecified;
    private String var;
    private int scope;
    private List params;
    
    public MessageSupport() {
        this.params = new ArrayList();
        this.init();
    }
    
    private void init() {
        this.var = null;
        this.scope = 1;
        this.keyAttrValue = null;
        this.keySpecified = false;
        this.bundleAttrValue = null;
        this.bundleSpecified = false;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public void addParam(final Object arg) {
        this.params.add(arg);
    }
    
    public int doStartTag() throws JspException {
        this.params.clear();
        return 2;
    }
    
    public int doEndTag() throws JspException {
        String key = null;
        LocalizationContext locCtxt = null;
        if (this.keySpecified) {
            key = this.keyAttrValue;
        }
        else if (this.bodyContent != null && this.bodyContent.getString() != null) {
            key = this.bodyContent.getString().trim();
        }
        if (key != null) {
            if (!key.equals("")) {
                String prefix = null;
                if (!this.bundleSpecified) {
                    final Tag t = findAncestorWithClass((Tag)this, (Class)BundleSupport.class);
                    if (t != null) {
                        final BundleSupport parent = (BundleSupport)t;
                        locCtxt = parent.getLocalizationContext();
                        prefix = parent.getPrefix();
                    }
                    else {
                        locCtxt = BundleSupport.getLocalizationContext(this.pageContext);
                    }
                }
                else {
                    locCtxt = this.bundleAttrValue;
                    if (locCtxt.getLocale() != null) {
                        SetLocaleSupport.setResponseLocale(this.pageContext, locCtxt.getLocale());
                    }
                }
                String message = "???" + key + "???";
                if (locCtxt != null) {
                    final ResourceBundle bundle = locCtxt.getResourceBundle();
                    if (bundle != null) {
                        try {
                            if (prefix != null) {
                                key = prefix + key;
                            }
                            message = bundle.getString(key);
                            if (!this.params.isEmpty()) {
                                final Object[] messageArgs = this.params.toArray();
                                final MessageFormat formatter = new MessageFormat("");
                                if (locCtxt.getLocale() != null) {
                                    formatter.setLocale(locCtxt.getLocale());
                                }
                                else {
                                    final Locale locale = SetLocaleSupport.getFormattingLocale(this.pageContext);
                                    if (locale != null) {
                                        formatter.setLocale(locale);
                                    }
                                }
                                formatter.applyPattern(message);
                                message = formatter.format(messageArgs);
                            }
                        }
                        catch (final MissingResourceException mre) {
                            message = "???" + key + "???";
                        }
                    }
                }
                if (this.var != null) {
                    this.pageContext.setAttribute(this.var, (Object)message, this.scope);
                }
                else {
                    try {
                        this.pageContext.getOut().print(message);
                    }
                    catch (final IOException ioe) {
                        throw new JspTagException(ioe.toString(), (Throwable)ioe);
                    }
                }
                return 6;
            }
        }
        try {
            this.pageContext.getOut().print("??????");
        }
        catch (final IOException ioe2) {
            throw new JspTagException(ioe2.toString(), (Throwable)ioe2);
        }
        return 6;
    }
    
    public void release() {
        this.init();
    }
}
