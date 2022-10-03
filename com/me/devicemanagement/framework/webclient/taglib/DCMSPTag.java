package com.me.devicemanagement.framework.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class DCMSPTag extends ConditionalTagSupport
{
    private Boolean isMSP;
    
    public DCMSPTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            return Boolean.valueOf(isMsp).equals(this.isMSP);
        }
        catch (final Exception ex) {
            throw new JspTagException("Exception occured while checking whether is MSP or not.");
        }
    }
    
    public void setIsMSP(final Boolean isMSP) {
        this.isMSP = isMSP;
    }
    
    private void init() {
        this.isMSP = null;
    }
}
