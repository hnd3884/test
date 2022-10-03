package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSString;
import com.dd.plist.NSData;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import java.util.UUID;

public class IOSCommandPayload extends IOSPayload
{
    String commandUUID;
    
    public IOSCommandPayload() {
        this.commandUUID = null;
        this.commandUUID = UUID.randomUUID().toString();
        final NSDictionary commandDict = new NSDictionary();
        this.getPayloadDict().put("CommandUUID", (Object)this.commandUUID);
        this.getPayloadDict().put("Command", (NSObject)commandDict);
    }
    
    public NSDictionary getCommandDict() {
        return (NSDictionary)this.getPayloadDict().objectForKey("Command");
    }
    
    public void setRequestType(final String requestType) {
        this.getCommandDict().put("RequestType", (Object)requestType);
        this.commandUUID = requestType + ";" + this.commandUUID;
        this.getPayloadDict().put("CommandUUID", (Object)this.commandUUID);
    }
    
    public void setPayload(final byte[] payloadData) {
        final NSData nsdata = new NSData(payloadData);
        this.getCommandDict().put("Payload", (NSObject)nsdata);
    }
    
    public String getCommandUUID() {
        return this.commandUUID;
    }
    
    public void setCommandUUID(final String commandUUID) {
        final NSString requestType = (NSString)this.getCommandDict().objectForKey("RequestType");
        if (requestType != null) {
            this.commandUUID = requestType + ";" + commandUUID;
        }
        this.getPayloadDict().put("CommandUUID", (Object)this.commandUUID);
    }
    
    public void setCommandUUID(final String commandUUID, final boolean appendReqType) {
        if (appendReqType) {
            this.setCommandUUID(commandUUID);
        }
        else {
            this.commandUUID = commandUUID;
            this.getPayloadDict().put("CommandUUID", (Object)this.commandUUID);
        }
    }
}
