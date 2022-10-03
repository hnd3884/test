package mdm.integrations.certificateauthority.digicert.v1;

import javax.xml.soap.DetailEntry;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.HashMap;
import java.util.Iterator;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.client.Options;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import com.verisign.schemas.pkiservices._2011._08.usermanagement.UserManagementServiceStub;
import java.util.logging.Level;
import java.util.Map;
import javax.net.ssl.SSLContext;
import java.util.logging.Logger;

public class DigicertWSHandler
{
    Logger logger;
    private final String proxyHost;
    private final int proxyPort;
    private final String proxyUsername;
    private final String proxyPassword;
    private final SSLContext sslContext;
    private final String certificateOID;
    private final Map<Long, Map<String, String>> users;
    
    public DigicertWSHandler(final Map<String, Object> proxyDetails, final SSLContext sslContext, final Map<Long, Map<String, String>> userDetailsMap, final String certificateOID) {
        this.logger = Logger.getLogger("MdmCertificateIntegLogger");
        if (!String.valueOf(proxyDetails.get("PROXY_HOST")).isEmpty()) {
            this.proxyHost = proxyDetails.get("PROXY_HOST");
        }
        else {
            this.proxyHost = "";
        }
        if (!String.valueOf(proxyDetails.get("PROXY_PORT")).isEmpty()) {
            this.proxyPort = proxyDetails.get("PROXY_PORT");
        }
        else {
            this.proxyPort = -1;
        }
        this.proxyUsername = proxyDetails.get("PROXY_USER_NAME");
        this.proxyPassword = proxyDetails.get("PROXY_PASSWORD");
        this.sslContext = sslContext;
        this.certificateOID = certificateOID;
        this.users = userDetailsMap;
    }
    
    public Map<Long, Map<String, String>> createUserAndGetPasscodes() {
        try {
            this.logger.log(Level.INFO, "Going to get passcodes for resources: Certificate OID: {0}, Resources: {1}", new Object[] { this.certificateOID, this.users.keySet() });
            final UserManagementServiceStub userMgmtStub = this.getUserMgmtStubWithProxyAndSslContextConfigured();
            final UserManagementServiceStub.CreateOrUpdatePasscodeRequest passcodeRequest = this.createPasscodeRequestForUsers();
            this.logger.log(Level.INFO, "Initiating communication with digicert: Start time: {0}, Certificate OID: {1}, Resources: {2}", new Object[] { System.currentTimeMillis(), this.certificateOID, this.users.keySet() });
            final UserManagementServiceStub.CreateOrUpdatePasscodeResponse passcodeResponse = userMgmtStub.createOrUpdatePasscode(passcodeRequest);
            this.logger.log(Level.INFO, "Communication with digicert ended: End time: {0}, Certificate OID: {1}, Resources: {2}", new Object[] { System.currentTimeMillis(), this.certificateOID, this.users.keySet() });
            return this.getPasscodesFromResponse(passcodeResponse);
        }
        catch (final Exception e) {
            final String eMessage = "Exception while getting passcode from digicert: Certificate OID: " + this.certificateOID;
            this.logger.log(Level.SEVERE, eMessage, e);
            return this.constructMapWithFailureMessage(e);
        }
    }
    
    private UserManagementServiceStub getUserMgmtStubWithProxyAndSslContextConfigured() throws AxisFault {
        this.logger.log(Level.INFO, "Configuring proxy for transport: Certificate OID: {0}, Resources: {1}", new Object[] { this.certificateOID, this.users.keySet() });
        final Protocol customSocket = new Protocol("https", (SecureProtocolSocketFactory)new CustomSocketFactory(this.sslContext), 443);
        final UserManagementServiceStub userManagementServiceStub = new UserManagementServiceStub();
        final Options soapRequestOptions = userManagementServiceStub._getServiceClient().getOptions();
        if (!this.proxyHost.isEmpty() && this.proxyPort != -1) {
            final HttpTransportProperties.ProxyProperties proxyProperties = this.getProxyProperties();
            soapRequestOptions.setProperty("PROXY", (Object)proxyProperties);
        }
        soapRequestOptions.setProperty("CUSTOM_PROTOCOL_HANDLER", (Object)customSocket);
        return userManagementServiceStub;
    }
    
    private HttpTransportProperties.ProxyProperties getProxyProperties() {
        final HttpTransportProperties.ProxyProperties proxyProperties = new HttpTransportProperties.ProxyProperties();
        proxyProperties.setProxyName(this.proxyHost);
        proxyProperties.setProxyPort(this.proxyPort);
        proxyProperties.setUserName(this.proxyUsername);
        proxyProperties.setPassWord(this.proxyPassword);
        return proxyProperties;
    }
    
    private UserManagementServiceStub.CreateOrUpdatePasscodeRequest createPasscodeRequestForUsers() throws NoSuchAlgorithmException {
        final UserManagementServiceStub.CreateOrUpdatePasscodeRequest passcodeCreationRequest = new UserManagementServiceStub.CreateOrUpdatePasscodeRequest();
        passcodeCreationRequest.setCreateOrUpdatePasscodeRequest(this.getPasswordRequestMessage());
        return passcodeCreationRequest;
    }
    
    private UserManagementServiceStub.CreateOrUpdatePasscodeRequestMessageType getPasswordRequestMessage() throws NoSuchAlgorithmException {
        final UserManagementServiceStub.CreateOrUpdatePasscodeRequestMessageType passcodeRequestMessage = new UserManagementServiceStub.CreateOrUpdatePasscodeRequestMessageType();
        this.addBasicTxnDetailsToPasscodeRequestMessage(passcodeRequestMessage);
        this.addUserDetailsToPasscodeRequestMessage(passcodeRequestMessage);
        return passcodeRequestMessage;
    }
    
    private void addBasicTxnDetailsToPasscodeRequestMessage(final UserManagementServiceStub.CreateOrUpdatePasscodeRequestMessageType passcodeRequestMessage) throws NoSuchAlgorithmException {
        this.logger.log(Level.INFO, "Adding basic transaction details: Certificate OID: {0}, Resources: {1}", new Object[] { this.certificateOID, this.users.keySet() });
        this.setVersionToPasscodeRequestMessage(passcodeRequestMessage);
        this.setTxnIdToPasscodeRequestMessage(passcodeRequestMessage);
    }
    
    private void setVersionToPasscodeRequestMessage(final UserManagementServiceStub.CreateOrUpdatePasscodeRequestMessageType requestMessage) {
        this.logger.log(Level.INFO, "Adding version: Certificate OID: {0}, Resources: {1}", new Object[] { this.certificateOID, this.users.keySet() });
        final UserManagementServiceStub.VersionType versionType = new UserManagementServiceStub.VersionType();
        versionType.setVersionType("1.0");
        requestMessage.setVersion(versionType);
    }
    
    private void setTxnIdToPasscodeRequestMessage(final UserManagementServiceStub.CreateOrUpdatePasscodeRequestMessageType requestMessage) throws NoSuchAlgorithmException {
        this.logger.log(Level.INFO, "Adding transaction id: Certificate OID: {0}, Resources: {1}", new Object[] { this.certificateOID, this.users.keySet() });
        final UserManagementServiceStub.TransactionIDType transactionIDType = new UserManagementServiceStub.TransactionIDType();
        transactionIDType.setTransactionIDType(this.generateClientTransactionId());
        requestMessage.setClientTransactionID(transactionIDType);
    }
    
    private String generateClientTransactionId() throws NoSuchAlgorithmException {
        int randomInt = SecureRandom.getInstance("SHA1PRNG").nextInt();
        randomInt = ((randomInt < 0) ? (randomInt * -1) : randomInt);
        return "PKI " + randomInt;
    }
    
    private void addUserDetailsToPasscodeRequestMessage(final UserManagementServiceStub.CreateOrUpdatePasscodeRequestMessageType passcodeRequestMessage) {
        final UserManagementServiceStub.PasscodeInformationType[] passcodeCreationDetails = this.constructPasswordRequestForEachUserWithPersonalDetails();
        passcodeRequestMessage.setPasscodeInformation(passcodeCreationDetails);
    }
    
    private UserManagementServiceStub.PasscodeInformationType[] constructPasswordRequestForEachUserWithPersonalDetails() {
        final UserManagementServiceStub.PasscodeInformationType[] passCodeInformationArr = new UserManagementServiceStub.PasscodeInformationType[this.users.keySet().size()];
        int temp = 0;
        for (final Map.Entry<Long, Map<String, String>> userEntry : this.users.entrySet()) {
            this.logger.log(Level.INFO, "Constructing passcode request for: {0}, Certificate OID: {1}", new Object[] { userEntry.getKey(), this.certificateOID });
            final Map<String, String> userDetails = userEntry.getValue();
            passCodeInformationArr[temp++] = this.constructPasscodeRequestInfoForUser(userDetails);
        }
        return passCodeInformationArr;
    }
    
    private UserManagementServiceStub.PasscodeInformationType constructPasscodeRequestInfoForUser(final Map<String, String> user) {
        final UserManagementServiceStub.PasscodeInformationType passCodeInformationType = new UserManagementServiceStub.PasscodeInformationType();
        passCodeInformationType.setCertificateProfileOid(this.certificateOID);
        passCodeInformationType.setSeatId((String)user.get("EMAIL_ADDRESS"));
        passCodeInformationType.setFirstName((String)user.get("FIRST_NAME"));
        passCodeInformationType.setLastName((String)user.get("LAST_NAME"));
        passCodeInformationType.setEmail((String)user.get("EMAIL_ADDRESS"));
        return passCodeInformationType;
    }
    
    public Map<Long, Map<String, String>> getPasscodesFromResponse(final UserManagementServiceStub.CreateOrUpdatePasscodeResponse responseFromDigicert) {
        this.logger.log(Level.INFO, "Getting passcodes from response: Certificate OID: {0}", new Object[] { this.certificateOID });
        final UserManagementServiceStub.CreateOrUpdatePasscodeResponseMessageType passcodeResponses = responseFromDigicert.getCreateOrUpdatePasscodeResponse();
        final UserManagementServiceStub.PasscodeCreationStatusType[] passcodeCreationStatusList = passcodeResponses.getPasscodeCreationStatus();
        final Map<Long, Map<String, String>> resourceToPasscodeCreationResponseMap = new HashMap<Long, Map<String, String>>();
        for (final UserManagementServiceStub.PasscodeCreationStatusType createdPasscodeInfo : passcodeCreationStatusList) {
            final String seatId = createdPasscodeInfo.getPasscodeInformation().getSeatId();
            final Long resourceId = this.getResourceIdForSeatId(seatId);
            this.logger.log(Level.INFO, "Passcode creation response for: {0}", new Object[] { resourceId });
            final Map<String, String> passcodeCreationResponse = this.getPasscodeCreationResponse(createdPasscodeInfo);
            resourceToPasscodeCreationResponseMap.put(resourceId, passcodeCreationResponse);
            this.users.remove(resourceId);
        }
        return resourceToPasscodeCreationResponseMap;
    }
    
    public Map<String, String> getPasscodeCreationResponse(final UserManagementServiceStub.PasscodeCreationStatusType passcodeInfo) {
        final String status = passcodeInfo.getStatusCode();
        final Map<String, String> passcodeResponseMap = new HashMap<String, String>();
        passcodeResponseMap.put("STATUS", status);
        this.logger.log(Level.INFO, "Passcode creation status: Status: {0}", new Object[] { status });
        if (status.equals("0")) {
            final String passcode = passcodeInfo.getPasscodeInformation().getPasscode();
            passcodeResponseMap.put("PASSCODE", passcode);
        }
        else {
            passcodeResponseMap.put("PASSCODE", null);
        }
        return passcodeResponseMap;
    }
    
    private Map<Long, Map<String, String>> constructMapWithFailureMessage(final Exception e) {
        if (e instanceof SOAPFaultException) {
            final String faultCode = this.getFaultCode((SOAPFaultException)e);
            return this.constructMap(faultCode);
        }
        final String statusCode = this.getStatusCodeForErrorMessage(e.getMessage());
        this.logger.log(Level.INFO, "Error message for exception: {0}", new Object[] { statusCode });
        return this.constructMap(statusCode);
    }
    
    private Map<Long, Map<String, String>> constructMap(final String statusCode) {
        final Map<Long, Map<String, String>> passwordReqResponseMap = new HashMap<Long, Map<String, String>>();
        for (final Long resource : this.users.keySet()) {
            final Map<String, String> passwordResponse = new HashMap<String, String>();
            passwordResponse.put("PASSCODE", null);
            passwordResponse.put("STATUS", statusCode);
            passwordReqResponseMap.put(resource, passwordResponse);
        }
        return passwordReqResponseMap;
    }
    
    private String getStatusCodeForErrorMessage(final String errorMessage) {
        this.logger.log(Level.INFO, "Error message: {0}", new Object[] { errorMessage });
        switch (errorMessage) {
            case "Authentication failed.": {
                return "A300";
            }
            case "RA certificate expired": {
                return "A301";
            }
            case "RA certificate revoked": {
                return "A302";
            }
            case "Invalid seat Id": {
                return "A515";
            }
            case "Internal server error": {
                return "A600";
            }
            case "Invalid certificate OID": {
                return "A505";
            }
            case "Certificate profile inactive": {
                return "A201";
            }
            case "Exceeded the number of certificates available for this account.": {
                return "A605";
            }
            default: {
                return null;
            }
        }
    }
    
    private String getFaultCode(final SOAPFaultException sfe) {
        final Iterator errorIter = sfe.getFault().getDetail().getChildElements();
        if (errorIter != null) {
            String errorMessage = null;
            if (errorIter.hasNext()) {
                errorMessage = this.getErrorContent(errorIter);
                this.logger.log(Level.SEVERE, "Error code: {0}", new Object[] { errorMessage });
            }
            if (errorIter.hasNext()) {
                final String errorCode = this.getErrorContent(errorIter);
                this.logger.log(Level.SEVERE, "Error code: {0}", new Object[] { errorCode });
            }
            if (errorIter.hasNext()) {
                final String serverTxnId = this.getErrorContent(errorIter);
                this.logger.log(Level.SEVERE, "Server transaction Id: {0}", new Object[] { serverTxnId });
            }
            return errorMessage;
        }
        String faultCode = sfe.getFault().getFaultCode();
        if (sfe.getFault() != null && faultCode != null) {
            faultCode = sfe.getFault().getFaultCode();
            return faultCode;
        }
        return "";
    }
    
    private String getErrorContent(final Iterator errorIter) {
        final DetailEntry serverTxnIdEntry = errorIter.next();
        if (serverTxnIdEntry.getTextContent() != null) {
            return serverTxnIdEntry.getTextContent();
        }
        return "";
    }
    
    public Long getResourceIdForSeatId(final String seatId) {
        for (final Map.Entry<Long, Map<String, String>> user : this.users.entrySet()) {
            final Long resource = user.getKey();
            final Map<String, String> userInfo = user.getValue();
            final String emailAddress = userInfo.get("EMAIL_ADDRESS");
            if (seatId.equals(emailAddress)) {
                return resource;
            }
        }
        return null;
    }
}
