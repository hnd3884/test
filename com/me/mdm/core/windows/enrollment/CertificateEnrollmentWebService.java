package com.me.mdm.core.windows.enrollment;

import org.json.JSONException;
import com.me.mdm.core.auth.APIKey;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Map;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.net.URLEncoder;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.certificate.CertificateUtils;
import com.me.mdm.certificate.CertificateHandler;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMFactory;
import java.text.DateFormat;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAbstractFactory;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Collection;
import org.bouncycastle.util.Store;
import java.security.PrivateKey;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import com.me.devicemanagement.framework.server.certificate.CertificateGenerator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.cms.CMSSignedData;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.apache.commons.codec.binary.Base64;
import com.adventnet.sym.server.mdm.certificates.CertificateUtil;
import java.security.cert.X509Certificate;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.certificates.MdmCertAuthUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import org.apache.axiom.soap.SOAPMessage;
import java.util.logging.Logger;

public class CertificateEnrollmentWebService
{
    Logger logger;
    
    public CertificateEnrollmentWebService() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public String processRequest(final SOAPMessage soapMessage, final JSONObject jsonObject) {
        String responseString = null;
        try {
            final boolean isDeviceAllowed = jsonObject.optBoolean("isDeviceAllowed", false);
            if (isDeviceAllowed) {
                String customerID = jsonObject.optString("cid", (String)null);
                if (customerID == null) {
                    customerID = CustomerInfoUtil.getInstance().getDefaultCustomer().toString();
                }
                final String serverCertificateFile = MdmCertAuthUtil.Scep.getScepRootCACertificatePath(Long.parseLong(customerID));
                final String serverKeyFile = MdmCertAuthUtil.Scep.getScepRootCAPrivateKeyPath(Long.parseLong(customerID));
                this.logger.log(Level.INFO, "certificate Path used {0} ", serverCertificateFile);
                final InputStream certificateStream = ApiFactoryProvider.getFileAccessAPI().readFile(serverCertificateFile);
                final X509Certificate serverCertificate = (X509Certificate)CertificateUtil.convertInputStreamToX509CertificateChain(certificateStream)[0];
                final PrivateKey serverPrivateKey = CertificateUtil.convertInputStreamToRsaPrivateKey(ApiFactoryProvider.getFileAccessAPI().readFile(serverKeyFile));
                final String binarySecurityToken = String.valueOf(jsonObject.get("BinarySecurityToken"));
                final byte[] sDeviceDecodedBinaryToken = Base64.decodeBase64(binarySecurityToken.getBytes());
                final String windowsVendorPass = MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("WindowsVendorPass");
                final String nonceStr = this.nonceHash("MEMDM", windowsVendorPass, new String(sDeviceDecodedBinaryToken));
                final String requestType = String.valueOf(jsonObject.get("RequestType"));
                X509Certificate signedCertificate = null;
                if (requestType.equalsIgnoreCase("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Renew")) {
                    final CMSSignedData signedData = new CMSSignedData(sDeviceDecodedBinaryToken);
                    final Store certStore = signedData.getCertificates();
                    final Collection collection = certStore.getMatches((Selector)null);
                    final Iterator it = collection.iterator();
                    final X509CertificateHolder certificateHolder = it.next();
                    final X509Certificate certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certificateHolder.getEncoded()));
                    signedCertificate = CertificateGenerator.getInstance().generateClientCertificate(serverCertificate, serverPrivateKey, certificate.getPublicKey(), certificate.getSubjectDN().getName());
                }
                else {
                    final JcaPKCS10CertificationRequest clientCSR = new JcaPKCS10CertificationRequest(sDeviceDecodedBinaryToken);
                    if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("signWindowsWithUDID")) {
                        signedCertificate = CertificateGenerator.getInstance().generateClientCertificate(serverCertificate, serverPrivateKey, clientCSR.getPublicKey(), "MEMDMClientAuthentication");
                    }
                    else {
                        final String udid = jsonObject.getString("DeviceID");
                        signedCertificate = CertificateGenerator.getInstance().generateClientCertificate(serverCertificate, serverPrivateKey, clientCSR.getPublicKey(), new String(Base64.encodeBase64(udid.getBytes())));
                        this.logger.log(Level.INFO, "Signing was done for UDID {0} with B64UDID as subject", udid);
                    }
                }
                final String wpDMServerURL = this.constructServerURL(jsonObject);
                final String sDeviceProvisioningXMl = this.getDeviceProvisioningElement(jsonObject, signedCertificate, nonceStr, wpDMServerURL);
                final String encodedDeviceProvisioningXMlString = Base64.encodeBase64String(sDeviceProvisioningXMl.getBytes());
                responseString = this.getRSTRResponseString(jsonObject, encodedDeviceProvisioningXMlString);
            }
            else {
                responseString = this.getFaultMessage(jsonObject, "Authentication is failed");
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during processRequestForDEPToken of CertificateEnrollmentWebService \n", exp);
        }
        this.logger.log(Level.INFO, "CertificateEnrollmentWebService :: responseString :: {0}", responseString);
        return responseString;
    }
    
    public String nonceHash(final String mdmProviderId, final String password, final String nonce) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final String usrPwd = mdmProviderId + ":" + password;
        final String usrPwdHash = Base64.encodeBase64String(digest.digest(usrPwd.getBytes("utf-8")));
        final String usrPwdNonce = usrPwdHash + ":" + nonce;
        final String usrPwdNonceHash = Base64.encodeBase64String(digest.digest(usrPwdNonce.getBytes("utf-8")));
        return usrPwdNonceHash;
    }
    
    private String getFaultMessage(final JSONObject headerObject, final String errorMessage) {
        final DateFormat zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        zulu.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date createdDate = new Date();
        final Date expiredDate = new Date();
        expiredDate.setTime(createdDate.getTime() + 600000L);
        final String strCreatedDate = zulu.format(createdDate).toString();
        final String strExpiredDate = zulu.format(expiredDate).toString();
        final String relatesMessageID = headerObject.optString("MessageID", (String)null);
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMNamespace soapEnvelopeNameSpace = omfac.createOMNamespace("http://www.w3.org/2003/05/soap-envelope", "s");
        final SOAPEnvelope soapEnvelope = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPEnvelope(soapEnvelopeNameSpace);
        final OMNamespace soapAddressNameSpace = omfac.createOMNamespace("http://www.w3.org/2005/08/addressing", "a");
        soapEnvelope.declareNamespace(soapAddressNameSpace);
        final OMNamespace wsSecurityUtilityNameSpace = omfac.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "u");
        soapEnvelope.declareNamespace(wsSecurityUtilityNameSpace);
        final SOAPHeader soapHeader = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPHeader(soapEnvelope);
        final SOAPHeaderBlock omActionElement = soapHeader.addHeaderBlock("Action", soapAddressNameSpace);
        omActionElement.addAttribute("mustUnderstand", "1", soapEnvelopeNameSpace);
        omActionElement.setText("http://schemas.microsoft.com/windows/pki/2009/01/enrollment/RSTRC/wstep");
        soapHeader.addChild((OMNode)omActionElement);
        final SOAPHeaderBlock relatesToElement = soapHeader.addHeaderBlock("RelatesTo", soapAddressNameSpace);
        relatesToElement.setText(relatesMessageID);
        soapHeader.addChild((OMNode)relatesToElement);
        final OMNamespace osecurityNameSpace = omfac.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "o");
        final SOAPHeaderBlock securityElement = soapHeader.addHeaderBlock("Security", osecurityNameSpace);
        securityElement.addAttribute("mustUnderstand", "1", soapEnvelopeNameSpace);
        final OMElement timestampElement = omfac.createOMElement("Timestamp", wsSecurityUtilityNameSpace);
        timestampElement.addAttribute("id", "1", wsSecurityUtilityNameSpace);
        final OMElement createdElement = omfac.createOMElement("Created", wsSecurityUtilityNameSpace);
        createdElement.setText(strCreatedDate);
        final OMElement expiredElement = omfac.createOMElement("Expires", wsSecurityUtilityNameSpace);
        expiredElement.setText(strExpiredDate);
        timestampElement.addChild((OMNode)createdElement);
        timestampElement.addChild((OMNode)expiredElement);
        securityElement.addChild((OMNode)timestampElement);
        final OMElement faultElement = omfac.createOMElement("Fault", soapEnvelopeNameSpace);
        final OMElement faultCodeElement = omfac.createOMElement("Code", soapEnvelopeNameSpace);
        final OMElement codeValueElement = omfac.createOMElement("Value", soapEnvelopeNameSpace);
        codeValueElement.setText("s:Receiver");
        final OMElement subCodeElement = omfac.createOMElement("Subcode", soapEnvelopeNameSpace);
        final OMElement subCodeValueElement = omfac.createOMElement("Value", soapEnvelopeNameSpace);
        subCodeValueElement.setText("s:MessageFormat");
        subCodeElement.addChild((OMNode)subCodeValueElement);
        faultCodeElement.addChild((OMNode)codeValueElement);
        faultCodeElement.addChild((OMNode)subCodeElement);
        final OMElement reasonElement = omfac.createOMElement("Reason", soapEnvelopeNameSpace);
        final OMElement textElement = omfac.createOMElement("Text", soapEnvelopeNameSpace);
        textElement.setText(errorMessage);
        reasonElement.addChild((OMNode)textElement);
        faultElement.addChild((OMNode)faultCodeElement);
        faultElement.addChild((OMNode)reasonElement);
        final SOAPBody soapBody = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPBody(soapEnvelope);
        soapBody.addChild((OMNode)faultElement);
        return soapEnvelope.toString();
    }
    
    private String getRSTRResponseString(final JSONObject headerObject, final String deviceProvisioningBase64Binary) throws Exception {
        final DateFormat zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        zulu.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date createdDate = new Date();
        final Date expiredDate = new Date();
        expiredDate.setTime(createdDate.getTime() + 600000L);
        final String strCreatedDate = zulu.format(createdDate).toString();
        final String strExpiredDate = zulu.format(expiredDate).toString();
        final String relatesMessageID = headerObject.optString("MessageID", (String)null);
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMNamespace soapEnvelopeNameSpace = omfac.createOMNamespace("http://www.w3.org/2003/05/soap-envelope", "s");
        final SOAPEnvelope soapEnvelope = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPEnvelope(soapEnvelopeNameSpace);
        final OMNamespace soapAddressNameSpace = omfac.createOMNamespace("http://www.w3.org/2005/08/addressing", "a");
        soapEnvelope.declareNamespace(soapAddressNameSpace);
        final OMNamespace wsSecurityUtilityNameSpace = omfac.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "u");
        soapEnvelope.declareNamespace(wsSecurityUtilityNameSpace);
        final SOAPHeader soapHeader = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPHeader(soapEnvelope);
        final SOAPHeaderBlock omActionElement = soapHeader.addHeaderBlock("Action", soapAddressNameSpace);
        omActionElement.addAttribute("mustUnderstand", "1", soapEnvelopeNameSpace);
        omActionElement.setText("http://schemas.microsoft.com/windows/pki/2009/01/enrollment/RSTRC/wstep");
        soapHeader.addChild((OMNode)omActionElement);
        final SOAPHeaderBlock relatesToElement = soapHeader.addHeaderBlock("RelatesTo", soapAddressNameSpace);
        relatesToElement.setText(relatesMessageID);
        soapHeader.addChild((OMNode)relatesToElement);
        final OMNamespace osecurityNameSpace = omfac.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "o");
        final SOAPHeaderBlock securityElement = soapHeader.addHeaderBlock("Security", osecurityNameSpace);
        securityElement.addAttribute("mustUnderstand", "1", soapEnvelopeNameSpace);
        final OMElement timestampElement = omfac.createOMElement("Timestamp", wsSecurityUtilityNameSpace);
        timestampElement.addAttribute("id", "1", wsSecurityUtilityNameSpace);
        final OMElement createdElement = omfac.createOMElement("Created", wsSecurityUtilityNameSpace);
        createdElement.setText(strCreatedDate);
        final OMElement expiredElement = omfac.createOMElement("Expires", wsSecurityUtilityNameSpace);
        expiredElement.setText(strExpiredDate);
        timestampElement.addChild((OMNode)createdElement);
        timestampElement.addChild((OMNode)expiredElement);
        securityElement.addChild((OMNode)timestampElement);
        final OMNamespace wsTrustNameSpace = omfac.createOMNamespace("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "");
        OMElement rstrCollectionElement = omfac.createOMElement("RequestSecurityTokenResponseCollection", wsTrustNameSpace);
        final OMElement rstrElement = omfac.createOMElement("RequestSecurityTokenResponse", wsTrustNameSpace);
        final OMElement tokenTypeElement = omfac.createOMElement("TokenType", (OMNamespace)null);
        tokenTypeElement.setText("http://schemas.microsoft.com/5.0.0.0/ConfigurationManager/Enrollment/DeviceEnrollmentToken");
        rstrElement.addChild((OMNode)tokenTypeElement);
        final OMElement rstElement = omfac.createOMElement("RequestedSecurityToken", wsTrustNameSpace);
        final OMNamespace wssecurityNameSpace = omfac.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "");
        final OMElement bstElement = omfac.createOMElement("BinarySecurityToken", wssecurityNameSpace);
        bstElement.addAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd#base64binary", (OMNamespace)null);
        bstElement.addAttribute("ValueType", "http://schemas.microsoft.com/5.0.0.0/ConfigurationManager/Enrollment/DeviceEnrollmentProvisionDoc", (OMNamespace)null);
        bstElement.setText(deviceProvisioningBase64Binary);
        rstElement.addChild((OMNode)bstElement);
        final OMNamespace enrollmentNameSpace = omfac.createOMNamespace("http://schemas.microsoft.com/windows/pki/2009/01/enrollment", "");
        final OMElement requestElement = omfac.createOMElement("RequestID", enrollmentNameSpace);
        requestElement.setText("0");
        rstrElement.addChild((OMNode)rstElement);
        rstrElement.addChild((OMNode)requestElement);
        rstrCollectionElement.addChild((OMNode)rstrElement);
        String finalData = rstrCollectionElement.toString();
        finalData = finalData.replaceAll(" xmlns=\"\"", "");
        rstrCollectionElement = AXIOMUtil.stringToOM(finalData);
        final SOAPBody soapBody = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPBody(soapEnvelope);
        soapBody.addChild((OMNode)rstrCollectionElement);
        return soapEnvelope.toString();
    }
    
    private String getDeviceProvisioningElement(final JSONObject jsonObject, final X509Certificate signedCertificate, final String nonceStr, final String sWAPServerURL) throws Exception {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement deviceProvisioningElement = omfac.createOMElement("wap-provisioningdoc", (OMNamespace)null);
        deviceProvisioningElement.addAttribute(omfac.createOMAttribute("version", (OMNamespace)null, "1.1"));
        final String clientCertStore = jsonObject.optString("EnrollmentType", "Full").equalsIgnoreCase("Device") ? "System" : "User";
        final OMElement certificateStoreElement = this.getCertificateStoreElement(signedCertificate, jsonObject);
        deviceProvisioningElement.addChild((OMNode)certificateStoreElement);
        final String clientCommonName = signedCertificate.getSubjectX500Principal().getName();
        final OMElement applicationElement = this.getApplicationElement(clientCommonName, nonceStr, sWAPServerURL, clientCertStore);
        deviceProvisioningElement.addChild((OMNode)applicationElement);
        final OMElement registryElement = this.getRegistryElement();
        if (registryElement != null) {
            deviceProvisioningElement.addChild((OMNode)registryElement);
        }
        final OMElement dmClientElement = this.getDMClientElement();
        deviceProvisioningElement.addChild((OMNode)dmClientElement);
        if (jsonObject != null && jsonObject.optBoolean("AppTokenAvailable", false) && !jsonObject.optBoolean("IsExpiredNow", false)) {
            final OMElement enterpriseAppManagement = this.getEnterpriseAppManagementElement(jsonObject, clientCommonName);
            if (enterpriseAppManagement != null) {
                deviceProvisioningElement.addChild((OMNode)enterpriseAppManagement);
            }
        }
        return deviceProvisioningElement.toString();
    }
    
    private OMElement getCertificateStoreElement(final X509Certificate signedCertificate, final JSONObject jsonObject) throws Exception {
        final String clientCertStore = jsonObject.optString("EnrollmentType", "Full").equalsIgnoreCase("Device") ? "System" : "User";
        X509Certificate serverCertificate = CertificateHandler.getInstance().getAppropriateCertificate();
        final String signedEncodedValue = Base64.encodeBase64String(signedCertificate.getEncoded());
        final String signedCertificateFingerPrint = CertificateUtils.getCertificateFingerPrint(signedCertificate);
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement certificateStoreElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        certificateStoreElement.addAttribute(omfac.createOMAttribute("type", (OMNamespace)null, "CertificateStore"));
        if (serverCertificate == null) {
            serverCertificate = SSLCertificateUtil.getCertificate(SSLCertificateUtil.getInstance().getServerCertificateFilePath());
        }
        final String serverCertificateEncodedValue = Base64.encodeBase64String(serverCertificate.getEncoded());
        final String serverCertificateFingerPrint = CertificateUtils.getCertificateFingerPrint(serverCertificate);
        final OMElement rootCertificateStoreElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        rootCertificateStoreElement.addAttribute("type", "Root", (OMNamespace)null);
        final OMElement systemCertificateStoreElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        systemCertificateStoreElement.addAttribute("type", "System", (OMNamespace)null);
        final OMElement rootCertificateFingerPrintElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        rootCertificateFingerPrintElement.addAttribute("type", serverCertificateFingerPrint, (OMNamespace)null);
        final OMElement rootCertificateParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        rootCertificateParamElement.addAttribute("name", "EncodedCertificate", (OMNamespace)null);
        rootCertificateParamElement.addAttribute("value", serverCertificateEncodedValue, (OMNamespace)null);
        rootCertificateFingerPrintElement.addChild((OMNode)rootCertificateParamElement);
        systemCertificateStoreElement.addChild((OMNode)rootCertificateFingerPrintElement);
        rootCertificateStoreElement.addChild((OMNode)systemCertificateStoreElement);
        certificateStoreElement.addChild((OMNode)rootCertificateStoreElement);
        final OMElement myCertificateStoreElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        myCertificateStoreElement.addAttribute("type", "My", (OMNamespace)null);
        final OMElement userCertificateStoreElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        userCertificateStoreElement.addAttribute("type", clientCertStore, (OMNamespace)null);
        final OMElement userCertificateFingerPrintElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        userCertificateFingerPrintElement.addAttribute("type", signedCertificateFingerPrint, (OMNamespace)null);
        final OMElement userCertificateParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        userCertificateParamElement.addAttribute("name", "EncodedCertificate", (OMNamespace)null);
        userCertificateParamElement.addAttribute("value", signedEncodedValue, (OMNamespace)null);
        userCertificateFingerPrintElement.addChild((OMNode)userCertificateParamElement);
        userCertificateStoreElement.addChild((OMNode)userCertificateFingerPrintElement);
        final OMElement privateKeyContainerElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        privateKeyContainerElement.addAttribute("type", "PrivateKeyContainer", (OMNamespace)null);
        final OMElement keySpecParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        keySpecParamElement.addAttribute("name", "KeySpec", (OMNamespace)null);
        keySpecParamElement.addAttribute("value", "2", (OMNamespace)null);
        final OMElement containerParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        containerParamElement.addAttribute("name", "ContainerName", (OMNamespace)null);
        containerParamElement.addAttribute("value", "ConfigMgrEnrollment", (OMNamespace)null);
        final OMElement providerParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        providerParamElement.addAttribute("name", "ProviderType", (OMNamespace)null);
        providerParamElement.addAttribute("value", "1", (OMNamespace)null);
        privateKeyContainerElement.addChild((OMNode)keySpecParamElement);
        privateKeyContainerElement.addChild((OMNode)containerParamElement);
        privateKeyContainerElement.addChild((OMNode)providerParamElement);
        userCertificateStoreElement.addChild((OMNode)privateKeyContainerElement);
        myCertificateStoreElement.addChild((OMNode)userCertificateStoreElement);
        certificateStoreElement.addChild((OMNode)myCertificateStoreElement);
        return certificateStoreElement;
    }
    
    private OMElement getWstepElement(final Boolean isRoboSupported) {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement wstepElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        wstepElement.addAttribute("type", "WSTEP", (OMNamespace)null);
        final OMElement renewElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        renewElement.addAttribute("type", "Renew", (OMNamespace)null);
        final OMElement roboSupportParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        roboSupportParamElement.addAttribute("name", "ROBOSupport", (OMNamespace)null);
        roboSupportParamElement.addAttribute("value", isRoboSupported.toString(), (OMNamespace)null);
        roboSupportParamElement.addAttribute("datatype", "boolean", (OMNamespace)null);
        final OMElement renewPeriodParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        renewPeriodParamElement.addAttribute("name", "RenewPeriod", (OMNamespace)null);
        renewPeriodParamElement.addAttribute("value", "60", (OMNamespace)null);
        renewPeriodParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement retryIntervalParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        retryIntervalParamElement.addAttribute("name", "RetryInterval", (OMNamespace)null);
        retryIntervalParamElement.addAttribute("value", "4", (OMNamespace)null);
        retryIntervalParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        renewElement.addChild((OMNode)roboSupportParamElement);
        renewElement.addChild((OMNode)renewPeriodParamElement);
        renewElement.addChild((OMNode)retryIntervalParamElement);
        wstepElement.addChild((OMNode)renewElement);
        return wstepElement;
    }
    
    private OMElement getApplicationElement(final String clientCommonName, final String nonce, final String sWAPServerURL, final String clientCertStore) throws Exception {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement applicationElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        applicationElement.addAttribute("type", "APPLICATION", (OMNamespace)null);
        final OMElement appIDParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        appIDParamElement.addAttribute("name", "APPID", (OMNamespace)null);
        appIDParamElement.addAttribute("value", "w7", (OMNamespace)null);
        final OMElement providerIDParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        providerIDParamElement.addAttribute("name", "PROVIDER-ID", (OMNamespace)null);
        providerIDParamElement.addAttribute("value", "MEMDM", (OMNamespace)null);
        final OMElement nameParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        nameParamElement.addAttribute("name", "NAME", (OMNamespace)null);
        nameParamElement.addAttribute("value", "MEMDM", (OMNamespace)null);
        final OMElement initParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        initParamElement.addAttribute("name", "INIT", (OMNamespace)null);
        final OMElement addrParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        addrParamElement.addAttribute("name", "ADDR", (OMNamespace)null);
        addrParamElement.addAttribute("value", sWAPServerURL, (OMNamespace)null);
        final OMElement connectRetryParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        connectRetryParamElement.addAttribute("name", "CONNRETRYFREQ", (OMNamespace)null);
        connectRetryParamElement.addAttribute("value", "6", (OMNamespace)null);
        final OMElement initialBackOffParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        initialBackOffParamElement.addAttribute("name", "INITIALBACKOFFTIME", (OMNamespace)null);
        initialBackOffParamElement.addAttribute("value", "30000", (OMNamespace)null);
        final OMElement maxBackOffParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        maxBackOffParamElement.addAttribute("name", "MAXBACKOFFTIME", (OMNamespace)null);
        maxBackOffParamElement.addAttribute("value", "120000", (OMNamespace)null);
        final OMElement backCompAtRetryDisabledParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        backCompAtRetryDisabledParamElement.addAttribute("name", "BACKCOMPATRETRYDISABLED", (OMNamespace)null);
        final OMElement encodingParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        encodingParamElement.addAttribute("name", "DEFAULTENCODING", (OMNamespace)null);
        encodingParamElement.addAttribute("value", "application/vnd.syncml.dm+xml", (OMNamespace)null);
        final OMElement sslClientSearchParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        sslClientSearchParamElement.addAttribute("name", "SSLCLIENTCERTSEARCHCRITERIA", (OMNamespace)null);
        final String issuerNameValue = URLEncoder.encode(clientCommonName.toString().trim(), "utf-8");
        final String storePath = URLEncoder.encode("My\\" + clientCertStore, "utf-8");
        final String sslClientSearchCriValue = "Subject=" + issuerNameValue.trim() + "&Stores=" + storePath;
        sslClientSearchParamElement.addAttribute("value", sslClientSearchCriValue, (OMNamespace)null);
        applicationElement.addChild((OMNode)appIDParamElement);
        applicationElement.addChild((OMNode)providerIDParamElement);
        applicationElement.addChild((OMNode)nameParamElement);
        appIDParamElement.addChild((OMNode)initParamElement);
        applicationElement.addChild((OMNode)addrParamElement);
        applicationElement.addChild((OMNode)connectRetryParamElement);
        applicationElement.addChild((OMNode)initialBackOffParamElement);
        applicationElement.addChild((OMNode)maxBackOffParamElement);
        applicationElement.addChild((OMNode)backCompAtRetryDisabledParamElement);
        applicationElement.addChild((OMNode)encodingParamElement);
        applicationElement.addChild((OMNode)sslClientSearchParamElement);
        final OMElement clientAppAuthElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        clientAppAuthElement.addAttribute("type", "APPAUTH", (OMNamespace)null);
        final OMElement clientAuthLevelParamElelemt = omfac.createOMElement("parm", (OMNamespace)null);
        clientAuthLevelParamElelemt.addAttribute("name", "AAUTHLEVEL", (OMNamespace)null);
        clientAuthLevelParamElelemt.addAttribute("value", "CLIENT", (OMNamespace)null);
        final OMElement clientAuthTypeParamElelemt = omfac.createOMElement("parm", (OMNamespace)null);
        clientAuthTypeParamElelemt.addAttribute("name", "AAUTHTYPE", (OMNamespace)null);
        clientAuthTypeParamElelemt.addAttribute("value", "DIGEST", (OMNamespace)null);
        final OMElement clientAuthSecretParamElelemt = omfac.createOMElement("parm", (OMNamespace)null);
        clientAuthSecretParamElelemt.addAttribute("name", "AAUTHSECRET", (OMNamespace)null);
        final String windowsVendorPass = MDMApiFactoryProvider.getSecureKeyProviderAPI().getSecret("WindowsVendorPass");
        clientAuthSecretParamElelemt.addAttribute("value", windowsVendorPass, (OMNamespace)null);
        final OMElement clientAuthDataParamElelemt = omfac.createOMElement("parm", (OMNamespace)null);
        clientAuthDataParamElelemt.addAttribute("name", "AAUTHDATA", (OMNamespace)null);
        clientAuthDataParamElelemt.addAttribute("value", nonce, (OMNamespace)null);
        clientAppAuthElement.addChild((OMNode)clientAuthLevelParamElelemt);
        clientAppAuthElement.addChild((OMNode)clientAuthTypeParamElelemt);
        clientAppAuthElement.addChild((OMNode)clientAuthSecretParamElelemt);
        clientAppAuthElement.addChild((OMNode)clientAuthDataParamElelemt);
        applicationElement.addChild((OMNode)clientAppAuthElement);
        final OMElement serverAppAuthElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        serverAppAuthElement.addAttribute("type", "APPAUTH", (OMNamespace)null);
        final OMElement serverAuthLevelParamElelemt = omfac.createOMElement("parm", (OMNamespace)null);
        serverAuthLevelParamElelemt.addAttribute("name", "AAUTHLEVEL", (OMNamespace)null);
        serverAuthLevelParamElelemt.addAttribute("value", "APPSRV", (OMNamespace)null);
        final OMElement serverAuthTypeParamElelemt = omfac.createOMElement("parm", (OMNamespace)null);
        serverAuthTypeParamElelemt.addAttribute("name", "AAUTHTYPE", (OMNamespace)null);
        serverAuthTypeParamElelemt.addAttribute("value", "BASIC", (OMNamespace)null);
        final OMElement serverAuthNameParamElelemt = omfac.createOMElement("parm", (OMNamespace)null);
        serverAuthNameParamElelemt.addAttribute("name", "AAUTHNAME", (OMNamespace)null);
        serverAuthNameParamElelemt.addAttribute("value", "testclient", (OMNamespace)null);
        final OMElement serverAuthSecretParamElelemt = omfac.createOMElement("parm", (OMNamespace)null);
        serverAuthSecretParamElelemt.addAttribute("name", "AAUTHSECRET", (OMNamespace)null);
        serverAuthSecretParamElelemt.addAttribute("value", "test", (OMNamespace)null);
        serverAppAuthElement.addChild((OMNode)serverAuthLevelParamElelemt);
        serverAppAuthElement.addChild((OMNode)serverAuthTypeParamElelemt);
        serverAppAuthElement.addChild((OMNode)serverAuthNameParamElelemt);
        serverAppAuthElement.addChild((OMNode)serverAuthSecretParamElelemt);
        applicationElement.addChild((OMNode)serverAppAuthElement);
        return applicationElement;
    }
    
    protected OMElement getRegistryElement() {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement registryElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        registryElement.addAttribute("type", "Registry", (OMNamespace)null);
        final OMElement enrollmentElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        enrollmentElement.addAttribute("type", "HKLM\\Software\\Microsoft\\Enrollment", (OMNamespace)null);
        final OMElement renewalParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        renewalParamElement.addAttribute("name", "RenewalPeriod", (OMNamespace)null);
        renewalParamElement.addAttribute("value", "42", (OMNamespace)null);
        renewalParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        enrollmentElement.addChild((OMNode)renewalParamElement);
        registryElement.addChild((OMNode)enrollmentElement);
        final OMElement omaDMRetryElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        omaDMRetryElement.addAttribute("type", "HKLM\\Software\\Microsoft\\Enrollment\\OmaDmRetry", (OMNamespace)null);
        final OMElement retriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        retriesParamElement.addAttribute("name", "NumRetries", (OMNamespace)null);
        retriesParamElement.addAttribute("value", "8", (OMNamespace)null);
        retriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement retryIntervalParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        retryIntervalParamElement.addAttribute("name", "RetryInterval", (OMNamespace)null);
        retryIntervalParamElement.addAttribute("value", "15", (OMNamespace)null);
        retryIntervalParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement auxNumRetriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        auxNumRetriesParamElement.addAttribute("name", "AuxNumRetries", (OMNamespace)null);
        auxNumRetriesParamElement.addAttribute("value", "5", (OMNamespace)null);
        auxNumRetriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement auxRetryIntervalParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        auxRetryIntervalParamElement.addAttribute("name", "AuxRetryInterval", (OMNamespace)null);
        auxRetryIntervalParamElement.addAttribute("value", "3", (OMNamespace)null);
        auxRetryIntervalParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement aux2NumRetriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        aux2NumRetriesParamElement.addAttribute("name", "Aux2NumRetries", (OMNamespace)null);
        aux2NumRetriesParamElement.addAttribute("value", "0", (OMNamespace)null);
        aux2NumRetriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement aux2RetryIntervalParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        aux2RetryIntervalParamElement.addAttribute("name", "Aux2RetryInterval", (OMNamespace)null);
        aux2RetryIntervalParamElement.addAttribute("value", "60", (OMNamespace)null);
        aux2RetryIntervalParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        omaDMRetryElement.addChild((OMNode)retriesParamElement);
        omaDMRetryElement.addChild((OMNode)retryIntervalParamElement);
        omaDMRetryElement.addChild((OMNode)auxNumRetriesParamElement);
        omaDMRetryElement.addChild((OMNode)auxRetryIntervalParamElement);
        omaDMRetryElement.addChild((OMNode)aux2NumRetriesParamElement);
        omaDMRetryElement.addChild((OMNode)aux2RetryIntervalParamElement);
        registryElement.addChild((OMNode)omaDMRetryElement);
        return registryElement;
    }
    
    private OMElement getDMClientElement() {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement dmclientElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        dmclientElement.addAttribute("type", "DMClient", (OMNamespace)null);
        final OMElement providerElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        providerElement.addAttribute("type", "Provider", (OMNamespace)null);
        final OMElement mdmserverElement = this.getMdmServerElement();
        providerElement.addChild((OMNode)mdmserverElement);
        dmclientElement.addChild((OMNode)providerElement);
        return dmclientElement;
    }
    
    protected OMElement getMdmServerElement() {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement mdmserverElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        mdmserverElement.addAttribute("type", "MEMDM", (OMNamespace)null);
        final OMElement deviceNameParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        deviceNameParamElement.addAttribute("name", "EntDeviceName", (OMNamespace)null);
        deviceNameParamElement.addAttribute("value", "Administrator_WindowsPhone", (OMNamespace)null);
        deviceNameParamElement.addAttribute("datatype", "string", (OMNamespace)null);
        mdmserverElement.addChild((OMNode)deviceNameParamElement);
        return mdmserverElement;
    }
    
    protected OMElement getEnterpriseAppManagementElement(final JSONObject jsonObject, final String clientCommonName) throws Exception {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement enterpriseAppManagementElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        enterpriseAppManagementElement.addAttribute("type", "EnterpriseAppManagement", (OMNamespace)null);
        final OMElement entepriseIDElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        entepriseIDElement.addAttribute("type", String.valueOf(jsonObject.get("EnterpriseID")), (OMNamespace)null);
        final OMElement enterpriseTokenElement = omfac.createOMElement("parm", (OMNamespace)null);
        enterpriseTokenElement.addAttribute("datatype", "string", (OMNamespace)null);
        enterpriseTokenElement.addAttribute("name", "EnrollmentToken", (OMNamespace)null);
        enterpriseTokenElement.addAttribute("value", String.valueOf(jsonObject.get("EnrollmentToken")), (OMNamespace)null);
        final OMElement sslClientSearchParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        sslClientSearchParamElement.addAttribute("datatype", "string", (OMNamespace)null);
        sslClientSearchParamElement.addAttribute("name", "CertificateSearchCriteria", (OMNamespace)null);
        sslClientSearchParamElement.addAttribute("value", clientCommonName.trim(), (OMNamespace)null);
        final OMElement crlCheckElement = omfac.createOMElement("parm", (OMNamespace)null);
        crlCheckElement.addAttribute("name", "CRLCheck", (OMNamespace)null);
        crlCheckElement.addAttribute("value", "0", (OMNamespace)null);
        crlCheckElement.addAttribute("datatype", "string", (OMNamespace)null);
        entepriseIDElement.addChild((OMNode)enterpriseTokenElement);
        entepriseIDElement.addChild((OMNode)sslClientSearchParamElement);
        entepriseIDElement.addChild((OMNode)crlCheckElement);
        if (jsonObject.optString("StoreProductId", (String)null) != null) {
            final String companyHubProductID = "{" + jsonObject.get("StoreProductId") + "}";
            final String companyHubProductURL = String.valueOf(jsonObject.get("StoreURI"));
            final String storeName = String.valueOf(jsonObject.get("StoreName"));
            final OMElement storeProductIDElement = omfac.createOMElement("parm", (OMNamespace)null);
            storeProductIDElement.addAttribute("datatype", "string", (OMNamespace)null);
            storeProductIDElement.addAttribute("name", "StoreProductId", (OMNamespace)null);
            storeProductIDElement.addAttribute("value", companyHubProductID, (OMNamespace)null);
            final OMElement storeProductUriElement = omfac.createOMElement("parm", (OMNamespace)null);
            storeProductUriElement.addAttribute("datatype", "string", (OMNamespace)null);
            storeProductUriElement.addAttribute("name", "StoreURI", (OMNamespace)null);
            storeProductUriElement.addAttribute("value", companyHubProductURL, (OMNamespace)null);
            final OMElement storeNameElement = omfac.createOMElement("parm", (OMNamespace)null);
            storeNameElement.addAttribute("datatype", "string", (OMNamespace)null);
            storeNameElement.addAttribute("name", "StoreName", (OMNamespace)null);
            storeNameElement.addAttribute("value", storeName, (OMNamespace)null);
            entepriseIDElement.addChild((OMNode)storeProductIDElement);
            entepriseIDElement.addChild((OMNode)storeProductUriElement);
            entepriseIDElement.addChild((OMNode)storeNameElement);
        }
        enterpriseAppManagementElement.addChild((OMNode)entepriseIDElement);
        return enterpriseAppManagementElement;
    }
    
    private String constructServerURL(final JSONObject jsonObject) throws JSONException, UnsupportedEncodingException {
        final String customerID = jsonObject.optString("cid", (String)null);
        final String enrollRequestID = jsonObject.optString("erid", (String)null);
        final String managedUserID = jsonObject.optString("muid", (String)null);
        String serialNumber = jsonObject.optString("SerialNumber", (String)null);
        final String baseServerURL = jsonObject.optString("BaseServerURL", (String)null);
        String wpDMServerURL = baseServerURL + "/mdm/client/v1/wpserver" + "?cid=" + customerID + "&erid=" + enrollRequestID + "&muid=" + managedUserID;
        final HashMap requestMap = JSONUtil.getInstance().ConvertToSameDataTypeHash(jsonObject);
        APIKey key = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(requestMap);
        final JSONObject json = new JSONObject();
        json.put("ENROLLMENT_REQUEST_ID", (Object)enrollRequestID);
        key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
        wpDMServerURL = key.appendAsURLParams(wpDMServerURL);
        if (!MDMStringUtils.isEmpty(serialNumber)) {
            serialNumber = URLEncoder.encode(serialNumber, "UTF-8").replaceAll("\\+", "%20");
            wpDMServerURL = wpDMServerURL + "&SerialNumber=" + serialNumber;
        }
        return wpDMServerURL;
    }
}
