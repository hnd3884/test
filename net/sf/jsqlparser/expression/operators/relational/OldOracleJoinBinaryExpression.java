package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.BinaryExpression;

public abstract class OldOracleJoinBinaryExpression extends BinaryExpression implements SupportsOldOracleJoinSyntax
{
    private int oldOracleJoinSyntax;
    private int oraclePriorPosition;
    
    public OldOracleJoinBinaryExpression() {
        this.oldOracleJoinSyntax = 0;
        this.oraclePriorPosition = 0;
    }
    
    @Override
    public void setOldOracleJoinSyntax(final int oldOracleJoinSyntax) {
        this.oldOracleJoinSyntax = oldOracleJoinSyntax;
        if (oldOracleJoinSyntax < 0 || oldOracleJoinSyntax > 2) {
            throw new IllegalArgumentException("unknown join type for oracle found (type=" + oldOracleJoinSyntax + ")");
        }
    }
    
    @Override
    public String toString() {
        return (this.isNot() ? "NOT " : "") + ((this.oraclePriorPosition == 1) ? "PRIOR " : "") + this.getLeftExpression() + ((this.oldOracleJoinSyntax == 1) ? "(+)" : "") + " " + this.getStringExpression() + " " + ((this.oraclePriorPosition == 2) ? "PRIOR " : "") + this.getRightExpression() + ((this.oldOracleJoinSyntax == 2) ? "(+)" : "");
    }
    
    @Override
    public int getOldOracleJoinSyntax() {
        return this.oldOracleJoinSyntax;
    }
    
    @Override
    public int getOraclePriorPosition() {
        return this.oraclePriorPosition;
    }
    
    @Override
    public void setOraclePriorPosition(final int oraclePriorPosition) {
        this.oraclePriorPosition = oraclePriorPosition;
    }
}
