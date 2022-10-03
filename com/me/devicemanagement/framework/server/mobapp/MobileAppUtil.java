package com.me.devicemanagement.framework.server.mobapp;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class MobileAppUtil
{
    private static Logger logger;
    private static final String MOBAPP_LOGIN_CACHE = "MOBAPP_LOGIN_CACHE";
    private static MobileAppUtil mobAppUtil;
    public static final String MOBAPP_IOS = "iOS";
    public static final String MOBAPP_ANDROID = "Android";
    
    public static MobileAppUtil getInstance() {
        if (MobileAppUtil.mobAppUtil == null) {
            MobileAppUtil.mobAppUtil = new MobileAppUtil();
        }
        return MobileAppUtil.mobAppUtil;
    }
    
    public String updateMobileAppLoginUser() {
        String isMobileAppUsedUser = "ALL_APP";
        Boolean iosApp = false;
        Boolean androidApp = false;
        try {
            final Long techLoginID = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            Object isMobileLoginUser = DBUtil.getValueFromDB("MobileAppAPILoginExtn", "LOGIN_ID", techLoginID, "IOS_APP");
            if (isMobileLoginUser != null) {
                iosApp = (Boolean)isMobileLoginUser;
            }
            isMobileLoginUser = DBUtil.getValueFromDB("MobileAppAPILoginExtn", "LOGIN_ID", techLoginID, "ANDROID_APP");
            if (isMobileLoginUser != null) {
                androidApp = (Boolean)isMobileLoginUser;
            }
            isMobileAppUsedUser = this.setMobAppLoginUser(iosApp, androidApp);
        }
        catch (final Exception ex) {
            MobileAppUtil.logger.log(Level.WARNING, "Caught exception while setting mobile app user status in session.", ex);
        }
        return isMobileAppUsedUser;
    }
    
    private String setMobAppLoginUser(final Boolean iOSApp, final Boolean androidApp) {
        String loginDetails = null;
        if (!iOSApp && !androidApp) {
            loginDetails = "None";
        }
        else if (!iOSApp) {
            loginDetails = "iOS";
        }
        else if (!androidApp) {
            loginDetails = "Android";
        }
        return loginDetails;
    }
    
    public String isAllUsersLogInMobileApp() {
        final boolean androidApp = this.isAllMobAppUserLoggedIn("Android");
        final boolean iosApp = this.isAllMobAppUserLoggedIn("iOS");
        final String moAppUserLogin = this.setMobAppLoginUser(iosApp, androidApp);
        return moAppUserLogin;
    }
    
    public boolean isAllMobAppUserLoggedIn(final String appType) {
        boolean isAllUsersLoggedIn = false;
        final HashMap mobAppLoginCache = this.getMobileAppLoginCache();
        if (appType.equals("iOS")) {
            if (mobAppLoginCache.get("IS_ALL_IOSAPP_USERS_LOGIN") == null) {
                isAllUsersLoggedIn = this.getMobileAppLoginFromDB("iOS");
                mobAppLoginCache.put("IS_ALL_IOSAPP_USERS_LOGIN", isAllUsersLoggedIn);
                this.updateMobileAppLoginCache(mobAppLoginCache);
            }
            else {
                isAllUsersLoggedIn = mobAppLoginCache.get("IS_ALL_IOSAPP_USERS_LOGIN");
            }
        }
        else if (appType.equals("Android")) {
            if (mobAppLoginCache.get("IS_ALL_ANDROIDAPP_USERS_LOGIN") == null) {
                isAllUsersLoggedIn = this.getMobileAppLoginFromDB("Android");
                mobAppLoginCache.put("IS_ALL_ANDROIDAPP_USERS_LOGIN", isAllUsersLoggedIn);
                this.updateMobileAppLoginCache(mobAppLoginCache);
            }
            else {
                isAllUsersLoggedIn = mobAppLoginCache.get("IS_ALL_ANDROIDAPP_USERS_LOGIN");
            }
        }
        return isAllUsersLoggedIn;
    }
    
    public boolean getMobileAppLoginFromDB(final String appType) {
        boolean isAllUsersLoggedIn = false;
        int totalMobAppUsersCount = 0;
        if (appType.equals("iOS")) {
            totalMobAppUsersCount = this.getiOSMobileAppLogInUsersCount();
        }
        else if (appType.equals("Android")) {
            totalMobAppUsersCount = this.getAndroidMobileAppLogInUsersCount();
        }
        final int loginUserCount = DMUserHandler.getUsersCountWithLogin();
        if (totalMobAppUsersCount == loginUserCount) {
            isAllUsersLoggedIn = true;
        }
        return isAllUsersLoggedIn;
    }
    
    public HashMap getMobileAppLoginCache() {
        HashMap mobileAppLoginHash = new HashMap();
        final Object mobAppLoginCacheObj = ApiFactoryProvider.getCacheAccessAPI().getCache("MOBAPP_LOGIN_CACHE");
        if (mobAppLoginCacheObj != null) {
            mobileAppLoginHash = (HashMap)mobAppLoginCacheObj;
        }
        MobileAppUtil.logger.log(Level.FINEST, "In getMobileAppLoginCache --> mobileAppLoginHash : " + mobileAppLoginHash);
        return mobileAppLoginHash;
    }
    
    public void updateMobileAppLoginCache(final HashMap updateLoginCacheHash) {
        ApiFactoryProvider.getCacheAccessAPI().putCache("MOBAPP_LOGIN_CACHE", updateLoginCacheHash, 2);
    }
    
    public int getAndroidMobileAppLogInUsersCount() {
        final int totalMobAppUsersCount = this.userLoginCount("ANDROID_APP");
        return totalMobAppUsersCount;
    }
    
    public int getiOSMobileAppLogInUsersCount() {
        final int totalMobAppUsersCount = this.userLoginCount("IOS_APP");
        return totalMobAppUsersCount;
    }
    
    public int userLoginCount(final String colName) {
        int totalMobAppUsersCount = 0;
        try {
            final SelectQuery selectQuery = this.getMobAppLoginSelQuery();
            final Criteria paramCriteria = new Criteria(new Column("MobileAppAPILoginExtn", colName), (Object)Boolean.TRUE, 0);
            final Criteria criteria = paramCriteria.and(new Criteria(new Column("AaaUserStatus", "STATUS"), (Object)"DISABLED", 1));
            selectQuery.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            totalMobAppUsersCount = DBUtil.getDOSize(dataObject, "MobileAppAPILoginExtn");
            MobileAppUtil.logger.log(Level.INFO, "Total iOS MobAppUsersCount - " + totalMobAppUsersCount);
        }
        catch (final Exception ex) {
            MobileAppUtil.logger.log(Level.WARNING, "Problem while fetching userLoginCount for - " + colName + " - ", ex);
        }
        return totalMobAppUsersCount;
    }
    
    private SelectQuery getMobAppLoginSelQuery() {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MobileAppAPILoginExtn"));
        selectQuery.addJoin(new Join("MobileAppAPILoginExtn", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
        selectQuery.addJoin(new Join("AaaLogin", "AaaUserStatus", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        selectQuery.addSelectColumn(new Column("MobileAppAPILoginExtn", "LOGIN_ID"));
        return selectQuery;
    }
    
    static {
        MobileAppUtil.logger = Logger.getLogger(MobileAppUtil.class.getName());
        MobileAppUtil.mobAppUtil = null;
    }
}
