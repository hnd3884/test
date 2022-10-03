package com.adventnet.swissqlapi.sql.statement.select;

import java.util.Collection;
import com.adventnet.swissqlapi.SwisSQLAPI;
import com.adventnet.swissqlapi.sql.statement.create.DateClass;
import com.adventnet.swissqlapi.util.SwisSQLUtils;
import com.adventnet.swissqlapi.sql.functions.FunctionCalls;
import com.adventnet.swissqlapi.sql.exception.ConvertException;
import com.adventnet.swissqlapi.sql.statement.CommentClass;
import com.adventnet.swissqlapi.sql.UserObjectContext;
import java.util.Vector;

public class CaseStatement
{
    private String caseClause;
    private WhereExpression caseCondition;
    private SelectQueryStatement subquery;
    private Vector whenStatementList;
    private String elseClause;
    private SelectColumn elseStatement;
    private String endClause;
    private UserObjectContext context;
    private String decodeFunction;
    private CommentClass commentObj;
    private CommentClass commentObjAfterToken;
    private boolean castToTextInsideIf;
    private Vector curr_whereItem;
    
    public CaseStatement() {
        this.context = null;
        this.castToTextInsideIf = true;
        this.curr_whereItem = new Vector();
        this.caseClause = null;
        this.caseCondition = null;
        this.whenStatementList = null;
        this.elseClause = null;
        this.elseStatement = null;
        this.endClause = null;
        this.decodeFunction = null;
    }
    
    public void setCastToTextInsideIf(final boolean yes) {
        this.castToTextInsideIf = yes;
    }
    
    public void setCaseClause(final String s_case_clause) {
        this.caseClause = s_case_clause;
    }
    
    public void setObjectContext(final UserObjectContext obj) {
        this.context = obj;
    }
    
    public void setCaseCondition(final WhereExpression we_case_condition) {
        this.caseCondition = we_case_condition;
    }
    
    public void setSubQuery(final SelectQueryStatement subquery) {
        this.subquery = subquery;
    }
    
    public void setWhenStatementList(final Vector v_when_clause_list) {
        this.whenStatementList = v_when_clause_list;
    }
    
    public void setElseClause(final String s_else_clause) {
        this.elseClause = s_else_clause;
    }
    
    public void setElseStatement(final SelectColumn sc_else_statement) {
        this.elseStatement = sc_else_statement;
    }
    
    public void setEndClause(final String s_end_clause) {
        this.endClause = s_end_clause;
    }
    
    public void setDecodeFunction(final String s_decode_function) {
        this.decodeFunction = s_decode_function;
    }
    
    public void setCommentClass(final CommentClass commentObj) {
        this.commentObj = commentObj;
    }
    
    public CommentClass getCommentClass() {
        return this.commentObj;
    }
    
    public void setCommentClassAfterToken(final CommentClass endCommentObj) {
        this.commentObjAfterToken = endCommentObj;
    }
    
    public CommentClass getCommentClassAfterToken() {
        return this.commentObjAfterToken;
    }
    
    public String getDecodeFunction() {
        return this.decodeFunction;
    }
    
    public String getCaseClause() {
        return this.caseClause;
    }
    
    public WhereExpression getCaseCondition() {
        return this.caseCondition;
    }
    
    public SelectQueryStatement getSubQuery() {
        return this.subquery;
    }
    
    public Vector getWhenClauseList() {
        return this.whenStatementList;
    }
    
    public String getElseClause() {
        return this.elseClause;
    }
    
    public SelectColumn getElseStatement() {
        return this.elseStatement;
    }
    
    public String getEndClause() {
        return this.endClause;
    }
    
    public String convertToBooleanFunction(final WhereItem wi) throws ConvertException {
        final StringBuffer sb = new StringBuffer();
        if (wi.getRightWhereExp() == null && wi.getRightWhereSubQuery() == null) {
            final String isnull = wi.toString().toUpperCase();
            if (isnull.indexOf("NOT") == -1) {
                sb.append("DECODE( " + wi.getLeftWhereExp().toString() + ", NULL , 1 , 0 )");
            }
            else {
                sb.append("DECODE( " + wi.getLeftWhereExp().toString() + ", NULL , 0 , 1 )");
            }
        }
        else if (wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
            final String s_operator = wi.getOperator();
            if (s_operator.equals("=")) {
                sb.append("DECODE( " + wi.getLeftWhereExp().toString() + "," + wi.getRightWhereExp().toString());
                sb.append(",1,0)");
            }
            else if (s_operator.equals("!=") || s_operator.equals("<>") || s_operator.equals("^=")) {
                sb.append("DECODE(" + wi.getLeftWhereExp().toString() + "," + wi.getRightWhereExp().toString());
                sb.append(",0,1)");
            }
            else if (s_operator.equals("<")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                sb.append("(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,0,0,1 )");
            }
            else if (s_operator.equals(">")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,0, ");
                sb.append("(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),1,0 )");
            }
            else if (s_operator.equals("<=")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,1 )");
            }
            else if (s_operator.equals(">=")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),1,0)");
            }
            else if (s_operator.equalsIgnoreCase("IN")) {
                sb.append("DECODE( " + wi.getLeftWhereExp());
                sb.append(",");
                final WhereColumn wc = wi.getRightWhereExp();
                if (wc.getColumnExpression() != null) {
                    if (wc.getColumnExpression().size() == 1) {
                        sb.append(wc.getColumnExpression().get(0).toString() + ",1");
                    }
                    else {
                        for (int i = 0; i < wc.getColumnExpression().size(); ++i) {
                            if (!")".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                if (!"(".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                    if (!",".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                        sb.append(wc.getColumnExpression().get(i).toString() + ",");
                                        sb.append("1");
                                        if (i != wc.getColumnExpression().size() - 2) {
                                            sb.append(",");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                sb.append(",0 )");
            }
            else if (s_operator.equalsIgnoreCase("NOT IN")) {
                sb.append("DECODE( " + wi.getLeftWhereExp());
                sb.append(",");
                final WhereColumn wc = wi.getRightWhereExp();
                if (wc.getColumnExpression() != null) {
                    if (wc.getColumnExpression().size() == 1) {
                        sb.append(wc.getColumnExpression().get(0).toString() + ",0");
                    }
                    else {
                        for (int i = 0; i < wc.getColumnExpression().size(); ++i) {
                            if (i < wc.getColumnExpression().size() - 1) {
                                if (!")".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                    if (!"(".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                        if (!",".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                            sb.append(wc.getColumnExpression().get(i).toString() + ",");
                                            sb.append("0");
                                            if (i != wc.getColumnExpression().size() - 2) {
                                                sb.append(",");
                                            }
                                        }
                                    }
                                }
                            }
                            else if (!")".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                if (!"(".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                    if (!",".equals(wc.getColumnExpression().get(i).toString().trim())) {
                                        sb.append(wc.getColumnExpression().get(i).toString() + ",");
                                        sb.append("1");
                                        if (i != wc.getColumnExpression().size() - 2) {
                                            sb.append(",");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                sb.append(",1 )");
            }
            else {
                if (!s_operator.equalsIgnoreCase("BETWEEN")) {
                    throw new ConvertException("Current operator yet to be supported ");
                }
                final String var = wi.getLeftWhereExp().toString();
                sb.append("DECODE( GREATEST(" + var + ",");
                String v1 = "";
                String v2 = "";
                final WhereColumn wc2 = wi.getRightWhereExp();
                if (wc2.getColumnExpression() != null && wc2.getColumnExpression().size() == 3 && wc2.getColumnExpression().get(1).toString().trim().equalsIgnoreCase("AND")) {
                    v1 = wc2.getColumnExpression().get(0).toString();
                    v2 = wc2.getColumnExpression().get(2).toString();
                }
                sb.append(v1 + "), " + var + ", DECODE(LEAST(" + var + "," + v2 + ")," + var + ",1,0),0)");
            }
        }
        else if (wi.getRightWhereSubQuery() != null) {
            final String s_operator = wi.getOperator();
            if (s_operator.equals("=")) {
                sb.append("DECODE( " + wi.getLeftWhereExp().toString() + "," + "(" + wi.getRightWhereSubQuery().toString() + ")");
                sb.append(",1,0)");
            }
            else if (s_operator.equals("!=") || s_operator.equals("<>") || s_operator.equals("^=")) {
                sb.append("DECODE(" + wi.getLeftWhereExp().toString() + "," + "(" + wi.getRightWhereSubQuery().toString() + ")");
                sb.append(",0,1)");
            }
            else if (s_operator.equals("<")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),");
                sb.append("(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),0,0,0,1 )");
            }
            else if (s_operator.equals(">")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),0,0 ,");
                sb.append("(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),1,0 )");
            }
            else if (s_operator.equals("<=")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),");
                sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),0,1 )");
            }
            else if (s_operator.equals(">=")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),");
                sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),1,0)");
            }
            else {
                if (!"exists".equalsIgnoreCase(s_operator.trim())) {
                    throw new ConvertException("Current operator yet to be supported");
                }
                sb.append("DECODE( 1 , (");
                final SelectQueryStatement selectQueryStatement = wi.getRightWhereSubQuery();
                final Vector v3 = new Vector();
                v3.add("1");
                final WhereItem whereItem = new WhereItem();
                final WhereColumn whereColumnL = new WhereColumn();
                final WhereColumn whereColumnR = new WhereColumn();
                final Vector colExpL = new Vector();
                final Vector colExpR = new Vector();
                colExpL.add("ROWNUM");
                colExpR.add("2");
                whereColumnL.setColumnExpression(colExpL);
                whereColumnR.setColumnExpression(colExpR);
                whereItem.setLeftWhereExp(whereColumnL);
                whereItem.setRightWhereExp(whereColumnR);
                whereItem.setOperator("<");
                WhereExpression whereExpression = selectQueryStatement.getWhereExpression();
                if (whereExpression != null) {
                    whereExpression.addOperator("AND");
                    whereExpression.addWhereItem(whereItem);
                }
                else {
                    whereExpression = new WhereExpression();
                    whereExpression.addWhereItem(whereItem);
                    selectQueryStatement.setWhereExpression(whereExpression);
                }
                selectQueryStatement.getSelectStatement().setSelectItemList(v3);
                sb.append(selectQueryStatement.toString() + " ),1,0 )");
            }
        }
        return sb.toString();
    }
    
    public String convertToBooleanFunction(final WhereExpression caseCondition) throws ConvertException {
        final StringBuffer sb = new StringBuffer();
        if (caseCondition.getOpenBrace() == null && caseCondition.getWhereItems().size() > 1) {
            throw new ConvertException("Multiple Conditions yet to be supported  1");
        }
        if (caseCondition.getOpenBrace() != null && caseCondition.getWhereItems().size() == 1) {
            if (caseCondition.getWhereItems().elementAt(0) instanceof WhereExpression) {
                final WhereExpression we = caseCondition.getWhereItems().elementAt(0);
                if (we.getWhereItems().size() > 1) {
                    throw new ConvertException("Conversion Failure..Multiple Conditions are not supported 2");
                }
            }
        }
        else if (caseCondition.getWhereItems().get(1) instanceof WhereExpression) {
            final String str = this.convertToBooleanFunction(caseCondition.getWhereItems().get(1));
            sb.append("DECODE(2,");
            sb.append(str);
            sb.append(" + ");
        }
        WhereItem wi = new WhereItem();
        if (caseCondition.getWhereItems().elementAt(0) instanceof WhereExpression) {
            final WhereExpression we2 = caseCondition.getWhereItems().elementAt(0);
            wi = we2.getWhereItems().elementAt(0);
        }
        else {
            wi = caseCondition.getWhereItems().elementAt(0);
        }
        if (wi.getRightWhereExp() == null && wi.getRightWhereSubQuery() == null) {
            final String isnull = wi.toString().toUpperCase();
            if (isnull.indexOf("NOT") == -1) {
                sb.append("DECODE( " + wi.getLeftWhereExp().toString() + ", NULL , 1 , 0 )");
            }
            else {
                sb.append("DECODE( " + wi.getLeftWhereExp().toString() + ", NULL , 0 , 1 )");
            }
        }
        else if (wi.getRightWhereExp() != null && wi.getRightWhereSubQuery() == null) {
            final String s_operator = wi.getOperator();
            if (s_operator.equals("=")) {
                sb.append("DECODE( " + wi.getLeftWhereExp().toString() + "," + wi.getRightWhereExp().toString());
                sb.append(",1,0)");
            }
            else if (s_operator.equals("!=") || s_operator.equals("<>") || s_operator.equals("^=")) {
                sb.append("DECODE(" + wi.getLeftWhereExp().toString() + "," + wi.getRightWhereExp().toString());
                sb.append(",0,1)");
            }
            else if (s_operator.equals("<")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                sb.append("(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,0,0,1 )");
            }
            else if (s_operator.equals(">")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,0, ");
                sb.append("(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),1,0 )");
            }
            else if (s_operator.equals("<=")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),0,1 )");
            }
            else {
                if (!s_operator.equals(">=")) {
                    throw new ConvertException("Conversion Failure..operator not supported");
                }
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),");
                sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + wi.getRightWhereExp().toString() + "),1,0)");
            }
        }
        else if (wi.getRightWhereSubQuery() != null) {
            final String s_operator = wi.getOperator();
            if (s_operator.equals("=")) {
                sb.append("DECODE( " + wi.getLeftWhereExp().toString() + "," + "(" + wi.getRightWhereSubQuery().toString() + ")");
                sb.append(",1,0)");
            }
            else if (s_operator.equals("!=") || s_operator.equals("<>") || s_operator.equals("^=")) {
                sb.append("DECODE(" + wi.getLeftWhereExp().toString() + "," + "(" + wi.getRightWhereSubQuery().toString() + ")");
                sb.append(",0,1)");
            }
            else if (s_operator.equals("<")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),");
                sb.append("(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),0,0,0,1 )");
            }
            else if (s_operator.equals(">")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),0,0 ,");
                sb.append("(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),1,0 )");
            }
            else if (s_operator.equals("<=")) {
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + " ) ),");
                sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),0,1 )");
            }
            else {
                if (!s_operator.equals(">=")) {
                    throw new ConvertException("Conversion Failure..operator not supported");
                }
                sb.append("DECODE(ABS(" + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),");
                sb.append(" 0,1,( " + wi.getLeftWhereExp().toString() + " - " + "(" + wi.getRightWhereSubQuery().toString() + ") ),1,0)");
            }
        }
        return sb.toString();
    }
    
    public CaseStatement toMySQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        cs.setCaseClause(this.caseClause);
        if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toMySQLSelect(to_sqs, from_sqs));
        }
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            v_when_statement_list.addElement(this.whenStatementList.elementAt(i).toMySQLSelect(to_sqs, from_sqs));
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            cs.setElseStatement(this.elseStatement.toMySQLSelect(to_sqs, from_sqs));
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        return cs;
    }
    
    public CaseStatement toPostgreSQLSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        boolean needsCasting = false;
        cs.setCaseClause(this.caseClause);
        if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        needsCasting = (this.needsCastingForStringLiterals() || (this.castToTextInsideIf && to_sqs != null && to_sqs.canCastAllToTextColumns()));
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            final WhenStatement ws = this.whenStatementList.elementAt(i);
            final SelectColumn sc = ws.getThenStatement();
            if (sc != null && needsCasting) {
                final Vector colExp = sc.getColumnExpression();
                final Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "CHAR");
                if (castToCharExp != null) {
                    sc.setColumnExpression(castToCharExp);
                }
            }
            v_when_statement_list.addElement(ws.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toPostgreSQLSelect());
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            if (needsCasting) {
                final Vector colExp2 = this.elseStatement.getColumnExpression();
                final Vector castToCharExp2 = FunctionCalls.castToCharClass(colExp2, "CHAR");
                if (castToCharExp2 != null) {
                    this.elseStatement.setColumnExpression(castToCharExp2);
                }
            }
            cs.setElseStatement(this.elseStatement.toPostgreSQLSelect(to_sqs, from_sqs));
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        if (to_sqs != null) {
            to_sqs.addCurrentIndexToIfFunctionList();
        }
        return cs;
    }
    
    public CaseStatement toANSISelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        cs.setCaseClause(this.caseClause);
        if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toANSISelect(to_sqs, from_sqs));
        }
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            v_when_statement_list.addElement(this.whenStatementList.elementAt(i).toANSISelect(to_sqs, from_sqs));
        }
        if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toANSISelect());
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            cs.setElseStatement(this.elseStatement.toANSISelect(to_sqs, from_sqs));
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        return cs;
    }
    
    public CaseStatement toTeradataSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        cs.setCaseClause(this.caseClause);
        if (this.caseCondition != null) {
            this.caseCondition.setCaseExpressionBool(true);
            final WhereExpression caseExp = this.caseCondition.toTeradataSelect(to_sqs, from_sqs);
            if (this.caseCondition.getRownumClause() != null) {
                caseExp.setRownumClause(this.caseCondition.getRownumClause());
            }
            cs.setCaseCondition(caseExp);
        }
        boolean isDatePresent = false;
        int dateWhenStmtIdx = -1;
        for (int k = 0; k < this.whenStatementList.size(); ++k) {
            final SelectColumn sc1 = this.whenStatementList.elementAt(k).getThenStatement();
            for (int n = 0; n < sc1.getColumnExpression().size(); ++n) {
                final Object obj = sc1.getColumnExpression().get(n);
                if (obj instanceof FunctionCalls) {
                    final FunctionCalls fcObj = (FunctionCalls)obj;
                    if (fcObj.getFunctionName() != null) {
                        final String fnName = fcObj.getFunctionName().getColumnName();
                        if (SwisSQLUtils.getFunctionReturnType(fnName, fcObj.getFunctionArguments()).equalsIgnoreCase("date")) {
                            isDatePresent = true;
                            dateWhenStmtIdx = k;
                        }
                    }
                }
            }
        }
        if (this.elseStatement != null) {
            for (int n2 = 0; n2 < this.elseStatement.getColumnExpression().size(); ++n2) {
                final Object obj2 = this.elseStatement.getColumnExpression().get(n2);
                if (obj2 instanceof FunctionCalls) {
                    final FunctionCalls fcObj2 = (FunctionCalls)obj2;
                    if (fcObj2.getFunctionName() != null) {
                        final String fnName2 = fcObj2.getFunctionName().getColumnName();
                        if (SwisSQLUtils.getFunctionReturnType(fnName2, fcObj2.getFunctionArguments()).equalsIgnoreCase("date")) {
                            isDatePresent = true;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            final WhenStatement convertedWhenStmt = this.whenStatementList.elementAt(i).toTeradataSelect(to_sqs, from_sqs);
            if (isDatePresent) {
                final FunctionCalls caseFunc = new FunctionCalls();
                final TableColumn fnName3 = new TableColumn();
                fnName3.setColumnName("CAST");
                caseFunc.setFunctionName(fnName3);
                final Vector fnArgs = new Vector();
                fnArgs.add(convertedWhenStmt.getThenStatement());
                caseFunc.setAsDatatype("AS");
                final DateClass timestamp = new DateClass();
                timestamp.setDatatypeName("TIMESTAMP");
                timestamp.setSize("0");
                timestamp.setOpenBrace("(");
                timestamp.setClosedBrace(")");
                fnArgs.add(timestamp);
                caseFunc.setFunctionArguments(fnArgs);
                final SelectColumn newSelCol = new SelectColumn();
                final Vector colExp = new Vector();
                colExp.add(caseFunc);
                newSelCol.setColumnExpression(colExp);
                convertedWhenStmt.setThenStatement(newSelCol);
                v_when_statement_list.addElement(convertedWhenStmt);
            }
            else {
                v_when_statement_list.addElement(convertedWhenStmt);
            }
        }
        if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toTeradataSelect());
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            final SelectColumn convertedElseStatement = this.elseStatement.toTeradataSelect(to_sqs, from_sqs);
            if (isDatePresent) {
                final FunctionCalls caseFunc2 = new FunctionCalls();
                final TableColumn fnName4 = new TableColumn();
                fnName4.setColumnName("CAST");
                caseFunc2.setFunctionName(fnName4);
                final Vector fnArgs2 = new Vector();
                fnArgs2.add(convertedElseStatement);
                caseFunc2.setAsDatatype("AS");
                final DateClass timestamp2 = new DateClass();
                timestamp2.setDatatypeName("TIMESTAMP");
                timestamp2.setSize("0");
                timestamp2.setOpenBrace("(");
                timestamp2.setClosedBrace(")");
                fnArgs2.add(timestamp2);
                caseFunc2.setFunctionArguments(fnArgs2);
                final SelectColumn newSelCol2 = new SelectColumn();
                final Vector colExp2 = new Vector();
                colExp2.add(caseFunc2);
                newSelCol2.setColumnExpression(colExp2);
                cs.setElseStatement(newSelCol2);
            }
            else {
                cs.setElseStatement(convertedElseStatement);
            }
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        return cs;
    }
    
    public CaseStatement toDB2Select(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        cs.setCaseClause(this.caseClause);
        if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toDB2Select(to_sqs, from_sqs));
        }
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            v_when_statement_list.addElement(this.whenStatementList.elementAt(i).toDB2Select(to_sqs, from_sqs));
        }
        if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toDB2Select());
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            cs.setElseStatement(this.elseStatement.toDB2Select(to_sqs, from_sqs));
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        return cs;
    }
    
    public CaseStatement toMSSQLServerSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        cs.setCaseClause(this.caseClause);
        cs.setCaseCondition(this.caseCondition);
        if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toMSSQLServerSelect(to_sqs, from_sqs));
        }
        if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toMSSQLServerSelect());
        }
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            v_when_statement_list.addElement(this.whenStatementList.elementAt(i).toMSSQLServerSelect(to_sqs, from_sqs));
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            cs.setElseStatement(this.elseStatement.toMSSQLServerSelect(to_sqs, from_sqs));
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        return cs;
    }
    
    public CaseStatement toSybaseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        cs.setCaseClause(this.caseClause);
        cs.setObjectContext(this.context);
        cs.setCaseCondition(this.caseCondition);
        if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toSybaseSelect(to_sqs, from_sqs));
        }
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            v_when_statement_list.addElement(this.whenStatementList.elementAt(i).toSybaseSelect(to_sqs, from_sqs));
        }
        if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toSybaseSelect());
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            cs.setElseStatement(this.elseStatement.toSybaseSelect(to_sqs, from_sqs));
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        return cs;
    }
    
    public CaseStatement toOracleSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        if (SwisSQLAPI.convertCaseToDecode) {
            final StringBuffer sb = new StringBuffer();
            if (this.commentObj != null) {
                sb.append(this.commentObj.toString().trim() + " ");
            }
            sb.append("DECODE(");
            if (this.caseCondition == null) {
                if (this.subquery == null) {
                    sb.append("1 ,");
                    for (int i = 0; i < this.whenStatementList.size(); ++i) {
                        final WhenStatement when_statement = this.whenStatementList.elementAt(i).toOracleSelect(to_sqs, from_sqs);
                        final Vector whereItemList = new Vector();
                        final Vector operator = new Vector();
                        this.curr_whereItem = when_statement.getWhenCondition().getWhereItems();
                        this.loadWhereItemsOperators(whereItemList, operator);
                        sb.append(" DECODE( " + whereItemList.size() + " ,");
                        for (int j = 0; j < whereItemList.size(); ++j) {
                            sb.append(this.convertToBooleanFunction(whereItemList.get(j)));
                            if (j != whereItemList.size() - 1) {
                                sb.append(" + ");
                            }
                        }
                        sb.append(" , 1 , 0 ) ");
                        sb.append(",");
                        sb.append(when_statement.getThenStatement().toString());
                        sb.append(",");
                    }
                }
                else {
                    final SelectColumn caseSelectColumn = new SelectColumn();
                    final Vector items = new Vector();
                    items.add(this.subquery);
                    caseSelectColumn.setColumnExpression(items);
                    final SelectColumn decodeCondition = caseSelectColumn.toOracleSelect(null, null);
                    final String singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
                    SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
                    sb.append(decodeCondition.toString());
                    SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList;
                    sb.append(",");
                    for (int k = 0; k < this.whenStatementList.size(); ++k) {
                        final WhenStatement when_statement2 = this.whenStatementList.elementAt(k).toOracleSelect(to_sqs, from_sqs);
                        final Vector whereItemList2 = new Vector();
                        final Vector operator2 = new Vector();
                        this.curr_whereItem = when_statement2.getWhenCondition().getWhereItems();
                        if (this.curr_whereItem.get(0) != null) {
                            sb.append(this.curr_whereItem.get(0).toString());
                        }
                        sb.append(",");
                        sb.append(when_statement2.getThenStatement().toString());
                        sb.append(",");
                    }
                }
            }
            else {
                final WhereExpression decodeCondition2 = this.caseCondition.toOracleSelect(to_sqs, from_sqs);
                final String singleQueryConvertedToMultipleQueryList2 = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
                sb.append(decodeCondition2.toString());
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList2;
                sb.append(",");
                for (int l = 0; l < this.whenStatementList.size(); ++l) {
                    final WhenStatement when_statement3 = this.whenStatementList.elementAt(l).toOracleSelect(to_sqs, from_sqs);
                    final Vector whereItemList3 = new Vector();
                    final Vector operator3 = new Vector();
                    this.curr_whereItem = when_statement3.getWhenCondition().getWhereItems();
                    if (this.curr_whereItem.get(0) != null) {
                        sb.append(this.curr_whereItem.get(0).toString());
                    }
                    sb.append(",");
                    sb.append(when_statement3.getThenStatement().toString());
                    sb.append(",");
                }
            }
            if (this.elseStatement != null) {
                sb.append(this.elseStatement.toOracleSelect(to_sqs, from_sqs));
            }
            else {
                sb.deleteCharAt(sb.toString().lastIndexOf(44));
            }
            sb.append(")");
            this.decodeFunction = sb.toString();
            return this;
        }
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        cs.setCaseClause(this.caseClause);
        cs.setCommentClass(this.commentObj);
        if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toOracleSelect(to_sqs, from_sqs));
        }
        for (int m = 0; m < this.whenStatementList.size(); ++m) {
            v_when_statement_list.addElement(this.whenStatementList.elementAt(m).toOracleSelect(to_sqs, from_sqs));
        }
        if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toOracleSelect());
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            cs.setElseStatement(this.elseStatement.toOracleSelect(to_sqs, from_sqs));
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        return cs;
    }
    
    public CaseStatement toNetezzaSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        final Vector v_when_statement_list = new Vector();
        cs.setCaseClause(this.caseClause);
        if (this.caseCondition != null) {
            cs.setCaseCondition(this.caseCondition.toNetezzaSelect(to_sqs, from_sqs));
        }
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            v_when_statement_list.addElement(this.whenStatementList.elementAt(i).toNetezzaSelect(to_sqs, from_sqs));
        }
        if (this.subquery != null) {
            cs.setSubQuery(this.subquery.toNetezzaSelect());
        }
        cs.setWhenStatementList(v_when_statement_list);
        cs.setElseClause(this.elseClause);
        if (this.elseStatement != null) {
            cs.setElseStatement(this.elseStatement.toNetezzaSelect(to_sqs, from_sqs));
        }
        cs.setEndClause(this.endClause);
        cs.setDecodeFunction(this.decodeFunction);
        return cs;
    }
    
    public void loadWhereItemsOperators(final Vector whereItemList, final Vector operatorList) {
        for (int i = 0; i < this.curr_whereItem.size(); ++i) {
            final Object obj = this.curr_whereItem.get(i);
            if (obj instanceof WhereItem) {
                whereItemList.add(obj);
            }
            else if (obj instanceof WhereExpression) {
                final WhereExpression we = (WhereExpression)obj;
                we.loadWhereItemsOperators(whereItemList, operatorList);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.decodeFunction != null) {
            sb.append(this.decodeFunction);
        }
        else {
            ++SelectQueryStatement.beautyTabCount;
            if (this.commentObj != null) {
                sb.append("\n");
                for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                    sb.append("\t");
                }
                sb.append(this.commentObj.toString().trim());
            }
            sb.append("\n");
            for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                sb.append("\t");
            }
            sb.append(this.caseClause);
            if (this.caseCondition != null) {
                this.caseCondition.setObjectContext(this.context);
                sb.append(" " + this.caseCondition.toString());
            }
            if (this.subquery != null) {
                this.subquery.setObjectContext(this.context);
                sb.append("\n");
                for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                    sb.append("\t");
                }
                sb.append("(");
                sb.append(" " + this.subquery.toString());
                sb.append("\n");
                for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                    sb.append("\t");
                }
                sb.append(")");
            }
            ++SelectQueryStatement.beautyTabCount;
            for (int i_count = 0; i_count < this.whenStatementList.size(); ++i_count) {
                this.whenStatementList.elementAt(i_count).setObjectContext(this.context);
                sb.append("\n");
                for (int i = 0; i < SelectQueryStatement.beautyTabCount; ++i) {
                    sb.append("\t");
                }
                sb.append(" " + this.whenStatementList.elementAt(i_count).toString());
            }
            if (this.elseClause != null) {
                sb.append("\n");
                for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                    sb.append("\t");
                }
                sb.append(" " + this.elseClause);
                this.elseStatement.setObjectContext(this.context);
                sb.append(" " + this.elseStatement.toString());
            }
            --SelectQueryStatement.beautyTabCount;
            sb.append("\n");
            for (int j = 0; j < SelectQueryStatement.beautyTabCount; ++j) {
                sb.append("\t");
            }
            sb.append(" " + this.endClause.toString());
            if (this.commentObjAfterToken != null) {
                sb.append(this.commentObjAfterToken.toString().trim());
            }
            --SelectQueryStatement.beautyTabCount;
        }
        return sb.toString();
    }
    
    public CaseStatement toInformixSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final StringBuffer sb = new StringBuffer();
        sb.append("DECODE(");
        if (this.caseCondition == null) {
            if (this.subquery == null) {
                sb.append("1 ,");
                for (int i = 0; i < this.whenStatementList.size(); ++i) {
                    final WhenStatement when_statement = this.whenStatementList.elementAt(i).toInformixSelect(to_sqs, from_sqs);
                    final Vector whereItemList = new Vector();
                    final Vector operator = new Vector();
                    this.curr_whereItem = when_statement.getWhenCondition().getWhereItems();
                    this.loadWhereItemsOperators(whereItemList, operator);
                    sb.append(" DECODE( " + whereItemList.size() + " ,");
                    for (int j = 0; j < whereItemList.size(); ++j) {
                        sb.append(this.convertToBooleanFunction(whereItemList.get(j)));
                        if (j != whereItemList.size() - 1) {
                            sb.append(" + ");
                        }
                    }
                    sb.append(" , 1 , 0 ) ");
                    sb.append(",");
                    sb.append(when_statement.getThenStatement().toString());
                    sb.append(",");
                }
            }
            else {
                final SelectColumn caseSelectColumn = new SelectColumn();
                final Vector items = new Vector();
                items.add(this.subquery);
                caseSelectColumn.setColumnExpression(items);
                final SelectColumn decodeCondition = caseSelectColumn.toInformixSelect(null, null);
                final String singleQueryConvertedToMultipleQueryList = SelectQueryStatement.singleQueryConvertedToMultipleQueryList;
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = null;
                sb.append(decodeCondition.toString());
                SelectQueryStatement.singleQueryConvertedToMultipleQueryList = singleQueryConvertedToMultipleQueryList;
                sb.append(",");
                for (int k = 0; k < this.whenStatementList.size(); ++k) {
                    final WhenStatement when_statement2 = this.whenStatementList.elementAt(k).toInformixSelect(to_sqs, from_sqs);
                    final Vector whereItemList2 = new Vector();
                    final Vector operator2 = new Vector();
                    this.curr_whereItem = when_statement2.getWhenCondition().getWhereItems();
                    if (this.curr_whereItem.get(0) != null) {
                        sb.append(this.curr_whereItem.get(0).toString());
                    }
                    sb.append(",");
                    sb.append(when_statement2.getThenStatement().toString());
                    sb.append(",");
                }
            }
        }
        else {
            sb.append(this.caseCondition.toInformixSelect(to_sqs, from_sqs));
            sb.append(",");
            for (int i = 0; i < this.whenStatementList.size(); ++i) {
                final WhenStatement when_statement = this.whenStatementList.elementAt(i).toInformixSelect(to_sqs, from_sqs);
                final Vector whereItemList = new Vector();
                final Vector operator = new Vector();
                this.curr_whereItem = when_statement.getWhenCondition().getWhereItems();
                if (this.curr_whereItem.get(0) != null) {
                    sb.append(this.curr_whereItem.get(0).toString());
                }
                sb.append(",");
                sb.append(when_statement.getThenStatement().toString());
                sb.append(",");
            }
        }
        if (this.elseStatement != null) {
            sb.append(this.elseStatement.toInformixSelect(to_sqs, from_sqs));
        }
        else {
            sb.deleteCharAt(sb.toString().lastIndexOf(44));
        }
        sb.append(")");
        this.decodeFunction = sb.toString();
        return this;
    }
    
    public void replaceRownumTableColumn(final Object newColumn) throws ConvertException {
        if (this.caseCondition != null) {
            this.caseCondition.replaceRownumTableColumn(newColumn);
        }
        for (int i = 0; i < this.whenStatementList.size(); ++i) {
            final WhenStatement whenSt = this.whenStatementList.get(i);
            if (whenSt.getThenStatement() != null) {
                whenSt.getThenStatement().replaceRownumTableColumn(newColumn);
            }
            if (whenSt.getWhenCondition() != null) {
                whenSt.getWhenCondition().replaceRownumTableColumn(newColumn);
            }
        }
        if (this.elseStatement != null) {
            this.elseStatement.replaceRownumTableColumn(newColumn);
        }
    }
    
    public CaseStatement toVectorWiseSelect(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) throws ConvertException {
        final CaseStatement cs = new CaseStatement();
        try {
            final Vector v_when_statement_list = new Vector();
            boolean needsCasting = false;
            cs.setCaseClause(this.caseClause);
            if (this.caseCondition != null) {
                cs.setCaseCondition(this.caseCondition.toVectorWiseSelect(to_sqs, from_sqs));
            }
            needsCasting = this.needsCastingForStringLiterals();
            for (int i = 0; i < this.whenStatementList.size(); ++i) {
                final WhenStatement ws = this.whenStatementList.elementAt(i);
                final SelectColumn sc = ws.getThenStatement();
                if (sc != null && needsCasting) {
                    final Vector colExp = sc.getColumnExpression();
                    final Vector castToCharExp = FunctionCalls.castToCharClass(colExp, "CHAR");
                    if (castToCharExp != null) {
                        sc.setColumnExpression(castToCharExp);
                    }
                }
                v_when_statement_list.addElement(ws.toVectorWiseSelect(to_sqs, from_sqs));
            }
            cs.setWhenStatementList(v_when_statement_list);
            cs.setElseClause(this.elseClause);
            if (this.elseStatement != null) {
                if (needsCasting) {
                    final Vector colExp2 = this.elseStatement.getColumnExpression();
                    final Vector castToCharExp2 = FunctionCalls.castToCharClass(colExp2, "CHAR");
                    if (castToCharExp2 != null) {
                        this.elseStatement.setColumnExpression(castToCharExp2);
                    }
                }
                cs.setElseStatement(this.elseStatement.toVectorWiseSelect(to_sqs, from_sqs));
            }
            cs.setEndClause(this.endClause);
            cs.setDecodeFunction(this.decodeFunction);
        }
        catch (final Exception e) {
            System.err.println("Exception in Query : " + from_sqs);
            e.printStackTrace();
        }
        return cs;
    }
    
    public FunctionCalls getFunctionCall() {
        final FunctionCalls fc = new FunctionCalls();
        final TableColumn tc = new TableColumn();
        tc.setColumnName("IF");
        fc.setFunctionName(tc);
        return fc;
    }
    
    public FunctionCalls convertToFunctionCall(final SelectQueryStatement to_sqs, final SelectQueryStatement from_sqs) {
        FunctionCalls fcFinal = null;
        try {
            if (from_sqs != null && from_sqs.canUseIFFunctionForPGCaseWhenExp() && !from_sqs.isAmazonRedShift() && this.caseCondition == null && this.subquery == null) {
                final Vector fcList = new Vector();
                FunctionCalls fc;
                fcFinal = (fc = this.getFunctionCall());
                FunctionCalls reference = null;
                for (int i = 0; i < this.whenStatementList.size(); ++i) {
                    final WhenStatement ws = this.whenStatementList.elementAt(i);
                    if (i != 0) {
                        fc = reference;
                    }
                    final SelectColumn sc1 = new SelectColumn();
                    final Vector colExp = new Vector();
                    colExp.add(ws.getWhenCondition());
                    sc1.setColumnExpression(colExp);
                    final SelectColumn sc2 = ws.getThenStatement();
                    final SelectColumn sc3 = new SelectColumn();
                    final Vector colExp2 = new Vector();
                    if (i + 1 < this.whenStatementList.size()) {
                        reference = this.getFunctionCall();
                        colExp2.add(reference);
                    }
                    else if (this.elseStatement != null) {
                        colExp2.addAll(this.elseStatement.getColumnExpression());
                    }
                    else {
                        colExp2.add("NULL");
                    }
                    sc3.setColumnExpression(colExp2);
                    final Vector fnArgs = new Vector();
                    fnArgs.add(sc1);
                    fnArgs.add(sc2);
                    fnArgs.add(sc3);
                    fc.setFunctionArguments(fnArgs);
                    fcList.add(fc);
                }
            }
        }
        catch (final Exception e) {
            fcFinal = null;
        }
        return fcFinal;
    }
    
    public boolean needsCastingForStringLiterals() {
        boolean needsCasting = false;
        for (int j = 0; j < this.whenStatementList.size(); ++j) {
            final SelectColumn sc = this.whenStatementList.elementAt(j).getThenStatement();
            if (sc.needsCastingForStringLiterals(true)) {
                needsCasting = true;
                break;
            }
        }
        if (this.elseStatement != null && !needsCasting) {
            needsCasting = this.elseStatement.needsCastingForStringLiterals(true);
        }
        return needsCasting;
    }
}
