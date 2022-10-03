package com.me.mdm.onpremise.server.integration;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import org.json.simple.JSONObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.JSONArray;
import java.util.logging.Logger;

public class IntegrationProductUtil
{
    public static final int SUCCESS = 100;
    public static final int RELATION_EXISTS = 101;
    public static final int FAILURE = 102;
    private Logger logger;
    
    public IntegrationProductUtil() {
        this.logger = Logger.getLogger(IntegrationProductUtil.class.getSimpleName());
    }
    
    public static IntegrationProductUtil getNewInstance() {
        return new IntegrationProductUtil();
    }
    
    public JSONArray getIntegrationProductsList() throws DataAccessException, JSONException {
        final JSONArray products = new JSONArray();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IntegrationProduct"));
        selectQuery.addSelectColumn(Column.getColumn("IntegrationProduct", "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        final Iterator<Row> rows = dataObject.getRows("IntegrationProduct");
        while (rows.hasNext()) {
            final Row row = rows.next();
            final JSONObject product = new JSONObject();
            product.put((Object)"PRODUCT_ID", row.get("PRODUCT_ID"));
            product.put((Object)"PRODUCT_NAME", row.get("PRODUCT_NAME"));
            products.add((Object)product);
        }
        try {
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MDMMigration")) {
                for (int index = 0; index < products.size(); ++index) {
                    if (((JSONObject)products.get(index)).get((Object)"PRODUCT_NAME").toString().equalsIgnoreCase("MDMMigration")) {
                        products.remove(index);
                    }
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception is checking the MDM Migration Product in INTEGRATIONPRODUCT list", ex);
        }
        return products;
    }
    
    public int linkProductAndService(final Long productId, final Long serviceId) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("IntegrationProductServiceRel"));
            selectQuery.addSelectColumn(Column.getColumn("IntegrationProductServiceRel", "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("IntegrationProductServiceRel", "PRODUCT_ID"), (Object)productId, 0).and(new Criteria(Column.getColumn("IntegrationProductServiceRel", "SERVICE_ID"), (Object)serviceId, 0)));
            final DataObject dataObject = DataAccess.get(selectQuery);
            if (dataObject.isEmpty()) {
                final DataObject newDataObj = SyMUtil.getPersistence().constructDataObject();
                final Row row = new Row("IntegrationProductServiceRel");
                row.set("SERVICE_ID", (Object)serviceId);
                row.set("PRODUCT_ID", (Object)productId);
                newDataObj.addRow(row);
                SyMUtil.getPersistence().add(newDataObj);
                return 100;
            }
            return 101;
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, "Error Occurred in linkProductAndService()", (Throwable)e);
            return 102;
        }
    }
    
    public String getIntegrationProductName(final Long integProductId) {
        String integProductName = null;
        try {
            integProductName = (String)DBUtil.getValueFromDB("IntegrationProduct", "PRODUCT_ID", (Object)integProductId, "PRODUCT_NAME");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Error Occurred in getIntegrationProductName()", e);
        }
        return integProductName;
    }
}
