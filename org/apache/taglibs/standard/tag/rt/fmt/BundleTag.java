package org.apache.taglibs.standard.tag.rt.fmt;

import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;

public class BundleTag extends BundleSupport
{
    public void setBasename(final String basename) throws JspTagException {
        this.basename = basename;
    }
    
    public void setPrefix(final String prefix) throws JspTagException {
        this.prefix = prefix;
    }
}
