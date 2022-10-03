package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.WithinGroupExpression;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.WindowElement;
import java.util.List;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.schema.Column;
import java.util.Iterator;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.select.SubSelect;
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
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.relational.OldOracleJoinBinaryExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class ExpressionDeParser implements ExpressionVisitor, ItemsListVisitor
{
    private static final String NOT = "NOT ";
    private StringBuilder buffer;
    private SelectVisitor selectVisitor;
    private boolean useBracketsInExprList;
    private OrderByDeParser orderByDeParser;
    
    public ExpressionDeParser() {
        this.buffer = new StringBuilder();
        this.useBracketsInExprList = true;
        this.orderByDeParser = new OrderByDeParser();
    }
    
    public ExpressionDeParser(final SelectVisitor selectVisitor, final StringBuilder buffer) {
        this(selectVisitor, buffer, new OrderByDeParser());
    }
    
    ExpressionDeParser(final SelectVisitor selectVisitor, final StringBuilder buffer, final OrderByDeParser orderByDeParser) {
        this.buffer = new StringBuilder();
        this.useBracketsInExprList = true;
        this.orderByDeParser = new OrderByDeParser();
        this.selectVisitor = selectVisitor;
        this.buffer = buffer;
        this.orderByDeParser = orderByDeParser;
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    @Override
    public void visit(final Addition addition) {
        this.visitBinaryExpression(addition, " + ");
    }
    
    @Override
    public void visit(final AndExpression andExpression) {
        this.visitBinaryExpression(andExpression, " AND ");
    }
    
    @Override
    public void visit(final Between between) {
        between.getLeftExpression().accept(this);
        if (between.isNot()) {
            this.buffer.append(" NOT");
        }
        this.buffer.append(" BETWEEN ");
        between.getBetweenExpressionStart().accept(this);
        this.buffer.append(" AND ");
        between.getBetweenExpressionEnd().accept(this);
    }
    
    @Override
    public void visit(final EqualsTo equalsTo) {
        this.visitOldOracleJoinBinaryExpression(equalsTo, " = ");
    }
    
    @Override
    public void visit(final Division division) {
        this.visitBinaryExpression(division, " / ");
    }
    
    @Override
    public void visit(final DoubleValue doubleValue) {
        this.buffer.append(doubleValue.toString());
    }
    
    @Override
    public void visit(final HexValue hexValue) {
        this.buffer.append(hexValue.toString());
    }
    
    @Override
    public void visit(final NotExpression notExpr) {
        this.buffer.append("NOT ");
        notExpr.getExpression().accept(this);
    }
    
    public void visitOldOracleJoinBinaryExpression(final OldOracleJoinBinaryExpression expression, final String operator) {
        if (expression.isNot()) {
            this.buffer.append("NOT ");
        }
        expression.getLeftExpression().accept(this);
        if (expression.getOldOracleJoinSyntax() == 1) {
            this.buffer.append("(+)");
        }
        this.buffer.append(operator);
        expression.getRightExpression().accept(this);
        if (expression.getOldOracleJoinSyntax() == 2) {
            this.buffer.append("(+)");
        }
    }
    
    @Override
    public void visit(final GreaterThan greaterThan) {
        this.visitOldOracleJoinBinaryExpression(greaterThan, " > ");
    }
    
    @Override
    public void visit(final GreaterThanEquals greaterThanEquals) {
        this.visitOldOracleJoinBinaryExpression(greaterThanEquals, " >= ");
    }
    
    @Override
    public void visit(final InExpression inExpression) {
        if (inExpression.getLeftExpression() == null) {
            inExpression.getLeftItemsList().accept(this);
        }
        else {
            inExpression.getLeftExpression().accept(this);
            if (inExpression.getOldOracleJoinSyntax() == 1) {
                this.buffer.append("(+)");
            }
        }
        if (inExpression.isNot()) {
            this.buffer.append(" NOT");
        }
        this.buffer.append(" IN ");
        inExpression.getRightItemsList().accept(this);
    }
    
    @Override
    public void visit(final SignedExpression signedExpression) {
        this.buffer.append(signedExpression.getSign());
        signedExpression.getExpression().accept(this);
    }
    
    @Override
    public void visit(final IsNullExpression isNullExpression) {
        isNullExpression.getLeftExpression().accept(this);
        if (isNullExpression.isNot()) {
            this.buffer.append(" IS NOT NULL");
        }
        else {
            this.buffer.append(" IS NULL");
        }
    }
    
    @Override
    public void visit(final JdbcParameter jdbcParameter) {
        this.buffer.append("?");
        if (jdbcParameter.isUseFixedIndex()) {
            this.buffer.append(jdbcParameter.getIndex());
        }
    }
    
    @Override
    public void visit(final LikeExpression likeExpression) {
        this.visitBinaryExpression(likeExpression, likeExpression.isCaseInsensitive() ? " ILIKE " : " LIKE ");
        final String escape = likeExpression.getEscape();
        if (escape != null) {
            this.buffer.append(" ESCAPE '").append(escape).append('\'');
        }
    }
    
    @Override
    public void visit(final ExistsExpression existsExpression) {
        if (existsExpression.isNot()) {
            this.buffer.append("NOT EXISTS ");
        }
        else {
            this.buffer.append("EXISTS ");
        }
        existsExpression.getRightExpression().accept(this);
    }
    
    @Override
    public void visit(final LongValue longValue) {
        this.buffer.append(longValue.getStringValue());
    }
    
    @Override
    public void visit(final MinorThan minorThan) {
        this.visitOldOracleJoinBinaryExpression(minorThan, " < ");
    }
    
    @Override
    public void visit(final MinorThanEquals minorThanEquals) {
        this.visitOldOracleJoinBinaryExpression(minorThanEquals, " <= ");
    }
    
    @Override
    public void visit(final Multiplication multiplication) {
        this.visitBinaryExpression(multiplication, " * ");
    }
    
    @Override
    public void visit(final NotEqualsTo notEqualsTo) {
        this.visitOldOracleJoinBinaryExpression(notEqualsTo, " " + notEqualsTo.getStringExpression() + " ");
    }
    
    @Override
    public void visit(final NullValue nullValue) {
        this.buffer.append(nullValue.toString());
    }
    
    @Override
    public void visit(final OrExpression orExpression) {
        this.visitBinaryExpression(orExpression, " OR ");
    }
    
    @Override
    public void visit(final Parenthesis parenthesis) {
        if (parenthesis.isNot()) {
            this.buffer.append("NOT ");
        }
        this.buffer.append("(");
        parenthesis.getExpression().accept(this);
        this.buffer.append(")");
    }
    
    @Override
    public void visit(final StringValue stringValue) {
        this.buffer.append("'").append(stringValue.getValue()).append("'");
    }
    
    @Override
    public void visit(final Subtraction subtraction) {
        this.visitBinaryExpression(subtraction, " - ");
    }
    
    private void visitBinaryExpression(final BinaryExpression binaryExpression, final String operator) {
        if (binaryExpression.isNot()) {
            this.buffer.append("NOT ");
        }
        binaryExpression.getLeftExpression().accept(this);
        this.buffer.append(operator);
        binaryExpression.getRightExpression().accept(this);
    }
    
    @Override
    public void visit(final SubSelect subSelect) {
        this.buffer.append("(");
        if (this.selectVisitor != null) {
            if (subSelect.getWithItemsList() != null) {
                this.buffer.append("WITH ");
                final Iterator<WithItem> iter = subSelect.getWithItemsList().iterator();
                while (iter.hasNext()) {
                    iter.next().accept(this.selectVisitor);
                    if (iter.hasNext()) {
                        this.buffer.append(", ");
                    }
                    this.buffer.append(" ");
                }
                this.buffer.append(" ");
            }
            subSelect.getSelectBody().accept(this.selectVisitor);
        }
        this.buffer.append(")");
    }
    
    @Override
    public void visit(final Column tableColumn) {
        final Table table = tableColumn.getTable();
        String tableName = null;
        if (table != null) {
            if (table.getAlias() != null) {
                tableName = table.getAlias().getName();
            }
            else {
                tableName = table.getFullyQualifiedName();
            }
        }
        if (tableName != null && !tableName.isEmpty()) {
            this.buffer.append(tableName).append(".");
        }
        this.buffer.append(tableColumn.getColumnName());
    }
    
    @Override
    public void visit(final Function function) {
        if (function.isEscaped()) {
            this.buffer.append("{fn ");
        }
        this.buffer.append(function.getName());
        if (function.isAllColumns() && function.getParameters() == null) {
            this.buffer.append("(*)");
        }
        else if (function.getParameters() == null) {
            this.buffer.append("()");
        }
        else {
            final boolean oldUseBracketsInExprList = this.useBracketsInExprList;
            if (function.isDistinct()) {
                this.useBracketsInExprList = false;
                this.buffer.append("(DISTINCT ");
            }
            else if (function.isAllColumns()) {
                this.useBracketsInExprList = false;
                this.buffer.append("(ALL ");
            }
            this.visit(function.getParameters());
            this.useBracketsInExprList = oldUseBracketsInExprList;
            if (function.isDistinct() || function.isAllColumns()) {
                this.buffer.append(")");
            }
        }
        if (function.getAttribute() != null) {
            this.buffer.append(".").append(function.getAttribute());
        }
        if (function.getKeep() != null) {
            this.buffer.append(" ").append(function.getKeep());
        }
        if (function.isEscaped()) {
            this.buffer.append("}");
        }
    }
    
    @Override
    public void visit(final ExpressionList expressionList) {
        if (this.useBracketsInExprList) {
            this.buffer.append("(");
        }
        final Iterator<Expression> iter = expressionList.getExpressions().iterator();
        while (iter.hasNext()) {
            final Expression expression = iter.next();
            expression.accept(this);
            if (iter.hasNext()) {
                this.buffer.append(", ");
            }
        }
        if (this.useBracketsInExprList) {
            this.buffer.append(")");
        }
    }
    
    public SelectVisitor getSelectVisitor() {
        return this.selectVisitor;
    }
    
    public void setSelectVisitor(final SelectVisitor visitor) {
        this.selectVisitor = visitor;
    }
    
    @Override
    public void visit(final DateValue dateValue) {
        this.buffer.append("{d '").append(dateValue.getValue().toString()).append("'}");
    }
    
    @Override
    public void visit(final TimestampValue timestampValue) {
        this.buffer.append("{ts '").append(timestampValue.getValue().toString()).append("'}");
    }
    
    @Override
    public void visit(final TimeValue timeValue) {
        this.buffer.append("{t '").append(timeValue.getValue().toString()).append("'}");
    }
    
    @Override
    public void visit(final CaseExpression caseExpression) {
        this.buffer.append("CASE ");
        final Expression switchExp = caseExpression.getSwitchExpression();
        if (switchExp != null) {
            switchExp.accept(this);
            this.buffer.append(" ");
        }
        for (final Expression exp : caseExpression.getWhenClauses()) {
            exp.accept(this);
        }
        final Expression elseExp = caseExpression.getElseExpression();
        if (elseExp != null) {
            this.buffer.append("ELSE ");
            elseExp.accept(this);
            this.buffer.append(" ");
        }
        this.buffer.append("END");
    }
    
    @Override
    public void visit(final WhenClause whenClause) {
        this.buffer.append("WHEN ");
        whenClause.getWhenExpression().accept(this);
        this.buffer.append(" THEN ");
        whenClause.getThenExpression().accept(this);
        this.buffer.append(" ");
    }
    
    @Override
    public void visit(final AllComparisonExpression allComparisonExpression) {
        this.buffer.append("ALL ");
        allComparisonExpression.getSubSelect().accept((ExpressionVisitor)this);
    }
    
    @Override
    public void visit(final AnyComparisonExpression anyComparisonExpression) {
        this.buffer.append(anyComparisonExpression.getAnyType().name()).append(" ");
        anyComparisonExpression.getSubSelect().accept((ExpressionVisitor)this);
    }
    
    @Override
    public void visit(final Concat concat) {
        this.visitBinaryExpression(concat, " || ");
    }
    
    @Override
    public void visit(final Matches matches) {
        this.visitOldOracleJoinBinaryExpression(matches, " @@ ");
    }
    
    @Override
    public void visit(final BitwiseAnd bitwiseAnd) {
        this.visitBinaryExpression(bitwiseAnd, " & ");
    }
    
    @Override
    public void visit(final BitwiseOr bitwiseOr) {
        this.visitBinaryExpression(bitwiseOr, " | ");
    }
    
    @Override
    public void visit(final BitwiseXor bitwiseXor) {
        this.visitBinaryExpression(bitwiseXor, " ^ ");
    }
    
    @Override
    public void visit(final CastExpression cast) {
        if (cast.isUseCastKeyword()) {
            this.buffer.append("CAST(");
            this.buffer.append(cast.getLeftExpression());
            this.buffer.append(" AS ");
            this.buffer.append(cast.getType());
            this.buffer.append(")");
        }
        else {
            this.buffer.append(cast.getLeftExpression());
            this.buffer.append("::");
            this.buffer.append(cast.getType());
        }
    }
    
    @Override
    public void visit(final Modulo modulo) {
        this.visitBinaryExpression(modulo, " % ");
    }
    
    @Override
    public void visit(final AnalyticExpression aexpr) {
        final String name = aexpr.getName();
        final Expression expression = aexpr.getExpression();
        final Expression offset = aexpr.getOffset();
        final Expression defaultValue = aexpr.getDefaultValue();
        final boolean isAllColumns = aexpr.isAllColumns();
        final KeepExpression keep = aexpr.getKeep();
        final ExpressionList partitionExpressionList = aexpr.getPartitionExpressionList();
        final List<OrderByElement> orderByElements = aexpr.getOrderByElements();
        final WindowElement windowElement = aexpr.getWindowElement();
        this.buffer.append(name).append("(");
        if (expression != null) {
            expression.accept(this);
            if (offset != null) {
                this.buffer.append(", ");
                offset.accept(this);
                if (defaultValue != null) {
                    this.buffer.append(", ");
                    defaultValue.accept(this);
                }
            }
        }
        else if (isAllColumns) {
            this.buffer.append("*");
        }
        this.buffer.append(") ");
        if (keep != null) {
            keep.accept(this);
            this.buffer.append(" ");
        }
        this.buffer.append("OVER (");
        if (partitionExpressionList != null && !partitionExpressionList.getExpressions().isEmpty()) {
            this.buffer.append("PARTITION BY ");
            final List<Expression> expressions = partitionExpressionList.getExpressions();
            for (int i = 0; i < expressions.size(); ++i) {
                if (i > 0) {
                    this.buffer.append(", ");
                }
                expressions.get(i).accept(this);
            }
            this.buffer.append(" ");
        }
        if (orderByElements != null && !orderByElements.isEmpty()) {
            this.buffer.append("ORDER BY ");
            this.orderByDeParser.setExpressionVisitor(this);
            this.orderByDeParser.setBuffer(this.buffer);
            for (int j = 0; j < orderByElements.size(); ++j) {
                if (j > 0) {
                    this.buffer.append(", ");
                }
                this.orderByDeParser.deParseElement(orderByElements.get(j));
            }
            if (windowElement != null) {
                this.buffer.append(' ');
                this.buffer.append(windowElement);
            }
        }
        this.buffer.append(")");
    }
    
    @Override
    public void visit(final ExtractExpression eexpr) {
        this.buffer.append("EXTRACT(").append(eexpr.getName());
        this.buffer.append(" FROM ");
        eexpr.getExpression().accept(this);
        this.buffer.append(')');
    }
    
    @Override
    public void visit(final MultiExpressionList multiExprList) {
        final Iterator<ExpressionList> it = multiExprList.getExprList().iterator();
        while (it.hasNext()) {
            it.next().accept(this);
            if (it.hasNext()) {
                this.buffer.append(", ");
            }
        }
    }
    
    @Override
    public void visit(final IntervalExpression iexpr) {
        this.buffer.append(iexpr.toString());
    }
    
    @Override
    public void visit(final JdbcNamedParameter jdbcNamedParameter) {
        this.buffer.append(jdbcNamedParameter.toString());
    }
    
    @Override
    public void visit(final OracleHierarchicalExpression oexpr) {
        this.buffer.append(oexpr.toString());
    }
    
    @Override
    public void visit(final RegExpMatchOperator rexpr) {
        this.visitBinaryExpression(rexpr, " " + rexpr.getStringExpression() + " ");
    }
    
    @Override
    public void visit(final RegExpMySQLOperator rexpr) {
        this.visitBinaryExpression(rexpr, " " + rexpr.getStringExpression() + " ");
    }
    
    @Override
    public void visit(final JsonExpression jsonExpr) {
        this.buffer.append(jsonExpr.toString());
    }
    
    @Override
    public void visit(final JsonOperator jsonExpr) {
        this.visitBinaryExpression(jsonExpr, " " + jsonExpr.getStringExpression() + " ");
    }
    
    @Override
    public void visit(final WithinGroupExpression wgexpr) {
        this.buffer.append(wgexpr.toString());
    }
    
    @Override
    public void visit(final UserVariable var) {
        this.buffer.append(var.toString());
    }
    
    @Override
    public void visit(final NumericBind bind) {
        this.buffer.append(bind.toString());
    }
    
    @Override
    public void visit(final KeepExpression aexpr) {
        this.buffer.append(aexpr.toString());
    }
    
    @Override
    public void visit(final MySQLGroupConcat groupConcat) {
        this.buffer.append(groupConcat.toString());
    }
    
    @Override
    public void visit(final RowConstructor rowConstructor) {
        if (rowConstructor.getName() != null) {
            this.buffer.append(rowConstructor.getName());
        }
        this.buffer.append("(");
        boolean first = true;
        for (final Expression expr : rowConstructor.getExprList().getExpressions()) {
            if (first) {
                first = false;
            }
            else {
                this.buffer.append(", ");
            }
            expr.accept(this);
        }
        this.buffer.append(")");
    }
    
    @Override
    public void visit(final OracleHint hint) {
        this.buffer.append(hint.toString());
    }
    
    @Override
    public void visit(final TimeKeyExpression timeKeyExpression) {
        this.buffer.append(timeKeyExpression.toString());
    }
    
    @Override
    public void visit(final DateTimeLiteralExpression literal) {
        this.buffer.append(literal.toString());
    }
}
