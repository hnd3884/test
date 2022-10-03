package com.me.mdm.api.view;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;

public class GettingStartedFacade
{
    private static final String PAGE_KEY = "page_key";
    private static final String IS_CLOSE = "is_close";
    
    public JSONObject isShowGettingStarted(final JSONObject requestJSON) {
        try {
            final Long userId = APIUtil.getUserID(requestJSON);
            final String page = APIUtil.getStringFilter(requestJSON, "page_key");
            String isClose = MDMUtil.getUserParameter(userId, page);
            if (MDMUtil.getInstance().isEmpty(isClose)) {
                isClose = String.valueOf(false);
            }
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("page_key", (Object)page);
            responseJSON.put("is_close", (Object)isClose);
            return responseJSON;
        }
        catch (final SyMException | JSONException e) {
            Logger.getLogger(GettingStartedFacade.class.getName()).log(Level.SEVERE, " Error isShowGettingStarted() >   ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void updateGettingStarted(final JSONObject requestJSON) {
        try {
            final JSONObject bodyJSON = requestJSON.getJSONObject("msg_body");
            final String page = String.valueOf(bodyJSON.get("page_key"));
            final boolean isClose = bodyJSON.getBoolean("is_close");
            final Long userId = APIUtil.getUserID(requestJSON);
            MDMUtil.updateUserParameter(userId, page, String.valueOf(isClose));
        }
        catch (final SyMException | JSONException e) {
            Logger.getLogger(GettingStartedFacade.class.getName()).log(Level.SEVERE, " Error updateGettingStarted() >   ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
