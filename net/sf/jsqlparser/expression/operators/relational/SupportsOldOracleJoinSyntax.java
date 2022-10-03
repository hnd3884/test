package net.sf.jsqlparser.expression.operators.relational;

public interface SupportsOldOracleJoinSyntax
{
    public static final int NO_ORACLE_JOIN = 0;
    public static final int ORACLE_JOIN_RIGHT = 1;
    public static final int ORACLE_JOIN_LEFT = 2;
    public static final int NO_ORACLE_PRIOR = 0;
    public static final int ORACLE_PRIOR_START = 1;
    public static final int ORACLE_PRIOR_END = 2;
    
    int getOldOracleJoinSyntax();
    
    void setOldOracleJoinSyntax(final int p0);
    
    int getOraclePriorPosition();
    
    void setOraclePriorPosition(final int p0);
}
