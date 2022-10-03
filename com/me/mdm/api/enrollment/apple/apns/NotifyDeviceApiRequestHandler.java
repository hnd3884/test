package com.me.mdm.api.enrollment.apple.apns;

import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.notification.PushNotificationHandler;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.notification.NotificationHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class NotifyDeviceApiRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public NotifyDeviceApiRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject message = apiRequest.toJSONObject();
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject msgBody = message.getJSONObject("msg_body");
            if (msgBody.has("device_ids")) {
                final List<Long> memberSet = JSONUtil.getInstance().convertLongJSONArrayTOList(msgBody.getJSONArray("device_ids"));
                NotificationHandler.getInstance().SendNotification(memberSet);
                responseJSON.put("status", 202);
            }
            else {
                if (!msgBody.has("udid")) {
                    throw new APIHTTPException("COM0006", new Object[0]);
                }
                final List<String> memberSet2 = JSONUtil.getInstance().convertStringJSONArrayTOList(msgBody.getJSONArray("udid"));
                final JSONObject resp = PushNotificationHandler.getInstance().notifyAppleDeviceWithUdid(memberSet2);
                responseJSON.put("status", 202);
            }
            return responseJSON;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred in NotifyDeviceApiRequestHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
