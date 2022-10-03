package com.me.devicemanagement.framework.server.csv;

import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.Set;
import java.util.Map;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.simple.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerParamsHandler
{
    private static Logger logger;
    private static CustomerParamsHandler customerParamsHandler;
    
    private CustomerParamsHandler() {
    }
    
    public static CustomerParamsHandler getInstance() {
        if (CustomerParamsHandler.customerParamsHandler == null) {
            CustomerParamsHandler.customerParamsHandler = new CustomerParamsHandler();
        }
        return CustomerParamsHandler.customerParamsHandler;
    }
    
    public void addOrUpdateParameter(final String paramName, final String paramValue, final long customerID) throws Exception {
        try {
            CustomerParamsHandler.logger.log(Level.INFO, "In addOrUpdateParameter input paramName: {0} paramValue: {1}", new Object[] { paramName, paramValue });
            final Criteria c = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)paramName, 0, false));
            DataObject dobj = SyMUtil.getPersistence().get("CustomerParams", c);
            if (dobj.isEmpty()) {
                dobj = (DataObject)new WritableDataObject();
                final Row r = new Row("CustomerParams");
                r.set("PARAM_NAME", (Object)paramName);
                r.set("PARAM_VALUE", (Object)paramValue);
                r.set("CUSTOMER_ID", (Object)customerID);
                dobj.addRow(r);
            }
            else {
                final Row r = dobj.getRow("CustomerParams");
                r.set("PARAM_NAME", (Object)paramName);
                r.set("PARAM_VALUE", (Object)paramValue);
                r.set("CUSTOMER_ID", (Object)customerID);
                dobj.updateRow(r);
            }
            SyMUtil.getPersistence().update(dobj);
        }
        catch (final Exception ex) {
            CustomerParamsHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateParameter :{0}", ex);
            throw ex;
        }
    }
    
    public void addOrUpdateParameters(final JSONObject jsonObj, final long customerID) throws Exception {
        try {
            CustomerParamsHandler.logger.log(Level.INFO, "In addOrUpdateParameters input json: {0}", jsonObj.toJSONString());
            Criteria c = null;
            final Set<String> keySet = jsonObj.keySet();
            final String[] paramNames = keySet.toArray(new String[0]);
            c = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)paramNames, 8, false);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomerParams"));
            sQuery.addSelectColumn(Column.getColumn("CustomerParams", "CUSTOMER_PARAM_ID"));
            sQuery.addSelectColumn(Column.getColumn("CustomerParams", "PARAM_NAME"));
            sQuery.addSelectColumn(Column.getColumn("CustomerParams", "PARAM_VALUE"));
            sQuery.addSelectColumn(Column.getColumn("CustomerParams", "CUSTOMER_ID"));
            if (c != null) {
                sQuery.setCriteria(new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0).and(c));
            }
            else {
                sQuery.setCriteria(new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0));
            }
            final DataObject dobj = SyMUtil.getPersistence().get(sQuery);
            final DataObject emptyDobj = (DataObject)new WritableDataObject();
            final Set<Map.Entry<String, String>> entrySet = jsonObj.entrySet();
            Iterator<Map.Entry<String, String>> i = entrySet.iterator();
            i = entrySet.iterator();
            while (i.hasNext()) {
                final Map.Entry<String, String> element = i.next();
                final Criteria criteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)element.getKey(), 0, false);
                if (dobj.getRow("CustomerParams", criteria) == null) {
                    final Row r = new Row("CustomerParams");
                    r.set("PARAM_NAME", (Object)element.getKey());
                    r.set("PARAM_VALUE", (Object)element.getValue());
                    r.set("CUSTOMER_ID", (Object)customerID);
                    emptyDobj.addRow(r);
                }
                else {
                    final Row r = dobj.getRow("CustomerParams", criteria);
                    r.set("PARAM_NAME", (Object)element.getKey());
                    r.set("PARAM_VALUE", (Object)element.getValue());
                    dobj.updateRow(r);
                }
            }
            SyMUtil.getPersistence().update(dobj);
            SyMUtil.getPersistence().update(emptyDobj);
        }
        catch (final Exception ex) {
            CustomerParamsHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateParameters :{0}", ex);
            throw ex;
        }
    }
    
    public String getParameterValue(final String paramName, final long customerID) throws Exception {
        try {
            final Criteria c = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)paramName, 0, false));
            final DataObject dobj = SyMUtil.getPersistence().get("CustomerParams", c);
            final Row r = dobj.getRow("CustomerParams");
            if (r == null) {
                return null;
            }
            return (String)r.get("PARAM_VALUE");
        }
        catch (final Exception ex) {
            CustomerParamsHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateParameters :{0}", ex);
            throw ex;
        }
    }
    
    static {
        CustomerParamsHandler.logger = Logger.getLogger("MDMLogger");
        CustomerParamsHandler.customerParamsHandler = null;
    }
}
