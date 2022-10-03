package com.adventnet.sym.webclient.mdm.inv;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.apps.blacklist.BlacklistQueryUtils;
import com.me.mdm.server.android.knox.KnoxUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.table.web.TableViewModel;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class InstalledAppListTRAction extends MDMEmberTableRetrieverAction
{
    public Logger logger;
    
    public InstalledAppListTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        String empty_table_message = "";
        final String sResource_ID = viewCtx.getRequest().getParameter("RESOURCE_ID");
        if (ManagedDeviceHandler.getInstance().getPlatformType(Long.valueOf(sResource_ID)) == 3) {
            empty_table_message = "dc.mdm.inv.app.no_apps_distributed_from_mdm";
        }
        else {
            empty_table_message = "dc.common.NO_DATA_AVAILABLE";
        }
        final TableViewModel viewModel = (TableViewModel)viewCtx.getViewModel();
        final Row tableViewConfig = viewModel.getTableViewConfigRow();
        tableViewConfig.set("EMPTY_TABLE_MESSAGE", (Object)empty_table_message);
    }
    
    @Override
    public void setCriteria(SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String sResource_ID = request.getParameter("RESOURCE_ID");
            final String sStatus = request.getParameter("status");
            final String appScope = request.getParameter("appScope");
            final String modernAppFilter = request.getParameter("modernAppFilter");
            final String unique = viewCtx.getUniqueId();
            Criteria criteria = null;
            if (sResource_ID != null && !sResource_ID.trim().isEmpty()) {
                criteria = new Criteria(new Column("Resource", "RESOURCE_ID"), (Object)new Long(sResource_ID.trim()), 0, false);
                request.setAttribute("isKnoxEnabled", (Object)KnoxUtil.getInstance().isRegisteredAsKnox(new Long(sResource_ID.trim())));
            }
            if (sStatus != null && !sStatus.equalsIgnoreCase("") && !sStatus.equalsIgnoreCase("0")) {
                final BlacklistQueryUtils blackList = new BlacklistQueryUtils();
                Criteria filterCriteria = new Criteria();
                filterCriteria = blackList.getCriteriaforDeviceForApps(Integer.parseInt(sStatus));
                viewCtx.getRequest().setAttribute("status", (Object)sStatus);
                if (criteria != null) {
                    criteria = criteria.and(filterCriteria);
                }
                else {
                    criteria = filterCriteria;
                }
            }
            if (appScope != null && !appScope.trim().isEmpty()) {
                request.setAttribute("appScope", (Object)appScope);
                criteria = criteria.and(new Criteria(new Column("MdInstalledAppResourceRel", "SCOPE"), (Object)appScope, 0));
            }
            if (modernAppFilter != null && !modernAppFilter.trim().isEmpty() && !modernAppFilter.trim().equalsIgnoreCase("--")) {
                request.setAttribute("modernAppFilter", (Object)modernAppFilter);
                criteria = criteria.and(new Criteria(new Column("MdAppGroupDetails", "IS_MODERN_APP"), (Object)Boolean.valueOf(modernAppFilter), 0));
            }
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (!RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(loginId, false)) {
                query.addJoin(RBDAUtil.getInstance().getUserDeviceMappingJoin("Resource", "RESOURCE_ID"));
                final Criteria userDeviceCriteria = RBDAUtil.getInstance().getUserDeviceMappingCriteria(loginId);
                criteria = criteria.and(userDeviceCriteria);
            }
            query.setCriteria(criteria);
            query = AppSettingsDataHandler.getInstance().setOnViewFilterCriteria(query, request, unique);
            final String sQuery = RelationalAPI.getInstance().getSelectSQL((Query)query);
            this.logger.log(Level.FINE, "InstalledAppListTRAction Query -- {0}", sQuery);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in InstalledAppListTRAction ", ex);
        }
        super.setCriteria(query, viewCtx);
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        if (!selectQuery.getTableList().contains(Table.getTable("BlacklistAppCollectionStatus"))) {
            selectQuery.addJoin(new Join("MdAppToGroupRel", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
            final Criteria collectionCriteria = new Criteria(Column.getColumn("BlacklistAppToCollection", "COLLECTION_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "COLLECTION_ID"), 0);
            final Criteria resourceCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), 0);
            final Criteria scopeCriteria = new Criteria(Column.getColumn("MdInstalledAppResourceRel", "SCOPE"), (Object)Column.getColumn("BlacklistAppCollectionStatus", "SCOPE"), 0);
            selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", collectionCriteria.and(resourceCriteria).and(scopeCriteria), 1));
            selectQuery.addSelectColumn(new Column("BlacklistAppCollectionStatus", "STATUS", "BlacklistAppCollectionStatus.STATUS"));
        }
        return selectQuery;
    }
}
