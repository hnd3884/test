package com.me.mdm.server.seqcommands.ios.policy;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;
import com.me.mdm.server.seqcommands.ios.PolicySpecificSeqCommand;

public class IOSRemoveCustomKioskSeqCmd implements PolicySpecificSeqCommand
{
    @Override
    public JSONObject getSequentialCommandForPolicy(final List configDoList, final JSONObject policyParams) throws Exception {
        final JSONObject seqCmdObject = new JSONObject();
        final JSONObject collectionObject = new JSONObject();
        final JSONArray metaDataList = new JSONArray();
        final JSONArray collectionArray = new JSONArray();
        try {
            boolean needToCreateRemoveCommand = true;
            for (int i = 0; i < configDoList.size(); ++i) {
                final DataObject dataObject = configDoList.get(i);
                final Integer configID = (Integer)dataObject.getFirstValue("ConfigData", "CONFIG_ID");
                if (configID.equals(183)) {
                    final Row appLockRow = dataObject.getRow("AppLockPolicy");
                    final int kioskMode = (int)appLockRow.get("KIOSK_MODE");
                    if (kioskMode == 3 || kioskMode == 1) {
                        needToCreateRemoveCommand = false;
                    }
                }
            }
            if (needToCreateRemoveCommand) {
                new IOSRemoveAppLockSeqCmd().addRemoveCustomKioskProfile(policyParams, metaDataList, collectionArray);
            }
            collectionObject.put("metaDataList", (Object)metaDataList);
            collectionObject.put("collectionArray", (Object)collectionArray);
            seqCmdObject.put("collection", (Object)collectionObject);
        }
        catch (final Exception e) {
            throw e;
        }
        return seqCmdObject;
    }
}
