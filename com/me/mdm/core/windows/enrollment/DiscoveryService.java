package com.me.mdm.core.windows.enrollment;

import org.apache.axiom.soap.SOAPBody;
import java.util.Iterator;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAbstractFactory;
import com.me.mdm.core.enrollment.DeviceForEnrollmentHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import org.apache.axiom.soap.SOAPMessage;
import java.util.logging.Logger;

public class DiscoveryService
{
    Logger logger;
    
    public DiscoveryService() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public String processRequest(final SOAPMessage soapMessage, final JSONObject jsonObject) {
        SOAPEnvelope soapEnvelope = null;
        try {
            String discoveryServerURL = String.valueOf(jsonObject.get("requestURL"));
            final String pathPrefix = String.valueOf(jsonObject.get("pathPrefix"));
            String emailAddress = String.valueOf(jsonObject.get("EmailAddress"));
            this.logger.log(Level.INFO, "DiscoveryService :: processRequestForDEPToken :: discoveryServerURL : discovery url generated");
            String customerID = null;
            String authTokens = null;
            if (discoveryServerURL.contains("?")) {
                final String discoveryServerURLLocal = discoveryServerURL.split("\\?")[0];
                authTokens = discoveryServerURL.split("\\?")[1];
                discoveryServerURL = discoveryServerURLLocal;
            }
            if (authTokens == null || discoveryServerURL.contains("admin")) {
                final int lastIndex = discoveryServerURL.lastIndexOf(47);
                customerID = discoveryServerURL.substring(lastIndex + 1, discoveryServerURL.length());
            }
            final String baseServerURL = String.valueOf(jsonObject.get("BaseServerURL"));
            final String messageID = String.valueOf(jsonObject.get("MessageID"));
            emailAddress = SyMUtil.getInstance().encodeURIComponentEquivalent(emailAddress);
            final String servletPath = String.valueOf(jsonObject.get("ServletPath"));
            final String dcResID = MDMStringUtils.getValueFromParams(authTokens, "genericID");
            Boolean isDCEnroll = Boolean.FALSE;
            if (dcResID != null) {
                final Long erid = new DeviceForEnrollmentHandler().getERIDFromGenericID(dcResID, Long.parseLong(customerID));
                if (erid != null) {
                    authTokens = MDMStringUtils.removeValueFromUrlParams(authTokens, "genericID");
                    authTokens = MDMStringUtils.removeValueFromUrlParams(authTokens, "templateToken");
                    authTokens = ((authTokens == null) ? "" : (authTokens + "&")) + "erid=" + erid;
                    isDCEnroll = Boolean.TRUE;
                }
            }
            String enrollmentPolicyServerURL = baseServerURL + (servletPath.contains("/v2/wpdiscover/") ? "/mdm/client/v2/wpcheckin" : "/mdm/client/v1/wpcheckin") + (isDCEnroll ? "" : pathPrefix) + (pathPrefix.contains("admin") ? ("?cid=" + customerID) : "?");
            if (authTokens == null) {
                enrollmentPolicyServerURL = enrollmentPolicyServerURL + "email=" + emailAddress + "&cid=" + customerID;
            }
            if (authTokens != null) {
                enrollmentPolicyServerURL += (enrollmentPolicyServerURL.endsWith("?") ? "" : "&");
                enrollmentPolicyServerURL += authTokens;
            }
            this.logger.log(Level.INFO, "DiscoveryService :: processRequestForDEPToken :: enrollmentPolicyServerURL : discovery url generated");
            final OMNamespace soapEnvelopeNameSpace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.w3.org/2003/05/soap-envelope", "s");
            soapEnvelope = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPEnvelope(soapEnvelopeNameSpace);
            final SOAPHeader soapHeader = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPHeader(soapEnvelope);
            final OMNamespace soapAddressNameSpace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.w3.org/2005/08/addressing", "a");
            soapEnvelope.declareNamespace(soapAddressNameSpace);
            final OMNamespace soapSchemaInstanceNameSpace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
            soapEnvelope.declareNamespace(soapSchemaInstanceNameSpace);
            final SOAPHeaderBlock omActionElement = soapHeader.addHeaderBlock("Action", soapAddressNameSpace);
            omActionElement.addAttribute("mustUnderstand", "1", soapEnvelopeNameSpace);
            omActionElement.setText("http://schemas.microsoft.com/windows/management/2012/01/enrollment/IDiscoveryService/DiscoverResponse");
            soapHeader.addChild((OMNode)omActionElement);
            final SOAPHeaderBlock relatesToElement = soapHeader.addHeaderBlock("RelatesTo", soapAddressNameSpace);
            relatesToElement.setText(messageID);
            soapHeader.addChild((OMNode)relatesToElement);
            final OMFactory fac = OMAbstractFactory.getOMFactory();
            final OMNamespace omNs = fac.createOMNamespace("http://schemas.microsoft.com/windows/management/2012/01/enrollment", "");
            OMElement discoveryResponseElement = fac.createOMElement("DiscoverResponse", omNs);
            final OMElement discoveryResultElement = fac.createOMElement("DiscoverResult", (OMNamespace)null, (OMContainer)discoveryResponseElement);
            discoveryResultElement.declareNamespace("urn:a", "");
            final OMElement authPolicyElement = fac.createOMElement("AuthPolicy", (OMNamespace)null);
            final OMElement authUrlElement = fac.createOMElement("AuthUrl", (OMNamespace)null);
            final OMElement enrollmentPolicyUrlElement = fac.createOMElement("EnrollmentPolicyServiceUrl", (OMNamespace)null);
            final OMElement enrollmentServiceUrlElement = fac.createOMElement("EnrollmentServiceUrl", (OMNamespace)null);
            final OMElement federatedServiceNameElement = fac.createOMElement("FederatedServiceName", (OMNamespace)null);
            final OMElement federatedServicePolicyElement = fac.createOMElement("FederatedServicePolicy", (OMNamespace)null);
            discoveryResultElement.addChild((OMNode)authPolicyElement);
            authPolicyElement.addChild((OMNode)fac.createOMText((OMContainer)authPolicyElement, "OnPremise"));
            enrollmentPolicyUrlElement.addChild((OMNode)fac.createOMText((OMContainer)enrollmentPolicyUrlElement, enrollmentPolicyServerURL));
            enrollmentServiceUrlElement.addChild((OMNode)fac.createOMText((OMContainer)enrollmentServiceUrlElement, enrollmentPolicyServerURL));
            discoveryResultElement.addChild((OMNode)authUrlElement);
            discoveryResultElement.addChild((OMNode)enrollmentPolicyUrlElement);
            discoveryResultElement.addChild((OMNode)enrollmentServiceUrlElement);
            discoveryResultElement.addChild((OMNode)federatedServiceNameElement);
            discoveryResultElement.addChild((OMNode)federatedServicePolicyElement);
            discoveryResponseElement.addChild((OMNode)discoveryResultElement);
            final Iterator elementIterator = discoveryResponseElement.getChildElements();
            while (elementIterator.hasNext()) {
                final OMElement childElement = elementIterator.next();
                final Iterator namespaceIterator = childElement.getAllDeclaredNamespaces();
                while (namespaceIterator.hasNext()) {
                    final OMNamespace namespace = namespaceIterator.next();
                    namespaceIterator.remove();
                }
            }
            String finalData = discoveryResponseElement.toString();
            finalData = finalData.replaceAll(" xmlns=\"\"", "");
            discoveryResponseElement = AXIOMUtil.stringToOM(finalData);
            final SOAPBody soapBody = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPBody(soapEnvelope);
            soapBody.addChild((OMNode)discoveryResponseElement);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred in Discovery Service \n", exp);
        }
        return soapEnvelope.toString();
    }
}
