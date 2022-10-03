package com.me.mdm.server.util;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.util.logging.Logger;

public class MDMCustomerParamsHandler
{
    private static Logger logger;
    
    public void addOrUpdateParameter(final String paramName, final String paramValue, final long customerID) throws Exception {
        CustomerParamsHandler.getInstance().addOrUpdateParameter(paramName, paramValue, customerID);
    }
    
    public void addOrUpdateParameters(final JSONObject customerParamsValue, final long customerID) throws Exception {
        try {
            if (customerParamsValue != null) {
                final Iterator itr = customerParamsValue.keys();
                final ArrayList params = new ArrayList();
                while (itr.hasNext()) {
                    params.add(itr.next());
                }
                if (!params.isEmpty()) {
                    final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomerParams"));
                    sQuery.addSelectColumn(Column.getColumn("CustomerParams", "CUSTOMER_PARAM_ID"));
                    sQuery.addSelectColumn(Column.getColumn("CustomerParams", "PARAM_NAME"));
                    sQuery.addSelectColumn(Column.getColumn("CustomerParams", "PARAM_VALUE"));
                    sQuery.addSelectColumn(Column.getColumn("CustomerParams", "CUSTOMER_ID"));
                    final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0);
                    final Criteria paramsCrietria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)params.toArray(), 8, false);
                    sQuery.setCriteria(customerCriteria.and(paramsCrietria));
                    final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
                    final DataObject emptyDobj = (DataObject)new WritableDataObject();
                    for (final Object param : params) {
                        final Criteria criteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), param, 0);
                        if (dobj.getRow("CustomerParams", criteria) == null) {
                            final Row r = new Row("CustomerParams");
                            r.set("PARAM_NAME", param);
                            r.set("PARAM_VALUE", customerParamsValue.get((String)param));
                            r.set("CUSTOMER_ID", (Object)customerID);
                            emptyDobj.addRow(r);
                        }
                        else {
                            final Row r = dobj.getRow("CustomerParams", criteria);
                            r.set("PARAM_VALUE", customerParamsValue.get((String)param));
                            dobj.updateRow(r);
                        }
                    }
                    MDMUtil.getPersistence().update(dobj);
                    MDMUtil.getPersistence().update(emptyDobj);
                }
            }
        }
        catch (final Exception ex) {
            MDMCustomerParamsHandler.logger.log(Level.SEVERE, "Exception in addOrUpdateParameters :{0}", ex);
            throw ex;
        }
    }
    
    public void incrementParameters(final JSONObject customerParamsValue, final long customerID) throws Exception {
        if (customerParamsValue != null) {
            final Iterator itr = customerParamsValue.keys();
            final ArrayList params = new ArrayList();
            while (itr.hasNext()) {
                params.add(itr.next());
            }
            if (!params.isEmpty()) {
                final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomerParams"));
                sQuery.addSelectColumn(Column.getColumn("CustomerParams", "CUSTOMER_PARAM_ID"));
                sQuery.addSelectColumn(Column.getColumn("CustomerParams", "PARAM_NAME"));
                sQuery.addSelectColumn(Column.getColumn("CustomerParams", "PARAM_VALUE"));
                sQuery.addSelectColumn(Column.getColumn("CustomerParams", "CUSTOMER_ID"));
                final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerID, 0);
                final Criteria paramsCrietria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)params.toArray(), 8, false);
                sQuery.setCriteria(customerCriteria.and(paramsCrietria));
                final DataObject dobj = MDMUtil.getPersistence().get(sQuery);
                final DataObject emptyDobj = (DataObject)new WritableDataObject();
                for (final Object param : params) {
                    try {
                        final Criteria criteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), param, 0);
                        if (dobj.getRow("CustomerParams", criteria) == null) {
                            final Row r = new Row("CustomerParams");
                            r.set("PARAM_NAME", param);
                            r.set("PARAM_VALUE", customerParamsValue.get((String)param));
                            r.set("CUSTOMER_ID", (Object)customerID);
                            emptyDobj.addRow(r);
                        }
                        else {
                            final Row r = dobj.getRow("CustomerParams", criteria);
                            r.set("PARAM_VALUE", (Object)(Integer.parseInt((String)r.get("PARAM_VALUE")) + customerParamsValue.getInt((String)param)));
                            dobj.updateRow(r);
                        }
                    }
                    catch (final Exception ex) {
                        MDMCustomerParamsHandler.logger.log(Level.SEVERE, ex, () -> "Exception in updating value for param " + o);
                        throw ex;
                    }
                }
                MDMUtil.getPersistence().update(dobj);
                MDMUtil.getPersistence().update(emptyDobj);
            }
        }
    }
    
    public String getParameterValue(final String paramName, final long customerID) throws Exception {
        return CustomerParamsHandler.getInstance().getParameterValue(paramName, customerID);
    }
    
    static {
        MDMCustomerParamsHandler.logger = Logger.getLogger("MDMLogger");
    }
}
