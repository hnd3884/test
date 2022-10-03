package com.me.ems.framework.uac.api.v1.service;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;

public class CoreUserService
{
    private static Logger logger;
    
    public static CoreUserService getInstance() {
        return new CoreUserService();
    }
    
    public User getLoginDataForUser(final Long loginID) {
        final Criteria criteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginID, 0);
        final DataObject loginDO = DMUserHandler.getLoginDO(criteria);
        return this.constructDCUserObject(loginDO);
    }
    
    public User getLoginDataForUser(final String userName, final String domainName) {
        final DataObject loginDO = DMUserHandler.getLoginDO(userName, domainName);
        return this.constructDCUserObject(loginDO);
    }
    
    public User constructDCUserObject(final DataObject loginDO) {
        return ApiFactoryProvider.getUtilAccessAPI().constructDCUserObject(loginDO);
    }
    
    static {
        CoreUserService.logger = Logger.getLogger(CoreUserService.class.getName());
    }
}
