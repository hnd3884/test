package com.me.mdm.onpremise.server.util;

import java.util.Hashtable;
import java.util.Properties;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.me.mdm.server.util.MDMServerDetailsUtil;

public class MDMServerDetailsUtilImpl implements MDMServerDetailsUtil
{
    public JSONObject getServerDetails() {
        final Logger logger = Logger.getLogger(MDMServerDetailsUtilImpl.class.getName());
        try {
            final Properties properties = MDMUtil.getProductProperties();
            final JSONObject serverDetails = new JSONObject();
            serverDetails.put("build_number", ((Hashtable<K, Object>)properties).get("buildnumber"));
            serverDetails.put("product_name", ((Hashtable<K, Object>)properties).get("productname"));
            return serverDetails;
        }
        catch (final JSONException e) {
            logger.log(Level.SEVERE, "exception in getServerDetails", (Throwable)e);
            return null;
        }
    }
}
