package com.me.devicemanagement.framework.webclient.taglib;

import java.util.List;
import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class DCProductTag extends ConditionalTagSupport
{
    private String productCode;
    private Boolean show;
    
    public DCProductTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final String pattern = ",";
            final String[] productList = this.productCode.split(pattern);
            final List tagList = Arrays.asList(productList);
            final ArrayList<String> tags = new ArrayList<String>();
            tags.addAll(tagList);
            final ArrayList<String> productCodes = EMSProductUtil.getEMSProductCode();
            tags.retainAll(productCodes);
            if (tags.size() > 0) {
                return this.show;
            }
        }
        catch (final Exception ex) {
            throw new JspTagException("Exception occured while checking condition");
        }
        return !this.show;
    }
    
    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }
    
    public void setShow(final Boolean show) {
        this.show = show;
    }
    
    private void init() {
        this.productCode = null;
        this.show = null;
    }
}
