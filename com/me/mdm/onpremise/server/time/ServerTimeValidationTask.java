package com.me.mdm.onpremise.server.time;

import org.json.JSONObject;
import org.json.JSONException;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ServerTimeValidationTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    public void executeTask(final Properties props) {
        final JSONObject timeInfo = ServerTimeValidationUtil.getTimeDifferenceInfo();
        if (timeInfo == null) {
            ServerTimeValidationTask.LOGGER.warning("Error while getting InternetTime JSON");
            ServerTimeValidationUtil.setNTPFetchError();
            return;
        }
        try {
            ServerTimeValidationTask.LOGGER.info("Validating Server Time: " + timeInfo);
            if (timeInfo.getBoolean("sync_needed")) {
                ServerTimeValidationTask.LOGGER.warning("Time Mismatch in Server");
                ServerTimeValidationUtil.showMessage(timeInfo);
            }
            else {
                ServerTimeValidationUtil.hideMessage();
            }
        }
        catch (final JSONException e) {
            ServerTimeValidationTask.LOGGER.warning("Exception while executing server time validation task" + e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ServerTimeValidationTask.class.getName());
    }
}
