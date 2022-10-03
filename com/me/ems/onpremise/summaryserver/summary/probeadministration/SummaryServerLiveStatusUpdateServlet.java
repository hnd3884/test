package com.me.ems.onpremise.summaryserver.summary.probeadministration;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.ems.onpremise.summaryserver.common.probeadministration.ProbeDetailsUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Map;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class SummaryServerLiveStatusUpdateServlet extends HttpServlet
{
    public static Logger logger;
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HashMap errorMap = new HashMap();
        try {
            final String summaryServerAuthKey = request.getHeader("summaryServerAuthKey");
            final String probeIdStr = request.getHeader("probeId");
            final String updateRequest = request.getHeader("isUpdateApiKeyRequest");
            final String buildnumber = request.getHeader("buildnumber");
            Boolean isUpdateRequest = false;
            if (updateRequest != null) {
                isUpdateRequest = Boolean.valueOf(updateRequest);
            }
            final Long probeId = Long.valueOf(probeIdStr);
            if (!ProbeMgmtFactoryProvider.getProbeDetailsAPI().isValidSummaryServerAuthKey(summaryServerAuthKey, Long.valueOf((long)probeId))) {
                SummaryServerLiveStatusUpdateServlet.logger.log(Level.SEVERE, "SUMMARY API Key is not Valid");
                errorMap.put("errorMsg", " SUMMARY API Key is not Valid");
                response.setHeader("content-type", "application/json");
                response.sendError(401, "UnAuthorized");
                response.getWriter().println(new JSONObject((Map)errorMap));
                return;
            }
            final JSONObject jsonObject = new JSONObject();
            if (isUpdateRequest) {
                final HashMap apiKeyDetails = ProbeDetailsUtil.getApiKeyDetails(new Criteria(new Column("ProbeDetails", "PROBE_ID"), (Object)probeId, 0));
                jsonObject.put("summaryServerAuthKeyGeneratedBy", apiKeyDetails.get("summaryServerAuthKeyGeneratedBy"));
                jsonObject.put("summaryServerAuthKeyGeneratedOn", apiKeyDetails.get("summaryServerAuthKeyGeneratedOn"));
                response.setHeader("Content-Type", "application/json");
                response.getWriter().write(jsonObject.toString());
            }
            else {
                jsonObject.put("buildnumber", (Object)ProductUrlLoader.getInstance().getValue("buildnumber"));
                response.setHeader("Content-Type", "application/json");
                response.getWriter().write(jsonObject.toString());
            }
        }
        catch (final Exception e) {
            SummaryServerLiveStatusUpdateServlet.logger.log(Level.SEVERE, "Exception occurred while validating live status-summary auth key", e);
        }
    }
    
    static {
        SummaryServerLiveStatusUpdateServlet.logger = Logger.getLogger("probeActionsLogger");
    }
}
