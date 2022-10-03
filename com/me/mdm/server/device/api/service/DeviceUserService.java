package com.me.mdm.server.device.api.service;

import com.me.mdm.api.paging.SearchUtil;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.mdm.server.apple.useraccount.AppleMultiUserUtils;
import com.me.mdm.server.device.api.model.DeviceUserModel;
import java.util.ArrayList;
import com.me.mdm.server.device.api.model.DeviceUserListModel;
import com.me.mdm.server.device.api.model.SearchDeviceUser;
import java.util.logging.Logger;

public class DeviceUserService
{
    protected static Logger logger;
    
    public DeviceUserListModel getDeviceUser(final SearchDeviceUser deviceUser) {
        final DeviceUserListModel userListModel = new DeviceUserListModel();
        try {
            final List<DeviceUserModel> modelList = new ArrayList<DeviceUserModel>();
            final Criteria criteria = this.getCriteria(deviceUser);
            final DataObject dataObject = AppleMultiUserUtils.getDeviceUserAccountDO(criteria);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MdDeviceRecentUsersInfo");
                while (iterator.hasNext()) {
                    final DeviceUserModel userModel = new DeviceUserModel();
                    final Row recentUserRow = iterator.next();
                    userModel.setDeviceUserId((Long)recentUserRow.get("DEVICE_RECENT_USER_ID"));
                    userModel.setOrder((int)recentUserRow.get("ORDER"));
                    final Row userAccountRow = dataObject.getRow("MDDeviceUserAccounts", recentUserRow);
                    userModel.setDataUsed((String)userAccountRow.get("DATA_USED"));
                    userModel.setDataQuota((String)userAccountRow.get("DATA_QUOTA"));
                    userModel.setHasDataToSync((boolean)userAccountRow.get("DATA_SYNCED"));
                    userModel.setLoggedIn((boolean)userAccountRow.get("IS_LOGGED_IN"));
                    userModel.setMobileAccount((boolean)userAccountRow.get("IS_MOBILE_ACCOUNT"));
                    userModel.setUserGUID((String)userAccountRow.get("USER_GUID"));
                    userModel.setSecureToken((boolean)userAccountRow.get("HAS_SECURE_TOKEN"));
                    final Row userInfoRow = dataObject.getRow("MdDeviceRecentUsersInfoExtn", recentUserRow);
                    userModel.setUsername((String)userInfoRow.get("LOGON_USER_NAME"));
                    userModel.setLoginTime((Long)userInfoRow.get("LOGIN_TIME"));
                    modelList.add(userModel);
                }
            }
            userListModel.setDevices(modelList);
        }
        catch (final Exception e) {
            DeviceUserService.logger.log(Level.SEVERE, "Exception in getDevice user", e);
        }
        return userListModel;
    }
    
    private Criteria getCriteria(final SearchDeviceUser deviceUser) {
        Criteria criteria = new Criteria(new Column("MdDeviceRecentUsersInfo", "RESOURCE_ID"), (Object)deviceUser.getDeviceId(), 0);
        try {
            criteria = criteria.and(SearchUtil.setSearchCriteria(deviceUser));
        }
        catch (final Exception e) {
            DeviceUserService.logger.log(Level.SEVERE, "Exception in get criteria. only resource criteria added", e);
        }
        return criteria;
    }
    
    static {
        DeviceUserService.logger = Logger.getLogger("MDMApiLogger");
    }
}
