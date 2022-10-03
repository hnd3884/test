package com.sun.xml.internal.ws.policy.jaxws.spi;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicySubject;
import java.util.Collection;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.PolicyMap;

public interface PolicyMapConfigurator
{
    Collection<PolicySubject> update(final PolicyMap p0, final SEIModel p1, final WSBinding p2) throws PolicyException;
}
