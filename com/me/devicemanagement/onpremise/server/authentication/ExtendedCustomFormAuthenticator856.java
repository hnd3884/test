package com.me.devicemanagement.onpremise.server.authentication;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.me.devicemanagement.framework.server.eventlog.EventLogThreadLocal;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.adventnet.authentication.util.AuthUtil;
import org.json.simple.JSONObject;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.authenticator.CustomFormAuthenticator856;

public class ExtendedCustomFormAuthenticator856 extends CustomFormAuthenticator856
{
    protected boolean restoreRequest(final Request request, final Session session) throws IOException {
        final boolean returnValue = super.restoreRequest(request, session);
        final JSONObject secLog = new JSONObject();
        secLog.put((Object)"DOMAIN_NAME", (Object)AuthUtil.getUserCredential().getDomainName());
        secLog.put((Object)"AUTH_TYPE", (Object)request.getAuthType());
        secLog.put((Object)"LOGIN_TIME", (Object)SecurityOneLineLogger.formatTime(Long.valueOf(System.currentTimeMillis())));
        SecurityOneLineLogger.log((HttpServletRequest)request, "Login_Access", "Log_in", secLog, Level.INFO);
        return returnValue;
    }
    
    protected void forwardToErrorPage(final Request request, final HttpServletResponse response, final LoginConfig config) throws IOException {
        super.forwardToErrorPage(request, response, config);
        final JSONObject secLog = new JSONObject();
        secLog.put((Object)"REMARKS", request.getAttribute("login_status"));
        final String userName = request.getParameter("j_username");
        secLog.put((Object)"LOGIN_USER_NAME", (Object)userName);
        String domainName = request.getParameter("domainName");
        if (domainName == null) {
            domainName = "-";
        }
        secLog.put((Object)"DOMAIN_NAME", (Object)domainName);
        secLog.put((Object)"LOGIN_ATTEMPT_TIME", (Object)SecurityOneLineLogger.formatTime(Long.valueOf(System.currentTimeMillis())));
        SecurityOneLineLogger.log((HttpServletRequest)request, "Login_Access", "Log_in", secLog, Level.SEVERE);
        final String domain = domainName.equals("-") ? "" : (domainName + "\\");
        final String remoteHost = request.getRemoteHost();
        final String remarksArgs = domain + userName + "@@@" + remoteHost;
        EventLogThreadLocal.setSourceIpAddress(request.getRemoteAddr());
        EventLogThreadLocal.setSourceHostName(remoteHost);
        DCEventLogUtil.getInstance().addEvent(730, (String)null, (HashMap)null, "ems.uac.login_failed", (Object)remarksArgs, false);
    }
}
