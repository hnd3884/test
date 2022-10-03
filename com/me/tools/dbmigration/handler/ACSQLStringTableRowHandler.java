package com.me.tools.dbmigration.handler;

import java.util.logging.Level;
import com.adventnet.sym.server.customreport.CreateQuery;
import com.adventnet.db.adapter.DBAdapter;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.sym.webclient.reports.CustomReportUtil;
import com.me.devicemanagement.framework.server.customreport.CRCriteria;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.customreport.CustomReportDetails;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;
import com.adventnet.db.migration.handler.ACSQLTableRowHandler;

public class ACSQLStringTableRowHandler extends ACSQLTableRowHandler
{
    private static final Logger LOGGER;
    
    protected boolean ignoreUnknownACSQLStrings() {
        return true;
    }
    
    public Row preInvokeForInsert(final Row row) throws Exception {
        try {
            final Row acsqlStringRow = super.preInvokeForInsert(row);
            if (acsqlStringRow != null) {
                return acsqlStringRow;
            }
            ACSQLStringTableRowHandler.LOGGER.info(row.toString());
            if (row.get("SQL") == null) {
                final Long queryId = (Long)row.get("QUERYID");
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveViewDetails"));
                selectQuery.addSelectColumn(new Column((String)null, "*"));
                final Criteria criteria = new Criteria(Column.getColumn("CRSaveViewDetails", "QUERYID"), (Object)queryId, 0);
                selectQuery.setCriteria(criteria);
                final DataObject customReportDetailDO = DataAccess.get(selectQuery);
                if (!customReportDetailDO.isEmpty()) {
                    final Row customReportRow = customReportDetailDO.getRow("CRSaveViewDetails");
                    final Long subModuleId = (Long)customReportRow.get("SUB_MODULE_ID");
                    Long moduleId = new Long(0L);
                    final SelectQuery submoduleQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSubModule"));
                    submoduleQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                    final Criteria subCriteria = new Criteria(Column.getColumn("CRSubModule", "SUB_MODULE_ID"), (Object)subModuleId, 0);
                    submoduleQuery.setCriteria(subCriteria);
                    final DataObject submoduleDO = DataAccess.get(submoduleQuery);
                    String subModuleName = null;
                    if (submoduleDO != null) {
                        final Row firstRow = submoduleDO.getRow("CRSubModule");
                        moduleId = (Long)firstRow.get("MODULE_ID");
                        subModuleName = firstRow.get("SUB_MODULE_NAME").toString();
                    }
                    final CustomReportDetails crd = new CustomReportDetails();
                    crd.reportName = customReportRow.get("CRVIEWNAME").toString();
                    crd.reportDisplayName = customReportRow.get("DISPLAY_CRVIEWNAME").toString();
                    crd.reportDesc = customReportRow.get("CRVIEW_DESCRIPTION").toString();
                    crd.moduleID = moduleId;
                    crd.subModuleID = subModuleId;
                    crd.userID = (Long)customReportRow.get("USER_ID");
                    crd.sessionID = null;
                    final Long crSaveViewId = (Long)customReportRow.get("CRSAVEVIEW_ID");
                    final ArrayList selectedColList = new ArrayList();
                    final SelectQuery selectColQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveColumnDetails"));
                    selectColQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                    final Criteria selectColCri = new Criteria(Column.getColumn("CRSaveColumnDetails", "CRSAVEVIEW_ID"), (Object)crSaveViewId, 0);
                    selectColQuery.setCriteria(selectColCri);
                    selectColQuery.addSortColumn(new SortColumn(Column.getColumn("CRSaveColumnDetails", "DISPLAY_ORDER"), true));
                    final DataObject resultDO = DataAccess.get(selectColQuery);
                    final Iterator columnID = resultDO.getRows("CRSaveColumnDetails");
                    while (columnID.hasNext()) {
                        final Row crSelectColRow = columnID.next();
                        final Long columnId = (Long)crSelectColRow.get("COLUMN_ID");
                        selectedColList.add(columnId);
                    }
                    crd.selectColumnList = selectedColList;
                    final ArrayList selectedCriList = new ArrayList();
                    final SelectQuery selectCriteriaQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveCriteriaDetails"));
                    selectCriteriaQuery.addSelectColumn(Column.getColumn((String)null, "*"));
                    final Criteria selectCriteriaCri = new Criteria(Column.getColumn("CRSaveCriteriaDetails", "CRSAVEVIEW_ID"), (Object)crSaveViewId, 0);
                    selectCriteriaQuery.setCriteria(selectCriteriaCri);
                    selectCriteriaQuery.addSortColumn(new SortColumn(Column.getColumn("CRSaveCriteriaDetails", "DISPLAY_ORDER"), true));
                    final DataObject resultCriteriaDO = DataAccess.get(selectCriteriaQuery);
                    final Iterator selectCriteriaIter = resultCriteriaDO.getRows("CRSaveCriteriaDetails");
                    final SelectQuery columnNamequery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
                    columnNamequery.addSelectColumn(Column.getColumn((String)null, "*"));
                    final CRCriteria[] crc = new CRCriteria[20];
                    int i = 0;
                    while (selectCriteriaIter.hasNext()) {
                        final Row selectCriteriaRow = selectCriteriaIter.next();
                        final Long colid = (Long)selectCriteriaRow.get("COLUMN_ID");
                        crc[i] = new CRCriteria();
                        crc[i].columnID = (Long)selectCriteriaRow.get("COLUMN_ID");
                        crc[i].operatorValue = (String)selectCriteriaRow.get("CRITERIATYPE");
                        crc[i].searchValue = (String)selectCriteriaRow.get("SEARCHVALUE");
                        crc[i].logicalOperatorValue = ((String)selectCriteriaRow.get("LOGICALOPERATOR")).trim();
                        selectedCriList.add(crc[i]);
                        ++i;
                    }
                    crd.criteriaList = selectedCriList;
                    SelectQuery query = null;
                    SelectQuery modifiedQuery = null;
                    query = this.getCRSelectQuery(crd);
                    final CustomReportUtil cru = new CustomReportUtil();
                    modifiedQuery = cru.modifySelectQuery(query, subModuleName);
                    final DBAdapter destDBAdapter = DBMigrationUtil.getDestDBAdapter();
                    final String sqlQuery = destDBAdapter.getSQLGenerator().getSQLForSelect((Query)modifiedQuery);
                    row.set("SQL", (Object)sqlQuery);
                    return row;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }
    
    public SelectQuery getCRSelectQuery(final CustomReportDetails crd) {
        try {
            ACSQLStringTableRowHandler.LOGGER.info("Report Desc: " + crd.reportDesc + "- Display Name: " + crd.reportDisplayName + "- Name: " + crd.reportName);
            ACSQLStringTableRowHandler.LOGGER.info("Sort Column: " + crd.sortColumn + "- Criteria List: " + crd.criteriaList + "- Column List: " + crd.selectColumnList);
            ACSQLStringTableRowHandler.LOGGER.info("Module Id: " + crd.moduleID + "- Sub Module Id: " + crd.subModuleID + "- User Id: " + crd.userID);
            final CreateQuery createQueryObj = new CreateQuery();
            final DataObject moduleDO = createQueryObj.getModuleDO(crd.moduleID, crd.subModuleID, crd.selectColumnList);
            final DataObject criModuleDO = createQueryObj.getCriModuleDO(crd.moduleID, crd.subModuleID, crd.criteriaList);
            final DataObject joinDO = createQueryObj.getJoinDO(crd.subModuleID);
            final DataObject joinCriteriaDO = createQueryObj.getJoinCriteriaDO(crd.subModuleID);
            final SelectQuery crTempQuery = createQueryObj.getCRSelectQuery(moduleDO, joinDO, joinCriteriaDO, crd.selectColumnList, criModuleDO);
            final SelectQuery crQuery = createQueryObj.addCriteria(crTempQuery, crd.criteriaList, criModuleDO);
            createQueryObj.addUserIDCriteria(crQuery, crd.userID);
            return crQuery;
        }
        catch (final Exception e) {
            com.me.devicemanagement.framework.server.customreport.CreateQuery.logger.log(Level.WARNING, "CreateQuery :Exception from getCRSelectQuery:", e);
            return null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ACSQLStringTableRowHandler.class.getName());
    }
}
