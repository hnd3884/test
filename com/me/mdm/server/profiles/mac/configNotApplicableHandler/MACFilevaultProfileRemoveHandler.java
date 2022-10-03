package com.me.mdm.server.profiles.mac.configNotApplicableHandler;

import java.util.logging.Level;
import java.util.Collection;
import java.util.ArrayList;
import com.me.mdm.server.command.ios.commandtask.IOSRemoveProfileCommandTaskHandler;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class MACFilevaultProfileRemoveHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final List<Long> resourceList = new IOSRemoveProfileCommandTaskHandler().getResourceNotHavingProfile(configNotApplicable.resourceList, configNotApplicable.profileId);
        final List<Long> configApplicableList = new ArrayList<Long>(configNotApplicable.resourceList);
        configApplicableList.removeAll(resourceList);
        MACFilevaultProfileRemoveHandler.LOGGER.log(Level.INFO, "MACFilevaultProfileRemoveHandler resource list:{0} & directremoval:{1}", new Object[] { resourceList, configNotApplicable.directRemovalList });
        configNotApplicable.directRemovalList.removeAll(configApplicableList);
        return configNotApplicable.directRemovalList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
