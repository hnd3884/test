package com.me.mdm.server.apps.config.feedback;

import org.json.JSONArray;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONObject;

public class AndroidAppConfigurationFeedbackStatusHandler extends BaseAppConfigurationFeedbackStatusHandler
{
    @Override
    public void parseAndStoreAppConfigFeedback(final String qData, final Long customerID) {
        try {
            final JSONObject feedbackJSON = new JSONObject(qData);
            final Long resourceID = this.getResourceIDFromMessage(feedbackJSON);
            final JSONArray feedBackArray = feedbackJSON.getJSONObject("Message").getJSONArray("Feedbacks");
            for (int i = 0; i < feedBackArray.length(); ++i) {
                final JSONObject individualFeedback = feedBackArray.getJSONObject(i);
                final String identifier = individualFeedback.getString("PackageName");
                final Long appGroupID = AppsUtil.getInstance().getAppGroupIDFromIdentifier(identifier, 2, customerID);
                final String feedbackPath = this.checkAndCreateFeedbackDirectory(resourceID, appGroupID, customerID);
                this.writeFeedBackJSONInFile(feedbackPath, individualFeedback);
                final String feedbackRelativePath = this.getFeedbackRelativePath(resourceID, appGroupID, customerID) + File.separator + "app_config_feedback.json";
                this.addOrUpdateAppConfigurationFeedback(resourceID, appGroupID, feedbackRelativePath);
            }
            this.persistFinalDO();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in parseAndStoreAppConfigFeedback", ex);
        }
    }
}
