package com.me.devicemanagement.framework.server.util;

import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.ArrayList;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModifyMCColumnTrimUtils
{
    private static String className;
    private static Logger logger;
    private static ModifyMCColumnTrimUtils trimColumnUtils;
    private static final int RANGE_COUNT = 200;
    
    public static ModifyMCColumnTrimUtils getInstance() {
        if (ModifyMCColumnTrimUtils.trimColumnUtils == null) {
            ModifyMCColumnTrimUtils.trimColumnUtils = new ModifyMCColumnTrimUtils();
        }
        return ModifyMCColumnTrimUtils.trimColumnUtils;
    }
    
    public Long getColumnConfigID(final String viewName) {
        ModifyMCColumnTrimUtils.logger.log(Level.FINE, "Method to get Column Config ID for the view name : " + viewName);
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewConfiguration"));
            query.addJoin(new Join("ViewConfiguration", "ACTableViewConfig", new String[] { "VIEWNAME_NO" }, new String[] { "NAME" }, 2));
            final Criteria criteria = new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME"), (Object)viewName, 0);
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            ModifyMCColumnTrimUtils.logger.log(Level.FINE, "Query to get column config ID is " + query);
            final DataObject dobj = SyMUtil.getPersistence().get(query);
            ModifyMCColumnTrimUtils.logger.log(Level.FINE, "Data Object obtained is " + dobj);
            if (dobj != null && dobj.containsTable("ACTableViewConfig")) {
                final Row row = dobj.getFirstRow("ACTableViewConfig");
                final Long columnConfigID = (Long)row.get("COLUMNCONFIGLIST");
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Column Configuration ID for the view " + viewName + " is : " + columnConfigID);
                return columnConfigID;
            }
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting column config id for view : " + viewName, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting column config id for view : " + viewName, ex2);
        }
        return null;
    }
    
    public Long getViewID(final String viewName) {
        ModifyMCColumnTrimUtils.logger.log(Level.FINE, "Method to get the view ID for viewName : " + viewName);
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME"), (Object)viewName, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("ViewConfiguration", criteria);
            ModifyMCColumnTrimUtils.logger.log(Level.FINE, "DataObject obtained : " + dobj);
            if (dobj != null && dobj.containsTable("ViewConfiguration")) {
                final Row row = dobj.getFirstRow("ViewConfiguration");
                final Long viewID = (Long)row.get("VIEWNAME_NO");
                ModifyMCColumnTrimUtils.logger.log(Level.FINE, "ViewID for viewName " + viewName + " is : " + viewID);
                return viewID;
            }
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting column view id for view : " + viewName, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting column view id for view : " + viewName, ex2);
        }
        return null;
    }
    
    public String getViewName(final int viewID) {
        ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Method to get the ViewName for given viewID : " + viewID);
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ViewParams", "VIEW_ID"), (Object)viewID, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("ViewParams", criteria);
            ModifyMCColumnTrimUtils.logger.log(Level.FINE, "DataObject obtained : " + dobj);
            if (dobj != null && dobj.containsTable("ViewParams")) {
                final Row row = dobj.getFirstRow("ViewParams");
                final String viewName = (String)row.get("VIEW_NAME");
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "View Name isssss = " + viewName);
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "ViewID for viewName " + viewName + " is : " + viewID);
                return viewName;
            }
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting viewname for giving view id =  : " + viewID, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting viewname for giving view id =  : " + viewID, ex2);
        }
        return null;
    }
    
    public void setViewColumnTrimStatus(final String viewName, final boolean isColumnsTrimmed) {
        ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Method to set trim status as " + isColumnsTrimmed + " for the view : " + viewName);
        final Long viewID = this.getViewID(viewName);
        try {
            final Criteria criteria = new Criteria(Column.getColumn("MCViewColumnTrimStatus", "VIEW_ID"), (Object)viewID, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("MCViewColumnTrimStatus", criteria);
            if (dobj != null && dobj.containsTable("MCViewColumnTrimStatus")) {
                final Row row = dobj.getFirstRow("MCViewColumnTrimStatus");
                row.set("IS_COLUMNS_TRIMMED", (Object)isColumnsTrimmed);
                dobj.updateRow(row);
            }
            else {
                final Row row = new Row("MCViewColumnTrimStatus");
                row.set("VIEW_ID", (Object)viewID);
                row.set("IS_COLUMNS_TRIMMED", (Object)isColumnsTrimmed);
                dobj.addRow(row);
            }
            SyMUtil.getPersistence().update(dobj);
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while setting trim status for the view : " + viewName, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while setting trim status for the view : " + viewName, ex2);
        }
    }
    
    public boolean getViewColumnTrimStatus(final String viewName) {
        boolean isColumnsTrimmed = true;
        ModifyMCColumnTrimUtils.logger.log(Level.FINE, "Method to get the column trim status for the view : " + viewName);
        try {
            final Long viewID = this.getViewID(viewName);
            final Criteria criteria = new Criteria(Column.getColumn("MCViewColumnTrimStatus", "VIEW_ID"), (Object)viewID, 0);
            final DataObject dobj = SyMUtil.getPersistence().get("MCViewColumnTrimStatus", criteria);
            if (dobj != null && dobj.containsTable("MCViewColumnTrimStatus")) {
                final Row row = dobj.getFirstRow("MCViewColumnTrimStatus");
                isColumnsTrimmed = (boolean)row.get("IS_COLUMNS_TRIMMED");
            }
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting trim status for the view : " + viewName, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting trim status for the view : " + viewName, ex2);
        }
        ModifyMCColumnTrimUtils.logger.log(Level.FINE, "Column trim status for the view " + viewName + " is : " + isColumnsTrimmed);
        return isColumnsTrimmed;
    }
    
    public void globalViewTrim(final String viewname) {
        boolean globaltrimstatus = true;
        final Criteria criteria = null;
        try {
            final DataObject globalsettingDO = SyMUtil.getPersistence().get("ViewGlobalSettings", criteria);
            if (!globalsettingDO.isEmpty()) {
                final Row settingsRow = globalsettingDO.getFirstRow("ViewGlobalSettings");
                if (settingsRow != null) {
                    globaltrimstatus = (boolean)settingsRow.get("IS_COLUMNS_TRIMMED");
                }
            }
            final boolean isColumnsTrimmed = getInstance().getViewColumnTrimStatus(viewname);
            if (globaltrimstatus) {
                if (!isColumnsTrimmed) {
                    this.restoreTrim(viewname);
                }
            }
            else if (isColumnsTrimmed) {
                this.removeTrim(viewname, false);
            }
        }
        catch (final Exception ee) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while getting global View Trim Status : ", ee);
        }
    }
    
    public void removeTrim(final String viewName1, final boolean istrimmed) {
        final String viewName2 = viewName1;
        ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Request to remove column trim for the view : " + viewName2);
        try {
            final Long columnConfigID = getInstance().getColumnConfigID(viewName2);
            ModifyMCColumnTrimUtils.logger.log(Level.FINE, "columnConfigID : " + columnConfigID);
            final Criteria criteria = new Criteria(Column.getColumn("ACColumnConfiguration", "CONFIGNAME"), (Object)columnConfigID, 0);
            final DataObject columnsDobj = SyMUtil.getPersistence().get("ACColumnConfiguration", criteria);
            ModifyMCColumnTrimUtils.logger.log(Level.FINE, "columnsDobj : " + columnsDobj);
            if (columnsDobj != null && columnsDobj.containsTable("ACColumnConfiguration")) {
                final Iterator colIterator = columnsDobj.getRows("ACColumnConfiguration");
                while (colIterator.hasNext()) {
                    final Row acColumnConfigRow = colIterator.next();
                    final String displayName = (String)acColumnConfigRow.get("DISPLAYNAME");
                    final Integer trimLength = (Integer)acColumnConfigRow.get("TRIM_LENGTH");
                    ModifyMCColumnTrimUtils.logger.log(Level.FINE, "displayName : " + displayName);
                    ModifyMCColumnTrimUtils.logger.log(Level.FINE, "trimLength : " + trimLength);
                    ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to add/update MCColumnTrimValues table for the column having displayName : " + displayName + ", trimLength : " + trimLength + ", and columnConfigID : " + columnConfigID);
                    final Criteria configIDCrit = new Criteria(Column.getColumn("MCColumnTrimValues", "CONFIG_ID"), (Object)columnConfigID, 0);
                    final Criteria colNameCriit = new Criteria(Column.getColumn("MCColumnTrimValues", "COLUMN_NAME"), (Object)displayName, 0);
                    final Criteria mcColumnTrimValuesCrit = configIDCrit.and(colNameCriit);
                    final DataObject mcColumnTrimValuesDobj = SyMUtil.getPersistence().get("MCColumnTrimValues", mcColumnTrimValuesCrit);
                    ModifyMCColumnTrimUtils.logger.log(Level.FINE, "mcColumnTrimValuesDobj : " + mcColumnTrimValuesDobj);
                    if (trimLength != null) {
                        if (mcColumnTrimValuesDobj != null && mcColumnTrimValuesDobj.containsTable("MCColumnTrimValues")) {
                            ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to UPDATE MCColumnTrimValues table for the column having displayName : " + displayName + ", trimLength : " + trimLength + ", and columnConfigID : " + columnConfigID);
                            final Row mcColumnTrimValuesRow = mcColumnTrimValuesDobj.getFirstRow("MCColumnTrimValues");
                            if (mcColumnTrimValuesRow != null) {
                                mcColumnTrimValuesRow.set("TRIM_LENGTH", (Object)trimLength);
                                mcColumnTrimValuesDobj.updateRow(mcColumnTrimValuesRow);
                            }
                        }
                        else {
                            ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to ADD Row in MCColumnTrimValues table for the column having displayName : " + displayName + ", trimLength : " + trimLength + ", and columnConfigID : " + columnConfigID);
                            final Row mcColumnTrimValuesRow = new Row("MCColumnTrimValues");
                            mcColumnTrimValuesRow.set("CONFIG_ID", (Object)columnConfigID);
                            mcColumnTrimValuesRow.set("COLUMN_NAME", (Object)displayName);
                            mcColumnTrimValuesRow.set("TRIM_LENGTH", (Object)trimLength);
                            mcColumnTrimValuesDobj.addRow(mcColumnTrimValuesRow);
                        }
                    }
                    SyMUtil.getPersistence().update(mcColumnTrimValuesDobj);
                    ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to remove trim for column having displayName : " + displayName + " and columnConfigID : " + columnConfigID);
                    acColumnConfigRow.set("TRIM_LENGTH", (Object)null);
                    columnsDobj.updateRow(acColumnConfigRow);
                }
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to update view by removing all the column trims for the view " + viewName2);
                SyMUtil.getPersistence().update(columnsDobj);
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Successfully updated view by removing all the column trims for the view " + viewName2);
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to clear JVM Cache for mickey client");
                SyMUtil.getInstance().clearCacheForView(viewName2);
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "JVM Cache for mickey client has been cleared");
                getInstance().setViewColumnTrimStatus(viewName2, false);
            }
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while removing trim for view : " + viewName2, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while removing trim for view : " + viewName2, ex2);
        }
    }
    
    public void restoreTrim(final String viewName1) {
        final String viewName2 = viewName1;
        try {
            final Long columnConfigID = getInstance().getColumnConfigID(viewName2);
            final Criteria criteria = new Criteria(Column.getColumn("ACColumnConfiguration", "CONFIGNAME"), (Object)columnConfigID, 0);
            final DataObject columnsDobj = SyMUtil.getPersistence().get("ACColumnConfiguration", criteria);
            if (columnsDobj != null && columnsDobj.containsTable("ACColumnConfiguration")) {
                final Iterator colIterator = columnsDobj.getRows("ACColumnConfiguration");
                while (colIterator.hasNext()) {
                    final Row acColumnConfigRow = colIterator.next();
                    final String displayName = (String)acColumnConfigRow.get("DISPLAYNAME");
                    final Criteria configIDCrit = new Criteria(Column.getColumn("MCColumnTrimValues", "CONFIG_ID"), (Object)columnConfigID, 0);
                    final Criteria colNameCriit = new Criteria(Column.getColumn("MCColumnTrimValues", "COLUMN_NAME"), (Object)displayName, 0);
                    final Criteria mcColumnTrimValuesCrit = configIDCrit.and(colNameCriit);
                    final DataObject mcColumnTrimValuesDobj = SyMUtil.getPersistence().get("MCColumnTrimValues", mcColumnTrimValuesCrit);
                    ModifyMCColumnTrimUtils.logger.log(Level.FINE, "mcColumnTrimValuesDobj : " + mcColumnTrimValuesDobj);
                    if (mcColumnTrimValuesDobj != null && mcColumnTrimValuesDobj.containsTable("MCColumnTrimValues")) {
                        final Row mcColumnTrimValuesRow = mcColumnTrimValuesDobj.getFirstRow("MCColumnTrimValues");
                        if (mcColumnTrimValuesRow == null) {
                            continue;
                        }
                        final Integer trimLength = (Integer)mcColumnTrimValuesRow.get("TRIM_LENGTH");
                        ModifyMCColumnTrimUtils.logger.log(Level.INFO, "going to set trim length as " + trimLength + " for column " + displayName);
                        acColumnConfigRow.set("TRIM_LENGTH", (Object)trimLength);
                        columnsDobj.updateRow(acColumnConfigRow);
                    }
                }
                SyMUtil.getPersistence().update(columnsDobj);
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Column trim values for the view " + viewName2 + " has been restored");
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to clear JVM Cache for mickey client");
                SyMUtil.getInstance().clearCacheForView(viewName2);
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "JVM Cache for mickey client has been cleared");
                getInstance().setViewColumnTrimStatus(viewName2, true);
            }
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while restoring trim for view : " + viewName2, (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while restoring trim for view : " + viewName2, ex2);
        }
    }
    
    public void removeAllViewTrimLength() {
        try {
            final Criteria trimLengthCri = new Criteria(Column.getColumn("ACColumnConfiguration", "TRIM_LENGTH"), (Object)null, 1);
            final int trimLengthCount = DBUtil.getRecordActualCount("ACColumnConfiguration", "TRIM_LENGTH", trimLengthCri);
            final SelectQuery trimQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ACColumnConfiguration"));
            trimQuery.addSelectColumn(new Column((String)null, "*"));
            trimQuery.setCriteria(trimLengthCri);
            final Column timeCol = Column.getColumn("ACColumnConfiguration", "CONFIGNAME");
            final SortColumn sortCol = new SortColumn(timeCol, true);
            trimQuery.addSortColumn(sortCol);
            for (int grpStart = 1; grpStart <= trimLengthCount; grpStart += 200) {
                final Range rangeLimit = new Range(1, 200);
                trimQuery.setRange(rangeLimit);
                final DataObject columnsDobj = SyMUtil.getPersistence().get(trimQuery);
                if (columnsDobj != null && columnsDobj.containsTable("ACColumnConfiguration")) {
                    final Iterator colIterator = columnsDobj.getRows("ACColumnConfiguration");
                    while (colIterator.hasNext()) {
                        final Row acColumnConfigRow = colIterator.next();
                        final String displayName = (String)acColumnConfigRow.get("DISPLAYNAME");
                        final Integer trimLength = (Integer)acColumnConfigRow.get("TRIM_LENGTH");
                        ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to add/update MCColumnTrimValues table for the column having displayName : " + displayName + ", trimLength : " + trimLength + ", and columnConfigID : " + acColumnConfigRow.get("CONFIGNAME"));
                        final Criteria configIDCrit = new Criteria(Column.getColumn("MCColumnTrimValues", "CONFIG_ID"), acColumnConfigRow.get("CONFIGNAME"), 0);
                        final Criteria colNameCriit = new Criteria(Column.getColumn("MCColumnTrimValues", "COLUMN_NAME"), (Object)displayName, 0);
                        final Criteria mcColumnTrimValuesCrit = configIDCrit.and(colNameCriit);
                        final DataObject mcColumnTrimValuesDobj = SyMUtil.getPersistence().get("MCColumnTrimValues", mcColumnTrimValuesCrit);
                        if (trimLength != null) {
                            if (mcColumnTrimValuesDobj != null && mcColumnTrimValuesDobj.containsTable("MCColumnTrimValues")) {
                                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to UPDATE MCColumnTrimValues table for the column having displayName : " + displayName + ", trimLength : " + trimLength + ", and columnConfigID : " + acColumnConfigRow.get("CONFIGNAME"));
                                final Row mcColumnTrimValuesRow = mcColumnTrimValuesDobj.getFirstRow("MCColumnTrimValues");
                                if (mcColumnTrimValuesRow != null) {
                                    mcColumnTrimValuesRow.set("TRIM_LENGTH", (Object)trimLength);
                                    mcColumnTrimValuesDobj.updateRow(mcColumnTrimValuesRow);
                                }
                            }
                            else {
                                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to ADD Row in MCColumnTrimValues table for the column having displayName : " + displayName + ", trimLength : " + trimLength + ", and columnConfigID : " + acColumnConfigRow.get("CONFIGNAME"));
                                final Row mcColumnTrimValuesRow = new Row("MCColumnTrimValues");
                                mcColumnTrimValuesRow.set("CONFIG_ID", acColumnConfigRow.get("CONFIGNAME"));
                                mcColumnTrimValuesRow.set("COLUMN_NAME", (Object)displayName);
                                mcColumnTrimValuesRow.set("TRIM_LENGTH", (Object)trimLength);
                                mcColumnTrimValuesDobj.addRow(mcColumnTrimValuesRow);
                            }
                        }
                        SyMUtil.getPersistence().update(mcColumnTrimValuesDobj);
                        ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to remove trim for column having displayName : " + displayName + " and columnConfigID : " + acColumnConfigRow.get("CONFIGNAME"));
                        acColumnConfigRow.set("TRIM_LENGTH", (Object)null);
                        columnsDobj.updateRow(acColumnConfigRow);
                    }
                }
                ModifyMCColumnTrimUtils.logger.log(Level.FINE, "columnsDobj : " + columnsDobj);
                SyMUtil.getPersistence().update(columnsDobj);
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Successfully updated view by removing all the column trims for the ALL view ");
            }
            if (trimLengthCount > 0) {
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to clear JVM Cache for mickey client");
                ApiFactoryProvider.getCacheAccessAPI().clearCachedData();
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "JVM Cache for mickey client has been cleared");
                this.setAllViewColumnTrimStatus(false);
            }
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while removing trim for view : ", (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while removing trim for view : ", ex2);
        }
    }
    
    public void restoreAllViewTrimLength() {
        try {
            final Criteria trimLengthCri = new Criteria(Column.getColumn("MCColumnTrimValues", "TRIM_LENGTH"), (Object)null, 1);
            final int trimLengthCount = DBUtil.getRecordActualCount("MCColumnTrimValues", "TRIM_LENGTH", trimLengthCri);
            final SelectQuery trimQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MCColumnTrimValues"));
            trimQuery.addSelectColumn(new Column((String)null, "*"));
            final Column timeCol = Column.getColumn("MCColumnTrimValues", "CONFIG_ID");
            final SortColumn sortCol = new SortColumn(timeCol, true);
            trimQuery.addSortColumn(sortCol);
            for (int grpStart = 1; grpStart <= trimLengthCount; grpStart += 200) {
                final Range rangeLimit = new Range(grpStart, 200);
                trimQuery.setRange(rangeLimit);
                final DataObject columnsDobj = SyMUtil.getPersistence().get(trimQuery);
                ModifyMCColumnTrimUtils.logger.log(Level.FINE, "columnsDobj : " + columnsDobj);
                if (columnsDobj != null && columnsDobj.containsTable("MCColumnTrimValues")) {
                    final Iterator colIterator = columnsDobj.getRows("MCColumnTrimValues");
                    while (colIterator.hasNext()) {
                        final Row mcColumnTrimValuesRow = colIterator.next();
                        final String displayName = (String)mcColumnTrimValuesRow.get("COLUMN_NAME");
                        final Criteria configIDCrit = new Criteria(Column.getColumn("ACColumnConfiguration", "CONFIGNAME"), mcColumnTrimValuesRow.get("CONFIG_ID"), 0);
                        final Criteria colNameCriit = new Criteria(Column.getColumn("ACColumnConfiguration", "DISPLAYNAME"), (Object)displayName, 0);
                        final Criteria mcColumnTrimValuesCrit = configIDCrit.and(colNameCriit);
                        try {
                            final UpdateQuery adUpdateStatusQuery = (UpdateQuery)new UpdateQueryImpl("ACColumnConfiguration");
                            adUpdateStatusQuery.setCriteria(mcColumnTrimValuesCrit);
                            adUpdateStatusQuery.setUpdateColumn("TRIM_LENGTH", mcColumnTrimValuesRow.get("TRIM_LENGTH"));
                            SyMUtil.getPersistence().update(adUpdateStatusQuery);
                        }
                        catch (final Exception exp) {
                            ModifyMCColumnTrimUtils.logger.log(Level.WARNING, "Exception while restoring trim length " + displayName, exp);
                        }
                    }
                }
            }
            if (trimLengthCount > 0) {
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "Going to clear JVM Cache for mickey client");
                ApiFactoryProvider.getCacheAccessAPI().clearCachedData();
                ModifyMCColumnTrimUtils.logger.log(Level.INFO, "JVM Cache for mickey client has been cleared");
                this.setAllViewColumnTrimStatus(true);
            }
        }
        catch (final DataAccessException ex) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while restoring trim for view : ", (Throwable)ex);
        }
        catch (final Exception ex2) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "Exception while restoring trim for view : ", ex2);
        }
    }
    
    public void setAllViewColumnTrimStatus(final boolean isColumnsTrimmed) throws Exception {
        try {
            final ArrayList viewIDList = this.getViewIDList();
            final Criteria criteria = new Criteria(Column.getColumn("MCViewColumnTrimStatus", "VIEW_ID"), (Object)viewIDList.toArray(), 8);
            final DataObject dobj = SyMUtil.getPersistence().get("MCViewColumnTrimStatus", criteria);
            for (int i = 0; i < viewIDList.size(); ++i) {
                final Criteria localCriteria = new Criteria(Column.getColumn("MCViewColumnTrimStatus", "VIEW_ID"), viewIDList.get(i), 0);
                Row mcViewRow = dobj.getRow("MCViewColumnTrimStatus", localCriteria);
                if (mcViewRow != null) {
                    mcViewRow.set("IS_COLUMNS_TRIMMED", (Object)isColumnsTrimmed);
                    dobj.updateRow(mcViewRow);
                }
                else {
                    mcViewRow = new Row("MCViewColumnTrimStatus");
                    mcViewRow.set("VIEW_ID", viewIDList.get(i));
                    mcViewRow.set("IS_COLUMNS_TRIMMED", (Object)isColumnsTrimmed);
                    dobj.addRow(mcViewRow);
                }
            }
            SyMUtil.getPersistence().update(dobj);
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "setAllViewColumnTrimStatus() MCViewColumnTrimStatus updated the status for view ID list {0} ", viewIDList);
        }
        catch (final Exception exp) {
            ModifyMCColumnTrimUtils.logger.log(Level.SEVERE, "setAllViewColumnTrimStatus() Exception while restoring trim for view : ", exp);
        }
    }
    
    private ArrayList getViewIDList() throws Exception {
        final ArrayList viewIDList = new ArrayList();
        final String tableName = "MCColumnTrimValues";
        final String columnName = "CONFIG_ID";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        Column selCol = new Column(tableName, columnName);
        selCol = selCol.distinct();
        selectQuery.addSelectColumn(selCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)selectQuery, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                viewIDList.add(value);
            }
            ds.close();
        }
        catch (final QueryConstructionException ex) {
            throw ex;
        }
        catch (final SQLException ex2) {
            throw ex2;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {}
        }
        return viewIDList;
    }
    
    static {
        ModifyMCColumnTrimUtils.className = ModifyMCColumnTrimUtils.class.getName();
        ModifyMCColumnTrimUtils.logger = Logger.getLogger(ModifyMCColumnTrimUtils.className);
        ModifyMCColumnTrimUtils.trimColumnUtils = null;
    }
}
