package com.me.mdm.server.backup;

import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import org.json.JSONArray;

public class MDMDocsBackupListener implements MDMBackupListener
{
    @Override
    public JSONArray getFileBackupDetails() throws Exception {
        final JSONArray data = new JSONArray();
        final String directoryToCopy = "Documents";
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DocumentDetails"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "DOC_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "SIZE"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentDetails", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ServerDocDetails", "LOCATION_PATH"));
        selectQuery.addJoin(new Join("DocumentDetails", "ServerDocDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)1, 0));
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("DocumentDetails", "CUSTOMER_ID"), true));
        int start = 1;
        int end;
        final int limit = end = 500;
        int counter;
        do {
            counter = 0;
            selectQuery.setRange(new Range(start, end));
            final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dmDataSetWrapper.next()) {
                ++counter;
                String docName = "" + dmDataSetWrapper.getValue("DOC_NAME");
                final String docLoc = "" + dmDataSetWrapper.getValue("LOCATION_PATH");
                final int docType = (int)dmDataSetWrapper.getValue("DOC_TYPE");
                final String docExtn = DocMgmtDataHandler.getInstance().getDocExtention(docType);
                docName += docExtn;
                final long fileSize = (long)dmDataSetWrapper.getValue("SIZE");
                final long customerId = (long)dmDataSetWrapper.getValue("CUSTOMER_ID");
                final JSONObject fileDet = new JSONObject();
                fileDet.put("fileName", (Object)docName);
                fileDet.put("filePath", (Object)docLoc);
                fileDet.put("fileSize", fileSize);
                fileDet.put("directoryToCopy", (Object)directoryToCopy);
                fileDet.put("customerId", customerId);
                data.put((Object)fileDet);
            }
            start += limit;
            end += limit;
        } while (counter == limit);
        return data;
    }
}
