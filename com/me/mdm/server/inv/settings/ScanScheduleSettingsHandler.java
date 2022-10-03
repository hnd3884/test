package com.me.mdm.server.inv.settings;

import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.devicemanagement.framework.server.scheduler.SchedulerConstants;
import java.util.HashMap;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.devicemanagement.framework.server.scheduler.SchedulerInfo;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ScanScheduleSettingsHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public ScanScheduleSettingsHandler() {
        this.logger = Logger.getLogger("InventoryLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        JSONObject schedulerObj = new JSONObject();
        String scheduleName = "DeviceScanTaskScheduler";
        Label_0066: {
            if (!CustomerInfoUtil.getInstance().isMSP()) {
                CustomerInfoUtil.getInstance();
                if (!CustomerInfoUtil.isSAS()) {
                    break Label_0066;
                }
            }
            scheduleName = scheduleName + "__" + APIUtil.getCustomerID(apiRequest.toJSONObject());
        }
        final boolean disabled = ApiFactoryProvider.getSchedulerAPI().isSchedulerDisabled(scheduleName);
        try {
            final String scheduleTypeExtraText = "";
            final String startTimeText = "";
            final String startTimeExtraText = "";
            final Long datetime = new Long(System.currentTimeMillis());
            if (disabled) {
                schedulerObj.put("schedulerDisabled", (Object)Boolean.TRUE);
            }
            if (!disabled) {
                final SchedulerInfo scheduler = new SchedulerInfo();
                final HashMap schedule = ApiFactoryProvider.getSchedulerAPI().getScheduledValues(scheduleName);
                final int schedLen = schedule.size();
                schedulerObj.put("isOnceScheduleReq", false);
                schedulerObj.put("removeScheduleReq", true);
                schedulerObj.put("schedulerDisabled", disabled);
                if (schedLen > 0) {
                    MDMRestAPIFactoryProvider.getAPIUtil().formatSchedulerDetailsToJSON(schedulerObj, schedule, scheduleName);
                }
                else {
                    schedulerObj.put("schedulerDisabled", (Object)Boolean.TRUE);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occurred while converting schedular JSON", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            schedulerObj = MDMRestAPIFactoryProvider.getAPIUtil().convertServerJSONtoAPIJSON(schedulerObj);
            if (!disabled) {
                schedulerObj.put("next_scan_time", (Object)ApiFactoryProvider.getSchedulerAPI().getNextExecutionTimeForSchedule(scheduleName));
            }
            else {
                schedulerObj.put("next_scan_time", -1L);
            }
            responseJSON.put("RESPONSE", (Object)schedulerObj);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            final Long customerID = APIUtil.getCustomerID(request);
            final Long userID = APIUtil.getUserID(request);
            JSONObject schedulerJson = request.getJSONObject("msg_body");
            schedulerJson = MDMRestAPIFactoryProvider.getAPIUtil().convertAPIJSONtoServerJSON(schedulerJson);
            final Boolean disableSched = schedulerJson.optBoolean("schedulerDisabled");
            String schedulerName = "DeviceScanTaskScheduler";
            final String workflowName = "DeviceScanTaskCallerTemplate";
            final String workEngineId = "DesktopCentral";
            Label_0112: {
                if (!CustomerInfoUtil.getInstance().isMSP()) {
                    CustomerInfoUtil.getInstance();
                    if (!CustomerInfoUtil.isSAS()) {
                        break Label_0112;
                    }
                }
                schedulerName = schedulerName + "__" + APIUtil.getCustomerID(request);
            }
            Long taskId = null;
            schedulerJson.put("taskTimeZone", (Object)ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
            final String operationType = String.valueOf(7000);
            if (!disableSched) {
                ApiFactoryProvider.getSchedulerAPI().setSchedulerState((boolean)SchedulerConstants.ENABLE, schedulerName);
                final String className = "com.adventnet.sym.server.mdm.inv.DeviceScanTaskScheduler";
                taskId = ApiFactoryProvider.getSchedulerAPI().createScheduleFromJson(schedulerJson, operationType, "DeviceScanTaskScheduler", schedulerName, workEngineId, workflowName, className, "MDM Inventory Scheduler", (String)null, APIUtil.getUserName(request), APIUtil.getCustomerID(request), (Long)null);
                CustomerParamsHandler.getInstance().addOrUpdateParameter("SCHEDULED_SCAN_USER_ID", String.valueOf(userID), (long)customerID);
                this.logger.log(Level.FINE, "Task_Id for inventory : {0} customerID: {1}", new Object[] { taskId, APIUtil.getCustomerID(request) });
                MessageProvider.getInstance().hideMessage("CONFIGURE_DEVICE_SCHEDULED_SCAN", customerID);
            }
            else {
                ApiFactoryProvider.getSchedulerAPI().setSchedulerState((boolean)SchedulerConstants.DISABLE, schedulerName);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception Occurred in ScanScheduleSettingsHandler.doPut", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
