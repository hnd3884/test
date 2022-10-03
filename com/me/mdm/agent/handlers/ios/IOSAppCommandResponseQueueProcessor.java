package com.me.mdm.agent.handlers.ios;

import org.json.JSONException;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.command.CommandUtil;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.agent.handlers.BaseAppCommandQueueProcessor;

public class IOSAppCommandResponseQueueProcessor extends BaseAppCommandQueueProcessor
{
    @Override
    protected void processCommand() throws JSONException {
        final String strData = (String)this.queueDataObject.queueData;
        final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(strData));
        final String responsedData = hmap.get("CommandResponse");
        CommandUtil.getInstance().processCommand(responsedData, this.queueDataObject.customerID, hmap, 1, this.queueDataObject);
    }
}
