package com.me.ems.framework.common.api.v1.service;

import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.HashMap;
import com.me.ems.framework.common.core.ActionLogDAOUtil;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;
import com.me.ems.framework.common.factory.ActionLogService;

public class ActionLogServiceImpl implements ActionLogService
{
    protected Logger logger;
    
    public ActionLogServiceImpl() {
        this.logger = Logger.getLogger(ActionLogServiceImpl.class.getName());
    }
    
    @Override
    public Map<String, Object> getSettings(final User dcUser) throws APIException {
        final ActionLogDAOUtil actionLogDAOUtil = new ActionLogDAOUtil();
        final Map<String, Object> settingsMap = new HashMap<String, Object>();
        try {
            final boolean showRCView = this.isAllCustomersReasonBoxEnabled(dcUser);
            if (showRCView) {
                settingsMap.put("viewName", "RCEventView");
            }
            else {
                settingsMap.put("viewName", "AllEventView");
            }
            final List<Map<String, String>> modulesList = actionLogDAOUtil.getEventModuleList();
            settingsMap.put("modules", modulesList);
            final List<Map<String, String>> usersList = actionLogDAOUtil.getUsernamesList(dcUser);
            settingsMap.put("users", usersList);
        }
        catch (final Exception e) {
            throw new APIException("GENERIC0005");
        }
        return settingsMap;
    }
    
    @Override
    public Map<String, String> getRetentionPeriod() throws APIException {
        final Map<String, String> noOfDaysMap = new HashMap<String, String>();
        if (SyMUtil.getSyMParameter("maintain_event_log") != null) {
            noOfDaysMap.put("noOfDays", SyMUtil.getSyMParameter("maintain_event_log"));
            return noOfDaysMap;
        }
        this.logger.log(Level.SEVERE, "ActionLogViewer Exception: Exception while getting retention period");
        throw new APIException("GENERIC0005");
    }
    
    @Override
    public void updateRetentionPeriod(final Map<String, String> noOfDaysMap, final User user, final HttpServletRequest httpServletRequest) throws APIException {
        final String noOfDays = noOfDaysMap.get("noOfDays");
        try {
            final int days = Integer.parseInt(noOfDays);
            if (days < 30 || days > 750) {
                throw new Exception();
            }
        }
        catch (final Exception e) {
            throw new APIException("ACT_LOG001");
        }
        SyMUtil.updateSyMParameter("maintain_event_log", noOfDays);
        final String loginName = user.getName();
        final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
        DCEventLogUtil.getInstance().addEvent(4001, loginName, null, "dm.event.history.change", noOfDays, false, customerId);
        this.logger.log(Level.INFO, "ActionLogViewer Entry: Retention days changed by user from {0} to {1}", new Object[] { loginName, noOfDays });
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put((Object)"REMARK", (Object)"Duration for maintaining action log history has been changed to ".concat(noOfDays).concat(" by ").concat(loginName));
        SecurityOneLineLogger.log("DC_Integration", "DC_Action_Log_Viewer", jsonObject, Level.INFO);
    }
    
    protected boolean isAllCustomersReasonBoxEnabled(final User dcUser) {
        final ActionLogDAOUtil actionLogDAOUtil = new ActionLogDAOUtil();
        final List<Long> customerIdsList = CustomerInfoUtil.getInstance().getCustomerIDsForLogIn(dcUser.getLoginID());
        boolean isAllReasonBoxEnabled = true;
        try {
            final List<Integer> isReasonBoxEnabledList = actionLogDAOUtil.isReasonBoxEnabledValues(customerIdsList);
            for (final Integer isReasonBoxEnabled : isReasonBoxEnabledList) {
                if (isReasonBoxEnabled != 100) {
                    isAllReasonBoxEnabled = false;
                    break;
                }
            }
        }
        catch (final DataAccessException dae) {
            this.logger.log(Level.SEVERE, "ActionLogViewer Entry: Exception getting isReasonBoxEnabledValues", (Throwable)dae);
            isAllReasonBoxEnabled = false;
        }
        return isAllReasonBoxEnabled;
    }
}
