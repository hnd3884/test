package com.me.devicemanagement.framework.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class DCIframeFeaturesTag extends ConditionalTagSupport
{
    private String productCode;
    private Boolean show;
    
    public DCIframeFeaturesTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
            final String appname = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "appname");
            final String pattern = ",";
            final String[] productList = this.productCode.split(pattern);
            for (int i = 0; i < productList.length; ++i) {
                if (productList[i].equalsIgnoreCase(appname)) {
                    return this.show;
                }
            }
        }
        catch (final Exception ex) {
            throw new JspTagException("Exception occured while checking condition");
        }
        return !this.show;
    }
    
    public void setAppName(final String productCode) {
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
