package com.me.mdm.server.android.knox.core;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.group.GroupEvent;
import com.adventnet.sym.server.mdm.group.MDMCustomGroupListner;

public class KnoxCustomGroupListener implements MDMCustomGroupListner
{
    @Override
    public void customGroupAdded(final GroupEvent groupEvent) {
    }
    
    @Override
    public void customGroupDeleted(final GroupEvent groupEvent) {
        final SelectQuery knoxDSQuery = (SelectQuery)new SelectQueryImpl(new Table("KNOXDistributionSettings"));
        knoxDSQuery.addSelectColumn(new Column((String)null, "*"));
        knoxDSQuery.setCriteria(new Criteria(new Column("KNOXDistributionSettings", "CUSTOMER_ID"), (Object)groupEvent.customerID, 0));
        try {
            final DataObject dO = SyMUtil.getPersistence().get(knoxDSQuery);
            if (!dO.isEmpty()) {
                final Row knoxDsRow = dO.getFirstRow("KNOXDistributionSettings");
                if ((int)knoxDsRow.get("KNOXSETTINGS_OPTION") == 1 && (boolean)knoxDsRow.get("KNOXSETTINGS_TOGROUPONLY")) {
                    final SelectQuery knoxDSGroup = (SelectQuery)new SelectQueryImpl(new Table("KnoxLicenseDSToGroupRel"));
                    knoxDSGroup.addSelectColumn(new Column((String)null, "*"));
                    knoxDSGroup.setCriteria(new Criteria(new Column("KnoxLicenseDSToGroupRel", "KNOXSETTINGS_ID"), knoxDsRow.get("KNOXSETTINGS_ID"), 0));
                    final DataObject knoxDSDO = SyMUtil.getPersistence().get(knoxDSGroup);
                    if (knoxDSDO.isEmpty()) {
                        knoxDsRow.set("KNOXSETTINGS_OPTION", (Object)2);
                        knoxDsRow.set("KNOXSETTINGS_TOGROUPONLY", (Object)false);
                        dO.updateRow(knoxDsRow);
                        SyMUtil.getPersistence().update(dO);
                    }
                }
            }
        }
        catch (final DataAccessException ex) {
            Logger.getLogger(KnoxCustomGroupListener.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    @Override
    public void customGroupModified(final GroupEvent groupEvent) {
    }
    
    @Override
    public void customGroupPreDelete(final GroupEvent groupEvent) {
    }
}
