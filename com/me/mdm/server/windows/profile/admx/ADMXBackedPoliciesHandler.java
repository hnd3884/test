package com.me.mdm.server.windows.profile.admx;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.WritableDataObject;
import java.util.List;

public class ADMXBackedPoliciesHandler
{
    private static ADMXBackedPoliciesHandler admxHandler;
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;
    
    private ADMXBackedPoliciesHandler() {
    }
    
    public static ADMXBackedPoliciesHandler getInstance() {
        if (ADMXBackedPoliciesHandler.admxHandler == null) {
            return ADMXBackedPoliciesHandler.admxHandler = new ADMXBackedPoliciesHandler();
        }
        return ADMXBackedPoliciesHandler.admxHandler;
    }
    
    public Object addAdmxDataGroup(final List<ADMXBackedPolicy> admxPolicyList, WritableDataObject dataObject, final String groupName) throws DataAccessException {
        final String[] gpNames = new String[admxPolicyList.size()];
        for (int i = 0; i < admxPolicyList.size(); ++i) {
            gpNames[i] = admxPolicyList.get(i).getGpName();
        }
        final Criteria admxRepoCriteria = new Criteria(new Column("ADMXBackedPolicy", "GP_NAME"), (Object)gpNames, 8);
        final DataObject admxRepoDO = this.getADMXRepoDO(admxRepoCriteria);
        if (dataObject == null) {
            dataObject = new WritableDataObject();
        }
        final Row admxGroupRow = new Row("ADMXBackedPolicyGroup");
        admxGroupRow.set("ADMX_BACKED_POLICY_GROUP_NAME", (Object)groupName);
        dataObject.addRow(admxGroupRow);
        final Object groupId = admxGroupRow.get("ADMX_BACKED_POLICY_GROUP_ID");
        for (final ADMXBackedPolicy admxData : admxPolicyList) {
            final Long admxPolicyID = this.getADMXPolicyID(admxRepoDO, admxData.getGpName());
            final Row admxPolicyConfigRow = new Row("ADMXBackedPolicyConfig");
            admxPolicyConfigRow.set("ADMX_BACKED_POLICY_ID", (Object)admxPolicyID);
            admxPolicyConfigRow.set("ADMX_STATUS", (Object)(int)(admxData.isEnabled() ? 1 : 0));
            final Object admxPolicyConfigId = admxPolicyConfigRow.get("ADMX_BACKED_POLICY_CONFIG_ID");
            final Row admxGroupToAdmxPolicyRow = new Row("ADMXGroupToADMXPolicy");
            admxGroupToAdmxPolicyRow.set("ADMX_BACKED_POLICY_CONFIG_ID", admxPolicyConfigId);
            admxGroupToAdmxPolicyRow.set("ADMX_BACKED_POLICY_GROUP_ID", groupId);
            for (final String dataId : admxData.getData().keySet()) {
                final Long admxPolicyDataID = this.getADMXPolicyDataId(admxRepoDO, dataId);
                final Row admxDataConfigRow = new Row("ADMXBackedPolicyDataConfig");
                admxDataConfigRow.set("ADMX_BACKED_POLICY_CONFIG_ID", admxPolicyConfigId);
                admxDataConfigRow.set("ADMX_BACKED_POLICY_DATA_ID", (Object)admxPolicyDataID);
                admxDataConfigRow.set("ADMX_BACKED_POLICY_CONFIG_DATA_VALUE", (Object)admxData.getData().get(dataId));
                dataObject.addRow(admxDataConfigRow);
            }
            dataObject.addRow(admxPolicyConfigRow);
            dataObject.addRow(admxGroupToAdmxPolicyRow);
        }
        return groupId;
    }
    
    public void addADMXColumns(final SelectQuery selectQuery, final Column groupIdReferenceColumn) {
        selectQuery.addJoin(new Join(groupIdReferenceColumn.getTableAlias(), "ADMXBackedPolicyGroup", new String[] { groupIdReferenceColumn.getColumnAlias() }, new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXBackedPolicyGroup", "ADMXGroupToADMXPolicy", new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, new String[] { "ADMX_BACKED_POLICY_GROUP_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXGroupToADMXPolicy", "ADMXBackedPolicyConfig", new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXBackedPolicyConfig", "ADMXBackedPolicy", new String[] { "ADMX_BACKED_POLICY_ID" }, new String[] { "ADMX_BACKED_POLICY_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXBackedPolicy", "ADMXBackedPolicyData", new String[] { "ADMX_BACKED_POLICY_ID" }, new String[] { "ADMX_BACKED_POLICY_ID" }, 1));
        selectQuery.addJoin(new Join("ADMXBackedPolicyConfig", "ADMXBackedPolicyDataConfig", new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, new String[] { "ADMX_BACKED_POLICY_CONFIG_ID" }, 1));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyGroup", "ADMX_BACKED_POLICY_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyGroup", "ADMX_BACKED_POLICY_GROUP_NAME"));
        selectQuery.addSelectColumn(new Column("ADMXGroupToADMXPolicy", "ADMX_BACKED_POLICY_GROUP_ID"));
        selectQuery.addSelectColumn(new Column("ADMXGroupToADMXPolicy", "ADMX_BACKED_POLICY_CONFIG_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyConfig", "ADMX_BACKED_POLICY_CONFIG_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyConfig", "ADMX_BACKED_POLICY_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyConfig", "ADMX_STATUS"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicy", "ADMX_BACKED_POLICY_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicy", "GP_NAME"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicy", "LOC_URI"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyData", "ADMX_BACKED_POLICY_DATA_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyData", "DATA_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyData", "ADMX_BACKED_POLICY_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyDataConfig", "ADMX_BACKED_POLICY_CONFIG_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyDataConfig", "ADMX_BACKED_POLICY_DATA_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyDataConfig", "ADMX_BACKED_POLICY_CONFIG_DATA_VALUE"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyDataConfig", "ADMX_BACKED_POLICY_DATA_CONFIG_ID"));
    }
    
    public List<ADMXBackedPolicy> getADMXData(final DataObject dataObject) throws DataAccessException {
        if (!dataObject.containsTable("ADMXBackedPolicyGroup") || !dataObject.containsTable("ADMXGroupToADMXPolicy") || !dataObject.containsTable("ADMXBackedPolicyConfig") || !dataObject.containsTable("ADMXBackedPolicy")) {
            return new ArrayList<ADMXBackedPolicy>();
        }
        final ArrayList<ADMXBackedPolicy> admxPoliciesList = new ArrayList<ADMXBackedPolicy>();
        final Iterator admxPolicyConfigIterator = dataObject.getRows("ADMXBackedPolicyConfig");
        while (admxPolicyConfigIterator.hasNext()) {
            final Row admxPolicyConfigRow = admxPolicyConfigIterator.next();
            final Long admxPolicyConfigId = (Long)admxPolicyConfigRow.get("ADMX_BACKED_POLICY_CONFIG_ID");
            final Long admxPolicyId = (Long)admxPolicyConfigRow.get("ADMX_BACKED_POLICY_ID");
            final Row admxBackedPolicyRow = dataObject.getRow("ADMXBackedPolicy", new Criteria(new Column("ADMXBackedPolicy", "ADMX_BACKED_POLICY_ID"), (Object)admxPolicyId, 0));
            final String gpName = (String)admxBackedPolicyRow.get("GP_NAME");
            final String locURI = (String)admxBackedPolicyRow.get("LOC_URI");
            final Integer admxStatus = (Integer)admxPolicyConfigRow.get("ADMX_STATUS");
            final ADMXBackedPolicy admxData = new ADMXBackedPolicy(gpName, locURI, admxStatus == 1);
            final Iterator admxPolicyDataConfigIterator = dataObject.getRows("ADMXBackedPolicyDataConfig", new Criteria(new Column("ADMXBackedPolicyDataConfig", "ADMX_BACKED_POLICY_CONFIG_ID"), (Object)admxPolicyConfigId, 0));
            while (admxPolicyDataConfigIterator.hasNext()) {
                final Row admxPolicyConfigDataRow = admxPolicyDataConfigIterator.next();
                final Long admxPolicyDataID = (Long)admxPolicyConfigDataRow.get("ADMX_BACKED_POLICY_DATA_ID");
                final String dataId = (String)dataObject.getValue("ADMXBackedPolicyData", "DATA_ID", new Criteria(new Column("ADMXBackedPolicyData", "ADMX_BACKED_POLICY_DATA_ID"), (Object)admxPolicyDataID, 0));
                final String dataValue = (String)admxPolicyConfigDataRow.get("ADMX_BACKED_POLICY_CONFIG_DATA_VALUE");
                admxData.addData(dataId, dataValue);
            }
            admxPoliciesList.add(admxData);
        }
        return admxPoliciesList;
    }
    
    public DataObject getADMXRepoDO(final Criteria criteria) throws DataAccessException {
        final SelectQueryImpl selectQuery = new SelectQueryImpl(new Table("ADMXBackedPolicy"));
        selectQuery.addJoin(new Join("ADMXBackedPolicy", "ADMXBackedPolicyData", new String[] { "ADMX_BACKED_POLICY_ID" }, new String[] { "ADMX_BACKED_POLICY_ID" }, 1));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicy", "ADMX_BACKED_POLICY_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicy", "GP_NAME"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyData", "ADMX_BACKED_POLICY_DATA_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyData", "ADMX_BACKED_POLICY_ID"));
        selectQuery.addSelectColumn(new Column("ADMXBackedPolicyData", "DATA_ID"));
        if (criteria != null) {
            selectQuery.setCriteria(criteria);
        }
        return MDMUtil.getPersistenceLite().get((SelectQuery)selectQuery);
    }
    
    public Long getADMXPolicyID(final DataObject dataObject, final String gpName) throws DataAccessException {
        final Row admxPolicyRow = dataObject.getRow("ADMXBackedPolicy", new Criteria(new Column("ADMXBackedPolicy", "GP_NAME"), (Object)gpName, 0, false));
        return (Long)admxPolicyRow.get("ADMX_BACKED_POLICY_ID");
    }
    
    public Long getADMXPolicyID(final String gpName) throws DataAccessException {
        final Criteria admxRepoCriteria = new Criteria(new Column("ADMXBackedPolicy", "GP_NAME"), (Object)gpName, 0, false);
        return this.getADMXPolicyID(this.getADMXRepoDO(admxRepoCriteria), gpName);
    }
    
    public Long getADMXPolicyDataId(final DataObject dataObject, final String dataId) throws DataAccessException {
        final Row admxPolicyRow = dataObject.getRow("ADMXBackedPolicyData", new Criteria(new Column("ADMXBackedPolicyData", "DATA_ID"), (Object)dataId, 0));
        return (Long)admxPolicyRow.get("ADMX_BACKED_POLICY_DATA_ID");
    }
    
    public Long getADMXPolicyDataId(final String dataId) throws DataAccessException {
        final Criteria admxRepoCriteria = new Criteria(new Column("ADMXBackedPolicyData", "DATA_ID"), (Object)dataId, 0);
        return this.getADMXPolicyDataId(this.getADMXRepoDO(admxRepoCriteria), dataId);
    }
}
