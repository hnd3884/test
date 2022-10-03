package com.me.idps.core.sync.synch;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.idps.core.factory.IdpsFactoryProvider;
import org.json.simple.JSONArray;
import com.me.idps.core.util.DirectoryMickeyIssueHandler;
import com.adventnet.db.api.RelationalAPI;
import java.util.Map;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import com.me.idps.core.util.DirectoryUtil;
import java.util.Properties;
import com.adventnet.ds.query.UpdateQuery;
import com.me.idps.core.util.DirectoryQueryutil;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.List;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.idps.core.util.DirectoryMetricConstants;
import org.json.simple.JSONObject;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.CaseExpression;
import java.util.concurrent.TimeUnit;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;

public class DirectoryMetricsDataHandler
{
    private static DirectoryMetricsDataHandler directoryMetricsDataHandler;
    
    public static DirectoryMetricsDataHandler getInstance() {
        if (DirectoryMetricsDataHandler.directoryMetricsDataHandler == null) {
            DirectoryMetricsDataHandler.directoryMetricsDataHandler = new DirectoryMetricsDataHandler();
        }
        return DirectoryMetricsDataHandler.directoryMetricsDataHandler;
    }
    
    protected Criteria getValidObjCri() {
        return new Criteria(Column.getColumn("Resource", "NAME"), (Object)null, 1, false).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)null, 1, false)).and(new Criteria(Column.getColumn("Resource", "NAME"), (Object)new String[] { "", "-", "--", "---" }, 9, false)).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)new String[] { "", "-", "--", "---" }, 9, false));
    }
    
    private Integer getRank(final Integer count) {
        if (count > 0 && count < 500) {
            return 1;
        }
        if (count >= 500 && count < 5000) {
            return 2;
        }
        if (count >= 5000 && count < 10000) {
            return 3;
        }
        if (count >= 10000 && count < 50000) {
            return 4;
        }
        if (count >= 50000 && count < 100000) {
            return 5;
        }
        if (count >= 100000 && count < 500000) {
            return 6;
        }
        if (count >= 500000) {
            return 7;
        }
        return 0;
    }
    
    private Integer getDurationRank(final long millisDuration) {
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millisDuration);
        if (seconds < 60L) {
            return 1;
        }
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millisDuration);
        if (minutes < 1L) {
            return 1;
        }
        if (minutes >= 1L && minutes < 5L) {
            return 2;
        }
        if (minutes > 5L && minutes < 30L) {
            return 3;
        }
        return 4;
    }
    
    protected String getPercentage(final int value, final int total) {
        if (total == 0) {
            return "0";
        }
        return String.valueOf(value * 100 / total);
    }
    
    protected Column getCountCol(final String ceAlais, final Criteria criteria, final Object whenVal) {
        final CaseExpression ce = new CaseExpression(ceAlais);
        ce.addWhen(criteria, whenVal);
        return IdpsUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn((Column)ce);
    }
    
    private void addOrUpdate(final JSONObject custDirDetails, final boolean increment) {
        final Long customerID = (Long)custDirDetails.get((Object)"CUSTOMER_ID");
        if (customerID != null) {
            try {
                if (!increment) {
                    custDirDetails.put((Object)"VERSION", (Object)220506);
                }
                final List<String> dirKeys = DirectoryMetricConstants.getTrackingKeys();
                final DataObject dobj = IdpsUtil.getPersistenceLite().get("DirectoryMetrics", new Criteria(Column.getColumn("DirectoryMetrics", "CUSTOMER_ID"), (Object)customerID, 0));
                for (int i = 0; i < dirKeys.size(); ++i) {
                    final String key = dirKeys.get(i);
                    if (custDirDetails.containsKey((Object)key)) {
                        final String val = String.valueOf(custDirDetails.get((Object)key));
                        if (!SyMUtil.isStringEmpty(val) || val.equalsIgnoreCase("null")) {
                            Row row = dobj.getRow("DirectoryMetrics", new Criteria(Column.getColumn("DirectoryMetrics", "KEY"), (Object)key, 0));
                            if (row != null) {
                                if (increment) {
                                    try {
                                        final Integer oldValInt = Integer.valueOf(String.valueOf(row.get("VALUE")));
                                        row.set("VALUE", (Object)(Math.max(0, oldValInt) + Math.max(0, Integer.valueOf(val))));
                                    }
                                    catch (final Exception ex2) {}
                                }
                                else {
                                    row.set("VALUE", (Object)val);
                                }
                                dobj.updateRow(row);
                            }
                            else {
                                row = new Row("DirectoryMetrics");
                                row.set("KEY", (Object)key);
                                row.set("VALUE", (Object)val);
                                row.set("CUSTOMER_ID", (Object)customerID);
                                dobj.addRow(row);
                            }
                        }
                    }
                }
                if (increment) {
                    IDPSlogger.EVENT.log(Level.INFO, "incremented : {0}", new Object[] { IdpsUtil.getPrettyJSON(custDirDetails) });
                }
                IdpsUtil.getPersistenceLite().update(dobj);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void initMetricsTable(final Long customerID) throws DataAccessException {
        final DataObject dobj = IdpsUtil.getPersistenceLite().get("DirectoryMetrics", new Criteria(Column.getColumn("DirectoryMetrics", "CUSTOMER_ID"), (Object)customerID, 0));
        final DataObject insDobj = IdpsUtil.getPersistenceLite().constructDataObject();
        if (dobj != null) {
            final List<String> trackingKeys = DirectoryMetricConstants.getTrackingKeys();
            for (int i = 0; i < trackingKeys.size(); ++i) {
                final String key = trackingKeys.get(i);
                Row row = dobj.getRow("DirectoryMetrics", new Criteria(Column.getColumn("DirectoryMetrics", "KEY"), (Object)key, 0, false));
                if (row == null) {
                    row = new Row("DirectoryMetrics");
                    row.set("CUSTOMER_ID", (Object)customerID);
                    row.set("KEY", (Object)key);
                    row.set("VALUE", (Object)String.valueOf(0));
                    insDobj.addRow(row);
                }
            }
            IdpsUtil.getPersistenceLite().add(insDobj);
        }
    }
    
    private Column getOktaCountCol() {
        return this.getCountCol("OKTA_COUNT", new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)301, 0), Column.getColumn("DMDomain", "DOMAIN_ID"));
    }
    
    private Column getPeopeCountCol() {
        final Criteria cri = new Criteria(Column.getColumn("CustomerParams", "PARAM_VALUE"), (Object)"ENABLED", 0, false).and(new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)"ZohoPeopleIntegration", 0, false));
        return this.getCountCol("PEOPLE_COUNT", cri, Column.getColumn("CustomerParams", "CUSTOMER_ID"));
    }
    
    private Column getAzureCountCol() {
        return this.getCountCol("AZURE_COUNT", new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)3, 0), Column.getColumn("DMDomain", "DOMAIN_ID"));
    }
    
    private Column getHybridCountCol() {
        return this.getCountCol("HYBRID_COUNT", new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)4, 0), Column.getColumn("DMDomain", "DOMAIN_ID"));
    }
    
    private Column getGsuiteCountCol() {
        return this.getCountCol("GSUITE_COUNT", new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)101, 0), Column.getColumn("DMDomain", "DOMAIN_ID"));
    }
    
    private Column getZohoDirCountCol() {
        return this.getCountCol("ZOHO_DIR_COUNT", new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)201, 0), Column.getColumn("DMDomain", "DOMAIN_ID"));
    }
    
    private Column getTotalDirCountCol() {
        return this.getCountCol("TOTAL_DIR_COUNT", new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)1, 1), Column.getColumn("DMDomain", "DOMAIN_ID"));
    }
    
    private Column getDirectoryUserCountCol() {
        return this.getCountCol("DIR_USER_COUNT", this.getValidObjCri().and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0)), Column.getColumn("DirResRel", "RESOURCE_ID"));
    }
    
    private Column getDirectoryGroupCountCol() {
        return this.getCountCol("DIR_GROUP_COUNT", this.getValidObjCri().and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0)), Column.getColumn("DirResRel", "RESOURCE_ID"));
    }
    
    private Column getTotalUserCountCol() {
        return this.getCountCol("TOTAL_USER_COUNT", this.getValidObjCri().and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0)), Column.getColumn("Resource", "RESOURCE_ID"));
    }
    
    private Column getTotalGroupCountCol() {
        return this.getCountCol("TOTAL_GROUP_COUNT", this.getValidObjCri().and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)101, 0)), Column.getColumn("Resource", "RESOURCE_ID"));
    }
    
    private Column getDirectoryDeletedObjCountCol() {
        return this.getCountCol("DIR_OBJ_DEL_COUNT", this.getValidObjCri().and(new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0)).and(new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)4, 0)), Column.getColumn("DirObjRegIntVal", "RESOURCE_ID"));
    }
    
    private Column getDomainDeletedObjectCountCol() {
        final Criteria criteria = new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)null, 0).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)new Integer[] { 2, 101 }, 8)).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)new String[] { "MDM", "CRITERIA_GROUP" }, 9, false));
        return this.getCountCol("DEL_DIR_OBJ_COUNT", criteria, Column.getColumn("Resource", "RESOURCE_ID"));
    }
    
    private SelectQuery getSameEmailCountQuery(final Long customerID) {
        final Column sameEmailCustCol = new Column("CustomerInfo", "CUSTOMER_ID", "innerCust");
        final Column sameEmailCol = Column.getColumn("DirObjRegStrVal", "VALUE", "innerEmail");
        final Column emailCountCol = (Column)Column.createFunction("COUNT", new Object[] { Column.getColumn("DirObjRegStrVal", "VALUE") });
        emailCountCol.setType(4);
        emailCountCol.setColumnAlias("count");
        final Criteria emailNotNullCri = new Criteria(Column.getColumn("DirObjRegStrVal", "VALUE"), (Object)null, 1);
        final SelectQuery sameEmailInnerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        sameEmailInnerQuery.addJoin(new Join("CustomerInfo", "Resource", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        sameEmailInnerQuery.addJoin(new Join("Resource", "DirObjRegStrVal", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        sameEmailInnerQuery.setCriteria(emailNotNullCri.and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0)).and(new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)106L, 0)));
        if (customerID != null) {
            sameEmailInnerQuery.setCriteria(sameEmailInnerQuery.getCriteria().and(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
        }
        sameEmailInnerQuery.addSelectColumn(sameEmailCol);
        sameEmailInnerQuery.addSelectColumn(sameEmailCustCol);
        sameEmailInnerQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(sameEmailCustCol, sameEmailCol)), emailNotNullCri.and(new Criteria(emailCountCol, (Object)2, 4))));
        final DerivedTable dt = new DerivedTable("innerDT", (Query)sameEmailInnerQuery);
        final SelectQuery custSameEmailCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        custSameEmailCountQuery.addJoin(new Join(Table.getTable("CustomerInfo"), (Table)dt, new String[] { "CUSTOMER_ID" }, new String[] { sameEmailCustCol.getColumnAlias() }, 1));
        final Column innerEmailCol = new Column(dt.getTableAlias(), sameEmailCol.getColumnAlias());
        final CaseExpression ce = new CaseExpression("SAME_EMAIL_COUNT");
        ce.addWhen(new Criteria(innerEmailCol, (Object)null, 1), (Object)innerEmailCol);
        custSameEmailCountQuery.addSelectColumn(IdpsUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn((Column)ce));
        custSameEmailCountQuery.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"));
        custSameEmailCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(Column.getColumn("CustomerInfo", "CUSTOMER_ID")))));
        return custSameEmailCountQuery;
    }
    
    private SelectQuery getDuplicatedUserResourceCountQuery(final Long customerID) {
        final Column sameUserResCustCol = new Column("CustomerInfo", "CUSTOMER_ID", "innerCust");
        final Column sameUserResNameCol = Column.getColumn("Resource", "NAME", "innerResName");
        final Column sameUserResDomainCol = Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME", "innerDomain");
        final Column sameUserResCountCol = IdpsUtil.getCountOfColumn("Resource", "RESOURCE_ID", "count");
        final Criteria resDmDomainJoinCri = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)Column.getColumn("DMDomain", "CUSTOMER_ID"), 0).and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)Column.getColumn("DMDomain", "NAME"), 0, false));
        final SelectQuery sameUserResInnerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        sameUserResInnerQuery.addJoin(new Join("CustomerInfo", "Resource", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        sameUserResInnerQuery.addJoin(new Join("Resource", "DMDomain", resDmDomainJoinCri, 2));
        sameUserResInnerQuery.setCriteria(this.getValidObjCri().and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0)).and(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)1, 1)));
        sameUserResInnerQuery.addSelectColumn(sameUserResCustCol);
        sameUserResInnerQuery.addSelectColumn(sameUserResCountCol);
        sameUserResInnerQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(sameUserResCustCol, sameUserResNameCol, sameUserResDomainCol)), new Criteria(sameUserResCountCol, (Object)2, 4)));
        final DerivedTable dt = new DerivedTable("innerDT", (Query)sameUserResInnerQuery);
        final SelectQuery custSameUserResCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        custSameUserResCountQuery.addJoin(new Join(Table.getTable("CustomerInfo"), (Table)dt, new String[] { "CUSTOMER_ID" }, new String[] { sameUserResCustCol.getColumnAlias() }, 1));
        final Column innerUserResCountCol = new Column(dt.getTableAlias(), sameUserResCountCol.getColumnAlias());
        innerUserResCountCol.setType(4);
        final CaseExpression ce = new CaseExpression("RESOURCE_DUPLICATION_COUNT");
        ce.addWhen(new Criteria(innerUserResCountCol, (Object)null, 1).and(new Criteria(innerUserResCountCol, (Object)2, 4)), (Object)new Column(dt.getTableAlias(), sameUserResCustCol.getColumnAlias()));
        custSameUserResCountQuery.addSelectColumn(IdpsUtil.getInstance().getCountCaseExpressionColumn(ce));
        final Column custIDcol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        custSameUserResCountQuery.addSelectColumn(custIDcol);
        custSameUserResCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custIDcol))));
        return custSameUserResCountQuery;
    }
    
    private SelectQuery getDuplicatedDomainCountQuery(final Long customerID) {
        final Column sameDomainCustCol = new Column("CustomerInfo", "CUSTOMER_ID", "innerCust");
        final Column sameDomainCol = Column.getColumn("DMDomain", "NAME", "innerDomain");
        final Column domainCountCol = (Column)Column.createFunction("COUNT", new Object[] { Column.getColumn("DMDomain", "NAME") });
        domainCountCol.setType(4);
        domainCountCol.setColumnAlias("count");
        final Criteria domainNotNullCri = new Criteria(Column.getColumn("DMDomain", "NAME"), (Object)null, 1);
        final SelectQuery sameDomainInnerQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        sameDomainInnerQuery.addJoin(new Join("CustomerInfo", "DMDomain", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        sameDomainInnerQuery.setCriteria(domainNotNullCri);
        if (customerID != null) {
            sameDomainInnerQuery.setCriteria(sameDomainInnerQuery.getCriteria().and(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0)));
        }
        sameDomainInnerQuery.addSelectColumn(sameDomainCol);
        sameDomainInnerQuery.addSelectColumn(sameDomainCustCol);
        sameDomainInnerQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(sameDomainCustCol, sameDomainCol)), domainNotNullCri.and(new Criteria(domainCountCol, (Object)2, 4))));
        final DerivedTable dt = new DerivedTable("innerDT", (Query)sameDomainInnerQuery);
        final SelectQuery custSameDomainCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        custSameDomainCountQuery.addJoin(new Join(Table.getTable("CustomerInfo"), (Table)dt, new String[] { "CUSTOMER_ID" }, new String[] { sameDomainCustCol.getColumnAlias() }, 1));
        final Column innerDomainCol = new Column(dt.getTableAlias(), sameDomainCol.getColumnAlias());
        final CaseExpression ce = new CaseExpression("DOMAIN_DUPLICATION_COUNT");
        ce.addWhen(new Criteria(innerDomainCol, (Object)null, 1), (Object)innerDomainCol);
        custSameDomainCountQuery.addSelectColumn(IdpsUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn((Column)ce));
        custSameDomainCountQuery.addSelectColumn(Column.getColumn("CustomerInfo", "CUSTOMER_ID"));
        custSameDomainCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(Column.getColumn("CustomerInfo", "CUSTOMER_ID")))));
        return custSameDomainCountQuery;
    }
    
    private SelectQuery getDomainDetailsQuery(final Long customerID) {
        final SelectQuery domainCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        domainCountQuery.addJoin(new Join("CustomerInfo", "DMDomain", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        domainCountQuery.addSelectColumn(this.getOktaCountCol());
        domainCountQuery.addSelectColumn(this.getAzureCountCol());
        domainCountQuery.addSelectColumn(this.getGsuiteCountCol());
        domainCountQuery.addSelectColumn(this.getHybridCountCol());
        domainCountQuery.addSelectColumn(this.getZohoDirCountCol());
        domainCountQuery.addSelectColumn(this.getTotalDirCountCol());
        final Column custCol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        domainCountQuery.addSelectColumn(custCol);
        if (customerID != null) {
            domainCountQuery.setCriteria(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0));
        }
        domainCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custCol))));
        return domainCountQuery;
    }
    
    private SelectQuery getPeopleDomainQuery(final Long customerID) {
        final Column custCol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        final SelectQuery peopleIntegCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        peopleIntegCountQuery.addJoin(new Join("CustomerInfo", "CustomerParams", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        peopleIntegCountQuery.addSelectColumn(custCol);
        peopleIntegCountQuery.addSelectColumn(this.getPeopeCountCol());
        if (customerID != null) {
            peopleIntegCountQuery.setCriteria(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0));
        }
        peopleIntegCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custCol))));
        return peopleIntegCountQuery;
    }
    
    private SelectQuery getDomainObjDetailsQuery(final Long customerID) {
        final SelectQuery domainCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        domainCountQuery.addJoin(new Join("CustomerInfo", "Resource", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        domainCountQuery.addJoin(new Join("Resource", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        domainCountQuery.addSelectColumn(this.getTotalUserCountCol());
        domainCountQuery.addSelectColumn(this.getTotalGroupCountCol());
        domainCountQuery.addSelectColumn(this.getDirectoryUserCountCol());
        domainCountQuery.addSelectColumn(this.getDirectoryGroupCountCol());
        domainCountQuery.addSelectColumn(this.getDomainDeletedObjectCountCol());
        final Column custCol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        domainCountQuery.addSelectColumn(custCol);
        if (customerID != null) {
            domainCountQuery.setCriteria(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0));
        }
        domainCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custCol))));
        return domainCountQuery;
    }
    
    private SelectQuery getDirectoryDeletedObjCountQuery(final Long customerID) {
        final SelectQuery domainCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        domainCountQuery.addJoin(new Join("CustomerInfo", "Resource", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        domainCountQuery.addJoin(new Join("Resource", "DirResRel", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        domainCountQuery.addJoin(new Join("DirResRel", "DirObjRegIntVal", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
        domainCountQuery.addSelectColumn(this.getDirectoryDeletedObjCountCol());
        final Column custCol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        domainCountQuery.addSelectColumn(custCol);
        if (customerID != null) {
            domainCountQuery.setCriteria(new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0));
        }
        domainCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custCol))));
        return domainCountQuery;
    }
    
    private void updateErrorCount(final Connection connection, final Long customerID) throws Exception {
        final Column custCol = Column.getColumn("DMDomain", "CUSTOMER_ID", "dmCustIDcol");
        final CaseExpression ce = new CaseExpression("DOMAIN_SYNC_FAILED_COUNT");
        ce.addWhen(new Criteria(Column.getColumn("DMDomainSyncDetails", "FETCH_STATUS"), (Object)921, 1), (Object)Column.getColumn("DMDomainSyncDetails", "DM_DOMAIN_ID"));
        final Column errCountCol = (Column)Column.createFunction("COUNT", new Object[] { Column.createFunction("DISTINCT", new Object[] { ce }) });
        errCountCol.setType(4);
        errCountCol.setColumnAlias("DOMAIN_SYNC_FAILED_COUNT");
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DMDomain"));
        if (customerID != null) {
            selectQuery.setCriteria(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0));
        }
        selectQuery.addJoin(new Join("DMDomain", "DMDomainSyncDetails", new String[] { "DOMAIN_ID" }, new String[] { "DM_DOMAIN_ID" }, 2));
        selectQuery.addSelectColumn(custCol);
        selectQuery.addSelectColumn(errCountCol);
        selectQuery.setGroupByClause(new GroupByClause((List)Arrays.asList(custCol)));
        final DerivedTable innerDT = new DerivedTable("innerDT", (Query)selectQuery);
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("DirectoryMetrics");
        updateQuery.addJoin(new Join(Table.getTable("DirectoryMetrics"), (Table)innerDT, new Criteria(new Column(innerDT.getTableAlias(), errCountCol.getColumnAlias()), (Object)0, 0).and(new Criteria(Column.getColumn("DirectoryMetrics", "CUSTOMER_ID"), (Object)new Column(innerDT.getTableAlias(), custCol.getColumnAlias()), 0)), 2));
        updateQuery.setUpdateColumn("VALUE", (Object)0);
        updateQuery.setCriteria(new Criteria(Column.getColumn("DirectoryMetrics", "KEY"), (Object)"*ERR*", 2, false));
        DirectoryQueryutil.getInstance().executeUpdateQuery(connection, updateQuery, false);
    }
    
    void increment(JSONObject incrementDetails) {
        try {
            if (incrementDetails != null && !incrementDetails.isEmpty()) {
                incrementDetails = this.handleSyncDuration(incrementDetails);
                boolean increment = true;
                if (incrementDetails.containsKey((Object)"DirectoryMetrics")) {
                    increment = Boolean.valueOf(String.valueOf(incrementDetails.get((Object)"DirectoryMetrics")));
                }
                this.addOrUpdate(incrementDetails, increment);
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    public void enQueueIncrementTask(final Long customerID, final String key, final int value) {
        if (customerID != null) {
            final Properties props = new Properties();
            ((Hashtable<String, Long>)props).put("CUSTOMER_ID", customerID);
            final JSONObject qData = new JSONObject();
            qData.put((Object)key, (Object)value);
            qData.put((Object)"TASK_TYPE", (Object)"INCREMENT_METRICS");
            try {
                DirectoryUtil.getInstance().addTaskToQueue("adProc-task", props, qData);
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void updateDirTrackingData(final JSONObject custDirDetails) {
        if (custDirDetails.containsKey((Object)"DIR_USER_COUNT") && custDirDetails.containsKey((Object)"DIR_GROUP_COUNT") && custDirDetails.containsKey((Object)"TOTAL_USER_COUNT") && custDirDetails.containsKey((Object)"TOTAL_GROUP_COUNT")) {
            try {
                final Long customerID = (Long)custDirDetails.get((Object)"CUSTOMER_ID");
                final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
                dirProdImplRequest.args = new Object[] { customerID, custDirDetails };
                dirProdImplRequest.eventType = IdpEventConstants.DO_PROD_SPECIFIC_ME_TRACKING;
                final JSONObject prodProcessedDetails = (JSONObject)DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
                if (prodProcessedDetails != null) {
                    custDirDetails.putAll((Map)prodProcessedDetails);
                }
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
            final int dirUserCount = Integer.valueOf(String.valueOf(custDirDetails.get((Object)"DIR_USER_COUNT")));
            final int dirGroupCount = Integer.valueOf(String.valueOf(custDirDetails.get((Object)"DIR_GROUP_COUNT")));
            final int totalUserCount = Integer.valueOf(String.valueOf(custDirDetails.get((Object)"TOTAL_USER_COUNT")));
            final int totalGroupCount = Integer.valueOf(String.valueOf(custDirDetails.get((Object)"TOTAL_GROUP_COUNT")));
            custDirDetails.put((Object)"DIR_USER_RANK", (Object)this.getRank(dirUserCount));
            custDirDetails.put((Object)"DIR_GROUP_RANK", (Object)this.getRank(dirGroupCount));
            custDirDetails.put((Object)"DIR_USER_SHARE", (Object)this.getPercentage(dirUserCount, totalUserCount));
            custDirDetails.put((Object)"GROUP_DIR_SHARE", (Object)this.getPercentage(dirGroupCount, totalGroupCount));
        }
        this.addOrUpdate(custDirDetails, false);
    }
    
    private void updateDirTrackingData(final Connection connection, final SelectQuery domainDetailsQuery) throws Exception {
        String query = RelationalAPI.getInstance().getSelectSQL((Query)domainDetailsQuery);
        query = DirectoryMickeyIssueHandler.getInstance().rectifyQuery(query);
        final JSONArray domainDetails = IdpsUtil.executeSelectQuery(connection, query, domainDetailsQuery.getSelectColumns());
        for (int i = 0; domainDetails != null && i < domainDetails.size(); ++i) {
            final JSONObject custDirDetails = (JSONObject)domainDetails.get(i);
            this.updateDirTrackingData(custDirDetails);
        }
    }
    
    private void updateDirTrackingDetails(final Connection connection, final Long customerID) {
        try {
            this.initMetricsTable(customerID);
            this.updateErrorCount(connection, customerID);
            this.updateOPcountDetails(connection, customerID);
            this.updateDirTrackingData(connection, this.getPeopleDomainQuery(customerID));
            this.updateDirTrackingData(connection, this.getDomainDetailsQuery(customerID));
            this.updateDirTrackingData(connection, this.getSameEmailCountQuery(customerID));
            this.updateDirTrackingData(connection, this.getDomainObjDetailsQuery(customerID));
            this.updateDirTrackingData(connection, this.getDuplicatedDomainCountQuery(customerID));
            this.updateDirTrackingData(connection, this.getDirectoryDeletedObjCountQuery(customerID));
            this.updateDirTrackingData(connection, this.getDuplicatedUserResourceCountQuery(customerID));
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    private void updateOPcountDetails(final Connection connection, final Long customerID) throws Exception {
        final SelectQuery selectQuery = IdpsFactoryProvider.getIdpsProdEnvAPI().getOPcountQuery(customerID);
        String opQueryStr = RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
        opQueryStr = DirectoryMickeyIssueHandler.getInstance().rectifyQuery(opQueryStr);
        final JSONArray res = IdpsUtil.executeSelectQuery(connection, opQueryStr, selectQuery.getSelectColumns());
        for (int i = 0; i < res.size(); ++i) {
            final JSONObject curCustOPdetails = (JSONObject)res.get(i);
            final Long curCustID = Long.valueOf(String.valueOf(curCustOPdetails.get((Object)"CUSTOMER_ID")));
            final Integer opCount = Integer.valueOf(String.valueOf(curCustOPdetails.get((Object)"OP_COUNT")));
            final JSONObject custDirDetails = new JSONObject();
            custDirDetails.put((Object)"CUSTOMER_ID", (Object)curCustID);
            custDirDetails.put((Object)"OP_COUNT", (Object)opCount);
            this.addOrUpdate(custDirDetails, false);
            final int curOPcount = DirectoryUtil.getInstance().getZDopCount(customerID);
            if (curOPcount != opCount) {
                ApiFactoryProvider.getCacheAccessAPI().putCache(customerID + "_" + "OP_COUNT", (Object)opCount, 2);
            }
        }
    }
    
    void updateDirTrackingDetails(final Long customerID) {
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            this.updateDirTrackingDetails(connection, customerID);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex);
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                IDPSlogger.ERR.log(Level.SEVERE, null, ex2);
            }
        }
    }
    
    private Row getRow(final DataObject dobj, final String key) throws DataAccessException {
        return dobj.getRow("DirectoryMetrics", new Criteria(Column.getColumn("DirectoryMetrics", "KEY"), (Object)key, 0, false));
    }
    
    private JSONObject handleSyncDuration(final JSONObject incrementDetails) throws DataAccessException {
        incrementDetails.put((Object)"DirectoryMetrics", (Object)true);
        if (incrementDetails.containsKey((Object)"SYNC_ENGINE_OPS_DURATION")) {
            final Long customerID = Long.valueOf(String.valueOf(incrementDetails.get((Object)"CUSTOMER_ID")));
            final long duration = Long.valueOf(String.valueOf(incrementDetails.get((Object)"SYNC_ENGINE_OPS_DURATION")));
            final int syncDurationRank = this.getDurationRank(duration);
            final DataObject dobj = IdpsUtil.getPersistenceLite().get("DirectoryMetrics", new Criteria(Column.getColumn("DirectoryMetrics", "CUSTOMER_ID"), (Object)customerID, 0));
            long successSyncCount = (long)DirectoryUtil.getInstance().extractValue(this.getRow(dobj, "SUCCESS_SYNC_COUNT"), "VALUE", 0L);
            ++successSyncCount;
            incrementDetails.put((Object)"SUCCESS_SYNC_COUNT", (Object)successSyncCount);
            incrementDetails.put((Object)"SYNC_ENGINE_OPS_DURATION", (Object)syncDurationRank);
            incrementDetails.put((Object)"DirectoryMetrics", (Object)false);
        }
        return incrementDetails;
    }
    
    static {
        DirectoryMetricsDataHandler.directoryMetricsDataHandler = null;
    }
}
