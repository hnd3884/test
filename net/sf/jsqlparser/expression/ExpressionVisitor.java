package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.expression.operators.relational.JsonOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
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

public interface ExpressionVisitor
{
    void visit(final NullValue p0);
    
    void visit(final Function p0);
    
    void visit(final SignedExpression p0);
    
    void visit(final JdbcParameter p0);
    
    void visit(final JdbcNamedParameter p0);
    
    void visit(final DoubleValue p0);
    
    void visit(final LongValue p0);
    
    void visit(final HexValue p0);
    
    void visit(final DateValue p0);
    
    void visit(final TimeValue p0);
    
    void visit(final TimestampValue p0);
    
    void visit(final Parenthesis p0);
    
    void visit(final StringValue p0);
    
    void visit(final Addition p0);
    
    void visit(final Division p0);
    
    void visit(final Multiplication p0);
    
    void visit(final Subtraction p0);
    
    void visit(final AndExpression p0);
    
    void visit(final OrExpression p0);
    
    void visit(final Between p0);
    
    void visit(final EqualsTo p0);
    
    void visit(final GreaterThan p0);
    
    void visit(final GreaterThanEquals p0);
    
    void visit(final InExpression p0);
    
    void visit(final IsNullExpression p0);
    
    void visit(final LikeExpression p0);
    
    void visit(final MinorThan p0);
    
    void visit(final MinorThanEquals p0);
    
    void visit(final NotEqualsTo p0);
    
    void visit(final Column p0);
    
    void visit(final SubSelect p0);
    
    void visit(final CaseExpression p0);
    
    void visit(final WhenClause p0);
    
    void visit(final ExistsExpression p0);
    
    void visit(final AllComparisonExpression p0);
    
    void visit(final AnyComparisonExpression p0);
    
    void visit(final Concat p0);
    
    void visit(final Matches p0);
    
    void visit(final BitwiseAnd p0);
    
    void visit(final BitwiseOr p0);
    
    void visit(final BitwiseXor p0);
    
    void visit(final CastExpression p0);
    
    void visit(final Modulo p0);
    
    void visit(final AnalyticExpression p0);
    
    void visit(final WithinGroupExpression p0);
    
    void visit(final ExtractExpression p0);
    
    void visit(final IntervalExpression p0);
    
    void visit(final OracleHierarchicalExpression p0);
    
    void visit(final RegExpMatchOperator p0);
    
    void visit(final JsonExpression p0);
    
    void visit(final JsonOperator p0);
    
    void visit(final RegExpMySQLOperator p0);
    
    void visit(final UserVariable p0);
    
    void visit(final NumericBind p0);
    
    void visit(final KeepExpression p0);
    
    void visit(final MySQLGroupConcat p0);
    
    void visit(final RowConstructor p0);
    
    void visit(final OracleHint p0);
    
    void visit(final TimeKeyExpression p0);
    
    void visit(final DateTimeLiteralExpression p0);
    
    void visit(final NotExpression p0);
}
