package com.me.ems.framework.common.api.v1.service;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import com.me.ems.framework.common.api.utils.DashboardUtil;
import com.me.ems.framework.common.api.v1.model.helpermodel.DashboardPreferenceBean;
import java.util.logging.Logger;

public class DashboardPreferenceService
{
    private static Logger logger;
    
    public static DashboardPreferenceService getInstance() {
        return new DashboardPreferenceService();
    }
    
    public DashboardPreferenceBean formCustomerPreferenceBean(final Long custId, final Long loginID) throws Exception {
        final DashboardPreferenceBean resultBean = new DashboardPreferenceBean();
        final String dashboardName = DashboardUtil.getInstance().getFavouriteDashboard(loginID, custId);
        final JSONArray defaultRouteArr = new JSONArray();
        final JSONObject homeDashObj = new JSONObject();
        homeDashObj.put((Object)"mainTab", (Object)"home");
        homeDashObj.put((Object)"type", (Object)"dashboard");
        homeDashObj.put((Object)"default", (Object)dashboardName);
        defaultRouteArr.add((Object)homeDashObj);
        resultBean.setDefaultRoute(defaultRouteArr);
        return resultBean;
    }
    
    static {
        DashboardPreferenceService.logger = Logger.getLogger(DashboardPreferenceService.class.getName());
    }
}
