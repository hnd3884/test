package com.me.mdm.core.windows.enrollment;

import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPEnvelope;
import java.util.logging.Level;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAbstractFactory;
import org.json.JSONObject;
import org.apache.axiom.soap.SOAPMessage;
import java.util.logging.Logger;

public class CertificateEnrollmentPolicyWebService
{
    Logger logger;
    
    public CertificateEnrollmentPolicyWebService() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public String processRequest(final SOAPMessage soapMessage, final JSONObject jsonObject) {
        SOAPEnvelope soapEnvelope = null;
        try {
            final String relatesMessageID = String.valueOf(jsonObject.get("MessageID"));
            final OMNamespace soapEnvelopeNameSpace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.w3.org/2003/05/soap-envelope", "s");
            soapEnvelope = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPEnvelope(soapEnvelopeNameSpace);
            final SOAPHeader soapHeader = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPHeader(soapEnvelope);
            final OMNamespace soapAddressNameSpace = OMAbstractFactory.getOMFactory().createOMNamespace("http://www.w3.org/2005/08/addressing", "a");
            soapEnvelope.declareNamespace(soapAddressNameSpace);
            final SOAPHeaderBlock omActionElement = soapHeader.addHeaderBlock("Action", soapAddressNameSpace);
            omActionElement.addAttribute("mustUnderstand", "1", soapEnvelopeNameSpace);
            omActionElement.setText("http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy/IPolicy/GetPoliciesResponse");
            soapHeader.addChild((OMNode)omActionElement);
            final SOAPHeaderBlock relatesToElement = soapHeader.addHeaderBlock("RelatesTo", soapAddressNameSpace);
            relatesToElement.setText(relatesMessageID);
            soapHeader.addChild((OMNode)relatesToElement);
            final OMFactory fac = OMAbstractFactory.getOMFactory();
            final OMNamespace enrollmentPolicyNs = fac.createOMNamespace("http://schemas.microsoft.com/windows/pki/2009/01/enrollmentpolicy", "");
            final OMNamespace schemaNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema", "xsd");
            final OMNamespace schemaInstanceNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");
            OMElement getPoliciesResponseElement = fac.createOMElement("GetPoliciesResponse", enrollmentPolicyNs);
            getPoliciesResponseElement.declareNamespace(schemaNs);
            getPoliciesResponseElement.declareNamespace(schemaInstanceNs);
            final OMElement responseElement = fac.createOMElement("response", (OMNamespace)null, (OMContainer)getPoliciesResponseElement);
            final OMElement policyFriendlyNameElement = fac.createOMElement("policyFriendlyName", (OMNamespace)null, (OMContainer)responseElement);
            policyFriendlyNameElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement nextUpdateHoursElement = fac.createOMElement("nextUpdateHours", (OMNamespace)null, (OMContainer)responseElement);
            nextUpdateHoursElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement policiesNotChangedElement = fac.createOMElement("policiesNotChanged", (OMNamespace)null, (OMContainer)responseElement);
            policiesNotChangedElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement policiesElement = fac.createOMElement("policies", (OMNamespace)null, (OMContainer)responseElement);
            final OMElement policyElement = fac.createOMElement("policy", (OMNamespace)null, (OMContainer)policiesElement);
            final OMElement policyOIDElement = fac.createOMElement("policyOIDReference", (OMNamespace)null, (OMContainer)policyElement);
            policyOIDElement.addChild((OMNode)fac.createOMText((OMContainer)policyOIDElement, "0"));
            final OMElement cASElement = fac.createOMElement("cAS", (OMNamespace)null, (OMContainer)policyElement);
            cASElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement attributeElement = fac.createOMElement("attributes", (OMNamespace)null, (OMContainer)policyElement);
            final OMElement policySchemaElement = fac.createOMElement("policySchema", (OMNamespace)null, (OMContainer)attributeElement);
            policySchemaElement.addChild((OMNode)fac.createOMText((OMContainer)policySchemaElement, "3"));
            final OMElement privateKeyElement = fac.createOMElement("privateKeyAttributes", (OMNamespace)null, (OMContainer)attributeElement);
            final OMElement minimalKeyLengthElement = fac.createOMElement("minimalKeyLength", (OMNamespace)null, (OMContainer)privateKeyElement);
            minimalKeyLengthElement.addChild((OMNode)fac.createOMText((OMContainer)minimalKeyLengthElement, "2048"));
            final OMElement keySpecElement = fac.createOMElement("keySpec", (OMNamespace)null, (OMContainer)privateKeyElement);
            keySpecElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement keyUsageElement = fac.createOMElement("keyUsageProperty", (OMNamespace)null, (OMContainer)privateKeyElement);
            keyUsageElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement permissionsElement = fac.createOMElement("permissions", (OMNamespace)null, (OMContainer)privateKeyElement);
            permissionsElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement algorithmOIDReferenceElement = fac.createOMElement("algorithmOIDReference", (OMNamespace)null, (OMContainer)privateKeyElement);
            algorithmOIDReferenceElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement cryptoProvidersElement = fac.createOMElement("cryptoProviders", (OMNamespace)null, (OMContainer)privateKeyElement);
            cryptoProvidersElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement supersededPoliciesElement = fac.createOMElement("supersededPolicies", (OMNamespace)null, (OMContainer)attributeElement);
            supersededPoliciesElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement privateKeyFlagsElement = fac.createOMElement("privateKeyFlags", (OMNamespace)null, (OMContainer)attributeElement);
            privateKeyFlagsElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement subjectNameFlagsElement = fac.createOMElement("subjectNameFlags", (OMNamespace)null, (OMContainer)attributeElement);
            subjectNameFlagsElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement enrollmentFlagsElement = fac.createOMElement("enrollmentFlags", (OMNamespace)null, (OMContainer)attributeElement);
            enrollmentFlagsElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement generalFlagsElement = fac.createOMElement("generalFlags", (OMNamespace)null, (OMContainer)attributeElement);
            generalFlagsElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement hashAlgorithmOIDReferenceElement = fac.createOMElement("hashAlgorithmOIDReference", (OMNamespace)null, (OMContainer)attributeElement);
            hashAlgorithmOIDReferenceElement.addChild((OMNode)fac.createOMText((OMContainer)policyOIDElement, "0"));
            final OMElement rARequirementsElement = fac.createOMElement("rARequirements", (OMNamespace)null, (OMContainer)attributeElement);
            rARequirementsElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement keyArchivalAttributesElement = fac.createOMElement("keyArchivalAttributes", (OMNamespace)null, (OMContainer)attributeElement);
            keyArchivalAttributesElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement extensionElement = fac.createOMElement("extensions", (OMNamespace)null, (OMContainer)attributeElement);
            extensionElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement cASSubRootElement = fac.createOMElement("cAS", (OMNamespace)null, (OMContainer)getPoliciesResponseElement);
            cASSubRootElement.addAttribute(fac.createOMAttribute("nil", schemaInstanceNs, "true"));
            final OMElement oIDSElement = fac.createOMElement("oIDs", (OMNamespace)null, (OMContainer)getPoliciesResponseElement);
            final OMElement oIDElement = fac.createOMElement("oID", (OMNamespace)null, (OMContainer)oIDSElement);
            final OMElement valueElement = fac.createOMElement("value", (OMNamespace)null, (OMContainer)oIDElement);
            valueElement.addChild((OMNode)fac.createOMText((OMContainer)valueElement, "1.3.14.3.2.29"));
            final OMElement groupElement = fac.createOMElement("group", (OMNamespace)null, (OMContainer)oIDElement);
            groupElement.addChild((OMNode)fac.createOMText((OMContainer)groupElement, "1"));
            final OMElement oIDReferenceIDElement = fac.createOMElement("oIDReferenceID", (OMNamespace)null, (OMContainer)oIDElement);
            oIDReferenceIDElement.addChild((OMNode)fac.createOMText((OMContainer)oIDReferenceIDElement, "0"));
            final OMElement defaultNameElement = fac.createOMElement("defaultName", (OMNamespace)null, (OMContainer)oIDElement);
            defaultNameElement.addChild((OMNode)fac.createOMText((OMContainer)defaultNameElement, "szOID_OIWSEC_sha1RSASign"));
            String finalData = getPoliciesResponseElement.toString();
            finalData = finalData.replaceAll(" xmlns=\"\"", "");
            getPoliciesResponseElement = AXIOMUtil.stringToOM(finalData);
            final SOAPBody soapBody = OMAbstractFactory.getMetaFactory().getSOAP12Factory().createSOAPBody(soapEnvelope);
            soapBody.addChild((OMNode)getPoliciesResponseElement);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during processRequestForDEPToken of CertificateEnrollmentPolicyWebService \n", exp);
        }
        return soapEnvelope.toString();
    }
}
