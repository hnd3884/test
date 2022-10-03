package org.apache.taglibs.standard.tag.common.core;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.jsp.JspException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class ParamSupport extends BodyTagSupport
{
    protected String name;
    protected String value;
    protected boolean encode;
    
    public ParamSupport() {
        this.encode = true;
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.value = s;
        this.name = s;
    }
    
    public int doEndTag() throws JspException {
        final Tag t = findAncestorWithClass((Tag)this, (Class)ParamParent.class);
        if (t == null) {
            throw new JspTagException(Resources.getMessage("PARAM_OUTSIDE_PARENT"));
        }
        if (this.name == null || this.name.equals("")) {
            return 6;
        }
        final ParamParent parent = (ParamParent)t;
        String value = this.value;
        if (value == null) {
            if (this.bodyContent == null || this.bodyContent.getString() == null) {
                value = "";
            }
            else {
                value = this.bodyContent.getString().trim();
            }
        }
        if (this.encode) {
            final String enc = this.pageContext.getResponse().getCharacterEncoding();
            try {
                parent.addParameter(URLEncoder.encode(this.name, enc), URLEncoder.encode(value, enc));
            }
            catch (final UnsupportedEncodingException e) {
                throw new JspTagException((Throwable)e);
            }
        }
        else {
            parent.addParameter(this.name, value);
        }
        return 6;
    }
    
    public void release() {
        this.init();
    }
    
    public static class ParamManager
    {
        private List names;
        private List values;
        private boolean done;
        
        public ParamManager() {
            this.names = new LinkedList();
            this.values = new LinkedList();
            this.done = false;
        }
        
        public void addParameter(final String name, final String value) {
            if (this.done) {
                throw new IllegalStateException();
            }
            if (name != null) {
                this.names.add(name);
                if (value != null) {
                    this.values.add(value);
                }
                else {
                    this.values.add("");
                }
            }
        }
        
        public String aggregateParams(final String url) {
            if (this.done) {
                throw new IllegalStateException();
            }
            this.done = true;
            final StringBuffer newParams = new StringBuffer();
            for (int i = 0; i < this.names.size(); ++i) {
                newParams.append(this.names.get(i) + "=" + this.values.get(i));
                if (i < this.names.size() - 1) {
                    newParams.append("&");
                }
            }
            if (newParams.length() <= 0) {
                return url;
            }
            final int questionMark = url.indexOf(63);
            if (questionMark == -1) {
                return url + "?" + (Object)newParams;
            }
            final StringBuffer workingUrl = new StringBuffer(url);
            workingUrl.insert(questionMark + 1, (Object)newParams + "&");
            return workingUrl.toString();
        }
    }
}
