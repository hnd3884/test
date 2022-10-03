package com.me.mdm.server.apps;

import org.json.JSONObject;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import java.util.HashMap;
import java.util.logging.Logger;

public class BaseAppTrashModeAction implements AppTrashActionInterface
{
    public Logger logger;
    
    public BaseAppTrashModeAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void performUninstallForGroup(final HashMap params) {
        final Long customerId = params.get("CustomerID");
        final Long profileID = params.get("ProfileID");
        final HashMap grpList = params.get("GroupList");
        try {
            final List kioskProfileIDList = new AppTrashModeHandler().getSingleAppKioskProfiles(profileID);
            for (final Long kioskProfileID : kioskProfileIDList) {
                if (kioskProfileID != null) {
                    Map KioskgrpList = new HashMap();
                    KioskgrpList = ProfileUtil.getInstance().getManagedGroupsAssignedForProfile(kioskProfileID);
                    if (KioskgrpList.isEmpty()) {
                        continue;
                    }
                    ProfileUtil.getInstance().disassociateResourcesForProfiles(kioskProfileID, customerId, true, KioskgrpList, 1);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "error while getting Kiosk profile", (Throwable)e);
        }
        catch (final QueryConstructionException e2) {
            this.logger.log(Level.WARNING, "error while getting Kiosk profile", (Throwable)e2);
        }
        if (!grpList.isEmpty()) {
            final HashMap profileParams = new HashMap();
            profileParams.put("PROFILE_ID", profileID);
            profileParams.put("CUSTOMER_ID", customerId);
            profileParams.put("isGroup", true);
            profileParams.put("GrpList", grpList);
            profileParams.put("isApp", true);
            profileParams.put("commandName", "RemoveApplication");
            ProfileUtil.getInstance().disassociateResourcesForProfiles(profileParams);
        }
    }
    
    @Override
    public void performUninstallForResource(final HashMap params) {
        final Long customerId = params.get("CustomerID");
        final Long profileID = params.get("ProfileID");
        final HashMap grpList = params.get("ResourceList");
        try {
            final List kioskProfileIDList = new AppTrashModeHandler().getSingleAppKioskProfiles(profileID);
            for (final Long kioskProfileID : kioskProfileIDList) {
                if (kioskProfileID != null) {
                    Map KioskgrpList = new HashMap();
                    KioskgrpList = ProfileUtil.getInstance().getManagedDevicesAssignedForProfile(kioskProfileID);
                    if (KioskgrpList.isEmpty()) {
                        continue;
                    }
                    ProfileUtil.getInstance().disassociateResourcesForProfiles(kioskProfileID, customerId, false, KioskgrpList, 1);
                }
            }
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.WARNING, "erro while getting Kiosk profile", (Throwable)e);
        }
        catch (final QueryConstructionException e2) {
            this.logger.log(Level.WARNING, "erro while getting Kiosk profile", (Throwable)e2);
        }
        final HashMap profileParams = new HashMap();
        profileParams.put("PROFILE_ID", profileID);
        profileParams.put("CUSTOMER_ID", customerId);
        profileParams.put("isGroup", false);
        profileParams.put("GrpList", grpList);
        profileParams.put("isApp", true);
        profileParams.put("commandName", "RemoveApplication");
        ProfileUtil.getInstance().disassociateResourcesForProfiles(profileParams);
    }
    
    @Override
    public boolean isDeleteAllowed(final Long profileID, final JSONObject messages) {
        final boolean isDeleteAllowed = true;
        return isDeleteAllowed;
    }
}
