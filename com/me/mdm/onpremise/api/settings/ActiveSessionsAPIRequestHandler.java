package com.me.mdm.onpremise.api.settings;

import java.util.logging.Level;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.authentication.Credential;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.onpremise.server.user.ActiveSessionsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ActiveSessionsAPIRequestHandler extends ApiRequestHandler
{
    private ActiveSessionsFacade activeSessionsFacade;
    
    public ActiveSessionsAPIRequestHandler() {
        this.activeSessionsFacade = new ActiveSessionsFacade();
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final HttpSession session = apiRequest.httpServletRequest.getSession();
            final Credential credential = (Credential)session.getAttribute("com.adventnet.authentication.Credential");
            final Long currentSession = credential.getSessionId();
            final JSONObject requestObject = apiRequest.toJSONObject();
            requestObject.put("current_session", (Object)currentSession);
            responseJSON.put("RESPONSE", (Object)this.activeSessionsFacade.getActiveSessions(requestObject));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 202);
            this.activeSessionsFacade.deleteActiveSession(apiRequest.toJSONObject());
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Issue on deleting active sessions", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
