package com.adventnet.persistence;

import java.util.Arrays;
import com.adventnet.db.persistence.metadata.UniqueKeyDefinition;
import java.util.Collection;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.logging.Logger;

public class IndexedDO extends WritableDataObject
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    public static final int ONE_TO_ONE = 101;
    public static final int ONE_TO_MANY = 102;
    private DataObjectIndex doIndex;
    private HashMap idxStructureVsMap;
    
    public IndexedDO() {
        this.doIndex = null;
        this.idxStructureVsMap = new HashMap();
    }
    
    @Override
    public boolean getTrackOperations() {
        return false;
    }
    
    @Override
    public void setTrackOperations(final boolean track) throws DataAccessException {
        throw new DataAccessException("trackOperations cannot be changed.");
    }
    
    public boolean isIndexed() {
        return this.doIndex != null;
    }
    
    public void addIndex(final String indexTableName, final int[] indexCols) throws DataAccessException {
        this.addIndex(indexTableName, indexCols, indexCols, indexCols, indexTableName);
    }
    
    public void addIndex(final String indexTableName, final int[] indexCols, final int[] colIdxsToBeQueriedInIdxTabName, final int[] colIdxsToBeQueriedInFetchTabName, final String fetchTableName) throws DataAccessException {
        if (this.doIndex == null) {
            this.doIndex = new DataObjectIndex();
        }
        final boolean added = this.doIndex.addIndex(indexTableName, indexCols, colIdxsToBeQueriedInIdxTabName, colIdxsToBeQueriedInFetchTabName, fetchTableName);
        if (!added) {
            IndexedDO.OUT.log(Level.WARNING, "Already an index present for these configurations.indexTableName :: [{0}]   indexColumnName :: [{1}]   fetchTableName :: [{2}]. Hence this Index is not added.", new Object[] { indexTableName, PersistenceUtil.convertToString(indexCols), fetchTableName });
        }
    }
    
    private Object getKey(final int[] idxCols, final Row row) {
        final int size = idxCols.length;
        if (size == 1) {
            return row.get(idxCols[0]);
        }
        final List list = new ArrayList();
        for (int i = 0; i < size; ++i) {
            list.add(row.get(idxCols[i]));
        }
        return list;
    }
    
    @Override
    public void updateRow(final Row row) throws DataAccessException {
        super.updateRow(row);
        row.markAsClean();
    }
    
    @Override
    public void addRow(final Row row) throws DataAccessException {
        super.addRow(row);
        this.updateIndexes(row);
        row.markAsClean();
    }
    
    private void updateIndexes(final Row row) throws DataAccessException {
        if (this.doIndex != null) {
            final String tableName = row.getTableName();
            this.processIndexes(this.doIndex.getIndexesForTableAsMasterAndSlave(tableName), row, true);
            this.processIndexes(this.doIndex.getIndexesForTableAsMaster(tableName), row, false);
            this.processIndexes(this.doIndex.getIndexesForTableAsSlave(tableName), row, true);
        }
    }
    
    private void processIndexes(final List indexes, final Row row, final boolean isSlave) {
        if (indexes != null) {
            for (final IndexStructure idxStructure : indexes) {
                Row otherRow = null;
                if (isSlave) {
                    otherRow = this.getRow(idxStructure.indexTableName, idxStructure.leftColsInJoin, idxStructure.indexTableName, idxStructure.rightColsInJoin, row);
                    if (otherRow == null) {
                        otherRow = row;
                    }
                }
                else {
                    otherRow = this.getRow(idxStructure.fetchTableName, idxStructure.rightColsInJoin, idxStructure.fetchTableName, idxStructure.leftColsInJoin, row);
                }
                final Object key = this.getKey(idxStructure.idxCols, isSlave ? otherRow : row);
                this.putValueInHashMap(idxStructure, key, isSlave ? row : otherRow, idxStructure.indexType);
            }
        }
    }
    
    public void reIndexForTables(final List tableNames) throws DataAccessException {
        for (int i = 0; i < tableNames.size(); ++i) {
            final Iterator rowIterator = this.getRows(tableNames.get(i));
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                this.updateIndexes(row);
            }
        }
    }
    
    private void putValueInHashMap(final IndexStructure idxStructure, final Object key, final Row row, final int idxType) {
        final HashMap hashMap = this.idxStructureVsMap.get(idxStructure);
        final Object value = hashMap.get(key);
        if (idxType == 101) {
            hashMap.put(key, row);
        }
        else {
            List list = (List)value;
            if (value == null) {
                list = new ArrayList();
                hashMap.put(key, list);
            }
            if (!list.contains(row)) {
                list.add(row);
            }
        }
    }
    
    @Override
    boolean delete(final Row row) {
        final boolean isDelete = super.delete(row);
        if (this.doIndex != null) {
            final String tableName = row.getTableName();
            List indexes = this.doIndex.getDeleteIndexesForTableAsMaster(tableName);
            Iterator idxIterator = null;
            if (indexes != null) {
                idxIterator = indexes.iterator();
                while (idxIterator.hasNext()) {
                    final IndexStructure idxStru = idxIterator.next();
                    final HashMap hashMap = this.idxStructureVsMap.get(idxStru);
                    final Object key = this.getKey(idxStru.idxCols, row);
                    hashMap.remove(key);
                }
            }
            indexes = this.doIndex.getDeleteIndexesForTableAsSlave(tableName);
            if (indexes != null) {
                idxIterator = indexes.iterator();
                while (idxIterator.hasNext()) {
                    final IndexStructure idxStru = idxIterator.next();
                    final HashMap hashMap = this.idxStructureVsMap.get(idxStru);
                    final Row otherRow = this.getRow(idxStru.indexTableName, idxStru.leftColsInJoin, idxStru.indexTableName, idxStru.rightColsInJoin, row);
                    final Object key2 = this.getKey(idxStru.rightColsInJoin, otherRow);
                    if (idxStru.indexType == 101) {
                        hashMap.remove(key2);
                    }
                    else {
                        final List list = hashMap.get(key2);
                        if (list == null) {
                            continue;
                        }
                        list.remove(row);
                        if (list.size() != 0) {
                            continue;
                        }
                        hashMap.remove(key2);
                    }
                }
            }
        }
        return isDelete;
    }
    
    @Override
    public Iterator getRows(final String tableName, final Criteria criteria, final Join join) throws DataAccessException {
        if (this.doIndex != null && this.isSimpleCriteria(criteria)) {
            final IndexStructure idxStructure = this.getIndexStructure(tableName, criteria);
            final Object keyToBeFetched = criteria.getValue();
            final HashMap hashMap = this.idxStructureVsMap.get(idxStructure);
            if (hashMap != null) {
                return this.getRows(hashMap, keyToBeFetched, this.doIndex.getIndexType(idxStructure));
            }
        }
        return super.getRows(tableName, criteria, join);
    }
    
    @Override
    public Iterator getRows(final String tableName, final Criteria criteria) throws DataAccessException {
        if (this.doIndex != null && this.isSimpleCriteria(criteria)) {
            final IndexStructure idxStructure = this.getIndexStructure(tableName, criteria);
            final Object keyToBeFetched = criteria.getValue();
            final HashMap hashMap = this.idxStructureVsMap.get(idxStructure);
            if (hashMap != null) {
                return this.getRows(hashMap, keyToBeFetched, this.doIndex.getIndexType(idxStructure));
            }
        }
        return super.getRows(tableName, criteria);
    }
    
    @Override
    public Row getRow(final String tableName, final Criteria criteria, final Join join) throws DataAccessException {
        if (this.doIndex != null && this.isSimpleCriteria(criteria)) {
            final IndexStructure idxStructure = this.getIndexStructure(tableName, criteria);
            final Object keyToBeFetched = criteria.getValue();
            final HashMap hashMap = this.idxStructureVsMap.get(idxStructure);
            if (hashMap != null) {
                return this.getRow(hashMap, keyToBeFetched, this.doIndex.getIndexType(idxStructure));
            }
        }
        return this.getFirstRow(super.getRows(tableName, criteria, join));
    }
    
    @Override
    public Row getRow(final String tableName, final Criteria criteria) throws DataAccessException {
        if (this.doIndex != null && this.isSimpleCriteria(criteria)) {
            final IndexStructure idxStructure = this.getIndexStructure(tableName, criteria);
            final Object keyToBeFetched = criteria.getValue();
            final HashMap hashMap = this.idxStructureVsMap.get(idxStructure);
            if (hashMap != null) {
                return this.getRow(hashMap, keyToBeFetched, this.doIndex.getIndexType(idxStructure));
            }
        }
        return this.getFirstRow(super.getRows(tableName, criteria));
    }
    
    public Row getRow(final String tableName, final String columnName, final String fetchTableName, final Object keyToBeFetched) throws DataAccessException {
        final IndexStructure idxStructure = this.getIndexStructure(tableName, columnName, fetchTableName);
        return this.getRow(idxStructure, keyToBeFetched);
    }
    
    public Iterator getRows(final String tableName, final String columnName, final String fetchTableName, final Object keyToBeFetched) throws DataAccessException {
        final IndexStructure idxStructure = this.getIndexStructure(tableName, columnName, fetchTableName);
        return this.getRows(idxStructure, keyToBeFetched);
    }
    
    public Row getRow(final String tableName, final int[] colIndices, final String fetchTableName, final Object keyToBeFetched) {
        final IndexStructure idxStructure = new IndexStructure(tableName, colIndices, fetchTableName);
        return this.getRow(idxStructure, keyToBeFetched);
    }
    
    public Iterator getRows(final String tableName, final int[] colIndices, final String fetchTableName, final Object keyToBeFetched) {
        final IndexStructure idxStructure = new IndexStructure(tableName, colIndices, fetchTableName);
        return this.getRows(idxStructure, keyToBeFetched);
    }
    
    private Row getRow(final String tableName, final int[] colIndices, final String fetchTableName, final int[] keyCols, final Row fetchRow) {
        return this.getRow(tableName, colIndices, fetchTableName, this.getKey(keyCols, fetchRow));
    }
    
    private Iterator getRows(final String tableName, final int[] colIndices, final String fetchTableName, final int[] keyCols, final Row fetchRow) {
        return this.getRows(tableName, colIndices, fetchTableName, this.getKey(keyCols, fetchRow));
    }
    
    private Row getRow(final IndexStructure idxStructure, final Object keyToBeFetched) {
        if (this.doIndex == null) {
            return null;
        }
        final HashMap hashMap = this.idxStructureVsMap.get(idxStructure);
        if (hashMap == null) {
            return null;
        }
        return this.getRow(hashMap, keyToBeFetched, this.doIndex.getIndexType(idxStructure));
    }
    
    private Row getRow(final HashMap hashMap, final Object keyToBeFetched, final int indexType) {
        final Object retValue = hashMap.get(keyToBeFetched);
        if (retValue == null) {
            return null;
        }
        if (indexType == 101) {
            return (Row)retValue;
        }
        return ((List)retValue).get(0);
    }
    
    private Iterator getRows(final IndexStructure idxStructure, final Object keyToBeFetched) {
        if (this.doIndex == null) {
            return null;
        }
        final HashMap hashMap = this.idxStructureVsMap.get(idxStructure);
        if (hashMap == null) {
            return null;
        }
        return this.getRows(hashMap, keyToBeFetched, this.doIndex.getIndexType(idxStructure));
    }
    
    private Iterator getRows(final HashMap hashMap, final Object keyToBeFetched, final int indexType) {
        final Object retValue = hashMap.get(keyToBeFetched);
        if (retValue == null) {
            return null;
        }
        if (indexType == 102) {
            return ((List)retValue).iterator();
        }
        final List list = new ArrayList();
        list.add(retValue);
        IndexedDO.OUT.log(Level.WARNING, "The index has only one value :: " + list + " for the key :: " + keyToBeFetched + ", hence use getRow() method.");
        return list.iterator();
    }
    
    private boolean isSimpleCriteria(final Criteria criteria) {
        return criteria != null && criteria.getColumn() != null && criteria.getValue() != null && criteria.getComparator() == 0;
    }
    
    @Override
    public String toString() {
        return super.toString() + "\n\nIdxStructureVsMap :: " + this.idxStructureVsMap;
    }
    
    private IndexStructure getIndexStructure(final String tableName, final Criteria criteria) {
        final Column col = criteria.getColumn();
        final String tableAlias = col.getTableAlias();
        return new IndexStructure(tableName, new int[] { col.getColumnIndex() }, tableAlias);
    }
    
    private IndexStructure getIndexStructure(final String idxTableName, final String idxColumnName, final String fetchTableName) throws DataAccessException {
        try {
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(idxTableName);
            final int columnIndex = td.getColumnIndex(idxColumnName);
            return new IndexStructure(idxTableName, new int[] { columnIndex }, fetchTableName);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while converting the columnName :: [" + idxColumnName + "] to column index " + mde.getMessage());
        }
    }
    
    static {
        CLASS_NAME = IndexedDO.class.getName();
        OUT = Logger.getLogger(IndexedDO.CLASS_NAME);
    }
    
    class DataObjectIndex
    {
        HashMap idxStructureVsIndexType;
        HashMap indexesForTableAsMasterAndSlave;
        HashMap indexesForTableAsMaster;
        HashMap indexesForTableAsSlave;
        HashMap deleteIndexesForTableAsMaster;
        HashMap deleteIndexesForTableAsSlave;
        
        DataObjectIndex() {
            this.idxStructureVsIndexType = new HashMap();
            this.indexesForTableAsMasterAndSlave = new HashMap();
            this.indexesForTableAsMaster = new HashMap();
            this.indexesForTableAsSlave = new HashMap();
            this.deleteIndexesForTableAsMaster = new HashMap();
            this.deleteIndexesForTableAsSlave = new HashMap();
        }
        
        private List getIndexList(final HashMap hashMap, final String name) {
            List list = hashMap.get(name);
            if (list == null) {
                list = new ArrayList();
                hashMap.put(name, list);
            }
            return list;
        }
        
        private boolean updateIndexes(final IndexStructure idxStructure) {
            final String childTableName = idxStructure.childTableName;
            final String indexTableName = idxStructure.indexTableName;
            final String fetchTableName = idxStructure.fetchTableName;
            final String parentTableName = indexTableName.equals(childTableName) ? fetchTableName : indexTableName;
            if (this.idxStructureVsIndexType.containsKey(idxStructure)) {
                return false;
            }
            this.idxStructureVsIndexType.put(idxStructure, new Integer(idxStructure.indexType));
            if (indexTableName.equals(fetchTableName)) {
                this.getIndexList(this.indexesForTableAsMasterAndSlave, indexTableName).add(idxStructure);
            }
            else {
                final HashMap hashMap = childTableName.equals(indexTableName) ? this.indexesForTableAsMaster : this.indexesForTableAsSlave;
                this.getIndexList(hashMap, childTableName).add(idxStructure);
            }
            this.getIndexList(this.deleteIndexesForTableAsMaster, indexTableName).add(idxStructure);
            if (!indexTableName.equals(fetchTableName) && childTableName.equals(fetchTableName)) {
                this.getIndexList(this.deleteIndexesForTableAsSlave, fetchTableName).add(idxStructure);
            }
            IndexedDO.this.idxStructureVsMap.put(idxStructure, new HashMap<IndexStructure, HashMap>());
            return true;
        }
        
        boolean addIndex(final String indexTableName, final String[] indexColumnNames, final String[] colNamesToBeQueriedInIdxTabName, final String[] colNamesToBeQueriedInFetchTabName, final String fetchTableName) throws DataAccessException {
            final int[] idxColIndices = this.getColumnIndices(indexTableName, indexColumnNames);
            final int[] leftColIndices = this.getColumnIndices(indexTableName, colNamesToBeQueriedInIdxTabName);
            final int[] rightColIndices = this.getColumnIndices(fetchTableName, colNamesToBeQueriedInFetchTabName);
            final IndexStructure idxStructure = new IndexStructure(indexTableName, idxColIndices, leftColIndices, rightColIndices, fetchTableName);
            return this.updateIndexes(idxStructure);
        }
        
        private int[] getColumnIndices(final String tableName, final String[] columnNames) throws DataAccessException {
            try {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                final int size = columnNames.length;
                final int[] colIndices = new int[size];
                for (int i = 0; i < size; ++i) {
                    colIndices[i] = td.getColumnIndex(columnNames[i]);
                    if (colIndices[i] == -1) {
                        throw new DataAccessException("Invalid columnName found for the tableName :: [" + tableName + "]   columnNames :: [" + PersistenceUtil.convertToString(columnNames) + "]");
                    }
                }
                return colIndices;
            }
            catch (final MetaDataException mde) {
                throw new DataAccessException("Exception occured while processing the tableName :: [" + tableName + "]  and columnNames :: [" + PersistenceUtil.convertToString(columnNames) + "]" + mde.getMessage());
            }
        }
        
        boolean addIndex(final String indexTableName, final int[] indexCols, final int[] colIdxsToBeQueriedInIdxTabName, final int[] colIdxsToBeQueriedInFetchTabName, final String fetchTableName) throws DataAccessException {
            final IndexStructure idxStructure = new IndexStructure(indexTableName, indexCols, colIdxsToBeQueriedInIdxTabName, colIdxsToBeQueriedInFetchTabName, fetchTableName);
            return this.updateIndexes(idxStructure);
        }
        
        List getIndexesForTableAsMasterAndSlave(final String tableName) {
            return this.indexesForTableAsMasterAndSlave.get(tableName);
        }
        
        List getIndexesForTableAsMaster(final String tableName) {
            return this.indexesForTableAsMaster.get(tableName);
        }
        
        List getIndexesForTableAsSlave(final String tableName) {
            return this.indexesForTableAsSlave.get(tableName);
        }
        
        List getDeleteIndexesForTableAsMaster(final String tableName) {
            return this.deleteIndexesForTableAsMaster.get(tableName);
        }
        
        List getDeleteIndexesForTableAsSlave(final String tableName) {
            return this.deleteIndexesForTableAsSlave.get(tableName);
        }
        
        int getIndexType(final IndexStructure idxStructure) {
            return this.idxStructureVsIndexType.get(idxStructure);
        }
    }
    
    class IndexStructure
    {
        String indexTableName;
        String fetchTableName;
        String childTableName;
        int[] idxCols;
        int[] leftColsInJoin;
        int[] rightColsInJoin;
        int indexType;
        private int hashCode;
        
        IndexStructure(final String indexTblName, final int[] idxColumns, final String fetchTblName) {
            this.childTableName = null;
            this.rightColsInJoin = null;
            this.indexType = -1;
            this.hashCode = -1;
            this.indexTableName = indexTblName;
            this.fetchTableName = fetchTblName;
            this.idxCols = idxColumns;
        }
        
        IndexStructure(final String indexTblName, final int[] idxColumns, final int[] colIdxsToBeQueriedInIdxTabName, final int[] colIdxsToBeQueriedInFetchTabName, final String fetchTblName) throws DataAccessException {
            this.childTableName = null;
            this.rightColsInJoin = null;
            this.indexType = -1;
            this.hashCode = -1;
            this.indexTableName = indexTblName;
            this.fetchTableName = fetchTblName;
            this.indexType = ((!this.isUnique(indexTblName, idxColumns) || !this.isUnique(fetchTblName, colIdxsToBeQueriedInFetchTabName)) ? 102 : 101);
            this.idxCols = idxColumns;
            this.leftColsInJoin = colIdxsToBeQueriedInIdxTabName;
            this.rightColsInJoin = colIdxsToBeQueriedInFetchTabName;
            try {
                this.childTableName = (this.indexTableName.equals(fetchTblName) ? this.indexTableName : MetaDataUtil.getSlaveTableName(this.indexTableName, fetchTblName));
            }
            catch (final MetaDataException mde) {
                throw new DataAccessException("Exception occured while processing the childTableName " + mde.getMessage());
            }
        }
        
        private boolean isUnique(final String tableName, final int[] columnIndices) throws DataAccessException {
            try {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                final List<String> columnNames = new ArrayList<String>(columnIndices.length);
                final List<String> allCols = td.getColumnNames();
                for (int i = 0; i < columnIndices.length; ++i) {
                    columnNames.add(allCols.get(columnIndices[i] - 1));
                }
                final List pkCols = td.getPrimaryKey().getColumnList();
                if (pkCols.containsAll(columnNames) && columnNames.containsAll(pkCols)) {
                    return true;
                }
                final List uks = td.getUniqueKeys();
                if (uks != null) {
                    for (int j = 0; j < uks.size(); ++j) {
                        final List ukCols = uks.get(j).getColumns();
                        if (ukCols.containsAll(columnNames) && columnNames.containsAll(ukCols)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            catch (final MetaDataException mde) {
                throw new DataAccessException("Exception occured while processing the isUnique " + mde.getMessage());
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(this.indexTableName + "_");
            sb.append(PersistenceUtil.convertToString(this.idxCols) + "_" + this.fetchTableName);
            return sb.toString();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof IndexStructure)) {
                return false;
            }
            final IndexStructure newIdxStru = (IndexStructure)o;
            return this.indexTableName.equals(newIdxStru.indexTableName) && this.fetchTableName.equals(newIdxStru.fetchTableName) && Arrays.equals(this.idxCols, newIdxStru.idxCols);
        }
        
        @Override
        public int hashCode() {
            if (this.hashCode == -1) {
                this.hashCode = this.indexTableName.hashCode() + this.fetchTableName.hashCode() + this.generateHashCode(this.idxCols);
            }
            return this.hashCode;
        }
        
        private int generateHashCode(final int[] s) {
            int retValue = 0;
            for (int i = 0; i < s.length; ++i) {
                retValue = retValue * 10 + s[i];
            }
            return retValue;
        }
    }
}
