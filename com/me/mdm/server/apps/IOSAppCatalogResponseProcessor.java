package com.me.mdm.server.apps;

import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.mdm.server.command.CommandResponseProcessor;

public class IOSAppCatalogResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final Long resourceID = params.optLong("resourceId");
        final ArrayList resourceList = new ArrayList();
        resourceList.add(resourceID);
        MDMiOSEntrollmentUtil.getInstance().addOrUpdateIOSWebClipAppCatalogStatus(resourceList, true);
        return null;
    }
}
