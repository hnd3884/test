package com.me.devicemanagement.framework.webclient.taglib;

import javax.servlet.jsp.JspTagException;
import java.util.logging.Level;
import java.util.regex.Pattern;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class RoleTag extends ConditionalTagSupport
{
    private static Logger logger;
    private String roleName;
    
    public RoleTag() {
        this.roleName = null;
    }
    
    public void release() {
        super.release();
        this.roleName = null;
    }
    
    public void setroleName(final String roleName) {
        this.roleName = roleName;
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final HttpServletRequest servletRequest = (HttpServletRequest)this.pageContext.getRequest();
            final String loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            if (this.roleName.contains("|")) {
                final Pattern p = Pattern.compile("\\|\\|");
                final String[] roleNames = p.split(this.roleName);
                for (int i = 0; i < roleNames.length; ++i) {
                    final boolean temp = servletRequest.isUserInRole(roleNames[i]);
                    if (temp) {
                        return temp;
                    }
                }
                RoleTag.logger.log(Level.FINER, "User" + loginUserName + "try to use blocking url " + (Object)servletRequest.getRequestURL());
                return false;
            }
            final boolean userRole = servletRequest.isUserInRole(this.roleName);
            if (!userRole) {
                RoleTag.logger.log(Level.FINER, "User " + loginUserName + " try to use blocking url " + (Object)servletRequest.getRequestURL());
            }
            return userRole;
        }
        catch (final Exception ex) {
            throw new JspTagException("Exception occured while processing authorization view");
        }
    }
    
    static {
        RoleTag.logger = Logger.getLogger("UserManagementLogger");
    }
}
