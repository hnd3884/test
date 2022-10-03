package com.me.devicemanagement.framework.server.util;

import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataObject;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessAPI;
import java.util.Iterator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.ds.query.Range;
import java.sql.SQLException;
import java.io.IOException;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.RandomAccessFile;

public abstract class BackUpUtil
{
    protected String featureName;
    protected String location;
    protected String filename;
    protected long maxSizeOfFolder;
    protected RandomAccessFile memoryMappedFile;
    protected long featureID;
    protected boolean i18N;
    protected boolean isArgsInI18N;
    protected ArrayList<Integer> i18Nlist;
    protected HashMap<Integer, Integer> pairs;
    protected int rowFetchLimit;
    private boolean readOnlyMode;
    private static Logger logger;
    
    public BackUpUtil() {
        this.location = "";
        this.filename = "";
        this.maxSizeOfFolder = 0L;
        this.i18N = false;
        this.isArgsInI18N = false;
        this.i18Nlist = new ArrayList<Integer>();
        this.pairs = new HashMap<Integer, Integer>();
        this.rowFetchLimit = 1000;
        this.readOnlyMode = true;
    }
    
    public boolean isReadOnlyMode() {
        return this.readOnlyMode;
    }
    
    public void setReadOnlyMode(final boolean readOnlyMode) {
        this.readOnlyMode = readOnlyMode;
    }
    
    public String executeBackUp(final String module, final String tableName, final SelectQuery query, final Criteria criteria, final boolean i18N) {
        this.executeBackUp(module, tableName, query, criteria, i18N, false, null);
        return this.filename;
    }
    
    public String executeBackUp(final String module, final String tableName, final SelectQuery query, final Criteria criteria, final boolean i18n, final ArrayList<Integer> list) {
        this.i18N = i18n;
        this.i18Nlist = list;
        this.executeBackUp(module, tableName, query, criteria, this.i18N, false, null);
        return this.filename;
    }
    
    public String executeBackUp(final String module, final String tableName, final SelectQuery query, final Criteria criteria, final boolean isI18n, final boolean isArgs, final HashMap pair) {
        this.i18N = isI18n;
        this.isArgsInI18N = isArgs;
        this.pairs = pair;
        final boolean isFeature = this.getPropertiesOfModule(module);
        if (isFeature) {
            this.backUpToCSV(tableName, query, criteria);
        }
        return this.filename;
    }
    
    public int getRowCount(final String tablename, final Criteria criteria) {
        int recordsCount = 0;
        try {
            final Row r = new Row(tablename);
            final String colName = r.getPKColumns().get(0);
            if (this.isReadOnlyMode()) {
                recordsCount = ReadOnlyDBUtil.getRecordCount(tablename, colName, criteria);
            }
            else {
                recordsCount = DBUtil.getRecordCount(tablename, colName, criteria);
            }
            return recordsCount;
        }
        catch (final Exception ex) {
            BackUpUtil.logger.log(Level.SEVERE, null, ex);
            return recordsCount;
        }
    }
    
    public static boolean checkString(final int columnTypeValue) {
        return columnTypeValue == -9 || columnTypeValue == -16 || columnTypeValue == -15 || columnTypeValue == 12 || columnTypeValue == -1 || columnTypeValue == 1;
    }
    
    public static String convertString(String value) {
        value = value.replaceAll("[\"]", "");
        value = "\"" + value + "\"";
        return value;
    }
    
    private boolean writeBackUpFile(final DMDataSetWrapper ds, final int columnsCount, final double folderSizeBytes) {
        try {
            StringBuffer tempString = new StringBuffer("");
            while (ds.next()) {
                try {
                    tempString = new StringBuffer();
                    for (int columnvalue = 1; columnvalue <= columnsCount; ++columnvalue) {
                        final int columnTypeValue = ds.getColumnType(columnvalue);
                        if (ds.getValue(columnvalue) != null) {
                            if (this.i18N && this.i18Nlist.contains(columnvalue)) {
                                try {
                                    final String value = (String)ds.getValue(columnvalue);
                                    tempString.append(I18N.getMsg(value, new Object[0]));
                                }
                                catch (final Exception ex) {
                                    BackUpUtil.logger.log(Level.SEVERE, null, ex);
                                }
                            }
                            else if (checkString(columnTypeValue)) {
                                String value = (String)ds.getValue(columnvalue);
                                value = convertString(value);
                                tempString.append(value);
                            }
                            else {
                                tempString.append(ds.getValue(columnvalue));
                            }
                        }
                        else {
                            tempString.append("-");
                        }
                        if (columnvalue != columnsCount) {
                            tempString.append(",");
                        }
                        else {
                            tempString.append("\n");
                        }
                    }
                    if (tempString.length() + this.memoryMappedFile.length() < folderSizeBytes) {
                        this.memoryMappedFile.writeBytes((Object)tempString + "");
                    }
                    else {
                        if (this.checkFileLimitIsReached()) {
                            return true;
                        }
                        this.memoryMappedFile.writeBytes((Object)tempString + "");
                    }
                }
                catch (final IOException ex2) {
                    BackUpUtil.logger.log(Level.SEVERE, null, ex2);
                }
            }
        }
        catch (final SQLException ex3) {
            BackUpUtil.logger.log(Level.SEVERE, null, ex3);
        }
        return false;
    }
    
    private boolean writeBackUpFileWithArgs(final DMDataSetWrapper ds, final int columnsCount, final double folderSizeBytes) {
        try {
            StringBuffer tempString = new StringBuffer("");
            while (ds.next()) {
                try {
                    tempString = new StringBuffer();
                    for (int columnvalue = 1; columnvalue <= columnsCount; ++columnvalue) {
                        final int columnTypeValue = ds.getColumnType(columnvalue);
                        if (!this.pairs.containsValue(columnvalue)) {
                            if (ds.getValue(columnvalue) != null) {
                                if (this.i18N && this.isArgsInI18N && this.pairs != null && this.pairs.containsKey(columnvalue)) {
                                    try {
                                        final int valueIndex = this.pairs.get(columnvalue);
                                        final String remarksArgs = (String)ds.getValue(valueIndex);
                                        final String modulenameToCheck = (String)ds.getValue(2);
                                        if (this.featureName.equals("ActionLog") & modulenameToCheck.equalsIgnoreCase("Inventory Mgmt")) {
                                            final String value = ds.getValue(columnvalue) + "";
                                            String columnValue = I18NUtil.transformRemarksAndArguments(value, remarksArgs);
                                            columnValue = convertString(columnValue);
                                            tempString.append(columnValue);
                                        }
                                        else {
                                            final String value = (String)ds.getValue(columnvalue);
                                            String columnValue = I18NUtil.transformRemarks(value, remarksArgs);
                                            columnValue = convertString(columnValue);
                                            tempString.append(columnValue);
                                        }
                                    }
                                    catch (final Exception ex) {
                                        BackUpUtil.logger.log(Level.SEVERE, null, ex);
                                    }
                                }
                                else if (checkString(columnTypeValue)) {
                                    String value2 = (String)ds.getValue(columnvalue);
                                    value2 = convertString(value2);
                                    tempString.append(value2);
                                }
                                else {
                                    tempString.append(ds.getValue(columnvalue));
                                }
                            }
                            else {
                                tempString.append("-");
                            }
                            if (columnvalue != columnsCount) {
                                tempString.append(",");
                            }
                            else {
                                tempString.append("\n");
                            }
                        }
                    }
                    if (tempString.length() + this.memoryMappedFile.length() < folderSizeBytes) {
                        this.memoryMappedFile.writeBytes((Object)tempString + "");
                    }
                    else {
                        if (this.checkFileLimitIsReached()) {
                            return true;
                        }
                        this.memoryMappedFile.writeBytes((Object)tempString + "");
                    }
                }
                catch (final IOException ex2) {
                    BackUpUtil.logger.log(Level.SEVERE, null, ex2);
                }
            }
        }
        catch (final SQLException ex3) {
            BackUpUtil.logger.log(Level.SEVERE, null, ex3);
        }
        return false;
    }
    
    public void backUpToCSV(final String tablename, final SelectQuery query, final Criteria criteria) {
        try {
            String folderpath = "";
            double folderSizeBytes = 0.0;
            final int recordsCount = this.getRowCount(tablename, criteria);
            BackUpUtil.logger.log(Level.INFO, "Total Records to take back Up:" + recordsCount);
            if (recordsCount <= 0) {
                return;
            }
            folderpath = this.createCsvFile(query, tablename, criteria);
            final int columnsCount = this.writeHeadersAndGetCount(query, tablename, criteria);
            folderSizeBytes = (double)(this.maxSizeOfFolder * 1024L * 1024L);
            boolean isFileMaxReached = false;
            for (int i = 1; i <= recordsCount; i += this.rowFetchLimit) {
                final Range range = new Range(i, this.rowFetchLimit);
                query.setRange(range);
                DMDataSetWrapper ds = null;
                if (this.isReadOnlyMode()) {
                    ds = DMReadOnlyDataSetWrapper.executeQuery(query);
                }
                else {
                    ds = DMDataSetWrapper.executeQuery(query);
                }
                if (!this.isArgsInI18N) {
                    isFileMaxReached = this.writeBackUpFile(ds, columnsCount, folderSizeBytes);
                }
                else {
                    isFileMaxReached = this.writeBackUpFileWithArgs(ds, columnsCount, folderSizeBytes);
                }
                if (isFileMaxReached) {
                    break;
                }
            }
            this.updateSizeColumn();
            this.filename = this.updateFileName();
            final ArrayList filestoDelete = this.retrieveExcessFilesList(folderpath, folderSizeBytes);
            if (!filestoDelete.isEmpty()) {
                this.deleteFilesInLocation(filestoDelete, folderpath);
            }
        }
        catch (final Exception ex) {
            BackUpUtil.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateSizeColumn() {
        try {
            final UpdateQuery query = (UpdateQuery)new UpdateQueryImpl("DCCleanUpDataFiles");
            final Criteria crit = new Criteria(Column.getColumn("DCCleanUpDataFiles", "FILE_NAME"), (Object)this.filename, 0);
            query.setCriteria(crit);
            query.setUpdateColumn("FILE_SIZE", (Object)this.memoryMappedFile.length());
            SyMUtil.getPersistence().update(query);
            this.memoryMappedFile.close();
            BackUpUtil.logger.log(Level.INFO, "Size of file is updated in DB");
        }
        catch (final IOException ex) {
            BackUpUtil.logger.log(Level.SEVERE, null, ex);
        }
        catch (final DataAccessException ex2) {
            BackUpUtil.logger.log(Level.SEVERE, null, (Throwable)ex2);
        }
        catch (final Exception e) {
            BackUpUtil.logger.log(Level.SEVERE, null, e);
        }
    }
    
    public void deleteFilesInLocation(final ArrayList<String> filestoDelete, final String folderpath) {
        BackUpUtil.logger.log(Level.INFO, "Deleting Files");
        try {
            for (final String filename : filestoDelete) {
                final FileAccessAPI fileAccessAPI = ApiFactoryProvider.getFileAccessAPI();
                if (fileAccessAPI.isFileExists(folderpath + filename)) {
                    fileAccessAPI.deleteFile(folderpath + filename);
                }
            }
        }
        catch (final Exception e) {
            BackUpUtil.logger.log(Level.SEVERE, "Exception while cleaning up files");
        }
    }
    
    public int writeHeadersAndGetCount(final SelectQuery query, final String tablename, final Criteria criteria) {
        int columnsCount = 0;
        try {
            final List columnsList = query.getSelectColumns();
            columnsCount = columnsList.size();
            int i;
            Boolean c;
            for (i = 0, i = 0; i < columnsList.size() - 1; ++i) {
                if (this.pairs != null) {
                    c = this.pairs.containsValue(i + 1);
                    if (c) {
                        continue;
                    }
                }
                this.memoryMappedFile.writeBytes(columnsList.get(i) + ",");
            }
            this.memoryMappedFile.writeBytes(columnsList.get(i) + "\n");
        }
        catch (final IOException ex) {
            BackUpUtil.logger.log(Level.SEVERE, null, ex);
        }
        catch (final Exception ex2) {
            BackUpUtil.logger.log(Level.SEVERE, null, ex2);
        }
        return columnsCount;
    }
    
    public String createCsvFile(final SelectQuery query, final String tablename, final Criteria criteria) {
        String folderpath = "";
        try {
            BackUpUtil.logger.log(Level.INFO, "Creating CSV File");
            final SimpleDateFormat formatter = new SimpleDateFormat("-yy-MM-dd-HH-mm-ss-SSS");
            this.filename = this.filename.replace("-date", formatter.format(new Date()));
            folderpath = System.getProperty("server.home") + File.separator + this.location + File.separator;
            final String filepath = this.createNewFile();
            this.memoryMappedFile = new RandomAccessFile(filepath, "rw");
            BackUpUtil.logger.log(Level.INFO, "File is created in Location:" + filepath);
            try {
                final DataObject dObj = SyMUtil.getPersistence().constructDataObject();
                final Row r = new Row("DCCleanUpDataFiles");
                r.set("FEATURE_ID", (Object)this.featureID);
                r.set("FILE_NAME", (Object)this.filename);
                r.set("CREATED_TIME", (Object)System.currentTimeMillis());
                r.set("FILE_SIZE", (Object)this.memoryMappedFile.length());
                dObj.addRow(r);
                SyMUtil.getPersistence().add(dObj);
                BackUpUtil.logger.log(Level.INFO, "Added to database:" + r);
            }
            catch (final DataAccessException ex) {
                BackUpUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
            }
        }
        catch (final IOException ex2) {
            BackUpUtil.logger.log(Level.SEVERE, null, ex2);
        }
        catch (final Exception e) {
            BackUpUtil.logger.log(Level.SEVERE, "Exception Occured", e);
        }
        return folderpath;
    }
    
    public boolean getPropertiesOfModule(final String module) {
        try {
            this.featureName = module;
            final Criteria criteria = new Criteria(new Column("DCCleanUpDataInfo", "FEATURE_NAME"), (Object)module, 0);
            final DataObject dObj = DataAccess.get("DCCleanUpDataInfo", criteria);
            if (dObj.isEmpty()) {
                BackUpUtil.logger.log(Level.INFO, "Invalid Feature Name");
                return false;
            }
            final Row oldRow = dObj.getFirstRow("DCCleanUpDataInfo");
            this.filename = (String)oldRow.get("FILE_NAME");
            this.location = (String)oldRow.get("BACKUP_LOCATION");
            this.location = this.location.replace("/", File.separator);
            this.maxSizeOfFolder = (long)oldRow.get("MAX_FOLDER_SIZE");
            this.featureID = (long)oldRow.get("FEATURE_ID");
        }
        catch (final DataAccessException ex) {
            BackUpUtil.logger.log(Level.SEVERE, null, (Throwable)ex);
            return false;
        }
        catch (final Exception e) {
            BackUpUtil.logger.log(Level.INFO, "Error occured while retreiving Properties", e);
            return false;
        }
        return true;
    }
    
    public abstract String updateFileName();
    
    public abstract void deleteRecentFile();
    
    public abstract ArrayList retrieveExcessFilesList(final String p0, final double p1);
    
    public abstract boolean checkFileLimitIsReached();
    
    public abstract String createNewFile() throws Exception;
    
    static {
        BackUpUtil.logger = Logger.getLogger(BackUpUtil.class.getName());
    }
}
