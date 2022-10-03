package com.me.ems.onpremise.common.api.v1.service;

import com.me.devicemanagement.onpremise.webclient.dblock.CleanDbLockFiles;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.util.AVTestUtil;
import java.util.Map;
import java.util.logging.Logger;

public class CommonOnpremiseService
{
    Logger logger;
    
    public CommonOnpremiseService() {
        this.logger = Logger.getLogger(CommonOnpremiseService.class.getName());
    }
    
    public Map<String, Integer> getAvTestStatus() throws APIException {
        Integer response;
        try {
            this.logger.info("Inside AVTestAction");
            final int status = AVTestUtil.checkAvTestResultAndSetBanner();
            if (status == 2 || status == 3) {
                response = 1;
            }
            else if (status == 4 || status == 5) {
                response = 2;
            }
            else {
                response = 0;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while running AV test : ", ex);
            response = 2;
        }
        final Map<String, Integer> responseMap = new HashMap<String, Integer>();
        responseMap.put("status", response);
        return responseMap;
    }
    
    public void closeDbLockNotification() throws APIException {
        CleanDbLockFiles.SetNotificationOff();
    }
}
