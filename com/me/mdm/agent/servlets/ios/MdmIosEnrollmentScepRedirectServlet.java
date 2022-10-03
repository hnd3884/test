package com.me.mdm.agent.servlets.ios;

import java.util.Iterator;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import com.adventnet.iam.security.SecurityUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import java.io.InputStream;
import com.me.mdm.server.factory.MdmIosScepEnrollmentAPI;
import org.apache.commons.io.IOUtils;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class MdmIosEnrollmentScepRedirectServlet extends HttpServlet
{
    private final Logger logger;
    
    public MdmIosEnrollmentScepRedirectServlet() {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            this.logger.log(Level.INFO, "=============================== Redirecting the GET request =============================");
            final String queryParams = this.constructQueryParams(request);
            final MdmIosScepEnrollmentAPI mdmIosScepEnrollmentAPI = MDMApiFactoryProvider.getIosScepEnrollmentAPI();
            if (mdmIosScepEnrollmentAPI == null) {
                final String redirectUrl = "/mdm/enrollment/identitycertificate/scep?" + queryParams;
                this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: OP, so forwarding the request to actual servlet for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                request.getRequestDispatcher(redirectUrl).forward((ServletRequest)request, (ServletResponse)response);
            }
            else {
                this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Cloud, so making the http request to actual servlet for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                final InputStream inputStream = mdmIosScepEnrollmentAPI.makeHttpRequest("/mdm/enrollment/identitycertificate/scep", queryParams, "GET", null);
                if (inputStream != null) {
                    this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Response received for GET http request to actual servlet for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                    final byte[] responseBytes = IOUtils.toByteArray(inputStream);
                    if (queryParams.contains("GetCACert")) {
                        this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Returning GetCACert response for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                        response.setHeader("Content-Type", "application/x-x509-ca-cert");
                    }
                    else if (queryParams.contains("GetCACaps")) {
                        this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Returning GetCACaps response for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                        response.setHeader("Content-Type", "text/plain");
                    }
                    this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Writing response servlet for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                    response.getOutputStream().write(responseBytes);
                    response.getOutputStream().close();
                }
                else {
                    this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Cloud, Problem while making the http request to actual servlet for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                    response.sendError(500);
                }
            }
            this.logger.log(Level.INFO, "=============================== Redirection successful for GET =============================");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "MdmIosEnrollmentScepRedirectServlet-doGet: Exception while redirecting {GET} request Enrollment request Id - " + httpServletRequest.getParameter("erid") + ", customer id - " + httpServletRequest.getParameter("customerId"));
        }
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            this.logger.log(Level.INFO, "=============================== Redirecting the POST request =============================");
            final String queryParams = this.constructQueryParams(request);
            final MdmIosScepEnrollmentAPI mdmIosScepEnrollmentAPI = MDMApiFactoryProvider.getIosScepEnrollmentAPI();
            if (mdmIosScepEnrollmentAPI == null) {
                final String redirectUrl = "/mdm/enrollment/identitycertificate/scep?" + queryParams;
                this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: OP, so forwarding the POST request for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                request.getRequestDispatcher(redirectUrl).forward((ServletRequest)request, (ServletResponse)response);
            }
            else {
                this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Cloud, so making the http POST request to actual servlet for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                final byte[] pkiMessageBytes = IOUtils.toByteArray((InputStream)request.getInputStream());
                final InputStream inputStream = mdmIosScepEnrollmentAPI.makeHttpRequest("/mdm/enrollment/identitycertificate/scep", queryParams, "POST", pkiMessageBytes);
                if (inputStream != null) {
                    this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Response received for POST http request to actual servlet for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                    final byte[] responseBytes = IOUtils.toByteArray(inputStream);
                    response.setHeader("Content-Type", "application/x-pki-message");
                    this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Writing POST response for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                    response.getOutputStream().write(responseBytes);
                    response.getOutputStream().close();
                }
                else {
                    this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Cloud, Problem while making the POST http request to actual servlet for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(queryParams), this.getCustomerId(queryParams) });
                    response.sendError(500);
                }
            }
            this.logger.log(Level.INFO, "=============================== Redirection successful for POST =============================");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepRedirectServlet-doPost: Exception while redirecting {POST} request", e);
        }
    }
    
    private String constructQueryParams(final HttpServletRequest request) throws Exception {
        try {
            final String operation = request.getParameter("operation");
            final String message = request.getParameter("message");
            final String urlParamsDecoded = this.decodeHexQueryParams(SecurityUtil.getRequestPath(request));
            this.checkAndRejectIfEncapiKeyParamIsNotPresent(urlParamsDecoded);
            String queryParams = urlParamsDecoded + "&operation=" + operation;
            if (message != null && !message.isEmpty()) {
                this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Appending message query param for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(urlParamsDecoded), this.getCustomerId(urlParamsDecoded) });
                queryParams = queryParams + "&message=" + message;
            }
            this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Redirect path successfully constructed for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(urlParamsDecoded), this.getCustomerId(urlParamsDecoded) });
            this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Operation: {0}", new Object[] { operation });
            return queryParams;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepRedirectServlet-getRedirectUrl: Exception while constructing SCEP redirect URL from the request", e);
            throw e;
        }
    }
    
    private String decodeHexQueryParams(String urlPath) throws DecoderException {
        urlPath = urlPath.substring(urlPath.lastIndexOf("/mdm/enrollment/identitycertificate/scepredirect/") + "/mdm/enrollment/identitycertificate/scepredirect/".length());
        if (urlPath.contains("/")) {
            urlPath = urlPath.substring(0, urlPath.indexOf("/"));
        }
        final byte[] redirectParamsBytes = Hex.decodeHex(urlPath.toCharArray());
        final String urlParamsDecoded = new String(redirectParamsBytes);
        this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Query params hex-decoded for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(urlParamsDecoded), this.getCustomerId(urlParamsDecoded) });
        return urlParamsDecoded;
    }
    
    private void checkAndRejectIfEncapiKeyParamIsNotPresent(final String urlParamsDecoded) throws ServletException {
        this.logger.log(Level.INFO, "MdmIosEnrollmentScepRedirectServlet: Checking if encapi key is present for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(urlParamsDecoded), this.getCustomerId(urlParamsDecoded) });
        final Pattern pattern = Pattern.compile("encapiKey\\=[a-z0-9A-Z\\s\\.\\-%=\\/+]{0,200}");
        final Matcher matcher = pattern.matcher(urlParamsDecoded);
        final boolean isEncapiKeyPresent = matcher.find();
        if (!isEncapiKeyPresent) {
            this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepRedirectServlet: Bad request: No encapiKey found for Erid - {0}, customer Id - {1}", new Object[] { this.getErid(urlParamsDecoded), this.getCustomerId(urlParamsDecoded) });
            throw new ServletException();
        }
    }
    
    private String getErid(final String urlQueryParams) {
        try {
            final List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(urlQueryParams, StandardCharsets.UTF_8);
            for (final NameValuePair nameValuePair : nameValuePairs) {
                if (nameValuePair.getName().equals("erid")) {
                    return nameValuePair.getValue();
                }
            }
            return "";
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "MdmIosEnrollmentScepRedirectServlet: Exception while parsing erid for logging purposes.");
            this.logger.log(Level.FINE, "MdmIosEnrollmentScepRedirectServlet: Exception while parsing erid for logging purposes. urlQueryParams - {0}", new Object[] { urlQueryParams });
            return "";
        }
    }
    
    private String getCustomerId(final String urlQueryParams) {
        try {
            final List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(urlQueryParams, StandardCharsets.UTF_8);
            for (final NameValuePair nameValuePair : nameValuePairs) {
                if (nameValuePair.getName().equals("customerId")) {
                    return nameValuePair.getValue();
                }
            }
            return "";
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "MdmIosEnrollmentScepRedirectServlet: Exception while parsing customer id for logging purposes.");
            this.logger.log(Level.FINE, "MdmIosEnrollmentScepRedirectServlet: Exception while parsing customer id for logging purposes. urlQueryParams - {0}", new Object[] { urlQueryParams });
            return "";
        }
    }
}
