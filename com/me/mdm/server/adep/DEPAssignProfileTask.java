package com.me.mdm.server.adep;

import java.util.Hashtable;
import java.util.Properties;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class DEPAssignProfileTask implements CommonQueueProcessorInterface, SchedulerExecutionInterface
{
    public static Logger logger;
    
    @Override
    public void processData(final CommonQueueData data) {
        try {
            DEPAssignProfileTask.logger.log(Level.INFO, "DEPAssignProfileTask is being executed");
            final int a = 1;
            final JSONObject queueData = data.getJsonQueueData();
            final Long custoemrId = data.getCustomerId();
            final Long depTokenId = queueData.getLong("DEP_TOKEN_ID");
            final JSONObject profileJSON = AppleDEPProfileHandler.getInstance(depTokenId, custoemrId).getDEPProfileDetails();
            if (profileJSON != null) {
                String userId = (String)queueData.opt("USER_ID");
                if (userId == null) {
                    final Long userIdLong = (Long)profileJSON.get("ADDED_USER");
                    if (userIdLong != null) {
                        userId = String.valueOf(userIdLong);
                    }
                    else {
                        DEPAssignProfileTask.logger.log(Level.SEVERE, "User ID obtained NULL from DB in DEPAssignProfileTask");
                    }
                }
                profileJSON.put("ADDED_USER", Long.parseLong(userId));
                AppleDEPProfileHandler.getInstance(depTokenId, custoemrId).createProfile(profileJSON);
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(DEPAssignProfileTask.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger(DEPAssignProfileTask.class.getName()).log(Level.SEVERE, null, ex2);
        }
    }
    
    public void executeTask(final Properties props) {
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)props).get("jsonParams")));
            tempData.setCustomerId(((Hashtable<K, Long>)props).get("customerId"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            DEPAssignProfileTask.logger.log(Level.SEVERE, "Cannot fetch JSON from Props", (Throwable)exp);
        }
    }
    
    static {
        DEPAssignProfileTask.logger = Logger.getLogger("MDMEnrollment");
    }
}
