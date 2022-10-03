package com.me.devicemanagement.framework.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class DCIFrameLoginTag extends ConditionalTagSupport
{
    private Boolean isIframeLogin;
    
    public DCIFrameLoginTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
            String isPuginLogin = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "isPuginLogin");
            final String isJiraLogin = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "isJiraLogin");
            final String isJiraLoginParameter = request.getParameter("isJiraLogin");
            if (isPuginLogin == null && isJiraLogin == null && isJiraLoginParameter == null) {
                isPuginLogin = "false";
            }
            else if ((isPuginLogin != null && isPuginLogin.equalsIgnoreCase("PLUGIN_LOGIN")) || (isJiraLogin != null && isJiraLogin.equalsIgnoreCase("PLUGIN_LOGIN")) || (isJiraLoginParameter != null && Boolean.valueOf(isJiraLoginParameter))) {
                isPuginLogin = "true";
                WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "isPuginLogin", "PLUGIN_LOGIN");
            }
            return isPuginLogin != null && Boolean.valueOf(isPuginLogin).equals(this.isIframeLogin);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new JspTagException("Exception occured while checking whether is SDP Integration Mode is enabled or not.");
        }
    }
    
    public void setisIframeLogin(final Boolean isIframeLogin) {
        this.isIframeLogin = isIframeLogin;
    }
    
    private void init() {
        this.isIframeLogin = null;
    }
}
