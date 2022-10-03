package com.adventnet.sym.server.mdm.apps.vpp;

import java.util.Collection;
import org.json.JSONArray;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class VPPAppAssignCommand
{
    public JSONObject commandJson;
    
    public VPPAppAssignCommand() {
        this.commandJson = new JSONObject();
    }
    
    public JSONObject getCommandJSON() {
        return this.commandJson;
    }
    
    public void setSToken(final String sToken) throws JSONException {
        this.getCommandJSON().put("sToken", (Object)sToken);
    }
    
    public void setClientUserIdStr(final String clientUserIdStr) throws JSONException {
        this.getCommandJSON().put("clientUserIdStr", (Object)clientUserIdStr);
    }
    
    public void setUserId(final Long userId) throws JSONException {
        this.getCommandJSON().put("userId", (Object)userId);
    }
    
    public void setAdamId(final Integer adamId) throws JSONException {
        this.getCommandJSON().put("adamId", (Object)adamId);
    }
    
    public void setAdamIdStr(final Integer adamId) throws JSONException {
        this.getCommandJSON().put("adamIdStr", (Object)adamId.toString());
    }
    
    public void setPricingParam(final String pricingParam) throws JSONException {
        this.getCommandJSON().put("pricingParam", (Object)pricingParam);
    }
    
    public void setClientContext(final String clientContext) throws JSONException {
        this.getCommandJSON().put("clientContext", (Object)clientContext);
    }
    
    public void setDeviceAssociations(final List associationsList) throws JSONException {
        final JSONArray array = new JSONArray((Collection)associationsList);
        this.getCommandJSON().put("associateSerialNumbers", (Object)array);
    }
    
    public void setUserAssociations(final List associationsList) throws JSONException {
        final JSONArray array = new JSONArray((Collection)associationsList);
        this.getCommandJSON().put("associateClientUserIdStrs", (Object)array);
    }
    
    public void setDeviceDisassociations(final List disassociationsList) throws JSONException {
        final JSONArray array = new JSONArray((Collection)disassociationsList);
        this.getCommandJSON().put("disassociateSerialNumbers", (Object)array);
    }
    
    public void setUserDisassociations(final List disassociationsList) throws JSONException {
        final JSONArray array = new JSONArray((Collection)disassociationsList);
        this.getCommandJSON().put("disassociateClientUserIdStrs", (Object)array);
    }
    
    public void setAssignedOnlyKey(final Boolean value) {
        this.getCommandJSON().put("assignedOnly", (Object)value);
    }
    
    public void setNotifyDisassociations(final Boolean notifyDisassociation) throws JSONException {
        this.getCommandJSON().put("notifyDisassociation", (Object)notifyDisassociation);
    }
    
    public void setLicenseIdDisassociations(final List disassociationsList) throws JSONException {
        final JSONArray array = new JSONArray((Collection)disassociationsList);
        this.getCommandJSON().put("disassociateLicenseIdStrs", (Object)array);
    }
    
    public void setLicenseId(final Long licenseId) throws JSONException {
        this.getCommandJSON().put("licenseId", (Object)licenseId);
    }
    
    public void setBatchToken(final String batchToken) throws JSONException {
        this.getCommandJSON().put("batchToken", (Object)batchToken);
    }
    
    public void setSinceModifiedToken(final String sinceModifiedToken) throws JSONException {
        this.getCommandJSON().put("sinceModifiedToken", (Object)sinceModifiedToken);
    }
    
    public void setIncludeLicenseCounts(final Boolean includeLicenseCounts) throws JSONException {
        this.getCommandJSON().put("includeLicenseCounts", (Object)includeLicenseCounts);
    }
    
    @Override
    public String toString() {
        return this.getCommandJSON().toString();
    }
}
