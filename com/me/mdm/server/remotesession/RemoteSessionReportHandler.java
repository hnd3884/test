package com.me.mdm.server.remotesession;

import java.util.Iterator;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONObject;

public class RemoteSessionReportHandler
{
    public void addOrUpdateRemoteSessionReport(final Long sessionId, final JSONObject reportJSON) throws DataAccessException, JSONException {
        final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("RemoteSessionReport"));
        final Criteria cmdHistCriteria = new Criteria(new Column("RemoteSessionReport", "SESSION_ID"), (Object)sessionId, 0);
        sQuery.setCriteria(cmdHistCriteria);
        sQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dO = DataAccess.get(sQuery);
        if (!dO.isEmpty()) {
            Row row = dO.getRow("RemoteSessionReport");
            row = this.fillUpRow(reportJSON, row);
            dO.updateRow(row);
        }
        else {
            Row row = new Row("RemoteSessionReport");
            row.set("SESSION_ID", (Object)sessionId);
            row = this.fillUpRow(reportJSON, row);
            dO.addRow(row);
        }
        DataAccess.update(dO);
    }
    
    private Row fillUpRow(final JSONObject reportJSON, final Row row) throws JSONException {
        final Iterator it = reportJSON.keys();
        while (it.hasNext()) {
            final String key = it.next();
            row.set(key, reportJSON.get(key));
        }
        return row;
    }
}
