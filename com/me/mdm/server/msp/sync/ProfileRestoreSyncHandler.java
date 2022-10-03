package com.me.mdm.server.msp.sync;

import com.adventnet.ds.query.UpdateQuery;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.util.ArrayList;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;

public class ProfileRestoreSyncHandler extends ProfilesSyncEngine
{
    ProfileRestoreSyncHandler(final DCQueueData dcQueueData) {
        super(dcQueueData);
    }
    
    @Override
    public void sync() {
        try {
            final List customerList = SyncConfigurationsUtil.getApplicableCustomers(this.customerId);
            ProfileRestoreSyncHandler.logger.log(Level.INFO, "Restore profile {0} for customers {1}", new Object[] { this.parentProfileDO, customerList });
            final Iterator<Long> iterator = customerList.iterator();
            final List profileIdList = new ArrayList();
            while (iterator.hasNext()) {
                final Long customerId = iterator.next();
                ProfileRestoreSyncHandler.logger.log(Level.INFO, "Restore profile for customer {0}", new Object[] { customerId });
                try {
                    final JSONObject childProfileDetails = this.getChildSpecificUVH(customerId);
                    final Long profileId = childProfileDetails.getLong("PROFILE_ID");
                    if (profileId.equals(-1L)) {
                        continue;
                    }
                    profileIdList.add(profileId);
                }
                catch (final Exception ex) {
                    ProfileRestoreSyncHandler.logger.log(Level.SEVERE, "Exception in getting profileId for Customer {0} in profile restore {1}", new Object[] { customerId, ex });
                }
            }
            final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("Profile");
            updateQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileIdList.toArray(), 8));
            updateQuery.setUpdateColumn("IS_MOVED_TO_TRASH", (Object)false);
            MDMUtil.getPersistence().update(updateQuery);
        }
        catch (final Exception ex2) {
            ProfileRestoreSyncHandler.logger.log(Level.SEVERE, "Exception in ProfileRestoreSyncHandler {0} {1}", new Object[] { this.parentProfileDO, ex2 });
        }
    }
}
