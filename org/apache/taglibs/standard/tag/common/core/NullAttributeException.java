package org.apache.taglibs.standard.tag.common.core;

import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.JspTagException;

public class NullAttributeException extends JspTagException
{
    public NullAttributeException(final String tag, final String att) {
        super(Resources.getMessage("TAG_NULL_ATTRIBUTE", att, tag));
    }
}
