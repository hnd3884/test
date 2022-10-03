package com.me.webclient.admin;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.metrack.METrackerHandler;
import com.adventnet.sym.server.admin.SoMUtil;
import com.adventnet.sym.server.util.SyMUtil;
import org.json.JSONObject;
import com.me.mdm.server.factory.MDMChatAPI;

public class MDMChatImpl implements MDMChatAPI
{
    public JSONObject getBasicChatInfo(final JSONObject customerInfo) {
        try {
            final Properties properties = SyMUtil.getProductInfo();
            final String mdm = properties.getProperty("mdm");
            final String db = properties.getProperty("db");
            final String adCount = Integer.toString(SoMUtil.getInstance().getADManagedDomainsCount());
            final boolean isMETrackEnabled = METrackerHandler.getMETrackSettings();
            if (isMETrackEnabled) {
                customerInfo.put("DB", (Object)((db != null) ? db : "--"));
                customerInfo.put("CUSTOMER_DETAILS", (Object)((mdm != null) ? mdm : "--"));
                customerInfo.put("AD_COUNT", (Object)adCount);
            }
            else {
                customerInfo.put("DB", (Object)"--");
                customerInfo.put("CUSTOMER_DETAILS", (Object)"--");
                customerInfo.put("AD_COUNT", (Object)"--");
            }
        }
        catch (final Exception e) {
            Logger.getLogger(MDMChatImpl.class.getName()).log(Level.INFO, "Exception while getting customer details : {0}", e);
        }
        return customerInfo;
    }
    
    public String getUserEmail() {
        String emailID = "";
        try {
            final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Properties prop = DMUserHandler.getContactInfoProp(userID);
            if (prop.get("EMAIL_ID") != null) {
                emailID = ((Hashtable<K, String>)prop).get("EMAIL_ID");
                if (emailID.contains("adventnet") || emailID.contains("zohocorp")) {
                    emailID = "";
                }
            }
        }
        catch (final Exception ex) {}
        return emailID;
    }
}
