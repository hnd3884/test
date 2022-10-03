package com.me.devicemanagement.framework.webclient.admin;

import java.sql.Connection;
import com.adventnet.ds.query.Range;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.TableDatasetModel;

public interface DBQueryExecutorAPI
{
    default TableDatasetModel getTableModel(final String query, final String countSql, final boolean isUpdate) throws Exception {
        return DBQueryExecutorHandler.getAsTableModel(query, countSql, null, true, null, null, isUpdate);
    }
    
    Connection getConnection(final String p0) throws Exception;
}
