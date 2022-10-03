package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PivotXml;
import net.sf.jsqlparser.statement.select.ExpressionListItem;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.FunctionItem;
import net.sf.jsqlparser.statement.select.Pivot;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import java.util.Iterator;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.PivotVisitor;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;

public class ExpressionVisitorAdapter implements ExpressionVisitor, ItemsListVisitor, PivotVisitor, SelectItemVisitor
{
    private SelectVisitor selectVisitor;
    
    public SelectVisitor getSelectVisitor() {
        return this.selectVisitor;
    }
    
    public void setSelectVisitor(final SelectVisitor selectVisitor) {
        this.selectVisitor = selectVisitor;
    }
    
    @Override
    public void visit(final NullValue value) {
    }
    
    @Override
    public void visit(final Function function) {
        if (function.getParameters() != null) {
            function.getParameters().accept(this);
        }
        if (function.getKeep() != null) {
            function.getKeep().accept(this);
        }
    }
    
    @Override
    public void visit(final SignedExpression expr) {
        expr.getExpression().accept(this);
    }
    
    @Override
    public void visit(final JdbcParameter parameter) {
    }
    
    @Override
    public void visit(final JdbcNamedParameter parameter) {
    }
    
    @Override
    public void visit(final DoubleValue value) {
    }
    
    @Override
    public void visit(final LongValue value) {
    }
    
    @Override
    public void visit(final DateValue value) {
    }
    
    @Override
    public void visit(final TimeValue value) {
    }
    
    @Override
    public void visit(final TimestampValue value) {
    }
    
    @Override
    public void visit(final Parenthesis parenthesis) {
        parenthesis.getExpression().accept(this);
    }
    
    @Override
    public void visit(final StringValue value) {
    }
    
    @Override
    public void visit(final Addition expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final Division expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final Multiplication expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final Subtraction expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final AndExpression expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final OrExpression expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final Between expr) {
        expr.getLeftExpression().accept(this);
        expr.getBetweenExpressionStart().accept(this);
        expr.getBetweenExpressionEnd().accept(this);
    }
    
    @Override
    public void visit(final EqualsTo expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final GreaterThan expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final GreaterThanEquals expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final InExpression expr) {
        if (expr.getLeftExpression() != null) {
            expr.getLeftExpression().accept(this);
        }
        else if (expr.getLeftItemsList() != null) {
            expr.getLeftItemsList().accept(this);
        }
        expr.getRightItemsList().accept(this);
    }
    
    @Override
    public void visit(final IsNullExpression expr) {
        expr.getLeftExpression().accept(this);
    }
    
    @Override
    public void visit(final LikeExpression expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final MinorThan expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final MinorThanEquals expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final NotEqualsTo expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final Column column) {
    }
    
    @Override
    public void visit(final SubSelect subSelect) {
        if (this.selectVisitor != null) {
            if (subSelect.getWithItemsList() != null) {
                for (final WithItem item : subSelect.getWithItemsList()) {
                    item.accept(this.selectVisitor);
                }
            }
            subSelect.getSelectBody().accept(this.selectVisitor);
        }
        if (subSelect.getPivot() != null) {
            subSelect.getPivot().accept(this);
        }
    }
    
    @Override
    public void visit(final CaseExpression expr) {
        if (expr.getSwitchExpression() != null) {
            expr.getSwitchExpression().accept(this);
        }
        for (final Expression x : expr.getWhenClauses()) {
            x.accept(this);
        }
        if (expr.getElseExpression() != null) {
            expr.getElseExpression().accept(this);
        }
    }
    
    @Override
    public void visit(final WhenClause expr) {
        expr.getWhenExpression().accept(this);
        expr.getThenExpression().accept(this);
    }
    
    @Override
    public void visit(final ExistsExpression expr) {
        expr.getRightExpression().accept(this);
    }
    
    @Override
    public void visit(final AllComparisonExpression expr) {
    }
    
    @Override
    public void visit(final AnyComparisonExpression expr) {
    }
    
    @Override
    public void visit(final Concat expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final Matches expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final BitwiseAnd expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final BitwiseOr expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final BitwiseXor expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final CastExpression expr) {
        expr.getLeftExpression().accept(this);
    }
    
    @Override
    public void visit(final Modulo expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final AnalyticExpression expr) {
        expr.getExpression().accept(this);
        expr.getDefaultValue().accept(this);
        expr.getOffset().accept(this);
        if (expr.getKeep() != null) {
            expr.getKeep().accept(this);
        }
        for (final OrderByElement element : expr.getOrderByElements()) {
            element.getExpression().accept(this);
        }
        expr.getWindowElement().getRange().getStart().getExpression().accept(this);
        expr.getWindowElement().getRange().getEnd().getExpression().accept(this);
        expr.getWindowElement().getOffset().getExpression().accept(this);
    }
    
    @Override
    public void visit(final ExtractExpression expr) {
        expr.getExpression().accept(this);
    }
    
    @Override
    public void visit(final IntervalExpression expr) {
    }
    
    @Override
    public void visit(final OracleHierarchicalExpression expr) {
        expr.getConnectExpression().accept(this);
        expr.getStartExpression().accept(this);
    }
    
    @Override
    public void visit(final RegExpMatchOperator expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final ExpressionList expressionList) {
        for (final Expression expr : expressionList.getExpressions()) {
            expr.accept(this);
        }
    }
    
    @Override
    public void visit(final MultiExpressionList multiExprList) {
        for (final ExpressionList list : multiExprList.getExprList()) {
            this.visit(list);
        }
    }
    
    @Override
    public void visit(final NotExpression notExpr) {
        notExpr.getExpression().accept(this);
    }
    
    protected void visitBinaryExpression(final BinaryExpression expr) {
        expr.getLeftExpression().accept(this);
        expr.getRightExpression().accept(this);
    }
    
    @Override
    public void visit(final JsonExpression jsonExpr) {
        this.visit(jsonExpr.getColumn());
    }
    
    @Override
    public void visit(final JsonOperator expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final RegExpMySQLOperator expr) {
        this.visitBinaryExpression(expr);
    }
    
    @Override
    public void visit(final WithinGroupExpression wgexpr) {
        wgexpr.getExprList().accept(this);
        for (final OrderByElement element : wgexpr.getOrderByElements()) {
            element.getExpression().accept(this);
        }
    }
    
    @Override
    public void visit(final UserVariable var) {
    }
    
    @Override
    public void visit(final NumericBind bind) {
    }
    
    @Override
    public void visit(final KeepExpression expr) {
        for (final OrderByElement element : expr.getOrderByElements()) {
            element.getExpression().accept(this);
        }
    }
    
    @Override
    public void visit(final MySQLGroupConcat groupConcat) {
        for (final Expression expr : groupConcat.getExpressionList().getExpressions()) {
            expr.accept(this);
        }
        if (groupConcat.getOrderByElements() != null) {
            for (final OrderByElement element : groupConcat.getOrderByElements()) {
                element.getExpression().accept(this);
            }
        }
    }
    
    @Override
    public void visit(final Pivot pivot) {
        for (final FunctionItem item : pivot.getFunctionItems()) {
            item.getFunction().accept(this);
        }
        for (final Column col : pivot.getForColumns()) {
            col.accept(this);
        }
        if (pivot.getSingleInItems() != null) {
            for (final SelectExpressionItem item2 : pivot.getSingleInItems()) {
                item2.accept(this);
            }
        }
        if (pivot.getMultiInItems() != null) {
            for (final ExpressionListItem item3 : pivot.getMultiInItems()) {
                item3.getExpressionList().accept(this);
            }
        }
    }
    
    @Override
    public void visit(final PivotXml pivot) {
        for (final FunctionItem item : pivot.getFunctionItems()) {
            item.getFunction().accept(this);
        }
        for (final Column col : pivot.getForColumns()) {
            col.accept(this);
        }
        if (pivot.getInSelect() != null && this.selectVisitor != null) {
            pivot.getInSelect().accept(this.selectVisitor);
        }
    }
    
    @Override
    public void visit(final AllColumns allColumns) {
    }
    
    @Override
    public void visit(final AllTableColumns allTableColumns) {
    }
    
    @Override
    public void visit(final SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(this);
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
    public void visit(final OracleHint hint) {
    }
    
    @Override
    public void visit(final TimeKeyExpression timeKeyExpression) {
    }
    
    @Override
    public void visit(final DateTimeLiteralExpression literal) {
    }
}
