package com.me.mdm.onpremise.webclient.useradministration;

import javax.servlet.http.HttpServletRequest;
import com.me.mdm.onpremise.server.authentication.MDMPUserHandler;
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
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class UserAdministrationViewController extends MDMEmberTableRetrieverAction
{
    public static final Logger LOGGER;
    
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        try {
            if (!selectQuery.containsSubQuery()) {
                final SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("APIKeyInfo"));
                subSQ.addSelectColumn(Column.getColumn("APIKeyInfo", "CREATED_BY"));
                final List list = new ArrayList();
                final Column groupByCol = Column.getColumn("APIKeyInfo", "CREATED_BY");
                list.add(groupByCol);
                final GroupByClause groupBy = new GroupByClause(list);
                subSQ.setGroupByClause(groupBy);
                final DerivedTable dtab = new DerivedTable("APIKeyInfo", (Query)subSQ);
                final Table aaaUserTable = Table.getTable("AaaUser");
                selectQuery.addJoin(new Join(aaaUserTable, (Table)dtab, new String[] { "USER_ID" }, new String[] { "CREATED_BY" }, 1));
                selectQuery.addSelectColumn(Column.getColumn("APIKeyInfo", "CREATED_BY"));
            }
        }
        catch (final Exception e) {
            UserAdministrationViewController.LOGGER.log(Level.WARNING, "Exception in User Administration view :", e);
        }
        return selectQuery;
    }
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            request.setAttribute("UserLastLogon", (Object)MDMPUserHandler.getInstance().userLastLogonDetails());
        }
        catch (final Exception e) {
            UserAdministrationViewController.LOGGER.log(Level.WARNING, "Exception in MDMPUserAdministrationSqlViewController {0}", e);
        }
        super.setCriteria(query, viewCtx);
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
