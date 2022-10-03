package com.microsoft.sqlserver.jdbc;

import java.util.regex.Matcher;
import java.util.Stack;
import java.util.regex.Pattern;

final class JDBCSyntaxTranslator
{
    private String procedureName;
    private boolean hasReturnValueSyntax;
    private static final String sqlIdentifierPart = "(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))";
    private static final String sqlIdentifierWithoutGroups = "((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))(?:\\.(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))){0,3}?)";
    private static final String sqlIdentifierWithGroups = "((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?)))(?:\\.((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))))?";
    private static final Pattern jdbcCallSyntax;
    private static final Pattern sqlExecSyntax;
    private static final Pattern limitSyntaxWithOffset;
    private static final Pattern limitSyntaxGeneric;
    private static final Pattern selectPattern;
    private static final Pattern openQueryPattern;
    private static final Pattern openRowsetPattern;
    private static final Pattern limitOnlyPattern;
    
    JDBCSyntaxTranslator() {
        this.procedureName = null;
        this.hasReturnValueSyntax = false;
    }
    
    String getProcedureName() {
        return this.procedureName;
    }
    
    boolean hasReturnValueSyntax() {
        return this.hasReturnValueSyntax;
    }
    
    static String getSQLIdentifierWithGroups() {
        return "((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?)))(?:\\.((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))))?";
    }
    
    int translateLimit(final StringBuffer sql, int indx, final char endChar) throws SQLServerException {
        final Matcher selectMatcher = JDBCSyntaxTranslator.selectPattern.matcher(sql);
        final Matcher openQueryMatcher = JDBCSyntaxTranslator.openQueryPattern.matcher(sql);
        final Matcher openRowsetMatcher = JDBCSyntaxTranslator.openRowsetPattern.matcher(sql);
        final Matcher limitMatcher = JDBCSyntaxTranslator.limitOnlyPattern.matcher(sql);
        final Matcher offsetMatcher = JDBCSyntaxTranslator.limitSyntaxWithOffset.matcher(sql);
        final int startIndx = indx;
        final Stack<Integer> topPosition = new Stack<Integer>();
        State nextState = State.START;
        while (indx < sql.length()) {
            final char ch = sql.charAt(indx);
            switch (nextState) {
                case START: {
                    nextState = State.PROCESS;
                    continue;
                }
                case PROCESS: {
                    if (endChar == ch) {
                        nextState = State.END;
                        continue;
                    }
                    if ('\'' == ch) {
                        nextState = State.QUOTE;
                        continue;
                    }
                    if ('(' == ch) {
                        nextState = State.SUBQUERY;
                        continue;
                    }
                    if (limitMatcher.find(indx) && indx == limitMatcher.start()) {
                        nextState = State.LIMIT;
                        continue;
                    }
                    if (offsetMatcher.find(indx) && indx == offsetMatcher.start()) {
                        nextState = State.OFFSET;
                        continue;
                    }
                    if (openQueryMatcher.find(indx) && indx == openQueryMatcher.start()) {
                        nextState = State.OPENQUERY;
                        continue;
                    }
                    if (openRowsetMatcher.find(indx) && indx == openRowsetMatcher.start()) {
                        nextState = State.OPENROWSET;
                        continue;
                    }
                    if (selectMatcher.find(indx) && indx == selectMatcher.start()) {
                        nextState = State.SELECT;
                        continue;
                    }
                    ++indx;
                    continue;
                }
                case OFFSET: {
                    throw new SQLServerException(SQLServerException.getErrString("R_limitOffsetNotSupported"), null, 0, null);
                }
                case LIMIT: {
                    int openingParentheses = 0;
                    int closingParentheses = 0;
                    int pos = -1;
                    final String openingStr = limitMatcher.group(2);
                    final String closingStr = limitMatcher.group(5);
                    while (-1 != (pos = openingStr.indexOf(40, pos + 1))) {
                        ++openingParentheses;
                    }
                    pos = -1;
                    while (-1 != (pos = closingStr.indexOf(41, pos + 1))) {
                        ++closingParentheses;
                    }
                    if (openingParentheses != closingParentheses) {
                        throw new SQLServerException(SQLServerException.getErrString("R_limitEscapeSyntaxError"), null, 0, null);
                    }
                    if (!topPosition.empty()) {
                        final Integer top = topPosition.pop();
                        final String rows = limitMatcher.group(1);
                        sql.delete(limitMatcher.start() - 1, limitMatcher.end());
                        if ('?' == rows.charAt(0)) {
                            sql.insert(top, " TOP (" + rows + ")");
                            indx += 7 + rows.length() - 1;
                        }
                        else {
                            sql.insert(top, " TOP " + rows);
                            indx += 5 + rows.length() - 1;
                        }
                    }
                    else {
                        indx = limitMatcher.end() - 1;
                    }
                    nextState = State.PROCESS;
                    continue;
                }
                case SELECT: {
                    indx = selectMatcher.end(1);
                    topPosition.push(indx);
                    nextState = State.PROCESS;
                    continue;
                }
                case QUOTE: {
                    ++indx;
                    if (sql.length() <= indx || '\'' != sql.charAt(indx)) {
                        nextState = State.QUOTE;
                        continue;
                    }
                    ++indx;
                    if (sql.length() > indx && '\'' == sql.charAt(indx)) {
                        nextState = State.QUOTE;
                        continue;
                    }
                    nextState = State.PROCESS;
                    continue;
                }
                case SUBQUERY: {
                    indx = ++indx + this.translateLimit(sql, indx, ')');
                    nextState = State.PROCESS;
                    continue;
                }
                case OPENQUERY: {
                    indx = openQueryMatcher.start(1);
                    indx += this.translateLimit(sql, indx, '\'');
                    nextState = State.PROCESS;
                    continue;
                }
                case OPENROWSET: {
                    indx = openRowsetMatcher.start(1);
                    indx += this.translateLimit(sql, indx, '\'');
                    nextState = State.PROCESS;
                    continue;
                }
                case END: {
                    return ++indx - startIndx;
                }
            }
        }
        return indx - startIndx;
    }
    
    String translate(String sql) throws SQLServerException {
        Matcher matcher = JDBCSyntaxTranslator.jdbcCallSyntax.matcher(sql);
        if (matcher.matches()) {
            this.hasReturnValueSyntax = (null != matcher.group(1));
            this.procedureName = matcher.group(2);
            final String args = matcher.group(3);
            sql = "EXEC " + (this.hasReturnValueSyntax ? "? = " : "") + this.procedureName + ((null != args) ? (" " + args) : "");
        }
        else {
            matcher = JDBCSyntaxTranslator.sqlExecSyntax.matcher(sql);
            if (matcher.matches()) {
                this.hasReturnValueSyntax = (null != matcher.group(1));
                this.procedureName = matcher.group(3);
            }
        }
        matcher = JDBCSyntaxTranslator.limitSyntaxGeneric.matcher(sql);
        if (matcher.find()) {
            final StringBuffer sqlbuf = new StringBuffer(sql);
            this.translateLimit(sqlbuf, 0, '\0');
            return sqlbuf.toString();
        }
        return sql;
    }
    
    static {
        jdbcCallSyntax = Pattern.compile("(?s)\\s*?\\{\\s*?(\\?\\s*?=)?\\s*?[cC][aA][lL][lL]\\s+?((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))(?:\\.(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))){0,3}?)(?:\\s*?\\((.*)\\))?\\s*\\}.*+");
        sqlExecSyntax = Pattern.compile("\\s*?[eE][xX][eE][cC](?:[uU][tT][eE])??\\s+?(((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))(?:\\.(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))){0,3}?)\\s*?=\\s+?)??((?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))(?:\\.(?:(?:\\[(?:[^\\]]|(?:\\]\\]))+?\\])|(?:\"(?:[^\"]|(?:\"\"))+?\")|(?:\\S+?))){0,3}?)(?:$|(?:\\s+?.*+))");
        limitSyntaxWithOffset = Pattern.compile("\\{\\s*[lL][iI][mM][iI][tT]\\s+(.*)\\s+[oO][fF][fF][sS][eE][tT]\\s+(.*)\\}");
        limitSyntaxGeneric = Pattern.compile("\\{\\s*[lL][iI][mM][iI][tT]\\s+(.*)(\\s+[oO][fF][fF][sS][eE][tT](.*)\\}|\\s*\\})");
        selectPattern = Pattern.compile("([sS][eE][lL][eE][cC][tT])\\s+");
        openQueryPattern = Pattern.compile("[oO][pP][eE][nN][qQ][uU][eE][rR][yY]\\s*\\(.*,\\s*'(.*)'\\s*\\)");
        openRowsetPattern = Pattern.compile("[oO][pP][eE][nN][rR][oO][wW][sS][eE][tT]\\s*\\(.*,.*,\\s*'(.*)'\\s*\\)");
        limitOnlyPattern = Pattern.compile("\\{\\s*[lL][iI][mM][iI][tT]\\s+(((\\(|\\s)*)(\\d*|\\?)((\\)|\\s)*))\\s*\\}");
    }
    
    enum State
    {
        START, 
        END, 
        SUBQUERY, 
        SELECT, 
        OPENQUERY, 
        OPENROWSET, 
        LIMIT, 
        OFFSET, 
        QUOTE, 
        PROCESS;
    }
}
