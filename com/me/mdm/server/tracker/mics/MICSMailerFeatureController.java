package com.me.mdm.server.tracker.mics;

import java.util.Iterator;
import java.util.logging.Level;
import org.json.JSONArray;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.logging.Logger;

interface MICSMailerFeatureController
{
    public static final Logger LOGGER = Logger.getLogger(MICSMailerFeatureController.class.getName());
    public static final String FEATURE = "feature";
    public static final String MICS_PARAM_NAME = "paramname";
    public static final String MICS_PARAM_VALUE = "paramvalue";
    public static final String MICS_PARAMS = "params";
    public static final String MICS_FEATURE_NAME = "feature_name";
    public static final String MICS_FEATURE_URL = "feature_url";
    
    JSONObject getTrackingJSON(final ArrayList<MICSMailerAPI.MICSMailerSubFeature> p0);
    
    default void addTrackingData(final JSONObject jsonObject) {
        try {
            if (!MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("mics.mailer.enabled")) {
                return;
            }
            final JSONObject micsObject = new JSONObject();
            final JSONArray paramsArray = new JSONArray();
            final MICSMailerAPI micsMailerAPI = MDMApiFactoryProvider.getMicsMailerAPI();
            if (micsMailerAPI.isFeatureExcluded(jsonObject)) {
                return;
            }
            for (final String key : jsonObject.keySet()) {
                if (key.equalsIgnoreCase("feature")) {
                    continue;
                }
                final JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("paramname", (Object)key);
                jsonObject2.put("paramvalue", jsonObject.get(key));
                paramsArray.put((Object)jsonObject2);
            }
            micsObject.put("params", (Object)paramsArray);
            micsObject.put("feature_name", jsonObject.get("feature"));
            micsObject.put("feature_url", jsonObject.get("feature"));
            micsMailerAPI.postDataToMicsForMailer(micsObject);
        }
        catch (final Exception e) {
            MICSMailerFeatureController.LOGGER.log(Level.SEVERE, "MICS Tracker Exception -  Exception while adding data ");
            throw e;
        }
    }
}
