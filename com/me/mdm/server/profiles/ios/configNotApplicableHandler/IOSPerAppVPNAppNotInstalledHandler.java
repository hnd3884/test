package com.me.mdm.server.profiles.ios.configNotApplicableHandler;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.HashMap;
import com.me.mdm.server.profiles.ios.IOSPerAppVPNHandler;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class IOSPerAppVPNAppNotInstalledHandler implements MDMConfigNotApplicableListener
{
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        try {
            final List<Long> collectionIds = new ArrayList<Long>();
            collectionIds.add(configNotApplicable.collectionId);
            final List<Long> resourceList = new ArrayList<Long>();
            resourceList.addAll(configNotApplicable.resourceList);
            final HashMap<Long, List<Long>> collectionResource = new IOSPerAppVPNHandler().getAppInstalledResourceForCollection(collectionIds, configNotApplicable.resourceList);
            if (collectionResource.containsKey(configNotApplicable.collectionId)) {
                resourceList.removeAll(collectionResource.get(configNotApplicable.collectionId));
            }
            return resourceList;
        }
        catch (final Exception ex) {
            return new ArrayList<Long>();
        }
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 16, "");
        }
        catch (final DataAccessException ex) {}
    }
}
