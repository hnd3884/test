package com.me.ems.onpremise.support.api.v1.service;

import java.util.logging.Level;
import java.util.List;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;
import java.util.logging.Logger;
import com.me.ems.onpremise.support.utils.SupportTabUtils;

public class SupportTabService
{
    private SupportTabUtils supportTabUtils;
    private static Logger logger;
    
    public SupportTabService() {
        this.supportTabUtils = new SupportTabUtils();
    }
    
    public Map getSupportPageDetails() throws APIException {
        return this.supportTabUtils.getSupportTabDetails();
    }
    
    public List getBuildHistoryDetails() throws APIException {
        try {
            return this.supportTabUtils.getBuildHistoryDetails();
        }
        catch (final Exception e) {
            SupportTabService.logger.log(Level.WARNING, "Exception while getting getBuildHistoryDetails tab info ", e);
            throw new APIException("GENERIC0005");
        }
    }
    
    static {
        SupportTabService.logger = Logger.getLogger(SupportTabUtils.class.getName());
    }
}
