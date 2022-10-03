package com.adventnet.sym.server.admin;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import java.util.logging.Logger;

public class CustomGroupingHandler extends com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler
{
    public static final int CUSTOM_GROUP_TYPE_USER = 2;
    public static final int CUSTOM_GROUP_CTGY_STATIC_UNIQUE = 5;
    public static final int ALL_COMPUTER_GROUP = 10;
    public static final int CUSTOM_GROUP_ADDED = 1;
    public static final int CUSTOM_GROUP_MODIFIED = 2;
    public static final int CUSTOM_GROUP_DELETED = 3;
    public static final String DUMMY_CRITERIA_GROUP_DOMAIN = "CRITERIA_GROUP";
    public static final String DUMMY_STATIC_UNIQUE_CG = "All Features Group";
    public static final String DUMMY_DOMAIN = "DUMMYDOMAIN";
    private static Logger cgLogger;
    public static final String ALL_COMPUTERS_GROUP = "All Computers Group";
    public static final String ALL_USERS_GROUP = "All Users Group";
    
    public static Long getAllManagedGroupResourceId(final Long customerId, final int groupType) {
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
        boolean flagSet = false;
        try {
            if (skipCustomerFilter != null && skipCustomerFilter.equals("false")) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("true");
                flagSet = true;
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            selectQuery.addJoin(new Join("Resource", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria custCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria AllCategoryCrit = new Criteria(Column.getColumn("CustomGroup", "GROUP_CATEGORY"), (Object)10, 0);
            final Criteria computerTypeCrit = new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)groupType, 0);
            selectQuery.setCriteria(custCriteria.and(AllCategoryCrit).and(computerTypeCrit));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            CustomGroupingHandler.out.log(Level.FINE, "Select Query to getAllManagedGroupResourceId : {0}", selectQuery);
            final DataObject dobj = DataAccess.get(selectQuery);
            CustomGroupingHandler.out.log(Level.FINE, "DataObject Obtained : {0}", dobj);
            if (!dobj.isEmpty() && dobj.containsTable("CustomGroup")) {
                final Row row = dobj.getFirstRow("CustomGroup");
                if (row != null) {
                    return (Long)row.get("RESOURCE_ID");
                }
            }
        }
        catch (final Exception ex) {
            CustomGroupingHandler.out.log(Level.WARNING, ex, () -> "Exception occured while getting All Manged group Resource for customer " + customerId);
        }
        finally {
            if (flagSet) {
                CustomerInfoThreadLocal.setSkipCustomerFilter("false");
            }
        }
        return null;
    }
    
    static {
        CustomGroupingHandler.cgLogger = Logger.getLogger("CustomGroupLogger");
    }
}
