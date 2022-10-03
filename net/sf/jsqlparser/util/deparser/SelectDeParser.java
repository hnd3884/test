package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.Fetch;
import net.sf.jsqlparser.statement.select.Offset;
import net.sf.jsqlparser.statement.select.PivotXml;
import net.sf.jsqlparser.schema.Column;
import java.util.List;
import net.sf.jsqlparser.expression.MySQLIndexHint;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.statement.select.Pivot;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Top;
import java.util.Iterator;
import net.sf.jsqlparser.statement.select.First;
import net.sf.jsqlparser.statement.select.Skip;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.PivotVisitor;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;

public class SelectDeParser implements SelectVisitor, SelectItemVisitor, FromItemVisitor, PivotVisitor
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    
    public SelectDeParser() {
        this.buffer = new StringBuilder();
        this.expressionVisitor = new ExpressionVisitorAdapter();
    }
    
    public SelectDeParser(final ExpressionVisitor expressionVisitor, final StringBuilder buffer) {
        this.buffer = new StringBuilder();
        this.expressionVisitor = new ExpressionVisitorAdapter();
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
    }
    
    @Override
    public void visit(final PlainSelect plainSelect) {
        if (plainSelect.isUseBrackets()) {
            this.buffer.append("(");
        }
        this.buffer.append("SELECT ");
        final OracleHint hint = plainSelect.getOracleHint();
        if (hint != null) {
            this.buffer.append(hint).append(" ");
        }
        final Skip skip = plainSelect.getSkip();
        if (skip != null) {
            this.buffer.append(skip).append(" ");
        }
        final First first = plainSelect.getFirst();
        if (first != null) {
            this.buffer.append(first).append(" ");
        }
        if (plainSelect.getDistinct() != null) {
            if (plainSelect.getDistinct().isUseUnique()) {
                this.buffer.append("UNIQUE ");
            }
            else {
                this.buffer.append("DISTINCT ");
            }
            if (plainSelect.getDistinct().getOnSelectItems() != null) {
                this.buffer.append("ON (");
                final Iterator<SelectItem> iter = plainSelect.getDistinct().getOnSelectItems().iterator();
                while (iter.hasNext()) {
                    final SelectItem selectItem = iter.next();
                    selectItem.accept(this);
                    if (iter.hasNext()) {
                        this.buffer.append(", ");
                    }
                }
                this.buffer.append(") ");
            }
        }
        final Top top = plainSelect.getTop();
        if (top != null) {
            this.buffer.append(top).append(" ");
        }
        final Iterator<SelectItem> iter2 = plainSelect.getSelectItems().iterator();
        while (iter2.hasNext()) {
            final SelectItem selectItem2 = iter2.next();
            selectItem2.accept(this);
            if (iter2.hasNext()) {
                this.buffer.append(", ");
            }
        }
        if (plainSelect.getIntoTables() != null) {
            this.buffer.append(" INTO ");
            final Iterator<Table> iter3 = plainSelect.getIntoTables().iterator();
            while (iter3.hasNext()) {
                this.visit(iter3.next());
                if (iter3.hasNext()) {
                    this.buffer.append(", ");
                }
            }
        }
        if (plainSelect.getFromItem() != null) {
            this.buffer.append(" FROM ");
            plainSelect.getFromItem().accept(this);
        }
        if (plainSelect.getJoins() != null) {
            for (final Join join : plainSelect.getJoins()) {
                this.deparseJoin(join);
            }
        }
        if (plainSelect.getWhere() != null) {
            this.buffer.append(" WHERE ");
            plainSelect.getWhere().accept(this.expressionVisitor);
        }
        if (plainSelect.getOracleHierarchical() != null) {
            plainSelect.getOracleHierarchical().accept(this.expressionVisitor);
        }
        if (plainSelect.getGroupByColumnReferences() != null) {
            this.buffer.append(" GROUP BY ");
            final Iterator<Expression> iter4 = plainSelect.getGroupByColumnReferences().iterator();
            while (iter4.hasNext()) {
                final Expression columnReference = iter4.next();
                columnReference.accept(this.expressionVisitor);
                if (iter4.hasNext()) {
                    this.buffer.append(", ");
                }
            }
        }
        if (plainSelect.getHaving() != null) {
            this.buffer.append(" HAVING ");
            plainSelect.getHaving().accept(this.expressionVisitor);
        }
        if (plainSelect.getOrderByElements() != null) {
            new OrderByDeParser(this.expressionVisitor, this.buffer).deParse(plainSelect.isOracleSiblings(), plainSelect.getOrderByElements());
        }
        if (plainSelect.getLimit() != null) {
            new LimitDeparser(this.buffer).deParse(plainSelect.getLimit());
        }
        if (plainSelect.getOffset() != null) {
            this.deparseOffset(plainSelect.getOffset());
        }
        if (plainSelect.getFetch() != null) {
            this.deparseFetch(plainSelect.getFetch());
        }
        if (plainSelect.isForUpdate()) {
            this.buffer.append(" FOR UPDATE");
            if (plainSelect.getForUpdateTable() != null) {
                this.buffer.append(" OF ").append(plainSelect.getForUpdateTable());
            }
            if (plainSelect.getWait() != null) {
                this.buffer.append(plainSelect.getWait());
            }
        }
        if (plainSelect.isUseBrackets()) {
            this.buffer.append(")");
        }
    }
    
    @Override
    public void visit(final AllTableColumns allTableColumns) {
        this.buffer.append(allTableColumns.getTable().getFullyQualifiedName()).append(".*");
    }
    
    @Override
    public void visit(final SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(this.expressionVisitor);
        if (selectExpressionItem.getAlias() != null) {
            this.buffer.append(selectExpressionItem.getAlias().toString());
        }
    }
    
    @Override
    public void visit(final SubSelect subSelect) {
        this.buffer.append("(");
        if (subSelect.getWithItemsList() != null && !subSelect.getWithItemsList().isEmpty()) {
            this.buffer.append("WITH ");
            final Iterator<WithItem> iter = subSelect.getWithItemsList().iterator();
            while (iter.hasNext()) {
                final WithItem withItem = iter.next();
                withItem.accept(this);
                if (iter.hasNext()) {
                    this.buffer.append(",");
                }
                this.buffer.append(" ");
            }
        }
        subSelect.getSelectBody().accept(this);
        this.buffer.append(")");
        final Pivot pivot = subSelect.getPivot();
        if (pivot != null) {
            pivot.accept(this);
        }
        final Alias alias = subSelect.getAlias();
        if (alias != null) {
            this.buffer.append(alias.toString());
        }
    }
    
    @Override
    public void visit(final Table tableName) {
        this.buffer.append(tableName.getFullyQualifiedName());
        final Pivot pivot = tableName.getPivot();
        if (pivot != null) {
            pivot.accept(this);
        }
        final Alias alias = tableName.getAlias();
        if (alias != null) {
            this.buffer.append(alias);
        }
        final MySQLIndexHint indexHint = tableName.getIndexHint();
        if (indexHint != null) {
            this.buffer.append(indexHint);
        }
    }
    
    @Override
    public void visit(final Pivot pivot) {
        final List<Column> forColumns = pivot.getForColumns();
        this.buffer.append(" PIVOT (").append(PlainSelect.getStringList(pivot.getFunctionItems())).append(" FOR ").append(PlainSelect.getStringList(forColumns, true, forColumns != null && forColumns.size() > 1)).append(" IN ").append(PlainSelect.getStringList(pivot.getInItems(), true, true)).append(")");
    }
    
    @Override
    public void visit(final PivotXml pivot) {
        final List<Column> forColumns = pivot.getForColumns();
        this.buffer.append(" PIVOT XML (").append(PlainSelect.getStringList(pivot.getFunctionItems())).append(" FOR ").append(PlainSelect.getStringList(forColumns, true, forColumns != null && forColumns.size() > 1)).append(" IN (");
        if (pivot.isInAny()) {
            this.buffer.append("ANY");
        }
        else if (pivot.getInSelect() != null) {
            this.buffer.append(pivot.getInSelect());
        }
        else {
            this.buffer.append(PlainSelect.getStringList(pivot.getInItems()));
        }
        this.buffer.append("))");
    }
    
    public void deparseOffset(final Offset offset) {
        if (offset.isOffsetJdbcParameter()) {
            this.buffer.append(" OFFSET ?");
        }
        else if (offset.getOffset() != 0L) {
            this.buffer.append(" OFFSET ");
            this.buffer.append(offset.getOffset());
        }
        if (offset.getOffsetParam() != null) {
            this.buffer.append(" ").append(offset.getOffsetParam());
        }
    }
    
    public void deparseFetch(final Fetch fetch) {
        this.buffer.append(" FETCH ");
        if (fetch.isFetchParamFirst()) {
            this.buffer.append("FIRST ");
        }
        else {
            this.buffer.append("NEXT ");
        }
        if (fetch.isFetchJdbcParameter()) {
            this.buffer.append("?");
        }
        else {
            this.buffer.append(fetch.getRowCount());
        }
        this.buffer.append(" ").append(fetch.getFetchParam()).append(" ONLY");
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public ExpressionVisitor getExpressionVisitor() {
        return this.expressionVisitor;
    }
    
    public void setExpressionVisitor(final ExpressionVisitor visitor) {
        this.expressionVisitor = visitor;
    }
    
    @Override
    public void visit(final SubJoin subjoin) {
        this.buffer.append("(");
        subjoin.getLeft().accept(this);
        this.deparseJoin(subjoin.getJoin());
        this.buffer.append(")");
        if (subjoin.getPivot() != null) {
            subjoin.getPivot().accept(this);
        }
    }
    
    public void deparseJoin(final Join join) {
        if (join.isSimple()) {
            this.buffer.append(", ");
        }
        else {
            if (join.isRight()) {
                this.buffer.append(" RIGHT");
            }
            else if (join.isNatural()) {
                this.buffer.append(" NATURAL");
            }
            else if (join.isFull()) {
                this.buffer.append(" FULL");
            }
            else if (join.isLeft()) {
                this.buffer.append(" LEFT");
            }
            else if (join.isCross()) {
                this.buffer.append(" CROSS");
            }
            if (join.isOuter()) {
                this.buffer.append(" OUTER");
            }
            else if (join.isInner()) {
                this.buffer.append(" INNER");
            }
            else if (join.isSemi()) {
                this.buffer.append(" SEMI");
            }
            this.buffer.append(" JOIN ");
        }
        final FromItem fromItem = join.getRightItem();
        fromItem.accept(this);
        if (join.getOnExpression() != null) {
            this.buffer.append(" ON ");
            join.getOnExpression().accept(this.expressionVisitor);
        }
        if (join.getUsingColumns() != null) {
            this.buffer.append(" USING (");
            final Iterator<Column> iterator = join.getUsingColumns().iterator();
            while (iterator.hasNext()) {
                final Column column = iterator.next();
                this.buffer.append(column.toString());
                if (iterator.hasNext()) {
                    this.buffer.append(", ");
                }
            }
            this.buffer.append(")");
        }
    }
    
    @Override
    public void visit(final SetOperationList list) {
        for (int i = 0; i < list.getSelects().size(); ++i) {
            if (i != 0) {
                this.buffer.append(' ').append(list.getOperations().get(i - 1)).append(' ');
            }
            final boolean brackets = list.getBrackets() == null || list.getBrackets().get(i);
            if (brackets) {
                this.buffer.append("(");
            }
            list.getSelects().get(i).accept(this);
            if (brackets) {
                this.buffer.append(")");
            }
        }
        if (list.getOrderByElements() != null) {
            new OrderByDeParser(this.expressionVisitor, this.buffer).deParse(list.getOrderByElements());
        }
        if (list.getLimit() != null) {
            new LimitDeparser(this.buffer).deParse(list.getLimit());
        }
        if (list.getOffset() != null) {
            this.deparseOffset(list.getOffset());
        }
        if (list.getFetch() != null) {
            this.deparseFetch(list.getFetch());
        }
    }
    
    @Override
    public void visit(final WithItem withItem) {
        if (withItem.isRecursive()) {
            this.buffer.append("RECURSIVE ");
        }
        this.buffer.append(withItem.getName());
        if (withItem.getWithItemList() != null) {
            this.buffer.append(" ").append(PlainSelect.getStringList(withItem.getWithItemList(), true, true));
        }
        this.buffer.append(" AS (");
        withItem.getSelectBody().accept(this);
        this.buffer.append(")");
    }
    
    @Override
    public void visit(final LateralSubSelect lateralSubSelect) {
        this.buffer.append(lateralSubSelect.toString());
    }
    
    @Override
    public void visit(final ValuesList valuesList) {
        this.buffer.append(valuesList.toString());
    }
    
    @Override
    public void visit(final AllColumns allColumns) {
        this.buffer.append('*');
    }
    
    @Override
    public void visit(final TableFunction tableFunction) {
        this.buffer.append(tableFunction.toString());
    }
}
