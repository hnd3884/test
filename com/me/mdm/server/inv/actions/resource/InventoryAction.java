package com.me.mdm.server.inv.actions.resource;

import com.google.gson.annotations.SerializedName;

public class InventoryAction
{
    public String name;
    @SerializedName("localized_name")
    public String localizedName;
    @SerializedName("is_enabled")
    public boolean isEnabled;
    @SerializedName("action_info")
    public String actionInfo;
    @SerializedName("localized_action_info")
    public String localizedActionInfo;
    @SerializedName("status_description")
    public String statusDescription;
    @SerializedName("localized_status_description")
    public String localizedStatusDescription;
    @SerializedName("status_code")
    public Integer statusCode;
    @SerializedName("remarks")
    public String remarks;
    
    public InventoryAction() {
        this.localizedName = "--";
        this.actionInfo = "--";
        this.localizedActionInfo = "--";
        this.statusDescription = "--";
        this.localizedStatusDescription = "--";
        this.statusCode = -1;
        this.remarks = "--";
    }
}
