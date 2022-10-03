package com.me.ems.onpremise.summaryserver.probe.probeadministration;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.ds.query.Criteria;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import java.util.Map;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class ProbeLiveStatusUpdateServlet extends HttpServlet
{
    public static Logger logger;
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HashMap errorMap = new HashMap();
        try {
            final String probeServerAuthKey = request.getHeader("probeAuthKey");
            final String updateRequest = request.getHeader("isUpdateApiKeyRequest");
            final ProbeDetailsAPI probeDetailsAPI = ProbeMgmtFactoryProvider.getProbeDetailsAPI();
            final String buildnumber = request.getHeader("buildnumber");
            Boolean isUpdateRequest = false;
            if (updateRequest != null) {
                isUpdateRequest = Boolean.valueOf(updateRequest);
            }
            final JSONObject jsonObject = new JSONObject();
            if (!probeDetailsAPI.isValidProbeAuthKey(probeServerAuthKey)) {
                ProbeLiveStatusUpdateServlet.logger.log(Level.SEVERE, "PROBE API Key is not Valid");
                errorMap.put("errorMsg", "API Key is not Valid");
                response.setHeader("content-type", "application/json");
                response.sendError(401, "UnAuthorized");
                response.getWriter().println(new JSONObject((Map)errorMap));
                return;
            }
            if (isUpdateRequest) {
                final HashMap apiKeyDetails = ProbeDetailsUtil.getApiKeyDetails(null);
                jsonObject.put("probeServerAuthKeyGeneratedBy", apiKeyDetails.get("probeServerAuthKeyGeneratedBy"));
                jsonObject.put("probeServerAuthKeyGeneratedOn", apiKeyDetails.get("probeServerAuthKeyGeneratedOn"));
                response.setHeader("Content-Type", "application/json");
                response.setStatus(200);
                response.getWriter().write(jsonObject.toString());
            }
            else {
                jsonObject.put("buildnumber", (Object)ProductUrlLoader.getInstance().getValue("buildnumber"));
                response.setHeader("Content-Type", "application/json");
                response.setStatus(200);
                response.getWriter().write(jsonObject.toString());
            }
        }
        catch (final Exception e) {
            ProbeLiveStatusUpdateServlet.logger.log(Level.SEVERE, "Exception occurred while validating lIVE STATUS-PROBE auth key", e);
        }
    }
    
    static {
        ProbeLiveStatusUpdateServlet.logger = Logger.getLogger("probeActionsLogger");
    }
}
