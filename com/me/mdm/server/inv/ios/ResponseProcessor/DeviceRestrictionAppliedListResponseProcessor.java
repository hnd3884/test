package com.me.mdm.server.inv.ios.ResponseProcessor;

import com.me.mdm.server.seqcommands.SeqCmdRepository;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.PlistWrapper;
import java.util.HashMap;
import org.json.JSONException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class DeviceRestrictionAppliedListResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor, CommandResponseProcessor.ImmediateSeqResponseProcessor
{
    Logger logger;
    private static final Logger LOGGER;
    
    public DeviceRestrictionAppliedListResponseProcessor() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        try {
            final String strData = (String)params.get("strData");
            final Long resourceID = (Long)params.get("resourceId");
            final HashMap restrictionHash = this.getRestrictionHash(strData);
            MDMInvDataPopulator.getInstance().processIOSRestriction(resourceID, restrictionHash);
        }
        catch (final JSONException e) {
            DeviceRestrictionAppliedListResponseProcessor.LOGGER.log(Level.SEVERE, "Exception in parsing the response", (Throwable)e);
        }
        catch (final Exception e2) {
            DeviceRestrictionAppliedListResponseProcessor.LOGGER.log(Level.SEVERE, "Exception while processing ios restriction", e2);
        }
        return null;
    }
    
    private HashMap getRestrictionHash(final String strPlist) {
        final HashMap hsmap = new HashMap();
        this.logger.log(Level.INFO, "Processing restriction plist");
        try {
            final NSDictionary nsDict = PlistWrapper.getInstance().getDictForKey("GlobalRestrictions", strPlist);
            for (int i = 0; i < nsDict.allKeys().length; ++i) {
                final String key = nsDict.allKeys()[i];
                final NSObject nsObj = nsDict.objectForKey(key);
                final NSDictionary subDict = (NSDictionary)nsObj;
                this.logger.log(Level.INFO, "subDict Count {0}", subDict.allKeys().length);
                for (int j = 0; j < subDict.allKeys().length; ++j) {
                    final String key2 = subDict.allKeys()[j];
                    final NSDictionary valueDict = (NSDictionary)subDict.get((Object)key2);
                    final Object valueObj = valueDict.objectForKey("value");
                    if (valueObj != null) {
                        hsmap.put(key2, valueObj.toString());
                    }
                    else {
                        hsmap.put(key2, valueDict);
                    }
                }
            }
            this.logger.log(Level.INFO, "Processing restriction plist completed");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in getRestrctionHash()", ex);
        }
        return hsmap;
    }
    
    @Override
    public JSONObject processImmediateSeqCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final String commandUUID = params.optString("strCommandUuid");
        final JSONObject seqResponse = new JSONObject();
        final JSONObject response = new JSONObject();
        final JSONObject seqParams = new JSONObject();
        seqParams.put("isNeedToRemove", true);
        response.put("resourceID", (Object)resourceID);
        response.put("commandUUID", (Object)commandUUID);
        response.put("params", (Object)seqParams);
        response.put("action", 1);
        SeqCmdRepository.getInstance().processSeqCommand(response);
        return seqResponse;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
