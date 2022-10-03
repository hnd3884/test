package net.sf.jsqlparser.statement.select;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import java.util.List;

public class PlainSelect implements SelectBody
{
    private Distinct distinct;
    private List<SelectItem> selectItems;
    private List<Table> intoTables;
    private FromItem fromItem;
    private List<Join> joins;
    private Expression where;
    private List<Expression> groupByColumnReferences;
    private List<OrderByElement> orderByElements;
    private Expression having;
    private Limit limit;
    private Offset offset;
    private Fetch fetch;
    private Skip skip;
    private First first;
    private Top top;
    private OracleHierarchicalExpression oracleHierarchical;
    private OracleHint oracleHint;
    private boolean oracleSiblings;
    private boolean forUpdate;
    private Table forUpdateTable;
    private boolean useBrackets;
    private Wait wait;
    
    public PlainSelect() {
        this.distinct = null;
        this.oracleHierarchical = null;
        this.oracleHint = null;
        this.oracleSiblings = false;
        this.forUpdate = false;
        this.forUpdateTable = null;
        this.useBrackets = false;
    }
    
    public boolean isUseBrackets() {
        return this.useBrackets;
    }
    
    public void setUseBrackets(final boolean useBrackets) {
        this.useBrackets = useBrackets;
    }
    
    public FromItem getFromItem() {
        return this.fromItem;
    }
    
    public List<Table> getIntoTables() {
        return this.intoTables;
    }
    
    public List<SelectItem> getSelectItems() {
        return this.selectItems;
    }
    
    public Expression getWhere() {
        return this.where;
    }
    
    public void setFromItem(final FromItem item) {
        this.fromItem = item;
    }
    
    public void setIntoTables(final List<Table> intoTables) {
        this.intoTables = intoTables;
    }
    
    public void setSelectItems(final List<SelectItem> list) {
        this.selectItems = list;
    }
    
    public void addSelectItems(final SelectItem... items) {
        if (this.selectItems == null) {
            this.selectItems = new ArrayList<SelectItem>();
        }
        Collections.addAll(this.selectItems, items);
    }
    
    public void setWhere(final Expression where) {
        this.where = where;
    }
    
    public List<Join> getJoins() {
        return this.joins;
    }
    
    public void setJoins(final List<Join> list) {
        this.joins = list;
    }
    
    @Override
    public void accept(final SelectVisitor selectVisitor) {
        selectVisitor.visit(this);
    }
    
    public List<OrderByElement> getOrderByElements() {
        return this.orderByElements;
    }
    
    public void setOrderByElements(final List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }
    
    public Limit getLimit() {
        return this.limit;
    }
    
    public void setLimit(final Limit limit) {
        this.limit = limit;
    }
    
    public Offset getOffset() {
        return this.offset;
    }
    
    public void setOffset(final Offset offset) {
        this.offset = offset;
    }
    
    public Fetch getFetch() {
        return this.fetch;
    }
    
    public void setFetch(final Fetch fetch) {
        this.fetch = fetch;
    }
    
    public Top getTop() {
        return this.top;
    }
    
    public void setTop(final Top top) {
        this.top = top;
    }
    
    public Skip getSkip() {
        return this.skip;
    }
    
    public void setSkip(final Skip skip) {
        this.skip = skip;
    }
    
    public First getFirst() {
        return this.first;
    }
    
    public void setFirst(final First first) {
        this.first = first;
    }
    
    public Distinct getDistinct() {
        return this.distinct;
    }
    
    public void setDistinct(final Distinct distinct) {
        this.distinct = distinct;
    }
    
    public Expression getHaving() {
        return this.having;
    }
    
    public void setHaving(final Expression expression) {
        this.having = expression;
    }
    
    public List<Expression> getGroupByColumnReferences() {
        return this.groupByColumnReferences;
    }
    
    public void setGroupByColumnReferences(final List<Expression> list) {
        this.groupByColumnReferences = list;
    }
    
    public void addGroupByColumnReference(final Expression expr) {
        if (this.groupByColumnReferences == null) {
            this.groupByColumnReferences = new ArrayList<Expression>();
        }
        this.groupByColumnReferences.add(expr);
    }
    
    public OracleHierarchicalExpression getOracleHierarchical() {
        return this.oracleHierarchical;
    }
    
    public void setOracleHierarchical(final OracleHierarchicalExpression oracleHierarchical) {
        this.oracleHierarchical = oracleHierarchical;
    }
    
    public boolean isOracleSiblings() {
        return this.oracleSiblings;
    }
    
    public void setOracleSiblings(final boolean oracleSiblings) {
        this.oracleSiblings = oracleSiblings;
    }
    
    public boolean isForUpdate() {
        return this.forUpdate;
    }
    
    public void setForUpdate(final boolean forUpdate) {
        this.forUpdate = forUpdate;
    }
    
    public Table getForUpdateTable() {
        return this.forUpdateTable;
    }
    
    public void setForUpdateTable(final Table forUpdateTable) {
        this.forUpdateTable = forUpdateTable;
    }
    
    public OracleHint getOracleHint() {
        return this.oracleHint;
    }
    
    public void setOracleHint(final OracleHint oracleHint) {
        this.oracleHint = oracleHint;
    }
    
    public void setWait(final Wait wait) {
        this.wait = wait;
    }
    
    public Wait getWait() {
        return this.wait;
    }
    
    @Override
    public String toString() {
        final StringBuilder sql = new StringBuilder();
        if (this.useBrackets) {
            sql.append("(");
        }
        sql.append("SELECT ");
        if (this.oracleHint != null) {
            sql.append(this.oracleHint).append(" ");
        }
        if (this.skip != null) {
            sql.append(this.skip).append(" ");
        }
        if (this.first != null) {
            sql.append(this.first).append(" ");
        }
        if (this.distinct != null) {
            sql.append(this.distinct).append(" ");
        }
        if (this.top != null) {
            sql.append(this.top).append(" ");
        }
        sql.append(getStringList(this.selectItems));
        if (this.intoTables != null) {
            sql.append(" INTO ");
            final Iterator<Table> iter = this.intoTables.iterator();
            while (iter.hasNext()) {
                sql.append(iter.next().toString());
                if (iter.hasNext()) {
                    sql.append(", ");
                }
            }
        }
        if (this.fromItem != null) {
            sql.append(" FROM ").append(this.fromItem);
            if (this.joins != null) {
                for (final Join join : this.joins) {
                    if (join.isSimple()) {
                        sql.append(", ").append(join);
                    }
                    else {
                        sql.append(" ").append(join);
                    }
                }
            }
            if (this.where != null) {
                sql.append(" WHERE ").append(this.where);
            }
            if (this.oracleHierarchical != null) {
                sql.append(this.oracleHierarchical.toString());
            }
            sql.append(getFormatedList(this.groupByColumnReferences, "GROUP BY"));
            if (this.having != null) {
                sql.append(" HAVING ").append(this.having);
            }
            sql.append(orderByToString(this.oracleSiblings, this.orderByElements));
            if (this.limit != null) {
                sql.append(this.limit);
            }
            if (this.offset != null) {
                sql.append(this.offset);
            }
            if (this.fetch != null) {
                sql.append(this.fetch);
            }
            if (this.isForUpdate()) {
                sql.append(" FOR UPDATE");
                if (this.forUpdateTable != null) {
                    sql.append(" OF ").append(this.forUpdateTable);
                }
                if (this.wait != null) {
                    sql.append(this.wait);
                }
            }
        }
        else if (this.where != null) {
            sql.append(" WHERE ").append(this.where);
        }
        if (this.useBrackets) {
            sql.append(")");
        }
        return sql.toString();
    }
    
    public static String orderByToString(final List<OrderByElement> orderByElements) {
        return orderByToString(false, orderByElements);
    }
    
    public static String orderByToString(final boolean oracleSiblings, final List<OrderByElement> orderByElements) {
        return getFormatedList(orderByElements, oracleSiblings ? "ORDER SIBLINGS BY" : "ORDER BY");
    }
    
    public static String getFormatedList(final List<?> list, final String expression) {
        return getFormatedList(list, expression, true, false);
    }
    
    public static String getFormatedList(final List<?> list, final String expression, final boolean useComma, final boolean useBrackets) {
        String sql = getStringList(list, useComma, useBrackets);
        if (sql.length() > 0) {
            if (expression.length() > 0) {
                sql = " " + expression + " " + sql;
            }
            else {
                sql = " " + sql;
            }
        }
        return sql;
    }
    
    public static String getStringList(final List<?> list) {
        return getStringList(list, true, false);
    }
    
    public static String getStringList(final List<?> list, final boolean useComma, final boolean useBrackets) {
        final StringBuilder ans = new StringBuilder();
        String comma = ",";
        if (!useComma) {
            comma = "";
        }
        if (list != null) {
            if (useBrackets) {
                ans.append("(");
            }
            for (int i = 0; i < list.size(); ++i) {
                ans.append(list.get(i)).append((i < list.size() - 1) ? (comma + " ") : "");
            }
            if (useBrackets) {
                ans.append(")");
            }
        }
        return ans.toString();
    }
}
