package com.adventnet.ds.query;

public interface QueryConstants
{
    public static final int EQUAL = 0;
    public static final int NOT_EQUAL = 1;
    public static final int LIKE = 2;
    public static final int NOT_LIKE = 3;
    public static final int GREATER_EQUAL = 4;
    public static final int GREATER_THAN = 5;
    public static final int LESS_EQUAL = 6;
    public static final int LESS_THAN = 7;
    public static final int IN = 8;
    public static final int NOT_IN = 9;
    public static final int STARTS_WITH = 10;
    public static final int ENDS_WITH = 11;
    public static final int CONTAINS = 12;
    public static final int NOT_CONTAINS = 13;
    public static final int BETWEEN = 14;
    public static final int NOT_BETWEEN = 15;
    public static final PreparedStmtConstRepr PREPARED_STMT_CONST = PreparedStmtConstRepr.getInstance();
    
    public static class PreparedStmtConstRepr
    {
        private static PreparedStmtConstRepr psInst;
        
        static PreparedStmtConstRepr getInstance() {
            if (PreparedStmtConstRepr.psInst == null) {
                PreparedStmtConstRepr.psInst = new PreparedStmtConstRepr();
            }
            return PreparedStmtConstRepr.psInst;
        }
        
        @Override
        public String toString() {
            return "?";
        }
        
        static {
            PreparedStmtConstRepr.psInst = null;
        }
    }
}
