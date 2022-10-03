package com.me.devicemanagement.framework.server.customreport;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Hashtable;
import java.util.logging.Logger;

public class CustomReportCriteriaUtil
{
    private static Logger out;
    private static CustomReportCriteriaUtil criteria;
    
    public static synchronized CustomReportCriteriaUtil getInstance() {
        if (CustomReportCriteriaUtil.criteria == null) {
            CustomReportCriteriaUtil.criteria = new CustomReportCriteriaUtil();
        }
        return CustomReportCriteriaUtil.criteria;
    }
    
    public Long[] addExtraColumns(final String[] colList) {
        final Hashtable colHash = new Hashtable();
        int count = 0;
        final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumnInternalMap"));
        selQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        final Long[] extraColumns = new Long[10];
        try {
            final DataObject columnsDO = SyMUtil.getPersistence().get(selQuery);
            final Iterator colIDs = columnsDO.getRows("CRColumnInternalMap");
            while (colIDs.hasNext()) {
                final Row row = colIDs.next();
                final Long rowid = (Long)row.get("COLUMN_ID");
                final Long associatedid = (Long)row.get("ASSOCIATED_COLUMN_ID");
                colHash.put(rowid, associatedid);
            }
            for (int j = 0; j < colList.length; ++j) {
                final Long colid = new Long(colList[j]);
                final Long asscoiatedID = colHash.get(colid);
                if (asscoiatedID != null) {
                    int check = 0;
                    for (int k = 0; k < extraColumns.length; ++k) {
                        if (extraColumns[k] != null && extraColumns[k] == asscoiatedID) {
                            check = 1;
                        }
                    }
                    if (check == 0) {
                        extraColumns[count++] = asscoiatedID;
                        CustomReportCriteriaUtil.out.log(Level.INFO, "Extra column ID is " + asscoiatedID);
                    }
                }
            }
        }
        catch (final Exception e) {
            CustomReportCriteriaUtil.out.log(Level.WARNING, "Exception is ", e);
        }
        CustomReportCriteriaUtil.out.log(Level.INFO, "EXtra columns " + extraColumns);
        return extraColumns;
    }
    
    static {
        CustomReportCriteriaUtil.out = Logger.getLogger("CustomReportLogger");
        CustomReportCriteriaUtil.criteria = null;
    }
}
