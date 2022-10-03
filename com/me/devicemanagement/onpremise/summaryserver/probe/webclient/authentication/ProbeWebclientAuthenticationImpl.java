package com.me.devicemanagement.onpremise.summaryserver.probe.webclient.authentication;

import java.util.ArrayList;
import com.me.ems.summaryserver.common.probeadministration.ProbeDetailsAPI;
import javax.net.ssl.SSLHandshakeException;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.sdp.DCCredentialHandler;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import org.apache.commons.codec.binary.Base64;
import com.me.devicemanagement.onpremise.server.sdp.Ticket;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.webclient.authentication.WebclientAuthentication;

public class ProbeWebclientAuthenticationImpl implements WebclientAuthentication
{
    private static Logger out;
    
    @Override
    public Ticket validateTicketForSSO(final String ticket, final HttpServletRequest request, final HttpServletResponse response, final boolean isRetry) {
        Ticket t = null;
        String summaryServerUrl = "";
        try {
            String probeAuthKey = request.getParameter("probeKey");
            String responseStr = "";
            probeAuthKey = new String(Base64.decodeBase64(probeAuthKey));
            if (ProbeMgmtFactoryProvider.getProbeDetailsAPI().isValidProbeAuthKey(probeAuthKey)) {
                final ProbeDetailsAPI probeDetailsAPI = ProbeMgmtFactoryProvider.getProbeDetailsAPI();
                summaryServerUrl = probeDetailsAPI.getSummaryServerBaseURL();
                summaryServerUrl += "servlets/TicketValidator";
                final URL urlObj = new URL(summaryServerUrl);
                final HttpURLConnection conn = (HttpURLConnection)urlObj.openConnection();
                conn.setRequestProperty("ticket", ticket);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                responseStr = rd.readLine();
                rd.close();
                if (!responseStr.equalsIgnoreCase("FAILURE")) {
                    final String decryptedString = new String(Base64.decodeBase64(responseStr));
                    final String[] domainUser = decryptedString.split("::");
                    final String userName = domainUser[0];
                    final String domain = domainUser[1];
                    t = new Ticket();
                    t.ticket = ticket;
                    t.principal = userName;
                    t.domainName = domain;
                    final ArrayList roles = DCCredentialHandler.getUserRole(userName, domain);
                    if (roles == null) {
                        return null;
                    }
                    final StringBuffer tempString = new StringBuffer();
                    for (int i = 0; i < roles.size(); ++i) {
                        if (i == 0) {
                            tempString.append(roles.get(i));
                        }
                        else {
                            tempString.append(";" + roles.get(i));
                        }
                    }
                    t.roles = tempString.toString();
                    return t;
                }
                else {
                    ProbeWebclientAuthenticationImpl.out.info("Ticket Authentication failed for SSO");
                }
            }
            else {
                ProbeWebclientAuthenticationImpl.out.info("Probe Key Authentication failed for SSO");
            }
        }
        catch (final SSLHandshakeException e) {
            if (isRetry) {
                ProbeWebclientAuthenticationImpl.out.log(Level.SEVERE, "Exception while validating probe ticket due to certificate unavailability for the second time", e);
                return null;
            }
            ProbeWebclientAuthenticationImpl.out.log(Level.WARNING, "Exception while validating probe ticket due to certificate unavailability , rechecking certificate ");
            HttpsHandlerUtil.processCertificateFromServer(summaryServerUrl, null);
            this.validateTicketForSSO(ticket, request, response, true);
        }
        catch (final Exception e2) {
            ProbeWebclientAuthenticationImpl.out.log(Level.SEVERE, "Exception while validating probe ticket", e2);
            return null;
        }
        return null;
    }
    
    static {
        ProbeWebclientAuthenticationImpl.out = Logger.getLogger("ProbeWebclientAuthenticationImpl");
    }
}
