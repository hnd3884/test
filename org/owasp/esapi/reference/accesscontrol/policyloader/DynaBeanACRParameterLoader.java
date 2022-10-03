package org.owasp.esapi.reference.accesscontrol.policyloader;

import org.apache.commons.configuration.XMLConfiguration;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.reference.accesscontrol.DynaBeanACRParameter;

public final class DynaBeanACRParameterLoader implements ACRParameterLoader<DynaBeanACRParameter>
{
    Logger logger;
    
    public DynaBeanACRParameterLoader() {
        this.logger = ESAPI.getLogger(this.getClass());
    }
    
    @Override
    public DynaBeanACRParameter getParameters(final XMLConfiguration config, final int currentRule) throws Exception {
        final DynaBeanACRParameter policyParameter = new DynaBeanACRParameter();
        final int numberOfParameters = config.getList("AccessControlRules.AccessControlRule(" + currentRule + ").Parameters.Parameter[@name]").size();
        for (int currentParameter = 0; currentParameter < numberOfParameters; ++currentParameter) {
            final String parameterName = config.getString("AccessControlRules.AccessControlRule(" + currentRule + ").Parameters.Parameter(" + currentParameter + ")[@name]");
            final String parameterType = config.getString("AccessControlRules.AccessControlRule(" + currentRule + ").Parameters.Parameter(" + currentParameter + ")[@type]");
            final Object parameterValue = ACRParameterLoaderHelper.getParameterValue(config, currentRule, currentParameter, parameterType);
            policyParameter.set(parameterName, parameterValue);
        }
        policyParameter.lock();
        this.logger.info(Logger.SECURITY_SUCCESS, "Loaded " + numberOfParameters + " parameters: " + policyParameter.toString());
        return policyParameter;
    }
}
