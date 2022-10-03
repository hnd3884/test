package com.me.mdm.agent.handlers.windows;

import java.util.Hashtable;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.server.drp.MDMRegistrationHandler;
import com.me.idps.core.util.ADSyncDataHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.apache.commons.codec.binary.Base64;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.commons.lang.StringUtils;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.me.mdm.server.windows.apps.WpAppSettingsHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.core.windows.enrollment.Win10CertificateEnrollmentWebService;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.core.windows.enrollment.CertificateEnrollmentWebService;
import com.me.mdm.core.windows.enrollment.CertificateEnrollmentPolicyWebService;
import com.me.mdm.core.windows.enrollment.DiscoveryService;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.mdm.core.windows.enrollment.SOAPMessageParser;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class WpEnrollmentRequestHandler extends BaseProcessDeviceRequestHandler
{
    private Logger logger;
    
    public WpEnrollmentRequestHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        final String requestData = (String)request.deviceRequestData;
        final SOAPMessageParser soapParser = new SOAPMessageParser();
        final SOAPMessage soapMessage = soapParser.getSOAPMessage(requestData.getBytes());
        final JSONObject headerMessage = soapParser.parseSOAPHeaders(soapMessage);
        final String action = String.valueOf(headerMessage.get("Action"));
        this.logger.log(Level.INFO, "The received action from windowsphone device : {0}", action);
        String sResponse = null;
        String serverBaseURL = MDMApiFactoryProvider.getMDMUtilAPI().getServerURLOnTomcatPortForClientAuthSetup();
        if (action != null) {
            if (action.equalsIgnoreCase("http://schemas.microsoft.com/windows/management/2012/01/enrollment/IDiscoveryService/Discover")) {
                final DiscoveryService service = new DiscoveryService();
                final JSONObject jsonObject = soapParser.parseDiscoverRequest(soapMessage);
                jsonObject.put("BaseServerURL", (Object)serverBaseURL);
                jsonObject.put("ServletPath", request.requestMap.get("ServletPath"));
                jsonObject.put("pathPrefix", request.requestMap.get("pathPrefix"));
                jsonObject.put("requestURL", request.requestMap.get("requestURL"));
                sResponse = service.processRequest(soapMessage, jsonObject);
            }
            else if (action.equalsIgnoreCase("http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy/IPolicy/GetPolicies")) {
                final CertificateEnrollmentPolicyWebService service2 = new CertificateEnrollmentPolicyWebService();
                headerMessage.put("BaseServerURL", (Object)serverBaseURL);
                headerMessage.put("ServletPath", request.requestMap.get("ServletPath"));
                sResponse = service2.processRequest(soapMessage, headerMessage);
            }
            else if (action.equalsIgnoreCase("http://schemas.microsoft.com/windows/pki/2009/01/enrollment/RST/wstep")) {
                serverBaseURL = MDMApiFactoryProvider.getMDMUtilAPI().getHttpsServerBaseUrl();
                CertificateEnrollmentWebService service3 = new CertificateEnrollmentWebService();
                JSONObject jsonObject = soapParser.parseCertificateEnrollmentRequest(soapMessage);
                String emailAddress = jsonObject.optString("email", (String)null);
                if (emailAddress == null && (jsonObject.has("erid") || jsonObject.has("encapiKey"))) {
                    final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("DeviceEnrollmentRequest"));
                    selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "ManagedUser", new String[] { "MANAGED_USER_ID" }, new String[] { "MANAGED_USER_ID" }, 2));
                    selectQuery.addJoin(new Join("ManagedUser", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2));
                    selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"));
                    selectQuery.addSelectColumn(Column.getColumn("Resource", "CUSTOMER_ID"));
                    selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
                    if (jsonObject.has("erid")) {
                        selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)jsonObject.getLong("erid"), 0));
                    }
                    else if (jsonObject.has("encapiKey")) {
                        selectQuery.addJoin(new Join("DeviceEnrollmentRequest", "DeviceToken", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
                        selectQuery.setCriteria(new Criteria(Column.getColumn("DeviceToken", "TOKEN_ENCRYPTED"), (Object)jsonObject.getString("encapiKey"), 0));
                    }
                    final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
                    if (!dataObject.isEmpty()) {
                        final Row row = dataObject.getRow("ManagedUser");
                        final Row resourceRow = dataObject.getRow("Resource");
                        emailAddress = (String)row.get("EMAIL_ADDRESS");
                        final Long customerID = (Long)resourceRow.get("CUSTOMER_ID");
                        jsonObject.put("email", (Object)emailAddress);
                        jsonObject.put("cid", (Object)customerID.toString());
                    }
                }
                String customerID2 = jsonObject.optString("cid", (String)null);
                if (customerID2 == null && jsonObject.has("erid")) {
                    customerID2 = String.valueOf(MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(Long.valueOf(String.valueOf(jsonObject.get("erid")))));
                    jsonObject.put("cid", (Object)customerID2);
                }
                if (jsonObject.optString("OSVersion", "").startsWith("10")) {
                    service3 = new Win10CertificateEnrollmentWebService();
                }
                jsonObject.put("certificateType", ApiFactoryProvider.getServerSettingsAPI().getCertificateType());
                jsonObject.put("BaseServerURL", (Object)serverBaseURL);
                jsonObject.put("ServletPath", request.requestMap.get("ServletPath"));
                jsonObject = this.setAzureADTemplateDetails(jsonObject);
                final String requestType = String.valueOf(jsonObject.get("RequestType"));
                if (!requestType.equalsIgnoreCase("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Renew")) {
                    final boolean isDeviceAllowed = this.handleAuthentication(jsonObject);
                    if (isDeviceAllowed) {
                        final Boolean isSelfEnroll = jsonObject.optBoolean("isSelfEnroll", (boolean)Boolean.FALSE);
                        if (!isSelfEnroll) {
                            final boolean isAetUploaded = WpAppSettingsHandler.getInstance().isAETUploaded(jsonObject.getLong("cid"));
                            if (isAetUploaded) {
                                final Properties codeSigningProperties = WpAppSettingsHandler.getInstance().getWpAETDetails(jsonObject.getLong("cid"));
                                jsonObject.put("AppTokenAvailable", (Object)Boolean.TRUE);
                                jsonObject.put("CodeSigningExpiration", (Object)((Hashtable<K, Boolean>)codeSigningProperties).get("IS_EXPIRED"));
                                jsonObject.put("EnrollmentToken", (Object)((Hashtable<K, String>)codeSigningProperties).get("APP_ENROLLMENT_TOKEN"));
                                jsonObject.put("EnterpriseID", (Object)((Hashtable<K, String>)codeSigningProperties).get("ENTERPRISE_ID"));
                                jsonObject.put("IsExpiredNow", (Object)WpAppSettingsHandler.getInstance().isWpAETExpiredNow(jsonObject.getLong("cid")));
                            }
                            final Properties companyHubApp = WpCompanyHubAppHandler.getInstance().getCompanyHubAppEnrollmentProp(jsonObject.getLong("cid"));
                            final Boolean isAppBasedEnrollment = Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"));
                            if (companyHubApp != null && !isAppBasedEnrollment) {
                                jsonObject.put("StoreProductId", (Object)((Hashtable<K, String>)companyHubApp).get("StoreProductId"));
                                jsonObject.put("StoreURI", (Object)((Hashtable<K, String>)companyHubApp).get("StoreURI"));
                                jsonObject.put("StoreName", (Object)((Hashtable<K, String>)companyHubApp).get("StoreName"));
                            }
                        }
                    }
                }
                sResponse = service3.processRequest(soapMessage, jsonObject);
            }
        }
        if (sResponse.contains("zapikey")) {
            final String passwordText = StringUtils.substringBetween(sResponse, "<EnrollmentServiceUrl>", "</EnrollmentServiceUrl>");
            final String loggerResponse = sResponse.replace(passwordText, "##########");
            this.logger.log(Level.INFO, "The response sent to device \n{0}", loggerResponse);
        }
        else {
            this.logger.log(Level.INFO, "The response sent to device \n{0}", sResponse);
        }
        return sResponse;
    }
    
    boolean handleAuthentication(final JSONObject jsonObject) {
        boolean isDeviceAllowed = false;
        try {
            final JSONObject authenticateJSON = new JSONObject();
            final String domainUserName = JSONUtil.getString(jsonObject, "Username", null);
            String password = jsonObject.optString("Password", (String)null);
            if (password == null) {
                password = new String(Base64.decodeBase64(String.valueOf(jsonObject.getJSONObject("UserAuthJson").get("tokenValue"))));
            }
            if (jsonObject.has("erid")) {
                authenticateJSON.put("EnrollmentRequestID", jsonObject.get("erid"));
            }
            authenticateJSON.put("OwnedBy", (Object)"Corporate");
            if (MDMStringUtils.isEmpty(domainUserName)) {
                authenticateJSON.put("EmailAddress", (Object)String.valueOf(jsonObject.get("email")));
                authenticateJSON.put("CustomerID", (Object)String.valueOf(jsonObject.get("cid")));
            }
            if (CustomerInfoUtil.isSAS) {
                authenticateJSON.put("EnrollmentRequestID", (Object)String.valueOf(jsonObject.get("erid")));
            }
            if (jsonObject.opt("EnrollmentData") != null && String.valueOf(jsonObject.get("EnrollmentData")).contains("AzureADEnrollment")) {
                authenticateJSON.put("AuthMode", (Object)"AzureADToken");
                authenticateJSON.put("AzureAdWebToken", (Object)password);
            }
            else if (domainUserName == null || domainUserName.indexOf("\\") == -1) {
                authenticateJSON.put("OTPPassword", (Object)password);
                authenticateJSON.put("AuthMode", (Object)"OTP");
            }
            else {
                final int domainIndex = domainUserName.indexOf(92);
                String domainName = "";
                String userName = "";
                if (domainIndex != -1) {
                    domainName = domainUserName.substring(0, domainIndex);
                    userName = domainUserName.substring(domainIndex + 1);
                }
                authenticateJSON.put("UserName", (Object)userName);
                authenticateJSON.put("DomainName", (Object)domainName);
                authenticateJSON.put("ADPassword", (Object)password);
                authenticateJSON.put("AuthMode", (Object)"ActiveDirectory");
                final Properties ADUserProps = ADSyncDataHandler.getInstance().getDirUserProps(CustomerInfoUtil.getInstance().getDefaultCustomer(), domainName, userName);
                authenticateJSON.put("EmailAddress", ((Hashtable<K, Object>)ADUserProps).get("EMAIL_ADDRESS"));
                jsonObject.put("cid", (Object)CustomerInfoUtil.getInstance().getDefaultCustomer());
                jsonObject.put("isSelfEnroll", (Object)Boolean.TRUE);
            }
            authenticateJSON.put("ClientType", (Object)"WindowsPhoneWorkplace");
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("MsgRequestType", (Object)"Authenticate");
            authenticateJSON.put("RegistrationType", (Object)"MDMRegistration");
            requestJSON.put("MsgRequest", (Object)authenticateJSON);
            requestJSON.put("DevicePlatform", (Object)"WindowsPhone");
            requestJSON.put("Status", (Object)"Acknowledged");
            MDMRegistrationHandler mdmRegistrationHandler = null;
            if (jsonObject.optString("ApplicationVersion").startsWith("8.0.")) {
                mdmRegistrationHandler = MDMRegistrationHandler.getInstance("WindowsPhone8");
            }
            else {
                mdmRegistrationHandler = MDMRegistrationHandler.getInstance("WindowsPhone");
            }
            final JSONObject authenticateResponseJSON = mdmRegistrationHandler.processMessage(requestJSON);
            if (String.valueOf(authenticateResponseJSON.get("Status")).equalsIgnoreCase("Acknowledged")) {
                final JSONObject msgResponseJSON = authenticateResponseJSON.getJSONObject("MsgResponse");
                final Long enrollmentRequestID = msgResponseJSON.getLong("EnrollmentRequestID");
                jsonObject.put("erid", (Object)enrollmentRequestID);
                final Properties properties = ManagedUserHandler.getInstance().getManagedUserDetailsForRequestAsProperties(enrollmentRequestID);
                jsonObject.put("muid", ((Hashtable<K, Object>)properties).get("MANAGED_USER_ID"));
                isDeviceAllowed = true;
                jsonObject.put("isDeviceAllowed", (Object)Boolean.TRUE);
            }
            else {
                isDeviceAllowed = false;
                jsonObject.put("isDeviceAllowed", (Object)Boolean.FALSE);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during user authentication \n", exp);
        }
        return isDeviceAllowed;
    }
    
    private JSONObject setAzureADTemplateDetails(final JSONObject jsonObject) throws DataAccessException, JSONException {
        if (jsonObject.optString("EnrollmentData", "").contains("AzureADEnrollment")) {
            String templateToken = "";
            final Criteria customerIDCriteria = new Criteria(Column.getColumn("EnrollmentTemplate", "CUSTOMER_ID"), (Object)Long.parseLong(String.valueOf(jsonObject.get("cid"))), 0);
            final Criteria templateType = new Criteria(Column.getColumn("EnrollmentTemplate", "TEMPLATE_TYPE"), (Object)32, 0);
            final DataObject dao = MDMUtil.getPersistence().get("EnrollmentTemplate", customerIDCriteria.and(templateType));
            if (!dao.isEmpty()) {
                templateToken = (String)dao.getFirstRow("EnrollmentTemplate").get("TEMPLATE_TOKEN");
            }
            jsonObject.put("templateToken", (Object)templateToken);
        }
        return jsonObject;
    }
}
