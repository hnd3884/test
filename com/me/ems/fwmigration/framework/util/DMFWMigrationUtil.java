package com.me.ems.fwmigration.framework.util;

import com.adventnet.ds.query.Join;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class DMFWMigrationUtil
{
    private static Logger logger;
    
    public static void updateChangedViewConfigurationTableEntries(final String viewName) throws Exception {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewConfiguration"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME"), (Object)viewName, 0));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject dataObject = DataAccess.get(selectQuery);
            DMFWMigrationUtil.logger.log(Level.INFO, "ViewName " + viewName + " DO for the same " + dataObject);
            Row viewConfigurationRow = null;
            if (!dataObject.isEmpty()) {
                viewConfigurationRow = DataAccess.get(selectQuery).getFirstRow("ViewConfiguration");
            }
            final SelectQuery persViewSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewConfiguration"));
            persViewSelectQuery.setCriteria(new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME"), (Object)(viewName + "_PERSVIEW_" + "*"), 2));
            persViewSelectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            final DataObject persViewdataObject = DataAccess.get(persViewSelectQuery);
            DMFWMigrationUtil.logger.log(Level.INFO, "ViewName of Personlised View and DO  " + persViewdataObject);
            if (!persViewdataObject.isEmpty() && viewConfigurationRow != null) {
                final Iterator<Row> iterator = persViewdataObject.getRows("ViewConfiguration");
                while (iterator.hasNext()) {
                    final Row r = iterator.next();
                    r.set("COMPONENTNAME", viewConfigurationRow.get("COMPONENTNAME"));
                    persViewdataObject.updateRow(r);
                }
                DMFWMigrationUtil.logger.log(Level.INFO, "going to update :" + persViewdataObject);
                DataAccess.update(persViewdataObject);
            }
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public static void updateChangedACTableViewConfigEntries(final String viewName) throws Exception {
        try {
            DMFWMigrationUtil.logger.log(Level.INFO, "going to update ACTableViewConfig  entries for personalized view of :" + viewName);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ACTableViewConfig"));
            selectQuery.addJoin(new Join("ACTableViewConfig", "ViewConfiguration", new String[] { "NAME" }, new String[] { "VIEWNAME_NO" }, 2));
            selectQuery.setCriteria(new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME"), (Object)viewName, 0));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            DataObject dataObject = DataAccess.get(selectQuery);
            if (!dataObject.isEmpty()) {
                final Row baseRow = DataAccess.get(selectQuery).getFirstRow("ACTableViewConfig");
                final String enableRowSelection = (String)baseRow.get("ENABLEROWSELECTION");
                final Boolean enableRowHover = (Boolean)baseRow.get("ENABLEROWHOVER");
                final Boolean enableExport = (Boolean)baseRow.get("ENABLEEXPORT");
                final String rowTransformer = (String)baseRow.get("ROWTRANSFORMER");
                selectQuery.setCriteria(new Criteria(Column.getColumn("ViewConfiguration", "VIEWNAME"), (Object)(viewName + "_PERSVIEW_" + "*"), 2));
                dataObject = DataAccess.get(selectQuery);
                if (!dataObject.isEmpty()) {
                    DMFWMigrationUtil.logger.log(Level.INFO, "Personalized views found.. Going to update..");
                    final Iterator<Row> iterator = dataObject.getRows("ACTableViewConfig");
                    while (iterator.hasNext()) {
                        final Row r = iterator.next();
                        r.set("ENABLEROWSELECTION", (Object)enableRowSelection);
                        r.set("ENABLEROWHOVER", (Object)enableRowHover);
                        r.set("ENABLEEXPORT", (Object)enableExport);
                        r.set("ROWTRANSFORMER", (Object)rowTransformer);
                        dataObject.updateRow(r);
                    }
                    DMFWMigrationUtil.logger.log(Level.INFO, "going to update :" + dataObject);
                    DataAccess.update(dataObject);
                }
            }
        }
        catch (final Exception ex) {
            DMFWMigrationUtil.logger.log(Level.SEVERE, "Exception while updating updateChangedACTableViewConfigEntries", ex);
            throw ex;
        }
    }
    
    static {
        DMFWMigrationUtil.logger = Logger.getLogger(DMFWMigrationUtil.class.getName());
    }
}
