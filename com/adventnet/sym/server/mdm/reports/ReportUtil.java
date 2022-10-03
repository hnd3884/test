package com.adventnet.sym.server.mdm.reports;

import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ReportUtil
{
    public Logger logger;
    private static ReportUtil reportUtil;
    
    public ReportUtil() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static ReportUtil getInstance() {
        if (ReportUtil.reportUtil == null) {
            ReportUtil.reportUtil = new ReportUtil();
        }
        return ReportUtil.reportUtil;
    }
    
    @Deprecated
    public ArrayList getAllProductNames(final Long customerId) {
        return this.getProductNames(customerId, null);
    }
    
    @Deprecated
    public ArrayList getProductNames(final Long customerId, final Integer platFormType) {
        final ArrayList productNames = new ArrayList();
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
            final Join managedDeviceJoin = new Join("Resource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join deviceInfoJoin = new Join("ManagedDevice", "MdDeviceInfo", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            final Join modelJoin = new Join("MdDeviceInfo", "MdModelInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 1);
            sq.addJoin(managedDeviceJoin);
            sq.addJoin(deviceInfoJoin);
            sq.addJoin(modelJoin);
            Criteria criteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            if (platFormType != null) {
                final Criteria platFormCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platFormType, 0);
                criteria = criteria.and(platFormCriteria);
            }
            sq.setCriteria(criteria);
            sq.addSelectColumn(new Column("MdModelInfo", "*"));
            final DataObject dataObject = MDMUtil.getPersistence().get(sq);
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("MdModelInfo");
                while (iterator.hasNext()) {
                    final Row managedDeviceRow = iterator.next();
                    final String sProductName = (String)managedDeviceRow.get("MODEL_NAME");
                    if (!productNames.contains(sProductName)) {
                        productNames.add(sProductName);
                    }
                }
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.WARNING, "Exception in getAllProductNames method : {0}", (Throwable)ex);
        }
        return productNames;
    }
    
    public List<String> getProductName(final Long customerId, final Integer platFormType) {
        final List<String> productNames = new ArrayList<String>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdModelInfo"));
            selectQuery.addJoin(new Join("MdModelInfo", "MdDeviceInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
            selectQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria criteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerId, 0);
            if (platFormType != null) {
                final Criteria platFormCriteria = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platFormType, 0);
                criteria = criteria.and(platFormCriteria);
            }
            selectQuery.setCriteria(criteria);
            final Column modelNameCol = new Column("MdModelInfo", "PRODUCT_NAME").distinct();
            modelNameCol.setColumnAlias("productname");
            selectQuery.addSelectColumn(modelNameCol);
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            if (dmDataSetWrapper != null) {
                while (dmDataSetWrapper.next()) {
                    final String sProductName = (String)dmDataSetWrapper.getValue("productname");
                    productNames.add(sProductName);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in getAllProductNames method : {0}", ex);
        }
        return productNames;
    }
    
    public List<String> getProductNameList(final Long customerId) {
        return this.getProductName(customerId, null);
    }
    
    static {
        ReportUtil.reportUtil = null;
    }
}
