package com.me.mdm.agent.servlets.dep;

import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.nio.charset.StandardCharsets;
import com.adventnet.sym.server.mdm.ios.payload.PayloadSigningFactory;
import com.me.mdm.server.enrollment.ios.IOSMobileConfigHandler;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import org.bouncycastle.cms.CMSTypedStream;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.dd.plist.PropertyListParser;
import org.bouncycastle.cms.CMSSignedDataParser;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import com.me.mdm.framework.certificate.MDMSignedCertificateVerifier;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.dd.plist.NSDictionary;
import java.io.OutputStream;
import com.dd.plist.Base64;
import com.me.mdm.server.dep.AdminEnrollmentHandler;
import java.util.logging.Level;
import com.me.mdm.server.adep.DEPEnrollmentUtil;
import org.json.JSONObject;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.servlets.auth.BasicAuthenticatedRequestServlet;

public class IOSDEPServlet extends BasicAuthenticatedRequestServlet
{
    public Logger logger;
    
    public IOSDEPServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.doPost(request, response, deviceRequest);
    }
    
    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.doPost(request, response, deviceRequest);
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            final JSONObject requestJSON = new JSONObject();
            final String requestPath = request.getPathInfo();
            final String templateToken = requestPath.substring(1);
            requestJSON.put("TemplateToken", (Object)templateToken);
            final boolean authenticationEnabled = DEPEnrollmentUtil.isADAuthenticationEnabledForTemplateToken(templateToken);
            final boolean isDepWebView = this.checkRequestBodyForWebView(request, deviceRequest);
            this.parseRequest(request, response, deviceRequest, requestJSON);
            if (response.isCommitted()) {
                return;
            }
            final long enrollmentRequestId = this.getEnrollmentRequestIdIfAlreadyManaged(requestJSON);
            if (enrollmentRequestId != -1L && this.isDeviceReEstablishingCommunication(enrollmentRequestId)) {
                this.logger.log(Level.INFO, "Re-sending the same mobile config as device is re-establishing communication. Udid: {0}", this.getUdidFromRequestJson(requestJSON));
                this.resendSameMobileConfig(enrollmentRequestId, this.getDeviceType(requestJSON), response);
            }
            else if (authenticationEnabled && isDepWebView) {
                this.redirectToSelfEnrollADAuthPage(request, response, requestJSON);
            }
            else if ((!authenticationEnabled || this.authenticate(request, response, requestJSON)) && templateToken != null) {
                final JSONObject newRequestJSON = new JSONObject(requestJSON.toString());
                newRequestJSON.put("MsgRequestType", (Object)"DEPDeviceProvisioning");
                final AdminEnrollmentHandler dep = new AdminEnrollmentHandler();
                final JSONObject responseJSON = dep.processMessage(newRequestJSON);
                final String status = String.valueOf(responseJSON.get("Status"));
                if (status.equals("Acknowledged")) {
                    final JSONObject msgResponseJSON = responseJSON.getJSONObject("MsgResponse");
                    final String mobileConfigEncodedContent = String.valueOf(msgResponseJSON.get("MobileConfigContent"));
                    final byte[] mobileConfigDecodedContent = Base64.decode(mobileConfigEncodedContent);
                    response.setContentType("application/x-apple-aspen-config");
                    response.setHeader("Content-Disposition", "attachment;filename=mdm.mobileconfig");
                    final OutputStream os = (OutputStream)response.getOutputStream();
                    os.write(mobileConfigDecodedContent);
                    os.flush();
                    os.close();
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "DeviceRegistrationServlet : Exception occured while handling messages - ", e);
        }
    }
    
    private void redirectToSelfEnrollADAuthPage(final HttpServletRequest request, final HttpServletResponse response, final JSONObject requestJSON) throws Exception {
        final JSONObject msgRequestJson = new JSONObject(requestJSON.getJSONObject("MsgRequest").toString());
        JSONObject msgJson = new JSONObject();
        msgJson.put("TemplateToken", requestJSON.get("TemplateToken"));
        msgJson.put("TEMPLATE_TOKEN", requestJSON.get("TemplateToken"));
        msgJson.put("SerialNumber", msgRequestJson.get("SerialNumber"));
        msgRequestJson.put("Message", (Object)msgJson);
        msgRequestJson.put("template_type", 10);
        msgRequestJson.put("MsgRequestType", (Object)"DepWebViewSolicitation");
        msgJson = new AdminEnrollmentHandler().processMessage(msgRequestJson);
        response.sendRedirect(msgJson.get("AutherzationURL").toString());
    }
    
    private boolean checkRequestBodyForWebView(final HttpServletRequest request, final DeviceRequest deviceRequest) {
        boolean isDepWebView = false;
        try {
            if (deviceRequest.deviceRequestDatabytes == null || deviceRequest.deviceRequestDatabytes.length == 0) {
                this.logger.log(Level.INFO, "[Apple DepServlet] Device request data is empty.. Going to check for header.");
                final String header = request.getHeader("x-apple-aspen-deviceinfo");
                if (header != null) {
                    isDepWebView = true;
                    deviceRequest.deviceRequestDatabytes = java.util.Base64.getDecoder().decode(header);
                    this.logger.log(Level.INFO, "[Apple DepServlet] Read device details from header. Device using Web view.");
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception on reading requestBodyBytes in IOS DEP servlet..", ex.getMessage());
        }
        return isDepWebView;
    }
    
    public void parseRequest(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest, final JSONObject requestJSON) throws Exception {
        final String templateToken = requestJSON.getString("TemplateToken");
        String serialNumber = "";
        String imei = "";
        final String easID = "";
        String productName = "iPhone";
        String udid = "";
        final String platformString = "iOS";
        NSDictionary nsDictionary = new NSDictionary();
        final boolean isUnsigedFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotVerifyAppleSignedContent");
        final byte[] requestBodyBytes = deviceRequest.deviceRequestDatabytes;
        try {
            Label_0435: {
                try {
                    Label_0239: {
                        if (!MDMSignedCertificateVerifier.isAppleDeviceRequest(requestBodyBytes.clone())) {
                            this.logger.log(Level.INFO, "Unable to verify if request is from Apple.....");
                            if (!isUnsigedFeatureEnabled) {
                                try {
                                    if (requestBodyBytes.length == 0) {
                                        this.logger.log(Level.SEVERE, "No body content in request");
                                        response.sendError(403, "No data in request body , MDM is Unable to authorize request . Expecting request from Apple device only.");
                                        return;
                                    }
                                    final CMSSignedDataParser parser = new CMSSignedDataParser(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build(), (byte[])requestBodyBytes.clone());
                                    final CMSTypedStream cmsTStream = parser.getSignedContent();
                                    this.logger.log(Level.SEVERE, "Request Rejected as MDM could not authorize apple client :{0}", cmsTStream.toString());
                                    break Label_0239;
                                }
                                catch (final Exception ex) {
                                    this.logger.log(Level.SEVERE, "Exception while reading data as signed content:{0}", ex.getMessage());
                                    response.sendError(403, "MDM Unable to authorize client , Expecting request from Apple device only.");
                                    return;
                                }
                            }
                            this.logger.log(Level.INFO, "Feature enabled to allow non-apple devices...");
                        }
                        else {
                            this.logger.log(Level.INFO, "Request verified from Apple device only....");
                        }
                    }
                    final CMSSignedDataParser parser = new CMSSignedDataParser(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build(), (byte[])requestBodyBytes.clone());
                    final CMSTypedStream cmsTStream = parser.getSignedContent();
                    nsDictionary = (NSDictionary)PropertyListParser.parse(cmsTStream.getContentStream());
                }
                catch (final Exception ex) {
                    this.logger.log(Level.INFO, "Unable to read request as CMS signed blob , going to read it as raw Plist");
                    try {
                        nsDictionary = (NSDictionary)PropertyListParser.parse(requestBodyBytes);
                        if (nsDictionary == null) {
                            nsDictionary = new NSDictionary();
                            throw new Exception("Request body is not of Plist");
                        }
                    }
                    catch (final Exception ex2) {
                        this.logger.log(Level.INFO, "Exception while reading request data as raw Plist. Probably request body is empty");
                        this.logger.log(Level.INFO, "Unable to read request as Plist also , going to blindly return a MDM Profile for {0}", productName);
                        final String productNameFromRequest = request.getParameter("PRODUCT");
                        if (productNameFromRequest == null) {
                            this.logger.log(Level.INFO, "No Product name is specified in request parameter PRODUCT , going to return MDM profile for {0}", productName);
                            break Label_0435;
                        }
                        productName = productNameFromRequest;
                        this.logger.log(Level.INFO, "{0} is  the ProductName specified in request param PRODUCT , going to return MDM profile for {1}", new Object[] { productNameFromRequest, productName });
                    }
                }
            }
            this.logger.log(Level.INFO, "IOS DEP Servlet : {0} ", nsDictionary.toXMLPropertyList());
            if (nsDictionary.containsKey("SERIAL")) {
                serialNumber = nsDictionary.objectForKey("SERIAL").toString();
            }
            if (nsDictionary.containsKey("IMEI")) {
                imei = nsDictionary.objectForKey("IMEI").toString();
            }
            if (nsDictionary.containsKey("PRODUCT")) {
                productName = nsDictionary.objectForKey("PRODUCT").toString();
            }
            if (nsDictionary.containsKey("UDID")) {
                udid = nsDictionary.objectForKey("UDID").toString();
            }
        }
        catch (final NullPointerException e) {
            this.logger.log(Level.SEVERE, "Null Pointer Exception while getting signed data. No data in input stream ", e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception while getting signed data ", e2);
        }
        if (templateToken != null) {
            requestJSON.put("DevicePlatform", (Object)platformString);
            final JSONObject msgRequestJSON = new JSONObject();
            msgRequestJSON.put("TemplateToken", (Object)templateToken);
            msgRequestJSON.put("SerialNumber", (Object)serialNumber);
            msgRequestJSON.put("EASID", (Object)easID);
            msgRequestJSON.put("IMEI", (Object)imei);
            msgRequestJSON.put("UDID", (Object)udid);
            msgRequestJSON.put("DeviceType", MDMInvDataPopulator.getModelType(productName));
            final String authorization = this.getAuthorization(request);
            if (authorization != null && !authorization.isEmpty()) {
                String username = this.parseUsername(authorization).trim();
                final String strNetBIOSName = (username.indexOf("\\") <= 0) ? "" : username.substring(0, username.indexOf("\\"));
                username = username.substring(username.indexOf("\\") + 1);
                msgRequestJSON.put("DomainName", (Object)strNetBIOSName);
                msgRequestJSON.put("UserName", (Object)username);
                msgRequestJSON.put("Password", (Object)this.parsePassword(authorization).trim());
            }
            final String adminAuthToken = request.getParameter("authtoken");
            if (adminAuthToken != null) {
                msgRequestJSON.put("device_authtoken", (Object)adminAuthToken);
            }
            final String adminAuthTokenScope = request.getParameter("SCOPE");
            if (adminAuthTokenScope != null) {
                msgRequestJSON.put("device_SCOPE", (Object)adminAuthTokenScope);
            }
            requestJSON.put("MsgRequest", (Object)msgRequestJSON);
        }
    }
    
    @Override
    public boolean authenticate(final String username, final String password, final HttpServletRequest request, final HttpServletResponse response, final JSONObject requestJSON) throws Exception {
        final JSONObject newRequestJSON = new JSONObject(requestJSON.toString());
        newRequestJSON.put("MsgRequestType", (Object)"DEPUserAuthenticate");
        final AdminEnrollmentHandler dep = new AdminEnrollmentHandler();
        final JSONObject responseJSON = dep.processMessage(newRequestJSON);
        final String status = String.valueOf(responseJSON.get("Status"));
        if (status.equals("Acknowledged")) {
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            msgRequestJSON.put("DomainName", newRequestJSON.getJSONObject("MsgRequest").get("DomainName"));
            requestJSON.put("MsgRequest", (Object)msgRequestJSON);
            return true;
        }
        return false;
    }
    
    private boolean isDeviceReEstablishingCommunication(final long enrollmentRequestId) {
        final boolean isReEstablishInitiatedByAdmin = MDMiOSEntrollmentUtil.getInstance().getReenrollReq(enrollmentRequestId);
        this.logger.log(Level.INFO, "Is Re-Establish communication triggered by Admin: {0} | Erid: {1}", new Object[] { isReEstablishInitiatedByAdmin, enrollmentRequestId });
        return isReEstablishInitiatedByAdmin;
    }
    
    private void resendSameMobileConfig(final long enrollmentRequestId, final int deviceType, final HttpServletResponse response) throws Exception {
        this.logger.log(Level.INFO, "IOSDEPServlet: Resending same mobile config for Erid {0} | Device Type: {1}", new Object[] { enrollmentRequestId, deviceType });
        byte[] bytes = IOSMobileConfigHandler.getInstance().generateMobileConfig(enrollmentRequestId, deviceType, 5);
        bytes = PayloadSigningFactory.getInstance().signPayload(new String(bytes, StandardCharsets.UTF_8));
        response.setContentType("application/x-apple-aspen-config");
        response.setHeader("Content-Disposition", "attachment;filename=mdm.mobileconfig");
        final OutputStream os = (OutputStream)response.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
    }
    
    private int getDeviceType(final JSONObject requestJSON) {
        int deviceType = 0;
        final Object keyDeviceType = requestJSON.getJSONObject("MsgRequest").opt("DeviceType");
        if (keyDeviceType != null) {
            deviceType = Integer.parseInt(keyDeviceType.toString());
        }
        return deviceType;
    }
    
    private long getEnrollmentRequestIdIfAlreadyManaged(final JSONObject requestJSON) {
        long enrollmentRequestId = -1L;
        try {
            final String templateToken = requestJSON.getJSONObject("MsgRequest").optString("TemplateToken", (String)null);
            Long customerId = null;
            if (!MDMStringUtils.isEmpty(templateToken)) {
                final JSONObject templateDetails = new EnrollmentTemplateHandler().getEnrollmentTemplateForTemplateToken(templateToken);
                if (templateDetails != null && templateDetails.has("CUSTOMER_ID")) {
                    customerId = templateDetails.getLong("CUSTOMER_ID");
                }
            }
            final String udid = this.getUdidFromRequestJson(requestJSON);
            if (!MDMStringUtils.isEmpty(udid) && customerId != null && customerId != -1L) {
                enrollmentRequestId = MDMEnrollmentRequestHandler.getInstance().getManagedEnrollmentRequestIdForUdid(udid, customerId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "IOSDEPServlet: Exception while picking the old Erid, thus returning as -1: {0}" + jsonObject.toString());
        }
        return enrollmentRequestId;
    }
    
    private String getUdidFromRequestJson(final JSONObject requestJSON) {
        final JSONObject messageRequest = requestJSON.getJSONObject("MsgRequest");
        return messageRequest.optString("UDID", (String)null);
    }
}
