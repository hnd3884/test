package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.xml.internal.ws.resources.ManagementMessages;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.istack.internal.logging.Logger;
import javax.xml.namespace.QName;

public class ManagedClientAssertion extends ManagementAssertion
{
    public static final QName MANAGED_CLIENT_QNAME;
    private static final Logger LOGGER;
    
    public static ManagedClientAssertion getAssertion(final WSPortInfo portInfo) throws WebServiceException {
        if (portInfo == null) {
            return null;
        }
        ManagedClientAssertion.LOGGER.entering(portInfo);
        final PolicyMap policyMap = portInfo.getPolicyMap();
        final ManagedClientAssertion assertion = ManagementAssertion.getAssertion(ManagedClientAssertion.MANAGED_CLIENT_QNAME, policyMap, portInfo.getServiceName(), portInfo.getPortName(), ManagedClientAssertion.class);
        ManagedClientAssertion.LOGGER.exiting(assertion);
        return assertion;
    }
    
    public ManagedClientAssertion(final AssertionData data, final Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
        super(ManagedClientAssertion.MANAGED_CLIENT_QNAME, data, assertionParameters);
    }
    
    @Override
    public boolean isManagementEnabled() {
        final String management = this.getAttributeValue(ManagedClientAssertion.MANAGEMENT_ATTRIBUTE_QNAME);
        if (management != null && (management.trim().toLowerCase().equals("on") || Boolean.parseBoolean(management))) {
            ManagedClientAssertion.LOGGER.warning(ManagementMessages.WSM_1006_CLIENT_MANAGEMENT_ENABLED());
        }
        return false;
    }
    
    static {
        MANAGED_CLIENT_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedClient");
        LOGGER = Logger.getLogger(ManagedClientAssertion.class);
    }
}
