package net.sf.jsqlparser.util;

import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.statement.Commit;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.statement.create.view.AlterView;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.WithinGroupExpression;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.IntervalExpression;
import java.util.ArrayList;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.Expression;
import java.util.Iterator;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.Statement;
import java.util.List;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;

public class TablesNamesFinder implements SelectVisitor, FromItemVisitor, ExpressionVisitor, ItemsListVisitor, SelectItemVisitor, StatementVisitor
{
    private static final String NOT_SUPPORTED_YET = "Not supported yet.";
    private List<String> tables;
    private List<String> otherItemNames;
    
    public List<String> getTableList(final Statement statement) {
        this.init();
        statement.accept(this);
        return this.tables;
    }
    
    @Override
    public void visit(final Select select) {
        if (select.getWithItemsList() != null) {
            for (final WithItem withItem : select.getWithItemsList()) {
                withItem.accept(this);
            }
        }
        select.getSelectBody().accept(this);
    }
    
    public List<String> getTableList(final Expression expr) {
        this.init();
        expr.accept(this);
        return this.tables;
    }
    
    @Override
    public void visit(final WithItem withItem) {
        this.otherItemNames.add(withItem.getName().toLowerCase());
        withItem.getSelectBody().accept(this);
    }
    
    @Override
    public void visit(final PlainSelect plainSelect) {
        if (plainSelect.getSelectItems() != null) {
            for (final SelectItem item : plainSelect.getSelectItems()) {
                item.accept(this);
            }
        }
        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(this);
        }
        if (plainSelect.getJoins() != null) {
            for (final Join join : plainSelect.getJoins()) {
                join.getRightItem().accept(this);
            }
        }
        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(this);
        }
        if (plainSelect.getOracleHierarchical() != null) {
            plainSelect.getOracleHierarchical().accept(this);
        }
    }
    
    @Override
    public void visit(final Table tableName) {
        final String tableWholeName = tableName.getFullyQualifiedName();
        if (!this.otherItemNames.contains(tableWholeName.toLowerCase()) && !this.tables.contains(tableWholeName)) {
            this.tables.add(tableWholeName);
        }
    }
    
    @Override
    public void visit(final SubSelect subSelect) {
        if (subSelect.getWithItemsList() != null) {
            for (final WithItem withItem : subSelect.getWithItemsList()) {
                withItem.accept(this);
            }
        }
        subSelect.getSelectBody().accept(this);
    }
    
    @Override
    public void visit(final Addition addition) {
        this.visitBinaryExpression(addition);
    }
    
    @Override
    public void visit(final AndExpression andExpression) {
        this.visitBinaryExpression(andExpression);
    }
    
    @Override
    public void visit(final Between between) {
        between.getLeftExpression().accept(this);
        between.getBetweenExpressionStart().accept(this);
        between.getBetweenExpressionEnd().accept(this);
    }
    
    @Override
    public void visit(final Column tableColumn) {
    }
    
    @Override
    public void visit(final Division division) {
        this.visitBinaryExpression(division);
    }
    
    @Override
    public void visit(final DoubleValue doubleValue) {
    }
    
    @Override
    public void visit(final EqualsTo equalsTo) {
        this.visitBinaryExpression(equalsTo);
    }
    
    @Override
    public void visit(final Function function) {
        final ExpressionList exprList = function.getParameters();
        if (exprList != null) {
            this.visit(exprList);
        }
    }
    
    @Override
    public void visit(final GreaterThan greaterThan) {
        this.visitBinaryExpression(greaterThan);
    }
    
    @Override
    public void visit(final GreaterThanEquals greaterThanEquals) {
        this.visitBinaryExpression(greaterThanEquals);
    }
    
    @Override
    public void visit(final InExpression inExpression) {
        if (inExpression.getLeftExpression() != null) {
            inExpression.getLeftExpression().accept(this);
        }
        else if (inExpression.getLeftItemsList() != null) {
            inExpression.getLeftItemsList().accept(this);
        }
        inExpression.getRightItemsList().accept(this);
    }
    
    @Override
    public void visit(final SignedExpression signedExpression) {
        signedExpression.getExpression().accept(this);
    }
    
    @Override
    public void visit(final IsNullExpression isNullExpression) {
    }
    
    @Override
    public void visit(final JdbcParameter jdbcParameter) {
    }
    
    @Override
    public void visit(final LikeExpression likeExpression) {
        this.visitBinaryExpression(likeExpression);
    }
    
    @Override
    public void visit(final ExistsExpression existsExpression) {
        existsExpression.getRightExpression().accept(this);
    }
    
    @Override
    public void visit(final LongValue longValue) {
    }
    
    @Override
    public void visit(final MinorThan minorThan) {
        this.visitBinaryExpression(minorThan);
    }
    
    @Override
    public void visit(final MinorThanEquals minorThanEquals) {
        this.visitBinaryExpression(minorThanEquals);
    }
    
    @Override
    public void visit(final Multiplication multiplication) {
        this.visitBinaryExpression(multiplication);
    }
    
    @Override
    public void visit(final NotEqualsTo notEqualsTo) {
        this.visitBinaryExpression(notEqualsTo);
    }
    
    @Override
    public void visit(final NullValue nullValue) {
    }
    
    @Override
    public void visit(final OrExpression orExpression) {
        this.visitBinaryExpression(orExpression);
    }
    
    @Override
    public void visit(final Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }
    
    @Override
    public void visit(final StringValue stringValue) {
    }
    
    @Override
    public void visit(final Subtraction subtraction) {
        this.visitBinaryExpression(subtraction);
    }
    
    @Override
    public void visit(final NotExpression notExpr) {
        notExpr.getExpression().accept(this);
    }
    
    public void visitBinaryExpression(final BinaryExpression binaryExpression) {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
    }
    
    @Override
    public void visit(final ExpressionList expressionList) {
        for (final Expression expression : expressionList.getExpressions()) {
            expression.accept(this);
        }
    }
    
    @Override
    public void visit(final DateValue dateValue) {
    }
    
    @Override
    public void visit(final TimestampValue timestampValue) {
    }
    
    @Override
    public void visit(final TimeValue timeValue) {
    }
    
    @Override
    public void visit(final CaseExpression caseExpression) {
    }
    
    @Override
    public void visit(final WhenClause whenClause) {
    }
    
    @Override
    public void visit(final AllComparisonExpression allComparisonExpression) {
        allComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }
    
    @Override
    public void visit(final AnyComparisonExpression anyComparisonExpression) {
        anyComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }
    
    @Override
    public void visit(final SubJoin subjoin) {
        subjoin.getLeft().accept(this);
        subjoin.getJoin().getRightItem().accept(this);
    }
    
    @Override
    public void visit(final Concat concat) {
        this.visitBinaryExpression(concat);
    }
    
    @Override
    public void visit(final Matches matches) {
        this.visitBinaryExpression(matches);
    }
    
    @Override
    public void visit(final BitwiseAnd bitwiseAnd) {
        this.visitBinaryExpression(bitwiseAnd);
    }
    
    @Override
    public void visit(final BitwiseOr bitwiseOr) {
        this.visitBinaryExpression(bitwiseOr);
    }
    
    @Override
    public void visit(final BitwiseXor bitwiseXor) {
        this.visitBinaryExpression(bitwiseXor);
    }
    
    @Override
    public void visit(final CastExpression cast) {
        cast.getLeftExpression().accept(this);
    }
    
    @Override
    public void visit(final Modulo modulo) {
        this.visitBinaryExpression(modulo);
    }
    
    @Override
    public void visit(final AnalyticExpression analytic) {
    }
    
    @Override
    public void visit(final SetOperationList list) {
        for (final SelectBody plainSelect : list.getSelects()) {
            plainSelect.accept(this);
        }
    }
    
    @Override
    public void visit(final ExtractExpression eexpr) {
    }
    
    @Override
    public void visit(final LateralSubSelect lateralSubSelect) {
        lateralSubSelect.getSubSelect().getSelectBody().accept(this);
    }
    
    @Override
    public void visit(final MultiExpressionList multiExprList) {
        for (final ExpressionList exprList : multiExprList.getExprList()) {
            exprList.accept(this);
        }
    }
    
    @Override
    public void visit(final ValuesList valuesList) {
    }
    
    protected void init() {
        this.otherItemNames = new ArrayList<String>();
        this.tables = new ArrayList<String>();
    }
    
    @Override
    public void visit(final IntervalExpression iexpr) {
    }
    
    @Override
    public void visit(final JdbcNamedParameter jdbcNamedParameter) {
    }
    
    @Override
    public void visit(final OracleHierarchicalExpression oexpr) {
        if (oexpr.getStartExpression() != null) {
            oexpr.getStartExpression().accept(this);
        }
        if (oexpr.getConnectExpression() != null) {
            oexpr.getConnectExpression().accept(this);
        }
    }
    
    @Override
    public void visit(final RegExpMatchOperator rexpr) {
        this.visitBinaryExpression(rexpr);
    }
    
    @Override
    public void visit(final RegExpMySQLOperator rexpr) {
        this.visitBinaryExpression(rexpr);
    }
    
    @Override
    public void visit(final JsonExpression jsonExpr) {
    }
    
    @Override
    public void visit(final JsonOperator jsonExpr) {
    }
    
    @Override
    public void visit(final AllColumns allColumns) {
    }
    
    @Override
    public void visit(final AllTableColumns allTableColumns) {
    }
    
    @Override
    public void visit(final SelectExpressionItem item) {
        item.getExpression().accept(this);
    }
    
    @Override
    public void visit(final WithinGroupExpression wgexpr) {
    }
    
    @Override
    public void visit(final UserVariable var) {
    }
    
    @Override
    public void visit(final NumericBind bind) {
    }
    
    @Override
    public void visit(final KeepExpression aexpr) {
    }
    
    @Override
    public void visit(final MySQLGroupConcat groupConcat) {
    }
    
    @Override
    public void visit(final Delete delete) {
        this.tables.add(delete.getTable().getName());
        if (delete.getWhere() != null) {
            delete.getWhere().accept(this);
        }
    }
    
    @Override
    public void visit(final Update update) {
        for (final Table table : update.getTables()) {
            this.tables.add(table.getName());
        }
        if (update.getExpressions() != null) {
            for (final Expression expression : update.getExpressions()) {
                expression.accept(this);
            }
        }
        if (update.getFromItem() != null) {
            update.getFromItem().accept(this);
        }
        if (update.getJoins() != null) {
            for (final Join join : update.getJoins()) {
                join.getRightItem().accept(this);
            }
        }
        if (update.getWhere() != null) {
            update.getWhere().accept(this);
        }
    }
    
    @Override
    public void visit(final Insert insert) {
        this.tables.add(insert.getTable().getName());
        if (insert.getItemsList() != null) {
            insert.getItemsList().accept(this);
        }
        if (insert.getSelect() != null) {
            this.visit(insert.getSelect());
        }
    }
    
    @Override
    public void visit(final Replace replace) {
        this.tables.add(replace.getTable().getName());
        if (replace.getExpressions() != null) {
            for (final Expression expression : replace.getExpressions()) {
                expression.accept(this);
            }
        }
        if (replace.getItemsList() != null) {
            replace.getItemsList().accept(this);
        }
    }
    
    @Override
    public void visit(final Drop drop) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final Truncate truncate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final CreateIndex createIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final CreateTable create) {
        this.tables.add(create.getTable().getFullyQualifiedName());
        if (create.getSelect() != null) {
            create.getSelect().accept(this);
        }
    }
    
    @Override
    public void visit(final CreateView createView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final Alter alter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final Statements stmts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final Execute execute) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final SetStatement set) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final RowConstructor rowConstructor) {
        for (final Expression expr : rowConstructor.getExprList().getExpressions()) {
            expr.accept(this);
        }
    }
    
    @Override
    public void visit(final HexValue hexValue) {
    }
    
    @Override
    public void visit(final Merge merge) {
        this.tables.add(merge.getTable().getName());
        if (merge.getUsingTable() != null) {
            merge.getUsingTable().accept(this);
        }
        else if (merge.getUsingSelect() != null) {
            merge.getUsingSelect().accept((FromItemVisitor)this);
        }
    }
    
    @Override
    public void visit(final OracleHint hint) {
    }
    
    @Override
    public void visit(final TableFunction valuesList) {
    }
    
    @Override
    public void visit(final AlterView alterView) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void visit(final TimeKeyExpression timeKeyExpression) {
    }
    
    @Override
    public void visit(final DateTimeLiteralExpression literal) {
    }
    
    @Override
    public void visit(final Commit commit) {
    }
    
    @Override
    public void visit(final Upsert upsert) {
        this.tables.add(upsert.getTable().getName());
        if (upsert.getItemsList() != null) {
            upsert.getItemsList().accept(this);
        }
        if (upsert.getSelect() != null) {
            this.visit(upsert.getSelect());
        }
    }
}
