package com.adventnet.db.archive;

import java.util.Collections;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.sql.Timestamp;
import com.adventnet.persistence.Row;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.ds.query.ArchiveTable;
import java.util.Map;

public class ArchiveTableInfo
{
    private Map<String, ArchiveTable> tableNameVsArchiveTable;
    private List<String> listOfArchivedTableNames;
    private DataObject archiveTableDetails;
    static final Logger LOGGER;
    
    public ArchiveTableInfo(final ArchivePolicyInfo policy, final List<String> tableNames) throws DataAccessException {
        this.tableNameVsArchiveTable = new HashMap<String, ArchiveTable>();
        this.listOfArchivedTableNames = new ArrayList<String>();
        this.archiveTableDetails = new WritableDataObject();
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
            int count = 0;
            for (final String tableName : tableNames) {
                String arcTabName = policy.getArchivePattern();
                final long seed = System.currentTimeMillis();
                if (arcTabName != null && arcTabName.length() > 0) {
                    final Calendar cal = Calendar.getInstance();
                    if (arcTabName.contains("%u")) {
                        arcTabName = arcTabName.replaceAll("%u", dateFormat.format(new Date()) + "_" + count++);
                    }
                    else {
                        arcTabName += (seed + "_" + tableName).hashCode();
                    }
                }
                else {
                    arcTabName = "ARC_" + (seed + "_" + tableName).hashCode();
                }
                arcTabName = arcTabName.replace("-", "_");
                final String invisibleName = "__" + tableName;
                final Row row = new Row("ArchiveTableDetails");
                row.set("ARCHIVE_POLICY_ID", policy.getArchivePolicyID());
                row.set("ARCHIVED_TABLENAME", arcTabName);
                row.set("ACTUAL_TABLENAME", tableName);
                final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final java.sql.Date date = new java.sql.Date(seed);
                row.set("ARCHIVED_DATE", Timestamp.valueOf(sdf.format(date)));
                final String desc = tableName + " archived on " + date;
                row.set("DESCRIPTION", desc);
                ArchiveTableInfo.LOGGER.log(Level.INFO, "TableName :: {0}  -> {1} --> {2}", new Object[] { tableName, arcTabName, invisibleName });
                this.archiveTableDetails.addRow(row);
                this.listOfArchivedTableNames.add(arcTabName);
                final ArchiveTable arcTable = new ArchiveTable(tableName, arcTabName, invisibleName);
                this.tableNameVsArchiveTable.put(tableName, arcTable);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public DataObject updateDetails() throws DataAccessException {
        ArchiveTableInfo.LOGGER.log(Level.INFO, "Updating archive table details {0}", this.archiveTableDetails);
        DataAccess.add(this.archiveTableDetails);
        return this.archiveTableDetails;
    }
    
    public Map<String, ArchiveTable> getArchiveMap() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ArchiveTable>)this.tableNameVsArchiveTable);
    }
    
    public ArchiveTable getArchiveTable(final String originalTableName) {
        return this.tableNameVsArchiveTable.get(originalTableName);
    }
    
    public List<String> getListOfArchivedTableNames() {
        return Collections.unmodifiableList((List<? extends String>)this.listOfArchivedTableNames);
    }
    
    static {
        LOGGER = Logger.getLogger(ArchiveTableInfo.class.getName());
    }
}
