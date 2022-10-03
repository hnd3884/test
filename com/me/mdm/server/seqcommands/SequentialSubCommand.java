package com.me.mdm.server.seqcommands;

import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SequentialSubCommand
{
    private Logger logger;
    public Long SequentialCommandID;
    public Long CommandID;
    public int order;
    public String Handler;
    public Long AddedAt;
    public Long UpdatedAt;
    public Long resourceID;
    public JSONObject params;
    public int status;
    public Boolean isImmidiate;
    
    public SequentialSubCommand() {
        this.logger = Logger.getLogger("MDMSequentialCommandsLogger");
    }
    
    @Override
    public String toString() {
        try {
            final JSONObject stringObject = new JSONObject();
            stringObject.put("SequentialCommandID", (Object)this.SequentialCommandID);
            stringObject.put("CommandID", (Object)this.CommandID);
            stringObject.put("order", this.order);
            stringObject.put("Handler", (Object)this.Handler);
            stringObject.put("AddedAt", (Object)this.AddedAt);
            stringObject.put("UpdatedAt", (Object)this.UpdatedAt);
            stringObject.put("resourceID", (Object)this.resourceID);
            stringObject.put("status", this.status);
            final String subcmdString = stringObject.toString();
            return subcmdString;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Error in tostring method", (Throwable)e);
            return null;
        }
    }
}
