package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.CustomProfilePayload;
import com.dd.plist.NSDictionary;
import java.util.HashMap;
import com.dd.plist.NSArray;
import com.me.mdm.webclient.formbean.MDMAppleCustomProfileFormBean;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.io.File;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DO2CustomProfilePayload implements DO2Payload
{
    private Logger logger;
    
    public DO2CustomProfilePayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        IOSPayload[] customPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("CustomProfileDetails");
            while (iterator.hasNext()) {
                final Row customProfileRow = iterator.next();
                String customProfilePath = (String)customProfileRow.get("CUSTOM_PROFILE_PATH");
                customProfilePath = customProfilePath.replace("/", File.separator);
                final String profileRepoPath = MDMApiFactoryProvider.getMDMUtilAPI().getCustomerDataParentPath();
                final String filePath = profileRepoPath + File.separator + customProfilePath;
                final NSDictionary rootDict = new MDMAppleCustomProfileFormBean().getDictionaryFromStream(filePath);
                final NSArray payloadArray = (NSArray)rootDict.get((Object)"PayloadContent");
                customPayload = new IOSPayload[payloadArray.count()];
                final HashMap payloadUUIDs = new HashMap();
                for (int i = 0; i < payloadArray.count(); ++i) {
                    final NSDictionary payloadDictionary = (NSDictionary)payloadArray.objectAtIndex(i);
                    final String payloadUUID = String.valueOf(payloadDictionary.get((Object)"PayloadUUID"));
                    customPayload[i] = new CustomProfilePayload(payloadDictionary);
                    final String newPayloadUUID = ((CustomProfilePayload)customPayload[i]).getPayloadUUIDFromPayloadDict();
                    payloadUUIDs.put(payloadUUID, newPayloadUUID);
                }
                for (int i = 0; i < payloadArray.count(); ++i) {
                    ((CustomProfilePayload)customPayload[i]).changePayloadUUID(payloadUUIDs);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Exception in getting rows", (Throwable)e);
        }
        catch (final Exception e2) {
            this.logger.log(Level.SEVERE, "Exception in parsing property list", e2);
        }
        return customPayload;
    }
}
