package com.sun.xml.internal.ws.policy.jaxws.spi;

import com.sun.xml.internal.ws.policy.PolicyException;
import javax.xml.ws.WebServiceFeature;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;

public interface PolicyFeatureConfigurator
{
    Collection<WebServiceFeature> getFeatures(final PolicyMapKey p0, final PolicyMap p1) throws PolicyException;
}
