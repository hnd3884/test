package com.me.devicemanagement.onpremise.server.search;

import org.json.JSONObject;
import com.me.devicemanagement.framework.server.search.SearchTrackAPI;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.search.AdvSearchUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;

public class METrackerSearchImpl implements MEDMTracker
{
    private Properties searchTrackingTrackerProperties;
    private Logger logger;
    private String sourceClass;
    private static Logger advSearchLogger;
    
    public METrackerSearchImpl() {
        this.searchTrackingTrackerProperties = new Properties();
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "METrackerSerachImpl";
    }
    
    @Override
    public Properties getTrackerProperties() {
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Search Tracking implementation starts...");
        if (!this.searchTrackingTrackerProperties.isEmpty()) {
            this.searchTrackingTrackerProperties = new Properties();
        }
        try {
            final SearchTrackAPI searchTrackAPI = AdvSearchUtil.getSearchTrackAPI();
            if (searchTrackAPI != null) {
                final JSONObject searchJsonDetails = searchTrackAPI.getDataAsJsonObject();
                this.searchTrackingTrackerProperties.setProperty("AdvSearchDetails", searchJsonDetails.toString());
                METrackerSearchImpl.advSearchLogger.log(Level.INFO, "# Search Tracker Data : " + searchJsonDetails);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while fetch Tracking Data", ex);
        }
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Details Summary for Evaluator : " + this.searchTrackingTrackerProperties);
        SyMLogger.info(this.logger, this.sourceClass, "getProperties", "Search Tracking implementation Ends...");
        return this.searchTrackingTrackerProperties;
    }
    
    static {
        METrackerSearchImpl.advSearchLogger = Logger.getLogger("AdvSearchLogger");
    }
}
