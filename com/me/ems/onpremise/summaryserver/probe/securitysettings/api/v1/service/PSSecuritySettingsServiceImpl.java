package com.me.ems.onpremise.summaryserver.probe.securitysettings.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.Map;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.onpremise.summaryserver.summary.securitysettings.api.core.SSSecuritySettingsService;
import com.me.ems.framework.securitysettings.api.v1.service.SecuritySettingsServiceImpl;

public class PSSecuritySettingsServiceImpl extends SecuritySettingsServiceImpl implements SSSecuritySettingsService
{
    public long getProbeSecurityConfigPercentage() {
        Long result = 0L;
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        try {
            final Map securitySettings = super.getSecuritySettingsDetails(customerId);
            result = securitySettings.get("securePercentage");
        }
        catch (final Exception e) {
            PSSecuritySettingsServiceImpl.logger.log(Level.SEVERE, "Exception while getting secure settings details", e);
        }
        return result;
    }
    
    public Map saveSecuritySettings(final Map saveSecuritySettingsDetails, final User user, final Long customerID, final HttpServletRequest httpServletRequest) throws APIException {
        final String isSSRequest = (String)httpServletRequest.getAttribute("summaryServerRequest");
        if (isSSRequest != null && isSSRequest.equalsIgnoreCase("true")) {
            if (saveSecuritySettingsDetails.containsKey("isShareLocked")) {
                final Map result = new HashMap();
                result.put("statusCode", true);
                return result;
            }
        }
        else if (!saveSecuritySettingsDetails.containsKey("isShareLocked") || !saveSecuritySettingsDetails.containsKey("isClientCertAuthEnabled")) {
            PSSecuritySettingsServiceImpl.logger.log(Level.SEVERE, "parameters found which not allowed for PS while saving security settings");
            final String keys = (String)saveSecuritySettingsDetails.entrySet().stream().map(entry -> ((Map.Entry)entry).getKey()).collect(Collectors.joining(", "));
            throw new APIException(Response.Status.PRECONDITION_FAILED, "IAM0006", "dc.rest.api_params_not_applicable_for_ps", new String[] { keys });
        }
        return super.saveSecuritySettings(saveSecuritySettingsDetails, user, customerID, httpServletRequest);
    }
}
