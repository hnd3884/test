package com.adventnet.db.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.QueryConstructionException;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.ds.query.Query;

public class SQLGeneratorForMSP
{
    private static final String EXCLUDE_CUS_TABLE = "CollnToCustomerRel";
    private static SQLGeneratorForMSP objSQLGeneratorForMSP;
    
    public static SQLGeneratorForMSP getInstance() {
        if (SQLGeneratorForMSP.objSQLGeneratorForMSP == null) {
            SQLGeneratorForMSP.objSQLGeneratorForMSP = new SQLGeneratorForMSP();
        }
        return SQLGeneratorForMSP.objSQLGeneratorForMSP;
    }
    
    public Query getCustomerCriteriaAppendedQuery(Query query) throws QueryConstructionException {
        try {
            final String isClientCall = CustomerInfoThreadLocal.getIsClientCall();
            if (isClientCall == null) {
                return query;
            }
            final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
            if (skipCustomerFilter != null && skipCustomerFilter.equals("true")) {
                return query;
            }
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            final Long[] customers = CustomerInfoUtil.getInstance().getCustomers();
            CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            query = this.appendCustomerCriteriaToQuery(query, customers);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new QueryConstructionException("", (Throwable)ex);
        }
        return query;
    }
    
    public Query appendCustomerCriteriaToQuery(final Query query, final Long[] customers) throws QueryConstructionException {
        try {
            boolean dcChange = false;
            if (customers != null && customers.length > 0) {
                if (customers.length == 1 && customers[0] == -1L) {
                    return query;
                }
                if (query instanceof SelectQuery) {
                    final SelectQuery sq = (SelectQuery)query;
                    final List tableList = sq.getTableList();
                    final List dcTableList = this.getDCTableList();
                    final Iterator it1 = tableList.iterator();
                    Criteria custCrit = null;
                    while (it1.hasNext()) {
                        final Table tbl = it1.next();
                        final String tableName = tbl.getTableName();
                        if (dcTableList.contains(tableName)) {
                            dcChange = true;
                            final Column column = Column.getColumn(tableName, "CUSTOMER_ID");
                            if (customers.length == 1) {
                                custCrit = new Criteria(column, (Object)customers[0], 0);
                            }
                            else {
                                custCrit = new Criteria(column, (Object)customers, 8);
                            }
                            if ("CollnToCustomerRel".equals(tableName)) {
                                final List joins = sq.getJoins();
                                for (final Join join : joins) {
                                    final String refTableName = join.getReferencedTableName();
                                    if (refTableName.equals(tableName) && join.getJoinType() == 1) {
                                        final Criteria cri = new Criteria(column, (Object)null, 0);
                                        custCrit = custCrit.or(cri);
                                    }
                                }
                                break;
                            }
                            break;
                        }
                    }
                    if (dcChange) {
                        Criteria crit = sq.getCriteria();
                        if (crit != null) {
                            crit = crit.and(custCrit);
                        }
                        else {
                            crit = custCrit;
                        }
                        sq.setCriteria(crit);
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new QueryConstructionException("", (Throwable)ex);
        }
        return query;
    }
    
    private List getDCTableList() {
        final List dcTable = new ArrayList();
        dcTable.add("LoginUserCustomerMapping");
        dcTable.add("CustomerEventLog");
        dcTable.add("Resource");
        dcTable.add("ResourceDomain");
        dcTable.add("BranchOfficeDetails");
        dcTable.add("DCSelectedComputer");
        dcTable.add("CollnToCustomerRel");
        dcTable.add("MetaDataParam");
        dcTable.add("ClientMetaData");
        dcTable.add("CmptsAddedForShutdownTask");
        dcTable.add("MasterRepository");
        dcTable.add("MasterMetaDataParams");
        dcTable.add("InvSW");
        dcTable.add("InvHW");
        dcTable.add("InvSWCategory");
        dcTable.add("InvSWLicense");
        dcTable.add("InvCompleteSummary");
        dcTable.add("AppDefDetailsToCustomerRel");
        dcTable.add("InvCertificates");
        return dcTable;
    }
    
    static {
        SQLGeneratorForMSP.objSQLGeneratorForMSP = null;
    }
}
