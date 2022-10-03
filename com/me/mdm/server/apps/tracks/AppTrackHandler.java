package com.me.mdm.server.apps.tracks;

import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.adventnet.i18n.I18N;
import java.util.List;
import java.util.logging.Logger;

public class AppTrackHandler
{
    private Logger logger;
    
    public AppTrackHandler() {
        this.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
    
    public void invokeOperation(final AppTrackEvent appTrackEvent, final int operation) {
        switch (operation) {
            case 1: {
                this.handleAppTrackAdded(appTrackEvent);
                break;
            }
            case 2: {
                this.handleAppTrackDeleted(appTrackEvent);
                break;
            }
            case 3: {
                this.handleAppTrackModified(appTrackEvent);
                break;
            }
        }
    }
    
    private void handleAppTrackDeleted(final AppTrackEvent appTrackEvent) {
        new AppTrackUtil().deprecateAppTracks(appTrackEvent.trackIds, appTrackEvent.appGroupId);
        final String sEventLogRemarks = "mdm.app.delete_app_track";
        try {
            for (final String trackId : appTrackEvent.trackIds) {
                final JSONObject appTrackJSON = new AppTrackUtil().getBasicTrackDetails(appTrackEvent.appGroupId, trackId);
                final String eventMsg = I18N.getMsg(sEventLogRemarks, new Object[] { appTrackJSON.optString("RELEASE_LABEL_DISPLAY_NAME", appTrackEvent.trackName), appTrackJSON.optString("PROFILE_NAME", appTrackEvent.appName) });
                MDMEventLogHandler.getInstance().addEvent(72516, "", eventMsg, Arrays.asList(""), appTrackEvent.customerId, System.currentTimeMillis());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Cannot log track deleted event due to {0}", e);
        }
    }
    
    private void handleAppTrackAdded(final AppTrackEvent appTrackEvent) {
        final String sEventLogRemarks = "mdm.app.new_app_track";
        try {
            final String eventMsg = I18N.getMsg(sEventLogRemarks, new Object[] { appTrackEvent.trackName, appTrackEvent.appName });
            MDMEventLogHandler.getInstance().addEvent(72514, "", eventMsg, Arrays.asList(""), appTrackEvent.customerId, System.currentTimeMillis());
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Cannot log track added event due to {0}", e);
        }
    }
    
    private void handleAppTrackModified(final AppTrackEvent appTrackEvent) {
        final String sEventLogRemarks = "mdm.app.update_app_track";
        final JSONObject appTrackJSON = new AppTrackUtil().getBasicTrackDetails(appTrackEvent.appGroupId, appTrackEvent.trackId);
        new AppTrackUtil().resumeAppTracks(appTrackEvent.trackId, appTrackEvent.appGroupId);
        try {
            final String eventMsg = I18N.getMsg(sEventLogRemarks, new Object[] { appTrackJSON.optString("RELEASE_LABEL_DISPLAY_NAME", appTrackEvent.trackName), appTrackJSON.optString("PROFILE_NAME", appTrackEvent.appName), AppsUtil.getValidVersion(appTrackEvent.version) });
            MDMEventLogHandler.getInstance().addEvent(72515, "", eventMsg, Arrays.asList(""), appTrackEvent.customerId, System.currentTimeMillis());
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Cannot log track updated event due to {0}", e);
        }
    }
}
