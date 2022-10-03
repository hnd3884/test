package com.me.mdm.server.apps.api.service;

import com.me.mdm.server.apps.blacklist.BlacklistAppHandler;
import java.util.List;
import com.me.mdm.server.apps.api.model.BlockListAppsModel;

public class BlocklistAppService
{
    public List<String> getCriticalApps(final BlockListAppsModel blockListAppsModel) {
        final BlacklistAppHandler blacklistAppHandler = new BlacklistAppHandler();
        return blacklistAppHandler.verifyCriticalApps(blockListAppsModel.getAppGroupIds(), blockListAppsModel.getCustomerId());
    }
}
