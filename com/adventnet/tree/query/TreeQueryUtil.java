package com.adventnet.tree.query;

import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.persistence.DataAccess;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.util.QueryUtil;
import java.util.List;
import java.util.Iterator;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class TreeQueryUtil
{
    private static final String CLASS_NAME;
    private static Logger out;
    
    public static Row addTreeQueryIntoDO(final TreeQuery select, final DataObject dataObject) throws DataAccessException, MetaDataException {
        final Row row = getTreeQueryRow(select, dataObject);
        dataObject.addRow(row);
        addTreeIdentifier(dataObject, select, select.getTreeIdentifier(), row);
        addObjectIdentifier(dataObject, select, select.getStartingParentKey(), row);
        getSQinTreeQueryRow(select, dataObject, row);
        return row;
    }
    
    private static void addTreeIdentifier(final DataObject dataObject, final TreeQuery select, final Row treeIdentifier, final Row treeIDRow) throws DataAccessException, MetaDataException {
        final DataObject dao = select.getTreeDefinition();
        final Iterator itr = dao.getRows("TreeIdentifierColumns");
        while (itr.hasNext()) {
            final Row treeId = itr.next();
            final String colName = String.valueOf(treeId.get(2));
            final String colVal = String.valueOf(treeIdentifier.get(colName));
            final Row row = new Row("TreeQueryIdentifierColumns");
            row.set(1, treeIDRow.get(1));
            row.set(2, (Object)treeIdentifier.getTableName());
            row.set(3, (Object)colName);
            row.set(4, (Object)colVal);
            dataObject.addRow(row);
        }
    }
    
    private static void addObjectIdentifier(final DataObject dataObject, final TreeQuery select, final Row objectIdentifier, final Row objectIDRow) throws DataAccessException {
        if (objectIdentifier != null) {
            final List colList = objectIdentifier.getPKColumns();
            for (int colSize = colList.size(), i = 1; i <= colSize; ++i) {
                final String colName = String.valueOf(colList.get(i - 1));
                final String colVal = String.valueOf(objectIdentifier.get(colName));
                final Row row = new Row("ObjectIdentifierColumns");
                row.set(1, objectIDRow.get("QUERYID"));
                row.set(2, (Object)objectIdentifier.getTableName());
                row.set(3, (Object)colName);
                row.set(4, (Object)colVal);
                dataObject.addRow(row);
            }
        }
    }
    
    private static Row getTreeQueryRow(final TreeQuery select, final DataObject dataObject) throws DataAccessException {
        final Row selectQRow = QueryUtil.addSelectQueryIntoDO((SelectQuery)select, dataObject);
        final int depth = 0;
        final String treeType = null;
        final Row row = new Row("TreeQuery");
        row.set(1, selectQRow.get("QUERYID"));
        row.set(3, (Object)new Integer(select.getDepth()));
        row.set(2, (Object)new String(select.getTreeType()));
        return row;
    }
    
    private static void getSQinTreeQueryRow(final TreeQuery select, final DataObject dataObject, final Row treeRow) throws DataAccessException {
        final HashMap selectQueries = ((TreeQueryImpl)select).getSelectQueries();
        for (final String key : selectQueries.keySet()) {
            final SelectQuery sqForTable = selectQueries.get(key);
            final Row sqRow = QueryUtil.addSelectQueryIntoDO(sqForTable, dataObject);
            final Object keyForSqRow = sqRow.get("QUERYID");
            final Row sqinTQ = new Row("SQinTreeQuery");
            sqinTQ.set(1, treeRow.get("QUERYID"));
            sqinTQ.set(2, keyForSqRow);
            sqinTQ.set(3, (Object)key);
            dataObject.addRow(sqinTQ);
        }
    }
    
    public static TreeQuery[] getTreeQueryFromDO(final DataObject dataObject) throws DataAccessException, MetaDataException {
        final List treeQueries = new ArrayList();
        final Iterator itr = dataObject.getRows("TreeQuery");
        while (itr.hasNext()) {
            TreeQueryImpl treeQ = null;
            final List tabRowsList = new ArrayList();
            final Row treeRow = itr.next();
            final Row parentRow = getCorrespondingRow(dataObject, "ObjectIdentifierColumns", treeRow);
            final Row treeIdRow = getCorrespondingRow(dataObject, "TreeQueryIdentifierColumns", treeRow);
            final int depth = (int)treeRow.get(3);
            final String treetype = (String)treeRow.get(2);
            final Iterator sqitr = dataObject.getRows("SQinTreeQuery");
            Row sqRow = null;
            SelectQuery squery = null;
            treeQ = new TreeQueryImpl(treetype, treeIdRow, parentRow, depth);
            while (sqitr.hasNext()) {
                sqRow = sqitr.next();
                final Object qid = sqRow.get("QUERYID");
                squery = QueryUtil.getSelectQuery((long)qid);
                treeQ.setSelectQuery(squery);
            }
            treeQueries.add(treeQ);
        }
        return treeQueries.toArray(new TreeQuery[treeQueries.size()]);
    }
    
    private static Row getCorrespondingRow(final DataObject dataObject, final String corresTable, final Row corresRow) throws DataAccessException, MetaDataException {
        final List tabRowsList = new ArrayList();
        final DataObject corresDO = DataAccess.constructDataObject();
        final long treeId = (long)corresRow.get("QUERYID");
        final Iterator tabitr = dataObject.getRows(corresTable);
        while (tabitr.hasNext()) {
            final Row corresTreeRow = tabitr.next();
            if ((long)corresRow.get("QUERYID") == treeId) {
                corresDO.addRow(corresTreeRow);
            }
        }
        final Iterator tabItr = corresDO.getRows(corresTable);
        String objectTabName = null;
        while (tabItr.hasNext()) {
            final Row tabRow = tabItr.next();
            tabRowsList.add(tabRow);
            objectTabName = (String)tabRow.get("TABLENAME");
        }
        final ArrayList columnList = new ArrayList();
        final ArrayList columnValList = new ArrayList();
        for (int tabsSize = tabRowsList.size(), i = 0; i < tabsSize; ++i) {
            final Row selectTab = tabRowsList.get(i);
            objectTabName = (String)selectTab.get("TABLENAME");
            columnList.add(selectTab.get("COLUMNNAME"));
            columnValList.add(selectTab.get("COLUMNVALUE"));
        }
        Row parentRow = null;
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName(objectTabName);
        for (int j = 0; j < columnList.size(); ++j) {
            parentRow = new Row(objectTabName);
            final ColumnDefinition cd = td.getColumnDefinitionByName((String)columnList.get(j));
            final String dataType = cd.getDataType();
            final Object objDataType = QueryUtil.convert((Object)columnValList.get(j), dataType);
            parentRow.set((String)columnList.get(j), objDataType);
        }
        return parentRow;
    }
    
    static {
        CLASS_NAME = TreeQueryUtil.class.getName();
        TreeQueryUtil.out = Logger.getLogger(TreeQueryUtil.CLASS_NAME);
    }
}
