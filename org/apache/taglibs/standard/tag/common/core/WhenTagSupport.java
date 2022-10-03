package org.apache.taglibs.standard.tag.common.core;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public abstract class WhenTagSupport extends ConditionalTagSupport
{
    public int doStartTag() throws JspException {
        final Tag parent;
        if (!((parent = this.getParent()) instanceof ChooseTag)) {
            throw new JspTagException(Resources.getMessage("WHEN_OUTSIDE_CHOOSE"));
        }
        if (!((ChooseTag)parent).gainPermission()) {
            return 0;
        }
        if (this.condition()) {
            ((ChooseTag)parent).subtagSucceeded();
            return 1;
        }
        return 0;
    }
}
