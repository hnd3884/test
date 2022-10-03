package com.adventnet.client;

import javax.servlet.UnavailableException;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Collection;
import com.adventnet.iam.Role;
import com.adventnet.iam.IAMProxy;
import com.adventnet.iam.IAMUtil;

public class IAMAuth implements AuthInterface
{
    @Override
    public Long getAccountID() {
        return IAMUtil.getCurrentUser().getZUID();
    }
    
    @Override
    public String getLoginName() {
        return IAMUtil.getCurrentUser().getLoginName();
    }
    
    @Override
    public Long getUserID() {
        return IAMUtil.getCurrentUser().getZUID();
    }
    
    @Override
    public boolean userExists(final String roleName) {
        final Collection<Role> roles = IAMProxy.SERVICEAPI.getAllRoles();
        for (final Role role : roles) {
            if (role.getRoleName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object encrypt(final Object paramName, final Object paramValue, final HttpServletRequest request) {
        return paramValue;
    }
    
    @Override
    public Object encrypt(final Object value) {
        return null;
    }
    
    @Override
    public String getListViewTotalHtmlString(final ViewContext vc) {
        return "";
    }
    
    @Override
    public List<Long> getAccountIDs(final List<String> roles) throws Exception {
        throw new UnavailableException("implementation is not done yet for IAM");
    }
}
