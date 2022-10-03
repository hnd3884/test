package com.microsoft.sqlserver.jdbc;

import java.io.InputStream;
import org.antlr.v4.runtime.ANTLRErrorListener;
import java.io.IOException;
import org.antlr.v4.runtime.CharStreams;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import org.antlr.v4.runtime.Token;
import java.util.ArrayList;

class SQLServerFMTQuery
{
    private static final String FMT_ON = "SET FMTONLY ON;";
    private static final String SELECT = "SELECT ";
    private static final String FROM = " FROM ";
    private static final String FMT_OFF = ";SET FMTONLY OFF;";
    private String prefix;
    private ArrayList<? extends Token> tokenList;
    private List<String> userColumns;
    private List<String> tableTarget;
    private List<String> possibleAliases;
    private List<List<String>> valuesList;
    
    List<String> getColumns() {
        return this.userColumns;
    }
    
    List<String> getTableTarget() {
        return this.tableTarget;
    }
    
    List<List<String>> getValuesList() {
        return this.valuesList;
    }
    
    List<String> getAliases() {
        return this.possibleAliases;
    }
    
    String constructColumnTargets() {
        if (this.userColumns.contains("?")) {
            return this.userColumns.stream().filter(s -> !"?".equals(s)).map(s -> "".equals(s) ? "NULL" : s).collect((Collector<? super Object, ?, String>)Collectors.joining(","));
        }
        return this.userColumns.isEmpty() ? "*" : this.userColumns.stream().map(s -> "".equals(s) ? "NULL" : s).collect((Collector<? super Object, ?, String>)Collectors.joining(","));
    }
    
    String constructTableTargets() {
        return this.tableTarget.stream().distinct().filter(s -> !this.possibleAliases.contains(s)).collect((Collector<? super Object, ?, String>)Collectors.joining(","));
    }
    
    String getFMTQuery() {
        final StringBuilder sb = new StringBuilder("SET FMTONLY ON;");
        if (!"".equals(this.prefix)) {
            sb.append(this.prefix);
        }
        sb.append("SELECT ");
        sb.append(this.constructColumnTargets());
        if (!this.tableTarget.isEmpty()) {
            sb.append(" FROM ");
            sb.append(this.constructTableTargets());
        }
        sb.append(";SET FMTONLY OFF;");
        return sb.toString();
    }
    
    private SQLServerFMTQuery() {
        this.prefix = "";
        this.tokenList = null;
        this.userColumns = new ArrayList<String>();
        this.tableTarget = new ArrayList<String>();
        this.possibleAliases = new ArrayList<String>();
        this.valuesList = new ArrayList<List<String>>();
    }
    
    SQLServerFMTQuery(final String userSql) throws SQLServerException {
        this.prefix = "";
        this.tokenList = null;
        this.userColumns = new ArrayList<String>();
        this.tableTarget = new ArrayList<String>();
        this.possibleAliases = new ArrayList<String>();
        this.valuesList = new ArrayList<List<String>>();
        if (null != userSql && 0 != userSql.length()) {
            final InputStream stream = new ByteArrayInputStream(userSql.getBytes(StandardCharsets.UTF_8));
            SQLServerLexer lexer = null;
            try {
                lexer = new SQLServerLexer(CharStreams.fromStream(stream));
            }
            catch (final IOException e) {
                SQLServerException.makeFromDriverError(null, userSql, e.getLocalizedMessage(), null, false);
            }
            if (null != lexer) {
                lexer.removeErrorListeners();
                lexer.addErrorListener((ANTLRErrorListener)new SQLServerErrorListener());
                this.tokenList = (ArrayList)lexer.getAllTokens();
                if (this.tokenList.size() <= 0) {
                    SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_noTokensFoundInUserQuery"), null, false);
                }
                final SQLServerTokenIterator iter = new SQLServerTokenIterator(this.tokenList);
                this.prefix = SQLServerParser.getCTE(iter);
                SQLServerParser.parseQuery(iter, this);
            }
            else {
                SQLServerException.makeFromDriverError(null, userSql, SQLServerResource.getResource("R_noTokensFoundInUserQuery"), null, false);
            }
        }
        else {
            SQLServerException.makeFromDriverError(null, this, SQLServerResource.getResource("R_noTokensFoundInUserQuery"), null, false);
        }
    }
}
