package com.adventnet.persistence;

import java.util.Collection;
import com.adventnet.ds.query.SelectQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.DeleteQuery;
import java.util.List;
import java.io.Serializable;

public class OperationInfo implements Serializable, Immutable
{
    private boolean bulk;
    private List selectQueries;
    private DataObject dataObject;
    private List tableNames;
    private List origTableNames;
    private List bulkTableNames;
    private List inputDeleteCriterias;
    private DeleteQuery inputDeleteQuery;
    private UpdateQuery inputUpdateQuery;
    public static final int ALL = -1;
    public static final int ADD = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final int ADD_UPDATE = 4;
    public static final int ADD_DELETE = 5;
    public static final int UPDATE_DELETE = 6;
    private String dsName;
    private int operation;
    private Map messageProperties;
    
    public OperationInfo() {
        this.dsName = null;
        this.operation = -1;
        this.messageProperties = null;
    }
    
    public boolean isBulk() {
        return this.bulk;
    }
    
    public void setBulk(final boolean v) {
        this.bulk = v;
    }
    
    public List getSelectQueries() {
        return this.selectQueries;
    }
    
    public void setSelectQueries(final List sqs) {
        this.selectQueries = sqs;
    }
    
    public DataObject getDataObject() {
        this.resetDeleteFlag(this.dataObject);
        return this.dataObject;
    }
    
    public void setDataObject(final DataObject v) {
        this.dataObject = v;
    }
    
    public List getTableNames() {
        return this.tableNames;
    }
    
    public void setTableNames(final List tableNames) {
        this.tableNames = tableNames;
    }
    
    public List getOrigTableNames() {
        return this.origTableNames;
    }
    
    public void setOrigTableNames(final List tableNames) {
        this.origTableNames = tableNames;
    }
    
    public List getBulkTableNames() {
        return this.bulkTableNames;
    }
    
    public void setBulkTableNames(final List tableNames) {
        this.bulkTableNames = tableNames;
    }
    
    public List getInputDeleteCriterias() {
        return this.inputDeleteCriterias;
    }
    
    public void setInputDeleteCriterias(final List criteriaList) {
        this.inputDeleteCriterias = criteriaList;
    }
    
    public DeleteQuery getInputDeleteQuery() {
        return this.inputDeleteQuery;
    }
    
    public void setInputDeleteQuery(final DeleteQuery query) {
        this.inputDeleteQuery = query;
    }
    
    public UpdateQuery getInputUpdateQuery() {
        return this.inputUpdateQuery;
    }
    
    public void setInputUpdateQuery(final UpdateQuery query) {
        this.inputUpdateQuery = query;
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("<OperationInfo>\n");
        buf.append("<DSName>").append(this.dsName).append("</DSName>\n");
        buf.append("<OperationType>").append(this.getOperationString(this.operation)).append("</OperationType>\n");
        buf.append("<bulk>").append(this.bulk).append("</bulk>\n");
        buf.append("<SelectQueries>\n").append(this.selectQueries).append("</SelectQueries>\n");
        buf.append("<DataObject>\n").append(this.dataObject).append("</DataObject>\n");
        buf.append("<TableNames>\n").append(this.tableNames).append("</TableNames>\n");
        buf.append("<OriginalTableNames>\n").append(this.origTableNames).append("</OriginalTableNames>\n");
        buf.append("<BulkTableNames>\n").append(this.bulkTableNames).append("</BulkTableNames>\n");
        buf.append("<InputDeleteCriteria>\n").append(this.inputDeleteCriterias).append("</InputDeleteCriteria>\n");
        if (this.inputDeleteQuery != null) {
            buf.append("<InputDeleteQuery>\n").append(this.inputDeleteQuery).append("</InputDeleteQuery>\n");
        }
        if (this.inputUpdateQuery != null) {
            buf.append("<InputUpdateQuery>\n").append(this.inputUpdateQuery).append("</InputUpdateQuery>\n");
        }
        buf.append("</OperationInfo>");
        return buf.toString();
    }
    
    @Override
    public boolean equals(final Object info) {
        if (info == null) {
            return false;
        }
        if (this == info) {
            return true;
        }
        if (!(info instanceof OperationInfo)) {
            return false;
        }
        final OperationInfo opInfo = (OperationInfo)info;
        if (opInfo.isBulk() != this.isBulk()) {
            return false;
        }
        if (this.getBulkTableNames() != null) {
            if (opInfo.getBulkTableNames() == null) {
                return false;
            }
            if (!this.compareList(opInfo.getBulkTableNames(), this.getBulkTableNames())) {
                return false;
            }
        }
        else if (opInfo.getBulkTableNames() != null && opInfo.getBulkTableNames().size() != 0) {
            return false;
        }
        if (this.getTableNames() != null) {
            if (opInfo.getTableNames() == null) {
                return false;
            }
            if (!this.compareList(this.getTableNames(), opInfo.getTableNames())) {
                return false;
            }
        }
        else if (opInfo.getTableNames() != null && opInfo.getTableNames().size() != 0) {
            return false;
        }
        if (this.getInputDeleteQuery() != null) {
            if (opInfo.getInputDeleteQuery() == null) {
                return false;
            }
            if (!opInfo.getInputDeleteQuery().equals(this.getInputDeleteQuery())) {
                return false;
            }
        }
        else if (opInfo.getInputDeleteQuery() != null) {
            return false;
        }
        if (this.getInputUpdateQuery() != null) {
            if (opInfo.getInputUpdateQuery() == null) {
                return false;
            }
            if (!opInfo.getInputUpdateQuery().equals(this.getInputUpdateQuery())) {
                return false;
            }
        }
        else if (opInfo.getInputUpdateQuery() != null) {
            return false;
        }
        if (this.getDataObject() != null) {
            if (opInfo.getDataObject() == null) {
                return false;
            }
            if (!opInfo.getDataObject().equals(this.getDataObject())) {
                return false;
            }
            if (!this.compareList(this.getDataObject().getOperations(), opInfo.getDataObject().getOperations())) {
                return false;
            }
        }
        else if (opInfo.getDataObject() != null) {
            return false;
        }
        final boolean equals = this.compareList(opInfo.getSelectQueries(), this.getSelectQueries());
        return equals;
    }
    
    private boolean compareList(final List list1, final List list2) {
        boolean equals = true;
        if (list1 == null) {
            equals = (list2 == null);
        }
        else if (list2 == null) {
            equals = false;
        }
        else {
            final int size = list1.size();
            if (list2.size() != size) {
                equals = false;
            }
            else {
                for (int i = 0; i < size; ++i) {
                    final Object sq = list1.get(i);
                    if (!list2.contains(sq)) {
                        equals = false;
                        break;
                    }
                }
            }
        }
        return equals;
    }
    
    private void resetDeleteFlag(final DataObject dObj) {
        if (dObj == null) {
            return;
        }
        final List actionInfos = dObj.getOperations();
        if (actionInfos.isEmpty()) {
            return;
        }
        for (final ActionInfo info : actionInfos) {
            if (info.getOperation() != 3) {
                continue;
            }
            final Row row = info.getValue();
            row.deletedAt = "-1";
        }
    }
    
    private String getOperationString(final int op) {
        switch (op) {
            case 1: {
                return "ADD";
            }
            case 2: {
                return "UPDATE";
            }
            case 3: {
                return "DELETE";
            }
            case 4: {
                return "ADD_UPDATE";
            }
            case 5: {
                return "ADD_DELETE";
            }
            case 6: {
                return "UPDATE_DELETE";
            }
            case -1: {
                return "ALL";
            }
            default: {
                return null;
            }
        }
    }
    
    private int getOperationInt(final String op) {
        if (op.equals("ADD")) {
            return 1;
        }
        if (op.equals("UPDATE")) {
            return 2;
        }
        if (op.equals("DELETE")) {
            return 3;
        }
        if (op.equals("ADD_UPDATE")) {
            return 4;
        }
        if (op.equals("ADD_DELETE")) {
            return 5;
        }
        if (op.equals("UPDATE_DELETE")) {
            return 6;
        }
        return -1;
    }
    
    public void setOperation(final String operationType) {
        this.operation = this.getOperationInt(operationType);
    }
    
    public int getOperation() {
        return this.operation;
    }
    
    public void setDSName(final String dataSourceName) {
        this.dsName = dataSourceName;
    }
    
    public String getDSName() {
        return this.dsName;
    }
    
    public void setMessageProperties(final Map messageProps) {
        this.messageProperties = messageProps;
    }
    
    public Map getMessageProperties() {
        return this.messageProperties;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final OperationInfo cloned = (OperationInfo)super.clone();
        if (this.dataObject != null) {
            cloned.dataObject = (DataObject)this.dataObject.clone();
        }
        if (this.selectQueries != null) {
            final int sqSize = this.selectQueries.size();
            final List newSQList = new ArrayList(sqSize);
            for (int i = 0; i < sqSize; ++i) {
                final SelectQuery thisQuery = this.selectQueries.get(i);
                final SelectQuery newQuery = (SelectQuery)thisQuery.clone();
                newSQList.add(newQuery);
            }
            cloned.selectQueries = newSQList;
        }
        cloned.bulkTableNames = ((this.bulkTableNames != null) ? new ArrayList(this.bulkTableNames) : new ArrayList());
        cloned.tableNames = ((this.tableNames != null) ? new ArrayList(this.tableNames) : new ArrayList());
        cloned.origTableNames = ((this.origTableNames != null) ? new ArrayList(this.origTableNames) : new ArrayList());
        cloned.bulk = this.bulk;
        return cloned;
    }
}
