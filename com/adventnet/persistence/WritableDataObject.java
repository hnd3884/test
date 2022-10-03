package com.adventnet.persistence;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Set;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.DerivedTable;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import java.util.Arrays;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.util.Collections;
import java.util.Comparator;
import com.zoho.conf.AppResources;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.internal.UniqueValueHolder;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.NodeList;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Level;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Collection;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.DataSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;
import java.io.Externalizable;

public class WritableDataObject implements DataObject, DeepCloneable, Externalizable
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    private static final long serialVersionUID = 11L;
    static String getDataObjectCloneExtent;
    private boolean trackOperations;
    protected HashMap<String, List<Row>> tableToRowList;
    protected HashMap<String, String> tableAliasToTableName;
    private boolean validated;
    private HashMap<String, OperationTables> operTypeVsOperTables;
    private Row actualRowDeleted;
    private SelectQuery query;
    private List joins;
    private HashMap deleteRowIndex;
    private transient boolean immutable;
    protected transient int modCount;
    private String[] operName;
    private List<String> writeProtectedTables;
    private static final Iterator EMPTY_ITERATOR;
    private transient Map transientRowIndex;
    
    public WritableDataObject() {
        this.trackOperations = true;
        this.validated = false;
        this.operTypeVsOperTables = new HashMap<String, OperationTables>();
        this.deleteRowIndex = null;
        this.immutable = false;
        this.modCount = 0;
        this.operName = new String[] { "noaction", "insert", "update", "delete", "on_delete_cascade" };
        this.writeProtectedTables = new ArrayList<String>();
        this.transientRowIndex = null;
        this.tableToRowList = new HashMap<String, List<Row>>();
        this.tableAliasToTableName = new HashMap<String, String>();
        this.joins = new ArrayList();
    }
    
    public WritableDataObject(final Iterator rowIterator) throws DataAccessException {
        this.trackOperations = true;
        this.validated = false;
        this.operTypeVsOperTables = new HashMap<String, OperationTables>();
        this.deleteRowIndex = null;
        this.immutable = false;
        this.modCount = 0;
        this.operName = new String[] { "noaction", "insert", "update", "delete", "on_delete_cascade" };
        this.writeProtectedTables = new ArrayList<String>();
        this.transientRowIndex = null;
        this.tableToRowList = new HashMap<String, List<Row>>();
        this.tableAliasToTableName = new HashMap<String, String>();
        this.cloneIntoList(rowIterator);
        this.joins = new ArrayList();
    }
    
    public WritableDataObject(final DataSet ds) throws DataAccessException {
        this.trackOperations = true;
        this.validated = false;
        this.operTypeVsOperTables = new HashMap<String, OperationTables>();
        this.deleteRowIndex = null;
        this.immutable = false;
        this.modCount = 0;
        this.operName = new String[] { "noaction", "insert", "update", "delete", "on_delete_cascade" };
        this.writeProtectedTables = new ArrayList<String>();
        this.transientRowIndex = null;
        this.tableToRowList = new HashMap<String, List<Row>>();
        this.tableAliasToTableName = new HashMap<String, String>();
        this.process(ds);
        this.getDirtyWriteProtectedTables(this.query = (SelectQuery)ds.getSelectQuery());
        for (final Table table : this.query.getTableList()) {
            this.fillTableAliasToTableName(table.getTableAlias(), table.getTableName());
        }
        this.joins = this.query.getJoins();
    }
    
    private void getDirtyWriteProtectedTables(final SelectQuery query) throws DataAccessException {
        final Iterator<Table> tabItr = query.getTableList().iterator();
        final List<String> tablist = new ArrayList<String>();
        while (tabItr.hasNext()) {
            tablist.add(tabItr.next().getTableName());
        }
        for (final String tableName : tablist) {
            try {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                if (td == null || !td.isDirtyWriteCheckColumnsDefined()) {
                    continue;
                }
                final Iterator<String> dirtywritecheckcolsItr = td.getPKExcludingDirtyWriteCheckColumnNames().iterator();
                final List<Column> dirtywritecheckcols = new ArrayList<Column>();
                while (dirtywritecheckcolsItr.hasNext()) {
                    dirtywritecheckcols.add(Column.getColumn(tableName, dirtywritecheckcolsItr.next()));
                }
                if (query.getSelectColumns().containsAll(dirtywritecheckcols)) {
                    continue;
                }
                this.writeProtectedTables.add(tableName);
            }
            catch (final MetaDataException mde) {
                throw new DataAccessException("Invalid tableName specified " + tableName, mde);
            }
        }
    }
    
    public WritableDataObject(final Element element) {
        this();
        final HashMap holderMap = new HashMap();
        final NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node childNodeN = childNodes.item(i);
            if (childNodeN.getNodeType() == 1) {
                final Node currentNode;
                final Element childNode = (Element)(currentNode = childNodes.item(i));
                if (currentNode.getNodeType() == 1) {
                    final Element rowNode = (Element)currentNode;
                    final String currentTableName = rowNode.getAttribute("tablename").trim();
                    String deletedAt = "-1";
                    if (rowNode.hasAttribute("deletedAt")) {
                        deletedAt = rowNode.getAttribute("deletedAt").trim();
                        WritableDataObject.OUT.log(Level.FINE, "Deletedat attribute does exists");
                    }
                    else {
                        WritableDataObject.OUT.log(Level.FINE, "Deletedat attribute does not exists");
                    }
                    WritableDataObject.OUT.log(Level.FINE, "DELETD AT IS :" + deletedAt);
                    Element actionInfoNode = null;
                    final Row currentRow = new Row(currentTableName);
                    currentRow.deletedAt = deletedAt;
                    final NodeList columnNodes = rowNode.getChildNodes();
                    for (int j = 0; j < columnNodes.getLength(); ++j) {
                        if (columnNodes.item(j).getNodeType() == 1) {
                            final Element columnNode = (Element)columnNodes.item(j);
                            if (columnNode.getNodeName().equals("ActionInfo")) {
                                actionInfoNode = columnNode;
                            }
                            else {
                                final String columnName = columnNode.getAttribute("name");
                                String value = null;
                                if (columnNode.hasAttribute("value")) {
                                    value = columnNode.getAttribute("value");
                                }
                                String oldValue = columnNode.getAttribute("oldvalue");
                                final String uniquevalueholder = columnNode.getAttribute("uniquevalueholder");
                                if (uniquevalueholder != null && !uniquevalueholder.trim().equals("")) {
                                    Object holder = holderMap.get(uniquevalueholder);
                                    if (holder == null) {
                                        WritableDataObject.OUT.log(Level.FINE, "Unique Holder NULL");
                                        holder = currentRow.get(columnName);
                                        holderMap.put(uniquevalueholder, holder);
                                    }
                                    else {
                                        WritableDataObject.OUT.log(Level.FINE, "Unique Holder not null {0}", holder);
                                        currentRow.set(columnName, holder);
                                    }
                                }
                                else if (value == null || value.equals("NULL")) {
                                    currentRow.set(columnName, null);
                                }
                                else {
                                    WritableDataObject.OUT.log(Level.FINE, "setting value for column {0} , value {1}", new Object[] { columnName, value });
                                    currentRow.setAppropValue(columnName, value);
                                }
                                if (oldValue != null && oldValue.trim().length() > 0) {
                                    WritableDataObject.OUT.log(Level.FINE, "setting old value for column {0} , value {1}", new Object[] { columnName, value });
                                    if ("(null)".equals(oldValue)) {
                                        WritableDataObject.OUT.log(Level.FINER, "Setting null original value for column {0}", columnName);
                                        oldValue = null;
                                    }
                                    currentRow.setAppropOrigValue(columnName, oldValue);
                                    currentRow.markAsDirty(columnName);
                                }
                            }
                        }
                    }
                    WritableDataObject.OUT.log(Level.FINEST, "ActionInfo Node for Row {0} is {1}", new Object[] { currentRow, actionInfoNode });
                    if (actionInfoNode != null) {
                        final String operation = actionInfoNode.getAttribute("operation");
                        if (operation.equals("INSERT")) {
                            WritableDataObject.OUT.log(Level.FINEST, "Insert Operation for currentRow {0}", currentRow);
                            this.addBlindly(currentRow);
                            this.addToOperations(1, currentRow, null);
                        }
                        else if (operation.equals("UPDATE")) {
                            this.addBlindly(currentRow);
                            this.addToOperations(2, currentRow, null);
                        }
                        else if (operation.equals("DELETE")) {
                            Criteria cr = null;
                            if (actionInfoNode.hasAttribute("criteria")) {
                                cr = new Criteria(actionInfoNode.getAttribute("criteria").trim());
                            }
                            this.addToOperations(3, currentRow, cr);
                        }
                        else if (operation.equals("ON_DELETE_CASCADE")) {
                            Criteria cr = null;
                            if (actionInfoNode.hasAttribute("criteria")) {
                                cr = new Criteria(actionInfoNode.getAttribute("criteria").trim());
                            }
                            this.addToOperations(4, currentRow, cr);
                        }
                    }
                    else {
                        this.addBlindly(currentRow);
                    }
                }
            }
        }
    }
    
    public Element getDOMElement() throws DataAccessException {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.newDocument();
            final Element dooNode = doc.createElement("DataObject");
            final List<String> sortedTableNames = PersistenceUtil.sortTables(this.getTableNames());
            for (int k = 0; k < sortedTableNames.size(); ++k) {
                final String sortedTableName = sortedTableNames.get(k);
                final Iterator iterator = this.getRows(sortedTableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    final Element rowNode = this.getRowNode(row, doc);
                    dooNode.appendChild(rowNode);
                }
            }
            final List operations = this.getOperations();
            for (final ActionInfo info : operations) {
                final Row row = info.getValue();
                if (info.getOperation() == 3 || info.getOperation() == 4) {
                    final Element rowNode = this.getRowNode(row, doc);
                    dooNode.appendChild(rowNode);
                }
            }
            return dooNode;
        }
        catch (final Exception e) {
            WritableDataObject.OUT.log(Level.FINE, "Exception occured in constructing getDOMElement for the DO {0}", this);
            throw new DataAccessException("Exception occured in constructing getDOMElement for the DO " + this + "Exception Message :: ", e);
        }
    }
    
    ActionInfo getActionInfo(final Row row, final int operType) {
        final OperationTables operTable = this.operTypeVsOperTables.get(this.operName[operType]);
        final HashMap tableVsActions = operTable.getActions();
        final ArrayList actList = tableVsActions.get(row.getTableName());
        final int listIndex = row.listIndex;
        final int totalActions = actList.size();
        int l;
        for (int startLoop = l = ((listIndex >= totalActions) ? (totalActions - 1) : listIndex); l >= 0; --l) {
            final ActionInfo actInfo = actList.get(l);
            final Row actionRow = actInfo.getValue();
            if (actionRow.equals(row)) {
                return actInfo;
            }
        }
        return null;
    }
    
    Element getRowNode(final Row row, final Document doc) {
        final Element rowNode = doc.createElement("Row");
        rowNode.setAttribute("tablename", row.getTableName());
        rowNode.setAttribute("deletedAt", row.deletedAt);
        final int operIndex = row.operationIndex;
        if (operIndex != -1) {
            final Element actionInfoNode = doc.createElement("ActionInfo");
            switch (operIndex) {
                case 1: {
                    actionInfoNode.setAttribute("operation", "INSERT");
                    break;
                }
                case 2: {
                    actionInfoNode.setAttribute("operation", "UPDATE");
                    break;
                }
                case 3: {
                    actionInfoNode.setAttribute("operation", "DELETE");
                    final ActionInfo ai = this.getActionInfo(row, 3);
                    if (ai != null && ai.getCondition() != null) {
                        actionInfoNode.setAttribute("criteria", ai.getCondition().toString());
                        break;
                    }
                    break;
                }
                case 4: {
                    actionInfoNode.setAttribute("operation", "ON_DELETE_CASCADE");
                    final ActionInfo ai = this.getActionInfo(row, 4);
                    if (ai != null && ai.getCondition() != null) {
                        actionInfoNode.setAttribute("criteria", ai.getCondition().toString());
                        break;
                    }
                    break;
                }
            }
            rowNode.appendChild(actionInfoNode);
        }
        final List columns = row.getColumns();
        for (int j = 0; j < columns.size(); ++j) {
            final String columnName = columns.get(j);
            final Object value = row.get(columnName);
            final Element columnNode = doc.createElement("ColumnDetail");
            columnNode.setAttribute("name", columnName);
            if (value instanceof UniqueValueHolder) {
                columnNode.setAttribute("uniquevalueholder", Integer.toString(value.hashCode()));
            }
            else if (value != null) {
                columnNode.setAttribute("value", value.toString());
            }
            else {
                columnNode.setAttribute("value", "NULL");
            }
            if (row.getChangedColumnIndex() != null) {
                final Object originalValue = row.getOriginalValue(columnName);
                if (originalValue != null && !(originalValue instanceof UniqueValueHolder)) {
                    columnNode.setAttribute("oldvalue", originalValue.toString());
                }
                else if (this.search(row.getChangedColumnIndex(), j + 1)) {
                    WritableDataObject.OUT.log(Level.FINE, "Column {0} has null as the original value", columnName);
                    columnNode.setAttribute("oldvalue", "(null)");
                }
            }
            rowNode.appendChild(columnNode);
        }
        return rowNode;
    }
    
    private boolean search(final int[] index, final int value) {
        for (int i = 0; i < index.length; ++i) {
            if (index[i] == value) {
                return true;
            }
        }
        return false;
    }
    
    private Iterator<Row> getIteratorFor(final String tableName, final String operationType) {
        final OperationTables oTables = this.operTypeVsOperTables.get(operationType);
        List<ActionInfo> aInfos = null;
        if (oTables != null) {
            final HashMap<String, List<ActionInfo>> tableNameVsActionInfos = oTables.tableVsActionInfo;
            aInfos = tableNameVsActionInfos.get(tableName);
        }
        if (aInfos == null || aInfos.size() == 0) {
            aInfos = new ArrayList<ActionInfo>();
        }
        return new ActionInfoIterator(aInfos);
    }
    
    @Override
    public Iterator<Row> getAddedRows(final String tableName) {
        return this.getIteratorFor(tableName, "insert");
    }
    
    @Override
    public Iterator<Row> getUpdatedRows(final String tableName) {
        return this.getIteratorFor(tableName, "update");
    }
    
    @Override
    public Iterator<Row> getDeletedRows(final String tableName) {
        return this.getIteratorFor(tableName, "delete");
    }
    
    public void setTrackOperations(final boolean track) throws DataAccessException {
        this.trackOperations = track;
    }
    
    public boolean getTrackOperations() {
        return this.trackOperations;
    }
    
    @Override
    public synchronized void addJoin(final Join join) throws DataAccessException {
        if (this.hasJoin(join)) {
            final String exceptionMsg = "Join already available in DataObject." + join;
            WritableDataObject.OUT.log(Level.FINE, exceptionMsg);
            throw new DataAccessException(exceptionMsg);
        }
        this.joins.add(join);
    }
    
    @Override
    public synchronized boolean hasJoin(final Join join) {
        return this.findIndex(join) > -1;
    }
    
    private int findIndex(final Join join) {
        if (join == null) {
            throw new NullPointerException("Join cannot be null");
        }
        final int len = this.joins.size();
        final String table1InJoin = join.getBaseTableAlias();
        final String table2InJoin = join.getReferencedTableAlias();
        for (int i = 0; i < len; ++i) {
            final Join joinInList = this.joins.get(i);
            final String table1InJoinInList = joinInList.getBaseTableAlias();
            final String table2InJoinInList = joinInList.getReferencedTableAlias();
            if ((table1InJoin.equals(table1InJoinInList) && table2InJoin.equals(table2InJoinInList)) || (table1InJoin.equals(table2InJoinInList) && table2InJoin.equals(table1InJoinInList))) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public synchronized boolean removeJoin(final Join join) {
        final int index = this.findIndex(join);
        if (index > -1) {
            this.joins.remove(index);
            return true;
        }
        return false;
    }
    
    protected List getRowsFor(final String tableAlias) {
        return this.tableToRowList.get(tableAlias);
    }
    
    @Override
    public Iterator getRows(final String tableAlias) throws DataAccessException {
        if (tableAlias == null) {
            if (this.transientRowIndex != null) {
                return this.transientRowIndex.values().iterator();
            }
            return this.getAllRows().iterator();
        }
        else {
            final List rowList = this.getRowsFor(tableAlias);
            if (rowList != null) {
                return new RowIterator(rowList, tableAlias, null, null, this);
            }
            return WritableDataObject.EMPTY_ITERATOR;
        }
    }
    
    @Override
    public Iterator getRows(final String tableAlias, final Row condition) throws DataAccessException {
        Join join = null;
        final List rowList = this.getRowsFor(tableAlias);
        if (rowList == null) {
            return WritableDataObject.EMPTY_ITERATOR;
        }
        if (condition != null) {
            final String condTable = condition.getTableName();
            if (!condTable.equals(tableAlias)) {
                join = this.getJoin(tableAlias, condTable);
            }
        }
        return new RowIterator(rowList, tableAlias, condition, join, this);
    }
    
    @Override
    public Iterator getRows(final String tableAlias, final Row row, final Join join) throws DataAccessException {
        if (join != null) {
            final String table1InJoin = join.getBaseTableAlias();
            final String table2InJoin = join.getReferencedTableAlias();
            if (row == null) {
                WritableDataObject.OUT.log(Level.FINE, "Condition Row cannot be null when there is a join.");
                throw new DataAccessException("single tableName - join mismatch : [" + tableAlias + "], " + join);
            }
            final String tableOfRow = row.getTableName();
            if (table1InJoin.equals(table2InJoin) && tableAlias.equals(tableOfRow) && tableAlias.equals(table1InJoin)) {
                final Criteria selfJoinCriteria = this.getCriteriaForSelfRefTable(tableAlias, row, join);
                WritableDataObject.OUT.log(Level.FINER, "selfJoin criteria is {0}", selfJoinCriteria);
                return this.getRows(tableAlias, selfJoinCriteria);
            }
            if ((!tableAlias.equals(table1InJoin) && !tableAlias.equals(table2InJoin)) || (!tableOfRow.equals(table1InJoin) && !tableOfRow.equals(table2InJoin))) {
                throw new DataAccessException("tableNames - join mismatch : [" + tableAlias + "], [" + tableOfRow + "], " + join);
            }
        }
        final List rowList = this.getRowsFor(tableAlias);
        if (rowList == null) {
            return WritableDataObject.EMPTY_ITERATOR;
        }
        return new RowIterator(rowList, tableAlias, row, join, this);
    }
    
    private Iterator getRows(final CriteriaBasedRowIterator.ITERATOR_FOR iteratorFor, final String tableName, final Criteria criteria, final Join join) throws DataAccessException {
        List list = this.getRowsFor(tableName);
        if (list == null || list.isEmpty()) {
            return WritableDataObject.EMPTY_ITERATOR;
        }
        if (criteria == null) {
            return this.getIterator(iteratorFor, tableName);
        }
        if (tableName == null) {
            return WritableDataObject.EMPTY_ITERATOR;
        }
        String criteriaTableName = null;
        try {
            criteriaTableName = CriteriaBasedRowIterator.getTableName(criteria, this);
        }
        catch (final Exception e) {
            throw new DataAccessException("Exception occurred while CriteriaBasedRowIterator.getTableName :: ", e);
        }
        list = this.getRowsFor(criteriaTableName);
        if (list == null || list.isEmpty()) {
            return WritableDataObject.EMPTY_ITERATOR;
        }
        if (criteriaTableName.equals(tableName)) {
            return this.getIterator(iteratorFor, tableName, criteria);
        }
        Join joinToUse = join;
        if (joinToUse == null) {
            String colName = null;
            try {
                if (criteria != null && criteria.getColumn() != null) {
                    colName = criteria.getColumn().getColumnName();
                }
                if (colName != null) {
                    joinToUse = this.getJoin(tableName, criteriaTableName, colName);
                }
                if (joinToUse == null) {
                    joinToUse = this.getJoin(tableName, criteriaTableName);
                }
            }
            catch (final Exception e2) {
                throw new DataAccessException("Exception occurred while resolving the join", e2);
            }
            if (joinToUse == null) {
                throw new DataAccessException("No join found between the table :: [" + tableName + "] and the criteriaTableName :: [" + criteriaTableName + "]");
            }
        }
        return this.getIterator(iteratorFor, tableName, criteria, joinToUse);
    }
    
    @Override
    public Iterator getRows(final String tableName, final Criteria criteria) throws DataAccessException {
        return this.getRows(CriteriaBasedRowIterator.ITERATOR_FOR.MULTIPLE_ROWS, tableName, criteria, null);
    }
    
    @Override
    public DataObject getDataObject(final String tableName, final Criteria criteria) throws DataAccessException {
        final Iterator iterator = this.getRows(CriteriaBasedRowIterator.ITERATOR_FOR.MULTIPLE_ROWS, tableName, criteria, null);
        final WritableDataObject dataObject = new WritableDataObject();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            if (WritableDataObject.getDataObjectCloneExtent.equalsIgnoreCase("ignore_uvh")) {
                dataObject.add((Row)row.clone());
            }
            else {
                dataObject.add(row);
            }
        }
        if (WritableDataObject.getDataObjectCloneExtent.equalsIgnoreCase("all")) {
            return (DataObject)dataObject.clone();
        }
        return dataObject;
    }
    
    @Override
    public Iterator getRows(final String tableName, final Criteria criteria, final Join join) throws DataAccessException {
        return this.getRows(CriteriaBasedRowIterator.ITERATOR_FOR.MULTIPLE_ROWS, tableName, criteria, join);
    }
    
    private Iterator getIterator(final CriteriaBasedRowIterator.ITERATOR_FOR iteratorFor, final String projectedTableName) throws DataAccessException {
        return new CriteriaBasedRowIterator(iteratorFor, this.getRowsFor(projectedTableName), null, this);
    }
    
    private Iterator getIterator(final CriteriaBasedRowIterator.ITERATOR_FOR iteratorFor, final String projectedTableName, final Criteria projectedTableCriteria) throws DataAccessException {
        return new CriteriaBasedRowIterator(iteratorFor, this.getRowsFor(projectedTableName), projectedTableCriteria, this);
    }
    
    private Iterator getIterator(final CriteriaBasedRowIterator.ITERATOR_FOR iteratorFor, final String projectedTableName, final Criteria criteria, final Join join) throws DataAccessException {
        return new CriteriaBasedRowIterator(iteratorFor, projectedTableName, this.getRowsFor(projectedTableName), criteria, this.getRowsFor(CriteriaBasedRowIterator.getTableName(criteria, this)), this, join);
    }
    
    @Override
    public Row getFirstRow(final String tableAlias) throws DataAccessException {
        if (tableAlias == null) {
            throw new DataAccessException("TableAlias cannot be null");
        }
        final List rowList = this.getRowsFor(tableAlias);
        if (rowList != null && rowList.size() > 0) {
            return rowList.get(0);
        }
        throw new DataAccessException("No rows found for the table " + tableAlias + " in this DataObject");
    }
    
    @Override
    public Row getFirstRow(final String tableAlias, final Row condition) throws DataAccessException {
        if (condition != null && tableAlias != null && tableAlias.equals(condition.getTableName())) {
            final Row presentRow = this.findRow(condition);
            if (presentRow == null) {
                WritableDataObject.OUT.log(Level.FINEST, "Row {0} not found in the Index {1} in this DataObject {2}", new Object[] { condition, this.transientRowIndex, this });
                throw new DataAccessException("No rows found for the table [" + tableAlias + "] matching the condition " + condition + " in this DataObject");
            }
            return presentRow;
        }
        else {
            final Iterator itr = this.getRows(tableAlias, condition);
            if (itr.hasNext()) {
                return itr.next();
            }
            if (condition == null) {
                WritableDataObject.OUT.log(Level.FINE, "No rows found for the table {0} in the object {1}", new Object[] { tableAlias, this });
                throw new DataAccessException("No rows found for the table [" + tableAlias + "] in this DataObject");
            }
            WritableDataObject.OUT.log(Level.FINE, "No rows found for the table {0} matching the condition {1} in the object {2}", new Object[] { tableAlias, condition, this });
            throw new DataAccessException("No rows found for the table [" + tableAlias + "] matching the condition " + condition + " in this DataObject");
        }
    }
    
    @Override
    public Row getFirstRow(final String tableAlias, final Row condition, final Join join) throws DataAccessException {
        final Iterator itr = this.getRows(tableAlias, condition, join);
        if (itr.hasNext()) {
            return itr.next();
        }
        WritableDataObject.OUT.log(Level.FINE, "No rows found for the table {0}, joined by {3}, matching the condition {1} in the object {2}", new Object[] { tableAlias, condition, this, join });
        throw new DataAccessException("No rows found for the table [" + tableAlias + "], joined by " + join + ", matching the condition " + condition + " in this DataObject");
    }
    
    protected Row getFirstRow(final Iterator iterator) {
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
    
    @Override
    public Row getRow(final String tableAlias, final Criteria condition) throws DataAccessException {
        return this.getFirstRow(this.getRows(CriteriaBasedRowIterator.ITERATOR_FOR.SINGLE_ROW, tableAlias, condition, null));
    }
    
    @Override
    public Row getRow(final String tableAlias, final Criteria condition, final Join join) throws DataAccessException {
        return this.getFirstRow(this.getRows(CriteriaBasedRowIterator.ITERATOR_FOR.SINGLE_ROW, tableAlias, condition, join));
    }
    
    @Override
    public Row getRow(final String tableAlias) throws DataAccessException {
        if (tableAlias == null) {
            throw new DataAccessException("TableAlias cannot be null");
        }
        final List rowList = this.getRowsFor(tableAlias);
        if (rowList != null && rowList.size() > 0) {
            return rowList.get(0);
        }
        return null;
    }
    
    @Override
    public Row getRow(final String tableAlias, final Row condition) throws DataAccessException {
        if (condition != null && tableAlias != null && tableAlias.equals(condition.getTableName())) {
            final Row presentRow = this.findRow(condition);
            return presentRow;
        }
        return this.getFirstRow(this.getRows(tableAlias, condition));
    }
    
    @Override
    public Row getRow(final String tableAlias, final Row condition, final Join join) throws DataAccessException {
        return this.getFirstRow(this.getRows(tableAlias, condition, join));
    }
    
    @Override
    public DataObject getDataObject(final List tableNames, final Row instance) throws DataAccessException {
        WritableDataObject.OUT.entering(WritableDataObject.CLASS_NAME, "getDataObject", new Object[] { tableNames, instance });
        if (instance == null || tableNames == null) {
            throw new DataAccessException("Both the condition row and the list of tableNames cannot be of null");
        }
        final WritableDataObject dObj = new WritableDataObject();
        dObj.query = this.query;
        dObj.joins = this.joins;
        dObj.tableAliasToTableName = this.tableAliasToTableName;
        final String tableName = instance.getTableName();
        if (!tableNames.contains(tableName)) {
            WritableDataObject.OUT.log(Level.FINER, "The table corresponding to the specified Row instance {1} should be in the tableNames list {0}", new Object[] { tableNames, instance });
            tableNames.add(tableName);
        }
        List joinsForTables = null;
        joinsForTables = ((this.query == null && this.joins.isEmpty()) ? QueryConstructor.getJoins(tableNames) : this.getJoins(tableNames));
        this.populate(dObj, joinsForTables, instance);
        WritableDataObject.OUT.exiting(WritableDataObject.CLASS_NAME, "getDataObject", dObj);
        if (WritableDataObject.getDataObjectCloneExtent.equalsIgnoreCase("all")) {
            return (DataObject)dObj.clone();
        }
        return dObj;
    }
    
    private void cloneIntoList(final Iterator iterator) throws DataAccessException {
        WritableDataObject.OUT.entering(WritableDataObject.CLASS_NAME, "cloneIntoList", iterator.hasNext());
        while (iterator.hasNext()) {
            this.add(iterator.next());
        }
    }
    
    @Override
    public Iterator get(final String tableAlias, final String columnName) throws DataAccessException {
        WritableDataObject.OUT.entering(WritableDataObject.CLASS_NAME, "get", new Object[] { tableAlias, columnName });
        final Iterator rows = this.getRows(tableAlias);
        if (!rows.hasNext()) {
            return WritableDataObject.EMPTY_ITERATOR;
        }
        final List values = new ArrayList();
        while (rows.hasNext()) {
            final Row row = rows.next();
            final Object value = row.get(columnName);
            values.add(value);
        }
        WritableDataObject.OUT.exiting(WritableDataObject.CLASS_NAME, "get", values);
        return values.iterator();
    }
    
    @Override
    public Iterator get(final String tableAlias, final int columnIndex) throws DataAccessException {
        final Iterator rows = this.getRows(tableAlias);
        if (!rows.hasNext()) {
            return WritableDataObject.EMPTY_ITERATOR;
        }
        final List values = new ArrayList();
        while (rows.hasNext()) {
            final Row row = rows.next();
            final Object value = row.get(columnIndex);
            values.add(value);
        }
        WritableDataObject.OUT.exiting(WritableDataObject.CLASS_NAME, "get", values);
        return values.iterator();
    }
    
    @Override
    public Object getFirstValue(final String tableAlias, final String columnName) throws DataAccessException {
        final Row firstRow = this.getFirstRow(tableAlias);
        return firstRow.get(columnName);
    }
    
    @Override
    public Object getFirstValue(final String tableAlias, final int columnIndex) throws DataAccessException {
        final Row firstRow = this.getFirstRow(tableAlias);
        return firstRow.get(columnIndex);
    }
    
    @Override
    public Object getValue(final String tableAlias, final int columnIndex, final Row condition) throws DataAccessException {
        final Row firstRow = this.getRow(tableAlias, condition);
        return (firstRow != null) ? firstRow.get(columnIndex) : null;
    }
    
    @Override
    public Object getValue(final String tableAlias, final String columnName, final Row condition) throws DataAccessException {
        final Row firstRow = this.getRow(tableAlias, condition);
        return (firstRow != null) ? firstRow.get(columnName) : null;
    }
    
    @Override
    public Object getValue(final String tableAlias, final int columnIndex, final Row condition, final Join join) throws DataAccessException {
        final Row firstRow = this.getRow(tableAlias, condition, join);
        return (firstRow != null) ? firstRow.get(columnIndex) : null;
    }
    
    @Override
    public Object getValue(final String tableAlias, final String columnName, final Row condition, final Join join) throws DataAccessException {
        final Row firstRow = this.getRow(tableAlias, condition, join);
        return (firstRow != null) ? firstRow.get(columnName) : null;
    }
    
    @Override
    public Object getValue(final String tableAlias, final int columnIndex, final Criteria criteria) throws DataAccessException {
        final Row firstRow = this.getRow(tableAlias, criteria);
        return (firstRow != null) ? firstRow.get(columnIndex) : null;
    }
    
    @Override
    public Object getValue(final String tableAlias, final String columnName, final Criteria criteria) throws DataAccessException {
        final Row firstRow = this.getRow(tableAlias, criteria);
        return (firstRow != null) ? firstRow.get(columnName) : null;
    }
    
    @Override
    public List getTableNames() throws DataAccessException {
        final Iterator itr = this.tableToRowList.keySet().iterator();
        final ArrayList tableNamesList = new ArrayList();
        while (itr.hasNext()) {
            tableNamesList.add(itr.next());
        }
        return tableNamesList;
    }
    
    @Override
    public void sortRows(final String tableAlias, final SortColumn... sortCols) throws DataAccessException {
        if (!AppResources.getString("allow.sortrows.on.immutable.do", "true").equals("true")) {
            this.checkImmutable();
        }
        final List<Row> rowList = this.tableToRowList.get(tableAlias);
        if (sortCols == null) {
            throw new DataAccessException("Sort Columns cannot be null.");
        }
        if (rowList != null) {
            final Row temp = rowList.get(0);
            for (final SortColumn column : sortCols) {
                if (column == null) {
                    throw new DataAccessException("The SortColumn cannot be null");
                }
                if (temp.getColumnType(column.getColumnName()).equals("BLOB") || temp.getColumnType(column.getColumnName()).equals("SBLOB")) {
                    WritableDataObject.OUT.log(Level.SEVERE, "The Column name {0} specified is of BLOB type.", column.getColumnName());
                    throw new DataAccessException("The Column name specified is of BLOB type");
                }
                if (!tableAlias.equals(column.getTableAlias())) {
                    throw new DataAccessException("The specified sortcolumn has different table name");
                }
            }
            Collections.sort(rowList, new Comparator<Row>() {
                private int i = 0;
                private Object obj1;
                private Object obj2;
                
                protected String nextName() {
                    return (this.i < sortCols.length) ? sortCols[this.i].getColumn().getColumnName() : "";
                }
                
                protected int getMultiplier() {
                    return sortCols[this.i].isAscending() ? 1 : -1;
                }
                
                @Override
                public int compare(final Row r1, final Row r2) {
                    try {
                        int result = 0;
                        final String name = this.nextName();
                        if (name != "") {
                            this.obj1 = r1.get(name);
                            this.obj2 = r2.get(name);
                            if (this.obj1 instanceof String && this.obj2 instanceof String && !sortCols[this.i].isCaseSensitive()) {
                                result = ((this.obj1 == null && this.obj2 == null) ? 0 : ((this.obj1 == null) ? (-1 * this.getMultiplier()) : ((this.obj2 == null) ? (1 * this.getMultiplier()) : (((String)this.obj1).compareToIgnoreCase((String)this.obj2) * this.getMultiplier()))));
                            }
                            else {
                                result = ((this.obj1 == null && this.obj2 == null) ? 0 : ((this.obj1 == null) ? (-1 * this.getMultiplier()) : ((this.obj2 == null) ? (1 * this.getMultiplier()) : (((Comparable)this.obj1).compareTo(this.obj2) * this.getMultiplier()))));
                            }
                            if (result == 0) {
                                ++this.i;
                                return this.compare(r1, r2);
                            }
                        }
                        this.i = 0;
                        return result;
                    }
                    catch (final ClassCastException ex) {
                        WritableDataObject.OUT.log(Level.SEVERE, "Only Column values of the same type is supported.");
                        throw ex;
                    }
                }
            });
        }
    }
    
    @Override
    public boolean containsTable(final String tableAlias) throws DataAccessException {
        return this.tableToRowList.get(tableAlias) != null;
    }
    
    @Override
    public void set(final String tableAlias, final String columnName, final Object value) throws DataAccessException {
        this.checkImmutable();
        final Iterator itr = this.getRows(tableAlias);
        while (itr.hasNext()) {
            final Row row = itr.next();
            row.set(columnName, value);
            this.updateRow(row);
        }
    }
    
    @Override
    public void set(final String tableAlias, final int columnIndex, final Object value) throws DataAccessException {
        this.checkImmutable();
        final Iterator itr = this.getRows(tableAlias);
        while (itr.hasNext()) {
            final Row row = itr.next();
            row.set(columnIndex, value);
            this.updateRow(row);
        }
    }
    
    @Override
    public void set(final String tableAlias, final String columnName, final Object value, final Criteria criteria) throws DataAccessException {
        this.checkImmutable();
        final Iterator<?> itr = this.getRows(tableAlias, criteria);
        while (itr.hasNext()) {
            final Row row = (Row)itr.next();
            row.set(columnName, value);
            this.updateRow(row);
        }
    }
    
    @Override
    public void set(final String tableAlias, final int columnIndex, final Object value, final Row condition) throws DataAccessException {
        this.checkImmutable();
        final Iterator<?> itr = this.getRows(tableAlias, condition);
        while (itr.hasNext()) {
            final Row row = (Row)itr.next();
            row.set(columnIndex, value);
            this.updateRow(row);
        }
    }
    
    @Override
    public void set(final String tableAlias, final String columnName, final Object value, final Row condition) throws DataAccessException {
        this.checkImmutable();
        final Iterator<?> itr = this.getRows(tableAlias, condition);
        while (itr.hasNext()) {
            final Row row = (Row)itr.next();
            row.set(columnName, value);
            this.updateRow(row);
        }
    }
    
    @Override
    public void set(final String tableAlias, final int columnIndex, final Object value, final Criteria criteria) throws DataAccessException {
        this.checkImmutable();
        final Iterator<?> itr = this.getRows(tableAlias, criteria);
        while (itr.hasNext()) {
            final Row row = (Row)itr.next();
            row.set(columnIndex, value);
            this.updateRow(row);
        }
    }
    
    @Override
    public void addRow(final Row newRow) throws DataAccessException {
        this.checkImmutable();
        ++this.modCount;
        if (this.isPKNull(newRow)) {
            WritableDataObject.OUT.log(Level.FINE, "Value for at least one of the PK columns is null in the row {0}", newRow);
        }
        if (!this.add(newRow)) {
            WritableDataObject.OUT.log(Level.FINEST, "Already a row with the same set of primary keys as the passed row {0} found in this object {1}", new Object[] { newRow, this });
            throw new DataAccessException("Already a row with the same set of primary keys found in this object, this row " + newRow + " already exists in this dataobject " + this);
        }
        this.validated = false;
        this.addToOperations(1, newRow, null);
    }
    
    @Override
    public void updateBlindly(final Row row) throws DataAccessException {
        final String tableName = row.getOriginalTableName();
        TableDefinition tabDef = null;
        try {
            tabDef = MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Invalid tableName specified " + tableName, mde);
        }
        if (tabDef != null && tabDef.isDirtyWriteCheckColumnsDefined()) {
            throw new DataAccessException("DO.updateBlindly(row) is not allowed for dirty-write protected table : " + tableName);
        }
        final Row existingRow = this.findRow(row);
        if (existingRow != null) {
            throw new DataAccessException("Already a row [" + existingRow + "] with the same set of Primary Key found in this DO, hence updateBlindly(row) cannot be used for this row :: [" + row + "]. Instead use updateRow(row)");
        }
        if (row.hasUVGColInPK() && row.hasUVHValueInPK()) {
            throw new DataAccessException("The value in the PK Column cannot be a UniqueValueHolder object, it should be proper Integer/Long value :: " + row);
        }
        if (row.getChangedColumnIndex() != null) {
            this.validated = false;
            this.addBlindly(row);
            this.addToOperations(2, row, null);
        }
        else {
            WritableDataObject.OUT.log(Level.WARNING, "No column value seems to be changed in this row :: [{0}], hence ignoring this row for the updateBlindly operation", row);
        }
    }
    
    @Override
    public void updateRow(final Row modifRow) throws DataAccessException {
        this.updateRow(modifRow, true);
    }
    
    private void updateRow(final Row modifRow, final boolean useChangeColumnIndex) throws DataAccessException {
        if (this.writeProtectedTables.contains(modifRow.getOriginalTableName())) {
            throw new DataAccessException("Dirty-write-check-Columns is not included in selectColumns for this table: [ " + modifRow.getOriginalTableName() + " ] in the SelectQuery");
        }
        this.checkImmutable();
        if (useChangeColumnIndex && modifRow.getChangedColumnIndex() == null) {
            return;
        }
        Row row = this.findRow(modifRow);
        boolean pkModif = false;
        if (row == null) {
            if (modifRow.hasPKChanged()) {
                this.reIndex(modifRow);
                row = this.findRow(modifRow);
                pkModif = true;
            }
            if (row == null) {
                WritableDataObject.OUT.log(Level.FINEST, "Specified row {0} is not found in this object {1}", new Object[] { modifRow, this });
                return;
            }
        }
        if (!pkModif) {
            this.update(row, modifRow);
        }
        this.validated = false;
        if (row.getChangedColumnIndex() != null) {
            this.addToOperations(2, row, null);
        }
    }
    
    public HashMap getOperationTables() {
        return this.operTypeVsOperTables;
    }
    
    @Override
    public int size(final String tableAlias) {
        if (tableAlias == null) {
            WritableDataObject.OUT.log(Level.FINEST, "TableAlias must not be null {0}", tableAlias);
            return -1;
        }
        if (this.tableToRowList == null) {
            WritableDataObject.OUT.log(Level.FINEST, "No row found in DataObject {0}", this);
            return -1;
        }
        if (!this.tableToRowList.containsKey(tableAlias)) {
            WritableDataObject.OUT.log(Level.FINEST, "No row found for tableName {0} in DataObject {1}", new Object[] { tableAlias, this });
            return -1;
        }
        final List rows = this.tableToRowList.get(tableAlias);
        return rows.size();
    }
    
    @Override
    public void deleteRow(final Row delRow) throws DataAccessException {
        this.deleteRow(delRow, false);
    }
    
    @Override
    public void deleteRowIgnoreFK(final Row delRow) throws DataAccessException {
        this.deleteRow(delRow, false, false);
    }
    
    int deleteRow(final Row delRow, final boolean sameRef) throws DataAccessException {
        return this.deleteRow(delRow, sameRef, true);
    }
    
    int deleteRow(final Row delRow, final boolean sameRef, final boolean onDeleteCascade) throws DataAccessException {
        this.checkImmutable();
        ++this.modCount;
        Row row = null;
        if (sameRef) {
            row = delRow;
        }
        else {
            row = this.findRow(delRow);
        }
        if (row == null) {
            WritableDataObject.OUT.log(Level.FINEST, "Specified row {0} is not found in this object {1}", new Object[] { delRow, this });
            return -1;
        }
        this.actualRowDeleted = row;
        if (onDeleteCascade) {
            final List referringRows = new ArrayList();
            this.loadRefRows(row.getTableName(), row, referringRows);
            Object obj = null;
            for (int i = 0; i < referringRows.size(); ++i) {
                obj = referringRows.get(i);
                if (obj == row) {
                    referringRows.remove(i);
                    --i;
                }
            }
            final int size = referringRows.size();
            if (size > 0) {
                for (int j = 0; j < size; ++j) {
                    final Row rowToDelete = referringRows.get(j);
                    this.deleteAndUpdateOperInfo(rowToDelete, rowToDelete.getTableName());
                }
            }
        }
        Object obj2 = null;
        final List rowList = this.getRowsFor(row.getTableName());
        int delIndex = -1;
        if (rowList == null) {
            return -1;
        }
        for (int j = 0; j < rowList.size(); ++j) {
            obj2 = rowList.get(j);
            if (obj2 == row) {
                delIndex = j;
                break;
            }
        }
        if (delIndex != -1) {
            this.deleteAndUpdateOperInfo(row, row.getTableName());
            this.actualRowDeleted = null;
            this.validated = false;
        }
        return delIndex;
    }
    
    int deleteAndReturnIndex(final Row toDelete) throws DataAccessException {
        return this.deleteAndReturnIndex(toDelete, false);
    }
    
    int deleteAndReturnIndex(final Row toDelete, final boolean sameRef) throws DataAccessException {
        return this.deleteRow(toDelete, sameRef);
    }
    
    int deleteAndReturnIndex(final Row toDelete, final boolean sameRef, final boolean onDeleteCascade) throws DataAccessException {
        return this.deleteRow(toDelete, sameRef, onDeleteCascade);
    }
    
    private void loadRefRows(final String tableAlias, final Row toDelete, final List referringRows) throws DataAccessException {
        final List tablesToDelete = this.getListOfReferringTableNames(tableAlias);
        WritableDataObject.OUT.log(Level.FINE, "List of referring table are {0} for table {1}", new Object[] { tablesToDelete, tableAlias });
        if (tablesToDelete == null) {
            referringRows.add(toDelete);
            return;
        }
        for (int size = tablesToDelete.size(), i = 0; i < size; ++i) {
            final String name = tablesToDelete.get(i);
            Join join = null;
            List fkDefs = null;
            try {
                fkDefs = MetaDataUtil.getForeignKeys(tableAlias, name);
            }
            catch (final MetaDataException mde) {
                WritableDataObject.OUT.log(Level.FINEST, "Exception occured while getting the foreign key definitions between {0} and {1}", new Object[] { tableAlias, name });
                fkDefs = new ArrayList();
            }
            if (name.equals(tableAlias)) {
                join = this.getJoinForSelfRefTables(name);
            }
            else {
                join = this.getJoin(tableAlias, name);
            }
            WritableDataObject.OUT.log(Level.FINEST, "Join returned {0}", join);
            final Iterator itr = this.getRows(name, toDelete, join);
            while (itr.hasNext()) {
                final Row refRow = itr.next();
                for (int fkSize = fkDefs.size(), j = 0; j < fkSize; ++j) {
                    final ForeignKeyDefinition fkDef = fkDefs.get(j);
                    this.handleFKConstraint(toDelete, refRow, fkDef, referringRows);
                }
            }
        }
        referringRows.add(toDelete);
    }
    
    private void handleFKConstraint(final Row toDelete, final Row refRow, final ForeignKeyDefinition fkDef, final List referringRows) throws DataAccessException {
        final int constraint = fkDef.getConstraints();
        switch (constraint) {
            case 1: {
                this.loadRefRows(refRow.getTableName(), refRow, referringRows);
                break;
            }
            case 0: {
                WritableDataObject.OUT.log(Level.FINEST, "Encountered a table {0} with ON_DELETE_RESTRICT option. So, avoiding the delete of Row {1}", new Object[] { refRow.getTableName(), toDelete });
                throw new DataAccessException("ForeignKey constraint fails. Cannot delete Row" + toDelete);
            }
            case 2: {
                final List columnList = fkDef.getForeignKeyColumns();
                for (int colSize = columnList.size(), i = 0; i < colSize; ++i) {
                    final ForeignKeyColumnDefinition fkcd = columnList.get(i);
                    final ColumnDefinition lcd = fkcd.getLocalColumnDefinition();
                    final String refColumnName = fkcd.getReferencedColumnDefinition().getColumnName();
                    final String columnName = lcd.getColumnName();
                    if (toDelete.get(refColumnName).equals(refRow.get(columnName))) {
                        refRow.set(lcd.getColumnName(), null);
                    }
                }
                this.updateRow(refRow);
                break;
            }
            case 3: {
                final List columnList = fkDef.getForeignKeyColumns();
                for (int colSize = columnList.size(), i = 0; i < colSize; ++i) {
                    final ForeignKeyColumnDefinition fkcd = columnList.get(i);
                    final ColumnDefinition lcd = fkcd.getLocalColumnDefinition();
                    final Object defaultValue = lcd.getDefaultValue();
                    refRow.set(lcd.getColumnName(), defaultValue);
                }
                this.updateRow(refRow);
                break;
            }
        }
    }
    
    private Join getJoinForSelfRefTables(final String tableName) throws DataAccessException {
        final ForeignKeyDefinition fkDef = QueryConstructor.getSuitableFK(tableName, tableName);
        final String masterTable = fkDef.getMasterTableName();
        final String slaveTable = fkDef.getSlaveTableName();
        final List fkCols = fkDef.getForeignKeyColumns();
        final int size = fkCols.size();
        final String[] masterColumns = new String[size];
        final String[] slaveColumns = new String[size];
        for (int i = 0; i < size; ++i) {
            final ForeignKeyColumnDefinition fkColDef = fkCols.get(i);
            slaveColumns[i] = fkColDef.getLocalColumnDefinition().getColumnName();
            masterColumns[i] = fkColDef.getReferencedColumnDefinition().getColumnName();
        }
        return new Join(slaveTable, masterTable, slaveColumns, masterColumns, 2);
    }
    
    private void deleteAndUpdateOperInfo(final Row toDelete, final String tableAlias) throws DataAccessException {
        this.delete(toDelete);
        this.cleanUpTableName(tableAlias);
        WritableDataObject.OUT.log(Level.FINEST, "Adding Delete ActionInfo for {0} ", toDelete);
        this.addToOperations(3, toDelete, null);
    }
    
    private List getListOfReferringTableNames(final String parentTableName) throws DataAccessException {
        List refTableList = null;
        try {
            final List tableNames = this.getTableNames();
            final List tableDefs = MetaDataUtil.getAllRelatedTableDefinitions(parentTableName);
            if (tableDefs == null) {
                return null;
            }
            for (int size = tableDefs.size(), i = 0; i < size; ++i) {
                final String tableName = tableDefs.get(i).getTableName();
                if (tableNames.contains(tableName)) {
                    if (refTableList == null) {
                        refTableList = new ArrayList();
                    }
                    refTableList.add(tableName);
                }
            }
        }
        catch (final MetaDataException meExp) {
            throw new DataAccessException(meExp.getMessage(), meExp);
        }
        return refTableList;
    }
    
    @Override
    public void deleteRows(final String tableAlias, final Criteria condition) throws DataAccessException {
        this.checkImmutable();
        final Iterator itr = this.getRows(tableAlias, condition);
        while (itr.hasNext()) {
            itr.next();
            itr.remove();
        }
    }
    
    @Override
    public void deleteRows(final String tableAlias, final Row condition) throws DataAccessException {
        this.checkImmutable();
        final Iterator itr = this.getRows(tableAlias, condition);
        while (itr.hasNext()) {
            itr.next();
            itr.remove();
        }
    }
    
    @Override
    public void deleteRowsIgnoreFK(final String tableAlias, final Criteria condition) throws DataAccessException {
        this.checkImmutable();
        final CriteriaBasedRowIterator itr = (CriteriaBasedRowIterator)this.getRows(tableAlias, condition);
        while (itr.hasNext()) {
            itr.next();
            itr.removeIgnoreFK();
        }
    }
    
    @Override
    public void deleteRowsIgnoreFK(final String tableAlias, final Row condition) throws DataAccessException {
        this.checkImmutable();
        final RowIterator itr = (RowIterator)this.getRows(tableAlias, condition);
        while (itr.hasNext()) {
            itr.next();
            itr.removeIgnoreFK();
        }
    }
    
    private void cleanUpTableName(final String tableAlias) throws DataAccessException {
        final List tableNames = this.getTableNames();
        final Iterator itr = this.getRows(tableAlias);
        if (!itr.hasNext()) {
            tableNames.remove(tableAlias);
        }
    }
    
    public List getDataObjects() throws DataAccessException {
        WritableDataObject.OUT.entering(WritableDataObject.CLASS_NAME, "getDataObjects");
        if (this.query == null) {
            WritableDataObject.OUT.log(Level.FINER, "DataObject.getDataObjects() method can be invoked only on DataObject instances that are retrieved from the database. The object, which the method is invoked on, is {0}", this);
            throw new DataAccessException("DataObject.getDataObjects() method can be invoked only on DataObject instances that are retrieved from the database");
        }
        final List subObjects = new ArrayList();
        final List tableNames = this.query.getTableList();
        final Table primaryTable = tableNames.get(0);
        final String primaryTableName = primaryTable.getTableAlias();
        final Iterator itr = this.getRows(primaryTableName);
        while (itr.hasNext()) {
            final Row row = itr.next();
            final WritableDataObject subDObj = new WritableDataObject();
            subDObj.query = this.query;
            subDObj.joins = this.joins;
            subDObj.tableAliasToTableName = this.tableAliasToTableName;
            this.populate(subDObj, this.joins, row);
            if (WritableDataObject.getDataObjectCloneExtent.equalsIgnoreCase("all")) {
                subObjects.add(subDObj.clone());
            }
            else {
                subObjects.add(subDObj);
            }
        }
        WritableDataObject.OUT.exiting(WritableDataObject.CLASS_NAME, "getDataObjects", subObjects);
        return subObjects;
    }
    
    @Override
    public List getOperations() {
        final List operations = new ArrayList();
        for (final OperationTables ot : this.operTypeVsOperTables.values()) {
            final Iterator actionItr = ot.getActions().values().iterator();
            while (actionItr.hasNext()) {
                operations.addAll(actionItr.next());
            }
        }
        return operations;
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("<WritableDataObject");
        if (this.immutable) {
            buff.append(" immutable=true");
        }
        buff.append(">\n");
        try {
            final List tableNames = this.getTableNames();
            if (tableNames != null) {
                buff.append(" <Tables>\n");
                buff.append(tableNames);
                buff.append(" </Tables>\n");
            }
        }
        catch (final Exception x) {
            WritableDataObject.OUT.log(Level.FINER, "Exception while appending tablenames", x);
        }
        if (this.query != null) {
            buff.append(this.query + "\n");
        }
        if (this.joins != null) {
            buff.append(" <JoinsInDO>\n");
            for (int joinsSize = this.joins.size(), i = 0; i < joinsSize; ++i) {
                buff.append("  ").append(this.joins.get(i)).append("\n");
            }
            buff.append(" </JoinsInDO>\n");
        }
        final List operations = this.getOperations();
        if (operations != null) {
            buff.append(" <Operations>\n");
            for (int operationSize = operations.size(), j = 0; j < operationSize; ++j) {
                buff.append("  ").append(operations.get(j)).append("\n");
            }
            buff.append(" </Operations>\n");
        }
        final List rowList = this.getAllRows();
        if (rowList != null) {
            buff.append(" <Rows>\n");
            for (int rowSize = rowList.size(), k = 0; k < rowSize; ++k) {
                buff.append("  ").append(rowList.get(k)).append("\n");
            }
            buff.append(" </Rows>\n");
        }
        buff.append("</WritableDataObject>\n");
        return buff.toString();
    }
    
    public void clearOperations() {
        this.checkImmutable();
        final List operations = this.getOperations();
        for (int size = operations.size(), i = 0; i < size; ++i) {
            final ActionInfo info = operations.get(i);
            final Row row = info.getValue();
            row.markAsClean();
        }
        this.operTypeVsOperTables = new HashMap<String, OperationTables>();
        this.deleteRowIndex = null;
        this.transientRowIndex = null;
    }
    
    private void populate(final WritableDataObject dObj, final List joins, final Row row) throws DataAccessException {
        final ArrayList clonedJoins = new ArrayList(joins);
        Iterator dObjIterator = null;
        int dObjInitialSize = 0;
        int dObjChangedSize = 0;
        final String rowTableName = row.getTableName();
        final Row condRow = this.findRow(row);
        if (condRow != null) {
            if (WritableDataObject.getDataObjectCloneExtent.equalsIgnoreCase("ignore_uvh")) {
                dObj.addBlindly((Row)condRow.clone());
            }
            else {
                dObj.addBlindly(condRow);
            }
            ++dObjChangedSize;
            while (dObjChangedSize > dObjInitialSize) {
                dObjInitialSize = dObjChangedSize;
                final Iterator joinIterator = clonedJoins.iterator();
                String tableNameToBeFetchedFromdObj = null;
                String tableNameToBeFetchedFromActualDO = null;
                while (joinIterator.hasNext()) {
                    final Join join = joinIterator.next();
                    WritableDataObject.OUT.log(Level.FINEST, "Starting to process the join :: {0}", join);
                    final String baseTableName = join.getBaseTableAlias();
                    final String referenceTableName = join.getReferencedTableAlias();
                    tableNameToBeFetchedFromActualDO = null;
                    tableNameToBeFetchedFromdObj = null;
                    if (dObj.containsTable(baseTableName) && !dObj.containsTable(referenceTableName)) {
                        tableNameToBeFetchedFromdObj = baseTableName;
                        tableNameToBeFetchedFromActualDO = referenceTableName;
                    }
                    else {
                        if (dObj.containsTable(baseTableName) || !dObj.containsTable(referenceTableName)) {
                            continue;
                        }
                        tableNameToBeFetchedFromdObj = referenceTableName;
                        tableNameToBeFetchedFromActualDO = baseTableName;
                    }
                    dObjIterator = dObj.getRows(tableNameToBeFetchedFromdObj);
                    while (dObjIterator.hasNext()) {
                        final Row r = dObjIterator.next();
                        WritableDataObject.OUT.log(Level.FINEST, "Processing Row :: {0} for Join :: {1}", new Object[] { r, join });
                        final Iterator iterator = this.getRows(tableNameToBeFetchedFromActualDO, r, join);
                        while (iterator.hasNext()) {
                            final Row rowToBeAdded = iterator.next();
                            if (dObj.findRow(rowToBeAdded) == null) {
                                if (WritableDataObject.getDataObjectCloneExtent.equalsIgnoreCase("ignore_uvh")) {
                                    dObj.addBlindly((Row)rowToBeAdded.clone());
                                }
                                else {
                                    dObj.addBlindly(rowToBeAdded);
                                }
                            }
                            WritableDataObject.OUT.log(Level.FINEST, "Row added in the returnDO :: {0}", rowToBeAdded);
                            ++dObjChangedSize;
                        }
                    }
                    if (dObjChangedSize > dObjInitialSize) {
                        WritableDataObject.OUT.log(Level.FINEST, "Removed Join :: {0}", join);
                        joinIterator.remove();
                    }
                }
            }
            WritableDataObject.OUT.log(Level.FINEST, "dObj :: {0}", dObj);
            return;
        }
        WritableDataObject.OUT.log(Level.FINEST, "Specified row {0} not found in the dataObject {1} , the transientRowIndex {2} and is PK changed {3}", new Object[] { row, this, this.transientRowIndex, row.hasPKChanged() });
        throw new DataAccessException("Specified row " + row + " not found in the dataObject.");
    }
    
    void deleteActionInfo(final String tableName) {
        for (int i = 1; i < this.operName.length; ++i) {
            final OperationTables operTables = this.operTypeVsOperTables.get(this.operName[i]);
            if (operTables != null) {
                operTables.getActions().remove(tableName);
            }
        }
    }
    
    void addToOperations(final int operation, final Row value, final Criteria condition) {
        if (!this.getTrackOperations()) {
            return;
        }
        if (!value.getTableName().equals(value.getOriginalTableName())) {
            this.tableAliasToTableName.put(value.getTableName(), value.getOriginalTableName());
        }
        OperationTables tAction = this.operTypeVsOperTables.get(this.operName[operation]);
        if (tAction == null) {
            tAction = new OperationTables(operation);
            this.operTypeVsOperTables.put(this.operName[operation], tAction);
        }
        tAction.addActionInfo(value, condition);
    }
    
    public HashMap sortActionInfos() throws DataAccessException {
        final HashMap sortedLists = new HashMap();
        for (int i = 1; i < this.operName.length; ++i) {
            final String operationName = this.operName[i];
            final OperationTables operTable = this.operTypeVsOperTables.get(operationName);
            if (operTable != null) {
                final HashMap tableVsActionList = operTable.getActions();
                final List involvedTables = operTable.getTablesInvolved();
                List sortedTables = PersistenceUtil.sortTables(this, involvedTables);
                if (operationName.equals("delete")) {
                    final ArrayList deleteTableList = new ArrayList();
                    for (int k = sortedTables.size() - 1; k >= 0; --k) {
                        deleteTableList.add(sortedTables.get(k));
                    }
                    sortedTables = deleteTableList;
                }
                for (final String tableName : sortedTables) {
                    final List actList = tableVsActionList.get(tableName);
                    List sortedActList = sortedLists.get(operationName);
                    if (sortedActList == null) {
                        sortedActList = new ArrayList();
                        sortedLists.put(operationName, sortedActList);
                    }
                    sortedActList.addAll(actList);
                }
            }
        }
        return sortedLists;
    }
    
    private void remove(final Row toRemove, final Map tableNameToRowList) {
        final String name = toRemove.getTableName();
        final List list = tableNameToRowList.get(name);
        final boolean remove = list.remove(toRemove);
    }
    
    private Criteria getCriteriaForSelfRefTable(final String tableAlias, final Row row) throws DataAccessException {
        Criteria criteria = null;
        final String name = row.getTableName();
        final ForeignKeyDefinition fkDefn = QueryConstructor.getSuitableFK(name, name);
        if (fkDefn == null) {
            return null;
        }
        final List fkColDefs = fkDefn.getForeignKeyColumns();
        if (fkColDefs == null || fkColDefs.isEmpty()) {
            return null;
        }
        for (int size = fkColDefs.size(), i = 0; i < size; ++i) {
            final ForeignKeyColumnDefinition fkCol = fkColDefs.get(i);
            final ColumnDefinition refColDef = fkCol.getReferencedColumnDefinition();
            final ColumnDefinition localColDef = fkCol.getLocalColumnDefinition();
            final String refColName = refColDef.getColumnName();
            final String localColName = localColDef.getColumnName();
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn(row.getTableName(), refColName), row.get(localColName), 0);
            }
            else {
                final Criteria crt = new Criteria(Column.getColumn(tableAlias, refColName), row.get(localColName), 0);
                criteria = criteria.and(crt);
            }
        }
        return criteria;
    }
    
    private Criteria getCriteriaForSelfRefTable(final String tableAlias, final Row row, final Join join) throws DataAccessException {
        WritableDataObject.OUT.log(Level.FINER, "Table {0} Row {1} Join {2}", new Object[] { tableAlias, row, join });
        if (join.getBaseTableColumnIndices() == null) {
            PersistenceUtil.populateColumnIndicesInformation(join);
        }
        final int[] refColIndices = join.getReferencedTableColumnIndices();
        final int[] baseColIndices = join.getBaseTableColumnIndices();
        final int[] keyIndices = row.getKeyIndices();
        final int[] toComp = Arrays.equals(keyIndices, baseColIndices) ? refColIndices : baseColIndices;
        Criteria criteria = null;
        for (int length = toComp.length, i = 0; i < length; ++i) {
            WritableDataObject.OUT.log(Level.FINER, "BaseTableColumn {0} ReferenceTableColumn {1}", new Object[] { join.getBaseTableColumn(i), join.getReferencedTableColumn(i) });
            if (criteria == null) {
                criteria = new Criteria(new Column(tableAlias, toComp[i]), row.get(keyIndices[i]), 0);
            }
            else {
                final Criteria crt = new Criteria(new Column(tableAlias, toComp[i]), row.get(keyIndices[i]), 0);
                criteria = criteria.and(crt);
            }
        }
        return criteria;
    }
    
    private ActionInfo getActionInfoForDelete(final Row originalRow, ActionInfo inCache) {
        final Row clonedRow = (Row)originalRow.clone();
        clonedRow.deletedAt = String.valueOf(System.currentTimeMillis());
        if (inCache == null) {
            inCache = new ActionInfo(3, clonedRow, null);
        }
        else {
            inCache.setValue(clonedRow);
            inCache.setOperation(3);
        }
        return inCache;
    }
    
    @Override
    public void merge(final DataObject dob) throws DataAccessException {
        this.checkImmutable();
        ++this.modCount;
        WritableDataObject writableDO = null;
        try {
            writableDO = (WritableDataObject)dob;
        }
        catch (final ClassCastException ccex) {
            throw new DataAccessException("Merge can be called only passing WritableDataObject, merging other implementations of DataObject are not supported");
        }
        final List passedOperations = writableDO.getOperations();
        for (int operSize = passedOperations.size(), i = 0; i < operSize; ++i) {
            boolean alreadyDeleted = false;
            final ActionInfo action = passedOperations.get(i);
            final int operation = action.getOperation();
            Row row = action.getValue();
            row = (Row)row.clone();
            final Row origRow = this.findRow(row);
            final List operations = this.getOperations();
            for (final ActionInfo actInfo : operations) {
                final Row oldRow = actInfo.getValue();
                if (actInfo.getOperation() == 3 && oldRow.getPKValues().equals(row.getPKValues())) {
                    alreadyDeleted = true;
                }
            }
            if (!alreadyDeleted) {
                switch (operation) {
                    case 1: {
                        if (origRow != null) {
                            WritableDataObject.OUT.log(Level.FINEST, "Already a row with the same set of primary keys and ActionInfo {0} found in this object {1}", new Object[] { row, this });
                            throw new DataAccessException("Already a row with the same set of primary keys and ActionInfo " + row + " found in this object " + this);
                        }
                        this.addBlindly(row);
                        this.addToOperations(operation, row, null);
                        break;
                    }
                    case 2: {
                        if (origRow == null) {
                            this.addBlindly(row);
                            this.addToOperations(operation, row, null);
                            break;
                        }
                        if (AppResources.getBoolean("useMergeUpdateOldBehaviour", Boolean.valueOf(false))) {
                            this.update(origRow, row);
                            this.addToOperations(operation, row, null);
                            break;
                        }
                        if (origRow.getOperationIndex() == 2) {
                            this.updateMerge(origRow, row);
                            break;
                        }
                        this.updateRow(row);
                        break;
                    }
                    case 3: {
                        if (origRow != null) {
                            this.deleteRow(row);
                            break;
                        }
                        this.addToOperations(operation, row, null);
                        break;
                    }
                    default: {
                        throw new DataAccessException("Unknown operation " + operation + " specified in the passed DataObject.");
                    }
                }
            }
        }
        final List rows = writableDO.getAllRows();
        for (int rowSize = rows.size(), j = 0; j < rowSize; ++j) {
            boolean alreadyDeleted2 = false;
            final Row passedRow = rows.get(j);
            final List operations2 = this.getOperations();
            for (final ActionInfo actInfo2 : operations2) {
                final Row oldRow2 = actInfo2.getValue();
                if (actInfo2.getOperation() == 3 && oldRow2.getPKValues().equals(passedRow.getPKValues())) {
                    alreadyDeleted2 = true;
                }
            }
            if (!alreadyDeleted2) {
                this.add(passedRow, true);
                this.fillTableAliasToTableName(passedRow);
            }
        }
        final List joinsInDataObjToMerge = writableDO.joins;
        for (int joinsSize = joinsInDataObjToMerge.size(), k = 0; k < joinsSize; ++k) {
            final Join joinToAdd = joinsInDataObjToMerge.get(k);
            if (!this.hasJoin(joinToAdd)) {
                this.addJoin(joinToAdd);
            }
        }
    }
    
    private void fillTableAliasToTableName(final Row row) {
        final String tabAlias = row.getTableName();
        final String tabName = row.getOriginalTableName();
        this.fillTableAliasToTableName(tabAlias, tabName);
    }
    
    private void fillTableAliasToTableName(final String tabAlias, final String tabName) {
        if (!tabAlias.equals(tabName)) {
            this.tableAliasToTableName.put(tabAlias, tabName);
        }
    }
    
    private Map getTableVsKeyIndices(final Map tableVsIndices) throws DataAccessException {
        final HashMap map = new HashMap(tableVsIndices.size());
        try {
            for (final Map.Entry entry : tableVsIndices.entrySet()) {
                final Table table = entry.getKey();
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(table.getTableName());
                final int[] keyIndices = td.getKeyIndices();
                map.put(table, keyIndices);
            }
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException(mde.getMessage(), mde);
        }
        return map;
    }
    
    private boolean isKeyIndex(final int index, final int[] keyIndices) {
        for (int i = 0; i < keyIndices.length; ++i) {
            if (keyIndices[i] == index) {
                return true;
            }
        }
        return false;
    }
    
    private void process(final DataSet ds) throws DataAccessException {
        WritableDataObject.OUT.entering(WritableDataObject.CLASS_NAME, "process", ds);
        final Row DUMMY_ROW = new Row();
        try {
            final SelectQuery sq = (SelectQuery)ds.getSelectQuery();
            final List tables = sq.getTableList();
            WritableDataObject.OUT.log(Level.FINEST, "Table List {0}", tables);
            final List selectColumns = sq.getSelectColumns();
            final TableMappings[] tMapArr = this.getTableMappings(tables, selectColumns, sq);
            while (ds.next()) {
                for (int k = 0; k < tMapArr.length; ++k) {
                    final TableMappings tMap = tMapArr[k];
                    final int[] keyIndices = tMap.getKeyIndices();
                    final int[] indices = tMap.getColumnIndices();
                    Object[] values = new Object[indices.length];
                    for (int i = 0; i < indices.length; ++i) {
                        if (indices[i] == 0) {
                            values[i] = null;
                        }
                        else {
                            values[i] = ds.getValue(indices[i]);
                        }
                        if (values[i] == null && Arrays.binarySearch(keyIndices, i + 1) >= 0) {
                            values = null;
                            break;
                        }
                    }
                    if (values != null) {
                        DUMMY_ROW.reinit(tMap.getTableName(), tMap.getTableAlias(), values);
                        this.add(DUMMY_ROW, true);
                    }
                }
            }
        }
        catch (final SQLException exc) {
            exc.printStackTrace();
            WritableDataObject.OUT.log(Level.SEVERE, "Exception occured during processing the DataSet {0}", ds);
            throw new DataAccessException("Exception occured during processing the DataSet " + ds, exc);
        }
    }
    
    private List getJoins(final List tableNames) throws DataAccessException {
        final List toBeJoined = new ArrayList(tableNames);
        final String tableName = toBeJoined.remove(0);
        final List newJoins = new ArrayList();
        if (this.joins != null) {
            this.formJoinsFromJoins(tableName, newJoins, toBeJoined);
        }
        this.formJoinsFromMetaData(tableName, newJoins, toBeJoined);
        return newJoins;
    }
    
    private void formJoinsFromJoins(final String tableName, final List newJoins, final List toBeJoined) throws DataAccessException {
        WritableDataObject.OUT.entering(WritableDataObject.CLASS_NAME, "formJoinsFromJoins", new Object[] { tableName, newJoins, toBeJoined });
        final int size = toBeJoined.size();
        for (int i = 0; i < toBeJoined.size(); ++i) {
            final String relatedTableName = toBeJoined.get(i);
            final Join join = this.getJoinFromJoins(tableName, relatedTableName);
            if (join != null) {
                toBeJoined.remove(i);
                newJoins.add(join);
                this.formJoinsFromJoins(relatedTableName, newJoins, toBeJoined);
                i = -1;
            }
        }
        WritableDataObject.OUT.log(Level.FINEST, "Returning from formJoins joins={0} toBeJoined={1}", new Object[] { newJoins, toBeJoined });
    }
    
    private void formJoinsFromMetaData(final String tableName, final List newJoins, final List toBeJoined) throws DataAccessException {
        WritableDataObject.OUT.entering(WritableDataObject.CLASS_NAME, "formJoinsFromMetaData", new Object[] { tableName, newJoins, toBeJoined });
        final int size = toBeJoined.size();
        for (int i = 0; i < toBeJoined.size(); ++i) {
            final String relatedTableName = toBeJoined.get(i);
            final Join join = this.getJoinFromMetaData(tableName, relatedTableName);
            if (join != null) {
                toBeJoined.remove(i);
                newJoins.add(join);
                this.formJoinsFromMetaData(relatedTableName, newJoins, toBeJoined);
                i = -1;
            }
        }
        WritableDataObject.OUT.log(Level.FINEST, "Returning from formJoins joins={0} toBeJoined={1}", new Object[] { newJoins, toBeJoined });
    }
    
    private Join getJoin(final String table1, final String table2) throws DataAccessException {
        return this.getJoin(table1, table2, null);
    }
    
    private Join getJoin(final String table1, final String table2, final String colName) throws DataAccessException {
        Join join = null;
        if (this.joins != null) {
            join = this.getJoinFromJoins(table1, table2);
        }
        if (join == null) {
            if (colName == null) {
                join = this.getJoinFromMetaData(table1, table2);
            }
            else {
                join = this.getJoinFromMetaData(table1, table2, colName);
            }
        }
        return join;
    }
    
    private Join getJoinFromMetaData(final String table1, final String table2) throws DataAccessException {
        return this.getJoinFromMetaData(table1, table2, null);
    }
    
    private Join getJoinFromMetaData(final String table1, final String table2, final String colName) throws DataAccessException {
        Join join = null;
        ForeignKeyDefinition fkDefn = null;
        if (colName != null) {
            fkDefn = QueryConstructor.getSuitableFK(table1, table2, colName);
        }
        else {
            fkDefn = QueryConstructor.getSuitableFK(table1, table2);
        }
        if (fkDefn != null) {
            join = QueryConstructor.getJoin(fkDefn);
        }
        return join;
    }
    
    private Join getJoinFromJoins(final String table1, final String table2) throws DataAccessException {
        for (int size = this.joins.size(), i = 0; i < size; ++i) {
            final Join join = this.joins.get(i);
            final String baseTab = join.getBaseTableAlias();
            final String refTab = join.getReferencedTableAlias();
            if ((table1.equals(baseTab) && table2.equals(refTab)) || (table1.equals(refTab) && table2.equals(baseTab))) {
                return join;
            }
        }
        WritableDataObject.OUT.log(Level.FINER, "No join is found connecting the tables {0} and {1} in the SelectQuery {2}", new Object[] { table1, table2, this.query });
        return null;
    }
    
    private Map getTableBasedIndices(final List tables, final List selectColumns) throws DataAccessException {
        final Map tableVsIndices = new HashMap();
        for (int size = tables.size(), i = 0; i < size; ++i) {
            final Table table = tables.get(i);
            final String tableName = table.getTableName();
            final String tableAlias = table.getTableAlias();
            final int[] indices = this.getIndices(tableName, tableAlias, selectColumns, null);
            if (indices != null) {
                tableVsIndices.put(table, indices);
            }
        }
        return tableVsIndices;
    }
    
    private boolean hasSelectColumns(final String tableAlias, final List selectColumns) {
        for (int i = 0; i < selectColumns.size(); ++i) {
            final Column col = selectColumns.get(i);
            if (tableAlias.equals(col.getTableAlias())) {
                return true;
            }
        }
        return false;
    }
    
    private TableMappings[] getTableMappings(final List tables, final List selectColumns, final Query sq) throws DataAccessException {
        final int size = tables.size();
        final TableMappings[] tMappingsArr = new TableMappings[size];
        int counter = 0;
        for (int i = 0; i < size; ++i) {
            final Table table = tables.get(i);
            String tableName = table.getTableName();
            final String tableAlias = table.getTableAlias();
            if (table instanceof DerivedTable) {
                if (PersistenceInitializer.onSAS()) {
                    if (AppResources.getBoolean("ignore.derivedtable.in.selectcols.of.dob", Boolean.valueOf(true))) {
                        if (this.hasSelectColumns(tableAlias, selectColumns)) {
                            WritableDataObject.OUT.log(Level.WARNING, "DataObject is not allowed for SelectQuery containing DerivedTable. Kindly use DataSet.");
                        }
                        continue;
                    }
                }
                else {
                    if (this.hasSelectColumns(tableAlias, selectColumns)) {
                        throw new DataAccessException("DataObject is not allowed for SelectQuery containing DerivedTable. Kindly use DataSet.");
                    }
                    continue;
                }
            }
            else if (sq != null) {
                tableName = getTableNameFromDerivedTable(tableName, sq);
            }
            final int[] indices = this.getIndices(tableName, tableAlias, selectColumns, sq);
            try {
                final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
                if (indices != null) {
                    final TableMappings tMap = new TableMappings(tableName, tableAlias);
                    tMap.setColumnIndices(indices);
                    tMap.setKeyIndices(td.getKeyIndices());
                    tMappingsArr[counter++] = tMap;
                }
            }
            catch (final MetaDataException mde) {
                throw new DataAccessException("Exception occured while getting the table definition for " + tableName);
            }
        }
        if (counter < size) {
            final TableMappings[] retMapArr = new TableMappings[counter];
            System.arraycopy(tMappingsArr, 0, retMapArr, 0, counter);
            return retMapArr;
        }
        return tMappingsArr;
    }
    
    private static String getTableNameFromDerivedTable(final String tableName, final Query sq) {
        if (((SelectQuery)sq).getDerivedTables().size() > 0) {
            final List<DerivedTable> derivedTables = ((SelectQuery)sq).getDerivedTables();
            for (final DerivedTable dt : derivedTables) {
                if (dt.getTableAlias().equalsIgnoreCase(tableName)) {
                    if (dt.getSubQuery() instanceof SelectQuery) {
                        return ((SelectQuery)dt.getSubQuery()).getTableList().get(0).getTableName();
                    }
                    if (dt.getSubQuery() instanceof UnionQuery) {
                        final UnionQuery current = (UnionQuery)dt.getSubQuery();
                        final Query leftQuery = current.getLeftQuery();
                        final Query rightQuery = current.getRightQuery();
                        if (leftQuery != null) {
                            getTableNameFromDerivedTable(tableName, leftQuery);
                        }
                        if (rightQuery != null) {
                            getTableNameFromDerivedTable(tableName, rightQuery);
                        }
                    }
                }
                if (dt.getSubQuery() != null && dt.getSubQuery() instanceof SelectQuery) {
                    getTableNameFromDerivedTable(tableName, dt.getSubQuery());
                }
                else {
                    if (dt.getSubQuery() == null || !(dt.getSubQuery() instanceof UnionQuery)) {
                        continue;
                    }
                    final UnionQuery current = (UnionQuery)dt.getSubQuery();
                    final Query leftQuery = current.getLeftQuery();
                    final Query rightQuery = current.getRightQuery();
                    if (leftQuery != null) {
                        getTableNameFromDerivedTable(tableName, leftQuery);
                    }
                    if (rightQuery == null) {
                        continue;
                    }
                    getTableNameFromDerivedTable(tableName, rightQuery);
                }
            }
        }
        return tableName;
    }
    
    private int[] getIndices(String tableName, final String tableAlias, final List selectColumns, final Query sq) throws DataAccessException {
        WritableDataObject.OUT.entering(WritableDataObject.CLASS_NAME, "getIndices", new Object[] { tableName, tableAlias, selectColumns });
        try {
            if (!AppResources.getBoolean("ignore.derivedtable.in.selectcols.of.dob", Boolean.valueOf(true)) && sq != null) {
                tableName = getTableNameFromDerivedTable(tableName, sq);
            }
            final TableDefinition td = MetaDataUtil.getTableDefinitionByName(tableName);
            final List<String> columnNames = td.getColumnNames();
            final int[] indices = new int[columnNames.size()];
            boolean hasSelectColumn = false;
            final int[] keyIndices = td.getKeyIndices();
            final int nullPKcount = 0;
            for (int i = 0; i < indices.length; ++i) {
                final String columnName = columnNames.get(i);
                final int pos = this.findPosition(selectColumns, tableAlias, columnNames.get(i));
                if (pos != -1) {
                    hasSelectColumn = true;
                }
                else {
                    final int searchIndex = Arrays.binarySearch(keyIndices, i + 1);
                    if (searchIndex >= 0) {
                        return null;
                    }
                }
                indices[i] = pos + 1;
            }
            WritableDataObject.OUT.log(Level.FINEST, this.getString(indices));
            return (int[])(hasSelectColumn ? indices : null);
        }
        catch (final MetaDataException mde) {
            throw new DataAccessException("Exception occured while getting the table definition for [" + tableName + "]");
        }
    }
    
    private String getString(final int[] indices) {
        final StringBuffer buff = new StringBuffer("Positions:");
        for (int i = 0; i < indices.length; ++i) {
            buff.append(indices[i] + ", ");
        }
        return buff.toString();
    }
    
    private int findPosition(final List selectColumns, final String tableAlias, final String columnName) {
        for (int i = 0; i < selectColumns.size(); ++i) {
            final Column col = selectColumns.get(i);
            if (tableAlias.equals(col.getTableAlias()) && columnName.equals(col.getColumnName())) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public Row findRow(final Row row) throws DataAccessException {
        if (this.transientRowIndex == null) {
            this.indexAllRows();
        }
        final Object rowId = row.getPKValues();
        final Row retRow = this.transientRowIndex.get(rowId);
        return retRow;
    }
    
    void indexAllRows() {
        WritableDataObject.OUT.log(Level.FINEST, "Indexing All Rows!!!");
        final List rowList = this.getAllRows();
        final int size = rowList.size();
        WritableDataObject.OUT.log(Level.FINEST, "RowList{0}", rowList);
        this.transientRowIndex = new LinkedHashMap();
        for (int i = 0; i < size; ++i) {
            final Row thisRow = rowList.get(i);
            this.transientRowIndex.put(thisRow.getPKValues(), thisRow);
        }
    }
    
    boolean indexRow(final Row thisRow) {
        return this.indexRow(thisRow, true);
    }
    
    private boolean indexRow(final Row thisRow, final boolean addToIndex) {
        if (this.transientRowIndex == null) {
            this.indexAllRows();
        }
        final Object pkValues = thisRow.getPKValues();
        final boolean found = this.transientRowIndex.containsKey(pkValues);
        if (addToIndex && !found) {
            this.transientRowIndex.put(thisRow.getPKValues(), thisRow);
            thisRow.clearOldPKValues();
        }
        return !found;
    }
    
    private void delIndex(final Row delRow) {
        if (this.transientRowIndex != null) {
            final Row row = this.transientRowIndex.remove(delRow.getPKValues());
        }
    }
    
    private void reIndex(final Row modifRow) {
        Row newRow = null;
        if (modifRow.hasPKChanged()) {
            if (this.transientRowIndex != null) {
                final Object oldPK = modifRow.getOldPKValues();
                newRow = this.transientRowIndex.remove(oldPK);
                if (newRow == null) {
                    newRow = this.transientRowIndex.remove(modifRow.getPKValues());
                }
            }
            else {
                newRow = modifRow;
            }
            this.indexRow(newRow);
        }
    }
    
    void clearIndices() {
        this.transientRowIndex = null;
        this.deleteRowIndex = null;
    }
    
    boolean add(final Row row) throws DataAccessException {
        return this.add(row, false);
    }
    
    private boolean add(final Row row, final boolean cloneIt) throws DataAccessException {
        this.checkImmutable();
        return this.addBlindly(row, cloneIt);
    }
    
    boolean addBlindly(final Row newRow) {
        return this.addBlindly(newRow, false);
    }
    
    boolean addBlindly(Row newRow, final boolean cloneIt) {
        boolean isIndexed = false;
        if (this.indexRow(newRow, false)) {
            final String tableName = newRow.getTableName();
            List rowList = this.getRowsFor(tableName);
            if (rowList == null) {
                rowList = new ArrayList();
                this.tableToRowList.put(tableName, rowList);
            }
            if (cloneIt) {
                newRow = (Row)newRow.clone();
            }
            rowList.add(newRow);
            this.indexRow(newRow);
            isIndexed = true;
        }
        this.fillTableAliasToTableName(newRow);
        return isIndexed;
    }
    
    private boolean isPKNull(final Row row) {
        boolean isNull = false;
        final int[] pkColumns = row.getKeyIndices();
        for (int i = 0; i < pkColumns.length; ++i) {
            final Object value = row.get(pkColumns[i]);
            if (value == null) {
                isNull = true;
                break;
            }
        }
        return isNull;
    }
    
    private boolean update(final Row row, final Row modifRow) throws DataAccessException {
        if (row != modifRow) {
            for (int size = row.getColumns().size(), i = 1; i <= size; ++i) {
                final Object modifValue = modifRow.get(i);
                if (!this.checkEquals(row.get(i), modifValue)) {
                    row.set(i, modifValue);
                }
            }
        }
        this.reIndex(row);
        return true;
    }
    
    private boolean updateMerge(final Row row, final Row modifRow) {
        final List<Integer> changedIndcs = modifRow.getChangedColumnIndices();
        for (final Integer index : changedIndcs) {
            row.set(index, modifRow.get(index));
        }
        this.reIndex(row);
        return true;
    }
    
    boolean delete(final Row row) {
        boolean delRow = false;
        Object obj = null;
        if (row == null) {
            return false;
        }
        final String tableName = row.getTableName();
        final List rowList = this.tableToRowList.get(tableName);
        if (rowList == null) {
            return false;
        }
        for (int i = 0; i < rowList.size(); ++i) {
            obj = rowList.get(i);
            if (obj == row) {
                rowList.remove(i);
                --i;
                delRow = true;
                break;
            }
        }
        if (rowList.isEmpty()) {
            this.tableToRowList.remove(tableName);
        }
        this.delIndex(row);
        return delRow;
    }
    
    int deleteRows(final String tableAlias) throws DataAccessException {
        this.checkImmutable();
        int counter = 0;
        final List rowList = this.getRowsFor(tableAlias);
        if (rowList == null) {
            return 0;
        }
        final List clonedRowList = new ArrayList(rowList);
        final Iterator iterator = this.getRows(tableAlias);
        while (iterator.hasNext()) {
            iterator.remove();
            iterator.next();
            ++counter;
        }
        return counter;
    }
    
    private boolean checkEquals(final Object value1, final Object value2) {
        return (value1 == null) ? (value2 == null) : value1.equals(value2);
    }
    
    private void deepClone(final WritableDataObject clone) throws CloneNotSupportedException {
        final List rowList = this.getAllRows();
        final IdentityHashMap oldVsNewRow = new IdentityHashMap(rowList.size());
        final IdentityHashMap oldVsNewUVH = new IdentityHashMap();
        for (int i = 0; i < rowList.size(); ++i) {
            final Row row = rowList.get(i);
            final Row newRow = (Row)row.clone();
            for (int count = newRow.getColumns().size(), kk = 1; kk < count + 1; ++kk) {
                final Object value = newRow.get(kk);
                if (value instanceof UniqueValueHolder) {
                    final UniqueValueHolder olduvh = (UniqueValueHolder)value;
                    UniqueValueHolder newuvh = oldVsNewUVH.get(value);
                    if (newuvh == null) {
                        newuvh = (UniqueValueHolder)olduvh.clone();
                        oldVsNewUVH.put(value, newuvh);
                    }
                    newRow.setBlindly(kk, newuvh);
                }
            }
            clone.addBlindly(newRow);
            oldVsNewRow.put(row, newRow);
        }
        final List operations = this.getOperations();
        for (final ActionInfo actInfo : operations) {
            final Row oldRow = actInfo.getValue();
            Row newRow2 = oldVsNewRow.get(oldRow);
            if (actInfo.getOperation() == 3 || actInfo.getOperation() == 4) {
                newRow2 = (Row)oldRow.clone();
            }
            clone.addToOperations(actInfo.getOperation(), newRow2, actInfo.getCondition());
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        WritableDataObject dob = null;
        try {
            dob = (WritableDataObject)obj;
        }
        catch (final ClassCastException ccex) {
            WritableDataObject.OUT.log(Level.FINEST, "Exception while type casting ", obj);
            return false;
        }
        return this.checkRowEquals(this.tableToRowList, dob.tableToRowList);
    }
    
    @Override
    public int hashCode() {
        try {
            final ArrayList tableNames = (ArrayList)this.getTableNames();
            return (tableNames == null) ? 0 : tableNames.hashCode();
        }
        catch (final Exception x) {
            WritableDataObject.OUT.log(Level.FINER, "Exception while getting tablenames ", x);
            return -1;
        }
    }
    
    private boolean checkRowEquals(final Map map1, final Map map2) {
        boolean equals = true;
        if (map1 == null) {
            equals = (map2 == null);
        }
        else {
            equals = (map2 != null && map1.equals(map2));
        }
        return equals;
    }
    
    @Override
    public void validate() throws DataAccessException {
        final List operations = this.getOperations();
        for (final Object obj : operations) {
            final ActionInfo actInfo = (ActionInfo)obj;
            actInfo.getValue().validate();
        }
        this.validated = true;
    }
    
    @Override
    public Object clone() {
        WritableDataObject clone = null;
        try {
            clone = (WritableDataObject)super.clone();
            clone.clearIndices();
            clone.tableToRowList = new HashMap<String, List<Row>>(this.tableToRowList.size());
            clone.operTypeVsOperTables = new HashMap<String, OperationTables>(this.operTypeVsOperTables.size());
            this.deepClone(clone);
            clone.clearIndices();
            clone.query = this.query;
            clone.immutable = false;
        }
        catch (final CloneNotSupportedException cnse) {
            WritableDataObject.OUT.log(Level.FINEST, "Exception occured while cloning the WritableDataObject {0}", new Object[] { this });
            WritableDataObject.OUT.log(Level.FINEST, "", cnse);
        }
        return clone;
    }
    
    @Override
    public boolean isEmpty() {
        return this.tableToRowList.isEmpty();
    }
    
    @Override
    public boolean isValidated() {
        return this.validated;
    }
    
    public void cleanupOnDeleteCascadeOperations() {
        this.checkImmutable();
        final List operations = this.getOperations();
        for (int size = operations.size(), i = 0; i < size; ++i) {
            final ActionInfo info = operations.get(i);
            if (info.getCause() == 4) {
                operations.remove(info);
                --i;
                --size;
            }
        }
    }
    
    public void reverse() throws DataAccessException {
        final List operationList = this.getOperations();
        final int size = operationList.size();
        for (int i = size - 1; i >= 0; --i) {
            final ActionInfo info = operationList.get(i);
            final int operation = info.getOperation();
            final Row thisRow = info.getValue();
            switch (operation) {
                case 1: {
                    this.deleteRow(thisRow, true);
                    break;
                }
                case 2: {
                    this.reverseUpdateRow(thisRow);
                    break;
                }
                case 3: {
                    this.addRow(thisRow);
                    thisRow.deletedAt = "-1";
                    break;
                }
                default: {
                    final Exception dTrace = new Exception("Stack Trace");
                    WritableDataObject.OUT.log(Level.WARNING, "Unknown operation: {0} ", new Object[] { new Integer(operation) });
                    WritableDataObject.OUT.log(Level.WARNING, "", dTrace);
                    break;
                }
            }
        }
        this.clearOperations();
    }
    
    private void reverseUpdateRow(final Row row) throws DataAccessException {
        final List columns = row.getColumns();
        for (final String colName : columns) {
            row.set(colName, row.getOriginalValue(colName));
        }
        this.updateRow(row);
    }
    
    private List getAllRows() {
        final Iterator itr = this.tableToRowList.keySet().iterator();
        final List returnList = new ArrayList(this.tableToRowList.size());
        while (itr.hasNext()) {
            final Object key = itr.next();
            final List values = this.getRowsFor((String)key);
            returnList.addAll(values);
        }
        return returnList;
    }
    
    @Override
    public void append(final DataObject dob) throws DataAccessException {
        this.checkImmutable();
        ++this.modCount;
        WritableDataObject writableDO = null;
        try {
            writableDO = (WritableDataObject)dob;
        }
        catch (final ClassCastException ccex) {
            throw new DataAccessException("Append can be called only passing WritableDataObject, appending other implementations of DataObject are not supported");
        }
        final List rowList = writableDO.getAllRows();
        for (int rows = rowList.size(), i = 0; i < rows; ++i) {
            Row doRow = rowList.get(i);
            doRow = (Row)doRow.clone();
            this.addBlindly(doRow);
            this.addToOperations(1, doRow, null);
        }
    }
    
    private void copy(final Map destination, final Map source) throws DataAccessException {
        if (destination != null && source != null) {
            for (final Object key : source.keySet()) {
                final Object value = source.get(key);
                if (key == null && value == null) {
                    continue;
                }
                final Row row = (Row)key;
                final ActionInfo ai = (ActionInfo)value;
                if (destination.get(row) != null) {
                    throw new DataAccessException("Already a Row " + row + " exists in the destination DataObject");
                }
                destination.put(row, ai);
            }
        }
    }
    
    public void makeImmutable() {
        if (!this.immutable) {
            this.indexAllRows();
            this.markAllRowsImmutable();
            this.immutable = true;
        }
    }
    
    private void markAllRowsImmutable() {
        for (final List rows : this.tableToRowList.values()) {
            for (int i = 0; i < rows.size(); ++i) {
                final Row row = rows.get(i);
                row.makeImmutable();
            }
        }
    }
    
    private void checkImmutable() {
        if (this.immutable) {
            throw new UnsupportedOperationException("Trying to change the WritableDataObject used in the Notification. It is not allowed.");
        }
    }
    
    @Override
    public DataObject diff(final DataObject dob) throws DataAccessException {
        return this.diff(dob, false);
    }
    
    @Override
    public DataObject diff(final DataObject dob, final boolean processOnlySelectedCols) throws DataAccessException {
        WritableDataObject.OUT.log(Level.FINEST, "This DO = {0} \n Incoming DO = {1}", new Object[] { this, dob });
        final List incomingTableList = dob.getTableNames();
        final WritableDataObject retDo = new WritableDataObject();
        Row newThisRow = null;
        Row matchingRHSRow = null;
        Row newIncomingRow = null;
        Row matchingLHSRow = null;
        Row clonedIncomingRow = null;
        for (final String tableName : this.getTableNames()) {
            WritableDataObject.OUT.log(Level.FINEST, "Iterating the tableNames {0} ", tableName);
            if (!incomingTableList.contains(tableName)) {
                WritableDataObject.OUT.log(Level.FINEST, "Table {0} is not present in the incoming DO. Removing.. [", tableName + "]");
                final Iterator thisRows = this.getRows(tableName);
                while (thisRows.hasNext()) {
                    newThisRow = thisRows.next();
                    retDo.addToOperations(3, (Row)newThisRow.clone(), null);
                }
            }
        }
        for (final String tableName2 : incomingTableList) {
            WritableDataObject.OUT.log(Level.FINEST, "New Table data that is added {0}", tableName2);
            final Iterator thisRows2 = this.getRows(tableName2);
            while (thisRows2.hasNext()) {
                newThisRow = thisRows2.next();
                WritableDataObject.OUT.log(Level.FINEST, "New Row from this {0}", newThisRow);
                matchingRHSRow = dob.findRow(newThisRow);
                WritableDataObject.OUT.log(Level.FINEST, "Matching row from RHS DO {0}", matchingRHSRow);
                if (null == matchingRHSRow) {
                    retDo.addToOperations(3, (Row)newThisRow.clone(), null);
                }
                else {
                    if (newThisRow.equals(matchingRHSRow)) {
                        continue;
                    }
                    retDo.addBlindly((Row)newThisRow.clone(), true);
                    WritableDataObject.OUT.log(Level.FINEST, "Row {0} not the same as in the incoming DO Row {1}. Updating ", new Object[] { newThisRow, matchingRHSRow });
                    retDo.updateRow((Row)matchingRHSRow.clone(), false);
                }
            }
            final Iterator rows = dob.getRows(tableName2);
            while (rows.hasNext()) {
                newIncomingRow = rows.next();
                matchingLHSRow = this.findRow(newIncomingRow);
                if (null == matchingLHSRow) {
                    WritableDataObject.OUT.log(Level.FINEST, "Adding incoming DO Row {0}", newIncomingRow);
                    clonedIncomingRow = (Row)newIncomingRow.clone();
                    retDo.addBlindly(clonedIncomingRow, false);
                    retDo.addToOperations(1, clonedIncomingRow, null);
                }
            }
        }
        return retDo;
    }
    
    public List getModifiedTables() {
        Set modiTables = null;
        for (int i = 1; i < this.operName.length; ++i) {
            final OperationTables operTable = this.operTypeVsOperTables.get(this.operName[i]);
            if (operTable != null) {
                final List tablesInvolved = operTable.getTablesInvolved();
                if (modiTables == null) {
                    modiTables = new HashSet();
                }
                modiTables.addAll(tablesInvolved);
            }
        }
        return (null != modiTables) ? new ArrayList(modiTables) : null;
    }
    
    public List getModifiedTablesFor(final String operationName) {
        if (operationName == null || !this.operTypeVsOperTables.containsKey(operationName)) {
            WritableDataObject.OUT.log(Level.FINEST, "No tables found for specified operation {0}", operationName);
            return null;
        }
        final OperationTables operTable = this.operTypeVsOperTables.get(operationName);
        return operTable.getTablesInvolved();
    }
    
    public HashMap getActionsFor(final String operationName) {
        if (operationName == null || !this.operTypeVsOperTables.containsKey(operationName)) {
            WritableDataObject.OUT.log(Level.FINEST, "No actions for specified operation {0} ", operationName);
            return null;
        }
        final OperationTables operTable = this.operTypeVsOperTables.get(operationName);
        return operTable.getActions();
    }
    
    public List removeActionsFor(final String operationName) {
        if (operationName == null || !this.operTypeVsOperTables.containsKey(operationName)) {
            WritableDataObject.OUT.log(Level.FINEST, "No actions for specified operation {0} ", operationName);
            return null;
        }
        final OperationTables remOperTable = this.operTypeVsOperTables.remove(operationName);
        final HashMap tableVsActions = remOperTable.getActions();
        final List tablesInvolved = remOperTable.getTablesInvolved();
        final List allTablesActions = new ArrayList();
        try {
            final List sortedTables = PersistenceUtil.sortTables(this, tablesInvolved);
            for (int size = sortedTables.size(), i = 0; i < size; ++i) {
                allTablesActions.addAll(tableVsActions.get(sortedTables.get(i)));
            }
            if (operationName.equals("delete")) {
                this.deleteRowIndex = null;
            }
        }
        catch (final Exception x) {
            WritableDataObject.OUT.log(Level.FINEST, "Exception while removing actions ", x);
        }
        for (int j = 0; j < allTablesActions.size(); ++j) {
            allTablesActions.get(j).getValue().operationIndex = -1;
        }
        return allTablesActions;
    }
    
    public String getOrigTableName(final String tableAlias) {
        if (this.tableAliasToTableName.containsKey(tableAlias)) {
            return this.tableAliasToTableName.get(tableAlias);
        }
        return tableAlias;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        final long startTime = System.currentTimeMillis();
        out.writeInt(-99);
        int size = this.joins.size();
        out.writeInt(size);
        for (int i = 0; i < size; ++i) {
            this.joins.get(i).writeExternal(out);
        }
        out.writeInt(this.operTypeVsOperTables.size());
        OperationTables operTables = null;
        if (this.operTypeVsOperTables.size() > 0) {
            for (final Object operKey : this.operTypeVsOperTables.keySet()) {
                out.writeUTF((String)operKey);
                operTables = this.operTypeVsOperTables.get(operKey);
                operTables.writeExternal(out);
            }
        }
        final Set keySet = this.tableToRowList.keySet();
        size = keySet.size();
        out.writeInt(size);
        final Iterator keyIter = keySet.iterator();
        List rowList = null;
        String key = null;
        Row tempRow = null;
        while (keyIter.hasNext()) {
            key = keyIter.next();
            rowList = this.tableToRowList.get(key);
            out.writeUTF(key);
            size = rowList.size();
            out.writeInt(size);
            for (int j = 0; j < size; ++j) {
                tempRow = rowList.get(j);
                tempRow.writeExternal(out);
            }
        }
        WritableDataObject.OUT.log(Level.INFO, " *** Time Taken to Serialize " + (System.currentTimeMillis() - startTime));
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        final long startTime = System.currentTimeMillis();
        int size = in.readInt();
        boolean old = Boolean.TRUE;
        if (size == -99) {
            size = in.readInt();
            old = Boolean.FALSE;
        }
        this.joins = new ArrayList(size);
        Join tempJoin = null;
        for (int i = 0; i < size; ++i) {
            tempJoin = new Join();
            tempJoin.readExternal(in);
            this.joins.add(tempJoin);
        }
        if (!old) {
            final int mapSize = in.readInt();
            if (mapSize > 0) {
                this.operTypeVsOperTables = new HashMap<String, OperationTables>();
                String operKey = "";
                OperationTables operTables = null;
                for (int noOfOperations = 0; noOfOperations < mapSize; ++noOfOperations) {
                    operKey = in.readUTF();
                    operTables = new OperationTables(this.getOperation(operKey));
                    operTables.readExternal(in);
                    this.operTypeVsOperTables.put(operKey, operTables);
                }
            }
        }
        this.tableToRowList = new HashMap<String, List<Row>>();
        size = in.readInt();
        String key = null;
        int rowSize = 0;
        List rowList = null;
        Row tempRow = null;
        for (int j = 0; j < size; ++j) {
            key = in.readUTF();
            rowSize = in.readInt();
            rowList = new ArrayList();
            for (int k = 0; k < rowSize; ++k) {
                tempRow = new Row();
                tempRow.readExternal(in);
                rowList.add(tempRow);
            }
            this.tableToRowList.put(key, rowList);
        }
        if (!old) {
            try {
                this.updateActionInfo();
            }
            catch (final Exception e) {
                WritableDataObject.OUT.info("An exception occured while updating ActionInfo!!!");
                e.printStackTrace();
            }
        }
        WritableDataObject.OUT.log(Level.INFO, " *** Time Taken to DESerialize  " + (System.currentTimeMillis() - startTime));
    }
    
    private int getOperation(final String oper) {
        if (oper.equalsIgnoreCase("INSERT")) {
            return 1;
        }
        if (oper.equalsIgnoreCase("UPDATE")) {
            return 2;
        }
        if (oper.equalsIgnoreCase("DELETE")) {
            return 3;
        }
        if (oper.equalsIgnoreCase("ON_DELETE_CASCADE")) {
            return 4;
        }
        return 0;
    }
    
    private void updateActionInfo() throws DataAccessException {
        Row originalRow = null;
        final int[] indices = null;
        OperationTables operTables = null;
        for (final Object type : this.operTypeVsOperTables.keySet()) {
            operTables = this.operTypeVsOperTables.get(type);
            for (final Object operTableName : operTables.getTablesInvolved()) {
                for (final Object acInfo : operTables.getActions().get(operTableName)) {
                    if (((ActionInfo)acInfo).getOperation() != 3 && ((ActionInfo)acInfo).getOperation() != 4) {
                        originalRow = this.findRow(((ActionInfo)acInfo).getValue());
                        if (null == originalRow) {
                            continue;
                        }
                        ((ActionInfo)acInfo).setValue(originalRow);
                    }
                }
            }
        }
    }
    
    public List getAllJoins() {
        return this.joins;
    }
    
    static {
        CLASS_NAME = WritableDataObject.class.getName();
        OUT = Logger.getLogger(WritableDataObject.CLASS_NAME);
        WritableDataObject.getDataObjectCloneExtent = "all";
        if (AppResources.getString("disable.clone.childdo", "false").equals("true")) {
            WritableDataObject.getDataObjectCloneExtent = "none";
        }
        else {
            WritableDataObject.getDataObjectCloneExtent = AppResources.getString("getdataobject.clone.extent", "all");
        }
        WritableDataObject.OUT.log(Level.FINE, "clone extent of getDataObject is: " + WritableDataObject.getDataObjectCloneExtent);
        EMPTY_ITERATOR = new Iterator() {
            @Override
            public boolean hasNext() {
                return false;
            }
            
            @Override
            public Object next() {
                throw new NoSuchElementException("No element to iterate");
            }
            
            @Override
            public void remove() {
                throw new IllegalStateException("No element to remove");
            }
        };
    }
    
    class ActionInfoIterator implements Iterator<Row>
    {
        Iterator<ActionInfo> iterator;
        
        public ActionInfoIterator(final List<ActionInfo> aInfos) {
            this.iterator = null;
            this.iterator = aInfos.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        @Override
        public Row next() {
            return this.iterator.next().getValue();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("This iterator is read-only, hence remove operation cannot be performed.");
        }
    }
    
    class TableMappings
    {
        String tableName;
        String tableAlias;
        int[] primaryIndices;
        int[] columnIndices;
        int[] rowIndexes;
        
        public TableMappings(final String tableName, final String tableAlias) {
            this.tableName = null;
            this.tableAlias = null;
            this.primaryIndices = null;
            this.columnIndices = null;
            this.rowIndexes = new int[1];
            this.tableName = tableName;
            this.tableAlias = tableAlias;
        }
        
        public String getTableName() {
            return this.tableName;
        }
        
        public String getTableAlias() {
            return this.tableAlias;
        }
        
        public void setKeyIndices(final int[] primaryIndices) {
            this.primaryIndices = primaryIndices;
        }
        
        public void setColumnIndices(final int[] columnIndices) {
            this.columnIndices = columnIndices;
        }
        
        public int[] getKeyIndices() {
            return this.primaryIndices;
        }
        
        public int[] getColumnIndices() {
            return this.columnIndices;
        }
        
        public boolean putRowHashCode(final int hashCode) {
            if (this.rowIndexes.length == 1) {
                this.rowIndexes[0] = hashCode;
                return true;
            }
            Arrays.sort(this.rowIndexes);
            if (Arrays.binarySearch(this.rowIndexes, hashCode) < 0) {
                final int rowArrLen = this.rowIndexes.length;
                final int[] tempArr = new int[rowArrLen + 1];
                System.arraycopy(this.rowIndexes, 0, tempArr, 0, rowArrLen);
                tempArr[rowArrLen] = hashCode;
                this.rowIndexes = tempArr;
                return true;
            }
            return false;
        }
        
        @Override
        public String toString() {
            final StringBuffer buff = new StringBuffer();
            buff.append("TableAlias: ");
            buff.append(this.tableAlias);
            buff.append(",TableName: ");
            buff.append(this.tableName);
            buff.append(",colIndex: " + WritableDataObject.this.getString(this.columnIndices));
            buff.append(",KeyIndex: " + WritableDataObject.this.getString(this.primaryIndices));
            return buff.toString();
        }
    }
    
    class OperationTables implements Externalizable, Serializable
    {
        int operType;
        HashMap<String, List<ActionInfo>> tableVsActionInfo;
        
        public OperationTables(final int operType) {
            this.operType = -1;
            this.tableVsActionInfo = new HashMap<String, List<ActionInfo>>();
            this.operType = operType;
        }
        
        public void addActionInfo(final Row value, final Criteria condition) {
            ActionInfo info = null;
            if (this.operType == 3 && WritableDataObject.this.actualRowDeleted != null && !WritableDataObject.this.actualRowDeleted.equals(value)) {
                info = new ActionInfo(this.operType, value, condition, 4);
            }
            else {
                info = new ActionInfo(this.operType, value, condition);
            }
            this.addActionInfoToList(value, info);
        }
        
        public HashMap getActions() {
            return this.tableVsActionInfo;
        }
        
        public List getTablesInvolved() {
            final ArrayList tablesList = new ArrayList();
            final Iterator it = this.tableVsActionInfo.keySet().iterator();
            while (it.hasNext()) {
                tablesList.add(it.next());
            }
            return tablesList;
        }
        
        private void moveToUpdateOperations(final Row value) {
            final String tableName = value.getTableName();
            final OperationTables deleteOperTable = WritableDataObject.this.operTypeVsOperTables.get(WritableDataObject.this.operName[3]);
            final HashMap tableVsDelActions = deleteOperTable.getActions();
            final ArrayList deleteList = tableVsDelActions.get(tableName);
            final int deleteListSize = deleteList.size();
            int origListIndex = value.listIndex;
            if (origListIndex == -1) {
                final Row delRow = WritableDataObject.this.deleteRowIndex.get(value.getPKValues());
                origListIndex = delRow.listIndex;
            }
            int k;
            final int length = k = ((origListIndex >= deleteListSize) ? (deleteListSize - 1) : origListIndex);
            while (k >= 0) {
                final ActionInfo deletedInfo = deleteList.get(k);
                final Row deletedRow = deletedInfo.getValue();
                if (deletedRow.getPKValues().equals(value.getPKValues())) {
                    deleteList.remove(k);
                    if (deleteList.isEmpty()) {
                        tableVsDelActions.remove(tableName);
                        break;
                    }
                    break;
                }
                else {
                    --k;
                }
            }
            value.operationIndex = -1;
            value.listIndex = -1;
            WritableDataObject.this.addToOperations(2, value, null);
        }
        
        private void addActionInfoToList(final Row value, final ActionInfo info) {
            final String tableName = value.getTableName();
            ArrayList actionInfoList = this.tableVsActionInfo.get(tableName);
            if (actionInfoList == null) {
                actionInfoList = new ArrayList();
                this.tableVsActionInfo.put(tableName, actionInfoList);
            }
            switch (this.operType) {
                case 1: {
                    if (WritableDataObject.this.deleteRowIndex != null && WritableDataObject.this.deleteRowIndex.containsKey(value.getPKValues())) {
                        WritableDataObject.OUT.log(Level.FINEST, "Delete RowIndex is {0}", WritableDataObject.this.deleteRowIndex.keySet());
                        WritableDataObject.OUT.log(Level.FINEST, "Value passed is :{0}", value.getPKValues());
                        this.moveToUpdateOperations(value);
                        if (actionInfoList.isEmpty()) {
                            this.tableVsActionInfo.remove(tableName);
                        }
                        return;
                    }
                    value.operationIndex = 1;
                    value.listIndex = actionInfoList.size();
                    actionInfoList.add(info);
                    break;
                }
                case 2: {
                    if (value.operationIndex == -1) {
                        value.operationIndex = 2;
                        value.listIndex = actionInfoList.size();
                        actionInfoList.add(info);
                        break;
                    }
                    break;
                }
                case 3: {
                    if (value.operationIndex == 1 || value.operationIndex == 2) {
                        final OperationTables operTable = WritableDataObject.this.operTypeVsOperTables.get(WritableDataObject.this.operName[value.operationIndex]);
                        final ArrayList existingActList = operTable.getActions().get(value.getTableName());
                        ActionInfo deletedInfo = null;
                        WritableDataObject.OUT.log(Level.FINEST, "Existing ActionInfo Lists {0} , Passed ActionInfo {1} , Passed ActionInfo index {2}", new Object[] { existingActList, info, new Integer(value.listIndex) });
                        int l;
                        final int length = l = ((value.listIndex >= existingActList.size()) ? (existingActList.size() - 1) : value.listIndex);
                        while (l >= 0) {
                            deletedInfo = existingActList.get(l);
                            if (deletedInfo.getValue().getPKValues().equals(info.getValue().getPKValues())) {
                                existingActList.remove(l);
                                if (existingActList.isEmpty()) {
                                    operTable.getActions().remove(value.getTableName());
                                }
                                if (deletedInfo.getOperation() == 2) {
                                    value.operationIndex = 3;
                                    value.listIndex = actionInfoList.size();
                                    if (WritableDataObject.this.deleteRowIndex == null) {
                                        WritableDataObject.this.deleteRowIndex = new HashMap();
                                    }
                                    WritableDataObject.this.deleteRowIndex.put(value.getPKValues(), value);
                                    actionInfoList.add(info);
                                    break;
                                }
                                value.operationIndex = -1;
                                value.listIndex = -1;
                                break;
                            }
                            else {
                                --l;
                            }
                        }
                        break;
                    }
                    value.operationIndex = 3;
                    value.listIndex = actionInfoList.size();
                    if (WritableDataObject.this.deleteRowIndex == null) {
                        WritableDataObject.this.deleteRowIndex = new HashMap();
                    }
                    WritableDataObject.this.deleteRowIndex.put(value.getPKValues(), value);
                    actionInfoList.add(info);
                    break;
                }
            }
            if (actionInfoList.isEmpty()) {
                this.tableVsActionInfo.remove(tableName);
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            sb.append("<TableActions>");
            sb.append("<Operation type=");
            sb.append(WritableDataObject.this.operName[this.operType]);
            sb.append("/>");
            final Iterator tablesItr = this.getTablesInvolved().iterator();
            while (tablesItr.hasNext()) {
                sb.append("<Tables>");
                sb.append(tablesItr.next());
                sb.append("</Tables>");
            }
            sb.append("<TableVsActionInfos>");
            sb.append(this.getActions());
            sb.append("</TableVsActionInfos>");
            sb.append("</TableActionos>");
            return sb.toString();
        }
        
        @Override
        public void writeExternal(final ObjectOutput output) throws IOException {
            Row row = null;
            Criteria condition = null;
            output.writeInt(this.getTablesInvolved().size());
            for (final Object operTableName : this.getTablesInvolved()) {
                output.writeUTF((String)operTableName);
                output.writeInt(this.getActions().get(operTableName).size());
                final List actionInfos = this.getActions().get(operTableName);
                for (final Object acInfo : actionInfos) {
                    output.writeInt(((ActionInfo)acInfo).getOperation());
                    row = ((ActionInfo)acInfo).getValue();
                    if (((ActionInfo)acInfo).getOperation() == 3 || ((ActionInfo)acInfo).getOperation() == 4) {
                        row.writeExternal(output);
                    }
                    else {
                        output.writeInt(row.getKeyIndices().length);
                        for (int pK = 0; pK < row.getKeyIndices().length; ++pK) {
                            output.writeObject(row.get(row.getKeyIndices()[pK]));
                        }
                    }
                    output.writeInt(((ActionInfo)acInfo).getCause());
                    condition = ((ActionInfo)acInfo).getCondition();
                    if (null != condition) {
                        output.writeBoolean(true);
                        condition.writeExternal(output);
                    }
                    else {
                        output.writeBoolean(false);
                    }
                }
            }
        }
        
        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            this.tableVsActionInfo = new HashMap<String, List<ActionInfo>>();
            String operTableName = "";
            int acInfoSize = -1;
            List actionInfos = null;
            int operation = -1;
            Row value = null;
            int cause = -1;
            ActionInfo acInfo = null;
            Criteria condition = null;
            boolean hasCondition = Boolean.FALSE;
            int pks = 0;
            Object temp = null;
            for (int operSize = in.readInt(), noOfTables = 0; noOfTables < operSize; ++noOfTables) {
                operTableName = in.readUTF();
                acInfoSize = in.readInt();
                actionInfos = new ArrayList(acInfoSize);
                for (int actions = 0; actions < acInfoSize; ++actions) {
                    operation = in.readInt();
                    value = new Row(operTableName);
                    if (operation == 3 || operation == 4) {
                        value.readExternal(in);
                    }
                    else {
                        pks = in.readInt();
                        for (int pkIndex = 0; pkIndex < pks; ++pkIndex) {
                            temp = in.readObject();
                            value.set(value.getKeyIndices()[pkIndex], temp);
                        }
                    }
                    cause = in.readInt();
                    condition = null;
                    hasCondition = in.readBoolean();
                    if (hasCondition) {
                        condition = new Criteria();
                        condition.readExternal(in);
                    }
                    acInfo = new ActionInfo(operation, value, condition, cause);
                    actionInfos.add(acInfo);
                }
                this.tableVsActionInfo.put(operTableName, actionInfos);
            }
        }
    }
}
