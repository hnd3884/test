package com.me.devicemanagement.framework.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class DMSASTag extends ConditionalTagSupport
{
    private Boolean isSAS;
    
    public DMSASTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() throws JspTagException {
        try {
            CustomerInfoUtil.getInstance();
            final boolean isSas = CustomerInfoUtil.isSAS();
            return Boolean.valueOf(isSas).equals(this.isSAS);
        }
        catch (final Exception ex) {
            throw new JspTagException("Exception occured while checking whether is MSP or not.");
        }
    }
    
    public void setisSAS(final Boolean isSAS) {
        this.isSAS = isSAS;
    }
    
    private void init() {
        this.isSAS = null;
    }
}
