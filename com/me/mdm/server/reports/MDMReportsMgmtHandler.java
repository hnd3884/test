package com.me.mdm.server.reports;

import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.reports.ReportUtil;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMReportsMgmtHandler
{
    private static final String PRODUCTS = "product";
    private static final String MODEL_NAME = "model_name";
    private static final String MODEL = "model";
    private static final Logger LOGGER;
    private static MDMReportsMgmtHandler mdmReportsMgmtHandler;
    
    public static MDMReportsMgmtHandler getInstance() {
        if (MDMReportsMgmtHandler.mdmReportsMgmtHandler == null) {
            MDMReportsMgmtHandler.mdmReportsMgmtHandler = new MDMReportsMgmtHandler();
        }
        return MDMReportsMgmtHandler.mdmReportsMgmtHandler;
    }
    
    public List getReportParamValue(final int reportId, final String paramName, final Long customerId, final JSONObject optionalParams) {
        final String lowerCase = paramName.toLowerCase();
        switch (lowerCase) {
            case "product": {
                return this.getProducts(customerId, reportId);
            }
            case "model_name": {
                final String model_name = optionalParams.optString("model_name", "");
                return this.getModelValues(customerId, model_name, reportId, "model_name");
            }
            case "model": {
                final String model_name = optionalParams.optString("model_name", "");
                return this.getModelValues(customerId, model_name, reportId, "model");
            }
            default: {
                return null;
            }
        }
    }
    
    private List<String> getProducts(final Long customerId, final int reportId) {
        List<String> productList = null;
        switch (reportId) {
            case 40301: {
                productList = ReportUtil.getInstance().getProductName(customerId, 2);
                break;
            }
            case 40303: {
                productList = ReportUtil.getInstance().getProductName(customerId, 1);
                break;
            }
            default: {
                productList = ReportUtil.getInstance().getProductNameList(customerId);
                break;
            }
        }
        return productList;
    }
    
    private List<String> getModelValues(final Long customerId, final String model_name, final int reportId, final String param) {
        final List modelList = new ArrayList();
        Criteria criteria = null;
        if (!MDMStringUtils.isEmpty(model_name)) {
            criteria = new Criteria(Column.getColumn("MdModelInfo", "MODEL_NAME"), (Object)model_name, 0, false);
            criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerId, 0));
        }
        else {
            final List productList = this.getProducts(customerId, reportId);
            if (productList != null && !productList.isEmpty()) {
                criteria = new Criteria(Column.getColumn("MdModelInfo", "PRODUCT_NAME"), (Object)productList.toArray(), 8);
            }
        }
        if (criteria != null && param != null) {
            try {
                Column modelColumn = null;
                switch (param) {
                    case "model": {
                        modelColumn = Column.getColumn("MdModelInfo", "MODEL").distinct();
                        break;
                    }
                    default: {
                        modelColumn = Column.getColumn("MdModelInfo", "MODEL_NAME").distinct();
                        break;
                    }
                }
                modelColumn.setColumnAlias("model");
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdModelInfo"));
                selectQuery.addJoin(new Join("MdModelInfo", "MdDeviceInfo", new String[] { "MODEL_ID" }, new String[] { "MODEL_ID" }, 2));
                selectQuery.addJoin(new Join("MdDeviceInfo", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                selectQuery.addSelectColumn(modelColumn);
                selectQuery.setCriteria(criteria);
                final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
                if (dmDataSetWrapper != null) {
                    while (dmDataSetWrapper.next()) {
                        final String model = (String)dmDataSetWrapper.getValue(modelColumn.getColumnAlias());
                        if (!MDMUtil.isStringEmpty(model)) {
                            modelList.add(model);
                        }
                    }
                }
            }
            catch (final Exception e) {
                MDMReportsMgmtHandler.LOGGER.log(Level.WARNING, "Cannot fetch model name for product", e);
            }
        }
        return modelList;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
