package com.me.ems.framework.security.breachnotification.api.v1.service;

import com.me.ems.framework.security.breachnotification.core.BreachNotificationAPI;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.security.breachnotification.core.BreachNotificationCoreUtil;
import java.util.Map;
import java.util.logging.Logger;

public class BreachNotificationService
{
    private static Logger logger;
    private static BreachNotificationService instance;
    
    public static BreachNotificationService getInstance() {
        if (BreachNotificationService.instance == null) {
            BreachNotificationService.instance = new BreachNotificationService();
        }
        return BreachNotificationService.instance;
    }
    
    public Map getNotificationDetails() throws APIException {
        try {
            final Map resultMap = BreachNotificationCoreUtil.getInstance().getUserInfo();
            return resultMap;
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception exp) {
            BreachNotificationService.logger.log(Level.SEVERE, "Exception occurred in BreachNotificationService", exp);
            throw new APIException("GENERIC0002", exp.getMessage(), new String[0]);
        }
    }
    
    public Map setNotificationDetails(final Map notificationDetails) throws APIException {
        final Map resultMap = new HashMap();
        try {
            ArrayList emailArray = notificationDetails.get("emailAddress");
            final LinkedHashSet<String> hashEmailSet = new LinkedHashSet<String>(emailArray);
            emailArray = new ArrayList((Collection<? extends E>)hashEmailSet);
            final boolean sendNotification = notificationDetails.get("sendNotification");
            if (sendNotification && emailArray.isEmpty()) {
                throw new APIException("GENERIC0002", "Email Id not present", new String[0]);
            }
            final boolean postStatus = BreachNotificationCoreUtil.getInstance().updateUserDetails(emailArray, sendNotification);
            if (!postStatus) {
                String formURL = "";
                final BreachNotificationAPI breachNotificationAPI = ApiFactoryProvider.getBreachNotificationAPI();
                if (breachNotificationAPI != null) {
                    formURL = breachNotificationAPI.getFormURL();
                }
                throw new APIException("GENERIC0010", true, formURL);
            }
            resultMap.put("notificationMessage", "Details Updated");
            return resultMap;
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception exp) {
            BreachNotificationService.logger.log(Level.SEVERE, "Exception occurred in BreachNotificationService", exp);
            throw new APIException("GENERIC0002", exp.getMessage(), new String[0]);
        }
    }
    
    static {
        BreachNotificationService.logger = Logger.getLogger("SecurityLogger");
        BreachNotificationService.instance = null;
    }
}
