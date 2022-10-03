package com.me.ems.onpremise.summaryserver.summary.proberegistration;

import javax.servlet.ServletException;
import java.util.Map;
import java.io.IOException;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import com.me.ems.onpremise.uac.summaryserver.revamp.summary.SummaryDefaultUsersDataUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import com.me.devicemanagement.framework.server.authentication.summaryserver.probe.DMUserHandlerForPS;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class ProbeRegistration extends HttpServlet
{
    public static Logger logger;
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final Map errorMap = new HashMap();
            final String probeProtocol = request.getParameter("probeProtocol");
            final String probeHost = request.getParameter("probeHost");
            final String probePort = request.getParameter("probePort");
            final String ipAddress = request.getParameter("ipAddress");
            final String timeZone = request.getParameter("timeZone");
            final String version = request.getParameter("version");
            final String probeApiKey = request.getParameter("probeAuthKey");
            final Float totalSpace = Float.parseFloat(request.getParameter("totalSpace"));
            final Float freeSpace = Float.parseFloat(request.getParameter("freeSpace"));
            final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getSystemUserName();
            final Long keyGeneratedUserId = DMUserHandlerForPS.getUserID(userName);
            final Long keyGeneratedTime = Long.parseLong(request.getParameter("apiKeyGeneratedOn"));
            final String installationKey = request.getParameter("installationKey");
            final Map probeDetails = new HashMap();
            probeDetails.put("protocol", probeProtocol);
            probeDetails.put("host", probeHost);
            probeDetails.put("port", probePort);
            probeDetails.put("probeAuthKey", probeApiKey);
            probeDetails.put("ipAddress", ipAddress);
            probeDetails.put("totalSpace", totalSpace);
            probeDetails.put("freeSpace", freeSpace);
            probeDetails.put("timeZone", timeZone);
            probeDetails.put("version", version);
            probeDetails.put("apiKeyGeneratedBy", keyGeneratedUserId);
            probeDetails.put("apiKeyGeneratedOn", keyGeneratedTime);
            final Long probeId = ProbeAuthUtil.getInstance().validateInstallationKey(installationKey);
            if (probeId == null) {
                ProbeRegistration.logger.log(Level.SEVERE, "Installation Key is not Valid");
                errorMap.put("errorMessage", "Installation Key is not Valid");
                response.setHeader("content-type", "application/json");
                response.getWriter().println(new JSONObject(errorMap));
                return;
            }
            if (probeId == -1L) {
                ProbeRegistration.logger.log(Level.SEVERE, "Installation Key is already used/expired");
                errorMap.put("errorMessage", "Key already used.Please add new Probe");
                response.setHeader("content-type", "application/json");
                response.getWriter().println(new JSONObject(errorMap));
                return;
            }
            final Boolean installed = ProbeUtil.getInstance().setProbeInstalled(probeId, probeDetails);
            if (!installed) {
                ProbeRegistration.logger.log(Level.SEVERE, "Unable to Update Installation Details");
                errorMap.put("errorMessage", "Unable to Update Installation Details");
                response.setHeader("content-type", "application/json");
                response.getWriter().println(new JSONObject(errorMap));
                return;
            }
            final JSONObject probeAuthDetails = ProbeAuthUtil.getInstance().storeAuthKeys(probeId, probeDetails);
            final Long seqGenValue = ProbeUtil.getInstance().generateAutoGenValueForProbe(probeId);
            probeAuthDetails.put("uvhRange", (Object)seqGenValue);
            final JSONObject defaultUserAndRoleIds = SummaryDefaultUsersDataUtil.getInstance().getSSDefaultUsersAndRoleUVHValues();
            probeAuthDetails.put("defaultUserAndRoleIds", (Object)defaultUserAndRoleIds);
            final String trackID = METrackerUtil.getMEDCTrackId();
            probeAuthDetails.put("Associated_SummaryServer_ID", (Object)trackID);
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(probeAuthDetails.toString());
        }
        catch (final IOException ioEx) {
            ProbeRegistration.logger.log(Level.WARNING, "Caught exception in writing JSON file", ioEx);
        }
        catch (final Exception ex) {
            ProbeRegistration.logger.log(Level.SEVERE, "Exception occurred while registering probe ", ex);
        }
    }
    
    static {
        ProbeRegistration.logger = Logger.getLogger("probeActionsLogger");
    }
}
