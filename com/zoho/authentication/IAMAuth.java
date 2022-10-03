package com.zoho.authentication;

import java.util.Locale;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import com.adventnet.iam.Role;
import com.adventnet.iam.IAMProxy;
import com.adventnet.iam.User;
import com.adventnet.iam.IAMUtil;

public class IAMAuth implements AuthInterface
{
    @Override
    public Long getAccountID() {
        final User user = IAMUtil.getCurrentUser();
        if (user != null) {
            return user.getZUID();
        }
        return -1L;
    }
    
    @Override
    public String getLoginName() {
        final User user = IAMUtil.getCurrentUser();
        if (user != null) {
            user.getLoginName();
        }
        return null;
    }
    
    @Override
    public Long getUserID() {
        final User user = IAMUtil.getCurrentUser();
        if (user != null) {
            user.getZUID();
        }
        return -1L;
    }
    
    @Override
    public boolean isUserExists(final String roleName) {
        final Collection<Role> roles = IAMProxy.SERVICEAPI.getAllRoles();
        for (final Role role : roles) {
            if (role.getRoleName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Long> getAccountIDs(final List<String> roles) throws Exception {
        return null;
    }
    
    @Override
    public Locale getLocale() {
        final User user = IAMUtil.getCurrentUser();
        if (user != null) {
            return new Locale(user.getLanguage(), user.getCountry());
        }
        return null;
    }
    
    @Override
    public boolean isUserAuthenticated() {
        final User user = IAMUtil.getCurrentUser();
        return user != null;
    }
}
