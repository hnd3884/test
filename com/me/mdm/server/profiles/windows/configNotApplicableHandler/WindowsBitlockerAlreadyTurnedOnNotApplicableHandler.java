package com.me.mdm.server.profiles.windows.configNotApplicableHandler;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.me.mdm.server.profiles.MDMConfigNotApplicable;
import java.util.logging.Logger;
import com.me.mdm.server.profiles.MDMConfigNotApplicableListener;

public class WindowsBitlockerAlreadyTurnedOnNotApplicableHandler implements MDMConfigNotApplicableListener
{
    private static final Logger LOGGER;
    
    @Override
    public List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable configNotApplicable) {
        final Long collectionId = configNotApplicable.collectionId;
        final List<Long> resourceList = configNotApplicable.resourceList;
        final List<Long> notApplicableList = new ArrayList<Long>();
        try {
            for (final Long resourceId : resourceList) {
                JSONObject securityInfo = new JSONObject();
                securityInfo = InventoryUtil.getInstance().getSecurityInfo(resourceId, securityInfo);
                final JSONObject securityDetails = securityInfo.optJSONObject("security");
                final boolean isDeviceEncrypted = securityDetails.optBoolean("STORAGE_ENCRYPTION");
                if (isDeviceEncrypted) {
                    notApplicableList.add(resourceId);
                }
            }
        }
        catch (final SyMException e) {
            WindowsBitlockerAlreadyTurnedOnNotApplicableHandler.LOGGER.log(Level.SEVERE, "Exception in getting not applicable device list", (Throwable)e);
        }
        return notApplicableList;
    }
    
    @Override
    public void setNotApplicableStatus(final List resourceIDList, final Long collnId) {
        try {
            MDMCollectionStatusUpdate.getInstance().updateStatusForCollntoRes(resourceIDList, collnId, 8, "mdm.bitlocker.already_turned_on");
        }
        catch (final DataAccessException e) {
            WindowsBitlockerAlreadyTurnedOnNotApplicableHandler.LOGGER.log(Level.SEVERE, "Exception in setting the collection Status", (Throwable)e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
