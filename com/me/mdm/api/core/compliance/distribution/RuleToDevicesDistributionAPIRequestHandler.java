package com.me.mdm.api.core.compliance.distribution;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.compliance.ComplianceFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class RuleToDevicesDistributionAPIRequestHandler extends ApiRequestHandler
{
    public Logger logger;
    private ComplianceFacade compliance;
    
    public RuleToDevicesDistributionAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
        this.compliance = null;
        this.compliance = new ComplianceFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        return apiRequest;
    }
}
