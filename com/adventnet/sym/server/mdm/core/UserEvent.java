package com.adventnet.sym.server.mdm.core;

import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserEvent
{
    public Long resourceID;
    public Long customerID;
    public Long userID;
    public JSONObject additionalDetails;
    
    public UserEvent(final Long resourceID) {
        this.resourceID = null;
        this.customerID = null;
        this.userID = null;
        this.additionalDetails = new JSONObject();
        this.resourceID = resourceID;
    }
    
    public UserEvent(final Long resourceID, final Long customerID) {
        this.resourceID = null;
        this.customerID = null;
        this.userID = null;
        this.additionalDetails = new JSONObject();
        this.resourceID = resourceID;
        this.customerID = customerID;
    }
    
    public UserEvent(final Long resourceID, final Long customerID, final Long userID) {
        this.resourceID = null;
        this.customerID = null;
        this.userID = null;
        this.additionalDetails = new JSONObject();
        this.resourceID = resourceID;
        this.customerID = customerID;
        this.userID = userID;
    }
    
    public UserEvent(final Long resourceID, final Long customerID, final Long userID, final Boolean isUserNameModified, final Boolean isEmailAddressModified) {
        this(resourceID, customerID, userID);
        try {
            final JSONObject additionalDetails = new JSONObject();
            final JSONArray jsonarray = new JSONArray();
            if (isUserNameModified) {
                jsonarray.put((Object)"NAME");
            }
            if (isEmailAddressModified) {
                jsonarray.put((Object)"EMAIL_ADDRESS");
            }
            additionalDetails.put("MODIFIED_FIELDS", (Object)jsonarray);
            this.additionalDetails = additionalDetails;
        }
        catch (final JSONException ex) {
            Logger.getLogger(UserEvent.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    @Override
    public String toString() {
        final org.json.simple.JSONObject jsonObject = new org.json.simple.JSONObject();
        jsonObject.put((Object)"RESOURCE_ID", (Object)this.resourceID);
        jsonObject.put((Object)"CUSTOMER_ID", (Object)this.customerID);
        jsonObject.put((Object)"Resource", (Object)this.additionalDetails);
        jsonObject.put((Object)"MANAGED_USER_ID", (Object)this.userID);
        return jsonObject.toString();
    }
}
