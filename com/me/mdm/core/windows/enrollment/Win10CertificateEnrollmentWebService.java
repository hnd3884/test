package com.me.mdm.core.windows.enrollment;

import org.json.JSONObject;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;

public class Win10CertificateEnrollmentWebService extends CertificateEnrollmentWebService
{
    @Override
    protected OMElement getMdmServerElement() {
        final OMFactory omfac = OMAbstractFactory.getOMFactory();
        final OMElement mdmserverElement = super.getMdmServerElement();
        final OMElement pollElement = omfac.createOMElement("characteristic", (OMNamespace)null);
        pollElement.addAttribute("type", "Poll", (OMNamespace)null);
        final OMElement numFirstRetriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        numFirstRetriesParamElement.addAttribute("name", "NumberOfFirstRetries", (OMNamespace)null);
        numFirstRetriesParamElement.addAttribute("value", "8", (OMNamespace)null);
        numFirstRetriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement intervalForFirstRetriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        intervalForFirstRetriesParamElement.addAttribute("name", "IntervalForFirstSetOfRetries", (OMNamespace)null);
        intervalForFirstRetriesParamElement.addAttribute("value", "15", (OMNamespace)null);
        intervalForFirstRetriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement numSecondRetriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        numSecondRetriesParamElement.addAttribute("name", "NumberOfSecondRetries", (OMNamespace)null);
        numSecondRetriesParamElement.addAttribute("value", "5", (OMNamespace)null);
        numSecondRetriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement intervalForSecondRetriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        intervalForSecondRetriesParamElement.addAttribute("name", "IntervalForSecondSetOfRetries", (OMNamespace)null);
        intervalForSecondRetriesParamElement.addAttribute("value", "3", (OMNamespace)null);
        intervalForSecondRetriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement numRemainingScheduledRetriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        numRemainingScheduledRetriesParamElement.addAttribute("name", "NumberOfRemainingScheduledRetries", (OMNamespace)null);
        numRemainingScheduledRetriesParamElement.addAttribute("value", "0", (OMNamespace)null);
        numRemainingScheduledRetriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement intervalForRemainingScheduledRetriesParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        intervalForRemainingScheduledRetriesParamElement.addAttribute("name", "IntervalForRemainingScheduledRetries", (OMNamespace)null);
        intervalForRemainingScheduledRetriesParamElement.addAttribute("value", "1560", (OMNamespace)null);
        intervalForRemainingScheduledRetriesParamElement.addAttribute("datatype", "integer", (OMNamespace)null);
        final OMElement pollOnLoginParamElement = omfac.createOMElement("parm", (OMNamespace)null);
        pollOnLoginParamElement.addAttribute("name", "PollOnLogin", (OMNamespace)null);
        pollOnLoginParamElement.addAttribute("value", "true", (OMNamespace)null);
        pollOnLoginParamElement.addAttribute("datatype", "boolean", (OMNamespace)null);
        final Boolean isPollOnLoginEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowPollLogin");
        pollElement.addChild((OMNode)numFirstRetriesParamElement);
        pollElement.addChild((OMNode)intervalForFirstRetriesParamElement);
        pollElement.addChild((OMNode)numSecondRetriesParamElement);
        pollElement.addChild((OMNode)intervalForSecondRetriesParamElement);
        pollElement.addChild((OMNode)numRemainingScheduledRetriesParamElement);
        pollElement.addChild((OMNode)intervalForRemainingScheduledRetriesParamElement);
        if (isPollOnLoginEnabled) {
            pollElement.addChild((OMNode)pollOnLoginParamElement);
        }
        mdmserverElement.addChild((OMNode)pollElement);
        return mdmserverElement;
    }
    
    @Override
    protected OMElement getRegistryElement() {
        return null;
    }
    
    @Override
    protected OMElement getEnterpriseAppManagementElement(final JSONObject jsonObject, final String clientCommonName) throws Exception {
        return null;
    }
}
