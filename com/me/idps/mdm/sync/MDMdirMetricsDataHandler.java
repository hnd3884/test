package com.me.idps.mdm.sync;

import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import org.json.simple.JSONArray;
import com.me.idps.core.util.DirectoryMickeyIssueHandler;
import com.adventnet.db.api.RelationalAPI;
import org.json.simple.JSONObject;
import java.sql.Connection;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.sync.synch.DirectoryMetricsDataHandler;

class MDMdirMetricsDataHandler extends DirectoryMetricsDataHandler
{
    private static MDMdirMetricsDataHandler mdmDirMetricsDataHandler;
    
    public static MDMdirMetricsDataHandler getInstance() {
        if (MDMdirMetricsDataHandler.mdmDirMetricsDataHandler == null) {
            MDMdirMetricsDataHandler.mdmDirMetricsDataHandler = new MDMdirMetricsDataHandler();
        }
        return MDMdirMetricsDataHandler.mdmDirMetricsDataHandler;
    }
    
    private Column getTotalManagedUserCountCol() {
        final Criteria cri = this.getValidObjCri().and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0));
        return this.getCountCol("TOTAL_MANAGEDUSER_COUNT", cri, Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
    }
    
    private Column getDirectoryManagedUserCountCol() {
        final Criteria criteria = this.getValidObjCri().and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 1)).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0));
        return this.getCountCol("DIR_MANAGEDUSER_COUNT", criteria, Column.getColumn("ManagedUser", "MANAGED_USER_ID"));
    }
    
    private SelectQuery getManagedUserShareQuery(final Long customerID) {
        final SelectQuery domainCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        domainCountQuery.addJoin(new Join("CustomerInfo", "Resource", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        domainCountQuery.addJoin(new Join("Resource", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        domainCountQuery.addJoin(new Join("Resource", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        domainCountQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        domainCountQuery.addSelectColumn(this.getTotalManagedUserCountCol());
        domainCountQuery.addSelectColumn(this.getDirectoryManagedUserCountCol());
        final Column custCol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        domainCountQuery.addSelectColumn(custCol);
        if (customerID != null) {
            domainCountQuery.setCriteria(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0));
        }
        domainCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custCol))));
        return domainCountQuery;
    }
    
    private SelectQuery getDuplicatedManagedUserCountQuery(final Long customerID) {
        final Column sameManagedUserCustCol = new Column("CustomerInfo", "CUSTOMER_ID", "innerCust");
        final Column sameManagedUserEmailCol = Column.getColumn("ManagedUser", "EMAIL_ADDRESS", "innerEmail");
        final Column sameManagedUserDomainCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME", "innerDomain");
        final Column sameManagedUserCountCol = (Column)Column.createFunction("COUNT", new Object[] { "*" });
        sameManagedUserCountCol.setType(4);
        sameManagedUserCountCol.setColumnAlias("count");
        final SelectQuery sameManagedUserInnerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        sameManagedUserInnerQuery.addJoin(new Join("CustomerInfo", "Resource", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        sameManagedUserInnerQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        sameManagedUserInnerQuery.setCriteria(this.getValidObjCri().and(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)null, 1)));
        if (customerID != null) {
            sameManagedUserInnerQuery.setCriteria(sameManagedUserInnerQuery.getCriteria().and(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
        }
        sameManagedUserInnerQuery.addSelectColumn(sameManagedUserCustCol);
        sameManagedUserInnerQuery.addSelectColumn(sameManagedUserEmailCol);
        sameManagedUserInnerQuery.addSelectColumn(sameManagedUserCountCol);
        sameManagedUserInnerQuery.addSelectColumn(sameManagedUserDomainCol);
        sameManagedUserInnerQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(sameManagedUserCustCol, sameManagedUserEmailCol, sameManagedUserDomainCol)), new Criteria(sameManagedUserCountCol, (Object)2, 4)));
        final DerivedTable dt = new DerivedTable("innerDT", (Query)sameManagedUserInnerQuery);
        final SelectQuery custSameUserResCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        custSameUserResCountQuery.addJoin(new Join(Table.getTable("CustomerInfo"), (Table)dt, new String[] { "CUSTOMER_ID" }, new String[] { sameManagedUserCustCol.getColumnAlias() }, 1));
        final Column innerUserResCountCol = new Column(dt.getTableAlias(), sameManagedUserCountCol.getColumnAlias());
        final CaseExpression ce = new CaseExpression("MANAGEDUSER_DUPLICATION_COUNT");
        ce.addWhen(new Criteria(innerUserResCountCol, (Object)null, 1).and(new Criteria(innerUserResCountCol, (Object)2, 4)), (Object)new Column(dt.getTableAlias(), sameManagedUserCustCol.getColumnAlias()));
        custSameUserResCountQuery.addSelectColumn(IdpsUtil.getInstance().getCountCaseExpressionColumn(ce));
        final Column custIDcol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        custSameUserResCountQuery.addSelectColumn(custIDcol);
        custSameUserResCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custIDcol))));
        return custSameUserResCountQuery;
    }
    
    private SelectQuery getManagedUserDuplAcrossDomainsCountQuery(final Long customerID) {
        final Column sameManagedUserCustCol = new Column("CustomerInfo", "CUSTOMER_ID", "innerCust");
        final Column sameManagedUserEmailCol = Column.getColumn("ManagedUser", "EMAIL_ADDRESS", "innerEmail");
        final Column sameManagedUserCountCol = (Column)Column.createFunction("COUNT", new Object[] { "*" });
        sameManagedUserCountCol.setType(4);
        sameManagedUserCountCol.setColumnAlias("count");
        final SelectQuery sameManagedUserInnerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        sameManagedUserInnerQuery.addJoin(new Join("CustomerInfo", "Resource", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        sameManagedUserInnerQuery.addJoin(new Join("Resource", "ManagedUser", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_USER_ID" }, 1));
        sameManagedUserInnerQuery.setCriteria(this.getValidObjCri().and(new Criteria(Column.getColumn("ManagedUser", "MANAGED_USER_ID"), (Object)null, 1)));
        if (customerID != null) {
            sameManagedUserInnerQuery.setCriteria(sameManagedUserInnerQuery.getCriteria().and(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
        }
        sameManagedUserInnerQuery.addSelectColumn(sameManagedUserCustCol);
        sameManagedUserInnerQuery.addSelectColumn(sameManagedUserEmailCol);
        sameManagedUserInnerQuery.addSelectColumn(sameManagedUserCountCol);
        sameManagedUserInnerQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(sameManagedUserCustCol, sameManagedUserEmailCol)), new Criteria(sameManagedUserCountCol, (Object)2, 4)));
        final DerivedTable dt = new DerivedTable("innerDT", (Query)sameManagedUserInnerQuery);
        final SelectQuery custSameUserResCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        custSameUserResCountQuery.addJoin(new Join(Table.getTable("CustomerInfo"), (Table)dt, new String[] { "CUSTOMER_ID" }, new String[] { sameManagedUserCustCol.getColumnAlias() }, 1));
        final Column innerUserResCountCol = new Column(dt.getTableAlias(), sameManagedUserCountCol.getColumnAlias());
        final CaseExpression ce = new CaseExpression("MANAGEDUSER_DUPL_ACROSS_DOMAINS_COUNT");
        ce.addWhen(new Criteria(innerUserResCountCol, (Object)null, 1).and(new Criteria(innerUserResCountCol, (Object)2, 4)), (Object)new Column(dt.getTableAlias(), sameManagedUserCustCol.getColumnAlias()));
        custSameUserResCountQuery.addSelectColumn(IdpsUtil.getInstance().getCountCaseExpressionColumn(ce));
        final Column custIDcol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        custSameUserResCountQuery.addSelectColumn(custIDcol);
        custSameUserResCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custIDcol))));
        return custSameUserResCountQuery;
    }
    
    private JSONObject getMetrics(final Connection connection, final SelectQuery selectQuery) throws Exception {
        String query = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
        query = DirectoryMickeyIssueHandler.getInstance().rectifyQuery(query);
        final JSONArray domainDetails = IdpsUtil.executeSelectQuery(connection, query, selectQuery.getSelectColumns());
        if (domainDetails != null && domainDetails.size() > 0) {
            return (JSONObject)domainDetails.get(0);
        }
        return null;
    }
    
    JSONObject getMDMmetrackingDetails(final Long customerID, final JSONObject input) {
        Connection connection = null;
        final JSONObject mdmMetrics = new JSONObject();
        try {
            connection = RelationalAPI.getInstance().getConnection();
            final int dirUserCount = Integer.valueOf(String.valueOf(input.get((Object)"DIR_USER_COUNT")));
            final JSONObject mdmManagedUserShareMetrics = this.getMetrics(connection, this.getManagedUserShareQuery(customerID));
            final int dirManagedUserCount = Integer.valueOf(String.valueOf(mdmManagedUserShareMetrics.get((Object)"DIR_MANAGEDUSER_COUNT")));
            final int totalManagedUserCount = Integer.valueOf(String.valueOf(mdmManagedUserShareMetrics.get((Object)"TOTAL_MANAGEDUSER_COUNT")));
            final String dirManagedUserPercentage = this.getPercentage(dirManagedUserCount, dirUserCount);
            final String manageduserDirPercentage = this.getPercentage(dirManagedUserCount, totalManagedUserCount);
            mdmMetrics.put((Object)"DIR_MANAGEDUSER_SHARE", (Object)dirManagedUserPercentage);
            mdmMetrics.put((Object)"MANAGEDUSER_DIR_SHARE", (Object)manageduserDirPercentage);
            final JSONObject mdmManagedUserDuplMetrics = this.getMetrics(connection, this.getDuplicatedManagedUserCountQuery(customerID));
            final int duplManagedUserEmailCount = Integer.valueOf(String.valueOf(mdmManagedUserDuplMetrics.get((Object)"MANAGEDUSER_DUPLICATION_COUNT")));
            mdmMetrics.put((Object)"MANAGEDUSER_DUPLICATION_COUNT", (Object)duplManagedUserEmailCount);
            final JSONObject mdmManagedUserDuplAcrossDomainsMetrics = this.getMetrics(connection, this.getManagedUserDuplAcrossDomainsCountQuery(customerID));
            final int managedUserDuplAcrossDomainsCount = Integer.valueOf(String.valueOf(mdmManagedUserDuplAcrossDomainsMetrics.get((Object)"MANAGEDUSER_DUPL_ACROSS_DOMAINS_COUNT")));
            mdmMetrics.put((Object)"MANAGEDUSER_DUPL_ACROSS_DOMAINS_COUNT", (Object)managedUserDuplAcrossDomainsCount);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection", ex);
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, "exception in closing connection", ex2);
            }
        }
        return mdmMetrics;
    }
    
    static {
        MDMdirMetricsDataHandler.mdmDirMetricsDataHandler = null;
    }
}
