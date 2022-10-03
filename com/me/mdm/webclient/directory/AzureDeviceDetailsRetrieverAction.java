package com.me.mdm.webclient.directory;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class AzureDeviceDetailsRetrieverAction extends MDMEmberTableRetrieverAction
{
    private boolean isTablePresent(final List<Table> tables, final String tableName) {
        for (int i = 0; i < tables.size(); ++i) {
            final Table curTable = tables.get(i);
            if (curTable.getTableAlias().equalsIgnoreCase(tableName) || curTable.getTableName().equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewContext) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewContext);
        final List<Table> tables = selectQuery.getTableList();
        if (!this.isTablePresent(tables, "DIROBJREGSTRVAL_DISPLAY_NAME")) {
            final String[] tableNames = { "DIROBJREGSTRVAL_NAME", "DIROBJREGSTRVAL_DISPLAY_NAME", "DIROBJREGSTRVAL_DEVICE_TYPE", "DIROBJREGSTRVAL_DEVICE_OS_TYPE", "DIROBJREGSTRVAL_DEVICE_ID", "DIROBJREGSTRVAL_DEVICE_STATUS", "DIROBJREGSTRVAL_PROFILE_TYPE", "DIROBJREGSTRVAL_LAST_LOGON", "DIROBJARRLNGVAL_UPN", "DIROBJREGSTRVA_UPN_REF" };
            final long[] attrId = { 104L, 111L, 119L, 120L, 121L, 123L, 124L, 127L, 126L, 112L };
            for (int i = 0; i < tableNames.length; ++i) {
                String valCol = "VALUE";
                String attrIDCol = "ATTR_ID";
                String joinTableName = "DirObjRegStrVal";
                String resIDcol = "OBJ_ID";
                final String joinBaseTable = "DirResRel";
                String joinBaseCriTable = "DirResRel";
                String joinBaseTableCriColumn = "OBJ_ID";
                if (attrId[i] == 123L) {
                    valCol = "VALUE";
                    attrIDCol = "ATTR_ID";
                    joinTableName = "DirObjRegIntVal";
                    resIDcol = "OBJ_ID";
                }
                if (attrId[i] == 126L) {
                    valCol = "VALUE";
                    attrIDCol = "ATTR_ID";
                    joinTableName = "DirObjArrLngVal";
                    resIDcol = "OBJ_ID";
                }
                if (attrId[i] == 112L) {
                    valCol = "VALUE";
                    attrIDCol = "ATTR_ID";
                    joinTableName = "DirObjRegStrVal";
                    resIDcol = "OBJ_ID";
                    joinBaseCriTable = "DIROBJARRLNGVAL_UPN";
                    joinBaseTableCriColumn = "VALUE";
                }
                final Criteria joinCri = new Criteria(Column.getColumn(tableNames[i], attrIDCol), (Object)attrId[i], 0).and(new Criteria(Column.getColumn(joinBaseCriTable, joinBaseTableCriColumn), (Object)Column.getColumn(tableNames[i], resIDcol), 0));
                selectQuery.addJoin(new Join(new Table(joinBaseTable), new Table(joinTableName, tableNames[i]), joinCri, 1));
                selectQuery.addSelectColumns((List)new ArrayList(Arrays.asList(new Column(tableNames[i], valCol, tableNames[i] + "_VALUE"), new Column(tableNames[i], resIDcol, tableNames[i] + "_OBJ_ID"))));
            }
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        Criteria cri = new Criteria(new Column("DirResRel", "DIR_RESOURCE_TYPE"), (Object)201, 0);
        final HttpServletRequest request = viewCtx.getRequest();
        final String domainId = request.getParameter("domainId");
        final String compliantState = request.getParameter("compliantStatus");
        String managedStatus = request.getParameter("managedStatus");
        if (SyMUtil.isStringValid(domainId)) {
            cri = cri.and(new Criteria(new Column("DirResRel", "DM_DOMAIN_ID"), (Object)domainId, 0));
        }
        if (compliantState != null && !compliantState.equalsIgnoreCase("none")) {
            final Criteria compliantStatusCriteria = new Criteria(new Column("DIROBJREGSTRVAL_DEVICE_STATUS", "VALUE"), (Object)compliantState, 0);
            cri = cri.and(compliantStatusCriteria);
        }
        if (managedStatus != null && !managedStatus.equalsIgnoreCase("none")) {
            if (managedStatus.equalsIgnoreCase("4")) {
                managedStatus = null;
            }
            final Criteria compliantStatusCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)managedStatus, 0);
            cri = cri.and(compliantStatusCriteria);
        }
        query.setCriteria(cri);
        query.addSortColumn(new SortColumn(Column.getColumn("DirResRel", "DM_DOMAIN_ID"), true));
        super.setCriteria(query, viewCtx);
    }
}
