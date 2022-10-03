package com.adventnet.authentication;

import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.authentication.util.AuthDBUtil;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class RoleCache
{
    private static Logger logger;
    private static HashMap roleCache;
    private static HashMap cachedTables;
    
    public static void clearAllCachedData() {
        RoleCache.logger.log(Level.FINER, " clearing all cachedData ");
        RoleCache.cachedTables = new HashMap();
        RoleCache.roleCache = new HashMap();
    }
    
    public static Object getRolesFromCache(final Object refId) {
        final Object roles = RoleCache.roleCache.get(refId);
        if (roles == null) {
            RoleCache.logger.log(Level.FINER, " No roles Cached for {0} ", refId);
            return null;
        }
        return roles;
    }
    
    public static void populateRoleCache() {
        clearAllCachedData();
        try {
            final List accList = AuthDBUtil.getAccountList();
            for (int i = 0; i < accList.size(); ++i) {
                final Long accountId = accList.get(i);
                final List roleList = AuthDBUtil.getAuthorizedRolesList(accountId);
                RoleCache.roleCache.put(accountId, roleList);
            }
        }
        catch (final DataAccessException dae) {
            RoleCache.logger.log(Level.FINER, " Exception while populating role cache} ", (Throwable)dae);
        }
        RoleCache.logger.log(Level.FINER, " Roles in cahce is {0} ", RoleCache.roleCache);
    }
    
    static {
        RoleCache.logger = Logger.getLogger(RoleCache.class.getName());
        RoleCache.roleCache = new HashMap();
        RoleCache.cachedTables = new HashMap();
    }
}
