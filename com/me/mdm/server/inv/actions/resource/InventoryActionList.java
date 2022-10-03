package com.me.mdm.server.inv.actions.resource;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class InventoryActionList
{
    public List<InventoryAction> actions;
    @SerializedName("knox_actions")
    public List<InventoryAction> knoxActions;
}
