package com.adventnet.ds.query;

import java.sql.Clob;
import java.sql.Blob;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import com.adventnet.db.adapter.DTResultSetAdapter;
import com.adventnet.db.adapter.DTTransformationUtil;
import com.zoho.mickey.api.DataTypeUtil;
import java.util.Comparator;
import java.util.Collections;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.util.ArrayList;
import java.sql.SQLException;
import java.text.Collator;
import java.util.Vector;
import java.util.Hashtable;
import java.util.List;
import java.sql.Statement;
import com.adventnet.db.adapter.ResultSetAdapter;
import java.util.logging.Logger;

public class DataSet implements AutoCloseable
{
    private static final Logger OUT;
    protected boolean closed;
    private ResultSetAdapter rs;
    private String[] columnNames;
    private int[] columnTypes;
    private String[] columnDataTypes;
    private int columnCount;
    private Query query;
    private Statement stmt;
    private List selectColumns;
    private List sortColumns;
    private Hashtable orderHash;
    int[] order;
    private Vector orderVect;
    private boolean firstTime;
    int startIndex;
    int noOfObjects;
    static Collator coll;
    private Object current;
    private boolean isI18nBase;
    private boolean isI18nPresent;
    private Vector currentVect;
    private int currentIndex;
    private RowComparator rowComp;
    private Object nextCurrent;
    private int rowsProcessed;
    private int checkStart;
    
    public DataSet(final ResultSetAdapter rs, final Query query, final List selectColumns, final Statement stmt) throws SQLException {
        this(rs, selectColumns);
        this.query = query;
        this.stmt = stmt;
        this.checkPresenceOfI18nColumn();
        final Range range = query.getRange();
        if (range != null) {
            this.startIndex = range.getStartIndex();
            this.noOfObjects = range.getNumberOfObjects();
        }
        this.rowComp = new RowComparator(this.sortColumns, this.orderVect, this.order);
    }
    
    private void checkPresenceOfI18nColumn() {
        if (this.query instanceof SelectQuery) {
            final SelectQuery selectQuery = (SelectQuery)this.query;
            this.sortColumns = selectQuery.getSortColumns();
            if (this.sortColumns != null && this.sortColumns.size() != 0) {
                this.order = new int[this.sortColumns.size()];
                this.getSortOrder();
                if (this.orderVect.size() > 0 && this.orderVect.get(0) != null) {
                    this.isI18nBase = true;
                }
            }
        }
    }
    
    private List getUnionSelectColumns() {
        final List list = new ArrayList();
        return list;
    }
    
    private void getSortOrder() {
        final int selSize = this.selectColumns.size();
        for (int sortSize = this.sortColumns.size(), i = 0; i < sortSize; ++i) {
            final SortColumn sortCol = this.sortColumns.get(i);
            if (sortCol != SortColumn.NULL_COLUMN) {
                final Column colInSort = sortCol.getColumn();
                int j = 0;
                while (j < selSize) {
                    final Column col = this.selectColumns.get(j);
                    if (colInSort.equals(col)) {
                        this.order[i] = j + 1;
                        if (sortCol.getSortOrder() == null) {
                            this.orderVect.add(null);
                            break;
                        }
                        this.orderVect.add(sortCol.getSortOrder());
                        this.isI18nPresent = true;
                        break;
                    }
                    else {
                        ++j;
                    }
                }
            }
        }
    }
    
    public DataSet(final ResultSetAdapter rs, final List selectColumns) throws SQLException {
        this.closed = false;
        this.rs = null;
        this.columnNames = null;
        this.columnTypes = null;
        this.columnDataTypes = null;
        this.columnCount = -1;
        this.query = null;
        this.stmt = null;
        this.selectColumns = null;
        this.sortColumns = null;
        this.orderHash = new Hashtable();
        this.order = null;
        this.orderVect = new Vector();
        this.firstTime = true;
        this.startIndex = -1;
        this.noOfObjects = 0;
        this.current = null;
        this.isI18nBase = false;
        this.isI18nPresent = false;
        this.currentVect = null;
        this.currentIndex = 0;
        this.rowComp = null;
        this.nextCurrent = null;
        this.rowsProcessed = 0;
        this.checkStart = 0;
        this.rs = rs;
        this.selectColumns = selectColumns;
        if (rs != null) {
            this.init();
        }
    }
    
    public DataSet(final ResultSetAdapter rs, final Statement stmt) throws SQLException {
        this.closed = false;
        this.rs = null;
        this.columnNames = null;
        this.columnTypes = null;
        this.columnDataTypes = null;
        this.columnCount = -1;
        this.query = null;
        this.stmt = null;
        this.selectColumns = null;
        this.sortColumns = null;
        this.orderHash = new Hashtable();
        this.order = null;
        this.orderVect = new Vector();
        this.firstTime = true;
        this.startIndex = -1;
        this.noOfObjects = 0;
        this.current = null;
        this.isI18nBase = false;
        this.isI18nPresent = false;
        this.currentVect = null;
        this.currentIndex = 0;
        this.rowComp = null;
        this.nextCurrent = null;
        this.rowsProcessed = 0;
        this.checkStart = 0;
        this.rs = rs;
        this.stmt = stmt;
        if (rs != null) {
            this.columnCount = rs.getColumnCount();
            this.columnNames = new String[this.columnCount];
            this.columnTypes = new int[this.columnCount];
            this.columnDataTypes = new String[this.columnCount];
            for (int i = 0; i < this.columnCount; ++i) {
                this.columnNames[i] = rs.getColumnLabel(i + 1);
                this.columnTypes[i] = rs.getColumnType(i + 1);
                if (this.query != null) {
                    this.columnDataTypes[i] = this.query.getSelectColumns().get(i + 1).getDataType();
                }
            }
        }
    }
    
    private void init() throws SQLException {
        this.fillColumnInfo();
    }
    
    protected void checkClosed() throws SQLException {
        if (this.closed) {
            throw new SQLException("DataSet is already closed");
        }
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public int getColumnCount() throws SQLException {
        this.checkClosed();
        return this.columnCount;
    }
    
    public String getColumnName(final int columnIndex) throws SQLException {
        this.checkClosed();
        if (columnIndex < 1 || columnIndex > this.columnNames.length) {
            throw new SQLException("Invalid column index");
        }
        return this.columnNames[columnIndex - 1];
    }
    
    public void fillColumnInfo() throws SQLException {
        this.columnCount = this.selectColumns.size();
        this.columnNames = new String[this.columnCount];
        this.columnTypes = new int[this.columnCount];
        this.columnDataTypes = new String[this.columnCount];
        for (int i = 0; i < this.columnCount; ++i) {
            final Column column = this.selectColumns.get(i);
            this.columnNames[i] = column.getColumnAlias();
            this.columnTypes[i] = column.getType();
            this.columnDataTypes[i] = column.getDataType();
            if (column.getDataType() == null && DataTypeManager.getDataTypeDefinition(column.getType()) != null) {
                this.columnDataTypes[i] = DataTypeManager.getDataTypeDefinition(column.getType()).getDataType();
            }
        }
    }
    
    public int getColumnType(final int columnIndex) throws SQLException {
        this.checkClosed();
        if (columnIndex < 1 || columnIndex > this.columnNames.length) {
            throw new SQLException("Invalid column index");
        }
        return this.columnTypes[columnIndex - 1];
    }
    
    public String getColumnDataType(final int columnIndex) throws SQLException {
        this.checkClosed();
        if (columnIndex < 1 || columnIndex > this.columnNames.length) {
            throw new SQLException("Invalid column index");
        }
        return this.columnDataTypes[columnIndex - 1];
    }
    
    public Query getSelectQuery() {
        return this.query;
    }
    
    public int findColumn(final String columnName) throws SQLException {
        int index = this.indexOf(columnName);
        if (index != -1) {
            return index;
        }
        if ((index = this.indexOf(columnName, true)) != -1) {
            return index;
        }
        throw new SQLException("Column Not Found: " + columnName);
    }
    
    public int indexOf(final String columnName) throws SQLException {
        return this.indexOf(columnName, false);
    }
    
    private int indexOf(final String columnName, final boolean ignoreCase) throws SQLException {
        this.checkClosed();
        if (this.columnNames == null || this.columnNames.length == 0) {
            DataSet.OUT.log(Level.FINER, "ColumnNames list is Empty.This probably means we have a function column, which does not have a associated name.\n Try using the columnIndex");
            throw new SQLException("Column Not Found: " + columnName);
        }
        for (int i = 0; i < this.columnNames.length; ++i) {
            if ((ignoreCase && this.columnNames[i].equalsIgnoreCase(columnName)) || this.columnNames[i].equals(columnName)) {
                return i + 1;
            }
        }
        return -1;
    }
    
    public boolean next() throws SQLException {
        this.checkClosed();
        if (this.noOfObjects != 0 && this.rowsProcessed == this.noOfObjects) {
            return false;
        }
        if (!this.isI18nPresent) {
            if (!this.rs.isRangeHandled()) {
                while (this.checkStart < this.startIndex - 1) {
                    if (!this.rs.next()) {
                        return false;
                    }
                    ++this.checkStart;
                }
            }
            ++this.rowsProcessed;
            final boolean result = this.rs.next();
            return result;
        }
        if (this.firstTime) {
            if (this.isI18nBase) {
                final boolean next = this.doI18nProcessing(this.order[0], this.orderVect.get(0));
                this.firstTime = false;
                if (next) {
                    if (this.currentVect == null) {
                        this.setCorrectCurrent();
                        final Vector temp = this.orderHash.get(this.current);
                        this.currentVect = temp.remove(0);
                    }
                    ++this.rowsProcessed;
                }
                return next;
            }
            final boolean next = this.doNormalProcessing(this.order[0]);
            this.firstTime = false;
            ++this.rowsProcessed;
            return next;
        }
        else {
            this.currentVect = null;
            if (this.isI18nBase) {
                if (this.current == null && this.isI18nBase) {
                    this.setCorrectCurrent();
                }
                final List tree = this.orderHash.get(this.current);
                if (tree == null) {
                    this.processResultSet();
                    if (this.orderHash.get(this.current) == null) {
                        final boolean set = this.setCorrectCurrent();
                        if (!set) {
                            return false;
                        }
                    }
                    if (this.orderHash.get(this.current) != null) {
                        Collections.sort(this.orderHash.get(this.current), this.rowComp);
                        final Vector temp = this.orderHash.get(this.current);
                        this.currentVect = temp.remove(0);
                        ++this.rowsProcessed;
                        return true;
                    }
                }
                else {
                    if (tree.size() != 0) {
                        Collections.sort((List<Object>)tree, this.rowComp);
                        this.currentVect = tree.remove(0);
                        ++this.rowsProcessed;
                        return true;
                    }
                    this.setCurrent();
                    this.processResultSet();
                    if (this.currentVect != null) {
                        return true;
                    }
                    if (this.orderHash.get(this.current) == null) {
                        final boolean set = this.setCorrectCurrent();
                        if (!set) {
                            return false;
                        }
                    }
                    if (this.orderHash.get(this.current) != null) {
                        final List tempSet = this.orderHash.get(this.current);
                        if (tempSet.size() == 0) {
                            return false;
                        }
                        Collections.sort((List<Object>)tempSet, this.rowComp);
                        this.currentVect = tempSet.remove(0);
                        ++this.rowsProcessed;
                        return true;
                    }
                }
                return false;
            }
            final Vector tempVect = this.orderHash.get(this.current);
            if (tempVect == null || tempVect.size() == 0) {
                ++this.rowsProcessed;
                return this.processCurrentSet(this.nextCurrent);
            }
            if (tempVect.size() != 0) {
                this.currentVect = tempVect.remove(0);
                ++this.rowsProcessed;
                return true;
            }
            ++this.rowsProcessed;
            return this.processCurrentSet(this.nextCurrent);
        }
    }
    
    private boolean processCurrentSet(final Object currVal) throws SQLException {
        List list = null;
        boolean next = false;
        if (currVal != null) {
            this.current = currVal;
            this.nextCurrent = null;
            next = true;
            if ((list = this.orderHash.get(this.current)) == null) {
                list = new Vector();
                this.orderHash.put(this.current, list);
            }
        }
        else if (this.rs.next()) {
            next = true;
            this.setCurrent();
            list = this.orderHash.get(this.current);
            if (list == null) {
                list = new Vector();
                this.orderHash.put(this.current, list);
            }
            this.processSingleRow(list);
        }
        while (this.rs.next()) {
            next = true;
            if (!this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true).equals(this.current)) {
                final List tempList = new Vector();
                this.nextCurrent = this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true);
                this.orderHash.put(this.nextCurrent, tempList);
                this.processSingleRow(tempList);
                Collections.sort((List<Object>)list, this.rowComp);
                this.currentVect = list.remove(0);
                return true;
            }
            this.processSingleRow(list);
        }
        if (list != null && list.size() > 0) {
            Collections.sort((List<Object>)list, this.rowComp);
            this.currentVect = list.remove(0);
        }
        return next;
    }
    
    private boolean setCorrectCurrent() throws SQLException {
        try {
            while (this.current == null) {
                this.setCurrent();
            }
            while (this.orderHash.get(this.current) == null || this.orderHash.get(this.current).size() == 0) {
                final boolean set = this.setCurrent();
                if (!set) {
                    return false;
                }
            }
        }
        catch (final Exception excp) {
            DataSet.OUT.fine("DEBUG*inside excp of setCorrectCurrent**********");
            return false;
        }
        return true;
    }
    
    private void processResultSet() throws SQLException {
        if (this.rs.next()) {
            if (this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true).equals(this.current)) {
                this.processCurrent();
            }
            else {
                this.processOthers(this.current);
            }
        }
    }
    
    private void processCurrent() throws SQLException {
        List set = this.orderHash.get(this.current);
        if (set == null) {
            set = new Vector();
            this.orderHash.put(this.current, set);
        }
        this.processSingleRow(set);
        while (this.rs.next()) {
            if (!this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true).equals(this.current)) {
                final List tempSet = this.orderHash.get(this.current);
                Collections.sort((List<Object>)tempSet, this.rowComp);
                this.currentVect = tempSet.remove(0);
                this.addToHash(this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true));
                return;
            }
            this.processSingleRow(set);
        }
    }
    
    private void processOthers(final Object value) throws SQLException {
        List set = this.orderHash.get(this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true));
        if (set == null) {
            set = new Vector();
            this.orderHash.put(this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true), set);
        }
        this.processSingleRow(set);
        while (this.rs.next()) {
            if (this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true).equals(value)) {
                this.processCurrent();
                return;
            }
            List list = this.orderHash.get(this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true));
            if (list == null) {
                list = new Vector();
                this.orderHash.put(this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true), list);
            }
            this.processSingleRow(list);
        }
    }
    
    private boolean setCurrent() throws SQLException {
        if (this.isI18nBase) {
            final Vector vect = this.orderVect.get(0);
            if (this.currentIndex + 1 >= vect.size()) {
                return false;
            }
            this.current = this.orderVect.get(0).get(++this.currentIndex);
        }
        else {
            this.current = this.getValue(this.order[0], this.columnTypes[this.order[0] - 1], true);
        }
        return true;
    }
    
    private boolean doI18nProcessing(final int index, final Vector sortOrder) throws SQLException {
        final List tempList = new Vector();
        Object curr = sortOrder.get(this.currentIndex);
        int currSize = 0;
        boolean next = false;
        while (this.rs.next()) {
            next = true;
            if (curr.equals(this.getValue(index, this.columnTypes[this.order[0] - 1], true))) {
                this.processSingleRow(tempList);
            }
            else {
                if (tempList.size() > 0) {
                    if (currSize + tempList.size() >= this.startIndex) {
                        final int elemToBeRemoved = this.startIndex - currSize - 1;
                        Collections.sort((List<Object>)tempList, this.rowComp);
                        if (elemToBeRemoved < tempList.size()) {
                            this.removeElements(tempList, elemToBeRemoved);
                        }
                        this.orderHash.put(curr, tempList);
                        this.current = curr;
                        this.currentVect = tempList.remove(0);
                        this.addToHash(this.getValue(index, this.columnTypes[this.order[0] - 1], true));
                        return true;
                    }
                    currSize += tempList.size();
                    tempList.clear();
                    curr = sortOrder.get(++this.currentIndex);
                    this.current = curr;
                }
                else if (this.current != null) {
                    final List alreadyProcessed = this.orderHash.get(this.current);
                    if (alreadyProcessed == null || alreadyProcessed.size() <= 0) {
                        this.addToHash(this.getValue(index, this.columnTypes[this.order[0] - 1], true));
                        continue;
                    }
                    if (currSize + alreadyProcessed.size() < this.startIndex - 1) {
                        currSize += alreadyProcessed.size();
                        alreadyProcessed.clear();
                        curr = sortOrder.get(++this.currentIndex);
                        this.current = curr;
                    }
                    else {
                        final int elemToBeRemoved2 = this.startIndex - currSize - 1;
                        Collections.sort((List<Object>)alreadyProcessed, this.rowComp);
                        if (elemToBeRemoved2 < alreadyProcessed.size()) {
                            this.removeElements(alreadyProcessed, elemToBeRemoved2);
                        }
                        if (alreadyProcessed.size() != 0) {
                            this.currentVect = alreadyProcessed.remove(0);
                            this.addToHash(this.getValue(index, this.columnTypes[this.order[0] - 1], true));
                            return true;
                        }
                    }
                }
                this.addToHash(this.getValue(index, this.columnTypes[this.order[0] - 1], true));
            }
        }
        if (currSize < this.startIndex) {
            if (tempList.size() > 0) {
                if (currSize + tempList.size() >= this.startIndex) {
                    final int elemToBeRemoved = this.startIndex - currSize - 1;
                    if (elemToBeRemoved < tempList.size()) {
                        this.removeElements(tempList, elemToBeRemoved);
                    }
                    this.orderHash.put(curr, tempList);
                    this.current = curr;
                    Collections.sort((List<Object>)tempList, this.rowComp);
                    this.currentVect = tempList.remove(0);
                    return true;
                }
                currSize += tempList.size();
                tempList.clear();
                curr = sortOrder.get(++this.currentIndex);
            }
            this.setCorrectCurrent();
            while (currSize < this.startIndex - 1) {
                final List removableList = this.orderHash.get(this.current);
                if (removableList == null || removableList.size() <= 0) {
                    return false;
                }
                if (removableList.size() + currSize >= this.startIndex) {
                    final int elemToBeRemoved2 = this.startIndex - currSize - 1;
                    if (elemToBeRemoved2 < removableList.size()) {
                        this.removeElements(removableList, elemToBeRemoved2);
                    }
                    this.orderHash.put(curr, tempList);
                    this.current = curr;
                    Collections.sort((List<Object>)removableList, this.rowComp);
                    this.currentVect = removableList.remove(0);
                    return true;
                }
                removableList.clear();
                this.setCorrectCurrent();
            }
        }
        return next;
    }
    
    private boolean doNormalProcessing(final int index) throws SQLException {
        Object curr = null;
        final List list = new Vector();
        int currSize = 0;
        boolean next = false;
        while (this.rs.next()) {
            next = true;
            if (curr == null) {
                curr = this.getValue(index, this.columnTypes[this.order[0] - 1], true);
            }
            if (curr.equals(this.getValue(index, this.columnTypes[this.order[0] - 1], true))) {
                this.processSingleRow(list);
            }
            else {
                if (currSize + list.size() >= this.startIndex) {
                    final List tempList = new Vector();
                    this.nextCurrent = this.getValue(index, this.columnTypes[this.order[0] - 1], true);
                    this.orderHash.put(this.nextCurrent, tempList);
                    this.processSingleRow(tempList);
                    final int elemToBeRemoved = this.startIndex - currSize - 1;
                    Collections.sort((List<Object>)list, this.rowComp);
                    if (elemToBeRemoved < list.size()) {
                        this.removeElements(list, elemToBeRemoved);
                    }
                    this.orderHash.put(curr, list);
                    this.current = curr;
                    this.currentVect = list.remove(0);
                    return next;
                }
                currSize += list.size();
                list.clear();
                curr = this.getValue(index, this.columnTypes[this.order[0] - 1], true);
                this.processSingleRow(list);
            }
        }
        if (next) {
            this.current = curr;
            this.orderHash.put(this.current, list);
            Collections.sort((List<Object>)list, this.rowComp);
            this.currentVect = list.remove(0);
        }
        return next;
    }
    
    private void removeElements(final List list, final int noOfElem) {
        for (int i = 0; i < noOfElem; ++i) {
            list.remove(i);
        }
    }
    
    private void addToHash(final Object value) throws SQLException {
        List list = this.orderHash.get(value);
        if (list == null) {
            list = new Vector();
            this.orderHash.put(value, list);
        }
        this.processSingleRow(list);
    }
    
    private void processSingleRow(final List list) throws SQLException {
        final Vector vect = new Vector();
        for (int selColSize = this.selectColumns.size(), i = 0; i < selColSize; ++i) {
            final Object obj = this.getValue(i + 1, this.columnTypes[i], true);
            vect.add(obj);
        }
        list.add(vect);
    }
    
    public int getRow() throws SQLException {
        this.checkClosed();
        return this.rs.getRow();
    }
    
    public boolean relative(final int rows) throws SQLException {
        this.checkClosed();
        return this.rs.relative(rows);
    }
    
    @Override
    public void close() throws SQLException {
        if (this.closed) {
            return;
        }
        if (this.rs != null) {
            this.rs.close();
        }
        if (this.stmt != null) {
            this.stmt.close();
        }
        this.closed = true;
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("<DATASET").append("\n   closed=").append(this.closed);
        if (this.query != null) {
            buff.append("\n  <SELECT-QUERY ").append(this.query);
            buff.append(">");
            buff.append("\n  </SELECT-QUERY>");
        }
        else {
            buff.append("\n  <SELECT-ATTRIBUTES ").append(this.selectColumns);
            buff.append(">");
            buff.append("\n  </SELECT-ATTRIBUTES>");
        }
        buff.append("\n   >");
        buff.append("</DATASET>");
        return buff.toString();
    }
    
    public boolean wasNull() throws SQLException {
        this.checkClosed();
        return this.rs.wasNull();
    }
    
    public Object getValue(final int columnIndex) throws SQLException {
        this.checkClosed();
        return this.getValue(columnIndex, this.columnTypes[columnIndex - 1]);
    }
    
    public Object getValue(final String columnName) throws SQLException {
        final int columnIndex = this.findColumn(columnName);
        return this.getValue(columnIndex);
    }
    
    public Object getValue(final int columnIndex, final int type) throws SQLException {
        if (this.currentVect != null) {
            return this.currentVect.get(columnIndex - 1);
        }
        return this.getValue(columnIndex, type, false);
    }
    
    public Object getValue(final String columnAlias, final int type) throws SQLException {
        if (this.currentVect != null) {
            return this.currentVect.get(this.findColumn(columnAlias) - 1);
        }
        return this.getValue(this.findColumn(columnAlias), type, false);
    }
    
    private Object getValue(final int columnIndex, final int type, final boolean i18n) throws SQLException {
        Object value = null;
        switch (type) {
            case -16:
            case -9:
            case -3:
            case -1:
            case 1:
            case 12: {
                value = this.rs.getString(columnIndex);
                break;
            }
            case -6:
            case 4:
            case 5: {
                final int intVal = this.rs.getInt(columnIndex);
                value = new Integer(intVal);
                break;
            }
            case 6:
            case 7: {
                final float floatVal = this.rs.getFloat(columnIndex);
                value = new Float(floatVal);
                break;
            }
            case 2:
            case 8: {
                final double doubleVal = this.rs.getDouble(columnIndex);
                value = new Double(doubleVal);
                break;
            }
            case 3: {
                value = this.rs.getBigDecimal(columnIndex);
                break;
            }
            case 91: {
                value = this.rs.getDate(columnIndex);
                break;
            }
            case 92: {
                value = this.rs.getTime(columnIndex);
                break;
            }
            case 93: {
                value = this.rs.getTimestamp(columnIndex);
                break;
            }
            case -5: {
                final long longVal = this.rs.getLong(columnIndex);
                value = new Long(longVal);
                break;
            }
            case -7:
            case 16: {
                value = this.rs.getBoolean(columnIndex);
                break;
            }
            case -4:
            case -2:
            case 2004: {
                value = this.rs.getBlobAsInputStream(columnIndex);
                break;
            }
            case 2005: {
                value = this.rs.getClob(columnIndex);
                break;
            }
            default: {
                final String dataType = this.columnDataTypes[columnIndex - 1];
                final String database = this.rs.getDBType();
                final DTResultSetAdapter rsResultSetAdapter = this.rs.getDTResultSetAdapter(dataType);
                if (DataTypeUtil.isUDT(dataType)) {
                    if (rsResultSetAdapter != null) {
                        Column selectCol = null;
                        if (this.query instanceof SelectQuery) {
                            selectCol = ((SelectQuery)this.query).getSelectColumns().get(columnIndex - 1);
                        }
                        else if (this.query instanceof UnionQuery) {
                            selectCol = ((UnionQuery)this.query).getSelectColumns().get(columnIndex - 1);
                        }
                        value = this.rs.getDTResultSetAdapter(dataType).getValue(this.rs, columnIndex, dataType);
                        String tableName = null;
                        if (selectCol.getDefinition() != null) {
                            tableName = selectCol.getDefinition().getTableName();
                        }
                        else {
                            DataSet.OUT.log(Level.WARNING, "Column Definition is null for column: " + selectCol.getColumnName() + ". So passing tablename as null for transformation.");
                        }
                        try {
                            value = DTTransformationUtil.unTransform(tableName, this.getColumnName(columnIndex), value, dataType, database);
                            break;
                        }
                        catch (final Exception e) {
                            throw new SQLException("Exception during transforming data." + e);
                        }
                    }
                    throw new IllegalArgumentException("DTResultSetAdapter is not defined for the type :: " + DataTypeManager.getDataTypeDefinition(dataType).getDataType());
                }
                throw new SQLException("Unknown type [" + dataType + " - " + type + "] received for column [" + this.rs.getColumnName(columnIndex) + "] columnIndex [index " + columnIndex + "]");
            }
        }
        if (this.rs.wasNull()) {
            value = null;
        }
        return value;
    }
    
    @Deprecated
    public String getString(final int columnIndex) throws SQLException {
        final Object val = this.getValue(columnIndex);
        if (val == null) {
            return null;
        }
        return String.valueOf(val);
    }
    
    @Deprecated
    public long getLong(final int columnIndex) throws SQLException {
        final Number value = (Number)this.getValue(columnIndex);
        if (value == null) {
            return 0L;
        }
        return value.longValue();
    }
    
    @Deprecated
    public float getFloat(final int columnIndex) throws SQLException {
        final Number value = (Number)this.getValue(columnIndex);
        if (value == null) {
            return 0.0f;
        }
        return value.floatValue();
    }
    
    @Deprecated
    public double getDouble(final int columnIndex) throws SQLException {
        final Number value = (Number)this.getValue(columnIndex);
        if (value == null) {
            return 0.0;
        }
        return value.doubleValue();
    }
    
    public Date getDate(final int columnIndex) throws SQLException {
        final Date value = (Date)this.getValue(columnIndex);
        if (value == null) {
            return null;
        }
        return value;
    }
    
    public String getAsString(final int columnIndex) throws SQLException {
        if (this.query != null) {
            final String dataType = this.columnDataTypes[columnIndex - 1];
            final int type = this.columnTypes[columnIndex - 1];
            if (null != dataType) {
                if (!DataTypeUtil.isUDT(dataType)) {
                    return this.rs.getString(columnIndex);
                }
            }
            else if (!DataTypeUtil.isUDT(type)) {
                return this.rs.getString(columnIndex);
            }
            final Object value = this.getValue(columnIndex);
            if (value == null) {
                return null;
            }
            return String.valueOf(value);
        }
        return this.rs.getString(columnIndex);
    }
    
    public Boolean getAsBoolean(final int columnIndex) throws SQLException {
        return this.rs.getBoolean(columnIndex);
    }
    
    public Date getAsDate(final int columnIndex) throws SQLException {
        return this.rs.getDate(columnIndex);
    }
    
    public Time getAsTime(final int columnIndex) throws SQLException {
        return this.rs.getTime(columnIndex);
    }
    
    public Timestamp getAsTimestamp(final int columnIndex) throws SQLException {
        return this.rs.getTimestamp(columnIndex);
    }
    
    public Long getAsLong(final int columnIndex) throws SQLException {
        return this.rs.getLong(columnIndex);
    }
    
    public BigDecimal getAsDecimal(final int columnIndex) throws SQLException {
        return this.rs.getBigDecimal(columnIndex);
    }
    
    public InputStream getAsBlob(final int columnIndex) throws SQLException {
        return this.rs.getBlobAsInputStream(columnIndex);
    }
    
    public String getAsString(final String columnAlias) throws SQLException {
        final int columnIndex = this.findColumn(columnAlias);
        if (this.query != null && columnIndex != -1) {
            final String dataType = this.columnDataTypes[columnIndex - 1];
            final int type = this.columnTypes[columnIndex - 1];
            if (null != dataType) {
                if (!DataTypeUtil.isUDT(dataType)) {
                    return this.rs.getString(columnAlias);
                }
            }
            else if (!DataTypeUtil.isUDT(type)) {
                return this.rs.getString(columnAlias);
            }
            final Object value = this.getValue(columnIndex);
            if (value == null) {
                return null;
            }
            return String.valueOf(value);
        }
        return this.rs.getString(columnAlias);
    }
    
    public Boolean getAsBoolean(final String columnAlias) throws SQLException {
        return this.rs.getBoolean(columnAlias);
    }
    
    public Date getAsDate(final String columnAlias) throws SQLException {
        return this.rs.getDate(columnAlias);
    }
    
    public Time getAsTime(final String columnAlias) throws SQLException {
        return this.rs.getTime(columnAlias);
    }
    
    public Timestamp getAsTimestamp(final String columnAlias) throws SQLException {
        return this.rs.getTimestamp(columnAlias);
    }
    
    public Long getAsLong(final String columnAlias) throws SQLException {
        return this.rs.getLong(columnAlias);
    }
    
    public BigDecimal getAsDecimal(final String columnAlias) throws SQLException {
        return this.rs.getBigDecimal(columnAlias);
    }
    
    public InputStream getAsBlob(final String columnAlias) throws SQLException {
        return this.rs.getBlobAsInputStream(columnAlias);
    }
    
    @Deprecated
    public Blob getBlob(final int columnIndex) throws SQLException {
        return (Blob)this.getValue(columnIndex);
    }
    
    @Deprecated
    public Clob getClob(final int columnIndex) throws SQLException {
        return (Clob)this.getValue(columnIndex);
    }
    
    @Deprecated
    public int getInt(final int columnIndex) throws SQLException {
        final Number value = (Number)this.getValue(columnIndex);
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }
    
    @Deprecated
    public String getString(final String columnName) throws SQLException {
        final int columnIndex = this.findColumn(columnName);
        return this.getString(columnIndex);
    }
    
    @Deprecated
    public Blob getBlob(final String columnName) throws SQLException {
        return (Blob)this.getValue(columnName);
    }
    
    @Deprecated
    public Clob getClob(final String columnName) throws SQLException {
        return (Clob)this.getValue(columnName);
    }
    
    @Deprecated
    public double getDouble(final String columnName) throws SQLException {
        final int columnIndex = this.findColumn(columnName);
        return this.getDouble(columnIndex);
    }
    
    @Deprecated
    public float getFloat(final String columnName) throws SQLException {
        final int columnIndex = this.findColumn(columnName);
        return this.getFloat(columnIndex);
    }
    
    @Deprecated
    public int getInt(final String columnName) throws SQLException {
        final int columnIndex = this.findColumn(columnName);
        return this.getInt(columnIndex);
    }
    
    @Deprecated
    public long getLong(final String columnName) throws SQLException {
        final int columnIndex = this.findColumn(columnName);
        return this.getLong(columnIndex);
    }
    
    public Date getDate(final String columnName) throws SQLException {
        final int columnIndex = this.findColumn(columnName);
        return this.getDate(columnIndex);
    }
    
    public boolean isNumber(final int columnIndex) throws SQLException {
        return this.rs.isNumber(columnIndex);
    }
    
    public boolean isNumber(final String columnAlias) throws SQLException {
        return this.rs.isNumber(this.findColumn(columnAlias));
    }
    
    public boolean isDate(final int columnIndex) throws SQLException {
        return this.rs.isDate(columnIndex);
    }
    
    public boolean isDate(final String columnAlias) throws SQLException {
        return this.rs.isDate(this.findColumn(columnAlias));
    }
    
    public boolean isTime(final int columnIndex) throws SQLException {
        return this.rs.isTime(columnIndex);
    }
    
    public boolean isTime(final String columnAlias) throws SQLException {
        return this.rs.isTime(this.findColumn(columnAlias));
    }
    
    public boolean isTimestamp(final int columnIndex) throws SQLException {
        return this.rs.isTimestamp(columnIndex);
    }
    
    public boolean isTimestamp(final String columnAlias) throws SQLException {
        return this.rs.isTimestamp(this.findColumn(columnAlias));
    }
    
    public boolean isChar(final int columnIndex) throws SQLException {
        return this.rs.isChar(columnIndex);
    }
    
    public boolean isChar(final String columnAlias) throws SQLException {
        return this.rs.isChar(this.findColumn(columnAlias));
    }
    
    public boolean isBlob(final int columnIndex) throws SQLException {
        return this.rs.isBlob(columnIndex);
    }
    
    public boolean isBlob(final String columnAlias) throws SQLException {
        return this.rs.isBlob(this.findColumn(columnAlias));
    }
    
    public boolean isBoolean(final int columnIndex) throws SQLException {
        return this.rs.isBoolean(columnIndex);
    }
    
    public boolean isBoolean(final String columnAlias) throws SQLException {
        return this.rs.isBoolean(this.findColumn(columnAlias));
    }
    
    public String getColumnClassName(final int columnIndex) throws SQLException {
        return this.rs.getClassType(columnIndex);
    }
    
    public String getColumnClassName(final String columnAlias) throws SQLException {
        return this.rs.getClassType(this.findColumn(columnAlias));
    }
    
    public int getFetchSize() throws SQLException {
        return this.rs.getFetchSize();
    }
    
    public int getType() throws SQLException {
        return this.rs.getType();
    }
    
    public ResultSetAdapter getResultSetAdapter() {
        return this.rs;
    }
    
    static {
        OUT = Logger.getLogger(DataSet.class.getName());
        DataSet.coll = Collator.getInstance();
    }
}
