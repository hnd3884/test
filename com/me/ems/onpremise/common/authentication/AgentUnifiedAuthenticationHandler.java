package com.me.ems.onpremise.common.authentication;

import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertificateUtil;
import com.me.devicemanagement.framework.server.cache.CacheAccessAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.apache.commons.codec.binary.Base64;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.io.File;
import java.util.Set;
import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.certificate.client.ClientCertAuthBean;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;

public class AgentUnifiedAuthenticationHandler implements UnifiedAuthenticationService
{
    private static Logger authLogger;
    private boolean isAuthEnabled;
    private static final String ENABLE_AUTH = "enable.auth.verify";
    public static Logger accessLogger;
    public static String x_SGS_AGENT_SSL_Client_Certificate_SerialNumber;
    public static String x_DS_AGENT_SSL_Client_Certificate_SerialNumber;
    public static String x_AGENT_SSL_Client_Certificate_SerialNumber;
    
    public AgentUnifiedAuthenticationHandler() {
        this.isAuthEnabled = Boolean.TRUE;
    }
    
    public void init() {
        this.initAgentAuth();
        this.initClientCertAuth();
    }
    
    public boolean authentication(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        boolean isAuthenticated = Boolean.FALSE;
        final boolean isAgentAuthValid = this.authenticationFilter(request, response);
        boolean isCertificateValid = Boolean.FALSE;
        final boolean clientCertAuthEnabled = ClientCertAuthBean.getInstance().getIsClientCertificateAuthenticationEnabled();
        if (isAgentAuthValid) {
            if (clientCertAuthEnabled) {
                isCertificateValid = this.clientCertAuthFilter(request, response);
                if (!isCertificateValid) {
                    AgentUnifiedAuthenticationHandler.authLogger.log(Level.SEVERE, "Rejecting request due to Invalid Client Cert Authorization " + request.getRequestURI());
                    throw new IAMSecurityException("AGENT_UNAUTHORIZED");
                }
                isAuthenticated = Boolean.TRUE;
            }
            else {
                isAuthenticated = isAgentAuthValid;
            }
        }
        return isAuthenticated;
    }
    
    public boolean authorization(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        return true;
    }
    
    private Set<String> readAllAuthProps() throws IOException {
        final String filePath = System.getProperty("server.home") + File.separator + "conf" + File.separator + "Tomcat" + File.separator;
        final Set<String> authTokenSet = new HashSet<String>();
        BufferedReader bufferedReader = null;
        final File oldFile = new File(filePath + ".htpasswd");
        final File newFile = new File(filePath + "Agent.key");
        try {
            if (newFile.exists()) {
                bufferedReader = new BufferedReader(new FileReader(newFile));
            }
            else {
                bufferedReader = new BufferedReader(new FileReader(oldFile));
            }
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                authTokenSet.add("Basic " + Base64.encodeBase64String(line.getBytes("UTF-8")));
            }
        }
        catch (final Exception e) {
            AgentUnifiedAuthenticationHandler.authLogger.log(Level.WARNING, "Error in getting Authprops", e);
            e.printStackTrace();
        }
        finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return authTokenSet;
    }
    
    private boolean isValidRequest(final String basicAuthHeader) throws IOException {
        Boolean isValid = false;
        final CacheAccessAPI cacheAccessAPI = ApiFactoryProvider.getCacheAccessAPI();
        Set<String> authTokenSet = (HashSet)cacheAccessAPI.getCache("http_auth_token");
        if (authTokenSet == null || authTokenSet.isEmpty()) {
            authTokenSet = this.readAllAuthProps();
            cacheAccessAPI.putCache("http_auth_token", (Object)authTokenSet);
        }
        if (authTokenSet.contains(basicAuthHeader)) {
            isValid = true;
        }
        return isValid;
    }
    
    private void initAgentAuth() {
    }
    
    private void initClientCertAuth() {
        try {
            ClientCertificateUtil.getInstance().setClientCertificateEnabledStatus();
            if (ClientCertAuthBean.getInstance().getClientCertAuthConfig() == null) {
                ClientCertificateUtil.getInstance().loadClientCertAuthConfig();
            }
            if (ClientCertAuthBean.getInstance().getIsClientCertificateAuthenticationEnabled()) {
                AgentUnifiedAuthenticationHandler.authLogger.info("Going to store all the client cert fingerprint to Reddis and hashmap.");
                ClientCertificateUtil.getInstance().setRedisEnabledStatus();
                ClientCertificateUtil.getInstance().storeAllTheClientCertFingerPrintToReddisAndMap();
                ClientCertificateUtil.getInstance().setAgentTomcatPort();
            }
            final Map httpHeadersMap = ClientCertAuthBean.getInstance().getClientCertAuthConfig().get("httpHeaders");
            AgentUnifiedAuthenticationHandler.x_SGS_AGENT_SSL_Client_Certificate_SerialNumber = httpHeadersMap.get("inSecureGatewayServer").toString();
            AgentUnifiedAuthenticationHandler.x_DS_AGENT_SSL_Client_Certificate_SerialNumber = httpHeadersMap.get("inDistributionServer").toString();
            AgentUnifiedAuthenticationHandler.x_AGENT_SSL_Client_Certificate_SerialNumber = httpHeadersMap.get("inCentralServer").toString();
            AgentUnifiedAuthenticationHandler.authLogger.log(Level.INFO, "Client Cert Auth Filter init success..");
        }
        catch (final Exception e) {
            AgentUnifiedAuthenticationHandler.authLogger.log(Level.WARNING, "Error in Certificate Authentication ", e);
        }
    }
    
    public boolean authenticationFilter(final HttpServletRequest servletRequest, final HttpServletResponse response) throws IOException {
        boolean isAuthenticated = Boolean.FALSE;
        final Enumeration<String> authHeader = servletRequest.getHeaders("authorization");
        if (!authHeader.hasMoreElements()) {
            AgentUnifiedAuthenticationHandler.authLogger.log(Level.INFO, "Rejecting the request since there is no Authorization header available URI : " + servletRequest.getRequestURI());
            return Boolean.FALSE;
        }
        while (authHeader.hasMoreElements()) {
            final String basicValue = authHeader.nextElement();
            if (this.isValidRequest(basicValue)) {
                isAuthenticated = Boolean.TRUE;
                break;
            }
        }
        if (!isAuthenticated) {
            AgentUnifiedAuthenticationHandler.authLogger.log(Level.INFO, "Rejecting request due to Invalid Authorization URI : " + servletRequest.getRequestURI());
            return Boolean.FALSE;
        }
        return isAuthenticated;
    }
    
    public boolean clientCertAuthFilter(final HttpServletRequest httpServletRequest, final HttpServletResponse response) {
        boolean isAuthenticated = Boolean.FALSE;
        final Integer requestPort = httpServletRequest.getLocalPort();
        boolean isDS = Boolean.FALSE;
        final String resourceID = httpServletRequest.getParameter("ResourceID");
        isDS = Boolean.parseBoolean(httpServletRequest.getParameter("isDS"));
        if (!ClientCertAuthBean.getInstance().isValidPort(requestPort)) {
            AgentUnifiedAuthenticationHandler.authLogger.log(Level.INFO, "Accessed using unauthorized port . Port Number " + requestPort);
            AgentUnifiedAuthenticationHandler.accessLogger.log(Level.INFO, "RESOURCE_ID " + resourceID + " RequestURI " + httpServletRequest.getRequestURI() + " QueryString " + httpServletRequest.getQueryString() + " Authentication Status FAILURE Cause Agent not accessed with agent port. Accessed port " + requestPort);
            return Boolean.FALSE;
        }
        final String agentClientCertificateSerialNumber = this.getAgentClientCertificateSerialNumber(httpServletRequest);
        String serialNumberOfResourceCertificate = null;
        if (resourceID != null && agentClientCertificateSerialNumber != null) {
            serialNumberOfResourceCertificate = ClientCertificateUtil.getInstance().getSerialNumberOfResourceCertificate(resourceID, isDS);
            if (serialNumberOfResourceCertificate == null) {
                AgentUnifiedAuthenticationHandler.accessLogger.log(Level.INFO, "RESOURCE_ID " + resourceID + " RequestURI " + httpServletRequest.getRequestURI() + " QueryString " + httpServletRequest.getQueryString() + " Serial number from certificate " + agentClientCertificateSerialNumber + " Serial number from db " + serialNumberOfResourceCertificate + " Authentication Status FAILURE Cause No serial number for resourceID in DB.");
                return Boolean.FALSE;
            }
            if (agentClientCertificateSerialNumber.toLowerCase().contains(serialNumberOfResourceCertificate)) {
                isAuthenticated = Boolean.TRUE;
                if (isDS) {
                    httpServletRequest.setAttribute("isDSRequest", (Object)Boolean.TRUE);
                }
            }
        }
        if (!isAuthenticated) {
            AgentUnifiedAuthenticationHandler.accessLogger.log(Level.INFO, "RESOURCE_ID " + resourceID + " RequestURI " + httpServletRequest.getRequestURI() + " QueryString " + httpServletRequest.getQueryString() + " Serial number from certificate " + agentClientCertificateSerialNumber + " Serial number from db " + serialNumberOfResourceCertificate + " Authentication Status FAILURE.");
            return Boolean.FALSE;
        }
        return isAuthenticated;
    }
    
    public String getAgentClientCertificateSerialNumber(final HttpServletRequest httpServletRequest) {
        String agentClientCertificateSerialNumber = null;
        final String X_SGS_AGENT = httpServletRequest.getHeader(AgentUnifiedAuthenticationHandler.x_SGS_AGENT_SSL_Client_Certificate_SerialNumber);
        final String X_DS_AGENT = httpServletRequest.getHeader(AgentUnifiedAuthenticationHandler.x_DS_AGENT_SSL_Client_Certificate_SerialNumber);
        final String X_AGENT = httpServletRequest.getHeader(AgentUnifiedAuthenticationHandler.x_AGENT_SSL_Client_Certificate_SerialNumber);
        if (X_DS_AGENT != null && X_DS_AGENT != "") {
            agentClientCertificateSerialNumber = X_DS_AGENT;
        }
        else if (X_SGS_AGENT != null && X_SGS_AGENT != "") {
            agentClientCertificateSerialNumber = X_SGS_AGENT;
        }
        else if (X_AGENT != null && X_AGENT != "") {
            agentClientCertificateSerialNumber = X_AGENT;
        }
        return agentClientCertificateSerialNumber;
    }
    
    static {
        AgentUnifiedAuthenticationHandler.authLogger = Logger.getLogger("AgentServerAuthLogger");
        AgentUnifiedAuthenticationHandler.accessLogger = Logger.getLogger("ClientCertFilterAccessLogger");
    }
}
