package com.me.mdm.api.core.misc;

import java.util.logging.Level;
import com.dd.plist.Base64;
import com.me.mdm.server.location.LocationDataHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.device.DeviceFacade;
import org.json.JSONObject;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class LocationCSVExportAPIHandler extends ApiRequestHandler
{
    public static final int ALL_DEVICE_RECENT_LOCATION = 1;
    public static final int DEVICE_LOC_HISTORY = 2;
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            final int type = APIUtil.getIntegerFilter(request, "export_type");
            if (type == -1) {
                throw new APIHTTPException("COM0024", new Object[] { "export_type" });
            }
            String fileName = null;
            String contentType = null;
            String data = null;
            final JSONObject responseJSON = new JSONObject();
            switch (type) {
                case 2: {
                    final Long deviceID = APIUtil.getLongFilter(request, "device_id");
                    final Long fromDate = APIUtil.getLongFilter(request, "from_date");
                    final Long toDate = APIUtil.getLongFilter(request, "to_date");
                    if (deviceID == -1L) {
                        throw new APIHTTPException("COM0024", new Object[] { "device_id" });
                    }
                    new DeviceFacade().validateIfDeviceExists(deviceID, APIUtil.getCustomerID(request));
                    final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(deviceID);
                    final JSONObject reqData = new JSONObject();
                    if (fromDate == -1L) {
                        reqData.put("EXPORT_HISTORY_TYPE", (Object)"4");
                        reqData.put("ManagedDeviceExtn.NAME", (Object)deviceName);
                        final StringBuilder lhData = new LocationDataHandler().exportLocationHistoryData(deviceID, reqData);
                        fileName = deviceName + "_LocationHistory.csv";
                        contentType = "text/csv";
                        data = Base64.encodeBytes(lhData.toString().getBytes());
                    }
                    else if (toDate == -1L) {
                        reqData.put("EXPORT_HISTORY_TYPE", (Object)"2");
                        reqData.put("SELECTED_FROM", (Object)fromDate);
                        reqData.put("SELECTED_TO", (Object)toDate);
                        reqData.put("ManagedDeviceExtn.NAME", (Object)deviceName);
                        final StringBuilder lhData = new LocationDataHandler().exportLocationHistoryData(deviceID, reqData);
                        fileName = deviceName + "_LocationHistory.csv";
                        contentType = "text/csv";
                        data = Base64.encodeBytes(lhData.toString().getBytes());
                    }
                    else {
                        reqData.put("EXPORT_HISTORY_TYPE", (Object)"3");
                        reqData.put("SELECTED_FROM", (Object)fromDate);
                        reqData.put("SELECTED_TO", (Object)toDate);
                        reqData.put("ManagedDeviceExtn.NAME", (Object)deviceName);
                        final StringBuilder lhData = new LocationDataHandler().exportLocationHistoryData(deviceID, reqData);
                        fileName = deviceName + "_LocationHistory.csv";
                        contentType = "text/csv";
                        data = Base64.encodeBytes(lhData.toString().getBytes());
                    }
                    responseJSON.put("file_name", (Object)fileName);
                    responseJSON.put("data", (Object)data);
                    responseJSON.put("content_type", (Object)contentType);
                    break;
                }
                case 1: {
                    final StringBuilder lhData = new LocationDataHandler().exportAllDeviceLocationData(APIUtil.getCustomerID(request));
                    fileName = "AllDevices_LocationHistory.csv";
                    contentType = "text/csv";
                    data = Base64.encodeBytes(lhData.toString().getBytes());
                    responseJSON.put("file_name", (Object)fileName);
                    responseJSON.put("data", (Object)data);
                    responseJSON.put("content_type", (Object)contentType);
                    break;
                }
            }
            final JSONObject returnJSON = new JSONObject();
            returnJSON.put("status", 200);
            returnJSON.put("RESPONSE", (Object)responseJSON);
            return returnJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception in LocationCSVExportAPIHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
