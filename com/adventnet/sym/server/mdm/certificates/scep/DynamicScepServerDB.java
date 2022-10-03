package com.adventnet.sym.server.mdm.certificates.scep;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;

public class DynamicScepServerDB
{
    private DynamicScepServerDB() {
    }
    
    public static DataObject getScepServerDetailsForScepConfig(final Long scepId) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("SCEPConfigurations"));
        selectQuery.addJoin(new Join("SCEPConfigurations", "SCEPServerToTemplate", new String[] { "SCEP_CONFIG_ID" }, new String[] { "SCEP_CONFIG_ID" }, 2));
        selectQuery.addJoin(new Join("SCEPConfigurations", "ScepDyChallengeCredentials", new String[] { "SCEP_CONFIG_ID" }, new String[] { "SCEP_CONFIG_ID" }, 1));
        selectQuery.addJoin(new Join("SCEPServerToTemplate", "SCEPServers", new String[] { "SCEP_SERVER_ID" }, new String[] { "SERVER_ID" }, 2));
        final Criteria criteria = new Criteria(new Column("SCEPConfigurations", "SCEP_CONFIG_ID"), (Object)scepId, 0);
        selectQuery.setCriteria(criteria);
        selectQuery.addSelectColumn(new Column("SCEPConfigurations", "*"));
        selectQuery.addSelectColumn(new Column("ScepDyChallengeCredentials", "*"));
        selectQuery.addSelectColumn(new Column("SCEPServers", "*"));
        return SyMUtil.getPersistence().get(selectQuery);
    }
}
