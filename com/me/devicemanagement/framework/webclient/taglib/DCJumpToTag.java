package com.me.devicemanagement.framework.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class DCJumpToTag extends ConditionalTagSupport
{
    private Boolean isJumpToAccess;
    
    public DCJumpToTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
            String isJumpTo = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "jumpto");
            if (isJumpTo == null) {
                isJumpTo = "false";
            }
            else if (isJumpTo.equalsIgnoreCase("true")) {
                isJumpTo = "true";
            }
            return isJumpTo != null && Boolean.valueOf(isJumpTo).equals(this.isJumpToAccess);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new JspTagException("Exception occured while checking whether is SDP Integration Mode is enabled or not.");
        }
    }
    
    public void setisJumpToAccess(final Boolean isJumpToAccess) {
        this.isJumpToAccess = isJumpToAccess;
    }
    
    private void init() {
        this.isJumpToAccess = null;
    }
}
