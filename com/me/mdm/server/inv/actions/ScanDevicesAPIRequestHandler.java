package com.me.mdm.server.inv.actions;

import java.util.Iterator;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.Collection;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ScanDevicesAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject message = apiRequest.toJSONObject();
        try {
            final JSONObject apiJSON = apiRequest.toJSONObject();
            final Long customerID = APIUtil.getCustomerID(apiJSON);
            final String userName = APIUtil.getUserName(apiJSON);
            final String sEventLogRemarks = "dc.mdm.actionlog.inv.scan_success";
            final List<Long> memberSet = JSONUtil.getInstance().convertLongJSONArrayTOList(message.getJSONObject("msg_body").getJSONArray("device_ids"));
            new DeviceFacade().validateIfDevicesExists(memberSet, APIUtil.getCustomerID(message));
            final HashMap deviceIdNameMap = ManagedDeviceHandler.getInstance().getDeviceNames(memberSet);
            final ArrayList<Object> remarksArgsList = new ArrayList<Object>();
            for (final Long resourceID : memberSet) {
                final String remarksArgs = deviceIdNameMap.get(resourceID);
                remarksArgsList.add(remarksArgs + "@@@" + userName);
            }
            MDMEventLogHandler.getInstance().addEvent(2041, memberSet, userName, sEventLogRemarks, remarksArgsList, customerID, MDMUtil.getCurrentTimeInMillis());
            DeviceInvCommandHandler.getInstance().scanDevice(memberSet, APIUtil.getUserID(message));
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                this.logger.log(Level.SEVERE, "exception occurred in ScanDevicesAPIRequestHandler", e);
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred in ScanDevicesAPIRequestHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
