package com.adventnet.sym.webclient.mdm.reports;

import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.config.AppRepositoryTRAction;

public class AppLicenseReportsViewTRAction extends AppRepositoryTRAction
{
    public Logger logger;
    private static final String APP_GRP_TO_COLLN_COUNT_TAB_NAME = "AppGroupToCollection.count";
    public static final String APP_GRP_TO_COLLN_COUNT_COL_NAME = "AppGroupToCollection.RELEASE_LABEL_ID.count";
    
    public AppLicenseReportsViewTRAction() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery vppQuery = super.fetchAndCacheSelectQuery(viewCtx);
        if (!vppQuery.getTableList().contains(Table.getTable("MdStoreAssetToAppGroupRel"))) {
            vppQuery.addJoin(new Join("MdAppGroupDetails", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            vppQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            vppQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            vppQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            vppQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            vppQuery.addJoin(new Join("ManagedBusinessStore", "MdBusinessStoreSyncStatus", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 1));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "VPP_ASSET_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "TOTAL_LICENSE", "MdVppAsset.TOTAL_LICENSE"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "AVAILABLE_LICENSE_COUNT", "MdVppAsset.AVAILABLE_LICENSE_COUNT"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "ASSIGNED_LICENSE_COUNT", "MdVppAsset.ASSIGNED_LICENSE_COUNT"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "LICENSE_TYPE"));
            vppQuery.addSelectColumn(Column.getColumn("MdVppAsset", "LAST_SYNC_TIME", "MdVppAsset.LAST_SYNC_TIME"));
            vppQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "LOCATION_NAME", "MdVPPTokenDetails.LOCATION_NAME"));
            vppQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "ORGANISATION_NAME", "MdVPPTokenDetails.ORGANISATION_NAME"));
            vppQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "VPP_LAST_SYNC_TIME"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "APP_GROUP_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdStoreAssetToAppGroupRel", "STORE_ASSET_ID"));
            vppQuery.addSelectColumn(Column.getColumn("MdBusinessStoreSyncStatus", "REMARKS"));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), true);
            vppQuery.addSortColumn(sortColumn);
        }
        return vppQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        final String businessStoreIDStr = (String)SYMClientUtil.getStateValue(viewCtx, "businessStoreID");
        final String isZeroAssigned = (String)SYMClientUtil.getStateValue(viewCtx, "isZeroAssigned");
        final String isZeroAvailable = (String)SYMClientUtil.getStateValue(viewCtx, "isZeroAvailable");
        final String organisationID = (String)SYMClientUtil.getStateValue(viewCtx, "organisationID");
        Criteria criteria = query.getCriteria();
        if (!MDMStringUtils.isEmpty(businessStoreIDStr)) {
            final Long businessStoreID = Long.parseLong(businessStoreIDStr);
            if (businessStoreID != -1L) {
                final Criteria tokenIDCriteria = new Criteria(new Column("MdBusinessStoreToVppRel", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
                if (criteria != null) {
                    criteria = criteria.and(tokenIDCriteria);
                }
                else {
                    criteria = tokenIDCriteria;
                }
            }
        }
        if (!MDMStringUtils.isEmpty(isZeroAssigned) && Boolean.parseBoolean(isZeroAssigned)) {
            final Criteria tempCri = new Criteria(new Column("MdVppAsset", "ASSIGNED_LICENSE_COUNT"), (Object)0, 0);
            if (criteria != null) {
                criteria = criteria.and(tempCri);
            }
            else {
                criteria = tempCri;
            }
        }
        if (!MDMStringUtils.isEmpty(isZeroAvailable) && Boolean.parseBoolean(isZeroAvailable)) {
            final Criteria tempCri = new Criteria(new Column("MdVppAsset", "AVAILABLE_LICENSE_COUNT"), (Object)0, 0);
            if (criteria != null) {
                criteria = criteria.and(tempCri);
            }
            else {
                criteria = tempCri;
            }
        }
        if (!MDMStringUtils.isEmpty(organisationID)) {
            final Long businessStoreID = Long.parseLong(organisationID);
            if (businessStoreID != -1L) {
                String organisationName = null;
                try {
                    organisationName = VPPTokenDataHandler.getInstance().getVppTokenDetails(businessStoreID).getString("ORGANISATION_NAME");
                }
                catch (final DataAccessException e) {
                    this.logger.log(Level.SEVERE, "Exception in setting criteria for Orgname", (Throwable)e);
                }
                final Criteria tokenIDCriteria2 = new Criteria(new Column("MdVPPTokenDetails", "ORGANISATION_NAME"), (Object)organisationName, 0);
                if (criteria != null) {
                    criteria = criteria.and(tokenIDCriteria2);
                }
                else {
                    criteria = tokenIDCriteria2;
                }
            }
        }
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        super.setCriteria(query, viewCtx);
    }
}
