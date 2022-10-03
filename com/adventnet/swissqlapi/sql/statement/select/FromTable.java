package com.adventnet.swissqlapi.sql.statement.select;

import java.util.List;
import com.adventnet.swissqlapi.sql.statement.SwisSQLStatement;
import com.adventnet.swissqlapi.util.database.MetadataInfoUtil;
import com.adventnet.swissqlapi.sql.statement.insert.ValuesClause;
import com.adventnet.swissqlapi.sql.statement.ModifiedObjectAttr;
import com.adventnet.swissqlapi.util.misc.CustomizeUtil;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.config.SwisSQLOptions;
import com.adventnet.swissqlapi.SwisSQLAPI;
import java.util.StringTokenizer;
import com.adventnet.swissqlapi.util.misc.StringFunctions;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.parser.Token;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import java.util.ArrayList;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class FromTable
{
    protected Object tableName;
    private String aliasName;
    private String joinClause;
    private String tableKeyword;
    private Vector joinExpression;
    private Vector UsingList;
    private String onOrUsingJoin;
    private String outer;
    private String outerOpenBrace;
    private String outerClosedBrace;
    private boolean isAS;
    private SetOperatorClause setOperatorClauseForFullJoin;
    private String updateLock;
    private String holdLock;
    private UserObjectContext context;
    private boolean isTenroxRequirement;
    private String fromClauseOpenBraces;
    private String fromClauseClosedBraces;
    private FromClause fc;
    private String lock;
    private String with;
    private String lockTableStatement;
    private String indexHint;
    private String origTableName;
    private ArrayList setOperatorClauseListForSubQuery;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    private QueryPartitionClause queryPartitionClause;
    private FromTable crossJoinForPartitionClause;
    private SelectQueryStatement crossJoinSelectQuery;
    private WhereExpression crossJoinExpression;
    private ArrayList columnAliasList;
    private ArrayList rowValuesList;
    
    public FromTable() {
        this.context = null;
        this.isTenroxRequirement = false;
        this.setOperatorClauseListForSubQuery = new ArrayList();
        this.queryPartitionClause = null;
        this.crossJoinForPartitionClause = null;
        this.crossJoinSelectQuery = null;
        this.crossJoinExpression = null;
        this.rowValuesList = new ArrayList();
    }
    
    public void setObjectContext(final UserObjectContext context) {
        this.context = context;
    }
    
    public void setIndexHint(final String s) {
        this.indexHint = s;
    }
    
    public void setSetOperatorClause(final SetOperatorClause setOperatorClauseForFullJoin) {
        this.setOperatorClauseForFullJoin = setOperatorClauseForFullJoin;
    }
    
    public void setTableName(final Object tn) {
        this.tableName = tn;
    }
    
    public void setAliasName(final String an) {
        this.aliasName = an;
    }
    
    public void setIsAS(final boolean is) {
        this.isAS = is;
    }
    
    public void setJoinClause(final String jc) {
        this.joinClause = jc;
    }
    
    public void setOnOrUsingJoin(final String s_onou) {
        this.onOrUsingJoin = s_onou;
    }
    
    public void setJoinExpression(final Vector v_je) {
        this.joinExpression = v_je;
    }
    
    public void setUsingList(final Vector v_ul) {
        this.UsingList = v_ul;
    }
    
    public void setLockTableStatement(final String lockTableStatement) {
        this.lockTableStatement = lockTableStatement;
    }
    
    public void setWith(final String with) {
        this.with = with;
    }
    
    public void setLock(final String lock) {
        this.lock = lock;
    }
    
    public void setTableKeyword(final String s_tk) {
        this.tableKeyword = s_tk;
    }
    
    public void setOuter(final String outer) {
        this.outer = outer;
    }
    
    public void setOuterOpenBrace(final String outerOpenBrace) {
        this.outerOpenBrace = outerOpenBrace;
    }
    
    public void setOuterClosedBrace(final String outerClosedBrace) {
        this.outerClosedBrace = outerClosedBrace;
    }
    
    public void setUpdateLock(final String updateLock) {
        this.updateLock = updateLock;
    }
    
    public void setHoldLock(final String lock) {
        this.holdLock = lock;
    }
    
    public void setFromClauseOpenBraces(final String fromClauseOpenBraces) {
        this.fromClauseOpenBraces = fromClauseOpenBraces;
    }
    
    public void setFromClauseClosedBraces(final String fromClauseClosedBraces) {
        this.fromClauseClosedBraces = fromClauseClosedBraces;
    }
    
    public void setFromClause(final FromClause fc) {
        this.fc = fc;
    }
    
    public void setColumnAliasList(final ArrayList columnAliasList) {
        this.columnAliasList = columnAliasList;
    }
    
    public void setOrigTableName(final String origTableName) {
        this.origTableName = origTableName;
    }
    
    public void setSetOperatorClauseListForSubQuery(final ArrayList socList) {
        this.setOperatorClauseListForSubQuery = socList;
    }
    
    public void setQueryPartitionClause(final QueryPartitionClause qpc) {
        this.queryPartitionClause = qpc;
    }
    
    public void setCrossJoinForPartitionClause(final FromTable crossJoinForPartitionClause) {
        this.crossJoinForPartitionClause = crossJoinForPartitionClause;
    }
    
    public void setCrossJoinSelectQuery(final SelectQueryStatement crossJoinSelectQuery) {
        this.crossJoinSelectQuery = crossJoinSelectQuery;
    }
    
    public void setCrossJoinExpression(final WhereExpression crossJoinExpression) {
        this.crossJoinExpression = crossJoinExpression;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass commentObj) {
        this.commentObjAfterToken = commentObj;
    }
    
    public Object getTableName() {
        return this.tableName;
    }
    
    public String getOrigTableName() {
        return this.origTableName;
    }
    
    public String getLockTableStatement() {
        return this.lockTableStatement;
    }
    
    public String getWith() {
        return this.with;
    }
    
    public String getLock() {
        return this.lock;
    }
    
    public String getTableKeyword() {
        return this.tableKeyword;
    }
    
    public String getAliasName() {
        return this.aliasName;
    }
    
    public boolean getIsAS() {
        return this.isAS;
    }
    
    public String getJoinClause() {
        return this.joinClause;
    }
    
    public Vector getJoinExpression() {
        return this.joinExpression;
    }
    
    public String getOuter() {
        return this.outer;
    }
    
    public String getOuterOpenBrace() {
        return this.outerOpenBrace;
    }
    
    public String getOuterClosedBrace() {
        return this.outerClosedBrace;
    }
    
    public String getFromClauseOpenBraces() {
        return this.fromClauseOpenBraces;
    }
    
    public String getUpdateLock() {
        return this.updateLock;
    }
    
    public String getFromClauseClosedBraces() {
        return this.fromClauseClosedBraces;
    }
    
    public FromClause getFromClause() {
        return this.fc;
    }
    
    public String getOnOrUsingJoin() {
        return this.onOrUsingJoin;
    }
    
    public Vector getUsingList() {
        return this.UsingList;
    }
    
    public ArrayList getSetOperatorClauseListForSubQuery() {
        return this.setOperatorClauseListForSubQuery;
    }
    
    public QueryPartitionClause getQueryPartitionClause() {
        return this.queryPartitionClause;
    }
    
    public FromTable getCrossJoinForPartitionClause() {
        return this.crossJoinForPartitionClause;
    }
    
    public SelectQueryStatement getCrossJoinSelectQuery() {
        return this.crossJoinSelectQuery;
    }
    
    public WhereExpression getCrossJoinExpression() {
        return this.crossJoinExpression;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public CommentClass getCommentClassAfterToken() {
        return this.commentObj;
    }
    
    public ArrayList getColumnAliasList() {
        return this.columnAliasList;
    }
    
    public void addCommentClassAfterToken(Token commentObj) {
        if (this.commentObjAfterToken != null) {
            final ArrayList specialTokenList = this.commentObjAfterToken.getSpecialToken();
            final int lastIndex = specialTokenList.size();
            while (commentObj.specialToken != null) {
                specialTokenList.add(lastIndex, commentObj.specialToken.image);
                commentObj = commentObj.specialToken;
            }
        }
        else if (commentObj != null && commentObj.specialToken != null) {
            final ArrayList<String> specialTokenList2 = new ArrayList<String>();
            while (commentObj.specialToken != null) {
                specialTokenList2.add(0, commentObj.specialToken.image);
                commentObj = commentObj.specialToken;
            }
            final CommentClass commentObjToBeInserted = new CommentClass();
            commentObjToBeInserted.setSpecialToken(specialTokenList2);
            this.commentObjAfterToken = commentObjToBeInserted;
        }
    }
    
    public FromTable convert(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs, final int database) throws ConvertException {
        if (database == 8) {
            return this.toANSISelect(to_sqs, from_sqs);
        }
        if (database == 3) {
            return this.toDB2Select(to_sqs, from_sqs);
        }
        if (database == 2) {
            return this.toMSSQLServerSelect(to_sqs, from_sqs);
        }
        if (database == 7) {
            return this.toSybaseSelect(to_sqs, from_sqs);
        }
        if (database == 5) {
            return this.toMySQLSelect(to_sqs, from_sqs);
        }
        if (database == 4) {
            return this.toPostgreSQLSelect(to_sqs, from_sqs);
        }
        if (database == 6) {
            return this.toInformixSelect(to_sqs, from_sqs);
        }
        if (database == 1) {
            return this.toOracleSelect(to_sqs, from_sqs);
        }
        if (database == 11) {
            return this.toNetezzaSelect(to_sqs, from_sqs);
        }
        if (database == 12) {
            return this.toTeradataSelect(to_sqs, from_sqs);
        }
        if (database == 13) {
            return this.toVectorWiseSelect(to_sqs, from_sqs);
        }
        return null;
    }
    
    public FromTable toMySQLSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        boolean isFullJoin = false;
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
                throw new ConvertException("Conversion failure..Natural join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
                throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural right join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                throw new ConvertException("Conversion failure..Key join is not supported");
            }
            if (this.joinClause.trim().equalsIgnoreCase("OUTER")) {
                ft.setJoinClause("OUTER JOIN");
            }
            else if (this.joinClause.trim().equalsIgnoreCase("FULL JOIN") || this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
                final FromClause fc = new FromClause();
                final FromTable newFromTable = new FromTable();
                newFromTable.setAliasName(this.aliasName);
                newFromTable.setIsAS(this.isAS);
                newFromTable.setJoinClause("RIGHT OUTER JOIN");
                newFromTable.setJoinExpression(this.joinExpression);
                newFromTable.setOnOrUsingJoin(this.onOrUsingJoin);
                newFromTable.setTableKeyword(this.tableKeyword);
                newFromTable.setTableName(this.tableName);
                newFromTable.setUsingList(this.UsingList);
                if (vendorSQS.getFromClause() != null) {
                    fc.setFromClause("FROM");
                    final Vector fromList = vendorSQS.getFromClause().getFromItemList();
                    final Vector newFromList = new Vector();
                    for (int i = 0; i < fromList.size(); ++i) {
                        if (fromList.get(i) instanceof FromTable) {
                            final FromTable getFT = fromList.get(i);
                            if (getFT != null && getFT.equals(this)) {
                                newFromList.add(newFromTable);
                            }
                            else if (fromList.get(i) instanceof FromTable) {
                                newFromList.add(fromList.get(i).toMySQLSelect(vembuSQS, vendorSQS));
                            }
                        }
                        else if (fromList.get(i) instanceof FromClause) {
                            fromList.get(i).toMySQLSelect(vembuSQS, vendorSQS);
                        }
                    }
                    fc.setFromItemList(newFromList);
                }
                final SelectQueryStatement sqs = new SelectQueryStatement();
                sqs.setFromClause(fc);
                if (vendorSQS.getSelectStatement() != null) {
                    sqs.setSelectStatement(vendorSQS.getSelectStatement().toMySQLSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getFetchClause() != null) {
                    sqs.setFetchClause(vendorSQS.getFetchClause().toMySQLSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getForUpdateStatement() != null) {
                    sqs.setForUpdateStatement(null);
                }
                if (vendorSQS.getGroupByStatement() != null) {
                    sqs.setGroupByStatement(vendorSQS.getGroupByStatement().toMySQLSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getHavingStatement() != null) {
                    sqs.setHavingStatement(vendorSQS.getHavingStatement().toMySQLSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getHierarchicalQueryClause() != null) {
                    sqs.setHierarchicalQueryClause(null);
                }
                if (vendorSQS.getLimitClause() != null) {
                    sqs.setLimitClause(vendorSQS.getLimitClause().toMySQLSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getSetOperatorClause() != null) {
                    sqs.setSetOperatorClause(null);
                }
                if (vendorSQS.getWhereExpression() != null) {
                    sqs.setWhereExpression(vendorSQS.getWhereExpression().toMySQLSelect(vembuSQS, vendorSQS));
                }
                (this.setOperatorClauseForFullJoin = new SetOperatorClause()).setSelectQueryStatement(sqs);
                this.setOperatorClauseForFullJoin.setSetClause("UNION");
                if (vendorSQS.getWhereExpression() != null) {
                    this.setOperatorClauseForFullJoin.setWhereExpression(vendorSQS.getWhereExpression().toMySQLSelect(vembuSQS, vendorSQS));
                    vendorSQS.setWhereExpression(null);
                }
                ft.setSetOperatorClause(this.setOperatorClauseForFullJoin);
                isFullJoin = true;
            }
            if (this.onOrUsingJoin == null && this.joinExpression != null) {
                ft.setOnOrUsingJoin("ON");
                final WhereExpression we = this.joinExpression.elementAt(0).toMySQLSelect(vembuSQS, vendorSQS);
                final Vector v = new Vector();
                v.addElement(we);
                ft.setJoinExpression(v);
            }
            else if (this.joinExpression != null) {
                ft.setOnOrUsingJoin(this.onOrUsingJoin);
                WhereExpression we = this.joinExpression.elementAt(0);
                final Vector operatorList = we.getOperator();
                we.setOperator(operatorList);
                we = we.toMySQLSelect(vembuSQS, vendorSQS);
                final Vector v2 = new Vector();
                v2.addElement(we);
                ft.setJoinExpression(v2);
            }
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            if (this.UsingList != null) {
                final Vector v3 = new Vector();
                for (int j = 0; j < this.UsingList.size(); ++j) {
                    if (this.UsingList.elementAt(j) instanceof TableColumn) {
                        v3.addElement(this.UsingList.elementAt(j).toMySQLSelect(vembuSQS, vendorSQS));
                    }
                    else {
                        v3.addElement(this.UsingList.elementAt(j));
                    }
                }
                ft.setUsingList(v3);
            }
            if (!isFullJoin) {
                ft.setJoinClause(this.joinClause);
            }
            else {
                ft.setJoinClause("LEFT OUTER JOIN");
            }
            if (this.joinClause.trim().equalsIgnoreCase("JOIN")) {
                ft.setJoinClause("INNER JOIN");
            }
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ((SelectQueryStatement)this.tableName).setCanHandleFunctionArgumentsCountMismatch(vendorSQS.getCanHandleFunctionArugmentsCountMismatch());
                ((SelectQueryStatement)this.tableName).setValidationHandler(vendorSQS.getValidationHandler());
                ((SelectQueryStatement)this.tableName).setcanAllowLogicalExpInAggFun(vendorSQS.getcanAllowLogicalExpInAggFun());
                ((SelectQueryStatement)this.tableName).setCanAllowBackTipInColumnName(vendorSQS.getCanAllowBackTipInColumnName());
                ((SelectQueryStatement)this.tableName).setCanReplaceDoubleDotsInTableName(vendorSQS.getCanReplaceDoubleDotsInTableName());
                ft.setTableName(((SelectQueryStatement)this.tableName).toMySQLSelect());
                if (this.aliasName == null) {
                    ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
                }
            }
            else {
                if (this.tableName instanceof FunctionCalls) {
                    throw new ConvertException();
                }
                if (this.tableName instanceof WithStatement) {
                    ft.setTableName(((WithStatement)this.tableName).toMySQL());
                }
                else if (this.tableName instanceof FromClause) {
                    ft.setTableName(((FromClause)this.tableName).toMySQLSelect(vembuSQS, vendorSQS));
                }
                else {
                    if (vendorSQS != null && vendorSQS.getCanReplaceDoubleDotsInTableName()) {
                        final String table_Name_String = (String)this.tableName;
                        final int atIndex = table_Name_String.indexOf("@");
                        ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
                        final Vector tokenVector = new Vector();
                        final String table_Name = (String)this.tableName;
                        final StringTokenizer st = new StringTokenizer(table_Name, ".");
                        int count = 0;
                        while (st.hasMoreTokens()) {
                            tokenVector.add(st.nextToken());
                            ++count;
                        }
                        if (count == 1 && atIndex != -1) {
                            final String dataBaseTableName = tokenVector.elementAt(0);
                            final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                            final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                            final String sqlDataBaseAndTableName = sqlDataBaseName + "." + sqlTableName;
                            ft.setTableName(sqlDataBaseAndTableName);
                        }
                        else if (count == 2 && atIndex != -1) {
                            final String dataBaseTableName = tokenVector.elementAt(1);
                            final int tableAtIndex = dataBaseTableName.indexOf("@");
                            final String sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                            final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                            final String sqlDataBaseAndTableName2 = sqlDataBaseName2 + "." + tokenVector.elementAt(0) + "." + sqlTableName2;
                            ft.setTableName(sqlDataBaseAndTableName2);
                        }
                    }
                    else {
                        ft.setTableName(this.tableName);
                    }
                    String tempTableName = (String)ft.getTableName();
                    if ((tempTableName.startsWith("\"") && tempTableName.endsWith("\"")) || (tempTableName.startsWith("[") && tempTableName.endsWith("]"))) {
                        tempTableName = tempTableName.substring(1, tempTableName.length() - 1);
                        tempTableName = "`" + tempTableName + "`";
                        ft.setTableName(tempTableName);
                    }
                }
            }
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', ' ').trim());
            }
            else if (this.aliasName.charAt(0) == '\"') {
                ft.setAliasName(this.aliasName.replace('\"', ' ').trim());
            }
            else {
                ft.setAliasName(this.aliasName);
            }
        }
        return ft;
    }
    
    public FromTable toPostgreSQLSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(null);
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                throw new ConvertException("Conversion failure..Key join is not supported");
            }
            if (this.onOrUsingJoin == null && this.joinExpression != null) {
                ft.setOnOrUsingJoin("ON");
                final WhereExpression we = this.joinExpression.elementAt(0).toPostgreSQLSelect(vembuSQS, vendorSQS);
                final Vector v = new Vector();
                v.addElement(we);
                ft.setJoinExpression(v);
            }
            else if (this.joinExpression != null) {
                ft.setOnOrUsingJoin(this.onOrUsingJoin);
                WhereExpression we = this.joinExpression.elementAt(0);
                final Vector operatorList = we.getOperator();
                for (int i = 0; i < operatorList.size(); ++i) {
                    if (operatorList.elementAt(i) instanceof String && operatorList.get(i).equalsIgnoreCase("AND")) {
                        operatorList.setElementAt("AND", i);
                    }
                }
                we.setOperator(operatorList);
                we = we.toPostgreSQLSelect(vembuSQS, vendorSQS);
                final Vector v2 = new Vector();
                v2.addElement(we);
                ft.setJoinExpression(v2);
            }
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            if (this.UsingList != null) {
                final Vector v3 = new Vector();
                for (int j = 0; j < this.UsingList.size(); ++j) {
                    if (this.UsingList.elementAt(j) instanceof TableColumn) {
                        v3.addElement(this.UsingList.elementAt(j).toPostgreSQLSelect(vembuSQS, vendorSQS));
                    }
                    else {
                        v3.addElement(this.UsingList.elementAt(j));
                    }
                }
                ft.setUsingList(v3);
            }
            this.removeJoinExpressionForCrossJoin(ft);
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                if (!this.setOperatorClauseListForSubQuery.isEmpty()) {
                    this.buildSetOperatorClauseForSubQuery((SelectQueryStatement)this.tableName);
                }
                ((SelectQueryStatement)this.tableName).setReportsMeta(vendorSQS != null && vendorSQS.getReportsMeta());
                ((SelectQueryStatement)this.tableName).setAmazonRedShiftFlag(vendorSQS.isAmazonRedShift());
                ((SelectQueryStatement)this.tableName).setMSAzureFlag(vendorSQS.isMSAzure());
                ((SelectQueryStatement)this.tableName).setOracleLiveFlag(vendorSQS.isOracleLive());
                ((SelectQueryStatement)this.tableName).setCanUseIFFunctionForPGCaseWhenExp(vendorSQS.canUseIFFunctionForPGCaseWhenExp());
                ((SelectQueryStatement)this.tableName).setCanUseUDFFunctionsForText(vendorSQS.canUseUDFFunctionsForText());
                ((SelectQueryStatement)this.tableName).setCanUseUDFFunctionsForNumeric(vendorSQS.canUseUDFFunctionsForNumeric());
                ((SelectQueryStatement)this.tableName).setCanUseUDFFunctionsForDateTime(vendorSQS.canUseUDFFunctionsForDateTime());
                ((SelectQueryStatement)this.tableName).setCanHandleStringLiteralsForNumeric(vendorSQS.canHandleStringLiteralsForNumeric());
                ((SelectQueryStatement)this.tableName).setCanHandleStringLiteralsForDateTime(vendorSQS.canHandleStringLiteralsForDateTime());
                ((SelectQueryStatement)this.tableName).setCanHandleNullsInsideINClause(vendorSQS.canHandleNullsInsideINClause());
                ((SelectQueryStatement)this.tableName).setCanCastStringLiteralToText(vendorSQS.canCastStringLiteralToText());
                ((SelectQueryStatement)this.tableName).setRemovalOptionForOrderAndFetchClauses(vendorSQS.getRemovalOptionForOrderAndFetchClauses());
                ((SelectQueryStatement)this.tableName).setCanUseDistinctFromForNullSafeEqualsOperator(vendorSQS.canUseDistinctFromForNullSafeEqualsOperator());
                ((SelectQueryStatement)this.tableName).setCanHandleHavingWithoutGroupBy(vendorSQS.canHandleHavingWithoutGroupBy());
                ((SelectQueryStatement)this.tableName).setCanUseUDFFunctionsForStrToDate(vendorSQS.canUseUDFFunctionsForStrToDate());
                ft.setTableName(((SelectQueryStatement)this.tableName).toPostgreSQLSelect());
            }
            else {
                if (this.tableName instanceof FunctionCalls) {
                    throw new ConvertException("Function calls in the place of Table name yet to be supported");
                }
                if (this.tableName instanceof WithStatement) {
                    ft.setTableName(((WithStatement)this.tableName).toPostgreSQL());
                }
                else if (this.tableName instanceof FromClause) {
                    ft.setTableName(((FromClause)this.tableName).toPostgreSQLSelect(vembuSQS, vendorSQS));
                }
                else {
                    if (((String)this.tableName).indexOf(".") > 0 && !vendorSQS.isAmazonRedShift()) {
                        final String table_name = (String)this.tableName;
                        if (table_name.indexOf("..") > 0) {
                            ft.setTableName(table_name.substring(table_name.indexOf("..") + 2, table_name.length()));
                        }
                        else {
                            final String table_Name_String = (String)this.tableName;
                            final int atIndex = table_Name_String.indexOf("@");
                            ft.setTableName(table_name.substring(table_name.indexOf(".") + 1, table_name.length()));
                            final Vector tokenVector = new Vector();
                            final String table_Name = (String)this.tableName;
                            final StringTokenizer st = new StringTokenizer(table_Name, ".");
                            int count = 0;
                            while (st.hasMoreTokens()) {
                                tokenVector.add(st.nextToken());
                                ++count;
                            }
                            if (count == 1 && atIndex != -1) {
                                final String dataBaseTableName = tokenVector.elementAt(0);
                                final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                                final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                                final String sqlDataBaseAndTableName = sqlTableName;
                                ft.setTableName(sqlDataBaseAndTableName);
                            }
                            else if (count == 2 && atIndex != -1) {
                                final String dataBaseTableName = tokenVector.elementAt(1);
                                final int tableAtIndex = dataBaseTableName.indexOf("@");
                                final String sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                                final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                                final String sqlDataBaseAndTableName2 = sqlTableName2;
                                ft.setTableName(sqlDataBaseAndTableName2);
                            }
                            if (((String)this.tableName).indexOf("[") >= 0) {
                                String tempTableName = (String)ft.getTableName();
                                String tableNameSubString = "";
                                int startIndex = tempTableName.indexOf("[");
                                if (startIndex != -1) {
                                    while (startIndex != -1) {
                                        if (startIndex == 0) {
                                            tableNameSubString = tempTableName.substring(1);
                                            tableNameSubString = "\"" + tableNameSubString;
                                            tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                                            startIndex = tableNameSubString.indexOf("[");
                                        }
                                        else {
                                            tableNameSubString = tempTableName.substring(0, startIndex);
                                            tableNameSubString += "\"";
                                            tableNameSubString += tempTableName.substring(startIndex + 1);
                                            tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                                            startIndex = tableNameSubString.indexOf("[");
                                        }
                                    }
                                    ft.setTableName(tableNameSubString);
                                }
                            }
                        }
                    }
                    else {
                        if (((String)this.tableName).indexOf("[") == 0) {
                            String tempTableName2 = (String)this.tableName;
                            String tableNameSubString2 = "";
                            int startIndex2 = tempTableName2.indexOf("[");
                            if (startIndex2 != -1) {
                                while (startIndex2 != -1) {
                                    if (startIndex2 == 0) {
                                        tableNameSubString2 = tempTableName2.substring(1);
                                        tableNameSubString2 = "\"" + tableNameSubString2;
                                        tableNameSubString2 = (tempTableName2 = StringFunctions.replaceFirst("\"", "]", tableNameSubString2));
                                        startIndex2 = tableNameSubString2.indexOf("[");
                                    }
                                    else {
                                        tableNameSubString2 = tempTableName2.substring(0, startIndex2);
                                        tableNameSubString2 += "\"";
                                        tableNameSubString2 += tempTableName2.substring(startIndex2 + 1);
                                        tableNameSubString2 = (tempTableName2 = StringFunctions.replaceFirst("\"", "]", tableNameSubString2));
                                        startIndex2 = tableNameSubString2.indexOf("[");
                                    }
                                }
                                ft.setTableName(tableNameSubString2);
                            }
                        }
                        else {
                            ft.setTableName(this.tableName);
                        }
                        final String table_Name_String2 = (String)this.tableName;
                        final int atIndex2 = table_Name_String2.indexOf("@");
                        final Vector tokenVector2 = new Vector();
                        final String table_Name2 = (String)this.tableName;
                        final StringTokenizer st2 = new StringTokenizer(table_Name2, ".");
                        int count2 = 0;
                        while (st2.hasMoreTokens()) {
                            tokenVector2.add(st2.nextToken());
                            ++count2;
                        }
                        if (count2 == 1 && atIndex2 != -1) {
                            final String dataBaseTableName2 = tokenVector2.elementAt(0);
                            final String sqlTableName3 = dataBaseTableName2.substring(0, atIndex2);
                            final String sqlDataBaseName3 = dataBaseTableName2.substring(atIndex2 + 1);
                            final String sqlDataBaseAndTableName3 = sqlTableName3;
                            ft.setTableName(sqlTableName3);
                        }
                        String tempTableName3 = (String)ft.getTableName();
                        String tableNameSubString3 = "";
                        final int startIndex3 = tempTableName3.indexOf("[");
                        if (startIndex3 != -1) {
                            if (startIndex3 == 0) {
                                tableNameSubString3 = tempTableName3.substring(1);
                                tableNameSubString3 = "\"" + tableNameSubString3;
                                tableNameSubString3 = (tempTableName3 = StringFunctions.replaceFirst("\"", "]", tableNameSubString3));
                            }
                            ft.setTableName(tempTableName3);
                        }
                    }
                    String tblName = (String)ft.getTableName();
                    if (tblName.charAt(0) == '\'') {
                        ft.setTableName(tblName.replace('\'', '\"'));
                    }
                    else if (tblName.charAt(0) == '`') {
                        ft.setTableName(tblName.replace('`', '\"'));
                    }
                    else {
                        ft.setTableName(tblName);
                    }
                    tblName = (String)ft.getTableName();
                    ft.setTableName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(tblName, vendorSQS != null && vendorSQS.getReportsMeta()));
                }
            }
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else if (this.aliasName.charAt(0) == '`') {
                ft.setAliasName(this.aliasName.replace('`', '\"'));
            }
            else if (this.aliasName.charAt(0) == '\"') {
                ft.setAliasName(this.aliasName);
            }
            else {
                ft.setAliasName("\"" + this.aliasName + "\"");
            }
            ft.setAliasName(SelectStatement.checkandRemoveDoubleQuoteForPostgresIdentifier(ft.getAliasName(), vendorSQS != null && vendorSQS.getReportsMeta()));
        }
        else if (this.tableName instanceof SelectQueryStatement) {
            final SelectQueryStatement sqs = (SelectQueryStatement)this.tableName;
            if (sqs.getSetOperatorClause() != null) {
                final SetOperatorClause soc = sqs.getSetOperatorClause();
                Label_2099: {
                    if (soc != null && soc.getSetClause() != null) {
                        if (soc.getSetClause().toUpperCase().startsWith("UNION") || soc.getSetClause().toUpperCase().startsWith("MINUS") || soc.getSetClause().toUpperCase().startsWith("INTERSECT")) {
                            break Label_2099;
                        }
                        if (soc.getSetClause().toUpperCase().startsWith("EXCEPT")) {
                            break Label_2099;
                        }
                    }
                    ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
                }
            }
            else {
                ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
            }
        }
        if (this.joinClause != null && this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
            ft.setOnOrUsingJoin(null);
        }
        ft.setJoinClause(this.joinClause);
        return ft;
    }
    
    public FromTable toDB2Select(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
                throw new ConvertException("Conversion failure..Natural join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("CROSS JOIN")) {
                this.joinClause = "INNER JOIN";
                this.setOnOrUsingJoin("ON");
                final Vector newWhereItems = new Vector();
                final WhereItem newWhereItem = new WhereItem();
                final WhereColumn newWhereColumn = new WhereColumn();
                final Vector whereColumnItems = new Vector();
                whereColumnItems.add("1");
                newWhereColumn.setColumnExpression(whereColumnItems);
                newWhereItem.setLeftWhereExp(newWhereColumn);
                newWhereItem.setRightWhereExp(newWhereColumn);
                newWhereItem.setOperator("=");
                final WhereExpression newWhereExpression = new WhereExpression();
                newWhereExpression.addWhereItem(newWhereItem);
                newWhereItems.add(newWhereExpression);
                this.setJoinExpression(newWhereItems);
            }
            else {
                if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
                    throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
                }
                if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
                    throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
                }
                if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
                    throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
                }
                if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
                    throw new ConvertException("Conversion failure..Natural right join can't be converted");
                }
                if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                    throw new ConvertException("Conversion failure..Key join is not supported");
                }
            }
            if (this.onOrUsingJoin == null && this.joinExpression != null) {
                ft.setOnOrUsingJoin("ON");
                final WhereExpression we = this.joinExpression.elementAt(0).toDB2Select(vembuSQS, vendorSQS);
                final Vector v = new Vector();
                v.addElement(we);
                ft.setJoinExpression(v);
            }
            else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
                this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
            }
            else if (this.joinExpression != null) {
                ft.setOnOrUsingJoin(this.onOrUsingJoin);
                final WhereExpression we = this.joinExpression.elementAt(0);
                final Vector operatorList = we.getOperator();
                we.setOperator(operatorList);
                we.toDB2Select(vembuSQS, vendorSQS);
                final Vector v2 = new Vector();
                v2.addElement(we);
                ft.setJoinExpression(v2);
            }
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ft.setTableKeyword(this.tableKeyword);
                ft.setTableName(((SelectQueryStatement)this.tableName).toDB2Select());
            }
            else if (this.tableName instanceof FunctionCalls) {
                ft.setTableKeyword(this.tableKeyword);
                ft.setTableName(((FunctionCalls)this.tableName).toDB2Select(vembuSQS, vendorSQS));
            }
            else if (this.tableName instanceof WithStatement) {
                ft.setTableName(((WithStatement)this.tableName).toDB2());
            }
            else if (this.tableName instanceof FromClause) {
                ft.setTableName(((FromClause)this.tableName).toDB2Select(vembuSQS, vendorSQS));
            }
            else {
                final String table_Name_String = (String)this.tableName;
                final int atIndex = table_Name_String.indexOf("@");
                ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
                final Vector tokenVector = new Vector();
                final String table_Name = (String)this.tableName;
                final StringTokenizer st = new StringTokenizer(table_Name, ".");
                int count = 0;
                while (st.hasMoreTokens()) {
                    tokenVector.add(st.nextToken());
                    ++count;
                }
                if (count == 1 && atIndex != -1) {
                    final String dataBaseTableName = tokenVector.elementAt(0);
                    final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                    final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                    final String sqlDataBaseAndTableName = sqlDataBaseName + "." + sqlTableName;
                    ft.setTableName(sqlDataBaseAndTableName);
                }
                else if (count == 2 && atIndex != -1) {
                    final String dataBaseTableName = tokenVector.elementAt(1);
                    final int tableAtIndex = dataBaseTableName.indexOf("@");
                    String sqlTableName2 = dataBaseTableName;
                    if (tableAtIndex != -1) {
                        sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                    }
                    final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                    final String sqlDataBaseAndTableName2 = sqlDataBaseName2 + "." + tokenVector.elementAt(0) + "." + sqlTableName2;
                    ft.setTableName(sqlDataBaseAndTableName2);
                }
                String tempTableName = (String)ft.getTableName();
                String tableNameSubString = "";
                int startIndex = tempTableName.indexOf("[");
                if (startIndex != -1) {
                    while (startIndex != -1) {
                        if (startIndex == 0) {
                            tableNameSubString = tempTableName.substring(1);
                            tableNameSubString = "\"" + tableNameSubString;
                            tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                            startIndex = tableNameSubString.indexOf("[");
                        }
                        else {
                            tableNameSubString = tempTableName.substring(0, startIndex);
                            tableNameSubString += "\"";
                            tableNameSubString += tempTableName.substring(startIndex + 1);
                            tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                            startIndex = tableNameSubString.indexOf("[");
                        }
                    }
                    ft.setTableName(tableNameSubString);
                }
            }
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else {
                ft.setAliasName(this.aliasName);
            }
        }
        else if (this.tableName instanceof SelectQueryStatement) {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
        }
        ft.setJoinClause(this.joinClause);
        final String ownerName = SwisSQLAPI.objectsOwnerName.get(new Integer(3));
        if (ownerName != null && ft.getTableName() instanceof String && ft.getTableName().toString().toLowerCase().indexOf(ownerName + ".") == -1) {
            ft.setTableName(ownerName + "." + ft.getTableName());
        }
        return ft;
    }
    
    public FromTable toANSISelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        ft.setTableKeyword(this.tableKeyword);
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ft.setTableName(((SelectQueryStatement)this.tableName).toANSISelect());
            }
            else {
                if (this.tableName instanceof FunctionCalls) {
                    throw new ConvertException("Functions yet to be supported in table names");
                }
                if (this.tableName instanceof WithStatement) {
                    ft.setTableName(((WithStatement)this.tableName).toANSISQL());
                }
                else if (this.tableName instanceof FromClause) {
                    ft.setTableName(((FromClause)this.tableName).toANSISelect(vembuSQS, vendorSQS));
                }
                else {
                    final String table_Name_String = (String)this.tableName;
                    final int atIndex = table_Name_String.indexOf("@");
                    final int lastIndexOfDot = table_Name_String.lastIndexOf(".");
                    String remoteLinkName = "";
                    if (atIndex != -1) {
                        remoteLinkName = table_Name_String.substring(atIndex + 1);
                    }
                    if (remoteLinkName.indexOf(".") != -1) {
                        remoteLinkName = "\"" + remoteLinkName + "\"";
                    }
                    ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
                    final Vector tokenVector = new Vector();
                    String table_Name = (String)this.tableName;
                    if (atIndex != -1 && lastIndexOfDot > atIndex) {
                        table_Name = table_Name.substring(0, atIndex);
                    }
                    final StringTokenizer st = new StringTokenizer(table_Name, ".");
                    int count = 0;
                    while (st.hasMoreTokens()) {
                        tokenVector.add(st.nextToken());
                        ++count;
                    }
                    if (count == 1 && atIndex != -1) {
                        final String dataBaseTableName = tokenVector.elementAt(0);
                        final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                        final String sqlDataBaseAndTableName = remoteLinkName + "." + sqlTableName;
                        ft.setTableName(sqlDataBaseAndTableName);
                    }
                    else if (count == 2 && atIndex != -1) {
                        final String dataBaseTableName = tokenVector.elementAt(1);
                        final String sqlDataBaseAndTableName2 = remoteLinkName + "." + tokenVector.elementAt(0) + "." + dataBaseTableName;
                        ft.setTableName(sqlDataBaseAndTableName2);
                    }
                    String tempTableName = (String)ft.getTableName();
                    String tableNameSubString = "";
                    int startIndex = tempTableName.indexOf("[");
                    if (startIndex != -1) {
                        while (startIndex != -1) {
                            if (startIndex == 0) {
                                tableNameSubString = tempTableName.substring(1);
                                tableNameSubString = "\"" + tableNameSubString;
                                tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                                startIndex = tableNameSubString.indexOf("[");
                            }
                            else {
                                tableNameSubString = tempTableName.substring(0, startIndex);
                                tableNameSubString += "\"";
                                tableNameSubString += tempTableName.substring(startIndex + 1);
                                tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                                startIndex = tableNameSubString.indexOf("[");
                            }
                        }
                        ft.setTableName(tableNameSubString);
                    }
                    else if (count == 1 && atIndex == -1) {
                        final String tableName = tokenVector.get(0).toString().trim();
                        if (tableName.startsWith("\"") || tableName.startsWith("'")) {
                            ft.setTableName(tableName);
                        }
                        else if (SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                            ft.setTableName("\"" + ft.getTableName() + "\"");
                        }
                        else {
                            ft.setTableName(ft.getTableName());
                        }
                    }
                    else if (count == 2 && atIndex == -1) {
                        final String databaseSchemaName = tokenVector.get(0).toString();
                        final String databaseTableName = tokenVector.get(1).toString();
                        if (!databaseSchemaName.startsWith("\"") && !databaseTableName.startsWith("\"") && !databaseSchemaName.startsWith("'") && !databaseTableName.startsWith("'") && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                            ft.setTableName("\"" + databaseSchemaName + "\"" + "." + "\"" + databaseTableName + "\"");
                        }
                        else {
                            ft.setTableName(ft.getTableName());
                        }
                    }
                    else if (count == 3 && atIndex == -1) {
                        final String databaseName = tokenVector.get(0).toString();
                        final String databaseSchemaName2 = tokenVector.get(1).toString();
                        final String databaseTableName2 = tokenVector.get(2).toString();
                        if (!databaseSchemaName2.startsWith("\"") && !databaseTableName2.startsWith("\"") && !databaseSchemaName2.startsWith("'") && !databaseTableName2.startsWith("'") && !databaseName.startsWith("\"") && !databaseName.startsWith("'") && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                            ft.setTableName("\"" + databaseName + "\"" + "." + "\"" + databaseSchemaName2 + "\"" + "." + "\"" + databaseTableName2 + "\"");
                        }
                        else {
                            ft.setTableName(ft.getTableName());
                        }
                    }
                    else if (SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                        ft.setTableName("\"" + ft.getTableName() + "\"");
                    }
                    else {
                        ft.setTableName(ft.getTableName());
                    }
                }
            }
        }
        if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            final WhereExpression we = this.joinExpression.elementAt(0).toDB2Select(vembuSQS, vendorSQS);
            final Vector v = new Vector();
            v.addElement(we);
            ft.setJoinExpression(v);
        }
        else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
            this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
        }
        else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            final WhereExpression we = this.joinExpression.elementAt(0);
            final Vector operatorList = we.getOperator();
            we.setOperator(operatorList);
            final Vector v2 = new Vector();
            v2.addElement(we.toANSISelect(vembuSQS, vendorSQS));
            ft.setJoinExpression(v2);
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            if (this.aliasName.charAt(0) == '\'' && SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                ft.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else if (this.aliasName.charAt(0) == '\"') {
                ft.setAliasName(this.aliasName);
            }
            else if (SwisSQLOptions.setDoubleQuotesToAnsiSqlTableObjects) {
                ft.setAliasName("\"" + this.aliasName + "\"");
            }
            else {
                ft.setAliasName(this.aliasName);
            }
        }
        else if (this.tableName instanceof SelectQueryStatement) {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
        }
        ft.setJoinClause(this.joinClause);
        return ft;
    }
    
    public FromTable toTeradataSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        ft.setTableKeyword(this.tableKeyword);
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryStmt = (SelectQueryStatement)this.tableName;
                if (!this.setOperatorClauseListForSubQuery.isEmpty()) {
                    this.buildSetOperatorClauseForSubQuery(subQueryStmt);
                }
                final Vector selectItemList = subQueryStmt.getSelectStatement().getSelectItemList();
                for (int i_count = 0; i_count < selectItemList.size(); ++i_count) {
                    if (selectItemList.elementAt(i_count) instanceof SelectColumn) {
                        final SelectColumn sc1 = selectItemList.elementAt(i_count);
                        if (vendorSQS != null && !vendorSQS.getTopLevel()) {
                            if (sc1.getColumnExpression().size() == 1 && !(sc1.getColumnExpression().get(0) instanceof TableColumn) && sc1.getAliasName() == null) {
                                String aliasForExpr = sc1.getTheCoreSelectItem().trim();
                                if (sc1.getColumnExpression().get(0) instanceof SelectColumn && sc1.getColumnExpression().get(0).getColumnExpression().size() == 1) {
                                    if (!(sc1.getColumnExpression().get(0).getColumnExpression().get(0) instanceof TableColumn)) {
                                        aliasForExpr = sc1.getColumnExpression().get(0).toString();
                                    }
                                    else {
                                        aliasForExpr = "";
                                    }
                                }
                                if (aliasForExpr.lastIndexOf(",") != -1) {
                                    aliasForExpr = aliasForExpr.substring(0, aliasForExpr.lastIndexOf(",")).trim();
                                }
                                if (aliasForExpr.indexOf("*/") != -1) {
                                    aliasForExpr = aliasForExpr.substring(aliasForExpr.indexOf("*/") + 2).trim();
                                }
                                boolean isNum = false;
                                try {
                                    Double.parseDouble(aliasForExpr);
                                    isNum = true;
                                }
                                catch (final NumberFormatException nfe) {
                                    isNum = false;
                                }
                                if (!isNum && !aliasForExpr.toLowerCase().startsWith("case") && aliasForExpr.indexOf(".") != -1 && aliasForExpr.indexOf(".") == aliasForExpr.lastIndexOf(".") && aliasForExpr.indexOf("(") == -1) {
                                    aliasForExpr = aliasForExpr.substring(aliasForExpr.lastIndexOf(".") + 1);
                                }
                                if (aliasForExpr.indexOf("/*") != -1) {
                                    aliasForExpr = aliasForExpr.substring(0, aliasForExpr.indexOf("/*")).trim();
                                }
                                if (!aliasForExpr.equalsIgnoreCase("*") && !aliasForExpr.startsWith("*") && !aliasForExpr.endsWith("*")) {
                                    if (aliasForExpr.length() > 30) {
                                        aliasForExpr = aliasForExpr.substring(0, 29);
                                    }
                                    if (aliasForExpr.length() > 0) {
                                        sc1.setAliasForExpression("\"" + aliasForExpr.replaceAll("\n", " ").replaceAll("\t", " ") + "\"");
                                    }
                                }
                            }
                            if (sc1.getAliasName() == null && sc1.getAliasForExpression() != null) {
                                sc1.setAliasName(sc1.getAliasForExpression());
                            }
                        }
                    }
                }
                SelectQueryStatement subQuery = subQueryStmt;
                if (!subQuery.isConverted()) {
                    subQuery = subQueryStmt.toTeradataSelect();
                }
                if (subQuery.getWithStatement() != null) {
                    SelectQueryStatement.getListOfWithStatements().add(subQuery.getWithStatement());
                    final SelectQueryStatement tempSubQuery = subQuery.getWithStatement().getWithSQS();
                    subQuery.getWithStatement().setWithSQS(null);
                    subQuery = tempSubQuery;
                }
                ft.setTableName(subQuery);
            }
            else {
                if (this.tableName instanceof FunctionCalls) {
                    throw new ConvertException("Functions yet to be supported in table names");
                }
                if (this.tableName instanceof WithStatement) {
                    ft.setTableName(((WithStatement)this.tableName).toTeradata());
                }
                else if (this.tableName instanceof FromClause) {
                    final FromClause teradataFromClause = ((FromClause)this.tableName).toTeradataSelect(vembuSQS, vendorSQS);
                    ft.setTableName(teradataFromClause);
                    if (this.aliasName == null && ft.getAliasName() == null && teradataFromClause.getAliasName() != null) {
                        ft.setAliasName(teradataFromClause.getAliasName());
                    }
                }
                else {
                    final String table_Name_String = (String)this.tableName;
                    final int atIndex = table_Name_String.indexOf("@");
                    if (table_Name_String.indexOf("..") != -1) {
                        ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
                    }
                    final int dotDotIndex = table_Name_String.indexOf("..");
                    ft.setTableName(table_Name_String);
                    if (dotDotIndex != -1) {
                        final String localTableName = table_Name_String.substring(table_Name_String.lastIndexOf(".") + 1, table_Name_String.length());
                        ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier(localTableName, SwisSQLUtils.getKeywords("teradata"), null, -1));
                    }
                    else {
                        final Vector tokenVector = new Vector();
                        final StringTokenizer st = new StringTokenizer(table_Name_String, ".");
                        int count = 0;
                        while (st.hasMoreTokens()) {
                            tokenVector.add(st.nextToken());
                            ++count;
                        }
                        if (count == 1) {
                            ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier(table_Name_String, SwisSQLUtils.getKeywords("teradata"), null, -1));
                        }
                        else if (count == 2) {
                            final String ownerName = tokenVector.elementAt(0);
                            String tableName = tokenVector.elementAt(1);
                            if (!tableName.startsWith("\"")) {
                                tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords("teradata"), null, -1);
                            }
                            if (ownerName.equalsIgnoreCase("DBO")) {
                                ft.setTableName(tableName);
                            }
                            else {
                                ft.setTableName(ownerName + "." + tableName);
                            }
                        }
                        else if (count == 3) {
                            final String dataBaseName = tokenVector.elementAt(0);
                            final String ownerName2 = tokenVector.elementAt(1);
                            String tableName2 = tokenVector.elementAt(2);
                            if (!tableName2.startsWith("\"")) {
                                tableName2 = CustomizeUtil.objectNamesToQuotedIdentifier(tableName2, SwisSQLUtils.getKeywords("teradata"), null, -1);
                            }
                            if (ownerName2.equalsIgnoreCase("DBO")) {
                                ft.setTableName(tableName2);
                            }
                            else {
                                ft.setTableName(ownerName2 + "." + tableName2);
                            }
                        }
                    }
                    final Vector tokenVector = new Vector();
                    final String table_Name = (String)this.tableName;
                    final StringTokenizer st2 = new StringTokenizer(table_Name, ".");
                    int count2 = 0;
                    while (st2.hasMoreTokens()) {
                        tokenVector.add(st2.nextToken());
                        ++count2;
                    }
                    if (count2 == 1 && atIndex != -1) {
                        final String dataBaseTableName = tokenVector.elementAt(0);
                        String sqlTableName = dataBaseTableName.substring(0, atIndex);
                        final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                        sqlTableName = CustomizeUtil.objectNamesToQuotedIdentifier(sqlTableName, SwisSQLUtils.getKeywords("teradata"), null, -1);
                        final String sqlDataBaseAndTableName = sqlDataBaseName + "." + sqlTableName;
                        ft.setTableName(sqlDataBaseAndTableName);
                    }
                    else if (count2 == 2 && atIndex != -1) {
                        final String dataBaseTableName = tokenVector.elementAt(1);
                        final int tableAtIndex = dataBaseTableName.indexOf("@");
                        String sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                        final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                        sqlTableName2 = CustomizeUtil.objectNamesToQuotedIdentifier(sqlTableName2, SwisSQLUtils.getKeywords("teradata"), null, -1);
                        final String sqlDataBaseAndTableName2 = sqlDataBaseName2 + "." + tokenVector.elementAt(0) + "." + sqlTableName2;
                        ft.setTableName(sqlDataBaseAndTableName2);
                    }
                    String tempTableName = (String)ft.getTableName();
                    String tableNameSubString = "";
                    int startIndex = tempTableName.indexOf("[");
                    if (startIndex != -1) {
                        while (startIndex != -1) {
                            if (startIndex == 0) {
                                tableNameSubString = tempTableName.substring(1);
                                tableNameSubString = "\"" + tableNameSubString;
                                tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                                startIndex = tableNameSubString.indexOf("[");
                            }
                            else {
                                tableNameSubString = tempTableName.substring(0, startIndex);
                                tableNameSubString += "\"";
                                tableNameSubString += tempTableName.substring(startIndex + 1);
                                tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                                startIndex = tableNameSubString.indexOf("[");
                            }
                        }
                        ft.setTableName(tableNameSubString);
                    }
                    else if (count2 == 2 && atIndex == -1) {
                        String databaseSchemaName = tokenVector.get(0).toString();
                        String databaseTableName = tokenVector.get(1).toString();
                        if (!databaseSchemaName.startsWith("\"") && !databaseTableName.startsWith("\"") && !databaseSchemaName.startsWith("'") && !databaseTableName.startsWith("'")) {
                            databaseSchemaName = CustomizeUtil.objectNamesToQuotedIdentifier(databaseSchemaName, SwisSQLUtils.getKeywords("teradata"), null, -1);
                            databaseTableName = CustomizeUtil.objectNamesToQuotedIdentifier(databaseTableName, SwisSQLUtils.getKeywords("teradata"), null, -1);
                            ft.setTableName(databaseSchemaName + "." + databaseTableName);
                        }
                        else {
                            ft.setTableName(ft.getTableName());
                        }
                    }
                    else if (count2 == 3 && atIndex == -1) {
                        String databaseName = tokenVector.get(0).toString();
                        String databaseSchemaName2 = tokenVector.get(1).toString();
                        String databaseTableName2 = tokenVector.get(2).toString();
                        if (!databaseSchemaName2.startsWith("\"") && !databaseTableName2.startsWith("\"") && !databaseSchemaName2.startsWith("'") && !databaseTableName2.startsWith("'") && !databaseName.startsWith("\"") && !databaseName.startsWith("'")) {
                            databaseName = CustomizeUtil.objectNamesToQuotedIdentifier(databaseName, SwisSQLUtils.getKeywords("teradata"), null, -1);
                            databaseSchemaName2 = CustomizeUtil.objectNamesToQuotedIdentifier(databaseSchemaName2, SwisSQLUtils.getKeywords("teradata"), null, -1);
                            databaseTableName2 = CustomizeUtil.objectNamesToQuotedIdentifier(databaseTableName2, SwisSQLUtils.getKeywords("teradata"), null, -1);
                            ft.setTableName(databaseName + "." + databaseSchemaName2 + "." + databaseTableName2);
                        }
                        else {
                            ft.setTableName(ft.getTableName());
                        }
                    }
                }
            }
        }
        if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            final WhereExpression we = this.joinExpression.elementAt(0).toDB2Select(vembuSQS, vendorSQS);
            final Vector v = new Vector();
            v.addElement(we);
            ft.setJoinExpression(v);
        }
        else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
            this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
        }
        else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            final WhereExpression we = this.joinExpression.elementAt(0);
            final Vector operatorList = we.getOperator();
            we.setOperator(operatorList);
            final Vector v2 = new Vector();
            final WhereExpression teradataWhereExp = we.toTeradataSelect(vembuSQS, vendorSQS);
            if (this.crossJoinExpression != null) {
                teradataWhereExp.addWhereExpression(this.crossJoinExpression);
                teradataWhereExp.addOperator("AND");
            }
            v2.addElement(teradataWhereExp);
            ft.setJoinExpression(v2);
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.fromClauseOpenBraces != null) {
            ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
            ft.setFromClauseClosedBraces(")");
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            ft.setAliasName(this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords("teradata"), null, -1));
        }
        else if (this.tableName instanceof SelectQueryStatement && vendorSQS != null) {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
        }
        ft.setJoinClause(this.joinClause);
        if (this.queryPartitionClause != null) {
            final FromTable crossJoinFt = new FromTable();
            final SelectQueryStatement crossJoinQuery = new SelectQueryStatement();
            final SelectStatement crossJoinSelect = new SelectStatement();
            crossJoinSelect.setSelectClause("SELECT");
            crossJoinSelect.setSelectQualifier("DISTINCT");
            final Vector crossJoinItems = new Vector();
            for (int ki = 0; ki < this.queryPartitionClause.getSelectColumnList().size(); ++ki) {
                if (this.queryPartitionClause.getSelectColumnList().get(ki) instanceof SelectColumn) {
                    final SelectColumn sc2 = this.queryPartitionClause.getSelectColumnList().get(ki);
                    if (sc2.getEndsWith() == null && ki < this.queryPartitionClause.getSelectColumnList().size() - 1) {
                        sc2.setEndsWith(",");
                    }
                    crossJoinItems.add(sc2.toTeradataSelect(null, null));
                }
            }
            crossJoinSelect.setSelectItemList(crossJoinItems);
            crossJoinQuery.setSelectStatement(crossJoinSelect);
            final FromClause fc = new FromClause();
            fc.setFromClause("FROM");
            final Vector fcFromItems = new Vector();
            final FromTable crossJoinTable = new FromTable();
            crossJoinTable.setTableName(ft.getTableName());
            crossJoinTable.setAliasName(ft.getAliasName());
            fcFromItems.add(crossJoinTable);
            fc.setFromItemList(fcFromItems);
            crossJoinQuery.setFromClause(fc);
            if (ft.getAliasName() != null) {
                crossJoinFt.setAliasName(ft.getAliasName() + 1);
            }
            else if (ft.getTableName() instanceof String) {
                crossJoinFt.setAliasName(ft.getTableName().toString() + 1);
            }
            else {
                crossJoinFt.setAliasName("cj1");
            }
            crossJoinFt.setTableName(crossJoinQuery);
            crossJoinFt.setJoinClause("CROSS JOIN");
            ft.setQueryPartitionClause(this.queryPartitionClause);
            ft.setCrossJoinForPartitionClause(crossJoinFt);
            final WhereExpression crossJoinWhereExp = new WhereExpression();
            Vector toSQSSelectItems = new Vector();
            if (vembuSQS != null) {
                toSQSSelectItems = vembuSQS.getSelectStatement().getSelectItemList();
            }
            for (int j = 0; j < crossJoinItems.size(); ++j) {
                final SelectColumn orig = crossJoinItems.get(j);
                final String origStrBase;
                String origStr = origStrBase = orig.getTheCoreSelectItem().trim();
                if (!origStr.toLowerCase().startsWith("case") && origStr.indexOf(".") != -1 && origStr.indexOf(".") == origStr.lastIndexOf(".") && origStr.indexOf("(") == -1) {
                    origStr = origStr.substring(origStr.lastIndexOf(".") + 1);
                }
                if (origStr.length() > 30) {
                    origStr = origStr.substring(0, 29);
                }
                if (!origStr.startsWith("\"") && !origStr.endsWith("\"")) {
                    origStr = "\"" + origStr + "\"";
                }
                orig.setAliasName(origStr);
                final WhereItem crossJoinWhereItem = new WhereItem();
                final WhereColumn crossJoinWhereCol = new WhereColumn();
                final Vector crossJoinColExp = new Vector();
                final TableColumn crossJoinCol = new TableColumn();
                crossJoinCol.setTableName(crossJoinFt.getAliasName());
                crossJoinCol.setColumnName(orig.getAliasName());
                crossJoinColExp.add(crossJoinCol);
                crossJoinWhereCol.setColumnExpression(crossJoinColExp);
                crossJoinWhereItem.setLeftWhereExp(crossJoinWhereCol);
                for (int ji = 0; ji < toSQSSelectItems.size(); ++ji) {
                    final SelectColumn toSQSCol = toSQSSelectItems.get(ji);
                    if (origStrBase.equalsIgnoreCase(toSQSCol.getTheCoreSelectItem().trim())) {
                        toSQSCol.setColumnExpression(crossJoinColExp);
                        if (toSQSCol.getAliasName() == null) {
                            toSQSCol.setAliasName(orig.getAliasName());
                        }
                    }
                }
                final WhereColumn origWhereCol = new WhereColumn();
                final SelectColumn origTableSelCol = new SelectColumn();
                origTableSelCol.setColumnExpression(orig.getColumnExpression());
                final Vector origWhereColExp = new Vector();
                origWhereColExp.add(origTableSelCol);
                origWhereCol.setColumnExpression(origWhereColExp);
                crossJoinWhereItem.setRightWhereExp(origWhereCol);
                crossJoinWhereExp.addWhereItem(crossJoinWhereItem);
                crossJoinWhereItem.setOperator("=");
                if (j != 0) {
                    crossJoinWhereExp.addOperator("AND");
                }
            }
            if (ft.getJoinExpression() != null) {
                ft.getJoinExpression().elementAt(0).addWhereExpression(crossJoinWhereExp);
                ft.getJoinExpression().elementAt(0).addOperator("AND");
            }
            else {
                ft.setCrossJoinExpression(crossJoinWhereExp);
            }
        }
        if (this.crossJoinForPartitionClause != null) {
            ft.setCrossJoinForPartitionClause(this.crossJoinForPartitionClause);
        }
        return ft;
    }
    
    public FromTable toMSSQLServerSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
                throw new ConvertException("Conversion failure..Natural join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
                throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural right join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                throw new ConvertException("Conversion failure..Key join is not supported");
            }
            if (this.onOrUsingJoin == null && this.joinExpression != null) {
                ft.setOnOrUsingJoin("ON");
                final WhereExpression we = this.joinExpression.elementAt(0).toMSSQLServerSelect(vembuSQS, vendorSQS);
                final Vector v = new Vector();
                v.addElement(we);
                ft.setJoinExpression(v);
            }
            else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
                this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
            }
            else if (this.joinExpression != null) {
                ft.setOnOrUsingJoin(this.onOrUsingJoin);
                final WhereExpression we = this.joinExpression.elementAt(0);
                final Vector operatorList = we.getOperator();
                we.setOperator(operatorList);
                final Vector v2 = new Vector();
                v2.addElement(we.toMSSQLServerSelect(vembuSQS, vendorSQS));
                ft.setJoinExpression(v2);
            }
        }
        if (vendorSQS != null && vendorSQS.getForUpdateStatement() != null) {
            ft.setUpdateLock("(UPDLOCK)");
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ft.setTableName(((SelectQueryStatement)this.tableName).toMSSQLServerSelect());
            }
            else if (this.tableName instanceof FunctionCalls) {
                if (this.tableKeyword != null) {
                    final FunctionCalls fn = ((FunctionCalls)this.tableName).toMSSQLServerSelect(vembuSQS, vendorSQS);
                    if (fn != null && fn.toString().indexOf("CAST") != -1) {
                        ft.setTableKeyword("TABLE(");
                    }
                    ft.setTableName(fn);
                }
                else {
                    ft.setTableName(((FunctionCalls)this.tableName).toMSSQLServerSelect(vembuSQS, vendorSQS));
                }
            }
            else if (this.tableName instanceof WithStatement) {
                ft.setTableName(((WithStatement)this.tableName).toMSSQLServer());
            }
            else if (this.tableName instanceof FromClause) {
                ft.setTableName(((FromClause)this.tableName).toMSSQLServerSelect(vembuSQS, vendorSQS));
            }
            else {
                final String table_Name_String = (String)this.tableName;
                final int atIndex = table_Name_String.indexOf("@");
                if (((String)this.tableName).indexOf("..") < 0) {
                    if (SwisSQLOptions.removeDBSchemaQualifier) {
                        ft.setTableName(table_Name_String.substring(table_Name_String.lastIndexOf(".") + 1));
                    }
                    else {
                        ft.setTableName(this.tableName);
                    }
                }
                else {
                    ft.setTableName(this.tableName);
                }
                final Vector tokenVector = new Vector();
                final String table_Name = (String)this.tableName;
                final StringTokenizer st = new StringTokenizer(table_Name, ".");
                int count = 0;
                while (st.hasMoreTokens()) {
                    tokenVector.add(st.nextToken());
                    ++count;
                }
                if (count == 1 && atIndex != -1 && atIndex != 0) {
                    final String dataBaseTableName = tokenVector.elementAt(0);
                    final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                    final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                    final String sqlDataBaseAndTableName = sqlDataBaseName + ".." + sqlTableName;
                    ft.setTableName(sqlDataBaseAndTableName);
                }
                else if (count == 2 && atIndex != -1) {
                    final String dataBaseTableName = tokenVector.elementAt(1);
                    final int tableAtIndex = dataBaseTableName.indexOf("@");
                    final String sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                    final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                    final String sqlDataBaseAndTableName2 = sqlDataBaseName2 + "." + tokenVector.elementAt(0) + "." + sqlTableName2;
                    ft.setTableName(sqlDataBaseAndTableName2);
                }
            }
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else if (this.aliasName.equalsIgnoreCase("noholdlock")) {
                if (ft.getUpdateLock() == null) {
                    ft.setAliasName("WITH(NOLOCK)");
                }
                else {
                    ft.setAliasName("WITH(NOLOCK, UPDLOCK)");
                    ft.setUpdateLock(null);
                }
            }
            else if (this.aliasName.equalsIgnoreCase("holdlock")) {
                if (ft.getUpdateLock() == null) {
                    ft.setAliasName("WITH(HOLDLOCK)");
                }
                else {
                    ft.setAliasName("WITH(HOLDLOCK, UPDLOCK)");
                    ft.setUpdateLock(null);
                }
            }
            else {
                ft.setAliasName(this.aliasName);
            }
        }
        else if (this.tableName instanceof SelectQueryStatement) {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
        }
        if (this.holdLock != null) {
            if (this.holdLock.equalsIgnoreCase("noholdlock")) {
                if (ft.getUpdateLock() == null) {
                    ft.setHoldLock("WITH(NOLOCK)");
                }
                else {
                    ft.setHoldLock("WITH(NOLOCK, UPDLOCK)");
                    ft.setUpdateLock(null);
                }
            }
            else if (this.holdLock.equalsIgnoreCase("holdlock")) {
                if (ft.getUpdateLock() == null) {
                    ft.setHoldLock("WITH(HOLDLOCK)");
                }
                else {
                    ft.setHoldLock("WITH(HOLDLOCK, UPDLOCK)");
                    ft.setUpdateLock(null);
                }
            }
        }
        if (this.joinClause != null && this.joinClause.equalsIgnoreCase("JOIN")) {
            ft.setJoinClause(" INNER " + this.joinClause);
        }
        else {
            ft.setJoinClause(this.joinClause);
        }
        if (this.indexHint != null) {
            if (this.indexHint.trim().startsWith("WITH")) {
                ft.setIndexHint(this.indexHint);
            }
            else if (ft.getUpdateLock() == null) {
                ft.setIndexHint(" WITH(index(" + this.indexHint + "))");
            }
            else {
                ft.setIndexHint(" WITH(index(" + this.indexHint + "), UPDLOCK)");
                ft.setUpdateLock(null);
            }
        }
        return ft;
    }
    
    public FromTable toSybaseSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        ft.setObjectContext(this.context);
        boolean isFullJoin = false;
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
                throw new ConvertException("Conversion failure..Natural join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
                throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural right join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                throw new ConvertException("Conversion failure..Key join is not supported");
            }
            if (this.joinClause.trim().equalsIgnoreCase("CROSS JOIN")) {
                this.joinClause = null;
            }
            else if (this.joinClause.trim().equalsIgnoreCase("NATURAL INNER JOIN")) {
                this.joinClause = "INNER JOIN";
            }
            else if (this.joinClause.trim().equalsIgnoreCase("FULL JOIN") || this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
                final FromClause fc = new FromClause();
                fc.setObjectContext(this.context);
                final FromTable newFromTable = new FromTable();
                newFromTable.setAliasName(this.aliasName);
                newFromTable.setObjectContext(this.context);
                newFromTable.setIsAS(this.isAS);
                newFromTable.setJoinClause("RIGHT OUTER JOIN");
                newFromTable.setJoinExpression(this.joinExpression);
                newFromTable.setOnOrUsingJoin(this.onOrUsingJoin);
                newFromTable.setTableKeyword(this.tableKeyword);
                newFromTable.setTableName(this.tableName);
                newFromTable.setUsingList(this.UsingList);
                if (vendorSQS.getFromClause() != null) {
                    fc.setFromClause("FROM");
                    final Vector fromList = vendorSQS.getFromClause().getFromItemList();
                    final Vector newFromList = new Vector();
                    for (int i = 0; i < fromList.size(); ++i) {
                        if (fromList.get(i) instanceof FromTable) {
                            final FromTable getFT = fromList.get(i);
                            if (getFT != null && getFT.equals(this)) {
                                newFromList.add(newFromTable);
                            }
                            else if (fromList.get(i) instanceof FromTable) {
                                newFromList.add(fromList.get(i).toSybaseSelect(vembuSQS, vendorSQS));
                            }
                        }
                        else if (fromList.get(i) instanceof FromClause) {
                            fromList.get(i).toSybaseSelect(vembuSQS, vendorSQS);
                        }
                    }
                    fc.setFromItemList(newFromList);
                }
                final SelectQueryStatement sqs = new SelectQueryStatement();
                sqs.setObjectContext(this.context);
                sqs.setFromClause(fc);
                if (vendorSQS.getSelectStatement() != null) {
                    sqs.setSelectStatement(vendorSQS.getSelectStatement().toSybaseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getFetchClause() != null) {
                    sqs.setFetchClause(vendorSQS.getFetchClause().toSybaseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getForUpdateStatement() != null) {
                    sqs.setForUpdateStatement(vendorSQS.getForUpdateStatement().toSybaseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getGroupByStatement() != null) {
                    sqs.setGroupByStatement(vendorSQS.getGroupByStatement().toSybaseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getHavingStatement() != null) {
                    sqs.setHavingStatement(vendorSQS.getHavingStatement().toSybaseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getHierarchicalQueryClause() != null) {
                    sqs.setHierarchicalQueryClause(vendorSQS.getHierarchicalQueryClause().toSybaseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getLimitClause() != null) {
                    sqs.setLimitClause(vendorSQS.getLimitClause().toSybaseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getSetOperatorClause() != null) {
                    sqs.setSetOperatorClause(vendorSQS.getSetOperatorClause().toSybaseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getWhereExpression() != null) {
                    sqs.setWhereExpression(vendorSQS.getWhereExpression().toSybaseSelect(vembuSQS, vendorSQS));
                }
                (this.setOperatorClauseForFullJoin = new SetOperatorClause()).setSelectQueryStatement(sqs);
                this.setOperatorClauseForFullJoin.setSetClause("UNION");
                this.setOperatorClauseForFullJoin.setObjectContext(this.context);
                if (vendorSQS.getWhereExpression() != null) {
                    this.setOperatorClauseForFullJoin.setWhereExpression(vendorSQS.getWhereExpression().toSybaseSelect(vembuSQS, vendorSQS));
                    vendorSQS.setWhereExpression(null);
                }
                ft.setSetOperatorClause(this.setOperatorClauseForFullJoin);
                isFullJoin = true;
            }
            if (this.onOrUsingJoin == null && this.joinExpression != null) {
                ft.setOnOrUsingJoin("ON");
                final WhereExpression we = this.joinExpression.elementAt(0).toSybaseSelect(vembuSQS, vendorSQS);
                final Vector v = new Vector();
                v.addElement(we);
                ft.setJoinExpression(v);
            }
            else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
                this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
            }
            else if (this.joinExpression != null) {
                ft.setOnOrUsingJoin(this.onOrUsingJoin);
                final WhereExpression we = this.joinExpression.elementAt(0);
                final Vector operatorList = we.getOperator();
                for (int j = 0; j < operatorList.size(); ++j) {
                    if (operatorList.elementAt(j) instanceof String && operatorList.get(j).equalsIgnoreCase("AND") && !isFullJoin) {
                        operatorList.setElementAt("AND", j);
                    }
                }
                we.setOperator(operatorList);
                we.toSybaseSelect(vembuSQS, vendorSQS);
                final Vector v2 = new Vector();
                v2.addElement(we);
                ft.setJoinExpression(v2);
            }
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ft.setTableName(((SelectQueryStatement)this.tableName).toSybaseSelect());
            }
            else if (this.tableName instanceof FunctionCalls) {
                ft.setTableName(((FunctionCalls)this.tableName).toSybaseSelect(vembuSQS, vendorSQS));
            }
            else if (this.tableName instanceof WithStatement) {
                ft.setTableName(((WithStatement)this.tableName).toSybase());
            }
            else if (this.tableName instanceof FromClause) {
                ft.setTableName(((FromClause)this.tableName).toSybaseSelect(vembuSQS, vendorSQS));
            }
            else {
                final String table_Name_String = (String)this.tableName;
                final int atIndex = table_Name_String.indexOf("@");
                if (((String)this.tableName).indexOf("..") < 0) {
                    final StringTokenizer toks = new StringTokenizer((String)this.tableName, ".");
                    ft.setTableName(this.tableName);
                }
                else {
                    ft.setTableName(this.tableName);
                }
                final Vector tokenVector = new Vector();
                final String table_Name = (String)this.tableName;
                final StringTokenizer st = new StringTokenizer(table_Name, ".");
                int count = 0;
                while (st.hasMoreTokens()) {
                    tokenVector.add(st.nextToken());
                    ++count;
                }
                if (count == 1 && atIndex != -1 && atIndex != 0) {
                    final String dataBaseTableName = tokenVector.elementAt(0);
                    final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                    final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                    final String sqlDataBaseAndTableName = sqlDataBaseName + ".." + sqlTableName;
                    ft.setTableName(sqlDataBaseAndTableName);
                }
                else if (count == 2 && atIndex != -1) {
                    final String dataBaseTableName = tokenVector.elementAt(1);
                    final int tableAtIndex = dataBaseTableName.indexOf("@");
                    String sqlTableName2 = dataBaseTableName;
                    if (tableAtIndex != -1) {
                        sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                    }
                    final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                    final String sqlDataBaseAndTableName2 = sqlDataBaseName2 + "." + tokenVector.elementAt(0) + "." + sqlTableName2;
                    ft.setTableName(sqlDataBaseAndTableName2);
                }
                String tempTableName = (String)ft.getTableName();
                String tableNameSubString = "";
                int startIndex = tempTableName.indexOf("[");
                if (startIndex != -1) {
                    while (startIndex != -1) {
                        if (startIndex == 0) {
                            tableNameSubString = tempTableName.substring(1);
                            tableNameSubString = "\"" + tableNameSubString;
                            tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                            startIndex = tableNameSubString.indexOf("[");
                        }
                        else {
                            tableNameSubString = tempTableName.substring(0, startIndex);
                            tableNameSubString += "\"";
                            tableNameSubString += tempTableName.substring(startIndex + 1);
                            tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                            startIndex = tableNameSubString.indexOf("[");
                        }
                    }
                    ft.setTableName(tableNameSubString);
                }
            }
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else {
                ft.setAliasName(this.aliasName);
            }
        }
        else if (this.tableName instanceof SelectQueryStatement) {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
        }
        if (!isFullJoin) {
            ft.setJoinClause(this.joinClause);
        }
        else {
            ft.setJoinClause("LEFT OUTER JOIN");
        }
        if (this.indexHint != null) {
            ft.setIndexHint(" ( index " + this.indexHint + ")");
        }
        return ft;
    }
    
    public void changeToOnJoin(final FromTable to_ft, final FromTable from_ft) throws ConvertException {
        int i = 0;
        String s_cn = null;
        if (from_ft != null) {
            if (from_ft.getAliasName() != null) {
                s_cn = from_ft.getAliasName();
            }
            else {
                s_cn = from_ft.getTableName().toString();
            }
        }
        final Vector v_ul = new Vector();
        v_ul.addElement("(");
        for (i = 0; i < this.UsingList.size(); ++i) {
            if (this.UsingList.elementAt(i) instanceof TableColumn) {
                final Vector v_wi = new Vector();
                final TableColumn tc = this.UsingList.elementAt(i);
                tc.setTableName(s_cn);
                v_ul.addElement(tc);
                v_ul.addElement("=");
                final TableColumn tcr = new TableColumn();
                tcr.setColumnName(this.UsingList.elementAt(i).getColumnName());
                if (this.aliasName != null) {
                    tcr.setTableName(this.aliasName);
                }
                else {
                    tcr.setTableName(this.tableName.toString());
                }
                v_ul.addElement(tcr);
            }
            else {
                final String s_cd = this.UsingList.elementAt(i);
                if (s_cd.equals(",")) {
                    v_ul.addElement("AND");
                }
                else {
                    v_ul.addElement(s_cd);
                }
            }
        }
        v_ul.addElement(")");
        to_ft.setJoinExpression(v_ul);
        to_ft.setOnOrUsingJoin("ON");
    }
    
    public Object clone() {
        final FromTable ft = new FromTable();
        ft.setObjectContext(this.context);
        ft.setIndexHint(this.indexHint);
        ft.setSetOperatorClause(this.setOperatorClauseForFullJoin);
        ft.setTableName(this.tableName);
        ft.setAliasName(this.aliasName);
        ft.setIsAS(this.isAS);
        ft.setJoinClause(this.joinClause);
        ft.setOnOrUsingJoin(this.onOrUsingJoin);
        ft.setJoinExpression(this.joinExpression);
        ft.setUsingList(this.UsingList);
        ft.setLockTableStatement(this.lockTableStatement);
        ft.setWith(this.with);
        ft.setLock(this.lock);
        ft.setTableKeyword(this.tableKeyword);
        ft.setOuter(this.outer);
        ft.setOuterOpenBrace(this.outerOpenBrace);
        ft.setOuterClosedBrace(this.outerClosedBrace);
        ft.setUpdateLock(this.updateLock);
        ft.setHoldLock(this.holdLock);
        ft.setFromClauseOpenBraces(this.fromClauseOpenBraces);
        ft.setFromClauseClosedBraces(this.fromClauseClosedBraces);
        ft.setFromClause(this.fc);
        ft.setOrigTableName(this.origTableName);
        ft.setCommentClass(this.commentObj);
        return ft;
    }
    
    private WhereExpression getClonedWhereExpression(final WhereExpression whereExpression) {
        final WhereExpression clonedWhereExpression = new WhereExpression();
        clonedWhereExpression.setOpenBrace(whereExpression.getOpenBrace());
        clonedWhereExpression.setCloseBrace(whereExpression.getCloseBrace());
        clonedWhereExpression.setOperator(whereExpression.getOperator());
        final Vector whereItemList = new Vector();
        final Vector whereItems = whereExpression.getWhereItems();
        final Vector clonedWhereItems = new Vector();
        clonedWhereExpression.setOperator((Vector)whereExpression.getOperator().clone());
        for (int i = 0; i < whereItems.size(); ++i) {
            if (whereItems.elementAt(i) instanceof WhereItem) {
                final WhereItem whereItem = (WhereItem)whereItems.elementAt(i).clone();
                whereItemList.addElement(whereItem);
            }
            else if (whereItems.elementAt(i) instanceof WhereExpression) {
                whereItemList.addElement(this.getClonedWhereExpression(whereItems.elementAt(i)));
            }
        }
        clonedWhereExpression.setWhereItem(whereItemList);
        return clonedWhereExpression;
    }
    
    private Vector getTablesOnlyList(final Vector vendorList) {
        final Vector vembuList = new Vector();
        for (int i = 0; i < vendorList.size(); ++i) {
            if (vendorList.elementAt(i) instanceof FromTable) {
                final FromTable ft = vendorList.get(i);
                final FromTable fromTable = new FromTable();
                fromTable.setTableName(ft.getTableName());
                fromTable.setAliasName(ft.getAliasName());
                fromTable.setJoinClause(null);
                fromTable.setJoinExpression(null);
                fromTable.setOnOrUsingJoin(null);
                fromTable.setUsingList(null);
                vembuList.addElement(fromTable);
            }
            else if (vendorList.elementAt(i) instanceof FromClause) {
                final FromClause newFC = vendorList.get(i);
                final Vector newFromItemsList = newFC.getFromItemList();
                this.getTablesOnlyList(newFromItemsList);
            }
        }
        return vembuList;
    }
    
    public FromTable toOracleSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                final SelectQueryStatement s = (SelectQueryStatement)this.tableName;
                ft.setTableName(s.toOracleSelect());
            }
            else if (this.tableName instanceof FunctionCalls) {
                if (this.tableKeyword != null) {
                    final FunctionCalls fn = ((FunctionCalls)this.tableName).toOracleSelect(vembuSQS, vendorSQS);
                    if (fn != null && fn.toString().indexOf("CAST") != -1) {
                        ft.setTableKeyword("TABLE(");
                    }
                    ft.setTableName(fn);
                }
                else if (this.tableName.toString().toUpperCase().indexOf("(NOLOCK)") == -1) {
                    ft.setTableKeyword("TABLE(");
                    ft.setTableName(((FunctionCalls)this.tableName).toOracleSelect(vembuSQS, vendorSQS));
                }
                else {
                    ft.setTableName(((FunctionCalls)this.tableName).getFunctionNameAsAString());
                }
            }
            else if (this.tableName instanceof WithStatement) {
                ft.setTableName(((WithStatement)this.tableName).toOracle());
            }
            else if (this.tableName instanceof FromClause) {
                final FromClause fromCl = (FromClause)this.tableName;
                ft.setTableName(fromCl.toOracleSelect(vembuSQS, vendorSQS));
            }
            else if (this.tableName instanceof ValuesClause && this.columnAliasList != null) {
                final ValuesClause vc = (ValuesClause)this.tableName;
                this.convertTableValueConstructorToSelectUnion(vc, this.columnAliasList, vembuSQS, vendorSQS);
                ft.setTableName("DUAL");
            }
            else {
                final String table_Name_String = (String)this.tableName;
                final int dotDotIndex = table_Name_String.indexOf("..");
                final String table_name = StringFunctions.replaceFirst(".", "..", (String)this.tableName);
                if (!(ft.getTableName() instanceof SelectQueryStatement)) {
                    ft.setTableName(table_name);
                }
                final Vector tokenVector = new Vector();
                final StringTokenizer st = new StringTokenizer(table_name, ".");
                int count = 0;
                while (st.hasMoreTokens()) {
                    tokenVector.add(st.nextToken());
                    ++count;
                }
                boolean ownerExistsInSrc = false;
                if (count == 3) {
                    tokenVector.remove(1);
                    count = 2;
                    ownerExistsInSrc = true;
                }
                if (count == 1) {
                    ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier((String)ft.getTableName(), SwisSQLUtils.getKeywords(1), null, 1));
                }
                else if (count == 2) {
                    final String dataBaseName = tokenVector.elementAt(0);
                    String tableName = tokenVector.elementAt(1);
                    String oracleDataBaseAndTableName = "";
                    tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(1), null, 1);
                    if (dataBaseName.equalsIgnoreCase("DBO") && this.isTenroxRequirement) {
                        oracleDataBaseAndTableName = "PUSER." + tableName;
                    }
                    else if (dataBaseName.equalsIgnoreCase("DBO") || (dataBaseName.equalsIgnoreCase("tempdb") && (ownerExistsInSrc || dotDotIndex != -1))) {
                        oracleDataBaseAndTableName = tableName;
                    }
                    else if (SwisSQLOptions.removeOracleSchemaQualifier) {
                        oracleDataBaseAndTableName = tableName;
                    }
                    else {
                        oracleDataBaseAndTableName = dataBaseName + "." + tableName;
                    }
                    ft.setTableName(oracleDataBaseAndTableName);
                }
                else if (count == 4) {
                    final String serverName = tokenVector.elementAt(0);
                    final String databaseName = tokenVector.elementAt(1);
                    String tableName2 = tokenVector.elementAt(3);
                    tableName2 = CustomizeUtil.objectNamesToQuotedIdentifier(tableName2, SwisSQLUtils.getKeywords(1), null, 1);
                    ft.setTableName(databaseName + "." + tableName2 + "@" + serverName);
                }
                String tempTableName = (String)ft.getTableName();
                String tableNameSubString = "";
                int startIndex = tempTableName.indexOf("[");
                if (startIndex != -1) {
                    while (startIndex != -1) {
                        if (startIndex == 0) {
                            tableNameSubString = tempTableName.substring(1);
                            final int endIndex = tableNameSubString.indexOf("]");
                            String temp = tableNameSubString.substring(0, endIndex);
                            if (SwisSQLOptions.retainQuotedIdentifierForOracle || temp.indexOf(" ") != -1) {
                                temp = "\"" + temp + "\"";
                            }
                            tableNameSubString = temp + tableNameSubString.substring(endIndex + 1);
                        }
                        else {
                            tableNameSubString = tempTableName.substring(0, startIndex);
                            final String temp2 = tempTableName.substring(startIndex + 1);
                            final int endIndex2 = temp2.indexOf("]");
                            final String token = temp2.substring(0, endIndex2);
                            if (SwisSQLOptions.retainQuotedIdentifierForOracle || token.indexOf(" ") != -1) {
                                tableNameSubString = tableNameSubString + "\"" + token + "\"" + temp2.substring(endIndex2 + 1);
                            }
                            else {
                                tableNameSubString = tableNameSubString + token + temp2.substring(endIndex2 + 1);
                            }
                        }
                        tempTableName = tableNameSubString;
                        startIndex = tableNameSubString.indexOf("[");
                    }
                    ft.setTableName(tableNameSubString);
                }
                String tt = (String)ft.getTableName();
                if (tt.startsWith("#")) {
                    tt = tt.substring(1);
                }
                if (tt.startsWith("#")) {
                    tt = tt.substring(1);
                }
                if (tt.startsWith("@")) {
                    tt = "\"" + tt + "\"";
                }
                ft.setTableName(tt);
                if (this.with != null && this.lock != null) {
                    ft.setLock(this.lock);
                    if (this.lock.equalsIgnoreCase("TABLOCK") || this.lock.equalsIgnoreCase("UPDLOCK")) {
                        this.lock = "SHARE";
                    }
                    if (this.lock.equalsIgnoreCase("TABLOCKX")) {
                        this.lock = "EXCLUSIVE";
                    }
                    if (!this.lock.equalsIgnoreCase("NOLOCK") && !this.lock.equalsIgnoreCase("ROWLOCK") && !this.lock.equalsIgnoreCase("XLOCK")) {
                        this.lockTableStatement = "LOCK TABLE " + tt + " IN " + this.lock + " MODE;";
                        if (SwisSQLOptions.handleLOCK_HINTSforOracle) {
                            ft.setLockTableStatement(this.lockTableStatement);
                        }
                    }
                    if (this.lock.equalsIgnoreCase("HOLDLOCK")) {
                        ft.setLockTableStatement(null);
                        final ForUpdateStatement forUpdateStmt = new ForUpdateStatement();
                        forUpdateStmt.setForUpdateClause("FOR UPDATE");
                        forUpdateStmt.setForUpdateQualifier("OF");
                        forUpdateStmt.setNoWaitQualifier("NOWAIT");
                        if (SwisSQLOptions.handleLOCK_HINTSforOracle) {
                            vembuSQS.setForUpdateStatement(forUpdateStmt);
                        }
                    }
                }
            }
            if (this.tableName.toString().startsWith("@")) {
                this.tableName = "\"" + this.tableName + "\"";
            }
        }
        if (this.origTableName != null) {
            ft.setOrigTableName(this.origTableName);
        }
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
                throw new ConvertException(" Natural join are not allowed in Oracle");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
                throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural right join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                throw new ConvertException("Conversion failure..Key join is not supported");
            }
            if (SwisSQLAPI.ANSIJOIN_ForOracle) {
                ft.setJoinClause(this.joinClause);
                ft.setOnOrUsingJoin(this.onOrUsingJoin);
                final Vector newJoinExpression = new Vector();
                if (this.joinExpression != null) {
                    for (int jCount = 0; jCount < this.joinExpression.size(); ++jCount) {
                        final Object o = this.joinExpression.get(jCount);
                        if (o instanceof String) {
                            newJoinExpression.add(o);
                        }
                        else if (o instanceof FunctionCalls) {
                            final FunctionCalls fc = (FunctionCalls)o;
                            newJoinExpression.add(fc.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else if (o instanceof SelectQueryStatement) {
                            final SelectQueryStatement sqsNew = (SelectQueryStatement)o;
                            newJoinExpression.add(sqsNew.toOracleSelect());
                        }
                        else if (o instanceof FromClause) {
                            final FromClause fcl = (FromClause)o;
                            newJoinExpression.add(fcl.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else if (o instanceof WhereExpression) {
                            final WhereExpression wexp = (WhereExpression)o;
                            newJoinExpression.add(wexp.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else if (o instanceof FromTable) {
                            final FromTable ftNew = (FromTable)o;
                            newJoinExpression.add(ftNew.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else if (o instanceof TableColumn) {
                            final TableColumn tcl = (TableColumn)o;
                            newJoinExpression.add(tcl.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else if (o instanceof WhereItem) {
                            final WhereItem wit = (WhereItem)o;
                            newJoinExpression.add(wit.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else if (o instanceof SelectColumn) {
                            final SelectColumn scl = (SelectColumn)o;
                            newJoinExpression.add(scl.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else if (o instanceof WhereColumn) {
                            final WhereColumn wcl = (WhereColumn)o;
                            newJoinExpression.add(wcl.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else if (o instanceof SelectStatement) {
                            final SelectStatement sst = (SelectStatement)o;
                            newJoinExpression.add(sst.toOracleSelect(vembuSQS, vendorSQS));
                        }
                        else {
                            newJoinExpression.add(o.toString());
                        }
                    }
                }
                ft.setJoinExpression(newJoinExpression);
            }
            else if (this.joinClause.trim().equalsIgnoreCase("FULL JOIN") || this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
                this.insertItem(vembuSQS, vendorSQS, 2, true);
            }
            else if ((this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("ON")) || (this.onOrUsingJoin == null && this.joinExpression != null)) {
                if (this.joinClause.equalsIgnoreCase("JOIN") || this.joinClause.equalsIgnoreCase("INNER JOIN")) {
                    this.insertItem(vembuSQS, vendorSQS, 1, true);
                }
                else {
                    this.moveJoinExpressionToWhereExpression(ft, vembuSQS, vendorSQS, true);
                }
            }
            else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
                this.insertItem(vembuSQS, vendorSQS, 3, true);
            }
            else if (this.joinClause.trim().equalsIgnoreCase("cross apply")) {
                ft.setJoinClause(" CROSS JOIN ");
                if (ft.getTableName() instanceof SelectQueryStatement) {
                    final SelectQueryStatement sqs = (SelectQueryStatement)ft.getTableName();
                    if (sqs.getWhereExpression() != null) {
                        final WhereExpression we = sqs.getWhereExpression();
                        if (vendorSQS.getWhereExpression() == null) {
                            vendorSQS.setWhereExpression(we);
                            sqs.setWhereExpression(null);
                        }
                        else {
                            final WhereExpression weSrc = vendorSQS.getWhereExpression();
                            final Vector weOperators = we.getOperator();
                            final Vector colExp = we.getWhereItems();
                            for (int i = 0; i < colExp.size(); ++i) {
                                if (colExp.get(i) instanceof WhereItem) {
                                    weSrc.addWhereItem(colExp.get(i));
                                    if (i == 0) {
                                        weSrc.addOperator("AND");
                                    }
                                    else {
                                        weSrc.addOperator(weOperators.get(i - 1));
                                    }
                                }
                                sqs.setWhereExpression(null);
                            }
                        }
                    }
                }
            }
            else if (this.joinClause.trim().equalsIgnoreCase("outer apply")) {
                ft.setJoinClause(" LEFT OUTER JOIN ");
                ft.setOnOrUsingJoin("ON");
                final Vector newJoinExp = new Vector();
                final WhereExpression weNew = new WhereExpression();
                final Vector wiVector = new Vector();
                final WhereItem wiNew = new WhereItem();
                final WhereColumn leftWhereCol = new WhereColumn();
                final WhereColumn rightWhereCol = new WhereColumn();
                final Vector whereCols = new Vector();
                final String value = "1";
                whereCols.add(value);
                leftWhereCol.setColumnExpression(whereCols);
                rightWhereCol.setColumnExpression(whereCols);
                wiNew.setLeftWhereExp(leftWhereCol);
                wiNew.setRightWhereExp(rightWhereCol);
                wiNew.setOperator("=");
                wiVector.add(wiNew);
                weNew.setWhereItem(wiVector);
                newJoinExp.add(weNew);
                ft.setJoinExpression(newJoinExp);
            }
        }
        ft.setIsAS(false);
        if (this.aliasName != null) {
            this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords(1), null, 1);
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else {
                ft.setAliasName(this.aliasName);
            }
            if ((this.aliasName.startsWith("[") && this.aliasName.endsWith("]")) || (this.aliasName.startsWith("`") && this.aliasName.endsWith("`"))) {
                this.aliasName = this.aliasName.substring(1, this.aliasName.length() - 1);
                if (SwisSQLOptions.retainQuotedIdentifierForOracle || this.aliasName.indexOf(32) != -1) {
                    this.aliasName = "\"" + this.aliasName + "\"";
                }
                ft.setAliasName(this.aliasName);
            }
        }
        return ft;
    }
    
    public FromTable toInformixSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
                throw new ConvertException("Conversion failure..Natural join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
                throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural right join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                throw new ConvertException("Conversion failure..Key join is not supported");
            }
            if (this.joinClause.trim().equalsIgnoreCase("FULL JOIN")) {
                ft.setJoinClause(null);
                this.insertItem(vembuSQS, vendorSQS, 2, true);
            }
            else if ((this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("ON")) || (this.onOrUsingJoin == null && this.joinExpression != null)) {
                if (this.joinClause.equalsIgnoreCase("JOIN") || this.joinClause.equalsIgnoreCase("INNER JOIN")) {
                    ft.setJoinClause(null);
                    this.insertItem(vembuSQS, vendorSQS, 1, true);
                }
                else {
                    ft.setJoinClause("OUTER");
                    this.insertItem(vembuSQS, vendorSQS, 4, true);
                }
            }
            else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
                this.insertItem(vembuSQS, vendorSQS, 3, true);
            }
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ft.setTableName(((SelectQueryStatement)this.tableName).toInformixSelect());
            }
            else if (this.tableName instanceof FunctionCalls) {
                ft.setTableName(((FunctionCalls)this.tableName).toInformixSelect(vembuSQS, vendorSQS));
            }
            else if (this.tableName instanceof WithStatement) {
                ft.setTableName(((WithStatement)this.tableName).toInformix());
            }
            else if (this.tableName instanceof FromClause) {
                ft.setTableName(((FromClause)this.tableName).toInformixSelect(vembuSQS, vendorSQS));
            }
            else {
                final String table_Name_String = (String)this.tableName;
                final int atIndex = table_Name_String.indexOf("@");
                final String table_name = StringFunctions.replaceFirst(".", "..", (String)this.tableName);
                if (!(ft.getTableName() instanceof SelectQueryStatement)) {
                    ft.setTableName(table_name);
                }
                final Vector tokenVector = new Vector();
                final String table_Name = (String)this.tableName;
                final StringTokenizer st = new StringTokenizer(table_Name, ".");
                int count = 0;
                while (st.hasMoreTokens()) {
                    tokenVector.add(st.nextToken());
                    ++count;
                }
                if (count == 1 && atIndex != -1) {
                    final String dataBaseTableName = tokenVector.elementAt(0);
                    final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                    final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                    final String sqlDataBaseAndTableName = sqlDataBaseName + "." + sqlTableName;
                    ft.setTableName(sqlDataBaseAndTableName);
                }
                else if (count == 2 && atIndex != -1) {
                    final String dataBaseTableName = tokenVector.elementAt(1);
                    final int tableAtIndex = dataBaseTableName.indexOf("@");
                    final String sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                    final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                    final String sqlDataBaseAndTableName2 = sqlDataBaseName2 + "." + tokenVector.elementAt(0) + "." + sqlTableName2;
                    ft.setTableName(sqlDataBaseAndTableName2);
                }
                String tempTableName = (String)ft.getTableName();
                String tableNameSubString = "";
                int startIndex = tempTableName.indexOf("[");
                if (startIndex != -1) {
                    while (startIndex != -1) {
                        if (startIndex == 0) {
                            tableNameSubString = tempTableName.substring(1);
                            tableNameSubString = "\"" + tableNameSubString;
                            tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                            startIndex = tableNameSubString.indexOf("[");
                        }
                        else {
                            tableNameSubString = tempTableName.substring(0, startIndex);
                            tableNameSubString += "\"";
                            tableNameSubString += tempTableName.substring(startIndex + 1);
                            tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                            startIndex = tableNameSubString.indexOf("[");
                        }
                    }
                    ft.setTableName(tableNameSubString);
                }
            }
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            ft.setAliasName(this.aliasName);
        }
        else if (this.tableName instanceof SelectQueryStatement) {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
        }
        return ft;
    }
    
    public FromTable toTimesTenSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ft.setTableName(((SelectQueryStatement)this.tableName).toTimesTenSelect());
            }
            else if (!(this.tableName instanceof FunctionCalls)) {
                if (this.tableName instanceof FromClause) {
                    final FromClause fromCl = (FromClause)this.tableName;
                    ft.setTableName(fromCl.toTimesTenSelect(vembuSQS, vendorSQS));
                }
                else {
                    final String table_Name_String = (String)this.tableName;
                    final int dotDotIndex = table_Name_String.indexOf("..");
                    ft.setTableName(table_Name_String);
                    if (dotDotIndex != -1) {
                        ft.setTableName(table_Name_String.substring(table_Name_String.lastIndexOf(".") + 1, table_Name_String.length()));
                        ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier((String)ft.getTableName(), SwisSQLUtils.getKeywords(10), null, 10));
                    }
                    else {
                        final Vector tokenVector = new Vector();
                        final StringTokenizer st = new StringTokenizer(table_Name_String, ".");
                        int count = 0;
                        while (st.hasMoreTokens()) {
                            tokenVector.add(st.nextToken());
                            ++count;
                        }
                        if (count == 1) {
                            ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier((String)ft.getTableName(), SwisSQLUtils.getKeywords(10), null, 10));
                        }
                        else if (count == 2) {
                            final String ownerName = tokenVector.elementAt(0);
                            String tableName = tokenVector.elementAt(1);
                            tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(10), null, 10);
                            if (ownerName.equalsIgnoreCase("DBO")) {
                                ft.setTableName(tableName);
                            }
                            else {
                                ft.setTableName(ownerName + "." + tableName);
                            }
                        }
                        else if (count == 3) {
                            final String dataBaseName = tokenVector.elementAt(0);
                            final String ownerName2 = tokenVector.elementAt(1);
                            String tableName2 = tokenVector.elementAt(2);
                            tableName2 = CustomizeUtil.objectNamesToQuotedIdentifier(tableName2, SwisSQLUtils.getKeywords(10), null, 10);
                            if (ownerName2.equalsIgnoreCase("DBO")) {
                                ft.setTableName(tableName2);
                            }
                            else {
                                ft.setTableName(ownerName2 + "." + tableName2);
                            }
                        }
                    }
                    String tempTableName = (String)ft.getTableName();
                    String tableNameSubString = "";
                    int startIndex = tempTableName.indexOf("[");
                    if (startIndex != -1) {
                        while (startIndex != -1) {
                            if (startIndex == 0) {
                                tableNameSubString = tempTableName.substring(1);
                                final int endIndex = tableNameSubString.indexOf("]");
                                String temp = tableNameSubString.substring(0, endIndex);
                                if (temp.indexOf(" ") != -1) {
                                    temp = "\"" + temp + "\"";
                                }
                                tableNameSubString = temp + tableNameSubString.substring(endIndex + 1);
                            }
                            else {
                                tableNameSubString = tempTableName.substring(0, startIndex);
                                final String temp2 = tempTableName.substring(startIndex + 1);
                                final int endIndex2 = temp2.indexOf("]");
                                final String token = temp2.substring(0, endIndex2);
                                if (token.indexOf(" ") != -1) {
                                    tableNameSubString = tableNameSubString + "\"" + token + "\"" + temp2.substring(endIndex2 + 1);
                                }
                                else {
                                    tableNameSubString = tableNameSubString + token + temp2.substring(endIndex2 + 1);
                                }
                            }
                            tempTableName = tableNameSubString;
                            startIndex = tableNameSubString.indexOf("[");
                        }
                        ft.setTableName(tableNameSubString);
                    }
                    String tt = (String)ft.getTableName();
                    if (tt.equalsIgnoreCase("dual") || tt.equalsIgnoreCase("sys.dual")) {
                        tt = "MONITOR";
                    }
                    ft.setTableName(tt);
                }
            }
        }
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
                throw new ConvertException("Natural joins are not allowed in TimesTen");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
                throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural right join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                throw new ConvertException("Conversion failure..Key join is not supported");
            }
            if (this.joinClause.trim().equalsIgnoreCase("FULL JOIN") || this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
                this.insertItem(vembuSQS, vendorSQS, 2, false);
            }
            else if ((this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("ON")) || (this.onOrUsingJoin == null && this.joinExpression != null)) {
                if (this.joinClause.equalsIgnoreCase("JOIN") || this.joinClause.equalsIgnoreCase("INNER JOIN")) {
                    this.insertItem(vembuSQS, vendorSQS, 1, false);
                }
                else {
                    this.moveJoinExpressionToWhereExpression(ft, vembuSQS, vendorSQS, false);
                }
            }
            else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
                this.insertItem(vembuSQS, vendorSQS, 3, false);
            }
        }
        ft.setIsAS(false);
        if (this.aliasName != null) {
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else {
                ft.setAliasName(this.aliasName = CustomizeUtil.objectNamesToQuotedIdentifier(this.aliasName, SwisSQLUtils.getKeywords(10), null, 10));
            }
        }
        return ft;
    }
    
    public FromTable toNetezzaSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        ft.setCommentClass(this.commentObj);
        ft.setTableKeyword(this.tableKeyword);
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ft.setTableName(((SelectQueryStatement)this.tableName).toNetezzaSelect());
            }
            else {
                if (this.tableName instanceof FunctionCalls) {
                    throw new ConvertException("Functions yet to be supported in table names");
                }
                if (this.tableName instanceof WithStatement) {
                    ft.setTableName(((WithStatement)this.tableName).toNetezza());
                }
                else if (this.tableName instanceof FromClause) {
                    ft.setTableName(((FromClause)this.tableName).toNetezzaSelect(vembuSQS, vendorSQS));
                }
                else {
                    final String table_Name_String = (String)this.tableName;
                    final int atIndex = table_Name_String.indexOf("@");
                    final String replaceDotDot = StringFunctions.replaceFirst(".", "..", (String)this.tableName);
                    ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier(replaceDotDot, SwisSQLUtils.getKeywords(11), null, 11));
                    final Vector tokenVector = new Vector();
                    final String table_Name = (String)this.tableName;
                    final StringTokenizer st = new StringTokenizer(table_Name, ".");
                    int count = 0;
                    while (st.hasMoreTokens()) {
                        tokenVector.add(st.nextToken());
                        ++count;
                    }
                    if (count == 1 && atIndex != -1) {
                        final String dataBaseTableName = tokenVector.elementAt(0);
                        final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                        final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                        final String sqlDataBaseAndTableName = sqlDataBaseName + "." + sqlTableName;
                        ft.setTableName(sqlDataBaseAndTableName);
                    }
                    else if (count == 2 && atIndex != -1) {
                        final String dataBaseTableName = tokenVector.elementAt(1);
                        final int tableAtIndex = dataBaseTableName.indexOf("@");
                        if (tableAtIndex != -1) {
                            final String sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                            final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                            final String sqlDataBaseAndTableName2 = sqlDataBaseName2 + "." + tokenVector.elementAt(0) + "." + sqlTableName2;
                            ft.setTableName(sqlDataBaseAndTableName2);
                        }
                        else {
                            ft.setTableName(this.tableName);
                        }
                    }
                    String tempTableName = (String)ft.getTableName();
                    String tableNameSubString = "";
                    int startIndex = tempTableName.indexOf("[");
                    if (startIndex != -1) {
                        while (startIndex != -1) {
                            if (startIndex == 0) {
                                tableNameSubString = tempTableName.substring(1);
                                tableNameSubString = "\"" + tableNameSubString;
                                tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                                startIndex = tableNameSubString.indexOf("[");
                            }
                            else {
                                tableNameSubString = tempTableName.substring(0, startIndex);
                                tableNameSubString += "\"";
                                tableNameSubString += tempTableName.substring(startIndex + 1);
                                tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", "]", tableNameSubString));
                                startIndex = tableNameSubString.indexOf("[");
                            }
                        }
                        ft.setTableName(tableNameSubString);
                    }
                    if (count == 1 && atIndex == -1) {
                        ft.setTableName(CustomizeUtil.objectNamesToQuotedIdentifier((String)ft.getTableName(), SwisSQLUtils.getKeywords(11), null, 11));
                    }
                    else if (count == 2 && atIndex == -1) {
                        String ownerName = tokenVector.elementAt(0);
                        String tableName = tokenVector.elementAt(1);
                        ownerName = CustomizeUtil.objectNamesToQuotedIdentifier(ownerName, SwisSQLUtils.getKeywords(11), null, 11);
                        tableName = CustomizeUtil.objectNamesToQuotedIdentifier(tableName, SwisSQLUtils.getKeywords(11), null, 11);
                        if (ownerName.equalsIgnoreCase("DBO")) {
                            ft.setTableName(tableName);
                        }
                        else if (SwisSQLOptions.renameTableNameAsSchemName_TableName && !ownerName.startsWith("\"") && !tableName.startsWith("\"")) {
                            ft.setTableName(ownerName + "." + ownerName + "_" + tableName);
                        }
                        else {
                            ft.setTableName(ownerName + "." + tableName);
                        }
                    }
                    else if (count == 3 && atIndex == -1) {
                        String dataBaseName = tokenVector.elementAt(0);
                        String ownerName2 = tokenVector.elementAt(1);
                        String tableName2 = tokenVector.elementAt(2);
                        dataBaseName = CustomizeUtil.objectNamesToQuotedIdentifier(dataBaseName, SwisSQLUtils.getKeywords(11), null, 11);
                        ownerName2 = CustomizeUtil.objectNamesToQuotedIdentifier(ownerName2, SwisSQLUtils.getKeywords(11), null, 11);
                        tableName2 = CustomizeUtil.objectNamesToQuotedIdentifier(tableName2, SwisSQLUtils.getKeywords(11), null, 11);
                        if (ownerName2.equalsIgnoreCase("DBO")) {
                            ft.setTableName(tableName2);
                        }
                        else if (SwisSQLOptions.renameTableNameAsSchemName_TableName && !ownerName2.startsWith("\"") && !tableName2.startsWith("\"") && !dataBaseName.startsWith("\"")) {
                            ft.setTableName(dataBaseName + "." + ownerName2 + "." + ownerName2 + "_" + tableName2);
                        }
                        else {
                            ft.setTableName(dataBaseName + "." + ownerName2 + "." + tableName2);
                        }
                    }
                }
            }
        }
        if (this.onOrUsingJoin == null && this.joinExpression != null) {
            ft.setOnOrUsingJoin("ON");
            final WhereExpression we = this.joinExpression.elementAt(0).toNetezzaSelect(vembuSQS, vendorSQS);
            final Vector v = new Vector();
            v.addElement(we);
            ft.setJoinExpression(v);
        }
        else if (this.onOrUsingJoin != null && this.onOrUsingJoin.equalsIgnoreCase("USING")) {
            this.changeToOnJoin(ft, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
        }
        else if (this.joinExpression != null) {
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            final WhereExpression we = this.joinExpression.elementAt(0);
            final Vector operatorList = we.getOperator();
            we.setOperator(operatorList);
            we.toNetezzaSelect(vembuSQS, vendorSQS);
            final Vector v2 = new Vector();
            v2.addElement(we);
            ft.setJoinExpression(v2);
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null && (!this.aliasName.equalsIgnoreCase("partition") || this.columnAliasList == null)) {
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', '\"'));
            }
            else {
                ft.setAliasName(this.aliasName);
            }
        }
        else if (this.tableName instanceof SelectQueryStatement) {
            ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
        }
        ft.setJoinClause(this.joinClause);
        return ft;
    }
    
    private void moveJoinExpressionToWhereExpression(final FromTable ft, final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS, final boolean toOracle) throws ConvertException {
        WhereExpression joinWE = null;
        final Vector whereItemList = new Vector();
        final Vector operatorList = new Vector();
        final Vector whereItemListWithoutJoin = new Vector();
        final Vector operatorListWithoutJoin = new Vector();
        final Object obj = this.joinExpression.elementAt(0);
        if (obj instanceof WhereExpression) {
            joinWE = (WhereExpression)obj;
            joinWE.loadWhereItemsOperators(whereItemList, operatorList);
        }
        SelectQueryStatement selectQueryStatement = null;
        SelectStatement selectStatement = null;
        Vector selectItemList = null;
        FromClause fromClause = null;
        Vector fromItemList = null;
        FromTable fromTable = null;
        WhereExpression whereExpression = null;
        boolean isRightOuterJoin = false;
        for (int i = 0; i < whereItemList.size(); ++i) {
            final WhereItem whereItem = whereItemList.elementAt(i);
            final Vector operators = operatorList.elementAt(i);
            WhereColumn leftItem = null;
            Vector leftColumnExpression = null;
            WhereColumn rightItem = null;
            Vector rightColumnExpression = null;
            if (whereItem != null) {
                leftItem = whereItem.getLeftWhereExp();
                if (leftItem != null) {
                    leftColumnExpression = leftItem.getColumnExpression();
                }
                rightItem = whereItem.getRightWhereExp();
                if (rightItem != null) {
                    rightColumnExpression = rightItem.getColumnExpression();
                }
            }
            if (rightColumnExpression != null) {}
            if (((leftColumnExpression != null && !(leftColumnExpression.elementAt(0) instanceof TableColumn)) || (rightColumnExpression != null && !(rightColumnExpression.elementAt(0) instanceof TableColumn)) || leftColumnExpression == null || rightColumnExpression == null) && toOracle) {
                String oneOfTheTableNames = null;
                if (leftColumnExpression != null && leftColumnExpression.elementAt(0) instanceof TableColumn) {
                    final TableColumn leftTableColumn = leftColumnExpression.elementAt(0);
                    oneOfTheTableNames = leftTableColumn.getTableName();
                }
                else if (rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof TableColumn) {
                    final TableColumn rightTableColumn = rightColumnExpression.elementAt(0);
                    oneOfTheTableNames = rightTableColumn.getTableName();
                }
                if (this.aliasName == null && this.tableName instanceof String) {
                    this.aliasName = this.tableName.toString();
                }
                else if (this.aliasName != null || !(this.tableName instanceof String)) {}
                if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") || this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                    if ((this.tableName instanceof String && this.tableName.toString().equalsIgnoreCase(oneOfTheTableNames)) || (this.aliasName != null && this.aliasName.equalsIgnoreCase(oneOfTheTableNames)) || oneOfTheTableNames == null) {
                        if (selectQueryStatement == null) {
                            if (this.tableName != null && this.tableName instanceof SelectQueryStatement) {
                                if (((SelectQueryStatement)this.tableName).getWhereExpression() != null) {
                                    final WhereExpression orgWhereExpression = ((SelectQueryStatement)this.tableName).getWhereExpression();
                                    final WhereExpression weClone = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                                    whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone);
                                    this.collectAllWhereColumnsInWhereExpression(whereExpression);
                                    orgWhereExpression.addWhereExpression(whereExpression);
                                    orgWhereExpression.addOperator("AND");
                                    ft.setAliasName(this.aliasName);
                                    ft.setTableName(this.tableName);
                                    selectQueryStatement = ((SelectQueryStatement)this.tableName).toOracleSelect();
                                }
                                else {
                                    final WhereExpression weClone2 = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                                    whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone2);
                                    ((SelectQueryStatement)this.tableName).setWhereExpression(whereExpression);
                                    this.collectAllWhereColumnsInWhereExpression(whereExpression);
                                    ft.setAliasName(this.aliasName);
                                    ft.setTableName(this.tableName);
                                    selectQueryStatement = ((SelectQueryStatement)this.tableName).toOracleSelect();
                                }
                            }
                            else {
                                selectQueryStatement = new SelectQueryStatement();
                                selectStatement = new SelectStatement();
                                selectStatement.setSelectClause("SELECT");
                                selectItemList = new Vector();
                                selectItemList.addElement("*");
                                selectStatement.setSelectItemList(selectItemList);
                                fromClause = new FromClause();
                                fromClause.setFromClause("FROM");
                                fromItemList = new Vector();
                                fromTable = new FromTable();
                                fromTable.setTableName(this.tableName);
                                fromTable.setAliasName(this.aliasName);
                                fromItemList.add(fromTable);
                                fromClause.setFromItemList(fromItemList);
                                selectQueryStatement.setSelectStatement(selectStatement);
                                selectQueryStatement.setFromClause(fromClause);
                                final WhereExpression weClone2 = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                                whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone2);
                                selectQueryStatement.setWhereExpression(whereExpression);
                            }
                        }
                        else {
                            final WhereExpression weClone2 = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                            whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone2);
                            selectQueryStatement.setWhereExpression(whereExpression);
                        }
                        ft.setAliasName(this.aliasName);
                        ft.setTableName(selectQueryStatement);
                    }
                    else {
                        if (selectQueryStatement == null) {
                            selectQueryStatement = new SelectQueryStatement();
                            selectStatement = new SelectStatement();
                            selectStatement.setSelectClause("SELECT");
                            selectItemList = new Vector();
                            selectItemList.addElement(this.aliasName + ".*");
                            selectStatement.setSelectItemList(selectItemList);
                            fromClause = new FromClause();
                            final FromClause vendorFromClause = vendorSQS.getFromClause();
                            final Vector vendorFromItemList = vendorFromClause.getFromItemList();
                            Vector vembuFromItemList = new Vector();
                            vembuFromItemList = this.getTablesOnlyList(vendorFromItemList);
                            fromClause.setFromClause("FROM");
                            fromClause.setFromItemList(vembuFromItemList);
                            whereExpression = new WhereExpression();
                            whereExpression = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                            selectQueryStatement.setSelectStatement(selectStatement);
                            selectQueryStatement.setFromClause(fromClause);
                            selectQueryStatement.setWhereExpression(whereExpression);
                        }
                        ft.setAliasName(this.aliasName);
                        ft.setTableName(selectQueryStatement);
                    }
                }
                else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                    final FromTable previousFromTable = this.getPreviousFromTableFromTheFromItemList(vendorSQS);
                    Object previousTableName = null;
                    String previousAliasName = null;
                    if (previousFromTable != null) {
                        if (previousFromTable.getTableName() instanceof String) {
                            previousTableName = previousFromTable.getTableName();
                        }
                        previousAliasName = previousFromTable.getAliasName();
                        if (previousAliasName == null && previousTableName instanceof String) {
                            previousAliasName = (String)previousTableName;
                        }
                        else if (previousAliasName != null || !(previousTableName instanceof String)) {}
                    }
                    FromTable toBeChangedConvertedPreviousFromTable = null;
                    if (vembuSQS.getFromClause() != null && vembuSQS.getFromClause().getFromItemList() != null && vembuSQS.getFromClause().getFromItemList().size() > 0 && vembuSQS.getFromClause().getFromItemList().lastElement() != null && vembuSQS.getFromClause().getFromItemList().lastElement() instanceof FromTable) {
                        toBeChangedConvertedPreviousFromTable = vembuSQS.getFromClause().getFromItemList().lastElement();
                    }
                    else if (vembuSQS.getFromClause() != null && vembuSQS.getFromClause().getFromItemList() != null && vembuSQS.getFromClause().getFromItemList().size() > 0 && vembuSQS.getFromClause().getFromItemList().lastElement() != null && vembuSQS.getFromClause().getFromItemList().lastElement() instanceof FromClause) {
                        final FromClause fc = vembuSQS.getFromClause().getFromItemList().lastElement();
                        final Vector fromItemsList = fc.getFromItemList();
                        toBeChangedConvertedPreviousFromTable = fc.getLastElement();
                    }
                    if (previousTableName != null && (previousTableName.toString().equalsIgnoreCase(oneOfTheTableNames) || previousAliasName.equalsIgnoreCase(oneOfTheTableNames) || oneOfTheTableNames == null)) {
                        if (selectQueryStatement == null) {
                            selectQueryStatement = new SelectQueryStatement();
                            selectStatement = new SelectStatement();
                            selectStatement.setSelectClause("SELECT");
                            selectItemList = new Vector();
                            selectItemList.addElement("*");
                            selectStatement.setSelectItemList(selectItemList);
                            fromClause = new FromClause();
                            fromClause.setFromClause("FROM");
                            fromItemList = new Vector();
                            fromTable = new FromTable();
                            fromTable.setTableName(previousTableName);
                            fromTable.setAliasName(previousAliasName);
                            fromItemList.add(fromTable);
                            fromClause.setFromItemList(fromItemList);
                            selectQueryStatement.setSelectStatement(selectStatement);
                            selectQueryStatement.setFromClause(fromClause);
                            final WhereExpression weClone3 = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                            whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone3);
                            selectQueryStatement.setWhereExpression(whereExpression);
                        }
                        else {
                            final WhereExpression weClone3 = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                            whereExpression = this.createNewWhereExpressionByRemovingTheJOINConditions(weClone3);
                            selectQueryStatement.setWhereExpression(whereExpression);
                        }
                        toBeChangedConvertedPreviousFromTable.setAliasName(previousAliasName);
                        toBeChangedConvertedPreviousFromTable.setTableName(selectQueryStatement);
                    }
                    else {
                        if (selectQueryStatement == null) {
                            selectQueryStatement = new SelectQueryStatement();
                            selectStatement = new SelectStatement();
                            selectStatement.setSelectClause("SELECT");
                            selectItemList = new Vector();
                            selectItemList.addElement(this.aliasName + ".*");
                            selectStatement.setSelectItemList(selectItemList);
                            fromClause = new FromClause();
                            final FromClause vendorFromClause2 = vendorSQS.getFromClause();
                            final Vector vendorFromItemList2 = vendorFromClause2.getFromItemList();
                            Vector vembuFromItemList2 = new Vector();
                            vembuFromItemList2 = this.getTablesOnlyList(vendorFromItemList2);
                            fromClause.setFromClause("FROM");
                            fromClause.setFromItemList(vembuFromItemList2);
                            whereExpression = new WhereExpression();
                            whereExpression = this.getClonedWhereExpression(joinWE.toOracleSelect(vembuSQS, vendorSQS));
                            selectQueryStatement.setSelectStatement(selectStatement);
                            selectQueryStatement.setFromClause(fromClause);
                            selectQueryStatement.setWhereExpression(whereExpression);
                        }
                        if (toBeChangedConvertedPreviousFromTable != null) {
                            toBeChangedConvertedPreviousFromTable.setAliasName(this.aliasName);
                            toBeChangedConvertedPreviousFromTable.setTableName(selectQueryStatement);
                        }
                    }
                }
            }
            else if (leftColumnExpression != null && leftColumnExpression.elementAt(0) instanceof TableColumn) {
                final TableColumn leftTableColumn2 = leftColumnExpression.elementAt(0);
                if (leftTableColumn2 != null && leftTableColumn2.getTableName() != null) {
                    if (rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof TableColumn) {
                        final TableColumn rightTableColumn = rightColumnExpression.elementAt(0);
                        if (rightTableColumn != null && rightTableColumn.getTableName() != null) {
                            final String rightTableName = rightTableColumn.getTableName();
                            if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") || this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                                if (this.tableName instanceof String) {
                                    String tableNameExtracted = null;
                                    if (this.tableName.toString().lastIndexOf(".") != -1) {
                                        tableNameExtracted = this.tableName.toString().substring(this.tableName.toString().lastIndexOf(".") + 1);
                                    }
                                    if ((tableNameExtracted != null && tableNameExtracted.equalsIgnoreCase(rightTableName)) || ((String)this.tableName).equalsIgnoreCase(rightTableName) || (this.aliasName != null && this.aliasName.equalsIgnoreCase(rightTableName))) {
                                        whereItem.setLeftJoin("+");
                                    }
                                    else {
                                        whereItem.setRightJoin("+");
                                    }
                                }
                                else if (this.tableName instanceof FunctionCalls) {
                                    final FunctionCalls fc2 = (FunctionCalls)this.tableName;
                                    String tableNameExtracted2 = null;
                                    final String tabName = fc2.getFunctionName().getColumnName();
                                    if (tabName.toString().lastIndexOf(".") != -1) {
                                        tableNameExtracted2 = tabName.toString().substring(tabName.toString().lastIndexOf(".") + 1);
                                    }
                                    final Vector v = fc2.getFunctionArguments();
                                    if (v.size() == 1 && v.get(0) instanceof SelectColumn) {
                                        final SelectColumn sc = v.get(0);
                                        final String temp = sc.toString();
                                        if (temp.trim().equalsIgnoreCase("NOLOCK")) {
                                            if ((tableNameExtracted2 != null && tableNameExtracted2.equalsIgnoreCase(rightTableName)) || tabName.equalsIgnoreCase(rightTableName) || (this.aliasName != null && this.aliasName.equalsIgnoreCase(rightTableName))) {
                                                whereItem.setLeftJoin("+");
                                            }
                                            else {
                                                whereItem.setRightJoin("+");
                                            }
                                        }
                                    }
                                }
                                else if (this.tableName instanceof SelectQueryStatement) {
                                    if (this.aliasName != null && this.aliasName.equalsIgnoreCase(rightTableName)) {
                                        whereItem.setLeftJoin("+");
                                    }
                                    else {
                                        whereItem.setRightJoin("+");
                                    }
                                }
                                else if (this.tableName instanceof FromClause) {
                                    final FromClause fromClauseInsideFT = (FromClause)this.tableName;
                                    final Vector fromItemListVector = fromClauseInsideFT.getFromItemList();
                                    this.getLeftJoinInWhereItemForTableNameAsFromClause(fromItemListVector, rightTableName, this.aliasName, whereItem);
                                }
                            }
                            else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                                isRightOuterJoin = true;
                                if (this.tableName instanceof String) {
                                    String tableNameExtracted = null;
                                    if (this.tableName.toString().lastIndexOf(".") != -1) {
                                        tableNameExtracted = this.tableName.toString().substring(this.tableName.toString().lastIndexOf(".") + 1);
                                    }
                                    if ((tableNameExtracted != null && tableNameExtracted.equalsIgnoreCase(rightTableName)) || ((String)this.tableName).equalsIgnoreCase(rightTableName) || (this.aliasName != null && this.aliasName.equalsIgnoreCase(rightTableName))) {
                                        whereItem.setRightJoin("+");
                                    }
                                    else {
                                        whereItem.setLeftJoin("+");
                                    }
                                }
                                else if (this.tableName instanceof FunctionCalls) {
                                    final FunctionCalls fc2 = (FunctionCalls)this.tableName;
                                    String tableNameExtracted2 = null;
                                    final String tabName = fc2.getFunctionName().getColumnName();
                                    if (tabName.toString().lastIndexOf(".") != -1) {
                                        tableNameExtracted2 = tabName.toString().substring(tabName.toString().lastIndexOf(".") + 1);
                                    }
                                    final Vector v = fc2.getFunctionArguments();
                                    if (v.size() == 1 && v.get(0) instanceof SelectColumn) {
                                        final SelectColumn sc = v.get(0);
                                        final String temp = sc.toString();
                                        if (temp.trim().equalsIgnoreCase("NOLOCK")) {
                                            if ((tableNameExtracted2 != null && tableNameExtracted2.equalsIgnoreCase(rightTableName)) || tabName.equalsIgnoreCase(rightTableName) || (this.aliasName != null && this.aliasName.equalsIgnoreCase(rightTableName))) {
                                                whereItem.setRightJoin("+");
                                            }
                                            else {
                                                whereItem.setLeftJoin("+");
                                            }
                                        }
                                    }
                                }
                                else if (this.tableName instanceof SelectQueryStatement) {
                                    if (this.aliasName != null && this.aliasName.equalsIgnoreCase(rightTableName)) {
                                        whereItem.setRightJoin("+");
                                    }
                                    else {
                                        whereItem.setLeftJoin("+");
                                    }
                                }
                                else if (this.tableName instanceof FromClause) {
                                    final FromClause fromClauseInsideFT = (FromClause)this.tableName;
                                    final Vector fromItemListVector = fromClauseInsideFT.getFromItemList();
                                    this.getRightJoinInWhereItemForTableNameAsFromClause(fromItemListVector, rightTableName, this.aliasName, whereItem);
                                }
                            }
                        }
                        else if (rightTableColumn != null) {
                            this.setJoinType(whereItem, leftTableColumn2.getTableName(), true);
                        }
                    }
                    else if (rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof String && !toOracle && isRightOuterJoin && this.tableName instanceof String) {
                        whereItem.setRightJoin("+");
                    }
                }
                else if (leftTableColumn2 != null && rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof TableColumn) {
                    final TableColumn rightTableColumn = rightColumnExpression.elementAt(0);
                    if (rightTableColumn != null && rightTableColumn.getTableName() != null) {
                        this.setJoinType(whereItem, rightTableColumn.getTableName(), false);
                    }
                    else if (rightTableColumn != null) {
                        final FromTable ftLeftExpr = MetadataInfoUtil.getTableOfColumn(vendorSQS, leftTableColumn2);
                        if (ftLeftExpr != null) {
                            final Object tblObj = ftLeftExpr.getTableName();
                            if (tblObj instanceof String && this.tableName instanceof String) {
                                if (((String)tblObj).equalsIgnoreCase((String)this.tableName)) {
                                    if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") || this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                                        whereItem.setRightJoin("+");
                                    }
                                    else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                                        whereItem.setLeftJoin("+");
                                    }
                                }
                                else if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") || this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                                    whereItem.setLeftJoin("+");
                                }
                                else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                                    whereItem.setRightJoin("+");
                                }
                            }
                        }
                        else {
                            vembuSQS.setGeneralComments("/* SwisSQL Message : Metadata required for accurate conversions. */");
                            if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") || this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                                whereItem.setLeftJoin("+");
                            }
                            else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                                whereItem.setRightJoin("+");
                            }
                        }
                    }
                }
            }
        }
        if (toOracle) {
            this.insertItem(vembuSQS, vendorSQS, 1, true);
        }
        else {
            this.insertItem(vembuSQS, vendorSQS, 1, false);
        }
    }
    
    private void setJoinType(final WhereItem whereItem, final String wcTableName, final boolean leftColumn) {
        if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") || this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
            if (this.tableName instanceof String) {
                String tableNameExtracted = null;
                if (this.tableName.toString().lastIndexOf(".") != -1) {
                    tableNameExtracted = this.tableName.toString().substring(this.tableName.toString().lastIndexOf(".") + 1);
                }
                if ((tableNameExtracted != null && tableNameExtracted.equalsIgnoreCase(wcTableName)) || ((String)this.tableName).equalsIgnoreCase(wcTableName) || (this.aliasName != null && this.aliasName.equalsIgnoreCase(wcTableName))) {
                    if (leftColumn) {
                        whereItem.setRightJoin("+");
                    }
                    else {
                        whereItem.setLeftJoin("+");
                    }
                }
                else if (leftColumn) {
                    whereItem.setLeftJoin("+");
                }
                else {
                    whereItem.setRightJoin("+");
                }
            }
        }
        else if ((this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) && this.tableName instanceof String) {
            String tableNameExtracted = null;
            if (this.tableName.toString().lastIndexOf(".") != -1) {
                tableNameExtracted = this.tableName.toString().substring(this.tableName.toString().lastIndexOf(".") + 1);
            }
            if ((tableNameExtracted != null && tableNameExtracted.equalsIgnoreCase(wcTableName)) || ((String)this.tableName).equalsIgnoreCase(wcTableName) || (this.aliasName != null && this.aliasName.equalsIgnoreCase(wcTableName))) {
                if (leftColumn) {
                    whereItem.setLeftJoin("+");
                }
                else {
                    whereItem.setRightJoin("+");
                }
            }
            else if (leftColumn) {
                whereItem.setRightJoin("+");
            }
            else {
                whereItem.setLeftJoin("+");
            }
        }
    }
    
    private void getLeftJoinInWhereItemForTableNameAsFromClause(final Vector fromItemListVector, final String rightTableName, final String aliasName, final WhereItem whereItem) {
        for (int count = 0; count < fromItemListVector.size(); ++count) {
            if (fromItemListVector.elementAt(count) instanceof FromTable) {
                final FromTable fromTableFromFC = fromItemListVector.get(count);
                final Object fromTableName = fromTableFromFC.getTableName();
                if (fromTableName instanceof String) {
                    if (((String)fromTableName).equalsIgnoreCase(rightTableName) || (aliasName != null && aliasName.equalsIgnoreCase(rightTableName))) {
                        whereItem.setLeftJoin("+");
                    }
                    else {
                        whereItem.setRightJoin("+");
                    }
                }
                else if (fromTableName instanceof SelectQueryStatement) {
                    if (aliasName != null && aliasName.equalsIgnoreCase(rightTableName)) {
                        whereItem.setLeftJoin("+");
                    }
                    else {
                        whereItem.setRightJoin("+");
                    }
                }
            }
            else if (fromItemListVector.elementAt(count) instanceof FromClause) {
                final FromClause newFC = fromItemListVector.get(count);
                final Vector newFromItemList = newFC.getFromItemList();
                this.getLeftJoinInWhereItemForTableNameAsFromClause(newFromItemList, rightTableName, aliasName, whereItem);
            }
        }
    }
    
    private void getRightJoinInWhereItemForTableNameAsFromClause(final Vector fromItemListVector, final String rightTableName, final String aliasName, final WhereItem whereItem) {
        for (int count = 0; count < fromItemListVector.size(); ++count) {
            if (fromItemListVector.elementAt(count) instanceof FromTable) {
                final FromTable fromTableFromFC = fromItemListVector.get(count);
                final Object fromTableName = fromTableFromFC.getTableName();
                if (fromTableName instanceof String) {
                    if (((String)fromTableName).equalsIgnoreCase(rightTableName) || (aliasName != null && aliasName.equalsIgnoreCase(rightTableName))) {
                        whereItem.setRightJoin("+");
                    }
                    else {
                        whereItem.setLeftJoin("+");
                    }
                }
                else if (fromTableName instanceof SelectQueryStatement) {
                    if (aliasName != null && aliasName.equalsIgnoreCase(rightTableName)) {
                        whereItem.setRightJoin("+");
                    }
                    else {
                        whereItem.setLeftJoin("+");
                    }
                }
            }
            else if (fromItemListVector.elementAt(count) instanceof FromClause) {
                final FromClause newFC = fromItemListVector.get(count);
                final Vector newFromItemList = newFC.getFromItemList();
                this.getRightJoinInWhereItemForTableNameAsFromClause(newFromItemList, rightTableName, aliasName, whereItem);
            }
        }
    }
    
    private WhereExpression createNewWhereExpressionByRemovingTheJOINConditions(final WhereExpression weClone) {
        final WhereExpression newWhereExp = new WhereExpression();
        final Vector whereItemsANDExpressions = weClone.getWhereItems();
        for (int i = 0; i < whereItemsANDExpressions.size(); ++i) {
            final Object obj = whereItemsANDExpressions.elementAt(i);
            if (obj instanceof WhereExpression) {
                WhereExpression we = (WhereExpression)obj;
                we = this.createNewWhereExpressionByRemovingTheJOINConditions(we);
            }
            else if (obj instanceof WhereItem) {
                final WhereItem whereItem = (WhereItem)obj;
                WhereColumn leftItem = null;
                Vector leftColumnExpression = null;
                WhereColumn rightItem = null;
                Vector rightColumnExpression = null;
                if (whereItem != null) {
                    leftItem = whereItem.getLeftWhereExp();
                    if (leftItem != null) {
                        leftColumnExpression = leftItem.getColumnExpression();
                    }
                    rightItem = whereItem.getRightWhereExp();
                    if (rightItem != null) {
                        rightColumnExpression = rightItem.getColumnExpression();
                    }
                }
                if (leftColumnExpression != null && leftColumnExpression.elementAt(0) instanceof TableColumn && rightColumnExpression != null && rightColumnExpression.elementAt(0) instanceof TableColumn) {
                    whereItemsANDExpressions.removeElementAt(i);
                    if (i != 0 && weClone.getOperator().size() > 0) {
                        weClone.getOperator().removeElementAt(i - 1);
                    }
                    else if (weClone.getOperator().size() > i) {
                        weClone.getOperator().removeElementAt(i);
                    }
                    --i;
                }
            }
        }
        weClone.setToOracle(true);
        return weClone;
    }
    
    public FromTable getPreviousFromTableFromTheFromItemList(final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromClause fc = vendorSQS.getFromClause();
        Vector v_fl;
        int i_cnt;
        for (v_fl = fc.getFromItemList(), i_cnt = 0, i_cnt = 0; i_cnt < v_fl.size(); ++i_cnt) {
            if (v_fl.elementAt(i_cnt) instanceof FromTable) {
                if (v_fl.elementAt(i_cnt).hashCode() == this.hashCode()) {
                    break;
                }
            }
            else if (v_fl.elementAt(i_cnt) instanceof FromClause) {
                return null;
            }
        }
        if (--i_cnt == -1) {
            return null;
        }
        FromTable t_ft = null;
        if (v_fl.elementAt(i_cnt) instanceof FromTable) {
            t_ft = v_fl.elementAt(i_cnt);
        }
        return t_ft;
    }
    
    private int getFromTableIndexFromFromItemList(final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromClause fc = vendorSQS.getFromClause();
        Vector v_fl;
        int i_cnt;
        for (v_fl = fc.getFromItemList(), i_cnt = 0, i_cnt = 0; i_cnt < v_fl.size(); ++i_cnt) {
            if (v_fl.elementAt(i_cnt) instanceof FromTable) {
                if (v_fl.elementAt(i_cnt).hashCode() == this.hashCode()) {
                    break;
                }
            }
            else if (v_fl.elementAt(i_cnt) instanceof FromClause) {
                return 0;
            }
        }
        return i_cnt;
    }
    
    private void markToBeRemovedOperators(final WhereExpression whereExpression) {
        final Vector whereItems = whereExpression.getWhereItems();
        final Vector operators = whereExpression.getOperator();
        Object obj = null;
        for (int i = 0; i < whereItems.size(); ++i) {
            obj = whereItems.elementAt(i);
            if (obj instanceof WhereItem) {
                final WhereItem wi = (WhereItem)obj;
                if (wi.getMovedToFromClause()) {
                    if (i != 0) {
                        operators.setElementAt("&AND", i - 1);
                    }
                    else if (operators.size() > i) {
                        operators.setElementAt("&AND", i);
                    }
                }
            }
            else if (obj instanceof WhereExpression) {
                this.markToBeRemovedOperators((WhereExpression)obj);
            }
        }
    }
    
    private void markNonJoinWhereItems(final WhereExpression whereExpression, final boolean toOracle) {
        final Vector whereItems = whereExpression.getWhereItems();
        final Vector operators = whereExpression.getOperator();
        Object obj = null;
        for (int i = 0; i < whereItems.size(); ++i) {
            obj = whereItems.elementAt(i);
            if (obj instanceof WhereItem) {
                final WhereItem whereItem = (WhereItem)obj;
                WhereColumn leftItem = null;
                Vector leftColumnExpression = null;
                WhereColumn rightItem = null;
                Vector rightColumnExpression = null;
                if (whereItem != null) {
                    leftItem = whereItem.getLeftWhereExp();
                    if (leftItem != null) {
                        leftColumnExpression = leftItem.getColumnExpression();
                    }
                    rightItem = whereItem.getRightWhereExp();
                    if (rightItem != null) {
                        rightColumnExpression = rightItem.getColumnExpression();
                    }
                }
                if (((leftColumnExpression != null && !(leftColumnExpression.elementAt(0) instanceof TableColumn)) || (rightColumnExpression != null && !(rightColumnExpression.elementAt(0) instanceof TableColumn)) || leftColumnExpression == null || rightColumnExpression == null) && toOracle) {
                    whereItem.setMovedToFromClause(true);
                    if (i != 0) {
                        operators.setElementAt("&AND", i - 1);
                    }
                    else if (operators.size() > i) {
                        operators.setElementAt("&AND", i);
                    }
                }
            }
            else if (obj instanceof WhereExpression) {
                this.markNonJoinWhereItems((WhereExpression)obj, toOracle);
            }
        }
    }
    
    private Object convertTableNameToOracle(final Object tableName, final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        if (tableName instanceof SelectQueryStatement) {
            return ((SelectQueryStatement)tableName).toOracleSelect();
        }
        if (tableName instanceof FunctionCalls) {
            return ((FunctionCalls)tableName).toOracleSelect(vembuSQS, vendorSQS);
        }
        final String table_name = StringFunctions.replaceFirst(".", "..", (String)tableName);
        return table_name;
    }
    
    private void addLeftJoin(final WhereExpression whereExpression) {
        final Vector whereItems = whereExpression.getWhereItems();
        Object obj = null;
        for (int i = 0; i < whereItems.size(); ++i) {
            obj = whereItems.elementAt(i);
            if (obj instanceof WhereItem) {
                final WhereItem wi = (WhereItem)obj;
                if (wi.isItAJoinItem()) {
                    wi.setLeftJoin("+");
                }
            }
            else if (obj instanceof WhereExpression) {
                this.addLeftJoin((WhereExpression)obj);
            }
        }
    }
    
    private void addRightJoin(final WhereExpression whereExpression) {
        final Vector whereItems = whereExpression.getWhereItems();
        Object obj = null;
        for (int i = 0; i < whereItems.size(); ++i) {
            obj = whereItems.elementAt(i);
            if (obj instanceof WhereItem) {
                final WhereItem wi = (WhereItem)obj;
                if (wi.isItAJoinItem()) {
                    wi.setRightJoin("+");
                }
            }
            else if (obj instanceof WhereExpression) {
                this.addRightJoin((WhereExpression)obj);
            }
        }
    }
    
    public void insertItem(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS, final int type, final boolean toOracle) throws ConvertException {
        final WhereExpression f_we = vendorSQS.getWhereExpression();
        WhereExpression t_we = new WhereExpression();
        if (type == 1) {
            t_we = this.joinExpression.elementAt(0);
            if (!this.joinClause.equalsIgnoreCase("JOIN") && !this.joinClause.equalsIgnoreCase("INNER JOIN")) {
                this.markNonJoinWhereItems(t_we, toOracle);
            }
            if (vembuSQS.getWhereExpression() != null) {
                vembuSQS.getWhereExpression().addOperator("AND");
                t_we.setCloseBrace(")");
                t_we.setOpenBrace("(");
                if (toOracle) {
                    vembuSQS.getWhereExpression().addWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
                }
                else {
                    vembuSQS.getWhereExpression().addWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
                }
            }
            else if (toOracle) {
                vembuSQS.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
            }
            else {
                vembuSQS.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
            }
        }
        else if (type == 2) {
            if (this.joinExpression != null) {
                t_we = this.joinExpression.elementAt(0);
                t_we = this.getClonedWhereExpression(t_we);
                this.addRightJoin(t_we);
                if (vembuSQS.getWhereExpression() != null) {
                    vembuSQS.getWhereExpression().addOperator("AND");
                    t_we.setCloseBrace(")");
                    t_we.setOpenBrace("(");
                    if (toOracle) {
                        vembuSQS.getWhereExpression().addWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
                    }
                    else {
                        vembuSQS.getWhereExpression().addWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
                    }
                }
                else if (toOracle) {
                    vembuSQS.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
                }
                else {
                    vembuSQS.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
                }
            }
            else if (toOracle) {
                vembuSQS.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
            }
            else {
                vembuSQS.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
            }
            final SetOperatorClause soc = new SetOperatorClause();
            soc.setSetClause(" UNION ");
            final SelectQueryStatement sqs = new SelectQueryStatement();
            sqs.setSelectStatement(vendorSQS.getSelectStatement());
            final FromClause fromClause = new FromClause();
            final FromClause vendorFromClause = vendorSQS.getFromClause();
            final Vector vendorFromItemList = vendorFromClause.getFromItemList();
            Vector vembuFromItemList = new Vector();
            vembuFromItemList = this.getTablesOnlyList(vendorFromItemList);
            fromClause.setFromClause("FROM");
            fromClause.setFromItemList(vembuFromItemList);
            sqs.setFromClause(fromClause);
            if (this.joinExpression != null) {
                t_we = this.joinExpression.elementAt(0);
                t_we = this.getClonedWhereExpression(t_we);
                this.addLeftJoin(t_we);
                if (vendorSQS.getWhereExpression() != null) {
                    if (toOracle) {
                        sqs.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
                    }
                    else {
                        sqs.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
                    }
                    sqs.getWhereExpression().addOperator("AND");
                    vendorSQS.getWhereExpression().setCloseBrace(")");
                    vendorSQS.getWhereExpression().setOpenBrace("(");
                    sqs.getWhereExpression().addWhereExpression(vendorSQS.getWhereExpression().toOracleSelect(vembuSQS, vendorSQS));
                }
                else if (toOracle) {
                    sqs.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
                }
                else {
                    sqs.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
                }
            }
            else if (toOracle) {
                sqs.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
            }
            else {
                sqs.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
            }
            sqs.setGroupByStatement(vendorSQS.getGroupByStatement());
            sqs.setForUpdateStatement(vendorSQS.getForUpdateStatement());
            sqs.setHavingStatement(vendorSQS.getHavingStatement());
            soc.setSelectQueryStatement(sqs);
            vembuSQS.setSetOperatorClause(soc);
        }
        else if (type == 3) {
            this.convertUsingListToWhereExp(this.UsingList, t_we, this.getPreviousFromTableFromTheFromItemList(vendorSQS));
            if (vembuSQS.getWhereExpression() != null) {
                vembuSQS.getWhereExpression().addOperator("AND");
                t_we.setCloseBrace(")");
                t_we.setOpenBrace("(");
                if (toOracle) {
                    vembuSQS.getWhereExpression().addWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
                }
                else {
                    vembuSQS.getWhereExpression().addWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
                }
            }
            else if (toOracle) {
                vembuSQS.setWhereExpression(t_we.toOracleSelect(vembuSQS, vendorSQS));
            }
            else {
                vembuSQS.setWhereExpression(t_we.toTimesTenSelect(vembuSQS, vendorSQS));
            }
        }
        else if (type == 4) {
            t_we = this.joinExpression.elementAt(0);
            if (vembuSQS.getWhereExpression() != null) {
                vembuSQS.getWhereExpression().addOperator("AND");
                t_we.setCloseBrace(")");
                t_we.setOpenBrace("(");
                vembuSQS.getWhereExpression().addWhereExpression(t_we.toInformixSelect(vembuSQS, vendorSQS));
            }
            else {
                vembuSQS.setWhereExpression(t_we.toInformixSelect(vembuSQS, vendorSQS));
            }
        }
    }
    
    public void convertUsingListToWhereExp(final Vector v_ul, final WhereExpression t_we, final FromTable fc) throws ConvertException {
        WhereItem wi = new WhereItem();
        int i = 0;
        String s_cn = null;
        if (fc != null) {
            if (fc.getAliasName() != null) {
                s_cn = fc.getAliasName();
            }
            else {
                s_cn = fc.getTableName().toString();
            }
        }
        for (i = 0; i < v_ul.size(); ++i) {
            if (v_ul.elementAt(i) instanceof TableColumn) {
                final Vector v_wi = new Vector();
                final WhereColumn wc_wi = new WhereColumn();
                final TableColumn tc = v_ul.elementAt(i);
                tc.setTableName(s_cn);
                v_wi.addElement(tc);
                wc_wi.setColumnExpression(v_wi);
                wi.setLeftWhereExp(wc_wi);
                final TableColumn tcr = new TableColumn();
                tcr.setColumnName(v_ul.elementAt(i).getColumnName());
                final Vector v_wir = new Vector();
                final WhereColumn wc_wir = new WhereColumn();
                if (this.aliasName != null) {
                    tcr.setTableName(this.aliasName);
                }
                else {
                    tcr.setTableName(this.tableName.toString());
                }
                v_wir.addElement(tcr);
                wc_wir.setColumnExpression(v_wir);
                wi.setRightWhereExp(wc_wir);
                wi.setOperator("=");
                if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") || this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                    wi.setLeftJoin("+");
                }
                else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") || this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                    wi.setRightJoin("+");
                }
                t_we.addWhereItem(wi);
                wi = new WhereItem();
            }
            else {
                final String s_cd = this.UsingList.elementAt(i);
                if (s_cd.equals(",")) {
                    t_we.addOperator("AND");
                }
                else {
                    t_we.addOperator(s_cd);
                }
            }
        }
    }
    
    public void convertJoinExpToWhereExp(final Vector v_jex, final WhereExpression t_we, final FromTable fc) throws ConvertException {
        WhereItem wi = new WhereItem();
        boolean LRflag = true;
        int i = 0;
        String s_cn = null;
        if (fc != null) {
            if (fc.getAliasName() != null) {
                s_cn = fc.getAliasName();
            }
            else {
                s_cn = fc.getTableName().toString();
            }
        }
        for (i = 0; i < v_jex.size(); ++i) {
            if (v_jex.elementAt(i) instanceof Vector) {
                this.convertJoinExpToWhereExp(v_jex.elementAt(i), t_we, fc);
            }
            else if (v_jex.elementAt(i) instanceof TableColumn) {
                final Vector v_wi = new Vector();
                final WhereColumn wc_wi = new WhereColumn();
                final TableColumn tc = v_jex.elementAt(i);
                if (fc != null && tc.getTableName() == null) {
                    tc.setTableName(s_cn);
                }
                if (LRflag) {
                    if (fc != null && tc.getTableName() == null) {
                        tc.setTableName(s_cn);
                    }
                    v_wi.addElement(tc);
                    wc_wi.setColumnExpression(v_wi);
                    wi.setLeftWhereExp(wc_wi);
                    LRflag = false;
                }
                else {
                    if (this.aliasName != null) {
                        if (tc.getTableName() == null) {
                            tc.setTableName(this.aliasName);
                        }
                    }
                    else if (tc.getTableName() == null) {
                        tc.setTableName(this.tableName.toString());
                    }
                    v_wi.addElement(tc);
                    wc_wi.setColumnExpression(v_wi);
                    wi.setRightWhereExp(wc_wi);
                    LRflag = true;
                }
            }
            else if (v_jex.elementAt(i) instanceof String) {
                if (v_jex.elementAt(i).equalsIgnoreCase("=")) {
                    TableColumn tc2 = new TableColumn();
                    if (v_jex.elementAt(i + 1) instanceof TableColumn) {
                        tc2 = v_jex.elementAt(i + 1);
                    }
                    if (tc2.getTableName().equalsIgnoreCase(this.aliasName) || tc2.getTableName().equalsIgnoreCase((String)this.tableName)) {
                        if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") | this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                            wi.setRightJoin("+");
                        }
                        else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") | this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                            wi.setLeftJoin("+");
                        }
                    }
                    else if (this.joinClause.equalsIgnoreCase("LEFT OUTER JOIN") | this.joinClause.equalsIgnoreCase("LEFT JOIN")) {
                        wi.setLeftJoin("+");
                    }
                    else if (this.joinClause.equalsIgnoreCase("RIGHT OUTER JOIN") | this.joinClause.equalsIgnoreCase("RIGHT JOIN")) {
                        wi.setRightJoin("+");
                    }
                    wi.setOperator("=");
                }
                else if (v_jex.elementAt(i).equalsIgnoreCase("AND") | v_jex.elementAt(i).equalsIgnoreCase("OR")) {
                    t_we.addOperator(v_jex.elementAt(i));
                    t_we.addWhereItem(wi);
                    wi = new WhereItem();
                }
            }
        }
        --i;
        if (!(v_jex.elementAt(i) instanceof Vector)) {
            t_we.addWhereItem(wi);
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final SelectStatement ss = new SelectStatement();
        if (this.joinClause != null) {
            sb.append("\n" + this.joinClause.toUpperCase());
        }
        if (this.commentObj != null) {
            sb.append(" " + this.commentObj.toString().trim());
        }
        if (this.outer != null) {
            sb.append(" " + this.outer.toUpperCase());
        }
        if (this.outerOpenBrace != null) {
            sb.append(" " + this.outerOpenBrace.toUpperCase());
        }
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                ((SelectQueryStatement)this.tableName).setObjectContext(this.context);
                if (this.tableKeyword != null) {
                    sb.append(" " + this.tableKeyword.toUpperCase());
                }
                sb.append("(" + this.tableName.toString() + ")");
            }
            else if (this.tableName instanceof FunctionCalls) {
                ((FunctionCalls)this.tableName).setObjectContext(this.context);
                if (this.tableKeyword != null) {
                    sb.append(" " + this.tableKeyword + this.tableName.toString() + ")");
                }
                else {
                    sb.append(this.tableName.toString());
                }
            }
            else if (this.tableName instanceof FromClause) {
                ((FromClause)this.tableName).setObjectContext(this.context);
                if (this.fromClauseOpenBraces != null) {
                    sb.append("(");
                }
                sb.append(this.tableName.toString());
                if (this.fromClauseClosedBraces != null) {
                    sb.append(")");
                }
            }
            else if (this.context != null) {
                String temp = null;
                if (this.origTableName != null) {
                    final Object obj = this.context.getEquivalent(this.origTableName);
                    if ((obj != null && obj.toString().equals(this.origTableName)) || obj == null) {
                        temp = this.context.getEquivalent(this.tableName.toString()).toString();
                    }
                    else {
                        temp = obj.toString();
                    }
                }
                else {
                    temp = this.context.getEquivalent(this.tableName.toString()).toString();
                }
                sb.append(" " + temp);
            }
            else {
                sb.append(" " + this.tableName.toString());
            }
        }
        if (this.commentObjAfterToken != null) {
            sb.append(" " + this.commentObjAfterToken.toString().trim());
        }
        if (this.isAS) {
            sb.append(" AS ");
        }
        if (this.aliasName != null) {
            if (this.context != null) {
                final String temp = this.context.getEquivalent(this.aliasName).toString();
                sb.append(" " + temp);
            }
            else {
                sb.append(" " + this.aliasName);
            }
        }
        if (this.holdLock != null) {
            sb.append("  " + this.holdLock);
        }
        if (this.queryPartitionClause == null && this.crossJoinForPartitionClause != null) {
            sb.append(" " + this.crossJoinForPartitionClause + " ");
        }
        if (this.outerClosedBrace != null) {
            sb.append(" " + this.outerClosedBrace.toUpperCase());
        }
        if (this.indexHint != null) {
            sb.append(this.indexHint);
        }
        if (this.onOrUsingJoin != null) {
            if (this.onOrUsingJoin.equalsIgnoreCase("using")) {
                if (this.joinExpression != null) {
                    sb.append(" " + this.convertJoinExpToString(this.joinExpression));
                }
                sb.append(" " + this.onOrUsingJoin.toUpperCase() + " ");
                sb.append("(");
                for (int i = 0; i < this.UsingList.size(); ++i) {
                    sb.append(this.UsingList.elementAt(i).toString() + " ");
                }
                sb.append(")");
            }
            else {
                sb.append(" " + this.onOrUsingJoin.toUpperCase());
                if (this.joinExpression != null) {
                    sb.append(" " + this.convertJoinExpToString(this.joinExpression));
                }
            }
        }
        else if (this.joinExpression != null) {
            sb.append(" " + this.convertJoinExpToString(this.joinExpression));
        }
        if (this.updateLock != null) {
            sb.append(" " + this.updateLock + " ");
        }
        if (this.setOperatorClauseForFullJoin != null) {
            sb.append(this.setOperatorClauseForFullJoin);
        }
        return sb.toString();
    }
    
    public String convertJoinExpToString(final Vector v_jex) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < v_jex.size(); ++i) {
            if (v_jex.elementAt(i) instanceof Vector) {
                sb.append(this.convertJoinExpToString(v_jex.elementAt(i)));
            }
            else {
                sb.append(v_jex.elementAt(i).toString() + " ");
            }
        }
        return sb.toString();
    }
    
    private void collectAllWhereColumnsInWhereExpression(final WhereExpression sqlWhereExpression) {
        if (sqlWhereExpression != null) {
            final Vector sqlWhereItemsList = sqlWhereExpression.getWhereItems();
            if (sqlWhereItemsList != null) {
                for (int i = 0; i < sqlWhereItemsList.size(); ++i) {
                    if (sqlWhereItemsList.get(0) instanceof WhereItem) {
                        final WhereColumn leftColumnExpression = sqlWhereItemsList.get(i).getLeftWhereExp();
                        final WhereColumn rightColumnExpression = sqlWhereItemsList.get(i).getRightWhereExp();
                        this.collectWhereColumnItems(leftColumnExpression);
                        this.collectWhereColumnItems(rightColumnExpression);
                    }
                }
            }
        }
    }
    
    private void collectWhereColumnItems(final WhereColumn sqlWhereColumn) {
        if (sqlWhereColumn != null) {
            final Vector sqlWhereColumnList = sqlWhereColumn.getColumnExpression();
            this.removeAllTableReferenceFromColumnsInWhereExpression(sqlWhereColumnList);
        }
    }
    
    private void removeAllTableReferenceFromColumnsInWhereExpression(final Vector sqlWhereColumnList) {
        if (sqlWhereColumnList != null) {
            for (int i = 0; i < sqlWhereColumnList.size(); ++i) {
                if (sqlWhereColumnList.get(i) instanceof TableColumn) {
                    final TableColumn sqlTableColumn = sqlWhereColumnList.get(i);
                    sqlTableColumn.setTableName(null);
                    sqlTableColumn.setOwnerName(null);
                }
                else if (sqlWhereColumnList.get(i) instanceof FunctionCalls) {
                    final Vector functionArguments = sqlWhereColumnList.get(i).getFunctionArguments();
                    this.removeAllTableReferenceFromColumnsInWhereExpression(functionArguments);
                }
                else if (sqlWhereColumnList.get(i) instanceof SelectColumn) {
                    final Vector colExp = sqlWhereColumnList.get(i).getColumnExpression();
                    this.removeAllTableReferenceFromColumnsInWhereExpression(colExp);
                }
            }
        }
    }
    
    private void buildSetOperatorClauseForSubQuery(final SelectQueryStatement tabName) {
        SetOperatorClause newSOC = new SetOperatorClause();
        for (int i = this.setOperatorClauseListForSubQuery.size() - 1; i >= 0; --i) {
            final SetOperatorClause tempSOC = new SetOperatorClause();
            final Object obj = this.setOperatorClauseListForSubQuery.get(i);
            SetOperatorClause socTemp = new SetOperatorClause();
            if (obj != null && obj instanceof SetOperatorClause) {
                SelectQueryStatement sqs = new SelectQueryStatement();
                socTemp = (SetOperatorClause)obj;
                final String setClause = socTemp.getSetClause();
                sqs = socTemp.getSelectQueryStatement();
                if (i == this.setOperatorClauseListForSubQuery.size() - 1) {
                    newSOC = socTemp;
                }
                else {
                    sqs.setSetOperatorClause(newSOC);
                    tempSOC.setSetClause(setClause);
                    tempSOC.setSelectQueryStatement(sqs);
                    newSOC = tempSOC;
                }
            }
        }
        tabName.setSetOperatorClause(newSOC);
    }
    
    private void convertTableValueConstructorToSelectUnion(final ValuesClause vc, final ArrayList columnAliasList, final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        ArrayList selectQueries = new ArrayList();
        for (int j = 0; j < columnAliasList.size(); ++j) {
            final Object obj = columnAliasList.get(j);
            if (obj instanceof String) {
                final String str = ((String)obj).trim();
                if (str.equals("(") || str.equals(",") || str.equals(")")) {
                    columnAliasList.remove(j);
                }
            }
        }
        this.groupRowValues(vc.getValuesList());
        selectQueries = this.convertValuesToSelectQueryStatements(this.rowValuesList, columnAliasList);
        SelectQueryStatement lastSQS = null;
        SetOperatorClause currentSOC = null;
        int i;
        for (int size = i = selectQueries.size() - 1; i != -1; --i) {
            currentSOC = new SetOperatorClause();
            if (i == size) {
                lastSQS = selectQueries.get(size);
            }
            else {
                currentSOC.setSetClause("UNION ALL");
                currentSOC.setSelectQueryStatement(lastSQS);
                final SelectQueryStatement tempSQS = selectQueries.get(i);
                tempSQS.setSetOperatorClause(currentSOC);
                lastSQS = tempSQS;
            }
        }
        vendorSQS.setSetOperatorClause(lastSQS.getSetOperatorClause());
        vembuSQS.setSelectStatement(lastSQS.getSelectStatement().toOracleSelect(vembuSQS, vendorSQS));
        vembuSQS.setFromClause(lastSQS.getFromClause());
    }
    
    private void groupRowValues(final List valuesList) throws ConvertException {
        final Vector newValuesList = new Vector();
        final int multiValListSize = valuesList.size();
        final int firstOpenBraceIndex = valuesList.indexOf("(");
        final int firstCloseBraceIndex = valuesList.indexOf(")");
        final int lastCloseBraceIndex = valuesList.lastIndexOf(")");
        final List firstValuesSet = valuesList.subList(firstOpenBraceIndex, firstCloseBraceIndex + 1);
        for (int firstValuesSetSize = firstValuesSet.size(), i = 0; i < firstValuesSetSize; ++i) {
            if (firstValuesSet.get(i) instanceof SelectColumn) {
                final SelectColumn sc = firstValuesSet.get(i);
                if (i != firstValuesSetSize - 2) {
                    sc.setEndsWith(",");
                }
                newValuesList.add(sc);
            }
        }
        this.rowValuesList.add(newValuesList);
        if (firstCloseBraceIndex == lastCloseBraceIndex) {
            return;
        }
        this.groupRowValues(valuesList.subList(firstCloseBraceIndex + 1, multiValListSize));
    }
    
    private ArrayList convertValuesToSelectQueryStatements(final ArrayList rowValuesList, final ArrayList columnAliasList) throws ConvertException {
        final ArrayList selectQueriesList = new ArrayList();
        final FromClause fcTemp = new FromClause();
        final FromTable ft1 = new FromTable();
        final Vector fromItems = new Vector();
        fcTemp.setFromClause("FROM");
        ft1.setTableName("DUAL");
        fromItems.add(ft1);
        fcTemp.setFromItemList(fromItems);
        for (int j = 0; j < rowValuesList.size(); ++j) {
            if (j == 0) {
                final Vector v = rowValuesList.get(0);
                for (int k = 0; k < v.size(); ++k) {
                    if (v.size() == columnAliasList.size()) {
                        final Object obj = v.get(k);
                        if (obj instanceof SelectColumn) {
                            final SelectColumn scTemp = (SelectColumn)obj;
                            scTemp.setAliasName(columnAliasList.get(k).toString());
                        }
                    }
                }
            }
            final SelectQueryStatement sqs = new SelectQueryStatement();
            final SelectStatement selectStmt = new SelectStatement();
            selectStmt.setSelectClause("SELECT");
            selectStmt.setSelectItemList(rowValuesList.get(j));
            sqs.setSelectStatement(selectStmt);
            sqs.setFromClause(fcTemp);
            selectQueriesList.add(sqs);
        }
        return selectQueriesList;
    }
    
    public FromTable toVectorWiseSelect(final SelectQueryStatement vembuSQS, final SelectQueryStatement vendorSQS) throws ConvertException {
        final FromTable ft = new FromTable();
        boolean isFullJoin = false;
        if (this.joinClause != null) {
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL JOIN")) {
                throw new ConvertException("Conversion failure..Natural join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT JOIN")) {
                throw new ConvertException("Conversion failure..Natural right Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL LEFT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural left Outer join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("NATURAL RIGHT OUTER JOIN")) {
                throw new ConvertException("Conversion failure..Natural right join can't be converted");
            }
            if (this.joinClause.trim().equalsIgnoreCase("KEY JOIN")) {
                throw new ConvertException("Conversion failure..Key join is not supported");
            }
            if (this.joinClause.trim().equalsIgnoreCase("OUTER")) {
                ft.setJoinClause("OUTER JOIN");
            }
            else if (this.joinClause.trim().equalsIgnoreCase("FULL JOIN") || this.joinClause.trim().equalsIgnoreCase("FULL OUTER JOIN")) {
                final FromClause fc = new FromClause();
                final FromTable newFromTable = new FromTable();
                newFromTable.setAliasName(this.aliasName);
                newFromTable.setIsAS(this.isAS);
                newFromTable.setJoinClause("RIGHT OUTER JOIN");
                newFromTable.setJoinExpression(this.joinExpression);
                newFromTable.setOnOrUsingJoin(this.onOrUsingJoin);
                newFromTable.setTableKeyword(this.tableKeyword);
                newFromTable.setTableName(this.tableName);
                newFromTable.setUsingList(this.UsingList);
                if (vendorSQS.getFromClause() != null) {
                    fc.setFromClause("FROM");
                    final Vector fromList = vendorSQS.getFromClause().getFromItemList();
                    final Vector newFromList = new Vector();
                    for (int i = 0; i < fromList.size(); ++i) {
                        if (fromList.get(i) instanceof FromTable) {
                            final FromTable getFT = fromList.get(i);
                            if (getFT != null && getFT.equals(this)) {
                                newFromList.add(newFromTable);
                            }
                            else if (fromList.get(i) instanceof FromTable) {
                                newFromList.add(fromList.get(i).toVectorWiseSelect(vembuSQS, vendorSQS));
                            }
                        }
                        else if (fromList.get(i) instanceof FromClause) {
                            fromList.get(i).toVectorWiseSelect(vembuSQS, vendorSQS);
                        }
                    }
                    fc.setFromItemList(newFromList);
                }
                final SelectQueryStatement sqs = new SelectQueryStatement();
                sqs.setFromClause(fc);
                if (vendorSQS.getSelectStatement() != null) {
                    sqs.setSelectStatement(vendorSQS.getSelectStatement().toVectorWiseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getForUpdateStatement() != null) {
                    sqs.setForUpdateStatement(null);
                }
                if (vendorSQS.getGroupByStatement() != null) {
                    sqs.setGroupByStatement(vendorSQS.getGroupByStatement().toVectorWiseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getHavingStatement() != null) {
                    sqs.setHavingStatement(vendorSQS.getHavingStatement().toVectorWiseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getHierarchicalQueryClause() != null) {
                    sqs.setHierarchicalQueryClause(null);
                }
                if (vendorSQS.getLimitClause() != null) {
                    sqs.setLimitClause(vendorSQS.getLimitClause().toVectorWiseSelect(vembuSQS, vendorSQS));
                }
                if (vendorSQS.getSetOperatorClause() != null) {
                    sqs.setSetOperatorClause(null);
                }
                if (vendorSQS.getWhereExpression() != null) {
                    sqs.setWhereExpression(vendorSQS.getWhereExpression().toVectorWiseSelect(vembuSQS, vendorSQS));
                }
                (this.setOperatorClauseForFullJoin = new SetOperatorClause()).setSelectQueryStatement(sqs);
                this.setOperatorClauseForFullJoin.setSetClause("UNION");
                if (vendorSQS.getWhereExpression() != null) {
                    this.setOperatorClauseForFullJoin.setWhereExpression(vendorSQS.getWhereExpression().toVectorWiseSelect(vembuSQS, vendorSQS));
                    vendorSQS.setWhereExpression(null);
                }
                ft.setSetOperatorClause(this.setOperatorClauseForFullJoin);
                isFullJoin = true;
            }
            if (this.onOrUsingJoin == null && this.joinExpression != null) {
                ft.setOnOrUsingJoin("ON");
                WhereExpression we = this.joinExpression.elementAt(0).toVectorWiseSelect(vembuSQS, vendorSQS);
                final Vector v = new Vector();
                we = we.toVectorWiseSelect(vendorSQS, vembuSQS);
                v.addElement(we);
                ft.setJoinExpression(v);
            }
            else if (this.joinExpression != null) {
                ft.setOnOrUsingJoin(this.onOrUsingJoin);
                WhereExpression we = this.joinExpression.elementAt(0);
                final Vector operatorList = we.getOperator();
                we.setOperator(operatorList);
                we = we.toVectorWiseSelect(vembuSQS, vendorSQS);
                final Vector v2 = new Vector();
                v2.addElement(we);
                ft.setJoinExpression(v2);
            }
            ft.setOnOrUsingJoin(this.onOrUsingJoin);
            if (this.UsingList != null) {
                final Vector v3 = new Vector();
                for (int j = 0; j < this.UsingList.size(); ++j) {
                    if (this.UsingList.elementAt(j) instanceof TableColumn) {
                        v3.addElement(this.UsingList.elementAt(j).toVectorWiseSelect(vembuSQS, vendorSQS));
                    }
                    else {
                        v3.addElement(this.UsingList.elementAt(j));
                    }
                }
                ft.setUsingList(v3);
            }
            if (!isFullJoin) {
                ft.setJoinClause(this.joinClause);
            }
            else {
                ft.setJoinClause("LEFT OUTER JOIN");
            }
            if (this.joinClause.trim().equalsIgnoreCase("JOIN")) {
                ft.setJoinClause("INNER JOIN");
            }
            this.removeJoinExpressionForCrossJoin(ft);
        }
        if (this.getOuter() != null) {
            ft.setOuter(this.outer);
            if (this.getOuterOpenBrace() != null) {
                ft.setOuterOpenBrace(this.outerOpenBrace);
            }
            if (this.outerClosedBrace != null) {
                ft.setOuterClosedBrace(this.outerClosedBrace);
            }
        }
        if (this.outerClosedBrace != null) {
            ft.setOuterClosedBrace(this.outerClosedBrace);
        }
        if (this.tableName != null) {
            if (this.tableName instanceof SelectQueryStatement) {
                final SelectQueryStatement subQueryStatement = (SelectQueryStatement)this.tableName;
                if (vendorSQS != null) {
                    subQueryStatement.setReportsMeta(vendorSQS != null && vendorSQS.getReportsMeta());
                    subQueryStatement.setAmazonRedShiftFlag(vendorSQS.isAmazonRedShift());
                    subQueryStatement.setMSAzureFlag(vendorSQS.isMSAzure());
                    subQueryStatement.setOracleLiveFlag(vendorSQS.isOracleLive());
                    subQueryStatement.setCanUseIFFunctionForPGCaseWhenExp(vendorSQS.canUseIFFunctionForPGCaseWhenExp());
                    subQueryStatement.setCanUseUDFFunctionsForText(vendorSQS.canUseUDFFunctionsForText());
                    subQueryStatement.setCanUseUDFFunctionsForNumeric(vendorSQS.canUseUDFFunctionsForNumeric());
                    subQueryStatement.setCanUseUDFFunctionsForDateTime(vendorSQS.canUseUDFFunctionsForDateTime());
                    subQueryStatement.setCanHandleStringLiteralsForNumeric(vendorSQS.canHandleStringLiteralsForNumeric());
                    subQueryStatement.setCanHandleStringLiteralsForDateTime(vendorSQS.canHandleStringLiteralsForDateTime());
                    subQueryStatement.setCanHandleNullsInsideINClause(vendorSQS.canHandleNullsInsideINClause());
                    subQueryStatement.setCanCastStringLiteralToText(vendorSQS.canCastStringLiteralToText());
                    subQueryStatement.setRemovalOptionForOrderAndFetchClauses(vendorSQS.getRemovalOptionForOrderAndFetchClauses());
                    subQueryStatement.setCanUseDistinctFromForNullSafeEqualsOperator(vendorSQS.canUseDistinctFromForNullSafeEqualsOperator());
                    subQueryStatement.setCanHandleHavingWithoutGroupBy(vendorSQS.canHandleHavingWithoutGroupBy());
                }
                subQueryStatement.setOrderByStatement(null);
                subQueryStatement.setLimitClause(null);
                subQueryStatement.setFetchClause(null);
                ft.setTableName(subQueryStatement.toVectorWiseSelect());
                if (this.aliasName == null) {
                    ft.setAliasName("SwisSQL_ALIAS" + (this.getFromTableIndexFromFromItemList(vendorSQS) + 1));
                }
            }
            else {
                if (this.tableName instanceof FunctionCalls) {
                    throw new ConvertException();
                }
                if (this.tableName instanceof WithStatement) {
                    ft.setTableName(((WithStatement)this.tableName).toVectorWise());
                }
                else if (this.tableName instanceof FromClause) {
                    ft.setTableName(((FromClause)this.tableName).toVectorWiseSelect(vembuSQS, vendorSQS));
                }
                else {
                    final String table_Name_String = (String)this.tableName;
                    final int atIndex = table_Name_String.indexOf("@");
                    ft.setTableName(StringFunctions.replaceFirst(".", "..", (String)this.tableName));
                    final Vector tokenVector = new Vector();
                    final String table_Name = (String)this.tableName;
                    final StringTokenizer st = new StringTokenizer(table_Name, ".");
                    int count = 0;
                    while (st.hasMoreTokens()) {
                        tokenVector.add(st.nextToken());
                        ++count;
                    }
                    if (count == 1 && atIndex != -1) {
                        final String dataBaseTableName = tokenVector.elementAt(0);
                        final String sqlTableName = dataBaseTableName.substring(0, atIndex);
                        final String sqlDataBaseName = dataBaseTableName.substring(atIndex + 1);
                        final String sqlDataBaseAndTableName = sqlDataBaseName + "." + sqlTableName;
                        ft.setTableName(sqlDataBaseAndTableName);
                    }
                    else if (count == 2 && atIndex != -1) {
                        final String dataBaseTableName = tokenVector.elementAt(1);
                        final int tableAtIndex = dataBaseTableName.indexOf("@");
                        final String sqlTableName2 = dataBaseTableName.substring(0, tableAtIndex);
                        final String sqlDataBaseName2 = dataBaseTableName.substring(tableAtIndex + 1);
                        final String sqlDataBaseAndTableName2 = sqlDataBaseName2 + "." + tokenVector.elementAt(0) + "." + sqlTableName2;
                        ft.setTableName(sqlDataBaseAndTableName2);
                    }
                    String tempTableName = (String)ft.getTableName();
                    String tableNameSubString = "";
                    String quotedIdenStartString = "[";
                    String quotedIdenEndString = "]";
                    if (tempTableName.startsWith("`")) {
                        quotedIdenStartString = "`";
                    }
                    if (tempTableName.endsWith("`")) {
                        quotedIdenEndString = "`";
                    }
                    int startIndex = tempTableName.indexOf(quotedIdenStartString);
                    if (startIndex != -1) {
                        while (startIndex != -1) {
                            if (startIndex == 0) {
                                tableNameSubString = tempTableName.substring(1);
                                tableNameSubString = "\"" + tableNameSubString;
                                tableNameSubString = (tempTableName = StringFunctions.replaceFirst("`", quotedIdenEndString, tableNameSubString));
                                startIndex = tableNameSubString.indexOf(quotedIdenStartString);
                            }
                            else {
                                tableNameSubString = tempTableName.substring(0, startIndex);
                                tableNameSubString += "\"";
                                tableNameSubString += tempTableName.substring(startIndex + 1);
                                tableNameSubString = (tempTableName = StringFunctions.replaceFirst("\"", quotedIdenEndString, tableNameSubString));
                                startIndex = tableNameSubString.indexOf(quotedIdenStartString);
                            }
                        }
                        ft.setTableName(tableNameSubString);
                    }
                }
            }
        }
        ft.setIsAS(this.isAS);
        if (this.aliasName != null) {
            if (this.aliasName.trim().equalsIgnoreCase("`at`") || this.aliasName.trim().equalsIgnoreCase("at")) {
                this.aliasName = "\"" + this.aliasName.trim().replaceAll("`", "") + "\"";
            }
            if (this.aliasName.charAt(0) == '\'') {
                ft.setAliasName(this.aliasName.replace('\'', ' ').trim());
            }
            else if (this.aliasName.charAt(0) == '`') {
                ft.setAliasName(this.aliasName.replace('`', ' ').trim());
            }
            else {
                ft.setAliasName(this.aliasName);
            }
        }
        return ft;
    }
    
    private void removeJoinExpressionForCrossJoin(final FromTable ft) {
        try {
            if (this.joinClause != null && this.onOrUsingJoin != null && this.joinExpression != null && this.joinClause.equalsIgnoreCase("CROSS JOIN") && this.onOrUsingJoin.equalsIgnoreCase("ON")) {
                final WhereExpression we = this.joinExpression.elementAt(0);
                if (we != null) {
                    final Vector wi = we.getWhereItems();
                    if (wi != null && wi.size() == 1 && wi.get(0).getLeftWhereExp().toString().trim().equalsIgnoreCase("1") && wi.get(0).getRightWhereExp().toString().trim().equalsIgnoreCase("1") && wi.get(0).getOperator() != null && wi.get(0).getOperator().trim().equals("=")) {
                        ft.setJoinExpression(null);
                        ft.setOnOrUsingJoin(null);
                    }
                    else {
                        ft.setJoinClause("INNER JOIN");
                    }
                }
            }
        }
        catch (final Exception ex) {}
    }
}
