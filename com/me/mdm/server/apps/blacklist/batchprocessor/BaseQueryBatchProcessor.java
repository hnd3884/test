package com.me.mdm.server.apps.blacklist.batchprocessor;

import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Range;
import java.util.HashMap;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;

public class BaseQueryBatchProcessor
{
    SelectQuery selectQuery;
    Column sortColumn;
    String recordCountTableName;
    BatchProcessorInterface batchProcessorInterface;
    public static final int PROCESS_AS_DO = 1;
    public static final int PROCESS_AS_DS = 2;
    int curSetRecordcount;
    
    public BaseQueryBatchProcessor(final BatchProcessorInterface batchProcessorInterface) {
        this.selectQuery = null;
        this.sortColumn = null;
        this.recordCountTableName = null;
        this.batchProcessorInterface = null;
        this.batchProcessorInterface = batchProcessorInterface;
    }
    
    public void setSelectQuery(final SelectQuery selectQuery, final Column sortColumn) {
        this.selectQuery = selectQuery;
        this.sortColumn = sortColumn;
        this.recordCountTableName = this.recordCountTableName;
        this.selectQuery.addSortColumn(new SortColumn(this.sortColumn, true));
    }
    
    public void performBatchProcessing(final int type, final int batchSize, final HashMap params) throws Exception {
        if (type == 1) {
            this.processAsDataObject(batchSize, params);
        }
        else if (type == 2) {
            this.processAsDataSet(batchSize, params);
        }
    }
    
    private void processAsDataObject(final int batchSize, final HashMap params) throws Exception {
        int startIndex = 1;
        int endIndex = batchSize;
        this.selectQuery.setRange(new Range(startIndex, batchSize));
        DataObject dataObject = null;
        do {
            dataObject = MDMUtil.getPersistence().get(this.selectQuery);
            if (!dataObject.isEmpty()) {
                this.batchProcessorInterface.processDOData(dataObject, params);
                startIndex += batchSize;
                endIndex += batchSize;
                this.selectQuery.setRange(new Range(startIndex, batchSize));
            }
        } while (!dataObject.isEmpty());
    }
    
    private void processAsDataSet(final int batchSize, final HashMap params) throws Exception {
        int startIndex = 1;
        int endIndex = batchSize;
        this.selectQuery.setRange(new Range(startIndex, batchSize));
        DMDataSetWrapper dataSet = null;
        int numRowRead = batchSize;
        do {
            dataSet = DMDataSetWrapper.executeQuery((Object)this.selectQuery);
            if (numRowRead > 0) {
                numRowRead = this.batchProcessorInterface.processDSData(dataSet, params);
                startIndex += batchSize;
                endIndex += batchSize;
                this.selectQuery.setRange(new Range(startIndex, batchSize));
            }
        } while (numRowRead > 0);
    }
}
