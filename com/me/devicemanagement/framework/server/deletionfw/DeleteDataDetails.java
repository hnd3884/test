package com.me.devicemanagement.framework.server.deletionfw;

import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Criteria;
import java.util.List;

public class DeleteDataDetails
{
    public String tableName;
    public List<String> tablesList;
    public Criteria criteria;
    public List<GroupByClause> groupByClauseList;
    public List<Column> nonDuplicateColumnList;
    public DeleteQuery deleteQuery;
    public SelectQuery selectQuery;
    public int chunkThreshold;
    public int groupThreshold;
    public long totalParentRowsDeleted;
    public long parentDeletionStartTime;
    public long parentDeletionDuration;
    public Long taskID;
    public List<Long> excludeTaskIds;
    public String deletedData;
    public boolean isPersistenceDeletion;
    public String statusKey;
    public ProcessType processType;
    public boolean isDeletionAsynchronous;
    public OPERATION_TYPE operationType;
    
    public DeleteDataDetails() {
        this.tableName = null;
        this.tablesList = null;
        this.criteria = null;
        this.groupByClauseList = null;
        this.nonDuplicateColumnList = null;
        this.deleteQuery = null;
        this.selectQuery = null;
        this.chunkThreshold = DeletionFWProps.chunkThreshold;
        this.groupThreshold = DeletionFWProps.groupThreshold;
        this.excludeTaskIds = null;
        this.deletedData = null;
        this.processType = ProcessType.DEPENDENT_DELETION;
        this.isDeletionAsynchronous = true;
    }
    
    public enum ProcessType
    {
        DEPENDENT_DELETION, 
        CLEANUP, 
        ADDITION_PRE_HANDLING;
    }
}
