package com.me.mdm.api.message;

import javax.servlet.http.HttpServletRequest;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MessageAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    
    public MessageAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    private Object getMsg(final APIRequest apiRequest, final boolean closeMsg) {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            final Long userId = APIUtil.getUserID(request);
            final Long loginId = APIUtil.getLoginID(request);
            Long customerId = APIUtil.optCustomerID(request);
            if (closeMsg) {
                customerId = APIUtil.getCustomerID(request);
            }
            final String pageName = String.valueOf(request.getJSONObject("msg_header").getJSONObject("filters").get("operation"));
            if (closeMsg) {
                final JSONObject bodyJSON = request.getJSONObject("msg_body");
                final long msgId = bodyJSON.optLong("msg_id");
                final long pageId = bodyJSON.optLong("page_id");
                MessageProvider.getInstance().closeMsgForUser(userId, Long.valueOf(msgId), Long.valueOf(pageId));
            }
            final HttpServletRequest httpServletRequest = apiRequest.httpServletRequest;
            final JSONObject apiRequestJSON = new JSONObject();
            apiRequestJSON.put("remote_address", (Object)httpServletRequest.getRemoteAddr());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)MDMMessageProvider.getInstance().getMessageJson(pageName, customerId, loginId, userId, apiRequestJSON));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in MessageRequestHandler ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        return this.getMsg(apiRequest, true);
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        return this.getMsg(apiRequest, false);
    }
}
