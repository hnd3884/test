package com.adventnet.sym.webclient.mdm.reports;

import com.adventnet.db.api.RelationalAPI;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class ApplicationByDeviceTRAction extends MDMTableRetrieverAction
{
    public Logger logger;
    
    public ApplicationByDeviceTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext arg0) throws Exception {
        SelectQuery selectQuery = super.fetchAndCacheSelectQuery(arg0);
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("temporary.selectqueryfix")) {
            selectQuery = MDMApiFactoryProvider.getMDMUtilAPI().deepCloneQuery(selectQuery);
        }
        if (!selectQuery.containsSubQuery()) {
            try {
                final Table resourceTable = Table.getTable("MdAppDetails");
                final SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("MdInstalledAppResourceRel"));
                subSQ.addSelectColumn(Column.getColumn("MdInstalledAppResourceRel", "APP_ID"));
                final Column resourceid_count_column = Column.getColumn("MdInstalledAppResourceRel", "RESOURCE_ID").count();
                resourceid_count_column.setColumnAlias("RESOURCE_ID");
                subSQ.addSelectColumn(resourceid_count_column);
                final List list = new ArrayList();
                final Column groupByCol = Column.getColumn("MdInstalledAppResourceRel", "APP_ID");
                list.add(groupByCol);
                final GroupByClause groupBy = new GroupByClause(list);
                subSQ.setGroupByClause(groupBy);
                final DerivedTable dtab = new DerivedTable("MdInstalledAppResourceRel", (Query)subSQ);
                selectQuery.addJoin(new Join(resourceTable, (Table)dtab, new String[] { "APP_ID" }, new String[] { "APP_ID" }, 1));
                final Column installation_count_column = new Column("MdInstalledAppResourceRel", "RESOURCE_ID");
                installation_count_column.setColumnAlias("INSTALLATION_COUNT");
                selectQuery.addSelectColumn(installation_count_column);
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception while dynamic join ---- :", e);
            }
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        super.setCriteria(query, viewCtx);
        try {
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)query);
            this.logger.log(Level.INFO, "SQL  ---- :{0}", sql);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while geting select query ---- :", e);
        }
    }
}
