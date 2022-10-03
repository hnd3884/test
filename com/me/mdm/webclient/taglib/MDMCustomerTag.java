package com.me.mdm.webclient.taglib;

import java.util.List;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Logger;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class MDMCustomerTag extends ConditionalTagSupport
{
    protected static Logger out;
    private Boolean show;
    
    public void setShow(final Boolean show) {
        this.show = show;
    }
    
    private void init() {
        this.show = null;
    }
    
    public MDMCustomerTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() {
        try {
            final List customerList = CustomerInfoUtil.getInstance().getCustomerIdList();
            if (!customerList.isEmpty() && (customerList.size() != 1 || customerList.get(0) != -1L)) {
                return this.show;
            }
        }
        catch (final Exception e) {
            MDMCustomerTag.out.log(Level.SEVERE, "Exception in MDMCustomerTag", e);
        }
        return !this.show;
    }
    
    static {
        MDMCustomerTag.out = Logger.getLogger(MDMCustomerTag.class.getName());
    }
}
