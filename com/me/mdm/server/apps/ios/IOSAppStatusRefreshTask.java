package com.me.mdm.server.apps.ios;

import org.json.JSONArray;
import java.util.Iterator;
import org.json.JSONException;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import java.util.Collection;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashSet;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.apps.AppStatusRefreshTaskInterface;

public class IOSAppStatusRefreshTask implements AppStatusRefreshTaskInterface
{
    private static final Logger LOGGER;
    private static final int BATCH_SIZE = 500;
    
    @Override
    public void addAppStatusCommand(final JSONObject appRefreshObject) {
        try {
            IOSAppStatusRefreshTask.LOGGER.log(Level.INFO, "IOS App refresh status command started");
            final HashSet resourceSet = new HashSet();
            final Iterator appRefreshKey = appRefreshObject.keys();
            while (appRefreshKey.hasNext()) {
                final String appGroupId = appRefreshKey.next();
                final JSONArray resourceArray = appRefreshObject.getJSONArray(appGroupId);
                resourceSet.addAll(JSONUtil.getInstance().convertJSONArrayTOList(resourceArray));
            }
            final List<String> commandUUIDs = new ArrayList<String>();
            commandUUIDs.add("InstalledApplicationList");
            commandUUIDs.add("ManagedApplicationList");
            final List<Long> resourceList = new ArrayList<Long>(resourceSet);
            final List<Long> commandList = DeviceCommandRepository.getInstance().getCommandIdsFromCommandUUIDs(commandUUIDs);
            IOSAppStatusRefreshTask.LOGGER.log(Level.INFO, "Going to add command for app status refresh. For resourceList:{0}", new Object[] { resourceList });
            final List alreadyAvailableResourceList = DeviceCommandRepository.getInstance().getCommandsAvailableDeviceList(commandUUIDs, resourceList);
            IOSAppStatusRefreshTask.LOGGER.log(Level.INFO, "Already command available resource:{0}", new Object[] { alreadyAvailableResourceList });
            resourceList.removeAll(alreadyAvailableResourceList);
            for (int i = 0; i < resourceList.size(); i += 500) {
                List<Long> batchList = new ArrayList<Long>();
                if (resourceList.size() < i + 500 - 1) {
                    batchList = resourceList.subList(i, resourceList.size());
                }
                else {
                    batchList = resourceList.subList(i, i + 500);
                }
                IOSAppStatusRefreshTask.LOGGER.log(Level.INFO, "Going to add command for batch resource:{0}", new Object[] { batchList });
                DeviceCommandRepository.getInstance().assignCommandToDevices(commandList, batchList);
                NotificationHandler.getInstance().SendNotification(batchList, 1);
            }
        }
        catch (final JSONException e) {
            IOSAppStatusRefreshTask.LOGGER.log(Level.SEVERE, "Exception in iOS addAppStatusCommand", (Throwable)e);
        }
        catch (final Exception e2) {
            IOSAppStatusRefreshTask.LOGGER.log(Level.SEVERE, "Exception in waking up devices", e2);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
