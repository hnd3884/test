package com.me.devicemanagement.framework.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class SasOrMspTag extends ConditionalTagSupport
{
    private Boolean show;
    
    public SasOrMspTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            CustomerInfoUtil.getInstance();
            final boolean isSas = CustomerInfoUtil.isSAS();
            if (isSas || isMsp) {
                return this.show;
            }
        }
        catch (final Exception ex) {
            throw new JspTagException("Exception occured while checking condition");
        }
        return !this.show;
    }
    
    public void setShow(final Boolean show) {
        this.show = show;
    }
    
    private void init() {
        this.show = null;
    }
}
