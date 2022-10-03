package com.me.mdm.webclient.service;

import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.sym.server.mdm.util.ServerCertificateFetchingUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.mdm.server.agent.DiscoveryServiceHandler;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class MDMDiscoveryService extends HttpServlet
{
    Logger logger;
    final String serviceDiscovery = "ServiceDiscovery";
    final String certificateRequest = "CertificateRequest";
    
    public MDMDiscoveryService() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final JSONObject requestJSON = JSONUtil.getInstance().parseJSONFromRequest(req);
        this.logger.log(Level.INFO, requestJSON.toString());
        String messageType;
        try {
            messageType = String.valueOf(requestJSON.get("MessageType"));
        }
        catch (final JSONException exp) {
            this.logger.log(Level.SEVERE, "Cannot parse JSON request {0}", exp.toString());
            messageType = "ServiceDiscovery";
        }
        if (messageType.equalsIgnoreCase("ServiceDiscovery")) {
            final JSONObject json = DiscoveryServiceHandler.getInstance().getLatestAgentCommDetails(JSONUtil.getInstance().parseJSONFromRequest(req));
            SYMClientUtil.writeJsonFormattedResponse(resp);
            resp.getWriter().println(json.toString());
        }
        else if (messageType.equalsIgnoreCase("CertificateRequest")) {
            JSONObject certificateJSON = new JSONObject();
            certificateJSON = ServerCertificateFetchingUtil.getInstance().fetchCertificateJSON();
            final JSONObject responseJSON = new JSONObject();
            try {
                responseJSON.put("MessageType", (Object)"CertificateRequest");
                responseJSON.put("MessageResponse", (Object)certificateJSON);
                responseJSON.put("Status", (Object)"Acknowledged");
                SYMClientUtil.writeJsonFormattedResponse(resp);
                resp.getWriter().write(responseJSON.toString());
                this.logger.log(Level.INFO, "The certificate JSON is {0}", responseJSON.toString());
            }
            catch (final JSONException e) {
                this.logger.log(Level.SEVERE, "Cannot parse JSON {0}", e.toString());
            }
        }
    }
    
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final JSONObject requestJSON = JSONUtil.getInstance().parseJSONFromRequest(req);
        this.logger.log(Level.INFO, requestJSON.toString());
        String messageType;
        try {
            messageType = String.valueOf(requestJSON.get("MessageType"));
        }
        catch (final JSONException exp) {
            this.logger.log(Level.SEVERE, "Cannot parse JSON request {0}", exp.toString());
            messageType = "ServiceDiscovery";
        }
        if (messageType.equalsIgnoreCase("ServiceDiscovery")) {
            final JSONObject json = DiscoveryServiceHandler.getInstance().getLatestAgentCommDetails(requestJSON);
            SYMClientUtil.writeJsonFormattedResponse(resp);
            resp.getWriter().write(json.toString());
        }
        else if (messageType.equalsIgnoreCase("CertificateRequest")) {
            JSONObject certificateJSON = new JSONObject();
            certificateJSON = ServerCertificateFetchingUtil.getInstance().fetchCertificateJSON();
            final JSONObject responseJSON = new JSONObject();
            try {
                responseJSON.put("MessageType", (Object)"CertificateRequest");
                responseJSON.put("MessageResponse", (Object)certificateJSON);
                responseJSON.put("Status", (Object)"Acknowledged");
                SYMClientUtil.writeJsonFormattedResponse(resp);
                resp.getWriter().write(responseJSON.toString());
                this.logger.log(Level.INFO, "The certificate JSON is {0}", responseJSON.toString());
            }
            catch (final JSONException e) {
                this.logger.log(Level.SEVERE, "Cannot parse JSON {0}", e.toString());
            }
        }
    }
}
