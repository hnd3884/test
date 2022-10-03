package com.me.devicemanagement.framework.webclient.taglib;

import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspTagException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class SelecttabTag extends ConditionalTagSupport
{
    private static Logger logger;
    private String tabName;
    
    public SelecttabTag() {
        this.tabName = null;
    }
    
    public void release() {
        super.release();
        this.tabName = null;
    }
    
    public void settabName(final String tabName) {
        this.tabName = tabName;
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final HttpServletRequest servletRequest = (HttpServletRequest)this.pageContext.getRequest();
            final String loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            if (this.tabName.equalsIgnoreCase("Home") || this.tabName.equalsIgnoreCase("Reports") || this.tabName.equalsIgnoreCase("Support")) {
                return true;
            }
            final List roleList = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            final Iterator roleIterator = roleList.iterator();
            while (roleIterator.hasNext()) {
                final String temp = roleIterator.next() + "";
                final String[] roleTemp = temp.split("_");
                if (this.tabName.contains(roleTemp[0])) {
                    return true;
                }
                if (!roleTemp[0].equalsIgnoreCase("MDM") && !roleTemp[0].equalsIgnoreCase("ModernMgmt")) {
                    continue;
                }
                if (this.tabName.equals("MDM")) {
                    return true;
                }
                if (this.tabName.equals("Enroll") && roleTemp[1].equalsIgnoreCase("Enrollment")) {
                    return true;
                }
                if (this.tabName.equals("Manage") && (roleTemp[1].equalsIgnoreCase("AppMgmt") || roleTemp[1].equalsIgnoreCase("Configurations") || roleTemp[1].equalsIgnoreCase("ContentMgmt") || roleTemp[1].equalsIgnoreCase("RemoteControl") || roleTemp[1].equalsIgnoreCase("OSUpdateMgmt") || roleTemp[1].equalsIgnoreCase("Compliance") || roleTemp[1].equalsIgnoreCase("Geofence") || roleTemp[1].equalsIgnoreCase("Announcement"))) {
                    return true;
                }
                if (this.tabName.equals("Asset") && roleTemp[1].equalsIgnoreCase("Inventory")) {
                    return true;
                }
            }
            if (this.tabName.equalsIgnoreCase("admin") && roleList.contains("Common_Read")) {
                return true;
            }
            if (this.tabName.equalsIgnoreCase("BrowserSecurity") && (roleList.contains("BSPSettings_Read") || roleList.contains("Common_Write"))) {
                return true;
            }
            SelecttabTag.logger.log(Level.FINER, "User" + loginUserName + "try to use blocking url  " + (Object)servletRequest.getRequestURL());
            return false;
        }
        catch (final Exception ex) {
            throw new JspTagException("Exception occured while processing SelecttabTag authorization view");
        }
    }
    
    static {
        SelecttabTag.logger = Logger.getLogger("UserManagementLogger");
    }
}
