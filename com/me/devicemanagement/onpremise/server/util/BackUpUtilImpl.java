package com.me.devicemanagement.onpremise.server.util;

import java.io.IOException;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.SelectQuery;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Column;
import java.io.File;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.BackUpUtil;

public class BackUpUtilImpl extends BackUpUtil
{
    private static Logger logger;
    
    public int getRowCount(final String tablename, final Criteria criteria) {
        int recordsCount = 0;
        try {
            final Row r = new Row(tablename);
            final String colName = r.getPKColumns().get(0);
            recordsCount = DBUtil.getRecordCount(tablename, colName, criteria);
            return recordsCount;
        }
        catch (final Exception ex) {
            BackUpUtilImpl.logger.log(Level.SEVERE, null, ex);
            return recordsCount;
        }
    }
    
    public void deleteRecentFile() {
        final File recentFile = new File(System.getProperty("server.home") + File.separator + this.location + File.separator + this.filename);
        if (recentFile.exists()) {
            BackUpUtilImpl.logger.log(Level.INFO, ".Deleting recent File as the database deletion failes");
            try {
                recentFile.delete();
                final Criteria criteria = new Criteria(new Column("DCCleanUpDataFiles", "FILE_NAME"), (Object)this.filename, 0);
                DataAccess.delete("DCCleanUpDataFiles", criteria);
                BackUpUtilImpl.logger.log(Level.INFO, this.filename + " is deleted and in DB entry too..");
            }
            catch (final DataAccessException ex) {
                BackUpUtilImpl.logger.log(Level.SEVERE, null, (Throwable)ex);
            }
        }
    }
    
    public boolean checkFileLimitIsReached() {
        return true;
    }
    
    public long getFolderSize(final long featureID) {
        Connection conn = null;
        long folderSize = 0L;
        try {
            final SelectQuery querybackUp = (SelectQuery)new SelectQueryImpl(Table.getTable("DCCleanUpDataFiles"));
            querybackUp.addSelectColumn(new Column("DCCleanUpDataFiles", "FILE_SIZE").summation());
            final Criteria criteria = new Criteria(new Column("DCCleanUpDataFiles", "FEATURE_ID"), (Object)featureID, 0);
            querybackUp.setCriteria(criteria);
            final RelationalAPI relapi = RelationalAPI.getInstance();
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)querybackUp, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    folderSize = (long)value;
                }
            }
            ds.close();
        }
        catch (final QueryConstructionException ex) {
            BackUpUtilImpl.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final SQLException ex2) {
            BackUpUtilImpl.logger.log(Level.SEVERE, null, ex2);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                BackUpUtilImpl.logger.log(Level.WARNING, "Error in closing connection.");
            }
        }
        return folderSize;
    }
    
    public ArrayList retrieveExcessFilesList(final String folderpath, final double folderSizeBytes) {
        final ArrayList filestoDelete = new ArrayList();
        if (this.getFolderSize(this.featureID) > folderSizeBytes) {
            final long foldersize = this.getFolderSize(this.featureID);
            final Long folderSizeLimitBytes = this.maxSizeOfFolder * 1024L * 1024L;
            BackUpUtilImpl.logger.log(Level.INFO, "Maximum Folder Size:" + folderSizeLimitBytes + "\t Existing size:" + foldersize);
            SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCCleanUpDataFiles"));
            final Criteria featureIdCri = new Criteria(new Column("DCCleanUpDataFiles", "FEATURE_ID"), (Object)this.featureID, 0);
            query.setCriteria(featureIdCri);
            final SortColumn sc = new SortColumn(new Column("DCCleanUpDataFiles", "CREATED_TIME"), false);
            final ArrayList FilenamesRecent = new ArrayList();
            int recordsCount = 0;
            long totalFileSize = 0L;
            boolean thresholdReached = false;
            Connection conn = null;
            try {
                recordsCount = this.getRowCount("DCCleanUpDataFiles", featureIdCri);
                int i = 0;
                while (i <= recordsCount) {
                    query.setRange(new Range(i, this.rowFetchLimit));
                    query.addSortColumn(sc);
                    query.addSelectColumn(new Column("DCCleanUpDataFiles", "FILE_SIZE"));
                    query.addSelectColumn(new Column("DCCleanUpDataFiles", "FILE_NAME"));
                    RelationalAPI relapi = RelationalAPI.getInstance();
                    conn = relapi.getConnection();
                    DataSet ds = relapi.executeQuery((Query)query, conn);
                    while (ds.next()) {
                        final long filesize = (long)ds.getValue(1);
                        totalFileSize += filesize;
                        if (totalFileSize < folderSizeLimitBytes) {
                            FilenamesRecent.add(ds.getValue(2));
                        }
                        else {
                            thresholdReached = true;
                        }
                    }
                    if (ds != null) {
                        ds.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                    if (thresholdReached) {
                        query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCCleanUpDataFiles"));
                        query.addSelectColumn(new Column("DCCleanUpDataFiles", "FILE_NAME"));
                        final Criteria delCrit = new Criteria(new Column("DCCleanUpDataFiles", "FILE_NAME"), (Object)FilenamesRecent.toArray(), 9);
                        query.setCriteria(featureIdCri.and(delCrit));
                        relapi = RelationalAPI.getInstance();
                        conn = relapi.getConnection();
                        ds = relapi.executeQuery((Query)query, conn);
                        while (ds.next()) {
                            filestoDelete.add(ds.getValue(1));
                        }
                        BackUpUtilImpl.logger.log(Level.INFO, "files List to delete:" + filestoDelete);
                        DataAccess.delete("DCCleanUpDataFiles", delCrit.and(featureIdCri));
                        if (ds != null) {
                            ds.close();
                        }
                        if (conn != null) {
                            conn.close();
                            break;
                        }
                        break;
                    }
                    else {
                        i += this.rowFetchLimit;
                    }
                }
            }
            catch (final Exception ex) {
                BackUpUtilImpl.logger.log(Level.SEVERE, null, ex);
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (final Exception ex2) {
                    BackUpUtilImpl.logger.log(Level.WARNING, "Error in closing connection.");
                }
            }
            finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (final Exception ex3) {
                    BackUpUtilImpl.logger.log(Level.WARNING, "Error in closing connection.");
                }
            }
        }
        return filestoDelete;
    }
    
    public String updateFileName() {
        return this.filename;
    }
    
    public String createNewFile() throws Exception {
        try {
            final String filepath = System.getProperty("server.home") + File.separator + this.location + File.separator + this.filename;
            final File fileCsvName = new File(filepath);
            final File parent = fileCsvName.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            fileCsvName.createNewFile();
            return filepath;
        }
        catch (final IOException e) {
            BackUpUtilImpl.logger.log(Level.SEVERE, "Exception while creating new file");
            throw new Exception(e);
        }
    }
    
    static {
        BackUpUtilImpl.logger = Logger.getLogger(BackUpUtil.class.getName());
    }
}
