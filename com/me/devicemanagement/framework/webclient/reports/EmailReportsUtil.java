package com.me.devicemanagement.framework.webclient.reports;

import java.util.Iterator;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Join;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.FrameworkStatusCodes;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.logging.Logger;

public class EmailReportsUtil
{
    static String className;
    static Logger out;
    
    public int saveSelectedReports(final String selectedRep) {
        try {
            final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
            final DataObject d = per.get("ReportsToEmailTemp", (Criteria)null);
            d.deleteRows("ReportsToEmailTemp", (Criteria)null);
            per.update(d);
            if (selectedRep != null) {
                final String[] selectedRepo = selectedRep.split(",");
                for (int noOfRep = selectedRepo.length, i = 0; i < noOfRep; ++i) {
                    EmailReportsUtil.out.log(Level.INFO, "Reports  " + selectedRepo[i]);
                    final Integer report = new Integer(selectedRepo[i]);
                    final Row r = new Row("ReportsToEmailTemp");
                    r.set("VIEW_ID", (Object)report);
                    d.addRow(r);
                    per.update(d);
                }
            }
            return 1000;
        }
        catch (final Exception dae) {
            EmailReportsUtil.out.log(Level.INFO, "Exception while trying to save the data " + selectedRep);
            return FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE;
        }
    }
    
    public String getReportandCategoryName() {
        String repname_catname = "";
        Connection conn = null;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ReportsToEmailTemp"));
            final Column title_col = Column.getColumn("ViewParams", "TITLE");
            final Column viewName = Column.getColumn("ViewParams", "VIEW_NAME");
            final Column viewId = Column.getColumn("ReportsToEmailTemp", "VIEW_ID");
            final Column categoryName = Column.getColumn("ReportCategory", "CATEGORY_NAME");
            final ArrayList colList = new ArrayList();
            colList.add(title_col);
            colList.add(viewName);
            colList.add(viewId);
            colList.add(categoryName);
            query.addSelectColumns((List)colList);
            final Join join = new Join("ReportsToEmailTemp", "ViewParams", new String[] { "VIEW_ID" }, new String[] { "VIEW_ID" }, 2);
            query.addJoin(join);
            query.addJoin(new Join("ViewParams", "ReportSubCategory", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2));
            query.addJoin(new Join("ReportSubCategory", "ReportCategory", new String[] { "CATEGORY_ID" }, new String[] { "CATEGORY_ID" }, 2));
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final String title = (String)ds.getValue("TITLE");
                final Integer view_id = (Integer)ds.getValue("VIEW_ID");
                final String categ_name = (String)ds.getValue("CATEGORY_NAME");
                repname_catname = repname_catname + title + ";" + categ_name + ";" + view_id + ",";
            }
            if (conn != null) {
                conn.close();
            }
            ds.close();
        }
        catch (final Exception eee) {
            EmailReportsUtil.out.log(Level.SEVERE, "Error : " + eee);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex) {}
        }
        return repname_catname;
    }
    
    public int deleteSelectedReports(final Integer viewId) {
        try {
            final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
            final DataObject d = per.get("ReportsToEmailTemp", (Criteria)null);
            final Criteria cri = new Criteria(Column.getColumn("ReportsToEmailTemp", "VIEW_ID"), (Object)viewId, 0);
            d.deleteRows("ReportsToEmailTemp", cri);
            per.update(d);
            return 1000;
        }
        catch (final Exception e) {
            EmailReportsUtil.out.log(Level.INFO, "Error While deleting report " + viewId);
            return FrameworkStatusCodes.UNPROCESSABLE_DATA_CODE;
        }
    }
    
    public static List getSelectedReports(final String reportTable) {
        List list = null;
        Integer view_id = null;
        try {
            list = new ArrayList();
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(reportTable));
            query.addSelectColumn(new Column(reportTable, "*"));
            final Join join = new Join(reportTable, "ViewParams", new String[] { "VIEW_ID" }, new String[] { "VIEW_ID" }, 2);
            query.addJoin(join);
            query.addJoin(new Join("ViewParams", "ReportSubCategory", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2));
            query.addJoin(new Join("ReportSubCategory", "ReportCategory", new String[] { "CATEGORY_ID" }, new String[] { "CATEGORY_ID" }, 2));
            final SortColumn sortcolumn = new SortColumn(Column.getColumn("ReportCategory", "CATEGORY_NAME"), true);
            query.addSortColumn(sortcolumn);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Iterator it = dataObject.getRows(reportTable);
                while (it.hasNext()) {
                    final Row row = it.next();
                    if (row != null) {
                        view_id = (Integer)row.get("VIEW_ID");
                    }
                    list.add(view_id);
                }
            }
            EmailReportsUtil.out.log(Level.INFO, "Inside the getSelectedReports Method " + dataObject);
        }
        catch (final Exception ee) {
            EmailReportsUtil.out.log(Level.INFO, "Error in getSelectedReports method " + ee);
        }
        return list;
    }
    
    static {
        EmailReportsUtil.className = EmailReportsUtil.class.getName();
        EmailReportsUtil.out = Logger.getLogger(EmailReportsUtil.className);
    }
}
