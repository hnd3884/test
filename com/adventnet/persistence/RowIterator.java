package com.adventnet.persistence;

import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.util.QueryUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import com.zoho.conf.AppResources;
import java.util.Map;
import com.adventnet.ds.query.Join;
import java.util.List;
import java.util.logging.Logger;
import java.util.Iterator;

public class RowIterator implements Iterator
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    private int currentIndex;
    private boolean hasNext;
    private boolean previousIdxValid;
    private int previousIndex;
    private List valueList;
    private DataObject dataObject;
    private String tableName;
    private Row condition;
    private Join join;
    private boolean hasNextVal;
    private transient int expectedModCount;
    private Map criteriaMap;
    private List rowColList;
    private List conColList;
    private int[] baseColumnIndices;
    private int[] refColumnIndices;
    
    RowIterator(final List valueList, final String tableName, final Row condition, final Join join, final DataObject dataObject) {
        this.currentIndex = -1;
        this.hasNext = false;
        this.previousIdxValid = false;
        this.previousIndex = -1;
        this.valueList = null;
        this.dataObject = null;
        this.tableName = null;
        this.condition = null;
        this.hasNextVal = false;
        this.criteriaMap = null;
        this.rowColList = null;
        this.conColList = null;
        this.baseColumnIndices = null;
        this.refColumnIndices = null;
        if (valueList == null) {
            throw new NullPointerException("Can not iterate through a null list");
        }
        this.expectedModCount = ((WritableDataObject)dataObject).modCount;
        this.valueList = valueList;
        this.tableName = tableName;
        this.condition = condition;
        this.join = join;
        this.dataObject = dataObject;
        this.initJoin();
        this.findNext();
    }
    
    private void checkForConcurrentMod() {
        if (AppResources.getString("do.iterator.exception.type", "").equalsIgnoreCase("concurrent") && this.expectedModCount != ((WritableDataObject)this.dataObject).modCount) {
            throw new ConcurrentModificationException("Modifying data object during iteration is illegal/not supported.");
        }
    }
    
    @Override
    public boolean hasNext() {
        return this.hasNextVal;
    }
    
    @Override
    public Object next() {
        this.checkForConcurrentMod();
        if (!this.hasNextVal) {
            throw new NoSuchElementException("Already at the end");
        }
        final Object nextVal = this.valueList.get(this.currentIndex);
        this.previousIndex = this.currentIndex;
        this.findNext();
        this.previousIdxValid = true;
        return nextVal;
    }
    
    @Override
    public void remove() {
        this.checkForConcurrentMod();
        try {
            if (!this.previousIdxValid) {
                throw new DataAccessException("Iterator.next() should be called before calling remove.");
            }
            final Row removedRow = this.valueList.get(this.previousIndex);
            final int deletedIndex = ((WritableDataObject)this.dataObject).deleteAndReturnIndex(removedRow, true);
            this.expectedModCount = ((WritableDataObject)this.dataObject).modCount;
            this.currentIndex = deletedIndex - 1;
            this.findNext();
            this.previousIdxValid = false;
        }
        catch (final DataAccessException excp) {
            final String message = "Exception occured while trying to remove Row from iterator";
            throw new RuntimeException(message, excp);
        }
    }
    
    public void removeIgnoreFK() {
        this.checkForConcurrentMod();
        try {
            if (!this.previousIdxValid) {
                throw new DataAccessException("Iterator.next() should be called before calling remove.");
            }
            final Row removedRow = this.valueList.get(this.previousIndex);
            final int deletedIndex = ((WritableDataObject)this.dataObject).deleteAndReturnIndex(removedRow, true, false);
            this.expectedModCount = ((WritableDataObject)this.dataObject).modCount;
            this.currentIndex = deletedIndex - 1;
            this.findNext();
            this.previousIdxValid = false;
        }
        catch (final DataAccessException excp) {
            final String message = "Exception occured while trying to remove Row from iterator";
            throw new RuntimeException(message, excp);
        }
    }
    
    private void findNext() {
        if (this.tableName == null) {
            ++this.currentIndex;
            this.hasNextVal = (this.currentIndex < this.valueList.size());
            return;
        }
        this.hasNextVal = false;
        while (!this.hasNextVal && ++this.currentIndex < this.valueList.size()) {
            final Row row = this.valueList.get(this.currentIndex);
            final String tableName = row.getTableName();
            if (tableName.equals(this.tableName)) {
                if (this.condition == null) {
                    this.hasNextVal = true;
                }
                else {
                    try {
                        if (this.join == null) {
                            this.hasNextVal = PersistenceUtil.match(row, this.condition);
                        }
                        else if (this.join.getCriteria() == null) {
                            this.hasNextVal = (this.baseColumnIndices != null && PersistenceUtil.matchRows(row, this.baseColumnIndices, this.condition, this.refColumnIndices));
                        }
                        else {
                            this.fillMapWithValues(row, false);
                            this.fillMapWithValues(this.condition, true);
                            this.hasNextVal = this.join.getCriteria().matches(this.getMap());
                        }
                    }
                    catch (final DataAccessException dae) {
                        RowIterator.OUT.log(Level.WARNING, "Exception occured during iterating through rows in RowIterator. Setting hasNext=false.", dae);
                        this.hasNextVal = false;
                    }
                }
            }
        }
    }
    
    private void processJoin() throws DataAccessException {
        if (this.join != null && this.join.getCriteria() == null) {
            if (this.join.getBaseTableColumnIndices() == null) {
                PersistenceUtil.populateColumnIndicesInformation(this.join);
            }
            if (this.valueList.get(0).getOriginalTableName().equals(this.join.getBaseTableName())) {
                this.baseColumnIndices = this.join.getBaseTableColumnIndices();
                this.refColumnIndices = this.join.getReferencedTableColumnIndices();
            }
            else if (this.condition.getOriginalTableName().equals(this.join.getBaseTableName())) {
                this.refColumnIndices = this.join.getBaseTableColumnIndices();
                this.baseColumnIndices = this.join.getReferencedTableColumnIndices();
            }
        }
    }
    
    private Map getMap() throws DataAccessException {
        if (this.criteriaMap == null) {
            this.criteriaMap = new HashMap();
            this.rowColList = new ArrayList();
            this.conColList = new ArrayList();
            this.fillMapWithColumns(this.criteriaMap, this.rowColList, this.join, this.valueList.get(0));
            this.fillMapWithColumns(this.criteriaMap, this.conColList, this.join, this.condition);
            this.join.setCriteria(QueryUtil.syncForDataType(this.join.getCriteria(), this.dataObject));
        }
        return this.criteriaMap;
    }
    
    private void fillMapWithColumns(final Map hMap, final List colList, final Join join, final Row row) throws DataAccessException {
        String tabAlias = null;
        if (join.getBaseTableAlias().equals(row.getTableName())) {
            tabAlias = join.getBaseTableAlias();
        }
        else {
            if (!join.getReferencedTableAlias().equals(row.getTableName())) {
                throw new DataAccessException("The given row :: " + row + " has no relationship with the specified join :: " + join);
            }
            tabAlias = join.getReferencedTableAlias();
        }
        final List colNames = row.getColumns();
        Column col = null;
        for (int i = 0; i < colNames.size(); ++i) {
            final String columnName = colNames.get(i);
            col = new Column(tabAlias, i + 1);
            col.setColumnName(columnName);
            colList.add(col);
            hMap.put(col, null);
        }
    }
    
    private void fillMapWithValues(final Row row, final boolean isCondition) throws DataAccessException {
        final List rowValues = row.getValues();
        for (int i = 0; i < rowValues.size(); ++i) {
            this.getMap().put(isCondition ? this.conColList.get(i) : this.rowColList.get(i), rowValues.get(i));
        }
    }
    
    private void initJoin() {
        if (this.join == null && this.condition != null) {
            final String conditionTable = this.condition.getTableName();
            if (!this.tableName.equals(conditionTable)) {
                try {
                    final ForeignKeyDefinition fkDef = PersistenceUtil.getSuitableFK(this.tableName, conditionTable);
                    this.join = QueryConstructor.getJoin(fkDef);
                }
                catch (final DataAccessException dae) {
                    final String mess = "Exception occured when trying to find connecting foreign key between tables " + this.tableName + " and " + conditionTable;
                    RowIterator.OUT.log(Level.FINER, mess, dae);
                    throw new RuntimeException(mess, dae);
                }
                RowIterator.OUT.log(Level.FINEST, "Join information for the RowIterator is {0}", this.join);
            }
        }
        try {
            this.processJoin();
        }
        catch (final DataAccessException dae2) {
            throw new RuntimeException("Exception occurred while processing joins.");
        }
    }
    
    boolean containsRow(final Row row) {
        return this.valueList != null && !this.valueList.isEmpty() && this.valueList.contains(row);
    }
    
    static {
        CLASS_NAME = RowIterator.class.getName();
        OUT = Logger.getLogger(RowIterator.CLASS_NAME);
    }
}
