package com.me.mdm.server.apps.android.afw.appmgmt;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.DBUtil;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.mdm.server.apps.usermgmt.StoreAccountManagementHandler;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;

public class StoreAppsLicenseHandler
{
    public void addOrUpdateStoreAppsLicenseToBSUsers(final Long bsId, final Long appGroupId, final JSONArray storeUserArr) throws DataAccessException, JSONException {
        final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria storeCriteria = new Criteria(Column.getColumn("StoreAppsLicenseSummary", "BUSINESSSTORE_ID"), (Object)bsId, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdLicenseToAppGroupRel"));
        final Join licenseDetails = new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        final Join storeAppsLicenseSummary = new Join("MdLicense", "StoreAppsLicenseSummary", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        final Join storeAppsLicenseToBSUsers = new Join("StoreAppsLicenseSummary", "StoreAppsLicenseToBSUser", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 1);
        selectQuery.addJoin(licenseDetails);
        selectQuery.addJoin(storeAppsLicenseSummary);
        selectQuery.addJoin(storeAppsLicenseToBSUsers);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(appCriteria.and(storeCriteria));
        DataObject dO = DataAccess.get(selectQuery);
        if (!dO.isEmpty()) {
            final Row row = dO.getRow("StoreAppsLicenseSummary");
            final Long licenseId = (Long)row.get("LICENSE_ID");
            final Criteria licenseCriteria = new Criteria(Column.getColumn("StoreAppsLicenseToBSUser", "LICENSE_ID"), (Object)licenseId, 0);
            for (int i = 0; i < storeUserArr.length(); ++i) {
                final Long bsUserId = new StoreAccountManagementHandler().getBSUserIdFromStoreId(bsId, String.valueOf(storeUserArr.get(i)));
                final Criteria bsUserIdCriteria = new Criteria(Column.getColumn("StoreAppsLicenseToBSUser", "BS_USER_ID"), (Object)bsUserId, 0);
                Row storeAppToBsUserRow = dO.getRow("StoreAppsLicenseToBSUser", bsUserIdCriteria.and(licenseCriteria));
                if (storeAppToBsUserRow == null) {
                    storeAppToBsUserRow = new Row("StoreAppsLicenseToBSUser");
                    storeAppToBsUserRow.set("BS_USER_ID", (Object)bsUserId);
                    storeAppToBsUserRow.set("LICENSE_ID", (Object)licenseId);
                    dO.addRow(storeAppToBsUserRow);
                }
            }
            dO = DataAccess.update(dO);
            this.updateProvisionedCount(dO, licenseId);
        }
    }
    
    public void removeStoreAppsLicenseToBSUsers(final Long bsId, final Long appGroupId, final JSONArray storeUserArr) throws DataAccessException, JSONException {
        final Criteria appCriteria = new Criteria(Column.getColumn("MdLicenseToAppGroupRel", "APP_GROUP_ID"), (Object)appGroupId, 0);
        final Criteria storeCriteria = new Criteria(Column.getColumn("StoreAppsLicenseSummary", "BUSINESSSTORE_ID"), (Object)bsId, 0);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdLicenseToAppGroupRel"));
        final Join licenseDetails = new Join("MdLicenseToAppGroupRel", "MdLicense", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        final Join storeAppsLicenseSummary = new Join("MdLicense", "StoreAppsLicenseSummary", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 2);
        final Join storeAppsLicenseToBSUsers = new Join("StoreAppsLicenseSummary", "StoreAppsLicenseToBSUser", new String[] { "LICENSE_ID" }, new String[] { "LICENSE_ID" }, 1);
        selectQuery.addJoin(licenseDetails);
        selectQuery.addJoin(storeAppsLicenseSummary);
        selectQuery.addJoin(storeAppsLicenseToBSUsers);
        selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        selectQuery.setCriteria(appCriteria.and(storeCriteria));
        DataObject dO = DataAccess.get(selectQuery);
        if (!dO.isEmpty()) {
            final Row row = dO.getRow("StoreAppsLicenseSummary");
            final Long licenseId = (Long)row.get("LICENSE_ID");
            final Criteria licenseCriteria = new Criteria(Column.getColumn("StoreAppsLicenseToBSUser", "LICENSE_ID"), (Object)licenseId, 0);
            for (int i = 0; i < storeUserArr.length(); ++i) {
                final Long bsUserId = new StoreAccountManagementHandler().getBSUserIdFromStoreId(bsId, String.valueOf(storeUserArr.get(i)));
                final Criteria bsUserIdCriteria = new Criteria(Column.getColumn("StoreAppsLicenseToBSUser", "BS_USER_ID"), (Object)bsUserId, 0);
                final Row storeAppToBsUserRow = dO.getRow("StoreAppsLicenseToBSUser", bsUserIdCriteria.and(licenseCriteria));
                if (storeAppToBsUserRow != null) {
                    dO.deleteRow(storeAppToBsUserRow);
                }
            }
            dO = DataAccess.update(dO);
            this.updateProvisionedCount(dO, licenseId);
        }
    }
    
    private void updateProvisionedCount(final DataObject dO, final Long licenseId) throws DataAccessException {
        if (!dO.isEmpty()) {
            final Iterator iter = dO.getRows("StoreAppsLicenseToBSUser", new Criteria(Column.getColumn("StoreAppsLicenseToBSUser", "LICENSE_ID"), (Object)licenseId, 0));
            final int provisionedCount = DBUtil.getIteratorSize(iter);
            final Row storeLicenseDetailsRow = dO.getRow("StoreAppsLicenseSummary", new Criteria(Column.getColumn("StoreAppsLicenseSummary", "LICENSE_ID"), (Object)licenseId, 0));
            storeLicenseDetailsRow.set("PROVISIONED_COUNT", (Object)provisionedCount);
            dO.updateRow(storeLicenseDetailsRow);
            DataAccess.update(dO);
        }
    }
}
