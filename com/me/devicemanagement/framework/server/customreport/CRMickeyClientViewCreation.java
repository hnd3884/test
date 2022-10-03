package com.me.devicemanagement.framework.server.customreport;

import java.util.List;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import java.util.Date;
import com.me.devicemanagement.framework.server.util.DBConstants;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public class CRMickeyClientViewCreation
{
    public static Logger logger;
    public static final String I18N_RECORD_NOT_FOUND = "dc.rep.customReport.rec_not_found";
    
    public int createMickeyView(final SelectQuery query, final CustomReportDetails customReportDetails, final boolean saveView, final Long customerID, final String dbType, final Properties viewConfigProperties) {
        try {
            final DataObject queryDO = this.storeSelectQuery();
            return this.storeMCViewDetails(queryDO, query, customReportDetails, saveView, customerID, dbType, viewConfigProperties);
        }
        catch (final Exception e) {
            CRMickeyClientViewCreation.logger.log(Level.WARNING, "Exception from createMickeyView ", e);
            return 1101;
        }
    }
    
    public DataObject storeSelectQuery() {
        try {
            final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
            final DataObject queryDO = DataAccess.constructDataObject();
            final Row selectQueryRow = this.addSelectQueryRow();
            queryDO.addRow(selectQueryRow);
            persistence.add(queryDO);
            return queryDO;
        }
        catch (final Exception e) {
            CRMickeyClientViewCreation.logger.log(Level.WARNING, "Exception from storeSelectQuery ", e);
            return null;
        }
    }
    
    @Deprecated
    public int storeMCViewDetails(final DataObject queryDO, final SelectQuery query, final CustomReportDetails customReportDetails, final boolean saveView, final Long customerID, final String dbType, final Properties viewConfigProp) {
        return this.storeMCViewDetails(queryDO, query, customReportDetails, saveView, customerID, dbType, viewConfigProp, true);
    }
    
    public int storeMCViewDetails(final DataObject queryDO, final SelectQuery query, final CustomReportDetails customReportDetails, final boolean saveView, final Long customerID, final String dbType, final Properties viewConfigProp, final boolean isHideMsgOnSuccess) {
        try {
            final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
            Row selectQueryRow = null;
            if (queryDO != null) {
                selectQueryRow = queryDO.getRow("SelectQuery");
            }
            final DataObject dobj = (DataObject)new WritableDataObject();
            final String actualSQLQuery = RelationalAPI.getInstance().getSelectSQL((Query)query);
            CRMickeyClientViewCreation.logger.log(Level.INFO, "Query is " + actualSQLQuery);
            final Row acSqlStringRow = this.addACSQLStringRow(actualSQLQuery, selectQueryRow);
            dobj.addRow(acSqlStringRow);
            final Row customViewRow = this.addCustomViewRow(customReportDetails, selectQueryRow);
            dobj.addRow(customViewRow);
            final Row viewConfigurationRow = this.addViewConfigurationRow(customReportDetails, viewConfigProp);
            dobj.addRow(viewConfigurationRow);
            CRMickeyClientViewCreation.logger.log(Level.INFO, "Query is added in viewConfigurationRow");
            final Row acColumnConfigurationListRow = this.addAcColumnConfigurationListRow(customViewRow);
            dobj.addRow(acColumnConfigurationListRow);
            CRMickeyClientViewCreation.logger.log(Level.INFO, "Query is added in AcColumnConfigurationListRow");
            final Row acTableViewConfigRow = this.addACTableViewConfigRow(viewConfigurationRow, customViewRow, acColumnConfigurationListRow, customReportDetails);
            dobj.addRow(acTableViewConfigRow);
            final Row webViewConfigRow = this.addWebViewConfig(viewConfigurationRow, viewConfigProp);
            dobj.addRow(webViewConfigRow);
            final Row featureParams = this.addFeatureParamsRow(viewConfigurationRow);
            dobj.addRow(featureParams);
            this.addRowToACColumnConfiguration(acColumnConfigurationListRow.get("NAME_NO"), customReportDetails, dobj, viewConfigProp);
            this.addPiiRedactConfigRow(dobj);
            if (!saveView) {
                final Row crViewDetailsRow = this.addCRViewDetailsRow(customReportDetails, selectQueryRow, viewConfigurationRow);
                dobj.addRow(crViewDetailsRow);
                persistence.add(dobj);
                return 1100;
            }
            persistence.add(dobj);
            return this.CRSaveViewDetails(customReportDetails, selectQueryRow, viewConfigurationRow, customerID, dbType, viewConfigProp, isHideMsgOnSuccess);
        }
        catch (final Exception e) {
            CRMickeyClientViewCreation.logger.log(Level.WARNING, "Exception from storeMCViewDetails ");
            e.printStackTrace();
            return 1101;
        }
    }
    
    private Row addSelectQueryRow() throws Exception {
        final Row selectQueryRow = new Row("SelectQuery");
        return selectQueryRow;
    }
    
    private Row addACSQLStringRow(final String stringQuery, final Row selectQueryRow) throws Exception {
        final Row acSqlStringRow = new Row("ACSQLString");
        acSqlStringRow.set("QUERYID", selectQueryRow.get("QUERYID"));
        acSqlStringRow.set("SQL", (Object)stringQuery);
        return acSqlStringRow;
    }
    
    private Row addCustomViewRow(final CustomReportDetails customReportDetails, final Row selectQueryRow) throws Exception {
        final Row customViewRow = new Row("CustomViewConfiguration");
        customViewRow.set("CVNAME", (Object)customReportDetails.reportName);
        customViewRow.set("QUERYID", selectQueryRow.get("QUERYID"));
        return customViewRow;
    }
    
    private Row addViewConfigurationRow(final CustomReportDetails customReportDetails, final Properties viewConfigProp) throws Exception {
        final Row viewConfigurationRow = new Row("ViewConfiguration");
        viewConfigurationRow.set("VIEWNAME", (Object)customReportDetails.reportName);
        final String componentName = viewConfigProp.containsKey("COMPONENTNAME") ? viewConfigProp.getProperty("COMPONENTNAME") : "ACSQLTable";
        viewConfigurationRow.set("COMPONENTNAME", this.getDDValues("UIComponent", "NAME", "NAME_NO", componentName));
        viewConfigurationRow.set("TITLE", (Object)ProductUrlLoader.getInstance().getValue("title"));
        viewConfigurationRow.set("DESCRIPTION", (Object)"Custom Report");
        viewConfigurationRow.set("CUSTOMIZETYPE", (Object)"NO");
        return viewConfigurationRow;
    }
    
    private void addPiiRedactConfigRow(final DataObject dobj) throws Exception {
        final Row viewConfigurationRow = dobj.getRow("ViewConfiguration");
        final Iterator acColumnConfigurationItr = dobj.getRows("ACColumnConfiguration");
        while (acColumnConfigurationItr.hasNext()) {
            final Row piiRedactConfigRow = new Row("PIIRedactConfig");
            final Row acColumnConfigurationRow = acColumnConfigurationItr.next();
            final String columnAlias = acColumnConfigurationRow.get("COLUMNALIAS").toString();
            final String redact_type = MetaDataUtil.getAttribute(columnAlias + ".pii");
            if (redact_type != null) {
                piiRedactConfigRow.set("VIEWNAME", viewConfigurationRow.get("VIEWNAME_NO"));
                piiRedactConfigRow.set("COLUMNALIAS", (Object)columnAlias);
                piiRedactConfigRow.set("REDACT_TYPE", (Object)redact_type);
                dobj.addRow(piiRedactConfigRow);
            }
        }
    }
    
    private Row addAcColumnConfigurationListRow(final Row customViewRow) throws Exception {
        final Row acColumnConfigurationListRow = new Row("ACColumnConfigurationList");
        acColumnConfigurationListRow.set("NAME", (Object)("CR_VIEW_" + customViewRow.get("CVID")));
        return acColumnConfigurationListRow;
    }
    
    private Row addACTableViewConfigRow(final Row viewConfigurationRow, final Row customViewRow, final Row acColumnConfigurationListRow, final CustomReportDetails customReportDetails) throws Exception {
        final Row acTableViewConfigRow = new Row("ACTableViewConfig");
        acTableViewConfigRow.set("NAME", viewConfigurationRow.get("VIEWNAME_NO"));
        acTableViewConfigRow.set("CVNAME", customViewRow.get("CVID"));
        acTableViewConfigRow.set("NAVIGATIONCONFIG", this.getDDValues("ACNavigationConfiguration", "NAME", "NAME_NO", "DCTableNavigation"));
        acTableViewConfigRow.set("COLUMNCONFIGLIST", acColumnConfigurationListRow.get("NAME_NO"));
        acTableViewConfigRow.set("EMPTY_TABLE_MESSAGE", (Object)"dc.rep.customReport.rec_not_found");
        acTableViewConfigRow.set("COLUMNCHOOSERMENUITEM", this.getDDValues("MenuItem", "MENUITEMID", "MENUITEMID_NO", "CCListInline"));
        acTableViewConfigRow.set("ENABLEROWSELECTION", (Object)"NONE");
        acTableViewConfigRow.set("PAGELENGTH", (Object)25);
        acTableViewConfigRow.set("SORTCOLUMN", (Object)this.getSortColumn(customReportDetails));
        acTableViewConfigRow.set("SORTORDER", (Object)"ASC");
        acTableViewConfigRow.set("ENABLEROWHOVER", (Object)Boolean.TRUE);
        acTableViewConfigRow.set("ENABLEEXPORT", (Object)Boolean.TRUE);
        return acTableViewConfigRow;
    }
    
    private Row addWebViewConfig(final Row viewConfigurationRow, final Properties webConfigProperties) throws Exception {
        final Row webViewConfigRow = new Row("WebViewConfig");
        webViewConfigRow.set("VIEWNAME", viewConfigurationRow.get("VIEWNAME_NO"));
        webViewConfigRow.set("VIEWCONTROLLER", (Object)webConfigProperties.getProperty("VIEWCONTROLLER"));
        webViewConfigRow.set("URL", (Object)webConfigProperties.getProperty("WEB_CONFIG_URL"));
        return webViewConfigRow;
    }
    
    private int addRowToACColumnConfiguration(final Object configName, final CustomReportDetails customReportDetails, final DataObject dataObject, final Properties webConfigProp) throws Exception {
        final CreateQuery cQuery = new CreateQuery();
        final ArrayList selectColumnList = (ArrayList)customReportDetails.selectColumnList;
        final DataObject moduleDO = cQuery.getModuleDO(customReportDetails.moduleID, customReportDetails.subModuleID, customReportDetails.selectColumnList);
        if (moduleDO != null) {
            final Iterator iterator = moduleDO.getRows("CRColumns");
            int i = 0;
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                if (row != null) {
                    final String columnID = row.get("COLUMN_ID") + "";
                    final String columnalias = row.get("COLUMN_NAME_ALIAS") + "";
                    final String displayName = row.get("DISPLAY_NAME") + "";
                    final String tableNameAlias = row.get("TABLE_NAME_ALIAS") + "";
                    final String tableName = (String)row.get("TABLE_NAME");
                    final String columnName = (String)row.get("COLUMN_NAME");
                    final String displayOrder = row.get("DISPLAY_ORDER") + "";
                    final String dataType = row.get("DATA_TYPE") + "";
                    Object defaultValue = null;
                    final Criteria tableCrit = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
                    final Criteria columnCrit = new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), (Object)columnName, 0, false);
                    final DataObject defaultValueDO = SyMUtil.getColumnDetailsForColumn(tableCrit.and(columnCrit));
                    if (defaultValueDO != null && !defaultValueDO.isEmpty()) {
                        final Row defaultValueRow = defaultValueDO.getFirstRow("ColumnDetails");
                        defaultValue = defaultValueRow.get("DEFAULT_VALUE");
                    }
                    final boolean visible = (boolean)row.get("DISPLAY_STATUS");
                    final Row acColumnConfigurationList = new Row("ACColumnConfiguration");
                    acColumnConfigurationList.set("CONFIGNAME", configName);
                    acColumnConfigurationList.set("COLUMNINDEX", (Object)selectColumnList.indexOf(columnID));
                    acColumnConfigurationList.set("COLUMNALIAS", (Object)(tableNameAlias + "." + columnalias));
                    acColumnConfigurationList.set("DISPLAYNAME", (Object)displayName);
                    acColumnConfigurationList.set("VISIBLE", (Object)visible);
                    if (defaultValue != null) {
                        acColumnConfigurationList.set("DEFAULT_TEXT", defaultValue);
                    }
                    acColumnConfigurationList.set("SORTENABLED", (Object)Boolean.TRUE);
                    acColumnConfigurationList.set("SEARCHENABLED", (Object)Boolean.FALSE);
                    if (dataType.equalsIgnoreCase("DATE")) {
                        acColumnConfigurationList.set("TRANSFORMER", (Object)webConfigProp.getProperty("TRANSFORMER_CLASS"));
                        CRMickeyClientViewCreation.logger.log(Level.INFO, "Setted ..transformer as DateTransformer ");
                    }
                    final Criteria tranCriteria = new Criteria(Column.getColumn("CRColumnTransformer", "COLUMN_ID"), (Object)columnID, 0);
                    final Row transRow = moduleDO.getRow("CRColumnTransformer", tranCriteria);
                    if (transRow != null) {
                        acColumnConfigurationList.set("TRANSFORMER", (Object)(transRow.get("CLASS_NAME") + ""));
                    }
                    dataObject.addRow(acColumnConfigurationList);
                }
                ++i;
            }
        }
        return 1000;
    }
    
    private Row addCRViewDetailsRow(final CustomReportDetails customReportDetails, final Row selectQueryRow, final Row viewConfigurationRow) throws Exception {
        final Row crViewDetailsRow = new Row("CRViewDetailsInfo");
        crViewDetailsRow.set("USER_ID", (Object)customReportDetails.userID);
        crViewDetailsRow.set("QUERYID", selectQueryRow.get("QUERYID"));
        crViewDetailsRow.set("VIEWID", viewConfigurationRow.get("VIEWNAME_NO"));
        crViewDetailsRow.set("VIEWNAME", viewConfigurationRow.get("VIEWNAME"));
        crViewDetailsRow.set("SESSIONID", (Object)customReportDetails.sessionID);
        return crViewDetailsRow;
    }
    
    private Row addFeatureParamsRow(final Row viewConfigurationRow) {
        final Row featureParamsRow = new Row("FeatureParams");
        featureParamsRow.set("VIEWNAME", viewConfigurationRow.get("VIEWNAME_NO"));
        featureParamsRow.set("FEATURENAME", (Object)"MCSearchDisabled");
        featureParamsRow.set("FEATUREVALUE", (Object)"true");
        return featureParamsRow;
    }
    
    private int CRSaveViewDetails(final CustomReportDetails customReportDetails, final Row selectQueryRow, final Row viewConfigurationRow, final Long customerID, final String dbType, final Properties viewConfigProp, final boolean isHideMsgOnSuccess) throws Exception {
        final Row crViewDetailsRow = new Row("CRSaveViewDetails");
        final int db_type = DBConstants.getDBTypeByDBName(dbType);
        final DataObject crsaveDO = (DataObject)new WritableDataObject();
        final Date date = new Date();
        crViewDetailsRow.set("QUERYID", selectQueryRow.get("QUERYID"));
        crViewDetailsRow.set("VIEWID", viewConfigurationRow.get("VIEWNAME_NO"));
        crViewDetailsRow.set("CRVIEWNAME", viewConfigurationRow.get("VIEWNAME"));
        crViewDetailsRow.set("DISPLAY_CRVIEWNAME", (Object)customReportDetails.reportDisplayName);
        crViewDetailsRow.set("SUB_MODULE_ID", (Object)customReportDetails.subModuleID);
        crViewDetailsRow.set("CRVIEW_DESCRIPTION", (Object)"Test Description");
        crViewDetailsRow.set("LAST_MODIFIED_TIME", (Object)date.getTime());
        crViewDetailsRow.set("USER_ID", (Object)customReportDetails.userID);
        crViewDetailsRow.set("DB_TYPE", (Object)db_type);
        crsaveDO.addRow(crViewDetailsRow);
        final Row crToCustomerRelRow = new Row("CRToCustomerRel");
        crToCustomerRelRow.set("CR_VIEW_ID", crViewDetailsRow.get("CRSAVEVIEW_ID"));
        crToCustomerRelRow.set("CUSTOMER_ID", (Object)customerID);
        crsaveDO.addRow(crToCustomerRelRow);
        final List<DCViewFilterCriteria> dcViewFilterCriteriaList = customReportDetails.criteriaList;
        final DCViewFilterUtil filterUtil = new DCViewFilterUtil();
        for (int criteriaSize = dcViewFilterCriteriaList.size(), order = 0; order < criteriaSize; ++order) {
            final DCViewFilterCriteria criteria = dcViewFilterCriteriaList.get(order);
            final Row criteriaColumnDetailRow = filterUtil.saveCriteriaColumnDetailRow(criteria, order);
            crsaveDO.addRow(criteriaColumnDetailRow);
            final Row criteriaRelRow = new Row("CRToCriteriaRel");
            criteriaRelRow.set("CRSAVEVIEW_ID", crViewDetailsRow.get("CRSAVEVIEW_ID"));
            criteriaRelRow.set("CRITERIA_COLUMN_ID", criteriaColumnDetailRow.get("CRITERIA_COLUMN_ID"));
            crsaveDO.addRow(criteriaRelRow);
        }
        final List columnList = customReportDetails.selectColumnList;
        for (int j = 0; j < columnList.size(); ++j) {
            if (columnList.get(j) != null) {
                final Row crcolumnRow = new Row("CRSaveColumnDetails");
                crcolumnRow.set("CRSAVEVIEW_ID", crViewDetailsRow.get("CRSAVEVIEW_ID"));
                crcolumnRow.set("COLUMN_ID", (Object)Long.valueOf(columnList.get(j).toString()));
                crcolumnRow.set("DISPLAY_ORDER", (Object)j);
                crsaveDO.addRow(crcolumnRow);
            }
        }
        SyMUtil.getPersistence().add(crsaveDO);
        final Row crViewParamsRow = new Row("CRViewParams");
        crViewParamsRow.set("CRSAVEVIEW_ID", crViewDetailsRow.get("CRSAVEVIEW_ID"));
        String url = viewConfigProp.getProperty("ACTION_URL");
        if (url != null) {
            final String reportID = "<reportID>";
            if (url.contains(reportID)) {
                url = url.replaceAll(reportID, crViewDetailsRow.get("CRSAVEVIEW_ID").toString());
            }
            final String viewName = "<viewName>";
            if (url.contains(viewName)) {
                url = url.replaceAll(viewName, viewConfigurationRow.get("VIEWNAME").toString());
            }
            crViewParamsRow.set("ACTION_URL", (Object)url);
        }
        final DataObject viewParamsDO = (DataObject)new WritableDataObject();
        viewParamsDO.addRow(crViewParamsRow);
        SyMUtil.getPersistence().add(viewParamsDO);
        if (isHideMsgOnSuccess) {
            MessageProvider.getInstance().hideMessage("CR_NOT_CREATED", customerID);
        }
        return 1100;
    }
    
    private Object getDDValues(final String tableName, final String criteriaColumnName, final String columnName, final String criteriaValue) {
        try {
            final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
            final Criteria c1 = new Criteria(Column.getColumn(tableName, criteriaColumnName), (Object)criteriaValue, 0);
            final DataObject dObj = persistence.get(tableName, c1);
            if (dObj != null) {
                return dObj.getFirstValue(tableName, columnName);
            }
        }
        catch (final Exception e) {
            CRMickeyClientViewCreation.logger.log(Level.WARNING, "Exception while getting getDDValues " + e);
        }
        return null;
    }
    
    private String getSortColumn(final CustomReportDetails customReportDetails) {
        try {
            final CreateQuery cQuery = new CreateQuery();
            final DataObject moduleDO = cQuery.getModuleDO(customReportDetails.moduleID, customReportDetails.subModuleID, customReportDetails.selectColumnList);
            if (moduleDO != null) {
                final Criteria criteria = new Criteria(Column.getColumn("CRColumns", "COLUMN_ID"), (Object)customReportDetails.sortColumn, 0);
                final Row columnRow = moduleDO.getRow("CRColumns", criteria);
                if (columnRow != null) {
                    final String tableNameAlias = (columnRow.get("TABLE_NAME_ALIAS") + "").trim();
                    final String columnNameAlias = (columnRow.get("COLUMN_NAME_ALIAS") + "").trim();
                    final String columnFullName = tableNameAlias + "." + columnNameAlias;
                    return columnFullName;
                }
            }
        }
        catch (final Exception e) {
            CRMickeyClientViewCreation.logger.log(Level.WARNING, "Exception while getting from getSortColumn:", e);
        }
        return null;
    }
    
    static {
        CRMickeyClientViewCreation.logger = Logger.getLogger(CRMickeyClientViewCreation.class.getName());
    }
}
