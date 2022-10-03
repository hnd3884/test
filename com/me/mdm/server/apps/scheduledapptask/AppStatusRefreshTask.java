package com.me.mdm.server.apps.scheduledapptask;

import com.me.mdm.server.apps.ios.IOSAppStatusRefreshTask;
import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.me.mdm.server.apps.AppStatusRefreshTaskInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class AppStatusRefreshTask implements CommonQueueProcessorInterface, AppStatusRefreshTaskInterface
{
    private static final Logger LOGGER;
    
    @Override
    public void processData(final CommonQueueData data) {
        try {
            final JSONObject queueJSON = data.getJsonQueueData();
            AppStatusRefreshTask.LOGGER.log(Level.INFO, "Processing AppStatusRefreshTask fro JSON:{0}", new Object[] { queueJSON });
            final Iterator platformIterator = queueJSON.keys();
            while (platformIterator.hasNext()) {
                final Integer platformType = Integer.valueOf(platformIterator.next());
                final JSONObject appGroupResourceJSON = queueJSON.getJSONObject(platformType.toString());
                final AppStatusRefreshTaskInterface appRefreshTask = this.getPlatformObject(platformType);
                appRefreshTask.addAppStatusCommand(appGroupResourceJSON);
            }
        }
        catch (final JSONException e) {
            AppStatusRefreshTask.LOGGER.log(Level.SEVERE, "Exception in Processing AppStatusRefreshTask", (Throwable)e);
        }
    }
    
    private AppStatusRefreshTaskInterface getPlatformObject(final Integer platformType) {
        AppStatusRefreshTaskInterface appRefreshtask = null;
        switch (platformType) {
            case 1: {
                appRefreshtask = new IOSAppStatusRefreshTask();
                break;
            }
            default: {
                appRefreshtask = new AppStatusRefreshTask();
                break;
            }
        }
        return appRefreshtask;
    }
    
    @Override
    public void addAppStatusCommand(final JSONObject appRefreshObject) {
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
