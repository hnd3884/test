package net.sf.jsqlparser.parser;

public interface CCJSqlParserTreeConstants
{
    public static final int JJTSTATEMENT = 0;
    public static final int JJTVOID = 1;
    public static final int JJTSTATEMENTS = 2;
    public static final int JJTCOLUMN = 3;
    public static final int JJTTABLE = 4;
    public static final int JJTWITHITEM = 5;
    public static final int JJTEXPRESSION = 6;
    public static final int JJTCASEWHENEXPRESSION = 7;
    public static final int JJTFUNCTION = 8;
    public static final int JJTSUBSELECT = 9;
    public static final String[] jjtNodeName = { "Statement", "void", "Statements", "Column", "Table", "WithItem", "Expression", "CaseWhenExpression", "Function", "SubSelect" };
}
