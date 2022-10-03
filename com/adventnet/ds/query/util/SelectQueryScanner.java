package com.adventnet.ds.query.util;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.SortColumn;
import java.util.List;
import org.w3c.dom.Element;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;

public class SelectQueryScanner
{
    private SelectQuery selectQuery;
    private Table mainTable;
    
    public SelectQueryScanner() {
        this.mainTable = null;
    }
    
    public SelectQuery visitElement_select_query(final Element selectElement) {
        List sortColumns = null;
        List selectColumns = null;
        Criteria mainCriteria = null;
        final NodeList nodes = selectElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("select-clause")) {
                        selectColumns = this.visitElement_select_clause(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("from-clause")) {
                        this.visitElement_from_clause(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("where-clause")) {
                        mainCriteria = this.visitElement_where_clause(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("order-by-clause")) {
                        sortColumns = this.visitElement_order_by_clause(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
        if (this.selectQuery != null) {
            this.selectQuery.addSelectColumns(selectColumns);
            if (sortColumns != null) {
                this.selectQuery.addSortColumns(sortColumns);
            }
            this.selectQuery.setCriteria(mainCriteria);
        }
        return this.selectQuery;
    }
    
    private List visitElement_select_clause(final Element selectClause) {
        final List selectCols = new ArrayList();
        final NodeList nodes = selectClause.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("select-column")) {
                        this.visitElement_select_column(nodeElement, selectCols);
                        break;
                    }
                    break;
                }
            }
        }
        return selectCols;
    }
    
    private void visitElement_select_column(final Element selectColElement, final List list) {
        final Column column = new Column();
        final NamedNodeMap cols = selectColElement.getAttributes();
        for (int i = 0; i < cols.getLength(); ++i) {
            final Attr col = (Attr)cols.item(i);
            if (col.getName().equals("alias")) {
                column.setColumnAlias(col.getValue());
            }
            if (col.getName().equals("table-name")) {
                String tableAlias = col.getValue();
                if (tableAlias.equals("null")) {
                    tableAlias = null;
                }
                column.setTableAlias(tableAlias);
            }
            if (col.getName().equals("name")) {
                column.setColumnName(col.getValue());
            }
        }
        if (column.getColumnAlias() == null) {
            column.setColumnAlias(column.getColumnName());
        }
        list.add(column);
        final NodeList nodes = selectColElement.getChildNodes();
        for (int j = 0; j < nodes.getLength(); ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    break;
                }
            }
        }
    }
    
    private void visitElement_from_clause(final Element fromElement) {
        final NodeList nodes = fromElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("table")) {
                        this.visitElement_table(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void visitElement_table(final Element tableElement) {
        final NamedNodeMap cols = tableElement.getAttributes();
        String tableName = null;
        String tableAlias = null;
        for (int i = 0; i < cols.getLength(); ++i) {
            final Attr col = (Attr)cols.item(i);
            if (col.getName().equals("alias")) {
                tableAlias = col.getValue();
            }
            if (col.getName().equals("name")) {
                tableName = col.getValue();
            }
        }
        if (tableAlias == null) {
            tableAlias = tableName;
        }
        final Table table = new Table(tableName, tableAlias);
        final NodeList nodes = tableElement.getChildNodes();
        final int relSize = nodes.getLength();
        if (relSize == 0) {
            this.mainTable = table;
            this.selectQuery = new SelectQueryImpl(this.mainTable);
        }
        for (int j = 0; j < relSize; ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("relation")) {
                        this.visitElement_relation(nodeElement, table);
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void visitElement_relation(final Element relationElement, final Table table) {
        final NamedNodeMap cols = relationElement.getAttributes();
        int joinType = -1;
        final String referedTableName = table.getTableName();
        final String referedTableAlias = table.getTableAlias();
        String baseTableAlias = null;
        for (int i = 0; i < cols.getLength(); ++i) {
            final Attr col = (Attr)cols.item(i);
            if (col.getName().equals("join-type")) {
                if (col.getValue().equals("left-join")) {
                    joinType = 1;
                }
                else if (col.getValue().equals("equi-join")) {
                    joinType = 2;
                }
            }
            if (col.getName().equals("related-table")) {
                baseTableAlias = col.getValue();
            }
        }
        final NodeList nodes = relationElement.getChildNodes();
        final List localColList = new ArrayList();
        final List relatedColList = new ArrayList();
        for (int j = 0; j < nodes.getLength(); ++j) {
            final Node node = nodes.item(j);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("key-map")) {
                        this.visitElement_key_map(nodeElement, localColList, relatedColList);
                        break;
                    }
                    break;
                }
            }
        }
        String[] referencedTableColumns = new String[localColList.size()];
        String[] baseTableColumns = new String[relatedColList.size()];
        referencedTableColumns = localColList.toArray(referencedTableColumns);
        baseTableColumns = relatedColList.toArray(baseTableColumns);
        String baseTableName = null;
        final List retTableList = this.selectQuery.getTableList();
        Table baseTable = null;
        for (int k = 0; k < retTableList.size(); ++k) {
            baseTable = retTableList.get(k);
            if (baseTable.getTableAlias().equals(baseTableAlias)) {
                baseTableName = baseTable.getTableName();
                break;
            }
        }
        final Join join = new Join(baseTableName, referedTableName, baseTableColumns, referencedTableColumns, baseTableAlias, referedTableAlias, joinType);
        this.selectQuery.addJoin(join);
    }
    
    private void visitElement_key_map(final Element keyElement, final List localColList, final List relatedColList) {
        final NamedNodeMap cols = keyElement.getAttributes();
        for (int i = 0; i < cols.getLength(); ++i) {
            final Attr col = (Attr)cols.item(i);
            if (col.getName().equals("local-column-name")) {
                localColList.add(col.getValue());
            }
            if (col.getName().equals("related-column-name")) {
                relatedColList.add(col.getValue());
            }
        }
    }
    
    private Criteria visitElement_where_clause(final Element element) {
        final List criteriaList = new ArrayList();
        String operatorName = null;
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("criteria")) {
                        final Criteria criteria = this.visitElement_criteria(nodeElement);
                        if (criteria != null) {
                            criteriaList.add(criteria);
                        }
                    }
                    if (nodeElement.getTagName().equals("operator")) {
                        operatorName = this.visitElement_operator(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
        if (criteriaList.size() > 1 && operatorName != null) {
            final Criteria criteria2 = criteriaList.get(0);
            final Criteria criteria3 = criteriaList.get(1);
            if (operatorName.equals("AND")) {
                return criteria2.and(criteria3);
            }
            if (operatorName.equals("OR")) {
                return criteria2.or(criteria3);
            }
        }
        return criteriaList.get(0);
    }
    
    public Criteria visitElement_criteria(final Node node) {
        final Node firstChild = node.getFirstChild();
        Element validElement = this.getValidElement(firstChild);
        if (validElement.getTagName().equals("relational-criteria")) {
            final Criteria criteria = this.visitElement_relational_criteria(validElement);
            return criteria;
        }
        Criteria criteria2 = null;
        Criteria criteria3 = null;
        String operatorName = null;
        if (validElement.getTagName().equals("criteria")) {
            criteria2 = this.visitElement_criteria(validElement);
        }
        validElement = this.getValidElement(validElement.getNextSibling());
        if (validElement.getTagName().equals("operator")) {
            operatorName = this.visitElement_operator(validElement);
        }
        validElement = this.getValidElement(validElement.getNextSibling());
        if (validElement.getTagName().equals("criteria")) {
            criteria3 = this.visitElement_criteria(validElement);
        }
        if (operatorName.equals("AND")) {
            return criteria2.and(criteria3);
        }
        if (operatorName.equals("OR")) {
            return criteria2.or(criteria3);
        }
        return null;
    }
    
    private Element getValidElement(Node node) {
        while (node.getNodeType() != 1) {
            node = node.getNextSibling();
        }
        return (Element)node;
    }
    
    private Criteria visitElement_relational_criteria(final Element element) {
        final Column column = new Column();
        Object value = null;
        int comparator = -1;
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("table-name")) {
                        column.setTableAlias(this.visitElement_table_name(nodeElement));
                    }
                    if (nodeElement.getTagName().equals("column")) {
                        column.setColumnName(this.visitElement_column(nodeElement));
                    }
                    if (nodeElement.getTagName().equals("comparator")) {
                        comparator = this.visitElement_comparator(nodeElement);
                    }
                    if (nodeElement.getTagName().equals("value")) {
                        value = this.visitElement_value(nodeElement);
                        break;
                    }
                    break;
                }
            }
        }
        return new Criteria(column, value, comparator);
    }
    
    private String visitElement_table_name(final Element element) {
        final NodeList nodes = element.getChildNodes();
        String tableName = null;
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    tableName = node.getNodeValue();
                    break;
                }
            }
        }
        return tableName;
    }
    
    private String visitElement_column(final Element element) {
        String columnName = null;
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    columnName = node.getNodeValue();
                    break;
                }
            }
        }
        return columnName;
    }
    
    private int visitElement_comparator(final Element element) {
        String comparator = null;
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    comparator = node.getNodeValue();
                    break;
                }
            }
        }
        return this.getComparator(comparator);
    }
    
    private int getComparator(String comparatorStr) {
        comparatorStr = comparatorStr.toUpperCase();
        if (comparatorStr.equals("EQUAL")) {
            return 0;
        }
        if (comparatorStr.equals("NOT_EQUAL")) {
            return 1;
        }
        if (comparatorStr.equals("LIKE")) {
            return 2;
        }
        if (comparatorStr.equals("NOT_LIKE")) {
            return 3;
        }
        if (comparatorStr.equals("GREATER_EQUAL")) {
            return 4;
        }
        if (comparatorStr.equals("GREATER_THAN")) {
            return 5;
        }
        if (comparatorStr.equals("LESS_EQUAL")) {
            return 6;
        }
        if (comparatorStr.equals("LESS_THAN")) {
            return 7;
        }
        if (comparatorStr.equals("IN")) {
            return 8;
        }
        if (comparatorStr.equals("NOT_IN")) {
            return 9;
        }
        return 0;
    }
    
    private Object visitElement_value(final Element element) {
        Object value = null;
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    value = node.getNodeValue();
                    break;
                }
            }
        }
        return value;
    }
    
    private String visitElement_operator(final Element element) {
        final NodeList nodes = element.getChildNodes();
        String operatorName = null;
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                }
                case 3: {
                    operatorName = node.getNodeValue();
                    break;
                }
            }
        }
        return operatorName;
    }
    
    private List visitElement_order_by_clause(final Element element) {
        final List sortCols = new ArrayList();
        final NodeList nodes = element.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            switch (node.getNodeType()) {
                case 1: {
                    final Element nodeElement = (Element)node;
                    if (nodeElement.getTagName().equals("sort-column")) {
                        this.visitElement_sort_column(nodeElement, sortCols);
                        break;
                    }
                    break;
                }
            }
        }
        return sortCols;
    }
    
    void visitElement_sort_column(final Element element, final List list) {
        final NamedNodeMap cols = element.getAttributes();
        String tableName = null;
        String colName = null;
        String order = null;
        for (int i = 0; i < cols.getLength(); ++i) {
            final Attr attr = (Attr)cols.item(i);
            if (attr.getName().equals("table-name")) {
                tableName = attr.getValue();
            }
            if (attr.getName().equals("name")) {
                colName = attr.getValue();
            }
            if (attr.getName().equals("order")) {
                order = attr.getValue();
            }
        }
        boolean isAscending = false;
        if (order != null && order.equals("ASC")) {
            isAscending = true;
        }
        final SortColumn sortCol = new SortColumn(tableName, colName, isAscending);
        list.add(sortCol);
    }
}
