package com.adventnet.sym.server.mdm.task;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.sym.webclient.mdm.config.ProfileConfigHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class TrashProfileDeletionTask
{
    private static Logger logger;
    
    public void executeTask() {
        try {
            final SelectQueryImpl query = new SelectQueryImpl(new Table("Profile"));
            query.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            Long lastDate = null;
            Criteria DeleteCri = new Criteria(new Column("Profile", "IS_MOVED_TO_TRASH"), (Object)true, 0);
            Hashtable ht = new Hashtable();
            ht = DateTimeUtil.determine_From_To_Times("today");
            final Long today = ht.get("date1");
            final String deletionTime = SyMUtil.getSyMParameter("ProfileDeletionTime");
            if (deletionTime != null) {
                lastDate = today - Integer.parseInt(deletionTime) * 24 * 60 * 60 * 1000L;
            }
            else {
                lastDate = today - 7776000000L;
            }
            final Criteria DateCri = new Criteria(new Column("Profile", "LAST_MODIFIED_TIME"), (Object)lastDate, 7);
            DeleteCri = DeleteCri.and(DateCri);
            query.setCriteria(DeleteCri);
            query.addSelectColumn(new Column("Profile", "PROFILE_ID"));
            query.addSelectColumn(new Column("ProfileToCustomerRel", "*"));
            final DataObject dataObject = MDMUtil.getReadOnlyPersistence().get((SelectQuery)query);
            for (Iterator customerIterator = dataObject.getRows("ProfileToCustomerRel"); customerIterator.hasNext(); customerIterator = dataObject.getRows("ProfileToCustomerRel")) {
                final Row customerRow = customerIterator.next();
                final Long customerId = (Long)customerRow.get("CUSTOMER_ID");
                final Iterator it = dataObject.getRows("ProfileToCustomerRel", new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
                final ArrayList<String> ListProfileId = (ArrayList<String>)DBUtil.getColumnValuesAsList(it, "PROFILE_ID");
                TrashProfileDeletionTask.logger.log(Level.INFO, "Profile is deleting permanently. ProfileIds:{0} & CustomerId:{1}", new Object[] { ListProfileId, customerId });
                ProfileConfigHandler.deleteProfilePermanently(ListProfileId, customerId);
                dataObject.deleteRows("ProfileToCustomerRel", new Criteria(new Column("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerId, 0));
            }
            TrashProfileDeletionTask.logger.log(Level.INFO, "Profile Trash task completed.");
        }
        catch (final Exception ex) {
            Logger.getLogger(TrashProfileDeletionTask.class.getName()).log(Level.SEVERE, "Exception occur at the time of Deletion of the profile", ex);
        }
    }
    
    static {
        TrashProfileDeletionTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
