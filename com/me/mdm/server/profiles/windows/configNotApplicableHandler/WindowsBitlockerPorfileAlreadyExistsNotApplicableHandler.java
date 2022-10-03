package com.me.mdm.server.profiles.windows.configNotApplicableHandler;

import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class WindowsBitlockerPorfileAlreadyExistsNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final Long collectionId = configNotApplicable.collectionId;
        final List<Long> resourceList = configNotApplicable.resourceList;
        final List<Long> notApplicableList = new ArrayList<Long>();
        try {
            final Criteria configIdCriteria = new Criteria(Column.getColumn("ConfigData", "CONFIG_ID"), (Object)613, 0);
            final DataObject dataObject = ProfileUtil.getInstance().getMDMConfigDO(resourceList, configIdCriteria);
            if (!dataObject.isEmpty()) {
                for (final Long resourceId : resourceList) {
                    final Criteria resourceIdCriteria = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)resourceId, 0);
                    final Criteria collectionIdCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 1);
                    final Criteria statusCriteria = new Criteria(Column.getColumn("CollnToResources", "STATUS"), (Object)new Integer[] { 8, 12 }, 9);
                    final Iterator iterator = dataObject.getRows("CollnToResources", resourceIdCriteria.and(collectionIdCriteria).and(statusCriteria));
                    if (iterator.hasNext()) {
                        notApplicableList.add(resourceId);
                    }
                }
            }
        }
        catch (final DataAccessException ex) {
            WindowsBitlockerPorfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.SEVERE, "Exception in getting not applicable device list", (Throwable)ex);
        }
        return notApplicableList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "mdm.bitlocker.multiple_bitlocker_profile_error");
        }
        catch (final DataAccessException e) {
            WindowsBitlockerPorfileAlreadyExistsNotApplicableHandler.LOGGER.log(Level.SEVERE, "Exception in setting the collection Status", (Throwable)e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
